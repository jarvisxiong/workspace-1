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
if (pageEnc == null) pageEnc = "ISO-8859-1";
response.setContentType("text/html; charset=" + pageEnc);

String fileName = "general";
//LangProps lp = LangProps.getFromContext(anyxSrvc, fileName, lang, domain, Misc.cxt);
//Properties p = ServiceProperties.getFromContext(anyxSrvc, "service", Misc.cxt);
//String picLang = lp.getLang();
//***************************************************************************************************

int cs=4;
String bgColor="";
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
</body>

<table cellspacing="0" cellpadding="0" border="0" width="100%" height="100%">
<tr>
<td valign="top" align="left">


<table cellspacing="0" cellpadding="5" border="0">
<tr>
	<td colspan="<%=cs/2%>" align="left" valign="bottom"><img src="/images/accessdenied_title_en.gif"></td>	
	<td colspan="<%=cs/2%>" align="left" valign="bottom">&nbsp;</td>
</tr>

<tr><td colspan="<%=cs%>"><img src="/images/red_dot.gif" height="1" width="100%"></td></tr>
<tr>
	<td colspan="<%=cs%>">You need to be a registered user to be able to use this service.<br></td>
</tr>

<tr><td colspan="<%=cs%>"><img src="/images/red_dot.gif" height="1" width="100%"></td></tr>

</table> 

</td>
</tr>				
</table>	

</body>
</html>


