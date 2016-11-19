package ume.pareva.userservice;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.MobileNetworksDao;
import ume.pareva.dao.PassiveVisitorDao;
import ume.pareva.dao.QuizValidationDao;
import ume.pareva.dao.RegulatoryLogDao;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeQuizDao;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.PassiveVisitor;
import ume.pareva.pojo.QuizValidation;
import ume.pareva.pojo.RegulatoryLog;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.sdk.MiscDate;
import ume.pareva.uk.CompetitionMessage;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.dao.QuizSmsDao;
import ume.pareva.pojo.UmeClubMessages;

/**
 *
 * @author madan
 */
@Component("subscriptioncreation")
public class SubscriptionCreation {

    private final Logger logger = LogManager.getLogger(SubscriptionCreation.class.getName());
    private static final String BLOCKED_USER = "USER IS BLOCKED SO NOT FORWARDING FOR SUBSCRIPTION";
    private static final String SUBSCRIPTION_EXIST = "SUBSCRIPTION RECORD ALREADY EXISTS";
    private static final String NEWSUBSCRIPTION = "SUBSCRIPTION RECORD CREATED SUCCESSFULLY";
    private static final String SUBSCRIPTION_UPDATED = "OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY";
    private static final String SUBSCRIPTION_REQUEST="SUBSCRIPTION REQUEST RECEIVED SUCCESSFULLY";
    private static final String SUBSCRIPTION_REQUEST_REJECTED="REJECTED DUPLICATE RECEIVED";
    private static final String SUBSCRIPTION_REQUEST_REJECTED_EXISTS="REJECTED SUBSCRIPTION RECORD ALREADY EXISTS";
    private static final String SUBSCRIPTION_ERROR = "ERROR - CONTACT MADAN/ALEX - madan@umelimited.com/alex.sanchez@umelimited.com";

    @Autowired
    UmeUserDao umeuserdao;

    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;

    @Autowired
    RegionalDate regionaldate;

    @Autowired
    MobileClubDao mobileclubdao;

    @Autowired
    RegulatoryLogDao regulatorylog;

    @Autowired
    MobileClubBillingPlanDao billingplandao;
    
    @Autowired
    MobileClubCampaignDao campaigndao;
    
    @Autowired
    MobileNetworksDao mobilenetwork;
    
    @Autowired
    UmeSmsDao umesmsdao;
    
    @Autowired
    UmeQuizDao umequizdao;
    
    @Autowired
    QuizValidationDao quizvalidationdao;
    
    @Autowired
    CompetitionMessage compmessage;
    
    @Autowired
    PassiveVisitorDao passivevisitordao;
    
    @Autowired
    UmeClubDetailsDao umeclubdetailsdao;
    
    @Autowired
    QuizSmsDao quizsmsdao;

    //This is to make all users record containing the same domain otherwise it will create new user record for multiple subscribed domains
    private String commonDomain = "5510024809921CDS";

    public String checkSubscription(String msisdn, MobileClub club, String campaignid, int duration, String networkid, String landingpage) {
        System.out.println("restservicequery " + " CheckSubscription called inside subsriptionCreation ");
        return checkSubscription(msisdn, club, campaignid, duration, "", networkid, "", landingpage, "");
    }

    public String checkSubscription(String msisdn, MobileClub club, String campaignid, int duration, String networkid, String externalId, String landingpage) {
        return checkSubscription(msisdn, club, campaignid, duration, "", networkid, externalId, landingpage, "");
    }

