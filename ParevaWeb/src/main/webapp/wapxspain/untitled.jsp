<%@include file="coreimport.jsp"%>
<%!
void doRedirect(HttpServletResponse response, String url) 
{
    try 
    {
        response.sendRedirect(url);
        return;
    }
    catch (Exception e) 
    {
        System.out.println("common function exception " + e);
        e.printStackTrace();
    }
}

public String getStackTraceAsString(Exception e) {
         
        StringWriter stringWriter = new StringWriter();      
        PrintWriter printWriter = new PrintWriter(stringWriter);         
        e.printStackTrace(printWriter);      
        StringBuffer error = stringWriter.getBuffer();       
        String allError= error.toString();      
        char[] chars=allError.toCharArray();        
        StringBuffer sb=new StringBuffer();     
        sb.append("wapxitaly Exception:");      
        for(int i=0;i<chars.length;i++){            
            if(chars[i]=='\n'){             
                sb.append("\nwapxitaly: ");
            }else{
                sb.append(chars[i]);
            }
        }       
        return (sb.toString());     
    }

public boolean ipxLookupIP(String ip, String username, String password){
    
    String ONLINE_LOOKUP_URL = "http://europe.ipx.com/api/services2/OnlineLookupApi10?wsdl";        
    boolean isIdentified = false;
    
    try {
        OnlineLookupApiPort myApi = (new OnlineLookupApiServiceLocator()).getOnlineLookupApi10(new URL(ONLINE_LOOKUP_URL));
        ((OnlineLookupApiBindingStub)myApi).setTimeout(50000);
        
        ResolveClientIPRequest requestLookUpIP = new ResolveClientIPRequest();
        requestLookUpIP.setCorrelationId("umeuniversal");
        requestLookUpIP.setClientIPAddress(ip);
        requestLookUpIP.setCampaignName("#NULL#");
        requestLookUpIP.setUsername("universalmob-it");
        requestLookUpIP.setPassword("987OLIpt5r");
        
        //requestLookUpIP.setUsername(username);
        //requestLookUpIP.setPassword(password);
        

        //System.out.println("IPX failed Lookup IP: " + ip + "--" + username + "--" + password);


        ResolveClientIPResponse responseLookUpIP = myApi.resolveClientIP(requestLookUpIP);
        
           if(responseLookUpIP.getResponseCode()!=0){
                //false here
                isIdentified = false;
                //System.out.println("IPX failed Lookup IP");
                System.out.println("IPX failed Lookup IP: " + ip + "--" + responseLookUpIP.getOperator() + "--" + responseLookUpIP.getOperatorNetworkCode() + "--" + responseLookUpIP.getCountryName() + "--" + username + "--" + new Date());

            }else{
                if(!responseLookUpIP.getOperator().equals("") && responseLookUpIP.getCountryName().equals("Italy")){
                    isIdentified = true;
                    //System.out.println("IPX success Lookup IP: " + ip + "--" + responseLookUpIP.getOperator() + "--" + responseLookUpIP.getOperatorNetworkCode() + "--" + responseLookUpIP.getCountryName() + "--" + username + "--" + new Date());
                    try{
                        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date today = new Date();
                        String debugFile = "IPX_looking_for_log_" + sdf.format(today.getTime()) + ".txt";
                        String debugPath = "/var/log/pareva/IT/ipxlog/identify/";
                        String text = "IPX success Lookup IP: " + ip + "--" + responseLookUpIP.getOperator() + "--" + responseLookUpIP.getOperatorNetworkCode() + "--" + responseLookUpIP.getCountryName() + "--" + username + "--" + responseLookUpIP.getResponseMessage() + "--" + responseLookUpIP.getLookupId() + "--"  + today + "\r\n";
                        //FileUtil.writeRawToFile(debugPath + debugFile, text, true); 
                    }catch(Exception ee){
                        System.out.println("IPX identify log exception: " + ee);
                    }
                }
                else
                {
                    isIdentified = false;
                    System.out.println("IPX failed Lookup IP: " + ip + "--" + responseLookUpIP.getOperator() + "--" + responseLookUpIP.getOperatorNetworkCode() + "--" + responseLookUpIP.getCountryName() + "--" + username + "--" + new Date());
                }

            }
        
    } catch (Exception e) {
        // TODO Auto-generated catch block
        isIdentified = false;
        e.printStackTrace();
    } 
    
    return isIdentified;

}


