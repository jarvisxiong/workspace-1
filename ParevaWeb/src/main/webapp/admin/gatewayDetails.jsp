<%@include file="/WEB-INF/jspf/admin/gatewayDetails.jspf"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script src="http://cdn.jquerytools.org/1.2.4/full/jquery.tools.min.js"></script>

<script type="text/javascript" src="/lib/jquery/jquery.ui/jquery-ui-1.8.2.custom.min.js"></script>
<link rel="stylesheet" href="/lib/jquery/jquery.ui/css/smoothness/jquery-ui-1.8.2.custom.css" media="screen"/>

<script type="text/javascript" src="/lib/jquery/timepicker/jquery-ui-timepicker-addon.js"></script>
<link rel="stylesheet" href="/lib/jquery/timepicker/timepicker.css" media="screen"/>

<script>
    
$(function() {
    $("#schedule").datetimepicker({
        dateFormat: "dd.mm.yy",
        stepMinute: 1
    });
});


</script>
</head>
<body>

<form action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="unq" value="<%=unique%>">
    
<table cellspacing="0" cellpadding="0" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="blue_14"><b>Sms Gateway Details</b></td>
                <td align="right" valign="bottom" class="status">
            <a href="smsgateways.jsp">Back</a>
        </td>
    </tr>
    </table>    
</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td valign="top" align="left">      
    

    <table cellspacing="0" cellpadding="10" border="0" >
     
    <tr>
            <td>Gateway Type:<br></td>
            <td align="left">
                <select name="type">
                    <option value="">[Select]</option>
                    <option value="mxmgw" <% if (item.getType().equals("mxmgw")){%> selected <%}%>>Mixem Gateway</option>
                    <option value="utl_billing" <% if (item.getType().equals("utl_billing")){%> selected <%}%>>UTL Billing</option>
                    <option value="yo_billing" <% if (item.getType().equals("yo_billing")){%> selected <%}%>>Yo Billing</option>
                    <option value="ipx" <% if (item.getType().equals("ipx")){%> selected <%}%>>Ericsson IPX</option>
                    <option value="ipx_billing" <% if (item.getType().equals("ipx_billing")){%> selected <%}%>>Ericsson IPX Billing</option>
                    <option value="sts_za" <% if (item.getType().equals("sts_za")){%> selected <%}%>>STS South Africa</option>
                    <option value="sts_ke" <% if (item.getType().equals("sts_ke")){%> selected <%}%>>STS Kenya</option>
                    <option value="tanla" <% if (item.getType().equals("tanla")){%> selected <%}%>>Tanla</option>
                    
                </select>
            </td>
            <td rowspan="13" valign="top">SMS Accounts:</td>
            <td rowspan="13" valign="top">
                <textarea name="accounts" style="width:200px; height:400px;"><%=item.getAccounts()%></textarea>
            </td>    
    </tr>
        
    <tr>
            <td>Gateway Name:<br></td>
            <td align="left"><input type="text" name="name" style="width:200px;" value="<%=item.getName()%>" class="textbox"></td>
    </tr>
    <tr>
            <td>Is Default:<br></td>
            <td align="left">
                <input type="checkbox" name="isdefault" value="1" <% if (item.getIsDefault()==1){%> checked <%}%>>
            </td> 
    </tr>   
    <tr>
            <td>Msisdn Format:<br></td>
            <td align="left">
                <select name="msisdnformat">
                    <option value="0">[Select]</option>
                    <option value="2" <% if (item.getMsisdnFormat()==2){%> selected <%}%>>+358xxx</option>
                    <option value="4" <% if (item.getMsisdnFormat()==4){%> selected <%}%>>358xxx</option>
                    <option value="3" <% if (item.getMsisdnFormat()==3){%> selected <%}%>>00358xxx</option>                    
                </select>
            </td>
    </tr>
    <tr>
            <td>Host / Ip Address:<br></td>
            <td align="left"><input type="text" name="ip" style="width:200px;" value="<%=item.getIp()%>" class="textbox"></td>
    </tr>
    <tr>
            <td>Port:<br></td>
            <td align="left"><input type="text" name="port" style="width:50px;" value="<%=item.getPort()%>" class="textbox"></td>
    </tr>
    <tr>
            <td>Http Path:<br></td>
            <td align="left"><input type="text" name="httppath" style="width:200px;" value="<%=item.getHttpPath()%>" class="textbox"></td>
    </tr>
     <tr>
            <td>Http Path 2:<br></td>
            <td align="left"><input type="text" name="httppath2" style="width:200px;" value="<%=item.getHttpPath2()%>" class="textbox"></td>
    </tr>
    <tr>
            <td>Login:<br></td>
            <td align="left"><input type="text" name="login" style="width:200px;" value="<%=item.getLogin()%>" class="textbox"></td>
    </tr>
    <tr>
            <td>Password:<br></td>
            <td align="left"><input type="text" name="password" style="width:200px;" value="<%=item.getPassword()%>" class="textbox"></td>
    </tr>
    <tr>
            <td>Encrypt Password<br></td>
            <td align="left"><input type="checkbox" name="encryptpw" value="1" <% if (item.getEncryptPassword()==1){%> checked <%}%>></td>
    </tr>
    <tr>
            <td>Connection Pool Size:<br></td>
            <td align="left"><input type="text" name="conpool" style="width:50px;" value="<%=item.getConnectionPool()%>" class="textbox"></td>
    </tr>
    <tr>
            <td>Timeout:<br></td>
            <td align="left"><input type="text" name="timeout" style="width:50px;" value="<%=item.getTimeout()%>" class="textbox"></td>
    </tr>
    
    <tr>
            <td>Created:<br></td>
            <td align="left"><%=item.getCreated()%></td> 
    </tr>
    <tr>
            <td>&nbsp;<br></td>
            <td align="left"><input type="submit" name="submit" value="Save" style="width:100px;"/>
            &nbsp;&nbsp;<span class="status"><%=statusMsg%></span>
            </td> 
    </tr>
    
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

</table>
    
    
</form>

</body>
</html>