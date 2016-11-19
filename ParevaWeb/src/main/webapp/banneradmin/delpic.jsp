<%@ include file="/WEB-INF/jspf/coreimport.jspf" %>

<%
//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String uid = aReq.getUserId();
String ugid = aReq.getUserGroup();
String anyxSrvc = aReq.getAnyxService();
String domain = aReq.getDomain();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "iso-8859-1";
response.setContentType("text/html; charset=" + pageEnc);

String fileName = "del";
//***************************************************************************************************

String iGroup = aReq.get("igroup");
String picid = aReq.get("picid");
String runq = aReq.get("runq");
String unique = aReq.get("unq");
String imgType = aReq.get("itype");
String fp = aReq.get("fp");
String imgPage = "images.jsp";

%>
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
    <a href="javascript:load('<%=imgPage%>?delpic=<%=iGroup%>&unq=<%=unique%>&itype=<%=imgType%>')" class="small_blue">Delete</a>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="javascript:window.close()" class="small_blue">Cancel</a>
<% } else { %>
    <b>Are you sure you want to delete this image?</b>
    <br><br>
    <a href="javascript:load('<%=imgPage%>?delpic=<%=picid%>&runq=<%=runq%>&unq=<%=unique%>&itype=<%=imgType%>')" class="small_blue">Delete</a>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="javascript:window.close()" class="small_blue">Cancel</a>
<% } %>
</div>
</body>
</html>




