package ume.pareva.ire;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.CpaVisitLogDao;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.MobileNetworksDao;
import ume.pareva.dao.PassiveVisitorDao;
import ume.pareva.dao.QuizSmsDao;
import ume.pareva.dao.RegulatoryLogDao;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeDomainDao;
import ume.pareva.dao.UmeLanguagePropertyDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeQuizDao;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.dao.UmeSmsKeywordDao;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.PassiveVisitor;
import ume.pareva.pojo.QuizUserAttempted;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeClubMessages;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.userservice.StopUser;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.util.MessageReplacement;
import ume.pareva.util.ValidationUtil;
import ume.pareva.util.httpconnection.HttpURLConnectionWrapper;

/**
 *
 * @author madan
 */
public class IEMO extends HttpServlet {

    private final Logger logger = LogManager.getLogger(IEMO.class.getName());

    @Autowired
    HandsetDao handsetdao;

    @Autowired
    UmeTempCache tempcache;

    @Autowired
    UmeLanguagePropertyDao langpropdao;

    @Autowired
    MobileClubDao mobileclubdao;

    @Autowired
    Misc misc;

    @Autowired
    MobileClubCampaignDao campaigndao;

    @Autowired
    UmeQuizDao umequizdao;

    @Autowired
    ValidationUtil validationutil;

    @Autowired
    UmeSmsDao umesmsdao;

    @Autowired
    UmeUserDao umeuserdao;

    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;

    @Autowired
    MobileClubBillingPlanDao billingplandao;

    @Autowired
    CpaVisitLogDao cpavisitlogdao;

    @Autowired
    QuizSmsDao quizsmsdao;

    @Autowired
    MobileNetworksDao mobilenetwork;

    @Autowired
    PassiveVisitorDao passivevisitordao;

    @Autowired
    CpaLoggerDao cpaloggerdao;

    @Autowired
    UmeSmsKeywordDao smskeywordao;

    @Autowired
    StopUser stopuser;

    @Autowired
    UmeDomainDao umedomaindao;

    @Autowired
    UmeTempCache umesdc;

    @Autowired
    SubscriptionCreation subscriptioncreation;

    @Autowired
    UmeClubDetailsDao umeclubdetailsdao;

    @Autowired
    MessageReplacement messagereplace;
    
    @Autowired
    RegulatoryLogDao regulatorylog;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat sdfWeekY = new SimpleDateFormat("yyyy-ww");

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     *
     *
     * Variable	Content id	the unique message id number number	the originating
     * telephone number network	the originating network message	the contents of
     * the message shortcode	the orginating shortcode billing	The network
     * billing type where available country	The 2 digit ISO country code (e.g.
     * UK, US, ES).
     *
     * The CLUB Object is identified by KEYWORD sent to us AND if it is STOP
     * request We deactivate USER to all the clubs.
     *
     *
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();

        Enumeration parameterList = request.getParameterNames();
        while (parameterList.hasMoreElements()) {
            String sName = parameterList.nextElement().toString();
            System.out.println("txtnation: callback MO " + sName + ":" + request.getParameter(sName));
        }

        String id = request.getParameter("id"); //
        String msisdn = request.getParameter("number");
        String network = request.getParameter("network");
        String message = request.getParameter("message");
        String shortcode = request.getParameter("shortcode");
        String billing = request.getParameter("billing");
        String country = request.getParameter("country");
        String transactionId = Misc.generateUniqueIntegerId() + "";
        String regionCode="IE";

        if(msisdn!=null && msisdn.startsWith("47")){
            regionCode="NO";
        }
         if(msisdn!=null && msisdn.startsWith("46")){
            regionCode="SE";
        }
          if(msisdn!=null && msisdn.startsWith("353")){
            regionCode="IE";
        }

          if(msisdn!=null && msisdn.startsWith("61")){
              regionCode="AU";
        }
           if(msisdn!=null && msisdn.startsWith("30")){
              regionCode="GR";
        }
            if(msisdn!=null && msisdn.startsWith("357")){
              regionCode="CY";
        }
            
            
            ThreadContext.put("ROUTINGKEY",regionCode);
            ThreadContext.put("EXTRA", "");

