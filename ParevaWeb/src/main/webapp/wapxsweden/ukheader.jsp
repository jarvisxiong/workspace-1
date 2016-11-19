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
VideoList videolist=null;
UmeQuizDao umequizdao=null;
ValidationUtil validationutil=null;
UmeSmsDao umesmsdao=null;
UmeUserDao umeuserdao=null;
UmeMobileClubUserDao umemobileclubuserdao=null;
MobileClubBillingPlanDao billingplandao=null;
NetworkMapping networkMapping=null;
QuizValidationDao quizvalidationdao=null;
CpaAdvertiserDao advertiserdao=null;
CpaVisitLogDao cpavisitlogdao=null;
LandingPage landingpage=null;
CampaignHitCounterDao campaignhitcounterdao=null;
RevShareVisitorLogDao revvisitorlogdao=null;
RevSharePartersDao revsharepartnersdao=null;
InternetServiceProvider ipprovider=null;
ZACPA zacpalog=null;
QuizSmsDao quizsmsdao=null;
TemplateEngine templateengine=null;
PassiveVisitorDao passivevisitordao=null;
MobileNetworksDao mobilenetwork=null;

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
	   videolist=(VideoList) ac.getBean("videolist");
	   umequizdao=(UmeQuizDao) ac.getBean("umequizdao");
	   validationutil=(ValidationUtil) ac.getBean("validationutil");
	   umesmsdao=(UmeSmsDao) ac.getBean("umesmsdao");
	   umeuserdao=(UmeUserDao) ac.getBean("umeuserdao");
	   umemobileclubuserdao=(UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
	   billingplandao=(MobileClubBillingPlanDao) ac.getBean("billingplandao");
	   networkMapping=(NetworkMapping) ac.getBean("networkMapping");
           quizvalidationdao=(QuizValidationDao) ac.getBean("quizvalidationdao");
           advertiserdao=(CpaAdvertiserDao) ac.getBean("cpaadvertiserdao");
           cpavisitlogdao=(CpaVisitLogDao) ac.getBean("cpavisitlogdao");
           landingpage=(LandingPage) ac.getBean("landingpage");
           campaignhitcounterdao=(CampaignHitCounterDao) ac.getBean("campaignhitcounterdao");
           revvisitorlogdao=(RevShareVisitorLogDao) ac.getBean("revsharevisitorlogdao");
           revsharepartnersdao=(RevSharePartersDao) ac.getBean("revsharepartnerdao");
           ipprovider=(InternetServiceProvider) ac.getBean("internetserviceprovider");
           zacpalog=(ZACPA) ac.getBean("zacpa");
           quizsmsdao=(QuizSmsDao) ac.getBean("quizsmsdao");
           templateengine=(TemplateEngine) ac.getBean("templateengine");
            passivevisitordao=(PassiveVisitorDao) ac.getBean("passivevisitor");
            mobilenetwork=(MobileNetworksDao) ac.getBean("mobilenetworkdao");
	  
	   
      }
      catch(Exception e){
          e.printStackTrace();
      }

SdcLanguageProperty lp = langpropdao.get("general", service, httprequest.getLanguage(), dmn);
if(handset==null)
{
    handset = handsetdao.getHandset(request);
}
if(handset!=null) 
{    session.setAttribute("handset",handset);
    request.setAttribute("handset",handset);    
    //System.out.println("zahandsetinfo "+handset.getUnique());
}

Map<String,TemplatePojo> templates=new HashMap<String,TemplatePojo>();

    
    templates=tempcache.getTemplateMap();
 
TemplatePojo template=templates.get(dmn.getUnique());
String templatefolder=template.getTemplateFolder();
FileLoader irequiz_template_loader = new FileLoader();
//za_template_loader.setPrefix("/opt/pareva/templates/parsed/ZA/x-rated");
irequiz_template_loader.setPrefix(templatefolder.trim());
irequiz_template_loader.setSuffix(template.getSuffix().trim());
engine = new PebbleEngine(irequiz_template_loader);
EscaperExtension escaper = engine.getExtension(EscaperExtension.class);
escaper.setAutoEscaping(false);
this.getServletContext().setAttribute("irequiz_engine",engine);


//***************************************************************************************************
%>
