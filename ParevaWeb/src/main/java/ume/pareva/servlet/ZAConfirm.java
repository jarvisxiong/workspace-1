package ume.pareva.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat; 
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.zadoi.service.ZaDoi;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.ServletConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.DoiResult;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.PassiveVisitorDao;
import ume.pareva.dao.SdcMiscDate;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.PassiveVisitor;
import ume.pareva.pojo.SdcSmsGateway;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.smsapi.ZaSmsSubmit;
import ume.pareva.pojo.DoiResponse;
import ume.pareva.dao.DoiResponseLogDao;
import ume.pareva.dao.QueryHelper;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.smsservices.SmsService;
import ume.pareva.userservice.CheckStop;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.userservice.UserAuthentication;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.pojo.UmeClubMessages;

/**
 * Servlet implementation class ZAConfirm
 */
//@WebServlet("/ZAConfirm")
public class ZAConfirm extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Autowired
    UmeUserDao umeuserdao;

    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;

    @Autowired
    MobileClubBillingPlanDao billingplandao;

    @Autowired
    MobileClubCampaignDao campaigndao;

    @Autowired
    PassiveVisitorDao passivevisitordao;

    @Autowired
    HandsetDao handsetdao;

    @Autowired
    DoiResponseLogDao doiresponselogdao;

    @Autowired
    QueryHelper queryhelper;

    @Autowired
    CpaLoggerDao cpaloggerdao;

    @Autowired
    DoiResult doiresult;

    @Autowired
    SmsService smsservice;

    @Autowired
    UserAuthentication userauthentication;

    //CheckStop checkstop=(CheckStop) request.getAttribute("checkstop");
    @Autowired
    CheckStop checkstop;
    
    @Autowired
    UmeClubDetailsDao umeclubdetailsdao;
    
    @Autowired
    MobileClubDao mobileclubdao;
    
    @Autowired
    UmeTempCache umesdc;

    //SubscriptionCreation subscriptioncreation=(SubscriptionCreation) request.getAttribute("subscriptioncreation");
    @Autowired
    SubscriptionCreation subscriptioncreation;
    
    private final Logger logger = LogManager.getLogger(ZAConfirm.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ZAConfirm() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * This method is needed to support Autowired Spring beans
     */
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
        // TODO Auto-generated method stub
        confirmSubscription(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        confirmSubscription(request, response);
    }

    public void confirmSubscription(HttpServletRequest request, HttpServletResponse response) {

        ThreadContext.put("ROUTINGKEY", "ZA");
        ThreadContext.put("EXTRA", "");
        
        HttpSession session = request.getSession();
      

        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();

        String msisdn = "";
                try{
        msisdn=(String) request.getAttribute("msisdn");
                }catch(Exception e){msisdn="";}
        MobileClubCampaign cmpg = null;
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
        UmeClubDetails userclubdetails = null;
        String campaignUnique = aReq.get("cid");
        String pubId=aReq.get("pubid");

        if (club != null) {
            userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        }

        if (campaignUnique != null && campaignUnique.trim().length() > 0) {
            cmpg = UmeTempCmsCache.campaignMap.get(campaignUnique);
        }

        String campaignsrc = "";
        if (cmpg != null) {
            campaignsrc = cmpg.getSrc();
        }

        Handset userhandset = (Handset) session.getAttribute("handset");
        if (userhandset == null) {
            userhandset = handsetdao.getHandset(request);
        }

        String optintype = "wapoptin";
        //boolean exists = false;
        boolean confirmedSubscription = false;
        boolean sendWelcome = false;
        String status = "";
        String networkid = "";
        boolean stsDeclined = true;
        boolean stopUser = false;
        String submsisdn = aReq.get("submsisdn");
        //String requestreference = aReq.get("requestreference");
        //String country = aReq.get("country");
        //Calendar debug_time = new GregorianCalendar();
        //Date time_stamp = debug_time.getTime();
        String hash = "";
        String landingpage = aReq.get("l");
        String operatorid;// = "";
        String defClubDomain = "5510024809921CDS";

        //System.out.println("ZAdefaultdomain club in zaconfirm is "+defClubDomain);
        if (submsisdn.equalsIgnoreCase("") || submsisdn.trim().length() <= 0) {
            submsisdn = aReq.get("msisdn");
        }
        //==== START READING STS DOI CONFIRMATION REQUEST =======================

        if (aReq.get("result").length() > 1) {
            if (msisdn == null || msisdn.trim().equals("")) {
                msisdn = aReq.get("msisdn");

                if (msisdn.trim().equals("")) {
                    msisdn = aReq.get("submsisdn");
                }

            }
            if (aReq.get("optintype").equalsIgnoreCase("smsoptin")) {
                optintype = "smsoptin";
            }

            operatorid = aReq.get("operatorid");
            if (operatorid != null) {
                if (operatorid.trim().equals("1")) {
                    networkid = "vodacom";
                }
                if (operatorid.trim().equals("2")) {
                    networkid = "mtn";
                }
                if (operatorid.trim().equals("3")) {
                    networkid = "cellc";
                }
                if (operatorid.trim().equals("5")) {
                    networkid = "heita";
                }

            }
            
            
            DoiResponse doiResponse = new DoiResponse();
                doiResponse.setResponseId(Misc.generateUniqueId());
                doiResponse.setMsisdn(msisdn);
                doiResponse.setOptinType(optintype);
                doiResponse.setResponse(aReq.get("result"));
                doiResponse.setUserRespondedTime(new Date());
                doiResponse.setRequestUid(aReq.get("myuid"));
                doiResponse.setNetwork(networkid);
                ThreadContext.put("EXTRA", "DOI");
                logger.info("DOI Response msisdn {} optintype {} response {} requuestid {} network {} club {} ", doiResponse.getMsisdn(), doiResponse.getOptinType(), doiResponse.getResponse()
                , doiResponse.getRequestUid(), doiResponse.getNetwork(), club.getUnique());
                ThreadContext.put("EXTRA", "");
            try {
                   doiresponselogdao.saveDoiResponse(doiResponse);
            } catch (Exception e) {
                ThreadContext.put("EXTRA", "ERROR");
                logger.error("ERROR SAVING doiResponse FOR {}{} CLUB {} - {}", msisdn, optintype, club.getUnique(), e.getMessage());
                logger.error("ERROR SAVING doiResponse FOR {}{} CLUB {} ", msisdn, optintype, club.getUnique(), e);
                ThreadContext.put("EXTRA", "");
            }
            try {
                
                doiresult.saveDOIResult(msisdn, optintype, campaignUnique, club.getUnique(), aReq.get("timestamp"), aReq.get("result"), networkid);
                
            } catch (Exception e) {
                ThreadContext.put("EXTRA", "ERROR");
                logger.error("ERROR SAVING doiresult FOR {}{} CLUB {} - {}", msisdn, optintype, club.getUnique(), e.getMessage());
                logger.error("ERROR SAVING doiresult FOR {}{} CLUB {} ", msisdn, optintype, club.getUnique(), e);
                ThreadContext.put("EXTRA", "");
            }

            if (aReq.get("result").equals("ERROR")) {
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
                            String userwapidurl = "http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId();
                            request.setAttribute("wapidurl", userwapidurl);
                        } // END if clubUser!=null
                    } //END if user!=null  
                } //END if msisdn!="
                stsDeclined = true;
            }// END if result is ERROR 
            else if (aReq.get("result").equals("CONFIRM")) {
               
                stsDeclined = false;
                confirmedSubscription = true;
            
            } else {
                if (aReq.get("result").equals("TERMINATE")) { // JDV edit to accommodate new TERMINATE code from STS
                    stsDeclined = true;
                    stopUser = true;
                    //boolean terminateUser = deactivateUser(submsisdn, club, defClubDomain, umeuserdao, umemobileclubuserdao, userauthentication, request, checkstop);
                    //System.out.println("Zadoi deletion done for msisdn "+msisdn+" with response "+terminateUser);
                   
                } else {
                    
                        user = umeuserdao.getUser(msisdn);
                	List<UmeDomain> sisterDomains=getNotSubscribedClubDomains(club,mobileclubdao,user,umesdc);
                	//List<String> sisterClubUrlList=(List<String>)session.getAttribute("sisterClubUrlList");
                	if(sisterDomains!=null){
                            String declineUrl = "";
                            try {
                                UmeDomain sisterDomain = sisterDomains.get((int) java.lang.Math.floor(java.lang.Math.random() * sisterDomains.size()));
                                String redirectCampaignId = campaigndao.getCampaignUnique(sisterDomain.getUnique(), "Redirect");

                                declineUrl = "http://" + sisterDomain.getDefaultUrl() + "/?mid=" + MiscCr.encrypt(msisdn) + "&logtype=redirect&cid=" + redirectCampaignId;
                                request.setAttribute("declineurl", declineUrl);
                                //response.sendRedirect("http://"+sisterDomain.getDefaultUrl()+"/?mid="+MiscCr.encrypt(msisdn)+"&logtype=redirect&cid=" + redirectCampaignId);
                            } catch (Exception e) {
                               
                                ThreadContext.put("EXTRA", "ERROR");
                                logger.error("ERROR REDIRECTING TO SISTER CLUB {} - {}", declineUrl, e.getMessage());
                                logger.error("ERROR REDIRECTING TO SISTER CLUB {} ", declineUrl);
                                ThreadContext.put("EXTRA", "");
                            }
                		
                	}                 
                    
                                  
                    stsDeclined = true;
                }
            } //END ELSE 
        } //END aReq.getResult>1 
        else {
            //System.out.println("No STS Result is included  for this request ");
            logger.info("ZAHEADER NOTHING IS RECEIVED for msisdn {} club {} ! ",submsisdn, club.getUnique());
            
        }
        //========== END READING STS CONFIRMATION DOI REQUEST =========================

        if ((!submsisdn.equals("")) && (!stsDeclined)) {
            msisdn = submsisdn;
        }

        msisdn = SdcMisc.parseMobileNumber(msisdn);
        if (stsDeclined) {

            String servicelink = "<a href=\"/index.jsp\">" + "<font color=\"#990000\"> here</font> </a>";
            status = "You have chosen not to confirm your subscription charge.  Please click" + servicelink + " if you wish to enter the club.";
            
        } else if (club == null) {
            status = "ERROR: subscription service not found";
            logger.info(status);
            
        } else if ((club.getRegion().equalsIgnoreCase("ZA") && submsisdn.equals(""))) {
            status = "Sorry, we need your cell number! " + submsisdn + " Please go back and enter your cell number and we'll send you your FREE link instantly!";
            
        } else if ((club.getRegion().equalsIgnoreCase("ZA") && !SdcMisc.validateTel(submsisdn))) { // this is to get around a KE problem
            status = "Sorry, that's not a valid cell number! " + submsisdn + " Please go back and enter your cell number and we'll send you your FREE link instantly!";
           
        } else if (msisdn == null || (msisdn != null && msisdn.trim().length() <= 0)) {
            status = "Sorry, Empty cell number found";
            
        } else if (submsisdn != null && (submsisdn.trim().equals("")
                || submsisdn.length() <= 8 || (!submsisdn.startsWith("07")
                && !submsisdn.startsWith("08")
                && !submsisdn.startsWith("0027")
                && !submsisdn.startsWith("27")))) {
            status = "Sorry, that's not a valid cell number! (" + submsisdn + ") Please go back and enter your cell number and we'll send you your FREE link instantly!";
            logger.info("ZAConfirm THE VALUE OF STATUS IS "+status+" STS DECLINED IS "+stsDeclined);
            logger.info(status);
           
        } else if (!stsDeclined) {
            logger.info("INSIDE !STSDECLINED ");
            //SETTING msisdn in session. 
            if (!submsisdn.equals("")) {
                session.setAttribute("sdc_msisdn_param", submsisdn);
                session.setAttribute("ume_msisdn_param", submsisdn);
            } else {
                session.setAttribute("sdc_msisdn_param", msisdn);
                session.setAttribute("ume_msisdn_param", msisdn);
            }

            //System.out.println("defClubDomain: " + defClubDomain);
            if (club.getRegion().equals("ZA") && msisdn.startsWith("0")) {
                msisdn = "27" + msisdn.substring(1);
            }
            SdcMobileClubUser clubUser = null;
            MobileClubBillingPlan billingplan = null;

            if(userclubdetails.getBillingType().equalsIgnoreCase("subscription")){
            //User Subscription Starts Here ! 
                
            String subsresponse = subscriptioncreation.checkSubscription(msisdn, club, campaignUnique, 1, "", networkid, "", landingpage,pubId);
            if (subsresponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY")
                    || subsresponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY")) {
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
                    sendWelcome = true;
                }
                
                logger.info("ZACONFIRM CLUBUSER  " + clubUser.toString() + " SENDWELCOME IS " + sendWelcome);
                
                
                //========================= HANDLING GOOGLE AD tracks ==================================
                if(userclubdetails.getServedBy().equalsIgnoreCase("thirdparty")){
                    try{
                    String trackurl="http://www.googleadservices.com/pagead/conversion/882134728/?label=4HQjCOaS3mYQyJ3RpAM&guid=ON&script=0";                     
                    //URL obj = new URL(trackurl);
                    //HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                    request.setAttribute("googletrack","true");
                    session.setAttribute("googletrack","true");
                    
                    //System.out.println("googleadservice "+trackurl+"  ResponseCode "+connection.getResponseCode()+"  ResponseMessage "+connection.getResponseMessage());
                }
                catch(Exception e){
                    System.out.println("googleadservice Exception "+e);e.printStackTrace();}
                    
                } //END Melody Tracking
                //==============================END handling google ad tracks =========================== 
            }
            if (subsresponse.equals("SUBSCRIPTION RECORD ALREADY EXISTS")) {
                sendWelcome = false;
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
                    String redirecturl = "http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId();
                    request.setAttribute("personallink", redirecturl);
                    return;
                }
               
            }

                     //User Subscription Creation Ends here
            
            //System.out.println("=======================ADDING BILLING PLAN RECORD NOW ================== ");
            //====== Add a BillingPlan Record of this user =======
            //System.out.println("THE VALUE OF RESULT IS : "+"Before Plan "+clubUser.getParsedMobile()+" "+aReq.get("result")+" "+ clubUser.getActive()+" welcomesms "+sendWelcome);
             //Get the Active Billing Plan of the user. 
                billingplan = billingplandao.getActiveBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());
            
            if (billingplan==null && aReq.get("result").equals("CONFIRM") && sendWelcome) {
                campaigndao.log("confirmweb3", landingpage, user.getUnique(), user.getParsedMobile(), userhandset, domain, campaignUnique, club.getUnique(), "SUBSCRIBED", 0, request, response, networkid, optintype, "", "",pubId);
                billingplan = new MobileClubBillingPlan();
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
                billingplan.setNetworkCode(networkid);
                billingplan.setNextPush(clubUser.getBillingEnd());
                billingplan.setParsedMobile(user.getParsedMobile());
                billingplan.setPartialsPaid(0.0);
                billingplan.setSubscribed(clubUser.getSubscribed());
                billingplan.setPartialsRequired(Double.parseDouble(String.valueOf(userclubdetails.getFrequency() + "")));
                billingplan.setPushCount(0.0);
                billingplan.setServiceDate(new Date());
                billingplan.setSubUnique(clubUser.getUserUnique());
                billingplan.setExternalId(""); //This is for Italy SubscriptionId so just setting the values. 
                billingplan.setServiceDateBillsRemaining(Double.parseDouble(String.valueOf(userclubdetails.getFrequency())));
                billingplan.setPublisherId(pubId);
                billingplan.setaCampaign(campaignUnique);
                //billingplan.setServiceDateBillsRemaining(0.0);
                billingplandao.insertBillingPlan(billingplan);
            }
            }
            //System.out.println("====ADDED BILLING PLAN RECORD FOR ======= "+billingplan.getParsedMobile());

            /**
             * *****************************FollowUp
             * Update***********************************************
             */
            //System.out.println("CPA ZA Confirm: "+campaignsrc);
            String updatequery = "";
            //System.out.println("ConfirmZA Entering into passive update query now ");
            if (aReq.get("result").equals("CONFIRM")) {

                if (cmpg != null && cmpg.getSrc().trim().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("subscription")) {
                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                    String cpaparameter1 = (String) session.getAttribute("cpaparam1");
                    String cpaparameter2 = (String) session.getAttribute("cpaparam2");
                    String cpaparameter3 = (String) session.getAttribute("cpaparam3");
                    logger.info("cpaupdate for msisdn {} campaign {} {} club {} parameters [{}|{}|{}]", msisdn, campaignUnique, cmpg.getSrc(), club.getUnique(), cpaparameter1, cpaparameter2, cpaparameter3);
                    ThreadContext.put("EXTRA", "CPA");
                    logger.info("cpaupdate for msisdn {} campaign {} {} club {} parameters [{}|{}|{}]", msisdn, campaignUnique, cmpg.getSrc(), club.getUnique(), cpaparameter1, cpaparameter2, cpaparameter3);
                    ThreadContext.put("EXTRA", "");
                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                    int insertedRows = cpaloggerdao.insertIntoCpaLogging(msisdn, campaignUnique, club.getUnique(), 10, networkid, campaignsrc);

                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                    int updatecpavisit = cpaloggerdao.updateCpaVisitLogMsisdnSubscriptionDate(msisdn, clubUser.getSubscribed(), cmpg.getUnique(), cpaparameter1, cpaparameter2, cpaparameter3);
                }

                if (cmpg != null && cmpg.getSrc().trim().endsWith("RS")) {

                    String parameter1 = (String) session.getAttribute("revparam1");
                    String parameter2 = (String) session.getAttribute("revparam2");
                    String parameter3 = (String) session.getAttribute("revparam3");

                    //currentTime.add(Calendar.MINUTE, 10);
                    //String nextpush = SdcMiscDate.toSqlDate(new Date());

                    String enMsisdn = MiscCr.encrypt(msisdn);
                    logger.info("Revshare for msisdn {} campaign {} {} club {} parameters [{}|{}|{}]", msisdn, campaignUnique, cmpg.getSrc(), club.getUnique(), parameter1, parameter2, parameter3);
                    ThreadContext.put("EXTRA", "Revshare");
                    logger.info("Revshare for msisdn {} campaign {} {} club {} parameters [{}|{}|{}]", msisdn, campaignUnique, cmpg.getSrc(), club.getUnique(), parameter1, parameter2, parameter3);
                    ThreadContext.put("EXTRA", "");
                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                    int insertedRows =  cpaloggerdao.insertIntoRevShareLogging(0,  cmpg.getPayoutCurrency(), msisdn, enMsisdn, campaignUnique, club.getUnique(), 0, networkid, campaignsrc, 0);

                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                    int updatedRows = cpaloggerdao.updateRevShareVisitLogMsisdnSubscriptionDate(msisdn, clubUser.getSubscribed(),  cmpg.getUnique(), parameter1, parameter2, parameter3);
                }

                PassiveVisitor visitor = passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
                if (visitor != null && visitor.getStatus() == 0) {
                    passivevisitordao.updatePassiveVisitorStatus(visitor, 1);
                    logger.info("PassiveVisitor details {} ",visitor);
                }
                
            }
            /**
             * *******************************FollowUp-Update**************************************************
             */

            //===================== Sending Message Start===============================================
            if(userclubdetails.getBillingType().equalsIgnoreCase("subscription")
                    && (userclubdetails.getServiceType().equalsIgnoreCase("content") || userclubdetails.getServiceType().equalsIgnoreCase("competition"))){
            String resp = "";
            SdcSmsGateway gw = null;
            if (sendWelcome && aReq.get("result").equals("CONFIRM") && user!=null && clubUser!=null) {
                
                Calendar calStart = new GregorianCalendar();
                calStart.setTime(new Date());
                calStart.set(Calendar.HOUR_OF_DAY, 5);
                calStart.set(Calendar.MINUTE, 0);
                calStart.set(Calendar.SECOND, 0);
                calStart.set(Calendar.MILLISECOND, 0);
                Date today5am = calStart.getTime();

                Calendar calEnd = new GregorianCalendar();
                calEnd.setTime(new Date());
                calEnd.set(Calendar.HOUR_OF_DAY, 10);
                calEnd.set(Calendar.MINUTE, 0);
                calEnd.set(Calendar.SECOND, 0);
                calEnd.set(Calendar.MILLISECOND, 0);
                Date today7pm = calEnd.getTime();

                Date today = new Date();
                
                List<UmeClubMessages> welcomeMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Welcome");        
                if(welcomeMessages!=null && !welcomeMessages.isEmpty()){
                    for(int i=0;i<welcomeMessages.size();i++){
                        if(club!=null && !welcomeMessages.get(i).getaMessage().isEmpty()){    
                            ZaSmsSubmit welcomesms = new ZaSmsSubmit(aReq);
                            welcomesms.setUnique(Misc.generateUniqueId(10)+"-"+clubUser.getClubUnique());
                            welcomesms.setLogUnique(Misc.generateUniqueId(10)+"-"+clubUser.getClubUnique());
                            welcomesms.setSmsAccount("sts");
                            welcomesms.setUmeUser(user);
                            welcomesms.setToNumber(clubUser.getParsedMobile());
                            welcomesms.setFromNumber("1144");
                            welcomesms.setNetworkCode(clubUser.getNetworkCode());
                            welcomesms.setClubUnique(clubUser.getClubUnique());
                            welcomesms.setStatus("SENT");
                            welcomesms.setRefMessageUnique("WELCOME");
                            welcomesms.setMsgBody(welcomeMessages.get(i).getaMessage());
                            welcomesms.setCampaignId(1144);//(Integer.parseInt(club.getSmsExt())); 
                            //welcomesms.setMsgBody(userclubdetails.getClubWelcomeSms2() );
                            try {
                                gw = new SdcSmsGateway();                                
                                gw.setAccounts(welcomesms.getSmsAccount());
                                gw.setMsisdnFormat(4);
                                resp = smsservice.send(welcomesms, gw);
                                System.out.println("WELCOME MESSAGE SENT " + welcomesms.getMsgBody() + "  " + "Response " + resp + " msisdn: " + welcomesms.getToNumber());
                            } catch (NumberFormatException e) {}
                        } //END club!=null AND message is not Empty
                    } //END FOR LOOP of WelcomeMessage
                } //END IF WelcomeMessage is not null and not empty 

                try {
                    String personallinkmsg="Your Personal Link to the service is " + "http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId();
                    if(userclubdetails.getServiceType().equalsIgnoreCase(("competition"))){
                        personallinkmsg="Your Personal Link for your daily question is "+ "http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId();
                    }
                    ZaSmsSubmit personalink = new ZaSmsSubmit(aReq);
                    gw = new SdcSmsGateway();
                    gw.setAccounts(personalink.getSmsAccount());
                    gw.setMsisdnFormat(4);
                    personalink.setUnique(Misc.generateUniqueId(10)+"-"+clubUser.getClubUnique());
                    personalink.setLogUnique(Misc.generateUniqueId(10)+"-"+clubUser.getClubUnique());
                    personalink.setSmsAccount("sts");
                    personalink.setUmeUser(user);
                    personalink.setToNumber(clubUser.getParsedMobile());
                    personalink.setFromNumber("1144");
                    personalink.setMsgBody(personallinkmsg);
                    personalink.setCampaignId(1144);//(Integer.parseInt(club.getSmsExt()));
                    personalink.setNetworkCode(clubUser.getNetworkCode());
                    personalink.setClubUnique(clubUser.getClubUnique());
                    personalink.setRefMessageUnique("PERSONALLINK");
                    personalink.setStatus("SENT");
                    resp = smsservice.send(personalink, gw);
                    System.out.println("Personal Link sent " + resp + " msisdn " + clubUser.getParsedMobile());
                } catch (Exception e) {
                    
                    ThreadContext.put("EXTRA", "ERROR");
                    logger.error("ERROR SENDING PERSONAL LINK TO {}-{} {}", clubUser.getParsedMobile(),clubUser.getClubUnique(),e.getMessage());
                    logger.error("ERROR SENDING PERSONAL LINK TO {}-{} ", clubUser.getParsedMobile(),clubUser.getClubUnique(),e);
                    ThreadContext.put("EXTRA", "");
                }

            }
            
            }
            
