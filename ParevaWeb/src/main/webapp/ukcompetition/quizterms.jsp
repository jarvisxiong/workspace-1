<%@ include file="coreimport.jsp"%>
<%@ include file="ukheader.jsp"%>

<%
 PrintWriter writer = response.getWriter();
 Map<String, Object> context = new HashMap();
 context.put("contenturl","http://"+dmn.getContentUrl());
 
engine.getTemplate("quiz_terms").evaluate(writer, context);





%>