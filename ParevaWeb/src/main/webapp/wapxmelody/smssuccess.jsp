<%@include file="zaheader.jsp"%>
<%
String mobileno=(String) request.getParameter("msisdn");
String status = "Thanks! You should receive an sms shortly. "
        + "Reply <b>YES</b> to confirm you are 18+ and start your <b>FREE</b> Trial.";


 PrintWriter writer = response.getWriter();
 Map<String, Object> context = new HashMap();
 
context.put("smssuccess","true"); 
context.put("gotomain","http://"+ dmn.getDefaultUrl());
context.put("contenturl","http://"+ dmn.getContentUrl());
context.put("message",status);
engine.getTemplate("status").evaluate(writer, context);
String output = writer.toString();


%>