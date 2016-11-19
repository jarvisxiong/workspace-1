<%@include file="/WEB-INF/jspf/admin/smsgateways.jspf"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script src="/lib/global_anyx.js" language="javascript"></script>
<script src="/lib/scriptaculous/prototype.js" language="javascript"></script>
<script src="/lib/scriptaculous/scriptaculous.js" language="javascript"></script>
<script type="text/javascript" language="javascript">
        
    function submitform(src, form) {
        if (src=="sort") form.cat.value="";
        form.submit();
    }
    function submitForm2 (thisForm, ind) {
        thisForm.si.value=ind;
        thisForm.submit();
    }
    
</script>

</head>
<body>
    
<form action="<%=fileName%>.jsp" method="post" name="form1">


<table cellspacing="0" cellpadding="0" border="0" width="99%">
<tr><td valign="top" align="left">
    
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
        <tr>
            <td align="left" valign="bottom" class="big_blue">SMS Accounts</td>
            <td align="right" class="status_red"><b><%=statusMsg%></b></td>
        </tr>
    </table>
    
</tr></td>   
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

    <table cellpadding="6" cellspacing="0" border="0" width="100%">
    <tr>
        <td class="grey_11"><b>Add New Gateway</b> 
            &nbsp;&nbsp; </td>
    <td class="grey_11">Name:&nbsp;&nbsp;<input type="text" size="50" name="name" value="<%=name%>">
    </td>
    <td align="right"><input type="submit" name="add" value="&nbsp;ADD NEW&nbsp;"></td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<tr><td><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>

<tr><td>        
        
    <br>
    <table cellpadding="4" cellspacing="0" border="0" width="100%" class="dotted">

        <tr>

          <th align="left">Gateway Name</th>
          <th align="center">Default</th>
          <th align="center">Ip Address</th>  
          <th align="center">Port</th>
          <th align="center">Connection Pool</th>  
          <th align="right">Created</th>
       </tr>
<%
    for (int i=0; i<umesdc.getSmsGatewayList().size(); i++) {
        item = umesdc.getSmsGatewayList().get(i);
        
%>
        
    <tr>

          <td align="left"><a href="gatewayDetails.jsp?unq=<%=item.getUnique()%>"><%=item.getName()%></a></td>
          <td align="center"><%=item.getIsDefault()%></td>
          <td align="center"><%=item.getIp()%></td> 
          <td align="center"><%=item.getPort()%></td> 
          <td align="center"><%=item.getConnectionPool()%></td> 
          <td align="right"><%=item.getCreated()%></td>
       </tr>

<%
   }
%>
       
   </table>
   
</td></tr>
</table>

</form>

</body>
</html>

