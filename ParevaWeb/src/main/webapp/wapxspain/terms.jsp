<%@include file="coreimport.jsp" %>
<jsp:include page="/IT/GlobalWapHeaderIT"/>
<%
    SdcRequest aReq = new SdcRequest(request);
    UmeDomain dmn =(UmeDomain) request.getAttribute("dmn");
    PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    String cid=(String)request.getAttribute("campaignId");
    String termsPage = aReq.get("termspage");
    String domain=(String)request.getAttribute("domain");
    
    TemplateEngine templateengine=(TemplateEngine)request.getAttribute("templateengine");
    PebbleEngine it_engine=templateengine.getTemplateEngine(domain);
    
    context.put("gotomain","http://"+ dmn.getDefaultUrl());
    context.put("contenturl","http://"+ dmn.getContentUrl());
    context.put("campaignid",cid);
    context.put("termspage",termsPage);

    it_engine.getTemplate("terms").evaluate(writer, context);
%>