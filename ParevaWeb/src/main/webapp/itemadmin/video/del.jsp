<%@include file="/WEB-INF/jspf/itemadmin/video/del.jspf"%>
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
<% if (!iGroup.equals("")) { %>
    <b>Are you sure you want to delete these images?</b>
    <br><br>
    <a href="javascript:load('<%=imgPage%>?delpic=<%=iGroup%>&unq=<%=unique%>&itype=<%=itype%>')" class="small_blue">Delete</a>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="javascript:window.close()" class="small_blue">Cancel</a>
<% } else { %>
    <b>Are you sure you want to delete this image?</b>
    <br><br>
    <a href="javascript:load('<%=imgPage%>?delpic=<%=picid%>&runq=<%=runq%>&unq=<%=unique%>&itype=<%=itype%>')" class="small_blue">Delete</a>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="javascript:window.close()" class="small_blue">Cancel</a>
<% } %>
</div>
</body>
</html>




