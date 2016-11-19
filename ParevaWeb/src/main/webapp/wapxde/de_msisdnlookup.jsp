<%@page import="com.germany.xml.Response"%>
<%@page import="ume.pareva.dao.UmeSessionParameters"%>
<%@page import="com.germany.utils.QueryBuilder"%>
<%@include file="coreimport.jsp" %>
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
<%@ include file="de_parameters.jsp"%>

<%
   UmeDomain dmn=(UmeDomain)request.getAttribute("dmn");
System.out.println("Germany Integration de msisdnlookup "+dmn.getUnique());
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
String subpage=(String) request.getAttribute("subpage");
String ip=(String) session.getAttribute("userip");
	/****************************************************/
	/*********************Declartion
	 /****************************************************/
	String resultCode = "";
	String sessionId = "";
	String resultText = "";
	String userid = "";
	String operatorCode = "";
	String url = "";
	String resultCodeFromNTH = "";

	QueryBuilder germanyQueryBuilder = new QueryBuilder(username,password, serviceCodeWAP, serviceCodeWIFI, conn_path, price);
        System.out.println("Germany Integration de_msisdnlookup "+username+ " "+password+" "+serviceCodeWAP+" "+serviceCodeWIFI+" "+conn_path+" "+price);

	/****************************************************/
	/*********************Initialization
	/****************************************************/

	userid = aReq.get("userid");

	if (isNullEmpty(userid)) {
		if (session.getAttribute("userid") != null) {
			userid = (String) session.getAttribute("userid");
		}

		if (isNullEmpty(userid)) {
			userid = "";
		}

	}

	if (isNullEmpty(resultCodeFromNTH)) {
		if (session.getAttribute("resultCode") != null) {
			resultCodeFromNTH = (String) session.getAttribute("resultCode");
		}

	}
	/****************************************************/

	/**
	Sending request for NTH to reconize msisdn 
	
	This will call two function [wapIdentifyUser(...),getUser(...)]
	
	 **/

	if (null == request.getParameter("callbackurl") || "".equals(request.getParameter("callbackurl"))) {
            System.out.println(" Germany Integration de_msisdnlooku callbackurl is NULL ");

		String callbackURL = "http://" + dmn.getDefaultUrl() + "/de_msisdnlookup.jsp?callbackurl=index_main&cid="+ campaignId + "&pg=" + subpage;
                System.out.println("Germany Integration de_msisdnlooku callbackurl is NULL and the value now is  "+callbackURL);

		url = germanyQueryBuilder.wapIdentifyUserWAP(ip, callbackURL);
                System.out.println("Germany Integration de_msisdnlooku callbackurl is NULL and the value now is  "+callbackURL+" IP of the user is "+ip);

                System.out.println("Germany Integration ");
		Response gresponse = germanyQueryBuilder.getResponseFromNTH(url);

		if (gresponse != null && gresponse.getRedirectURL() != null && gresponse.getRedirectURL().trim().length() > 0) {

			System.out.println("Germany wapIdentifyUser callbackurl is null  for wap resultCode "+ gresponse.getResultCode());
			System.out.println("Germany wapIdentifyUser callbackurl is null for wap redirect url "+ gresponse.getRedirectURL());
                        System.out.println("Germany wapIdentifyUser callbackurl is null for wap callbackURL "+ gresponse.getCallbackUrl());
			System.out.println("Germany wapIdentifyUser callbackurl is null for wap operatorCode "+ gresponse.getOperatorCode());

                        	System.out.println("Germany wapIdentifyUser callbackurl is null for wap sessionId "+ gresponse.getSessionId());
			System.out.println("Germany wapIdentifyUser callbackurl is null  for wap uId "+ gresponse.getUid());


			sessionId = gresponse.getSessionId();
			userid = gresponse.getUid();
			resultCode = gresponse.getResultCode();
			resultText = gresponse.getResultText();
			operatorCode = gresponse.getOperatorCode();

			addAttributeInSession(session, "userid", userid);
			addAttributeInSession(session, "sessionId", sessionId);
			addAttributeInSession(session, "resultCode", resultCode);
			addAttributeInSession(session, "operatorCode", operatorCode);

			/**
			
			if request.getParameter("callbackurl")==null then this portion execute where
			ses.setAttribute("msisdnlookup", "complete");
			wont be call because there is return just below  and					
			ses.setAttribute("msisdnlookup", "complete"); is at the end
			 **/

			response.sendRedirect(gresponse.getRedirectURL());
			return;
		}

	} else if (request.getParameter("callbackurl").trim().toLowerCase().equals("index_main")) {

		if (userid != null && userid.trim().length() > 0) {

			url = germanyQueryBuilder.getUserWAP(userid);
			Response gresponse = germanyQueryBuilder.getResponseFromNTH(url);

			if (gresponse != null) {
				
				
				System.out.println("Germany getUser callbackurl is index_main  for wap resultCode "+ gresponse.getResultCode());
				System.out.println("Germany getUser callbackurl is index_main  for wap resultText "+ gresponse.getResultText());						
				System.out.println("Germany getUser callbackurl is index_main  for wap Msisdn "+ gresponse.getMsisdn());
                                System.out.println("Germany wapIdentifyUser callbackurl is index_main  for wap resultCode "+ gresponse.getResultCode());
			System.out.println("Germany wapIdentifyUser callbackurl is index_main for wap redirect url "+ gresponse.getRedirectURL());
                        System.out.println("Germany wapIdentifyUser callbackurl is index_mainl for wap callbackURL "+ gresponse.getCallbackUrl());
			System.out.println("Germany wapIdentifyUser callbackurl is index_main for wap operatorCode "+ gresponse.getOperatorCode());

                        	System.out.println("Germany wapIdentifyUser callbackurl is index_main for wap sessionId "+ gresponse.getSessionId());
			System.out.println("Germany wapIdentifyUser callbackurl is index_mainl  for wap uId "+ gresponse.getUid());
				
				resultCode = gresponse.getResultCode();

				if (resultCode != null && resultCode.trim().equals("100")) {
					resultText = gresponse.getResultText();
					operatorCode = gresponse.getOperatorCode();

					if (gresponse.getMsisdn() != null && gresponse.getMsisdn().trim().length() > 2) {

						msisdn = gresponse.getMsisdn();
						operatorCode = gresponse.getOperatorCode();
						resultCode = gresponse.getResultCode();

						addAttributeInSession(session, "msisdn", msisdn);
						addAttributeInSession(session, "sessionId",sessionId);
						addAttributeInSession(session, "resultCode",resultCode);
						addAttributeInSession(session, "operatorCode", operatorCode);

                                        }
                                        else if(gresponse.getOperatorCode()!=null && gresponse.getOperatorCode().trim().equals("26203")){
                                            addAttributeInSession(session, "msisdn", "00xxxxxxxxxxx");
                                            addAttributeInSession(session, "sessionId",sessionId);
                                            addAttributeInSession(session, "resultCode",resultCode);
                                            addAttributeInSession(session, "operatorCode", operatorCode);
                                        }
				}

			}
		}

	}

	if (isNotNullEmpty(msisdn)) {
            String landingPage=(String) session.getAttribute("landingPage");
            String myisp=(String) session.getAttribute("myisp");
            String pubId = (String) session.getAttribute("cpapubid");
            
            

	    //campaigndao.log("index", landingPage, msisdn, msisdn, null, domain, campaignId, club.getUnique(), "INDEX", 0, request, response, myisp, "", "", "", pubId); 

	} else {

		//MobileClubCampaignDao.log("global_wap_header", "xhtml", uid,msisdn, null, domain, campaignId, club.getUnique(),"MSISDNP_FAIL", 0, request, response);
	}

	doRedirect(response, "http://" + dmn.getDefaultUrl()+ "/index_main.jsp?process=start&cid=" + campaignId+ "&pg=" + subpage+"&msisdn="+msisdn);
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