        Handset handset = (Handset) session.getAttribute("handset");

        boolean sendConfirmation = true;
        boolean createbillPlan = true;
        MobileClubCampaign cmpg = null;
        String keyClubUnique = "";

        String billednetwork = network; //This is to store original IE network code into club subscriber and billing plans

        network = mobilenetwork.getMobileNetwork(regionCode, network.toLowerCase());
        UmeUser user = null;
        boolean exist = umequizdao.quizReplyExistOrNot(id);
        System.out.println("txtnation exist value " + exist); //

        if (!exist) {
            MobileClub club = null;
            UmeClubDetails userclubdetails = null;

            if (!message.toLowerCase().contains("stop")) {
                if(regionCode.equalsIgnoreCase("AU")){
                    message=message.toLowerCase().trim().substring(0,3);
                }
                //keyClubUnique = smskeywordao.getClubUnique(message.toLowerCase(), regionCode);
                 keyClubUnique=smskeywordao.getClubUnique(message.toLowerCase().trim(),regionCode.toLowerCase().trim(), shortcode);
                System.out.println("keyworddao "+regionCode+" clubunique is " + keyClubUnique + " for message " + message);
                
                logger.info("MO msisdn {} message {} shortcode {}",msisdn,message,shortcode);
            }
            umequizdao.saveQuizReply(id, msisdn, message, shortcode, keyClubUnique, network);

            System.out.println("IEMO STOP " + "keyworddao "+regionCode+" shortcode = "+shortcode+" clubunique is " + keyClubUnique + " for message " + message);
            if (message != null && (message.toLowerCase().contains("stop") || message.toLowerCase().contains("quit"))) {
                if(shortcode.startsWith("0000") || shortcode.equals("60999") || shortcode.trim().isEmpty()){
                    stopuser.stopAllSubscription(msisdn, null,null, "iemo");
                }else{
                    stopuser.stopAllSubscriptionByShortCode(msisdn,shortcode, null, null,"iemo"); //Stop All Subscription
                }
                ThreadContext.put("EXTRA", "STOP");
                logger.info("MO msisdn {} message {} shortcode {}",msisdn,message,shortcode);
                ThreadContext.put("EXTRA", "");
                
                return;
            }

            if (keyClubUnique != null && !keyClubUnique.equals("")) {
                club = UmeTempCmsCache.mobileClubMap.get(keyClubUnique);
            }
            if (club != null) {
                userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
            }

            String msg = "";
            /*try {
             msg = club.getWebConfirmation();
             } catch (Exception e) {
             msg = "";
             }*/

            String campaignUnique = "", landingpage = "", pubId = "";
            boolean exists = false;
            boolean entryConfirmation = false;
            String status = "";
            String subscdate = "";
            String hash = "";
            String defClubDomain = "5510024809921CDS";

            Calendar c1 = new GregorianCalendar();
            Date bstart = c1.getTime();
            c1.setTime(bstart);
            c1.add(Calendar.DATE, club.getPeriod());
            Date bend = c1.getTime();

            int duration = club.getPeriod();
            if (duration <= 0) {
                duration = 7; // 7 Days Billing Period  making default
            }
            SdcMobileClubUser clubUser = null;
            MobileClubBillingPlan billingplan = null;

            boolean passiveexist = false;
            PassiveVisitor visitor = null;
            try {
                visitor = passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
            } catch (Exception e) {
                visitor = null;
            }
            if (visitor != null) {
                //System.out.println("==ConfirmZA VISITOR EXIST "+exist);
                try {
                    campaignUnique = visitor.getCampaign();//passivevisitordao.getCampaignId(msisdn, club.getUnique());
                } catch (Exception e) {
                    campaignUnique = "";
                }

                try {
                    landingpage = visitor.getLandignPage();//passivevisitordao.getLandingPage(msisdn, club.getUnique());
                } catch (Exception e) {
                    landingpage = "";
                }

                try {
                    pubId = visitor.getPubId();
                } catch (Exception e) {
                    pubId = "";
                }
            }

            //=========== Handling STOP Message =====================================
            if (message != null && (message.toLowerCase().contains("stop") || message.toLowerCase().contains("quit"))) {
                //stopuser.stopAllSubscription(msisdn, null, null); //Stop All Subscription
                //handled above . this is set blank to maintain else condition below. 
            }//End stop message
            //=============Handling STOP Message End ======================
            //========================== Subscription Handling ==============================
            else {
                System.out.println("txtnation IEMO " + "User not stopped ");

                //============== Passive Visitor =======================
                visitor = passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
                if (visitor != null && visitor.getStatus() == 0) {
                    passivevisitordao.updatePassiveVisitorStatus(visitor, 1);
                }
                
                //To address TxtNation Issues where we received multiple requests. 
                if(billednetwork.toLowerCase().contains("three") && regulatorylog.subscribedIn24Hour(msisdn, club.getUnique())) return;

                String subsresponse = subscriptioncreation.checkSubscription(msisdn, club, campaignUnique, duration, "", billednetwork, "", landingpage, pubId);

                if (subsresponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY")
                        || subsresponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY")) {
                    String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");
                    if (userUnique != null && !userUnique.equals("")) {
                        user = umeuserdao.getUser(msisdn); //
                    }
                    clubUser = user.getClubMap().get(club.getUnique());
                    if (clubUser == null) {
                        clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                    }
                    if (user != null && clubUser != null) {
                        user.getClubMap().put(club.getUnique(), clubUser);
                        createbillPlan = true;
                    }

                    logger.info("MO subscription  " + clubUser.toString() + " Create Billing Plan is " + createbillPlan);
                }

                if (subsresponse.equals("SUBSCRIPTION RECORD ALREADY EXISTS")) {
                    createbillPlan = false;
                    String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");
                    if (userUnique != null && !userUnique.equals("")) {
                        user = umeuserdao.getUser(msisdn);
                    }
                    clubUser = user.getClubMap().get(club.getUnique());
                    if (clubUser == null) {
                        clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                    }

                    System.out.println("IEMO subscription  " + clubUser.toString() + " createbillPlan IS " + createbillPlan);
                }
                //Get the Active Billing Plan of the user. 
                billingplan = billingplandao.getActiveBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());

                if (billingplan == null || createbillPlan) {
                    billingplan = new MobileClubBillingPlan();
                    billingplan.setTariffClass(club.getPrice());
                    billingplan.setActiveForAdvancement(1);
                    billingplan.setActiveForBilling(1);
                    billingplan.setAdhocsRemaining(0.0);
                    billingplan.setBillingEnd(bend);
                    billingplan.setClubUnique(club.getUnique());
                    billingplan.setContractType("");
                    billingplan.setLastPaid(clubUser.getSubscribed());
                    billingplan.setLastSuccess(new Date(0));
                    billingplan.setLastPush(clubUser.getSubscribed());
                    billingplan.setNetworkCode(billednetwork);
                    billingplan.setNextPush(bend);
                    billingplan.setParsedMobile(user.getParsedMobile());
                    billingplan.setPartialsPaid(0.0);
                    billingplan.setSubscribed(clubUser.getSubscribed());
                    billingplan.setPartialsRequired(Double.parseDouble(String.valueOf(userclubdetails.getFrequency() + "")));
                    billingplan.setPushCount(0.0);
                    billingplan.setServiceDate(bstart);
                    billingplan.setSubUnique(clubUser.getUserUnique());
                    billingplan.setExternalId(""); //This is for Italy SubscriptionId so just setting the values. 
                    billingplan.setServiceDateBillsRemaining(Double.parseDouble(String.valueOf(userclubdetails.getFrequency())));
                    billingplan.setaCampaign(clubUser.getCampaign());//
                    billingplan.setPublisherId(pubId); //
                    billingplandao.insertBillingPlan(billingplan);

                    campaigndao.log("iremo", landingpage, msisdn, msisdn, null, null, clubUser.getCampaign(), clubUser.getClubUnique(), "SUBSCRIBED", 0, request, response, network.toLowerCase(), "", "", "", pubId);
                    logger.info("BillingPlans Created  record {}  ", billingplan.toString());
                    //Subscription CPA
                    if (null != campaignUnique && !"".equalsIgnoreCase(campaignUnique)) {
                        cmpg = UmeTempCmsCache.campaignMap.get(campaignUnique);

                        if (cmpg != null) {
                            if (cmpg.getSrc().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("subscription")) {
                                // 2016.01.13 - AS - Removed commented code, check repo history if needed
                                int insertedRows = cpaloggerdao.insertIntoCpaLogging(user.getParsedMobile(), campaignUnique, club.getUnique(), 10, network, cmpg.getSrc());
                                ThreadContext.put("EXTRA", "CPA");
                                logger.info("MO msisdn {} campaign {} {} club {} network{} ",msisdn,campaignUnique, cmpg.getSrc(),club.getUnique(),network);
                                ThreadContext.put("EXTRA", "");
                            }

                        }

                    }

                    String toAddresse = user.getParsedMobile();

                    msg = club.getWebConfirmation();
                    String dateofweek = sdfWeekY.format(new Date()) + "";

                    System.out.println("IEMO subscription  RETRIEVING WELCOME MESSAGES FOR " + club.getUnique());
                    List<UmeClubMessages> welcomeMessages=umeclubdetailsdao.getUmeClubMessages( club.getUnique(), "Welcome");

                    if (welcomeMessages != null) {
                        System.out.println("IEMO subscription  WELCOME MESSAGE LIST SIZE = " +welcomeMessages.size());
                        int counter=1;
                        for (UmeClubMessages welcomeMsg : welcomeMessages) {

                            if (counter == 1) {
                                clubUser.setParam1("1");
                            } else if (counter == 2) {
                                clubUser.setParam2("1");
                            }
                            msg = welcomeMsg.getaMessage().trim();
                            System.out.println("IEMO subscription  TRYING TO SEND welcome message for " + user.getParsedMobile() + " - " + club.getUnique() + " [" +msg +  "]");
                            String welcomeTransactionId = Misc.generateUniqueIntegerId() + "-" + club.getUnique();
                            try {
                                Thread.sleep(20);
                            } catch (Exception e) {
                            }

                            String welcomeShortcode=userclubdetails.getClubSpoof();
                            boolean success = sendFreeSMS(toAddresse, billednetwork, club.getUnique(), msg, welcomeShortcode, welcomeTransactionId, club, umesmsdao, quizsmsdao, umequizdao, "welcome", "txtmo.jsp", dateofweek, false);
                            if (!success) {
                                System.out.println("IEMO subscription  FAILED sending welcome message for " + user.getParsedMobile() + " - " + club.getUnique());
                            }else{
                                counter++;
                            }

                        }
                    }

                    // Wait 15 secs to send the billables
                    try {
                        Thread.sleep(15000);
                    } catch (Exception e) {
                    }
                    System.out.println("IEMO subscription  RETRIEVING BILLING MESSAGES FOR " + club.getUnique());
                    List<UmeClubMessages> billableMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Billable");

                    if(billableMessages!=null && !billableMessages.isEmpty() && billableMessages.size()>0){
                        int counter=1;
                        String reqType="firstbillable";


                        for(int i=0;i<billableMessages.size();i++){

                          msg=billableMessages.get(i).getaMessage().trim();
                          transactionId = Misc.generateUniqueIntegerId() + "-"+club.getUnique();
                            if(counter==1) reqType="firstbillable";
                            if(counter==2) reqType="secondbillable";
                            if(counter==3) reqType="thirdbillable";
                            if(counter==4) reqType="fourthbillable";
                            if(counter==5) reqType="fifthbillable";
                            if(counter==6) reqType="sixthbillable";
                            if(counter==7) reqType="seventhbillable";
                            if(counter==8) reqType="eigthllable";
                            if(counter==9) reqType="ninthbillable";
                            if(counter==10) reqType="tenthdbillable";
                            
                            try{
                                Thread.sleep(20);
                            } catch (Exception e) {
                            }

                            if (sendPremiumSMS(toAddresse, billednetwork, club.getUnique(), msg, shortcode, transactionId, club, umesmsdao, quizsmsdao, umequizdao, reqType, "txtmo.jsp", dateofweek, false)) {

                                counter++;
                            }
                        } //END Billable message Loop
                    } //END Billable Message size >0 

                } //billingPlan ==null

            } //End else for users replying message

