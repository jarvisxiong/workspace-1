<%@include file="commonfunc.jsp"%>
<%    
    String status = "";
    String passcode = aReq.get("passcode").trim();
//    String messageId = aReq.get("messageid");
//    String operator = aReq.get("operator");
//    String msisdn = aReq.get("msisdn").trim();
    String messageId = (String)session.getAttribute("ipx_messageid");
    String operator = (String)session.getAttribute("ipx_operator");
    String msisdn = (String)session.getAttribute("ipx_msisdn");
        
    WebOptInApiPort myApi = new WebOptInApiServiceLocator().getWebOptInApi10(new URL("http://europe.ipx.com/api/services2/WebOptInApi10?wsdl"));
    ((WebOptInApiBindingStub) myApi).setTimeout(1 * 60 * 1000);

    //System.out.println("IPX clubuser: " + clubUser.getUnique());
    try {
        //System.out.println("IPX init validate webpin: "  + new Date() + "\t");
        ValidatePasscodeRequest aRequest = new ValidatePasscodeRequest();
        aRequest.setCorrelationId("ume_web_pin");
        aRequest.setConsumerId(msisdn);
        aRequest.setPasscode(passcode);
        aRequest.setReferenceId(messageId);
        aRequest.setUsername(clubIPXUserName);
        aRequest.setPassword(clubIPXPassword);

        ValidatePasscodeResponse aResponse = myApi.validatePasscode(aRequest);

        if (aResponse.getResponseCode() != 0) {
            System.out.println("IPX: Failed validate passcode with reasonCode:  " + aResponse.getReasonCode() + " and reason is: " + aResponse.getResponseMessage() + " msisdn: " + msisdn);
            status = "failed passcode";
        } else {
            //System.out.println("IPX transactionId is: " + aResponse.getWebOptInId());
            System.out.println("IPX: webpin successful :  " + aResponse.getWebOptInId() + " msisdn: " + msisdn);

            session.setAttribute("ipx_msisdn", msisdn);
            session.setAttribute("ipx_transactionid", aResponse.getWebOptInId());
            session.setAttribute("ipx_operator", operator);
            session.setAttribute("ipx_cid", campaignId);

            status = "successful";            
            /*
            String subscription_url = "http://" + dmn.getName() + "/subscribe.jsp?webpin=yes";
            //Redirect to subscription servlet 
            if (!campaignId.equals("")) {
                subscription_url += "&cid=" + campaignId;
            }
            System.out.println("IPX debug webpin subscription_url: " + subscription_url);
            doRedirect(response, subscription_url);
            return;
            */
        }
    } catch (Exception e) {
        status = "failed " + e.getMessage();
    }
    out.print(status);
%>