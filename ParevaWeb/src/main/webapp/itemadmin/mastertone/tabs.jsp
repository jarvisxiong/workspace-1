<%     
    String[][] _items = new String[3][2];
    _items[0][0] = "Properties"; _items[0][1] = "/itemadmin/mastertone/itemdetails.jsp"; 
    _items[1][0] = "Content Files"; _items[1][1] = "/itemadmin/mastertone/contentfiles.jsp";
    _items[2][0] = "Back"; _items[2][1] = "/itemadmin/index.jsp";
%>
<input type="hidden" name="_curitem" value="<%=_curitem%>">
<link rel="stylesheet" href="/images/tabs.css" type="text/css">    
    <ul id="tabs">
        <% 
        for (int _i=0; _i<_items.length;_i++) {
        if (_items[_i][1].indexOf("?")>-1) _items[_i][1] += "&_curitem=" + _i + "&unq=" + unique;
        else _items[_i][1] += "?_curitem=" + _i + "&unq=" + unique;
        %>   
        <% if (_curitem == _i) { %>
        <li><a href="<%=_items[_i][1]%>" class="on"><%=_items[_i][0]%></a></li>        
        <% } else {%>
        <li><a href="<%=_items[_i][1]%>"><%=_items[_i][0]%></a></li>    
        <% } %>
        <% } %>        
    </ul>
