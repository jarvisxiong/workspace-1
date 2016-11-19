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
    aReq.setPageEnc("iso-8859-1");
    String fileName = "index_ipx_charge";
//***************************************************************************************************

    StringBuilder sbf = new StringBuilder();
    String crlf = "\r\n";
    String debugFile = "IPX_notify_top_debug_" + MiscDate.sqlDate.format(new Date()) + ".txt";
    String debugPath = "/var/log/pareva/IT/ipxlog/notify/";

    sbf.append(fileName.toUpperCase() + ".JSP: " + new Date() + crlf + crlf);
    response.setContentType("text/plain; charset=UTF-8");

//***************************************************************************************************
    String ip = request.getRemoteAddr();
// Restrict IP
    System.out.println("IPX ip: " + ip);
    System.out.println("IPX -----------Subscription Notification---------- Starting " + fileName);

    String msisdn = aReq.get("msisdn");
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
    StopUser stopUser = null;

    try {
        ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
        ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request, servletContext);
        clubuserdao = (UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
        stopUser = (StopUser) ac.getBean("stopuser");
    } catch (Exception e) {
        e.printStackTrace();
    }

//String ipx_reponse = "";
    try {
        if (!msisdn.equals("")) {
            System.out.println("IPX msisdn " + msisdn);
            System.out.println("IPX club " + clubUnique);
            sbf.append("IPX msisdn " + msisdn + crlf);
            sbf.append("IPX club: " + clubUnique + crlf);
            try {
                SdcMobileClubUser clubUser = clubuserdao.getClubUserByMsisdn(msisdn, clubUnique);

                if (clubUser != null) {
                    sbf.append("IPX clubUser Unique " + clubUser.getUnique() + crlf);
                    sbf.append("IPX clubUser UserUnique " + clubUser.getUserUnique() + crlf);
                    sbf.append("IPX clubUser ClubUnique: " + clubUser.getClubUnique() + crlf);
                    sbf.append("IPX clubUser msisdn: " + clubUser.getParsedMobile() + crlf);
//                    MobileClub club = clubdao.getMobileClub(clubUser.getClubUnique());
                    try {
                        stopUser.stopSingleSubscriptionNormal(clubUser.getParsedMobile(), clubUnique, request, response);                        
//                        if (club != null) {
//                            clubdao.unsubscribe(club, null, clubUser.getParsedMobile());
//                            cacheManager.delete(clubUser.getUserUnique());
//                            userauthentication.invalidateUser(request);
//                            campaigndao.log("IPXReq", "", clubUser.getUserUnique(), clubUser.getParsedMobile(), null, club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), "STOP", 0, request, response, clubUser.getNetworkCode());                              
//                            if(clubUser.getCampaign()!=null && clubUser.getCampaign().trim().length()>0)
//                            cmpgn = UmeTempCmsCache.campaignMap.get(clubUser.getCampaign());
//                            
//                            if(cmpgn!=null && cmpgn.getSrc().endsWith("RS")) {
//                            String revshareloggingquery="insert into revShareLogging "
//                              + "(aUnique,aAmount,aCurrencyCode,aParsedMobile,aEncryptedMSISDN,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc,aEvent) values" +"('"+Misc.generateUniqueId()+"','0','"+cmpgn.getPayoutCurrency()+"','"+clubUser.getParsedMobile()+"','"+MiscCr.encrypt(clubUser.getParsedMobile())+"','"+clubUser.getCampaign()+"','"+club.getUnique()+"','"+sdf2.format(new Date())+"','"+sdf2.format(new Date())+"','0','"+clubUser.getNetworkCode()+"','"+cmpgn.getSrc()+"','2')";
//                                int updateRow=zacpa.executeUpdateCPA(revshareloggingquery);
//                            }
//                        }
                    } catch (Exception campaignException) {
                        sbf.append("IPX subscription Termination campaignLog exception: " + MiscDate.sqlDate.format(new Date()) + " - " + campaignException + crlf);
                    }
                }else{
                    String MixemRedirectUrl = "http://mixem.umelimited.com/anyxgate/indexsms_ipx_terminate.jsp";
                    MixemRedirectUrl += "?msisdn=" + java.net.URLEncoder.encode(msisdn, "UTF-8");
                    MixemRedirectUrl += "&club=" + java.net.URLEncoder.encode(clubUnique, "UTF-8");             
                    MixemRedirectUrl += "&Redirect=" + "redirect";
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

                    response.sendRedirect(MixemRedirectUrl);
                    return;       
                }
            } catch (Exception e) {
                sbf.append("IPX subscription Termination exception: " + MiscDate.sqlDate.format(new Date()) + " - " + e + crlf);
                //sbf.append("IPX subscription Termination exception: " + e + crlf);
            }

            FileUtil.writeRawToFile(debugPath + debugFile, sbf.toString() + crlf, true);
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
    
if(msisdn.equals("")){
    out.write("failed");
}else{
    out.write("successful");
}
%>