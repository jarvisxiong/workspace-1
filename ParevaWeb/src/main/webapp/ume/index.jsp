<%@page import="ume.pareva.core.UmeHandleRequests"%>
<%@include file="/WEB-INF/jspf/coreimport.jspf"%>

<%
UmeHandleRequests handlerequests=null;
try{
ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
handlerequests=(UmeHandleRequests) ac.getBean("umehandlerequests");

}
catch(Exception e){
    e.printStackTrace();
}
handlerequests.handleRequest(request,response,session);

return;
%>