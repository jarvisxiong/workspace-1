/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.it;

import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiBindingStub;
import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiPort;
import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiServiceLocator;
import com.ipx.www.api.services.subscriptionapi40.types.CreateSubscriptionRequest;
import com.ipx.www.api.services.subscriptionapi40.types.CreateSubscriptionResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;
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
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeDomainDao;
import ume.pareva.dao.UmeLanguagePropertyDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeQuizDao;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.dao.UmeSmsKeywordDao;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.ire.IREConnConstants;
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
import ume.pareva.sdk.MiscDate;
import ume.pareva.smsapi.IpxSmsConnection;
import ume.pareva.smsapi.IpxSmsSubmit;
import ume.pareva.userservice.StopUser;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.util.MessageReplacement;
import ume.pareva.util.ValidationUtil;
import ume.pareva.util.httpconnection.HttpURLConnectionWrapper;

/**
 *
 * @author trung
 */
@WebServlet(name = "ITMO", urlPatterns = {"/ITMO"})
public class ITMO extends HttpServlet {

    private final Logger logger = LogManager.getLogger(ITMO.class.getName());

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
    MessageReplacement messageReplacement;
    
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
        ThreadContext.put("ROUTINGKEY", "IT");
        ThreadContext.put("EXTRA", "MO");

        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<DeliveryResponse ack=\\\"true\\\"/>");
        response.setStatus(response.SC_OK);
        
        System.out.println("****** ipx: callback MO BEGIN ******");
        Enumeration parameterList = request.getParameterNames();

        while (parameterList.hasMoreElements()) {
            String sName = parameterList.nextElement().toString();
            System.out.println("ipx: callback MO " + sName + ":" + request.getParameter(sName));
        }
        System.out.println("****** ipx: callback MO END ******");

        String id = request.getParameter("MessageId"); //
        String msisdn = request.getParameter("OriginatorAddress");
        String network = request.getParameter("Operator");
        String message = request.getParameter("Message");
        String shortcode = request.getParameter("DestinationAddress");
        String transactionId = request.getParameter("MessageId");
