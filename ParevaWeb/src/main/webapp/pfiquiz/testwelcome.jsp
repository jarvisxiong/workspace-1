<%@page import="java.util.Enumeration"%>
<%
Enumeration e = request.getParameterNames();
    for (;e.hasMoreElements();) {
        String elem = (String) e.nextElement();
        System.out.println("compredirection testWelcome "+elem+":"+request.getParameter(elem));
                }

    
    
Enumeration eh=request.getHeaderNames();
for(;eh.hasMoreElements();){
    String elem = (String) eh.nextElement();
        System.out.println("compredirection testWelcome "+elem+":"+request.getHeader(elem));
    
}

%>