<%@page import="ume.pareva.smsapi.IpxSmsConnection"%>
<%@page import="ume.pareva.smsapi.IpxSmsSubmit"%>
<%@include file="commonfunc.jsp"%>
<%@include file = "CookieManager.jsp"%>
<%
//    System.out.println("smsoptin debug called upon");
    SdcRequest aReqSdc = new SdcRequest(request);
    String resp = "";
    /* TODO output your page here. You may use following sample code. */
    String mobileno = "";
    String operator = aReq.get("operator");
    UmeClubDetails clubdetails = null;

    try {
        mobileno = Misc.parseMsisdn((String) request.getParameter("submsisdn"));
        System.out.println("Msisdn: " + mobileno.toString());
    } catch (Exception e) {
        mobileno = "";
    }

    mobileno = parseMsisdn(mobileno);
    boolean validateMobileNo = checkMsisdn(mobileno);
//    System.out.println("The value of validMobileNo is "+validateMobileNo);
//    System.out.println("The value of operator is "+operator);
//    System.out.println("Msisdn after processing: " + mobileno.toString());
    
    if(operator.equals(""))
        operator = "vodafone";

    SdcMobileClubUser clubuser = null;
    UmeMobileClubUserDao umemobileclubuserdao = null;
    SmsDoiLogDao smsdoilogdao = null;

    try {
        ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
        ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request, servletContext);
        umemobileclubuserdao = (UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
        umeuserdao = (UmeUserDao) ac.getBean("umeuserdao");
        mobileclubdao = (MobileClubDao) ac.getBean("mobileclubdao");
        smsdoilogdao = (SmsDoiLogDao) ac.getBean("smsdoilogdao");
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    String landingPage=(String)session.getAttribute("landingPage");
    String campaignIdSession = (String)session.getAttribute("campaignid");
    if(landingPage==null || landingPage.isEmpty())
        landingPage = "landing_day";
    if(campaignIdSession==null)
        campaignIdSession = "";
    
    try{
        campaigndao.log("confirm", landingPage, mobileno,mobileno,null,domain, campaignIdSession, club.getUnique(), "MAN_BFV", 1, request,response,operator.toLowerCase().trim());
    }catch(Exception e){
        e.printStackTrace();
    }
    if (validateMobileNo) {
        //System.out.println("smsoptin testing domain "+domain+" defaulturl "+dmn.getDefaultUrl());


        if (club != null) {
            clubUnique = club.getUnique();
        }

        if (clubUnique != null) {
            clubuser = umemobileclubuserdao.getClubUserByMsisdn(mobileno, clubUnique);
            clubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
            if (clubdetails == null) {
                System.out.println("CLUB DETAILS IS NULL");
            }
            //     System.out.println("ZA msisdn test:"+" msisdn Handler  msisdn debugging "+mobileno+" club "+clubUnique+" "+clubdetails.toString());
        }
        if (clubuser != null) {
            //user = umeuserdao.getUser(clubuser.getUserUnique());
            user = umeuserdao.getUser(clubuser.getParsedMobile());
        }

        boolean isActive = false;
        String wapid = "";
        if (user != null) {
            isActive = mobileclubdao.isActive(user, club);
            wapid = user.getWapId();

            if (isActive) {
                response.sendRedirect("http://" + dmn.getDefaultUrl() + "/?id=" + wapid);
                return;
            }
        }

        if (!isActive) {
            System.out.println("Trying to send smsoptin now ");        
            String clubname = club.getClubName();
            String serviceid = club.getOtpServiceName();
            String networkCode = "";
            try {
                IpxSmsSubmit  sms = new IpxSmsSubmit(aReq);
                sms.setSmsAccount("ipx");
                sms.setToNumber(mobileno);
                sms.setCurrencyCode("EUR");

                sms.setUsername(club.getOtpSoneraId());
                sms.setPassword(club.getOtpTelefiId());

                String messageBody = "Il tuo codice di attivazione WEB e #IPX_PASSCODE# .Per accedere ADESSO clicca su: http://" + dmn.getName() +"/o.jsp?" + "o=#IPX_PASSCODE#";
                String messageBodyNormal = "Il tuo codice di attivazione WEB e #IPX_PASSCODE# .Per accedere ADESSO clicca su: http://" + dmn.getName() +"/o.jsp";
                String messageWindBody = "Il tuo codice di attivazione WEB e #SUB_PASSCODE# .Per accedere ADESSO clicca su: http://" + dmn.getName() +"/o.jsp?" + "o=#SUB_PASSCODE#";
                String messageWindBodyNormal = "Il tuo codice di attivazione WEB e #SUB_PASSCODE# .Per accedere ADESSO clicca su: http://" + dmn.getName() +"/o.jsp";
 
//                if(trickyTime)
//                    sms.setMsgBody(messageBody);
//                else
                    sms.setMsgBody(messageBody);

                if(operator.equals("vodafone")){
                    sms.setFromNumber("4884884");
                    sms.setMsgBody("#SUB_PASSCODE#");
                }
                else if(operator.equals("wind")){
                    sms.setFromNumber("4884884");
//                    if(trickyTime)
//                        sms.setMsgBody(messageWindBody);
//                    else
//                        sms.setMsgBody(messageWindBodyNormal);
                        sms.setMsgBody(messageWindBody);

                }else{//TIM operator
                    sms.setFromNumber("3399942323");
                    sms.setServiceCategory("DIRECTORY_SERVICE");
                    sms.setReferenceID("#NULL#");
                }

                //Thread.sleep(1000);  
                IpxSmsConnection smsConnection = new IpxSmsConnection();
                resp = smsConnection.doRequest(sms);
                
                if(resp.contains("successful--")){

                    campaigndao.log("confirm", landingPage, mobileno,mobileno,null,domain, campaignIdSession, club.getUnique(), "MANUAL", 1, request,response,networkCode.toLowerCase().trim());
                    
                    session.setAttribute("ipx_msisdn", mobileno);               
                    session.setAttribute("ipx_operator", operator);
                    session.setAttribute("ipx_messageid", resp.replace("successful--", ""));

                    CookieManager cm = new CookieManager(request, response);
                    int timeSavedCookies = 7*24*3600;
                    cm.add("ipx_msisdn", mobileno, timeSavedCookies);
                    cm.add("ipx_operator", operator, timeSavedCookies);
                    cm.add("ipx_messageid", resp.replace("successful--", ""), timeSavedCookies);
                    cm.add("campaignid", (String)session.getAttribute("campaignid"), timeSavedCookies);

//                    System.out.println("Hello Trung ipx_msisdn: " + cm.get("ipx_msisdn"));
//                    System.out.println("Hello Trung ipx_operator: " + cm.get("ipx_operator"));
//                    System.out.println("Hello Trung ipx_messageid: " + cm.get("ipx_messageid"));
                }
                //String resp = billingConnection.doRequest(sms);
                //resp = SmsExtDao.send((SdcSmsSubmit)sms);
                //System.out.println("IPX resp: " + resp  + " - " + new Date());
            } catch (Exception e) {
                System.out.println("Exception SMSOPTIN for msisdn " + mobileno + " club" + clubname + ": " + e);
                e.printStackTrace();
            }
            
        }
    }else{
        resp = "failed--is not validate msisdn or operator choose is not correct";
    }
    
    System.out.println("sms optin resp " + resp);

    out.write(resp);
%>

<%!
    boolean checkMsisdn(String mobileno) {
        boolean validmsisdn = (mobileno != null && !mobileno.trim().equals("") && SdcMisc.validateTel(mobileno) && mobileno.startsWith("39") && (mobileno.length()>=8));
        return validmsisdn;
    }
    String parseMsisdn(String mobileno){
        if (mobileno != null && mobileno.trim().length() > 0) {
        if (mobileno.contains("+")) {
            mobileno = mobileno.replace("+", "").trim();
        }
        }
        if (mobileno != null && mobileno.startsWith("0")) {
            mobileno = "39" + mobileno.substring(1);
        }
        if(mobileno != null && mobileno.startsWith("3") && !mobileno.startsWith("39")) mobileno = "39"+mobileno;
        return mobileno;
    }

%>