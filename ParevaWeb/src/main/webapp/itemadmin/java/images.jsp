<%@include file="/WEB-INF/jspf/itemadmin/java/images.jspf"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<title>UME Admin Panel</title>
<script language="JavaScript">
function win(urlPath) {
    var winl = (screen.width-200)/2;
    var wint = (screen.height-100)/2;
    var settings = 'height=100,width=200,directories=no,resizable=no,status=no,scrollbars=no,menubar=no,location=no,top=' + wint + ',left=' + winl;
    delWin = window.open(urlPath,'mmsdel',settings);
    delWin.focus();
}

function win2(urlPath) {
    var winl = (screen.width-400)/2;
    var wint = (screen.height-100)/2;
    var settings = 'height=100,width=400,directories=no,resizable=no,status=no,scrollbars=no,menubar=no,location=no,top=' + wint + ',left=' + winl;
    uplWin = window.open(urlPath,'uplwin',settings);
    uplWin.focus();
}
</script>
</head>
<body>


<table cellspacing="0" cellpadding="0" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="blue_14"><b>Java Game Details: </b> <span class="grey_12"><b><%=item.getTitle()%></b></span></td>
        <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>    
</td></tr>
<tr><td valign="top" align="left">
    <% int _curitem = Integer.parseInt(aReq.get("_curitem", tabIndex)); %>
    <%@ include file="/itemadmin/java/tabs.jsp" %>
    <br>
</td></tr>
   
<tr>
<td valign="top" align="left">   

<form enctype="multipart/form-data" action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="unq" value="<%=unique%>">
<input type="hidden" name="itype" value="<%=imgType%>">

    <table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr bgcolor="#FFFFFF">
           <td align="left" class="grey_12">
           Upload new resource:
           </td>
           <% if (!imgType.equals("web")) { %>
           <td align="center" class="grey_11">
               Resample uploaded image to: &nbsp;
               <select name="restype">
                    <option value="full" <%if (restype.equals("full")){%> selected <%}%>>Full width</option>
                    <option value="shot" <%if (restype.equals("shot")){%> selected <%}%>>Screenshot width</option>
                    <option value="thumb" <%if (restype.equals("thumb")){%> selected <%}%>>Thumbnail width</option>
                    <option value="icon" <%if (restype.equals("icon")){%> selected <%}%>>Icon width</option>
                </select>
           </td>
           <% } %>
           <td align="right">
                <input type="file" size="20" name="img" class="textbox" value="">
                <input type="submit" name="submit" value="Upload">
           </td>
        </tr>
    
    </table>

</form>
</td></tr>
    
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>
<tr><td>

<form action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="unq" value="<%=unique%>">
<input type="hidden" name="itype" value="<%=imgType%>">

    <table cellspacing="0" cellpadding="4" border="0" width="100%">
	<tr bgcolor="#FFFFFF">
           <td align="left" class="grey_12">
           Resample selected image:
           </td>
           <!--
            <td align="left" class="grey_12">
           Replace original: <input type="checkbox" name="repl" value="1" <% if (repl.equals("1")) {%> checked <%}%>>
           </td>
           -->
            <td align="left" class="grey_12">
           <select name="ftype">
               <option value="gif" <% if (fileType.equals("gif")) {%> selected <%}%>>Gif</option>
               <option value="jpg" <% if (fileType.equals("jpg")) {%> selected <%}%>>Jpg</option>
           </select>
           </td>
            <td align="left" class="grey_12"><nobr>
           Width: <input type="text" name="neww" size="4" value="<%=neww%>">
           &nbsp;&nbsp; 
           Height: <input type="text" name="newh" size="4" value="<%=newh%>">
            </nobr>
            </td>
            <td align="left" class="grey_10">
            Leave the other dimension<br>empty to constrain proportions
           </td>
           <td align="right">                
                <input type="submit" name="resample" value="Resample">
           </td>
        </tr>
    
    </table>


</td></tr>

<tr><td><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td align="right"><br><input type="submit" name="save" value="&nbsp;&nbsp;Save&nbsp;&nbsp;"></td></tr>

