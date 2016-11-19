<%
System.out.println(request.getParameter("region"));
request.getServletContext().getRequestDispatcher("/FetchClubByRegion").forward(request, response);

%>