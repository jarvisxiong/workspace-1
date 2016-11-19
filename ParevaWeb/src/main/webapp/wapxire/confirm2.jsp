<%@page import="javax.servlet.RequestDispatcher"%>
<%@page import="java.util.Enumeration"%>
<%
  Enumeration parameterList = request.getParameterNames();
  while( parameterList.hasMoreElements() )
  {
    String sName = parameterList.nextElement().toString();
   System.out.println("IEConfirm: confirm2.jsp "+ sName+":"+request.getParameter(sName));
      
    }  
%>

<%
  
RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/IEConfirm");
 dispatcher.forward(request, response);
%>