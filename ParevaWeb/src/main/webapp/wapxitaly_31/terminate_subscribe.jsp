<%@include file="commonfunc.jsp"%>
<%
    try {
        application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/terminate.jsp").forward(request, response);
    } catch (Exception e) {
        System.out.println(e);
    }
%>