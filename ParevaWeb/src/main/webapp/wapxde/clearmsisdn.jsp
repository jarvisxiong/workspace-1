<%@include file="coreimport.jsp"%>
<%
//***************************************************************************************************
UmeSessionParameters aReq = new UmeSessionParameters(request);
UmeUser user = aReq.getUser();
UmeDomain dmn = aReq.getDomain();
SdcService service = aReq.getService();


String domain = dmn.getUnique();
String ddir = dmn.getDefPublicDir();
String lang = aReq.getLanguage().getLanguageCode();

String stylesheet = aReq.getStylesheet();
String pageEnc = aReq.getEncoding();
response.setContentType("text/html; charset=" + pageEnc);

String fileName = request.getServletPath();
fileName = fileName.substring(fileName.lastIndexOf("/")+1);
fileName = fileName.substring(0,fileName.lastIndexOf("."));


HandsetDao handsetdao=null;
UmeTempCache anyxsdc=null;
UmeLanguagePropertyDao langpropdao=null;
Handset handset = (Handset)session.getAttribute("handset");
MobileClubDao mobileclubdao=null;
VideoClipDao videoclipdao=null;
PebbleEngine engine=null;
Misc misc=null;
UmeClubDetails clubdetails=null;
MobileClubCampaignDao campaigndao=null;
UmeUserDao umeuserdao=null;
UmeMobileClubUserDao umemobileclubuserdao=null;
UmeSmsDao umesmsdao=null;
 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      handsetdao=(HandsetDao) ac.getBean("handsetdao");
      anyxsdc=(UmeTempCache) ac.getBean("umesdc");
      langpropdao=(UmeLanguagePropertyDao) ac.getBean("umelanguagepropertydao");
      mobileclubdao=(MobileClubDao) ac.getBean("mobileclubdao");
      videoclipdao=(VideoClipDao) ac.getBean("videoclipdao");
      misc=(Misc) ac.getBean("misc");
      umeuserdao=(UmeUserDao) ac.getBean("umeuserdao");
      umemobileclubuserdao=(UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
      campaigndao=(MobileClubCampaignDao) ac.getBean("mobileclubcampaigndao");
      umesmsdao=(UmeSmsDao) ac.getBean("umesmsdao");
      //za_engine=(PebbleEngine) ac.getBean("pebbleEngine");
      }
      catch(Exception e){
          e.printStackTrace();
      }
 
SdcLanguageProperty lp = langpropdao.get("general", service, aReq.getLanguage(), dmn);
//***************************************************************************************************

String status = "";

System.out.println("SESSION: " + session);

session = request.getSession(true);
System.out.println("SESSION: " + session);
if (session!=null) {
    session.removeAttribute("sdc_msisdn_param");
    status = "MSISDN removed from session";
}
else status = "System error";


%>
<html>
<body>
<div id="pgframe" align="center"><div class="container">

<div class="item" align="left">
    <br/>
    <%=status%>

    <br/><br/>
</div>


</div></div>
</body>
</html>
