<%@include file="/WEB-INF/jspf/coreimport.jspf"%>

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

String fileName = "upload";
//***************************************************************************************************

String imgunq = aReq.get("imgunq");
String unique = aReq.get("unq");
String imgType = aReq.get("itype");
String fp = aReq.get("fp");
String imgPage = "images.jsp";

%>

<html>
<head>
<title>Upload Item</title>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language="JavaScript">
<!--
function sendForm(thisform) {
    thisform.submit();
    self.close();
}
//--></script>
</head>
<body>
<div align="center" class="grey_11">
<% if (!imgunq.equals("")) { %>

<table cellspacing="0" cellpadding="2" border="0" width="95%">
<tr><td>
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
        <td align="left" valign="bottom" class="big_blue">Upload New Image</td>
	<td align="right" valign="bottom" class="red_11">
        <a href="<%=imgPage%>?unq=<%=unique%>&itype=<%=imgType%>">Back</a>
    </td>
	</tr>
	</table>
</td></tr>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>
<tr><td>

<form enctype="multipart/form-data" action="<%=imgPage%>" method="post">
        <input type="hidden" name="unq" value="<%=unique%>">
        <input type="hidden" name="itype" value="<%=imgType%>">
        <input type="hidden" name="imgunq" value="<%=imgunq%>">

    <table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr bgcolor="#FFFFFF">
           <td align="left" class="grey_12">
           Replace with new image:
           </td>
           <td align="right">
                <input type="file" size="20" name="replimg" class="textbox" value="">
                <input type="submit" name="submit" value="Upload">
           </td>
        </tr>
    
    </table>

</form>
</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>
</table>
<% } %>

</div>
</body>
</html>




