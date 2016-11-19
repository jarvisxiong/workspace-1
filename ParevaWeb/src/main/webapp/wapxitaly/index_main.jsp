<%@include file="coreimport.jsp" %>
<jsp:include page="/IT/GlobalWapHeaderIT"/>
<%--<jsp:include page="/IT/IndexMainIt"/>--%>
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
