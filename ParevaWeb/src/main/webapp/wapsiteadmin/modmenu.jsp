<%@ include file="/WEB-INF/jspf/wapsiteadmin/modmenu.jspf" %>

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
        <td align="left" valign="bottom" class="big_blue"><b>Modify Navi Menu</b></td>
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

    <input type="hidden" name="menuunique" value="<%=props[0]%>">

    <table cellspacing="0" cellpadding="6" border="0" width="100%">
    <tr><td colspan="4"><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>

    <tr>
        <td class="grey_12">
            <nobr>List Alignment:&nbsp;
            <select name="listalign">
                <option value="left" <%if (props[7].equals("left")){%> selected <%}%>>Left</option>
                <option value="center" <%if (props[7].equals("center")){%> selected <%}%>>Center</option>
                <option value="right" <%if (props[7].equals("right")){%> selected <%}%>>Right</option>
            </select>
            </nobr>
        </td>
        <td class="grey_12" colspan="3">
            <nobr>Matrix Alignment:&nbsp;
            <select name="matrixalign">
                <option value="left" <%if (props[8].equals("left")){%> selected <%}%>>Left</option>
                <option value="center" <%if (props[8].equals("center")){%> selected <%}%>>Center</option>
                <option value="right" <%if (props[8].equals("right")){%> selected <%}%>>Right</option>
            </select>
            </nobr>
        </td>
        
    </tr>

    <tr>
    <% for (int i=1; i<5; i++) { %>
    
        <td class="grey_12">
            <nobr>Profile <%=i%>:&nbsp;
            <select name="prof<%=i%>">
                <option value="list" <%if (props[i].equals("list")){%> selected <%}%>>List</option>
                <option value="list_div" <%if (props[i].equals("list_div")){%> selected <%}%>>List with Divider</option>

                <option value="matrix_2" <%if (props[i].equals("matrix_2")){%> selected <%}%>>Matrix 2xN</option>
                <option value="matrix_2_text" <%if (props[i].equals("matrix_2_text")){%> selected <%}%>>Matrix 2xN with Text</option>

                <option value="matrix_3" <%if (props[i].equals("matrix_3")){%> selected <%}%>>Matrix 3xN</option>
                <option value="matrix_3_text" <%if (props[i].equals("matrix_3_text")){%> selected <%}%>>Matrix 3xN with Text</option>

                <option value="matrix_4" <%if (props[i].equals("matrix_4")){%> selected <%}%>>Matrix 4xN</option>
                <option value="matrix_4_text" <%if (props[i].equals("matrix_4_text")){%> selected <%}%>>Matrix 4xN with Text</option>
                
            </select>
            </nobr>
        </td>
  <% } %>
      </tr>

      <tr><td colspan="4"><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
    
     <tr>
          <td colspan="2" align="left" class="black_10">
            Resample from profile 4 image to:
            <select name="restype">
                <option value="full" <%if (restype.equals("full")){%> selected <%}%>>Full width</option>
                <option value="shot" <%if (restype.equals("shot")){%> selected <%}%>>Screenshot width</option>
                <option value="thumb" <%if (restype.equals("thumb")){%> selected <%}%>>Thumbnail width</option>
                <option value="icon" <%if (restype.equals("icon")){%> selected <%}%>>Icon width</option>
            </select>
        </td>
        <td colspan="2" class="grey_12" align="right">
            <input type="submit" name="add" value="&nbsp;&nbsp;Add New Menu Item&nbsp;&nbsp;">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input type="submit" name="save" value="&nbsp;&nbsp;Save&nbsp;&nbsp;">
        </td>
     </tr>  
     
     

     <% for (int i=0; i<menuItems.size(); i++) {
            pprops = (String[]) menuItems.get(i);
     %>
     <tr><td colspan="4">
        <table cellspacing="0" cellpadding="0" border="1" width="100%">     
        <tr bgcolor="#DDDDDD">
        <td colspan="2">
            <table cellspacing="0" cellpadding="6" border="0" width="100%">
            <tr bgcolor="#DDDDDD">
                <td class="red_12">
                    Index:
                        <select name="ind_<%=pprops[0]%>">
                      <%
                        int ind = Integer.parseInt(pprops[1]);
                        for (int p=1; p<=menuItems.size(); p++) {
                        %>
                      <option value="<%=p%>" <%if (ind==p){%> selected <%}%>><%=p%>&nbsp;</option>
                        <% } %>
                      </select>

                    &nbsp;
                    Title:
                    <input type="text" name="title_<%=pprops[0]%>" value="<%=pprops[2]%>" size="20">
                    <!--
                    &nbsp;  Lang Key:
                    <input type="text" name="langkey_<%=pprops[0]%>" value="<%=pprops[3]%>" size="15">
                    -->
                    &nbsp;  Link:
                    <input type="text" name="link_<%=pprops[0]%>" value="<%=pprops[4]%>" size="25">
               </td>  
               
                <td align="right" class="black_10">
                    
                    <input type="submit" name="res_<%=pprops[0]%>" value="Resample">
                </td>
            </tr>
            </table>
        </td></tr>
        <tr>
        <% for (int k=4; k>0; k--) {
            gprops = (String[]) pics.get(pprops[0] + "_" + k);
            System.out.println("k: " + k + ": " + gprops[1]);
                       
            if (k==2) out.println("</tr><tr>");
        %>
            <td align="center" class="grey_11" valign="top">   
                <table cellpadding="4" cellspacing="0" border="0" width="100%">
                    <tr bgcolor="#EEEEEE">
                 <td align="left" class="blue_12"><b>Xhtml Profile <%=i%></b></td>
                 <td align="right" class="blue_11">
                     <a href="javascript:delwin('del.jsp?picid=<%=Misc.hex8Code(gprops[1])%>&srcp=<%=fileName%>&srvc=<%=srvc%>&pic=<%=gprops[0]%>')">Delete</a>&nbsp;&nbsp;
                </td>
                    </tr>
                   <tr><td colspan="2" align="center" class="grey_11" valign="top">
                    <% if (k==4){%><input type="hidden" name="resrc_<%=pprops[0]%>" value="<%=gprops[1]%>"><%}%>
                        <br>
                        <% if (!gprops[1].equals("")) { %>
                            <%=gprops[2]%>x<%=gprops[3]%> px<br>
                            <%=gprops[1]%><br>
                           <a href="selpic.jsp?pic=<%=gprops[0]%>&srvc=<%=srvc%>&fp=<%=fileName%>"><img src="/images/javagames/promos/<%=gprops[1]%>" border="0"></a>
                        <% } else { %>[No Image]<%}%>
                        <br><a href="selpic.jsp?pic=<%=gprops[0]%>&srvc=<%=srvc%>&fp=<%=fileName%>&restype=<%=restype%>"><span class="blue_11"><b><nobr>Change Image</nobr></b></span></a>
                   </td></tr>
                </table>
            </td> 
        <%}%>
        </tr>           
        </table>

     </td></tr>
     <%}%>
       
    <tr><td colspan="4" class="grey_12" align="right">
        <input type="submit" name="save" value="&nbsp;&nbsp;Save&nbsp;&nbsp;">
    </td></tr>
        
    </table>
 
<%}%>
</td></tr>
</table>

</form>
</body>
</html>


<% } catch (Exception ex) { System.out.println(ex); } %>