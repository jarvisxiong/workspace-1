<%@page import="ume.pareva.it.DigitAPI"%>
<%@page import="ume.pareva.snp.CacheManager"%>
<%@page import="ume.pareva.it.IpxBillingDirect"%>
<%@include file="coreimport.jsp" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
//***************************************************************************************************
    SdcRequest aReq = new SdcRequest(request);
    aReq.setPageEnc("iso-8859-1");
    String fileName = "index_ipx_charge";
//***************************************************************************************************
    StringBuilder sbf = new StringBuilder();
    String crlf = "\r\n";
    String debugFile = "IPX_notify_top_debug_" + MiscDate.sqlDate.format(new Date()) + ".txt";
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
    DigitAPI firstsms = null;

    try {
        ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
        ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request, servletContext);
        firstsms = (DigitAPI) ac.getBean("digitapiit");
    } catch (Exception e) {
        e.printStackTrace();
    }


    if (!msisdn.equals("")) {
        System.out.println("IPX msisdn " + msisdn);
        System.out.println("IPX club " + clubUnique);

        String msg = "La tua richiesta e' stata approvata. Grazie";
        String fromNumber = "Conferma";
        firstsms.setMsg(msg);
        firstsms.setMsisdn(msisdn);
        firstsms.setFrom(fromNumber); //11 alphanumeric characters OR upto 16 digit phone number
        firstsms.setReport("True");
        firstsms.setNetwork("UNKNOWN");

        try {
            firstsms.sendSMS();
            out.write("successful");
        } catch (Exception e) {
            out.write("failed: " + e);
        }   
    }else{
        out.write("failed");
    }
%>
