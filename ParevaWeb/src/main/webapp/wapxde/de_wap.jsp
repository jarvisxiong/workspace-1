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
System.out.println("Germany "+" wapsubscribe called");
	String userid = "";
	String resultCode = "";
	String sessionId = "";
	String resultText = "";
	String operatorCode = "";
	String trid = "";
	String url = "";
	String result = "";
	String isWapUser = "yes";
	String callbackurl = aReq.get("callbackurl");

System.out.println("Germany Testing: "+" WAP Subscribe "+ " user: "+username+" password: "+password+" service "+serviceCodeWAP+" "+serviceCodeWIFI+" "+conn_path+" "+price);

	QueryBuilder germanyQueryBuilder = new QueryBuilder(username,password, serviceCodeWAP, serviceCodeWIFI, conn_path, price);

	/****************************************************/
	/*********************Initialization
	 /****************************************************/

	String resultCodeFromNTH = "";

	if (isNotNullEmpty(operatorCode)) {
		ses.setAttribute("operatorCode", operatorCode);
	}

	if (isNullEmpty(operatorCode)) {
		if (ses.getAttribute("operatorCode") != null) {
			operatorCode = (String) ses.getAttribute("userid");
		}

	}

	if (isNullEmpty(resultCodeFromNTH)) {
		if (ses.getAttribute("resultCode") != null) {
			resultCodeFromNTH = (String) ses.getAttribute("resultCode");
		}

	}

	/********************Initilize variable*********************/

	if (ses.getAttribute("userid") != null) {
		userid = (String) ses.getAttribute("userid");
		if (isNullEmpty(userid)) {
			userid = "";
		}
	}

	if (ses.getAttribute("sessionId") != null) {
		sessionId = (String) ses.getAttribute("sessionId");
		if (isNullEmpty(sessionId)) {
			sessionId = "";
		}
	}

	if (ses.getAttribute("operatorCode") != null) {
		operatorCode = (String) ses.getAttribute("operatorCode");
		if (isNullEmpty(operatorCode)) {
			operatorCode = "";
		}
	}

	/***************************************************************************/

	System.out.println("Germany before wapAuthorize for wap resultCode "+ msisdn);
	System.out.println("Germany before wapAuthorize for wap resultCode "+ resultCodeFromNTH);
	System.out.println("Germany before wapAuthorize for wap process "+ process);

  System.out.println("Germany Testing: "+ "WAP CONDITION TESTING : "+(isNotNullEmpty(msisdn) && isNotNullEmpty(process)
			&& process.trim().equals("msisdnsubscribeform")
			&& isNotNullEmpty(resultCodeFromNTH)
			&& resultCodeFromNTH.trim().equals("100")
			&& (!callbackurl.trim().equals("confirm"))));

	if (isNotNullEmpty(msisdn) && isNotNullEmpty(process)
			&& process.trim().equals("msisdnsubscribeform")
			&& isNotNullEmpty(resultCodeFromNTH)
			&& resultCodeFromNTH.trim().equals("100")
			&& (!callbackurl.trim().equals("confirm"))) {

		String returnURL = "http://" + dmn.getDefaultUrl()+ "/de_wap.jsp?callbackurl=confirm&cid="+ campaignId + "&pg=" + subpage;

		ses.removeAttribute("process");

		url = germanyQueryBuilder.wapAuthorizeWAP(userid, price,msisdn, operatorCode, returnURL);

		  System.out.println("Germany Testing: "+" After calling WAP Authorize : "+url);
		Response gresponse = germanyQueryBuilder.getResponseFromNTH(url);

		if (gresponse != null) {
			resultCode = gresponse.getResultCode();
			resultText = gresponse.getResultText();

			System.out.println("Germany WapAuthorize for wap resultCode "+ resultCode);
			System.out.println("Germany WapAuthorize for wap resultCode "+ resultText);

			if (resultCode.trim().toLowerCase().equals("150")) {

				sessionId = gresponse.getSessionId();
				userid = gresponse.getUid();
				operatorCode = gresponse.getOperatorCode();

				addAttributeInSession(ses, "sessionId", sessionId);
				addAttributeInSession(ses, "operatorCode", operatorCode);

				String redirectURL = gresponse.getRedirectURL();

				if (redirectURL != null	&& redirectURL.trim().length() > 0) {
					response.sendRedirect(gresponse.getRedirectURL());
					return;
				}

			}
		}

	}

	if (isNotNullEmpty(userid) && sessionId != null && callbackurl.trim().equals("confirm")) {
		url = germanyQueryBuilder.checkStatusWAP(sessionId);
		Response gresponse = germanyQueryBuilder
				.getResponseFromNTH(url);

		if (gresponse != null) {

			System.out.println("Germany checkStatus for wap resultCode "+ gresponse.getResultCode());
			System.out.println("Germany checkStatus for wap resultCode "+ gresponse.getStatusNumber());
		}

		if (gresponse != null && gresponse.getResultCode().trim().equals("100")	&& gresponse.getStatusNumber() != null && gresponse.getStatusNumber().trim().toLowerCase().equals("2")) {

			String notificationURL = "http://" + dmn.getDefaultUrl()+ "/notification.jsp?from=wifi";

			result = "confirm";

			/**

			url = germanyQueryBuilder.preparePaymentWAP(sessionId,
				userid, msisdn, notificationURL);
			gresponse = germanyQueryBuilder.getResponseFromNTH(url);
			if (gresponse != null) {
			resultText = gresponse.getResultText();
			trid = gresponse.getTrid();

			url = germanyQueryBuilder.commitPaymentWAP(trid);
			gresponse = germanyQueryBuilder.getResponseFromNTH(url);
			if (gresponse != null) {
				resultText = gresponse.getResultText();
				if (gresponse.getResultCode().trim().toLowerCase()
						.equals("100")) {
					ses.setAttribute("sessionId", sessionId);
					result = "confirm";
				}

			}
			}
			
			 **/
		}
	}

	if (isNullEmpty(result)) {
		result = "error";
		if (isNotNullEmpty(msisdn)) {
			ses.setAttribute("process", "reentrymsisdn");
		}

	}

	doRedirect(response, "/confirm.jsp?cid=" + campaignId + "&pg=" 	+ subpage + "&result=" + result + "&sessionId=" + sessionId+ "&operatorCode=" + operatorCode + "&wapuser=" + isWapUser);
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
