package ume.pareva.ke.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.tempuri.APIRequestResponse;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.SdcMiscDate;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeClubMessages;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.smsapi.ZaSmsSubmit;
import ume.pareva.smsservices.SmsService;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.userservice.UserAuthentication;

import com.kenya.sms.service.KenyaSms;
import com.zadoi.service.ZaDoi;

public class KEConfirm extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Autowired
	UmeMobileClubUserDao umemobileclubuserdao;
	
	@Autowired
	UmeUserDao umeuserdao;
	
	@Autowired
	MobileClubDao mobileclubdao;
    
	@Autowired
	MobileClubCampaignDao mobileclubcampaigndao;
	
	@Autowired
	HandsetDao handsetdao;
	
	@Autowired
    UserAuthentication userauthentication;
	
	@Autowired
    MobileClubBillingPlanDao billingplandao;

	@Autowired
	SubscriptionCreation subscriptioncreation;

	@Autowired
	UmeClubDetailsDao umeclubdetailsdao;

	@Autowired
    SmsService smsservice;
    
	@Autowired
    CpaLoggerDao cpaloggerdao;
	
	@Autowired
    UmeSmsDao umesmsdao;
	

    public KEConfirm() {
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
		Map<String,Object>subscriptionMap=new HashMap<String,Object>();
		SdcRequest sdcRequest=new SdcRequest(request);
		UmeDomain dmn=sdcRequest.getDomain();
		String optin = sdcRequest.get("optin");
		String requestReference = sdcRequest.get("requestreference");
		String serviceIdentifier = sdcRequest.get("serviceidentifier");
		String result = sdcRequest.get("result");//if result is confirm then go to confirm page same as wap
		String msisdn = sdcRequest.get("msisdn").trim();
		String errorDescription = sdcRequest.get("errordescription");
		String operatorId = sdcRequest.get("operatorid");
		String timestamp = sdcRequest.get("timestamp");
		String stsClubName=sdcRequest.get("clubname");
		String requestuid=sdcRequest.get("requestId");
		String landingPage=sdcRequest.get("landingPage");
		String campaignId=sdcRequest.get("cid");
		String networkId="";
		boolean stsDeclined=false;
		boolean confirmedSubscription=false;
		boolean stopUser=false;
		boolean sendWelcome=false;
		String defClubDomain = "5510024809921CDS";
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		UmeClubDetails userClubDetails = null;
        
		if(club!=null){
        	userClubDetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        }
        HttpSession session=request.getSession();
        MobileClubCampaign cmpg = null;
        if (campaignId != null && campaignId.trim().length() > 0) {
            cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
        }

        String campaignSrc = "";
        if (cmpg != null) {
            campaignSrc = cmpg.getSrc();
        }

		if(msisdn!=null && msisdn.trim().length()>0){
			if(msisdn.startsWith("+")) 
				msisdn=msisdn.substring(1);
	    }
		
		if (result != null && (result.trim().toUpperCase().equals("CONFIRM")                                
	                         || result.trim().toUpperCase().equals("ERROR")
	                         || result.trim().toUpperCase().equals("TERMINATE")
	                         || result.trim().toUpperCase().equals("DECLINE")
	                         || result.trim().toUpperCase().equals("DECLINED"))) {
			 
			networkId=getNetworkId(operatorId);

			if (result.equals("ERROR")) {
				processErrorCondition(msisdn,club);
				stsDeclined = true;
			}else if (result.equals("CONFIRM")) {
				stsDeclined = false;
				confirmedSubscription = true;
			} else if (result.equals("TERMINATE")) { // JDV edit to accommodate new TERMINATE code from STS
				stopUser = true;
				deactivateUser(msisdn, club, defClubDomain, umeuserdao, umemobileclubuserdao, userauthentication, request);
				return;
			} else {
				stsDeclined = true;
			}
			
			if (!stsDeclined) {
				
				SdcMobileClubUser clubUser = null;
				UmeUser user=null;
				
				if (confirmedSubscription) {
					
					if (cmpg != null && cmpg.getSrc().trim().toLowerCase().endsWith("cpa")) {
						int insertedRows = cpaloggerdao.insertIntoCpaLogging(msisdn, campaignId, club.getUnique(), 10, networkId, campaignSrc);
					}

					if (cmpg != null && cmpg.getSrc().trim().endsWith("RS")) {
						String enMsisdn = MiscCr.encrypt(msisdn);
						int insertedRows =  cpaloggerdao.insertIntoRevShareLogging(0,  cmpg.getPayoutCurrency(), msisdn, enMsisdn, campaignId, club.getUnique(), 0, networkId, campaignSrc, 0);
					}

					/*PassiveVisitor visitor = passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
					if (visitor != null && visitor.getStatus() == 0) {
						passivevisitordao.updatePassiveVisitorStatus(visitor, 1);
					}*/
					
					if(userClubDetails.getBillingType().equalsIgnoreCase("subscription")){
						subscriptionMap=createOrUpdateSubscription(msisdn,club,campaignId,networkId,landingPage);	
						user=(UmeUser)subscriptionMap.get("user");
						clubUser=(SdcMobileClubUser)subscriptionMap.get("clubUser");
						sendWelcome=(boolean)subscriptionMap.get("sendWelcome");
						addBillingPlan(user,clubUser,userClubDetails,club,networkId);
					}
					
					biLog(user,networkId,request,response);
					
					
					if(userClubDetails.getServiceType().equalsIgnoreCase("content") && user!=null && clubUser!=null){
						
						if(sendWelcome){
							sendWelcomeSms(user,clubUser,club,sdcRequest);
							sendPersonalLink(user,clubUser,club,sdcRequest);	                
						}

						//=========== If it is Provided by UME
						if(userClubDetails.getServedBy().equalsIgnoreCase("ume")){                
							response.sendRedirect("http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId());
						}

						//========  If it is provided by Third Party like MobiPlanet
						else if(userClubDetails.getServedBy().equalsIgnoreCase("thirdparty")){
							response.sendRedirect("http://" + dmn.getRedirectUrl() + "/?m="+Misc.encrypt(clubUser.getParsedMobile())+"&sub=success&credit="+clubUser.getCredits());
						}
						//====== If no setting is present=============
						else{ 
							response.sendRedirect("http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId());
						}
					}
					
					if(userClubDetails.getServiceType().equalsIgnoreCase("competition") && user!=null && clubUser!=null){
						response.sendRedirect("http://" + dmn.getDefaultUrl() + "/thankyoupage?msisdn="+user.getParsedMobile());
					}
				}
			}
		}else{
			response.sendRedirect("http://"+dmn.getDefaultUrl());
		}
	}
	
	public String getNetworkId(String operatorId){
		String networkId="";
		if (operatorId != null) {
			if (operatorId.trim().equals("1")) {
				networkId = "vodacom";
			}
			if (operatorId.trim().equals("2")) {
				networkId = "mtn";
			}
			if (operatorId.trim().equals("3")) {
				networkId = "cellc";
			}
			if (operatorId.trim().equals("5")) {
				networkId = "heita";
			}
		}
		return networkId;
		
	}
	
	public void processErrorCondition(String msisdn,MobileClub club){
		UmeUser user=null;
		if (!msisdn.equals("")) {
            String userid = umeuserdao.getUserUnique(msisdn, "msisdn", "");
            if (!userid.equals("")) {
                user = umeuserdao.getUser(msisdn);
            }
            if (user != null) {
                SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                if (clubUser != null) {
                    if (clubUser.getActive() == 0) {
                        clubUser.setActive(1);
                        umemobileclubuserdao.saveItem(clubUser);
                        user.getClubMap().put(club.getUnique(), clubUser);
                    }
                    
                } // END if clubUser!=null
            } //END if user!=null  
        }
		
	}
	
	public Map<String,Object> createOrUpdateSubscription(String msisdn,MobileClub club,String campaignId,String networkId,String landingPage){
		boolean sendWelcome=false;
		SdcMobileClubUser clubUser = null;
		UmeUser user=null;
		String subsresponse = subscriptioncreation.checkSubscription(msisdn, club, campaignId, 1, "", networkId, "", landingPage,"");
		String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");
		if (userUnique != null && !userUnique.equals("")) {
			user = umeuserdao.getUser(msisdn);
		}
		clubUser = user.getClubMap().get(club.getUnique());
		if (clubUser == null) {
			clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
		}
		if (user != null && clubUser != null) {
			user.getClubMap().put(club.getUnique(), clubUser);
		}

		if (subsresponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY")
				|| subsresponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY")) {
			sendWelcome = true;
		}

		Map<String,Object> subscriptionMap=new HashMap<String,Object>();
		subscriptionMap.put("user",user);
		subscriptionMap.put("clubUser", clubUser);
		subscriptionMap.put("sendWelcome",sendWelcome);
		return subscriptionMap;
		
	}
		
	public void addBillingPlan(UmeUser user,SdcMobileClubUser clubUser,UmeClubDetails userclubdetails,MobileClub club,String networkId){
		MobileClubBillingPlan billingplan = new MobileClubBillingPlan();
        billingplan.setTariffClass(club.getPrice());
        billingplan.setActiveForAdvancement(1);
        billingplan.setActiveForBilling(1);
        billingplan.setAdhocsRemaining(0.0);
        billingplan.setBillingEnd(clubUser.getBillingEnd());
        billingplan.setClubUnique(club.getUnique());
        billingplan.setContractType("");
        billingplan.setLastPaid(new Date());
        billingplan.setLastSuccess(new Date(0));
        billingplan.setLastPush(new Date(0));
        billingplan.setNetworkCode(networkId);
        billingplan.setNextPush(clubUser.getBillingEnd());
        billingplan.setParsedMobile(user.getParsedMobile());
        billingplan.setPartialsPaid(0.0);
        billingplan.setSubscribed(clubUser.getSubscribed());
        billingplan.setPartialsRequired(Double.parseDouble(String.valueOf(userclubdetails.getFrequency() + "")));
        billingplan.setPushCount(0.0);
        billingplan.setServiceDate(new Date());
        billingplan.setSubUnique(clubUser.getUserUnique());
        billingplan.setExternalId(""); //This is for Italy SubscriptionId so just setting the values. 
        //billingplan.setServiceDateBillsRemaining(Double.parseDouble(String.valueOf(userclubdetails.getFrequency())));
        billingplan.setServiceDateBillsRemaining(0.0);
        billingplandao.insertBillingPlan(billingplan);
		
	}
	
	public void biLog(UmeUser user,String networkId,HttpServletRequest request,HttpServletResponse response){
		SdcRequest sdcRequest=new SdcRequest(request);
		UmeDomain dmn=sdcRequest.getDomain();
		String campaignId=sdcRequest.get("cid");
		Handset handset=handsetdao.getHandset(request);
		String landingPage=sdcRequest.get("landingPage");
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		mobileclubcampaigndao.log("confirm", landingPage, user.getUnique(), user.getParsedMobile(),handset, dmn.getUnique(), campaignId, club.getUnique(), "SUBSCRIBED", 0, request, response, networkId, "smsoptin", "", "");
       }
	
	public void sendWelcomeSms(UmeUser user,SdcMobileClubUser clubUser,MobileClub club,SdcRequest sdcRequest){
		
		List<UmeClubMessages> welcomeMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Welcome");
        UmeDomain dmn=sdcRequest.getDomain();       
        for(int i=0;i<welcomeMessages.size();i++){
            
        	KenyaSms freeKenyaSms=new KenyaSms();
        	APIRequestResponse apiRequestResponse=freeKenyaSms.processRequest("Sending Welcome SMS", welcomeMessages.get(i).getaMessage(), club.getOtpServiceName(), clubUser.getParsedMobile(),"http://"+dmn.getDefaultUrl()+"/status.jsp");
                //APIRequestResponse apiRequestResponse=freeKenyaSms.processRequest("Sending Welcome SMS", club.getWelcomeSms(), club.getOtpServiceName(), clubUser.getParsedMobile(),"http://"+dmn.getDefaultUrl()+"/status.jsp");
        	System.out.println("Welcome Message Response Success or Not: "+apiRequestResponse.isRequestSuccess());
        	System.out.println("Welcome Message Response Error Code : "+apiRequestResponse.getErrorCode());
        	System.out.println("Welcome Message Response Error Description: "+apiRequestResponse.getErrorText());
        	smsMsgLog(user,clubUser,club,sdcRequest,welcomeMessages.get(i).getaMessage(),"WELCOME");
        }
		
	}
	
	public void sendPersonalLink(UmeUser user,SdcMobileClubUser clubUser,MobileClub club,SdcRequest sdcRequest){
		try {
			UmeDomain dmn=sdcRequest.getDomain();
			String personalLink="Your Personal Link to the service is " + "http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId();
			KenyaSms freeKenyaSms=new KenyaSms();
			APIRequestResponse apiRequestResponse=freeKenyaSms.processRequest("Sending Personal Link", personalLink, club.getOtpServiceName(), clubUser.getParsedMobile(),"http://"+dmn.getDefaultUrl()+"/status.jsp");
			System.out.println("Personal Link Response Success or Not: "+apiRequestResponse.isRequestSuccess());
        	System.out.println("Personal Link Response Error Code : "+apiRequestResponse.getErrorCode());
        	System.out.println("Personal Link Response Error Description: "+apiRequestResponse.getErrorText());
        	smsMsgLog(user,clubUser,club,sdcRequest,personalLink,"PEROSNAL LINK");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public boolean deactivateUser(String msisdn, MobileClub club, String defClubDomain, UmeUserDao umeuserdao, UmeMobileClubUserDao umemobileclubuserdao, UserAuthentication userauthentication, HttpServletRequest request) {
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String unSubscribed = sdf2.format(new Date());
		boolean terminated = false;
		UmeUser user = null;
		SdcMobileClubUser clubUser = null;
		String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");

		if (!userUnique.equals("")) {
			user = umeuserdao.getUser(msisdn);
		}

		if (user != null) {
			clubUser = user.getClubMap().get(club.getUnique());
		}

		if (clubUser != null && clubUser.getActive()==1) {
			clubUser.setActive(0);
			clubUser.setUnsubscribed(SdcMiscDate.parseSqlDateString(unSubscribed));
			umemobileclubuserdao.saveItem(clubUser);
			try {
				if (clubUser.getCampaign() != null) {
					MobileClubCampaign campaign = UmeTempCmsCache.campaignMap.get(clubUser.getCampaign());
					if (campaign != null && campaign.getSrc().trim().endsWith("RS")) {

					}
				}
				userauthentication.invalidateUser(request);
			} catch (Exception e) {
				System.out.println("Exception occurred while performing RevShare Termination in ZACONFIRM line no. 810" + e);
			}
		}

		ZaDoi zadoi = new ZaDoi();
		String token = zadoi.authenticate();
		String serviceName = club.getOtpServiceName();
		terminated = zadoi.delete_DoubleOptIn_Record(token, serviceName, msisdn);
		return terminated;
    }
	
	
	public void smsMsgLog(UmeUser user,SdcMobileClubUser clubUser,MobileClub club,SdcRequest sdcRequest,String message,String messageType){
		ZaSmsSubmit zaSmsSubmit = new ZaSmsSubmit(sdcRequest);
		zaSmsSubmit.setUnique(Misc.generateUniqueId(10)+"-"+clubUser.getClubUnique());
		zaSmsSubmit.setLogUnique(Misc.generateUniqueId(10)+"-"+clubUser.getClubUnique());
		zaSmsSubmit.setSmsAccount("sts");
		zaSmsSubmit.setUmeUser(user);
		zaSmsSubmit.setToNumber(clubUser.getParsedMobile());
		zaSmsSubmit.setFromNumber(club.getSmsExt());
		zaSmsSubmit.setNetworkCode(clubUser.getNetworkCode());
		zaSmsSubmit.setClubUnique(clubUser.getClubUnique());
		zaSmsSubmit.setStatus("SENT");
		zaSmsSubmit.setRefMessageUnique(messageType);
		zaSmsSubmit.setMsgBody(message);
        umesmsdao.log(zaSmsSubmit);
	}
		

}
