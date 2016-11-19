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
CpaVisitLogDao cpavisitlogdao=null;
ZACPA zacpalog=null;
QuizSmsDao quizsmsdao=null;
MobileClubBillingDao mobileclubbillingdao=null;
MobileNetworksDao mobilenetwork=null;
PassiveVisitorDao passivevisitordao=null;

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
           cpavisitlogdao=(CpaVisitLogDao) ac.getBean("cpavisitlogdao");
            zacpalog=(ZACPA) ac.getBean("zacpa");
            quizsmsdao=(QuizSmsDao) ac.getBean("quizsmsdao");
            mobileclubbillingdao=(MobileClubBillingDao) ac.getBean("mobileclubbillingdao");
            mobilenetwork=(MobileNetworksDao) ac.getBean("mobilenetworkdao");
            passivevisitordao=(PassiveVisitorDao) ac.getBean("passivevisitor");
	  
	   
      }
      catch(Exception e){
          e.printStackTrace();
      }

SdcLanguageProperty lp = langpropdao.get("general", service, httprequest.getLanguage(), dmn);
//***************************************************************************************************
%>
