<%@include file="/WEB-INF/jspf/coreimport.jspf"%>

<%
//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String uid = aReq.getUserId();
String ugid = aReq.getUserGroup();
String anyxSrvc = aReq.getAnyxService();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "iso-8859-1";
response.setContentType("text/html; charset=" + pageEnc);

String fileName = "updateredirect";
//***************************************************************************************************

String domains = aReq.get("domains");
String defaulturl = aReq.get("defaulturl");
String redirecturl=aReq.get("redirecturl");
String updateurl=aReq.get("rdirect");

%>

<html>
<head>
<title>Update Redirect link</title>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language="JavaScript">
<!--
function load(file) {
    top.opener.window.location.href = file;
    self.close();
}
//--></script>
</head>
<body>
<div align="center" class="grey_11">
<% if (!domains.equals("")) { %>

        <b>Please click update to change the redirection setting</b>
        <br>
        <%=defaulturl%> to <%=updateurl%>
	<br><br>
<!--        <a href="javascript:load('redirectSettings.jsp?domains=<%=domains%>&defaulturl=<%=defaulturl%>&redirecturl=<%=redirecturl%>&updateurl=<%=updateurl%>')" class="small_blue">-->
        <a href="javascript:load('redirectSettings.jsp')">
            <%
            Connection con = DBHStatic.getConnection();
            String sqlstr = "UPDATE redirectsetting set aRedirectUrl='"+updateurl+"' where aDomainUnique='"+domains+"' AND aDefaultUrl='"+defaulturl+"'";
            DBHStatic.execUpdate(con, sqlstr);
            DBHStatic.closeConnection(con);
            %>
            
            Update</a>
	<% } %>

</div>
</body>
</html>




