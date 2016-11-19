package ume.pareva.userservice;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component("smsresponses")
public class SmsResponses {

    private final Map<String, String> responseMap = initialiseMap();

    public String getSmsStatus(String region, int code) {
        String key = region + code;
        return findInMap(key);
    }
    
    public String getSmsStatus(String region, String code) {
        String key = region + code;
        return findInMap(key);
    }
    
     private String findInMap(String key) {
        String response = responseMap.get(key);
        if(response==null){
            return "FAILED";
        }
        return response;
    }
     
    private Map<String, String> initialiseMap() {
        Map<String, String> tempMap = new HashMap<>();

        // UK RESPONSES
        tempMap.put("UK1", "Engage Processing");
        tempMap.put("UK2", "Delivered To Network (final)");
        tempMap.put("UK3", "Delivered To Network (intermediate)");
        tempMap.put("UK4", "Operator is Retrying Message");
        tempMap.put("UK5", "DELIVERED");
        tempMap.put("UK6", "Failed @ Operator");
        tempMap.put("UK7", "Failed @ Engage");
        tempMap.put("UK11", "Unknown Subscriber");
        tempMap.put("UK13", "Subscriber Barred");
        tempMap.put("UK14", "Incorrect Billing");
        tempMap.put("UK15", "Invalid Originator");
        tempMap.put("UK16", "Message Expired @ Engage");
        tempMap.put("UK17", "Invalid Expiry Value");
        tempMap.put("UK18", "Duplicated Message");
        tempMap.put("UK20", "Zero Length Data");
        tempMap.put("UK21", "Binary Too Long");
        tempMap.put("UK22", "Binary Incorrect Format");
        tempMap.put("UK23", "SIM Full");
        tempMap.put("UK24", "Absent Subscriber");
        tempMap.put("UK25", "Error in Delivery To Operator");
        tempMap.put("UK26", "Message Expired @ Operator");
        tempMap.put("UK27", "Not defined -  Status Unknow");
        tempMap.put("UK201", "Invalid Login");
        tempMap.put("UK202", "Invalid XML");
        tempMap.put("UK203", "XML/Envelope encoding mismatch");
        tempMap.put("UK204", "Unexpected XML Definition");
        tempMap.put("UK206", "Maximum File Size Exceeded");
        tempMap.put("UK208", "Invalid DESTINATION_ADDR");

        // FINISHES INITIALISATION
        return  tempMap;
    }

}
