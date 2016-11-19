<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
String nbrs="";
String pendingcount="";
String stopsubscriptioncount="";
if(request.getAttribute("stop_pending")!=null)
   pendingcount=(String) request.getAttribute("stop_pending");
if(request.getAttribute("stop_subscription")!=null)
    stopsubscriptioncount=(String) request.getAttribute("stop_subscription");


%>



<html>
<head>
<script LANGUAGE="JavaScript">
function confirmSubmit() {
    var agree=confirm("Are you sure you wish to continue?");
    if (agree) return true;
    else return false ;
}
</script>

</head>
<body>
<%
if(!(pendingcount.equalsIgnoreCase("") && stopsubscriptioncount.equalsIgnoreCase("")))
{
%>

Stopped Pending Tickets<%=pendingcount%>
Stopped Subscription <%=stopsubscriptioncount%>

<%} else{%>
    <form action="" method="post" style="padding: 0px; margin: 0px;">
  
        <table cellspacing="0" cellpadding="0" border="0" width="99%">
<tr><td>   
        <table cellspacing="0" cellpadding="4" border="0" width="100%">
            <tr>
                <td align="left" valign="bottom" class="big_blue">Bulk Stop</td>
               
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

    </tr>
    <tr>
            <td colspan="2"><input type="submit" name="submit" value="Submit" style="width:200px;" onClick="return confirmSubmit();"></td>
    </tr>
   

    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

</table>
            
</form>            
<%}%>
</body>
</html>
