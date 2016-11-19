<%@include file="globall-wap-header.jsp"%>
<%

String pagename=httprequest.get("renderpage");
String message=httprequest.get("message");


PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();

String mid=httprequest.get("mid");

if(!"".equals(mid)){
    String deMsisdn = MiscCr.decrypt(mid);
    context.put("mid",mid);
    context.put("msisdn",deMsisdn);
    context.put("msisdnexist","true");
}
else{
 context.put("msisdnexist","false");
 context.put("msisdn","");

}


context.put("contenturl","http://"+dmn.getContentUrl());
context.put("message",message);
engine.getTemplate(pagename).evaluate(writer, context); 

%>
