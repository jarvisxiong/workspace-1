<%@page import="javax.servlet.RequestDispatcher"%>
<%@page import="java.util.Enumeration"%>
<%
  Enumeration parameterList = request.getParameterNames();
  while( parameterList.hasMoreElements() )
  {
    String sName = parameterList.nextElement().toString();
   System.out.println("ZAHEADER: index.jsp "+ sName+":"+request.getParameter(sName));
      
    }  
%>

<%
  
RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/ZAIndex");
 dispatcher.forward(request, response);
%>

