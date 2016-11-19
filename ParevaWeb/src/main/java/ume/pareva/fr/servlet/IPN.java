package ume.pareva.fr.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubBillingTry;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobileBillingDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UserSessionDao;
import ume.pareva.fr.util.FRUtil;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.MobileClubBillingSuccesses;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UserSession;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.userservice.RegionalDate;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.util.NetworkMapping;
import ume.pareva.util.ZACPA;

public class IPN extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( IPN.class.getName());

	@Autowired
	ZACPA zacpa;

	@Autowired
	SubscriptionCreation subscriptioncreation;

	@Autowired
	UmeMobileClubUserDao umemobileclubuserdao;

	@Autowired
	UmeClubDetailsDao umeClubDetailsDao;

	@Autowired
	MobileBillingDao mobilebillingdao;

	@Autowired
	MobileClubCampaignDao mobileclubcampaigndao;

	@Autowired
	HandsetDao handsetdao;

	@Autowired
	CpaLoggerDao cpaloggerdao;

	@Autowired
	RegionalDate regionaldate;

	@Autowired
	UmeTempCache umesdc;

	@Autowired
	NetworkMapping networkMapping;

	@Autowired
	FRUtil frUtil;

	@Autowired
	UserSessionDao userSessionDao;


	public IPN() {
		super();
	}

	public void init(ServletConfig config) throws ServletException
	{
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

	public void processRequest(HttpServletRequest request, HttpServletResponse response){
		Map<String,String> ipnMap=frUtil.mapRequestToIPN(request);
		saveNotification(ipnMap);
		String event=ipnMap.get("event");
		String sessionId=ipnMap.get("session_id");
		String subscriptionId=ipnMap.get("subscription_id");
		String transactionId=ipnMap.get("transaction_id");
		if(event.trim().equalsIgnoreCase("Subscription")){
			if(!sessionId.equals("")){
				UserSession userSession=userSessionDao.getUserSessionBySessionId(sessionId);
				userSession.setSubscriptionId(subscriptionId);
				userSessionDao.updateUserSession(userSession);
				MobileClub club = UmeTempCmsCache.mobileClubMap.get(userSession.getClubUnique());
				String campaignId=userSession.getCampaignId();
				String subscriptionResponse=createUser(subscriptionId,userSession,request,response);
				SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUserByMsisdn(subscriptionId, club.getUnique());
				if(subscriptionResponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY") || subscriptionResponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY")){
					biLog(subscriptionId,userSession,request,response);
					if(campaignId!=null && !campaignId.trim().equals("")){
						MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
						if(cmpg!=null){
							boolean notifycpa_rs=cmpg.getSrc().trim().toLowerCase().endsWith("cpa") || cmpg.getSrc().trim().endsWith("RS");
							if(notifycpa_rs && cmpg.getCpaType().trim().equalsIgnoreCase("subscription")){
								cpaLog(subscriptionId,userSession,clubUser);
							}
						}
					}
				}
				Date subscribedDate=clubUser.getSubscribed();
				Date today=regionaldate.getRegionalDate(club.getRegion(), new Date());
				String responseCode="";
				if(DateUtils.isSameDay(subscribedDate,today)){
					responseCode="003";
				}else{
					responseCode="00";
				}
				UmeClubDetails umeClubDetails=UmeTempCmsCache.umeClubDetailsMap.get(userSession.getClubUnique());
				if(umeClubDetails.getFreeDay().equals("0"))
					insertBillingTryForUser(clubUser,club,transactionId,responseCode);
			}else{
				logger.error("Virgopass sent blank sessionId");
			}
		}
		else if(event.trim().equalsIgnoreCase("Reload")){
			String clubUnique="";
			UserSession userSession=userSessionDao.getUserSessionBySubscriptionId(subscriptionId);
			if(userSession==null){
				clubUnique="8189288704541KDS";
			}else{
				clubUnique=userSession.getClubUnique();
			}
			SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUserByMsisdn(subscriptionId, clubUnique);
			if(userSession==null){
				userSession=new UserSession.UserSessionBuilder(Misc.generateAnyxSessionId(), clubUnique, clubUser.getCampaign(), clubUser.getLandingpage(), clubUser.getNetworkCode(), "Unknown", new Date())
							.subscriptionId(subscriptionId).build();
			}
			MobileClub club = UmeTempCmsCache.mobileClubMap.get(clubUnique);
			String campaignId=clubUser.getCampaign();
			updateBillingDate(clubUser);
			insertBillingTryForUser(clubUser,club,transactionId,"00");
			if(campaignId!=null && !campaignId.trim().equals("")){
				MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
				if(cmpg!=null){
					boolean notifycpa_rs=cmpg.getSrc().trim().toLowerCase().endsWith("cpa") || cmpg.getSrc().trim().endsWith("RS");
					if(notifycpa_rs && cmpg.getCpaType().trim().equalsIgnoreCase("billing")){
						cpaLog(subscriptionId,userSession,clubUser);
					}
				}
			}
		}else if(event.trim().equalsIgnoreCase("Resiliation")){
			String stop = "STOP";
			String clubUnique="";
			UserSession userSession=userSessionDao.getUserSessionBySubscriptionId(subscriptionId);
			if(userSession==null){
				clubUnique="8189288704541KDS";
			}else{
				clubUnique=userSession.getClubUnique();
			}
			MobileClub club = UmeTempCmsCache.mobileClubMap.get(clubUnique);
			SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUserByMsisdn(subscriptionId, clubUnique);
			Handset handset=handsetdao.getHandset(request);
			if (DateUtils.isSameDay(clubUser.getSubscribed(), new Date())) {
				stop = "STOPFD";
			}
			mobileclubcampaigndao.log("ipn", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), handset, club.getWapDomain(), clubUser.getCampaign(), club.getUnique(), stop, 0, request, response, "", "", "", "");

		}

		logger.info("France Notification Saved");

		System.out.println("*****************************************IPN Notification Start***********************************************");

		StringBuilder responsebuilder=new StringBuilder();
		responsebuilder.append("Error Code: "+ipnMap.get("error_code"));
		responsebuilder.append("Event: "+ipnMap.get("event"));
		responsebuilder.append("Purchase ID: "+ipnMap.get("purchase_id"));
		responsebuilder.append("Subscription ID: "+ipnMap.get("subscription_id"));
		responsebuilder.append("Session ID: "+ipnMap.get("session_id"));
		responsebuilder.append("Alias: "+ipnMap.get("alias"));
		responsebuilder.append("Operator ID: "+ipnMap.get("ope_id"));
		responsebuilder.append("Transaction ID: "+ipnMap.get("transaction_id"));

		System.out.println("francecallback franceIPN "+responsebuilder.toString());

		System.out.println("*****************************************IPN Notification End*************************************************");
	}

	public void updateBillingDate(SdcMobileClubUser clubUser){
		Date billingRenew =new Date();
		Date billingEnd=DateUtils.addDays(billingRenew, 7);
		clubUser.setBillingRenew(billingRenew);
		clubUser.setBillingEnd(billingEnd);
		umemobileclubuserdao.saveItem(clubUser);
	}

	public void insertBillingTryForUser(SdcMobileClubUser clubUser,MobileClub club,String transactionId,String responseCode){
		MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
		mobileClubBillingTry.setParsedMsisdn(clubUser.getParsedMobile());
		mobileClubBillingTry.setTransactionId(transactionId);
		mobileClubBillingTry.setResponseCode(responseCode);
		mobileClubBillingTry.setCreated(new Date());
		mobileClubBillingTry.setRegionCode(club.getRegion());
		mobileClubBillingTry.setClubUnique(club.getUnique());
		mobileClubBillingTry.setCampaign(clubUser.getCampaign());
		mobilebillingdao.insertBillingTry(mobileClubBillingTry);

		if(responseCode.equals(("00"))){
			MobileClubBillingPlan mobileClubBillingPlan= new MobileClubBillingPlan();
			mobileClubBillingPlan.setParsedMobile(clubUser.getParsedMobile());
			mobileClubBillingPlan.setClubUnique(club.getUnique());
			mobileClubBillingPlan.setTariffClass(club.getPrice());
			MobileClubBillingSuccesses mobileClubBillingSuccesses = new MobileClubBillingSuccesses(mobileClubBillingPlan, mobileClubBillingTry);
			mobilebillingdao.insertBillingSuccess(mobileClubBillingSuccesses);
		}
	}

	public String createUser(String subscriptionId,String campaignId,String network,String landingPage,HttpServletRequest request){
		UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		return subscriptioncreation.checkSubscription(subscriptionId,club,campaignId,7,network,landingPage);
		//SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUserByMsisdn(subscriptionId, club.getUnique());
		//insertBillingTryForUser(clubUser,club); 

	}

	public void biLog(String subscriptionId,UserSession userSession,HttpServletRequest request,HttpServletResponse response){
		Handset handset=handsetdao.getHandset(request);
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(userSession.getClubUnique());
		mobileclubcampaigndao.log("ipn",userSession.getLandingPage(),subscriptionId,subscriptionId,handset,club.getWapDomain(),userSession.getCampaignId(),userSession.getClubUnique(),"SUBSCRIBED",0,request,response,userSession.getNetwork(),userSession.getNetworkType(),"","");    
	}

	public String createUser(String subscriptionId,UserSession userSession,HttpServletRequest request,HttpServletResponse response){
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(userSession.getClubUnique());
		return subscriptioncreation.checkSubscription(subscriptionId,club,userSession.getCampaignId(),7,userSession.getNetwork(),userSession.getLandingPage());

	}

	public void cpaLog(String subscriptionId,UserSession userSession,SdcMobileClubUser clubUser){
		String campaignId=userSession.getCampaignId();
		if(!campaignId.equals("")){
			MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
			if (cmpg != null && cmpg.getSrc().trim().toLowerCase().endsWith("cpa")) {
				int insertedRows = cpaloggerdao.insertIntoCpaLogging(subscriptionId, campaignId, userSession.getClubUnique(), 10, userSession.getNetwork(), cmpg.getSrc());
				int updatecpavisit = cpaloggerdao.updateCpaVisitLogMsisdnSubscriptionDate(subscriptionId, clubUser.getSubscribed(), cmpg.getUnique(), userSession.getCpaParam1(), userSession.getCpaParam2(),userSession.getCpaParam3());
			}

			if (cmpg != null && cmpg.getSrc().trim().endsWith("RS")) {
				//String enMsisdn = MiscCr.encrypt(msisdn);
				int insertedRows =  cpaloggerdao.insertIntoRevShareLogging(0,  cmpg.getPayoutCurrency(), subscriptionId, subscriptionId, campaignId, userSession.getClubUnique(), 0, userSession.getNetwork(), cmpg.getSrc(), 0);
				int updatedRows = cpaloggerdao.updateRevShareVisitLogMsisdnSubscriptionDate(subscriptionId, clubUser.getSubscribed(), cmpg.getUnique(), userSession.getRevParam1(), userSession.getRevParam2(), userSession.getRevParam3());
			}


		}
	}

	public void saveNotification(Map<String,String> ipnMap){
		String sqlstr="insert into franceNotification (aUnique,aErrorCode,aEvent,aPurchaseId,aSubscriptionId,aSessionId,aAliase,aOpeId,aTransactionId,aCreated) values("
				+ "'" + Misc.generateUniqueId() + "'"
				+ ",'" + ipnMap.get("error_code") + "'"
				+ ",'" + ipnMap.get("event") + "'"
				+ ",'" + ipnMap.get("purchase_id") + "'"
				+ ",'" + ipnMap.get("subscription_id") + "'"
				+ ",'" + ipnMap.get("session_id") + "'"
				+ ",'" + ipnMap.get("alias") + "'"
				+ ",'" + ipnMap.get("ope_id") + "'"
				+ ",'" + ipnMap.get("transaction_id") + "'"
				+ ",'" + new Timestamp(System.currentTimeMillis()) + "'"
				+ ")";
		zacpa.executeUpdateCPA(sqlstr);
	}


}
