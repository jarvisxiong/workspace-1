<%@page import="ume.pareva.snp.CacheManager"%>
<%@page import="ume.pareva.it.IpxBillingDirect"%>
<%@include file="coreimport.jsp" %>
<jsp:include page="/IT/GlobalWapHeaderIT"/>
<%
    UmeSessionParameters aReq = (UmeSessionParameters) request.getAttribute("aReq");
    UmeUser user = (UmeUser) request.getAttribute("user");
    UmeDomain dmn = (UmeDomain) request.getAttribute("dmn");
    SdcService service = (SdcService) request.getAttribute("service");
    MobileClub club = (MobileClub) request.getAttribute("club");
    MobileClubCampaign cmpg = (MobileClubCampaign) request.getAttribute("cmpg");
    String msisdn = (String) request.getAttribute("msisdn");
    String domain = (String) request.getAttribute("domain");
    UmeUserDao umeuserdao = (UmeUserDao) request.getAttribute("umeuserdao");
    UmeMobileClubUserDao umemobileclubuserdao = (UmeMobileClubUserDao) request.getAttribute("umemobileclubuserdao");
    MobileClubBillingPlanDao billingplandao = (MobileClubBillingPlanDao) request.getAttribute(("billingplandao"));
    UmeClubDetails userclubdetails = (UmeClubDetails) request.getAttribute("clubdetails");
    MobileClubCampaignDao campaigndao = (MobileClubCampaignDao) request.getAttribute("campaigndao");
    IpxBillingDirect directBilling = (IpxBillingDirect) request.getAttribute("ipxdirectbilling");
    Boolean nightTime = (Boolean) request.getAttribute("nightTime");
    MobileClubDao clubdao = (MobileClubDao)request.getAttribute("mobileclubdao");
    CacheManager cachemanager = (CacheManager)request.getAttribute("cachemanager");
    ZACPA zacpa=(ZACPA) request.getAttribute("zacpa");
    UserAuthentication userauthentication=(UserAuthentication) request.getAttribute("userauthentication");


    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String confirm = aReq.get("confirm");

    String clubIPXUserName = (String) session.getAttribute("clubIPXUserName");
    String clubIPXPassword = (String) session.getAttribute("clubIPXPassword");
    
    
    Map<String, Object> context = new HashMap();
    TemplateEngine templateengine=(TemplateEngine)request.getAttribute("templateengine");
    PebbleEngine it_engine=templateengine.getTemplateEngine(domain);
    PrintWriter writer = response.getWriter();

