<%@ include file="/WEB-INF/jspf/admin/services.jspf" %>

<html>
<head>
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form method="post" action="<%=fileName%>.jsp">
<input type="hidden" name="stype" value="<%=sType%>">

<table cellspacing="0" cellpadding="4" border="0" width="98%">
<tr><td valign="top" align="left">
<table cellspacing="0" cellpadding="0" border="0" width="100%">
    <tr>
            <td align="left" valign="bottom" class="blue_14"><b>Services </b></td>
            <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>
</td></tr>
<tr><td valign="top" align="left">
        <%@ include file="/admin/tabs_services.jsp" %>
    <br>
</td></tr>

<tr><td valign="top" align="left">

       <table cellspacing="0" cellpadding="5" border="0" width="100%" class="dotted">
       <tr>
            <th align='middle'>&nbsp;</th>
            <th align='left'><a href="<%=fileName%>.jsp?stype=<%=sType%>&sort=<%=namesort%>"><%=lp.get(5)%></a></th>
            <th align='middle'><%=lp.get(6)%></th>
            <th align='left'><a href="<%=fileName%>.jsp?stype=<%=sType%>&sort=<%=dirsort%>"><%=lp.get(7)%></a></th>
            <th align='left'><a href="<%=fileName%>.jsp?stype=<%=sType%>&sort=<%=catsort%>"><%=lp.get(8)%></a></th>
            <th align='middle'><%=lp.get(9)%></th>
            <th align='middle'><%=lp.get(10)%></th>
            <th align='middle'><a href="<%=fileName%>.jsp?stype=<%=sType%>&sort=<%=datesort%>">Created</a></th>
            <th align='middle'>&nbsp;</th>
        <tr>

<%
            int scount = 0;
            
            for (int i=0; i<umesdc.getServiceList().size(); i++) {
                sdcs = umesdc.getServiceList().get(i);

                if (umesdc.getCategoryMap().get(sdcs.getCategory())!=null) catName = umesdc.getCategoryMap().get(sdcs.getCategory()).getName();
                else catName = "";
                
                if (sType.equals("std") && sdcs.getServiceType()!=SdcService.ServiceType.STD) continue;
                else if (sType.equals("system") && sdcs.getServiceType()!=SdcService.ServiceType.ANYXSYSTEM) continue;
                else if (sType.equals("admin") && sdcs.getServiceType()!=SdcService.ServiceType.ANYXADMIN) continue;
                scount++;
          %>
        <tr>
            <td><%=scount%></td>
            <td><a href="serviceDetails.jsp?sid=<%=sdcs.getUnique()%>&stype=<%=sType%>"><%=sdcs.getName()%></a></td>
            <td align='middle'><input type="text" name="index_<%=sdcs.getUnique()%>" value="<%=sdcs.getIndex()%>" size="4"></td>
            <td><%=sdcs.getDirectory()%></td>
            <td><%=catName%></td>
            <td align='middle'><%=sdcs.getSecLevel()%></td>
            <td align='middle'><%=sdcs.getActive()%></td>
            <td align='middle'><%=SdcMiscDate.date.format(sdcs.getCreated())%></td>
            <td align="right"><input type="checkbox" name="sel_<%=sdcs.getUnique()%>"></td>

        </tr>
          <% } %>
       </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td align="right"><input type="submit" name="save" value="<%=lp.get(18)%>"></td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td align="right"><input type="submit" name="delete" value="<%=lp.get(20)%>"></td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
</table>

</form>

</body>
</html>