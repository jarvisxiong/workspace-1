<%@include file="/WEB-INF/jspf/coreimport.jspf"%><%
//***************************************************************************************************
SdcRequest aReq = new SdcRequest(request);
UmeUser user = aReq.getUser();
UmeDomain dmn = aReq.getDomain();
SdcService service = aReq.getService();

String domain = dmn.getUnique();
String lang = aReq.getLanguage().getLanguageCode();

String stylesheet = aReq.getStylesheet();
String pageEnc = aReq.getEncoding();
response.setContentType("text/html; charset=" + pageEnc);

String fileName = request.getServletPath();
fileName = fileName.substring(fileName.lastIndexOf("/")+1);
fileName = fileName.substring(0,fileName.lastIndexOf("."));

UmeLanguagePropertyDao langpropdao=null;
try{
ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
langpropdao=(UmeLanguagePropertyDao) ac.getBean("umelanguagepropertydao");
}
catch(Exception e){
    e.printStackTrace();
}




SdcLanguageProperty lp = langpropdao.get("general", service, aReq.getLanguage(), dmn);
//***************************************************************************************************

String pg = aReq.get("pg");

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>

<body>
<div align="center" class="grey_11">
    <img src="/images/glass_dot.gif" height="5" width="1"><br>    
    <b>Screen Size: &nbsp;&nbsp;</b>
    <a href="index.jsp?xprof=1&pg=<%=pg%>" target="content"><span class="blue_11"><b>128+</b></span></a>
    &nbsp;&nbsp;
    <a href="index.jsp?xprof=2&pg=<%=pg%>" target="content"><span class="blue_11"><b>176+</b></span></a>
    &nbsp;&nbsp;
    <a href="index.jsp?xprof=3&pg=<%=pg%>" target="content"><span class="blue_11"><b>240+</b></span></a>
    &nbsp;&nbsp;
    <a href="index.jsp?xprof=4&pg=<%=pg%>" target="content"><span class="blue_11"><b>320+</b></span></a>
</div>
</body>
