<%@page import="java.util.Enumeration"%>
<%
    
    Enumeration enums=request.getParameterNames();
    while(enums.hasMoreElements()){
        String param=(String) enums.nextElement();
        System.out.println("franceparam "+ param+":"+request.getParameter(param));
    }
request.getServletContext().getRequestDispatcher("/FRCallback").forward(request,response);
%>