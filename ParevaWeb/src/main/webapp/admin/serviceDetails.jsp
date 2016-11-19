<%@ include file="/WEB-INF/jspf/admin/serviceDetails.jspf" %>
<html>
<head>
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form method="post" action="<%=fileName%>.jsp">
<input type="hidden" name="sid" value="<%=sid%>">
<input type="hidden" name="stype" value="<%=sType%>">

<table cellspacing="0" cellpadding="4" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
    <tr>
            <td align="left" valign="bottom" class="blue_14"><b>Service Details </b></td>
            <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>
</td></tr>
<tr><td valign="top" align="left">
        <%@ include file="/admin/tabs_servicedetails.jsp" %>
    <br>
</td></tr>

<tr><td valign="top" align="left">

        <table cellspacing="0" cellpadding="4" border="0" width="100%">
        <tr><td colspan="2"><%=lp.get(5)%></td></tr>

        <tr bgcolor="<%=bgColor%>">
            <td width="150"><%=lp.get(6)%></td>
            <td><b><%=sdcs.getUnique()%></b></td>
        </tr>
        <tr bgcolor="<%=bgColor%>">
            <td width="150"><%=lp.get(7)%></td>
            <% if (sdcs.getServiceType()==SdcService.ServiceType.ANYXSYSTEM) {
                    String active="";
                    if (sdcs.getActive()==1) active= lp.get(8);
                    else active= lp.get(9);
                    out.print("<td>" + active + "</td>");
            }
            else { %>
            <td><input type="checkbox" name="aActive"  <% if (sdcs.getActive()==1) {%> checked <% } %> value="1"></td>
            <% } %>
        </tr>
        <tr bgcolor="<%=bgColor%>">
            <td width="150"><%=lp.get(10)%></td>
            <td><input type="text" name="aName" size="20" value="<%=sdcs.getName()%>"></td>
        </tr>
        <tr bgcolor="<%=bgColor%>">
            <td width="150"><%=lp.get(11)%></td>
            <td><input type="text" name="aIndex" size="4" value="<%=sdcs.getIndex()%>"></td>
        </tr>
        <tr bgcolor="<%=bgColor%>">
            <td width="150"><%=lp.get(12)%></td>
            <td><select name="aSrvcType">
                    <option value="<%=SdcService.ServiceType.STD%>" <% if (sdcs.getServiceType()==SdcService.ServiceType.STD) { %> selected <% } %>><%=lp.get(13)%></option>
                    <option value="<%=SdcService.ServiceType.ANYXADMIN%>" <% if (sdcs.getServiceType()==SdcService.ServiceType.ANYXADMIN) { %> selected <% } %>><%=lp.get(14)%></option>
                    <option value="<%=SdcService.ServiceType.ANYXSYSTEM%>" <% if (sdcs.getServiceType()==SdcService.ServiceType.ANYXSYSTEM) { %> selected <% } %>><%=lp.get(15)%></option>
                </select>
            </td>
        </tr>

        <tr><td colspan="2"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

        <tr>
            <td colspan="2"><%=lp.get(16)%></td>
        </tr>
        
        <tr bgcolor="<%=bgColor%>">
            <td width="150"><%=lp.get(20)%></td>
            <td><select name="aCategory">
                <option value="" <% if (sdcs.getCategory().equals("")) { %> selected <% } %>><%=lp.get(21)%></option>
            <% for (int i=0; i<umesdc.getCategoryList().size(); i++) {
                    sdccat = umesdc.getCategoryList().get(i);
            %>
                    <option value="<%=sdccat.getUnique()%>" <% if (sdcs.getCategory().equals(sdccat.getUnique())) { %> selected <% } %>><%=sdccat.getName()%></option>
            <%
                    }
            %>
                    </select>
            </td>
        </tr>
        <tr bgcolor="<%=bgColor%>">
            <td width="150"><%=lp.get(22)%></td>
            <td>
            <input type="checkbox" name="visib_p" value="1" <%=vP%>><%=lp.get(23)%>
            &nbsp;&nbsp;
            <input type="checkbox" name="visib_r" value="1" <%=vR%>><%=lp.get(24)%>
            &nbsp;&nbsp;
            <input type="checkbox" name="visib_a" value="1" <%=vA%>><%=lp.get(25)%>
            </td>
        </tr>

        <tr><td colspan="2"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

        <tr>
                <td colspan="2"><%=lp.get(39)%></td>
        </tr>
        <tr bgcolor="<%=bgColor%>">
                <td width="150"><%=lp.get(40)%></td>
                <td><select name="aSecLevel">
                <% for (int i=0; i<10; i++) { %>
                    <option value="<%=i%>" <% if (sdcs.getSecLevel()==i) { %> selected <% } %>><%=i%>&nbsp;&nbsp;</option>
                <% } %>
                        </select>
                </td>
        </tr>
        <tr bgcolor="<%=bgColor%>">
                <td width="150" valign="top"><%=lp.get(41)%></td>
                <td>
                        <table cellpadding="2" border="0">

        <%
        boolean selected = false;
        int count = 0;
        for (int i=0; i<umesdc.getPackageList().size(); i++) {
                sdcpack = umesdc.getPackageList().get(i);

                selected=false;

                for (int k=0; k<sdcs.getPackageList().size(); k++) {
                    if (sdcpack==sdcs.getPackageList().get(k)) { selected=true; break; }
                }
                if (count==0) out.println("<tr>");
        %>
                <td><input type="checkbox" name="pack_<%=sdcpack.getUnique()%>" <% if (selected==true) {%> checked <% } %> ><%=sdcpack.getName()%></td>
        <%
                if (count==1) { count=-1; out.println("</tr>"); }
                
                count++;
        }
        if (count==1) {
        %>
                <td>&nbsp;</td></tr>
        <% } %>
                        </table>
                </td>
        </tr>

        <tr><td colspan="2"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

        <tr>
                <td colspan="2"><%=lp.get(42)%></td>
        </tr>
        <tr bgcolor="<%=bgColor%>">
                <td width="150"><%=lp.get(43)%></td>
                <td><input type="text" name="aDirectory" size="40" value="<%=sdcs.getDirectory()%>"><br></td>
        </tr>
        <tr bgcolor="<%=bgColor%>">
                <td width="150"><%=lp.get(44)%></td>
                <td><input type="text" name="aDefaultPage" size="40" value="<%=sdcs.getDefaultPage()%>"><br></td>
        </tr>
        <tr bgcolor="<%=bgColor%>">
                <td width="150">Promo Page:</td>
                <td><input type="text" name="aSrvcCode" size="40" value="<%=sdcs.getServiceCode()%>"><br></td>
        </tr>
        <tr bgcolor="<%=bgColor%>">
                <td width="150"><%=lp.get(53)%></td>
                <td><input type="text" name="aDefParams" size="40" value="<%=sdcs.getDefParameters()%>"><br></td>
        </tr>
        

        <tr><td colspan="2"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

        <tr>
                <td colspan="2"><%=lp.get(48)%></td>
        </tr>
        <tr bgcolor="<%=bgColor%>">
                <td width="150"><%=lp.get(49)%></td>
                <td><%=sdcs.getModified()%></td>
        </tr>
        <tr bgcolor="<%=bgColor%>">
                <td width="150"><%=lp.get(50)%></td>
                <td><%=sdcs.getCreated()%></td>
        </tr>
        <tr bgcolor="<%=bgColor%>">
                <td width="150" valign="top"><%=lp.get(51)%></td>
                <td><textarea name="aDescription" cols="26" rows="2"><%=sdcs.getDescription()%></textarea></td>
        </tr>

        </table>

</td></tr>

<tr><td align="middle"><input type="submit" name="save" value="<%=lp.get(52)%>" style="width:100px;"></td></tr>

</table>

</form>

</body>
</html>


