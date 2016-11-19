<%@include file="/WEB-INF/jspf/admin/packageDetails.jspf"%>


<html>
<head>
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form method="post" action="<%=fileName%>.jsp" name="thisForm">
<input type="hidden" name="pid" value="<%=pid%>">

<table cellspacing="0" cellpadding="4" border="0" width="98%">
<tr><td>

    <table cellspacing="0" cellpadding="4" border="0" width="100%" >
    <tr>
	<td align="left" valign="bottom" class="big_blue">Package Details</td>
	<td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%><br>
		<a href="packages.jsp" class="small_blue"><%=lp.get(6)%></a></td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

    <table cellspacing="0" cellpadding="4" border="0" width="100%" >
        <tr>
                <td class="grey_11"><%=lp.get(7)%></td>
                <td class="grey_11"><b><%=item.getUnique()%></b></td>
        </tr>
        <tr>
                <td  class="grey_11"><%=lp.get(8)%></td>
                <td  class="grey_11"><%=item.getModified()%></td>
        </tr>
        <tr>
                <td  class="grey_11"><%=lp.get(9)%></td>
                <td  class="grey_11"><%=item.getCreated()%></td>
        </tr>
        <tr>
                <td  class="grey_11"><%=lp.get(11)%></td>
                <td ><input type="checkbox" name="aActive" <% if (item.getActive()==1) {%> checked <% } %> value="1"></td>
        </tr>
        <tr>
                <td  class="grey_11"><%=lp.get(10)%></td>
                <td ><input type="text" name="aName" size="20" value="<%=item.getName()%>"></td>
        </tr>
        <tr>
                <td  valign="top" class="grey_11"><%=lp.get(12)%></td>
                <td ><textarea name="aDescription" cols="30" rows="2"><%=item.getDescription()%></textarea></td>
        </tr>
    </table>
        
</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td align="right"><input type="submit" name="save" value="&nbsp;&nbsp;<%=lp.get(13)%>&nbsp;&nbsp;"></td></tr>

</table>

</form>

</body>
</html>