    public String checkSubscription(String msisdn, MobileClub club, String campaignid, int duration, String subscription, String networkid, String externalId, String landingpage, String pubId) {
        
        
        UmeUser user = null;
        SdcMobileClubUser clubUser = null;
        String subscriptionresponse = "ERROR CONTACT ";
        UmeClubDetails userclubdetails = null;
        MobileClubCampaign cmpg = null;
        String campaignsrc;
        
        
        
       if(club!=null){
         userclubdetails= UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
       }
            if (pubId!=null && pubId.equalsIgnoreCase("RESTAPI") && !subscription.equalsIgnoreCase("subscribe")) {
           
              if (club.getRegion().equalsIgnoreCase("UK")) {
                  String coregpublisherid="";
            if(campaignid!=null && !campaignid.equalsIgnoreCase("")) {
            
            
            if(campaignid.contains("-")){
                String[]campaignarray = getCampaignId(campaignid, "-");
                campaignid=campaignarray[0];
                coregpublisherid = campaignarray[1];
            }else{
                coregpublisherid="";
            }
            cmpg = UmeTempCmsCache.campaignMap.get(campaignid);
                
            
            }
            
            if(cmpg!=null) campaignsrc=cmpg.getSrc();
            campaigndao.log("restapi","", msisdn,msisdn,null,club.getWapDomain(), campaignid, club.getUnique(), "MANUAL", 0,null,null,"unknown_api","","","",coregpublisherid);
            
            
            
                String freeCostId="1";
                String serviceId="1";
                //String deliveryReceipt="13";
                String deliveryReceipt="11";
                String typeId="2";
                //String teasermsg=userclubdetails.getTeaser();
                
                  List<UmeClubMessages> teaserMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Teaser");
        
                //String teasermsg=userClubDetails.getTeaser();
                String teasermsg=teaserMessages.get(0).getaMessage();
                if(teasermsg!=null) {
                    System.out.println("uknewlogic  restservicequery Teaser Message: "+msisdn+": "+ teasermsg);
                }
                if(teasermsg==null || "".equals(teasermsg))
                teasermsg=userclubdetails.getTeaser();
                
         
                String shortCode=club.getSmsNumber();
                String transactionId=String.valueOf(Misc.generateUniqueIntegerId())+"-"+club.getUnique();
                   //======== FINDING IF USER EXISTS OR NOT ==========================
                String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", commonDomain);
                if (!userUnique.equals("")) user = umeuserdao.getUser(msisdn);
                if (user!=null) {
		
                clubUser = user.getClubMap().get(club.getUnique());
                if(clubUser==null){
	        clubUser=umemobileclubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
                }
                }
//                if(clubUser==null){
//                    campaigndao.log("restapi","", msisdn,msisdn,null,club.getWapDomain(), campaignid, club.getUnique(), "MANUAL", 0,null,null,"unknown_api","","","",campaignsrc);
//                }
                if (user==null || clubUser==null || (clubUser!=null && clubUser.getActive()!=1)) {
                String[] toAddress={msisdn};
                QuizValidation validation=new QuizValidation();
                validation.setaParsedMobile(msisdn);
                validation.setaCampaign(campaignid);
                validation.setaClubUnique(club.getUnique());
                int inserted=quizvalidationdao.insertValidationRecord(validation);
                compmessage.requestSendSmsTimer(toAddress,freeCostId,serviceId,deliveryReceipt,teasermsg,shortCode,transactionId,typeId,club,10*10,false,campaignid);
                subscriptionresponse=SUBSCRIPTION_REQUEST;
                
                               
                    //======== Passive Visitor Setup ==========================
                     boolean exist=passivevisitordao.exists(msisdn, club.getUnique());
                    if(!exist){
                        PassiveVisitor visitor=new PassiveVisitor();
                        visitor.setUnique(SdcMisc.generateUniqueId());
                        visitor.setClubUnique(club.getUnique());
                        visitor.setFollowUpFlag(0);
                        visitor.setParsedMobile(msisdn);
                        visitor.setStatus(0);
                        visitor.setCreated(new Date());
                        visitor.setCampaign(campaignid);
                        visitor.setLandignPage("");
                        visitor.setPubId(pubId);
                        passivevisitordao.insertPassiveVisitor(visitor);
                        
                    }
                    else if(exist){
                        PassiveVisitor visitor=passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
                        visitor.setCampaign(campaignid);
                        visitor.setFollowUpFlag(0);
                        visitor.setStatus(0);
                        visitor.setCreated(new Date());
                        visitor.setLandignPage("");
                        visitor.setPubId(pubId);
                        passivevisitordao.updatePassiveVisitor(visitor);
                    }
                    
                //  ======== Passive Visitor SETUP End =========================
                
                    //============ REJECTING IF Teaser is sent in 24 hour ================
                    if(quizsmsdao.sentIn24Hour(msisdn,club.getUnique()))
                    subscriptionresponse=SUBSCRIPTION_REQUEST_REJECTED;
                    
                    //============ END REJECTING IF Teaser is sent in 24 hour ===========
                    
             
                }
                else{
                subscriptionresponse=SUBSCRIPTION_REQUEST_REJECTED_EXISTS;//SUBSCRIPTION_EXIST;
                System.out.println("quiz2winlanding page "+clubUser.getParsedMobile()+"  "+clubUser.getActive()+" inside else condition ");
	     
	    }
     
   
        
        //==========END IF USER EXISTS OR NOT ============================
                   
            
                    
                 //subscriptionresponse="REQUEST RECEIVED";
                } else if (club.getRegion().equalsIgnoreCase(("IE"))) {
                    //TODO send Welcome and Billable Message based on clubDetails
                } else if (club.getRegion().equalsIgnoreCase(("IT"))) {
                    //TODO send Welcome and Billable Message based on clubDetails
                }
            }
        else{

        System.out.println("restservicequery " + " checkSubscription REAL Method");
        Date bstart = new Date();
        Date subscriptiontime = regionaldate.getRegionalDate(club.getRegion(), new Date());
        Date billingend = DateUtils.addDays(bstart, duration);
        String binetwork="unknown";

        System.out.println("restservicequery " + " Values of Date bstart " + bstart + " subscriptiontime " + subscriptiontime + " billingend " + billingend);

        if (msisdn != null && !"".equalsIgnoreCase(msisdn)) {
            String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");

            if (userUnique != null && !userUnique.equals("")) {
                user = umeuserdao.getUser(msisdn);
            }
            if (user != null) {
                if (user.getAccountType() == 99) {
                    // User account is blocked/barred
                    //TODO doRedirect(response, "http://" + dmn.getDefaultUrl() + "/blocked.jsp" );
                    //System.out.println(msisdn+ " USER IS BLOCKED SO NOT FORWARDING FOR SUBSCRIPTION !!! ");

                    return msisdn + BLOCKED_USER;
                }
                System.out.println("restservicequery " + " USER !=null RECORD CREATION " + msisdn + " club " + club.getUnique() + " " + club.getName() + " USER " + user.getUnique());
                user.updateMap("active", "1");
                umeuserdao.commitUpdateMap(user);
                user.clearUpdateMap();

                clubUser = user.getClubMap().get(club.getUnique());
                if (clubUser == null) {
                    clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                }
                if (clubUser != null) {
                    if (clubUser.getActive() != 1) {
                        clubUser.setParsedMobile(user.getParsedMobile());
                        clubUser.setActive(1);
                        clubUser.setCredits(club.getCreditAmount());
                        clubUser.setAccountType(0);
                        clubUser.setBillingStart(bstart);
                        clubUser.setBillingEnd(billingend);
                        clubUser.setCampaign(campaignid);
                        clubUser.setNetworkCode(networkid);
                        clubUser.setaExternalId(externalId);
                        clubUser.setSubscribed(subscriptiontime);
                        clubUser.setLandingpage(landingpage);
                        clubUser.setParam1("0");
                        clubUser.setParam2("0");
                        umemobileclubuserdao.saveItem(clubUser);
                        subscriptionresponse = SUBSCRIPTION_UPDATED;
                    } else {
                        //TODO SEND MESSAGE ClubUser is Already Active !
                        //NOT PROCESSING WITH SUBSCRIPTION 
                        System.out.println("restservicequery " + " USER !=null SUBSCRIPTION EXIST " + msisdn + " club " + club.getUnique() + " " + club.getName() + " USER " + user.getUnique());
                        subscriptionresponse = SUBSCRIPTION_EXIST;

                    }

                } //END clubUser!=null
                else { //CLUBUSER REcord doesn't exist
                    clubUser = createClubSubscription(club, user, campaignid, duration, subscription, networkid, externalId, landingpage);
                    subscriptionresponse = NEWSUBSCRIPTION;

                }

            } //End IF USER!=NULL
            else { //USER RECORD DOESN'T EXIST AND WE PRESUME CLUB-SUBSCRIPTION RECORD DOESN'T EXIST TOO 
                System.out.println("restservicequery " + " NEW USER RECORD CREATION " + msisdn + " club " + club.getUnique() + " " + club.getName());
                user = createUser(club, msisdn);
                clubUser = createClubSubscription(club, user, campaignid, duration, subscription, networkid, externalId, landingpage);
                subscriptionresponse = NEWSUBSCRIPTION;

            }

        }
        System.out.println("restservicequery " + " The VALUE of subscriptionresponse is " + subscriptionresponse);
        if (subscriptionresponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY")
                || subscriptionresponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY")) {
            RegulatoryLog item = new RegulatoryLog();
            item.setUnique(Misc.generateUniqueId(10));
            item.setUserUnique(clubUser.getUserUnique());
            item.setCreated(new Date());
            item.setCampaignId(clubUser.getCampaign());
            item.setClubUnique(clubUser.getClubUnique());
            item.setDomain(club.getWapDomain());
            item.setEvent("SUBSCRIBED");
            item.setIp("");
            item.setEventlocaldate(clubUser.getSubscribed());
            item.setNetworkCode(clubUser.getNetworkCode());
            item.setOptInType("");
            item.setParam1("");
            item.setParam2("");
            item.setParsedMobile(clubUser.getParsedMobile());
            item.setPubId(pubId);
            regulatorylog.insert(item);


        }
    }
        return subscriptionresponse;
    }