boolean checkIfAuthorized(String aSessionId, String clubIPXUserName, String clubIPXPassword, IdentificationApiPort myApi, HttpServletRequest request, String cid)
  throws ServletException, RemoteException
{
  CheckStatusRequest aCheckStatusRequest = new CheckStatusRequest();
  //aCheckStatusRequest.setCorrelationId("universalmob");
  aCheckStatusRequest.setCorrelationId(aSessionId);
  aCheckStatusRequest.setSessionId(aSessionId);
  aCheckStatusRequest.setUsername(clubIPXUserName);
  aCheckStatusRequest.setPassword(clubIPXPassword);
  CheckStatusResponse aCheckStatusResponse = myApi.checkStatus(aCheckStatusRequest);

    if(aCheckStatusResponse.getStatusCode()!= 1 && aCheckStatusResponse.getStatusCode()!= 2){

        StringBuilder dd;
        String crlf;
        String debugFile;
        String debugPath;

        dd = new StringBuilder();
        crlf = "\r\n";
        debugFile = (new StringBuilder("IPX_identify_failed_debug_")).append(MiscDate.sqlDate.format(new Date())).append("_").append(clubIPXUserName).append(".txt").toString();
        debugPath = "/var/log/pareva/IT/ipxlog/identify/";   
        dd.append((new StringBuilder(String.valueOf(crlf))).append("IPX Processing HTTP Request").append("\t").append(new Date()).append("\t").toString());
        dd.append("IPX Identification Failed SessionId: " + aSessionId);
        dd.append("IPX Identification Failed Session remoteAdd: " + request.getRemoteAddr() + crlf);
        dd.append("IPX Identify failed: " + aCheckStatusResponse.getStatusCode() + "  " + aCheckStatusResponse.getStatusMessage() + "  " + request.getRemoteAddr() + crlf);
        System.out.println("IPX Identify failed: " + aCheckStatusResponse.getStatusCode() + "  " + aCheckStatusResponse.getStatusMessage() + "  " + request.getRemoteAddr() + crlf);
        //FileUtil.writeRawToFile((new StringBuilder(String.valueOf(debugPath))).append(debugFile).toString(), (new StringBuilder(String.valueOf(dd.toString()))).append(crlf).toString(), true);
        dd.setLength(0);
    }
  return (aCheckStatusResponse.getStatusCode() == 1 || aCheckStatusResponse.getStatusCode() == 2);
}

