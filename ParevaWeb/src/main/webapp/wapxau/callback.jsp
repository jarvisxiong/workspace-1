<%@page import="java.util.Enumeration"%>
<%
  Enumeration parameterList = request.getParameterNames();
  while( parameterList.hasMoreElements() )
  {
    String sName = parameterList.nextElement().toString();
   System.out.println("australiatesting: callback "+ sName+":"+request.getParameter(sName));
      
    }

  
  request.getServletContext().getRequestDispatcher("/AUCallback").forward(request,response);
  
  
%>