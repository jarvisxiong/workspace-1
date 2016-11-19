<%@ page import="com.mixmobile.anyx.sdk.*, com.mixmobile.anyx.cms.*, java.util.*, java.text.*, java.sql.Connection, java.sql.ResultSet" %>

<%
//***************************************************************************************************
AnyxRequest aReq = new AnyxRequest(request);
String uid = aReq.getUserId();
String ugid = aReq.getUserGroup();
String anyxSrvc = aReq.getAnyxService();
String domain = aReq.getDomain();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "ISO-8859-1";
response.setContentType("text/html; charset=" + pageEnc);

String fileName = "pricegroups";
//***************************************************************************************************

String dm = Misc.getCookie("_MXMDOMAINUNIQUE", request);
if (dm.equals("")) { out.println("domain missing"); return; }

DecimalFormat df = new DecimalFormat("##0.##");

Connection con = DBHStatic.getConnection();
ResultSet rs = null;
ResultSet rs2 = null;
String sqlstr = "";
String statusMsg ="";
String[] props = null;

double p1 = 0;
double p2 = 0;
double p3 = 0;
double p4 = 0;
String p1unit = "";
String p2unit = "";
String p3unit = "";
String p4unit = "";


String save = aReq.get("save");

if (!save.equals("")) {
    
    int stat = 0;
    String pr = "";    
    String prcode = "";
    String prunit = "";
    
    pr = aReq.get("p1");    
    pr = Misc.replaceChars(pr, ",", ".");    
    
    prcode = aReq.get("p1unit").toUpperCase();    
    if (prcode.equals("EUR")) prunit = "&euro;";
    else if (prcode.equals("USD")) prunit = "&#36;";
    else if (prcode.equals("GBP")) prunit = "&pound;"; 
    else if (prcode.equals("SEK")) prunit = "Kr";
    else if (prcode.equals("DKK")) prunit = "Kr";
    else if (prcode.equals("ZAR")) prunit = "R";
    else if (prcode.equals("UGX")) prunit = "USh";
    else if (prcode.equals("KES")) prunit = "KSh";
    else if (prcode.equals("CREDIT")) prunit = "credit/s";
    
    sqlstr = "UPDATE domainPriceGroups SET aEndUserPrice='" + pr + "',aUnit='" + prunit + "',aUnitCode='" + prcode + "' WHERE aDomain='" + dm + "' AND aPriceGroup='1'";
    System.out.println(sqlstr);
    stat = DBHStatic.execUpdate(con, sqlstr);
    if (stat==0) {
        sqlstr = "INSERT INTO domainPriceGroups VALUES('" + Misc.generateUniqueId() + "','" + dm + "','1','" + pr + "','" + prunit + "','" + prcode + "')";
        DBHStatic.execUpdate(con, sqlstr);
    }
    
    pr = aReq.get("p2");    
    pr = Misc.replaceChars(pr, ",", "."); 
    
    /*
   prcode = aReq.get("p2unit").toUpperCase();    
    if (prcode.equals("EUR")) prunit = "&euro;";
    else if (prcode.equals("USD")) prunit = "&#36;";
    else if (prcode.equals("GBP")) prunit = "&pound;"; 
    else if (prcode.equals("SEK")) prunit = "Kr";
    */    
    sqlstr = "UPDATE domainPriceGroups SET aEndUserPrice='" + pr + "',aUnit='" + prunit + "',aUnitCode='" + prcode + "' WHERE aDomain='" + dm + "' AND aPriceGroup='2'";
    System.out.println(sqlstr);
    stat = DBHStatic.execUpdate(con, sqlstr);
    if (stat==0) {
        sqlstr = "INSERT INTO domainPriceGroups VALUES('" + Misc.generateUniqueId() + "','" + dm + "','2','" + pr + "','" + prunit + "','" + prcode + "')";
        DBHStatic.execUpdate(con, sqlstr);
    }
    
    pr = aReq.get("p3");    
    pr = Misc.replaceChars(pr, ",", ".");    
    
    /*
    prcode = aReq.get("p3unit").toUpperCase();    
    if (prcode.equals("EUR")) prunit = "&euro;";
    else if (prcode.equals("USD")) prunit = "&#36;";
    else if (prcode.equals("GBP")) prunit = "&pound;"; 
    else if (prcode.equals("SEK")) prunit = "Kr";
    */
    sqlstr = "UPDATE domainPriceGroups SET aEndUserPrice='" + pr + "',aUnit='" + prunit + "',aUnitCode='" + prcode + "' WHERE aDomain='" + dm + "' AND aPriceGroup='3'";
    stat = DBHStatic.execUpdate(con, sqlstr);
    if (stat==0) {
        sqlstr = "INSERT INTO domainPriceGroups VALUES('" + Misc.generateUniqueId() + "','" + dm + "','3','" + pr + "','" + prunit + "','" + prcode + "')";
        DBHStatic.execUpdate(con, sqlstr);
    }
    
    pr = aReq.get("p4");    
    pr = Misc.replaceChars(pr, ",", ".");    
    /*
    prcode = aReq.get("p4unit").toUpperCase();    
    if (prcode.equals("EUR")) prunit = "&euro;";
    else if (prcode.equals("USD")) prunit = "&#36;";
    else if (prcode.equals("GBP")) prunit = "&pound;"; 
    else if (prcode.equals("SEK")) prunit = "Kr";
    */
    sqlstr = "UPDATE domainPriceGroups SET aEndUserPrice='" + pr + "',aUnit='" + prunit + "',aUnitCode='" + prcode + "' WHERE aDomain='" + dm + "' AND aPriceGroup='4'";
    stat = DBHStatic.execUpdate(con, sqlstr);
    if (stat==0) {
        sqlstr = "INSERT INTO domainPriceGroups VALUES('" + Misc.generateUniqueId() + "','" + dm + "','4','" + pr + "','" + prunit + "','" + prcode + "')";
        DBHStatic.execUpdate(con, sqlstr);
    }
    
    InitCmsParameters.reloadDomainPriceGroups();
}

