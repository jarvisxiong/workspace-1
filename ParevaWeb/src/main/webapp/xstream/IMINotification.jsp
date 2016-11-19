<%

System.out.println("iminotificationcall  ");


RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/BillingNotification");
 dispatcher.forward(request, response);
 

%>

Pareva Billing Handling