            // ======================== Subscription Handling END ============================
        } // END if not Exist

    }

    public boolean sendSMS(String[] msisdn, String serviceId, String msgText, String shortCode, String transactionId, MobileClub club, UmeSmsDao umesmsdao, QuizSmsDao quizsmsdao) {

        /**
         * $req = 'reply=0'; $req .= '&id='.uniqid(); $req .=
         * '&number='.$number; $req .= '&network=INTERNATIONAL'; $req .=
         * '&message='.$msg; $req .= '&value=0'; $req .= '&currency=GBP'; $req
         * .= '&cc='.$company; $req .= '&title='.$title; $req .= '&ekey='.$ekey;
         * http://client.txtnation.com/gateway.php?reply=0&id=123456&number=353851485271
         * &network=international&value=0&currency=EUR&cc=moonlight&ekey=a6815e707c675f7a3f307656d462bca6&message=Your+message.
         */
        String irehttp = "http://client.txtnation.com/gateway.php";
        String id = Misc.generateUniqueId() + "-" + club.getUnique();
        String network = "international";
        String ekey = club.getOtpServiceName();//"a6815e707c675f7a3f307656d462bca6";
        String msg = msgText;

        UmeClubDetails userclubdetails = null; //
        userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());

        String cc = "ume", title = "";
        try {
            cc = userclubdetails.getCompanyCode();
        } catch (Exception e) {
            cc = "ume";
        }

        try {
            title = userclubdetails.getClubSpoof();
        } catch (Exception e) {
            title = "Quiz2Win";
        }

        HttpURLConnectionWrapper urlwrapper = new HttpURLConnectionWrapper(IREConnConstants.getDomainHttp());
        Map<String, String> ireMap = new HashMap<String, String>();

        ireMap.put("reply", "0");
        ireMap.put("id", id);
        ireMap.put("number", msisdn[0]);
        ireMap.put("network", network);
        ireMap.put("value", "0");
        ireMap.put("currency", club.getCurrency());
        ireMap.put("cc", cc);
        ireMap.put("ekey", ekey);
        ireMap.put("message", msg);
        ireMap.put("title", shortCode);
        
        if(club.getRegion().equalsIgnoreCase("IE"))
        ireMap.put("smscat", "991");

        urlwrapper.wrapGet(ireMap);

        String responsecode = urlwrapper.getResponseCode();
        String responsedesc = urlwrapper.getResponseContent();
        boolean isSuccessful = urlwrapper.isSuccessful();

        System.out.println("txtnation: http request sent " + irehttp);

        System.out.println("txtnation:  confirm.jsp response " + responsecode + "  desc " + responsedesc + " successful: " + isSuccessful);
