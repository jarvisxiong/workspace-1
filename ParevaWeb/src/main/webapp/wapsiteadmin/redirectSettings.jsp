<%@include file="/WEB-INF/jspf/coreimport.jspf"%>

<%
//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String uid = aReq.getUserId();
String ugid = aReq.getUserGroup();
String anyxSrvc = aReq.getAnyxService();
String adminGroup = aReq.getAdminGroup();
String domain = aReq.getDomain();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "ISO-8859-1";
response.setContentType("text/html; charset=" + pageEnc);

String fileName = "redirectSettings";
//***************************************************************************************************
String imageDir = System.getProperty("document_root") + "/images/javagames/promos/";


Connection con = DBHStatic.getConnection();
ResultSet rs = null;
ResultSet rs2 = null;
String sqlstr = "";
String statusMsg ="";
String[] props = null;
String domainlink = "";
Map params = null;
String domainunique="",defaulturl="";
List list = new ArrayList();

boolean foundDomain = false;
List dms = new ArrayList();
String domainsfrom=aReq.get("domains");
String defaulturlfrom=aReq.get("defaulturl");

String dm = aReq.get("dm");
String subpage = aReq.get("subpage");

String save = aReq.get("ss");

String cmd = aReq.get("cmd");
String dividerColor1 = "#000000";
String dividerColor2 = "#FFFFFF";
String redirectur="";
String domainre="";
String uniquere="";


sqlstr = "SELECT dm.aDomainUnique,dm.aDefaultUrl,rd.aRedirectUrl FROM domains dm,redirectsetting rd WHERE "
        + " dm.aDomainUnique=rd.aDomainUnique "
        + " AND dm.aActive='1' ";
        //+ " WHERE dm.aDomainUnique!='fjq32V44EqwaKUb' AND dm.aActive='1' AND ug.aActive='1' AND dm.aWapIps='1'";
if (!Anyxcms.isSystemAdmin(adminGroup)) sqlstr += " AND cd.aClientUnique='" + ugid + "'";
System.out.println(sqlstr);
rs = DBHStatic.getRs(con, sqlstr);
while (rs.next()) {
    props = new String[4];
    props[0] = rs.getString("aDomainUnique");
    props[1] = rs.getString("aDefaultUrl");
    props[2] = rs.getString("aRedirectUrl");
        dms.add(props);
    if (dm.equals(props[0])) foundDomain = true;
}

System.out.println("DOMAIN: " + dm);

if ("POST".equalsIgnoreCase(request.getMethod()))
{
redirectur=request.getParameter("redirecturl");
domainre=request.getParameter("domains");
uniquere=request.getParameter("defaulturl");
        
        

}

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script src="/lib/global_anyx.js" language="javascript"></script>
<script src="/lib/scriptaculous/prototype.js" language="javascript"></script>
<script src="/lib/scriptaculous/scriptaculous.js" language="javascript"></script>

<script language="JavaScript">
    var newwindow = '';
    var redurl='';
    var domains='';
    var defaulturl="";
    var changeurl="";
    function openwin(url) {

        var winl = (screen.width-400)/2;
        var wint = (screen.height-800)/2;
        var settings = 'height=800,width=400,directories=no,resizable=yes,status=yes,scrollbars=yes,menubar=no,location=no,top=' + wint + ',left=' + winl;
        newwindow=window.open(url,'sim',settings);
        newwindow.focus();
    }

    function win(url) {

        var winl = (screen.width-400)/2;
        var wint = (screen.height-800)/2;
        var settings = 'height=800,width=400,directories=no,resizable=yes,status=yes,scrollbars=yes,menubar=no,location=no,top=' + wint + ',left=' + winl;
        if (!newwindow.closed && newwindow.location) {
            newwindow.location.href = url;
        }
    }

    function win2(urlPath, loopID) {
        var winl = (screen.width-200)/2;
        var wint = (screen.height-100)/2;
        var settings = 'height=100,width=200,directories=no,resizable=no,status=no,scrollbars=no,menubar=no,location=no,top=' + wint + ',left=' + winl;
        
        var changeurls=document.getElementById("redirection_"+ loopID).value;
        urlPath+='&rdirect='+changeurls;
        editWin = window.open(urlPath,'mmsedit',settings);
        editWin.focus();
    }

    function form_submit(thisform) {
      
        return thisform.changeurl.value;
    }
    function myFunction(f) {
alert("This form is " + f.name + ".");
return true;
} 
   

</script>
<script language=JavaScript src="/lib/picker.js"></script>
</head>
<body>



<table cellspacing="0" cellpadding="0" border="0" width="95%">
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="big_blue"><b>Redirection Settings</b></td>
            <td align="right" valign="bottom" class="status_red"><b><%=statusMsg%></b>&nbsp;&nbsp;</td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<tr><td valign="top" align="left">
    <br>
    
</td></tr>


<% if (dms.size()>0) {  %>
<tr><td ><img src="/images/glass_dot.gif" width="1" height="6"></td></tr>
<tr><td valign="top" align="left">
     <table cellspacing="0" cellpadding="10" border="0" class="tableview" width="100%">
        <tr>
             <th width="200" align="left" class="grey_12"><nobr><b>DomainUnique</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></th>
            <th width="200" align="left" class="grey_12"><nobr><b>Domains</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></th>
            <th width="200" align="left" class="grey_12"><b>Current Redirection Link</b></th>
            <th width="200" align="left" class="grey_12"><b>Change To Redirection Link</b></th>
            
            <th width="80">&nbsp;</th>

            <th>&nbsp;</th>
        </tr>
    </table>
<!--       <form name="redirectform">-->
    <ul id="itemlist" style="list-style-type: none; padding: 0; margin: 0;">
<%
      String bgcolor = "";

    for (int i=0; i<dms.size(); i++) {
        props = (String[]) dms.get(i);
    
%>
  <li id="item_<%=props[0]%>">
      
    <table cellspacing="0" cellpadding="10" border="0" class="tableview" width="100%">
         
        <tr bgcolor="<%=bgcolor%>">
            <td width="150" align="left" class="grey_11"><input type="text"  id="domains" name="domains" value="<%=props[0]%>"></td>
            <td width="160" align="left" class="grey_11"><input type="text"  name="defaulturl" value="<%=props[1]%>" size="30"></td>
            <td width="160" align="left" class="grey_11"><input type="text"  name="redirecturl" value="<%=props[2]%>" size="30"></td>
            <td width="160" align="left" class="grey_11"> <input type="text" id="redirection_<%= i %>" name="redirection" value=" "></td>
                
            <td align="right"><nobr>&nbsp;&nbsp;<a href="javascript:win2('updateredirect.jsp?domains=<%=props[0]%>&defaulturl=<%=props[1]%>&redirecturl=<%=props[2]%>',<%=i%>)"><span class="blue_11">[Update]</span></a></nobr></td>
    
           
        </tr>
    </table>
  </li>
    <% } %>
</ul>
<!--</form>-->
</td></tr>
<tr><td><br><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<% }%>

</table>
Domain Unique: <%=domainsfrom%>
Default URL : <%=defaulturlfrom%>
Redirect URL :<%=redirectur%>


</body>
</html>

<% DBHStatic.closeConnection(con); %>