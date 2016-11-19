<%@include file="/WEB-INF/jspf/coreimport.jspf" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
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
</head>
<body>
	<%
	AdminRequest aReq = new AdminRequest(request);
	String regionid=aReq.get("regionid");
	String dm=aReq.get("dm");
	String ss=aReq.get("ss");
	String fileContent="";	
	
	if(ss.equals("1")){
		fileContent=request.getParameter("cssContent");
		try{
		File updatedCSS=new File(request.getParameter("cssFileLocation")+"/"+request.getParameter("cssFile"));
		updatedCSS.delete();
		FileUtils.writeStringToFile(updatedCSS,fileContent);
		//ServerParam appServerParameters=new ServerParam();
		//appServerParameters.doFullInit(getServletContext());
		}catch(Exception e){
		//	System.out.println("Error Opening/Writing File");
		}
	}
	
	FileInputStream fileInputStream = new FileInputStream(request.getParameter("cssFileLocation")+"/"+request.getParameter("cssFile")); 
	
	try{
		fileContent = IOUtils.toString(fileInputStream);
	}catch(Exception e){
		System.out.println("Error Reading CSS file");
		
	}
	finally {
    	fileInputStream.close();
    }
	

	%>

	
	<b>Editing <%=request.getParameter("cssFile")%></b>
	<a style="float:right;" href="index.jsp?regionid=<%=regionid%>&dm=<%=dm%>">Back</a><br />
	<br />
	<form action="editCSS.jsp" method="post">
		<textarea name="cssContent"><%=fileContent%></textarea>
		<br />
		<br />
		<input type="hidden" name="dm" value="<%=dm%>">
		<input type="hidden" name="regionid" value="<%=regionid%>">
		<input type="hidden" name="ss" value="1">
		<input type="hidden" name="cssFileLocation" value="<%=request.getParameter("cssFileLocation")%>">
		<input type="hidden" name="cssFile" value="<%=request.getParameter("cssFile")%>">
		<input type="submit" value="Save">
	</form>
</body>
</html>