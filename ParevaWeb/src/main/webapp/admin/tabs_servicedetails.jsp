<%     
    String[][] _items = new String[4][2];
    _items[0][0] = "Properties"; _items[0][1] = "serviceDetails.jsp?sid=" + sid + "&stype=" + sType;
    _items[1][0] = "Service Menus"; _items[1][1] = "serviceMenus.jsp?sid=" + sid + "&stype=" + sType;
    _items[2][0] = "Sms Keywords"; _items[2][1] = "smsKeywords.jsp?sid=" + sid + "&stype=" + sType;
    _items[3][0] = "Back To Service List"; _items[3][1] = "services.jsp?stype=" + sType;
%>
<link rel="stylesheet" href="/images/tabs.css" type="text/css">    
<ul id="tabs">
    <% 
    for (int _i=0; _i<_items.length;_i++) {
        if (_i==1 && sType.equals("system")) continue;
    %>   
    <% if (_curitem == _i) { %>
    <li><a href="<%=_items[_i][1]%>" class="on"><%=_items[_i][0]%></a></li>        
    <% } else {%>
    <li><a href="<%=_items[_i][1]%>"><%=_items[_i][0]%></a></li>    
    <% } %>
    <% } %>        
</ul>
