<%@include file="coreimport.jsp"%>
<%@include file="commonfunc.jsp"%>
<%
//UmeSessionParameters aReq = new UmeSessionParameters(request);
//UmeUser user = httprequest.getUser();
//String lang = httprequest.getLanguage().getLanguageCode();
//UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
//SdcService service = (SdcService) request.getAttribute("umeservice");

//String domain = dmn.getUnique();
//String ddir = dmn.getDefPublicDir();
//String durl=dmn.getDefaultUrl().toString().trim();
//String cloudfronturl=dmn.getContentUrl();
TemplateEngine templateengine=null;
MobileClubCampaign cmpg = null;
String campaignsrc="";
String visitsubscribed="1970-01-01 00:00:00";
//String ipaddress=request.getRemoteAddr();
String isSubscribed="0";
String transaction_ref="incpa";
String msisdn="";

//HandsetDao handsetdao=null;
try{
    ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
    ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
    templateengine=(TemplateEngine) ac.getBean("templateengine");
    handsetdao=(HandsetDao) ac.getBean("handsetdao");
      
    }
    catch(Exception e){
        e.printStackTrace();
    }

String myip=request.getHeader("X-Forwarded-For");

if (myip != null) {           
           int idx = myip.indexOf(',');
           if (idx > -1) {
           myip = myip.substring(0, idx);
           }
    }

session.setAttribute("userip",myip);
request.setAttribute("userip",myip);
Handset handset = handsetdao.getHandset(request);
//String lang = aReq.getLanguage().getLanguageCode();
if(handset!=null) 
{
    session.setAttribute("handset",handset);
    request.setAttribute("handset",handset);
}


String previewLandingPage=aReq.get("previewLandingPage");
if(""!=previewLandingPage){
previewLandingPage=previewLandingPage.substring(0,previewLandingPage.indexOf("."));
System.out.println("previewLandingPage: "+previewLandingPage);
}

if(!previewLandingPage.equals("")){
    String msisdnexist=aReq.get("msisdnexist");
    String previewNetwork=aReq.get("previewNetworkInput");
    System.out.println("previewNetwork "+previewNetwork);
	PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
	
        
        if("".equals(msisdnexist)) msisdnexist="false";
        context.put("msisdnexist",msisdnexist);

        context.put("contenturl","http://"+dmn.getContentUrl());
        context.put("operator",previewNetwork);

	
	PebbleEngine engine=templateengine.getTemplateEngine(dmn.getUnique());
	engine.getTemplate(previewLandingPage).evaluate(writer, context); 

}else{

	java.util.List<HashMap<String, Object>> videos = new ArrayList<HashMap<String, Object>>();
	videos=getVideos(domain,videolist,handset,lang);
String landingPage="unknown";
//session.setAttribute("cloudfrontUrl",cloudfronturl);
//application.setAttribute("cloudfrontUrl",cloudfronturl);
String campaignId=aReq.get("cid");

/**
 * need to comment form here ==========================
 */
try{
    landingPage=request.getParameter("landingpage");
}
catch(Exception e){landingPage="landing-default";}

if(landingPage==null || landingPage.trim().length()<=0) landingPage="landing-default";

//==================================================

MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
String clubUnique = "";
if (club!=null) clubUnique = club.getUnique();

if(campaignId!=null && campaignId.trim().length()>0) {
    
   if (!campaignId.equals("")) cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
   if(cmpg!=null) campaignsrc = cmpg.getSrc();
      
    }

//========================= Retrieving  LandingPage in this part =========================================
String vodacomHeader=request.getHeader("x-up-vfza-id");

String myisp="";
myisp=ipprovider.findIsp(myip);

/****************************************************************************************************************************************/
if(cmpg!=null && cmpg.getSrc().equalsIgnoreCase("test")) {     
    landingPage=landingpage.initializeLandingPage(domain,campaignId,"test");
    request.setAttribute("landingPage",landingPage);
           
}
else{
if(!campaignId.equals("")){
		landingPage=landingpage.initializeLandingPage(domain,campaignId,"all");
                request.setAttribute("landingPage",landingPage);
	}else {
		landingPage=landingpage.initializeLandingPage(domain);
                request.setAttribute("landingPage",landingPage);
	}
}
if(campaignsrc.trim().toLowerCase().contains("cpa")){    
//CPA Tracking Code comes here.
%>
<%@include file="cpavisit.jsp"%>
<%@include file="cpavisitlog.jsp"%>
<%}
java.sql.Date today=new java.sql.Date(System.currentTimeMillis());
CampaignHitCounter campaignHitCounter=campaignhitcounterdao.HitRecordExistsOrNot(today,domain,campaignId,landingPage);
if(campaignHitCounter==null){
	campaignHitCounter=new CampaignHitCounter();
	campaignHitCounter.setaUnique(Misc.generateUniqueId());
	campaignHitCounter.setaDomainUnique(domain);
	campaignHitCounter.setCampaignId(campaignId);
	campaignHitCounter.setLandingPage(landingPage);
	campaignHitCounter.setDate(today);
	campaignHitCounter.setHitCounter(1);
	campaignHitCounter.setSubscribeCounter(0);
	campaignhitcounterdao.saveCampaignHitCounter(campaignHitCounter);
	
}else{
	//campaignHitCounter.setDate(today);
	campaignhitcounterdao.updateCampaignHitCounter(campaignHitCounter);
}
	
 if(!aReq.get("cid").equalsIgnoreCase("")){       
       campaigndao.log("index", landingPage, msisdn,msisdn, handset,domain, campaignId, clubUnique, "INDEX", 0, request,response,myisp.toLowerCase());    
    }

PebbleEngine in_engine=templateengine.getTemplateEngine(domain);
PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();
context.put("contenturl","http://"+dmn.getContentUrl());
context.put("sendAction","http://in.ubilling.net/pay/6f314396b3a7ddd005e7af0e7d0bb676");
context.put("s","1");
context.put("z","http%3A%2F%2F"+dmn.getDefaultUrl()+"%2Fvideos.jsp");
context.put("affiliateData","landingPage%3D"+landingPage+"%7Ccid%3D"+campaignId);
context.put("videos",videos);

in_engine.getTemplate(landingPage).evaluate(writer, context);
}
%>

