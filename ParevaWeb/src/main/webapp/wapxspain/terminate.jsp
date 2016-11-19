<%@page import="ume.pareva.util.ZACPA"%>
<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@page import="ume.pareva.snp.CacheManager"%>
<%@page import="ume.pareva.it.IpxBillingDirect"%>
<%@include file="coreimport.jsp" %>
<jsp:include page="/ES/GlobalWapHeaderES"/>
<%
    UmeSessionParameters aReq = (UmeSessionParameters) request.getAttribute("aReq");
    UmeUser user = (UmeUser) request.getAttribute("user");
    UmeDomain dmn = (UmeDomain) request.getAttribute("dmn");
    MobileClub club = (MobileClub) request.getAttribute("club");
    MobileClubCampaign cmpg = (MobileClubCampaign) request.getAttribute("cmpg");
    String msisdn = (String) request.getAttribute("msisdn");
    String domain = (String) request.getAttribute("domain");
    UmeMobileClubUserDao umemobileclubuserdao = (UmeMobileClubUserDao) request.getAttribute("umemobileclubuserdao");
    MobileClubCampaignDao campaigndao = (MobileClubCampaignDao) request.getAttribute("campaigndao");
    Boolean nightTime = (Boolean) request.getAttribute("nightTime");
    StopUser stopUserDao = (StopUser)request.getAttribute("stopUserDao");

    String confirm = aReq.get("confirm");

    Map<String, Object> context = new HashMap();
    TemplateEngine templateengine=(TemplateEngine)request.getAttribute("templateengine");
    PebbleEngine it_engine=templateengine.getTemplateEngine(domain);
    PrintWriter writer = response.getWriter();

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
            }
            System.out.println("IPX Terminate request: msisdn: " + msisdn);
            System.out.println("IPX Terminate request: subscriptionId: " + subscriptionId);
            try {

                stopUserDao.stopSingleSubscriptionNormal(clubUser.getParsedMobile(), clubUser.getClubUnique(), request, response);
                
                campaigndao.log("IPXLink", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
                                    club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), "LINKSTOP", 0, request, response, clubUser.getNetworkCode());
//                request.getSession().setAttribute("ipx_msisdn", null);
                request.getSession().setAttribute("ipx_msisdn", null);
                request.getSession().setAttribute("ipx_transactionid", null);
                request.getSession().setAttribute("ipx_operator", null);
                request.getSession().setAttribute("ipx_subscriptionid", null);
                System.out.println("IPX terminate go to index.jsp");
               
                response.sendRedirect("/terms.jsp?termspage=terminated");
                return;
                
            } catch (Exception e) {
                System.out.println("IPX Terminate terminateSubscription catching exception: " + e.getMessage());
                response.sendRedirect("/terms.jsp?termspage=terminated");
                return;
            }
        } else {
            System.out.println("IPX Terminate you are not a user");
            response.sendRedirect("/terms.jsp?termspage=terminated");
            return;            
        }
    } else if (confirm.equals("no")) {
        response.sendRedirect("/index_main.jsp");
        return;
    }
    if (confirm.equals("")) {
        context.put("contenturl","http://"+ dmn.getContentUrl());
        it_engine.getTemplate("disactive").evaluate(writer, context);
    }
%>
