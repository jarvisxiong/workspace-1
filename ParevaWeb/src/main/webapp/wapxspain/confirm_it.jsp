<%@include file="coreimport.jsp" %>
<jsp:include page="/ES/GlobalWapHeaderES"/>
<%    
    if(request.getAttribute("campaignId")!=null && ((String)request.getAttribute("campaignId")).equals("1234")){   
        session.setAttribute("ipx_msisdn", "342222222");
        session.setAttribute("ipx_operator", "airtel");
        session.setAttribute("ipx_subscriptionid", "2-2222222");
        session.setAttribute("ipx_transactionid", "999999");
    }
%>

<jsp:include page="/ES/ESConfirm"/>
<%
    UmeDomain dmn =(UmeDomain) request.getAttribute("dmn");
    PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    String cid=(String)request.getAttribute("campaignId");
    String subpage = (String)request.getAttribute("subpage");
    String domain=(String)request.getAttribute("domain");
    Boolean trickyTime = (Boolean) request.getAttribute("trickyTime");

    String confirmedLink = (String) session.getAttribute("personallink"); //
    if (confirmedLink == null) {
        confirmedLink = (String) request.getAttribute("personallink");
    }
    System.out.println("*********** ESConfirm confirmedLink: " + confirmedLink);
    
    TemplateEngine templateengine=(TemplateEngine)request.getAttribute("templateengine");
    PebbleEngine it_engine=templateengine.getTemplateEngine(domain);
    context.put("gotomain","http://"+ dmn.getDefaultUrl());
    context.put("contenturl","http://"+ dmn.getContentUrl());
    
    if(confirmedLink!=null){
//        response.sendRedirect(confirmedLink);
//        return;
        context.put("confirmedLink", confirmedLink);
        it_engine.getTemplate("av").evaluate(writer, context);
    }else{
        //context.put("message",(String)session.getAttribute("status"));
        //it_engine.getTemplate("subscriptionconfirmation").evaluate(writer, context);
        if(cid!=null && trickyTime)
            response.sendRedirect("http://sx.leadzu.com/?m=1GJASITE66599X1&a=PUBID");
        else
            response.sendRedirect("http://google.es"); 
        return;
    }


//    context.put("confirmedLink", confirmedLink);
//    context.put("message",(String)session.getAttribute("status"));
//    it_engine.getTemplate("subscriptionconfirmation").evaluate(writer, context);
    //String output = writer.toString();
    //System.out.println(output);
%>