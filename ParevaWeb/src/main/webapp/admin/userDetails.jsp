<%@include file="/WEB-INF/jspf/admin/userdetails.jspf"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<table cellspacing="0" cellpadding="0" border="0" width="100%">
<tr>
<td valign="top" align="left">

<table cellspacing="0" cellpadding="5" border="0" width="98%">

<form method="post" action="userDetails.jsp">
<input type="hidden" name="search" value="<%=search%>">
<input type="hidden" name="sstr" value="<%=sstr%>">
<input type="hidden" name="stype" value="<%=stype%>">
<input type="hidden" name="index" value="<%=index%>">
<input type="hidden" name="uid" value="<%=uid%>">

<tr>
	<td colspan="<%=cs-2%>" align="left" valign="bottom" class="big_blue">User Details</td>
	<td colspan="<%=cs-2%>" align="right" valign="bottom" class="status"><%=statusMsg1%><br><a href="users.jsp?<%=params%>"><%=lp.get(14)%></a></td>
</tr>
<tr bgcolor="<%=bgColor%>">
	<td colspan="<%=cs-3%>" align="left"><%=lp.get(15)%></td>	
	<td colspan="<%=cs-3%>" align="left"><b><%=user.getUnique()%></b></td>
	<td colspan="<%=cs-3%>" align="left"><%=lp.get(16)%></td>	
	<td colspan="<%=cs-3%>" align="left"><b><%=System.getProperty(user.getDomain() + "_name")%></b></td>
</tr>
<tr bgcolor="<%=bgColor%>">
	<td colspan="<%=cs-3%>" align="left"><%=lp.get(17)%></td>	
	<td colspan="<%=cs-3%>" align="left"><input type="text" size="20" name="aLogin" Value="<%=user.getLogin()%>"></td>
	<td colspan="<%=cs-3%>" align="left"><%=lp.get(18)%></td>
	<td colspan="<%=cs-3%>" align="left"><input type="checkbox" name="aActive" 
		<% if (user.getActive()==1) {%> checked <% } %>
	 	value="1"></td>
</tr>
<tr bgcolor="<%=bgColor%>">
	<td colspan="<%=cs-3%>" align="left"><%=lp.get(19)%></td>	
	<td colspan="<%=cs-3%>" align="left"><input type="text" size="20" name="aFirstName" Value="<%=user.getFirstName()%>"></td>
	<td colspan="<%=cs-3%>" align="left"><%=lp.get(20)%></td>	
	<td colspan="<%=cs-3%>" align="left"><input type="text" size="20" name="aLastName" Value="<%=user.getLastName()%>"></td>
</tr>
<tr bgcolor="<%=bgColor%>">
	<td colspan="<%=cs-3%>" align="left"><%=lp.get(21)%></td>	
	<td colspan="<%=cs-3%>" align="left"><input type="text" size="20" name="aMobile" Value="<%=user.getMobile()%>"></td>
	<td colspan="<%=cs-3%>" align='left'><%=lp.get("nickname")%></td>
	<td colspan="<%=cs-3%>" align="left"><input type="text" size="20" name="aNickName" Value="<%=user.getNickName()%>"></td>
</tr>
<tr bgcolor="<%=bgColor%>">
        <td align="left"><%=lp.get(34)%></td>
	<td align="left"><input type="text" size="5" name="aCredits" Value="<%=user.getCredits()%>">
        &nbsp;&nbsp;&nbsp;
	<input type="checkbox" name="aGrpCred" value="1" <% if (user.getUseGroupCredits()==1){ %> checked <% } %>>&nbsp;<%=lp.get(50)%>
        </td>
        <td align="left">Wap ID:</td>
	<td align="left"><input type="text" size="20" name="aWapId" Value="<%=user.getWapId()%>"></td>


</tr>
<tr bgcolor="<%=bgColor%>">
    
	<td colspan="<%=cs-3%>" align="left"><%=lp.get(24)%></td>	
	<td colspan="<%=cs-3%>" align="left">
		<select name="aUserGroup">
		<option value=""><%=lp.get(25)%>&nbsp;</option>
		<% 
		for (int i=0; i<anyxsdc.getUserGroupList().size(); i++) {
			sdcgroup = anyxsdc.getUserGroupList().get(i);
                        if (!sdcgroup.getDomain().equals(user.getDomain())) continue;
                %>
			<option value="<%=sdcgroup.getUnique()%>" <% if (user.getUserGroup().equals(sdcgroup.getUnique())) { %> selected <%}%>>
                            <%=sdcgroup.getName()%>&nbsp;&nbsp;</option>
		<%}%>
		</select>
        </td>
	<td colspan="<%=cs-3%>" align="left"><%=lp.get(26)%></td>	
	<td colspan="<%=cs-3%>" align="left">
		<select name="aAdminGroup">
			<option value="0" <% if (user.getAdminGroup()==0) { %> selected <% } %> ><%=lp.get(27)%>&nbsp;&nbsp;&nbsp;&nbsp;</option>
			<option value="5" <% if (user.getAdminGroup()==5) { %> selected <% } %> ><%=lp.get(28)%></option>
			<option value="6" <% if (user.getAdminGroup()==6) { %> selected <% } %> ><%=lp.get(29)%></option>
			<option value="9" <% if (user.getAdminGroup()==9) { %> selected <% } %> ><%=lp.get(30)%></option>
		</select></td>			
