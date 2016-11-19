<%@page import="com.mixmobile.anyx.cms.MobileClub"%>
<%@ page import="com.mixem.sdc.*,com.mixem.sdc.sms.*,com.mixmobile.anyx.kohera.*,com.mixmobile.anyx.sdk.*, com.mixmobile.anyx.snp.*, com.mixmobile.anyx.cms.*,
                java.util.Properties, java.io.File, java.sql.ResultSet, java.sql.Connection,
                com.ipx.www.api.services.subscriptionapi31.types.*, com.ipx.www.api.services.subscriptionapi31.*,
                com.ipx.www.api.services.smsapi52.*, com.ipx.www.api.services.smsapi52.types.*,java.net.URL,com.mixem.sdc.sms.SmsExtDao, com.ipxbillingextra.*"%>
<%
//************************************************************************************************

System.out.println("nepal "+" bulk stool called upon");
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
String serviceName="";
ResultSet rs = null;
ResultSet rs2=null;
Connection con = DBH.getConnection();
String sqlstr = "";

String nbrs = aReq.get("msisdn");
String submit = aReq.get("submit");

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
            System.out.println("ZA stopping number "+list[i]);
        try {       
            //Disabled pending tickets     
            sqlstr = "UPDATE mobileClubBillingLog SET aStatus='0' WHERE aParsedMobile='" + list[i] + "' AND aStatus='2'";
            bCount += DBH.execUpdate(con, sqlstr);
                      
            // getting rs
            sqlstr = "SELECT  aUserUnique as user, aParam2 as subscriptionId, aNetworkCode as operator, aClubUnique as clubUnique   " 
                            + "FROMmobileClubSubscribers "
                            + "WHERE aParsedMobile='" + list[i]
                            + "' AND aActive= 1";
            System.out.println("IPX " + sqlstr);
            rs = DBH.getRs(con, sqlstr);
           
         
            
             sqlstr = "UPDATEmobileClubSubscribers SET aActive='0', aUnsubscribed='" + MiscDate.now24sql() + "'"
                    + " WHERE aParsedMobile='" + list[i] + "' AND aActive='1'";
            uCount += DBH.execUpdate(con, sqlstr);
           
                  String msisdn = list[i];
             
                  if(msisdn.startsWith("263"))
                    {
                    while(rs.next())
               {
                    
                    String [] props=new String[3];
                    String uids=rs.getString("user");
                    String clubids=rs.getString("clubUnique");
                    MobileClub clube = UmeTempCmsCache.mobileClubMap.get(rs.getString("clubUnique"));
                   if(uids != null) UmePersonalCache.delete(uids);
                   try{
                       
                        UmeUser zimuser = UmeUserDao.getUser(uids);
                        SdcMobileClubUser zimclubUser = zimuser.getClubMap().get(clubids);
                        zimclubUser.setUnsubscribed(new java.util.Date());
                        
                         StsSmsSubmit sms = new StsSmsSubmit();
            sms.setSmsAccount(UmeSmsDao.getSmsAccount(clube.getRegion()));
            sms.setUserUnique(uids);
            sms.setToNumber(msisdn);
            sms.setMsgBody(clube.getStopSms());
            try { sms.setCampaignId(Integer.parseInt(clube.getSmsExt())); } catch (NumberFormatException e) {}
            sms.setMsgCode1(zimclubUser.getCampaign());
                     
            String resp = SmsExtDao.send(sms);
            System.out.println("zimstop message: "+" from bulk stop: "+sms.getMsgBody()+" msisdn: "+msisdn+" response: "+resp);
            MobileClubCampaignDao.log("STSReq", "USERSTOP", uids, msisdn,null,dmn.getUnique(),zimclubUser.getCampaign(), clube.getUnique(),"STOP", 0, request, response);            
                       
                   }catch(Exception exed){exed.printStackTrace();}
                    
                  }
                    
             }
 
             
            if(msisdn.startsWith("27"))
            {
                            
              
                while(rs.next())
               {
                    
                    String [] props=new String[3];
                    String uid=rs.getString("user");
                    String operator = rs.getString("operator"); 
                    String clubid=rs.getString("clubUnique");
                    MobileClub club = UmeTempCmsCache.mobileClubMap.get(rs.getString("clubUnique"));
                   if(uid != null) UmePersonalCache.delete(uid);
                    //====ZA Stop
                         if(club.getRegion().equals("ZA")){
                    try { 
                        UmeUser user = UmeUserDao.getUser(uid);
                        SdcMobileClubUser clubUser = user.getClubMap().get(clubid);
                        clubUser.setUnsubscribed(new java.util.Date());
                       MobileClubCampaignDao.log("STSReq", "USERSTOP", uid, user.getMobile(),null,dmn.getUnique(),clubUser.getCampaign(), club.getUnique(),"STOP", 0, request, response);   
        String sqlstrr="select aOtpServiceName from mobileClubs where aUnique='"+clubid+"'";
         
        if(con==null) con = DBH.getConnection();
        rs2=DBH.getRs(con, sqlstrr);
           if(rs2.next())
            {
             
            serviceName=rs2.getString(1);
             responses= kohera.delete_DoubleOptIn_Record(token,serviceName,user.getMobile());  
           
            
            }
       
        
            rs2.close();
                                                            
                    }catch (Exception e) { 
                        System.out.println("ZA stopping number "+ ": " + e.getMessage());      
                    }
                }
                    //=- end ZA stop                                                          
                    
                }
                 //Stopped subscriptions            
           
                
            }
     
           rs.close();
        }
        catch (Exception e) { System.out.println("nepal "+"exception: "+e);
        
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