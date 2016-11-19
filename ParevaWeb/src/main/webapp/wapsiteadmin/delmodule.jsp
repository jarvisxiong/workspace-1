<%@ include file="/WEB-INF/jspf/wapsiteadmin/delmodule.jspf" %>

<html>
<head>
<title>Delete Item</title>
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
<% if (!srvc.equals("")) { %>

        <b>Are you sure you want to delete this service module and all related information?</b>
	<br><br>
        <a href="javascript:load('index.jsp?cmd=del&srvc=<%=srvc%>&ind=<%=ind%>&regionid=<%=regionid%>')" class="small_blue">Delete</a>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<a href="javascript:window.close()" class="small_blue">Cancel</a>
<% } %>

</div>
</body>
</html>