</tr>
<tr bgcolor="<%=bgColor%>">
	<td colspan="<%=cs-3%>" align='left'><%=lp.get(31)%></td> 	
	<td colspan="<%=cs-3%>" align='left'>
		<select name="aSecLevel">
		<% for (int i=1; i<10; i++) { %>
			<option value="<%=i%>" 
			<% if (user.getSecLevel()==i) { %> selected <% } %>
			><%=i%>&nbsp;&nbsp;&nbsp;&nbsp;</option>		
		<% } %>
		</select></td>	
	<td colspan="<%=cs-3%>" align="left"><%=lp.get(32)%></td>	
	<td colspan="<%=cs-3%>" align="left">
		<SELECT NAME="aPrefLang">
	<% for (int i=0; i<anyxsdc.getLanguageList().size(); i++) {
		sdclang = anyxsdc.getLanguageList().get(i);
	%>
		<OPTION VALUE="<%=sdclang.getLanguageCode()%>" <% if (user.getLanguage().equals(sdclang.getLanguageCode())) {%> SELECTED <%}%>>
                    <%=sdclang.getLanguageName()%>&nbsp;&nbsp;&nbsp;</OPTION>
	<% } %>
		</SELECT>
	</td>	
</tr>
<tr bgcolor="<%=bgColor%>">
	<td colspan="<%=cs-3%>" align="left">Country:</td>
	<td colspan="<%=cs-1%>" align="left"><input type="text" size="20" name="aCountry" Value="<%=ud.getCountry()%>"></td>
</tr>

<tr bgcolor="<%=bgColor%>">
	<td colspan="<%=cs-3%>" align="left"><%=lp.get(33)%></td>
	<td colspan="<%=cs-1%>" align="left"><input type="text" size="80" name="aEmail" Value="<%=user.getEmail()%>"></td>
</tr>
<tr bgcolor="<%=bgColor%>">
	<td colspan="<%=cs-3%>" align="left">Active Hash:</td>
	<td colspan="<%=cs-1%>" align="left"><input type="text" size="80" name="aActiveClubCode" Value="<%=user.getActiveClubCode()%>"></td>
</tr>
<tr bgcolor="<%=bgColor%>">
	
	<td align="left"><%=lp.get(37)%></td>
	<td colspan="3" align="left"><%=user.getCreated()%></td>
</tr>
<tr bgcolor="<%=bgColor%>">

	<td align="left">SNP Profile</td>
	<td colspan="3" align="left"><a href="http://snp.mixem.com?id=<%=user.getWapId()%>" target="_blank">Open in a new window</a></td>
</tr>

<tr>	
	<td colspan="<%=cs%>" align="right">
<% if (dataSaved) { %> <font size="1" color="Blue"><%=lp.get(38)%></font>&nbsp;&nbsp; <% } %>
	<input type="submit" name="save" value="<%=lp.get(42)%>"></td>	
</tr>	
</form>


<tr><td colspan="<%=cs%>"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr>
	<td colspan="<%=cs-2%>" align="left"><%=lp.get(39)%></td>
	<td colspan="<%=cs-2%>" align="right" class="status">&nbsp;<%=statusMsg2%></td>
</tr>

<form method="post" action="userDetails.jsp">
<input type="hidden" name="search" value="<%=search%>">
<input type="hidden" name="sstr" value="<%=sstr%>">
<input type="hidden" name="stype" value="<%=stype%>">
<input type="hidden" name="index" value="<%=index%>">
<input type="hidden" name="uid" value="<%=uid%>">

<tr bgcolor="<%=bgColor%>">
	<td colspan="4" align="left">
		&nbsp;&nbsp;<input type="radio" name="p3type" value="0" 
		<% if (pwdType.equals("0") || pwdType.equals("")) {%> checked <%}%> ><%=lp.get(40)%>
		&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="p3type" value="1" 
		<% if (pwdType.equals("1")) {%> checked <%}%> >&nbsp;
		<input type="password" name="p3" value="" size="15">
	</td>
</tr>
<tr bgcolor="<%=bgColor%>">
	<td colspan="<%=cs-2%>" align="left">
		&nbsp;&nbsp;<input type="checkbox" name="sendEmail" <% if (sendEmail.equals("1")) {%> checked <% } %> value="1">
		Send Email
		<br>
		&nbsp;&nbsp;<input type="checkbox" name="sendSms" <% if (sendSms.equals("1")) {%> checked <% } %> value="1">
		Send SMS
		<br>
		&nbsp;&nbsp;<input type="checkbox" name="sh_on_sc" <% if (showOnScreen.equals("1")) {%> checked <% } %> value="1">
		Show On Screen
	</td>
	<td colspan="<%=cs-2%>" align="right">	
		<input type="submit" name="resetPwd" value="<%=lp.get(43)%>">
	</td>	
</tr>
</form>
<tr><td colspan="<%=cs%>">&nbsp;</td>
</tr>
<tr><td colspan="<%=cs%>"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

</table> 
<br><br>
</td>
</tr>				
</table>	

</body>
</html>