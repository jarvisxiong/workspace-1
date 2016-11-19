<%@include file="coreimport.jsp" %>
<%
String landingloaded=(String) request.getAttribute("landingPage");
             //System.out.println("LANDING PAGE IN INDEX_MAIN "+landingloaded);
             
               if(landingloaded==null || landingloaded.equalsIgnoreCase("")) {
                 landingloaded=(String) session.getAttribute("landingPage");
             }            
        
             
             request.setAttribute("landingPage",landingloaded);
 



%>
<jsp:include page="/MainIndex"/>
<%
  String redirecturl=(String) request.getParameter("redirectto");
        if(redirecturl==null) redirecturl=(String) request.getAttribute("redirectto");
        if(redirecturl==null) redirecturl=(String) session.getAttribute("redirectto");
        if(redirecturl!=null)
        {
            response.sendRedirect(redirecturl);
            return;
        }



%>