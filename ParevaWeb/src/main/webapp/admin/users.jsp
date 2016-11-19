<%@include file="/WEB-INF/jspf/admin/users.jspf"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form method="post" action="<%=fileName%>.jsp">
    
<table cellspacing="0" cellpadding="4" border="0" width="98%">
<tr>
<td valign="top" align="left">

    <table cellspacing="0" cellpadding="4" border="0" width="100%">
    <tr>
            <td align="left" valign="bottom" class="big_blue">USERS</td>
            <td align="right" valign="bottom"><a href="adduser.jsp" class="small_bkue"><%=lp.get(1)%></a></td>
    </tr>
    </table>
</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<tr><td class="grey_11"><%=lp.get(2)%><br><br></td></tr>

<tr><td>
        <table cellspacing="0" cellpadding="6" border="0" width="100%">

            <tr>
                <td bgcolor="<%=bgColor%>" align='left' valign='top' class="grey_11"><%=lp.get(3)%></td>
                <td bgcolor="<%=bgColor%>" align='left' valign='top' class="grey_11"><b><%=totalUsers%></b></td>
            </tr>


            <tr>
                <td bgcolor="<%=bgColor%>" align='left' valign='top' class="grey_11"><%=lp.get(4)%></td>
                <td bgcolor="<%=bgColor%>" align='left' valign='top' class="grey_11"><b><%=activeUsers%></b></td>
            </tr>
            <tr>
                <td bgcolor="<%=bgColor%>" align='left' valign='top' class="grey_11">Users currently online:</td>
                <td bgcolor="<%=bgColor%>" align='left' valign='top' class="grey_11"><b><%=onlineUsers%></b></td>
            </tr>

        </table>
</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>
        <table cellspacing="0" cellpadding="4" border="0" width="100%">
            <td>Search String: <input type="text" name="sstr" value="<%=sstr%>"></td>
            <td>
                <select name="stype">
                    <option value="all" <% if (stype.equals("all")){%> selected <%}%>>All Fields</option>
                    <option value="lastname" <% if (stype.equals("lastname")){%> selected <%}%>>Last Name</option>
                    <option value="firstname" <% if (stype.equals("firstname")){%> selected <%}%>>First Name</option>
                    <option value="nickname" <% if (stype.equals("nickname")){%> selected <%}%>>Nick Name</option>
                    <option value="msisdn" <% if (stype.equals("msisdn")){%> selected <%}%>>Msisdn</option>
                    <option value="email" <% if (stype.equals("email")){%> selected <%}%>>Email</option>
                    <option value="login" <% if (stype.equals("login")){%> selected <%}%>>Login</option>
                    <option value="unique" <% if (stype.equals("unique")){%> selected <%}%>>Unique</option>
                    <option value="domain" <% if (stype.equals("domain")){%> selected <%}%>>Domain</option>
                </select>
            <td><input type="submit" name="search" value="Search">
        </table>
</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<% if (list!=null) { %>
<tr><td>
        <% if (pageCount>1) { %>
        <div style="text-align:center; line-height:34px;">
            <span class="lightgrey_11">
            <% if (curPage>1){ %><a href="<%=fileName%>.jsp?<%=linkParams%><%=((curPage-2)*maxCount)%>">&laquo; Previous</a>
            <% } else { %>&laquo; Previous <% } %>
            |&nbsp;&nbsp;
            <%

            for (int k=startPage; k<endPage; k++) {
                int newIndex = k*maxCount;
                if (newIndex==index) { out.print("<span class='lightgrey_14'><b>" + (k+1) + "</b></span>"); }
                else {
            %>
                <a href="<%=fileName%>.jsp?<%=linkParams%><%=newIndex%>"><%=(k+1)%></a>
            <%
                }
            }
            %>

            &nbsp;&nbsp;|
            <% if ((pageCount-curPage)>0){ %><a href="<%=fileName%>.jsp?<%=linkParams%><%=(curPage*maxCount)%>">Next &raquo;</a>
            <% } else { %>Next &raquo;<% } %>
            </span>
        </div>
        <% } %>

    <table cellspacing="0" cellpadding="4" border="0" width="100%" class="dotted">
        <tr>
            <th>&nbsp;</th>
            <th align="left">Name</th>
            <th align="left">Nick Name</th>
            <th align="left">Active</th>
            <th align="left">Domain</th>
            <th align="left">Created</th>
            <th>&nbsp;</th>
        </tr>
        
        <% for (int i=0; i<list.size(); i++) {
                item = list.get(i);
        %>
        <tr>
            <td><a href="userDetails.jsp?uid=<%=item.getParsedMobile()%>&<%=params%>"><%=(index+i+1)%></a></td>
            <td><a href="userDetails.jsp?uid=<%=item.getParsedMobile()%>&<%=params%>"><%=item.getFirstName()%> <%=item.getLastName()%></a></td>
            <td><%=item.getNickName()%></td>
            <td><%=item.getActive()%></td>
            <td><%=System.getProperty(item.getDomain() + "_name")%></td>
            <td><%=item.getCreated()%></td>
            <td><input type="checkbox" name="del_<%=item.getUnique()%>" value="1"></td>
        </tr>
        <%}%>
    </table>
</td></tr>

<% if (list.size()>0) {%>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>
    <table cellspacing="0" cellpadding="4" border="0" width="100%">
    <tr>
	<td align="left" class="grey_11"><%=lp.get(40)%></td>
	<td align="right"><input type="submit" name="delete" value="<%=lp.get(42)%>"></td>
    </tr>
    </table>
</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<%}%>
<%}%>
</table> 

</form>
            
</body>
</html>