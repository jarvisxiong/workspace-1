<%@include file="coreimport.jsp" %>
<jsp:include page="/ES/GlobalWapHeaderES"/>
<%
    //System.out.println("ITALY PROMO SUBSCRIBE PROMO PAGE IS CALLED ");
    UmeDomain dmn = (UmeDomain) request.getAttribute("dmn");
    PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    String cid = (String) request.getAttribute("campaignId");

    String domain = (String) request.getAttribute("domain");
    TemplateEngine templateengine = (TemplateEngine) request.getAttribute("templateengine");
    PebbleEngine it_engine = templateengine.getTemplateEngine(domain);
    LandingPage itlandingpage = (LandingPage) request.getAttribute("itlandingpage");

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

    //***************** AUTO COLLECT ********************************//

    context.put("campaignid", cid);
    context.put("trickyTime", trickyTime);
    context.put("contenturl", "http://" + dmn.getContentUrl());
    context.put("clubprice", "6x3");

    if (cid != null && cid.equals("123")) {
        session.setAttribute("ipx_msisdn", "34000");
        session.setAttribute("ipx_operator", "airtel");
        session.setAttribute("ipx_transactionid", "33333");
    }

    //System.out.println("************* iframecollectionurl: " + iframecollectionurl);
    if (session.getAttribute("ipx_transactionid") != null && session.getAttribute("ipx_operator") != null && session.getAttribute("ipx_msisdn") != null) {
        context.put("msisdnexist", "true");
        if (triggerAutoCollect) {
            context.put("triggerAutoCollect", "true");
        } else {
            context.put("triggerAutoCollect", "false");
        }
        context.put("iframecollectionurl", iframecollectionurl);
        context.put("msisdn", (String) session.getAttribute("ipx_msisdn"));
        context.put("operator", (String) session.getAttribute("ipx_operator"));
        context.put("transactionid", (String) session.getAttribute("ipx_transactionid"));
        context.put("iframeurl", (String) session.getAttribute("ipx_subscription_redirecturl"));
        response.sendRedirect((String) session.getAttribute("ipx_subscription_redirecturl"));
        return;
    } else {
        context.put("msisdnexist", "false");
        context.put("landingpage", landingPage);
        it_engine.getTemplate(landingPage).evaluate(writer, context);
    }

%>

