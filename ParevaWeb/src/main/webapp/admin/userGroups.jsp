<%@include file="/WEB-INF/jspf/admin/usergroups.jspf"%>


<html>
<head>
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form method="post" action="userGroups.jsp">

<table cellspacing="0" cellpadding="4" border="0" width="98%">
<tr><td valign="top" align="center">

    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr>
	<td align="left" valign="bottom" class="big_blue"><%=lp.get(7)%></td>
	<td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>
    <table cellspacing="0" cellpadding="5" border="0" width="100%" class="tableview">
        <tr>
            <th align='middle' class="grey_10">&nbsp;</th>
            <th colspan="2" align='left'><%=lp.get(8)%></th>
            <th align='middle'><%=lp.get(9)%></th>
            <th align='middle'>Created</th>
            <th align='right'><%=lp.get(11)%></th>
        <tr>
<%

String bg2 = "#EEEEEE";
bgColor="#FFFFFF";
int indexCount = 1;

for (int i=0; i<list.size(); i++) {
    group = list.get(i);
    if (!group.getParent().equals("") && !group.getParent().equals("root")) continue;

    if (bgColor.equals("#DDDDDD")) bgColor="#EEEEEE";
    else bgColor="#DDDDDD";

%>

<tr>
	<td align='left' class="grey_10"><%=indexCount%></a></td> 	
	<td colspan="2" align='left'><a href="userGroupDetails.jsp?ugid=<%=group.getUnique()%>" class="small_blue"><%=group.getName()%></a></td>
	<td align='middle' class="grey_10"><%=System.getProperty(group.getDomain() + "_name")%></td>
        <td align='middle' class="grey_10"><%=group.getCreated()%></td>
	<td align='right' class="grey_10"><input type="checkbox" name="del_<%=group.getUnique()%>"></td>
</tr>
<% 	indexCount++;

    for (int k=0; k<list.size(); k++) {
        subgroup = list.get(k);
        if (!subgroup.getParent().equals(group.getUnique())) continue;

%>
<tr>
	<td bgcolor="<%=bg2%>" align='left' class="grey_10">&nbsp;&nbsp;<%=indexCount%></td>
        <td bgcolor="<%=bg2%>"><img src="/images/glass_dot.gif" width="10" height="1"></td>
	<td bgcolor="<%=bg2%>" align='left' class="grey_10">&nbsp;&nbsp;<a href="userGroupDetails.jsp?ugid=<%=subgroup.getUnique()%>" class="small_blue"><%=subgroup.getName()%></a></td>
	<td bgcolor="<%=bg2%>" align='middle' class="grey_10"><%=System.getProperty(subgroup.getDomain() + "_name")%></td>
        <td bgcolor="<%=bg2%>" align='middle' class="grey_10"><%=subgroup.getCreated()%></td>
	<td bgcolor="<%=bg2%>" align='right' class="grey_10"><input type="checkbox" name="del_<%=subgroup.getUnique()%>"></td>
</tr>
<%			indexCount++;
     }
} 
%>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr><td align="left" class="grey_11"><%=lp.get(12)%><br><br></td></tr>
	<tr>
		<td align="left" class="grey_11"><%=lp.get(13)%>&nbsp;<input type="text" name="addName" value="<%=addName%>" size="20">
		&nbsp;
		Parent:
		<select name="parent">
			<option value="root">Root</option>
		<% 	for (int k=0; k<list.size(); k++) {
                            group = list.get(k);
                            if (!group.getParent().equals("") && !group.getParent().equals("root")) continue;
                %>
			<option value="<%=group.getUnique()%>"><%=group.getName()%></option>
		<% }%>
			</select>
			&nbsp; 
		
		<%=lp.get(14)%> <select name="dUnique">
		<% 	for (int k=0; k<umesdc.getDomainList().size(); k++) {
                            umedomain = umesdc.getDomainList().get(k);
		%>
			<option value="<%=umedomain.getUnique()%>"><%=umedomain.getName()%></option>
		<% }%>
			</select>
			<br><bR>
		</td>		
	<td align="right" class="grey_11"><input type="submit" name="add" value="&nbsp;&nbsp;&nbsp;<%=lp.get(16)%>&nbsp;&nbsp;&nbsp;"></td>
        </tr>
	</table>
</td>
</tr>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td>
        <table cellspacing="0" cellpadding="0" border="0" width="100%">
        <tr>
            <td align="left" class="grey_11"><%=lp.get(15)%></td>
            <td align="right"><input type="submit" name="delete" value="<%=lp.get(17)%>"></td>
        </tr>
        </table>
</td></tr>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
			
</table>

</form>

</body>
</html>