if (dm.equals("")) {
    Cookie[] cookies = request.getCookies();
    Cookie ck = null;
    if (cookies!=null) {
        for (int i=0; i<cookies.length; i++) {
            ck = cookies[i];
            if (ck!=null && ck.getName().equals("_MXMDOMAINUNIQUE")) { dm = ck.getValue(); break; }
        }
    }
}

if (dm.equals("null")) {
    Cookie ck = new Cookie("_MXMDOMAINUNIQUE", "");
    ck.setMaxAge(-1);
    ck.setPath("/");
    response.addCookie(ck);
}
else if (!dm.equals("")) {  
    Cookie ck = new Cookie("_MXMDOMAINUNIQUE", dm);
    ck.setMaxAge(-1);
    ck.setPath("/");
    response.addCookie(ck);
       
      
    sqlstr = "SELECT * from domainPriceGroups WHERE aDomain='" + dm + "'";    
    rs = DBHStatic.getRs(con, sqlstr);
    while (rs.next()) {
        if (rs.getInt("aPriceGroup")==1) { p1 = rs.getDouble("aEndUserPrice"); p1unit = rs.getString("aUnitCode"); }
        else if (rs.getInt("aPriceGroup")==2) { p2 = rs.getDouble("aEndUserPrice"); p2unit = rs.getString("aUnitCode"); }
        else if (rs.getInt("aPriceGroup")==3) { p3 = rs.getDouble("aEndUserPrice"); p3unit = rs.getString("aUnitCode"); }
        else if (rs.getInt("aPriceGroup")==4) { p4 = rs.getDouble("aEndUserPrice"); p4unit = rs.getString("aUnitCode"); }      
    }
    rs.close();
 
}

List dms = new ArrayList();
sqlstr = "SELECT domains.aDomainUnique,aName,aDefaultUrl,aClientUnique FROM domains INNER JOIN clientDomains ON clientDomains.aDomainUnique=domains.aDomainUnique "
        + " WHERE domains.aDomainUnique!='fjq32V44EqwaKUb' AND aActive='1'";
rs = DBHStatic.getRs(con, sqlstr);
while (rs.next()) {
    props = new String[2];
    props[0] = rs.getString("aDomainUnique");
    props[1] = rs.getString("aName");
    dms.add(props);
}

