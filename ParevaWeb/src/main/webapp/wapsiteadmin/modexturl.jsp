<%@ include file="/WEB-INF/jspf/wapsiteadmin/modexturl.jspf" %>
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
        <td class="grey_12" valign="top"><nobr>Retrieve from URL:&nbsp;</nobr></td>
        <td class="grey_12" width="100%">
            <input type="text" name="exturl" value="<%=props[1]%>" size="100">
        </td>
    </tr>
    
    
     <tr>
        <td colspan="2" class="grey_12" align="right">
        <input type="submit" name="save" value="&nbsp;&nbsp;Save&nbsp;&nbsp;">
        </td>
     </tr>

     <% if (!urlcontent.equals("")) { %>
     
     <tr><td colspan="2">
             
        <table cellspacing="10" cellpadding="0" border="0" width="100%">
        <tr>
        <td valign="top" width="50%">
            <table cellspacing="0" cellpadding="4" border="0" width="100%">
            <tr bgcolor="#DDDDDD">
               <td class="red_12">
                    <b>HTML from URL</b>
               </td>
               
            </tr>
            <tr >
               <td class="grey_12">
            <textarea style="width:100%; height:500px;">
             <%=urlcontent.trim()%>
                   </textarea>
               </td>

            </tr>
            </table>
        </td>

        <td valign="top" align="left" width="50%">
            <table cellspacing="0" cellpadding="4" border="0" width="100%">
            <tr bgcolor="#DDDDDD">
               <td class="red_12">
                    <b>WYSIWYG</b>
               </td>

            </tr>
            <tr >
               <td class="grey_12" align="left">
                   <iframe src="<%=props[1]%>" style="width:100%; height:500px;"/>
               </td>

            </tr>
            </table>
        </td>
        
        </tr>
        
        </table>
    </td></tr>
     <%}%>
</table>
        
<%}%>
</td></tr>
</table>

</form>
</body>
</html>