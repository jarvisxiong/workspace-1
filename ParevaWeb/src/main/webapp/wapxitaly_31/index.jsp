<%@include file="commonfunc.jsp"%>
<%@include file="cpavisit.jsp"%>
<%
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
	TemplateEngine templateengine=null;
        
        if("".equals(msisdnexist)) msisdnexist="false";
        context.put("msisdnexist",msisdnexist);
        
        context.put("operator",previewNetwork);


	try{
     ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
     ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
     templateengine=(TemplateEngine) ac.getBean("templateengine");
       
     }
     catch(Exception e){
         e.printStackTrace();
     }
	PebbleEngine engine=templateengine.getTemplateEngine(dmn.getUnique());
	engine.getTemplate(previewLandingPage).evaluate(writer, context); 

}

else{
String uid = "";
    String wapid = "";
    String msisdn = aReq.getMsisdn();
    String mobilenumber = "", parsedmobile = "";

//====Required for CPA=====
    String insertQuery = "";
    String campaignsrc = "";
    String landingPage = "";
    String visitsubscribed = "1970-01-01 00:00:00";
    
    String myip = request.getHeader("X-Forwarded-For");
    MobileClubCampaign cmpg = null;

    if (myip != null) {
        int idx = myip.indexOf(',');
        if (idx > -1) {
            myip = myip.substring(0, idx);
        }
    }else{
        myip = request.getRemoteAddr();
    }

    session.setAttribute("userip", myip);
    request.setAttribute("userip", myip);

    String isSubscribed = "0";
    String transaction_ref = "itcpa";
//====CPA Requirement end ===========
    



//This is for Campaign Hit counter  LandingPage Rotation Logic
if(!campaignId.equals("")){
		landingPage=landingpage.initializeLandingPage(domain,campaignId,"all");
                request.setAttribute("landingPage",landingPage);
                session.setAttribute("landingPage",landingPage);
	}else {
		landingPage=landingpage.initializeLandingPage(domain);
                request.setAttribute("landingPage",landingPage);
                session.setAttribute("landingPage",landingPage);
	}


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




//=========END landingpage rotation logic
    if (campaignId != null && campaignId.trim().length() > 0) {

        if (!campaignId.equals("")) {
            cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
             if(campaignId.equals("2409942441KDS")){System.out.println("ItalyCPA "+campaignId);}
        }
        if (cmpg != null) {
            campaignsrc = cmpg.getSrc();
            if(cmpg.getUnique().equals("2409942441KDS")){System.out.println("ItalyCPA "+cmpg.getUnique()+" "+cmpg.getSrc());}
        }
        try {
            if (cmpg != null) {
                campaigndao.log("index",landingPage, "", "", handset, domain, campaignId, club.getUnique(), "INDEX", 0, request, response, myip);
            }
        } catch (Exception e) {
            System.out.println("campaigndao Italy IPX Exception " + e);
        }
    }

    if (campaignsrc.trim().toLowerCase().contains("cpa")) {
        
        
%>
//CPA Tracking Code comes here. 
<%@include file="cpavisitlog.jsp"%>
<%    }
    if (campaignsrc.trim().endsWith("RS")) {
%>   
<%@include file="revsharevisitlog.jsp"%>
<%    }

//if(club!=null && club.getRegion().equals("IT") && !isDeskTopOrTablet && isIdentified && user==null)
/*
     if(club!=null && club.getRegion().equals("IT") && user==null)
     {	
     System.out.println("%%%% IPX goto identify %%%%%");
     String identifyUrl = "/identify.jsp" ;
     if(!campaignId.equals(""))
     identifyUrl += "?cid=" + campaignId;  
     doRedirect(response, identifyUrl);
     return;
     }
     */
    isIdentified = ipxLookupIP(myip, clubIPXUserName, clubIPXPassword);
//NEW WAY
//if(club!=null && club.getRegion().equals("IT") && !isDeskTopOrTablet && isIdentified && user==null)
    if (club != null && club.getRegion().equals("IT") && !isDeskTopOrTablet && isIdentified && user == null) //if(club!=null && club.getRegion().equals("IT") && user==null)
    {
%>
<%@include file="identify_function.jsp"%>
<%    }
    
    try {
        application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp").forward(request, response);
        //return;
        //application.getRequestDispatcher("/IndexMain").forward(request,response); 
    } catch (Exception e) {
        System.out.println("wapxitaly index Exception " + e);
        String exception_ = getStackTraceAsString(e);
        System.out.println(exception_);
    }
    
}

%>


