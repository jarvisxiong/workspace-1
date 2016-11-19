<%@include file="coreimport.jsp" %>
<jsp:include page="/IT/TerminateBulkIT"/>
<%
    SdcRequest aReq = new SdcRequest(request);
    String stylesheet = aReq.getStylesheet();
    String pageEnc = aReq.getEncoding();
    response.setContentType("text/html; charset=" + pageEnc);
    String fileName = request.getServletPath();
    fileName = fileName.substring(fileName.lastIndexOf("/")+1);
    fileName = fileName.substring(0,fileName.lastIndexOf("."));
    
    Integer bCount = (Integer)request.getAttribute("bCount");
    Integer itCount = (Integer)request.getAttribute("itCount");
    String statusMsg = "";
    String nbrs = aReq.get("msisdn");

%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">

</head>
<body>

<form action="<%=fileName%>.jsp" method="post" style="padding: 0px; margin: 0px;">
  
<table cellspacing="0" cellpadding="0" border="0" width="99%">
<tr><td>    
            <table cellspacing="0" cellpadding="4" border="0" width="100%">
            <tr>
                <td align="left" valign="bottom" class="big_blue">Bulk Stop</td>
                <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
            </tr>
        </table>
</td></tr>        
<tr><td>&nbsp;</td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td>Copy MSISDNs in the field below. One number per each row in international format (27123456789).</td></tr>
<tr><td>&nbsp;</td></tr>
<tr><td>  
    
    <table cellspacing="0" cellpadding="5" border="0" style="width:100%;">
    <tr>
            <td>
                <textarea name="msisdn" style="width:200px; height:300px; resize:none;"><%=nbrs%></textarea>
            </td>
            <td align="left" valign="top" width="80%">
                <span class="blue_14">
                <%if(itCount!=null && itCount>0){%>
                    Disabled pending tickets: <%=bCount%>
                        <br><br>Italy termination successful: <%=itCount%>
                    <%}%>
                </span>     
            </td>    
    </tr>
    <tr>
            <td colspan="2"><input type="submit" name="submit" value="Submit" style="width:200px;"></td>
    </tr>
    </table>

</td></tr>
</table>
            
</form>            

</body>
</html>