<%@page import="java.util.Enumeration"%>
<%
    //This page is called from BillingNotification
System.out.println("MobBill Sending Notification");

Enumeration inenum=request.getParameterNames();
while(inenum.hasMoreElements()){
    String param=(String) inenum.nextElement();
    String paramvalue=request.getParameter(param);
    System.out.println("pfibiling "+param+":"+request.getParameter(param));
    
    %>
 <%=param%>: <%=paramvalue%>
    
    
<%}%>