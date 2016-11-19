<%@include file="/WEB-INF/jspf/itemadmin/java/descriptions.jspf"%>




<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<script language="javascript">
function submitForm (thisForm) {
	thisForm.del.value="1";
	thisForm.submit();
}
</script>
</head>
<body>


<form action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="unq" value="<%=unique%>">

<table cellspacing="0" cellpadding="0" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr>
            <td align="left" valign="bottom" class="blue_14"><b>Java Game Details: </b> <span class="grey_12"><b><%=orgTitle%></b></span></td>
        <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>    
</td></tr>
<tr><td valign="top" align="left">
    <% int _curitem = Integer.parseInt(aReq.get("_curitem", "1")); %>
    <%@ include file="/itemadmin/java/tabs.jsp" %>
    <br>
</td></tr>
   
<tr>
<td valign="top" align="left">   

<tr><td valign="top" align="left">
    <br>
    <table cellspacing="0" cellpadding="6" border="0">        
    <tr>    
        <td align="left" class="grey_12"><nobr><b>Language:</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></td>	
        <td align="left">
            <select name="langcode" onChange="javascript:this.form.submit()">
            <option value="null">[Select Language]</option>
            <% for (int i=0; i<lgs.size(); i++) {
                props = (String[]) lgs.get(i);
            %>
            <option value="<%=props[2]%>" <% if (langCode.equalsIgnoreCase(props[2])){%> selected <%}%>><%=props[1]%> (<%=props[2]%>)</option>

            <% } %> 
            </select>  
        </td>
    </tr>    
    </table>
</td>
</tr>
<tr><td><img src="/images/glass_dot.gif" height="10" width="100%"></td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<% if (!langCode.equals("")) { %>
<tr><td valign="top" align="left">
    <br>
    <table cellspacing="0" cellpadding="8" border="0">        
      <tr>    
        <td align="left" class="grey_12" valign="top"><nobr><b>Game Title:</b></nobr></td>	
        <td align="left">
            <input type="text" name="title" value="<%=Misc.utfToUnicode(title, pageEnc)%>" size="52" class="textbox">
        </td>
    </tr>     
    <tr>    
        <td align="left" class="grey_12" valign="top"><nobr><b>Punch Line:</b><img src="/images/glass_dot.gif" width="30" height="1"></nobr></td>	
        <td align="left">
            <textarea name="punch" cols="50" rows="3" class="textbox"><%=Misc.utfToUnicode(punch, pageEnc)%></textarea>
        </td>
    </tr>    
    
    <tr>    
        <td align="left" class="grey_12" valign="top"><nobr><b>Short Desc:</b></nobr></td>	
        <td align="left">
            <textarea name="short" cols="50" rows="3" class="textbox"><%=Misc.utfToUnicode(dshort, pageEnc)%></textarea>
        </td>
    </tr>    
    
     <tr>    
        <td align="left" class="grey_12" valign="top"><nobr><b>Long Desc:</b></nobr></td>	
        <td align="left">
            <textarea name="long" cols="50" rows="6" class="textbox"><%=Misc.utfToUnicode(dlong, pageEnc)%></textarea>
        </td>
    </tr>    
    
    <tr>    
        <td align="left" class="grey_12" valign="top"><nobr><b>Very Long Desc:</b></nobr></td>	
        <td align="left">
            <textarea name="verylong" cols="50" rows="6" class="textbox"><%=Misc.utfToUnicode(verylong, pageEnc)%></textarea>
        </td>
    </tr>  
    
        
   <tr>    
        <td align="left" class="grey_12" valign="top"><nobr><b>Picture 1 Caption:</b></nobr></td>	
        <td align="left">
            <input type="text" name="pic1" value="<%=Misc.utfToUnicode(pic1, pageEnc)%>" size="52" class="textbox">
        </td>
    </tr>   
    </table>
</td></tr>
<tr><td><img src="/images/glass_dot.gif" height="10" width="100%"></td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td><img src="/images/glass_dot.gif" height="10" width="100%"></td></tr>
<tr><td align="right"><input type="submit" name="save" value="&nbsp;&nbsp;&nbsp;Save&nbsp;&nbsp;&nbsp;"></td></tr>

<%}%>


</td>
</tr>
</table>

</form>

</body>
</html>




