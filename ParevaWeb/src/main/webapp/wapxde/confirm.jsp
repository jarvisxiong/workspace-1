<%@page import="com.germany.xml.Response"%>
<%@page import="com.germany.utils.QueryBuilder"%>
<jsp:include page="/DEHeader"/>

<%
    if(session.getAttribute("deredirection")==null){
    String redirectto = (String) request.getParameter("redirecturl");
    
    if (redirectto == null) {
        redirectto = (String) request.getAttribute("redirecturl");
    }
    if (redirectto == null) {
        redirectto = (String) session.getAttribute("redirecturl");
    }
    if (redirectto != null) {
        response.sendRedirect(redirectto);
        session.setAttribute("deredirection","true");
        return;
    }
}
%>
<%@include file="coreimport.jsp" %>
<%
    
     HttpSession ses = request.getSession();
    UmeDomain dmn=(UmeDomain)request.getAttribute("dmn");
System.out.println("ZA PROMO SUBSCRIBE CALLED UPON ========  DOMAIN UNIQUE "+dmn.getUnique());
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
String subpage=(String) request.getAttribute("subpage");
//String networkid="";

String cid=(String)request.getAttribute("campaignId");
String pageEnc=(String)request.getAttribute("pageEnc");

 String process=(String) request.getAttribute("deprocess");
 if(process==null) process=(String) session.getAttribute("deprocess");
 
 
        UmeUserDao umeuserdao=(UmeUserDao) request.getAttribute("umeuserdao");
        UmeMobileClubUserDao umemobileclubuserdao=(UmeMobileClubUserDao) request.getAttribute("umemobileclubuserdao");
        MobileClubBillingPlanDao billingplandao=(MobileClubBillingPlanDao) request.getAttribute("billingplandao");
	//System.out.println(anyxsdc.smsAccountMap);
	System.out.println("DM: " + dmn.getUnique());

	String status = "";
 
	boolean NTHDeclined = false;
	String errorCode = aReq.get("errorCode");

	String requestreference = aReq.get("requestreference");
	System.out.println(" Germany requestreference\t\t" + requestreference);

	String country = aReq.get("country");

	String wapuser = aReq.get("wapuser");

if (isNotNullEmpty(wapuser) && wapuser.trim().toLowerCase().equals("yes")) {
		wapuser = "wap";
	} else {
		wapuser = "wifi";
	}

	String sessionId = aReq.get("sessionId");
	String operatorCod = aReq.get("operatorCode");
        
        System.out.println("Germany : "+ "Confirm.jsp  sessionId: "+sessionId+"   at "+new Date());
        System.out.println("Germany : "+ "Confirm.jsp  operatorCode: "+operatorCod+"   at "+new Date());

	Calendar debug_time = new GregorianCalendar();
	Date time_stamp = debug_time.getTime();

	boolean processingResult = false;

	String campaignUnique = "";

        if (cmpg != null) campaignUnique = cmpg.getUnique();

	if (aReq.get("result").length() > 1) { // if we got an answer from STS

		processingResult = true;

		if (aReq.get("result").equals("confirm") || aReq.get("result").equals("error")) {
                    
                    System.out.println(" Germany "+" Result "+ aReq.get("result")+" at "+ new Date());

			if (aReq.get("result").equals("error")) {

				// Log this event
				//campaigndao.log("global_wap_header", "xhtml",uid, msisdn, null, domain, campaignUnique,	club.getUnique(), "NTH_CONFIRM", 0, request,response);
                                //campaigndao.log("index", landingPage, msisdn, msisdn, handset, domain, campaignId, clubUnique, "INDEX", 0, request, response, myisp, "", "", "", pubId);
				NTHDeclined = false;
			} else {
				// Log this event
				//campaigndao.log("global_wap_header", "xhtml",uid, msisdn, null, domain, campaignUnique,	club.getUnique(), "NTH_DECLINE", 0, request,response);
				NTHDeclined = true;
			}

		}
	}
        if (club == null) {
		status = "ERROR: subscription service not found";

	} else if (processingResult && isNullEmpty(msisdn)) {
		status = "Sorry, we need your cell number! "+ msisdn+ " Please go back and enter your cell number and we'll send you your FREE link instantly!";
		//campaigndao.log("confirm", "xhtml", uid, msisdn,handset, domain, "", club.getUnique(), "NUMBER_INVLD",0, request, response);

	} else if (processingResult && isNotNullEmpty(msisdn)
			&& NTHDeclined && isNullEmpty(errorCode)) { // this is to get around a KE problem
		status = "Sorry, that's not a valid cell number! "+ msisdn+ " Please go back and enter your cell number and we'll send you your FREE link instantly!";
		//campaigndao.log("confirm", "xhtml", uid, msisdn,handset, domain, "", club.getUnique(), "NUMBER_INVLD",0, request, response);
	} else if (processingResult && isNotNullEmpty(msisdn)
			&& NTHDeclined && isNotNullEmpty(errorCode)) { // this is to get around a KE problem
		status = "You have insert  "+ errorCode	+ ". Please go back and enter your cell number and we'll send you your FREE link instantly!";
		//campaigndao.log("confirm", "xhtml", uid, msisdn,handset, domain, "", club.getUnique(), "NUMBER_INVLD",	0, request, response);
	} else if (!processingResult) {

		status = "Sorry, that's not a valid cell number! "+ msisdn+ " Please go back and enter your cell number and we'll send you your FREE link instantly!";
	}

