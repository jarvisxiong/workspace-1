<%@ page import="com.mixmobile.anyx.sdk.*, com.mixem.sdc.*, com.mixem.sdc.sms.*, java.util.*, java.lang.Math, com.mixmobile.anyx.cms.*,com.ipxbillingextra.*,
            java.sql.Connection, java.sql.ResultSet, java.text.*,
            com.ipx.www.api.services.subscriptionapi31.types.*, com.ipx.www.api.services.subscriptionapi31.*,
            com.ipx.www.api.services.identificationapi31.types.*, com.ipx.www.api.services.identificationapi31.*,
            com.ipx.www.api.services.smsapi52.*, com.ipx.www.api.services.smsapi52.types.*, java.net.*, java.io.*, org.apache.commons.*, org.jsoup.Jsoup, 
            org.jsoup.nodes.Document, org.jsoup.select.Elements" %><%

SdcRequest aReq = new SdcRequest(request);
SdcUser user = aReq.getUser();
SdcDomain dmn = aReq.getDomain();
SdcService service = aReq.getService();
String domain = dmn.getUnique();
String ddir = dmn.getDefPublicDir();
String lang = aReq.getLanguage().getLanguageCode();
String stylesheet = aReq.getStylesheet();
String pageEnc = aReq.getEncoding();
response.setContentType("text/html; charset=" + pageEnc);
SdcLanguageProperty lp = SdcLanguagePropertyDao.get("general", service, aReq.getLanguage(), dmn);
%>
<html>
<body>Ci dispiace! Riprova più tardi</body>
</html>