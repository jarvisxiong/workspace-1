<%     
    String[][] _items = new String[3][2];
    _items[0][0] = "Properties"; _items[0][1] = "clubDetails.jsp";
    _items[1][0] = "Messages"; _items[1][1] = "msgs.jsp";
    _items[2][0] = "Back"; _items[2][1] = "index.jsp";
%>
<input type="hidden" name="_curitem" value="<%=_curitem%>"> 
    <ul id="tabs">
        <% 
        for (int _i=0; _i<_items.length;_i++) {
        if (_items[_i][1].indexOf("?")>-1) _items[_i][1] += "&_curitem=" + _i + "&unq=" + unq;
        else _items[_i][1] += "?_curitem=" + _i + "&unq=" + unq;
        %>   
        <% if (_curitem == _i) { %>
        <li><a href="<%=_items[_i][1]%>" class="on jsLoad"><%=_items[_i][0]%></a></li>        
        <% } else {%>
        <li><a href="<%=_items[_i][1]%>" class="jsLoad"><%=_items[_i][0]%></a></li>    
        <% } %>
        <% } %>        
    </ul>

<script>
    $(".jsLoad").on("click", function(e) {
        var url = $(this).data("url");
        $('.contentHolder').load("../clubadmin/" + url);
    });
</script>