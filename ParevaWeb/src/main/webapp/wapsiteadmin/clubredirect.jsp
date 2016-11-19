
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script src="/lib/global_anyx.js" language="javascript"></script>
<script src="/lib/scriptaculous/prototype.js" language="javascript"></script>
<script src="/lib/scriptaculous/scriptaculous.js" language="javascript"></script>

<script language="JavaScript">
    var newwindow = '';

    function openwin(url) {

        var winl = (screen.width-400)/2;
        var wint = (screen.height-800)/2;
        var settings = 'height=800,width=400,directories=no,resizable=yes,status=yes,scrollbars=yes,menubar=no,location=no,top=' + wint + ',left=' + winl;
        newwindow=window.open(url,'sim',settings);
        newwindow.focus();
    }

    function win(url) {

        var winl = (screen.width-400)/2;
        var wint = (screen.height-800)/2;
        var settings = 'height=800,width=400,directories=no,resizable=yes,status=yes,scrollbars=yes,menubar=no,location=no,top=' + wint + ',left=' + winl;
        if (!newwindow.closed && newwindow.location) {
            newwindow.location.href = url;
        }
    }

    function win2(urlPath) {
        var winl = (screen.width-200)/2;
        var wint = (screen.height-100)/2;
        var settings = 'height=100,width=200,directories=no,resizable=no,status=no,scrollbars=no,menubar=no,location=no,top=' + wint + ',left=' + winl;
        delWin = window.open(urlPath,'mmsdel',settings);
        delWin.focus();
    }

    function form_submit(thisform) {
        thisform.ss.value="";
        thisform.submit();
    }

</script>

<script language=JavaScript src="/lib/picker.js"></script>

<script language="JavaScript">
function change_color (form, form_element, subpage) {
    TCP.popupwithsubmit(form_element,0,form, "http://<%=System.getProperty(dm + "_url")%>/simulator.jsp?pg=" + subpage, window);
}
</script>

</head>
<body>

<form action="<%=fileName%>.jsp" method="post" name="form1">
<input type="hidden" name="ss" value="1">

<table cellspacing="0" cellpadding="0" border="0" width="95%">
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="big_blue"><b>Club Redirect Settings</b></td>
            <td align="right" valign="bottom" class="status_red"><b><%=statusMsg%></b>&nbsp;&nbsp;</td>
    </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<tr><td valign="top" align="left">
    <br>
    <table cellspacing="0" cellpadding="6" border="0">
        <tr>
            <td align="left" class="grey_12"><nobr><b>Domain:</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></td>
            <td align="left">
                <select name="dm" onChange="javascript:form_submit(this.form);">
                    <option value="null">[Select Domain]</option>
                    <%
                      for (int i=0; i<dms.size(); i++) {
                        props = (String[]) dms.get(i);
                    %>
                    <option value="<%=props[0]%>" <% if (props[0].equals(dm)){
                        foundDomain = true;
                        client=props[3];
                        domainlink = "http://" + props[2] + "/";
                    %> selected
                    <%}%>><%=props[1]%> -- http://<%=props[2]%></option>

                    <% } %>
                </select>
            </td>
        </tr>
   
    
    </table>
</td></tr>


<% if (list.size()>0) {  %>
<tr><td ><img src="/images/glass_dot.gif" width="1" height="6"></td></tr>
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="10" border="0" class="tableview" width="100%">
        <tr>
            <th width="500" align="left" class="grey_12"><nobr><b>Domain</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></th>
            <th width="500" align="left" class="grey_12"><b>Redirect URL</b></th>
            <th width="80">&nbsp;</th>

            <th>&nbsp;</th>
        </tr>
    </table>

    <ul id="itemlist" style="list-style-type: none; padding: 0; margin: 0;">
<%
    String editLink = "";
    String bgcolor = "";

    for (int i=0; i<list.size(); i++) {
        props = (String[]) list.get(i);
        ind = Integer.parseInt(props[3]);

        //System.out.println("props[8]: " + props[8]);

        if (props[8].equals("hotgame")) editLink = "modpromo.jsp?cmd=hotgame";
        else if (props[8].equals("weekgame")) editLink = "modpromo.jsp?cmd=weekgame";
        else if (props[8].equals("mainpromo")) editLink = "modpromo.jsp?cmd=mainpromo";
        else if (props[8].equals("contest")) editLink = "modcontest.jsp?cmd=";
        else if (props[8].equals("freetext")) editLink = "modtext.jsp?cmd=";
        else if (props[8].equals("hotmaster")) editLink = "modpromo2.jsp?cmd=hotmaster";
        else if (props[8].equals("hotbg")) editLink = "modpromo3.jsp?cmd=hotbg";
        else if (props[8].equals("hotvideo")) editLink = "modpromo4.jsp?cmd=hotvideo";
        else if (props[8].equals("exturl")) editLink = "modexturl.jsp?cmd=";
        else if (props[8].equals("menu")) editLink = "modmenu.jsp?cmd=";
        else if (props[8].equals("fonecta_edut")) editLink = "mod_fonecta_edut.jsp?cmd=";
        else editLink = "";

        //System.out.println(props[5] + ": " + ind + ": " + list.size());
        if (props[5].equals("Divider")) bgcolor = "#DDDDDD";
        else bgcolor = "#FFFFFF";

%>
    <li id="item_<%=props[0]%>">
    <table cellspacing="0" cellpadding="10" border="0" class="tableview" width="100%">
        <tr bgcolor="<%=bgcolor%>">
           
            <td width="500" align="left" class="grey_12"><input type="text" name="title_<%=props[0]%>" size=20 value="<%=props[2]%>"></td>
            <td width="500" align="left"><nobr><input type="text" size="8" name="font_<%=props[0]%>" value="<%=props[7]%>" style="background-color:<%=props[7]%>;"></td>
                
           <td align="right"><nobr>&nbsp;&nbsp;<a href="javascript:win2('delmodule.jsp?srvc=<%=props[0]%>&ind=<%=ind%>')"><span class="blue_11">[Update]</span></a></nobr></td>
        </tr>
    </table>
    </li>
    <% } %>
</ul>
</td></tr>
<tr><td align="right"><br><input type="submit" name="save" value="&nbsp;&nbsp;Save&nbsp;&nbsp;"></td></tr>
<tr><td><br><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<% }%>

</table>

</form>
<script type="text/javascript">
    if ( navigator.appName.toLowerCase().indexOf('microsoft') == -1 )
    Sortable.create(
        "itemlist",
        {
            onUpdate: function(){
                temp = Sortable.serialize('itemlist');
                temp = temp.split ( '&itemlist[]=' );
                makeRequest( 'updatemoduleorder.jsp?' + temp , '' );
                //win('http://<%=System.getProperty(dm + "_url")%>/simulator.jsp<%=sublink%>');
            }
        }
    );
</script>
</body>
</html>

<% DBHStatic.closeConnection(con); %>
