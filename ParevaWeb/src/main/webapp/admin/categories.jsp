<%@include file="/WEB-INF/jspf/admin/categories.jspf"%>
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
        <td align="left" valign="bottom" class="big_blue">Categories</td>
        <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

       <table cellspacing="0" cellpadding="5" border="0" width="100%" class="dotted">
       <tr>
            <th align='middle'>&nbsp;</th>
            <th align='middle'><a href="<%=fileName%>.jsp?sort=<%=namesort%>"><%=lp.get(8)%></a></th>
            <th align='middle'><a href="<%=fileName%>.jsp?sort=<%=indexsort%>"><%=lp.get(9)%></a></th>
            <th align='middle'><%=lp.get(10)%></th>
            <th align='middle'><%=lp.get("domains")%></th>
            <th align='middle'><%=lp.get(11)%></th>
        <tr>

<%

String visib = "";
String dname = "";
String[] ds = null;

for (int i=0; i<list.size(); i++) {
    item = list.get(i);

    dname = "";

    if (item.getVisibility()==0) visib = "Public";
    else if (item.getVisibility()==1) visib = "Private";
    else if (item.getVisibility()==2) visib = "Public & Private";

    ds = SdcMisc.stringSplit(item.getDomains(), "?");
    for (int k=0;k<ds.length;k++) {
        if (dname.length()>0 && !dname.endsWith(", ")) dname += ", ";
        try { dname += umesdc.getDomainMap().get(ds[k]).getName(); } catch (NullPointerException e) { System.out.println(ds[k] + ": " + item.getName() + ": " + e); }
    }

    if (dname.equals("")) dname = "<span class='grey_11'><i>No Domains</i></span>";

%>

<tr>
	<td align='middle'><%=(i+1)%></td>
	<td align='middle'><a href="categoryDetails.jsp?ctid=<%=item.getUnique()%>"><%=item.getName()%></a></td>
	<td align='middle'><%=item.getIndex()%></td>
	<td align='middle'><%=visib%></td>
        <td align='middle'><%=dname%></td>
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