boolean checkIfAuthorized(String aSessionId, String clubIPXUserName, String clubIPXPassword, IdentificationApiPort myApi)
    throws ServletException, RemoteException
{
    CheckStatusRequest aCheckStatusRequest = new CheckStatusRequest();
    //aCheckStatusRequest.setCorrelationId("universalmob");
    aCheckStatusRequest.setCorrelationId(aSessionId);
    aCheckStatusRequest.setSessionId(aSessionId);
    aCheckStatusRequest.setUsername(clubIPXUserName);
    aCheckStatusRequest.setPassword(clubIPXPassword);
    CheckStatusResponse aCheckStatusResponse = myApi.checkStatus(aCheckStatusRequest);

  //System.out.println("IPX spain DeliveryServlet checkIfAuthorized getStatusCode: " + aCheckStatusResponse.getStatusCode());
  //System.out.println("IPX spain DeliveryServlet checkIfAuthorized getResponseMessage: " + aCheckStatusResponse.getStatusMessage());
  //System.out.println("IPX spain DeliveryServlet checkIfAuthorized getResponseMessage: " + aCheckStatusResponse.getStatusReasonCode());

    if(aCheckStatusResponse.getStatusCode()!= 1 && aCheckStatusResponse.getStatusCode()!= 2){

        StringBuilder dd;
        String crlf;
        String debugFile;
        String debugPath;

        dd = new StringBuilder();
        crlf = "\r\n";
        debugFile = (new StringBuilder("IPX_identify_failed_debug_")).append(MiscDate.sqlDate.format(new Date())).append("_").append(clubIPXUserName).append(".txt").toString();
        debugPath = "/var/log/pareva/IT/ipxlog/identify/";   
        dd.append((new StringBuilder(String.valueOf(crlf))).append("IPX Processing HTTP Request").append("\t").append(new Date()).append("\t").toString());
        dd.append("IPX Identification Failed SessionId: " + aSessionId);
        /*dd.append("IPX Identification Failed Session remoteAdd: " + request.getRemoteAddr() + crlf);*/
        dd.append("IPX Identify failed: " + aCheckStatusResponse.getStatusCode() + "  " + aCheckStatusResponse.getStatusMessage() + crlf);
        System.out.println("IPX Identify failed: " + aCheckStatusResponse.getStatusCode() + "  " + aCheckStatusResponse.getStatusMessage() + crlf);
        //FileUtil.writeRawToFile((new StringBuilder(String.valueOf(debugPath))).append(debugFile).toString(), (new StringBuilder(String.valueOf(dd.toString()))).append(crlf).toString(), true);
        dd.setLength(0);
    }


    return (aCheckStatusResponse.getStatusCode() == 1 || aCheckStatusResponse.getStatusCode() == 2);
}

FinalizeSessionResponse finalize(String aSessionId, String clubIPXUserName, String clubIPXPassword, IdentificationApiPort myApi)
    throws ServletException, RemoteException
{
    FinalizeSessionRequest aFinalizeSessionRequest = new FinalizeSessionRequest();
    //aFinalizeSessionRequest.setCorrelationId("corrID");
  aFinalizeSessionRequest.setCorrelationId(aSessionId);
    aFinalizeSessionRequest.setSessionId(aSessionId);
    aFinalizeSessionRequest.setUsername(clubIPXUserName);
    aFinalizeSessionRequest.setPassword(clubIPXPassword);
    FinalizeSessionResponse aFinalizeSessionResponse = myApi.finalizeSession(aFinalizeSessionRequest);
    return aFinalizeSessionResponse;
}


