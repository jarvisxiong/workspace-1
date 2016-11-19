<%@include file="coreimport.jsp" %>
<%
request.setAttribute("pagename","promo_subscribe.jsp");
UmeDomain dmn=(UmeDomain)request.getAttribute("dmn");
MobileClubCampaign cmpg=(MobileClubCampaign)request.getAttribute("cmpg");
UmeSessionParameters aReq = (UmeSessionParameters)request.getAttribute("aReq");
MobileClubDao mobileclubdao = (MobileClubDao)request.getAttribute("mobileclubdao");
UmeUser user = (UmeUser)request.getAttribute("user");
MobileClub club=(MobileClub)request.getAttribute("club");
String msisdn=(String)request.getAttribute("msisdn");
String campaignId=(String)request.getAttribute("campaignId");
PebbleEngine engine=(PebbleEngine)request.getAttribute("engine");
String domain = (String)request.getAttribute("domain");
LandingPage landingpage=(LandingPage)request.getAttribute("landingpageengine");
String wapid = (String)request.getAttribute("wapid");
String networkid=(String)request.getAttribute("networkid");

 String merchanttoken=(String) request.getAttribute("merchanttoken");
 if(merchanttoken==null) merchanttoken=(String) session.getAttribute("merchanttoken");
 
 String sessiontoken=(String) request.getAttribute("sessiontoken");
 if(sessiontoken==null) sessiontoken=(String) session.getAttribute("sessiontoken");
 
    response.addHeader("X-PFI-MerchantToken",merchanttoken);
    response.addHeader("X-PFI-SessionToken", sessiontoken);

PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    Writer stringWriter=new StringWriter();
    
    String landingloaded=(String)request.getAttribute("landingPage");
    
    context.put("landingpage",landingloaded);
    
    if (user != null && club != null) {
		if (mobileclubdao.isActive(user, club)) {
			response.sendRedirect("http://" + dmn.getDefaultUrl()+ "/?id=" + wapid);
			return;
		}
	}


    engine.getTemplate(landingloaded).evaluate(writer, context);


%>
