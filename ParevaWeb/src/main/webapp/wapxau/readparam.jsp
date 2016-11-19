<%
UmeSessionParameters httprequest = new UmeSessionParameters(request);
UmeUser user = httprequest.getUser();
String lang = httprequest.getLanguage().getLanguageCode();
UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
SdcService service = (SdcService) request.getAttribute("umeservice");

String domain = dmn.getUnique();
String ddir = dmn.getDefPublicDir();
String durl=dmn.getDefaultUrl().toString().trim();
String cloudfronturl=dmn.getContentUrl();
session.setAttribute("cloudfrontUrl",cloudfronturl);
application.setAttribute("cloudfrontUrl",cloudfronturl);

%>