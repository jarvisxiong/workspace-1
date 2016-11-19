package ume.pareva.po.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import ume.pareva.dao.SdcRequest;

@Component("mappingUtilPo")
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
		callbackMap.put("error_code",sdcRequest.get("error_code"));
		callbackMap.put("error_desc",sdcRequest.get("event"));
		callbackMap.put("subscription_id",sdcRequest.get("subscription_id"));
		callbackMap.put("session_id",sdcRequest.get("session_id"));
		callbackMap.put("ope_id",sdcRequest.get("ope_id"));
		callbackMap.put("opt1",sdcRequest.get("opt1"));
		callbackMap.put("opt2",sdcRequest.get("opt2"));
		return callbackMap;
	}
}