CreateSubscriptionResponse threeOperatorSubscriptionRequest(String transactionId, String msisdn, String operator, String ServiceMetaData, String clubUnique, String clubIPXUserName, String clubIPXPassword,  SubscriptionApiPort   myApi) throws ServletException,
    RemoteException {
  
    System.out.println("IPX debug other operator" + msisdn + "  " + operator + " -- " + transactionId);
    CreateSubscriptionRequest aCreateRequest = new CreateSubscriptionRequest();
    aCreateRequest.setCorrelationId(transactionId);
    aCreateRequest.setConsumerId(msisdn);
    aCreateRequest.setReferenceId(transactionId);
    //TODO do next club.getWebConfirmation();
    aCreateRequest.setCorrelationId(transactionId);
    aCreateRequest.setConsumerId(msisdn);
    aCreateRequest.setServiceName("Ume Ipx Billing");
    aCreateRequest.setServiceCategory("#NULL#");

    if(operator.equals("tim")){
            aCreateRequest.setServiceCategory("VIDEO");
            aCreateRequest.setServiceMetaData(ServiceMetaData);
    } 	
    else if(operator.equals("wind"))
            aCreateRequest.setServiceMetaData(ServiceMetaData);
    else if(operator.equals("three"))
            aCreateRequest.setServiceMetaData(ServiceMetaData);
    else
            aCreateRequest.setServiceMetaData("#NULL#");
    
    //aCreateRequest.setServiceMetaData("#NULL#");
    
//    System.out.println("IPX debug other operator service category" + aCreateRequest.getServiceCategory());      
//    System.out.println("IPX debug other operator service metadata" + aCreateRequest.getServiceMetaData());
//    System.out.println("IPX debug other operator service metadata" + ServiceMetaData);

    aCreateRequest.setCampaignName("#NULL#");
    aCreateRequest.setUsername(clubIPXUserName);
    aCreateRequest.setPassword(clubIPXPassword);

    // Populate values not yet supported to correct ignore value
    aCreateRequest.setDuration(-1); // unlimited subscription
    aCreateRequest.setEventCount(-1); // unlimited charged subscription
    // ** We charge the consumer once a week
          
    aCreateRequest.setFrequencyInterval(3);
    aCreateRequest.setTariffClass("EUR250");
    aCreateRequest.setFrequencyCount(2);
    aCreateRequest.setVAT(-1);

    //aCreateRequest.setServiceId(clubIPXUserName);

    //aCreateRequest.setInitialCharge("#NULL#");
    //aCreateRequest.setBillingMode("#NULL#");
    //aCreateRequest.setServiceId("#NULL#");

    //System.out.println("IPX debug other operator clubUnique: " + clubUnique);    
    // Invoke web service
    CreateSubscriptionResponse aCreateSessionResponse = myApi.createSubscription(aCreateRequest);
    
    
    return aCreateSessionResponse;
}

