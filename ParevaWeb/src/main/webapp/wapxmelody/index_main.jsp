<%@include file="coreimport.jsp" %>
<%

String simulateVideo="";
try{
simulateVideo=(String)request.getAttribute("simulate");
}catch(Exception e){
	simulateVideo="";
}
//System.out.println("Simulate Video = index_main.jsp "+simulateVideo);
request.setAttribute("simulateVideo", simulateVideo);


request.setAttribute("pagename","index_main.jsp");

 String landingloaded=(String) request.getAttribute("landingPage");
         System.out.println("LANDING PAGE IN INDEX_MAIN "+landingloaded);
             
               if(landingloaded==null || landingloaded.equalsIgnoreCase("")) {
                 landingloaded=(String) request.getParameter("l");
             }            
        
             
             request.setAttribute("landingPage",landingloaded);
%>

<jsp:include page="/GlobalWapHeader"/>
<%
String subscriptionurl=(String) request.getParameter("subscriptionurl");
if(subscriptionurl==null) subscriptionurl=(String) request.getAttribute("subscriptionurl");
if(subscriptionurl!=null && !subscriptionurl.equalsIgnoreCase(""))
{
    System.out.println("ZA INDEX MAIN dispatcher calling...  ");
	request.getServletContext().getRequestDispatcher("/wapxza/promo_subscribe.jsp?redir=indexmainjsp").forward(request, response);
	
    //response.sendRedirect(subscriptionurl);
    return;
}

String needToConfirm=(String) request.getParameter("confirmUser");
if(needToConfirm==null) needToConfirm=(String) request.getAttribute("confirmUser");
//System.out.println("Need To Confirm: "+needToConfirm);
if(needToConfirm!=null && needToConfirm.equalsIgnoreCase("true"))
{
    String wapoptin=(String) request.getParameter("wapoptin");
    if(wapoptin==null) wapoptin=(String) request.getAttribute("wapoptin");
    
    if(wapoptin!=null && wapoptin.equalsIgnoreCase("true")) {
        String wapurl=(String) request.getParameter("wappageurl");
        if(wapurl==null) wapurl=(String) request.getAttribute("wappageurl");
        
        if(wapurl!=null) 
        {
        	String landingPage="";
            String sendAction = wapurl;
           	UmeDomain dmn =(UmeDomain) request.getAttribute("dmn");
        	//System.out.println("WapUrl: "+wapurl);
        	PebbleEngine za_engine=(PebbleEngine)request.getAttribute("za_engine");
        	LandingPage landingpage=(LandingPage)request.getAttribute("landingpage");
        	CampaignHitCounterDao campaignhitcounterdao=(CampaignHitCounterDao)request.getAttribute("campaignhitcounterdao");
        	 PrintWriter writer = response.getWriter();
             Map<String, Object> context = new HashMap();
             String domain=(String)request.getAttribute("domain");
             String cid=(String)request.getAttribute("campaignId");
             String networkid=(String)request.getAttribute("networkid");
             
             System.out.println("==== INDEXMAIN ZA ENGINE PEBBLE ENGINE ==== "+za_engine+" ==="+domain+" ==== "+cid+" ===== "+networkid);
//             Map<String, Object> xhtmlImagesMap = UmeTempCmsCache.xhtmlImages.get("xhtml_"+domain);
//             String headerImage="";
//         	String footerImage="";
//         	if(xhtmlImagesMap.get("img_header1_4")!=null)
//         		headerImage=(String)xhtmlImagesMap.get("img_header1_4");
//         	if(xhtmlImagesMap.get("img_footer1_4")!=null)
//         		footerImage=(String)xhtmlImagesMap.get("img_footer1_4");
//         	
//         	
//         	context.put("headerImage",headerImage);
//         	context.put("footerImage",footerImage);
             //String networkid="mtn";
             context.put("msisdnexist","true");
             context.put("msisdn",(String)request.getAttribute("msisdn"));
             context.put("campaignid",cid);
             context.put("sendAction",sendAction);
             context.put("contenturl","http://"+dmn.getContentUrl());
             context.put("clubprice","6x3");
             context.put("landingPage",landingloaded);
             
         	za_engine.getTemplate(landingloaded).evaluate(writer, context);
            	 java.sql.Date today=new java.sql.Date(System.currentTimeMillis());
            	 
            	 try{
            	 CampaignHitCounter campaignHitCounter=campaignhitcounterdao.HitRecordExistsOrNot(today,domain,cid,landingPage);
            	 if(campaignHitCounter==null){
            	 	campaignHitCounter=new CampaignHitCounter();
            	 	campaignHitCounter.setaUnique(Misc.generateUniqueId());
            	 	campaignHitCounter.setaDomainUnique(domain);
            	 	campaignHitCounter.setCampaignId(cid);
            	 	campaignHitCounter.setLandingPage(landingPage);
            	 	campaignHitCounter.setDate(today);
            	 	campaignHitCounter.setHitCounter(1);
            	 	campaignHitCounter.setSubscribeCounter(0);
            	 	campaignhitcounterdao.saveCampaignHitCounter(campaignHitCounter);
            	 	
            	 }else{
            	 	//campaignHitCounter.setDate(today);
            	 	campaignhitcounterdao.updateCampaignHitCounter(campaignHitCounter);
            	 }
            	 }catch(Exception e){
            		 e.printStackTrace();
            	 }
             
        	
        		
        	return;
            //response.sendRedirect(wapurl);return;
        }
        
                
    }
    
    else if(wapoptin!=null && wapoptin.equalsIgnoreCase("false"))
    {
        String smsredirection=(String) request.getParameter("smsconfirmed");
        if(smsredirection==null ) smsredirection=(String) request.getAttribute("smsconfirmed");
        
        if(smsredirection!=null) 
        {
            request.getServletContext().getRequestDispatcher(smsredirection).forward(request, response);
            
            //response.sendRedirect(smsredirection);
            
            return;
        }
    }
   
}

System.out.println("NEED TO CONFIRM USER:"+ needToConfirm);

if(needToConfirm==null||simulateVideo.equals("video")){

	
%>

<jsp:include page="/ZA/IndexMain"/>

<%
  String redirecturl=(String) request.getParameter("redirectto");
        if(redirecturl==null) redirecturl=(String) request.getAttribute("redirectto");
        if(redirecturl==null) redirecturl=(String) session.getAttribute("redirectto");
        if(redirecturl!=null)
        {
            response.sendRedirect(redirecturl);
            return;
        }



%>

<%}%>