<%@include file="coreimport.jsp" %>
<jsp:include page="/IT/GlobalWapHeaderIT"/>
<%
    if(request.getAttribute("campaignId")!=null && ((String)request.getAttribute("campaignId")).equals("1234")){   
        session.setAttribute("ipx_msisdn", "392222222");
        session.setAttribute("ipx_operator", "vodafone");
        session.setAttribute("ipx_subscriptionid", "2-2222222");
        session.setAttribute("ipx_transactionid", "999999");
    }

%>

<jsp:include page="/IT/ITConfirm"/>
<%
    UmeDomain dmn =(UmeDomain) request.getAttribute("dmn");
    PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    String cid=(String)request.getAttribute("campaignId");
    String subpage = (String)request.getAttribute("subpage");
    String domain=(String)request.getAttribute("domain");
    String landingPage = (String)request.getAttribute("landingPage");

    String confirmedLink = (String)request.getAttribute("confirmedLink");
    System.out.println("*********** ITConfirm confirmedLink: " + confirmedLink);
    
    if(confirmedLink!=null){
        response.sendRedirect(confirmedLink);
        return;
    }

    TemplateEngine templateengine=(TemplateEngine)request.getAttribute("templateengine");
    PebbleEngine it_engine=templateengine.getTemplateEngine(domain);
//LandingPage landingpage=(LandingPage)request.getAttribute("landingpage");
//    System.out.println("Landing Page: "+landingpage.getLandingPage(domain));
        /*
        String redirecturl=(String) request.getParameter("personallink");
        if(redirecturl==null) redirecturl=(String) request.getAttribute("personallink");
        if(redirecturl!=null)
        {
            response.sendRedirect(redirecturl);
            return;
        }
        */
    context.put("gotomain","http://"+ dmn.getDefaultUrl());
    context.put("contenturl","http://"+ dmn.getContentUrl());
    context.put("message",(String)session.getAttribute("status"));
    context.put("landingpage",landingPage);
    it_engine.getTemplate("subscriptionconfirmation").evaluate(writer, context);
    //String output = writer.toString();
    //System.out.println(output);
%>