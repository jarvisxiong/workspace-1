package ume.pareva.po.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.SdcRequest;
import ume.pareva.po.util.RestUtil;
import ume.pareva.pojo.CampaignHitCounter;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.userservice.CpaRevShareServices;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;
import ume.pareva.util.NetworkMapping;

import com.mitchellbosecke.pebble.PebbleEngine;

public class POIndex extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Autowired
    RestUtil restUtil;

    @Autowired
    TemplateEngine templateEngine;//

    @Autowired
    MobileClubCampaignDao mobileclubcampaigndao;

    @Autowired
    HandsetDao handsetdao;

    @Autowired
    InternetServiceProvider internetserviceprovider;

    @Autowired
    LandingPage landingpage;

    @Autowired
    CampaignHitCounterDao campaignhitcounterdao;

    @Autowired
    CpaRevShareServices cparevshare;

    @Autowired
    NetworkMapping networkMapping;

    private static final String login = "universalmobile";
    private static final String password = "56gnP15A";
    private static final String networkInfoUrl = "http://billing.virgopass.com/api_v1.5.php?getNetworkInfo";
    private static final String userInfoUrl = "http://billing.virgopass.com/api_v1.5.php?getUserInfo";
    private static final String serviceId = "23461";
    private static final Logger logger = LogManager.getLogger(POIndex.class.getName());

    public POIndex() {
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

    public void processRequest(HttpServletRequest request, HttpServletResponse response) {

        SdcRequest sdcRequest = new SdcRequest(request);
        UmeDomain dmn = sdcRequest.getDomain();
        String campaignId = sdcRequest.get("cid");
        String subscriptionId = readCookie(request, "subscriptionId");
        if (!subscriptionId.equals("")) {
            logger.info("***********SUBSCRIPTION COOKIE PRESENT*************");
            Map<String, String> userInfoMap = getUserInfo(subscriptionId);
            String userStatus = userInfoMap.get("status");
            if (userStatus != null) {
                try {
                    if (userInfoMap.get("status").equals("suspended")) {
                        renderTemplate("account-suspended", "", "", request, response);
                        return;
                    } else if (userInfoMap.get("status").equals("subscribed")) {
                        logger.info("***********ACTIVE SUBSCRIPTION, REDIRECTING TO VIDEO PAGE*************");
                        HttpSession session = request.getSession();
                        String sessionType = "returningUser";
                        session.setAttribute("sessionType", sessionType);
                        response.sendRedirect("http://" + dmn.getDefaultUrl() + "/videos.jsp");
                        return;
                    }
                } catch (Exception e) {
                    logger.error("Error Redirecting After Reading Cookie");
                    e.printStackTrace();
                }
            }

        }
        Map<String, String> resutlMap = resolveNetwork(request);
        String network = "unknown";
        String networkType = resutlMap.get("network");
        
        //TODO here
        if (!networkType.equals("unknown")) {
            network = networkMapping.getFrNetworkMap().get(resutlMap.get("ope_id"));
        }
        String errorCode = resutlMap.get("error_code");

        String landingPageNetwork = "all";
        String landingPage = evaluateLandingPage(dmn.getUnique(), campaignId, landingPageNetwork);

        Date today = new Date();

        manageCampaignHit(today, dmn.getUnique(), campaignId, landingPage);

        if (errorCode.equals("0")) {
            renderTemplate(landingPage, network, networkType, request, response);
        } else {
            logger.error("Error Resolving Network For IP Address: " + request.getAttribute("ipAddress").toString());
            logger.error(resutlMap.get("error_desc"));
            try {
                response.sendRedirect("http://" + dmn.getDefaultUrl());
                return;
            } catch (IOException e) {
                logger.error("Error Redirecting To Home Page");
                e.printStackTrace();
            }

        }

    }

    public String readCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        String cookieValue = "";
        if (cookies != null) {
            for (int loopIndex = 0; loopIndex < cookies.length; loopIndex++) {
                Cookie cookie1 = cookies[loopIndex];
                if (cookie1.getName().equals(cookieName)) {
                    cookieValue = cookie1.getValue();
                    break;
                }
            }
        }

        return cookieValue;

    }

    public Map<String, String> getUserInfo(String subscriptionId) {
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("login", login);
        parameterMap.put("password", password);
        parameterMap.put("subscription_id", subscriptionId);
        Map<String, String> resutlMap = restUtil.makeRestCall(userInfoUrl, parameterMap);
        return resutlMap;
    }

    public Map<String, String> resolveNetwork(HttpServletRequest request) {

        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("login", login);
        parameterMap.put("password", password);
        parameterMap.put("ip_address", request.getAttribute("ipAddress").toString());
        Map<String, String> resutlMap = restUtil.makeRestCall(networkInfoUrl, parameterMap);
        return resutlMap;

    }

    public String evaluateLandingPage(String domain, String campaignId, String network) {
        String landingPage = "";
        if (!campaignId.equals("")) {
            landingPage = landingpage.initializeLandingPage(domain, campaignId, network);
        } else {
            landingPage = landingpage.initializeLandingPage(domain);
        }
        return landingPage;
    }

    public void manageCampaignHit(Date today, String domain, String campaignId, String landingPage) {

        CampaignHitCounter campaignHitCounter = campaignhitcounterdao.HitRecordExistsOrNot(today, domain, campaignId, landingPage);
        if (campaignHitCounter == null) {
            campaignHitCounter = new CampaignHitCounter();
            campaignHitCounter.setaUnique(Misc.generateUniqueId());
            campaignHitCounter.setaDomainUnique(domain);
            campaignHitCounter.setCampaignId(campaignId);
            campaignHitCounter.setLandingPage(landingPage);
            campaignHitCounter.setDate(today);
            campaignHitCounter.setHitCounter(1);
            campaignHitCounter.setSubscribeCounter(0);
            campaignhitcounterdao.saveCampaignHitCounter(campaignHitCounter);

        } else {
            campaignhitcounterdao.updateCampaignHitCounter(campaignHitCounter);
        }
    }

    public void biLog(String sessionId, String template, HttpServletRequest request, HttpServletResponse response) {
        UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
        SdcRequest sdcRequest = new SdcRequest(request);
        String campaignId = sdcRequest.get("cid");
        Handset handset = handsetdao.getHandset(request);
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
        String clubUnique = club.getUnique();
        String myisp = internetserviceprovider.findIsp(request.getAttribute("ipAddress").toString());
        mobileclubcampaigndao.log("index", template, sessionId, sessionId, handset, dmn.getUnique(), campaignId, clubUnique, "INDEX", 0, request, response, myisp.toLowerCase());
    }

    public void renderTemplate(String template, String network, String networkType, HttpServletRequest request, HttpServletResponse response) {

        UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
        SdcRequest sdcRequest = new SdcRequest(request);
        String campaignId = sdcRequest.get("cid");
        String sessionId = Misc.generateAnyxSessionId();
        if (!campaignId.equals("")) {
            MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
            if (cmpg != null) {
                cparevshare.cparevshareLog(request, cmpg, "", "pocpa", request.getSession(), "entry");
            }
            biLog(sessionId, template, request, response);
        }
        try {

            Map<String, Object> context = new HashMap<String, Object>();
            PebbleEngine frEngine = templateEngine.getTemplateEngine(dmn.getUnique());
            PrintWriter writer = response.getWriter();
            context.put("service_id", serviceId);
            context.put("contenturl", "http://" + dmn.getContentUrl());
            context.put("cid", campaignId);
            context.put("landingPage", template);
            context.put("sessionId", sessionId);
            context.put("contact", "contact.jsp");
            context.put("terms", "terms.jsp");
            context.put("account", "account.jsp");
            context.put("network", networkType + "_" + network);
            if (networkType.equals("unknown")) {
                context.put("networkType", "unIdentified");
            } else {
                context.put("networkType", "identified");
            }
            frEngine.getTemplate(template).evaluate(writer, context);

        } catch (Exception e) {
            logger.error("Error Loading Landing Template");
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    }

}
