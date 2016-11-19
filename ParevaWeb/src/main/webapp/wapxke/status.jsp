<%@page import="java.util.Enumeration"%>
<%
  Enumeration parameterList = request.getParameterNames();
  while( parameterList.hasMoreElements() )
  {
    String parameterName = parameterList.nextElement().toString();
    String parameterValue=request.getParameter(parameterName);
    System.out.println("Kenya SMS Callback Url ParameterName: "+parameterName);
    System.out.println("Kenya SMS Callback Url ParameterValue: "+parameterValue);
 %>  
    <%=parameterName%>: <%=parameterValue%>
  <%     
    }
  %>
