/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.it;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.time.DateUtils;
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
import ume.pareva.dao.SdcMiscDate;
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
import ume.pareva.util.ValidationUtil;
import ume.pareva.userservice.StopUser;

/**
 *
 * @author trung
 */
@WebServlet(name = "ITDr", urlPatterns = {"/ITDr"})

/**
 *
 * @author madan
 */
public class ITDr extends HttpServlet {

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

    /*@Autowired
     ZACPA zacpalog;*/
    @Autowired
    QuizSmsDao quizsmsdao;

    @Autowired
    MobileNetworksDao mobilenetwork;

    @Autowired
    CpaLoggerDao cpaloggerdao;

    @Autowired
    StopUser stopuser;

    /*@Autowired
     PassiveVisitorDao passivevisitordao;*/
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        ThreadContext.put("ROUTINGKEY", "IT");
        ThreadContext.put("EXTRA", "DR");
        response.setContentType("text/html;charset=UTF-8");
        System.out.println("****** ipx: callback ITDR BEGIN ******");

        Enumeration parameterList = request.getParameterNames();
        while (parameterList.hasMoreElements()) {
            String sName = parameterList.nextElement().toString();
            System.out.println("ipx: callback ITDR " + sName + ":" + request.getParameter(sName));
        }

        System.out.println("****** ipx: callback ITDR END ******");

    }

    private void processBillingDR(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String report = request.getParameter("report"); // This can be DELIVERED, ACKNOWLEDGED, FAILED
        String messageid = request.getParameter("message_id");
        String msisdn = request.getParameter("number");

        Calendar deliverytime = new GregorianCalendar();
        String serviceId = "6119598063441KDS";
        String defClubDomain = "5510024809921CDS";
        String freeCostId = "0";
        String deliveryReceipt = "11";
        String transactionId = Misc.generateUniqueIntegerId() + "";
        String typeId = "2";

        if (id.contains("-")) {
            serviceId = getClubId(id, "-").trim();
            System.out.println("IPXDr INSIDE - condition SERVICE ID IS " + serviceId);
        }
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(serviceId);
        UmeClubDetails clubdetail = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());

        quizsmsdao.updateResponse(id, report, sdf.format(deliverytime.getTime()), report, "");
        umesmsdao.updateResponse(id, report, sdf.format(deliverytime.getTime()), report, "");

        if (report.equals("DELIVERED")) {
            MobileClubBillingPlan mobileClubBillingPlan = null;
            String successResponse = "00";

            SdcSmsSubmit smsrecord = quizsmsdao.getSmsMsgLog(id);
            if (smsrecord != null && smsrecord.getMsgType().equalsIgnoreCase("premium")) {

                //====================== USER- CLUBUSER - BILLING PLANS =============================== 
                UmeUser user = null;
                SdcMobileClubUser clubUser = null;
                String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", defClubDomain);
                if (!userUnique.equals("")) {
                    user = umeuserdao.getUser(msisdn);
                }
                try {
                    System.out.println("IPXDr ITDR getting userunique from msisdn " + user.toString());
                } catch (Exception e) {
                    System.out.println("IPXDr ITDR Exception at line 178 EngageDR for msisdn " + msisdn);
                }

                if (user != null) {
                    clubUser = user.getClubMap().get(club.getUnique());

                    if (clubUser == null) {
                        clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                    }
                    if (clubUser != null) {
                        mobileClubBillingPlan = billingplandao.getAnyStatusBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());
                        System.out.println("ITBillingSuccess  BillingPlan is " + mobileClubBillingPlan.toString());
                    }
                }  //====================== USER- CLUBUSER - BILLING PLANS =============================== 

                boolean cpaLog = false;
                if (smsrecord.getLogUnique().equals(id) && smsrecord.getMsgType().equalsIgnoreCase("premium")
                        //&& (smsrecord.getMsgCode1().equals("txtmo.jsp") || smsrecord.getMsgCode1().equals("billingITd1"))
                        && smsrecord.getReqType().equals("firstbillable")) { //first billable

                    System.out.println("IPXDr ITDR getting userunique from msisdn " + smsrecord.getMsgCode1());

                    if (user != null && clubUser != null) {
                        //System.out.println("IPXDr ITDR getting ClubUser from msisdn "+clubUser.toString());
                        //boolean cpaLog = false;
                        //BillingTry Logging for first success
                        if (DateUtils.isSameDay(new Date(), clubUser.getSubscribed())) {
                            successResponse = "003";
                            cpaLog = true;
                        }

                        String biloggednetwork = mobilenetwork.getMobileNetwork(club.getRegion().toUpperCase(), clubUser.getNetworkCode());
                        MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                        mobileClubBillingTry.setUnique(id);
                        mobileClubBillingTry.setLogUnique(id);
                        mobileClubBillingTry.setAggregator("IPX");
                        mobileClubBillingTry.setClubUnique(club.getUnique());
                        mobileClubBillingTry.setCreated(new Date());
                        mobileClubBillingTry.setNetworkCode(biloggednetwork.toLowerCase());
                        mobileClubBillingTry.setParsedMsisdn(msisdn);
                        mobileClubBillingTry.setRegionCode(club.getRegion().toUpperCase());
                        mobileClubBillingTry.setResponseCode(successResponse);
                        mobileClubBillingTry.setResponseDesc("successful");
                        mobileClubBillingTry.setResponseRef(transactionId);
                        mobileClubBillingTry.setStatus("success");
                        mobileClubBillingTry.setTariffClass(club.getPrice());
                        mobileClubBillingTry.setTransactionId(transactionId);
                        mobileClubBillingTry.setCampaign(clubUser.getCampaign());

                        //End BillingTry Logging
                        if (mobileClubBillingPlan != null) {
                            Calendar c = Calendar.getInstance();
                            mobileClubBillingPlan.setLastSuccess(c.getTime());
                            mobileClubBillingPlan.setLastPaid(c.getTime());
                            mobileClubBillingPlan.setPushCount(mobileClubBillingPlan.getPushCount() + 1);

                            //Date nextPush = new Date();
                            if (mobileClubBillingPlan.getLastPush().before(mobileClubBillingPlan.getSubscribed())) {
                                mobileClubBillingPlan.setLastPush(new Date());
                            }

                            Date nextPush = DateUtils.addDays(mobileClubBillingPlan.getLastPush(), 7);
                            nextPush = DateUtils.setHours(nextPush, 9);
                            nextPush = DateUtils.truncate(nextPush, Calendar.HOUR_OF_DAY);

                            mobileClubBillingPlan.setNextPush(nextPush);

                            mobileClubBillingPlan.setLastSuccess(new Date());
                            //mobileClubBillingPlan.setLastPaid(new Date());

                            if (mobileClubBillingPlan.getServiceDateBillsRemaining() > 0) {
                                mobileClubBillingPlan.setServiceDateBillsRemaining(mobileClubBillingPlan.getServiceDateBillsRemaining() - 1);
                            }

                            umemobileclubuserdao.updateBillingRenew(clubUser.getUserUnique(), club.getUnique());
                            billingplandao.update(mobileClubBillingPlan);
                            System.out.println("ITBillingSuccess  calling UP billing success in ITDR ");
                            MobileClubBillingSuccesses mobileClubBillingSuccesses = new MobileClubBillingSuccesses(mobileClubBillingPlan, mobileClubBillingTry);
                            System.out.println("ITBillingSuccess  Saving Billing Successes " + mobileClubBillingSuccesses.toString());

                            try {
                                mobilebillingdao.insertBillingSuccess(mobileClubBillingSuccesses);
                                mobilebillingdao.insertBillingTry(mobileClubBillingTry);
                            } catch (Exception e) {
                            }

                        }

                    } //End if user!=null
                } //end if first billable
                if (smsrecord.getLogUnique().equals(id) && smsrecord.getMsgType().equalsIgnoreCase("premium")
                        //&& (smsrecord.getMsgCode1().equals("txtdr.jsp") || smsrecord.getMsgCode1().equals("billingITd2"))
                        && smsrecord.getReqType().equals("secondbillable")) {

                    if (user != null && clubUser != null) {

                        //BillingTry Logging for first success
                        if (DateUtils.isSameDay(new Date(), clubUser.getSubscribed())) {
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
                        mobileClubBillingTry.setStatus("success");
                        mobileClubBillingTry.setTariffClass(club.getPrice());
                        mobileClubBillingTry.setTransactionId(transactionId);
                        mobileClubBillingTry.setCampaign(clubUser.getCampaign());

                        //End BillingTry Logging
                        if (mobileClubBillingPlan != null) {
                            Calendar c = Calendar.getInstance();
                            mobileClubBillingPlan.setLastSuccess(c.getTime());
                            mobileClubBillingPlan.setLastPaid(c.getTime());
                            mobileClubBillingPlan.setPushCount(mobileClubBillingPlan.getPushCount() + 1);
                            //Date nextPush = new Date();

                            if (mobileClubBillingPlan.getLastPush().before(mobileClubBillingPlan.getSubscribed())) {
                                mobileClubBillingPlan.setLastPush(new Date());
                            }

                            Date nextPush = DateUtils.addDays(mobileClubBillingPlan.getLastPush(), 7);
                            nextPush = DateUtils.setHours(nextPush, 9);
                            nextPush = DateUtils.truncate(nextPush, Calendar.HOUR_OF_DAY);

                            mobileClubBillingPlan.setNextPush(nextPush);

                            mobileClubBillingPlan.setLastSuccess(new Date());
                            //mobileClubBillingPlan.setLastPaid(new Date());

                            if (mobileClubBillingPlan.getServiceDateBillsRemaining() > 0) {
                                mobileClubBillingPlan.setServiceDateBillsRemaining(mobileClubBillingPlan.getServiceDateBillsRemaining() - 1);
                            }

                            umemobileclubuserdao.updateBillingRenew(clubUser.getUserUnique(), club.getUnique());

                            billingplandao.update(mobileClubBillingPlan);
                            MobileClubBillingSuccesses mobileClubBillingSuccesses = new MobileClubBillingSuccesses(mobileClubBillingPlan, mobileClubBillingTry);

                            try {
                                mobilebillingdao.insertBillingSuccess(mobileClubBillingSuccesses);
                                mobilebillingdao.insertBillingTry(mobileClubBillingTry);
                            } catch (Exception e) {
                            }

                        }
                    } //END SEcond bilable uesr!=null

                }  //END Second Billable     

                if (smsrecord.getLogUnique().equals(id) && smsrecord.getMsgType().equalsIgnoreCase("premium")
                        //&& (smsrecord.getMsgCode1().equals("txtdr.jsp") || smsrecord.getMsgCode1().equals("billingITd3"))
                        && smsrecord.getReqType().equals("thirdbillable")) {  //Third Billable 
                    //===================================================================

                    if (user != null && clubUser != null) {
                        //boolean cpaLog = false;
                        //BillingTry Logging for first success
                        if (DateUtils.isSameDay(new Date(), clubUser.getSubscribed())) {
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
                        mobileClubBillingTry.setStatus("success");
                        mobileClubBillingTry.setTariffClass(club.getPrice());
                        mobileClubBillingTry.setTransactionId(transactionId);
                        mobileClubBillingTry.setCampaign(clubUser.getCampaign());

                        //End BillingTry Logging
                        if (mobileClubBillingPlan != null) {
                            Calendar c = Calendar.getInstance();
                            mobileClubBillingPlan.setLastSuccess(c.getTime());
                            mobileClubBillingPlan.setLastPaid(c.getTime());
                            mobileClubBillingPlan.setPushCount(mobileClubBillingPlan.getPushCount() + 1);
                            //Date nextPush = new Date();

                            if (mobileClubBillingPlan.getLastPush().before(mobileClubBillingPlan.getSubscribed())) {
                                mobileClubBillingPlan.setLastPush(new Date());
                            }

                            if (mobileClubBillingPlan.getServiceDateBillsRemaining() > 0) {
                                mobileClubBillingPlan.setServiceDateBillsRemaining(mobileClubBillingPlan.getServiceDateBillsRemaining() - 1);
                            }

                            Date nextPush = DateUtils.addDays(mobileClubBillingPlan.getLastPush(), 7);
                            nextPush = DateUtils.setHours(nextPush, 9);
                            nextPush = DateUtils.truncate(nextPush, Calendar.HOUR_OF_DAY);

                            mobileClubBillingPlan.setNextPush(nextPush);
                            mobileClubBillingPlan.setLastSuccess(new Date());

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
                                int weeks = Weeks.weeksBetween(dateTime1, dateTime2).getWeeks();

                                int totalSuccess = billingplandao.getTotalSuccess(clubUser.getParsedMobile(), clubUser.getClubUnique());
                                int pendingTickets = 0;
                                int successfrequency = weeks * 3; //We need to use Frequency here... 
                                if (totalSuccess == successfrequency) // User is upto-date 
                                {

                                    mobileClubBillingPlan.setServiceDateBillsRemaining(0.0);
                                    mobileClubBillingPlan.setLastPaid(new Date());
                                } else if (totalSuccess < successfrequency) { //UnderBill Users

                                    pendingTickets = successfrequency - totalSuccess;
                                    if (pendingTickets >= 3) {
                                        mobileClubBillingPlan.setServiceDateBillsRemaining(3.0);
                                    } else if (pendingTickets < 3 && pendingTickets > 1) {
                                        mobileClubBillingPlan.setServiceDateBillsRemaining(2.0);

                                    } else {
                                        mobileClubBillingPlan.setServiceDateBillsRemaining(1.0);
                                    }

                                    nextPush = DateUtils.addDays(mobileClubBillingPlan.getLastPush(), 1);
                                    nextPush = DateUtils.setHours(nextPush, 9);
                                    nextPush = DateUtils.truncate(nextPush, Calendar.HOUR_OF_DAY);
                                    mobileClubBillingPlan.setNextPush(nextPush);
                                } else if (totalSuccess > successfrequency) { //overbill users Or can be ok due to week calculation. 
                                    mobileClubBillingPlan.setLastPaid(new Date());
                                    int weekdifference = (totalSuccess - successfrequency) / 3;
                                    nextPush = DateUtils.addWeeks(mobileClubBillingPlan.getLastPush(), weekdifference); //Adding not to overbill continuously
                                    mobileClubBillingPlan.setNextPush(nextPush);
                                }

                                System.out.println("weeks IT Billing -- " + clubUser.getParsedMobile() + "-- " + clubUser.getClubUnique() + "-- " + clubUser.getSubscribed() + "-- no. of weeks " + weeks + " noofsuccess " + totalSuccess
                                        + " successfrequency " + successfrequency + " pending tickets " + pendingTickets + " ------- ");

                            } catch (Exception e) {
                            }

                            //=============End Calculating the User's Week of billings =================================
                            umemobileclubuserdao.updateBillingRenew(clubUser.getUserUnique(), club.getUnique());
                            billingplandao.update(mobileClubBillingPlan);
                            MobileClubBillingSuccesses mobileClubBillingSuccesses = new MobileClubBillingSuccesses(mobileClubBillingPlan, mobileClubBillingTry);

                            try {
                                mobilebillingdao.insertBillingSuccess(mobileClubBillingSuccesses);
                                mobilebillingdao.insertBillingTry(mobileClubBillingTry);

                            } catch (Exception e) {
                            }

                        }

                        QuizUserAttempted quizUserAttempted = new QuizUserAttempted();
                        quizUserAttempted.setaParsedMsisdn(msisdn);
                        quizUserAttempted.setClubUnique(club.getUnique());
                        quizUserAttempted.setType("Entry Confirmation");
                        quizUserAttempted.setStatus("true");
                        quizUserAttempted.setaUnique(String.valueOf(transactionId));
                        quizUserAttempted.setaCreated(new Date());
                        umequizdao.saveQuizUserAttempted(quizUserAttempted);

                    } //END thirdbillable user!=null

                    //=====================================================================
                } //End of third billable 

                CpaVisitLog visitorLog = cpavisitlogdao.getDetails(msisdn, clubUser.getCampaign());
                if (visitorLog != null) {

                    String campaignId = visitorLog.getaCampaignId();
                    if (null != campaignId && !"".equals(campaignId)) {
                        MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
                        if (cmpg != null) {
                            if (cmpg.getSrc().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("billing")) {
                                // 2016.01.13 - AS - Removed commented code, check repo history if needed
                                if (cpaLog) {
                                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                                    int insertedRows = cpaloggerdao.insertIntoCpaLogging(msisdn, campaignId, club.getUnique(), 10, clubUser.getNetworkCode(), cmpg.getSrc());
                                }

                            }
                        }
                    }

                }

            } //End smsrecord!=null

        } //end if report equals delivered
        else { //FOR ALL FAILURE 
            SdcSmsSubmit smsrecord = quizsmsdao.getSmsMsgLog(id);
            if (smsrecord != null && smsrecord.getMsgType().equalsIgnoreCase("premium")) {
                UmeUser user = null;
                MobileClubBillingPlan mobileClubBillingPlan = null;
                SdcMobileClubUser clubUser = null;
                String userUnique = "";
                try {
                    userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", defClubDomain);
                } catch (Exception e) {
                    System.out.println("IT Exception for at line 404 EngageDR " + msisdn);
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
                    mobileClubBillingPlan = billingplandao.getAnyStatusBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());
                }
                String biloggednetwork = "unknown";
                try {
                    biloggednetwork = mobilenetwork.getMobileNetwork("IT", clubUser.getNetworkCode());
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
                        mobileClubBillingTry.setResponseDesc(report.toLowerCase());
                        mobileClubBillingTry.setResponseRef(transactionId);
                        mobileClubBillingTry.setStatus("fail");
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
                         campaigndao.log("ITdr", clubUser.getLandingpage(), clubUser.getParsedMobile(), clubUser.getParsedMobile(), null, null, clubUser.getCampaign(), clubUser.getClubUnique(), stop, 0, null, null, biloggednetwork.toLowerCase());
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
                else if (report.equalsIgnoreCase("ACKNOWLEDGED") || report.equalsIgnoreCase("ACCEPTED")) {
                    //Don't do anything here as nextpush will be 7 days set from billing daemon
                    //This is treated as the msg might have been delivered !! We will receive
                    //notification of Delivered later on. 

                } else { //For all other 
                    //NO_CREDIT, FAILED,VALIDITY_EXPIRED,UNKNOWN,OPERATOR_ERROR

                    //DATETIME SCHEDULER FOR FAILURE 
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());
                    cal.add(Calendar.HOUR, 3);
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
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);

                    MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                    mobileClubBillingTry.setUnique(id);
                    mobileClubBillingTry.setLogUnique(id);
                    mobileClubBillingTry.setAggregator("ipx");
                    mobileClubBillingTry.setClubUnique(club.getUnique());
                    mobileClubBillingTry.setCreated(new Date());
                    mobileClubBillingTry.setNetworkCode(biloggednetwork.toLowerCase());
                    mobileClubBillingTry.setParsedMsisdn(msisdn);
                    mobileClubBillingTry.setRegionCode(club.getRegion().toUpperCase());
                    mobileClubBillingTry.setResponseCode("51");
                    mobileClubBillingTry.setResponseDesc("Insufficient Funds");
                    mobileClubBillingTry.setResponseRef(transactionId);
                    mobileClubBillingTry.setStatus("fail");
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
                        System.out.println("ITMODR == FAILED BILLING UPDATE  to " + mobileClubBillingPlan.getParsedMobile() + " --- " + mobileClubBillingPlan.getNextPush());
                        billingplandao.update(mobileClubBillingPlan);//, cal.getTime());
                    }

                } //end for all the failures
            } //END if smsrecord!=null or Premium
        } // end ELSE }
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