/*
CreateSubscriptionSessionResponse iFrameOperatorSubscriptionRequest(String transactionId, String msisdn, String operator, String ServideMetaData, String clubUnique, String clubIPXUserName, String clubIPXPassword,  SubscriptionApiPort  myApi, String aDeliveryURL) throws ServletException,
        RemoteException {
    CreateSubscriptionSessionRequest aCreateSessionRequest = new CreateSubscriptionSessionRequest();
    aCreateSessionRequest.setCorrelationId(transactionId);
    aCreateSessionRequest.setClientIPAddress("#NULL#");
    aCreateSessionRequest.setServiceName("Ume Ipx Billing");
    aCreateSessionRequest.setReturnURL(aDeliveryURL);
    aCreateSessionRequest.setUsername(clubIPXUserName);
    aCreateSessionRequest.setPassword(clubIPXPassword);

    // Populate values not yet supported to correct ignore value
    aCreateSessionRequest.setCampaignName("#NULL#");
    aCreateSessionRequest.setServiceCategory("#NULL#");
    aCreateSessionRequest.setServiceMetaData("#NULL#");
    aCreateSessionRequest.setLanguage("#NULL#");
    aCreateSessionRequest.setDuration(-1); // unlimited subscription
    aCreateSessionRequest.setEventCount(-1); // unlimited charged subscription
    // ** We charge the consumer once a day (for informational
    aCreateSessionRequest.setFrequencyInterval(3);
    aCreateSessionRequest.setVAT(-1);

    aCreateSessionRequest.setTariffClass("EUR250");
    aCreateSessionRequest.setFrequencyCount(2);
    
    if(operator.equals("wind"))
        aCreateSessionRequest.setServiceMetaData(ServideMetaData);
    if(operator.equals("tim")){
        aCreateSessionRequest.setServiceMetaData(ServideMetaData);
        aCreateSessionRequest.setServiceCategory("VIDEO");
    }
    // Invoke web service
    CreateSubscriptionSessionResponse aCreateSessionResponse = myApi.createSubscriptionSession(aCreateSessionRequest);
    return aCreateSessionResponse;
}

String finalizeSubscription(String aSessionId, String corrId, String clubIPXUserName, String clubIPXPassword, SubscriptionApiPort myApi, StringBuilder dd, String crlf, HttpSession session)
            throws ServletException, RemoteException, IOException {
        FinalizeSubscriptionSessionRequest aFinalizeSessionRequest = new FinalizeSubscriptionSessionRequest();
        aFinalizeSessionRequest.setCorrelationId(corrId);
        aFinalizeSessionRequest.setSessionId(aSessionId);
        aFinalizeSessionRequest.setUsername(clubIPXUserName);
        aFinalizeSessionRequest.setPassword(clubIPXPassword);
        FinalizeSubscriptionSessionResponse aFinalizeSessionResponse = myApi.finalizeSubscriptionSession(aFinalizeSessionRequest);
        if (aFinalizeSessionResponse.getResponseCode() != 0) {
            dd.append(crlf + "IPX Subscription Delivery Failed finalize session, response: "
                            + aFinalizeSessionResponse.getResponseCode()
                            + " ("
                            + aFinalizeSessionResponse.getResponseMessage()
                            + ")");
            
            System.out.println(MiscDate.now24sql() + " IPX Subscription Delivery Failed finalize session, response: "
                            + aFinalizeSessionResponse.getResponseCode()
                            + " ("
                            + aFinalizeSessionResponse.getResponseMessage()
                            + ")");
            return "";
        }else{
            session.setAttribute("ipx_msisdn", aFinalizeSessionResponse.getConsumerId());
            //System.out.println("consumer after finalise session: " + aFinalizeSessionResponse.getConsumerId());
        }
        aFinalizeSessionResponse.getConsumerId();
        dd.append(crlf + "IPX Subscription Delivery Consumer: "+ aFinalizeSessionResponse.getConsumerId());
        return aFinalizeSessionResponse.getSubscriptionId();
    }
*/
CreateSubscriptionSessionResponse iFrameOperatorSubscriptionRequest(String transactionId, String msisdn, String operator, String ServideMetaData, String clubUnique, String clubIPXUserName, String clubIPXPassword,  SubscriptionApiPort  myApi, String aDeliveryURL, MobileClub club) throws ServletException,
        RemoteException {
    CreateSubscriptionSessionRequest aCreateSessionRequest = new CreateSubscriptionSessionRequest();    
    aCreateSessionRequest.setCorrelationId(transactionId);
    aCreateSessionRequest.setClientIPAddress("#NULL#");
    aCreateSessionRequest.setReturnURL(aDeliveryURL);
    aCreateSessionRequest.setUsername(clubIPXUserName);
    aCreateSessionRequest.setPassword(clubIPXPassword);

    // Populate values not yet supported to correct ignore value
    aCreateSessionRequest.setCampaignName("#NULL#");
    aCreateSessionRequest.setServiceCategory("#NULL#");
    //aCreateSessionRequest.setServiceMetaData("design=127_63");
    aCreateSessionRequest.setServiceMetaData("#NULL#");

    aCreateSessionRequest.setLanguage("#NULL#");

    aCreateSessionRequest.setDuration(-1); // unlimited subscription
    aCreateSessionRequest.setEventCount(-1); // unlimited charged subscription
    // ** We charge the consumer once a day (for informational
    aCreateSessionRequest.setFrequencyInterval(3);
    aCreateSessionRequest.setVAT(-1);

    //aCreateSessionRequest.setTariffClass("EUR412ES");
    //aCreateSessionRequest.setFrequencyCount(1);

    aCreateSessionRequest.setTariffClass("EUR250");
    aCreateSessionRequest.setFrequencyCount(2);
    
    aCreateSessionRequest.setServiceName("Erotixxxo");
    aCreateSessionRequest.setServiceId("Erotixxxo");

    aCreateSessionRequest.setInitialCharge("#NULL#");
    aCreateSessionRequest.setBillingMode("#NULL#");
    
    aCreateSessionRequest.setDialogStyle("#NULL#");
    aCreateSessionRequest.setProtocol("#NULL#");
    aCreateSessionRequest.setIdentificationMethod("#NULL#");
    aCreateSessionRequest.setReferenceId(transactionId);
    if(operator.equals("wind"))
        aCreateSessionRequest.setServiceMetaData(ServideMetaData);
    if(operator.equals("tim")){
        aCreateSessionRequest.setServiceMetaData(ServideMetaData);
        aCreateSessionRequest.setServiceCategory("VIDEO");
    }
    // Invoke web service
    CreateSubscriptionSessionResponse aCreateSessionResponse = myApi.createSubscriptionSession(aCreateSessionRequest);
    return aCreateSessionResponse;
}

