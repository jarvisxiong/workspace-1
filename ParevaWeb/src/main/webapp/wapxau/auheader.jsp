<%@include file="coreimport.jsp"%>
<%@include file="readparam.jsp"%>
<%@include file="cpavisit.jsp"%>
<%
HandsetDao handsetdao=null;
UmeTempCache tempcache=null;
UmeLanguagePropertyDao langpropdao=null;
Handset handset = (Handset)session.getAttribute("handset");
MobileClubDao mobileclubdao=null;
VideoClipDao videoclipdao=null;
PebbleEngine au_engine=null;
Misc misc=null;
MobileClubCampaignDao campaigndao=null;
VideoListUK videolistuk=null;
LandingPage landingpage=null;
CampaignHitCounterDao campaignhitcounterdao=null;
UKSuccessDao uksuccessdao=null;
CpaAdvertiserDao advertiserdao=null;
CpaVisitLogDao cpavisitlogdao=null;
ZACPA zacpa=null;
RevShareVisitorLogDao revvisitorlogdao=null;
RevSharePartersDao revsharepartnersdao=null;
RevSharePayoutDao revsharepayoutdao=null;
UmeMobileClubUserDao umemobileclubuserdao=null;
InternetServiceProvider ipprovider=null;

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
      au_engine=(PebbleEngine) ac.getBean("pebbleEngine");
      videolistuk=(VideoListUK) ac.getBean("videolistuk");
      landingpage=(LandingPage) ac.getBean("landingpage");
      campaignhitcounterdao=(CampaignHitCounterDao) ac.getBean("campaignhitcounterdao");
      uksuccessdao=(UKSuccessDao) ac.getBean("uksuccessdao");
      zacpa=(ZACPA) ac.getBean("zacpa");
      revvisitorlogdao=(RevShareVisitorLogDao) ac.getBean("revsharevisitorlogdao");
     revsharepartnersdao=(RevSharePartersDao) ac.getBean("revsharepartnerdao");
     advertiserdao=(CpaAdvertiserDao) ac.getBean("cpaadvertiserdao");
     cpavisitlogdao=(CpaVisitLogDao) ac.getBean("cpavisitlogdao");
     revsharepayoutdao=(RevSharePayoutDao) ac.getBean("revsharepayoutdao");
     umemobileclubuserdao=(UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
     ipprovider=(InternetServiceProvider) ac.getBean("internetserviceprovider");
      }
      catch(Exception e){
          e.printStackTrace();
      }

SdcLanguageProperty lp = langpropdao.get("general", service, httprequest.getLanguage(), dmn);

//***************************************************************************************************

//System.out.println("xstreamtesting: on ukheader Domain "+domain+" domain url "+dmn.getDefaultUrl());
Map<String, String> dParamMap = UmeTempCmsCache.domainParameters.get(domain);
if (dParamMap==null) dParamMap = new HashMap<String,String>();

MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
Map dImages = (Map) UmeTempCmsCache.xhtmlImages.get("xhtml_" + domain);

String clubUnique = "";
if (club!=null) clubUnique = club.getUnique();

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
    request.setAttribute("handset",handset);
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
Map<String,TemplatePojo> templates=new HashMap<String,TemplatePojo>();
if(getServletContext().getAttribute("au_engine")==null){
    
    templates=tempcache.getTemplateMap();
 
TemplatePojo template=templates.get(dmn.getUnique());
String templatefolder=template.getTemplateFolder();
FileLoader au_template_loader = new FileLoader();
//za_template_loader.setPrefix("/opt/pareva/templates/parsed/ZA/x-rated");
au_template_loader.setPrefix(templatefolder.trim());
au_template_loader.setSuffix(template.getSuffix().trim());
au_engine = new PebbleEngine(au_template_loader);
EscaperExtension za_escaper = au_engine.getExtension(EscaperExtension.class);
za_escaper.setAutoEscaping(false);
this.getServletContext().setAttribute("au_engine",au_engine);
}else{
	au_engine=(PebbleEngine)getServletContext().getAttribute("au_engine");
}





/*
FileLoader uk_template_loader = new FileLoader();
uk_template_loader.setPrefix("/opt/pareva/templates/parsed/UK/x-stream");
uk_template_loader.setSuffix(".html");
au_engine = new PebbleEngine(uk_template_loader);
EscaperExtension uk_escaper = au_engine.getExtension(EscaperExtension.class);
uk_escaper.setAutoEscaping(false);
this.getServletContext().setAttribute("au_engine",au_engine);
        */
%>
