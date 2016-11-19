<%@ include file="/WEB-INF/jspf/wapsiteadmin/modtext.jspf" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language="JavaScript">
    var newwindow = '';
    
    function win(url) {

        var winl = (screen.width-400)/2;
        var wint = (screen.height-800)/2;
        var settings = 'height=800,width=400,directories=no,resizable=yes,status=yes,scrollbars=yes,menubar=no,location=no,top=' + wint + ',left=' + winl;

        if (!newwindow.closed && newwindow.location) {
            newwindow.location.href = url;
        }
        else {
            newwindow=window.open(url,'sim',settings);
            if (!newwindow.opener) newwindow.opener = self;
        }
        this.focus();
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
<input type="hidden" name="srvc" value="<%=srvc%>">

<table cellspacing="0" cellpadding="0" border="0" width="95%">
<tr><td valign="top" align="left">
    
    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="big_blue"><b>Modify Free Text</b></td>	
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
    
    <tr>
        <td class="grey_12" valign="top"><nobr>Main page text:&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
           <textarea name="title" cols="60" rows="6" class="textbox"><%=props[1]%></textarea>       
        </td>
    </tr>
    
     <tr>
        <td class="grey_12" valign="top"><nobr>Body text:&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
           <textarea name="body" cols="60" rows="10" class="textbox"><%=props[2]%></textarea>       
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
                    Resample from profile 4 image to: 
                    <select name="restype2">
                        <option value="full" <%if (restype2.equals("full")){%> selected <%}%>>Full width</option>
                        <option value="shot" <%if (restype2.equals("shot")){%> selected <%}%>>Screenshot width</option>
                        <option value="thumb" <%if (restype2.equals("thumb")){%> selected <%}%>>Thumbnail width</option>
                        <option value="icon" <%if (restype2.equals("icon")){%> selected <%}%>>Icon width</option>
                    </select>
                    <input type="submit" name="res2_<%=props[0]%>" value="Resample">
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
                 <td align="left" class="blue_12"><b>Xhtml Profile <%=i%></b></td>
                 <td align="right" class="blue_11">
                     <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pprops[1])%>&srcp=<%=fileName%>&srvc=<%=srvc%>&pic=<%=pprops[0]%>')">Delete</a>&nbsp;&nbsp;
                </td>
                    </tr>
                   <tr><td colspan="2" align="center" class="grey_11" valign="top">
                    <% if (i==4){%><input type="hidden" name="resrc_<%=props[0]%>_prm" value="<%=pprops[1]%>"><%}%>
                        <br>
                        <% if (!pprops[1].equals("")) { %> 
                            <%=pprops[2]%>x<%=pprops[3]%> px<br>
                            <%=pprops[1]%><br>                
                           <a href="selpic.jsp?pic=<%=pprops[0]%>&srvc=<%=srvc%>&fp=<%=fileName%>"><img src="/images/javagames/promos/<%=pprops[1]%>" border="0"></a>
                        <% } else { %>[No Image]<%}%>
                        <br><a href="selpic.jsp?pic=<%=pprops[0]%>&srvc=<%=srvc%>&fp=<%=fileName%>"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
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
                    <b>Service page image</b>
               </td>
                <td align="right" class="black_10">
                    Resample from profile 4 image:
                    <select name="restype1">
                        <option value="full" <%if (restype1.equals("full")){%> selected <%}%>>Full width</option>
                        <option value="shot" <%if (restype1.equals("shot")){%> selected <%}%>>Screenshot width</option>
                        <option value="thumb" <%if (restype1.equals("thumb")){%> selected <%}%>>Thumbnail width</option>
                        <option value="icon" <%if (restype1.equals("icon")){%> selected <%}%>>Icon width</option>
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
                     <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pprops[1])%>&srcp=<%=fileName%>&srvc=<%=srvc%>&pic=<%=pprops[0]%>')">Delete</a>&nbsp;&nbsp;
                </td>
                    </tr>
                   <tr><td colspan="2" align="center" class="grey_11" valign="top">
                    <% if (i==4){%><input type="hidden" name="resrc_<%=props[0]%>" value="<%=pprops[1]%>"><%}%>
                        <br>
                        <% if (!pprops[1].equals("")) { %> 
                            <%=pprops[2]%>x<%=pprops[3]%> px<br>
                            <%=pprops[1]%><br>                
                           <a href="selpic.jsp?pic=<%=pprops[0]%>&srvc=<%=srvc%>&fp=<%=fileName%>"><img src="/images/javagames/promos/<%=pprops[1]%>" border="0"></a>
                        <% } else { %>[No Image]<%}%>
                        <br><a href="selpic.jsp?pic=<%=pprops[0]%>&srvc=<%=srvc%>&fp=<%=fileName%>"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
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
