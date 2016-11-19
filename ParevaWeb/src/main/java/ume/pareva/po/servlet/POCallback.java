package ume.pareva.po.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubBillingTry;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobileBillingDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.po.util.MappingUtil;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.util.NetworkMapping;


public class POCallback extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(POCallback.class.getName());

	@Autowired
	MappingUtil mappingUtil;

	@Autowired
	MobileClubCampaignDao mobileclubcampaigndao;

	@Autowired
	HandsetDao handsetdao;

	@Autowired
	InternetServiceProvider internetserviceprovider;

	@Autowired
	SubscriptionCreation subscriptioncreation;

	@Autowired
	UmeMobileClubUserDao umemobileclubuserdao;

	@Autowired
	MobileBillingDao mobilebillingdao;

	@Autowired
	CpaLoggerDao cpaloggerdao;
	
	@Autowired
    NetworkMapping networkMapping;

	public POCallback() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Map<String,String> callbackMap=mappingUtil.mapRequestToCallback(request);
		System.out.println("*****************************************Callback Parameter Start***********************************************");

		System.out.println("Error Code: "+callbackMap.get("error_code"));
		System.out.println("Error Desc: "+callbackMap.get("error_desc"));
		System.out.println("Subscription ID: "+callbackMap.get("subscription_id"));
		System.out.println("Session ID: "+callbackMap.get("session_id"));
		System.out.println("Operator ID: "+callbackMap.get("ope_id"));
		System.out.println("Opt1: "+callbackMap.get("opt1"));
		System.out.println("Opt2: "+callbackMap.get("opt2"));

		StringBuilder responsebuilder=new StringBuilder();
		responsebuilder.append("Error Code: "+callbackMap.get("error_code"));
		responsebuilder.append("Error Desc: "+callbackMap.get("error_desc"));
		responsebuilder.append("Subscription ID: "+callbackMap.get("subscription_id"));
		responsebuilder.append("Session ID: "+callbackMap.get("session_id"));
		responsebuilder.append("Operator ID: "+callbackMap.get("ope_id"));
		responsebuilder.append("Opt1: "+callbackMap.get("opt1"));
		responsebuilder.append("Opt2: "+callbackMap.get("opt2"));

		System.out.println("francecallback URL "+responsebuilder.toString());

		System.out.println("*****************************************Callback Parameter End*************************************************");

		String errorCode=callbackMap.get("error_code");
		String sessionType="";
		String network="unknown";
		UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		if(!callbackMap.get("ope_id").equals(""))
			network=networkMapping.getFrNetworkMap().get(callbackMap.get("ope_id"));
		if(errorCode.equals("0")){
			String campaignId=getCampaignAndNetworkType(callbackMap).get("campaign");

			if(!campaignId.equals("")){
				MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
				if (cmpg != null && cmpg.getSrc().trim().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("subscription")) {
					int insertedRows = cpaloggerdao.insertIntoCpaLogging(callbackMap.get("subscription_id"), campaignId, club.getUnique(), 10, network, cmpg.getSrc());
				}

				if (cmpg != null && cmpg.getSrc().trim().endsWith("RS")) {
					//String enMsisdn = MiscCr.encrypt(msisdn);
					int insertedRows =  cpaloggerdao.insertIntoRevShareLogging(0,  cmpg.getPayoutCurrency(), callbackMap.get("subscription_id"), callbackMap.get("subscription_id"), campaignId, club.getUnique(), 0, network, cmpg.getSrc(), 0);
				}

				
			}
			sessionType="newUser";
			saveCookie(callbackMap,response);
			String subResponse=createUser(callbackMap,request);
			if(!campaignId.equals("")&&(subResponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY") || subResponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY"))){
				biLog(callbackMap,request,response);
			}
			redirectUserAndSaveSession(callbackMap,sessionType,request,response);
		}else if(errorCode.equals("37")){
			sessionType="returningUser";
			redirectUserAndSaveSession(callbackMap,sessionType,request,response);

		}else if (errorCode.equals("33")){
			logger.info("User Cancelled Payment");
			response.sendRedirect("http://"+dmn.getDefaultUrl());
		}else{

		}			
	}

	public void saveCookie(Map<String,String> callbackMap,HttpServletResponse response){
		String subscriptionId=callbackMap.get("subscription_id");
		Cookie cookie = new Cookie("subscriptionId",subscriptionId);
		cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
		response.addCookie(cookie);
	}

	public void biLog(Map<String,String> callbackMap,HttpServletRequest request,HttpServletResponse response){
		String network="";
		String template=callbackMap.get("opt2");
		if(!callbackMap.get("ope_id").equals(""))
			network=networkMapping.getFrNetworkMap().get(callbackMap.get("ope_id"));
		String campaignId=getCampaignAndNetworkType(callbackMap).get("campaign");
		String networkType=getCampaignAndNetworkType(callbackMap).get("networkType");
		
		UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
		String subscriptionId=callbackMap.get("subscription_id");
		Handset handset=handsetdao.getHandset(request);
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		String clubUnique=club.getUnique();
	//	String myisp=internetserviceprovider.findIsp(request.getAttribute("ipAddress").toString());
		String sessionId=callbackMap.get("session_id");
		mobileclubcampaigndao.log("callback_ok",template,subscriptionId,sessionId,handset,dmn.getUnique(),campaignId,clubUnique,"SUBSCRIBED",0,request,response,network.substring(0,19),networkType,"","");    
	}

	public String createUser(Map<String,String> callbackMap,HttpServletRequest request){
		String network="unknown";
		UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		String subscriptionId=callbackMap.get("subscription_id");
		String campaignId=getCampaignAndNetworkType(callbackMap).get("campaign");
		String landingPage=callbackMap.get("opt2");
		if(!callbackMap.get("ope_id").equals(""))
			network=networkMapping.getFrNetworkMap().get(callbackMap.get("ope_id"));
		return subscriptioncreation.checkSubscription(subscriptionId,club,campaignId,7,network.substring(0,19),landingPage);
		//SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUserByMsisdn(subscriptionId, club.getUnique());
		//insertBillingTryForUser(clubUser,club); 

	}

	public void insertBillingTryForUser(SdcMobileClubUser clubUser,MobileClub club){
		MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
		mobileClubBillingTry.setTransactionId("");
		mobileClubBillingTry.setResponseCode("003");
		mobileClubBillingTry.setCreated(new Date());
		mobileClubBillingTry.setRegionCode(club.getRegion());
		mobileClubBillingTry.setClubUnique(club.getUnique());
		mobileClubBillingTry.setCampaign(clubUser.getCampaign());
		mobilebillingdao.insertBillingTry(mobileClubBillingTry);
	}

	public void redirectUserAndSaveSession(Map<String,String> callbackMap,String sessionType,HttpServletRequest request,HttpServletResponse response){
		UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		UmeClubDetails userClubDetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());

		HttpSession session=request.getSession();
		try{
			if(userClubDetails.getServiceType().equals("Content")){
				if(userClubDetails.getServedBy().equals("UME")){
					session.setAttribute("sessionType",sessionType);
					response.sendRedirect("http://"+dmn.getDefaultUrl()+"/videos.jsp");					
				}else{
					response.sendRedirect("http://"+dmn.getRedirectUrl());
				}

			}else {
				//redirection code for competition service
			}
		}catch(Exception e){
			logger.error("Error Redirecting To Video Page");
			e.printStackTrace();
		}

	}

	public Map<String,String> getCampaignAndNetworkType(Map<String,String> callbackMap){
		Map<String,String> campaingAndNetwork=new HashMap<String,String>();
		String networkType="";
		String campaign="";
		if(!callbackMap.get("opt1").equals("")&&callbackMap.get("opt1").contains("_")){
			campaign=callbackMap.get("opt1").split("_")[0];
			networkType=callbackMap.get("opt1").split("_")[1];
		}
		campaingAndNetwork.put("networkType", networkType);
		campaingAndNetwork.put("campaign", campaign);
		return campaingAndNetwork;
	}


}
