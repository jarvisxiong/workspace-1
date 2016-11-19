<%@include file="coreimport.jsp"%>
<%@include file="readparam.jsp"%>
<%
HandsetDao handsetdao=null;
UmeTempCache tempcache=null;
UmeLanguagePropertyDao langpropdao=null;
Handset handset = (Handset)session.getAttribute("handset");
MobileClubDao mobileclubdao=null;
VideoClipDao videoclipdao=null;
PebbleEngine engine=null;
Misc misc=null;
MobileClubCampaignDao campaigndao=null;
VideoListUK videolistuk=null;
 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      handsetdao=(HandsetDao) ac.getBean("handsetdao");
      tempcache=(UmeTempCache) ac.getBean("umesdc");
      langpropdao=(UmeLanguagePropertyDao) ac.getBean("umelanguagepropertydao");
      mobileclubdao=(MobileClubDao) ac.getBean("mobileclubdao");
      videoclipdao=(VideoClipDao) ac.getBean("videoclipdao");
      misc=(Misc) ac.getBean("misc");
      campaigndao=(MobileClubCampaignDao) ac.getBean("mobileclubcampaigndao");
      engine=(PebbleEngine) ac.getBean("pebbleEngine");
      videolistuk=(VideoListUK) ac.getBean("videolistuk");
      }
      catch(Exception e){
          e.printStackTrace();
      }

SdcLanguageProperty lp = langpropdao.get("general", service, httprequest.getLanguage(), dmn);

//***************************************************************************************************

System.out.println("UK Domain "+domain+" domain url "+dmn.getDefaultUrl());
Map<String, String> dParamMap = UmeTempCmsCache.domainParameters.get(domain);
if (dParamMap==null) dParamMap = new HashMap<String,String>();

MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
Map dImages = (Map) UmeTempCmsCache.xhtmlImages.get("xhtml_" + domain);

String campaignId = httprequest.get("cid");
MobileClubCampaign cmpg = null;
if (!campaignId.equals("")) cmpg = UmeTempCmsCache.campaignMap.get(campaignId);

String uid = "";
String wapid = "";

String ip = request.getRemoteAddr();
String subpage = httprequest.get("pg");
request.setAttribute("subpage",subpage);

HttpSession ses = request.getSession();
String msisdn = "";

if(handset==null){
try{
handset=handsetdao.getHandset(request);
session.setAttribute("handset",handset);
}
catch(Exception e){e.printStackTrace();}
}

//FileLoader uk_template_loader = new FileLoader();
//uk_template_loader.setPrefix("/opt/pareva/templates/parsed/UK/uktemplate");
//uk_template_loader.setSuffix(".html");
//engine = new PebbleEngine(uk_template_loader);
//EscaperExtension uk_escaper = engine.getExtension(EscaperExtension.class);
//uk_escaper.setAutoEscaping(false);
//this.getServletContext().setAttribute("uk_engine",engine);

FileLoader uk_template_loader = new FileLoader();
uk_template_loader.setPrefix("/opt/pareva/templates/parsed/UK/pornomonkey");
uk_template_loader.setSuffix(".html");
engine = new PebbleEngine(uk_template_loader);
EscaperExtension uk_escaper = engine.getExtension(EscaperExtension.class);
uk_escaper.setAutoEscaping(false);
this.getServletContext().setAttribute("uk_engine",engine);
%>
