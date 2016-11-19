package ume.pareva.pt.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import ume.pareva.aggregator.go4mobility.Go4MobilityService;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.SdcSmsMessage;
import ume.pareva.pojo.SdcLanguage;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;

import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.GetSubscriptionPortfolioRequest;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.GetSubscriptionPortfolioResult;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.SubscriptionDetail;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.UserIdToken;

@Component("umeRequest")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS,value = "request")
public class UmeRequest {

	private static final Logger logger = LogManager.getLogger( UmeRequest.class.getName());
	private UserIdToken userIdToken;

	@Autowired 
	Go4MobilityService go4MobilityService;

	@Autowired 
	private HttpServletRequest req;

	SdcLanguage lang;
	String pageEnc = "";

	/* public UmeRequest(HttpServletRequest req) {
        this.req = req;
        lang = (SdcLanguage) req.getAttribute("sdc_lang");
        pageEnc = getEncoding();
    }*/



	public UmeUser getUser() { return (UmeUser) req.getAttribute("sdc_user"); }
	public UmeDomain getDomain() { return (UmeDomain) req.getAttribute("sdc_domain"); }
	public SdcService getService() { return (SdcService) req.getAttribute("sdc_service"); }
	public SdcSmsMessage getSmsMessage() { return (SdcSmsMessage) req.getAttribute("sdc_smsmsg"); }
	public String getMsisdn() { return req.getAttribute("sdc_msisdn")!=null ? (String) req.getAttribute("sdc_msisdn") : ""; }

	public SdcLanguage getLanguage() { return lang; }
	public String getLanguageCode() { 
		if (lang!=null) return lang.getLanguageCode();
		else return "en";
	}
	public String getStylesheet() {
		if (lang!=null) return lang.getStylesheet();
		else return "";
	}
	public String getEncoding() {
		if (lang!=null) return lang.getEncoding();
		else return "utf-8";
	}
	public String getPageEnc() { return getEncoding(); }    
	public void setPageEnc(String enc) { pageEnc = enc; }

	public String getFileName() {
		String fileName = req.getServletPath();
		fileName = fileName.substring(fileName.lastIndexOf("/")+1);
		fileName = fileName.substring(0,fileName.lastIndexOf("."));
		return fileName;
	}

	public String get(String name) { return get(name, "", true, pageEnc); }
	public String get(String name, boolean trim) { return get(name, "", trim, pageEnc); }
	public String get(String name, String defaultValue) { return get(name, defaultValue, true, pageEnc); }
	public String get(String name, String defaultValue, boolean trim) { return get(name, defaultValue, trim, pageEnc); }

	public String get(String name, String defaultValue, boolean trim, String encoding) {
		String temp = req.getParameter(name);
		if (temp!=null && !temp.equals("")) {
			temp = SdcMisc.encodeUnicode(temp, encoding);
			if (trim) temp = temp.trim();
			return temp;
		}
		if(defaultValue==null){
			return "";
		}
		return defaultValue;
	}

	public int getInt(String name) { return getInt(name, 0, true, pageEnc); }
	public int getInt(String name, boolean trim) { return getInt(name, 0, trim, pageEnc); }
	public int getInt(String name, int defaultValue) { return getInt(name, defaultValue, true, pageEnc); }
	public int getInt(String name, int defaultValue, boolean trim) { return getInt(name, defaultValue, trim, pageEnc); }

	public int getInt(String name, int defaultValue, boolean trim, String encoding) {
		String temp = req.getParameter(name);        
		if (temp!=null && !temp.equals("")) {
			int intTemp = 0;
			if (trim) temp = temp.trim();
			try { intTemp = Integer.parseInt(temp); } catch (NumberFormatException e) {}
			return intTemp;
		}
		return defaultValue;
	}

	public static String get(String name, HttpServletRequest req) { return get(name, "", true, req); }
	public static String get(String name, boolean trim, HttpServletRequest req) { return get(name, "", trim, req); }
	public static String get(String name, String defaultValue, HttpServletRequest req) { return get(name, defaultValue, true, req); }
	public static String get(String name, String defaultValue, boolean trim, HttpServletRequest req) {
		if (req.getParameter(name)!=null && !req.getParameter(name).equals("")) {
			if (trim) return req.getParameter(name).trim();
			else return req.getParameter(name);
		}
		return defaultValue;
	}


	public String readCookie(String cookieName){
		Cookie[] cookies = req.getCookies();
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

	public Cookie getCookie(String cookieName){
		Cookie[] cookies = req.getCookies();
		Cookie cookie=null;
		if(cookies!=null){
			for(int loopIndex = 0; loopIndex < cookies.length; loopIndex++) { 
				cookie = cookies[loopIndex];
				if (cookie.getName().equals(cookieName)) {
					break;
				}
			}  
		}
		return cookie;
	}


	public String getNetworkCode(){
		HttpSession session=req.getSession();
		String networkCode="";
		if(session.getAttribute("networkCode")!=null){
			networkCode=session.getAttribute("networkCode").toString();
		}else{
			String subscriptionId=readCookie("subscriptionId");
			if(!subscriptionId.equals("")){
				logger.info("Analyzing Subscription Cookie");
				userIdToken=go4MobilityService.getUserIdToken(Go4MobilityService.login, Go4MobilityService.pw);
				GetSubscriptionPortfolioRequest getSubscriptionPortfolioRequest=go4MobilityService.getSubscriptionPortfolioRequest(userIdToken, Go4MobilityService.serviceName);
				GetSubscriptionPortfolioResult subscriptionPortfolioResult=go4MobilityService.getSubscriptionPortfolio(getSubscriptionPortfolioRequest);
				if(subscriptionPortfolioResult!=null){
					if(subscriptionPortfolioResult.getResultCode()==0){
						List<SubscriptionDetail> subscriptionDetailList=new ArrayList<SubscriptionDetail>();
						if(subscriptionPortfolioResult.getSubscriptionDetail().length>0){
							try{
								subscriptionDetailList=(Arrays.asList(subscriptionPortfolioResult.getSubscriptionDetail()));
							}catch(Exception e){
								logger.error("Exception: Error Converting Subscripton Detail Array to ArrayList");	
							}
						}
						for(SubscriptionDetail subscriptionDetail: subscriptionDetailList){
							if(subscriptionDetail.getSubscriptionId().equals(subscriptionId)){
								String subscriptionStatus=subscriptionDetail.getStatus();
								logger.info("Subscription Status: {} for Subscription Id: {}",subscriptionStatus,subscriptionId);
								if(subscriptionStatus.equals("ACTIVE")){
									session.setAttribute("subscriptionId",subscriptionDetail.getSubscriptionId());
									session.setAttribute("networkCode",subscriptionDetail.getMnc());
									networkCode=subscriptionDetail.getMnc();
									//					redirectUserAndSaveSession(subscriptionStatus,request,response);
								}else{
									session.setAttribute("subscriptionStatus",subscriptionDetail.getStatus());
								}
							}
						}
					}
				}

			}else{
				if(req.getParameter("networkCode")!=null){
					networkCode=req.getParameter("networkCode");
					session.setAttribute("networkCode",networkCode);
				}
			}


		}
		return networkCode;

	}
}
