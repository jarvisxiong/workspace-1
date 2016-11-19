package ume.pareva.ae.servlet;

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
import ume.pareva.ae.util.RestUtil;
import ume.pareva.pojo.CampaignHitCounter;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.userservice.CpaRevShareServices;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;

import com.mitchellbosecke.pebble.PebbleEngine; //
import java.util.logging.Level;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.UmeUser;

@WebServlet("/AEIndex")
public class AEIndex extends HttpServlet {

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
    UmeTempCache umesdc;

    @Autowired
    CpaRevShareServices cparevshare;

    @Autowired
    UmeUserDao umeuserdao;
    
    @Autowired
    MobileClubDao mobileclubdao;

    private static final String country = "uae";
    private static final String id_application = "4193";
    private static final String networkInfoUrl = "http://activepayments.me/Default.aspx";

    private static final Logger logger = LogManager.getLogger(AEIndex.class.getName());

    public AEIndex() {
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
        UmeUser user = sdcRequest.getUser();
        String msisdn = sdcRequest.getMsisdn();
        String uid = "", wapid = "";
//        String subpage = sdcRequest.get("pg");
//        String enMsisdn = sdcRequest.get("mid"); //reading encrypted msisdn which is sent as mid param. (broadcast, followup)
        HttpSession session = request.getSession();
        String domain = dmn.getUnique();
        MobileClubCampaign cmpg = null;
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
        String landingPage = "";
        String pubId = "";

        if (campaignId != null && campaignId.trim().length() > 0) {
            if (!campaignId.equals("")) {
                cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
            }
        }
        
        String landingPageNetwork = "all";
        landingPage = evaluateLandingPage(dmn.getUnique(), campaignId, landingPageNetwork);
        request.setAttribute("landingPage", landingPage);
        session.setAttribute("landingPage", landingPage);
        request.setAttribute("cid", campaignId);
        session.setAttribute("cid", campaignId);

        
        try {
            if (user == null && (msisdn != null || !msisdn.equalsIgnoreCase(""))) {
                user = umeuserdao.getUser(msisdn);
            }

            if (user != null && mobileclubdao.isActive(user, club)) {
                uid = user.getUnique();
                System.out.println("UID: " + uid);
                wapid = user.getWapId();
                try {
                    ServletContext application = request.getServletContext(); //.setAttribute(null, request);´
                    application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp").forward(request, response);
                } catch (Exception e) {
                    logger.error("AEIndex EXCEPTION {}", e.getMessage());
                    logger.error("AEIndex EXCEPTION ", e);
                }
            } else if (sdcRequest.get("msisdn").equals("") && msisdn.equals("")
                    && session.getAttribute("doneMsisdnParse") == null) {
                session.setAttribute("doneMsisdnParse", true);
                // Log this event
                //String _rdurl = "http://" + dmn.getDefaultUrl() + "/index_main.jsp?cid=" + campaignId + "&pg=" + subpage + "&routerresponse=1&mroute=1";
                    if (cmpg != null)
                        pubId = (String) session.getAttribute("cpapubid");
                
                String _rdurl = "http://" + dmn.getDefaultUrl() + "/index.jsp?cid=" + campaignId + "&routerresponse=1&l=" + landingPage + "&pubid=" + pubId;
                String url = networkInfoUrl + "?country=" + country + "&id_application=" + id_application + "&page=" + java.net.URLEncoder.encode(_rdurl, "UTF-8");
                System.out.println("AE debug BEFORE REDIRECTION ::> " + url);
                response.sendRedirect(url);
                // doRedirect(response, url);
                return;
            }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            logger.error("WAPXAE EXCEPTION 424-", e);
        }

        Date today = new Date();
        manageCampaignHit(today, dmn.getUnique(), campaignId, landingPage);
        
        String network = sdcRequest.get("operator").toLowerCase();
        if(network.equals(""))
            network = "unknown";
        msisdn = sdcRequest.get("msisdn");
        
        renderTemplate(landingPage, network, msisdn, request, response);
    }

