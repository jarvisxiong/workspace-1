<%     
    String[][] _items = new String[9][2];
    _items[0][0] = "Properties"; _items[0][1] = "/itemadmin/java.jsp";
    _items[1][0] = "Descriptions"; _items[1][1] = "/itemadmin/java/descriptions.jsp";
    _items[2][0] = "Web Pics"; _items[2][1] = "/itemadmin/java/images.jsp?itype=web";
    _items[3][0] = "Wap Pics"; _items[3][1] = "/itemadmin/java/images.jsp?itype=mob";
    _items[4][0] = "Promo Pics"; _items[4][1] = "/itemadmin/java/images.jsp?itype=promo";
    _items[5][0] = "Phone Support"; _items[5][1] = "/itemadmin/java/supphones_list.jsp";    
    //_items[6][0] = "Game Files"; _items[6][1] = "updatephones.jsp?ftype=game";  
   // _items[7][0] = "Demo Files"; _items[7][1] = "updatephones.jsp?ftype=demo";
    _items[6][0] = "Link Games"; _items[6][1] = "/itemadmin/java/linkgames.jsp";
    _items[7][0] = "Also Bought"; _items[7][1] = "/itemadmin/java/alsobought.jsp";
    _items[8][0] = "Back"; _items[8][1] = "/itemadmin/index.jsp"; 
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
