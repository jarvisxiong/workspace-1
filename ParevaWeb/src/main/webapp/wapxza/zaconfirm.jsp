
<%
request.setAttribute("pagename","zaconfirm.jsp");
System.out.println("ZAHEADER zaconfirm.jsp is called ");
%>
<%@include file="coreimport.jsp"%>
<%
SdcRequest aReq = new SdcRequest(request);
UmeDomain dmnn = aReq.getDomain();


%>
<jsp:include page="/ZAConfirm"/>
<%
         String redirecturl=(String) request.getParameter("personallink");
        if(redirecturl==null) redirecturl=(String) request.getAttribute("personallink");
        if(redirecturl!=null)
        {
            response.sendRedirect(redirecturl);
            return;
        }
//
//	//PebbleEngine za_engine=(PebbleEngine)request.getServletContext().getAttribute("za_engine");
//        PebbleEngine za_engine=(PebbleEngine)request.getAttribute("za_engine");
//	
//	PrintWriter writer = response.getWriter();
//	Map<String, Object> context = new HashMap();
//        
   
        
        String userdoiErrorUrl=(String) request.getAttribute("wapidurl");
        if(userdoiErrorUrl!=null)
        {
            response.sendRedirect(userdoiErrorUrl);
            return;
        }
 
         //request.getServletContext().getRequestDispatcher("/wapxza/promo_subscribe.jsp?redir=zaconfirm").forward(request, response);
	
    //response.sendRedirect(subscriptionurl);
    //return;
//	context.put("gotomain","http://"+ dmnn.getDefaultUrl());
//	context.put("contenturl","http://"+ dmnn.getContentUrl());
//	context.put("message",(String)session.getAttribute("status"));
//	za_engine.getTemplate("subscriptionconfirmation").evaluate(writer, context);
//	String output = writer.toString();
        

%>
