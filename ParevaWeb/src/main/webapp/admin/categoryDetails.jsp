<%@include file="/WEB-INF/jspf/admin/categorydetails.jspf"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form method="post" action="<%=fileName%>.jsp">
<input type="hidden" name="ctid" value="<%=ctid%>">

<table cellspacing="0" cellpadding="4" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
    <tr>
            <td align="left" valign="bottom" class="blue_14"><b>Category Details </b></td>
            <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>
</td></tr>
<tr><td valign="top" align="left">
        <%@ include file="/admin/tabs_categorydetails.jsp" %>
    <br>
</td></tr>

<tr><td valign="top" align="left">

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
                <td  class="grey_11"><%=lp.get(10)%></td>
                <td ><input type="text" name="name" size="20" value="<%=item.getName()%>"></td>
        </tr>
         <tr>
                <td  class="grey_11"><%=lp.get(11)%></td>
                <td ><input type="text" name="index" size="4" value="<%=item.getIndex()%>"></td>
        </tr>

         <tr>
                <td  class="grey_11"><%=lp.get("groups")%></td>
                <td >
                    <% if (!item.getUserGroups().equals("")){%> <b>Limited Groups</b> <%} else {%> All Groups <%}%>
                </td>
        </tr>

        <tr>
            <td  class="grey_11" valign="top">Domains:</td>
            <td>
                <table cellspacing="0" cellpadding="3" border="0" width="100%">
                 
                    <%
                    boolean selected = false;
                    int count = 0;
                    for (int i=0; i<umesdc.getDomainList().size(); i++) {
                        sdcd = umesdc.getDomainList().get(i);

                        selected=false;
                        if (item.getDomains().indexOf(sdcd.getUnique())>-1) { selected=true; }                        
                        if (count==0) out.println("<tr>");
                        
                    %>
                        <td><input type="checkbox" name="sdcd_<%=sdcd.getUnique()%>" <% if (selected==true) {%> checked <% } %> ><%=sdcd.getName()%></td>
                    <%
                        if (count==1) { count=-1; out.println("</tr>"); }
                        count++;
                    }
                    %>
                        
                    <% if (count==1) { %> <td>&nbsp;</td></tr> <% } %>
                
                </table>
            </td>
        </tr>

        <tr>
                <td class="grey_11"><%=lp.get(12)%></td>
                <td><input type="text" name="picture" size="20" value="<%=item.getPicture()%>"></td>
        </tr>
        <tr>
                <td class="grey_11"><%=lp.get(13)%></td>
                <td><input type="text" name="page" size="20" value="<%=item.getPage()%>"></td>
        </tr>
        <tr>
                <td class="grey_11"><%=lp.get(14)%></td>
                <td><input type="checkbox" name="expand" value="1" <% if (item.getExpand()==1) {%> checked <% } %>></td>
        </tr>
        <tr>
                <td class="grey_11"><%=lp.get(15)%></td>
                <td>
                    <input type="radio" name="visibility" value="0" <% if (item.getVisibility()==0) {%> checked <% } %>><%=lp.get(16)%>
                    <input type="radio" name="visibility" value="1" <% if (item.getVisibility()==1) {%> checked <% } %>><%=lp.get(17)%>
                    <input type="radio" name="visibility" value="2" <% if (item.getVisibility()==2) {%> checked <% } %>><%=lp.get(18)%>
                </td>
        </tr>
<tr><td colspan="2"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td colspan="2"><b><%=lp.get("langnames")%></b></td></tr>

    <%
        String langName = "";

        for (int k=0; k<umesdc.getLanguageList().size(); k++) {
            sdclang = umesdc.getLanguageList().get(k);

            langName = item.getNameMap().get(sdclang.getLanguageCode());
            if (langName==null) langName = "";
    %>
<tr>
	<td class="grey_11"><%=sdclang.getLanguageName()%>:</td>
	<td><input type="text" name="sdclang_<%=sdclang.getLanguageCode()%>" size="20" value="<%=langName%>"></td>
</tr>
    <% } %>

    <tr><td colspan="2"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
    <tr><td colspan="2" align="right"><input type="submit" name="save" value="<%=lp.get(19)%>" style="width:150px;"></td></tr>
    </table>

</td></tr>
</table>
</form>

        
</body>
</html>