//            else if(userclubdetails.getBillingType().equalsIgnoreCase("subscription")
//                    && userclubdetails.getServiceType().equalsIgnoreCase("competition")){
//                //TODO SEND TEASER MESSAGE
//            }
                   
            //==========  Sending message End ==================================
            status = club.getWebConfirmation();
            if (status == null || status.equals("")) {
                status = "";//Your account has been activated.<br>You should receive a Text shortly containing your personal link to the service.";
            }		    //System.out.println("status: " + status);
            session.setAttribute("status", status);
            if(clubUser!=null) session.setAttribute("sdc_msisdn_param", clubUser.getParsedMobile());

            String redirecturl = "http://" + dmn.getDefaultUrl();
            
            if(aReq.get("result").equals("CONFIRM")){
                
            //========= If Club Service is Content
                if(userclubdetails.getServiceType().equalsIgnoreCase("content") && user!=null && clubUser!=null){
                    
                    //=========== If it is Provided by UME
                    if(userclubdetails.getServedBy().equalsIgnoreCase("ume")){                
                    redirecturl="http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId();
                    }
                    
                    //========  If it is provided by Third Party like MobiPlanet
                    else if(userclubdetails.getServedBy().equalsIgnoreCase("thirdparty")){
                       redirecturl="http://" + dmn.getRedirectUrl() + "/?m="+Misc.encrypt(clubUser.getParsedMobile())+"&sub=success&credit="+clubUser.getCredits();
                    }
                    //====== If no setting is present=============
                    else redirecturl="http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId();
                }
                
                //===== TODO if this service is Competition =================
            if(userclubdetails.getServiceType().equalsIgnoreCase("competition") && user!=null && clubUser!=null){
                redirecturl="http://"+dmn.getDefaultUrl()+"/weeklyquiz.jsp?clubid="+club.getUnique()+"&msisdn="+user.getParsedMobile()+"&mid="+Misc.encrypt(user.getParsedMobile());
            }
            
         }
            //============ IF not CONFIRM ============== 
            else redirecturl = "http://" + dmn.getDefaultUrl()+"?status="+status;
            
            //==== Setting in request an dsession for zaconfirm.jsp ============ 
            request.setAttribute("personallink", redirecturl);
            session.setAttribute("personallink", redirecturl);
            return;

    }
    }

    public boolean deactivateUser(String msisdn, MobileClub club, String defClubDomain, UmeUserDao umeuserdao, UmeMobileClubUserDao umemobileclubuserdao, UserAuthentication userauthentication, HttpServletRequest request, CheckStop checkstop) {
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

        boolean shouldStop = true;//checkstop.checkStopCount("ZA");
        if (shouldStop) {
            if (clubUser != null && clubUser.getActive()==1) {
                clubUser.setActive(0);
                clubUser.setUnsubscribed(SdcMiscDate.parseSqlDateString(unSubscribed));
                umemobileclubuserdao.saveItem(clubUser);
                //boolean disabled=billingplandao.disableBillingPlan(msisdn, club.getUnique());
                try {
                    if (clubUser.getCampaign() != null) {
                        MobileClubCampaign campaign = UmeTempCmsCache.campaignMap.get(clubUser.getCampaign());
                        if (campaign != null && campaign.getSrc().trim().endsWith("RS")) {

                        }
                    }
                    userauthentication.invalidateUser(request);
                } catch (Exception e) {
                    System.out.println("Exception occurred while performing RevShare Termination in ZACONFIRM line no. 810" + e);
                    ThreadContext.put("EXTRA", "ERROR");
                    logger.error("ERROR WHILE PERFORMING REVSHARE TERMINATION {}-{} {}", clubUser.getParsedMobile(),clubUser.getClubUnique(),e.getMessage());
                    logger.error("ERROR WHILE PERFORMING REVSHARE TERMINATION {}-{} ", clubUser.getParsedMobile(),clubUser.getClubUnique(),e);
                    ThreadContext.put("EXTRA", "");
                }
            }

            ZaDoi zadoi = new ZaDoi();
            long beforeAuth = System.currentTimeMillis();
            String token = zadoi.authenticate();
            String serviceName = club.getOtpServiceName();
            terminated = zadoi.delete_DoubleOptIn_Record(token, serviceName, msisdn);
            
            PassiveVisitor visitor = passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
            if(visitor!=null && visitor.getStatus()==1){
                passivevisitordao.deletePassiveRecord(msisdn, club.getUnique());
            }
            
            
        }
        //System.out.println("==USER TERMINATED ==== "+terminated+" mobile "+msisdn+" club: "+club.getUnique());
        return terminated;
    }
    
   private List<UmeDomain> getNotSubscribedClubDomains(MobileClub club, MobileClubDao mobileclubdao, UmeUser user, UmeTempCache umesdc) {
        List<String> sisterClubList = new ArrayList<String>();
        // String[] sisterClubArray=new String;
        String sisterClubs = club.getSisterClubs();
        System.out.println("Sister Clubs: " + sisterClubs);
        if (sisterClubs.contains("?")) {
            sisterClubList = new ArrayList<String>(Arrays.asList(sisterClubs.split("\\?")));
        } else {
            if (sisterClubs != null && !sisterClubs.equals("")) {
                sisterClubList.add(sisterClubs);
            }

        }

        boolean activeInSisterClub = false;
        List<UmeDomain> notSubscribedClubDomains = new ArrayList<UmeDomain>();
        //List<String> notSubscribedClubDomainsUrls = new ArrayList<String>();

        if (sisterClubList.size() > 0 && sisterClubList != null) {
            for (String sisterClubUnique : sisterClubList) {
                MobileClub sisterClub =UmeTempCmsCache.mobileClubMap.get(sisterClubUnique);// mobileclubdao.getMobileClubMap().get(sisterClubUnique);
                if (user != null) {
                    activeInSisterClub = mobileclubdao.isActive(user, sisterClub);
                    if (!activeInSisterClub) {
                        String clubDomainUnique = sisterClub.getWapDomain();
                        //UmeDomain sisterDomain=umesdc.getDomainMap().get(clubDomainUnique);
                        notSubscribedClubDomains.add(umesdc.getDomainMap().get(clubDomainUnique));
                        //notSubscribedClubDomainsUrls.add(umesdc.getDomainMap().get(clubDomainUnique).getDefaultUrl());
	    		/* doRedirect(response,"http://" + sisterDomain.getDefaultUrl() +"/?id=" + user.getWapId()+"&mid="+enMsi+"&logtype=redirect");
                         return; */
                    }
                } else {
                    String clubDomainUnique = sisterClub.getWapDomain();
                    notSubscribedClubDomains.add(umesdc.getDomainMap().get(clubDomainUnique));
                }

            }
        }
        return notSubscribedClubDomains;
    }

}
