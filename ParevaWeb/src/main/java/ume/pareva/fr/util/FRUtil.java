package ume.pareva.fr.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.fr.FRConstant;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Smtp;
import ume.pareva.userservice.SubscriptionCreation;

@Component("frUtil")
public class FRUtil {

	@Autowired
	SubscriptionCreation subscriptioncreation;

	@Autowired
	HandsetDao handsetdao;

	@Autowired
	MobileClubCampaignDao mobileclubcampaigndao;

	@Autowired
	UmeMobileClubUserDao umemobileclubuserdao;

	@Autowired
	Smtp smtp;

	@Autowired
	UmeRequest umeRequest;

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
		callbackMap.put("error_desc",sdcRequest.get("error_desc"));
		callbackMap.put("subscription_id",sdcRequest.get("subscription_id"));
		callbackMap.put("session_id",sdcRequest.get("session_id"));
		callbackMap.put("ope_id",sdcRequest.get("ope_id"));
		callbackMap.put("opt1",sdcRequest.get("opt1"));
		callbackMap.put("opt2",sdcRequest.get("opt2"));
		callbackMap.put("event",sdcRequest.get("event"));
		callbackMap.put("welcome",sdcRequest.get("welcome"));

		return callbackMap;
	}

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

	public String readCookie(HttpServletRequest request,String cookieName){
		Cookie[] cookies = request.getCookies();
		String cookieValue = "";
		if(cookies!=null){
			for(int loopIndex = 0; loopIndex < cookies.length; loopIndex++) { 
				Cookie cookie1 = cookies[loopIndex];
				if (cookie1.getName().equals(cookieName)) {
					cookieValue=cookie1.getValue();
					break;
				}
			}  
		}
		return cookieValue;
	}

	public void setSessionAttribute(HttpServletRequest request, String attributeName,String attributeValue){
		if(attributeName!=null&&!attributeName.equals("")){
			HttpSession session=request.getSession(false);
			if(session==null){
				session=request.getSession(true);
			}
			session.setAttribute(attributeName,attributeValue);
		}
	}

	public Map<String,String> getUserInfo(String subscriptionId){
		Map<String,String> parameterMap=new HashMap<String,String>();
		parameterMap.put("login",FRConstant.LOGIN);
		parameterMap.put("password",FRConstant.PASSWORD);
		parameterMap.put("subscription_id",subscriptionId);
		Map<String,String> resutlMap=makeRestCall(FRConstant.USER_INFO_URL, parameterMap);
		return resutlMap;
	}

	public Map<String,String> readCpaParamFromCookie(String campaignId,HttpServletRequest request){
		Cookie[] cookies = null;
		Map<String,String> cpaParamMap=new HashMap<String,String>();
		String cpaParam1="",cpaParam2="",cpaParam3="",cpaPubId="";
		cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("cpaparam1") && (cookie.getValue() != null && !cookie.getValue().isEmpty())) {
					cpaParam1 = cookie.getValue();
				}
				if (cookie.getName().equals("cpaparam2") && (cookie.getValue() != null && !cookie.getValue().isEmpty())) {
					cpaParam2 = cookie.getValue();
				}
				if (cookie.getName().equals("cpaparam3") && (cookie.getValue() != null && !cookie.getValue().isEmpty())) {
					cpaParam3 = cookie.getValue();
				}
				if(cookie.getName().equals("cpapubid") && (cookie.getValue()!=null && !cookie.getValue().isEmpty())){
					cpaPubId=cookie.getValue();
				}
			} 
		}
		cpaParamMap.put("cpaParam1",cpaParam1);
		cpaParamMap.put("cpaParam2",cpaParam2);
		cpaParamMap.put("cpaParam3",cpaParam3);
		cpaParamMap.put("cpaPubId",cpaPubId);
		return cpaParamMap;
	}

	public Map<String,String> readRevshareParamFromCookie(String campaignId,HttpServletRequest request){
		Cookie[] cookies = null;
		Map<String,String> RevshareParamMap=new HashMap<String,String>();
		String revParam1="",revParam2="",revParam3="";
		cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("revparam1") && (cookie.getValue() != null && !cookie.getValue().isEmpty())) {
					revParam1 = cookie.getValue();
				}
				if (cookie.getName().equals("revparam2") && (cookie.getValue() != null && !cookie.getValue().isEmpty())) {
					revParam2 = cookie.getValue();
				}
				if (cookie.getName().equals("revparam3") && (cookie.getValue() != null && !cookie.getValue().isEmpty())) {
					revParam3 = cookie.getValue();
				}
			}
		}
		RevshareParamMap.put("revParam1",revParam1);
		RevshareParamMap.put("revParam2",revParam2);
		RevshareParamMap.put("revParam3",revParam3);
		return RevshareParamMap;
	}

	public RestTemplate getRestTemplate(){
		RestTemplate restTemplate=new RestTemplate();
		restTemplate.setRequestFactory(getClientRequestFactory());
		return restTemplate;
	}

	private HttpComponentsClientHttpRequestFactory getClientRequestFactory(){
		HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory=new HttpComponentsClientHttpRequestFactory();
		HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
		httpComponentsClientHttpRequestFactory.setConnectTimeout(5000);
		httpComponentsClientHttpRequestFactory.setReadTimeout(5000);
		httpComponentsClientHttpRequestFactory.setHttpClient(httpClient);
		return httpComponentsClientHttpRequestFactory;

	}

	public boolean sendPassword(SdcMobileClubUser clubUser,UmeDomain dmn){
		String from=FRConstant.FROM;
		String to=clubUser.getParam1();
		String subject="Login Details";
		String personalLink="http://"+dmn.getDefaultUrl()+"/login.jsp?username="+clubUser.getParam1()+"&password="+SdcMisc.decrypt(clubUser.getParam2());
		String body="Voici vos identifiants:\n" 
				+"Identifiant: "+clubUser.getParam1()+"\n"
				+"Mot de passe: "+SdcMisc.decrypt(clubUser.getParam2())+"\n"
				+"Cliquez ici pour accéder au service: "+personalLink;	
		String cc="";
		String bcc="";
		return smtp.send(from,to,subject,body,cc,bcc);

	}

	public String getServiceUrl(){
		UmeDomain dmn=umeRequest.getDomain();
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		UmeClubDetails userClubDetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
		String serviceUrl="";
		if(userClubDetails.getServiceType().equals("Content")){
			if(userClubDetails.getServedBy().equals("UME")){
				serviceUrl="http://"+dmn.getDefaultUrl()+"/videos.jsp";
				//response.sendRedirect("http://"+dmn.getDefaultUrl()+"/videos.jsp");					
			}else{
				serviceUrl="http://"+dmn.getRedirectUrl();
				//response.sendRedirect("http://"+dmn.getRedirectUrl());
			}

		}else {
			//redirection code for competition service
		}
		return serviceUrl;
	}
}
