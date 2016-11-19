<%@ include file="/WEB-INF/jspf/wapsiteadmin/header.jspf" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language=JavaScript src="/lib/picker.js"></script>
<script language="JavaScript">
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

<form action="<%=fileName%>.jsp" method="post" name="form1">
<input type="hidden" name="ss" value="1">
<input type="hidden" name="dm" value="<%=dm%>">


<table cellspacing="0" cellpadding="0" border="0" width="95%">
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="big_blue"><b>Header and Footer</b></td>	
            <td align="right" valign="bottom" class="status_red"><b><%=statusMsg%></b>&nbsp;&nbsp;<a href="index.jsp">Back</a></td>
    </tr>
    </table>
    
</td></tr>			
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td><img src="/images/glass_dot.gif" height="6" width="1"></td></tr>
<tr><td align="right"><a href="<%=fileName%>.jsp?reload=1">Activate Changes</a></td>
<!--
<tr><td valign="top" align="left">
    <br>
    <table cellspacing="0" cellpadding="6" border="0" width="100%">        
    <tr>    
        <td align="left" class="grey_12"><nobr><b>Domain:</b><img src="/images/glass_dot.gif" width="35" height="1"></nobr></td>	
        <td align="left"><nobr>
        <select name="dm" onChange="javascript:this.form.submit()" read-only>
        <option value="null">[Select Domain]</option>
        <% for (int i=0; i<dms.size(); i++) {
                props = (String[]) dms.get(i);
        %>
        <option value="<%=props[0]%>" <% if (props[0].equals(dm)){%> selected <%}%>><%=props[1]%></option>
        
        <% } %> 
        </select>
        <% if (!dm.equals("") && !dm.equals("null")) {%>
        &nbsp;
        <a href="javascript:win('http://<%=System.getProperty(dm + "_url")%>/simulator.jsp');">Open/refresh xhtml</a>
        </nobr>
        </td>
        <td align="right" width="100%"><a href="<%=fileName%>.jsp?reload=1">Activate Changes</a>
        <%}%>
        </td>
    </tr>    
    </table>