DBHStatic.closeConnection(con);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language="JavaScript">
function win(urlPath) {
    var winl = (screen.width-400)/2;
    var wint = (screen.height-800)/2;
    var settings = 'height=800,width=400,directories=no,resizable=yes,status=yes,scrollbars=yes,menubar=no,location=no,top=' + wint + ',left=' + winl;
    newWin = window.open(urlPath,'sim',settings);
    newWin.focus();
}
</script>
</head>
<body>

<form action="<%=fileName%>.jsp" method="post" name="form1">
<input type="hidden" name="ss" value="1">
<input type="hidden" name="dm" value="<%=dm%>">

<table cellspacing="0" cellpadding="0" border="0" width="95%">
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="big_blue"><b>Price Groups</b></td>	
            <td align="right" valign="bottom" class="status_red"><b><%=statusMsg%></b>&nbsp;&nbsp;<a href="index.jsp">Back</a></td>
    </tr>
    </table>
    
</td></tr>			
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

 <% if (!dm.equals("") && !dm.equals("null")) {  %>
    
<tr><td valign="top" align="left">
<br>
    <table cellspacing="0" cellpadding="12" border="0">     
    <tr>    
       <td align="left" valign="top" class="grey_12"><b>Domain Currency:</b></td>
       <td><img src="/images/glass_dot.gif" height="1" width="20"></td>
       <td align="left" valign="top" class="grey_12">
            <select name="p1unit">
                <option value="UGX" <% if (p1unit.equals("UGX")){%> selected <%}%>>UGX (USh)</option>
                <option value="KES" <% if (p1unit.equals("KES")){%> selected <%}%>>KES (KSh)</option>
                <option value="ZAR" <% if (p1unit.equals("ZAR")){%> selected <%}%>>RAND (R)</option>
                <option value="CREDIT" <% if (p1unit.equals("CREDIT")){%> selected <%}%>>Credits</option>
                <option value="EUR" <% if (p1unit.equals("EUR")){%> selected <%}%>>EUR (&euro;)&nbsp;&nbsp;&nbsp;&nbsp;</option>
                <option value="SEK" <% if (p1unit.equals("SEK")){%> selected <%}%>>SEK (Kr)</option>
                <option value="DKK" <% if (p1unit.equals("DKK")){%> selected <%}%>>DKK (Kr)</option>
                <option value="USD" <% if (p1unit.equals("USD")){%> selected <%}%>>USD (&#36;)</option>
                <option value="GBP" <% if (p1unit.equals("GBP")){%> selected <%}%>>GBP (&pound;)</option>
            </select>
       </td>   
    </tr>
    <tr>    
       <td align="left" valign="top" class="grey_12"><b>End User Price For Price Group 1:</b></td>
       <td><img src="/images/glass_dot.gif" height="1" width="20"></td>
       <td align="left" valign="top" class="grey_12">
            <input type="text" name="p1" value="<%=df.format(p1)%>" size="6">
            &nbsp;
            <%=p1unit%>
       </td>   
    </tr>
    <tr>    
       <td align="left" valign="top" class="grey_12"><b>End User Price For Price Group 2:</b></td>
       <td><img src="/images/glass_dot.gif" height="1" width="20"></td>
       <td align="left" valign="top" class="grey_12">
            <input type="text" name="p2" value="<%=df.format(p2)%>" size="6">
            &nbsp;
            <%=p1unit%>
       </td>   
    </tr>
    <tr>    
       <td align="left" valign="top" class="grey_12"><b>End User Price For Price Group 3:</b></td>
       <td><img src="/images/glass_dot.gif" height="1" width="20"></td>
       <td align="left" valign="top" class="grey_12">
            <input type="text" name="p3" value="<%=df.format(p3)%>" size="6">
            &nbsp;
            <%=p1unit%>
       </td>   
    </tr>
    <tr>    
       <td align="left" valign="top" class="grey_12"><b>End User Price For Price Group 4:</b></td>
       <td><img src="/images/glass_dot.gif" height="1" width="20"></td>
       <td align="left" valign="top" class="grey_12">
            <input type="text" name="p4" value="<%=df.format(p4)%>" size="6">
            &nbsp;
            <%=p1unit%>
       </td>   
    </tr>
    </table>
</td></tr>
<tr><td align="right"><input type="submit" name="save" value="&nbsp;&nbsp;Save&nbsp;&nbsp;"></td></tr>
<tr><td><br><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<% }%>

</table>

</form>
</body>
</html>