//        String regionCode = "IT";
        
        requestSendSubscriptionTimer(request, response, 4*1000, id, msisdn, network, message, shortcode, transactionId);
    }
    
    
    public void requestSendSubscriptionTimer(final HttpServletRequest request,
                    final HttpServletResponse response, int time, final String id, final String msisdn, 
                    final String network, final String message, final String shortcode, final String transactionId) {
        new java.util.Timer().schedule( 
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        processSubscription(request, response, id, msisdn, network, message, shortcode, transactionId);
                        }
                }, 
                time 
        );

    }
    public void processSubscription(HttpServletRequest request, HttpServletResponse response, String id, String msisdn, 
                   String network, String message, String shortcode, String transactionId){
        Enumeration parameterList = request.getParameterNames();
        
        System.out.println("****** ipx processSubscription: callback MO BEGIN ******");

        while (parameterList.hasMoreElements()) {
            String sName = parameterList.nextElement().toString();
            System.out.println("ipx processSubscription: callback MO " + sName + ":" + request.getParameter(sName));
        }
        System.out.println("****** ipx processSubscription: callback MO END ******");
//        String id = request.getParameter("MessageId"); //
//        String msisdn = request.getParameter("OriginatorAddress");
//        String network = request.getParameter("Operator");
//        String message = request.getParameter("Message");
//        String shortcode = request.getParameter("DestinationAddress");
//        String transactionId = request.getParameter("MessageId");
        String regionCode = "IT";
        
//        String messageAlphabet = request.getParameter("MessageAlphabet");

        String subscriptionId = "";

        boolean createbillPlan = true;
        MobileClubCampaign cmpg = null;
        String keyClubUnique = "";

        if(network!=null)
            network = network.toLowerCase();
        
        String billednetwork = network; //This is to store original IT network code into club subscriber and billing plans

//        network = mobilenetwork.getMobileNetwork(regionCode, network.toLowerCase());
        System.out.println("****** ipx: Debug MO BEGIN ******");

        UmeUser user = null;
        boolean exist = umequizdao.quizReplyExistOrNot(id);
        System.out.println("ipx exist value " + exist);

        if (!exist) {
            MobileClub club = null;
            UmeClubDetails userclubdetails = null;

            if (!message.toLowerCase().contains("stop")) {
                //keyClubUnique = smskeywordao.getClubUnique(message.toLowerCase(), regionCode);
                keyClubUnique = smskeywordao.getClubUnique(message.trim().replace(" ", ""), regionCode.toLowerCase().trim(), shortcode);
                System.out.println("keyworddao " + regionCode + " clubunique is " + keyClubUnique + " for message " + message);

            }
            umequizdao.saveQuizReply(id, msisdn, message, shortcode, keyClubUnique, network);

            if (message != null && (message.toLowerCase().contains("stop") || message.toLowerCase().contains("quit"))) {
                stopuser.stopAllSubscriptionByShortCode(msisdn, shortcode, null, null, "itmo"); //Stop All Subscription
                return;
            }

            if (keyClubUnique != null && !keyClubUnique.equals("")) {
                club = UmeTempCmsCache.mobileClubMap.get(keyClubUnique);
            }
            if (club != null) {
                userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
                
//                try {
////                    Thread.sleep(11000);
//                } catch (InterruptedException ex) {
//                    java.util.logging.Logger.getLogger(ITMO.class.getName()).log(Level.SEVERE, null, ex);
//                }
                
                System.out.print("IPX :" + club.getOtpSoneraId() + "--" + club.getOtpTelefiId() + "--" + club.getName());
                
//                subscriptionId = createSubscriptionId(transactionId, msisdn,
//                network, club.getOtpServiceName(), club.getUnique(), club.getOtpSoneraId(), club.getOtpTelefiId(),
//                club); 
                
                
                
//                subscriptionId = createSubscriptionId(transactionId, msisdn,
//                network, "service=MOBIPLANET", club.getUnique(), "umeP-it", "MNTrsd45",
//                club); 
                
                subscriptionId = createSubscriptionId(transactionId, msisdn,
                network, club.getOtpServiceName(), club.getUnique(), club.getOtpSoneraId(), club.getOtpTelefiId(),
                club); 
                
//                if(subscriptionResponse.getResponseCode()==0){
//                    subscriptionId = subscriptionResponse.getSubscriptionId();
//                }
            }

            String msg = "";
            try {
                msg = club.getWebConfirmation();
            } catch (Exception e) {
                msg = "";
            }
            String campaignUnique = "", landingpage = "", pubId = "";

            Calendar c1 = new GregorianCalendar();
            Date bstart = c1.getTime();
            c1.setTime(bstart);
            c1.add(Calendar.DATE, 7);
            Date bend = c1.getTime();

            SdcMobileClubUser clubUser = null;
            MobileClubBillingPlan billingplan = null;

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
            
            if(subscriptionId==null || (subscriptionId!=null && subscriptionId.isEmpty())){
                //subscriptionFailed
            }
          
            else {
                System.out.println("ipx ITMO " + "User not stopped ");

                //============== Passive Visitor =======================
                visitor = passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
                if (visitor != null && visitor.getStatus() == 0) {
                    passivevisitordao.updatePassiveVisitorStatus(visitor, 1);
                }

//                String subsresponse = subscriptioncreation.checkSubscription(msisdn, club, campaignUnique, 7, "", billednetwork, "", landingpage, pubId);
                String subsresponse = subscriptioncreation.checkSubscription(msisdn, club, campaignUnique, 7, "", billednetwork, subscriptionId, landingpage, pubId, transactionId, subscriptionId);

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
                        createbillPlan = true;
                    }

                    logger.info("ITMO subscription  " + clubUser.toString() + " Create Billing Plan is " + createbillPlan);
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

                    System.out.println("ITMO subscription  " + clubUser.toString() + " createbillPlan IS " + createbillPlan);
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
                    billingplan.setExternalId(subscriptionId); //This is for Italy SubscriptionId so just setting the values. 
                    billingplan.setServiceDateBillsRemaining(Double.parseDouble(String.valueOf(userclubdetails.getFrequency())));
                    billingplandao.insertBillingPlan(billingplan);

                    campaigndao.log("iremo", landingpage, msisdn, msisdn, null, null, clubUser.getCampaign(), clubUser.getClubUnique(), "SUBSCRIBED", 0, request, response, network.toLowerCase(), "", "", "", pubId);

                    //Subscription CPA
                    if (null != campaignUnique && !"".equalsIgnoreCase(campaignUnique)) {
                        cmpg = UmeTempCmsCache.campaignMap.get(campaignUnique);

                        if (cmpg != null) {
                            if (cmpg.getSrc().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("subscription")) {
                                // 2016.01.13 - AS - Removed commented code, check repo history if needed
                                int insertedRows = cpaloggerdao.insertIntoCpaLogging(user.getParsedMobile(), campaignUnique, club.getUnique(), 10, network, cmpg.getSrc());
                            }
                        }
                    }

                    String toAddresse = user.getParsedMobile();

                    msg = club.getWebConfirmation();
                    String dateofweek = sdfWeekY.format(new Date()) + "";

                    java.util.List<UmeClubMessages> billableMessages = umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Billable");

                    if (billableMessages != null && !billableMessages.isEmpty() && billableMessages.size() > 0) {
                        int counter = 1;
                        String reqType = "firstbillable";

                        for (int i = 0; i < billableMessages.size(); i++) {

                            msg = billableMessages.get(i).getaMessage().trim();
                            transactionId = Misc.generateUniqueIntegerId() + "-" + club.getUnique();
                            if (counter == 1) {
                                reqType = "firstbillable";
                            }
                            if (counter == 2) {
                                reqType = "secondbillable";
                            }
//                            if (counter == 3) {
//                                reqType = "thirdbillable";
//                            }

                            if (sendPremiumSMS(toAddresse, billednetwork, club.getUnique(), msg, shortcode, transactionId, club, umesmsdao, quizsmsdao, umequizdao, reqType, "txtmo.jsp", dateofweek, false, subscriptionId)) {
                                counter++;
                            }
                        } //END Billable message Loop
                    } //END Billable Message size >0 

                } //billingPlan ==null

            } //End else for users replying message

            // ======================== Subscription Handling END ============================
        } // END if not Exist
        System.out.println("****** ipx: Debug MO END ******");
    }

    public boolean sendSMS(String[] msisdn, String serviceId, String msgText, String shortCode, String transactionId, MobileClub club, UmeSmsDao umesmsdao, QuizSmsDao quizsmsdao) {

        boolean success = true;
        return success;
    }

    public boolean sendPremiumSMS(String msisdn, String network, String serviceId, String msgText, String shortCode, String transactionId, MobileClub club, UmeSmsDao umesmsdao, QuizSmsDao quizsmsdao, UmeQuizDao umequizdao, String reqType, String sendFrom, String dateofweek, boolean wappush, String subscriptionId) {
        boolean success = false;
        String id = Misc.generateUniqueId() + "-" + club.getUnique();
        String cc = "ume";

        UmeClubDetails userclubdetails = null;
        userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        try {
            cc = userclubdetails.getCompanyCode();
        } catch (Exception e) {
            cc = "ume";
        }

        boolean isSuccess = false;
//        double tariffClass = 100;
        
        //o   For H3G we start with 4.88euro per week (2.44 x2 charging events)
        //For TIM we start with 4.04euro per week (2.04 x2 charging events)
//        double tariffClass = 204;
        double tariffClass = 252;

        if(network.equals("wind") || network.equals("tim")){
            tariffClass = 252;
        }
        
        IpxSmsSubmit sms = new IpxSmsSubmit();
        IpxSmsConnection smsConnection = new IpxSmsConnection();
        sms.setFromNumber("4882000");
        sms.setToNumber(msisdn);

        sms.setReferenceID(subscriptionId);
        sms.setSmsAccount("ipx");
        sms.setUsername(club.getOtpSoneraId());
        sms.setPassword(club.getOtpTelefiId());
        
        
        msgText = messageReplacement.replaceMessagePlaceholders(msgText, msisdn, club.getUnique());

        sms.setMsgBody(msgText);
        sms.setCurrencyCode("EUR");
        sms.setTariffClass(tariffClass);
        
        sms.setServiceMetaData(club.getOtpServiceName());

        
//        sms.setServiceMetaData("");

        if (network.equals("tim")) {
            sms.setServiceCategory("VARIE");
        }
        String resp = smsConnection.doRequest(sms, 1);
        boolean isSuccessful = resp.contains("successful");
        
        //TODO send SMS premium IPX
        if (isSuccessful) {
            logQuiz(msisdn, club.getUnique(), transactionId);
            logSMS(id, id, shortCode, msisdn, "Premium", msgText, club, sendFrom, reqType, network, dateofweek, cc);
        }

        return success;
    }

    public void logQuiz(String msisdn, String clubUnique, String transactionId) {
        QuizUserAttempted quizUserAttempted = new QuizUserAttempted();
        quizUserAttempted.setaParsedMsisdn(msisdn);
        quizUserAttempted.setClubUnique(clubUnique);
        quizUserAttempted.setType("Entry Confirmation");
        quizUserAttempted.setStatus("false");
        quizUserAttempted.setaUnique(transactionId);
        quizUserAttempted.setaCreated(new java.sql.Timestamp(System.currentTimeMillis()));
        umequizdao.saveQuizUserAttempted(quizUserAttempted);
    }

    public void logSMS(String unique, String id, String shortCode, String msisdn, String msgType, String msgText,
            MobileClub club, String sendFrom, String reqType, String network, String dateofweek, String cc) {
        SdcSmsSubmit sdcSmsSubmit = new SdcSmsSubmit();
        sdcSmsSubmit.setUnique(unique);
        sdcSmsSubmit.setLogUnique(id);
        sdcSmsSubmit.setFromNumber(shortCode);
        sdcSmsSubmit.setToNumber(msisdn);
        sdcSmsSubmit.setMsgType(msgType);
        sdcSmsSubmit.setMsgBody(msgText);
        sdcSmsSubmit.setCost(club.getPrice());
        sdcSmsSubmit.setMsgCode1(sendFrom);
        sdcSmsSubmit.setReqType(reqType);
        sdcSmsSubmit.setNetworkCode(network);
        sdcSmsSubmit.setMsgUdh(dateofweek);
        sdcSmsSubmit.setClubUnique(club.getUnique());
        sdcSmsSubmit.setStatus("SENT");
        sdcSmsSubmit.setSmsAccount(cc);

        if (msgType.equals("Premium")) {
            sdcSmsSubmit.setRefMessageUnique("BILLING");
        }

        umesmsdao.log(sdcSmsSubmit);
        quizsmsdao.log(sdcSmsSubmit);
    }