//break//
//if(confirm.equals("yes")){
//if(confirm.equals("yes") || clubIPXUserName.equals("umesolo18-it")){
    if (confirm.equals("yes")) {

        if (user != null && club != null) {
            msisdn = user.getParsedMobile();

            SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUser(user.getUnique(), club.getUnique());
            String subscriptionId = "";
            if (clubUser != null) {
                subscriptionId = clubUser.getParam2();

                if (nightTime && !clubUser.getCampaign().equals("")) {
                    response.sendRedirect("/terms.jsp?termspage=terminated");
                    return;
                }
               //IpxLogUnsubscribe.unsubscribeLog(clubUser, "Terminate from user click disactivate link", "3");
            }

            System.out.println("IPX Terminate request: msisdn: " + msisdn);
            System.out.println("IPX Terminate request: subscriptionId: " + subscriptionId);
            try {
//            String anIpxUserId = "universalmob-it"; 
//            String anIpxPassword = "987OLIpt5r";
                // Target URL 
                URL anIpxSubUrl = new URL("http://europe.ipx.com/api/services2/SubscriptionApi31?wsdl");

                SubscriptionApiPort aPort = new SubscriptionApiServiceLocator().getSubscriptionApi31(anIpxSubUrl);

                // Set read timeout to 10 minute            
                ((SubscriptionApiBindingStub) aPort).setTimeout(10 * 60 * 1000);
                TerminateSubscriptionRequest aTerminateRequest = new TerminateSubscriptionRequest();
                aTerminateRequest.setCorrelationId("universalmob");
                aTerminateRequest.setConsumerId(msisdn);
                aTerminateRequest.setSubscriptionId(subscriptionId);
                aTerminateRequest.setUsername(clubIPXUserName);
                aTerminateRequest.setPassword(clubIPXPassword);

                TerminateSubscriptionResponse aTerminateResponse = aPort.terminateSubscription(aTerminateRequest);
                // Debug
                System.out.println("IPX Terminate result: responseCode: " + aTerminateResponse.getResponseCode());
                System.out.println("IPX Terminate result: responseMessage: " + aTerminateResponse.getResponseMessage());

                //Checking from 2013 - 04 - 02
                clubdao.unsubscribe(club, null, msisdn);
                cachemanager.delete(clubUser.getUserUnique());
                userauthentication.invalidateUser(request);
                campaigndao.log("IPXReq", "", clubUser.getUserUnique(), clubUser.getParsedMobile(), null, domain, clubUser.getCampaign(), clubUser.getClubUnique(), "STOP", 0, request, response, clubUser.getNetworkCode());
                    if(clubUser.getCampaign()!=null && clubUser.getCampaign().trim().length()>0)
                            cmpg = UmeTempCmsCache.campaignMap.get(clubUser.getCampaign());
                            
                            if(cmpg!=null && cmpg.getSrc().endsWith("RS")) {
                            String revshareloggingquery="insert into revShareLogging "
                              + "(aUnique,aAmount,aCurrencyCode,aParsedMobile,aEncryptedMSISDN,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc,aEvent) values" +"('"+Misc.generateUniqueId()+"','0','"+cmpg.getPayoutCurrency()+"','"+clubUser.getParsedMobile()+"','"+MiscCr.encrypt(clubUser.getParsedMobile())+"','"+clubUser.getCampaign()+"','"+club.getUnique()+"','"+sdf2.format(new Date())+"','"+sdf2.format(new Date())+"','0','"+clubUser.getNetworkCode()+"','"+cmpg.getSrc()+"','2')";
                                int updateRow=zacpa.executeUpdateCPA(revshareloggingquery);
                            }
                
//                request.getSession().setAttribute("ipx_msisdn", null);
                request.getSession().setAttribute("ipx_msisdn", null);
                request.getSession().setAttribute("ipx_transactionid", null);
                request.getSession().setAttribute("ipx_operator", null);
                request.getSession().setAttribute("ipx_subscriptionid", null);
                System.out.println("IPX terminate go to index.jsp");

                //TODO deactivate whole billables
                //response.sendRedirect("/index.jsp");                
                response.sendRedirect("/terms.jsp?termspage=terminated");
                return;
                
//                if (clubIPXUserName.equals("umesolo18-it")) {
//                    //TODO redirect from here
//                    response.sendRedirect("/?pg=9194319217931KDS");
//                    return;
//                } else {
//                    //TODO redirect from here
//                    response.sendRedirect("/?pg=9066933326831KDS");
//                    return;
//                }
                
            } catch (Exception e) {
                System.out.println("IPX Terminate terminateSubscription catching exception: " + e.getMessage());
//                response.sendRedirect("/terminate_message.jsp?message=2");
//                return;
                response.sendRedirect("/terms.jsp?termspage=terminated");
                return;
            }
        } else {
            System.out.println("IPX Terminate you are not a user");
//            response.sendRedirect("/index.jsp");
//            return;
            response.sendRedirect("/terms.jsp?termspage=terminated");
            return;            
        }
    } else if (confirm.equals("no")) {
        //System.out.println("IPX Terminate user not terminate the subscription");
        response.sendRedirect("/index_main.jsp");
        return;
    }

    if (confirm.equals("")) {
        context.put("contenturl","http://"+ dmn.getContentUrl());
        it_engine.getTemplate("disactive").evaluate(writer, context);
    }
%>
