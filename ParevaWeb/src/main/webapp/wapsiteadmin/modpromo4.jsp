<%@ include file="/WEB-INF/jspf/wapsiteadmin/modpromo4.jspf" %>
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
</script>
</head>
<body>

<form action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="ss" value="1">
<input type="hidden" name="srvc" value="<%=srvc%>">
<input type="hidden" name="cmd" value="<%=cmd%>">
<input type="hidden" name="clnt" value="<%=client%>">


<table cellspacing="0" cellpadding="0" border="0" width="95%">
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr>
            <td align="left" valign="bottom" class="big_blue"><b>Modify Service Module: <%=srvcTitle%></b></td>	
            <td align="right" valign="bottom" class="status_red"><b><%=statusMsg%></b>&nbsp;&nbsp;<a href="index.jsp&regionid=<%=regionid%>&dm=<%=dm%>">Back</a></td>
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

<tr><td><img src="/images/glass_dot.gif" height="20" width="1"></td></tr> 
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
                       <select name="game_<%=props[0]%>" onChange="javascript:this.form.submit()">
                    <option value="">[Select]</option>
                    <% for (int i=0; i<items.size(); i++) {
                        gprops = (String[]) items.get(i);
                    %>
                    <option value="<%=gprops[0]%>" <% if (gprops[0].equals(props[1])){ %> selected <% } %>><%=gprops[1]%></option>
                    <% } %>
                    </select>
               </td>                
            </tr>
            </table>
        </td></tr>
        <tr>
        <% for (int i=6; i>2; i--) {
            if (i==4) out.println("</tr><tr>");
        %>
        <td align="center" class="grey_11" valign="top">   
            <table cellpadding="4" cellspacing="0" border="0" width="100%">
                <tr bgcolor="#EEEEEE">
             <td align="left" class="blue_12"><b>Xhtml Profile <%=(i-2)%></b></td>
                </tr>
               <tr><td align="center" class="grey_11" valign="top">
                    <br>
                    <% if (!props[i].equals("")) { %>    
                       <img src="<%=props[i]%>" border="0">
                    <% } else { %>
                        [No Image]
                    <%}%>
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


