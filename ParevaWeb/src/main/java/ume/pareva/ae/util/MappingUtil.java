package ume.pareva.ae.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import ume.pareva.dao.SdcRequest;

@Component("mappingUtilUAE")
public class MappingUtil {

	public Map<String,String> mapRequestToIPN(HttpServletRequest request){
		Map<String,String> ipnMap=new HashMap<String,String>();
		SdcRequest sdcRequest=new SdcRequest(request);
		ipnMap.put("error_code",sdcRequest.get("error_code"));
		ipnMap.put("event",sdcRequest.get("event"));
		ipnMap.put("purchase_id",sdcRequest.get("purchase_id"));
		ipnMap.put("subscription_id",sdcRequest.get("subscription_id"));
		ipnMap.put("session_id",sdcRequest.get("session_id"));
		ipnMap.put("alias",sdcRequest.get("alias"));
		ipnMap.put("ope_id",sdcRequest.get("ope_id"));
		ipnMap.put("transaction_id",sdcRequest.get("transaction_id"));
		return ipnMap;
	}

	public Map<String,String> mapRequestToCallback(HttpServletRequest request){
		Map<String,String> callbackMap=new HashMap<String,String>();
		SdcRequest sdcRequest=new SdcRequest(request);
		callbackMap.put("smsid",sdcRequest.get("smsid"));
		callbackMap.put("rate",sdcRequest.get("rate"));
		callbackMap.put("status",sdcRequest.get("status"));
		callbackMap.put("msisdn",sdcRequest.get("msisdn"));
		callbackMap.put("action",sdcRequest.get("action"));
		callbackMap.put("smstype",sdcRequest.get("smstype"));
		callbackMap.put("id_application",sdcRequest.get("id_application"));
		callbackMap.put("opid",sdcRequest.get("opid"));
		
		return callbackMap;
	}
}
