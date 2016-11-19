<%@page import="java.sql.Time"%>

<%
request.setAttribute("pagename","promo_subscribe.jsp");
%>
<%
System.out.println("ZA PROMO SUBSCRIBE CALLED UPON ======== ");
UmeDomain dmn=(UmeDomain)request.getAttribute("dmn");
System.out.println("ZA PROMO SUBSCRIBE CALLED UPON ========  DOMAIN UNIQUE "+dmn.getUnique());
String needToConfirm=(String) request.getParameter("confirmUser");
if(needToConfirm==null) needToConfirm=(String) request.getAttribute("confirmUser");
System.out.println("ZA PROMO SUBSCRIBE CALLED UPON ========  NEED to Confirm  "+needToConfirm);
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
PebbleEngine za_engine=(PebbleEngine)request.getAttribute("za_engine");
String domain = (String)request.getAttribute("domain");
LandingPage landingpage=(LandingPage)request.getAttribute("landingpage");
//LandingPage_campaign landingpage=(LandingPage_campaign)request.getAttribute("landingpage");
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
    
    String landingloaded=(String)request.getAttribute("landingPage");
if(landingloaded==null || landingloaded.equalsIgnoreCase("") || landingloaded.trim().length()<=0) {
                  try{
             landingloaded=landingpage.initializeLandingPage(domain);
                 }catch(Exception e){landingloaded="default-landing";}
             }
    
    context.put("landingpage",landingloaded);
if(needToConfirm!=null && needToConfirm.equalsIgnoreCase("true"))
{
    String debugmsisdn=(String)request.getAttribute("msisdn");
    if(debugmsisdn!=null){
          if(debugmsisdn.equalsIgnoreCase("27619736129"))
                    {
                        System.out.println("27619736129 inside promo_subscribe value of needToConfirm is   "+needToConfirm);
                    }
    }
    
    
    String wapoptin=(String) request.getParameter("wapoptin");
    if(wapoptin==null) wapoptin=(String) request.getAttribute("wapoptin");
    
      if(debugmsisdn!=null){
          if(debugmsisdn.equalsIgnoreCase("27619736129"))
                    {
                        System.out.println("27619736129 inside promo_subscribe value of wapoptin is   "+wapoptin);
                    }
    }
    
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
            
            za_engine.getTemplate(landingloaded).evaluate(writer, context);
        	return;
        }       
                
    }
    
    else if(wapoptin!=null && wapoptin.equalsIgnoreCase("false"))
    {
        String smsredirection=(String) request.getParameter("smsconfirmed");
        if(smsredirection==null ) smsredirection=(String) request.getAttribute("smsconfirmed");
        
        if(smsredirection!=null) 
        {
              if(debugmsisdn!=null){
          if(debugmsisdn.equalsIgnoreCase("27619736129"))
                    {
                        System.out.println("27619736129 inside promo_subscribe value of smsredirection is   "+smsredirection);
                    }
    }
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
        actionpage="smsoptin.jsp";
        msisdnExist="false";
        invalidmsisdnmsg="Invalid Number! Please Try Again. ";
    
    }
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
context.put("campaignId",campaignId);
}else
	context.put("campaignId","");

//System.out.println("invalidmsisdn message "+invalidmsisdnmsg);
context.put("msisdn",msisdn);
context.put("domains",(String[])request.getAttribute("domains"));
context.put("websitetitle",title);
context.put("msisdnexist",msisdnExist);
context.put("sendAction",actionpage);
context.put("campaignid",campaignId);
context.put("clubprice",club.getPrice()+"");
context.put("invalidnumber",invalidmsisdnmsg);
context.put("contenturl","http://"+ dmn.getContentUrl());
//System.out.println("domain subscribe page = "+domain);

String simulateLanding=aReq.get("simulate");
if(simulateLanding.equals("")){
 	za_engine.getTemplate(landingloaded).evaluate(writer, context);
}else{
	String landingName=aReq.get("pageName");
	System.out.println("Landing Name: "+landingName);
	
	za_engine.getTemplate(landingName).evaluate(writer, context);
}
System.out.println("Landing Page from Index.jsp: "+landingloaded);
}
%>

