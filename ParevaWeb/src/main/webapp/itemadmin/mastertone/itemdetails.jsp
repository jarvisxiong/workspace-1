<%@include file="/WEB-INF/jspf/itemadmin/mastertone/itemdetails.jspf"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script src="http://cdn.jquerytools.org/1.2.4/full/jquery.tools.min.js"></script>

<script type="text/javascript" src="/lib/jquery/jquery.ui/jquery-ui-1.8.2.custom.min.js"></script>
<link rel="stylesheet" href="/lib/jquery/jquery.ui/css/smoothness/jquery-ui-1.8.2.custom.css" media="screen"/>

<script type="text/javascript" src="/lib/jquery/timepicker/jquery-ui-timepicker-addon.js"></script>
<link rel="stylesheet" href="/lib/jquery/timepicker/timepicker.css" media="screen"/>

<script>

$(function() {
    $("#release").datetimepicker({
        dateFormat: "dd.mm.yy",
        stepMinute: 15
    });
    $("#expire").datetimepicker({
        dateFormat: "dd.mm.yy",
        stepMinute: 15
    });

});

</script>

<script language="javascript">
<!--
function submitForm (thisForm) {
	thisForm.del.value="1";
	thisForm.submit();
}
//-->
</script>
</head>
<body>

<% if (!delete.equals("")) { %>
    
    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr><td align="left" valign="bottom" class="grey_12"><br><b>
    Are your sure you want to permanently delete this tone?
    </b>
    </td></tr>
    <tr><td  align="left" valign="bottom" class="normal_blue"><br>
    <a href="/itemadmin/deleteitem.jsp?unq=<%=unique%>&itype=mastertone"><b>DELETE</b></a>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <a href="<%=fileName%>.jsp?unq=<%=unique%>"><b>CANCEL</b></a>
    </td></tr>
    </table>
    
<% } else { %>

<form action="<%=fileName%>.jsp" method="post" name="myform">
<input type="hidden" name="unq" value="<%=unique%>">
<input type="hidden" name="del" value="">

<table cellspacing="0" cellpadding="0" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="blue_14"><b><%=title%> Details: </b> <span class="grey_12"><b><%=item.getSongName()%></b></span></td>
        <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>    
</td></tr>
<tr><td valign="top" align="left">
    <% int _curitem = Integer.parseInt(aReq.get("_curitem", "0")); %>
    <%@ include file="/itemadmin/mastertone/tabs.jsp" %>
    <br>
</td></tr>

<tr>
<td valign="top" align="left">      
    
    <table cellpadding="6" cellspacing="0" border="0" width="100%">

    <tr>
    <td class="grey_11">Unique Id:</td>
    <td class="blue_11"><%=item.getUnique()%></td>
    </tr>        

    <tr>
    <td class="grey_11">Web Sample:</td>
    <td class="grey_11"><%=webSampleOk%> 
    &nbsp;&nbsp;&nbsp;<!--
    <a href="sendtest.jsp?unq=<%=unique%>" class="small_blue">Send Tone</a>-->
    </td>
    </tr>
    
    <tr>
    <td class="grey_11">Wap Sample:</td>
    <td class="grey_11"><%=wapSampleOk%></td>
    </tr>
<!--
    <tr>
    <td class="grey_11" valign="top">Data files:</td>
    <td class="grey_11">
        <% 
        Iterator iter = item.getFormatMap().keySet().iterator();
        while (iter.hasNext()) { 
            String key = (String) iter.next();
        %>
               <%=key%> : <%=(String) item.getFormatMap().get(key)%> <br>
        <% } %>
    </td>
    </tr>
-->
    <tr>
    <td class="grey_11">Song Title:</td>
    <td><input type="text" size="40" name="songname" class="textbox" value="<%=item.getSongName()%>"></td>
    </tr>
    <tr>
    <td class="grey_11">Artist:</td>
    <td><input type="text" size="40" name="artist" class="textbox" value="<%=item.getArtist()%>"></td>
    </tr>
    <tr>

    <tr>
    <td class="grey_11">Tone Type:</td><td>
    <select name="tonetype">
    <option value="1" <% if (item.getToneType().equals("1")){%> selected <% } %>>Real Tone (Original Artist)</option>
    <option value="4" <% if (item.getToneType().equals("4")){%> selected <% } %>>True Tone (Cover Artist)</option>
    <option value="5" <% if (item.getToneType().equals("5")){%> selected <% } %>>Fun Tone & Sound Effect</option>
    </select>
    </td>
    </tr>

    <tr>
    <td class="grey_11">Published:</td>
    <td class="grey_11">
    <input type="radio" name="publ" value="1" <% if (item.getStatus()==1){%> checked ><span class="green_12"><b>YES</b></span><% } else {%>>Yes <%}%>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="publ" value="2" <% if (item.getStatus()==2){%> checked ><span class="red_12"><b>NO</b></span><% } else {%>>No <%}%>

    </td>
    </tr>
    <tr>
        <td class="grey_11">Release:</td>
        <td><input type="text" name="release" id="release" value="<%=sdf.format(item.getRelease())%>"/></td>
    </tr>
    <tr>
        <td class="grey_11">Expire:</td>
        <td><input type="text" name="expire" id="expire" value="<%=sdf.format(item.getExpire())%>"/></td>
    </tr>
    
    <tr>
    <td class="grey_10">Request Count:</td>
    <td class="grey_10"><%=item.getRequestCount()%></td>
    </tr>
    <tr>
	<td class="grey_10">Content Provider:</td>
	<td class="grey_10">                
           <select name="owner" id="owner" disabled>
            <option value="">[Select Provider]</option>
            <% 
               for (int i=0; i<cps.size(); i++) { 
                    ContentProvider cp = cps.get(i);
            %>           
                <option value="<%=cp.getUnique()%>" <% if (item.getOwner().equals(cp.getUnique())){%> selected <%}%>><%=cp.getName()%></option>

            <% } %>
            </select>
	</td>
        </tr>       
    <tr>
    <td class="grey_10">Uploaded:</td>
    <td class="grey_10"><%=item.getCreated()%></td>
    </tr>
    <tr>
    <td class="grey_10">Last request:</td>
    <td class="grey_10"><%=item.getLastAccessed()%></td>
    </tr>


    <tr>
    <td class="grey_11">Names/SMS Codes:</td>
    <td class="grey_11">
        <table cellpadding="5" cellspacing="0" border="0">
            <tr>
            <td class="grey_11">1:</td><td><input type="text" size="20" name="name1" class="textbox" value="<%=item.getName1().toUpperCase()%>"></td>
            <td class="grey_11">2:</td><td><input type="text" size="20" name="name2" class="textbox" value="<%=item.getName2().toUpperCase()%>"></td>
            </tr>
            <tr>
            <td class="grey_11">3:</td><td><input type="text" size="20" name="name3" class="textbox" value="<%=item.getName3().toUpperCase()%>"></td>
            <td class="grey_11">4:</td><td><input type="text" size="20" name="name4" class="textbox" value="<%=item.getName4().toUpperCase()%>"></td>
            </tr>
        </table>
            
    </td></tr>

    
    <td class="grey_11">Price Group:</td>
    <td class="grey_11">
        <select name="prcg">
            <option value="">[Select]</option>
            <% for (int i=1; i<10; i++) {%>
            <option value="<%=i%>" <% if (item.getPriceGroup()==i){%> selected <%}%>><%=i%>&nbsp;&nbsp;&nbsp;&nbsp;</option>
            <%}%>
        </select>
    </td>
    </tr>

    
    <tr>
	<td class="grey_11" valign="top">Categories:</td><td>
	<select class="textbox" name="catg" multiple size=12>
	<%
		
            for (int i=0; i<cats.size(); i++) {
                ItemCategory ic = cats.get(i);
	%>
	<option value="<%=ic.getUnique()%>" <% if (item.getCategory().indexOf(ic.getUnique())>-1){%> selected <%}%>><%=ic.getName1()%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
	<%
		if (ic.getSubCategories().size()>0) {
                    for (int k=0; k<ic.getSubCategories().size(); k++) {
                        ItemCategory icc = ic.getSubCategories().get(k);
	%>
	<option value="<%=icc.getUnique()%>" <% if (item.getCategory().indexOf(icc.getUnique())>-1){%> selected <%}%>>&nbsp;:&nbsp;<%=icc.getName1()%></option>
	<%
				}
			}
		} %>
	</select>
	</td>
    </tr>

    <tr>
    <td class="grey_11">NCB Status:</td><td>
    <input type="checkbox" name="teosto" value="1" <% if (item.getTeostoStatus()==1){%> checked <%}%>>
    </td>
    </tr>

    <tr>
    <td class="grey_11">NCB Title:</td>
    <td class="grey_11"><input type="text" size="40" name="tsongname" class="textbox" value="<%=item.getTeostoTitle()%>"></td>
    </tr>
    <tr>
    <td class="grey_11">NCB Artist:</td>
    <td class="grey_11"><input type="text" size="40" name="tartist" class="textbox" value="<%=item.getTeostoArtist()%>"></td>
    </tr>
    <tr>
    <td class="grey_11">NCB Composer:</td>
    <td class="grey_11"><input type="text" size="40" name="tcomposer" class="textbox" value="<%=item.getTeostoComposer()%>"></td>
    </tr>

    </table>

</td></tr>

<% if (allowMod) {%>
<tr>
<td align="left">
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr><td>
	<input type="button" name="delete" value="&nbsp;Delete&nbsp;" onClick="javascript:submitForm(this.form);">
	</td>                
	<td  align="right">
	<input type="submit" name="save" value="&nbsp;&nbsp;&nbsp;Save&nbsp;&nbsp;&nbsp;">
	</td></tr>
    </table>
</td></tr>
<% } %>

<tr><td ><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td ><span class="red_10"><b><%=statusMsg2%></b></span></td></tr>

</table>
</form>

<% } %>

<br><br><br>
</body>
</html>