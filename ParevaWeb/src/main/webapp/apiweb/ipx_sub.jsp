<%@page import="ume.pareva.util.ZACPA"%>
<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@page import="ume.pareva.snp.CacheManager"%>
<%@page import="ume.pareva.it.IpxBillingDirect"%>
<%@include file="coreimport.jsp" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
//***************************************************************************************************
    SdcRequest aReq = new SdcRequest(request);
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Calendar nowTime = GregorianCalendar.getInstance();
    nowTime.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
    Date currentDate = nowTime.getTime();
// important:
    aReq.setPageEnc("iso-8859-1");
    String fileName = "index_ipx_charge";
//***************************************************************************************************

    StringBuilder sbf = new StringBuilder();
    String crlf = "\r\n";
    String debugFile = "IPX_notify_sub_debug_" + MiscDate.sqlDate.format(new Date()) + ".txt";
    String debugPath = "/var/log/pareva/IT/ipxlog/notify/";

    sbf.append(fileName.toUpperCase() + ".JSP: " + new Date() + crlf + crlf);
    response.setContentType("text/plain; charset=UTF-8");

//***************************************************************************************************
    String ip = request.getRemoteAddr();
// Restrict IP
    System.out.println("IPX ip: " + ip);
    System.out.println("IPX -----------Subscription Notification---------- Starting " + fileName);

    String subscriptionId = aReq.get("SubscriptionId");
    String subscriptionStatus = aReq.get("SubscriptionStatus");
    String subscriptionStatusMessage = aReq.get("SubscriptionStatusMessage");

    String SessionId = aReq.get("SessionId");

    String clubUnique = aReq.get("club");

    if (true) {
        sbf.append(fileName.toUpperCase() + ".JSP: " + new Date() + crlf);
        sbf.append("############# IPXM subscription Notification HTTP PARAMETERS" + crlf);
        Enumeration ee = request.getParameterNames();
        for (; ee.hasMoreElements();) {
            String elem = (String) ee.nextElement();
            sbf.append("IPXM subscription Notification --" + elem + ": " + request.getParameter(elem) + crlf);
        }
        sbf.append("END ############# IPXM subscription Notification HTTP PARAMETERS" + crlf);
    }
    FileUtil.writeRawToFile(debugPath + debugFile, sbf.toString() + crlf, true);

//***************************************************************************************************
    UmeMobileClubUserDao clubuserdao = null;
    MobileClubCampaignDao campaigndao = null;
    MobileClubBillingPlanDao billingplandao = null;
    MobileClubDao clubdao = null;
//ZACPA zacpa=null;
    MobileClubCampaign cmpgn = null;
    IpxBillingDirect billingDirect = null;
    CacheManager cacheManager = null;
    ZACPA zacpa = null;
    UserAuthentication userauthentication = null;
    CheckStop checkstop = null;

    try {
        ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
        ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request, servletContext);
        clubuserdao = (UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
        clubdao = (MobileClubDao) ac.getBean("mobileclubdao");
        //umesmsdaoextension=(UmeSmsDaoExtension) ac.getBean("umesmsdaoextension");
        campaigndao = (MobileClubCampaignDao) ac.getBean("mobileclubcampaigndao");
        billingplandao = (MobileClubBillingPlanDao) ac.getBean("billingplandao");
        //zacpa=(ZACPA) ac.getBean("zacpa");
        billingDirect = (IpxBillingDirect) ac.getBean("ipxbillingdirect");
        cacheManager = (CacheManager) ac.getBean("cachemanager");
        zacpa = (ZACPA) ac.getBean("zacpa");
        userauthentication = (UserAuthentication) ac.getBean("authorizeuser");
        checkstop = (CheckStop) ac.getBean("checkstop");

    } catch (Exception e) {
        e.printStackTrace();
    }

