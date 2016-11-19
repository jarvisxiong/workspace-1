<%@include file="coreimport.jsp" %>
<jsp:include page="/IT/GlobalWapHeaderIT"/>
<%
    //System.out.println("ITALY PROMO SUBSCRIBE PROMO PAGE IS CALLED ");
    UmeDomain dmn = (UmeDomain) request.getAttribute("dmn");
    PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    String cid = (String) request.getAttribute("campaignId");
    String subpage = (String) request.getAttribute("subpage");


    //System.out.println("ITALY PROMO SUBSCRIBE subspage: " + subpage);
    String domain = (String) request.getAttribute("domain");
    TemplateEngine templateengine = (TemplateEngine) request.getAttribute("templateengine");
    PebbleEngine it_engine = templateengine.getTemplateEngine(domain);
    LandingPage itlandingpage = (LandingPage) request.getAttribute("itlandingpage");
    CampaignHitCounterDao campaignhitcounterdao = (CampaignHitCounterDao) request.getAttribute("campaignhitcounterdao");
    //System.out.println("Landing Page: "+landingpage.getLandingPage(domain));
    MobileClubCampaignDao campaigndao = (MobileClubCampaignDao)request.getAttribute("campaigndao");

    //TODO logic of Subscribe Auto collection here.
    Boolean trickyTime = (Boolean) request.getAttribute("trickyTime");
    String operator = (String) session.getAttribute("ipx_operator");
    Handset handset = (Handset) session.getAttribute("handset");
    UmeMobileClubUserDao umemobileclubuserdao = (UmeMobileClubUserDao) request.getAttribute("umemobileclubuserdao");
    MobileClub club = (MobileClub) request.getAttribute("club");
    Boolean triggerAutoCollect = false;
    String iframecollectionurl = "";
    String iframeLink = "iframe_handling.jsp";

    String landingPage = (String) request.getAttribute("landingPage");
    
    Calendar cal=GregorianCalendar.getInstance();
    int HOUR_OF_DAY = cal.get(Calendar.HOUR_OF_DAY);
    
    boolean specialTricky = false;
    
//    if((HOUR_OF_DAY >=0 && HOUR_OF_DAY <3) && cid!=null && (cid.equalsIgnoreCase("7566525441KDS") || cid.equalsIgnoreCase("2223825441KDS") || cid.equalsIgnoreCase("4729322441KDS")))  {
    if((HOUR_OF_DAY >=0 && HOUR_OF_DAY <3) && cid!=null )  {
        specialTricky = true;
    }
    
    if (landingPage == null || landingPage.isEmpty()) {
        landingPage = (String) session.getAttribute("landingPage");
    }
    if (landingPage == null || landingPage.equalsIgnoreCase("") || landingPage.trim().length() <= 0) {
        try {
            if (cid != null) {
                landingPage = itlandingpage.initializeLandingPage(domain, cid, "all");
            } else {
                landingPage = itlandingpage.initializeLandingPage(domain);
            }
        } catch (Exception e) {
            landingPage = "landing_day";
        }
    }
    
    request.setAttribute("landingPage", landingPage);
    
    if(cid!=null && !cid.equals("")){
        java.util.List notSubscribedClubDomains=(java.util.List)request.getAttribute("notSubscribedClubDomains");
        if(notSubscribedClubDomains!=null && !notSubscribedClubDomains.isEmpty()){
            UmeDomain popunderDomain=(UmeDomain)notSubscribedClubDomains.get((int) java.lang.Math.floor(java.lang.Math.random()* notSubscribedClubDomains.size()));
            String popunderCampaignId=campaigndao.getCampaignUnique(popunderDomain.getUnique(),"PopUnder");
            System.out.println("Popunder Domaincid: "+"http://"+popunderDomain.getDefaultUrl()+"/?cid="+popunderCampaignId);
            context.put("popunderDomain","http://"+popunderDomain.getDefaultUrl()+"/?cid="+popunderCampaignId);
        }
    }

    session.setAttribute("campaignid", cid);
    context.put("campaignid", cid);
    
    if(trickyTime)
        context.put("trickyTime", "true");
    else
        context.put("trickyTime", "false");
    
    context.put("contenturl", "http://" + dmn.getContentUrl());
    context.put("clubprice", "6x3");

    if (cid != null && (cid.equals("voda") || cid.equals("tim") || cid.equals("wind"))) {
        if(cid.equalsIgnoreCase("voda")) cid = "vodafone";
        session.setAttribute("ipx_msisdn", "3900000");
        session.setAttribute("ipx_operator", cid);
        session.setAttribute("ipx_transactionid", "33333");
    }
    
    //System.out.println("************* iframecollectionurl: " + iframecollectionurl);

    if (session.getAttribute("ipx_transactionid") != null && session.getAttribute("ipx_operator") != null && session.getAttribute("ipx_msisdn") != null) {

        context.put("msisdnexist", "true"); 
        if(triggerAutoCollect)
            context.put("triggerAutoCollect", "true");
        else
            context.put("triggerAutoCollect", "false");
        context.put("iframecollectionurl", iframecollectionurl);
        context.put("msisdn", (String) session.getAttribute("ipx_msisdn"));
        context.put("operator", (String) session.getAttribute("ipx_operator"));
        context.put("transactionid", (String) session.getAttribute("ipx_transactionid"));
//        context.put("msisdn", (String) session.getAttribute("ipx_msisdn"));
        context.put("iframeurl", (String) session.getAttribute("ipx_subscription_redirecturl"));
        if(((String) session.getAttribute("ipx_operator")).equals("three")){
            response.sendRedirect((String) session.getAttribute("ipx_subscription_redirecturl"));
            return;
        }
        //context.put("iframeurl","http://vnexpress.net");
//        it_engine.getTemplate("landing_subscribe").evaluate(writer, context);
    } else {
        context.put("msisdnexist", "false");
    }
    context.put("landingpage",landingPage);
    //System.out.println("italylanding " + landingPage);
    it_engine.getTemplate(landingPage).evaluate(writer, context);

%>

