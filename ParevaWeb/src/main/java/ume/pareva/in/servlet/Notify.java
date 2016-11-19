package ume.pareva.in.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;



import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubBillingTry;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobBillNotificationDao;
import ume.pareva.dao.MobileBillingDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.pojo.MobBillCancellation;
import ume.pareva.pojo.MobBillNewActivation;
import ume.pareva.pojo.MobBillPayment;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.MobileClubBillingSuccesses;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.userservice.RegionalDate;
import ume.pareva.userservice.StopUser;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.util.DateUtil;

/**
 * Servlet implementation class Notify
 */
//@WebServlet("/Notify")
public class Notify extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Autowired
    MobBillNotificationDao mobBillNotificationDao;

    @Autowired
    MobileClubCampaignDao campaingdao;

    @Autowired
    HandsetDao handsetdao;

    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;

    @Autowired
    MobileBillingDao mobilebillingdao;

    @Autowired
    CpaLoggerDao cpaloggerdao;

    @Autowired
    SubscriptionCreation subscriptioncreation;
    
    @Autowired
    StopUser stopuser;
    
    @Autowired
    RegionalDate regionaldate;
    
    @Autowired
	MobileClubCampaignDao mobileclubcampaigndao;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Notify() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeDomain dmn = aReq.getDomain();
        String domain = dmn.getUnique();
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
        Handset handset = handsetdao.getHandset(request);
        MobileClubCampaign cmpg = null;
        String transactionId=aReq.get("transactionId");
        String landingPage = "";
        String campaignId = "";
        String type = aReq.get("type");
        if (type.equals("new_subscription")) {
            MobBillNewActivation mobBillNewActivation = new MobBillNewActivation();
            mobBillNewActivation.setPartner(aReq.get("partner"));
            mobBillNewActivation.setType(aReq.get("type"));
            mobBillNewActivation.setMobBillId(aReq.get("mobbillId"));
            mobBillNewActivation.setSubscriptionId(aReq.get("subscriptionId"));
            mobBillNewActivation.setDate(DateUtil.unixTimestamptoSqlTimestamp(aReq.get("date")));
            System.out.println("AffiliateData=" + aReq.get("affiliateData"));
            String[] affiliateData = aReq.get("affiliateData").split("\\|");
            for (int i = 0; i < affiliateData.length; i++) {
                System.out.println("AffiliateData[" + i + "] = " + affiliateData[i]);
                if (affiliateData[i].contains("landingPage")) {
                    landingPage = affiliateData[i].substring(affiliateData[i].indexOf("=") + 1);
                    mobBillNewActivation.setLandingPage(landingPage);
                } else if (affiliateData[i].contains("cid")) {
                    campaignId = affiliateData[i].substring(affiliateData[i].indexOf("=") + 1);
                    mobBillNewActivation.setCampaignId(campaignId);
                }

            }
            //mobBillNewActivation.setAffiliateData(aReq.get("affiliateData"));
            mobBillNewActivation.setOperator(aReq.get("operator"));
            mobBillNewActivation.setUserId(aReq.get("userId"));
            mobBillNotificationDao.saveMobBillNewActivation(mobBillNewActivation);

            subscriptioncreation.checkSubscription(mobBillNewActivation.getSubscriptionId(), club, campaignId, 7, mobBillNewActivation.getOperator(), landingPage);
            SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUserByMsisdn(mobBillNewActivation.getSubscriptionId(), club.getUnique());
            //======== CPA Notification ===================================

            if (campaignId != null && !campaignId.equals("")) {
                cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
                mobileclubcampaigndao.log("notify",landingPage,clubUser.getUserUnique(),aReq.get("subscriptionId"),handset,dmn.getUnique(),campaignId,club.getUnique(),"SUBSCRIBED",0,request,response,Misc.encodeForDb(aReq.get("operator")));
            }
            if (cmpg != null && cmpg.getSrc().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("subscription")) {
                // 2016.01.13 - AS - Removed commented code, check repo history if needed
                int insertedRows = cpaloggerdao.insertIntoCpaLogging(clubUser.getParsedMobile(), cmpg.getUnique(), club.getUnique(), 10, clubUser.getNetworkCode(), cmpg.getSrc());
            }

            //============END CPA Notification ===========================
        
        } else if (type.equals("activation_payment")) {
            MobBillPayment mobBillPayment = new MobBillPayment();
            mobBillPayment.setPartner(aReq.get("partner"));
            mobBillPayment.setType(aReq.get("type"));
            mobBillPayment.setSubscriptionId(aReq.get("subscriptionId"));
            mobBillPayment.setBillingId(Long.parseLong(aReq.get("billingId")));
            mobBillPayment.setTransationId(Long.parseLong(aReq.get("transactionId")));
            mobBillPayment.setDate(DateUtil.unixTimestamptoSqlTimestamp(aReq.get("date")));
            mobBillPayment.setAffiliateData(aReq.get("affiliateData"));
            mobBillPayment.setCurrrency(aReq.get("currency"));
            mobBillPayment.setEndUserSuspend(Long.parseLong(aReq.get("endUserSpend")));
            mobBillPayment.setOutPayment(Long.parseLong(aReq.get("outPayment")));
            mobBillNotificationDao.saveMobBillPayment(mobBillPayment);

            String[] affiliateData = aReq.get("affiliateData").split("\\|");
            for (int i = 0; i < affiliateData.length; i++) {
                System.out.println("AffiliateData[" + i + "] = " + affiliateData[i]);
                if (affiliateData[i].contains("cid")) {
                    campaignId = affiliateData[i].substring(affiliateData[i].indexOf("=") + 1);
                }

            }
            String responseCode="";
            String responseDescription=type;
            SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUserByMsisdn(mobBillPayment.getSubscriptionId(), club.getUnique());
			//String transactionId=aReq.get("transactionId");
			Date subscribedDate=clubUser.getSubscribed();
			Date today=regionaldate.getRegionalDate(club.getRegion(), new Date());
		    if(DateUtils.isSameDay(subscribedDate,today)){
		    	responseCode="003";
		    }else{
		    	responseCode="00";
		    }
		    if(mobBillPayment.getOutPayment()==0){
		    	responseCode="51";
		    	responseDescription="Insufficient Fund";
		    }
            updateBillingDate(clubUser);
			insertBillingTryForUser(clubUser,club,transactionId,responseCode,responseDescription,mobBillPayment.getEndUserSuspend()/100);
		
            //======== CPA Notification ===================================
            if (campaignId != null && !campaignId.equals("")) {
                cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
            }
            if (cmpg != null && cmpg.getSrc().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("billing")) {
                // 2016.01.13 - AS - Removed commented code, check repo history if needed
                int insertedRows = cpaloggerdao.insertIntoCpaLogging(clubUser.getParsedMobile(), cmpg.getUnique(), club.getUnique(), 10, clubUser.getNetworkCode(), cmpg.getSrc());
            }
            //============END CPA Notification ===========================
            
            

        } else if (type.equals("renewal")) {
        	MobBillPayment mobBillPayment = new MobBillPayment();
            mobBillPayment.setPartner(aReq.get("partner"));
            mobBillPayment.setType(aReq.get("type"));
            mobBillPayment.setSubscriptionId(aReq.get("subscriptionId"));
            mobBillPayment.setBillingId(Long.parseLong(aReq.get("billingId")));
            mobBillPayment.setTransationId(Long.parseLong(aReq.get("transactionId")));
            mobBillPayment.setDate(DateUtil.unixTimestamptoSqlTimestamp(aReq.get("date")));
            mobBillPayment.setAffiliateData(aReq.get("affiliateData"));
            mobBillPayment.setCurrrency(aReq.get("currency"));
            mobBillPayment.setEndUserSuspend(Long.parseLong(aReq.get("endUserSpend")));
            mobBillPayment.setOutPayment(Long.parseLong(aReq.get("outPayment")));
            mobBillNotificationDao.saveMobBillPayment(mobBillPayment);
            
            SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUserByMsisdn(mobBillPayment.getSubscriptionId(), club.getUnique());
			//String transactionId=aReq.get("transactionId");
            String responseCode="";
            String responseDescription=type;
            Date subscribedDate=clubUser.getSubscribed();
			Date today=regionaldate.getRegionalDate(club.getRegion(), new Date());
		    if(DateUtils.isSameDay(subscribedDate,today)){
		    	responseCode="003";
		    }else{
		    	responseCode="00";
		    }
		    if(mobBillPayment.getOutPayment()==0){
		    	responseCode="51";
		    	responseDescription="Insufficient Fund";
		    }
            
            updateBillingDate(clubUser);
			insertBillingTryForUser(clubUser,club,transactionId,responseCode,responseDescription,(1d*mobBillPayment.getEndUserSuspend())/100);
			
        }else if (type.equals("terminated")) {
            String stop = "STOP";
            MobBillCancellation mobBillCancellation = new MobBillCancellation();
            mobBillCancellation.setPartner(aReq.get("partner"));
            mobBillCancellation.setType(aReq.get("type"));
            mobBillCancellation.setSubscriptionId(aReq.get("subscriptionId"));
            mobBillCancellation.setDate(DateUtil.unixTimestamptoSqlTimestamp(aReq.get("date")));
            mobBillCancellation.setAffiliateData(aReq.get("affiliateData"));
            mobBillNotificationDao.saveMobBillCancellation(mobBillCancellation);
            stopuser.stopSingleSubscriptionNormal(aReq.get("subscriptionId"), club.getUnique(), request, response);
            
        }
    }
    
    public void insertBillingTryForUser(SdcMobileClubUser clubUser,MobileClub club,String transactionId,String responseCode,String responseDescription,double tariffClass){
		MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
		mobileClubBillingTry.setTransactionId(transactionId);
		mobileClubBillingTry.setResponseCode(responseCode);
		mobileClubBillingTry.setCreated(new Date());
		mobileClubBillingTry.setRegionCode(club.getRegion());
		mobileClubBillingTry.setClubUnique(club.getUnique());
		mobileClubBillingTry.setCampaign(clubUser.getCampaign());
		mobileClubBillingTry.setResponseDesc(responseDescription);
		mobileClubBillingTry.setTariffClass(tariffClass);
		mobileClubBillingTry.setParsedMsisdn(clubUser.getParsedMobile());
                mobileClubBillingTry.setAggregator("mobbill");
                mobileClubBillingTry.setNetworkCode(clubUser.getNetworkCode());
		mobilebillingdao.insertBillingTry(mobileClubBillingTry);
                
                      if(responseCode.equals("003") || responseCode.equals(("00"))){
                        MobileClubBillingPlan mobileClubBillingPlan= new MobileClubBillingPlan();
                        mobileClubBillingPlan.setParsedMobile(clubUser.getParsedMobile());
                        mobileClubBillingPlan.setClubUnique(club.getUnique());
                        mobileClubBillingPlan.setTariffClass(tariffClass);
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
    
   
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

}