//String ipx_reponse = "";
    try {
        if (subscriptionStatus.equals("3") || subscriptionStatus.equals("4")) {

            System.out.println("IPX SubscriptionId: " + subscriptionId);
            System.out.println("IPX SubscriptionStatus: " + subscriptionStatus);
            System.out.println("IPX SubscriptionStatusMessage: " + subscriptionStatusMessage);
            System.out.println("IPX ---------Subscription Notification ------------ Ending " + fileName);
            System.out.println("IPX SubscriptionId has been terminated: " + subscriptionId);

            sbf.append("IPX SubscriptionStatusMessage: " + subscriptionStatusMessage + crlf);
            sbf.append("IPX SubscriptionId has been terminated: " + subscriptionId + crlf);
            try {
                SdcMobileClubUser clubUser = clubuserdao.getClubUserBySubscriptionId(subscriptionId);

                if (clubUser != null) {
                    sbf.append("IPX clubUser Unique " + clubUser.getUnique() + crlf);
                    sbf.append("IPX clubUser UserUnique " + clubUser.getUserUnique() + crlf);
                    sbf.append("IPX clubUser ClubUnique: " + clubUser.getClubUnique() + crlf);
                    sbf.append("IPX clubUser msisdn: " + clubUser.getParsedMobile() + crlf);

                    //unsubscribe(MobileClub club, SdcUser user, String msisdn)
                    //MobileClub club = MobileClubDao.getMobileClub(clubUser.getClubUnique());
                    MobileClub club = clubdao.getMobileClubMap().get(clubUser.getClubUnique());
                    boolean shouldStop = checkstop.checkStopCount("IT");
                    try {
                        if (club != null) {
                            if (shouldStop) {
                                clubdao.unsubscribe(club, null, clubUser.getParsedMobile());
                                cacheManager.delete(clubUser.getUserUnique());
                                userauthentication.invalidateUser(request);
                                sbf.append("IPX sms domainTest: " + club.getWapDomain() + crlf);
                                String zastoplog="STOP";
                                clubUser.setUnsubscribed(new Date());
                                if(DateUtils.isSameDay(clubUser.getSubscribed(),clubUser.getUnsubscribed())) zastoplog="STOPFD";
                                campaigndao.log("IPXReq", "", clubUser.getUserUnique(), clubUser.getParsedMobile(), null, club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, request, response, clubUser.getNetworkCode());

                                if (clubUser.getCampaign() != null && clubUser.getCampaign().trim().length() > 0) {
                                    cmpgn = UmeTempCmsCache.campaignMap.get(clubUser.getCampaign());
                                }

                                if (cmpgn != null && cmpgn.getSrc().endsWith("RS")) {
                                    String revshareloggingquery = "insert into revShareLogging "
                                            + "(aUnique,aAmount,aCurrencyCode,aParsedMobile,aEncryptedMSISDN,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc,aEvent) values" + "('" + Misc.generateUniqueId() + "','0','" + cmpgn.getPayoutCurrency() + "','" + clubUser.getParsedMobile() + "','" + MiscCr.encrypt(clubUser.getParsedMobile()) + "','" + clubUser.getCampaign() + "','" + club.getUnique() + "','" + sdf2.format(new Date()) + "','" + sdf2.format(new Date()) + "','0','" + clubUser.getNetworkCode() + "','" + cmpgn.getSrc() + "','2')";
                                    int updateRow = zacpa.executeUpdateCPA(revshareloggingquery);
                                }
                            } else {
                                String revshareloggingquery = "insert into daemon_monitoring "
                                        + "(code,description,region,datetimestamp,event,error,type) values "
                                        + "('Pareva Core','ParevaCore','IT','" + MiscDate.sqlDate.format(new Date()) + "','MORE THAN 500 USERS HAVE UNSUBSCRIBED TODAY','1','core')";
                                int updateRow = zacpa.executeUpdateCPA(revshareloggingquery);

                            }
                        }
                    } catch (Exception campaignException) {
                        sbf.append("IPX subscription Termination campaignLog exception: " + MiscDate.sqlDate.format(new Date()) + " - " + campaignException + crlf);
                    }
                }
            } catch (Exception e) {
                sbf.append("IPX subscription Termination exception: " + MiscDate.sqlDate.format(new Date()) + " - " + e + crlf);
                //sbf.append("IPX subscription Termination exception: " + e + crlf);
            }

            FileUtil.writeRawToFile(debugPath + debugFile, sbf.toString() + crlf, true);
        }else if (subscriptionStatus.equals("1")){
            
        }
    } catch (Exception e) {
        out.write("<DeliveryResponse ack=\"false\"/>");
        System.out.println("IPX SUBSCRIPTION NOTIFICATION EXCEPTION: " + e);
        sbf.append("*************IPX SUBSCRIPTION NOTIFICATION EXCEPTION: " + e);
    }
    sbf.append("IPX ---------- SUBSCRIPTION notification----------- Ending " + fileName);
//FileUtil.writeRawToFile(debugPath + debugFile, sbf.toString() + crlf, true);    

    try {

        File file = new File(debugPath + debugFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(sbf.toString() + crlf);
        bw.close();
    } catch (IOException eeeee) {
        System.out.println("IPX NOTIFICATION WRITE FILE EXCEPTION: " + eeeee);
        eeeee.printStackTrace();
    }

    if (subscriptionId.equals("") && SessionId.equals("")) {
        out.write("<DeliveryResponse ack=\"false\"/>");
    } else {
        out.write("<DeliveryResponse ack=\"true\"/>");
    }
%>