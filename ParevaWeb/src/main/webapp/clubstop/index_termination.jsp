<%@ page import="com.mixem.sdc.*,com.mixem.sdc.sms.*,com.mixmobile.anyx.kohera.*,com.mixmobile.anyx.sdk.*, com.mixmobile.anyx.snp.*, com.mixmobile.anyx.cms.*,
                java.util.Properties, java.io.File, java.sql.ResultSet, java.sql.Connection,
                com.ipx.www.api.services.subscriptionapi31.types.*, com.ipx.www.api.services.subscriptionapi31.*,
                com.ipx.www.api.services.smsapi52.*, com.ipx.www.api.services.smsapi52.types.*,java.net.URL,com.mixem.sdc.sms.SmsExtDao"%>
<%
//***************************************************************************************************
SdcRequest aReq = new SdcRequest(request);
UmeUser sdcuser = aReq.getUser();
UmeDomain dmn = aReq.getDomain();
SdcService service = aReq.getService();

String domain = dmn.getUnique();
String langcode = aReq.getLanguage().getLanguageCode();

String stylesheet = aReq.getStylesheet();
String pageEnc = aReq.getEncoding();
response.setContentType("text/html; charset=" + pageEnc);

String fileName = request.getServletPath();
fileName = fileName.substring(fileName.lastIndexOf("/")+1);
fileName = fileName.substring(0,fileName.lastIndexOf("."));
SdcLanguageProperty lp = UmeLanguagePropertyDao.get(fileName, service, aReq.getLanguage(), dmn);
//***************************************************************************************************

String statusMsg = "";

ResultSet rs = null;
Connection con = DBH.getConnection();
String sqlstr = "";

String nbrs = aReq.get("msisdn");
String submit = aReq.get("submit");
String serviceName="";
String aRegion="";
boolean responses=true;
        
int bCount = 0;
int uCount = 0;
int itCount = 0;


try {      

        String msisdn = "393383206659";
        String[] props = new String[3];
        //String uid = rs.getString("user");
        String subscriptionId = "2-151606827";
        String operator = "tim";   
     
        //delete cache user for Stopped subscriptions
        //if(uid != null) CacheManagerSdc.delete(uid);    
        //making terminate subscription for Italy
            try { 
                System.out.println("IPX hello : " + msisdn);
                String anIpxUserId = "universalmob-it"; 
                String anIpxPassword = "987OLIpt5r";

                if(!operator.equals("vodafone")){
                    IpxSmsSubmit  sms = new IpxSmsSubmit(aReq);
                    if(operator.equals("wind")){
                        sms.setFromNumber("3202071010");
                    }
                    else{
                        sms.setFromNumber("3399942323");
                    }

                    sms.setSmsAccount("ipx");
                    sms.setToNumber(msisdn);
                    String msg = "Stop message";
                    //club.getStopSms();
                    sms.setMsgBody(msg);
                    sms.setCurrencyCode("EUR");
                    String resp = SmsExtDao.send((SdcSmsSubmit)sms);
                    System.out.println("IPX sms sending to number: " + msisdn + " - response: " + resp);
                }

                // Target URL 
                URL anIpxSubUrl = new URL("http://europe.ipx.com/api/services2/SubscriptionApi31?wsdl");

                SubscriptionApiPort aPort = new SubscriptionApiServiceLocator().getSubscriptionApi31(anIpxSubUrl);
                // Set read timeout to 10 minute
                ((SubscriptionApiBindingStub) aPort).setTimeout(10 * 60 * 1000); 
                TerminateSubscriptionRequest aTerminateRequest = new TerminateSubscriptionRequest();
                
                aTerminateRequest.setCorrelationId("universalmob");
                aTerminateRequest.setConsumerId(msisdn);
                aTerminateRequest.setSubscriptionId(subscriptionId);
                aTerminateRequest.setUsername(anIpxUserId);
                aTerminateRequest.setPassword(anIpxPassword);

                TerminateSubscriptionResponse aTerminateResponse = aPort.terminateSubscription(aTerminateRequest); 
                // Debug
                System.out.println("IPX Terminate result: responseCode: " + aTerminateResponse.getResponseCode());
                System.out.println("IPX Terminate result: responseMessage: " + aTerminateResponse.getResponseMessage());         

                //if(aTerminateResponse.getResponseCode() == 0){
                itCount++;
                //}                                      
            }catch (Exception e) { 
                System.out.println("IPX terminateSubscription catching exception"+ ": " + e.getMessage());      
            }
}
catch (Exception e) { System.out.println(e); }
%>