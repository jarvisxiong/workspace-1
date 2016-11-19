<%@include file="/WEB-INF/jspf/admin/packages.jspf"%>
<html>
<head>
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form method="post" action="<%=fileName%>.jsp">

<table cellspacing="0" cellpadding="5" border="0" width="98%">
<tr>
<td valign="top" align="center">

    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="big_blue">Packages</td>
        <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

       <table cellspacing="0" cellpadding="5" border="0" width="100%" class="dotted">
       <tr>
            <th align='middle'>&nbsp;</th>
            <th align='middle'><%=lp.get(8)%></th>
            <th align='middle'><%=lp.get(9)%></th>
            <th align='middle'><%=lp.get(10)%></th>
            <th align='middle'><%=lp.get(11)%></th>
        <tr>

<%

for (int i=0; i<list.size(); i++) {
    item = list.get(i);

%>

<tr>
	<td align='middle'><%=(i+1)%></td>
	<td align='middle'><a href="packageDetails.jsp?pid=<%=item.getUnique()%>"><%=item.getName()%></a></td>
	<td align='middle'><%=item.getActive()%></td>
	<td align='middle'><%=item.getCreated()%></td>
	<td align='middle'><input type="checkbox" name="del_<%=item.getUnique()%>"></td>
</tr>

<%
}
%>
       </table>
</td></tr>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td align="left">
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td colspan="2" align="left"><%=lp.get(12)%><br><br></td>
	</tr>
	<tr>
		<td align="left">Name:&nbsp; &nbsp;<input type="text" class="textbox" name="addName" value="" size="30"></td>
		<td align="right"><input type="submit" name="add" value="&nbsp;&nbsp;<%=lp.get(14)%>&nbsp;&nbsp;"></td>
	</tr>
	</table>
</td></tr>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>
        <table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr><td align="left"><%=lp.get(15)%><br><br></td></tr>
	<tr><td align="right"><input type="submit" name="delete" value="<%=lp.get(16)%>"></td></tr>
	</table>
</td></tr>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

</table>


</form>

</body>
</html>