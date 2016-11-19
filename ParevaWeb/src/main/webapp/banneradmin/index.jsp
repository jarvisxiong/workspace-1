<%@ include file="/WEB-INF/jspf/coreimport.jspf" %>
<%
try {
	
	application.getRequestDispatcher("/" + System.getProperty("dir_" + request.getParameter("anyx_srvc")) 
			+"/index_" + Misc.getScreenType(request.getHeader("User-Agent")) 
			+".jsp").forward(request,response);
} 
catch (NullPointerException e) { System.out.println("ERROR in " + request.getParameter("anyx_srvc") + "/index.jsp: " + e); }
%>