<%@include file="/WEB-INF/jspf/itemcategories/catdetails.jspf"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>
    
<form action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="unq" value="<%=unique%>">
<input type="hidden" name="shwsubs" value="<%=showSubs%>">
<input type="hidden" name="itype" value="<%=itype%>">

<table cellspacing="0" cellpadding="0" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="blue_14"><b>Category Details</td>
        <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>    
</td></tr>

<tr><td valign="top" align="left">
    <% int _curitem = Integer.parseInt(aReq.get("_curitem", "0")); %>
    <%@ include file="/itemcategories/tabs.jsp" %>
    <br>
</td></tr>
<tr><td>

    <table cellpadding="6" cellspacing="0" border="0">
    <% if (parent!=null) {%>
    <tr>
    <td class="grey_11">Parent Category:</td>
    <td><a href="catdetails.jsp?unq=<%=parent.getUnique()%>&itype=<%=itype%>"><%=parent.getName1()%></a></td>
    </tr>
    <% } %>
        
    <tr>
    <td class="grey_11">Name:</td>
    <td><input type="text" size="20" name="name1" class="textbox" value="<%=cat.getName1()%>"></td>
    </tr>
    <tr>
    <td class="grey_11">Alias 1:</td>
    <td><input type="text" size="20" name="name2" class="textbox" value="<%=cat.getName2()%>"></td>
    </tr>
    <tr>
    <td class="grey_11">Alias 2:</td>
    <td><input type="text" size="20" name="name3" class="textbox" value="<%=cat.getName3()%>"></td>
    </tr>
    <tr>
    <td class="grey_11">Alias 3:</td>
    <td><input type="text" size="20" name="name4" class="textbox" value="<%=cat.getName4()%>"></td>
    </tr>
    <tr>
        <td class="grey_11">Classification:</td>
        <td class="grey_11">
            <select name="class">
                <option value="">[Select]</option>
                <option value="safe" <% if (cat.getClassification().equals("safe")){%> selected <%}%>>Safe</option>
                <option value="bikini" <% if (cat.getClassification().equals("bikini")){%> selected <%}%>>Bikini</option>
                <option value="topless" <% if (cat.getClassification().equals("topless")){%> selected <%}%>>Topless</option>
                <option value="fullnude" <% if (cat.getClassification().equals("fullnude")){%> selected <%}%>>Full Nude</option>
                <option value="hardcore" <% if (cat.getClassification().equals("hardcore")){%> selected <%}%>>Hardcore</option>
            </select>
        </td>
    </tr>

    </table>
	
</td></tr>

<tr><td align="right"><input type="submit" name="save" value="&nbsp;Save&nbsp;"><br><br></td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

</table> 

</form>


</body>
</html>


