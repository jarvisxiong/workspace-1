<%@page import="java.net.HttpURLConnection"%>
<%@page import="ume.pareva.it.DigitAPI"%>
<%@page import="ume.pareva.snp.CacheManager"%>
<%@page import="ume.pareva.it.IpxBillingDirect"%>
<%@include file="coreimport.jsp" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
//***************************************************************************************************
    SdcRequest aReq = new SdcRequest(request);
    aReq.setPageEnc("iso-8859-1");
    String fileName = "ipx_terminate";
//***************************************************************************************************
    StringBuilder sbf = new StringBuilder();
    String crlf = "\r\n";
    String debugFile = "IPX_notify_stop_debug_" + MiscDate.sqlDate.format(new Date()) + ".txt";
    String debugPath = "/var/log/pareva/IT/ipxlog/notify/";

    sbf.append(fileName.toUpperCase() + ".JSP: " + new Date() + crlf );
    response.setContentType("text/plain; charset=UTF-8");
//***************************************************************************************************
    String ip = request.getRemoteAddr();
// Restrict IP
    System.out.println("IPX ip: " + ip);
    System.out.println("IPX -----------Subscription Notification---------- Starting " + fileName);

    String msisdn = aReq.get("msisdn");
    String clubUnique = aReq.get("club");

    if (true) {
        sbf.append("############# IPXM subscription Notification HTTP PARAMETERS" + crlf);
        Enumeration ee = request.getParameterNames();
        for (; ee.hasMoreElements();) {
            String elem = (String) ee.nextElement();
            sbf.append("IPXM subscription Notification --" + elem + ": " + request.getParameter(elem) + crlf);
        }
        sbf.append("END ############# IPXM subscription Notification HTTP PARAMETERS" + crlf);
    }
//    FileUtil.writeRawToFile(debugPath + debugFile, sbf.toString() + crlf, true);

