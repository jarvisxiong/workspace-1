<%@ include file="/WEB-INF/jspf/itemadmin/alltonecheck.jspf" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<link rel="stylesheet" href="/lib/previewplay.css" media="screen" />

<script src="/lib/global_anyx.js" language="javascript"></script>
<script type="text/javascript" src="/lib/JavaScriptFlashGateway.js"></script>

<script type="text/javascript" language="javascript">

    function submitform(src, form) {
        if (src=="sort") form.cat.value="";
        form.submit();
    }

    function submitForm2 (thisForm, ind, source) {
        thisForm.si.value=ind
        thisForm.formsrc.value=source;
        thisForm.submit();
    }

    function submitForm3 (thisForm, ind, source) {
        thisForm.si.value=ind
        thisForm.formsrc.value=source;
        thisForm.sss.value="";
        thisForm.submit();
    }

</script>

</head>
<body>
<div class="previewsample"><script type="text/javascript" src="/lib/previewplayCms.js"></script></div>
<form action="<%=fileName%>.jsp" method="post" name="form1">
<input type="hidden" name="si" value="">
<input type="hidden" name="formsrc" value="">

<table cellspacing="0" cellpadding="0" border="0" width="99%">
<tr><td valign="top" align="left">

    <table cellspacing="0" cellpadding="3" border="0" width="100%">
        <tr>
            <td align="left" valign="bottom" class="big_blue">AllTone Check</td>
            <td align="right" class="status_red"><b><%=statusMsg%></b></td>
        </tr>
    </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<tr><td>


</table>

</form>

</body>
</html>