    //========== This is particularly for Italy but requires to Change ========================================================
    public String checkSubscription(String msisdn, MobileClub club, String campaignid, int duration, String subscription, String networkid, String externalId, String landingpage, String pubId, String param1, String param2) {
        UmeUser user = null;
        SdcMobileClubUser clubUser = null;
        String subscriptionresponse = "ERROR CONTACT ";
        UmeClubDetails userclubdetails = null;
        
        if(club!=null){
         userclubdetails= UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
       }

        System.out.println("restservicequery " + " checkSubscription REAL Method");
        Date bstart = new Date();
        Date subscriptiontime = regionaldate.getRegionalDate(club.getRegion(), new Date());
        Date billingend = DateUtils.addDays(bstart, duration);

        System.out.println("restservicequery " + " Values of Date bstart " + bstart + " subscriptiontime " + subscriptiontime + " billingend " + billingend);

        if (msisdn != null && !"".equalsIgnoreCase(msisdn)) {
            String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");

            if (userUnique != null && !userUnique.equals("")) {
                user = umeuserdao.getUser(msisdn);
            }
            if (user != null) {
                if (user.getAccountType() == 99) {
                    // User account is blocked/barred
                    //TODO doRedirect(response, "http://" + dmn.getDefaultUrl() + "/blocked.jsp" );
                    //System.out.println(msisdn+ " USER IS BLOCKED SO NOT FORWARDING FOR SUBSCRIPTION !!! ");

                    return msisdn + BLOCKED_USER;
                }
                System.out.println("restservicequery " + " USER !=null RECORD CREATION " + msisdn + " club " + club.getUnique() + " " + club.getName() + " USER " + user.getUnique());
                user.updateMap("active", "1");
                umeuserdao.commitUpdateMap(user);
                user.clearUpdateMap();

                clubUser = user.getClubMap().get(club.getUnique());
                if (clubUser == null) {
                    clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                }
                if (clubUser != null) {
                    if (clubUser.getActive() != 1) {
                        clubUser.setParsedMobile(user.getParsedMobile());
                        clubUser.setActive(1);
                        clubUser.setCredits(club.getCreditAmount());
                        clubUser.setAccountType(0);
                        clubUser.setBillingStart(bstart);
                        clubUser.setBillingEnd(billingend);
                        clubUser.setCampaign(campaignid);
                        clubUser.setNetworkCode(networkid);
                        clubUser.setaExternalId(externalId);
                        clubUser.setSubscribed(subscriptiontime);
                        clubUser.setLandingpage(landingpage);
                        clubUser.setParam1(param1);
                        clubUser.setParam2(param2);
                        umemobileclubuserdao.saveItem(clubUser);
                        subscriptionresponse = SUBSCRIPTION_UPDATED;
                    } else {
                        //TODO SEND MESSAGE ClubUser is Already Active !
                        //NOT PROCESSING WITH SUBSCRIPTION 
                        System.out.println("restservicequery " + " USER !=null SUBSCRIPTION EXIST " + msisdn + " club " + club.getUnique() + " " + club.getName() + " USER " + user.getUnique());
                        subscriptionresponse = SUBSCRIPTION_EXIST;

                    }

                } //END clubUser!=null
                else { //CLUBUSER REcord doesn't exist
                    clubUser = createClubSubscription(club, user, campaignid, duration, subscription, networkid, externalId, landingpage,param1,param2);
                    subscriptionresponse = NEWSUBSCRIPTION;

                }

            } //End IF USER!=NULL
            else { //USER RECORD DOESN'T EXIST AND WE PRESUME CLUB-SUBSCRIPTION RECORD DOESN'T EXIST TOO 
                System.out.println("restservicequery " + " NEW USER RECORD CREATION " + msisdn + " club " + club.getUnique() + " " + club.getName());
                user = createUser(club, msisdn);
                clubUser = createClubSubscription(club, user, campaignid, duration, subscription, networkid, externalId, landingpage,param1,param2);
                subscriptionresponse = NEWSUBSCRIPTION;

            }

        }
        System.out.println("restservicequery " + " The VALUE of subscriptionresponse is " + subscriptionresponse);
        if (subscriptionresponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY")
                || subscriptionresponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY")) {
            RegulatoryLog item = new RegulatoryLog();
            item.setUnique(Misc.generateUniqueId(10));
            item.setUserUnique(clubUser.getUserUnique());
            item.setCreated(new Date());
            item.setCampaignId(clubUser.getCampaign());
            item.setClubUnique(clubUser.getClubUnique());
            item.setDomain(club.getWapDomain());
            item.setEvent("SUBSCRIBED");
            item.setEventlocaldate(clubUser.getSubscribed());
            item.setIp("");
            item.setNetworkCode(clubUser.getNetworkCode());
            item.setOptInType("");
            item.setParam1(param1);
            item.setParam2(param2);
            item.setParsedMobile(clubUser.getParsedMobile());
            item.setPubId(pubId);
            regulatorylog.insert(item);
   
        }
        return subscriptionresponse;
    }

