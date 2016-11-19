package ume.pareva.au;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.ThreadContext;
import ume.pareva.util.httpconnection.HttpURLConnectionWrapper;

public class AustraliaBaseConnection {

    static Logger logger = LogManager.getLogger(AustraliaBaseConnection.class.getName());
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final String MESSAGE_PRERREQ_PARAM_NEEDED = "ERROR: PARAMETER NEEDED FOR PREPARATORY REQUEST IS MISSING: ";

    public String longAustralia(Date date) {
        return sdf.format(date);
    }

    public String sendPreparatoryRequest(HttpURLConnectionWrapper hcuw, Map<String, String> params) {
        logger.info("SENDING PREPARATORY REQUES");
        if (params.get("serviceID") == null || params.get("serviceID").trim().isEmpty()) {
            return MESSAGE_PRERREQ_PARAM_NEEDED + "serviceID";
        } else if (params.get("price") == null || params.get("price").trim().isEmpty()) {
            return MESSAGE_PRERREQ_PARAM_NEEDED + "price";
        }
        Map<String, String> prepReqParameters = new HashMap<>();
        for (String key : params.keySet()) {
            if (key.equals("price") || key.equals("serviceID")) {
                prepReqParameters.put(key, params.get(key));
            }
        }

        prepReqParameters.put("requestType", "getTransID");
        prepReqParameters.put("username", AustraliaConnConstants.getUserName());
        prepReqParameters.put("password", AustraliaConnConstants.getPassword());

        hcuw.wrapGet(prepReqParameters);
        logger.info("RESPONSE CODE: {} - RESPONSE MESSAGE: {} - RESPONSE CONTENT: {}" ,hcuw.getResponseCode(),hcuw.getResponseMessage() , hcuw.getResponseContent());
        return hcuw.getResponseContent();

    }

}
