
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

function loadTemplate(themeLocation,templateName){
	document.getElementById("themeLocation").value=themeLocation;
	document.getElementById("templateName").value=templateName;
	document.getElementById("ss").value="";
	document.forms['form1'].submit();
	
}


</script>
</head>
<body>
	<%
	AdminRequest aReq = new AdminRequest(request);
//	String regionid=aReq.get("regionid");
//	String dm=aReq.get("dm");
	String ss=aReq.get("ss");
//	String domainlink=aReq.get("domainlink");
	//String fileContent="";	
String themeLocation=aReq.get("themeLocationInput");  
String fileContent=aReq.get("templateContent"); 
    String templateName=aReq.get("templateName");
//    System.out.println("Seelected SErvice :"+selectedService);
    
        
        try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      
      }
      catch(Exception e){
          e.printStackTrace();
      }
	
	if(ss.equals("1")){
		
  //              regionid=aReq.get("regionid");
		try{
			System.out.println("Tyring to save file");
		File updatedTemplate=new File(themeLocation+"/"+templateName);
		updatedTemplate.delete();
		
		FileUtils.writeStringToFile(updatedTemplate,fileContent);
                String username="alex";
                String password="alex123";
                System.out.println("file saved");
               
		//ServerParam appServerParameters=new ServerParam();
		//appServerParameters.doFullInit(getServletContext());
		}catch(Exception e){
			System.out.println("Error Opening/Writing File");
		}
	}
	
	File templateLocation=new File(themeLocation);
	String[] templates=templateLocation.list();
	
	if(!templateName.equals("")){
	FileInputStream fileInputStream = new FileInputStream(themeLocation+"/"+templateName); 
	
	try{
		 		
		fileContent = IOUtils.toString(fileInputStream,"UTF-8");
	}catch(Exception e){
		System.out.println("Error Reading Template");
		
	}
	finally {
    	fileInputStream.close();
    }
	}
	
/* 	java.util.List jspList=new ArrayList();
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
 */
	%>

	<%if (!templateName.equals("")){ %>
		<b>Editing <%=templateName%></b></br>
	<%} %>
<%-- //	<a style="float:right;" href="index.jsp?regionid=<%=regionid%>&dm=<%=dm%>">Back</a><br />
 --%>	<br />
	<form name="form1" id="form1" action="themeEditor.jsp" method="post">
	
	<%-- Select Service: <select name="selectService" id="selectService" onChange="javascript:form_submit(this.form);">
            <option value="" selected>Select Service</option>
            <%for(int k=0;k<jspList.size();k++){
            HashMap serviceMap=(HashMap)jspList.get(k);%>
			<option value="<%=serviceMap.get("jspName")%>"><%=serviceMap.get("serviceName")%></option>
			
			<%}%> 
			
			</select> 
			
			Select Service: <select name="selectService" id="selectService" onChange="javascript:form_submit(this.form);">
            <option value="" selected>Select Service</option>
            
			<option value="video" <%if (selectedService.equals("video")) {%>selected<%} %>>video</option>
			<option value="landing" <%if (selectedService.equals("landing")) {%>selected<%} %>>landing</option>
			
			
			
			</select>
			--%>
			<div>
				<%for(int i=0;i<templates.length;i++){%>
						
				<a href="javascript:loadTemplate('<%=themeLocation%>','<%=templates[i]%>')"><%=templates[i]%></a>
					
					<%}%>
			
			</div>
			
		<textarea name="templateContent"><%=fileContent%></textarea>
		<br />
		<br />
		<input type="hidden" id="ss" name="ss" value="1">
		<input type="hidden" id="themeLocation" name="themeLocationInput" value="<%=themeLocation%>">
		<input type="hidden" id="templateName" name="templateName" value="<%=templateName%>"> 
        <%--         <input type="hidden" name="regionid" value="<%=request.getParameter("regionid")%>">
                <input type="hidden" name="dm" value="<%=request.getParameter("dm")%>">
                <input type="hidden" name="domainlink" value="<%=request.getParameter("domainlink")%>"> --%>
		<input type="submit" value="Save">
	<%-- 	<%if(selectedService.equals("")) {%>
		<input type="button" value="preview" disabled>
		<%} else {%>
		<input type="button" value="preview" onclick="javascript:openwin('<%=domainlink%>simulator.jsp?p=<%=selectedService%>&n=<%=templateName.substring(0,templateName.indexOf("."))%>');"">
		<%} %>
	 --%>	
		
	</form>
</body>
</html>