package ume.pareva.ae.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobileBillingDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.ae.util.MappingUtil;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.userservice.RegionalDate;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.util.NetworkMapping;
import ume.pareva.util.ZACPA;

@WebServlet("/AEIPN")
public class AEIPN extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AEIPN.class.getName());
    
	@Autowired
	MappingUtil mappingUtil;
	
	@Autowired
	ZACPA zacpa;
	
	@Autowired
	SubscriptionCreation subscriptioncreation;
	
	@Autowired
    UmeMobileClubUserDao umemobileclubuserdao;
	
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

	public AEIPN() {
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
		Map<String,String> ipnMap=mappingUtil.mapRequestToIPN(request);
		String sqlstr="insert into uaeNotification (aUnique,aErrorCode,aEvent,aPurchaseId,aSubscriptionId,aSessionId,aAliase,aOpeId,aTransactionId,aCreated) values("
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
		String event=ipnMap.get("event");
		UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		String subscriptionId=ipnMap.get("subscription_id");
		String responseCode="";
		SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUserByMsisdn(subscriptionId, club.getUnique());
		String transactionId=ipnMap.get("transaction_id");
		Date subscribedDate=clubUser.getSubscribed();
		Date today=regionaldate.getRegionalDate(club.getRegion(), new Date());
		if(DateUtils.isSameDay(subscribedDate,today)){
			responseCode="003";
		}else{
			responseCode="00";
		}
		if(event.equals("Subscription")){
			//cpaLog(ipnMap,clubUser,club);
			
			
			
			insertBillingTryForUser(clubUser,club,transactionId,responseCode);
		}
		else if(event.equals("Reload")){
			updateBillingDate(clubUser);
			insertBillingTryForUser(clubUser,club,transactionId,responseCode);
		//	cpaLog(ipnMap,clubUser,club);
		}else if(event.equals("Resiliation")){
			String stop = "STOP";
			Handset handset=handsetdao.getHandset(request);
			if (DateUtils.isSameDay(clubUser.getSubscribed(), new Date())) {
                stop = "STOPFD";
            }
            mobileclubcampaigndao.log("ipn", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), handset, dmn.getUnique(), clubUser.getCampaign(), club.getUnique(), stop, 0, request, response, "", "", "", "");

		}
		
		logger.info("UAE Notification Saved");
		
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
		
		System.out.println("uaecallback uaeIPN "+responsebuilder.toString());
		
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
                System.out.println("frbillingtries "+mobileClubBillingTry.getParsedMsisdn()+" responsecode "+mobileClubBillingTry.getResponseCode());
		mobilebillingdao.insertBillingTry(mobileClubBillingTry);
	}
	
	public void cpaLog(Map<String,String> ipnMap,SdcMobileClubUser clubUser,MobileClub club){
		String campaignId=clubUser.getCampaign();
		if(!campaignId.equals("")){
			MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
			if (cmpg != null && cmpg.getSrc().trim().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("billing")) {
				int insertedRows = cpaloggerdao.insertIntoCpaLogging(ipnMap.get("subscription_id"), campaignId, club.getUnique(), 10, clubUser.getNetworkCode(), cmpg.getSrc());
			}

			if (cmpg != null && cmpg.getSrc().trim().endsWith("RS")) {
				//String enMsisdn = MiscCr.encrypt(msisdn);
				int insertedRows =  cpaloggerdao.insertIntoRevShareLogging(0,  cmpg.getPayoutCurrency(), ipnMap.get("subscription_id"), ipnMap.get("subscription_id"), campaignId, club.getUnique(), 0, clubUser.getNetworkCode(), cmpg.getSrc(), 0);
			}
		}

	}


}