    //========= END ITaly requirement for Subscription ================================================= 
    private UmeUser createUser(MobileClub club, String msisdn) {
        boolean createUser = false;

        UmeUser user = new UmeUser(); // <- new user...
        user.setMobile(msisdn);
        user.setParsedMobile(msisdn);
        user.setWapId(SdcMisc.generateLogin(10));
        user.setDomain(commonDomain); //keeping partner domain here or default Domain name
        user.setActive(1);
        user.setCredits(club.getCreditAmount());
        System.out.println("restservicequery " + "==== USER CREATIN === " + user.getMobile() + " " + user.getWapId() + " " + user.getDomain());
        String stat = umeuserdao.addNewUser(user);
        System.out.println("restservicequery " + " value of stat is ==== " + stat);
        return user;
    }

    private SdcMobileClubUser createClubSubscription(MobileClub club, UmeUser user, String campaignid, int duration, String subscription, String networkid, String externalId, String landingpage) {

//        Date bstart = new Date();
//        Date subscriptiontime = regionaldate.getRegionalDate(club.getRegion(), new Date());
//        Date billingend = DateUtils.addDays(bstart, duration);
//
//        SdcMobileClubUser clubUser = new SdcMobileClubUser();
//        clubUser.setUnique(SdcMisc.generateUniqueId("BUS"));
//        clubUser.setUserUnique(user.getUnique());
//        clubUser.setClubUnique(club.getUnique());
//        clubUser.setParsedMobile(user.getParsedMobile());
//        clubUser.setActive(1);
//        clubUser.setCredits(club.getCreditAmount());
//        clubUser.setAccountType(0);
//        clubUser.setBillingStart(bstart);
//        clubUser.setBillingEnd(billingend);
//        clubUser.setBillingRenew(bstart);
//        clubUser.setPushCount(0);
//        clubUser.setCreated(new Date());
//        clubUser.setCampaign(campaignid);
//        clubUser.setNetworkCode(networkid);
//        clubUser.setUnsubscribed(new Date(0));
//        clubUser.setNextPush(MiscDate.parseSqlDate("2011-01-01"));
//        clubUser.setaExternalId(externalId);
//        clubUser.setSubscribed(subscriptiontime);
//        clubUser.setLandingpage(landingpage);
//        clubUser.setParam1("0");
//        clubUser.setParam2("0");
//        umemobileclubuserdao.saveItem(clubUser);
        return createClubSubscription(club,user, campaignid,duration,subscription, networkid, externalId, landingpage, "", "");

        //return clubUser;
    }

