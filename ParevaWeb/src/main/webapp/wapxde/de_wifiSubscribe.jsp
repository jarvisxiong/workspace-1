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
<%@ include file="de_parameters.jsp"%>
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
	/****************************************************/
	/*********************Declartion
	 /****************************************************/
System.out.println(" Germany "+ "wifi subscribe called ");
	String resultCode = "";
	String sessionId = "";
	String resultText = "";
	String userid = "";
	String operatorCode = "";
	String url = "";
	String resultCodeFromNTH = "";
	String result = "";
	String errorCode="";

	QueryBuilder germanyQueryBuilder = new QueryBuilder(username,password, serviceCodeWAP, serviceCodeWIFI, conn_path, price);

	//*************************************************************

	String pin = "";

	/*******************************************************************************/
	/********************Initilize variable*********************/
	
	pin = aReq.get("passcode");

	if (ses.getAttribute("sessionId") != null) {
		sessionId = (String) ses.getAttribute("sessionId");
		if (isNullEmpty(sessionId)) {
			sessionId = "";
		}
	}
	
	

	if (ses.getAttribute("userid") != null) {
		userid = (String) ses.getAttribute("userid");
		if (isNullEmpty(userid)) {
			userid = "";
		}
	}

	
	
	if (ses.getAttribute("operatorCode") != null) {
		operatorCode = (String) ses.getAttribute("operatorCode");
		if (isNullEmpty(operatorCode)) {
			operatorCode = "";
		}
	}

	

	/***************************************************************************/

	
	/*******************************************************************************/

	if (isNotNullEmpty(msisdn) && isNotNullEmpty(process) && process.trim().toLowerCase().equals("msisdnentryform")) {

		//This is execute in index_main.jsp and with submsisdn the lower code will call NTH to send pin 

		String notificationURL = "http://" + dmn.getDefaultUrl()+ "/notification.jsp?from=wifi";

		url = germanyQueryBuilder.webAuthorizeWIFI(msisdn,notificationURL);
		Response gresponse = germanyQueryBuilder.getResponseFromNTH(url);

		if (gresponse != null && gresponse.getResultCode().trim().equals("150")) {
			sessionId = gresponse.getSessionId();
			operatorCode = gresponse.getOperatorCode();

			addAttributeInSession(ses,"sessionId",sessionId);	
			addAttributeInSession(ses,"operatorCode",operatorCode);	
		

		}

		doRedirect(response, "/index_main.jsp?cid=" + campaignId+ "&pg=" + subpage);
		return;

	}

	if (isNotNullEmpty(process) && process.trim().toLowerCase().equals("pinvalidationform")) {

		if (isNotNullEmpty(pin) && isNotNullEmpty(sessionId)) {

			url = germanyQueryBuilder.webValidatePinWIFI(pin, sessionId);
			Response gresponse = germanyQueryBuilder.getResponseFromNTH(url);
			if (gresponse != null) {

				if (isNotNullEmpty(gresponse.getResultCode().trim()) && gresponse.getResultCode().trim().equals("100")) {
								
								
					String notificationURL = "http://" + dmn.getDefaultUrl() + "/notification.jsp?from=wifi";

					result = "confirm";
 
				/**
					url = germanyQueryBuilder.preparePaymentWIFI(
							sessionId, price);
					gresponse = germanyQueryBuilder
							.getResponseFromNTH(url);

					if (gresponse != null) {

						if (isNotNullEmpty(gresponse.getResultCode()
								.trim())
								&& gresponse.getResultCode().trim()
										.equals("100")) {

							url = germanyQueryBuilder
									.commitPaymentWIFI(gresponse
											.getTrid());
							gresponse = germanyQueryBuilder
									.getResponseFromNTH(url);

							if (gresponse != null) {

								if (isNotNullEmpty(gresponse
										.getResultCode().trim())
										&& gresponse.getResultCode()
												.trim().equals("100")) {

									result = "confirm";

								}

							}

						}

					}
					
					**/

				}else{
					errorCode="invalidPin pin "+pin;
				}

			}
		}

	}

	if (isNullEmpty(result)) {
		result = "error";
		if (isNotNullEmpty(msisdn)) {
			addAttributeInSession(ses,"process", "reentrymsisdn");
		}


	}

	doRedirect(response, "/confirm.jsp?cid=" + campaignId + "&pg="+ subpage + "&result=" + result+"&errorCode="+errorCode+"&sessionId="+sessionId+"&operatorCode="+operatorCode);
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
