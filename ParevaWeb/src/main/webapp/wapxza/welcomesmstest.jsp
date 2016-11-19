<%@include file="coreimport.jsp"%>
<%@page import="ume.pareva.smsapi.ZaSmsSubmit"%>

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
<%
 UmeSessionParameters aReq=new UmeSessionParameters(request);
 UmeUser umeuser=new UmeUser();
 umeuser.setParsedMobile("27723937357");
 umeuser.setActive(1);
 UmeSmsDaoExtension umesmsdaoextension=null;
UmeSmsDao umesmsdao=null;
try{
    ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
    umesmsdaoextension=(UmeSmsDaoExtension) ac.getBean("umesmsdaoextension");
    umesmsdao=(UmeSmsDao) ac.getBean("umesmsdao");
    
    
}
catch(Exception e){e.printStackTrace();}
ZaSmsSubmit welcomesms = new ZaSmsSubmit(aReq);
                welcomesms.setSmsAccount("sts");
                welcomesms.setUmeUser(umeuser);
                 welcomesms.setToNumber("27723937357");
                welcomesms.setFromNumber("43201");
                welcomesms.setMsgBody("Your Personal Link to the service is http://x-rated.realgirls.mobi/?id=27Cnx324u7 OR x-rated.realgirls.mobi/?id=27Cnx324u7 " );
                
                
                
                try { 
                     //welcomesms.setCampaignId(Integer.parseInt("1630"));
                    welcomesms.setCampaignId(Integer.parseInt("1630"));
                   
                   } catch (NumberFormatException e) {}
                welcomesms.setMsgCode1("");
                
                String resp = "";
                try{
                SdcSmsGateway gw=new SdcSmsGateway();
                gw.setAccounts(welcomesms.getSmsAccount());
                gw.setMsisdnFormat(4);
                resp=umesmsdaoextension.send(welcomesms, gw);
                System.out.println(welcomesms.getToNumber()+" response is "+resp);
                }
                catch(Exception e){e.printStackTrace();}
                System.out.println("WELCOME MESSAGE TEST "+welcomesms.getMsgBody()+"  \n"+ "Response "+resp);


}//end if header
%>
