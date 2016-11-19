<%@page import="java.sql.Time"%>
<jsp:include page="/GlobalWapHeader"/>

<%
	String subscriptionurl=(String) request.getParameter("subscriptionurl");
String landingPage=(String)request.getAttribute("landingPage");
if(subscriptionurl==null) subscriptionurl=(String) request.getAttribute("subscriptionurl");
if(subscriptionurl!=null && !subscriptionurl.equalsIgnoreCase(""))
{
    response.sendRedirect(subscriptionurl);return;
}

String needToConfirm=(String) request.getParameter("confirmUser");
if(needToConfirm==null) needToConfirm=(String) request.getAttribute("confirmUser");

MobileClubCampaign cmpg=(MobileClubCampaign)request.getAttribute("cmpg");
MobileClubCampaignDao campaigndao = (MobileClubCampaignDao)request.getAttribute("campaigndao");
MobileClubDao mobileclubdao = (MobileClubDao)request.getAttribute("mobileclubdao");
UmeSessionParameters aReq = (UmeSessionParameters)request.getAttribute("aReq");
UmeUser user = (UmeUser)request.getAttribute("user");
UmeDomain dmn = (UmeDomain)request.getAttribute("dmn");
String pageEnc=(String)request.getAttribute("pageEnc");
MobileClub club=(MobileClub)request.getAttribute("club");
String msisdn=(String)request.getAttribute("msisdn");
String campaignId=(String)request.getAttribute("campaignId");
PebbleEngine za_engine=(PebbleEngine)request.getAttribute("za_engine");
String domain = (String)request.getAttribute("domain");
LandingPage landingpage=(LandingPage)request.getAttribute("landingpage");
String wapid = (String)request.getAttribute("wapid");
String networkid=(String)request.getAttribute("networkid");
//String networkid="";
String title = Misc.utfToUnicode(Misc.hex8Decode(aReq.get("ttl")),pageEnc);
String cid=(String)request.getAttribute("campaignId");
    PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    Writer stringWriter=new StringWriter();
//    Map<String, Object> xhtmlImagesMap = UmeTempCmsCache.xhtmlImages.get("xhtml_"+domain);
//    String headerImage="";
//	String footerImage="";
//	if(xhtmlImagesMap.get("img_header1_4")!=null)
//		headerImage=(String)xhtmlImagesMap.get("img_header1_4");
//	if(xhtmlImagesMap.get("img_footer1_4")!=null)
//		footerImage=(String)xhtmlImagesMap.get("img_footer1_4");
//	
//	
//	context.put("headerImage",headerImage);
//	context.put("footerImage",footerImage);
    
if(needToConfirm!=null && needToConfirm.equalsIgnoreCase("true"))
{
    String wapoptin=(String) request.getParameter("wapoptin");
    if(wapoptin==null) wapoptin=(String) request.getAttribute("wapoptin");
    
    if(wapoptin!=null && wapoptin.equalsIgnoreCase("true"))
    {
        String wapurl=(String) request.getParameter("wappageurl");
        if(wapurl==null) wapurl=(String) request.getAttribute("wappageurl");
        
        if(wapurl!=null) 
        {
        	System.out.println("WapUrl: "+wapurl);
        	//tinyURL tU = new tinyURL();
        	
            context.put("msisdnexist","true");
            context.put("msisdn",(String)request.getAttribute("msisdn"));
            context.put("campaignid",cid);
            context.put("sendAction",wapurl);
            //context.put("sendAction",tU.getTinyURL(wapurl));
            
            za_engine.getTemplate(landingPage).evaluate(writer, context);
        	return;
        }       
                
    }
    
    else if(wapoptin!=null && wapoptin.equalsIgnoreCase("false"))
    {
        String smsredirection=(String) request.getParameter("smsconfirmed");
        if(smsredirection==null ) smsredirection=(String) request.getAttribute("smsconfirmed");
        
        if(smsredirection!=null) 
        {
            response.sendRedirect(smsredirection);return;
        }
    }
    
    
    
    
    
    
}




if(needToConfirm==null) {
%>

<%@include file="coreimport.jsp" %>


<%
	if (cmpg != null) {
		cmpg.setHitCount(cmpg.getHitCount() + 1);
		cmpg.setLastHit(new Date());
		campaigndao.saveItem(cmpg);
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

//	mobileclubcampaigndao.log("confirm", "xhtml", uid, msisdn, handset,
	//		domain, campaignUnique, clubUnique, "MSISDN_REQ", 0,
		//	request, response);
        
String actionpage="";
String msisdnExist="false";
String invalidmsisdnmsg="";
if(msisdn ==null || msisdn.trim().equalsIgnoreCase("") || msisdn.trim().length()<=0){
    actionpage="smsoptin.jsp";
    msisdnExist="false";
}

else {
    if(msisdn.startsWith("27")){
    actionpage="index_main.jsp";
    msisdnExist="true";
    }
    else{
        actionpage="smsoptin.jsp";
        msisdnExist="false";
        invalidmsisdnmsg="Invalid Number! Please Try Again. ";
    }
    }

System.out.println("invalidmsisdn message "+invalidmsisdnmsg);

context.put("msisdn",msisdn);

context.put("websitetitle",title);
context.put("msisdnexist",msisdnExist);
context.put("sendAction",actionpage);
context.put("campaignid",campaignUnique);
context.put("clubprice",club.getPrice()+"");
context.put("invalidnumber",invalidmsisdnmsg);
context.put("contenturl","http://"+ dmn.getContentUrl());
//System.out.println("domain subscribe page = "+domain);



za_engine.getTemplate(landingPage).evaluate(writer, context);
System.out.println("Landing Page from Index.jsp: "+landingPage);
}
%>

