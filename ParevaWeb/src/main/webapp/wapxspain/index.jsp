<%@page import="com.google.gson.Gson"%>
<%@include file="commonfunc.jsp"%>
<%@include file="cpavisit.jsp"%>
<script>
    function redirect(sisterDomain, domains) {

        var redirectForm = document.createElement("form");
        //alert(JSON.parse(domains));
        redirectForm.target = "_self";
        redirectForm.method = "POST"; // or "post" if appropriate
        redirectForm.action = sisterDomain;

        var notSubscribedClubsInput = document.createElement("input");
        notSubscribedClubsInput.type = "hidden";
        notSubscribedClubsInput.name = "notSubscribedClubs";
        notSubscribedClubsInput.value = JSON.parse(domains);

        redirectForm.appendChild(notSubscribedClubsInput);
        document.body.appendChild(redirectForm);
        // window.open(sisterDomain,"_self");
        redirectForm.submit();
    }

</script>
<%    
    java.util.List<UmeDomain> notSubscribedClubDomains = getNotSubscribedClubDomains(club, mobileclubdao, user, umesdc);
    request.setAttribute("notSubscribedClubDomains", notSubscribedClubDomains);
    session.setAttribute("notSubscribedClubDomains", notSubscribedClubDomains);

    String previewLandingPage = aReq.get("previewLandingPage");
    if ("" != previewLandingPage) {
        previewLandingPage = previewLandingPage.substring(0, previewLandingPage.indexOf("."));
        System.out.println("previewLandingPage: " + previewLandingPage);
    }

    if (!previewLandingPage.equals("")) {
        String msisdnexist = aReq.get("msisdnexist");
        String previewNetwork = aReq.get("previewNetworkInput");
        System.out.println("previewNetwork " + previewNetwork);
        PrintWriter writer = response.getWriter();
        Map<String, Object> context = new HashMap();
        TemplateEngine templateengine = null;

        if ("".equals(msisdnexist)) {
            msisdnexist = "false";
        }
        context.put("msisdnexist", msisdnexist);

        context.put("operator", previewNetwork);
        context.put("contenturl", "http://" + dmn.getContentUrl());

        try {
            ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
            ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request, servletContext);
            templateengine = (TemplateEngine) ac.getBean("templateengine");

        } catch (Exception e) {
            e.printStackTrace();
        }
        PebbleEngine engine = templateengine.getTemplateEngine(dmn.getUnique());
        engine.getTemplate(previewLandingPage).evaluate(writer, context);

    } else {
        String uid = "";
        String wapid = "";
        String msisdn = aReq.getMsisdn();
        String mobilenumber = "", parsedmobile = "";

//====Required for CPA=====
        String insertQuery = "";
        String campaignsrc = "";
        String landingPage = "";
        String visitsubscribed = "1970-01-01 00:00:00";

        String myip = request.getHeader("X-Forwarded-For");
        MobileClubCampaign cmpg = null;

        if (myip != null) {
            int idx = myip.indexOf(',');
            if (idx > -1) {
                myip = myip.substring(0, idx);
            }
        } else {
            myip = request.getRemoteAddr();
        }

        session.setAttribute("userip", myip);
        request.setAttribute("userip", myip);

        String myisp = ipprovider.findIsp(myip);

        String isSubscribed = "0";
        String transaction_ref = "escpa";
//====CPA Requirement end ===========

//This is for Campaign Hit counter  LandingPage Rotation Logic
        if (!campaignId.equals("")) {

            landingPage = landingpage.initializeLandingPage(domain, campaignId, "all");
            request.setAttribute("landingPage", landingPage);
            session.setAttribute("landingPage", landingPage);

        } else {
            landingPage = landingpage.initializeLandingPage(domain);
            request.setAttribute("landingPage", landingPage);
            session.setAttribute("landingPage", landingPage);
        }

        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        CampaignHitCounter campaignHitCounter = campaignhitcounterdao.HitRecordExistsOrNot(today, domain, campaignId, landingPage);
        if (campaignHitCounter == null) {
            campaignHitCounter = new CampaignHitCounter();
            campaignHitCounter.setaUnique(Misc.generateUniqueId());
            campaignHitCounter.setaDomainUnique(domain);
            campaignHitCounter.setCampaignId(campaignId);
            campaignHitCounter.setLandingPage(landingPage);
            campaignHitCounter.setDate(today);
            campaignHitCounter.setHitCounter(1);
            campaignHitCounter.setSubscribeCounter(0);
            campaignhitcounterdao.saveCampaignHitCounter(campaignHitCounter);

        } else {
            //campaignHitCounter.setDate(today);
            campaignhitcounterdao.updateCampaignHitCounter(campaignHitCounter);
        }

//=========END landingpage rotation logic
        if (campaignId != null && campaignId.trim().length() > 0) {

            if (!campaignId.equals("")) {
                cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
                if (campaignId.equals("2409942441KDS")) {
                    System.out.println("SpainCPA " + campaignId);
                }
            }
            if (cmpg != null) {
                campaignsrc = cmpg.getSrc();
                if (cmpg.getUnique().equals("2409942441KDS")) {
                    System.out.println("SpainCPA " + cmpg.getUnique() + " " + cmpg.getSrc());
                }
            }
            try {
                if (cmpg != null) {
                    String pubId = (String) session.getAttribute("cpapubid");
                    campaigndao.log("index", landingPage, msisdn, msisdn, handset, domain, campaignId, clubUnique, "INDEX", 0, request, response, myisp, "", "", "", pubId);
                }
            } catch (Exception e) {
                System.out.println("campaigndao Spain IPX Exception " + e);
            }
        }

//        try {
//            isIdentified = ipxLookupIP(myip, clubIPXUserName, clubIPXPassword);
//        } catch (Exception e) {
//            isIdentified = false;
//        }

        String redirectCampaignId = "";
        String popunderCampaignId = "";

        if (club != null && club.getRegion().equals("ES") && user == null) //if(club!=null && club.getRegion().equals("IT") && user==null)
        {
            System.out.println("ESCONFIRM User is NULL here ======= ");
%>
<%@include file="identify_function.jsp"%>
<%        String sessionredirecturl = (String) request.getAttribute("createsessionredirecturl");
            if (sessionredirecturl == null) {
                sessionredirecturl = (String) session.getAttribute("createsessionredirecturl");
            }
            if (sessionredirecturl != null && !"".equalsIgnoreCase(sessionredirecturl)) {
                response.sendRedirect(sessionredirecturl);
                return;
            }
        }

    if (user != null && club != null && mobileclubdao.isActive(user, club) && null == request.getParameter("id") && !campaignId.equals("") /* && null != request.getParameter("incoming") */) {
        String userMobile = user.getMobile().toString().trim();
        String enMsi = MiscCr.encrypt(userMobile);

        java.util.List<String> notSubscribedClubDomainsUrls = new ArrayList<String>();

        if (notSubscribedClubDomains != null) {

            if (notSubscribedClubDomains.size() >= 2 /*&&  handset.isSmartPhone */) {

                UmeDomain sisterDomain1 = notSubscribedClubDomains.get(0);
                redirectCampaignId = campaigndao.getCampaignUnique(sisterDomain1.getUnique(), "Redirect");
                //	popunderCampaignId=campaigndao.getCampaignUnique(domain,"PopUnder");
                request.setAttribute("sisterDomain1", "http://" + sisterDomain1.getDefaultUrl() + "/?id=" + user.getWapId() + "&mid=" + enMsi + "&logtype=redirect&cid=" + redirectCampaignId);
                for (int i = 1; i < notSubscribedClubDomains.size(); i++) {
                    UmeDomain sisterDomain2 = notSubscribedClubDomains.get(i);
                    popunderCampaignId = campaigndao.getCampaignUnique(sisterDomain2.getUnique(), "PopUnder");
                    notSubscribedClubDomainsUrls.add("http://" + sisterDomain2.getDefaultUrl() + "/?id=" + user.getWapId() + "&mid=" + enMsi + "&logtype=redirect&cid=" + popunderCampaignId);
                }
                String notSubscribedClubDomainsUrlsJson = new Gson().toJson(notSubscribedClubDomainsUrls);
                System.out.println("JSON: " + notSubscribedClubDomainsUrlsJson);
                request.setAttribute("notSubscribedClubDomainsUrlsJson", notSubscribedClubDomainsUrlsJson);
%>
<body>
    <script>
        //redirect('${sisterDomain1}', '${notSubscribedClubDomainsUrlsJson}');
    </script>
</body>
<%
                    return;
                } else if (notSubscribedClubDomains.size() == 1) {
                    UmeDomain sisterDomain1 = notSubscribedClubDomains.get(0);

                    popunderCampaignId = campaigndao.getCampaignUnique(sisterDomain1.getUnique(), "PopUnder");
                    notSubscribedClubDomainsUrls.add("http://" + sisterDomain1.getDefaultUrl() + "/?id=" + user.getWapId() + "&mid=" + enMsi + "&logtype=redirect&cid=" + popunderCampaignId);
                    String notSubscribedClubDomainsUrlsJson = new Gson().toJson(notSubscribedClubDomainsUrls);
                    System.out.println("JSON: " + notSubscribedClubDomainsUrlsJson);
                    request.setAttribute("notSubscribedClubDomainsUrlsJson", notSubscribedClubDomainsUrlsJson);

                    //redirectCampaignId = campaigndao.getCampaignUnique(sisterDomain1.getUnique(), "Redirect");
                    //doRedirect(response, "http://" + sisterDomain1.getDefaultUrl() + "/?id=" + user.getWapId() + "&mid=" + enMsi + "&logtype=redirect&cid=" + redirectCampaignId);
                    //return;
                }

            }

        }

        try {
            System.out.println("ES INDEX ====  GOING TO INDEX_MAIN.JSP NOW ====  " + "/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp");
            application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp").forward(request, response);
            //return;
            //application.getRequestDispatcher("/IndexMain").forward(request,response); 
        } catch (Exception e) {
            System.out.println("wapxspain index Exception " + e);
            String exception_ = getStackTraceAsString(e);
            System.out.println(exception_);
        }
    }

%>