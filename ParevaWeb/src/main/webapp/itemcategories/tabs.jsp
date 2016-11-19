<%     
    String[][] _items = new String[4][2];
    _items[0][0] = "Properties"; _items[0][1] = "/itemcategories/catdetails.jsp"; 
    System.out.println("CAT: " + cat);
    if (cat.getParentUnique().equals("root")) {
        _items[1][0] = "Sub Categories"; _items[1][1] = "/itemcategories/subcats.jsp";
    }
    _items[2][0] = "Items"; _items[2][1] = "/itemcategories/items.jsp";
    _items[3][0] = "Back"; _items[3][1] = "/itemcategories/index.jsp";
%>
<input type="hidden" name="_curitem" value="<%=_curitem%>">
<link rel="stylesheet" href="/images/tabs.css" type="text/css">    
    <ul id="tabs">
        <% 
        for (int _i=0; _i<_items.length;_i++) {
            if (_items[_i][0]==null) continue;
            
            if (_items[_i][1].indexOf("?")>-1) _items[_i][1] += "&_curitem=" + _i + "&unq=" + unique + "&itype=" + itype;
            else _items[_i][1] += "?_curitem=" + _i + "&unq=" + unique + "&itype=" + itype;
        %>   
        <% if (_curitem == _i) { %>
        <li><a href="<%=_items[_i][1]%>" class="on"><%=_items[_i][0]%></a></li>        
        <% } else {%>
        <li><a href="<%=_items[_i][1]%>"><%=_items[_i][0]%></a></li>    
        <% } %>
        <% } %>        
    </ul>
