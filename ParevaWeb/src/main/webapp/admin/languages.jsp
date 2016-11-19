<%@include file="/WEB-INF/jspf/admin/languages.jspf"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form method="post" action="<%=fileName%>.jsp">

<table cellspacing="0" cellpadding="5" border="0" width="98%">
<tr>
<td valign="top" align="center">

    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="big_blue">Languages</td>
        <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

       <table cellspacing="0" cellpadding="5" border="0" width="100%" class="dotted">
       <tr>
            <th align='middle'>&nbsp;</th>
            <th align='middle'><%=lp.get(10)%></th>
            <th align='middle'><%=lp.get(11)%></th>
            <th align='middle'><%=lp.get(12)%></th>
            <th align='middle'><%=lp.get("fileenc")%></th>
            <th align='middle'><%=lp.get(13)%></th>            
            <th align='middle'>&nbsp;</th>
        <tr>

<%

for (int i=0; i<list.size(); i++) {
    item = list.get(i);

%>

<tr>
	<td align='middle'><%=(i+1)%></td>
	<td align='middle'><%=item.getLanguageName()%></td>
        <td align='middle'><%=item.getLanguageCode()%></td>
        <td align='middle'><%=item.getEncoding()%></td>
        <td align='middle'><%=item.getFileEncoding()%></td>
	<td align='middle'><%=item.getStylesheet()%></td>        
	<td align='right'><input type="checkbox" name="del_<%=item.getLanguageCode()%>_<%=item.getUnique()%>"></td>
</tr>

<%
}
%>

<tr>
	<td align="middle">&nbsp;</td>
	<td align="middle"><input type="text" name="addName" value="<%=addName%>" size="10"></td>
	<td align="middle"><input type="text" name="addCode" value="<%=addCode%>" size="3"></td>
	<td align="middle"><input type="text" name="addEnc" value="<%=addEnc%>" size="8"></td>
        <td align="middle"><input type="text" name="addFileEnc" value="<%=addFileEnc%>" size="20"></td>
	<td align="middle"><input type="text" name="addSheet" value="<%=addSheet%>" size="20"></td>        
	<td align="right"><input type="submit" name="add" value="Add" style="width:50px;"></td>
</tr>

<tr><td colspan="7" align="right">
        <%=lp.get(17)%>&nbsp;&nbsp;<input type="checkbox" name="delMenus" <%=delMenus%>>
        &nbsp;&nbsp;&nbsp;
        <%=lp.get(16)%>&nbsp;&nbsp;<input type="checkbox" name="copyExist" <%=copyExist%>>
</td></tr>

<tr><td colspan="7" align="right">
        <input type="submit" name="delete" value="<%=lp.get(18)%>">
</td></tr>



       </table>
</td></tr>

</table>

</form>

</body>
</html>