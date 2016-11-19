<%@include file="/WEB-INF/jspf/admin/genSettings.jspf"%>

<html>
<head>
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>


<form method="post" action="<%=fileName%>.jsp">

<table cellspacing="0" cellpadding="5" border="0" width="99%">
<tr>
	<td align="left" valign="bottom" class="big_blue">General Settings</td>
	<td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
</tr>



<tr><td colspan="<%=cs%>"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td colspan="<%=cs%>"><%=lp.get(3)%></td></tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(4)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_document_root" value="<%=System.getProperty("document_root")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(31)%></td>
<td bgcolor="<%=bgColor%>" align="left">
    <select name="param_default_domain">
    <%
        String defDomain = (String) System.getProperty("default_domain");
        if (defDomain==null) defDomain = "";

        for (int i=0; i<domains.size(); i++) {
            domain = domains.get(i);
    %>
    <option value="<%=domain.getUnique()%>" <% if (defDomain.equals(domain.getUnique())){%> selected <%}%>><%=domain.getName()%></option>
    <% } %>
    </select>
</td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(11)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_sms_account" value="<%=System.getProperty("sms_account")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left">Default SMS account 2</td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_sms_account2" value="<%=System.getProperty("sms_account2")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(5)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_smtp_server" value="<%=System.getProperty("smtp_server")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(6)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_smtp_default_from" value="<%=System.getProperty("smtp_default_from")%>"></td>
</tr>

<tr><td colspan="<%=cs%>"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td colspan="<%=cs%>"><%=lp.get(7)%></td></tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(8)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_mxmgwLogin" value="<%=System.getProperty("mxmgwLogin")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(9)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="password" size="50" name="param_mxmgwPw" value="<%=System.getProperty("mxmgwPw")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(10)%></td>
<td bgcolor="<%=bgColor%>" align="left">
    <select name="param_mxmgwEncPw">
        <option value="" <% if (System.getProperty("mxmgwEncPw")==null || System.getProperty("mxmgwEncPw").equals("")){%> selected <%}%>>[Select]</option>
        <option value="1" <% if (System.getProperty("mxmgwEncPw")!=null && System.getProperty("mxmgwEncPw").equals("1")){%> selected <%}%>>Yes, encrypt</option>
        <option value="0" <% if (System.getProperty("mxmgwEncPw")!=null && System.getProperty("mxmgwEncPw").equals("0")){%> selected <%}%>>No, do not encrypt</option>
    </select>
</tr>

<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(12)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_mxmgwIpAddress" value="<%=System.getProperty("mxmgwIpAddress")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(13)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_mxmgwPort" value="<%=System.getProperty("mxmgwPort")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(14)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_mxmgwTimeout" value="<%=System.getProperty("mxmgwTimeout")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(15)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_mxmgwConnections" value="<%=System.getProperty("mxmgwConnections")%>"></td>
</tr>

<tr><td colspan="<%=cs%>"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td colspan="<%=cs%>"><%=lp.get(20)%></td></tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(21)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_logoutPage" value="<%=System.getProperty("logoutPage")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(22)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_generalError" value="<%=System.getProperty("generalError")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(23)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_sessionExpired" value="<%=System.getProperty("sessionExpired")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(24)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_serviceAuthnFailed" value="<%=System.getProperty("serviceAuthnFailed")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(25)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_serviceTimeout" value="<%=System.getProperty("serviceTimeout")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(26)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_serviceNotOk" value="<%=System.getProperty("serviceNotOk")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(27)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_notAvailable" value="<%=System.getProperty("notAvailable")%>"></td>
</tr>

<tr><td colspan="<%=cs%>"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td colspan="<%=cs%>"><%=lp.get(28)%></td></tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left"><%=lp.get(29)%></td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_serviceTemplates" value="<%=System.getProperty("serviceTemplates")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left">Template Directory</td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_template_directory" value="<%=System.getProperty("template_directory")%>"></td>
</tr>
<tr>
<td bgcolor="<%=bgColor%>" align="left">Template Type</td>
<td bgcolor="<%=bgColor%>" align="left"><input type="text" size="50" name="param_template_type" value="<%=System.getProperty("template_type")%>"></td>
</tr>

<tr><td colspan="<%=cs%>"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<tr><td colspan="<%=cs%>" align="right"><input type="submit" name="save" value="<%=lp.get(30)%>"></td></tr>

</table>
</form>
</body>
</html>