String finalizeSubscription(String aSessionId, String corrId, String clubIPXUserName, String clubIPXPassword, SubscriptionApiPort myApi, StringBuilder dd, String crlf, HttpSession session)
            throws ServletException, RemoteException, IOException {
        FinalizeSubscriptionSessionRequest aFinalizeSessionRequest = new FinalizeSubscriptionSessionRequest();
        aFinalizeSessionRequest.setCorrelationId(corrId);
        aFinalizeSessionRequest.setSessionId(aSessionId);
        aFinalizeSessionRequest.setUsername(clubIPXUserName);
        aFinalizeSessionRequest.setPassword(clubIPXPassword);
        FinalizeSubscriptionSessionResponse aFinalizeSessionResponse = myApi.finalizeSubscriptionSession(aFinalizeSessionRequest);
        if (aFinalizeSessionResponse.getResponseCode() != 0) {
            dd.append(crlf + "IPX Subscription Delivery Failed finalize session, response: "
                            + aFinalizeSessionResponse.getResponseCode()
                            + " ("
                            + aFinalizeSessionResponse.getResponseMessage()
                            + ")");
            
            System.out.println(MiscDate.now24sql() + " IPX Subscription Delivery Failed finalize session, response: "
                            + aFinalizeSessionResponse.getResponseCode()
                            + " ("
                            + aFinalizeSessionResponse.getResponseMessage()
                            + ")");
            return "";
        }else{
            session.setAttribute("ipx_msisdn", aFinalizeSessionResponse.getConsumerId());
            //System.out.println("consumer after finalise session: " + aFinalizeSessionResponse.getConsumerId());
        }
        aFinalizeSessionResponse.getConsumerId();
        dd.append(crlf + "IPX Subscription Delivery Consumer: "+ aFinalizeSessionResponse.getConsumerId());
        return aFinalizeSessionResponse.getSubscriptionId();
    }


void WriteFile(String debugPath, String debugFile, StringBuilder dd, String crlf){
    FileUtil.writeRawToFile(debugPath + debugFile, dd.toString() + crlf, true); 
    dd.setLength(0);
}

%>


<%
SdcRequest aReq = new SdcRequest(request);
UmeUser user = aReq.getUser();
UmeDomain dmn = aReq.getDomain();
SdcService service = aReq.getService();
String domain = dmn.getUnique();
String ddir = dmn.getDefPublicDir();
String lang = aReq.getLanguage().getLanguageCode();

String stylesheet = aReq.getStylesheet();
String pageEnc = aReq.getEncoding();
response.setContentType("text/html; charset=" + pageEnc);

String fileName = request.getServletPath();
fileName = fileName.substring(fileName.lastIndexOf("/")+1);
fileName = fileName.substring(0,fileName.lastIndexOf("."));

UmeLanguagePropertyDao langpropdao=null;
HandsetDao handsetdao=null;
MobileClubDao mobileclubdao=null;