//    public CreateSubscriptionResponse createSubscriptionId(String transactionId, String msisdn,
    public String createSubscriptionId(String transactionId, String msisdn,
            String operator, String ServiceMetaData, String clubUnique, String clubIPXUserName, String clubIPXPassword,
            MobileClub club) {
//        String SUBSCRIBE_REQUEST_URL = "";
        String SUBSCRIBE_REQUEST_URL = "http://europe.ipx.com/api/services2/SubscriptionApi40?wsdl";
        CreateSubscriptionResponse aCreateSessionResponse = null;
        System.out.println("IPX debug other operator" + msisdn + "  " + operator + " -- " + transactionId);
        SubscriptionApiPort subscriptionApi;
        try {
            subscriptionApi = new SubscriptionApiServiceLocator()
                    .getSubscriptionApi40(new URL(SUBSCRIBE_REQUEST_URL));
            ((SubscriptionApiBindingStub) subscriptionApi).setTimeout(4 * 60 * 1000);

            CreateSubscriptionRequest aCreateRequest = new CreateSubscriptionRequest();
            aCreateRequest.setCorrelationId(transactionId);
            aCreateRequest.setConsumerId(msisdn);
            aCreateRequest.setReferenceId(transactionId);
            //TODO do next club.getWebConfirmation();
            aCreateRequest.setConsumerId(msisdn);
            aCreateRequest.setServiceName("Ume Ipx Billing");
            aCreateRequest.setServiceCategory("#NULL#");

//            aCreateRequest.setServiceName(club.getClubName());
            aCreateRequest.setInitialCharge("#NULL#");
            aCreateRequest.setBillingMode("#NULL#");
            aCreateRequest.setServiceId("#NULL#");

//    aCreateRequest.setServiceName("Erotixxxo Mas Info: 900809734");
            aCreateRequest.setServiceCategory("#NULL#");

            if (operator.equals("tim")) {
                aCreateRequest.setServiceCategory("VIDEO");
                aCreateRequest.setServiceMetaData(ServiceMetaData);
            } else if (operator.equals("wind")) {
                aCreateRequest.setServiceMetaData(ServiceMetaData);
            } else if (operator.equals("three")) {
                aCreateRequest.setServiceMetaData(ServiceMetaData);
            } else {
                aCreateRequest.setServiceMetaData("#NULL#");
            }
            aCreateRequest.setCampaignName("#NULL#");
            aCreateRequest.setUsername(clubIPXUserName);
            aCreateRequest.setPassword(clubIPXPassword);

            aCreateRequest.setDuration(-1); // unlimited subscription
            aCreateRequest.setEventCount(-1); // unlimited charged subscription

            aCreateRequest.setFrequencyInterval(3);
            
//            aCreateRequest.setTariffClass("EUR250");
            aCreateRequest.setTariffClass("EUR252");

            if (operator.equals("wind") || operator.equals("tim")){
                aCreateRequest.setTariffClass("EUR252");                          
            }
            
            aCreateRequest.setFrequencyCount(2);
            aCreateRequest.setVAT(-1);

            aCreateSessionResponse = subscriptionApi.createSubscription(aCreateRequest);
            
            System.out.println("IPX subscription Request: " + aCreateRequest.toString());
            System.out.println("IPX subscription Request: " + aCreateRequest.getConsumerId() + "--"
            + "getReferenceId: " + aCreateRequest.getReferenceId() + "--"
            + "getTariffClass: " + aCreateRequest.getTariffClass() + "--"
            + "getUsername: " + aCreateRequest.getUsername()+ "--"
            + "getPassword: " + aCreateRequest.getPassword()+ "--"
            + "getServiceName: " + aCreateRequest.getServiceName()+ "--"
            + "getServiceCategory: " + aCreateRequest.getServiceCategory()+ "--"
            + "getServiceMetaData: " + aCreateRequest.getServiceMetaData()+ "--"
            );

            
            System.out.println("IPX Subscription creation: " + MiscDate.now24sql() + "--" + 
                    msisdn + "--" + operator + "--" + transactionId + "--" +ServiceMetaData + "--" + 
                    aCreateSessionResponse.getResponseMessage() + "--" + aCreateSessionResponse.getResponseCode() + "--" 
                    + aCreateSessionResponse.getSubscriptionId() + "--" + aCreateSessionResponse.getSubscriptionStatusMessage());            
        } catch (Exception ex) {
            System.out.println("ITMO Create Subscription Exception: " + ex);
        }
        return aCreateSessionResponse.getSubscriptionId();
    }

    private String getClubId(String longString, String separateby) {
        int index = longString.indexOf(separateby);
        if (index > 0) {
            return longString.substring(index + separateby.length());
        } else {
            return "6119598063441KDS";
        }
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
