<%@include file="commonfunc.jsp"%>
<%    SubscriptionApiPort myApi = new SubscriptionApiServiceLocator().getSubscriptionApi40(new URL(SUBSCRIBE_REQUEST_URL));
    ((SubscriptionApiBindingStub) myApi).setTimeout(50000);

    String CONFIRM_TARGET = "/confirm_it.jsp";
    CONFIRM_TARGET += "?cid=" + campaignId;

    StringBuilder dd = new StringBuilder();
    String tab = new Character((char) 9).toString();
    String crlf = "\r\n";
    String debugFile = "IPX_subscription_debug_" + MiscDate.sqlDate.format(new Date()) + ".txt";
    String subscriptionFile = "IPX_subscription.csv";
    String subscriptionWithoutID = "IPX_subscription_withoutID.csv";
    String debugPath = "/var/log/pareva/IT/ipxlog/subscription/";

    String transactionId = (String) session.getAttribute("ipx_transactionid");
    String operator = (String) session.getAttribute("ipx_operator");
    String msisdn = (String) session.getAttribute("ipx_msisdn");
    String ServiceMetaData = (String) session.getAttribute("clubServiceMetadata");
    String aSessionId = (String) session.getAttribute("ipx_subscription_sessionid");
    String corrId = (String) session.getAttribute("ipx_subscription_corrid");

    if (aSessionId != null && corrId != null && msisdn != null) {

        if(operator.equals("wind"))
            Thread.sleep(3500);

        String aSubscriptionID = finalizeSubscription(aSessionId, corrId, clubIPXUserName, clubIPXPassword, myApi, dd, crlf, session);
        System.out.println("*** aSubscriptionID: " + aSubscriptionID);

        //retry logic
        if (aSubscriptionID.equals("") && (operator.equals("wind") || operator.equals("vodafone"))) {
            aSubscriptionID = finalizeSubscription(aSessionId, corrId, clubIPXUserName, clubIPXPassword, myApi, dd, crlf, session);
            System.out.println("*** aSubscriptionID: " + aSubscriptionID);
        }

        if (aSubscriptionID.equals("") && (operator.equals("wind") || operator.equals("vodafone"))) {
            aSubscriptionID = finalizeSubscription(aSessionId, corrId, clubIPXUserName, clubIPXPassword, myApi, dd, crlf, session);
            System.out.println("*** aSubscriptionID: " + aSubscriptionID);
        }
        if (aSubscriptionID.equals("") && (operator.equals("wind") || operator.equals("vodafone"))) {
            aSubscriptionID = finalizeSubscription(aSessionId, corrId, clubIPXUserName, clubIPXPassword, myApi, dd, crlf, session);
            System.out.println("*** aSubscriptionID: " + aSubscriptionID);
        }

        if (aSubscriptionID.equals("") && (operator.equals("wind") || operator.equals("vodafone"))) {
            aSubscriptionID = finalizeSubscription(aSessionId, corrId, clubIPXUserName, clubIPXPassword, myApi, dd, crlf, session);
            System.out.println("*** aSubscriptionID: " + aSubscriptionID);
        }
        if (aSubscriptionID.equals("") && (operator.equals("wind") || operator.equals("vodafone"))) {
            aSubscriptionID = finalizeSubscription(aSessionId, corrId, clubIPXUserName, clubIPXPassword, myApi, dd, crlf, session);
            System.out.println("*** aSubscriptionID: " + aSubscriptionID);
        }
        if (aSubscriptionID.equals("") && (operator.equals("wind") || operator.equals("vodafone"))) {
            aSubscriptionID = finalizeSubscription(aSessionId, corrId, clubIPXUserName, clubIPXPassword, myApi, dd, crlf, session);
            System.out.println("*** aSubscriptionID: " + aSubscriptionID);
        }
        if (aSubscriptionID.equals("") && (operator.equals("wind") || operator.equals("vodafone"))) {
            aSubscriptionID = finalizeSubscription(aSessionId, corrId, clubIPXUserName, clubIPXPassword, myApi, dd, crlf, session);
            System.out.println("*** aSubscriptionID: " + aSubscriptionID);
        }

        // Set ipx attributes session and redirect to confirm_it.jsp
        session.setAttribute("ipx_subscriptionid", aSubscriptionID);
        session.setAttribute("ipx_transactionid", corrId);
        //session.setAttribute("ipx_msisdn", msisdn);
        session.setAttribute("ipx_operator", operator);
        msisdn = (String) session.getAttribute("ipx_msisdn");

        dd.append(crlf + MiscDate.now24sql() + " - IPX Subscription DeliveryServlet Subscription ID: " + aSubscriptionID
            + " of MSISDN: " + msisdn + " for Operator: " + operator
            + " transactionId: " + transactionId
            + " sessionId: " + aSessionId + " campaignId: " + campaignId + " corrId: " + corrId
            + " ipx_responseCode: " + session.getAttribute("ipx_responseCode") + " ipx_responseMessage: "
            + (String) session.getAttribute("ipx_responseMessage"));
        
        System.out.println(crlf + MiscDate.now24sql() + " - IPX Subscription DeliveryServlet Subscription ID: " + aSubscriptionID
            + " of MSISDN: " + msisdn + " for Operator: " + operator
            + " transactionId: " + transactionId
            + " sessionId: " + aSessionId + " campaignId: " + campaignId + " corrId: " + corrId
            + " ipx_responseCode: " + session.getAttribute("ipx_responseCode") + " ipx_responseMessage: "
            + (String) session.getAttribute("ipx_responseMessage"));


        try {
            msisdn = (String) session.getAttribute("ipx_msisdn");
            String logDeliverSubscribedQuery
                    = "INSERT INTO ipxSubscribedLog (aUnique, aParsedMobile,aNetworkCode,aClubUnique,aCreated,aCampaign,aTransactionId,"
                    + "aSubscriptionId,aSessionId,aStatusCode, aSubscriptionDesc) values"
                    + "('" + Misc.generateUniqueId() + "','" + msisdn + "','" + operator
                    + "','" + clubUnique + "','"
                    + MiscDate.toSqlDate(new Date()) + "','"
                    + campaignId + "','" + transactionId + "','" + aSubscriptionID + "','"
                    + aSessionId + "','" + session.getAttribute("ipx_responseCode") + "','"
                    + (String) session.getAttribute("ipx_responseMessage") + "')";
            zalogDao.executeUpdateCPA(logDeliverSubscribedQuery);
        } catch (Exception ee) {
            System.out.println("Ipx delivery subscription exception: " + ee);
        }
        
        //FOR LOGGING
//        dd.append(crlf + "Initiating API IPX Subscription DeliveryServlet");
//        dd.append(MiscDate.now24sql());
//	dd.append(crlf + "IPX Subscription DeliveryServlet Found current IPX sessionid: "+ aSessionId + ", check status");
//	dd.append(crlf + "IPX Subscription DeliveryServlet Found current IPX msisdn: "+ msisdn + ", check status");
//	dd.append(crlf + "IPX Subscription DeliveryServlet Found current IPX corrId: "+ corrId + ", check status");
//	dd.append(crlf + "IPX Subscription DeliveryServlet Consumer is authenticated and authorized, deliver content");
//	dd.append(crlf + "IPX Subscription DeliveryServlet Content is delivered, finalize the session");				
//	dd.append(crlf + "IPX Subscription DeliveryServlet Subscription ID: " + aSubscriptionID
//		+" of MSISDN: " + msisdn + " for Operator: " + operator + " transactionId: "+ transactionId);	


//        System.out.println(dd.toString());
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
    } else {
        dd.append(crlf + "IPX Subscription Delivery No sessionid found for consumer");
        System.out.println(dd.toString());
        response.sendRedirect(CONFIRM_TARGET);
        return;
    }
%>