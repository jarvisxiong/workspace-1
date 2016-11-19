<%@include file="/WEB-INF/jspf/itemcategories/subcats.jspf"%>



<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script src="/lib/global_anyx.js" language="javascript"></script>
<script type="text/javascript" src="/lib/scriptaculous/prototype.js"></script>
<script type="text/javascript" src="/lib/scriptaculous/scriptaculous.js"></script>
</head>
</head>
<body>
    
<form action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="cat" value="<%=unique%>">
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
    
    <table cellpadding="0" cellspacing="0" border="0" class="tableview" width="100%">
    <tr class="grey_11">
        <th width="20" align=''>&nbsp;</th>
        <th width="250" align='left'>Name</th>
        <th width="50" align='center'>Items</th>
    <tr>
    </table>
    
    <ul id="itemlist" style="list-style-type: none; padding: 0; margin: 0;">
<% 

for (int i=0; i<list.size(); i++) { 
    cat = (ItemCategory) list.get(i);
%>
    
    <li id="item_<%=cat.getUnique()%>">
        <table cellpadding="0" cellspacing="0" border="0" class="tableview" width="100%">
            <tr>
                <td width="20" align='left' class="grey_10"><%=(i+1)%><img src="/images/glass_dot.gif" height="1" width="10"></td>
                <td width="250" align='left'><a href="catdetails.jsp?unq=<%=cat.getUnique()%>&itype=<%=itype%>" class="small_blue"><%=cat.getName1()%></a></td>      
                <td width="50" align='center' class="grey_10"><%=cat.getItemCount()%></td>
            </tr>

        </table>
    </li>
<%
}
%>

</ul>

</td></tr>


</table> 

</form>

<script type="text/javascript">    
    Sortable.create(
        "itemlist", 
        { 
            onUpdate: function(){ 
                temp = Sortable.serialize('itemlist');
                temp = temp.split ('&itemlist[]=');                
                makeRequest('updatecatindex.jsp?' + temp , '');                
            } 
        } 
    );  
</script>
</body>
</html>


