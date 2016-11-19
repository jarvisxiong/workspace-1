package ume.pareva.ire;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Random;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubBillingTry;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.CpaVisitLogDao;
import ume.pareva.dao.MobileBillingDao;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.MobileNetworksDao;
import ume.pareva.dao.QuizSmsDao;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeLanguagePropertyDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeQuizDao;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.CpaVisitLog;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.MobileClubBillingSuccesses;
import ume.pareva.pojo.QuizUserAttempted;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.userservice.RegionalDate;
import ume.pareva.util.ValidationUtil;
import ume.pareva.userservice.StopUser;

/**
 *
 * @author madan
 */
public class IEDr extends HttpServlet {
    
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
    MobileBillingDao mobilebillingdao;
    
    @Autowired
    CpaVisitLogDao cpavisitlogdao;
    
    @Autowired
    QuizSmsDao quizsmsdao;
    
    @Autowired
    MobileNetworksDao mobilenetwork;
    
    @Autowired
    CpaLoggerDao cpaloggerdao;
    
    @Autowired
    StopUser stopuser;
    
    @Autowired
    RegionalDate regionaldate;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //
    private final Logger logger = LogManager.getLogger(IEDr.class.getName());
    
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
     * @throws IOException if an I/O error occurs action	mp_report id	the unique
     * message id number number	the originating telephone number report	the
     * delivery report (e.g. delivered/acked/failed)
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        Calendar deliverytime = new GregorianCalendar();
        String serviceId = "6119598063441KDS";
        String defClubDomain = "5510024809921CDS";
        String freeCostId = "0";
        String deliveryReceipt = "11";
        String transactionId = Misc.generateUniqueIntegerId() + "";
        String typeId = "2";
        boolean cpaqueue = true;
        
        Enumeration parameterList = request.getParameterNames();
        while (parameterList.hasMoreElements()) {
            String sName = parameterList.nextElement().toString();
            System.out.println("txtnation: callback IEDR " + sName + ":" + request.getParameter(sName));
        }
        
        String id = request.getParameter("id");
        String report = request.getParameter("report"); // This can be DELIVERED, ACKNOWLEDGED, FAILED
        String messageid = request.getParameter("message_id");
        String msisdn = request.getParameter("number");
        
        if (id.contains("-")) {
            serviceId = getClubId(id, "-").trim();
            System.out.println("txtnation INSIDE - condition SERVICE ID IS " + serviceId);
        }
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(serviceId);
        UmeClubDetails clubdetail = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        
        ThreadContext.put("ROUTINGKEY", club.getRegion().toUpperCase().trim());
        ThreadContext.put("EXTRA", "DLR");
        
