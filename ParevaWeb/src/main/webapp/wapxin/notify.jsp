<%
System.out.println("MobBill Sending Notification");

request.getServletContext().getRequestDispatcher("/IN/Notify").forward(request,response);

%>
