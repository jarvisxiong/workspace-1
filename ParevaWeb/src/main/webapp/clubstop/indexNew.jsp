<%@page import="com.mixem.sdc.sms.SmsExtDao"%>
<%@page import="com.mixem.sdc.sms.StsSmsSubmit"%>
<%@page import="com.mixmobile.anyx.kohera.Kohera"%>
<%@ page import="com.mixem.sdc.*, com.mixmobile.anyx.sdk.*, com.mixmobile.anyx.snp.*, com.mixmobile.anyx.cms.*,
                java.util.Properties,java.util.List,java.util.ArrayList,java.util.StringTokenizer, java.io.File, java.sql.ResultSet, java.sql.Connection"%><%
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

boolean responses=true;

ResultSet rs = null;
Connection con = DBH.getConnection();
String sqlstr = "";

String nbrs = aReq.get("msisdn");
String submit = aReq.get("submit");
//List<String>serviceName=new ArrayList<String>();
String serviceName="";
String msisdns="";
String clubUnique="";

int bCount = 0;
int uCount = 0;

if (!submit.equals("")) {
    
    String[] list = Misc.stringSplit(nbrs, "\r\n");
    
    StringBuffer sb = new StringBuffer();
    Kohera kohera = new Kohera();
    String token = kohera.authenticate();
    
    for (int i=0; i<list.length; i++) {
        System.out.println(list[i]);
        StringTokenizer obj =new StringTokenizer(list[i],",");
while(obj.hasMoreTokens())
{
msisdns=obj.nextToken();
serviceName=obj.nextToken();
}
        try { 
            sqlstr="select aUnique from mobileClubs where aOtpServiceName='"+serviceName+"'";
            rs=DBH.getRs(con, sqlstr);
            while(rs.next())
            {
            clubUnique=rs.getString(1);
            }
            
            sqlstr = "UPDATE mobileClubBillingLog SET aStatus='0' WHERE aParsedMobile='" + msisdns + "' AND aStatus='2'"
                    + " AND aClubUnique='"+clubUnique+"'";
            System.out.println(sqlstr);
            bCount+= DBH.execUpdate(con, sqlstr);
            
            sqlstr = "UPDATEmobileClubSubscribers SET aActive='0', aUnsubscribed='" + MiscDate.now24sql() + "'"
                    + " WHERE aParsedMobile='" + msisdns + "' AND aActive='1' AND aClubUnique='"+clubUnique+"'";
            System.out.println(sqlstr);
            uCount += DBHStatic.execUpdate(con, sqlstr);
          
             System.out.println("Sending Delete Opt In Record Info to Kohera for User :>" + list[i]);
                    //deleting the record from STS through Web Service method
       responses= kohera.delete_DoubleOptIn_Record(token,serviceName,msisdns);
      System.out.println("Response for Delete Opt In Record Info to Kohera for User :>" + msisdns+ " For Service ID "+ serviceName+ "is "+ responses);
      MobileClub club = UmeTempCmsCache.mobileClubMap.get(clubUnique);
       StsSmsSubmit stopsms = new StsSmsSubmit();
        stopsms.initRequest(aReq);
               
        if(msisdns.startsWith("27"))
                stopsms.setSmsAccount("sts_ke");
            else
                stopsms.setSmsAccount("sts");
        
         stopsms.setToNumber(msisdns);
         stopsms.setMsgBody(club.getStopSms());
         stopsms.setCampaignId(Integer.parseInt(club.getSmsExt()));
         stopsms.setClubUnique(club.getUnique());
         stopsms.setSessionId(session.getId());
         String  resp = SmsExtDao.send(stopsms);  
         System.out.println("Club User Stop Message is sent to- ClubWise Bulk Stop "+stopsms.getToNumber()+" club: "+stopsms.getClubUnique()+" message: "+stopsms.getMsgBody()+" resp: "+ resp+" at "+ new java.util.Date());
             
        }
                catch (Exception e) { System.out.println("ClubWise Stop Exception: "+e); }
       
        
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
                <textarea name="msisdn" style="width:600px; height:300px; resize:none;"><%=nbrs%></textarea>
            </td>
            <td align="left" valign="top" width="80%">
                <% if (!submit.equals("")) { %>
                <span class="blue_14">
                Stopped subscriptions: <%=uCount%><br><br>
                Disabled pending tickets: <%=bCount%><br><br>
              
                    
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


