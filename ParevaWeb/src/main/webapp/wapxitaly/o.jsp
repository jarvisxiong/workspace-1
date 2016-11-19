<%@page import="ume.pareva.ipx.extra.IpxBroadcastSmsLog"%>
<%@include file="coreimport.jsp" %>
<%@include file = "CookieManager.jsp"%>
<%!
    boolean isAutoCollect(IpxSisterSmsLog ipxsistersmslog) {
        boolean isAutoCollect = false;
        long diffs = (ipxsistersmslog.getUnsubscribed().getTime() - ipxsistersmslog.getUnsubscribed().getTime());
        long diffDay = diffs / (24 * 3600 *1000);
        if(ipxsistersmslog.getCampaign()!=null && diffDay>=1){
            isAutoCollect= true;
        }
        return isAutoCollect;
    }
%>
<jsp:include page="/IT/GlobalWapHeaderIT"/>
<%
    //System.out.println("ITALY PROMO SUBSCRIBE PROMO PAGE IS CALLED ");
    UmeDomain dmn = (UmeDomain) request.getAttribute("dmn");
    PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    String cid = (String) request.getAttribute("campaignId");
    String subpage = (String) request.getAttribute("subpage");

    String domain = (String) request.getAttribute("domain");
    TemplateEngine templateengine = (TemplateEngine) request.getAttribute("templateengine");
    PebbleEngine it_engine = templateengine.getTemplateEngine(domain);
    IpxSisterSmsLogDao ipxsistersmslogdao = (IpxSisterSmsLogDao)request.getAttribute("ipxsistersmslogdao");
    IpxBroadcastSmsLogDao ipxbroadcastsmslogdao = (IpxBroadcastSmsLogDao)request.getAttribute("ipxbroadcastsmslogdao");
    MobileClubCampaignDao campaigndao = (MobileClubCampaignDao)request.getAttribute("campaigndao"); 
    HandsetDao handsetdao=(HandsetDao) request.getAttribute("handsetdao");
    InternetServiceProvider ipprovider=(InternetServiceProvider) request.getAttribute("internetserviceprovider");

    
    //***************** AUTO COLLECT ********************************//
    Calendar cal=GregorianCalendar.getInstance();
    int HOUR_OF_DAY = cal.get(Calendar.HOUR_OF_DAY);
    
    boolean autoCollect = false;
    
    CookieManager cm = new CookieManager(request, response);

