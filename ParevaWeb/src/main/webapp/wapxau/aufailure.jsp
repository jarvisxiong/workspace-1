<%@include file="auheader.jsp"%>
<%
//doRedirect(response,"aufailure.jsp?msisdn="+msisdn+"&transid="+transID+"&status="+status+"&reason="+reason);
 
    msisdn=httprequest.get("msisdn");
    String reason=httprequest.get("reason");
    String status=httprequest.get("status");
    String statusMsg="Error: "+reason;
    
    
    if(reason.equalsIgnoreCase("AV_FAILED")) statusMsg="Transaction requires adult verification.";
    if(reason.equalsIgnoreCase("ACCOUNT_BARRED")) statusMsg="Your account has been suspended or barred.";
    if(reason.equalsIgnoreCase("FAILED_CREDIT")) statusMsg="Your account does not have enough credit to process this transaction."; //pre-paid
    if(reason.equalsIgnoreCase("FAILED_CREDIT_LIMIT")) statusMsg="Your account does not have enough credit to process this transaction."; //post-paid
    if(reason.equalsIgnoreCase("FAILED_SPEND_LIMIT")) statusMsg="You have reached your Premium Services spend threshold.";
    if(reason.equalsIgnoreCase("INVALID")) statusMsg="You are attempting to purchase on an unsupported network or mobile network operator.";
    
    if(reason.equalsIgnoreCase("INVALID_CHARGE_AMOUNT")) statusMsg="The price attempting to be charged is not valid for this service. Contact Oxygen8";
    if(reason.equalsIgnoreCase("SYSTEM_ERROR")) statusMsg="A Direct Billing system error has occurred. Contact Oxygen8.";
    if(reason.equalsIgnoreCase("UNKNOWN_ERROR")) statusMsg="An unknown error has occurred. Contact Oxygen8.";
    if(reason.equalsIgnoreCase("UNSUPPORTED")) statusMsg="The end consumer?s account does not support Direct Billing.";
    if(reason.equalsIgnoreCase("REPEAT_SUBSCRIPTION_NOT_PERMITTED")) statusMsg="The consumer is already subscribed to the service and may not be charged again.";
    
    
    PrintWriter writer = response.getWriter();
    Map<String, Object> context = new HashMap(); 
    
    context.put("msisdn",msisdn);
    context.put("statusmsg",statusMsg);
    context.put("contenturl","http://"+dmn.getContentUrl());
    au_engine.getTemplate("aufailure").evaluate(writer, context);
    
    
    
    
%>

