<%@include file="coreimport.jsp" %>
<jsp:include page="/DEHeader"/>
<%
request.setAttribute("pagename","promo_subscribe.jsp");
%>
<%
System.out.println("Germany Integration promo_subscribe ");
UmeDomain dmn=(UmeDomain)request.getAttribute("dmn");
//System.out.println("Germany Integration promo_subscribe ========  DOMAIN UNIQUE "+dmn.getUnique());
MobileClubCampaign cmpg=(MobileClubCampaign)request.getAttribute("cmpg");
MobileClubCampaignDao campaigndao = (MobileClubCampaignDao)request.getAttribute("campaigndao");
MobileClubDao mobileclubdao = (MobileClubDao)request.getAttribute("mobileclubdao");
UmeSessionParameters aReq = (UmeSessionParameters)request.getAttribute("aReq");
UmeUser user = (UmeUser)request.getAttribute("user");

MobileClub club=(MobileClub)request.getAttribute("club");

String msisdn=(String)request.getAttribute("msisdn");
String campaignId=(String)request.getAttribute("campaignId");
PebbleEngine engine=(PebbleEngine)request.getAttribute("engine");
String domain = (String)request.getAttribute("domain");
LandingPage landingPage=(LandingPage)request.getAttribute("landingpage");
//LandingPage_campaign landingpage=(LandingPage_campaign)request.getAttribute("landingpage");
String wapid = (String)request.getAttribute("wapid");
String networkid=(String)request.getAttribute("networkid");
//String networkid="";

String cid=(String)request.getAttribute("campaignId");
String pageEnc=(String)request.getAttribute("pageEnc");

 String process=(String) request.getAttribute("deprocess");
 if(process==null) process=(String) session.getAttribute("deprocess");
 
     String landingloaded=(String)request.getAttribute("landingPage");
if(landingloaded==null || landingloaded.equalsIgnoreCase("") || landingloaded.trim().length()<=0) {                 
             landingloaded=landingPage.initializeLandingPage(domain);
                
             }
 

	//String title = Misc.utfToUnicode(Misc.hex8Decode(aReq.get("ttl")),pageEnc);


	if (cmpg != null) {
		cmpg.setHitCount(cmpg.getHitCount() + 1);
		cmpg.setLastHit(new Date());
		//MobileClubCampaignDao.saveItem(cmpg);
	}

	if (user != null && club != null) {
		if (mobileclubdao.isActive(user, club)) {
			response.sendRedirect("http://" + dmn.getDefaultUrl()+ "/?id=" + wapid);
			return;
		}
	}

	String campaignUnique = "";
	if (cmpg != null)
		campaignUnique = cmpg.getUnique();



 System.out.println("Germany Testing : "+ msisdn);
System.out.println("Germany Testing: "+" Promo Testing for WAP "+ (isNotNullEmpty(msisdn) && isNotNullEmpty(process)&& process.trim().equals("start")));

	//MobileClubCampaignDao.log("confirm", "xhtml", uid, msisdn, handset,domain, campaignUnique, clubUnique, "MSISDN_REQ", 0,	request, response);

 Map<String, Object> context = new HashMap();
    PrintWriter writer = response.getWriter();
    Writer stringWriter=new StringWriter();
    
    String operator=(String) session.getAttribute("operatorCode");
    String sendaction="de_wap.jsp";
        
     if(isNotNullEmpty(msisdn) && isNotNullEmpty(process) && process.trim().equals("start")){
        context.put("msisdnexist","true");
        context.put("process","msisdnsubscribeform");
       
    }
     else if (isNullEmpty(msisdn) || (isNotNullEmpty(process) && process.trim().equals("reentrymsisdn"))){
         context.put("msisdnexist","false");
         sendaction="de_wifiSubscribe.jsp";
          context.put("process","msisdnentryform");
     }
     else if (isNotNullEmpty(msisdn) && isNotNullEmpty(process) && process.trim().equals("msisdnentryform")) {
         sendaction="de_wifiSubscribe.jsp";
         context.put("msisdnexist","false");
         context.put("process","pinvalidationform");
         //PIN TO BE SHOWED 
         
     }
     else{
          sendaction="de_wifiSubscribe.jsp";
          context.put("process","msisdnentryform");
     }
    
    context.put("msisdn", msisdn);
    context.put("submsisdn", msisdn);
    context.put("cid",campaignId);
    context.put("campaignid",campaignId);
    context.put("contenturl","http://"+dmn.getContentUrl());
    context.put("clubprice","4.99");
    context.put("operator",operator);
    context.put("landingpage",landingloaded);
    context.put("sendaction",sendaction);
    
    System.out.println("The value of sendaction is "+sendaction);
    

    String simulateLanding=aReq.get("simulate");
if(simulateLanding.equals("")){
 	engine.getTemplate(landingloaded).evaluate(writer, context);
}else{
	String landingName=aReq.get("pageName");
	System.out.println("Landing Name: "+landingName);
	
	engine.getTemplate(landingName).evaluate(writer, context);
        return;
}


%>


	<%!// Option to redirect via a debug page...
	void doRedirect(HttpServletResponse response, String url_) {
		String referer = "headers";
		try {
			response.sendRedirect(url_);
		} catch (Exception e) {
			System.out.println("doRedirect EXCEPTION: " + e);
		}
	}

	void addAttributeInSession(HttpSession ses, String key, String value) {

		if (isNotNullEmpty(value)) {
			ses.setAttribute(key, value);
                }
	}

	boolean isNullEmpty(String value) {

		return (value == null || (value != null && value.trim().length() <= 0) || (value != null && value
				.trim().toLowerCase().equals("null")));

	}

	boolean isNotNullEmpty(String value) {
		return (value != null && value.trim().length() > 0 && !(value.trim()
				.toLowerCase().equals("null")));
	}

	// Used for logging specially formatted messages%>

