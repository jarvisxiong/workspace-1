<%@ include file="/WEB-INF/jspf/wapsiteadmin/index.jspf" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script src="/lib/global_anyx.js" language="javascript"></script>
<script src="/lib/scriptaculous/prototype.js" language="javascript"></script>
<script src="/lib/scriptaculous/scriptaculous.js" language="javascript"></script>
<script language=JavaScript src="/lib/picker.js"></script>
<script type="text/javascript" src="/lib/jscolor/jscolor.js"></script>
<script language="JavaScript">
function change_color (form, form_element, subpage) {
    TCP.popupwithsubmit(form_element,0,form, "http://<%=System.getProperty(dm + "_url")%>/simulator.jsp?pg=" + subpage, window);
}


</script>
</head>
<body>
<%
Map<String, String> dParamMap = Anyxcms.domainParameters.get(request.getParameter("dm"));

%>

<form action="edittemplate.jsp?dm=<%=domain_for_edit_template%>" method="post" name="form1">


<table>
<tr>
<td>Header Section</td>
</tr>
<tr>
<td>
Background Color: 
</td>
<td>
 <td width="80" align="left"><nobr><input class="color" name="header_bgcolor" value="<%=dParamMap.get("header_bg")%>" ></nobr></td>				
</td>
</tr>
<tr>
<td>
Font Color: 
</td>
<td>
<td width="80" align="left"><nobr><input class="color" name="header_fontcolor" value="<%=dParamMap.get("header_font")%>"></nobr></td>				
</td>
</tr>
<tr>
<td>
Image: 
</td>
<td>
 <td width="80" align="left"><nobr><input class="color" name="header_image"></nobr></td>				
</td>
<td>
</td>
</tr>



<tr>
<td>Body Section</td>
</tr>
<tr>
<td>
Background Color: 
</td>
<td>
 <td width="80" align="left"><nobr><input class="color" name="body_bgcolor" value="<%=dParamMap.get("body_bg")%>"></nobr></td>				
</td>
</tr>
<tr>
<td>
Font Color: 
</td>
<td>
<td width="80" align="left"><nobr><input class="color" name="body_fontcolor" value="<%=dParamMap.get("body_font")%>"></nobr></td>				
</td>
</tr>
<tr>
<td>
Image: 
</td>
<td>
 <td width="80" align="left"><nobr><input class="color" name="body_image"></nobr></td>				
</td>
</tr>



<tr>
<td>Footer Section</td>
</tr>
<tr>
<td>
Background Color: 
</td>
<td>
 <td width="80" align="left"><nobr><input class="color" name="footer_bgcolor" value="<%=dParamMap.get("footer_bg")%>"></nobr></td>				
</td>
</tr>
<tr>
<td>
Font Color: 
</td>
<td>
<td width="80" align="left"><nobr><input class="color" name="footer_fontcolor" value="<%=dParamMap.get("footer_font")%>"></nobr></td>				
</td>
</tr>
<tr>
<td>
Image: 
</td>
<td>
 <td width="80" align="left"><nobr><input class="color" name="footer_image"></nobr></td>				
</td>
</tr>
<tr>
<td>
<input type="submit" value="Submit">
</td>
</tr>
</tr>
</table>
</form>
</body>
</html>