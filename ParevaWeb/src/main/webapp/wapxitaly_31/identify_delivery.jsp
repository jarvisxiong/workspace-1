<%@include file="commonfunc.jsp"%>
<%  String SUBSCRIPTION_TARGET = "/index_main.jsp";
    String operator = "";
    String landingPage = (String) request.getAttribute("landingPage");
    if (null == landingPage || "".equalsIgnoreCase(landingPage)) {
        landingPage = (String) session.getAttribute("landingPage");
    }

    IdentificationApiPort myApi = (new IdentificationApiServiceLocator()).getIdentificationApi31(new URL(IDENTIFY_REQUEST_URL));
    ((IdentificationApiBindingStub) myApi).setTimeout(50000);
    String aSessionId = (String) session.getAttribute("ipx_idensessionid");

    try {
        if (aSessionId != null) {
            if (checkIfAuthorized(aSessionId, clubIPXUserName, clubIPXPassword, myApi, request, campaignId)) {

                FinalizeSessionResponse aFinalSessionResponse = finalize(aSessionId, clubIPXUserName, clubIPXPassword, myApi);
                String transactionId = aFinalSessionResponse.getTransactionId();
                operator = aFinalSessionResponse.getOperator();
                String msisdn = aFinalSessionResponse.getConsumerId();
                session.setAttribute("ipx_transactionid", transactionId);
                session.setAttribute("ipx_operator", operator.toLowerCase());
                session.setAttribute("ipx_msisdn", msisdn);

                //TODO ASK MADAN HOW TO LOG IDENTIFIED
                campaigndao.log("confirm", landingPage, "", msisdn, handset, domain, campaignId, club.getUnique(), "IDENTIFIED", 1, request, response, operator.toLowerCase());

                StringBuilder dd;
                String crlf;
                String debugFile;
                String debugPath;
                dd = new StringBuilder();
                crlf = "\r\n";
                debugFile = (new StringBuilder("IPX_identify_debug_")).append(MiscDate.sqlDate.format(new Date())).append("_").append(club.getUnique()).append(".txt").toString();
                debugPath = "/var/log/pareva/IT/ipxlog/identify/";
                dd.append((new StringBuilder(String.valueOf(crlf))).append("IPX Processing HTTP Request").append("\t").append(new Date()).append("\t").toString());
                dd.append((new StringBuilder(String.valueOf(crlf))).append("IPX DeliveryServlet clubUnique: ").append(club.getUnique()).toString());
                dd.append((new StringBuilder(String.valueOf(crlf))).append("IPX DeliveryServlet transactionId: ").append(transactionId).toString());
                dd.append((new StringBuilder(String.valueOf(crlf))).append("IPX DeliveryServlet campaignId: ").append(campaignId).toString());
                dd.append((new StringBuilder(String.valueOf(crlf))).append("IPX DeliveryServlet ipx_operator: ").append(operator.toLowerCase()).toString());
                dd.append((new StringBuilder(String.valueOf(crlf))).append("IPX DeliveryServlet ipx_msisdn: ").append(msisdn).toString());
                FileUtil.writeRawToFile((new StringBuilder(String.valueOf(debugPath))).append(debugFile).toString(), (new StringBuilder(String.valueOf(dd.toString()))).append(crlf).toString(), true);
                dd.setLength(0);

                String domainName = System.getProperty(domain + "_url");
                if ((msisdn != null && !msisdn.equals(""))) {
//                    user = umeuserdao.authenticateUser(msisdn, "", 3, dmn);
                    user = umeuserdao.getUser(msisdn);
                    if (user != null && club != null && mobileclubdao.isActive(user, club)) {
                        response.sendRedirect("http://" + domainName + "/" + "?id=" + user.getWapId());
                        return;
                    }
                }
                if (operator.toLowerCase().equals("vodafone") || operator.toLowerCase().equals("wind") || operator.toLowerCase().equals("tim") || operator.toLowerCase().equals("three")) {
                    try {
                        SUBSCRIPTION_TARGET = "/subscribe.jsp?cid=" + campaignId;
                        doRedirect(response, SUBSCRIPTION_TARGET);
                        return;
//return;
                    } catch (Exception ee) {
                        System.out.println("IPX identify_delivery exception: " + ee);
                    }
                } else {
                }
            } else {
                SUBSCRIPTION_TARGET += "&cid=" + campaignId;
            }
        } else {
            SUBSCRIPTION_TARGET += "&cid=" + campaignId;
        }
    } catch (RemoteException e) {
    }
    application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/promo_subscribe.jsp" + "?pg=subscribe&cid=" + campaignId).forward(request, response);
%>