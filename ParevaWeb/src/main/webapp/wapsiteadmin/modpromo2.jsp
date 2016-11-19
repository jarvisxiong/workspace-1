<%@ include file="/WEB-INF/jspf/wapsiteadmin/modpromo2.jspf" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language="JavaScript">
function submitForm(thisForm, fvalue) {
    thisForm.ss.value=fvalue;
    thisForm.submit();
}
function win(urlPath) {
    var winl = (screen.width-400)/2;
    var wint = (screen.height-800)/2;
    var settings = 'height=800,width=400,directories=no,resizable=yes,status=yes,scrollbars=yes,menubar=no,location=no,top=' + wint + ',left=' + winl;
    newWin = window.open(urlPath,'sim',settings);
    newWin.focus();
}
function delwin(urlPath) {
    var winl = (screen.width-200)/2;
    var wint = (screen.height-100)/2;
    var settings = 'height=100,width=200,directories=no,resizable=no,status=no,scrollbars=no,menubar=no,location=no,top=' + wint + ',left=' + winl;
    delWin = window.open(urlPath,'mmsdel',settings);
    delWin.focus();
}
</script>
</head>
<body>

<form action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="ss" value="1">
<input type="hidden" name="srvc" value="<%=srvc%>">
<input type="hidden" name="cmd" value="<%=cmd%>">

<table cellspacing="0" cellpadding="0" border="0" width="95%">
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr>
            <td align="left" valign="bottom" class="big_blue"><b>Modify Service Module: <%=srvcTitle%></b></td>	
            <td align="right" valign="bottom" class="status_red"><b><%=statusMsg%></b>&nbsp;&nbsp;<a href="index.jsp">Back</a></td>
    </tr>
    </table>
    
</td></tr>			
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td valign="top" align="left">
    <br>
    
    <table cellspacing="0" cellpadding="6" border="0" width="100%">        
    <tr>    
        <td align="left" class="grey_12"><nobr><b>Domain:</b><img src="/images/glass_dot.gif" width="30" height="1">
            <b><%=domainName%></b>            
            </nobr>
        </td>
        <td align="right">
        <a href="javascript:win('http://<%=System.getProperty(dm + "_url")%>/simulator.jsp?pg=<%=subpage%>');">Open/refresh xhtml</a>
        </td>
    </tr>
    </table>
</td></tr>
<tr><td><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>
<tr><td valign="top" align="right"><input type="submit" name="ss" value="&nbsp;&nbsp;Save&nbsp;&nbsp;"></td></tr>    

<tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr> 
<% if (list.size()>0){ %>
<tr><td>
   
    <% for (int k=0; k<list.size(); k++) { 
        props = (String[]) list.get(k);        
        
    %> 
        <table cellspacing="0" cellpadding="2" border="1" width="100%">     
        <tr bgcolor="#DDDDDD">
        <td colspan="2">
            <table cellspacing="0" cellpadding="2" border="0" width="100%">     
            <tr bgcolor="#DDDDDD">
               <td class="red_12">
                    <b>Index: </b> 
                    <select name="ind_<%=props[0]%>" onChange="javascript:this.form.submit()">
                      <%    
                            int ind = Integer.parseInt(props[2]);                  
                            for (int p=1; p<=list.size(); p++) {
                        %>
                      <option value="<%=p%>" <%if (ind==p){%> selected <%}%>><%=p%>&nbsp;</option>
                        <% } %>
                      </select>
              
                    &nbsp;&nbsp;
                    <input type="text" name="item_<%=props[0]%>" value="<%=props[1]%>" size="20">
                    &nbsp;<%=props[3]%> / <%=props[4]%>                       
               </td>
               <td align="right" class="black_10">
                    Resample from profile 4 image to: 
                    <select name="restype">
                        <option value="full" <%if (restype.equals("full")){%> selected <%}%>>Full width</option>
                        <option value="shot" <%if (restype.equals("shot")){%> selected <%}%>>Screenshot width</option>
                        <option value="thumb" <%if (restype.equals("thumb")){%> selected <%}%>>Thumbnail width</option>
                        <option value="icon" <%if (restype.equals("icon")){%> selected <%}%>>Icon width</option>
                    </select>
                    <input type="submit" name="res_<%=props[0]%>" value="Resample">
                </td>                
            </tr>
            </table>
        </td></tr>
        <tr>
        <% for (int i=4; i>0; i--) {
            pprops = (String[]) pics.get(props[0] + "_" + i);
            
            if (i==2) out.println("</tr><tr>");
        %>
        <td align="center" class="grey_11" valign="top">   
            <table cellpadding="4" cellspacing="0" border="0" width="100%">
                <tr bgcolor="#EEEEEE">
                    <td align="left" class="blue_12"><b>Xhtml Profile <%=i%></b></td>
                    <td align="right" class="blue_11">
                     <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pprops[1])%>&srcp=<%=fileName%>&srvc=<%=srvc%>&pic=<%=pprops[0]%>&cmd=<%=cmd%>')">Delete</a>&nbsp;&nbsp;
                    </td>
                </tr>
               <tr><td colspan="2" align="center" class="grey_11" valign="top">
                <% if (i==4){%><input type="hidden" name="resrc_<%=props[0]%>" value="<%=pprops[1]%>"><%}%>
                    <br>
                    <% if (!pprops[1].equals("")) { %> 
                        <%=pprops[2]%>x<%=pprops[3]%> px<br>
                        <%=pprops[1]%><br>                
                       <a href="selpic.jsp?pic=<%=pprops[0]%>&cmd=<%=cmd%>&srvc=<%=srvc%>&fp=modpromo2"><img src="/images/javagames/promos/<%=pprops[1]%>" border="0"></a>
                    <% } else { %>[No Image]<%}%>
                    <br><a href="selpic.jsp?pic=<%=pprops[0]%>&cmd=<%=cmd%>&srvc=<%=srvc%>&fp=modpromo2"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
               </td></tr>
            </table>
        </td> 
        <%}%>
        </tr>
        </table>    
        <br>
    <% } %>
    
</td></tr>    

<%}%>
</table>

</form>
</body>
</html>