    public String getLandingPage(HttpSession session, HttpServletRequest request, String campaignId, String domain) {
        String landingPage = "";
        String pubId = "";

        /*
         When returned from STS msisdn router it must contain landingapge with parameter "l" 
         */
        if (session.getAttribute("doneMsisdnParse") != null) {
            landingPage = (String) request.getParameter("l");
            try {
                pubId = (String) request.getParameter("pubid");
            } catch (Exception e) {
                pubId = "";
            }
        }
        if (landingPage == null || landingPage.isEmpty()) {
            if (!campaignId.equals("")) {
                landingPage = landingpage.initializeLandingPage(domain, campaignId, "all");
            } else {
                landingPage = landingpage.initializeLandingPage(domain);
            }
        }
        request.setAttribute("landingPage", landingPage);
        session.setAttribute("landingPage", landingPage);
        request.setAttribute("cid", campaignId);
        session.setAttribute("cid", campaignId);
        return landingPage;
        // ====  End Landing Page Retrieval =============================================
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

//    public Map<String, String> getUserInfo(String subscriptionId) {
//        Map<String, String> parameterMap = new HashMap<String, String>();
//        parameterMap.put("login", login);
//        parameterMap.put("password", password);
//        parameterMap.put("subscription_id", subscriptionId);
//        Map<String, String> resutlMap = restUtil.makeRestCall(userInfoUrl, parameterMap);
//        return resutlMap;
//    }
//
//    public Map<String, String> resolveNetwork(HttpServletRequest request) {
//
//        Map<String, String> parameterMap = new HashMap<String, String>();
//        parameterMap.put("login", login);
//        parameterMap.put("password", password);
//        parameterMap.put("ip_address", request.getAttribute("ipAddress").toString());
//        Map<String, String> resutlMap = restUtil.makeRestCall(networkInfoUrl, parameterMap);
//        return resutlMap;
//    }
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

    public void biLog(String sessionId, String template, HttpServletRequest request, HttpServletResponse response, MobileClubCampaign cmpg) {
        UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
        SdcRequest sdcRequest = new SdcRequest(request);
        String campaignId = sdcRequest.get("cid");
        Handset handset = handsetdao.getHandset(request);
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
        String clubUnique = club.getUnique();
        String myisp = internetserviceprovider.findIsp(request.getAttribute("ipAddress").toString());
        if (request.getParameter("loghits") == null) {
            mobileclubcampaigndao.log("index", template, sessionId, sessionId, handset, dmn.getUnique(), campaignId, clubUnique, "INDEX", 0, request, response, myisp.toLowerCase());
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////             
        if (cmpg != null && !cmpg.getaInterstitialLandingPage().equals("") && cmpg.getaInterstitialLandingPage() != null && request.getParameter("loghits") == null) {

            String testlanding = request.getParameter("interlanding");
            String interstitialLandingPage = testlanding;

            if (interstitialLandingPage == null || "".equals(interstitialLandingPage)) {
                interstitialLandingPage = cmpg.getaInterstitialLandingPage();
            }
            String interstitialRedirected = request.getParameter("interstitialredirected"); //(String)session.getAttribute("interstitialRedirected"); //
            String redirectBack = "http://" + dmn.getDefaultUrl();
            if (interstitialRedirected == null) {
                //session.setAttribute("interstitialRedirected",null);
                String interstitialDomainUnique = dmn.getPartnerDomain();
                UmeDomain interstitialDomain = umesdc.getDomainMap().get(interstitialDomainUnique);
                //redirectBack="http://"+dmn.getDefaultUrl()+"/?cid="+cid+"&clubid="+clubId+"&interstitiallandingpage="+interstitialLandingPage+"&interstitialredirected=1&loghits=no";
                System.out.println("interstitial AE redirectback url = " + redirectBack);
                String interstitialRedirectUrl = "http://" + interstitialDomain.getDefaultUrl();

                String params = "redirectback=" + redirectBack + "&cid=" + campaignId + "&clubid=" + club.getUnique() + "&interstitiallandingpage=" + interstitialLandingPage + "&interstitialredirected=1";
                interstitialRedirectUrl += "/?C3RC183=" + Misc.encrypt(params);
                System.out.println("interstitialAE index params " + params);

                try {
                    response.sendRedirect(interstitialRedirectUrl);
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(AEIndex.class.getName()).log(Level.SEVERE, null, ex);
                }
                return;
            }
        }

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    }

    public void renderTemplate(String template, String network, String msisdn, HttpServletRequest request, HttpServletResponse response) {
        UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
        SdcRequest sdcRequest = new SdcRequest(request);
        String campaignId = sdcRequest.get("cid");
        String sessionId = Misc.generateAnyxSessionId();
        if (!campaignId.equals("")) {
            MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
            if (cmpg != null) {
                cparevshare.cparevshareLog(request, cmpg, "", "aecpa", request.getSession(), "entry");
            }
            biLog(sessionId, template, request, response, cmpg);
        }
        try {
            Map<String, Object> context = new HashMap<String, Object>();
            PebbleEngine aeEngine = templateEngine.getTemplateEngine(dmn.getUnique());
            PrintWriter writer = response.getWriter();
            context.put("id_application", id_application);
            context.put("contenturl", "http://" + dmn.getContentUrl());
            context.put("cid", campaignId);
            context.put("landingPage", template);
            context.put("sessionId", sessionId);
            context.put("contact", "contact.jsp");
            context.put("terms", "terms.jsp");
            context.put("account", "account.jsp");
            context.put("network", network);
            context.put("msisdn", msisdn);
            
            
            String EtisalatUrl = "http://pt1.etisalat.ae/lp/etisalatd2c/actelaprod2c/index.htm?packageid=1064&txnid=102314&lang=en&usertype=free&device=sf&iurl=http://dh4gnu4kdfpl8.cloudfront.net/static/AE/mobiplanet/img/promo_image.jpg"
                    + "&rurl=http://" + dmn.getDefaultUrl() + "/thanks.jsp";
             
            context.put("etisalaturl", EtisalatUrl);

            if (network.equals("unknown") || msisdn.equals("")) {
                context.put("msisdnexist", "false");
                context.put("identified", "false");
            } else {
                context.put("msisdnexist", "true");
                context.put("identified", "true");
            }
            if (aeEngine != null) {
                aeEngine.getTemplate(template).evaluate(writer, context);
            } else {
                System.out.println("UAE aeEngine is nll : ");
            }
        } catch (Exception e) {
            logger.error("Error Loading Landing Template");
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