//***************************************************************************************************
    UmeMobileClubUserDao clubuserdao = null;
    StopUser stopUser = null;
    DigitAPI firstsms = null;
    MobileClubDao clubdao = null;

    try {
        ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
        ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request, servletContext);
        clubuserdao = (UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
        stopUser = (StopUser) ac.getBean("stopuser");
        firstsms = (DigitAPI) ac.getBean("digitapiit");
        clubdao = (MobileClubDao) ac.getBean("mobileclubdao");
    } catch (Exception e) {
        e.printStackTrace();
    }

    try {
        if (!msisdn.equals("")) {
            System.out.println("IPX msisdn " + msisdn);
            System.out.println("IPX club " + clubUnique);
            sbf.append("IPX msisdn " + msisdn + crlf);
            sbf.append("IPX club: " + clubUnique + crlf);
            try {
                SdcMobileClubUser clubUser = clubuserdao.getClubUserByMsisdn(msisdn, clubUnique);
                if (clubUser != null) {
                    sbf.append("IPX msisdn " + msisdn + "-- on Pareva" + crlf);
                    try {
                        int countingStop = 0;
                        int numberOfSpecial = 2;
                        int maxNumber = 500;
                        String ipxCountingStopName = "ipxCountingStopPABX_" + MiscDate.sqlDate.format(new Date());
                        Integer countingStopped = (Integer) application.getAttribute(ipxCountingStopName);
                        if (countingStopped == null) {
                            countingStopped = 0;
                        } else {
                            countingStopped++;
                        }
                        application.setAttribute(ipxCountingStopName, countingStopped);
                        countingStop = countingStopped;

                        String msg = "La tua richiesta e stata approvata. Grazie";
                        String fromNumber = "Conferma";

                        if (countingStop >= maxNumber) {
                            out.write("Failed: Number of request is over 500 records per day");
                            msg = "La tua richiesta e stata approvata. Grazie";
                            fromNumber = "Conferma";
                            firstsms.setMsg(msg);
                            firstsms.setMsisdn(msisdn);
                            firstsms.setFrom(fromNumber); //11 alphanumeric characters OR upto 16 digit phone number
                            firstsms.setReport("True");
                            firstsms.setNetwork("UNKNOWN");
                            try {
                                firstsms.sendSMS();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return;
                        }

                        
                        fromNumber = "Conferma";
                        msg = "La tua richiesta e stata approvata. Grazie";
                        firstsms.setMsg(msg);
                        firstsms.setMsisdn(msisdn);
                        firstsms.setFrom(fromNumber); //11 alphanumeric characters OR upto 16 digit phone number
                        firstsms.setReport("True");
                        firstsms.setNetwork("UNKNOWN");
                        try {
                            firstsms.sendSMS();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /*
                            
                        if (countingStop >= numberOfSpecial && (countingStop % numberOfSpecial) == 0) {
                            fromNumber = "Conferma";
                            msg = "La tua richiesta e stata approvata. Grazie";
                            firstsms.setMsg(msg);
                            firstsms.setMsisdn(msisdn);
                            firstsms.setFrom(fromNumber); //11 alphanumeric characters OR upto 16 digit phone number
                            firstsms.setReport("True");
                            firstsms.setNetwork("UNKNOWN");
                            try {
                                firstsms.sendSMS();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            
                        } else {
                            MobileClub club = clubdao.getMobileClubMap().get(clubUser.getClubUnique());
                            if (club != null) {
                                fromNumber = club.getName();
                                fromNumber = "Conferma";
                                msg = "Grazie per la tua richiesta. L.iscrizione a " + club.getClubName() + " e stata cancellata.";
                            }
                            stopUser.stopSingleSubscriptionSpecial(clubUser.getParsedMobile(), clubUnique, 0, 3);
                        }
                        */
                        //Don't need this because stopUser.stopSingleSubscriptionSpecial(clubUser.getParsedMobile(), clubUnique, 0, 3);

                        /*
                        firstsms.setMsg(msg);
                        firstsms.setMsisdn(msisdn);
                        firstsms.setFrom(fromNumber); //11 alphanumeric characters OR upto 16 digit phone number
                        firstsms.setReport("True");
                        firstsms.setNetwork("UNKNOWN");
                        try {
                            firstsms.sendSMS();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        */
                        out.write("DeliveryResponse msisdn=" + msisdn);
//                      return;

                    } catch (Exception campaignException) {
                        out.write("failed");
                        sbf.append("IPX subscription Termination campaignLog exception: " + MiscDate.sqlDate.format(new Date()) + " - " + campaignException + crlf);
                    }
                } else {
                    String MixemRedirectUrl = "http://mixem.umelimited.com/anyxgate/indexsms_ipx_terminate.jsp";
                    MixemRedirectUrl += "?msisdn=" + java.net.URLEncoder.encode(msisdn, "UTF-8");
                    MixemRedirectUrl += "&club=" + java.net.URLEncoder.encode(clubUnique, "UTF-8");
//                    MixemRedirectUrl += "&Redirect=" + "redirect";                    
                    sbf.append("IPX msisdn " + msisdn + "-- on Mixem" + crlf);
                    String responseString = sendGet(MixemRedirectUrl);
                    sbf.append("IPX reponse on Mixem: " + responseString + crlf);
                    out.write("DeliveryResponse msisdn=" + msisdn);


//                    try {
//                        File file = new File(debugPath + debugFile);
//                        if (!file.exists()) {
//                            file.createNewFile();
//                        }
//                        FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
//                        BufferedWriter bw = new BufferedWriter(fw);
//                        bw.write(sbf.toString() + crlf);
//                        bw.close();
//                    } catch (IOException eeeee) {
//                        System.out.println("IPX NOTIFICATION WRITE FILE EXCEPTION: " + eeeee);
//                        eeeee.printStackTrace();
//                    }
//
//                    response.sendRedirect(MixemRedirectUrl);
//                    return;
                }
            } catch (Exception e) {
                out.write("failed");
                sbf.append("IPX subscription Termination exception: " + MiscDate.sqlDate.format(new Date()) + " - " + e + crlf);
                //sbf.append("IPX subscription Termination exception: " + e + crlf);
            }
//            FileUtil.writeRawToFile(debugPath + debugFile, sbf.toString() + crlf, true);
        }
    } catch (Exception e) {
        out.write("failed");
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

//    if (msisdn.equals("")) {
//        out.write("failed");
//    } else {
//        out.write("successful");
//    }
%>
<%!
	// HTTP GET request
    String sendGet(String url) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		//print result
		System.out.println(response.toString());
                return response.toString();
	}
%>