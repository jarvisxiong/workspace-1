<%@include file="ptheader.jsp"%>
<%

String pagename=httprequest.get("renderpage");
String message=httprequest.get("message");


PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap<String,Object>();

context.put("contenturl","http://"+dmn.getContentUrl());
context.put("message",message);
engine.getTemplate(template.getTemplateName()+"/"+pagename).evaluate(writer, context); 

%>
