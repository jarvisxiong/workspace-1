<jsp:include page="/GlobalWapHeader"/>
<%
String routerresponse = (String)request.getParameter("routerresponse");
if(routerresponse==null) routerresponse=(String) request.getAttribute("routerresponse");
if(routerresponse!=null && routerresponse.equalsIgnoreCase("true"))
{
    String redirectUrl=(String) request.getParameter("routerurl");
    if(redirectUrl==null) redirectUrl=(String) request.getAttribute("routerurl");
    
    if(redirectUrl!=null)
    {
        response.sendRedirect(redirectUrl);return;
    }
    
}

String subscriptionurl=(String) request.getParameter("subscriptionurl");
if(subscriptionurl==null) subscriptionurl=(String) request.getAttribute("subscriptionurl");
if(subscriptionurl!=null && !subscriptionurl.equalsIgnoreCase(""))
{
    response.sendRedirect(subscriptionurl);return;
}

String needToConfirm=(String) request.getParameter("confirmUser");
if(needToConfirm==null) needToConfirm=(String) request.getAttribute("confirmUser");

if(needToConfirm!=null && needToConfirm.equalsIgnoreCase("true"))
{
    String wapoptin=(String) request.getParameter("wapoptin");
    if(wapoptin==null) wapoptin=(String) request.getAttribute("wapoptin");
    
    if(wapoptin!=null && wapoptin.equalsIgnoreCase("true"))
    {
        String wapurl=(String) request.getParameter("wappageurl");
        if(wapurl==null) wapurl=(String) request.getAttribute("wappageurl");
        
        if(wapurl!=null) 
        {
//        	 PrintWriter writer = response.getWriter();
//             Map<String, Object> context = new HashMap();
//             String cid=(String)request.getAttribute("campaignId");
//             context.put("msisdnexist","true");
//             context.put("msisdn",(String)request.getAttribute("msisdn"));
//             context.put("campaignid",cid);
//             context.put("sendAction",wapurl);
//        	PebbleEngine za_engine=(PebbleEngine)request.getServletContext().getAttribute("za_engine");
//        	String domain=(String)request.getAttribute("domain");
//        	LandingPage landingpage=(LandingPage)request.getAttribute("landingpage");
//        	if(za_engine!=null && !cid.equals("")){
//        		za_engine.getTemplate(landingpage.getLandingPage(domain)).evaluate(writer, context);
//        	}else if (za_engine!=null && cid.equals("")){
//        		za_engine.getTemplate("landing-page").evaluate(writer, context);
//            	
//        	}
//        		
//        	return;
            response.sendRedirect(wapurl);return;
        }
        
                
    }
    
    else if(wapoptin!=null && wapoptin.equalsIgnoreCase("false"))
    {
        String smsredirection=(String) request.getParameter("smsconfirmed");
        if(smsredirection==null ) smsredirection=(String) request.getAttribute("smsconfirmed");
        
        if(smsredirection!=null) 
        {
            response.sendRedirect(smsredirection);return;
        }
    }
    
    
    
    
    
    
}




if(routerresponse==null && needToConfirm==null)
{
%>
<%@include file="coreimport.jsp" %>
<%
PebbleEngine za_engine=(PebbleEngine)request.getServletContext().getAttribute("za_engine");
UmeDomain dmn=(UmeDomain)request.getAttribute("dmn");
String mobileno=(String) request.getParameter("invalidno");
String status = "Thanks! You should receive an sms shortly. "
        + "Reply <b>YES</b> to confirm you are 18+ and start your <b>FREE</b> Trial.";


 PrintWriter writer = response.getWriter();
 Map<String, Object> context = new HashMap();
 
context.put("smssuccess","true"); 
context.put("gotomain","http://"+ dmn.getDefaultUrl());
context.put("contenturl","http://"+ dmn.getContentUrl());
context.put("message",status);
za_engine.getTemplate("status").evaluate(writer, context);
String output = writer.toString();



}//end if header
%>