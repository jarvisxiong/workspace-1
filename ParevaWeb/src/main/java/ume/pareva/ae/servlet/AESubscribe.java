package ume.pareva.ae.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet; //
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.SdcRequest;
import ume.pareva.ae.util.RestUtil;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;

@WebServlet("/AESubscribe")
public class AESubscribe extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Autowired
    RestUtil restUtil;

    @Autowired
    MobileClubCampaignDao mobileclubcampaigndao;

    @Autowired
    HandsetDao handsetdao;

    private static final String login = "universalmobile";
    private static final String password = "56gnP15A";
    private static final String tokenUrl = "http://billing.virgopass.com/api_v1.5.php?getToken";
    private static final String subscriptionUrl = "http://billing.virgopass.com/api_v1.5.php?subscription";
    private static final String serviceId = "23461";
    private static final Logger logger = LogManager.getLogger(AESubscribe.class.getName());

    public AESubscribe() {
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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("Subscribing User");
        UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
        SdcRequest sdcRequest = new SdcRequest(request);
        String sessionId = sdcRequest.get("sessionId");
        Map<String, String> resutlMap = getToken(sessionId);
        String errorCode = resutlMap.get("error_code");
        if (errorCode.equals("0")) {
            subscribeUser(resutlMap, request, response);
        } else {
            logger.error("Error Getting Token");
            logger.error("Redirecting To Home Page");
            response.sendRedirect("http://" + dmn.getDefaultUrl());
            return;
        }
    }

    public Map<String, String> getToken(String sessionId) {

        Map<String, String> tokenParameterMap = new HashMap<String, String>();
        tokenParameterMap.put("login", login);
        tokenParameterMap.put("password", password);
        tokenParameterMap.put("service_id", serviceId);
        tokenParameterMap.put("session_id", sessionId);
        Map<String, String> resutlMap = restUtil.makeRestCall(tokenUrl, tokenParameterMap);
        return resutlMap;

    }

    public void subscribeUser(Map<String, String> resutlMap, HttpServletRequest request, HttpServletResponse response) {
        String networkType = "unknown";
        SdcRequest sdcRequest = new SdcRequest(request);
        String templateType = sdcRequest.get("templateType");
        String cid = sdcRequest.get("cid");
        String landingPage = sdcRequest.get("landingPage");
        String network = sdcRequest.get("network");
        String token = resutlMap.get("token");
        System.out.println("Template Type: " + templateType);
        System.out.println("landingPage: " + landingPage);
        System.out.println("Network: " + network);
        if (!cid.equals("")) {
            biLog(request, response);
        }
        if (!network.equals("") && network.contains("_")) {
            networkType = network.split("_")[0];
        }
        try {
            if (templateType.equals("unIdentified")) {
                String msisdn = sdcRequest.get("msisdn");
                if (msisdn.startsWith("0")) {
                    msisdn = "+971" + msisdn.substring(1);
                    msisdn = URLEncoder.encode(msisdn, "UTF-8");
                }
                System.out.println("Subscription URL: " + subscriptionUrl + "&token=" + token + "&msisdn=" + msisdn + "&opt1=" + cid + "_" + networkType + "&opt2=" + landingPage);
                response.sendRedirect(subscriptionUrl + "&token=" + token + "&msisdn=" + msisdn + "&opt1=" + cid + "_" + networkType + "&opt2=" + landingPage);

                return;
            } else {
                System.out.println("Subscription URL: " + subscriptionUrl + "&token=" + token + "&opt1=" + cid + "_" + networkType + "&opt2=" + landingPage);
                response.sendRedirect(subscriptionUrl + "&token=" + token + "&opt1=" + cid + "_" + networkType + "&opt2=" + landingPage);
                return;
            }
        } catch (Exception e) {
            logger.error("Error Subscribing User");
            e.printStackTrace();
        }

    }

    public void biLog(HttpServletRequest request, HttpServletResponse response) {
        String operator = "unknown";
        String networkType = "unknown";
        UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
        SdcRequest sdcRequest = new SdcRequest(request);
        String templateType = sdcRequest.get("templateType").toUpperCase();
        String campaignId = sdcRequest.get("cid");
        String network = sdcRequest.get("network");
        String sessionId = sdcRequest.get("sessionId");
        String landingPage = sdcRequest.get("landingPage");
        Handset handset = handsetdao.getHandset(request);
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
        String clubUnique = club.getUnique();
        if (templateType.equalsIgnoreCase("unIdentified")) {
            templateType = "MANUAL";
        }
        if (!network.equals("") && network.contains("_")) {
            networkType = network.split("_")[0];
            operator = network.split("_")[1];
        }
        mobileclubcampaigndao.log("subscribe", landingPage, sessionId, sessionId, handset, dmn.getUnique(), campaignId, clubUnique, templateType, 0, request, response, operator, networkType, "", "");
    }

}
