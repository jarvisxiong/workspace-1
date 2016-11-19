<%@include file="/WEB-INF/jspf/itemcategories/index.jspf"%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script src="/lib/global_anyx.js" language="javascript"></script>
<script type="text/javascript" src="/lib/scriptaculous/prototype.js"></script>
<script type="text/javascript" src="/lib/scriptaculous/scriptaculous.js"></script>
</head>

<body>
    
<form method="post" action="<%=fileName%>.jsp">
<input type="hidden" name="shwsubs" value="<%=showSubs%>">

<table cellspacing="0" cellpadding="2" border="0" width="95%">
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="2" border="0" width="100%">
        <tr>
            <td align="left" valign="bottom" class="big_blue">Item Categories</td>
            <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
        </tr>
    </table>    
</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

    <table cellpadding="6" cellspacing="0" border="0" width="100%">
    <tr>
        <td class="grey_11"><b>Select Item Type</b> &nbsp;&nbsp; 
    <select name="itype" onChange="javascript:this.form.submit();">
        <option value="">[Select]</option>       
        <option value="java" <% if (itype.equals("java")){%> selected <%}%>>Java Games</option>
        <option value="bgimage" <% if (itype.equals("bgimage")){%> selected <%}%>>Bg Images</option>
        <option value="mastertone" <% if (itype.equals("mastertone")){%> selected <%}%>>RealTones</option>
        <option value="truetone" <% if (itype.equals("truetone")){%> selected <%}%>>Truetones</option>
        <option value="funtone" <% if (itype.equals("funtone")){%> selected <%}%>>Funtones</option>
        <option value="ringtone" <% if (itype.equals("ringtone")){%> selected <%}%>>Poly/Mono</option>
        <option value="gifanim" <% if (itype.equals("gifanim")){%> selected <%}%>>Animations</option>
        <option value="coupon" <% if (itype.equals("coupon")){%> selected <%}%>>Mobi Coupons</option>
        <option value="video" <% if (itype.equals("video")){%> selected <%}%>>Video Clips</option>
        <option value="event" <% if (itype.equals("event")){%> selected <%}%>>Event</option>
    
    </select>
    </td>    
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<!--
<tr><td align="right">
<%  if (showSubs.equals("1")) { %> <a href="<%=fileName%>.jsp?shwsubs=0" class="small_blue">Hide sub-categories</a> 
<% } else { %> <a href="<%=fileName%>.jsp?shwsubs=1" class="small_blue">Show sub-categories</a> <% } %>
</td></tr>
-->
<tr><td>
    
    <table cellpadding="0" cellspacing="0" border="0" class="tableview" width="100%">
    <tr class="grey_11">
        <th width="30" align=''>&nbsp;</th>
        <th width="250" align='left'>Name</th>
        <th width="100" align='left'>Classification</th>
        <th width="50" align='center'>Items</th>
        <th width="250" align='left'>Mobile Link</th>
        <th align='right'>Select</th>		
    <tr>
    </table>
    
    <ul id="itemlist" style="list-style-type: none; padding: 0; margin: 0;">
<% 
//System.out.println("The seize of list is "+list.size());
for (int i=0; i<list.size(); i++) { 
    cat = (ItemCategory) list.get(i);
    //System.out.println(cat.getUnique()+" cat "+cat.getParentUnique()+ "cat "+cat.getName1()+" catsubcategories "+cat.getSubCategories().get(0));
    if (!cat.getParentUnique().equals("root")) continue;
    sublist = cat.getSubCategories();
%>
    
    <li id="item_<%=cat.getUnique()%>">
        <table cellpadding="0" cellspacing="0" border="0" class="tableview" width="100%">
            <tr>
                <td width="30" align='left' class="grey_10"><%=(i+1)%><img src="/images/glass_dot.gif" height="1" width="10"></td>
                <td width="250" align='left'><a href="catdetails.jsp?unq=<%=cat.getUnique()%>&shwsubs=<%=showSubs%>&itype=<%=itype%>" class="small_blue"><%=cat.getName1()%></a></td>      
                <td width="100" align='center' class="grey_10"><%=cat.getClassification()%></td>
                <td width="50" align='center' class="grey_10"><%=cat.getItemCount()%></td>
                <td width="250" align='left' class="grey_10"><input type="text" size="60" value="<%=waplink%><%=cat.getUnique()%>"></td>
                <td align='right' class="grey_10"><input type="checkbox" name="del_<%=cat.getUnique()%>"></td>	
            </tr>

            <%
            if (sublist!=null) {
                for (int k=0; k<sublist.size(); k++) {
                    subcat = (ItemCategory) sublist.get(k);
            %>
            <tr bgcolor="#EEEEEE">
                <td width="20" align='left' class="grey_10">&nbsp;</td>
                <td width="250" align='left' class="grey_10"><%=(k+1)%>&nbsp;&nbsp;<a href="catdetails.jsp?unq=<%=subcat.getUnique()%>&shwsubs=<%=showSubs%>&itype=<%=itype%>"><%=subcat.getName1()%></a></td>
                <td width="100" align='center' class="grey_10"><%=subcat.getClassification()%></td>
                <td width="50" align='center' class="grey_10"><%=subcat.getItemCount()%></td>
                <td width="250" align='left' class="grey_10"><input type="text" size="60" value="<%=waplink%><%=subcat.getUnique()%>"></td>
                <td align='right' class="grey_10"><input type="checkbox" name="del_<%=subcat.getUnique()%>"></td>
            </tr>
            <% }
            }
            %>

        </table>
    </li>
<%
}
%>

</ul>

</td></tr>

<tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
<tr><td align="left">
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
        <tr>
                <td colspan="2" align="left" class="grey_11"><b>Add new category</b><br><br></td>
        </tr>
        <tr>
            <td align="left" class="grey_11">Name:&nbsp;<input type="text" name="addname" value="<%=addName%>" size="20">
            &nbsp;&nbsp;&nbsp; Parent: &nbsp;
            <select name="parent">
            <option value="root">Root</option>
            <% 	for (int k=0; k<list.size(); k++) {
                    cat = (ItemCategory) list.get(k);                    
                    if (!cat.getParentUnique().equals("root")) continue;                    
            %>
                    <option value="<%=cat.getUnique()%>"><%=cat.getName1()%></option>
            <% }%>
            </select>
            </td>
            <td align="right" ><input type="submit" name="add" value="&nbsp;&nbsp;&nbsp;Add&nbsp;&nbsp;&nbsp;"></td>
        </tr>
    </table>
    <br>
</td></tr>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td>
   <table cellspacing="0" cellpadding="0" border="0" width="100%">
        <tr>
            <td colspan="3" align="left" class="grey_11"><b>Delete selected categories</b></td>
            <td colspan="3" align="right"><input type="submit" name="delete" value="Delete"></td>
        </tr>
    </table>
</td></tr>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>&nbsp;</td></tr>

</table>

</form>
<script type="text/javascript">
    if ( navigator.appName.toLowerCase().indexOf('microsoft') == -1 )
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


