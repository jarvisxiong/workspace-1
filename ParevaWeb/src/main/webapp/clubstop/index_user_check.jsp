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



String nbrs = aReq.get("msisdn");
String submit = aReq.get("submit");
String serviceName="";
String aRegion="";
boolean responses=true;
StringBuffer sb = null;
        
int bCount = 0;
int uCount = 0;
int itCount = 0;

System.out.println("IPX submit: " + submit);
String stringNumber = "";

if (!submit.equals("")) {
    
    String[] list = Misc.stringSplit(nbrs, "\r\n");
    
    sb = new StringBuffer();

    for (int i=0; i<list.length; i++) {
        System.out.println(list[i]);
        
        try {       
            String msisdn = list[i];
            String userUnique = UmeUserDao.getUserUnique(msisdn, "msisdn","");                
            if (userUnique.equals(""))
                //sb.append(msisdn + "\n");
                stringNumber += msisdn + "<br>";
            else
                System.out.println("IPX user has been inside database: " + msisdn);
        }
        catch (Exception e) { 
            System.out.println("IPX: Exception: " + e); 
        }
    }
}
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

<div>
                    <% if (!submit.equals("")) { %>
                <span class="blue_14">
                        <br><br><%=stringNumber%>
                </span>     
                <% } %>
</div>      

</body>
</html>