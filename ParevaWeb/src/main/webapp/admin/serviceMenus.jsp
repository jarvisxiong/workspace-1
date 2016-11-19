<%@ include file="/WEB-INF/jspf/admin/serviceMenus.jspf" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form method="post" action="<%=fileName%>.jsp">
<input type="hidden" name="formsubmit" value="1">
<input type="hidden" name="sid" value="<%=sid%>">
<input type="hidden" name="stype" value="<%=sType%>">

<table cellspacing="0" cellpadding="4" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
    <tr>
            <td align="left" valign="bottom" class="blue_14"><b>Service Menus</b></td>
            <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>
</td></tr>
<tr><td valign="top" align="left">
        <%@ include file="/admin/tabs_servicedetails.jsp" %>
    <br>
</td></tr>

<tr><td valign="top" align="left">

        <table cellspacing="0" cellpadding="4" border="0" width="98%">

        <tr>
            <td align="left"><%=lp.get(3)%>&nbsp;&nbsp;</td>
            <td align="left" width="80%">
            <SELECT NAME="menulang" onChange="this.form.submit()">
                    <OPTION VALUE="" <% if (menulang.equals("")) {%> SELECTED <%}%>	><%=lp.get(4)%></OPTION>
            <% for (int i=0; i<umesdc.getLanguageList().size(); i++) {
                    sdclang = umesdc.getLanguageList().get(i);
            %>
                    <OPTION VALUE="<%=sdclang.getLanguageCode()%>" <% if (menulang.equals(sdclang.getLanguageCode())) {%> SELECTED <%}%>><%=sdclang.getLanguageName()%></OPTION>
            <% } %>
            </SELECT>
            </td>
        </tr>

        <% if (!menulang.equals("")) {%>

        <tr><td colspan="2"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
        <tr>
            <td><%=lp.get(5)%></td>
            <td>
            <SELECT NAME="copylang">
		<OPTION VALUE="" <% if (copylang.equals("")) {%> SELECTED <%}%>><%=lp.get(4)%></OPTION>
            <% for (int i=0; i<umesdc.getLanguageList().size(); i++) {
                    sdclang =umesdc.getLanguageList().get(i);
                    if (sdclang.getLanguageCode().equals(menulang)) continue;
            %>
                    <OPTION VALUE="<%=sdclang.getLanguageCode()%>" <% if (copylang.equals(sdclang.getLanguageCode())) {%> SELECTED <%}%>><%=sdclang.getLanguageName()%></OPTION>
            <% } %>
            </SELECT>
	&nbsp;&nbsp;<input type="submit" name="copy" value="<%=lp.get(7)%>">
            </td>
        </tr>

        <%}%>

        </table>

</td></tr>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<% if (!menulang.equals("")) {%>

<tr><td valign="top" align="left">

        <table cellspacing="0" cellpadding="4" border="0" width="98%" class="dotted">

        <tr>
            <th align='left'><%=lp.get(8)%></th>
            <th align='left'><%=lp.get(9)%></th>
            <th colspan="2" align='left'><%=lp.get(10)%></th>
        </tr>

        <% for (int i=0; i<menuList.size(); i++) {
                sdcmenu = menuList.get(i);
                if (!sdcmenu.getLanguageCode().equals(menulang)) continue;
        
                 if (sdcmenu.getIndex().equals("0")) {
         %>
            <input type="hidden" name="index_<%=sdcmenu.getUnique()%>" value="0">
            <input type="hidden" name="target_<%=sdcmenu.getUnique()%>" value="<%=sdcs.getDefaultPage()%>">
            <tr>
                <td align='left'><%=lp.get(11)%></td>
                <td align='left'><input type="text" name="name_<%=sdcmenu.getUnique()%>" value="<%=sdcmenu.getName()%>" size="20"></td>
                <td colspan="2" align='left'><%=sdcs.getDefaultPage()%></td>
            </tr>

<%              } else {
%>

            <tr>
                <td align='left'><input type="text" name="index_<%=sdcmenu.getUnique()%>" value="<%=sdcmenu.getIndex()%>" size="2"></td>
                <td align='left'><input type="text" name="name_<%=sdcmenu.getUnique()%>" value="<%=sdcmenu.getName()%>" size="20"></td>
                <td align='left'><input type="text" name="target_<%=sdcmenu.getUnique()%>" value="<%=sdcmenu.getTargetPage()%>" size="30"></td>
                <td align="right"><input type="checkbox" name="sel_<%=sdcmenu.getUnique()%>" value="1"></td>
            </tr>

<%		}
         }

%>
        
        

        <tr>
            <td align="right" colspan="4">
                <input type="submit" name="delete" value="<%=lp.get(17)%>" style="width:110px;">
                &nbsp;&nbsp;&nbsp;
                <input type="submit" name="save" value="<%=lp.get(13)%>" style="width:110px;">
            </td>
        </tr>

        <tr>
            <td align='left'><input type="text" name="add_aIndex" value="<%=aindex%>" size="2"></td>
            <td align='left'><input type="text" name="add_aName" value="<%=aname%>" size="20"></td>
            <td align='left'><input type="text" name="add_aTargetPage" value="<%=atarget%>" size="30"></td>
            <td align="right"><input type="submit" name="add" value="<%=lp.get(14)%>" style="width:110px;"></td>
        </tr>
       
        </table>
</td></tr>
<%}%>
</table>

</form>

</body>
</html>


