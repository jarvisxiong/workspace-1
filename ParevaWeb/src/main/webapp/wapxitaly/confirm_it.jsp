<%@include file="coreimport.jsp" %>
<jsp:include page="/IT/GlobalWapHeaderIT"/>
<%
    if (request.getAttribute("campaignId") != null && ((String) request.getAttribute("campaignId")).equals("1234")) {
        session.setAttribute("ipx_msisdn", "393333333");
        session.setAttribute("ipx_operator", "wind");
        session.setAttribute("ipx_subscriptionid", "2-333333");
        session.setAttribute("ipx_transactionid", "999999");
    }

    if (request.getAttribute("campaignId") != null && ((String) request.getAttribute("campaignId")).equals("tim_blocked")) {
        session.setAttribute("ipx_msisdn", "3999999999");
        session.setAttribute("ipx_operator", "tim");
        session.setAttribute("ipx_tim_block", "true");
        session.setAttribute("ipx_transactionid", "999999");
    }
%>
<jsp:include page="/IT/ITConfirm"/>
<%
    UmeDomain dmn = (UmeDomain) request.getAttribute("dmn");
    PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap();
    String cid = (String) request.getAttribute("campaignId");
    String subpage = (String) request.getAttribute("subpage");
    String domain = (String) request.getAttribute("domain");
    String landingPage = (String) request.getAttribute("landingPage");

//    String confirmedLink = (String) request.getAttribute("confirmedLink");
//    System.out.println("*********** ITConfirm confirmedLink: " + confirmedLink);
//
//    if (confirmedLink != null) {
//        response.sendRedirect(confirmedLink);
//        return;
//    }

    String redirecturl = (String) session.getAttribute("personallink"); //
    if (redirecturl == null) {
        redirecturl = (String) request.getAttribute("personallink");
    }
    System.out.println("CONFIRM_IT.jsp REDIRECT URL received is "+redirecturl);
    if (redirecturl != null) {
        response.sendRedirect(redirecturl);
        return;
//        String op_er = (String)session.getAttribute("ipx_operator");
//            if(redirecturl.contains("cid=") && op_er!=null && op_er.equals("wind")){
//        }else{
//            response.sendRedirect(redirecturl);
//            return;
//        }
    }
    
//    MobileClubCampaignDao campaigndao = (MobileClubCampaignDao)request.getAttribute("campaigndao");
//
//    if(cid!=null && !cid.equals("")){
//        java.util.List notSubscribedClubDomains=(java.util.List)request.getAttribute("notSubscribedClubDomains");
//        if(notSubscribedClubDomains!=null && !notSubscribedClubDomains.isEmpty()){
//            UmeDomain popunderDomain=(UmeDomain)notSubscribedClubDomains.get((int) java.lang.Math.floor(java.lang.Math.random()* notSubscribedClubDomains.size()));
//            String popunderCampaignId=campaigndao.getCampaignUnique(popunderDomain.getUnique(),"PopUnder");
//            System.out.println("Popunder Domaincid: "+"http://"+popunderDomain.getDefaultUrl()+"/?cid="+popunderCampaignId);
//            context.put("popunderDomain","http://"+popunderDomain.getDefaultUrl()+"/?cid="+popunderCampaignId);
//        }else{
//            System.out.println("Popunder Domaincid: " + "null babe");
//        }
//    }
    

    TemplateEngine templateengine = (TemplateEngine) request.getAttribute("templateengine");
    PebbleEngine it_engine = templateengine.getTemplateEngine(domain);
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

    if ((String) session.getAttribute("ipx_tim_block") != null) {
        context.put("message", "ipx_tim_block");
    } else {
        context.put("message", (String) session.getAttribute("status")); 
    }

    context.put("gotomain", "http://" + dmn.getDefaultUrl());
    context.put("contenturl", "http://" + dmn.getContentUrl());
    context.put("landingpage", landingPage);
    it_engine.getTemplate("subscriptionconfirmation").evaluate(writer, context);
    //String output = writer.toString();
    //System.out.println(output);
%>