    //=========== This is for IT Club User Subscription =========================================
    private SdcMobileClubUser createClubSubscription(MobileClub club, UmeUser user, String campaignid, int duration, String subscription, String networkid, String externalId, String landingpage, String param1, String param2) {

        Date bstart = new Date();
        Date subscriptiontime = regionaldate.getRegionalDate(club.getRegion(), new Date());
        Date billingend = DateUtils.addDays(bstart, duration);

        SdcMobileClubUser clubUser = new SdcMobileClubUser();
        clubUser.setUnique(SdcMisc.generateUniqueId("BUS"));
        clubUser.setUserUnique(user.getUnique());
        clubUser.setClubUnique(club.getUnique());
        clubUser.setParsedMobile(user.getParsedMobile());
        clubUser.setActive(1);
        clubUser.setCredits(club.getCreditAmount());
        clubUser.setAccountType(0);
        clubUser.setBillingStart(bstart);
        clubUser.setBillingEnd(billingend);
        clubUser.setBillingRenew(bstart);
        clubUser.setPushCount(0);
        clubUser.setCreated(new Date());
        clubUser.setCampaign(campaignid);
        clubUser.setNetworkCode(networkid);
        clubUser.setUnsubscribed(new Date(0));
        clubUser.setNextPush(MiscDate.parseSqlDate("2011-01-01"));
        clubUser.setaExternalId(externalId);
        clubUser.setSubscribed(subscriptiontime);
        clubUser.setLandingpage(landingpage);
        clubUser.setParam1(param1);
        clubUser.setParam2(param2);
        umemobileclubuserdao.saveItem(clubUser);

        return clubUser;
    }