//    
        boolean success = false;

        if (isSuccessful) {
            for (int i = 0; i < msisdn.length; i++) {

                SdcSmsSubmit sdcSmsSubmit = new SdcSmsSubmit();

                sdcSmsSubmit.setUnique(String.valueOf(transactionId));
                sdcSmsSubmit.setFromNumber(shortCode);
                sdcSmsSubmit.setToNumber(msisdn[i]);
                sdcSmsSubmit.setMsgType("Free");
                msgText = msgText.replaceAll(": ", ":".trim());
                System.out.println("IE Exception msgText " + msgText);
                sdcSmsSubmit.setMsgBody(msgText);
                sdcSmsSubmit.setLogUnique(id);
                sdcSmsSubmit.setCost(club.getPrice());
                sdcSmsSubmit.setClubUnique(club.getUnique());
                sdcSmsSubmit.setSmsAccount(cc);
                sdcSmsSubmit.setClubUnique(club.getUnique());
                sdcSmsSubmit.setStatus("SENT");
                sdcSmsSubmit.setMsgCode1("txtmo.jsp");
                umesmsdao.log(sdcSmsSubmit);
                quizsmsdao.log(sdcSmsSubmit);

            }
            success = true;
        } else {
            System.out.println("irelandtesting  " + responsecode + ":" + responsedesc);
        }
        return success;
    }

    public boolean sendPremiumSMS(String msisdn, String network, String serviceId, String msgText, String shortCode, String transactionId, MobileClub club, UmeSmsDao umesmsdao, QuizSmsDao quizsmsdao, UmeQuizDao umequizdao, String reqType, String sendFrom, String dateofweek, boolean wappush) {

        /**
         * $req = 'reply=0'; $req .= '&id='.uniqid(); $req .=
         * '&number='.$number; $req .= '&network=INTERNATIONAL'; $req .=
         * '&message='.$msg; $req .= '&value=0'; $req .= '&currency=GBP'; $req
         * .= '&cc='.$company; $req .= '&title='.$title; $req .= '&ekey='.$ekey;
         * http://client.txtnation.com/gateway.php?reply=0&id=123456&number=353851485271
         * &network=international&value=0&currency=EUR&cc=moonlight&ekey=a6815e707c675f7a3f307656d462bca6&message=Your+message
         */
        String irehttp = "http://client.txtnation.com/gateway.php";
        String id = Misc.generateUniqueId() + "-" + club.getUnique();
        //network="international";
        String ekey = club.getOtpServiceName();//"a6815e707c675f7a3f307656d462bca6";
        System.out.println("iemomessagenewclass  BEFORE changing " + msgText);
        String msgBkp = msgText;

        msgText = messagereplace.replaceMessagePlaceholders(msgText, msisdn, club.getUnique());

        if (msgText.isEmpty()) {
            msgText = msgBkp;
        }
        String msg = msgText;

        System.out.println("iemomessagenewclass  AFTER changing " + msg);
        if (msgText.contains("####")) {
            String encrypt = "Q2W" + MiscCr.encrypt(msisdn).substring(0, 5);
            msgText = msgText.replace("####", encrypt); //SdcMisc.generateLogin(5));
        }

        if (msgText.contains("****")) {
            //http:// defaultdomain/?id=user.getWapId();
            String domainUnique = club.getWapDomain();
            UmeDomain domain = umesdc.getDomainMap().get(domainUnique);
            UmeUser user = umeuserdao.getUser(msisdn);
            String personalLink = "http://" + domain.getDefaultUrl();
            if (user != null) {
                personalLink = personalLink + "/?id=" + user.getWapId();
            } else {
                logger.warn("NO USER FOUND FOR {}", msisdn);
            }
            msgText = msgText.replace("****", personalLink);
        }

           if(msgText.contains("thisisthencryptedversionofmsisdn")){
                msgText=msgText.replace("thisisthencryptedversionofmsisdn",MiscCr.encrypt(msisdn));
                System.out.println("iebilablemessage msisdn "+msisdn+" msg is "+msg);
        }

        try {

            msg = java.net.URLEncoder.encode(msgText, "utf-8");
        } catch (Exception e) {
        }

        UmeClubDetails userclubdetails = null;
        userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());

        String cc = "ume";
        try {
            cc = userclubdetails.getCompanyCode();
        } catch (Exception e) {
            cc = "ume";
        }

        HttpURLConnectionWrapper urlwrapper = urlwrapper = new HttpURLConnectionWrapper(IREConnConstants.getDomainHttp());
        Map<String, String> ireMap = new HashMap<String, String>();

        ireMap.put("reply", "0");
        ireMap.put("id", id);
        ireMap.put("number", msisdn);
        ireMap.put("network", network);
        //ireMap.put("value","0");
        ireMap.put("value", club.getPrice() + "");
        ireMap.put("currency", club.getCurrency());
        ireMap.put("cc", cc);
        ireMap.put("ekey", ekey);
        System.out.println("iemomessage SENDING  " + msg);
        ireMap.put("message", msg);
        ireMap.put("title", shortCode);
        if (club.getRegion().equals("IE")) {
            ireMap.put("smscat", "991");
        }

        if (wappush) {
            ireMap.put("wappush", "1");
        }

        urlwrapper.wrapGet(ireMap);

        String responsecode = urlwrapper.getResponseCode();
        String responsedesc = urlwrapper.getResponseContent();
        boolean isSuccessful = urlwrapper.isSuccessful();

        irehttp += "?reply=0&id=" + id + "&number=" + msisdn + "&network=" + network + "&value=" + club.getPrice() + "&currency=" + club.getCurrency() + "&cc=" + cc + "&ekey=" + ekey + "&message=" + msg;

        System.out.println("txtnation: http premium request sent " + irehttp);

        System.out.println("txtnation:  txtmo.jsp response " + responsecode + "  desc " + responsedesc + " successful: " + isSuccessful);