<tr><td>
<br>
<%
String type = "";
String key = "";

while (iterator.hasNext()) {
    key = (String) iterator.next();
    if (!key.startsWith(imgType)) continue;

    imageGroups = (java.util.List) item.getImageMap().get(key);
    if (imageGroups==null) imageGroups = new ArrayList();
    
    for (int k=0; k<imageGroups.size(); k++) {
        img = (ItemImageGroup) imageGroups.get(k);
        images = (java.util.List) img.getImages();              

%>
        <table cellpadding="2" cellspacing="0" border="1" width="100%" bgcolor="#ffffff">
         <tr bgcolor="#EEEEEE"><td class="blue_11" align="left" colspan="2">
            <table cellspacing="0" cellpadding="2" border="0" width="100%">     
            <tr bgcolor="#DDDDDD"><td class="blue_12">
            &nbsp;&nbsp;Index:
              <select name="ind_<%=img.getUnique()%>">
              <%    
                 for (int p=1; p<11; p++) {
                %>
              <option value="<%=p%>" <%if (img.getIndex()==p){%> selected <%}%>><%=p%>&nbsp;</option>
                <% } %>
              </select>
            &nbsp;&nbsp;&nbsp;&nbsp;Type:
              <select name="type_<%=img.getUnique()%>">             
                <option value="<%=imgType%>image" <%if (key.equals(imgType + "image")){%> selected <%}%>>Image</option>                
                <option value="<%=imgType%>icon" <%if (key.equals(imgType + "icon")){%> selected <%}%>>Icon</option> 
              </select>
            </td>
            <td align="right"><a href="javascript:win('del.jsp?igroup=<%=img.getUnique()%>&unq=<%=unique%>&itype=<%=imgType%>')"><span class="blue_12">Delete Images</span></a>&nbsp;</td>
            </tr>
            </table>
         </td>
         </tr>  
        <tr bgcolor="#ffffff" class="grey_11">	
         
<%	
	for (int i=0; i<images.size(); i++) {
           im = (ItemImage) images.get(i);         
           
           if (im.getPath().toLowerCase().endsWith("gif")) type = "GIF";
           else if (im.getPath().toLowerCase().endsWith("png")) type = "PNG";
           else type = "JPG";
           
           if (i==2) out.println("</tr><tr>");
        %>		
		
            <td align="center" valign="top" class="grey_11">
                <table cellpadding="4" cellspacing="0" border="0" width="100%"  >
                <tr>
                <td align="center" valign="top" class="grey_11" bgcolor="#EEEEEE">
                    <b>Profile: <%=im.getProfile()%></b>
                </td>
                </tr>
                <tr>
                <td align="center" valign="top" class="grey_11">        
                  <br>
              <% if (!im.getPath().equals("")) { %>
                  <%=im.getWidth()%>x<%=im.getHeight()%>, <%=type%><br>
                  <a href="upload.jsp?imgunq=<%=im.getUnique()%>&unq=<%=unique%>&itype=<%=imgType%>"><img src="<%=im.getPath()%>" border="0"></a>    
                  <br>
                  Select: <input type="radio" name="pic" value="<%=Misc.hex8Code(im.getPath())%>">
                  &nbsp;&nbsp;
                  <a href="javascript:win('del.jsp?picid=<%=Misc.hex8Code(im.getPath())%>&runq=<%=im.getUnique()%>&unq=<%=unique%>&itype=<%=imgType%>')">Delete</a>
              <% } else { %>
                <a href="upload.jsp?imgunq=<%=im.getUnique()%>&unq=<%=unique%>&itype=<%=imgType%>">[Add Image]</a> 
              <% } %>
                </tr>
                </table>
            </td>
<%
} 
%>
	
        </tr>
</table>
<br>
<% }
 }   
%>

</td></tr>

<tr><td align="right"><br><input type="submit" name="save" value="&nbsp;&nbsp;Save&nbsp;&nbsp;"></td></tr>

</form>
</table>


</body>
</html>


