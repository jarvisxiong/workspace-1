<%@page import="org.hibernate.type.StandardBasicTypes"%>
<%@page import="ume.pareva.util.ZACPA"%>
<%@page import="ume.pareva.smsapi.IpxBillingSubmit"%>
<%@page import="org.hibernate.Hibernate"%>
<%@page import="java.util.concurrent.Executors"%>
<%@page import="java.util.concurrent.ExecutorService"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="ume.pareva.it.IpxBillingDirect"%>
<%@include file="coreimport.jsp" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
//***************************************************************************************************
    SdcRequest aReq = new SdcRequest(request);

//UmeSessionParameters httprequest= new UmeSessionParameters(request);
//UmeUser user = httprequest.getUser();
//UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
//SdcService service = (SdcService) request.getAttribute("umeservice");
    Calendar nowTime = GregorianCalendar.getInstance();
    nowTime.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
    Date currentDate = nowTime.getTime();
// important:
    aReq.setPageEnc("iso-8859-1");
    String fileName = "index_ipx_charge";
//***************************************************************************************************

    StringBuilder sbf = new StringBuilder();
    String crlf = "\r\n";
    String debugFile = "IPX_notify_charged_debug_" + MiscDate.sqlDate.format(new Date()) + ".txt";
    String debugPath = "/var/log/pareva/IT/ipxlog/notify/";

    sbf.append(fileName.toUpperCase() + ".JSP: " + new Date() + crlf + crlf);
    response.setContentType("text/plain; charset=UTF-8");

//***************************************************************************************************
    String type = aReq.get("Type");
    String tariffClass = aReq.get("TariffClass");
    String consumerId = aReq.get("ConsumerId");
    String operator = aReq.get("Operator");
    String timeStamp = aReq.get("TimeStamp");
    String statusCode = aReq.get("StatusCode");
    String statusText = aReq.get("StatusText");
    String referenceId = aReq.get("ReferenceId");
    String subscriptionId = aReq.get("SubscriptionId");
    String reasonCode = aReq.get("ReasonCode");
    String reasonText = aReq.get("ReasonText");

    String clubUnique = aReq.get("club");

    sbf.append("IPX --------Charge notification ------------- Starting " + fileName + crlf);
    sbf.append("IPX cLength: " + request.getContentLength() + crlf);
    sbf.append("IPX type: " + type + crlf);
    sbf.append("IPX tariffClass: " + tariffClass + crlf);
    sbf.append("IPX consumerId: " + consumerId + crlf);
    sbf.append("IPX operator: " + operator + crlf);
    sbf.append("IPX timeStamp: " + timeStamp + crlf);
    sbf.append("IPX statusCode: " + statusCode + crlf);
    sbf.append("IPX statusText: " + statusText + crlf);
    sbf.append("IPX referenceId: " + referenceId + crlf);
    sbf.append("IPX subscriptionId: " + subscriptionId + crlf);
    sbf.append("IPX reasonCode: " + reasonCode + crlf);
    sbf.append("IPX reasonText: " + reasonText + crlf);

//***************************************************************************************************
    UmeMobileClubUserDao clubuserdao = null;
    MobileClubCampaignDao campaigndao = null;
    MobileClubBillingPlanDao billingplandao = null;
