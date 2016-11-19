<%@page import="java.sql.Time"%>

<%
request.setAttribute("pagename","promo_subscribe.jsp");

UmeDomain dmn=(UmeDomain)request.getAttribute("dmn");
MobileClubCampaign cmpg=(MobileClubCampaign)request.getAttribute("cmpg");
MobileClubCampaignDao campaigndao = (MobileClubCampaignDao)request.getAttribute("campaigndao");
MobileClubDao mobileclubdao = (MobileClubDao)request.getAttribute("mobileclubdao");
UmeSessionParameters aReq = (UmeSessionParameters)request.getAttribute("aReq");
UmeUser user = (UmeUser)request.getAttribute("user");
//UmeDomain dmn = (UmeDomain)request.getAttribute("dmn");
String pageEnc=(String)request.getAttribute("pageEnc");
MobileClub club=(MobileClub)request.getAttribute("club");
String msisdn=(String)request.getAttribute("msisdn");
String campaignId=(String)request.getAttribute("campaignId");
PebbleEngine engine=(PebbleEngine)request.getAttribute("engine");
String domain = (String)request.getAttribute("domain");
LandingPage landingpage=(LandingPage)request.getAttribute("landingpageengine");
//LandingPage_campaign landingpage=(LandingPage_campaign)request.getAttribute("landingpage");
String wapid = (String)request.getAttribute("wapid");
String networkid=(String)request.getAttribute("networkid");
//String networkid="";
//String title = Misc.utfToUnicode(Misc.hex8Decode(aReq.get("ttl")),pageEnc);
String cid=(String)request.getAttribute("campaignId");
    PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    Writer stringWriter=new StringWriter();
    
    String landingloaded=(String)request.getAttribute("landingPage");
if(landingloaded==null || landingloaded.equalsIgnoreCase("") || landingloaded.trim().length()<=0) {
                  try{
             landingloaded=landingpage.initializeLandingPage(domain);
                 }catch(Exception e){landingloaded="";}
             }
    
    context.put("landingpage",landingloaded);
    System.out.println("ieindex promo_subscribe.jsp : "+landingloaded);


%>

<%@include file="coreimport.jsp" %>


<%
	if (user != null && club != null) {
		if (mobileclubdao.isActive(user, club)) {
			response.sendRedirect("http://" + dmn.getDefaultUrl()+ "/?id=" + wapid);
			return;
		}
	}

	String campaignUnique = "";
	if (cmpg != null) campaignUnique = cmpg.getUnique();

if(campaignId!=null && !campaignId.equals("")){
java.util.List notSubscribedClubDomains=(java.util.List)request.getAttribute("notSubscribedClubDomains");

if(notSubscribedClubDomains!=null &&!notSubscribedClubDomains.isEmpty()){
	UmeDomain popunderDomain=(UmeDomain)notSubscribedClubDomains.get((int) java.lang.Math.floor(java.lang.Math.random()* notSubscribedClubDomains.size()));
	//String userMobile=user.getMobile().toString().trim();
	//String enMsi=MiscCr.encrypt(userMobile);
	String popunderCampaignId=campaigndao.getCampaignUnique(popunderDomain.getUnique(),"PopUnder");
	System.out.println("Popunder Domain: "+"http://"+popunderDomain.getDefaultUrl()+"/?cid="+popunderCampaignId);
	context.put("popunderDomain","http://"+popunderDomain.getDefaultUrl()+"/?cid="+popunderCampaignId);
}

}


String showmsisdnrouter=(String) request.getAttribute("showmsisdnrouter");
if(showmsisdnrouter==null) showmsisdnrouter=(String) session.getAttribute("showmsisdnrouter");

if(showmsisdnrouter!=null && showmsisdnrouter.equalsIgnoreCase("true")){
    String transactionid=(String) request.getAttribute("transactionid");
    if(transactionid==null) transactionid=(String) session.getAttribute("transactionid");
    
    String msisdnrouter=(String) request.getAttribute("msisdnrouter");
    if(msisdnrouter==null) msisdnrouter=(String) session.getAttribute("msisdnrouter");
    
    context.put("transactionid",transactionid);
    context.put("msisdnrouter",msisdnrouter);
    context.put("showmsisdnrouter","true");
}

//System.out.println("invalidmsisdn message "+invalidmsisdnmsg);
context.put("msisdn",msisdn);
context.put("domains",(String[])request.getAttribute("domains"));
context.put("campaignid",campaignId);
context.put("clubprice",club.getPrice()+"");
context.put("contenturl","http://"+ dmn.getContentUrl());
//System.out.println("domain subscribe page = "+domain);



	engine.getTemplate(landingloaded).evaluate(writer, context);
		



%>

