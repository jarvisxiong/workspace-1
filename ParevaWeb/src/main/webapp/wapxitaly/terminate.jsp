<%@page import="ume.pareva.it.IpxTimerSendSms"%>
<%@page import="ume.pareva.util.ZACPA"%>
<%@page import="ume.pareva.smsapi.IpxSmsSubmit"%>
<%@page import="ume.pareva.smsapi.IpxSmsConnection"%>
<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@page import="ume.pareva.snp.CacheManager"%>
<%@page import="ume.pareva.it.IpxBillingDirect"%>
<%@include file="coreimport.jsp" %>
<%@include file = "CookieManager.jsp"%>

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
//    MobileClubBillingPlanDao billingplandao = (MobileClubBillingPlanDao) request.getAttribute(("billingplandao"));
//    UmeClubDetails userclubdetails = (UmeClubDetails) request.getAttribute("clubdetails");
    MobileClubCampaignDao campaigndao = (MobileClubCampaignDao) request.getAttribute("campaigndao");
//    IpxBillingDirect directBilling = (IpxBillingDirect) request.getAttribute("ipxdirectbilling");
    Boolean nightTime = (Boolean) request.getAttribute("nightTime");
    MobileClubDao clubdao = (MobileClubDao)request.getAttribute("mobileclubdao");
    CacheManager cachemanager = (CacheManager)request.getAttribute("cachemanager");
    ZACPA zacpa=(ZACPA) request.getAttribute("zacpa");
    UserAuthentication userauthentication=(UserAuthentication) request.getAttribute("userauthentication");
    IpxTimerSendSms timersms = (IpxTimerSendSms)request.getAttribute("timersms");
    StopUser stopUserDao = (StopUser)request.getAttribute("stopUserDao");


    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String confirm = aReq.get("confirm");
    SdcRequest aSdcReq = new SdcRequest(request);

    String clubIPXUserName = (String) session.getAttribute("clubIPXUserName");
    String clubIPXPassword = (String) session.getAttribute("clubIPXPassword");

    Map<String, Object> context = new HashMap();
    TemplateEngine templateengine=(TemplateEngine)request.getAttribute("templateengine");
    PebbleEngine it_engine=templateengine.getTemplateEngine(domain);
    PrintWriter writer = response.getWriter();


    if (confirm.equals("yes")) {        
            String clubname = club.getClubName();
            try {
                msisdn = user.getParsedMobile();
                SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUser(user.getUnique(), club.getUnique());
                
                String popunderCampaignId = "";
                if(clubUser.getCampaign()!=null && !clubUser.getCampaign().isEmpty()){
                    java.util.List notSubscribedClubDomains=(java.util.List)request.getAttribute("notSubscribedClubDomains");
                    if(notSubscribedClubDomains==null || notSubscribedClubDomains.isEmpty())
                    notSubscribedClubDomains=(java.util.List)session.getAttribute("notSubscribedClubDomains");

                    if(notSubscribedClubDomains!=null && !notSubscribedClubDomains.isEmpty()){
                        UmeDomain popunderDomain=(UmeDomain)notSubscribedClubDomains.get((int) java.lang.Math.floor(java.lang.Math.random()* notSubscribedClubDomains.size()));
                        popunderCampaignId=campaigndao.getCampaignUnique(popunderDomain.getUnique(),"AutoFollowupIT");
                        MobileClub clubTemp = UmeTempCmsCache.mobileClubMap.get(popunderDomain.getUnique());

//                                    String AutoFollowupITCampaign = campaigndao.getCampaignUnique(domain, "AutoFollowupIT");
                        if(clubTemp!=null){     
                
                            String link = "http://"+popunderDomain.getDefaultUrl()+"/?cid="+popunderCampaignId;
                            //popunderDomain.getUseDefault();
                            String fromNumber = "VideoCasa";
                            String msg = "Prova VIDEO CASALINGHI per video per adulti illimitati. Clicca qui <LINK>";
                            if(link.contains("solo18")){
                                msg = "PROVA GRATUITA! Video per adulti illimitati su SOLO18. Prova gratis qui <LINK>";
                                fromNumber = "Solo18";
                            }
                            msg.replace("<LINK>", link);
                            int time = 0;
                            timersms.requestSendSmsMessageTimer(msisdn, fromNumber, msg, time);                         
                        }
                    }else{
                    }
                }else{
                    System.out.println("** User not campaign");
                }
                
                
            } catch (Exception e) {
                System.out.println("Exception SMSOPTIN for msisdn " + msisdn + " club" + clubname + ": " + e);
                e.printStackTrace();
            }
        
        
        //response.sendRedirect("/terms.jsp?termspage=terminated");
        //return;   
        
        if (user != null && club != null) {
            msisdn = user.getParsedMobile();

            SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUser(user.getUnique(), club.getUnique());
            String subscriptionId = "";
            if (clubUser != null) {
                subscriptionId = clubUser.getParam2();

                if (nightTime && !clubUser.getCampaign().equals("") && !clubUser.getNetworkCode().equals("vodafone")) {
                    application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/terms.jsp?termspage=terminated").forward(request, response);
                    return;
                    //response.sendRedirect("/terms.jsp?termspage=terminated");
                    //return;
                }
               //IpxLogUnsubscribe.unsubscribeLog(clubUser, "Terminate from user click disactivate link", "3");
            }

            //System.out.println("IPX Terminate request: msisdn: " + msisdn);
            //System.out.println("IPX Terminate request: subscriptionId: " + subscriptionId);
            try {
//            
                stopUserDao.stopSingleSubscriptionNormal(clubUser.getParsedMobile(), clubUser.getClubUnique(), request, response);
                campaigndao.log("IPXLink", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
                                    club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), "LINKSTOP", 0, request, response, clubUser.getNetworkCode());
                
                request.getSession().setAttribute("ipx_msisdn", null);
                request.getSession().setAttribute("ipx_transactionid", null);
                request.getSession().setAttribute("ipx_operator", null);
                request.getSession().setAttribute("ipx_subscriptionid", null);
                application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/terms.jsp?termspage=terminated").forward(request, response);
                //response.sendRedirect("/terms.jsp?termspage=terminated");
                return;
               
                
            } catch (Exception e) {
                System.out.println("IPX Terminate terminateSubscription catching exception: " + e.getMessage());
                application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/terms.jsp?termspage=terminated").forward(request, response);
                //response.sendRedirect("/terms.jsp?termspage=terminated");
                return;
            }
        } else {
            System.out.println("IPX Terminate you are not a user");
//            response.sendRedirect("/terms.jsp?termspage=terminated");
            application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/terms.jsp?termspage=terminated").forward(request, response);
            return;            
        }
        
    } else if (confirm.equals("no")) {
        //System.out.println("IPX Terminate user not terminate the subscription");
        response.sendRedirect("/index_main.jsp");
        application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp").forward(request, response);
        return;
    }

    if (confirm.equals("")) {
        context.put("contenturl","http://"+ dmn.getContentUrl());
        it_engine.getTemplate("disactive").evaluate(writer, context);
    }
%>