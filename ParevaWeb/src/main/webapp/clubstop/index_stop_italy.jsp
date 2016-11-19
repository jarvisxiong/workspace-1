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

System.out.println("IPX submit: " + submit);

if (!submit.equals("")) {
    
    String[] list = Misc.stringSplit(nbrs, "\r\n");
    
    StringBuffer sb = new StringBuffer();
    Kohera kohera = new Kohera();
    String token = kohera.authenticate();
    for (int i=0; i<list.length; i++) {
        System.out.println(list[i]);
        
        try {       
            //Disabled pending tickets     
            sqlstr = "UPDATE mobileClubBillingLog SET aStatus='0' WHERE aParsedMobile='" + list[i] + "' AND aStatus='2'";
            bCount += DBH.execUpdate(con, sqlstr);
                      
            // getting rs
            sqlstr = "SELECT  aUserUnique as user, aParam2 as subscriptionId, aNetworkCode as operator   " 
                            + "FROMmobileClubSubscribers "
                            + "WHERE aParsedMobile='" + list[i] + "'";
                            //+ "' AND aActive= 1";
            System.out.println("IPX " + sqlstr);
            rs = DBH.getRs(con, sqlstr);

            //Stopped subscriptions    
            /*        
            sqlstr = "UPDATEmobileClubSubscribers SET aActive='0', aUnsubscribed='" + MiscDate.now24sql() + "'"
                    + " WHERE aParsedMobile='" + list[i] + "' AND aActive='1'";
            System.out.println("IPX " + sqlstr);
            uCount += DBH.execUpdate(con, sqlstr);
            */
            String msisdn = list[i];
            while (rs.next()) {
                String[] props = new String[3];
                String uid = rs.getString("user");
                String subscriptionId = rs.getString("subscriptionId");
                String operator = rs.getString("operator");                
                //delete cache user for Stopped subscriptions
                System.out.println("IPX uid: " + uid);
                System.out.println("IPX subscriptionId: " + subscriptionId);
                System.out.println("IPX operator: " + operator);

                if(uid != null) UmePersonalCache.delete(uid);    
                //making terminate subscription for Italy
                if(operator != null && (operator.equals("vodafone") || operator.equals("wind") || operator.equals("tim"))){
                    try { 
                        System.out.println("IPX hello : " + msisdn);
                        String anIpxUserId = "universalmob-it"; 
                        String anIpxPassword = "987OLIpt5r";
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
                            if(!operator.equals("vodafone")){
                                IpxSmsSubmit  sms = new IpxSmsSubmit(aReq);
                                if(operator.equals("wind")){
                                    sms.setFromNumber("3202071010");
                                }
                                else{
                                    sms.setFromNumber("3399942323");
                                    //sms.setServiceCategory("ACCESS");
                                    //sms.setReferenceID(subscriptionId);
                                }
                                sms.setSmsAccount("ipx");
                                sms.setToNumber(msisdn);
                                String msg = "Il servizio VIDEO CASALINGHI è stato disattivato. Per info chiama il 199241488.";
                                //club.getStopSms();
                                sms.setMsgBody(msg);
                                sms.setCurrencyCode("EUR");
                                String resp = SmsExtDao.send((SdcSmsSubmit)sms);
                                System.out.println("IPX sms sending: " + resp);
                            }
                        //}                                      
                    }catch (Exception e) { 
                        System.out.println("IPX terminateSubscription catching exception"+ ": " + e.getMessage());     
                        DBH.closeConnection(con); 
                    }
                }
            }
           rs.close();
        }
        catch (Exception e) { 
            System.out.println("IPX: Exception: " + e); 
            DBH.closeConnection(con);
        }
    }
}
DBH.closeConnection(con);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">

<script LANGUAGE="JavaScript">
function confirmSubmit() {
    var agree=confirm("Are you sure you wish to continue?");
    if (agree) return true ;
    else return false ;
}
</script>

</head>
<body>

<form action="<%=fileName%>.jsp" method="post" style="padding: 0px; margin: 0px;">
  
<table cellspacing="0" cellpadding="0" border="0" width="99%">
<tr><td>    
            <table cellspacing="0" cellpadding="4" border="0" width="100%">
            <tr>
                <td align="left" valign="bottom" class="big_blue">Bulk Stop</td>
                <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
            </tr>
        </table>
</td></tr>        
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td>Copy MSISDNs in the field below. One number per each row in international format (27123456789).</td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td>  
    
    <table cellspacing="0" cellpadding="5" border="0" style="width:100%;">
    <tr>
            <td>
                <textarea name="msisdn" style="width:200px; height:300px; resize:none;"><%=nbrs%></textarea>
            </td>
            <td align="left" valign="top" width="80%">
                <% if (!submit.equals("")) { %>
                <span class="blue_14">
                Stopped subscriptions: <%=uCount%><br><br>
                Disabled pending tickets: <%=bCount%>
                    <%if(itCount>0){%>
                        <br><br>Italy termination successful: <%=itCount%>
                    <%}%>
                </span>     
                <% } %>
            </td>    
    </tr>
    <tr>
            <td colspan="2"><input type="submit" name="submit" value="Submit" style="width:200px;" onClick="return confirmSubmit();"></td>
    </tr>
   

    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

</table>
            
</form>            

</body>
</html>