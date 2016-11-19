package ume.pareva.ae.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.ae.util.AESendMessage;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubBillingTry;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobileBillingDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.ae.util.MappingUtil;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeClubMessages;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.StopUser;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.util.NetworkMapping;

@WebServlet("/AECallback")
public class AECallback extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(AECallback.class.getName());

    @Autowired
    MappingUtil mappingUtil;

    @Autowired
    MobileClubCampaignDao mobileclubcampaigndao;

    @Autowired
    HandsetDao handsetdao;

    @Autowired
    InternetServiceProvider internetserviceprovider;

    @Autowired
    SubscriptionCreation subscriptioncreation;

    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;

    @Autowired
    UmeClubDetailsDao umeclubdetailsdao;

    @Autowired
    MobileBillingDao mobilebillingdao;

    @Autowired
    CpaLoggerDao cpaloggerdao;

    @Autowired
    NetworkMapping networkMapping;

    @Autowired
    AESendMessage aeSendMessage;

    @Autowired
    StopUser stopuser;

    public AECallback() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> callbackMap = mappingUtil.mapRequestToCallback(request);
        System.out.println("*****************************************Callback Parameter Start***********************************************");

        System.out.println("smsid: " + callbackMap.get("smsid"));
        System.out.println("rate: " + callbackMap.get("rate"));
        System.out.println("status: " + callbackMap.get("status"));
        System.out.println("msisdn: " + callbackMap.get("msisdn"));
        System.out.println("action: " + callbackMap.get("action"));
        System.out.println("smstype: " + callbackMap.get("smstype"));
        System.out.println("id_application: " + callbackMap.get("id_application"));
        System.out.println("opid: " + callbackMap.get("opid"));

        System.out.println("*****************************************Callback Parameter End*************************************************");

        String status = callbackMap.get("status");
        String sessionType = "";
        String network = getUaeNetwork(callbackMap.get("opid"));

        UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());

        //network=networkMapping.getFrNetworkMap().get(callbackMap.get("opid"));
        if (callbackMap.get("action").equalsIgnoreCase("UNSUB")) {
//          response.sendRedirect("http://" + dmn.getDefaultUrl());
            terminateUser(callbackMap, request, response, club);
        }
        if (status.equals("delivered") && callbackMap.get("action").equalsIgnoreCase("SUB")) {
//                String campaignId = getCampaignAndNetworkType(callbackMap).get("campaign");
            String campaignId = "SUB";
            if (!campaignId.equals("")) {
                MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
                if (cmpg != null && cmpg.getSrc().trim().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("subscription")) {
                    int insertedRows = cpaloggerdao.insertIntoCpaLogging(callbackMap.get("msisdn"), campaignId, club.getUnique(), 10, network, cmpg.getSrc());
                }
                if (cmpg != null && cmpg.getSrc().trim().endsWith("RS")) {
                    //String enMsisdn = MiscCr.encrypt(msisdn);
                    int insertedRows = cpaloggerdao.insertIntoRevShareLogging(0, cmpg.getPayoutCurrency(), callbackMap.get("msisdn"), callbackMap.get("msisdn"), campaignId, club.getUnique(), 0, network, cmpg.getSrc(), 0);
                }
            }
            sessionType = "newUser";
            saveCookie(callbackMap, response);
            String subResponse = createUser(callbackMap, request);
            if (!campaignId.equals("") && (subResponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY") || subResponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY"))) {
                biLog(callbackMap, request, response);
            }
            if ((subResponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY") || subResponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY"))) {
                sendWelcomeSms(callbackMap, request, club);
            }

        }
    }

    private void terminateUser(Map<String, String> callbackMap, HttpServletRequest request, HttpServletResponse response, MobileClub club) {
        String msisdn = callbackMap.get("msisdn");
        stopuser.stopSingleSubscriptionNormal(msisdn, club.getUnique(), request, response);
        //DOn't need to send SMS
        //sendTerminateSms(callbackMap, club);
    }

    private void sendTerminateSms(Map<String, String> callbackMap, MobileClub club) {
        String network = "";
        String msisdn = callbackMap.get("msisdn");

        if (!callbackMap.get("opid").equals("")) {
            network = getUaeNetwork(callbackMap.get("opid"));
        }
        UmeClubDetails userClubDetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());

        String smsid = callbackMap.get("smsid");
        String msgText = "";
        String shortCode = getAEShortCodeFromNetwork(network);
        String smsType = "Stop";
        List<UmeClubMessages> stopMessages = umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Stop");
        if (stopMessages != null && !stopMessages.isEmpty()) {
            msgText = stopMessages.get(0).getaMessage();
            //                        System.out.println("**Stop message: " + StopMessage);
        }
        aeSendMessage.requestAESendSmsTimer(msisdn, network, msgText, shortCode, smsid, smsType, club, userClubDetails, 1000, false);
    }

    private String getUaeNetwork(String operatorId) {
        String network = "unknown";
        if (operatorId != null && operatorId.equals("30")) {
            network = "etisalat";
        } else if (operatorId != null && operatorId.equals("41")) {
            network = "du";
        }
        return network;
    }

    private void sendWelcomeSms(Map<String, String> callbackMap, HttpServletRequest request, MobileClub club) {

        String network = "";
        if (!callbackMap.get("opid").equals("")) {
            network = getUaeNetwork(callbackMap.get("opid"));
        }

        String msisdn = callbackMap.get("msisdn");
        UmeClubDetails userClubDetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());

        String smsid = callbackMap.get("smsid");
        String msgText = "";
        String shortCode = getAEShortCodeFromNetwork(network);
        String smsType = "Welcome";

        List<UmeClubMessages> welcomeMessages = umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Welcome");
        if (welcomeMessages != null && !welcomeMessages.isEmpty()) {
            System.out.println("Send Welcome SMS IPX Club Details: " + msisdn);
            for (UmeClubMessages welcomeMessage : welcomeMessages) {
                msgText = welcomeMessage.getaMessage();
                aeSendMessage.requestAESendSmsTimer(msisdn, network, msgText, shortCode, smsid, smsType, club, userClubDetails, 1000, false);
            }
        } else {
            if (club.getWelcomeSms() != null && !club.getWelcomeSms().isEmpty()) {
                msgText = club.getWelcomeSms();
                aeSendMessage.requestAESendSmsTimer(msisdn, network, msgText, shortCode, smsid, smsType, club, userClubDetails, 1000, false);
            }
        }
    }

    private String getAEShortCodeFromNetwork(String network) {
        String shortCode = "1154";
        if (network.equalsIgnoreCase("Etisalat")) {
            shortCode = "MobiPlanet";
        }
        return shortCode;
    }

    public void saveCookie(Map<String, String> callbackMap, HttpServletResponse response) {
        String msisdn = callbackMap.get("subscription_id");
        Cookie cookie = new Cookie("subscriptionId", msisdn);
        cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
        response.addCookie(cookie);
    }

    public void biLog(Map<String, String> callbackMap, HttpServletRequest request, HttpServletResponse response) {
        String network = "";
        if (!callbackMap.get("opid").equals("")) {
            network = getUaeNetwork(callbackMap.get("opid"));
        }

        //TODO check this issue with Madan
//        String template = callbackMap.get("opt2");
//        String campaignId = getCampaignAndNetworkType(callbackMap).get("campaign");
//        String networkType = getCampaignAndNetworkType(callbackMap).get("networkType");
        String template = "callback_ok";
        String campaignId = "SUB";
        String networkType = "SUB";
        //END TODO

        UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
        String msisdn = callbackMap.get("msisdn");
        Handset handset = handsetdao.getHandset(request);
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
        String clubUnique = club.getUnique();
        String smsid = callbackMap.get("smsid");
        mobileclubcampaigndao.log("callback_ok", template, msisdn, smsid, handset, dmn.getUnique(), campaignId, clubUnique, "SUBSCRIBED", 0, request, response, network, networkType, "", "");
    }

    public String createUser(Map<String, String> callbackMap, HttpServletRequest request) {
        String network = getUaeNetwork(callbackMap.get("opid"));
        UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");

        MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
        String msisdn = callbackMap.get("msisdn");

        //TODO check this issue with Madan
//        String campaignId = getCampaignAndNetworkType(callbackMap).get("campaign");
//        String landingPage = callbackMap.get("opt2");
        String campaignId = "SUB";
        String landingPage = "callback_ok";
        //END TODO

        return subscriptioncreation.checkSubscription(msisdn, club, campaignId, 7, network, landingPage);
    }

    public void insertBillingTryForUser(SdcMobileClubUser clubUser, MobileClub club) {
        MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
        mobileClubBillingTry.setTransactionId("");
        mobileClubBillingTry.setResponseCode("003");
        mobileClubBillingTry.setCreated(new Date());
        mobileClubBillingTry.setRegionCode(club.getRegion());
        mobileClubBillingTry.setClubUnique(club.getUnique());
        mobileClubBillingTry.setCampaign(clubUser.getCampaign());
        mobilebillingdao.insertBillingTry(mobileClubBillingTry);
    }

    private void redirectUserAndSaveSession(Map<String, String> callbackMap, String sessionType, HttpServletRequest request, HttpServletResponse response) {
        UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
        UmeClubDetails userClubDetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        HttpSession session = request.getSession();
        try {
            if (userClubDetails.getServiceType().equals("Content")) {
                if (userClubDetails.getServedBy().equals("UME")) {
                    session.setAttribute("sessionType", sessionType);
                    response.sendRedirect("http://" + dmn.getDefaultUrl() + "/videos.jsp");
                } else {
                    response.sendRedirect("http://" + dmn.getRedirectUrl());
                }
            } else {
                //redirection code for competition service
            }
        } catch (Exception e) {
            logger.error("Error Redirecting To Video Page");
            e.printStackTrace();
        }

    }

    public Map<String, String> getCampaignAndNetworkType(Map<String, String> callbackMap) {
        Map<String, String> campaingAndNetwork = new HashMap<String, String>();
        String networkType = "";
        String campaign = "";
        if (!callbackMap.get("opt1").equals("") && callbackMap.get("opt1").contains("_")) {
            campaign = callbackMap.get("opt1").split("_")[0];
            networkType = callbackMap.get("opt1").split("_")[1];
        }
        campaingAndNetwork.put("networkType", networkType);
        campaingAndNetwork.put("campaign", campaign);
        return campaingAndNetwork;
    }

}