//    
        boolean success = false;

        if (isSuccessful) {

            QuizUserAttempted quizUserAttempted = new QuizUserAttempted();
            quizUserAttempted.setaParsedMsisdn(msisdn);
            quizUserAttempted.setClubUnique(club.getUnique());
            quizUserAttempted.setType("Entry Confirmation");
            quizUserAttempted.setStatus("false");
            quizUserAttempted.setaUnique(String.valueOf(transactionId));
            quizUserAttempted.setaCreated(new java.sql.Timestamp(System.currentTimeMillis()));
            umequizdao.saveQuizUserAttempted(quizUserAttempted);

            SdcSmsSubmit sdcSmsSubmit = new SdcSmsSubmit();

            sdcSmsSubmit.setUnique(id);
            sdcSmsSubmit.setLogUnique(id);
            sdcSmsSubmit.setFromNumber(shortCode);
            sdcSmsSubmit.setToNumber(msisdn);
            sdcSmsSubmit.setMsgType("Premium");
            sdcSmsSubmit.setMsgBody(msgText);
            sdcSmsSubmit.setCost(club.getPrice());
            sdcSmsSubmit.setMsgCode1(sendFrom);
            sdcSmsSubmit.setReqType(reqType);
            sdcSmsSubmit.setNetworkCode(network);
            sdcSmsSubmit.setMsgUdh(dateofweek);
            sdcSmsSubmit.setClubUnique(club.getUnique());
            sdcSmsSubmit.setStatus("SENT");
            sdcSmsSubmit.setRefMessageUnique("BILLING");
            sdcSmsSubmit.setSmsAccount(cc);
            umesmsdao.log(sdcSmsSubmit);
            quizsmsdao.log(sdcSmsSubmit);

            success = true;
        } else {
            System.out.println("txtnation irelandtesting  " + responsecode + ":" + responsedesc);
        }
        System.out.println("txtnation success returning is  " + success + "for reqType :" + reqType + " send from : " + sendFrom);
        return success;
    }

    private String getClubId(String longString, String separateby) {
        int index = longString.indexOf(separateby);
        if (index > 0) {
            return longString.substring(index + separateby.length());
        } else {
            return "6119598063441KDS";
        }
    }

    public boolean sendFreeSMS(String msisdn, String network, String serviceId, String msgText, String shortCode, String transactionId, MobileClub club, UmeSmsDao umesmsdao, QuizSmsDao quizsmsdao, UmeQuizDao umequizdao, String reqType, String sendFrom, String dateofweek, boolean wappush) {

        String irehttp = "http://client.txtnation.com/gateway.php";
        String id = Misc.generateUniqueId() + "-" + club.getUnique();
        String ekey = club.getOtpServiceName();
        System.out.println("IEMO subscription BEFORE changing " + msgText);
        String msgBkp = msgText;

        msgText=messagereplace.replaceMessagePlaceholders(msgText, msisdn, club.getUnique());

        if (msgText.isEmpty()) {
            msgText = msgBkp;
        }
        String msg = msgText;

        try {

            msg = java.net.URLEncoder.encode(msgText, "utf-8");
        } catch (Exception e) {
        }

        UmeClubDetails userclubdetails = null;
        userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());

        System.out.println("txtnation:  using " + shortCode + " as Shortcode for welcome");
        String cc = "ume";
        try {
            cc = userclubdetails.getCompanyCode();
        } catch (Exception e) {
            cc = "ume";
        }

        HttpURLConnectionWrapper urlwrapper = urlwrapper = new HttpURLConnectionWrapper(IREConnConstants.getDomainHttp());
        Map<String, String> ireMap = new HashMap<String, String>();

        ireMap.put("reply", "0");
        ireMap.put("id", id);
        ireMap.put("number", msisdn);
        ireMap.put("network", network);
        ireMap.put("value", "0");
        ireMap.put("currency", club.getCurrency());
        ireMap.put("cc", cc);
        ireMap.put("ekey", ekey);
        ireMap.put("message", msg);
        ireMap.put("title", shortCode);
        
        if(club.getRegion().equalsIgnoreCase("IE"))
                ireMap.put("smscat", "991");

        if (wappush) {
            ireMap.put("wappush", "1");
        }

        urlwrapper.wrapGet(ireMap);

        String responsecode = urlwrapper.getResponseCode();
        String responsedesc = urlwrapper.getResponseContent();
        boolean isSuccessful = urlwrapper.isSuccessful();

        irehttp += "?reply=0&id=" + id + "&number=" + msisdn + "&network=" + network + "&value=0&currency="+club.getCurrency()+"&cc=" + cc + "&ekey=" + ekey + "&message=" + msg;

        System.out.println("IEMO subscription txtnation: http welcome request sent " + irehttp);

        System.out.println("IEMO subscription txtnation:  txtmo.jsp response " + responsecode + "  desc " + responsedesc + " successful: " + isSuccessful);
