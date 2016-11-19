<%@include file="/WEB-INF/jspf/admin/adduser.jspf"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form method="post" action="adduser.jsp" name="thisForm">
<input type="hidden" name="search" value="<%=search%>">
<input type="hidden" name="sstr" value="<%=sstr%>">
<input type="hidden" name="stype" value="<%=stype%>">
<input type="hidden" name="index" value="<%=index%>">

<table cellspacing="0" cellpadding="0" border="0" width="98%">
    
<% if (forceCreate) { %>
	<input type="hidden" name="aw" value="11">
<% } %>

<tr>
<td valign="top" align="center">

<table cellspacing="0" cellpadding="5" border="0" width="95%">

<tr>
	<td colspan="<%=cs-2%>" align="left" valign="bottom" class="big_blue">Add Users</td>	
	<td colspan="<%=cs-2%>" align="right" valign="bottom"><a href="users.jsp"><%=lp.get(18)%></a></td>	
</tr>

<tr><td colspan="<%=cs%>"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr>
	<td colspan="<%=cs-3%>"><%=lp.get(19)%></td>
	<td colspan="<%=cs-1%>">
            <select name="dUnique" onChange="window.document.thisForm.submit();">
		<option value=""><%=lp.get(20)%>&nbsp;</option>
		<%
		for (int i=0; i<umesdc.getDomainList().size(); i++) {
			umedomain = umesdc.getDomainList().get(i);
                %>
			<option value="<%=umedomain.getUnique()%>" <% if (dmid.equals(umedomain.getUnique())) { %> selected <%}%>>
                            <%=umedomain.getName()%>&nbsp;&nbsp;</option>
		<%}%>
        </select>
	</td>
</tr>

<tr><td colspan="<%=cs%>"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr>
	<td colspan="<%=cs%>">&nbsp;</td>
</tr>
<tr>
	<td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align="left"><%=lp.get(21)%></td>	
	<td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align="left"><input type="text" size="20" name="aFirstname" value="<%=firstName%>"></td>		
	<td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align='left'><%=lp.get(22)%></td> 	
	<td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align='left'><input type="text" size="20" name="aLastname" value="<%=lastName%>"></td> 
</tr>
<tr>
	<td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align='left'><%=lp.get(23)%></td> 	
	<td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align='left'><input type="text" size="20" name="aMobile1" value="<%=mobile1%>"></td> 
	<td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align="left"><%=lp.get(34)%></td>
	<td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align="left">

            <SELECT NAME="aPrefLang">
	<% for (int i=0; i<umesdc.getLanguageList().size(); i++) {
		sdclang = umesdc.getLanguageList().get(i);
	%>
		<OPTION VALUE="<%=sdclang.getLanguageCode()%>" <% if (prefLang.equals(sdclang.getLanguageCode())) {%> SELECTED <%}%>>
                    <%=sdclang.getLanguageName()%>&nbsp;&nbsp;&nbsp;</OPTION>
	<% } %>
		</SELECT>

	</td>	
</tr>		
<tr>
	<td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align="left"><%=lp.get(26)%></td>	
	<td colspan="3" bgcolor="<%=bgColor%>" align="left">
		<% if (dmid.equals("")) {%> 
			<select name="aUserGroup" disabled>
			<option value=""><%=lp.get(27)%>&nbsp;</option>
                        </select>
		<% } else { %>
                <select name="aUserGroup">
                    <%
                    for (int i=0; i<umesdc.getUserGroupList().size(); i++) {
                        umegroup = umesdc.getUserGroupList().get(i);
                        if (!umegroup.getDomain().equals(dmid)) continue;
                    %>
                            <option value="<%=umegroup.getUnique()%>" <% if (userGroup.equals(umegroup.getUnique())) { %> selected <%}%>>
                                <%=umegroup.getName()%>&nbsp;&nbsp;</option>
                    <%}%>
		
                </select>
                <%}%>
	</td>		
			
</tr>
<tr>
	<td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align='left'><%=lp.get(33)%></td> 	
	<td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align='left'>
		<select name="aSecLevel">
		<% for (int i=1; i<10; i++) { %>
			<option value="<%=i%>" 
			<% if (secLevel==i) { %> selected <% } %>
			><%=i%>&nbsp;&nbsp;&nbsp;&nbsp;</option>		
		<% } %>
		</select>
        </td>
        <td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align="left"><%=lp.get(28)%></td>
	<td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align="left">
            <select name="aAdminGroup">
                    <option value="0" <% if (adminGroup==0) { %> selected <% } %> ><%=lp.get(29)%>&nbsp;&nbsp;&nbsp;&nbsp;</option>
                    <option value="5" <% if (adminGroup==5) { %> selected <% } %> ><%=lp.get(30)%></option>
                    <option value="6" <% if (adminGroup==6) { %> selected <% } %> ><%=lp.get(31)%></option>
                    <option value="9" <% if (adminGroup==9) { %> selected <% } %> ><%=lp.get(32)%></option>
            </select>
        </td>
	
</tr>

<tr>
	<td colspan="<%=cs-3%>" bgcolor="<%=bgColor%>" align="left"><%=lp.get(35)%></td>	
	<td colspan="<%=cs-1%>" bgcolor="<%=bgColor%>" align="left"><input type="text" size="60" name="aEmail" value="<%=email%>"></td>		
</tr>
<tr>
	<td colspan="<%=cs-2%>" bgcolor="<%=bgColor%>" align="left"><%=lp.get(36)%></td>	
	<td colspan="<%=cs-2%>" bgcolor="<%=bgColor%>" align="left">
		<input type="checkbox" name="byEmail" <% if (byEmail.equals("1")) { %> checked <% } %> value="1"><%=lp.get(37)%> &nbsp;&nbsp;&nbsp;
		<input type="checkbox" name="bySms" <% if (bySms.equals("1")) { %> checked <% } %> value="1"><%=lp.get(38)%></td>	
</tr>
<tr>	
	<td colspan="<%=cs-1%>" align="right">&nbsp;<font size="1" color="Blue"><%=statusMsg%></font></td>
	<td colspan="<%=cs-3%>" align="right"><input type="submit" name="save" value="<%=lp.get(40)%>"></td>	
</tr>
<tr>	
	<td colspan="<%=cs%>" align="left" valign="top" class="error"><%=error%></td>
</tr>
<tr><td colspan="<%=cs%>"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
	
</table> 


</td></tr>				
</table>

</form>

</body>
</html>