else {

		// Log this event
		//campaigndao.log("confirm", "xhtml", uid, msisdn,handset, domain, campaignUnique, club.getUnique(),"MANUAL", 0, request, response);

		String defClubDomain = System.getProperty("CMS_defaultClubDomain");

		if (defClubDomain == null) {
			defClubDomain = "5510024809921CDS"; //got it from application.properties
		}

		System.out.println("Germany defClubDomain: "+ System.getProperty("CMS_defaultClubDomain"));

		Calendar c1 = new GregorianCalendar();
		Date bstart = c1.getTime();
		c1.add(Calendar.DATE, club.getPeriod());
		Date bend = c1.getTime();

		SdcMobileClubUser clubUser = null;
		String hash = "";

		if (user == null && isNotNullEmpty(msisdn)) {
			String userUnique = umeuserdao.getUserUnique(msisdn,"msisdn", defClubDomain);
        	if (!userUnique.equals(""))
				user = umeuserdao.getUser(userUnique);
		}

		if (user != null) {

			if (user.getAccountType() == 99) {
				// User account is blocked/barred
				System.out.println("Account Type = 99");
				doRedirect(response, "http://" + dmn.getDefaultUrl()+ "/blocked.jsp");
				return;
			}

			user.updateMap("active", "1");
			umeuserdao.commitUpdateMap(user);
			user.clearUpdateMap();

			clubUser = user.getClubMap().get(club.getUnique());
			if (clubUser != null) {
				clubUser.setParsedMobile(user.getParsedMobile());
				clubUser.setActive(1);
				clubUser.setCredits(club.getCreditAmount());
				clubUser.setAccountType(0);
				//if (confMsg) clubUser.setAccountType(5);
				clubUser.setBillingStart(bstart);
				clubUser.setBillingEnd(bend);
				if (!campaignUnique.equals(""))
					clubUser.setCampaign(campaignUnique);
				clubUser.setNetworkCode(operatorCod);
				clubUser.setSubscribed(new Date());
				clubUser.setNextPush(bstart);

				//this one is used in closeSubscription
				clubUser.setParam1(sessionId);
				clubUser.setParam2(wapuser);

				System.out.println("SAVING EXISTING USER: "
						+ clubUser.getAccountType() + ": "
						+ clubUser.getUserUnique() + ": " + clubUser);
				umemobileclubuserdao.saveItem(clubUser);

				//confMsg = false;
			} else {

				clubUser = new SdcMobileClubUser();
				clubUser.setUnique(SdcMisc.generateUniqueId());
				clubUser.setUserUnique(user.getUnique());
				clubUser.setClubUnique(club.getUnique());
				clubUser.setParsedMobile(user.getParsedMobile());
				clubUser.setActive(1);
				clubUser.setCredits(club.getCreditAmount());
				clubUser.setAccountType(0);
				//if (confMsg) clubUser.setAccountType(5);
				clubUser.setBillingStart(bstart);
				clubUser.setBillingEnd(bend);
				clubUser.setBillingRenew(bstart);
				clubUser.setPushCount(0);
				clubUser.setCreated(new Date());
				clubUser.setCampaign(campaignUnique);
				clubUser.setNetworkCode(operatorCod);
				clubUser.setUnsubscribed(new Date(0));
				clubUser.setSubscribed(new Date());
				clubUser.setNextPush(bstart);

				//this one is used in preparepayment and commitpaypent
				clubUser.setParam1(sessionId);
				clubUser.setParam2(wapuser);

				umemobileclubuserdao.saveItem(clubUser);
				user.getClubMap().put(club.getUnique(), clubUser);
			}

		} else {

			// Set up a completely new user
			//if (user==null) {
			user = new UmeUser(); // <- new user...
			user.setMobile(msisdn);
			user.setWapId(SdcMisc.generateLogin(10));
			user.setDomain(defClubDomain);
			user.setActive(1);
			user.setCredits(club.getCreditAmount());
			String stat = umeuserdao.addNewUser(user);
			//}

			System.out.println("STAT: " + stat);
			if (stat.equals("")) {

				clubUser = new SdcMobileClubUser();
				clubUser.setUnique(SdcMisc.generateUniqueId());
				clubUser.setUserUnique(user.getUnique());
				clubUser.setClubUnique(club.getUnique());
				clubUser.setParsedMobile(user.getParsedMobile());
				clubUser.setActive(1);
				clubUser.setCredits(club.getCreditAmount());
				clubUser.setAccountType(0);
				//if (confMsg) clubUser.setAccountType(5);
				clubUser.setBillingStart(bstart);
				clubUser.setBillingEnd(bend);
				clubUser.setBillingRenew(bstart);
				clubUser.setPushCount(0);
				clubUser.setCreated(new Date());
				clubUser.setCampaign(campaignUnique);
				clubUser.setNetworkCode(operatorCod);
				clubUser.setUnsubscribed(new Date(0));
				clubUser.setSubscribed(new Date());

				//this one is used in preparepayment and commitpaypent
				clubUser.setParam1(sessionId);
				clubUser.setParam2(wapuser);

				umemobileclubuserdao.saveItem(clubUser);

				hash = user.getActiveClubCode();

				status = "OK: User added, unique: " + user.getUnique();
			} else
				status = SdcMisc.htmlEscape(stat);
		}

		System.out.println("HASH: " + hash);

		if (cmpg == null) {
			campaignUnique = clubUser.getCampaign();
			cmpg = UmeTempCmsCache.campaignMap.get(campaignUnique);
		}

		Calendar calender=new GregorianCalendar();
		//calender.add(Calendar.HOUR, 22);//22 hours because its valid only for 24 hours so billing must be done before 24 hours
		
              
	System.out.println("Germany :"+" Confirm.jsp  Billing Ticket Created for msisdn "+user.getParsedMobile()+" at "+ new Date());
		MobileClubBillingPlan bill = new MobileClubBillingPlan();
		bill.setSubUnique(user.getUnique());
		bill.setClubUnique(club.getUnique());
		bill.setParsedMobile(user.getParsedMobile());
		bill.setNetworkCode(operatorCod);
		bill.setSubscribed(calender.getTime());
		bill.setActiveForAdvancement(1);
                bill.setActiveForBilling(1);
                bill.setLastPaid(new Date());
                bill.setLastSuccess(new Date());
                bill.setNextPush(MiscDate.parseSqlDate("2011-01-01"));
		bill.setTariffClass(club.getPrice());
		bill.setLastPush(MiscDate.parseSqlDate("2011-01-01"));
		bill.setaCampaign(campaignUnique);                
                
		billingplandao.insertBillingPlan(bill);
                            

//		if (cmpg != null) {
//			cmpg.setBillingCount(cmpg.getBillingCount() + 1);
//			MobileClubCampaignDao.saveItem(cmpg);
//		}

		System.out.println("Updating campaign status for the user: "+ user.getParsedMobile());
                //campaigndao.log("confirm", "xhtml", user.getUnique(), user.getParsedMobile(), handset, domain, campaignUnique, club.getUnique(),"SUBSCRIBED", 0, request, response);
		status = club.getWebConfirmation();

		 
		if (status == null || status.equals(""))
			status = "Your account has been activated.<br>You should receive a Text shortly containing your personal link to the service.";
	 
		ses.setAttribute("sdc_msisdn_param", msisdn);
            if(user!=null){
                doRedirect(response, "http://" + dmn.getDefaultUrl() + "/?id="+ user.getWapId());   
                return;
            }
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