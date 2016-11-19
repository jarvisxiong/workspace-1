<%
try { application.getRequestDispatcher("/umeadmin/play.jsp").forward(request,response); }
catch (Exception e) { System.out.println(e); }
return;
%>