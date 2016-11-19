<%
System.out.println("======= ADMIN PUB ACT UPLOAD CALLED ========= ");
try { application.getRequestDispatcher("/umeadmin/act_upload.jsp").forward(request,response); }
catch (Exception e) { System.out.println(e); }
return;
%>