<%@ include file="/WEB-INF/jspf/wapsiteadmin/modcontest.jspf" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language="JavaScript">
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
<input type="hidden" name="srvc" value="<%=srvc%>">

<table cellspacing="0" cellpadding="0" border="0" width="95%">
<tr><td valign="top" align="left">
    
    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr>
            <td align="left" valign="bottom" class="big_blue"><b>Modify Contest <%=srvcTitle%></b></td>	
            <td align="right" valign="bottom" class="status_red"><b><%=statusMsg%></b>&nbsp;&nbsp;<a href="index.jsp">Back</a></td>
    </tr>
    </table>
    
</td></tr>			
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
<tr><td valign="top" align="left">

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
<tr><td><img src="/images/glass_dot.gif" height="10" width="1"></td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td valign="top" align="left">    
   
<% if (props!=null) { %>
    <table cellspacing="0" cellpadding="6" border="0" width="100%"> 
    <tr><td colspan="2"><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>
    <!--
    <tr>
        <td class="grey_12"><nobr>Direct link:&nbsp;</nobr></td>
        <td class="grey_11" width="100%">/contestdetail.jsp?srvc=<%=srvc%></td>
    </tr>
    -->
    <tr>
        <td class="grey_12"><nobr>Contest Title:&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
           <input type="text" name="title" value="<%=Misc.utfToUnicode(props[1], pageEnc)%>" class="textbox">        
        </td>
    </tr>
    <tr>
        <td class="grey_12"><nobr>Contest ID&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
           <input type="text" name="coid" value="<%=Misc.utfToUnicode(props[10], pageEnc)%>" class="textbox">        
        </td>
    </tr>
    <tr>
        <td class="grey_12"><nobr>Body Text:&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
           <textarea name="body" cols="60" rows="3" class="textbox"><%=Misc.utfToUnicode(props[2], pageEnc)%></textarea>        
        </td>
    </tr>
    <tr>
        <td class="grey_12"><nobr>Game:&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
            <select name="game">
            <option value="">[Select]</option>
            <% for (int i=0; i<games.size(); i++) {
                gprops = (String[]) games.get(i);
            %>
            <option value="<%=gprops[0]%>" <% if (gprops[0].equals(props[7])){ %> selected <% } %>><%=gprops[1]%></option>
            <% } %>
            </select>
    </td></tr>   
    
    <tr>
        <td class="grey_12"><nobr>Option 1:&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
           <input type="text" name="opt1" size="63" value="<%=Misc.utfToUnicode(props[3], pageEnc)%>" class="textbox">              
        </td>
        </tr>
        <tr>
        <td class="grey_12"><nobr>Option 2:&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
           <input type="text" name="opt2" size="63" value="<%=Misc.utfToUnicode(props[4], pageEnc)%>" class="textbox">              
        </td>
        </tr>
        <tr>
        <td class="grey_12"><nobr>Option 3:&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
           <input type="text" name="opt3" size="63" value="<%=Misc.utfToUnicode(props[5], pageEnc)%>" class="textbox">              
        </td>
        </tr>
        <tr>
        <td class="grey_12"><nobr>Option 4:&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
           <input type="text" name="opt4" size="63" value="<%=Misc.utfToUnicode(props[6], pageEnc)%>" class="textbox">              
        </td>
        </tr>
        <tr>
        <td class="grey_12"><nobr>Footer Text:&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
           <textarea name="body2" cols="60" rows="3" class="textbox"><%=Misc.utfToUnicode(props[11], pageEnc)%></textarea>        
        </td>
    </tr>
        <tr>
        <td class="grey_12"><nobr>Add Free Text Field:&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
           <input type="checkbox" name="ftext" value="1" <% if (props[8].equals("1")) {%> checked <%}%>>              
        </td>
        </tr>
        <tr>
        <td class="grey_12"><nobr>Add Link to Other Contest:&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
           <input type="checkbox" name="link" value="1" <% if (props[9].equals("1")) {%> checked <%}%>>              
        </td>
        </tr>
     <tr>
        <td colspan="2" class="grey_12" align="right">
        <input type="submit" name="save" value="&nbsp;&nbsp;Save&nbsp;&nbsp;">
        </td>
     </tr>
    <tr><td colspan="2">
        <table cellspacing="0" cellpadding="0" border="1" width="100%">     
        <tr bgcolor="#DDDDDD">
        <td colspan="2">
            <table cellspacing="0" cellpadding="2" border="0" width="100%">     
            <tr bgcolor="#DDDDDD">
               <td class="red_12">
                    <b>Main page image</b>
               </td>
                <td align="right" class="black_10">
                    Resample from profile 4 image: <input type="submit" name="res2_<%=props[0]%>" value="Resample">
                </td>
            </tr>
            </table>
        </td></tr>
        <tr>
        <% for (int i=4; i>0; i--) {
            pprops = (String[]) pics.get(props[0] + "_prm_" + i);
            
            if (i==2) out.println("</tr><tr>");
        %>
            <td align="center" class="grey_11" valign="top">   
                <table cellpadding="4" cellspacing="0" border="0" width="100%">
                    <tr bgcolor="#EEEEEE">
                 <td align="left" class="blue_12"><b>Xhtml Profile <%=i%></b>&nbsp;&nbsp;&nbsp;width: <%=hh.get("" + i)%> px</td>
                    </tr>
                   <tr><td align="center" class="grey_11" valign="top">
                    <% if (i==4){%><input type="hidden" name="resrc_<%=props[0]%>_prm" value="<%=pprops[1]%>"><%}%>
                        <br>
                        <% if (!pprops[1].equals("")) { %> 
                            <%=pprops[2]%>x<%=pprops[3]%> px<br>
                            <%=pprops[1]%><br>                
                           <a href="selpic.jsp?pic=<%=pprops[0]%>&srvc=<%=srvc%>"><img src="/images/javagames/promos/<%=pprops[1]%>" border="0"></a>
                        <% } else { %>[No Image]<%}%>
                        <br><a href="selpic.jsp?pic=<%=pprops[0]%>&srvc=<%=srvc%>"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
                   </td></tr>
                </table>
            </td> 
        <%}%>
        </tr>           
        </table>
        
    <br>
    </td></tr>    
    
     <tr><td colspan="2">
        <table cellspacing="0" cellpadding="0" border="1" width="100%">     
        <tr bgcolor="#DDDDDD">
        <td colspan="2">
            <table cellspacing="0" cellpadding="2" border="0" width="100%">     
            <tr bgcolor="#DDDDDD">
               <td class="red_12">
                    <b>Contest page image</b>
               </td>
                <td align="right" class="black_10">
                    Resample from profile 4 image: <input type="submit" name="res_<%=props[0]%>" value="Resample">
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
                 <td align="left" class="blue_12"><b>Xhtml Profile <%=i%></b>&nbsp;&nbsp;&nbsp;width: <%=hh.get("" + i)%> px</td>
                    </tr>
                   <tr><td align="center" class="grey_11" valign="top">
                    <% if (i==4){%><input type="hidden" name="resrc_<%=props[0]%>" value="<%=pprops[1]%>"><%}%>
                        <br>
                        <% if (!pprops[1].equals("")) { %> 
                            <%=pprops[2]%>x<%=pprops[3]%> px<br>
                            <%=pprops[1]%><br>                
                           <a href="selpic.jsp?pic=<%=pprops[0]%>&srvc=<%=srvc%>"><img src="/images/javagames/promos/<%=pprops[1]%>" border="0"></a>
                        <% } else { %>[No Image]<%}%>
                        <br><a href="selpic.jsp?pic=<%=pprops[0]%>&srvc=<%=srvc%>"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
                   </td></tr>
                </table>
            </td> 
        <%}%>
        </tr>           
        </table>        
    
    </td></tr>    
      
        
        
        <tr>
        <td colspan="2" class="grey_12" align="right">
        <input type="submit" name="save" value="&nbsp;&nbsp;Save&nbsp;&nbsp;">
        </td>
        </tr>
    </table>
 
<%}%>
</td></tr>
</table>

</form>
</body>
</html>


