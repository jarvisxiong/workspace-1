<%@ include file="/WEB-INF/jspf/wapsiteadmin/xhtml.jspf" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language=JavaScript src="/publicresources/javascript/picker.js"></script>
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
<table cellspacing="0" cellpadding="0" border="0" width="95%">
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="5" border="0">
    <tr>
        <td align="left" valign="bottom" class="big_blue"><b>Xhtml Profiles</b></td>	
            <td align="left" valign="bottom" class="status_red"><b><%=statusMsg%></b>&nbsp;&nbsp;</td>
    </tr>
    </table>
    
</td></tr>			
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

 <% if (list.size()>0) {  %>
    
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="6" border="0" class="tableview" width="100%"> 
    <tr>    
        <th align="left" class="grey_12"><nobr><b>Profile</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></th>	
        <th align="center" class="grey_12"><b>From Screen Size</b></th>
        <th align="center" class="grey_12"><b>To Screen Size</b></th>
        <th align="center" class="grey_12"><b>Full Width</b></th>
        <th align="center" class="grey_12"><b>Screenshot Width</b></th>
    </tr>

<%
            for (int i=0; i<list.size(); i++) {
                props = (String[]) list.get(i);
    %>
    <tr>    
        <td align="left" class="grey_11">&nbsp;&nbsp;<%=props[1]%></td>	
        <td align="center" class="grey_12"><input type="text" name="scs1_<%=props[0]%>" size=6 value="<%=props[2]%>"></td>
        <td align="center" class="grey_12"><input type="text" name="scs2_<%=props[0]%>" size=6 value="<%=props[3]%>"></td>
        <td align="center" class="grey_12"><input type="text" name="full_<%=props[0]%>" size=6 value="<%=props[4]%>"></td>
        <td align="center" class="grey_12"><input type="text" name="shot_<%=props[0]%>" size=6 value="<%=props[5]%>"></td>
      
    </tr>
    
    <%
            }
    
    %>
    </table>
</td></tr>
<tr><td><br><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
<tr><td align="right"><input type="submit" name="save" value="&nbsp;&nbsp;Save&nbsp;&nbsp;"></td></tr>
<tr><td><br><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<% }%>

</table>

</form>
</body>
</html>