        SdcSmsSubmit smsrecord = quizsmsdao.getSmsMsgLog(id);
        if (smsrecord != null) {
            if (!smsrecord.getStatus().equalsIgnoreCase("DELIVERED")) {
                
                quizsmsdao.updateResponse(id, report, sdf.format(deliverytime.getTime()), report, "");
                umesmsdao.updateResponse(id, report, sdf.format(deliverytime.getTime()), report, "");
                
                if (report.equals("DELIVERED")) {
                    MobileClubBillingPlan mobileClubBillingPlan = null;
                    String successResponse = "00";
                    
                    if (smsrecord.getMsgType().equalsIgnoreCase("premium")) {
                        try {
                            Random rnd = new Random();
                            Thread.sleep(1000 + rnd.nextInt(2000));
                        } catch (Exception e) {
                            //catch sleep exception                                
                        }

                        //====================== USER- CLUBUSER - BILLING PLANS =============================== 
                        UmeUser user = null;
                        SdcMobileClubUser clubUser = null;
                        String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", defClubDomain);
                        if (!userUnique.equals("")) {
                            user = umeuserdao.getUser(msisdn);
                        }
                        
                        if (user != null) {
                            clubUser = user.getClubMap().get(club.getUnique());
                            
                            if (clubUser == null) {
                                clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                            }
                            if (clubUser != null) {
                                mobileClubBillingPlan = billingplandao.getActiveBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());
                                //System.out.println("IEBillingSuccess  BillingPlan is " + mobileClubBillingPlan.toString());
                            }
                        }  //====================== USER- CLUBUSER - BILLING PLANS =============================== 

                        boolean cpaLog = false;
                        if (smsrecord.getLogUnique().equals(id) && smsrecord.getMsgType().equalsIgnoreCase("premium")) {  //Billable 
                            //===================================================================

                            if (user != null && clubUser != null) {
                            //boolean cpaLog = false;
                                //BillingTry Logging for first success

                                Date billdate = regionaldate.getRegionalDate(club.getRegion(), new Date());
                                if (DateUtils.isSameDay(billdate, clubUser.getSubscribed())) {
                                    successResponse = "003";
                                    cpaLog = true;
                                }
                                
                                String biloggednetwork = mobilenetwork.getMobileNetwork(club.getRegion().toUpperCase(), clubUser.getNetworkCode());
                                MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                                mobileClubBillingTry.setUnique(id);
                                mobileClubBillingTry.setLogUnique(id);
                                mobileClubBillingTry.setAggregator("TXT");
                                mobileClubBillingTry.setClubUnique(club.getUnique());
                                mobileClubBillingTry.setCreated(new Date());
                                mobileClubBillingTry.setNetworkCode(biloggednetwork.toLowerCase());
                                mobileClubBillingTry.setParsedMsisdn(msisdn);
                                mobileClubBillingTry.setRegionCode(club.getRegion().toUpperCase());
                                mobileClubBillingTry.setResponseCode(successResponse);
                                mobileClubBillingTry.setResponseDesc("successful");
                                mobileClubBillingTry.setResponseRef(transactionId);
                                mobileClubBillingTry.setStatus(report.toLowerCase());
                                mobileClubBillingTry.setTariffClass(club.getPrice());
                                mobileClubBillingTry.setTransactionId(transactionId);
                                mobileClubBillingTry.setCampaign(clubUser.getCampaign());

                                //End BillingTry Logging
                                if (mobileClubBillingPlan != null) {
                                    Calendar c = Calendar.getInstance();
                                    mobileClubBillingPlan.setPreviousSuccess(mobileClubBillingPlan.getLastSuccess());
                                    mobileClubBillingPlan.setLastSuccess(c.getTime());
                                    //mobileClubBillingPlan.setLastPaid(c.getTime());
                                    mobileClubBillingPlan.setPushCount(mobileClubBillingPlan.getPushCount() + 1);
                                    mobileClubBillingPlan.setPartialsPaid(mobileClubBillingPlan.getPartialsPaid() + 1);

                                    //Date nextPush = new Date();
                                    if (mobileClubBillingPlan.getServiceDateBillsRemaining() > 0) {
                                        mobileClubBillingPlan.setServiceDateBillsRemaining(mobileClubBillingPlan.getServiceDateBillsRemaining() - 1);
                                    }
                                    
                                    //=====================commented on 2016-10-05 ============================ 
                                    if (mobileClubBillingPlan.getLastPush().before(mobileClubBillingPlan.getSubscribed())) {
                                        // this is done because it is set by default to 1970-01
                                        logger.info("LASTPUSH has Value previous than Subscription Date NOT POSSIBLE !! PLAN {} - {} - {} - {}",mobileClubBillingPlan.getParsedMobile(),mobileClubBillingPlan.getClubUnique(),mobileClubBillingPlan.getSubscribed(),mobileClubBillingPlan.getLastPush());
                                        mobileClubBillingPlan.setLastPush(new Date());
                                    }
                                    
                                    //=====================commented on 2016-10-05 ============================
                                    
                                    Date nextPush = DateUtils.addDays(mobileClubBillingPlan.getLastPush(), 7);
                                    nextPush = DateUtils.setHours(nextPush, 9);
                                    nextPush = DateUtils.truncate(nextPush, Calendar.HOUR_OF_DAY);
                                    
                                    mobileClubBillingPlan.setNextPush(nextPush);
                                    
                                    //mobileClubBillingPlan.setLastSuccess(new Date());
                                    
                                    if (clubdetail.getBillingType().equalsIgnoreCase("Adhoc")) {
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(new Date());
                                        cal.add(Calendar.HOUR, 3);
                                        
                                        cal.set(Calendar.MINUTE, 0);
                                        cal.set(Calendar.SECOND, 0);
                                        
                                        mobileClubBillingPlan.setNextPush(cal.getTime());
//                                if(mobileClubBillingPlan.getServiceDateBillsRemaining()>0D){
//                                    mobileClubBillingPlan.setServiceDateBillsRemaining(mobileClubBillingPlan.getServiceDateBillsRemaining()-1);
//                                    }

                                        if ((mobileClubBillingPlan.getPartialsRequired() - mobileClubBillingPlan.getPartialsPaid()) <= 2) {
                                            cpaqueue = true;
                                        } else {
                                            cpaqueue = false;
                                        }
                                        System.out.println("IEMODR == ADHOC BILLING UPDATE  to " + mobileClubBillingPlan.getParsedMobile() + " --- " + mobileClubBillingPlan.getNextPush());
                                        
                                    } else {
                                        /*
                                         @Date- 2016-04-05
                                         @Author Madan
                                         Trying weeks of subscription and their success
                                         */
                                        //=========== START WEEK Calculation for User Total Bill ====================
                                        try {
                                            Date subscribedDate = clubUser.getSubscribed();
                                            Date today = new Date();
                                            DateTime dateTime1 = new DateTime(subscribedDate);
                                            DateTime dateTime2 = new DateTime(today);
                                            int weeks = 1 + Weeks.weeksBetween(dateTime1, dateTime2).getWeeks();
                                            
                                            int totalSuccess = 1 + billingplandao.getTotalSuccessAfterEachSubscription(clubUser.getParsedMobile(), clubUser.getClubUnique(), clubUser.getSubscribed());
                                            int pendingTickets = 0;
                                            int expectedSuccesses = weeks * clubdetail.getFrequency(); //We need to use Frequency here... 

                                            logger.info("PLAN {}-{} SUBSCRIPTION {} WEEKS {} TOTAL SUCCESSES {} - EXPECTED SUCCESSES {} ",
                                                    clubUser.getParsedMobile(), clubUser.getClubUnique(), sdf.format(clubUser.getSubscribed()),
                                                    weeks, totalSuccess, expectedSuccesses);
                                            mobileClubBillingPlan.setParam2("");
                                            if (totalSuccess == expectedSuccesses) { // User is upto-date 
                                                mobileClubBillingPlan.setServiceDateBillsRemaining(clubdetail.getFrequency() * 1D);
                                                mobileClubBillingPlan.setLastPaid(new Date());
                                                mobileClubBillingPlan.setNextPush(nextPush);
                                                
                                                int days = (int) Math.ceil((double) totalSuccess / clubdetail.getFrequency()) * club.getPeriod();
                                                Date nextPushBasedOnSubscription = DateUtils.addDays(clubUser.getSubscribed(), days);
                                                nextPushBasedOnSubscription = DateUtils.setHours(nextPushBasedOnSubscription, 9);
                                                nextPushBasedOnSubscription = DateUtils.truncate(nextPushBasedOnSubscription, Calendar.HOUR_OF_DAY);
                                                if (nextPushBasedOnSubscription.after(nextPush)) {
                                                    mobileClubBillingPlan.setNextPush(nextPushBasedOnSubscription);
                                                }
                                                mobileClubBillingPlan.setParam2("UPTODATE"+ totalSuccess + " EXP"+expectedSuccesses);
                                                logger.info("UP TO DATE, clubuser SUBSCRIBED {} DAYS {} NEXTPUSHBASEDONSUBS {} CALCULATED NEXTPUSH {} -- NEXTPUSH SET TO {}",
                                                        sdf.format(clubUser.getSubscribed()),days, sdf.format(nextPushBasedOnSubscription),
                                                        sdf.format(nextPush), sdf.format(mobileClubBillingPlan.getNextPush()));
                                                
                                            } else if (totalSuccess < expectedSuccesses) { //UnderBill Users 8  9
                                                pendingTickets = expectedSuccesses - totalSuccess; //1
                                                if (pendingTickets >= clubdetail.getFrequency()) {
                                                    mobileClubBillingPlan.setServiceDateBillsRemaining(1D + clubdetail.getFrequency());
                                                } else {
                                                    mobileClubBillingPlan.setServiceDateBillsRemaining(1D * pendingTickets);
                                                }
                                                mobileClubBillingPlan.setParam2("UNDER " + String.format("%1$4s", expectedSuccesses - totalSuccess ) + " TOTAL "+ totalSuccess + " EXP "+expectedSuccesses);
                                                nextPush = DateUtils.addDays(mobileClubBillingPlan.getLastPush(), 1);
                                                nextPush = DateUtils.setHours(nextPush, 9);
                                                nextPush = DateUtils.truncate(nextPush, Calendar.HOUR_OF_DAY);
                                            //nextPush = DateUtils.setHours(nextPush, 9);
                                                //nextPush = DateUtils.truncate(nextPush, Calendar.HOUR_OF_DAY);
                                                mobileClubBillingPlan.setNextPush(nextPush);
                                            } else if (totalSuccess > expectedSuccesses) { //overbill users Or can be ok due to week calculation.  10,  9
                                                mobileClubBillingPlan.setLastPaid(new Date());
                                                int weekdifference = 1 + ((totalSuccess - expectedSuccesses) / clubdetail.getFrequency()); //1/3
                                                logger.info("PLAN {}-{} SUBSCRIPTION {} OVERBILLED ADDING WEEKS {} ",clubUser.getParsedMobile(), clubUser.getClubUnique(), weekdifference);
                                                nextPush = DateUtils.addWeeks(mobileClubBillingPlan.getLastPush(), weekdifference); //Adding not to overbill continuously
                                                nextPush = DateUtils.addDays(nextPush, 1);
                                                nextPush = DateUtils.setHours(nextPush, 7);
                                                nextPush = DateUtils.truncate(nextPush, Calendar.HOUR_OF_DAY);                                               
                                                
                                                mobileClubBillingPlan.setNextPush(nextPush);
                                                mobileClubBillingPlan.setServiceDateBillsRemaining(1D);
                                                mobileClubBillingPlan.setParam2("OVER " + String.format("%1$4s", totalSuccess - expectedSuccesses) + " TOTAL "+ totalSuccess + " EXP "+expectedSuccesses);
                                            }
                                            logger.info("PLAN {}-{} SUBSCRIPTION {} NEXTPUSH {} ",
                                                    clubUser.getParsedMobile(), clubUser.getClubUnique(),
                                                    sdf.format(mobileClubBillingPlan.getSubscribed()), sdf.format(mobileClubBillingPlan.getNextPush()));
                                            
                                            
                                            if (!mobileClubBillingPlan.getExternalId().toLowerCase().startsWith("adhoc")) {
                                                Date billingEnd = DateUtils.addDays(nextPush, 7);
                                                mobileClubBillingPlan.setBillingEnd(billingEnd);
                                            }
                                            
                                            System.out.println("weeks IE Billing -- " + clubUser.getParsedMobile() + "-- " + clubUser.getClubUnique() + "-- " + clubUser.getSubscribed() + "-- no. of weeks " + weeks + " noofsuccess " + totalSuccess
                                                    + " successfrequency " + expectedSuccesses + " pending tickets " + pendingTickets + " ------- ");
                                            
                                        } catch (Exception e) {
                                        }

                                        //=============End Calculating the User's Week of billings =================================
                                    }
                                    
                                    umemobileclubuserdao.updateBillingRenew(clubUser.getUserUnique(), club.getUnique());
                                    billingplandao.update(mobileClubBillingPlan);
                                    MobileClubBillingSuccesses mobileClubBillingSuccesses = new MobileClubBillingSuccesses(mobileClubBillingPlan, mobileClubBillingTry);
                                    
                                    ThreadContext.put("EXTRA", "BILL");
                                    logger.info("SUCCESSFUL id {} report {} subscription ", id, report, clubUser);
                                    logger.info("SUCCESSFUL id{} report {}  BillingPlans ", id, report, mobileClubBillingPlan);
                                    ThreadContext.put("EXTRA", "DLR");
                                    
                                    try {
                                        mobilebillingdao.insertBillingSuccess(mobileClubBillingSuccesses);
                                        mobilebillingdao.insertBillingTry(mobileClubBillingTry);
                                        
                                    } catch (Exception e) {
                                        ThreadContext.put("EXTRA", "ERROR");
                                        logger.error("WARNING DLR Error id {} msisdn {}", id, msisdn, e.getMessage());
                                        logger.error("ERROR DLR error id{} msisdn {} ", id, msisdn, e);
                                        ThreadContext.put("EXTRA", "DLR");
                                    }
                                    
                                }

//                        QuizUserAttempted quizUserAttempted = new QuizUserAttempted();
//                        quizUserAttempted.setaParsedMsisdn(msisdn);
//                        quizUserAttempted.setClubUnique(club.getUnique());
//                        quizUserAttempted.setType("Entry Confirmation");
//                        quizUserAttempted.setStatus("true");
//                        quizUserAttempted.setaUnique(String.valueOf(transactionId));
//                        quizUserAttempted.setaCreated(new Date());
//                        umequizdao.saveQuizUserAttempted(quizUserAttempted);
                            } //END thirdbillable user!=null

                            //=====================================================================
                        } //End of third billable 

                        CpaVisitLog visitorLog = cpavisitlogdao.getDetails(msisdn, clubUser.getCampaign());
                        if (visitorLog != null && cpaqueue) {
                            
                            String campaignId = visitorLog.getaCampaignId();
                            if (null != campaignId && !"".equals(campaignId)) {
                                MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
                                if (cmpg != null) {
                                    if (cmpg.getSrc().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("billing")) {
                                        // 2016.01.13 - AS - Removed commented code, check repo history if needed
                                        if (cpaLog) {
                                            // 2016.01.13 - AS - Removed commented code, check repo history if needed
                                            int insertedRows = cpaloggerdao.insertIntoCpaLogging(msisdn, campaignId, club.getUnique(), 10, clubUser.getNetworkCode(), cmpg.getSrc());
                                            
                                            ThreadContext.put("EXTRA", "CPA");
                                            logger.info("DLR msisdn {} campaign {} {} club {} network{} ", msisdn, campaignId, cmpg.getSrc(), club.getUnique(), clubUser.getNetworkCode());
                                            ThreadContext.put("EXTRA", "DLR");
                                        }
                                        
                                    }
                                }
                            }
                            
                        }
                        
                    } //End smsrecord!=null

                } //end if report equals delivered
                else { //FOR ALL FAILURE 
                    //SdcSmsSubmit smsrecord = quizsmsdao.getSmsMsgLog(id);
                    if (smsrecord.getMsgType().equalsIgnoreCase("premium")) {
                        UmeUser user = null;
                        MobileClubBillingPlan mobileClubBillingPlan = null;
                        SdcMobileClubUser clubUser = null;
                        String userUnique = "";
                        try {
                            userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", defClubDomain);
                        } catch (Exception e) {
                            System.out.println("IE Exception for at line 365 EngageDR " + msisdn);
                            e.printStackTrace();
                        }
                        if (!userUnique.equals("")) {
                            user = umeuserdao.getUser(msisdn);
                        }
                        
                        if (user != null) {
                            clubUser = user.getClubMap().get(club.getUnique());
                            
                            if (clubUser == null) {
                                clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                                
                            }
                        }
                        if (clubUser != null) {
                            mobileClubBillingPlan = billingplandao.getActiveBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());
                        }
                        String biloggednetwork = "unknown";
                        try {
                            biloggednetwork = mobilenetwork.getMobileNetwork(club.getRegion().toUpperCase(), clubUser.getNetworkCode());
                        } catch (Exception e) {
                            biloggednetwork = "unknown";
                        }
                        
                        if (report.equals("REJECTED")) {// || report.equals("INVALID_MSISDN")){
                            //DEACtivate user as we can't bill them 
                            if (clubUser != null && clubUser.getActive() == 1) {
                                // 2016-05-10 Alex Sanchez if (smsrecord != null) {
                                MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                                mobileClubBillingTry.setUnique(id);
                                mobileClubBillingTry.setLogUnique(id);
                                mobileClubBillingTry.setAggregator("TXT");
                                mobileClubBillingTry.setClubUnique(club.getUnique());
                                mobileClubBillingTry.setCreated(new Date());
                                mobileClubBillingTry.setNetworkCode(biloggednetwork.toLowerCase());
                                mobileClubBillingTry.setParsedMsisdn(msisdn);
                                mobileClubBillingTry.setRegionCode(club.getRegion().toUpperCase());
                                mobileClubBillingTry.setResponseCode("99");
                                mobileClubBillingTry.setResponseDesc("error");
                                mobileClubBillingTry.setResponseRef(transactionId);
                                mobileClubBillingTry.setStatus(report.toLowerCase());
                                mobileClubBillingTry.setTransactionId(transactionId);
                                mobileClubBillingTry.setCampaign(clubUser.getCampaign());
                                mobileClubBillingTry.setTariffClass(club.getPrice());
                                mobilebillingdao.insertBillingTry(mobileClubBillingTry);

                            //stopuser.stopSingleSubscription(msisdn, club.getUnique(), null, null);
                        /*
                                 SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                 String unSubscribed = sdf2.format(new Date());
                                 clubUser.setActive(0);
                                 clubUser.setUnsubscribed(SdcMiscDate.parseSqlDateString(unSubscribed));
                                 umemobileclubuserdao.saveItem(clubUser);

                                 String stop = "STOP";
                                 try {
                                 if (DateUtils.isSameDay(clubUser.getSubscribed(), clubUser.getUnsubscribed())) {
                                 stop = "STOPFD";
                                 }
                                 campaigndao.log("iedr", clubUser.getLandingpage(), clubUser.getParsedMobile(), clubUser.getParsedMobile(), null, null, clubUser.getCampaign(), clubUser.getClubUnique(), stop, 0, null, null, biloggednetwork.toLowerCase());
                                 } catch (Exception e) {
                                 System.out.println("Exception EngageDr while campaignlog for stops " + e);
                                 e.printStackTrace();
                                 }
                                 //umemobileclubuserdao.disable(clubUser.getUnique());
                                 if (mobileClubBillingPlan != null) {
                                 billingplandao.disableBillingPlan(msisdn, club.getUnique());

                                 }
                                 */
                                // 2016-05-10 Alex Sanchez } //end smsrecord!=null 
                            } // If ClubUser is active 
                        } //end rejected or invalid msisdn
                        else if (report.toUpperCase().startsWith("ACKNOWLEDGED") || report.toUpperCase().startsWith("ACCEPTED")) {
                        //Don't do anything here as nextpush will be 7 days set from billing daemon
                            //This is treated as the msg might have been delivered !! We will receive
                            //notification of Delivered later on. 

                            if (clubdetail.getBillingType().equalsIgnoreCase("Adhoc") && mobileClubBillingPlan != null) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(new Date());
                                cal.add(Calendar.HOUR, 3 * 24);
                                
                                cal.set(Calendar.MINUTE, 0);
                                cal.set(Calendar.SECOND, 0);
                                
                                mobileClubBillingPlan.setNextPush(cal.getTime());
                                mobileClubBillingPlan.setPushCount(mobileClubBillingPlan.getPushCount() + 1);
                                billingplandao.update(mobileClubBillingPlan);
                            }
                            
                        } else { //For all other 
                            //NO_CREDIT, FAILED,VALIDITY_EXPIRED,UNKNOWN,OPERATOR_ERROR

                            //DATETIME SCHEDULER FOR FAILURE 
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(new Date());
                            
                            if (report.toUpperCase().equalsIgnoreCase("VALIDITY_EXPIRED")
                                    && clubUser != null && clubUser.getNetworkCode().toUpperCase().contains("THREE")) {
                                
                                cal.add(Calendar.DATE, 7);
                                cal.set(Calendar.HOUR_OF_DAY, 9);
                            } else {
                                
                                if (clubdetail.getPaymentType().equalsIgnoreCase("Adhoc")) {
                                    cal.set(Calendar.HOUR_OF_DAY, 21);
                                } else {
                                    cal.add(Calendar.HOUR, 3);
                                }
                                //cal.add(Calendar.HOUR, 72);

                                if (cal.get(Calendar.HOUR_OF_DAY) > 18) {
                                    cal.add(Calendar.DATE, 1);
                                    cal.set(Calendar.HOUR_OF_DAY, 9);
                                } else if (cal.get(Calendar.HOUR_OF_DAY) > 12) {
                                    cal.set(Calendar.HOUR_OF_DAY, 18);
                                } else if (cal.get(Calendar.HOUR_OF_DAY) > 9) {
                                    cal.set(Calendar.HOUR_OF_DAY, 12);
                                } else {
                                    cal.set(Calendar.HOUR_OF_DAY, 9);
                                }
                            }
                            
                            if (report.toUpperCase().equalsIgnoreCase("NO_CREDIT") && clubUser != null
                                    && club.getRegion().equals("AU") && clubUser.getNetworkCode().toUpperCase().startsWith("TELSTRA")) {
                                cal.add(Calendar.DATE, 3);
                            }
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            
                            MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                            mobileClubBillingTry.setUnique(id);
                            mobileClubBillingTry.setLogUnique(id);
                            mobileClubBillingTry.setAggregator("TXT");
                            mobileClubBillingTry.setClubUnique(club.getUnique());
                            mobileClubBillingTry.setCreated(new Date());
                            mobileClubBillingTry.setNetworkCode(biloggednetwork.toLowerCase());
                            mobileClubBillingTry.setParsedMsisdn(msisdn);
                            mobileClubBillingTry.setRegionCode(club.getRegion().toUpperCase());
                            mobileClubBillingTry.setResponseCode("51");
                            mobileClubBillingTry.setResponseDesc("Insufficient Funds");
                            mobileClubBillingTry.setResponseRef(transactionId);
                            mobileClubBillingTry.setStatus(report.toLowerCase());
                            mobileClubBillingTry.setTransactionId(transactionId);
                            mobileClubBillingTry.setCampaign(clubUser.getCampaign());
                            mobileClubBillingTry.setTariffClass(club.getPrice());
                            mobilebillingdao.insertBillingTry(mobileClubBillingTry);
                            
                            if (mobileClubBillingPlan != null) {
                            //Calendar cal = Calendar.getInstance();
                                //cal.setTime(new Date());
                                //mobileClubBillingPlan.setLastPush(cal.getTime());
                                mobileClubBillingPlan.setNextPush(cal.getTime());
                                mobileClubBillingPlan.setPushCount(mobileClubBillingPlan.getPushCount() + 1);
                                //cal.add(Calendar.HOUR_OF_DAY, 12);
                                System.out.println("IEMODR == FAILED BILLING UPDATE  to " + mobileClubBillingPlan.getParsedMobile() + " --- " + mobileClubBillingPlan.getNextPush());
                                billingplandao.update(mobileClubBillingPlan);//, cal.getTime());
                            }
                            
                        } //end for all the failures

                        ThreadContext.put("EXTRA", "BILL");
                        logger.info("FAILURE id {} report {} subscription ", id, report, clubUser);
                        logger.info("FAILURE id {} report {}  BillingPlans ", id, report, mobileClubBillingPlan);
                        ThreadContext.put("EXTRA", "DLR");
                    } //END if smsrecord!=null or Premium
                } // end ELSE 
            } else {
                logger.info("RECORD {} ALREADY DELIVERED NOT UPDATING -- MSISDN {} --STATUS SENT {}", id, smsrecord.getToNumber(),report);
            }
        } else {
            logger.info("SMSMSGRECORD {} IS NULL", id);
        }
    }
    
    private String getClubId(String longString, String separateby) {
        int index = longString.indexOf(separateby);
        if (index > 0) {
            return longString.substring(index + separateby.length());
        } else {
            return "6119598063441KDS"; //If nothing is identified, take this as a default one. 
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
