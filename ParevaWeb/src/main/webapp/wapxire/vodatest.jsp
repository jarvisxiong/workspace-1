<%@page import="ume.pareva.sdk.Misc"%>
<%@page import="ume.pareva.sdk.MiscCr"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%

String transactionid=MiscCr.MD5("moonlight"+"||"+Misc.generateUniqueId());
String uri=java.net.URLEncoder.encode("partner:91909c32-e422-42e3-845a-d3cbf15af4fa","UTF-8");
String msisdnrouter="<img src=\"http://msisdn.sla-alacrity.com/authenticate/image.gif?transaction_id="+transactionid+"&uri=partner:91909c32-e422-42e3-845a-d3cbf15af4fa\" alt=\"\">";
System.out.println("manualvodafone VODAFONE TEST step1 "+transactionid+" msisdnrouter: "+msisdnrouter);



%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Vodafone test</title>
    </head>
    <body>
        <h1>Vodatest</h1>
   
       transactionid: <%=transactionid%>
       
       <br/> Send Step2 Request for above Step1
       <form action="msisdncheck.jsp" method="POST"> 
                                             <input type="hidden" name="transactionid" value="<%=transactionid%>">
                                             <input type="hidden" name="step2request" value="true">
                                             <input type="submit" value="Submit" >
       </form>
    <img src="http://msisdn.sla-alacrity.com/authenticate/image.gif?transaction_id=<%=transactionid%>&uri=<%=uri%>" alt="">
    </body>
</html>
