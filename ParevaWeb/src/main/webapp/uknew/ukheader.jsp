<%@page import="ume.pareva.userservice.LandingPage"%>
<%@page import="ume.pareva.userservice.InternetServiceProvider"%>
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
InternetServiceProvider ipprovider=null;
CampaignHitCounterDao campaignhitcounterdao=null;
LandingPage landingpage=null;
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
      ipprovider=(InternetServiceProvider) ac.getBean("internetserviceprovider");
      landingpage=(LandingPage) ac.getBean("landingpage");
      campaignhitcounterdao=(CampaignHitCounterDao) ac.getBean("campaignhitcounterdao");
      }
      catch(Exception e){
          e.printStackTrace();
      }

SdcLanguageProperty lp = langpropdao.get("general", service, httprequest.getLanguage(), dmn);

//***************************************************************************************************

//System.out.println("UK Domain "+domain+" domain url "+dmn.getDefaultUrl());
Map<String, String> dParamMap = null;
try{
UmeTempCmsCache.domainParameters.get(domain);
}catch(Exception e){}
if (dParamMap==null) dParamMap = new HashMap<String,String>();

MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
Map dImages = (Map) UmeTempCmsCache.xhtmlImages.get("xhtml_" + domain);

String campaignId ="";

try{
    campaignId=(String) request.getAttribute("campaignid");
    if(campaignId==null) campaignId=(String) session.getAttribute("campaignid");
    if(campaignId==null || campaignId.equalsIgnoreCase(""))
    {
        campaignId=httprequest.get("cid");
    }
}
catch(Exception e){campaignId="";}

MobileClubCampaign cmpg = null;
if (campaignId!=null && !campaignId.equals("")) {    
    cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
    
    request.setAttribute("campaignid",campaignId);
    session.setAttribute("campaignid",campaignId);
    
}

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
uk_template_loader.setPrefix("/opt/pareva/templates/parsed/UK/18stream");
uk_template_loader.setSuffix(".html");
engine = new PebbleEngine(uk_template_loader);
EscaperExtension uk_escaper = engine.getExtension(EscaperExtension.class);
uk_escaper.setAutoEscaping(false);
this.getServletContext().setAttribute("uk_engine",engine);
%>
