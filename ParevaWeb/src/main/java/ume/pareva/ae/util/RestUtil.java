package ume.pareva.ae.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component("restUtilUAE")
public class RestUtil {
	
	
	public Map<String,String> makeRestCall(String url,Map<String,String> parameterMap){
		
		String parameterName="";
		String parameterValue="";
		String parameter="";
		Set<String> parameterMapKey=parameterMap.keySet();
		Iterator<String> keyIterator=parameterMapKey.iterator();
		while(keyIterator.hasNext()){
			parameterName=keyIterator.next();
			parameterValue=parameterMap.get(parameterName);
			parameter=parameter+"&"+parameterName+"="+parameterValue;
		}
		url=(url+parameter).trim();
		Map<String,String> resultMap=new HashMap<String,String>();
		try{
			RestTemplate restTemplate = new RestTemplate();
			resultMap=prepareResultMap(restTemplate.getForObject(url, String.class));
		}catch(Exception e){
			System.out.println("Error Calling Virgopass Rest API");
			e.printStackTrace();
		}
	    return resultMap;
	}
	
	public Map<String, String> prepareResultMap(String result){
		
		String[] resultSplitByNewLine = result.split("\\r?\\n");
		Map<String,String> resultMap = new HashMap<String,String>();
		for(int i=0;i<resultSplitByNewLine.length;i++){
			String[] resultSplitByColon=resultSplitByNewLine[i].split(":");
			resultMap.put(resultSplitByColon[0].trim(), resultSplitByColon[1].trim());
		}
		
		return resultMap;
		
	}
	
	    
        //Was used for testing
	public static void main(String[] arg){
		
		//176.152.141.22
		//String userInfoUrl="http://billing.virgopass.com/api_v1.5.php?getUserInfo";
		String networkInfoUrl="http://billing.virgopass.com/api_v1.5.php?getNetworkInfo";   
		   
		RestUtil restUtil=new RestUtil();
		Map<String,String> parameterMap=new HashMap<String,String>();
		parameterMap.put("login","universalmobile");
		parameterMap.put("password", "56gnP15A");
		parameterMap.put("ip_address","80.215.131.176");
		
		/*Map<String,String> resutlMap=restUtil.makeRestCall(userInfoUrl, parameterMap);
		System.out.println(resutlMap.get("status"));
		System.out.println(resutlMap.get("subscription_date"));
		System.out.println(resutlMap.get("service_id"));
		System.out.println(resutlMap.get("next_reload"));*/
		
		Map<String,String> resutlMap=restUtil.makeRestCall(networkInfoUrl, parameterMap);
		System.out.println(resutlMap.get("ope_id"));
		System.out.println(resutlMap.get("network"));
		
	}
        

}
