<%@page import="ume.pareva.core.UmeFilterInitialise"%>
<%@page import="ume.pareva.ParevaStartup"%>
<%@include file="/WEB-INF/jspf/coreimport.jspf" %>
<%@include file="/WEB-INF/jspf/db.jspf" %>
<html>
<head>
<style>
textarea {
	-webkit-box-sizing: border-box;
	-moz-box-sizing: border-box;
	box-sizing: border-box;
	width: 100%;
	height: 80%;
	resize:none;
}
</style>

<script>

var newwindow = '';

function openwin(url) {

    var winl = (screen.width-400)/2;
    var wint = (screen.height-800)/2;
    var settings = 'height=800,width=400,directories=no,resizable=yes,status=yes,scrollbars=yes,menubar=no,location=no,top=' + wint + ',left=' + winl;
    newwindow=window.open(url,'sim',settings);
    newwindow.focus();
}

function form_submit(thisform) {
    thisform.ss.value="";
    thisform.submit();
}

</script>
</head>
<body>
	<%
	AdminRequest aReq = new AdminRequest(request);
	String regionid=aReq.get("regionid");
	String dm=aReq.get("dm");
	String ss=aReq.get("ss");
	String domainlink=aReq.get("domainlink");
	String fileContent="";	
    String selectedService=aReq.get("selectService");  
    String templateName=aReq.get("templateName");
    System.out.println("Seelected SErvice :"+selectedService);
    
    
    
         ParevaStartup reload=null;
         UmeFilterInitialise initialisefilter=null;
        
        try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      reload=(ParevaStartup)ac.getBean("umestartup");
      initialisefilter=(UmeFilterInitialise)ac.getBean("filterinitialise");
      }
      catch(Exception e){
          e.printStackTrace();
      }
	
	if(ss.equals("1")){
		fileContent=request.getParameter("templateContent");
                regionid=aReq.get("regionid");
		try{
		File updatedTemplate=new File(request.getParameter("templateLocation")+"/"+request.getParameter("templateName"));
		updatedTemplate.delete();
		FileUtils.writeStringToFile(updatedTemplate,fileContent);
                String username="alex";
                String password="alex123";
                initialisefilter.initializeFilter();
                reload.init(this.getServletConfig().getClass().newInstance()); //reload.reInitialise(username,password);
		//ServerParam appServerParameters=new ServerParam();
		//appServerParameters.doFullInit(getServletContext());
		}catch(Exception e){
		//	System.out.println("Error Opening/Writing File");
		}
	}
	
	FileInputStream fileInputStream = new FileInputStream(request.getParameter("templateLocation")+"/"+request.getParameter("templateName")); 
	
	try{
		fileContent = IOUtils.toString(fileInputStream,"UTF-8");
	}catch(Exception e){
		System.out.println("Error Reading Template");
		
	}
	finally {
    	fileInputStream.close();
    }
	
	java.util.List jspList=new ArrayList();
	String sqlstr="select aName,aServiceCode from clientServices c, services s where s.aServiceUnique=c.aSrvcUnique and aDomain='"+dm+"' and aStatus='1'";
	Query query=dbsession.createSQLQuery(sqlstr).addScalar("aName").addScalar("aServiceCode");
	java.util.List result=query.list();
	
	for(Object o:result)
	{
		Object[] row=(Object[]) o;
		HashMap serviceMap=new HashMap();
		serviceMap.put("serviceName",String.valueOf(row[0]));
		serviceMap.put("jspName",String.valueOf(row[1]));
		jspList.add(serviceMap);
		
		
	}

	%>

	
	<b>Editing <%=templateName%></b></br>
	
	<a style="float:right;" href="index.jsp?regionid=<%=regionid%>&dm=<%=dm%>">Back</a><br />
	<br />
	<form action="editTemplatePage.jsp" method="post">
	<%-- Select Service: <select name="selectService" id="selectService" onChange="javascript:form_submit(this.form);">
            <option value="" selected>Select Service</option>
            <%for(int k=0;k<jspList.size();k++){
            HashMap serviceMap=(HashMap)jspList.get(k);%>
			<option value="<%=serviceMap.get("jspName")%>"><%=serviceMap.get("serviceName")%></option>
			
			<%}%> 
			
			</select> --%>
			
			Select Service: <select name="selectService" id="selectService" onChange="javascript:form_submit(this.form);">
            <option value="" selected>Select Service</option>
            
			<option value="video" <%if (selectedService.equals("video")) {%>selected<%} %>>video</option>
			<option value="landing" <%if (selectedService.equals("landing")) {%>selected<%} %>>landing</option>
			
			
			
			</select>
		<textarea name="templateContent"><%=fileContent%></textarea>
		<br />
		<br />
		<input type="hidden" name="ss" value="1">
		<input type="hidden" name="templateLocation" value="<%=request.getParameter("templateLocation")%>">
		<input type="hidden" name="templateName" value="<%=request.getParameter("templateName")%>"> 
                <input type="hidden" name="regionid" value="<%=request.getParameter("regionid")%>">
                <input type="hidden" name="dm" value="<%=request.getParameter("dm")%>">
                <input type="hidden" name="domainlink" value="<%=request.getParameter("domainlink")%>">
		<input type="submit" value="Save">
		<%if(selectedService.equals("")) {%>
		<input type="button" value="preview" disabled>
		<%} else {%>
		<input type="button" value="preview" onclick="javascript:openwin('<%=domainlink%>simulator.jsp?p=<%=selectedService%>&n=<%=templateName.substring(0,templateName.indexOf("."))%>');"">
		<%} %>
		
		
	</form>
</body>
</html>