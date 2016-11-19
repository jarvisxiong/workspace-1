/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.ae.util;

/**
 *
 * @author trung
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppConstants {

    public static final String USERNAME = "umel!mited";
    public static final String PASSWORD = "umel!m!ted";
    public static final String BASE_URL = "http://clients.actelme.com/MainSMS/push_Content.aspx";
    public static final String GET_DLR_URL = "http://ip/GetDLR.aspx";
    public Map<String, List<String>> COUNTRY_OPERATOR_MAP;
    public static final Map<String, String> RETURN_CODE_MAP;
    static{
        RETURN_CODE_MAP = new HashMap<>();
        RETURN_CODE_MAP.put("Invalid or Unregistered account", "Invalid username and/or password");
        RETURN_CODE_MAP.put("Invalid Originator", "Invalid short code");
        RETURN_CODE_MAP.put("Invalid Receiver", "Invalid mobile number");
        RETURN_CODE_MAP.put("Invalid Countryname", "Invalid or empty country name");
        RETURN_CODE_MAP.put("Invalid Operatorname", "Invalid or empty operator name");
        RETURN_CODE_MAP.put("Invalid ContentBody", "Empty message or invalid text");
        RETURN_CODE_MAP.put("Invalid ContentTypeDetails", "Empty ContentTypeDetails");
        RETURN_CODE_MAP.put("Invalid ContentSubType", "Empty ContentSubType");
        RETURN_CODE_MAP.put("Invalid ContentId", "Empty ID");
        RETURN_CODE_MAP.put("Invalid request Duplication", "Duplicate content id posting; only its change resolve this issue");
        RETURN_CODE_MAP.put("Invalid request destination is in black list", "Blocked MSISDN");
        RETURN_CODE_MAP.put("Error Posting", "Internal link error");
        RETURN_CODE_MAP.put("Invalid request internal error", "Internal technical routing faluire.");
        RETURN_CODE_MAP.put("OK", "Message Sent Succefully");
        
    }

    public AppConstants() {
        COUNTRY_OPERATOR_MAP = new HashMap<>();
        List<String> operatorList = new ArrayList<>();
        operatorList.add("Djezzy");
        operatorList.add("Mobilis");
        operatorList.add("Nedjma");
        COUNTRY_OPERATOR_MAP.put("Algeria", operatorList);
        operatorList.clear();
        operatorList.add("batelco");
        operatorList.add("Zain");
        COUNTRY_OPERATOR_MAP.put("Bahrain", operatorList);
        operatorList.clear();
        operatorList.add("Mobinil");
        operatorList.add("Vodafone");
        operatorList.add("Etisalat");
        COUNTRY_OPERATOR_MAP.put("Egypt", operatorList);
        operatorList.clear();
        operatorList.add("Smartcom");
        COUNTRY_OPERATOR_MAP.put("International", operatorList);
        operatorList.clear();
        operatorList.add("MTN");
        COUNTRY_OPERATOR_MAP.put("Iran", operatorList);
        operatorList.clear();
        operatorList.add("ZainIQ");
        operatorList.add("Etisaluna");
        operatorList.add("Korectel");
        operatorList.add("asiacell");
        operatorList.add("Iragna");
        COUNTRY_OPERATOR_MAP.put("Irag", operatorList);
        operatorList.clear();
        operatorList.add("Jordan Telecom");
        operatorList.add("Zain");
        operatorList.add("Orange");
        operatorList.add("Umniah");
        operatorList.add("Xpress");
        COUNTRY_OPERATOR_MAP.put("Jordan", operatorList);
        operatorList.clear();
        operatorList.add("MOBILIY");
        operatorList.add("STC");
        operatorList.add("Zain");
        COUNTRY_OPERATOR_MAP.put("KSA", operatorList);
        operatorList.clear();
        operatorList.add("viva");
        operatorList.add("Zain");
        operatorList.add("wataniya");
        COUNTRY_OPERATOR_MAP.put("Kuwait", operatorList);
        operatorList.clear();
        operatorList.add("Alfa");
        operatorList.add("MTCTouch");
        COUNTRY_OPERATOR_MAP.put("Lebanon", operatorList);
        operatorList.clear();
        operatorList.add("Libyana");
        operatorList.add("elmadar");
        COUNTRY_OPERATOR_MAP.put("Libya", operatorList);
        operatorList.clear();
        operatorList.add("Wana");
        operatorList.add("MAROCTEL");
        operatorList.add("MEDITEL");
        COUNTRY_OPERATOR_MAP.put("Morocco", operatorList);
        operatorList.clear();
        operatorList.add("OmanTel");
        operatorList.add("Nawras");
        operatorList.add("omanMobile");
        COUNTRY_OPERATOR_MAP.put("Oman", operatorList);
        operatorList.clear();
        operatorList.add("Jawal");
        operatorList.add("Zain");
        operatorList.add("Paltel");
        COUNTRY_OPERATOR_MAP.put("Palestine", operatorList);
        operatorList.clear();
        operatorList.add("qtel");
        COUNTRY_OPERATOR_MAP.put("Qatar", operatorList);
        operatorList.clear();
        operatorList.add("Europe");
        COUNTRY_OPERATOR_MAP.put("Spain", operatorList);
        operatorList.clear();
        operatorList.add("MTN");
        operatorList.add("Sudatel");
        operatorList.add("Zain");
        COUNTRY_OPERATOR_MAP.put("Sudan", operatorList);
        operatorList.clear();
        operatorList.add("MTN");
        operatorList.add("syriatel");
        COUNTRY_OPERATOR_MAP.put("Syria", operatorList);
        operatorList.clear();
        operatorList.add("Tunisiana");
        operatorList.add("Tunistelecom");
        COUNTRY_OPERATOR_MAP.put("Tunisia", operatorList);
        operatorList.clear();
        operatorList.add("DU");
        operatorList.add("Etisalat");
        COUNTRY_OPERATOR_MAP.put("UAE", operatorList);
        operatorList.clear();
        operatorList.add("DU");
        operatorList.add("Y");
        operatorList.add("sabafon");
        operatorList.add("MTN");
        operatorList.add("yemenMobile");
        COUNTRY_OPERATOR_MAP.put("yemen", operatorList);
    }

    public Map<String, List<String>> getCountryOperatorMap() {
        return this.COUNTRY_OPERATOR_MAP;
    }
}
