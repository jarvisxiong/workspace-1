<%@include file="/WEB-INF/jspf/itemadmin/java/alsobought.jspf"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language="javascript">
function submitForm (thisForm) {
	thisForm.del.value="1";
	thisForm.submit();
}
</script>
</head>
<body>


<form action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="unq" value="<%=unique%>">

<table cellspacing="0" cellpadding="0" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr>
            <td align="left" valign="bottom" class="blue_14"><b>Java Game Details: </b> <span class="grey_12"><b><%=orgTitle%></b></span></td>
            <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>    
</td></tr>
<tr><td valign="top" align="left">
    <% int _curitem = Integer.parseInt(aReq.get("_curitem", "1")); %>
    <%@ include file="/itemadmin/java/tabs.jsp" %>
    <br>
</td></tr>
   
<tr>
<td valign="top" align="left">   


<tr><td><img src="/images/glass_dot.gif" height="10" width="100%"></td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<tr><td valign="top" align="left">
    <br>
    <table cellspacing="0" cellpadding="2" border="0" class="tableview" width="100%">        
        <tr>
            <th align="left"></th>
            <th align="left">Name</th>
            <th align="center">Price Group</th>
            <th align="center">Status   <select name="dm" onchange="this.form.submit()">
                <% for (int i=0; i<dms.size(); i++) { 
                            values = (String[]) dms.get(i);
                        %>
                        <option value="<%=values[3]%>" <% if (dm.equals(values[3])){%> selected <%}%>><%=values[1]%></option>
                   <% } %>
                    </select>
            </th>
            <th align="center">
                Provider
            </th>
        </tr>
        
        <%
            String row = "";
            for( int i = 0; i < list.size(); i ++ ){
            values = (String[]) list.get( i );
            if (row.equals("row2")) row = "";
            else row = "row2";
            %>
            <tr class="<%=row%>">
                <td>
                    <a href="gamedetails.jsp?unq=<%=values[0]%>&dm=&sort=cr"><img src="<%=values[3]%>" border="0"></a>
                </td>
                <td valign="top" align="left"><a href="gamedetails.jsp?unq=<%=values[0]%>&dm=&sort=cr"><b><%=values[2]%></b></a>&nbsp;(<%=values[1]%>)</td>
                <td valign="top" align="center"><%=values[4]%></td>
                <td valign="top" align="center"><%= values[5]%></td>
                <td valign="top" align="center"><%= values[6] %></td>
            
            </tr>
            
            <%
            }%>
            <% 
                if( list.size() == 0){
                %><tr><td colspan="5">No Results</td></tr><%
                }
            %>
        
    </table>
</td></tr>
<tr><td><img src="/images/glass_dot.gif" height="10" width="100%"></td></tr>




</td>
</tr>
</table>

</form>

</body>
</html>




