<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Calendar"%>
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


String sqlstr = "";

String nbrs = aReq.get("msisdn");
String submit = aReq.get("submit");
//List<String>serviceName=new ArrayList<String>();

Calendar c = new GregorianCalendar();
int nowHour = c.get(Calendar.HOUR_OF_DAY);
int nowMinute = c.get(Calendar.MINUTE);

c.add(Calendar.HOUR, -11);
String times=c.getTime().toString();
Calendar cal=new GregorianCalendar();
SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String dates=sdf2.format(cal.getTime());


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

  
 
                Current Date and Time: <%=dates%><br><br>
                Current Hour: <%=nowHour%><br><br>
                Current Minute: <%=nowMinute%><br><br>
                Before 11 hour: <%=times%><br><br> 
              
            
              
                    
                    

</body>
</html>


