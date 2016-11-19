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
<a style="float:right;" href="index.jsp?regionid=<%=regionid%>&dm=<%=dm%>">Back</a><br />
<form action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="ss" value="1">
<input type="hidden" name="srvc" value="<%=srvc%>">
<input type="hidden" name="cmd" value="<%=cmd%>">
<input type="hidden" name="clnt" value="<%=client%>">

Number Of Elements: <input type="text" name="noe" value="<%=numberOfElements%>">

<input type="submit" value="save">

</form>
</body>
</html>