<%!public java.util.List<HashMap<String, Object>> getVideos(String domain,VideoList videolist,Handset handset,String lang){
		java.util.List<String[]> list = (java.util.List<String[]>) UmeTempCmsCache.clientServices
				.get(domain);
		java.util.List<String> videoCategorySrvcList = new ArrayList<String>();
		java.util.List<String> bannerSrvcList = new ArrayList<String>();
		java.util.List<HashMap<String, Object>> videos = new ArrayList<HashMap<String, Object>>();
		java.util.List<HashMap<String, Object>> categoryList = new ArrayList<HashMap<String, Object>>();
		java.util.List<String> videoUniqueList = new ArrayList<String>();
		java.util.List<BannerAd> uniqueBanners = new ArrayList<BannerAd>();
		java.util.List pageList = new ArrayList();

		String category = "";
		int numberOfElements = 0;
		if (list != null && !list.isEmpty() && list.size() > 0) {
			for (int i = 0; i < list.size(); ++i) {
				String[] servicesList = (String[]) list.get(i);
				String srvc = servicesList[1];
				String fName = servicesList[3];

				if (fName.equals("promo_hot_video_category.jsp")) {
					videoCategorySrvcList.add(srvc);
					continue;
				}
				if (!fName.equals("promo_banner.jsp"))
					bannerSrvcList.add(srvc);
				continue;
			}
		}
		HashMap videoParameter = new HashMap();
		videoParameter = videolist.getVideoCategory(videoCategorySrvcList,
				domain, handset);
		categoryList = (java.util.List) videoParameter.get("categoryList");
		category = (String) videoParameter.get("category");
		numberOfElements = 3;//Integer.parseInt(videoParameter.get("number_of_elements").toString());
		videoUniqueList = (java.util.List) videoParameter.get("list");

		videos = videolist.getVideos(0, videoUniqueList, domain, handset,
				numberOfElements, lang);
		return videos;

	}%>

