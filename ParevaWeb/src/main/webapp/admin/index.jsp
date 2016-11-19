<%@include file="/WEB-INF/jspf/admin/index.jspf"%>
<html>
<head>
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>

<form method="post" action="index.jsp">
    
<table cellspacing="0" cellpadding="4" border="0" width="98%">
<tr><td>


        <table cellspacing="0" cellpadding="4" border="0" width="100%">
        <tr>
                <td align="left" valign="bottom" class="big_blue">General Information</td>
                <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
        </tr>
        </table>

</td></tr>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>
    <table cellspacing="0" cellpadding="4" border="0" width="100%">
        <tr>
                <td colspan="2" class="grey_11"><%=lp.get(17)%><br><br></td>
        </tr>

        <tr>
                <td bgcolor="<%=bgColor%>" align='left' class="grey_11">Free Memory:</td>
                <td bgcolor="<%=bgColor%>" align='right' class="grey_11"><%=df1.format(fMem/1024/1024)%> Mb</td>
        </tr>
        <tr>
                <td bgcolor="<%=bgColor%>" align='left' class="grey_11">Total Memory:</td>
                <td bgcolor="<%=bgColor%>" align='right' class="grey_11"><%=df1.format(tMem/1024/1024)%> Mb</td>
        </tr>
        <tr>
                <td bgcolor="<%=bgColor%>" align='left' class="grey_11">Max Memory:</td>
                <td bgcolor="<%=bgColor%>" align='right' class="grey_11"><%=df1.format(mMem/1024/1024)%> Mb</td>
        </tr>
        <tr>
                <td bgcolor="<%=bgColor%>" align='left' class="grey_11"><%=lp.get(18)%></td>
                <td bgcolor="<%=bgColor%>" align='right' class="grey_11"><b><%=propsLoaded%></b></td>
        </tr>
        <tr>
                <td bgcolor="<%=bgColor%>" align='left' class="grey_11"><%=lp.get(19)%></td>
                <td bgcolor="<%=bgColor%>" align='right' class="grey_11"><b><%=paramsLoaded%></b></td>
        </tr>
        <tr>
                <td colspan="2" align='right' class="status">
                <input type="submit" name="appgc" Value="Run GarbageCollection" style="width:150px;">&nbsp;&nbsp;
                <input type="submit" name="appReload" Value="<%=lp.get(30)%>" style="width:150px;"></td>
        </tr>
    </table>
</td></tr>


<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>


</table>

</form>
</body>
</html>