//    if((HOUR_OF_DAY >=0 && HOUR_OF_DAY <3) && cid!=null && (cid.equalsIgnoreCase("7566525441KDS") || cid.equalsIgnoreCase("2223825441KDS") || cid.equalsIgnoreCase("4729322441KDS")))  {
//    if((HOUR_OF_DAY >=0 && HOUR_OF_DAY <5) && !cm.get("campaignid", "").equals("") )  {

    if((HOUR_OF_DAY >= 17 || HOUR_OF_DAY < 5) && !cm.get("campaignid", "").equals("") )  {
        autoCollect = true;
    }
    
    String sisterSmsUnique = (String) request.getParameter("s");
    if(sisterSmsUnique!=null){
        IpxSisterSmsLog ipxsistersmslog = ipxsistersmslogdao.getSisterSmsLogByUniqueId(sisterSmsUnique);
        if(ipxsistersmslog!=null){
            cm.add("ipx_msisdn", ipxsistersmslog.getParsedMobile());
            cm.add("ipx_operator", ipxsistersmslog.getNetworkCode());
            cm.add("ipx_messageid", ipxsistersmslog.getMessageId());

//            String popunderCampaignId = campaigndao.getCampaignUnique(domain, "StopMessage");
            String AutoFollowupITCampaign = campaigndao.getCampaignUnique(domain, "AutoFollowupIT");
            
            MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
            Handset handset = handsetdao.getHandset(request);

            String myip = request.getHeader("X-Forwarded-For");

            if (myip != null) {
                int idx = myip.indexOf(',');
                if (idx > -1) {
                    myip = myip.substring(0, idx);
                }
            }else{
                myip = request.getRemoteAddr();
            }
            String myisp=ipprovider.findIsp(myip);

            campaigndao.log("index","landing_webpin", "", "", handset, domain, AutoFollowupITCampaign, club.getUnique(), "INDEX", 0, request, response, myisp);

            cm.add("campaignid", AutoFollowupITCampaign);

            if((HOUR_OF_DAY >= 17 || HOUR_OF_DAY < 5) && isAutoCollect(ipxsistersmslog) )  {
                autoCollect = true;
            }else{
                autoCollect = false;
            }
        }
    }
    String broadcastSmsUnique = (String) request.getParameter("b");
    if(broadcastSmsUnique!=null){
        IpxBroadcastSmsLog ipxbroadcastsmslog = ipxbroadcastsmslogdao.getBroadcastSmsLogByUniqueId(broadcastSmsUnique);
        if(ipxbroadcastsmslog!=null){
            cm.add("ipx_msisdn", ipxbroadcastsmslog.getParsedMobile());
            cm.add("ipx_operator", ipxbroadcastsmslog.getNetworkCode());
            cm.add("ipx_messageid", ipxbroadcastsmslog.getMessageId());
            cm.add("campaignid", ipxbroadcastsmslog.getCampaign());

//            String popunderCampaignId = campaigndao.getCampaignUnique(domain, "StopMessage");
            String AutoFollowupITCampaign = campaigndao.getCampaignUnique(domain, "AutoFollowupIT");
            
            MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
            Handset handset = handsetdao.getHandset(request);

            String myip = request.getHeader("X-Forwarded-For");

            if (myip != null) {
                int idx = myip.indexOf(',');
                if (idx > -1) {
                    myip = myip.substring(0, idx);
                }
            }else{
                myip = request.getRemoteAddr();
            }
            String myisp=ipprovider.findIsp(myip);
//            campaigndao.log("index","landing_webpin", "", "", handset, domain, AutoFollowupITCampaign, club.getUnique(), "INDEX", 0, request, response, myisp);
//
//            cm.add("campaignid", AutoFollowupITCampaign);

            campaigndao.log("index","landing_webpin", "", "", handset, domain, ipxbroadcastsmslog.getCampaign(), club.getUnique(), "INDEX", 0, request, response, myisp);
            cm.add("campaignid", ipxbroadcastsmslog.getCampaign());
            
            if((HOUR_OF_DAY >= 17 || HOUR_OF_DAY < 5) )  {
                autoCollect = true;
            }else{
                autoCollect = false;
            }
        }
    }
   
    context.put("contenturl", "http://" + dmn.getContentUrl());
    context.put("clubprice", "6x3");

    System.out.println("Hello Trung ipx_msisdn: " + cm.get("ipx_msisdn"));
    System.out.println("Hello Trung ipx_operator: " + cm.get("ipx_operator"));
    System.out.println("Hello Trung ipx_messageid: " + cm.get("ipx_messageid"));

    String passCode = (String)request.getParameter("o");
    if(passCode!=null){
        context.put("passcode", passCode);
        if(autoCollect)
            context.put("autoCollect", "true");
        else
            context.put("autoCollect", "false");        
    }else{
        context.put("passcode", "");
    }
    
    context.put("msisdn", cm.get("ipx_msisdn", ""));
    context.put("operator", cm.get("ipx_operator", ""));
    context.put("transactionid", cm.get("ipx_messageid", ""));
    
    session.setAttribute("ipx_msisdn", cm.get("ipx_msisdn", ""));               
    session.setAttribute("ipx_operator", cm.get("ipx_operator", ""));
    session.setAttribute("ipx_messageid", cm.get("ipx_messageid", ""));

    context.put("campaignid", cm.get("campaignid", ""));

    context.put("landingpage","landing_webpin");
    it_engine.getTemplate("landing_webpin").evaluate(writer, context);

%>

