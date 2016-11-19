<%@include file="/WEB-INF/jspf/admin/categoriesandusergroups.jspf"%>



<html>
<head>
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form method="post" action="<%=fileName%>.jsp">
<input type="hidden" name="ctid" value="<%=ctid%>">

<table cellspacing="0" cellpadding="4" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
    <tr>
            <td align="left" valign="bottom" class="blue_14"><b>Category Details</b></td>
            <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>
</td></tr>
<tr><td valign="top" align="left">
        <%@ include file="/admin/tabs_categorydetails.jsp" %>
    <br>
</td></tr>

<tr><td>
    <table cellspacing="0" cellpadding="5" border="0" width="100%" class="tableview">
        <tr>
            <th colspan="2" align='left'><%=lp.get("name")%></th>
            <th align='right'><%=lp.get("select")%></th>
        <tr>
<%

String bg2 = "#EEEEEE";

boolean checked = false;

for (int i=0; i<list.size(); i++) {
    group = list.get(i);
    if (!group.getParent().equals("") && !group.getParent().equals("root")) continue;

    if (item.getDomains().indexOf(group.getDomain())==-1) continue;

    if (item.getUserGroups().indexOf(group.getUnique())>-1) checked = true;
    else checked = false;

%>

<tr>
	<td colspan="2" align='left' class="grey_11"><b><%=group.getName()%></b></td>
	<td align='right' class="grey_10"><input type="checkbox" name="sel_<%=group.getUnique()%>" <%if (checked){%> checked <%}%>></td>
</tr>
<% 	

    for (int k=0; k<list.size(); k++) {
        subgroup = list.get(k);
        if (!subgroup.getParent().equals(group.getUnique())) continue;
        if (item.getDomains().indexOf(group.getDomain())==-1) continue;
        
        if (item.getUserGroups().indexOf(subgroup.getUnique())>-1) checked = true;
        else checked = false;

%>
<tr>
	<td bgcolor="<%=bg2%>"><img src="/images/glass_dot.gif" width="10" height="1"></td>
	<td bgcolor="<%=bg2%>" align='left' class="grey_10"><%=subgroup.getName()%></td>
	<td bgcolor="<%=bg2%>" align='right' class="grey_10"><input type="checkbox" name="sel_<%=subgroup.getUnique()%>" <%if (checked){%> checked <%}%>></td>
</tr>
<%			
     }
}
%>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td align="right"><input type="submit" name="save" value="Save" style="width:100px;"></td></tr>

</table>
</form>