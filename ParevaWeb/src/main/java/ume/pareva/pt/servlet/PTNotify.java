package ume.pareva.pt.servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

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
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobileBillingDao;
import ume.pareva.dao.QueryHelper;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.MobileClubBillingSuccesses;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.PTUtility;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Misc;
import ume.pareva.userservice.RegionalDate;
import ume.pareva.userservice.StopUser;
import ume.pareva.util.NetworkMapping;
import ume.pareva.util.ZACPA;


public class PTNotify extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( PTNotify.class.getName());

	@Autowired
	UmeRequest umeRequest;

	@Autowired
	StopUser stopuser;

	@Autowired
	UmeMobileClubUserDao umemobileclubuserdao;

	@Autowired
	CpaLoggerDao cpaloggerdao;

	@Autowired
	RegionalDate regionaldate;

	@Autowired
	MobileBillingDao mobilebillingdao;

	@Autowired
	PTUtility ptUtility;

	@Autowired
	NetworkMapping networkMapping;

	@Autowired
	ZACPA zacpa;

	@Autowired
	QueryHelper queryHelper;

	public PTNotify() {
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

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UmeDomain dmn=umeRequest.getDomain();
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		String type=umeRequest.get("type");
		String retry=umeRequest.get("retry");
		MobileClubCampaign cmpg = null;
		if(type.equals("CHARGE_OK")){
			String transactionId=umeRequest.get("transactionId");
			String subscriptionId=umeRequest.get("subscriptionId");
			boolean inserted=false;
			if(!retry.equals("")){
				String sqlstr="Select * from portugalNotification where aTransactionId="+transactionId;
				if(queryHelper.executeSelectQuery(sqlstr)>0)
					inserted=true;
			}
			if(!inserted){
				saveNotification();
				SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUserByMsisdn(subscriptionId, club.getUnique());
				String campaignId=clubUser.getCampaign();
				if (campaignId != null && !campaignId.equals("")) {
					cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
				}
				if (cmpg != null && cmpg.getSrc().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("billing")) {
					// 2016.01.13 - AS - Removed commented code, check repo history if needed
					int insertedRows = cpaloggerdao.insertIntoCpaLogging(clubUser.getParsedMobile(), cmpg.getUnique(), club.getUnique(), 10, clubUser.getNetworkCode(), cmpg.getSrc());
				}
				String responseCode="";
				Date subscribedDate=clubUser.getSubscribed();
				Date today=regionaldate.getRegionalDate(club.getRegion(), new Date());
				if(DateUtils.isSameDay(subscribedDate,today)){
					responseCode="003";
				}else{
					responseCode="00";
				}
				updateBillingDate(clubUser);
				insertBillingTryForUser(clubUser,club,transactionId,responseCode);
			}
		}else if(type.equals("UNSUBSCRIBE")){
			saveNotification();
			String subscriptionId=umeRequest.get("subscriptionId");
			stopuser.stopSingleSubscriptionNormal(subscriptionId, club.getUnique(), request, response);

		}
	}

	public void insertBillingTryForUser(SdcMobileClubUser clubUser,MobileClub club,String transactionId,String responseCode){
		MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
		mobileClubBillingTry.setTransactionId(transactionId);
		mobileClubBillingTry.setResponseCode(responseCode);
		mobileClubBillingTry.setCreated(new Date());
		mobileClubBillingTry.setRegionCode(club.getRegion());
		mobileClubBillingTry.setClubUnique(club.getUnique());
		mobileClubBillingTry.setCampaign(clubUser.getCampaign());
		mobileClubBillingTry.setParsedMsisdn(clubUser.getParsedMobile());
		mobilebillingdao.insertBillingTry(mobileClubBillingTry);
		if(responseCode.equals("003") || responseCode.equals(("00"))){
			MobileClubBillingPlan mobileClubBillingPlan= new MobileClubBillingPlan();
			mobileClubBillingPlan.setParsedMobile(clubUser.getParsedMobile());
			mobileClubBillingPlan.setClubUnique(club.getUnique());
			mobileClubBillingPlan.setTariffClass(club.getPrice());
			MobileClubBillingSuccesses mobileClubBillingSuccesses = new MobileClubBillingSuccesses(mobileClubBillingPlan, mobileClubBillingTry);
			mobilebillingdao.insertBillingSuccess(mobileClubBillingSuccesses);
		}
	}

	public void updateBillingDate(SdcMobileClubUser clubUser){
		Date billingRenew =new Date();
		Date billingEnd=DateUtils.addDays(billingRenew, 7);
		clubUser.setBillingRenew(billingRenew);
		clubUser.setBillingEnd(billingEnd);
		umemobileclubuserdao.saveItem(clubUser);
	}

	public void saveNotification(){
		String aUnique=Misc.generateUniqueId();
		String aType=umeRequest.get("type");
		String aService=umeRequest.get("service");
		String aRetry=umeRequest.get("retry");
		String aMovementDate=umeRequest.get("movementDate");
		if(!aMovementDate.equals(""))
			aMovementDate=ptUtility.xmlDateToSqlDate(aMovementDate);
		else
			aMovementDate="1970-01-01 00:00:00";
		double aValue=Double.parseDouble(!(umeRequest.get("value").equals(""))?umeRequest.get("value"):"0");
		String aCurrency=umeRequest.get("currency");
		String aMCC=umeRequest.get("mcc");
		String aMNC=umeRequest.get("mnc");
		String aNetwork=networkMapping.getPtNetworkMap().get(aMNC);
		String aTransactionId=umeRequest.get("transactionId");
		String aSubscriptionId=umeRequest.get("subscriptionId");
		String aMsisdn=umeRequest.get("msisdn");
		String aSubscriberId=umeRequest.get("subscriberId");
		String aActivationDate=umeRequest.get("activationDate");
		if(!aActivationDate.equals(""))
			aActivationDate=ptUtility.xmlDateToSqlDate(aActivationDate);
		else
			aActivationDate="1970-01-01 00:00:00";
		String aDeactivationDate=umeRequest.get("deactivationDate");
		if(!aDeactivationDate.equals(""))
			aDeactivationDate=ptUtility.xmlDateToSqlDate(aDeactivationDate);
		else
			aDeactivationDate="1970-01-01 00:00:00";
		String aStatusDate=umeRequest.get("statusDate");
		if(!aStatusDate.equals(""))
			aStatusDate=ptUtility.xmlDateToSqlDate(aStatusDate);
		else
			aStatusDate="1970-01-01 00:00:00";
		String sqlstr="insert into portugalNotification (aUnique,aType,aService,aRetry,aMovementDate,aValue,aCurrency,aMCC,aMNC,aNetwork,aTransactionId,aSubscriptionId,"
				+ "aMsisdn,aSubscriberId,aActivationDate,aDeactivationDate,aStatusDate,aCreated) values("
				+ "'" + aUnique + "'"
				+ ",'" + aType + "'"
				+ ",'" + aService + "'"
				+ ",'" + aRetry + "'"
				+ ",'" + aMovementDate + "'"
				+ ",'" + aValue + "'"
				+ ",'" + aCurrency + "'"
				+ ",'" + aMCC + "'"
				+ ",'" + aMNC + "'"
				+ ",'" + aNetwork + "'"
				+ ",'" + aTransactionId + "'"
				+ ",'" + aSubscriptionId + "'"
				+ ",'" + aMsisdn + "'"
				+ ",'" + aSubscriberId + "'"
				+ ",'" + aActivationDate + "'"
				+ ",'" + aDeactivationDate + "'"
				+ ",'" + aStatusDate + "'"
				+ ",'" + new Timestamp(System.currentTimeMillis()) + "'"
				+ ")";
		System.out.println(sqlstr);
		zacpa.executeUpdateCPA(sqlstr);

	}

}