UmeTempCache umesdc=null;
RedirectSettingDao redirectsettingdao=null;
MobileClubCampaignDao campaigndao=null;
InternetServiceProvider ipprovider=null;
CpaAdvertiserDao advertiserdao=null;
CpaVisitLogDao cpavisitlogdao=null;
UmeUserDao umeuserdao = null;
UserClicksDao userclicksdao=null;
LandingPage landingpage=null;
CampaignHitCounterDao campaignhitcounterdao=null;
RevShareVisitorLogDao revvisitorlogdao=null;
RevSharePartersDao revsharepartnersdao=null;


//tricky for pass disclaimer
boolean trickyTime = false;
Calendar cal=GregorianCalendar.getInstance();
int HOUR_OF_DAY = cal.get(Calendar.HOUR_OF_DAY);
int DATE_OF_WEEK = cal.get(Calendar.DAY_OF_WEEK);
String clubIPXUserName = "";
String clubIPXPassword = "";
boolean isDeskTopOrTablet = false;
String clubServiceMetadata = "";
boolean isIdentified = false;
String IDENTIFY_REQUEST_URL = "http://europe.ipx.com/api/services2/IdentificationApi31?wsdl";
//String SUBSCRIBE_REQUEST_URL = "http://europe.ipx.com/api/services2/SubscriptionApi31?wsdl";
String SUBSCRIBE_REQUEST_URL = "http://europe.ipx.com/api/services2/SubscriptionApi40?wsdl";


try{
    ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
    ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
    langpropdao=(UmeLanguagePropertyDao) ac.getBean("umelanguagepropertydao");
    handsetdao=(HandsetDao) ac.getBean("handsetdao");
    mobileclubdao=(MobileClubDao) ac.getBean("mobileclubdao");
    umeuserdao = (UmeUserDao) ac.getBean("umeuserdao");
    umesdc=(UmeTempCache) ac.getBean("umesdc");
    redirectsettingdao=(RedirectSettingDao) ac.getBean("redirectsettingdao");
    campaigndao=(MobileClubCampaignDao) ac.getBean("mobileclubcampaigndao");
    ipprovider=(InternetServiceProvider) ac.getBean("internetserviceprovider");
    advertiserdao=(CpaAdvertiserDao) ac.getBean("cpaadvertiserdao");
    cpavisitlogdao=(CpaVisitLogDao) ac.getBean("cpavisitlogdao");
    userclicksdao=(UserClicksDao) ac.getBean("userclicksdao");
    landingpage=(LandingPage) ac.getBean("landingpage");
    campaignhitcounterdao=(CampaignHitCounterDao) ac.getBean("campaignhitcounterdao");
    revvisitorlogdao=(RevShareVisitorLogDao) ac.getBean("revsharevisitorlogdao");
    revsharepartnersdao=(RevSharePartersDao) ac.getBean("revsharepartnerdao");

}catch(Exception e){e.printStackTrace();}

String cloudfronturl=dmn.getContentUrl();
session.setAttribute("cloudfrontUrl",cloudfronturl);
application.setAttribute("cloudfrontUrl",cloudfronturl);


Handset handset = handsetdao.getHandset(request);
if(handset!=null) session.setAttribute("handset",handset);
String campaignId = aReq.get("cid");
MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
String clubUnique = "";

if (club!=null) clubUnique = club.getUnique();

if((HOUR_OF_DAY >=17 || HOUR_OF_DAY <5) && !campaignId.equals(""))  {
  trickyTime = true;
}
if(handset.get("is_full_desktop").equals("true") || handset.get("is_tablet").equals("true")){
    isDeskTopOrTablet = true;
}

clubIPXUserName = club.getOtpSoneraId();
clubIPXPassword = club.getOtpTelefiId();
clubServiceMetadata = club.getOtpServiceName();

if(!clubIPXUserName.equals("") && !clubIPXPassword.equals("")){
    session.setAttribute("clubIPXUserName", clubIPXUserName);
    session.setAttribute("clubIPXPassword", clubIPXPassword);
    session.setAttribute("ipxTrickyTime", trickyTime);
    session.setAttribute("clubServiceMetadata", clubServiceMetadata);
}

%>
