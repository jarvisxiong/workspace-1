<%@include file="/WEB-INF/jspf/itemcategories/items.jspf"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script src="/lib/global_anyx.js" language="javascript"></script>
<script type="text/javascript" src="/lib/scriptaculous/prototype.js"></script>
<script type="text/javascript" src="/lib/scriptaculous/scriptaculous.js"></script>

<link rel="stylesheet" href="/lib/previewplay.css" media="screen" />

<script src="/lib/global_anyx.js" language="javascript"></script>
<script type="text/javascript" src="/lib/JavaScriptFlashGateway.js"></script>

<script type="text/javascript" language="javascript">

    function submitform(src, form) {
        if (src=="sort") form.cat.value="";
        form.submit();
    }

    function submitForm2 (thisForm, ind, source) {
        thisForm.si.value=ind
        thisForm.formsrc.value=source;
        thisForm.submit();
    }

    function submitForm3 (thisForm, ind, source) {
        thisForm.si.value=ind
        thisForm.formsrc.value=source;
        thisForm.sss.value="";
        thisForm.cat.selectedIndex=0;
        thisForm.submit();
    }

</script>

</head>
</head>
<body>
<div class="previewsample"><script type="text/javascript" src="/lib/previewplay.js"></script></div>
<form action="<%=fileName%>.jsp" method="post" name="form1">
<input type="hidden" name="si" value="">
<input type="hidden" name="formsrc" value="">
<input type="hidden" name="unq" value="<%=unique%>">
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
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>
<% if (pageCount>1) { %>

    <tr>
    <td align="left" class="grey_11">
    <% if (sIndex>0) { %>
            <a href="javascript:submitForm2(document.form1,<%=(sIndex-maxCount)%>,0);"><span class="blue_11">PREVIOUS</span></a> |
    <% } else { %>
            PREVIOUS |
    <% }
            if (((sIndex+maxCount)/maxCount)<pageCount) { %>
    <a href="javascript:submitForm2(document.form1,<%=(sIndex+maxCount)%>,0);"><span class="blue_11">NEXT</span></a> |
    <% } else { %>
            NEXT |
    <%
            }

    for (int k=0; k<pageCount; k++) {
        int newIndex = k*maxCount;
        if (newIndex==sIndex) { out.print("<span class='red_11' style='font-family: Verdana;'><b>" + (k+1) + "</b></span>"); }
        else {
    %>
                <a href="javascript:submitForm2(document.form1,<%=newIndex%>,0);"><span class="blue_11" style="font-family: Verdana;"><b><%=(k+1)%></b></span></a>
    <%
                }
            }
    %>

    </td>
</tr>
<% } %>
<tr><td>

    <br>
    <table cellpadding="4" cellspacing="0" border="0" width="100%" class="tableview">

        <tr>
          <th align="left" width="150">Item</th>
          <th align="left" width="200">&nbsp;</th>
          <th align="center" width="100">Price Group</th>
          <th align="center" width="200">Status
          <!--
          <select name="clnt" onChange="javascript:this.form.submit();">
          <option value="" <% if (clnt.equals("")){%> selected <%}%>>[Select Client]</option>
          <% for (int i=0; i<clients.size(); i++) {
              Client client = clients.get(i);
            %>
           <option value="<%=client.getUnique()%>" <% if (clnt.equals(client.getUnique())){%> selected <%}%>><%=client.getName()%></option>
           <% } %>-->
          <!--  </select>-->
          </th>
          <th align="right">Provider</th>
       </tr>

    <%
    if (list!=null) {
        String itemStat = "";

        for (int i=0; i<list.size(); i++) {
            item = (ContentItem) list.get(i);

            if (item.getStatus()==1) itemStat = "<span class=\"green_11\">Active</span>";
            else itemStat = "<span class=\"red_11\">Disabled</span>";

            flashid = "flashid_" + Misc.generateUniqueId();
    %>

        <tr>
            <% if (itype.equals("mastertone") || itype.equals("truetone") || itype.equals("funtone")){
                itype = "mastertone";
            %>
            <td valign="top" align="left" width="300">
                <a href="/itemadmin/<%=itype%>/itemdetails.jsp?unq=<%=item.getUnique()%>"><%=item.getTitle()%></a>
            </td>
            <td width="50">
                <a href="javascript:void(0)" onclick="javascript:playSample('preview/<%=item.getOwner()%>/<%=item.getPreviewFile()%>.flv', '<%=flashid%>','<%=durl%>')">
            <img id="<%=flashid%>" src="/images/media_play_blue.gif" alt="" border="0"/></a>
            </td>

            <% } else if (itype.equals("ringtone")){
                Ringtone rtone = (Ringtone) item;
            %>
            <td valign="top" align="left" width="300"><a href="/itemadmin/<%=itype%>/itemdetails.jsp?unq=<%=item.getUnique()%>"><%=item.getTitle()%></a></td>
            <td width="50">
                <a href="javascript:void(0)" onclick="javascript:playSample('<%=rtone.getSampleFile()%>', 'poly_<%=flashid%>','<%=durl%>',2)">
            <img id="poly_<%=flashid%>" src="/images/media_play_blue.gif" alt="" border="0"/></a>

            &nbsp;&nbsp;
            <a href="javascript:void(0)" onclick="javascript:playSample('<%=rtone.getMonoSample()%>', 'mono_<%=flashid%>','<%=durl%>',1)">
            <img id="mono_<%=flashid%>" src="/images/media_play_blue.gif" alt="" border="0"/></a>
            </td>

            <% } else if (itype.equals("truetone") || itype.equals("funtone")){
            %>
            <td valign="top" align="left" width="300"><a href="/itemadmin/<%=itype%>/itemdetails.jsp?unq=<%=item.getUnique()%>"><%=item.getTitle()%></a></td>
            <td width="50">
                <a href="javascript:void(0)" onclick="javascript:playSample('<%=item.getPreviewFile()%>.mp3', '<%=flashid%>','<%=durl%>',3)">
            <img id="<%=flashid%>" src="/images/media_play_blue.gif" alt="" border="0"/></a>
            
            </td>
            
            <% } else { %>
            <td><a href="/itemadmin/<%=itype%>/itemdetails.jsp?unq=<%=item.getUnique()%>"><img src="<%=item.getPreviewFile()%>" border="0"></a></td>
            <td valign="top" align="left" width="200"><a href="<%=itype%>/itemdetails.jsp?unq=<%=item.getUnique()%>"><%=item.getTitle()%></a></td>
            <% } %>
            
            <td valign="top" align="center" width="100"><%=item.getPriceGroup()%></td>
            <td valign="top" align="center" width="200"><%=itemStat%></td>
            <td valign="top" align="right" class="black_10"><%=SdkTempCache.getUserGroupName(item.getOwner())%></td>
        </tr>

    <%
        }
    } %>
</table>
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