//ZACPA zacpa=null;
    MobileClubCampaign cmpgn = null;
    RevShareLoggingDao revshareloggingdao = null;
    MobileClubDao mobileclubdao = null;
    ApplicationContext ac = null;
    SessionFactory sessionfactory = null;
    Session dbsession = null;
    Transaction dbtransaction = null;
    try {
        ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
        ac = RequestContextUtils.getWebApplicationContext(request, servletContext);
        clubuserdao = (UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
        campaigndao = (MobileClubCampaignDao) ac.getBean("mobileclubcampaigndao");
        billingplandao = (MobileClubBillingPlanDao) ac.getBean("billingplandao");
        revshareloggingdao = (RevShareLoggingDao) ac.getBean("revshareloggingdao");
        mobileclubdao = (MobileClubDao) ac.getBean("mobileclubdao");
        sessionfactory = (SessionFactory) ac.getBean("sessionFactory");
        dbsession = sessionfactory.openSession();
    } catch (Exception e) {
        e.printStackTrace();
    }

//String ipx_reponse = "";
    try {
        if (type.equals("CHARGE") && !subscriptionId.equals("")) {
            //if(!subscriptionId.equals("")){   
            //String sqlstr = "";

            out.write("<DeliveryResponse ack=\"true\"/>");

            String logUnique = "";
            String billCreated = "1970-01-01 00:00:00";
            SdcMobileClubUser clubUser = null;
            MobileClubBillingPlan billingPlan = null;
            if (consumerId.equals("")) {
                clubUser = clubuserdao.getClubUserBySubscriptionId(subscriptionId);
            } else {
                consumerId.replace("'", "");
                clubUser = clubuserdao.getClubUserBySubscriptionId(subscriptionId, consumerId);
            }

            //IF CLUBUSER IS ON PAREVA DOING BILLING LOGIC, ELSE REDIRECT TO MIXEM PLATFORM.
            if (clubUser != null) {
                dbtransaction = dbsession.beginTransaction();
                Query query = null;

                try {
                    String checktidQuery = "select * from itNotificationLog where aUnique='" + referenceId + "'";
                    query = dbsession.createSQLQuery(checktidQuery).addScalar("aUnique",  StandardBasicTypes.STRING);

                    java.util.List result = query.list();

                    if (result != null && result.size() > 0) {
                        sbf.append("IPX itNotificationLog existing: " + referenceId + "--" + consumerId);
                    } else {

                        String addNotificationLogQuery = " INSERT INTO itNotificationLog"
                                + " (aUnique)"
                                + " VALUES('" + referenceId + "')";
                        try {
//                            System.out.println("iminotification_debug uknotificationQuery " + addNotificationLogQuery);
                            sbf.append("IPX iminotification_debug uknotificationQuery " + addNotificationLogQuery);
                            query = dbsession.createSQLQuery(addNotificationLogQuery);
                            query.executeUpdate();
                        } catch (Exception e) {
                            System.out.println("iminotification_debug " + " Exception " + e);
                            e.printStackTrace();
                        }

                        ///////////////
                        try {
                            MobileClubBillingTry item = new MobileClubBillingTry();
                            item.setUnique(Misc.generateUniqueId());
                            item.setLogUnique(logUnique);
                            item.setAggregator("ipx");
                            item.setStatus(statusText);
                            item.setTransactionId(subscriptionId);
                            item.setResponseRef(referenceId);
                            item.setResponseCode(reasonCode);
                            item.setResponseDesc(reasonText);
                            item.setCreated(currentDate);
                            item.setRegionCode("IT");
                            item.setNetworkCode(clubUser.getNetworkCode());
                            item.setParsedMsisdn(clubUser.getParsedMobile());
                            item.setTariffClass(250);
                            item.setBillingType("club");
                            item.setClubUnique(clubUser.getClubUnique());
                            item.setCampaign(clubUser.getCampaign());
                            item.setTicketCreated(SdcMiscDate.parseSqlDateString(billCreated));
                            
                            processBillingPlan(ac, statusCode, subscriptionId, referenceId, clubUser, item);
                            
                        } catch (Exception eeee) {
                            System.out.println("IPX oooo notify charged: " + eeee);
                        }
                    }

                } catch (Exception ee) {
                    sbf.append("IPX itNotificationLog checking exception charged: " + ee);
                    System.out.println("IPX itNotificationLog checking exception charged: " + ee);
                }

                ///////////////
            } else {
                String MixemRedirectUrl = "http://mixem.umelimited.com/anyxgate/indexsms_ipx_charge.jsp";
                MixemRedirectUrl += "?Type=" + type;
                MixemRedirectUrl += "&TariffClass=" + java.net.URLEncoder.encode(tariffClass, "UTF-8");
                MixemRedirectUrl += "&ConsumerId=" + java.net.URLEncoder.encode(consumerId, "UTF-8");
                MixemRedirectUrl += "&Operator=" + java.net.URLEncoder.encode(operator, "UTF-8");
                MixemRedirectUrl += "&TimeStamp=" + java.net.URLEncoder.encode(timeStamp, "UTF-8");
                MixemRedirectUrl += "&StatusCode=" + java.net.URLEncoder.encode(statusCode, "UTF-8");
                MixemRedirectUrl += "&StatusText=" + java.net.URLEncoder.encode(statusText, "UTF-8");
                MixemRedirectUrl += "&ReferenceId=" + java.net.URLEncoder.encode(referenceId, "UTF-8");
                MixemRedirectUrl += "&SubscriptionId=" + java.net.URLEncoder.encode(subscriptionId, "UTF-8");
                MixemRedirectUrl += "&ReasonCode=" + java.net.URLEncoder.encode(reasonCode, "UTF-8");
                MixemRedirectUrl += "&ReasonText=" + java.net.URLEncoder.encode(reasonText, "UTF-8");
                MixemRedirectUrl += "&Redirect=" + "redirect";
                final String finalrequesturl = MixemRedirectUrl;
//                System.out.println(finalrequesturl);

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                    public void run() {
                        try {
                            URL digitrequesturl = new URL(finalrequesturl);
                            HttpURLConnection digitsmsRequest = (HttpURLConnection) digitrequesturl
                                    .openConnection();
                            // digitsmsRequest.setDoOutput(true);
                            digitsmsRequest.setRequestMethod("GET");
                            digitsmsRequest.setReadTimeout(15 * 1000);
                            int responseCode = digitsmsRequest.getResponseCode();
//                            System.out.println("IPX Response Code : " + responseCode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                executorService.shutdown();
            }
        }
    } catch (Exception e) {
        out.write("<DeliveryResponse ack=\"false\"/>");
        sbf.append("<DeliveryResponse ack=\"false\"/>" + crlf);
        System.out.println("IPX CHARGED NOTIFICATION EXCEPTION: " + e);
        sbf.append("*************IPX Charged NOTIFICATION EXCEPTION: " + e);
    }
    sbf.append("IPX ---------- Charge notification----------- Ending " + fileName);
//FileUtil.writeRawToFile(debugPath + debugFile, sbf.toString() + crlf, true);    
    if (subscriptionId.equals("")) {
        out.write("<DeliveryResponse ack=\"false\"/>");
        sbf.append("<DeliveryResponse ack=\"false\"/>" + crlf);
    }
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
    if (dbtransaction != null) {
        dbtransaction.commit();
    }
    if (dbsession != null) {
        dbsession.close();
    }
%>
<%!
    void processBillingPlan(final ApplicationContext ac, final String statusCode, final String subscriptionId, final String referenceId, final SdcMobileClubUser clubUser, final MobileClubBillingTry btry) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            public void run() {
                try {
                    Calendar nowTime = GregorianCalendar.getInstance();
                    nowTime.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
                    Date currentDate = nowTime.getTime();

                    UmeMobileClubUserDao clubuserdao = null;
                    MobileClubCampaignDao campaigndao = null;
                    MobileClubBillingPlanDao billingplandao = null;
                    MobileClubCampaign cmpgn = null;
                    RevShareLoggingDao revshareloggingdao = null;
                    MobileClubDao mobileclubdao = null;
                    ZACPA zalog = null;
                    IpxBillingDirect billingDirect = null;
                    MobileBillingDao mobileclubbillingsuccessdao = null;

                    try {
                        clubuserdao = (UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
                        campaigndao = (MobileClubCampaignDao) ac.getBean("mobileclubcampaigndao");
                        billingplandao = (MobileClubBillingPlanDao) ac.getBean("billingplandao");
                        revshareloggingdao = (RevShareLoggingDao) ac.getBean("revshareloggingdao");
                        mobileclubdao = (MobileClubDao) ac.getBean("mobileclubdao");
                        zalog = (ZACPA) ac.getBean("zacpa");
                        billingDirect = (IpxBillingDirect) ac.getBean("ipxbillingdirect");
                        mobileclubbillingsuccessdao = (MobileBillingDao) ac.getBean("mobilebillingdao");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    MobileClubBillingPlan billingPlan = billingplandao.getActiveBillingPlanByMsisdnAndSubscriptionId(clubUser.getParsedMobile(), subscriptionId);
                    //TODO TODO TODOCHECK referenceId here here.
                    if (statusCode.equals("0")) {
                        if (billingPlan != null) {
                            if (billingPlan.getServiceDateBillsRemaining() > 0) {
                                
                                billingPlan.setLastSuccess(billingPlan.getLastPush());
                                billingPlan.setLastPaid(billingPlan.getLastPush());
                                billingPlan.setPartialsPaid(billingPlan.getPartialsPaid() + 1);
                                if (billingPlan.getServiceDateBillsRemaining() > 0) {
                                    billingPlan.setServiceDateBillsRemaining(billingPlan.getServiceDateBillsRemaining() - 1);
                                }
                                billingPlan.setNextPush(MiscDate.getNextHourZeroIT());
                                if(!billingPlan.getNetworkCode().equals("tim"))
                                    billingPlan.setNextPush(new Date());
                                
                                billingplandao.update(billingPlan);
//                                billingplandao.update(billingPlan);
                                clubuserdao.updateBillingRenew(clubUser.getUnique());

                                if(isInFirstBillPeriod(billingPlan.getSubscribed(), billingPlan.getPartialsPaid()))
                                    btry.setResponseCode("003");
                                
                                try {
                                    IpxBillingSubmit sms = new IpxBillingSubmit();
                                    sms.setSmsAccount("ipx_billing");
                                    sms.setServiceDesc(1);
                                    sms.setResponse("0"); 
                                    System.out.println("IPX NotifyBilling billingsuccessdao  ******");
                                    MobileClubBillingSuccesses success = new MobileClubBillingSuccesses(billingPlan, btry, sms.getSmsAccount(), sms.getServiceDesc(), sms.getResponse(), "S");
                                    System.out.println("IPX NotifyBilling billingsuccessdao  ******_:" + success.toString());                         
                                    mobileclubbillingsuccessdao.insertBillingSuccess(success);
                                    
                                } catch (Exception e) {
                                    System.out.println("IPX NotifyBilling billingsuccessdao exception: "
                                            + e + "--" + new Date());
                                    e.printStackTrace();
                                }

                                MobileClubCampaign cmpg = campaigndao.getCampaignMap().get(clubUser.getCampaign());

                                if (clubUser.getCampaign() != null) {
                                    cmpg = UmeTempCmsCache.campaignMap.get(clubUser.getCampaign());
                                    if (cmpg != null) {
                                        cmpg.setBillingCount(cmpg.getBillingCount() + 1);
                                        campaigndao.saveItem(cmpg);
                                    }
                                    if (cmpg != null && cmpg.getSrc().endsWith("RS")) {
                                        MobileClub club = mobileclubdao.getMobileClubMap().get(clubUser.getClubUnique());
                                        revshareloggingdao.addRevShareLogging(cmpg, "", clubUser.getParsedMobile(), MiscCr.encrypt(clubUser.getParsedMobile()), club, clubUser.getNetworkCode(), "IT", "1");
                                    }
                                    if (cmpg != null && cmpg.getSrc().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase(("billing"))) {
                                        SimpleDateFormat currentsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Calendar currentTime = Calendar.getInstance();
                                        currentTime.add(Calendar.MINUTE, 10);
                                        String nextpush = currentsdf.format(currentTime.getTime());
                                        String cpaloggingquery = "insert into cpaLogging (aParsedMobile,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc) values"
                                                + "('" + clubUser.getParsedMobile() + "','" + clubUser.getCampaign() + "','" + clubUser.getClubUnique() + "','" + currentsdf.format(new Date()) + "','" + nextpush + "','" + "0" + "','" + clubUser.getNetworkCode() + "','" + cmpg.getSrc() + "')";
                                        int insertedRows = zalog.executeUpdateCPA(cpaloggingquery);
                                    }

                                }
                            } else {
                            }
                        }
                    } else {
                        //System.out.println("IPX statusCode different 0");
                        if (billingPlan != null) {
                            Date nextPush = null;
                            if (nowTime.get(Calendar.HOUR_OF_DAY) >= 0 && nowTime.get(Calendar.HOUR_OF_DAY) < 12) {
                                nowTime.set(Calendar.HOUR_OF_DAY, 12);
                                nowTime.set(Calendar.MINUTE, 00);
                                nowTime.set(Calendar.SECOND, 00);
                                nextPush = nowTime.getTime();
                            } else if (nowTime.get(Calendar.HOUR_OF_DAY) >= 12 && nowTime.get(Calendar.HOUR_OF_DAY) < 23) {
                                //if (nowTime.get(Calendar.HOUR_OF_DAY) >= 12 && nowTime.get(Calendar.HOUR_OF_DAY) < 23)
                                nowTime.add(Calendar.DATE, 1);
                                nowTime.set(Calendar.HOUR_OF_DAY, 00);
                                nowTime.set(Calendar.MINUTE, 00);
                                nowTime.set(Calendar.SECOND, 00);
                                nextPush = nowTime.getTime();
                            }else{
                                nowTime.set(Calendar.HOUR_OF_DAY, 12);
                                nowTime.set(Calendar.MINUTE, 00);
                                nowTime.set(Calendar.SECOND, 00);
                                nextPush = nowTime.getTime();
                            }
                            billingplandao.updateFailedBillingPlan(billingPlan, nextPush);
                        }
                    }
                    
                    billingDirect.addTryItem(btry);

                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        executorService.shutdown();
    }
    boolean isInFirstBillPeriod(Date billingStartDate, double billingPaid){
        if(daysBetween(billingStartDate, new Date())<=7 && billingPaid<=2)
            return true;
        else 
            return false;
    }
    
    int daysBetween(Date d1, Date d2){
       return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
    
    
%>