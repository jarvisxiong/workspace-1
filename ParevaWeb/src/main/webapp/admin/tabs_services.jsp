<%     
    String[][] _items = new String[4][2];
    _items[0][0] = "Standard Services"; _items[0][1] = "services.jsp";
    _items[1][0] = "Admin Services"; _items[1][1] = "services.jsp?stype=admin";
    _items[2][0] = "System Services"; _items[2][1] = "services.jsp?stype=system";
    _items[3][0] = "Add New Service"; _items[3][1] = "addservice.jsp";
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
