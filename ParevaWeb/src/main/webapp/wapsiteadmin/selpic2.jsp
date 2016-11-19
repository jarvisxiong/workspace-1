<%@ include file="/WEB-INF/jspf/wapsiteadmin/selpic2.jspf" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language="JavaScript">
function win(urlPath) {
    var winl = (screen.width-200)/2;
    var wint = (screen.height-100)/2;
    var settings = 'height=100,width=200,directories=no,resizable=no,status=no,scrollbars=no,menubar=no,location=no,top=' + wint + ',left=' + winl;
    delWin = window.open(urlPath,'mmsdel',settings);
    delWin.focus();
}
</script>
</head>
<body>

<form enctype="multipart/form-data" action="<%=fileName%>.jsp" method="post">
    <input type="hidden" name="unq" value="<%=unique%>">

<table cellspacing="0" cellpadding="0" border="0" width="95%">
<tr>
<td valign="top" align="left">


	<table cellspacing="0" cellpadding="5" border="0" width="100%">
	<tr>
		<td align="left" valign="bottom" class="big_blue">Select/Change Image</td>
		<td align="right" class="status_red"><b><%=statusMsg%></b>&nbsp;&nbsp;
                <a href="header.jsp">Back</a>
		</td>
	</tr>
	
        </table>
        
</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<tr>
<td valign="top" align="left">
<br>
    <table cellpadding="5" cellspacing="0" border="0" width="100%">
       <tr><td class="blue_12">Upload new image:</td>
           <td class="grey_11">
               <input type="file" size="20" name="img" value="">
               &nbsp;
               Name:&nbsp; <input type="text" size="20" name="imgname" value="">
           </td>
        <td class="grey_11" align="right">
        <input type="submit" name="submit" value="Upload">
       </td>
      </tr>
   </table>
<br>
</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr>
<td valign="top" align="left">
<br>
    
    <table cellpadding="12" cellspacing="0" border="0" width="100%" bgcolor="#ffffff">
<%	
	int cc = 0;
        String alias = "";
	
	for (int i=0; i<files.length; i++) { 	
		fName = files[i].getName();
		lowName = fName.toLowerCase();
		if (!lowName.endsWith("jpg") && !lowName.endsWith("jpeg") && !lowName.endsWith("gif") && !lowName.endsWith("png")) continue;
		if (lowName.startsWith(".")) continue;
                
                if (fName.indexOf("_")>-1) {
                    alias = fName.substring(0, fName.lastIndexOf("_")) + fName.substring(fName.indexOf("."));
                    alias = alias.toUpperCase();
                }
                else alias = fName;
		
		if (cc==0) { %>		
			
		<tr bgcolor="#ffffff" class="grey_12">		
<%      } %>		
		
		<td align="center" valign="bottom">
                  <a href="header.jsp?updpic=<%=Misc.hex8Code(fName)%>&unq=<%=unique%>"><img src="/images/wap/source/<%=fName%>" border="0"></a>
		  <br>
		  <a href="header.jsp?updpic=<%=Misc.hex8Code(fName)%>&unq=<%=unique%>"><span class="grey_10"><%=alias%></span></a>	
                  <br>
                  <a href="javascript:win('del2.jsp?picid=<%=Misc.hex8Code(fName)%>&unq=<%=unique%>')">Delete</a>
		</td>
<%		
		cc++;
		if (cc==2) { out.println("</tr>"); cc=0; }
		
    } 

    if (cc>0) { 
		for (int i=0; i<(2-cc); i++) {  %> <td>&nbsp;</td> <% } 
		out.println("</tr>");
	}
%>
	
	</table>

	
</td>
</tr>				
</table>	
</form>

</body>
</html>


