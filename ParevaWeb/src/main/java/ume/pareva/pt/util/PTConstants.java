package ume.pareva.pt.util;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component("ptConstants")
public class PTConstants {
	
	private HashMap<Integer,String> resultCodeMap=new HashMap<Integer,String>();
	private HashMap<Integer,String> actionCodeMap=new HashMap<Integer,String>();
	
	
	@PostConstruct
	public void init() {
		
		resultCodeMap.put(0, "OK");
		resultCodeMap.put(100, "INVALID_CREDENTIALS");
		resultCodeMap.put(101, "NO_AUTHORIZATION");
		resultCodeMap.put(102, "SERVICE_NOT_FOUND");
		resultCodeMap.put(104, "INVALID_DATA");
		resultCodeMap.put(105, "INVALID_STATUS");
		resultCodeMap.put(108, "INVALID_DATA_NO_MCC");
		resultCodeMap.put(109, "INVALID_DATA_NO_MNC");
		resultCodeMap.put(110, "INVALID_DATA_INV_OPERATOR");
		resultCodeMap.put(111, "VALIDATION_GENERAL_ERROR");
		resultCodeMap.put(112, "INVALID_DATA_INV_MSISDN");
		resultCodeMap.put(113, "INVALID_DATA_INV_MSISDN_ON_CARRIER");
		resultCodeMap.put(115, "WIFI_IDENTIFICATION_NOT_SUPPORTED");
		resultCodeMap.put(116, "INVALID_DATA_INV_EMPTY_PIN");
		resultCodeMap.put(117, "INVALID_DATA_INV_PIN_FORMAT");
		resultCodeMap.put(118, "INVALID_DATA_INV_PIN");
		resultCodeMap.put(119, "INVALID_DATA_INV_PIN_NO_RETRY");
		resultCodeMap.put(120, "BILLING_GENERAL_ERROR");
		resultCodeMap.put(124, "BILLING_OPER_GENERAL_ERROR");
		resultCodeMap.put(130, "SHARED_PROVISIONING_ERROR");
		resultCodeMap.put(150, "SUBSCRIPTION_SUSPENDED_ERROR");
		resultCodeMap.put(200, "GENERAL_ERROR");
		
		actionCodeMap.put(0, "ACCESS_GRANTED");
		actionCodeMap.put(1, "REDIRECT_URL");
		actionCodeMap.put(2, "DISPLAY_ERROR");
		
	}


	public HashMap<Integer, String> getResultCodeMap() {
		return resultCodeMap;
	}


	public void setResultCodeMap(HashMap<Integer, String> resultCodeMap) {
		this.resultCodeMap = resultCodeMap;
	}


	public HashMap<Integer, String> getActionCodeMap() {
		return actionCodeMap;
	}


	public void setActionCodeMap(HashMap<Integer, String> actionCodeMap) {
		this.actionCodeMap = actionCodeMap;
	}
	
	

}
