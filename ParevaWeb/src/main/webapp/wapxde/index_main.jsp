<jsp:include page="/DEHeader"/>

<%
    if(session.getAttribute("deredirection")==null){
    String redirectto = (String) request.getParameter("redirecturl");
    
    if (redirectto == null) {
        redirectto = (String) request.getAttribute("redirecturl");
    }
    if (redirectto == null) {
        redirectto = (String) session.getAttribute("redirecturl");
    }
    if (redirectto != null) {
        response.sendRedirect(redirectto);
        session.setAttribute("deredirection","true");
        return;
    }
}
%>

<jsp:include page="/MainIndex"/>

<%
    String redirecturl = (String) request.getParameter("redirectto");
    if (redirecturl == null) {
        redirecturl = (String) request.getAttribute("redirectto");
    }
    if (redirecturl == null) {
        redirecturl = (String) session.getAttribute("redirectto");
    }
    if (redirecturl != null) {
        response.sendRedirect(redirecturl);
        return;
    }
%>