//    
        boolean success = false;

        if (isSuccessful) {

            QuizUserAttempted quizUserAttempted = new QuizUserAttempted();
            quizUserAttempted.setaParsedMsisdn(msisdn);
            quizUserAttempted.setClubUnique(club.getUnique());
            quizUserAttempted.setType("Entry Confirmation");
            quizUserAttempted.setStatus("false");
            quizUserAttempted.setaUnique(String.valueOf(transactionId));
            quizUserAttempted.setaCreated(new java.sql.Timestamp(System.currentTimeMillis()));
            umequizdao.saveQuizUserAttempted(quizUserAttempted);

            SdcSmsSubmit sdcSmsSubmit = new SdcSmsSubmit();

            sdcSmsSubmit.setUnique(id);
            sdcSmsSubmit.setLogUnique(id);
            sdcSmsSubmit.setFromNumber(shortCode);
            sdcSmsSubmit.setToNumber(msisdn);
            sdcSmsSubmit.setMsgType("Free");
            sdcSmsSubmit.setMsgBody(msgText);
            sdcSmsSubmit.setCost(0);
            sdcSmsSubmit.setMsgCode1(sendFrom);
            sdcSmsSubmit.setReqType(reqType);
            sdcSmsSubmit.setNetworkCode(network);
            sdcSmsSubmit.setMsgUdh(dateofweek);
            sdcSmsSubmit.setClubUnique(club.getUnique());
            sdcSmsSubmit.setStatus("SENT");
            sdcSmsSubmit.setRefMessageUnique("WELCOME");
            sdcSmsSubmit.setSmsAccount(cc);
            umesmsdao.log(sdcSmsSubmit);
            quizsmsdao.log(sdcSmsSubmit);

            success = true;
        } else {
            System.out.println("IEMO subscription txtnation irelandtesting  " + responsecode + ":" + responsedesc);
        }
        System.out.println("IEMO subscription txtnation success returning is  " + success + "for reqType :" + reqType + " send from : " + sendFrom);
        return success;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
