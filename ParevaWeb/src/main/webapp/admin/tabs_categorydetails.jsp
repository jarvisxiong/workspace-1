<%     
    String[][] _items = new String[3][2];
    _items[0][0] = "Properties"; _items[0][1] = "categoryDetails.jsp?ctid=" + ctid;
    _items[1][0] = "User Groups"; _items[1][1] = "categoriesAndUserGroups.jsp?ctid=" + ctid;
    _items[2][0] = "Back To Category List"; _items[2][1] = "categories.jsp";
%>
<link rel="stylesheet" href="/images/tabs.css" type="text/css">    
<ul id="tabs">
    <% 
    for (int _i=0; _i<_items.length;_i++) {    
    %>   
    <% if (_curitem == _i) { %>
    <li><a href="<%=_items[_i][1]%>" class="on"><%=_items[_i][0]%></a></li>        
    <% } else {%>
    <li><a href="<%=_items[_i][1]%>"><%=_items[_i][0]%></a></li>    
    <% } %>
    <% } %>        
</ul>
