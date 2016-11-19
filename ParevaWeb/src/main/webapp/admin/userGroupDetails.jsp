<%@include file="/WEB-INF/jspf/admin/usergroupdetails.jspf"%>
<html>
<head>
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language="JavaScript"> 
function modifyCredits(val) {
	if (val == true) { document.thisForm.aCredits.disabled=0; }
	else { document.thisForm.aCredits.disabled=1; }
}
</script> 
</head>
<body>

<form method="post" action="userGroupDetails.jsp" name="thisForm">
<input type="hidden" name="ugid" value="<%=ugid%>">

<table cellspacing="0" cellpadding="4" border="0" width="98%">
<tr><td>

    <table cellspacing="0" cellpadding="4" border="0" width="100%" >
    <tr>
	<td align="left" valign="bottom" class="big_blue">User Group Details</td>
	<td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%><br>
		<a href="userGroups.jsp" class="small_blue"><%=lp.get(6)%></a></td>
    </tr>
    </table>
</td></tr>


<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td align="right"><a href="resellerKeywords.jsp?ugid=<%=ugid%>" class="blue_12">User Group SMS Keywords</a></td></tr>
<tr><td>

    <table cellspacing="0" cellpadding="4" border="0" width="100%" >
    <tr>
            <td class="grey_11"><%=lp.get(8)%></td>
            <td class="grey_11"><b><%=group.getUnique()%></b></td>
    </tr>
    <tr>
            <td class="grey_11"><%=lp.get(12)%></td>
            <td class="grey_11"><input type="checkbox" name="aActive" <% if (group.getActive()==1) {%> checked <% } %> value="1"></td>
    </tr>
    <tr>
            <td class="grey_11"><%=lp.get(13)%></td>
            <td class="grey_11"><input type="text" name="aName" size="40" value="<%=group.getName()%>"><br></td>
    </tr>
    <tr>
            <td class="grey_11">Parent:</td>
            <td class="grey_11"><%=parentName%></td>
    </tr>
    <tr>
            <td  class="grey_11">Price Group:</td>
            <td  class="grey_11">
                <select name="aPriceGroup">
            <% for (int i=0; i<6; i++) {%>
                    <option value="<%=i%>" <% if (group.getPriceGroup()==i){%> selected <%}%>>&nbsp;&nbsp;<%=i%>&nbsp;&nbsp;</option>
            <% } %>
                </select>
            </td>
    </tr>

    <tr>
            <td  class="grey_11"><%=lp.get(9)%></td>
            <td  class="grey_11"><%=group.getModified()%></td>
    </tr>
    <tr>
            <td  class="grey_11"><%=lp.get(10)%></td>
            <td  class="grey_11"><%=group.getCreated()%></td>
    </tr>
    <tr>
            <td  class="grey_11"><%=lp.get(11)%></td>
            <td  class="grey_11"><%=System.getProperty(group.getDomain() + "_name")%></td>
    </tr>
    <tr>
            <td  class="grey_11"><%=lp.get(21)%></td>
            <td  class="grey_11"><input type="text" disabled name="aCredits" size="10" value="<%=group.getCredits()%>" >
                    &nbsp;&nbsp;&nbsp;
                    <input type="checkbox" name="modcred" value="1" onClick="modifyCredits(this.form.modcred.checked);"> Modify credits
            </td>
    </tr>

    <tr>
            <td  valign="top" class="grey_11"><%=lp.get(14)%></td>
            <td  class="grey_11"><textarea name="aDescription" cols="60" rows="5"><%=group.getDescription()%></textarea></td>
    </tr>
    <tr>
            <td colspan="2" align="right"><input type="submit" name="save" value="&nbsp;&nbsp;<%=lp.get(19)%>&nbsp;&nbsp;"></td>
    </tr>
    </table>
</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<% if (dd!=null){%>

<tr><td class="grey_11"><%=lp.get(15)%></td></tr>

<tr><td>
	<table cellpadding=5 cellspacing=0 border=0 width=100%>

<%
boolean selected=false;
boolean packageInDomain=false;
boolean serviceInPackage = false;

for (int i=0; i<dd.getPackageList().size(); i++) {
	sdcpack = dd.getPackageList().get(i);

        selected=false;

	for (int k=0; k<group.getPackageList().size(); k++) {
            grpack = group.getPackageList().get(k);
            if (grpack.getUnique().equals(sdcpack.getUnique())) { selected=true; break; }
	}
%>
	<tr bgcolor="#EEEEAA">
	<td><input type="checkbox" name="pack_<%=sdcpack.getUnique()%>" <% if (selected) { %> checked <%}%>></td>
	<td colspan="3" align="left" class="grey_11">
		<b>&nbsp;<%=sdcpack.getName()%></b>,&nbsp;&nbsp;
		<% if (sdcpack.getActive()==1) {%>Active<%} else {%>Disabled<%}%>
	</td>
	</tr>
<%
	for (int k=0; k<anyxsdc.getServiceList().size(); k++) {
            sdcservice = anyxsdc.getServiceList().get(k);

            if (sdcservice.getPackageList()==null) continue;

            serviceInPackage = false;

            for (int t=0; t<sdcservice.getPackageList().size(); t++) {
                if (sdcservice.getPackageList().get(t).getUnique().equals(sdcpack.getUnique())) {
                    serviceInPackage = true;
                    break;
                }
            }

            if (!serviceInPackage) continue;

%>

		<tr bgcolor="#EEEEEE">
		<td>&nbsp;</td>
		<td class="blue_10">&nbsp;&nbsp;&nbsp;<%=sdcservice.getName()%></td>
		<td class="grey_10">Sec Level: <%=sdcservice.getSecLevel()%></td>
		<td class="grey_10"><% if (sdcservice.getActive()==1) {%><%=lp.get(26)%><%} else {%><%=lp.get(27)%><%}%></td>
		</tr>
	<%
	}
}

%>
	</table>
</td></tr>
<tr><td colspan="<%=cs%>" align="right"><input type="submit" name="save" value="<%=lp.get(19)%>"></td></tr>
<%}%>
</table>

</form>

</body>
</html>