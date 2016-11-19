package ume.pareva.it.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
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
import ume.pareva.cms.MobileClubCampaignDao; //
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.it.IpxTimerSendSms;
import ume.pareva.it.IpxBillingDirect;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeClubMessages;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.sdk.MiscDate;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.util.ZACPA;

/**
 * Servlet implementation class ZAConfirm
 */
// @WebServlet("/ITConfirm")
public class ITConfirm extends HttpServlet {

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
    CpaLoggerDao cpaloggerdao;

    @Autowired
    MobileClubDao mobileclubdao;

    @Autowired
    SubscriptionCreation subscriptioncreation;
    
    @Autowired
    UmeClubDetailsDao umeclubdetailsdao;
    
    private final Logger logger = LogManager.getLogger(ITConfirm.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ITConfirm() {
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
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        confirmSubscription(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        confirmSubscription(request, response);
    }

    public void confirmSubscription(HttpServletRequest request, HttpServletResponse response) {
        ThreadContext.put("ROUTINGKEY", "IT");

        System.out.println("***************ITCONFIRM BABE*********************");

        //============   Variable Initialisation =======
        HttpSession session = request.getSession();
        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();

        MobileClubCampaign cmpg = null;
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
        UmeClubDetails userclubdetails = null;
        String campaignUnique = aReq.get("cid");
        String pubId = (String) session.getAttribute("cpapubid"); //aReq.get("pubid");
        if (pubId == null) {
            pubId = "";
        }

        if (club != null) {
            userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        }

            //  ======== Variable Initialisation End ==============================
        String msisdn = (String) request.getAttribute("msisdn");
        IpxBillingDirect directBilling = (IpxBillingDirect) request.getAttribute("ipxdirectbilling");
        IpxTimerSendSms timersms = (IpxTimerSendSms) request.getAttribute("timersms");

        String status = "";
        String landingPage = (String) request.getAttribute("landingPage");
        if (null == landingPage || "".equalsIgnoreCase(landingPage)) {
            landingPage = (String) session.getAttribute("landingPage");
        }

//            boolean confMsg = false;
        boolean delaySms = false;
            // String submsisdn="";

            // Calendar debug_time = new GregorianCalendar();
        // ITALY
        msisdn = (String) request.getSession().getAttribute("ipx_msisdn");
        String transactionId = (String) request.getSession().getAttribute("ipx_transactionid");
        String operator_ = (String) request.getSession().getAttribute("ipx_operator");
        if (operator_ == null) {
            operator_ = "";
        } else {
            operator_ = operator_.toLowerCase();
        }
        String subscriptionId = (String) request.getSession().getAttribute("ipx_subscriptionid");
        String messageId = aReq.get("messageid");
        String threeResponse = aReq.get("s");

        if (!threeResponse.equals("")) {
            Enumeration ee = request.getParameterNames();
            for (; ee.hasMoreElements();) {
                String elem = (String) ee.nextElement();
                //System.out.println("IPXM THREE Subscription --" + elem+ ": " + request.getParameter(elem));
            }
        }
        // ITALY
        if (msisdn == null) {
            msisdn = "";
        }
        if (transactionId == null) {
            transactionId = "";
        }
        if (subscriptionId == null) {
            subscriptionId = "";
        }
        msisdn = SdcMisc.parseMobileNumber(msisdn);

        System.out.println("***************msisdn: " + msisdn);

        if (club == null) {
            status = "Error Subscription not found";
        } else if (msisdn.equals("")) {
            status = "Error: Msisdn is invalid";// lp.get("error_msisdn");
        } else if (transactionId.equals("")) {
            status = "Error Transaction"; // lp.get("error_transaction");
        } else if (subscriptionId.equals("")) {
            status = "Error Subscription failed";// lp.get("error_subscription_failed");
        } 
        else 
        {

            System.out.println("*************** Got Subscription *********************");

            String defClubDomain = "5510024809921CDS";

            Calendar c1 = new GregorianCalendar();
            c1.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
            Date bstart = c1.getTime();
            Date getNowTime = c1.getTime();
            c1.add(Calendar.DATE, club.getPeriod());
            c1.add(Calendar.MINUTE, -5);
            Date bend = c1.getTime();

            if (campaignUnique != null && !campaignUnique.trim().isEmpty()) {

                cmpg = UmeTempCmsCache.campaignMap.get(campaignUnique);
                delaySms = true;
            }

            SdcMobileClubUser clubUser = null;

            System.out.println("*************** Checking User *********************");
            if (user == null && !msisdn.equals("")) {
                System.out.println("*************** inside msisdn: " + msisdn);
                user = umeuserdao.getUser(msisdn);
            }
                //======== Italy Subscription starts here ======================

            if (userclubdetails.getBillingType().equalsIgnoreCase("subscription")) {

                String subsresponse = subscriptioncreation.checkSubscription(msisdn, club, campaignUnique, 7, "", operator_, subscriptionId, landingPage, pubId, transactionId, subscriptionId);
                System.out.println("ITCONFIRM subscription response is "+subsresponse);
                if (subsresponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY")
                        || subsresponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY")) {

                    
                    String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");
                    if (userUnique != null && !userUnique.equals("")) {
                        user = umeuserdao.getUser(msisdn);
                    }
                    clubUser = user.getClubMap().get(club.getUnique());
                    if (clubUser == null) {
                        clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique()); //should be never NULl
                    }
                    if (user != null && clubUser != null) {
                        user.getClubMap().put(club.getUnique(), clubUser);
                    }

                    logger.info("ITCONFIRM CLUBUSER SUBSCRIPTION CREATED SUCCESSFULLY " + clubUser.toString());
                }

                if (subsresponse.equals("SUBSCRIPTION RECORD ALREADY EXISTS")) {

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
                        System.out.println("ITCONFIRM ---  Redirect URL is "+redirecturl);
                        request.setAttribute("personallink", redirecturl);
                        session.setAttribute("personallink", redirecturl);
                        return;
                    }

                }

            }

            if (operator_.equalsIgnoreCase("tim")) {
//                delaySms = false;
                List<UmeClubMessages> welcomeMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Welcome");
                if(welcomeMessages!=null && !welcomeMessages.isEmpty()){
                    System.out.println("Send Welcome SMS IPX Club Details: " + msisdn);
                    for (UmeClubMessages welcomeMessage : welcomeMessages) {
                        if(!club.getOtpSoneraId().equals("umecip-it"))
                            timersms.requestSendSmsTimerMessage(welcomeMessage.getaMessage(), msisdn, club.getClubName(), user, 3 * 60 * 1000, delaySms, club);
                        else
                            timersms.requestSendSmsMessage(club, msisdn, operator_, subscriptionId, null, user, 3 * 60 * 1000, false, welcomeMessage.getaMessage());
                    }
                }else{
                    if(club.getWelcomeSms()!=null && !club.getWelcomeSms().isEmpty()){
                        System.out.println("Send Welcome SMS IPX from Club Welcome" + msisdn);
                        if(!club.getOtpSoneraId().equals("umecip-it"))
                            timersms.requestSendSmsTimerMessage(club.getWelcomeSms(), msisdn, club.getClubName(), user, 3 * 60 * 1000, delaySms, club);
                        else
                            timersms.requestSendSmsMessage(club, msisdn, operator_, subscriptionId, null, user, 3 * 60 * 1000, false, club.getWelcomeSms());
                    }
                }
            }
            
            if (operator_.equalsIgnoreCase("wind") && delaySms) {
//                delaySms = false;
                List<UmeClubMessages> welcomeMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Teaser");
                if(welcomeMessages!=null && !welcomeMessages.isEmpty()){
                    for (UmeClubMessages welcomeMessage : welcomeMessages) {
                        timersms.requestSendSmsTimerMessage(welcomeMessage.getaMessage(), msisdn, club.getClubName(), user, 10 * 1000, delaySms, club);
                    }
                }
//                else{
//                    if(club.getWelcomeSms()!=null && !club.getWelcomeSms().isEmpty()){
//                        timersms.requestSendSmsTimerMessage(club.getWelcomeSms(), msisdn, club.getClubName(), user, 10 * 1000, delaySms);
//                    }
//                }
//                timersms.requestSendSmsSpainTimer(club, msisdn, club.getClubName(), user, 3 * 60 * 1000, delaySms);
            }
            
            
            MobileClubBillingPlan billingplan = null;
            billingplan = new MobileClubBillingPlan();
            billingplan.setTariffClass(club.getPrice());
            billingplan.setActiveForAdvancement(1);
            billingplan.setActiveForBilling(1);
            billingplan.setAdhocsRemaining(0.0);
            billingplan.setBillingEnd(bend);
            billingplan.setClubUnique(club.getUnique());
            billingplan.setLastPaid(clubUser.getSubscribed());
            billingplan.setLastSuccess(new Date(0));
            billingplan.setLastPush(new Date(0));
            billingplan.setNetworkCode(operator_);
            billingplan.setNextPush(bend);
            billingplan.setParsedMobile(user.getParsedMobile());
            billingplan.setPartialsPaid(0.0);
            billingplan.setSubscribed(clubUser.getSubscribed());
            //billingplan.setPartialsRequired(Double.parseDouble(String.valueOf(userclubdetails.getFrequency() + "")));
            billingplan.setPartialsRequired(2.0);
            billingplan.setPushCount(0.0);
            billingplan.setServiceDate(getNowTime);
            billingplan.setSubUnique(clubUser.getUserUnique());
            billingplan.setPublisherId(pubId);
            billingplan.setaCampaign(campaignUnique);            
            // billingplan.setServiceDateBillsRemaining(Double.parseDouble(String.valueOf(userclubdetails.getFrequency())));
            billingplan.setServiceDateBillsRemaining(2.0);
            billingplan.setBillingEnd(bend);
            billingplan.setContractType("");
            billingplan.setExternalId(subscriptionId);
            System.out.println("go into billing plan");

            if (club.getOtpSoneraId().equalsIgnoreCase("umesolo18-it")) //THIS IS FOR TRIAL VERSION                    
            {
                directBilling.requestBillingPlanTrial(billingplan, clubUser, club.getOtpSoneraId(), club.getOtpTelefiId(), club.getOtpServiceName());
            } else //THIS IS FOR BILLING INSTANT
            {
                directBilling.requestBillingPlanDirect(billingplan, clubUser, club.getOtpSoneraId(), club.getOtpTelefiId(), club.getOtpServiceName());
            }
                   //======== Billing Pland Ends here ==============================

            String optintype = "wapoptin";
            if(transactionId.contains("6-"))
                optintype = "smsoptin";
            
            campaigndao.log("confirm", landingPage, user.getUnique(), user.getParsedMobile(), null, domain, campaignUnique, club.getUnique(), "SUBSCRIBED",
                    1, request, response, operator_.toLowerCase(), optintype, "", "", pubId);
            
            if (cmpg != null) {
                System.out.println("ITcmpg is " + cmpg.getUnique() + " -- " + cmpg.getCampaign() + "--- " + cmpg.getSrc());
                cmpg.setBillingCount(cmpg.getBillingCount() + 2);
                try {
                    campaigndao.saveItem(cmpg);
                } catch (Exception e) {
                    System.out.println("ItalyConfirm Exception line no.431 " + e.getMessage());
                }
            }

            String cpaloggingquery = "";
            if (cmpg != null && cmpg.getSrc().trim().toLowerCase().endsWith("cpa")) {
                System.out.println("ITcmpg is " + cmpg.getUnique() + " -- " + cmpg.getCampaign() + " -- " + cmpg.getSrc());
                SimpleDateFormat currentsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar currentTime = Calendar.getInstance();
                currentTime.add(Calendar.MINUTE, 10);
                String nextpush = currentsdf.format(currentTime.getTime());

                String cpaparameter1 = (String) session.getAttribute("cpaparam1");
                String cpaparameter2 = (String) session.getAttribute("cpaparam2");
                String cpaparameter3 = (String) session.getAttribute("cpaparam3");

                String cpaLogstatus = "0";

                //IF CPAType is subscription then we log inside cpaLogging for notification
                if (cmpg.getCpaType().equalsIgnoreCase("subscription")) {

                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                    int insertedRows = cpaloggerdao.insertIntoCpaLogging(msisdn, campaignUnique, club.getUnique(), 10, clubUser.getNetworkCode(), cmpg.getSrc());
                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                    int updatecpavisit = cpaloggerdao.updateCpaVisitLogMsisdnSubscriptionDate(msisdn, clubUser.getSubscribed(), cmpg.getUnique(), cpaparameter1, cpaparameter2, cpaparameter3);
                }
            }

            if (cmpg != null && cmpg.getSrc().trim().endsWith("RS")) {

                String parameter1 = (String) session.getAttribute("revparam1");
                String parameter2 = (String) session.getAttribute("revparam2");
                String parameter3 = (String) session.getAttribute("revparam3");

                //System.out.println("ZACONFIRM PARAMETER INFORMATION "+parameter1+" "+parameter2+" "+parameter3);
                SimpleDateFormat currentsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar currentTime = Calendar.getInstance();
                //currentTime.add(Calendar.MINUTE, 10);
                String nextpush = currentsdf.format(currentTime.getTime());

                String enMsisdn = MiscCr.encrypt(msisdn);
                int insertedRows = cpaloggerdao.insertIntoRevShareLogging(0, cmpg.getPayoutCurrency(), msisdn, enMsisdn, campaignUnique, club.getUnique(), 0, clubUser.getNetworkCode(), cmpg.getSrc(), 0);

                // 2016.01.13 - AS - Removed commented code, check repo history if needed
                int updatedRows = cpaloggerdao.updateRevShareVisitLogMsisdnSubscriptionDate(msisdn, clubUser.getSubscribed(), cmpg.getUnique(), parameter1, parameter2, parameter3);
            }

            System.out.println("***************END OF IT CONFIRM BABE*********************");

            String redirecturl = "http://" + dmn.getDefaultUrl();

            //========= If Club Service is Content
            if (userclubdetails.getServiceType().equalsIgnoreCase("content") && user != null && clubUser != null) {
                System.out.println("** Club Service is Content: ");
                //=========== If it is Provided by UME
                if (userclubdetails.getServedBy().equalsIgnoreCase("ume")) {
                    System.out.println("** Club Service is Provided by UME: ");

                    redirecturl = "http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId();
                } //========  If it is provided by Third Party like MobiPlanet
                else if (userclubdetails.getServedBy().equalsIgnoreCase("thirdparty")) {
                    System.out.println("** Club Service is Provided by thirdparty: ");
                    redirecturl = "http://" + dmn.getRedirectUrl() + "/?m=" + Misc.encrypt(clubUser.getParsedMobile()) + "&sub=success&credit=" + clubUser.getCredits();
                } //====== If no setting is present=============
                else {
                    System.out.println("** Club Service is Provided by Content Else: ");
                    redirecturl = "http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId();
                }
            }

            //===== TODO if this service is Competition =================
            if (userclubdetails.getServiceType().equalsIgnoreCase("competition") && user != null && clubUser != null) {
                //redirecturl = "http://" + dmn.getDefaultUrl() + "/thankyoupage.jsp?msisdn=" + user.getParsedMobile();
                redirecturl="http://" + dmn.getDefaultUrl()+ "/weeklyquiz.jsp?clubid=" +club.getUnique()+"&msisdn="+user.getParsedMobile()+"&mid="+Misc.encrypt(user.getParsedMobile());
            }
            
//            if(!status.trim().equals("")) //If nothing then redirect with status. 
//            {
//                //redirecturl = "http://" + dmn.getDefaultUrl() + "index_main.jsp?status=" + status;
//                request.setAttribute("status", status);
//                session.setAttribute("status", status);
//                System.out.println("** status: " + status);                
//                java.util.List notSubscribedClubDomains=(java.util.List)request.getAttribute("notSubscribedClubDomains");
//                if(notSubscribedClubDomains!=null && !notSubscribedClubDomains.isEmpty()){
//                    UmeDomain popunderDomain=(UmeDomain)notSubscribedClubDomains.get((int) java.lang.Math.floor(java.lang.Math.random()* notSubscribedClubDomains.size()));
//                    String popunderCampaignId=campaigndao.getCampaignUnique(popunderDomain.getUnique(),"PopUnder");
//                    System.out.println("personallink: "+"http://"+popunderDomain.getDefaultUrl()+"/?cid="+popunderCampaignId);
//                    redirecturl = "http://"+popunderDomain.getDefaultUrl()+"/?cid="+popunderCampaignId;
//                    request.setAttribute("personallink", redirecturl);
//                    session.setAttribute("personallink", redirecturl);
//                }else{
//                    System.out.println("**** inside if notSubscribedClubDomains is null or empty: " + notSubscribedClubDomains);
//                }
//            }else{
//                request.setAttribute("personallink", redirecturl);
//                session.setAttribute("personallink", redirecturl);
//                System.out.println("** personallink: " + redirecturl);
//            }
                request.setAttribute("personallink", redirecturl);
                session.setAttribute("personallink", redirecturl);
                System.out.println("** personallink: " + redirecturl);
            return;
        }
        System.out.println("**** status: " + status);
        
        if(operator_!=null && !operator_.equalsIgnoreCase("tim")){
            java.util.List notSubscribedClubDomains=(java.util.List)request.getAttribute("notSubscribedClubDomains");
            if(notSubscribedClubDomains==null || notSubscribedClubDomains.isEmpty()){
                notSubscribedClubDomains=(java.util.List)session.getAttribute("notSubscribedClubDomains");
            }
            if(notSubscribedClubDomains!=null && !notSubscribedClubDomains.isEmpty()){
                UmeDomain popunderDomain=(UmeDomain)notSubscribedClubDomains.get((int) java.lang.Math.floor(java.lang.Math.random()* notSubscribedClubDomains.size()));
                String popunderCampaignId=campaigndao.getCampaignUnique(popunderDomain.getUnique(),"Redirect");
                System.out.println("personallink: "+"http://"+popunderDomain.getDefaultUrl()+"/?cid="+popunderCampaignId);
                String redirecturl = "http://"+popunderDomain.getDefaultUrl()+"/?cid="+popunderCampaignId;
                request.setAttribute("personallink", redirecturl);
                session.setAttribute("personallink", redirecturl);
            }else{
                System.out.println("**** notSubscribedClubDomains is null or empty: " + notSubscribedClubDomains);
            }
        }
        return;
    } // End method Confirm Subscription
}
