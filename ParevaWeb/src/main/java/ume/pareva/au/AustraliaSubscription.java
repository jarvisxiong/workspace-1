package ume.pareva.au;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import ume.pareva.util.httpconnection.HttpURLConnectionWrapper;

public class AustraliaSubscription extends AustraliaBaseConnection {

    static final Logger logger = LogManager.getLogger(AustraliaSubscription.class.getName());

    private static final String MESSAGE_SUBSCRIPTION_PARAM_NEEDED = "ERROR: PARAMETER NEEDED FOR SUBSCRIPTION IS MISSING: ";

    private static final String ACTION_SUBSCRIBE = "subscribe";
    private static final String ACTION_UNSUBSCRIBE = "unsubscribe";

    public String subscribe(Map<String, String> params) {
        return attemptSubscriptionAction(ACTION_SUBSCRIBE, params);
    }

    public String unsubscribe(Map<String, String> params) {
        return attemptSubscriptionAction(ACTION_UNSUBSCRIBE, params);

    }

// test msisdn = "61412104406";
    private String attemptSubscriptionAction(String action, Map<String, String> params) {
        ThreadContext.put("ROUTINGKEY", "AUSTRALIA");
        if (params.get("msisdn") == null || params.get("msisdn").trim().isEmpty()) {
            return MESSAGE_SUBSCRIPTION_PARAM_NEEDED + "msisdn";
        } else if (params.get("listid") == null || params.get("listid").trim().isEmpty()) {
            return MESSAGE_SUBSCRIPTION_PARAM_NEEDED + "listid";
        } else if (params.get("productname") == null || params.get("productname").trim().isEmpty()) {
            return MESSAGE_SUBSCRIPTION_PARAM_NEEDED + "productname";
        }
        HttpURLConnectionWrapper hucw = new HttpURLConnectionWrapper(AustraliaConnConstants.getDomainHttps());

        String transactionId = sendPreparatoryRequest(hucw, params);
        if(!hucw.isSuccessful()){
        //TODO
            logger.info("PREPARATORY REQUEST FAILED");
        }

        Map<String, String> subscriptionParameters = new HashMap<>();
        for (String key : params.keySet()) {
            if (key.equals("msisdn") || key.equals("listid") || key.equals("serviceID")) {
                subscriptionParameters.put(key, params.get(key));
            }
        }
        subscriptionParameters.put("action", action);
        subscriptionParameters.put("TransID", transactionId);
        subscriptionParameters.put("date", longAustralia(new Date()));
        //hucm.wrapPost(subscriptionParameters);
        hucw.wrapPost(subscriptionParameters);
        if(!hucw.isSuccessful()){
        //TODO
            logger.info("SUBSCRIPTION REQUEST FAILED");
        }
        return hucw.getResponseContent();
    }
}
