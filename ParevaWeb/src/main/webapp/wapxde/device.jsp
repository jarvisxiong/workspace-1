<%@include file="coreimport.jsp"%>
<%@ page import="java.util.*, java.lang.Math, java.sql.Connection, java.sql.ResultSet, java.text.*" %>
<%

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
LangProps lp = LangProps.getFromContext(domain + "_pub", "general", lang, domain, application, true);
//***************************************************************************************************

System.out.println("############# HTTP HEADERS");
Enumeration ee = request.getHeaderNames();
for (;ee.hasMoreElements();) {
    String elem = (String) ee.nextElement();
    System.out.println(elem + ": " + request.getHeader(elem));
}
System.out.println("END ############# HTTP HEADERS");


System.out.println("############# HTTP PARAMETERS");
ee = request.getParameterNames();
for (;ee.hasMoreElements();) {
    String elem = (String) ee.nextElement();
    System.out.println(elem + ": " + request.getParameter(elem));
}
System.out.println("END ############# HTTP PARAMETERS");

Handset handset = HandsetDao.getHandset(request, false);
response.setContentType(handset.getContentType(pageEnc));

System.out.println(HandsetDao.getXml(handset));

%><%=handset.getInfo()%>