    //=========== END IT Club User Subscription ================================================
    public Map<String, String> checkUser(String encryptedmsisdn, String clubUnique) {
        Map<String, String> userstatus = new HashMap<>(0);

        MobileClub club = UmeTempCmsCache.mobileClubMap.get(clubUnique);
        String deMsisdn = MiscCr.decrypt(encryptedmsisdn);
        UmeUser umeUser = umeuserdao.getUser(deMsisdn);
        SdcMobileClubUser clubUser =null;
        try{
        clubUser=umeUser.getClubMap().get(clubUnique);
        }catch(Exception e){}

        if (clubUser == null) {
            clubUser = umemobileclubuserdao.getClubUserByMsisdn(deMsisdn, clubUnique);
        }
        if(clubUser==null){
            clubUser = umemobileclubuserdao.getClubUserByMsisdn(encryptedmsisdn, clubUnique);
        }
        if (clubUser != null) {
            DateTime nowTime = new DateTime();
            DateTime billingRenew = new DateTime(clubUser.getBillingRenew().getTime());
            int days = Days.daysBetween(billingRenew, nowTime).getDays();
            //if (mobileclubdao.isActive(umeUser, club)) {
            if(clubUser.getActive()==1){
                userstatus.put(encryptedmsisdn, "active");
            } else {
                userstatus.put(encryptedmsisdn, "inactive");
            }

        } else {
            userstatus.put(encryptedmsisdn, "doesn't Exist");
        }
        return userstatus;
    }

    public Map<String, String> creditDetail(String encryptedmsisdn, String clubUnique, int credits) {
        String availableCredit = "0";
        if (credits == -1 || credits == 0) {
            credits = 1;
        }

        Map<String, String> usercredit = new HashMap<>(0);
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(clubUnique);
        String deMsisdn = MiscCr.decrypt(encryptedmsisdn);
        UmeUser umeUser = umeuserdao.getUser(deMsisdn);
        SdcMobileClubUser clubUser = umeUser.getClubMap().get(club.getUnique());

        if (clubUser == null) {
            clubUser = umemobileclubuserdao.getClubUserByMsisdn(deMsisdn, clubUnique);
        }
         if(clubUser==null){
            clubUser = umemobileclubuserdao.getClubUserByMsisdn(encryptedmsisdn, clubUnique);
        }
        if (clubUser != null) {
            umemobileclubuserdao.subtract(clubUser, credits);
            availableCredit = Integer.toString(clubUser.getCredits());
            usercredit.put(encryptedmsisdn, availableCredit);
        } else {
            usercredit.put(encryptedmsisdn, "doesn't exist");
        }
        return usercredit;
    }
    
      //3177399541KDS-114
    public String[] getCampaignId(String longString, String separateby) {
        System.out.println("restapi  inside getCampaignId: " + longString);
        String[] array = longString.split(separateby);
        System.out.println("restapi  inside getCampaignId:: " + array[0] + " 1: " + array[1]);
        return array;
    }
    

}
