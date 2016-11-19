<%@include file="commonfunc.jsp"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%    SubscriptionApiPort myApi = new SubscriptionApiServiceLocator()
            .getSubscriptionApi40(new URL(SUBSCRIBE_REQUEST_URL));
    // Set timeout to 1 minute
    ((SubscriptionApiBindingStub) myApi).setTimeout(4 * 60 * 1000);

    String CONFIRM_TARGET = "/confirm_it.jsp";

    CONFIRM_TARGET += "?cid=" + campaignId;

    StringBuilder dd = new StringBuilder();
    String tab = new Character((char) 9).toString();
    String crlf = "\r\n";
    String debugFile = "IPX_subscription_debug_" + MiscDate.sqlDate.format(new Date()) + ".txt";
    String subscriptionFile = "IPX_subscription.csv";
    String debugPath = "/var/log/pareva/ES/ipxlog/subscription/";

    debugFile = "IPX_subscription_debug_" + MiscDate.sqlDate.format(new Date()) + ".txt";
    dd.append(crlf + "IPX Subscription Session Processing HTTP Request");

    String transactionId = request.getParameter("transactionid");
    String operator = request.getParameter("op");
    String msisdn = request.getParameter("msisdn");
    
    String ServiceMetaData = (String) session.getAttribute("clubServiceMetadata");
    clubIPXUserName = (String) session.getAttribute("clubIPXUserName");
    clubIPXPassword = (String) session.getAttribute("clubIPXPassword");
    response.setContentType("text/plain; charset=UTF-8");

    String webpin = aReq.get("webpin");

    System.out.println("IPX debug " + msisdn + "--" + operator + " -- " + webpin + " -- " + campaignId);
    System.out.println("IPX debug " + msisdn + "--" + operator + " -- " + transactionId + " -- " + campaignId);

    if (transactionId != null && msisdn != null && operator != null) {
        // Calculate the absolute destination to the target
        StringBuffer aCurrentRequestURL = request.getRequestURL();
        dd.append(crlf + "IPX Subscription Session: " + MiscDate.now24sql() + "--" + msisdn + "--" + operator + "--" + transactionId);

        String aDeliveryURL = aCurrentRequestURL.substring(0, aCurrentRequestURL.indexOf(request.getServletPath())) + CONFIRM_TARGET;
        dd.append(crlf + "IPX Subscription ServiceMetaData: " + ServiceMetaData);
        dd.append(crlf + "IPX Subscription Current request URL: " + aCurrentRequestURL);
        dd.append(crlf + "IPX Subscription Delivery URL: " + aDeliveryURL);
        System.out.println(crlf + "IPX Subscription Delivery URL: " + aDeliveryURL);
        // We do not want to rely on cookies, perform URL rewriting instead
        aDeliveryURL = response.encodeRedirectURL(aDeliveryURL);
        operator = operator.toLowerCase();
        try {

            CreateSubscriptionResponse aResponse
                    = directOperatorSubscriptionRequest(transactionId, msisdn, operator, ServiceMetaData, clubUnique, clubIPXUserName, clubIPXPassword, myApi);
            if (aResponse.getResponseCode() != 0) {
                dd.append(crlf + MiscDate.now24sql() + "--" + msisdn + "--" + operator + "--" + transactionId + "--IPX failed created subscription, response: "
                        + aResponse.getResponseCode() + " ("
                        + aResponse.getResponseMessage() + ")");
                System.out.println(crlf + MiscDate.now24sql() + "--" + msisdn + "--" + operator + "--" + transactionId + "--IPX failed created subscription, response: "
                        + aResponse.getResponseCode() + " ("
                        + aResponse.getResponseMessage() + ")");
//                WriteFile(debugPath, debugFile, dd, crlf);
                //response.sendRedirect(CONFIRM_TARGET);
                //return;
                out.println("failed");
            }else{
                String aSubscriptionID = aResponse.getSubscriptionId();
                String corrId = aResponse.getCorrelationId();
                operator = aResponse.getOperator();

                dd.append(crlf + "IPX Subscription SessionServlet Subscription ID: " + aSubscriptionID
                        + " of MSISDN: " + msisdn + " for Operator: " + operator + " transactionId: " + transactionId);
                System.out.println(crlf + "IPX Subscription SessionServlet Subscription ID: " + aSubscriptionID
                        + " of MSISDN: " + msisdn + " for Operator: " + operator + " transactionId: " + transactionId);

                if (msisdn != null && operator != null && aSubscriptionID != null && transactionId != null) {
                    String subscriptionText = msisdn + "," + operator + "," + aSubscriptionID + "," + transactionId + "," + clubUnique + crlf;
                    FileUtil.writeRawToFile(debugPath + "subscriptions/" + subscriptionFile, subscriptionText, true);
                }
                session.setAttribute("ipx_subscriptionid", aSubscriptionID);
                session.setAttribute("ipx_transactionid", corrId);
                session.setAttribute("ipx_msisdn", msisdn);
                session.setAttribute("ipx_operator", operator.toLowerCase());
//                WriteFile(debugPath, debugFile, dd, crlf);
                System.out.println("IPX confirm_target: " + CONFIRM_TARGET);
                System.out.println(dd.toString());                
                String subscriptionSucces= "successful," + aSubscriptionID;					
                out.println(subscriptionSucces);
            }
        } catch (RemoteException e) {
            System.out.println("Subscribe Exception" + dd.toString());
            e.printStackTrace();
        }
    }
    dd.append(crlf + "Cannot recognize network and transactionid of the subscriber");
    System.out.println(dd.toString());

    WriteFile(debugPath, debugFile, dd, crlf);
    return;
%>