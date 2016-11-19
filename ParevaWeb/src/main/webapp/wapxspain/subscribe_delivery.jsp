<%@include file="commonfunc.jsp"%>
<%
	
SubscriptionApiPort myApi = new SubscriptionApiServiceLocator().getSubscriptionApi40(new URL(SUBSCRIBE_REQUEST_URL));
((SubscriptionApiBindingStub) myApi).setTimeout(50000);


String CONFIRM_TARGET = "/confirm_it.jsp";
CONFIRM_TARGET += "?cid=" + campaignId;

StringBuilder dd = new StringBuilder();
String tab = new Character((char)9).toString();
String crlf = "\r\n";
String debugFile = "IPX_subscription_debug_" + MiscDate.sqlDate.format(new Date()) + ".txt";
String debugPath = "/var/log/pareva/ES/ipxlog/subscription/";

dd.append(crlf + "Initiating API IPX Subscription DeliveryServlet");
dd.append(MiscDate.now24sql());

String transactionId = (String) session.getAttribute("ipx_transactionid");
String operator = (String) session.getAttribute("ipx_operator");
String msisdn = (String) session.getAttribute("ipx_msisdn");
String ServiceMetaData = (String)session.getAttribute("clubServiceMetadata");
String aSessionId = (String) session.getAttribute("ipx_subscription_sessionid");
String corrId = (String) session.getAttribute("ipx_subscription_corrid");

if (aSessionId != null && corrId != null && msisdn != null) {



	String aSubscriptionID = finalizeSubscription(aSessionId, corrId, clubIPXUserName, clubIPXPassword, myApi, dd, crlf, session);
	// Set ipx attributes session and redirect to confirm_it.jsp
	session.setAttribute("ipx_subscriptionid", aSubscriptionID);
	session.setAttribute("ipx_transactionid", corrId);
	//session.setAttribute("ipx_msisdn", msisdn);
	session.setAttribute("ipx_operator", operator);		
	msisdn = (String) session.getAttribute("ipx_msisdn");

	//FOR LOGGING
	dd.append(crlf + "IPX Subscription DeliveryServlet Found current IPX sessionid: "+ aSessionId + ", check status");
	dd.append(crlf + "IPX Subscription DeliveryServlet Found current IPX msisdn: "+ msisdn + ", check status");
	dd.append(crlf + "IPX Subscription DeliveryServlet Found current IPX corrId: "+ corrId + ", check status");
	dd.append(crlf + "IPX Subscription DeliveryServlet Consumer is authenticated and authorized, deliver content");
	dd.append(crlf + "IPX Subscription DeliveryServlet Content is delivered, finalize the session");				
	dd.append(crlf + "IPX Subscription DeliveryServlet Subscription ID: " + aSubscriptionID
		+" of MSISDN: " + msisdn + " for Operator: " + operator + " transactionId: "+ transactionId);	

	System.out.println(dd.toString());    
	FileUtil.writeRawToFile(debugPath + debugFile, dd.toString() + crlf, true);
	dd.setLength(0);
	/*
	if(msisdn!=null && operator!=null && !aSubscriptionID.equals("") && transactionId!=null){
		String subscriptionText= msisdn + "," + operator + "," + aSubscriptionID + "," + transactionId + crlf;
		String subscriptionTextWithDate= MiscDate.now24sql() + "," + msisdn + "," + operator + "," + aSubscriptionID + "," + transactionId + crlf;
		FileUtil.writeRawToFile(debugPath + "subscriptions/" + subscriptionWithID, subscriptionTextWithDate, true);	
		FileUtil.writeRawToFile(debugPath + "subscriptions/" + subscriptionFile, subscriptionText, true);		
	}
	*/
	response.sendRedirect(CONFIRM_TARGET);
	return;
}else{
	dd.append(crlf + "IPX Subscription Delivery No sessionid found for consumer");
	System.out.println(dd.toString());    
	response.sendRedirect(CONFIRM_TARGET);
	return;
}
%>