</td></tr>
-->
<% if (!dm.equals("")) { %>

    <% if (list.size()>0) {  
            String[] pp1 = (String[]) list.get(0);
            String[] pp2 = (String[]) list.get(1);
            String[] pp3 = (String[]) list.get(2);
            String[] pp4 = (String[]) list.get(3);
    %>
<tr><td valign="top" align="left">
<input type="hidden" name="re1src" value="<%=pp1[4]%>">
<br><br>
        <table cellspacing="0" cellpadding="4" border="1" width="100%">     
        <tr bgcolor="#DDDDDD"><td colspan="2">
            <table cellspacing="0" cellpadding="4" border="0" width="100%">     
            <tr bgcolor="#DDDDDD">
               <td class="red_12"><b>Main Header</b></td>
               <td align="right" class="black_10">Resample from profile 4 image: <input type="submit" name="resample1" value="Resample"></td>
            </tr>
            </table>
        </td></tr>
        <tr bgcolor="#EEEEEE">
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp1[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(3)[3]%> px 
                 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp1[4])%>&srcp=<%=fileName%>&pic=<%=pp1[0]%>')">Delete</a>

             </td>
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp2[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(2)[3]%> px
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp2[4])%>&srcp=<%=fileName%>&pic=<%=pp2[0]%>')">Delete</a>
             </td>
        </tr>
        <tr>
            <td align="center" class="grey_11" valign="top"><br>
            <% if (!pp1[4].equals("")) { %> 
                <%=pp1[5]%>x<%=pp1[6]%> px<br>
                <%=pp1[4]%><br>                
               <a href="selpic2.jsp?unq=<%=pp1[0]%>&fp=header"><img src="/images/wap/<%=pp1[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp1[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>        
             <td align="center" class="grey_11" valign="top"><br>            
            <% if (!pp2[4].equals("")) { %> 
                 <%=pp2[5]%>x<%=pp2[6]%> px<br>
                <%=pp2[4]%><br>   
               <a href="selpic2.jsp?unq=<%=pp2[0]%>&fp=header"><img src="/images/wap/<%=pp2[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp2[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>   
        </tr>
        <tr bgcolor="#EEEEEE">
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp3[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(1)[3]%> px
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp3[4])%>&srcp=<%=fileName%>&pic=<%=pp3[0]%>')">Delete</a>
             </td>
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp4[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(0)[3]%> px
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp4[4])%>&srcp=<%=fileName%>&pic=<%=pp4[0]%>')">Delete</a>
             </td>
        </tr>
        <tr>
            <td align="center" class="grey_11" valign="top"><br>
            <% if (!pp3[4].equals("")) { %>  
                 <%=pp3[5]%>x<%=pp3[6]%> px<br>
                <%=pp3[4]%><br>   
               <a href="selpic2.jsp?unq=<%=pp3[0]%>&fp=header"><img src="/images/wap/<%=pp3[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp3[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>        
             <td align="center" class="grey_11" valign="top"><br>           
            <% if (!pp4[4].equals("")) { %>  
            <%=pp4[5]%>x<%=pp4[6]%> px<br>
                <%=pp4[4]%><br>     
               <a href="selpic2.jsp?unq=<%=pp4[0]%>&fp=header"><img src="/images/wap/<%=pp4[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp4[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>   
        </tr>
        </table>
</td></tr>
<%   
    pp1 = (String[]) list.get(4);
    pp2 = (String[]) list.get(5);
    pp3 = (String[]) list.get(6);
    pp4 = (String[]) list.get(7);
%>
<tr><td valign="top" align="left">
<input type="hidden" name="re2src" value="<%=pp1[4]%>">
<br><br>
        <table cellspacing="0" cellpadding="4" border="1" width="100%">     
        <tr bgcolor="#DDDDDD"><td class="red_12" colspan="2">
            <table cellspacing="0" cellpadding="4" border="0" width="100%">     
            <tr bgcolor="#DDDDDD">
               <td class="red_12"><b>Main Footer</b></td>
               <td align="right" class="black_10">Resample from profile 4 image: <input type="submit" name="resample2" value="Resample"></td>
            </tr>
            </table>
        
        </td></tr>
        <tr bgcolor="#EEEEEE">
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp1[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(3)[3]%> px 
                 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp1[4])%>&srcp=<%=fileName%>&pic=<%=pp1[0]%>')">Delete</a>

             </td>
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp2[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(2)[3]%> px
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp2[4])%>&srcp=<%=fileName%>&pic=<%=pp2[0]%>')">Delete</a>
             </td>
        </tr>
        <tr>
            <td align="center" class="grey_11" valign="top"><br>
            <% if (!pp1[4].equals("")) { %>   
            <%=pp1[5]%>x<%=pp1[6]%> px<br>
                <%=pp1[4]%><br>
               <a href="selpic2.jsp?unq=<%=pp1[0]%>&fp=header"><img src="/images/wap/<%=pp1[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp1[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>        
             <td align="center" class="grey_11" valign="top"><br>             
            <% if (!pp2[4].equals("")) { %>       
                <%=pp2[5]%>x<%=pp2[6]%> px<br>
                <%=pp2[4]%><br>
               <a href="selpic2.jsp?unq=<%=pp2[0]%>&fp=header"><img src="/images/wap/<%=pp2[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp2[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>   
        </tr>
         <tr bgcolor="#EEEEEE">
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp3[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(1)[3]%> px
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp3[4])%>&srcp=<%=fileName%>&pic=<%=pp3[0]%>')">Delete</a>
             </td>
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp4[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(0)[3]%> px
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp4[4])%>&srcp=<%=fileName%>&pic=<%=pp4[0]%>')">Delete</a>
             </td>
        </tr>
        <tr>
            <td align="center" class="grey_11" valign="top"><br> 
            <% if (!pp3[4].equals("")) { %>         
  <%=pp3[5]%>x<%=pp3[6]%> px<br>
                <%=pp3[4]%><br>
               <a href="selpic2.jsp?unq=<%=pp3[0]%>&fp=header"><img src="/images/wap/<%=pp3[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp3[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>        
             <td align="center" class="grey_11" valign="top"><br>             
            <% if (!pp4[4].equals("")) { %>   
        <%=pp4[5]%>x<%=pp4[6]%> px<br>
                <%=pp4[4]%><br>
               <a href="selpic2.jsp?unq=<%=pp4[0]%>&fp=header"><img src="/images/wap/<%=pp4[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp4[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>   
        </tr>
        </table>
</td></tr>

<%   
    pp1 = (String[]) list.get(8);
    pp2 = (String[]) list.get(9);
    pp3 = (String[]) list.get(10);
    pp4 = (String[]) list.get(11);
%>
<tr><td valign="top" align="left">
<input type="hidden" name="re3src" value="<%=pp1[4]%>">
<br><br>
        <table cellspacing="0" cellpadding="4" border="1" width="100%">     
        <tr bgcolor="#DDDDDD"><td class="red_12" colspan="2">
            <table cellspacing="0" cellpadding="4" border="0" width="100%">     
            <tr bgcolor="#DDDDDD">
               <td class="red_12"><b>Service Header</b></td>
               <td align="right" class="black_10">Resample from profile 4 image: <input type="submit" name="resample3" value="Resample"></td>
            </tr>
            </table>
        </tr>
        <tr bgcolor="#EEEEEE">
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp1[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(3)[3]%> px 
                 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp1[4])%>&srcp=<%=fileName%>&pic=<%=pp1[0]%>')">Delete</a>

             </td>
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp2[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(2)[3]%> px
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp2[4])%>&srcp=<%=fileName%>&pic=<%=pp2[0]%>')">Delete</a>
             </td>
        </tr>
        <tr>
            <td align="center" class="grey_11" valign="top"><br>
            <% if (!pp1[4].equals("")) { %>   
        <%=pp1[5]%>x<%=pp1[6]%> px<br>
                <%=pp1[4]%><br>
               <a href="selpic2.jsp?unq=<%=pp1[0]%>&fp=header"><img src="/images/wap/<%=pp1[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp1[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>        
             <td align="center" class="grey_11" valign="top"><br>          
            <% if (!pp2[4].equals("")) { %>   
        <%=pp2[5]%>x<%=pp2[6]%> px<br>
                <%=pp2[4]%><br>
               <a href="selpic2.jsp?unq=<%=pp2[0]%>&fp=header"><img src="/images/wap/<%=pp2[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp2[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>   
        </tr>
         <tr bgcolor="#EEEEEE">
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp3[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(1)[3]%> px
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp3[4])%>&srcp=<%=fileName%>&pic=<%=pp3[0]%>')">Delete</a>
             </td>
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp4[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(0)[3]%> px
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp4[4])%>&srcp=<%=fileName%>&pic=<%=pp4[0]%>')">Delete</a>
             </td>
        </tr>
        <tr>
            <td align="center" class="grey_11" valign="top"><br> 
            <% if (!pp3[4].equals("")) { %>     
      <%=pp3[5]%>x<%=pp3[6]%> px<br>
                <%=pp3[4]%><br>
               <a href="selpic2.jsp?unq=<%=pp3[0]%>&fp=header"><img src="/images/wap/<%=pp3[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp3[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>        
             <td align="center" class="grey_11" valign="top"><br>             
            <% if (!pp4[4].equals("")) { %>   
        <%=pp4[5]%>x<%=pp4[6]%> px<br>
                <%=pp4[4]%><br>
               <a href="selpic2.jsp?unq=<%=pp4[0]%>&fp=header"><img src="/images/wap/<%=pp4[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp4[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>   
        </tr>
        </table>
</td></tr>

<%   
    pp1 = (String[]) list.get(12);
    pp2 = (String[]) list.get(13);
    pp3 = (String[]) list.get(14);
    pp4 = (String[]) list.get(15);
%>
<tr><td valign="top" align="left">
<input type="hidden" name="re4src" value="<%=pp1[4]%>">
<br><br>
        <table cellspacing="0" cellpadding="4" border="1" width="100%">     
        <tr bgcolor="#DDDDDD"><td class="red_12" colspan="2">
            <table cellspacing="0" cellpadding="4" border="0" width="100%">     
            <tr bgcolor="#DDDDDD">
               <td class="red_12"><b>Service Footer</b></td>
               <td align="right" class="black_10">Resample from profile 4 image: <input type="submit" name="resample4" value="Resample"></td>
            </tr>
            </table>        
        </td></tr>
        <tr bgcolor="#EEEEEE">
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp1[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(3)[3]%> px 
                 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp1[4])%>&srcp=<%=fileName%>&pic=<%=pp1[0]%>')">Delete</a>

             </td>
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp2[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(2)[3]%> px
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp2[4])%>&srcp=<%=fileName%>&pic=<%=pp2[0]%>')">Delete</a>
             </td>
        </tr>
        <tr>
            <td align="center" class="grey_11" valign="top"><br>
            <% if (!pp1[4].equals("")) { %>       
    <%=pp1[5]%>x<%=pp1[6]%> px<br>
                <%=pp1[4]%><br>
               <a href="selpic2.jsp?unq=<%=pp1[0]%>&fp=header"><img src="/images/wap/<%=pp1[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp1[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>        
             <td align="center" class="grey_11" valign="top"><br>             
            <% if (!pp2[4].equals("")) { %>  
        <%=pp2[5]%>x<%=pp2[6]%> px<br>
                <%=pp2[4]%><br>
               <a href="selpic2.jsp?unq=<%=pp2[0]%>&fp=header"><img src="/images/wap/<%=pp2[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp2[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>   
        </tr>
         <tr bgcolor="#EEEEEE">
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp3[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(1)[3]%> px
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp3[4])%>&srcp=<%=fileName%>&pic=<%=pp3[0]%>')">Delete</a>
             </td>
             <td align="left" class="blue_12"><b>Xhtml Profile <%=pp4[3]%></b>&nbsp;&nbsp;&nbsp;width: <%=SdkTempCache.xhtmlProfiles.get(0)[3]%> px
             &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(pp4[4])%>&srcp=<%=fileName%>&pic=<%=pp4[0]%>')">Delete</a>
             </td>
        </tr>
        <tr>
            <td align="center" class="grey_11" valign="top"><br>  
            <% if (!pp3[4].equals("")) { %>    
       <%=pp3[5]%>x<%=pp3[6]%> px<br>
                <%=pp3[4]%><br>
               <a href="selpic2.jsp?unq=<%=pp3[0]%>&fp=header"><img src="/images/wap/<%=pp3[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp3[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>        
             <td align="center" class="grey_11" valign="top"><br>         
            <% if (!pp4[4].equals("")) { %> 
          <%=pp4[5]%>x<%=pp4[6]%> px<br>
                <%=pp4[4]%><br>
               <a href="selpic2.jsp?unq=<%=pp4[0]%>&fp=header"><img src="/images/wap/<%=pp4[4]%>" border="0"></a>
            <% } else { %>[No Image]<%}%>
            <br><a href="selpic2.jsp?unq=<%=pp4[0]%>&fp=header"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
            </td>   
        </tr>
        </table>
</td></tr>

    <% }%>

<% } %>

</table>
<br><br>
</form>
</body>
</html>