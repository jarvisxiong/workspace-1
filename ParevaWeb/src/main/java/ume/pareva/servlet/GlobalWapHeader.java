package ume.pareva.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.cms.VideoClipDao;
import ume.pareva.dao.DoiResult;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.PassiveVisitorDao;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.SmsDoiLogDao;
import ume.pareva.dao.UmeLanguagePropertyDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.dao.UmeSmsDaoExtension;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.dao.WAPDoiLogDao;
import ume.pareva.pojo.PassiveVisitor;
import ume.pareva.pojo.SdcLanguageProperty;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.SmsDoiRequest;
import ume.pareva.pojo.SmsDoiResponse;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.pojo.WapDoiRequest;
import ume.pareva.pojo.WapDoiResponse;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.template.TemplateEngine;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;
import ume.pareva.userservice.VideoList;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.zadoi.service.ZaDoi;
import ume.pareva.cms.BannerAdDao;
import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.DoiResponseLogDao;
import ume.pareva.dao.QueryHelper;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.userservice.CheckStop;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.userservice.UserAuthentication;

/**
 * Servlet implementation class GlobalWapHeader
 */
//@WebServlet("/GlobalWapHeader")
public class GlobalWapHeader extends HttpServlet {

    @Autowired
    UmeTempCache umesdc;

    @Autowired
    HandsetDao handsetdao;

    @Autowired
    UmeLanguagePropertyDao langpropdao;

    @Autowired
    MobileClubDao mobileclubdao;

    @Autowired
    VideoClipDao videoclipdao;

    @Autowired
    UmeUserDao umeuserdao;

    @Autowired
    MobileClubCampaignDao campaigndao;

    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;

    @Autowired
    VideoList videolist;

    @Autowired
    TemplateEngine templateengine;

    @Autowired
    InternetServiceProvider internetserviceprovider;

    @Autowired
    LandingPage landingpage;

    @Autowired
    Misc misc;

    @Autowired
    UmeClubDetailsDao clubdetailsdao;

    @Autowired
    MobileClubBillingPlanDao billingplandao;

    /*@Autowired
    ZACPA zacpa;*/
    @Autowired
    QueryHelper queryhelper;

    @Autowired
    DoiResult doiresult;

    @Autowired
    PassiveVisitorDao passivevisitordao;

    @Autowired
    UmeSmsDaoExtension umesmsdaoextension;

    @Autowired
    UmeSmsDao umesmsdao;

    @Autowired
    WAPDoiLogDao wapdoilogdao;

    @Autowired
    SmsDoiLogDao smsdoilogdao;

    @Autowired
    DoiResponseLogDao doiresponselogdao;

    @Autowired
    CampaignHitCounterDao campaignhitcounterdao;

    @Autowired
    UserAuthentication userauthentication;

    @Autowired
    CheckStop checkstop;

    @Autowired
    Check24Hour check24hour;

    @Autowired
    SubscriptionCreation subscriptioncreation;
    
    @Autowired
    CpaLoggerDao cpaloggerdao;
    
    @Autowired
    BannerAdDao banneraddao;

    private static final long serialVersionUID = 1L;
    private final Logger logger = LogManager.getLogger(GlobalWapHeader.class.getName());

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
        processGlobalWapHeader(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        processGlobalWapHeader(request, response);
    }

    protected void processGlobalWapHeader(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        RequestDispatcher rd = null;

        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        logger.info("ZAHEADER  GLOBAL WAP HEADER IS CALLED UPON.... 2 ");

        String networkid = "";

        String landingPage = (String) request.getAttribute("landingPage");
        String pubId= pubId=(String) request.getAttribute("cpapubid");
        if(pubId==null) pubId=(String) session.getAttribute("cpapubid");
        if(pubId==null) pubId="";
        
        logger.info("ZAHEADER GLOBALWAP HEADER LANDING PAGE " + landingPage);

        String domain = dmn.getUnique();
        String ddir = dmn.getDefPublicDir();
        String lang = aReq.getLanguage().getLanguageCode();

        String durl = dmn.getDefaultUrl().toString().trim();
        String stylesheet = aReq.getStylesheet();
        String pageEnc = aReq.getEncoding();
        request.setAttribute("pageEnc", pageEnc);
        response.setContentType("text/html; charset=" + pageEnc);

        //This ID is received only after msisdn lookup process which must be equivalent to sts_id declared below.
        String stsReturnId = aReq.get("ID");
        System.out.println("ZAHEADER  STSREUTNID IS " + stsReturnId);

        String fileName = request.getServletPath();
        System.out.println("ZAHEADER filename is " + fileName);
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        request.setAttribute("fileName", fileName);
        request.setAttribute("domain", domain);

        Handset handset = (Handset) session.getAttribute("handset");
        if (handset == null) {
            handset = (Handset) request.getAttribute("handset");
        }
        if (handset == null) {
            handset = handsetdao.getHandset(request);
        }
        session.setAttribute("handset", handset);
        request.setAttribute("handset", handset);
        PebbleEngine za_engine = null;
        UmeClubDetails clubdetails = null;//
        SdcLanguageProperty lp = langpropdao.get("general", service, aReq.getLanguage(), dmn);

                //System.out.println("ZA FILTER  GLOBAL WAP HEADER IS CALLED UPON.... 3 ");
        boolean onMainIndexPage = fileName.indexOf("index") > -1;

        System.out.println("ZAHEADER onMainIndexPage is " + onMainIndexPage);

        String crlf = "\r\n";
        StringBuilder sbf = new StringBuilder();
        sbf.append(fileName.toUpperCase() + ".JSP: " + new Date() + crlf + crlf);
        Misc.addHttpHeaders(sbf, request);
        Misc.addHttpParameters(sbf, request);
        Map<String, String> dParamMap = UmeTempCmsCache.domainParameters.get(domain);
        if (dParamMap == null) {
            dParamMap = new HashMap<String, String>();
        }

        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);

        if (club != null) {
            clubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        }

        String campaignId = aReq.get("cid");
        MobileClubCampaign cmpg = null;
        if (!campaignId.equals("")) {
            cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
        }
        
        logger.info("========= GLOBALWAPHEADER ======  campaignID is "+campaignId+" CLUB INFO IS "+club.getRegion()+" -- "+club.getUnique());
        String uid = "";
        String wapid = "";
        //READING IP of USER
        String ip = "";

        ip = (String) session.getAttribute("userip");
        if (ip == null || ip.trim().length() <= 0) {
            ip = (String) request.getAttribute("userip");
        }

        if (ip == null || ip.trim().length() <= 0) {
            ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }

        }

        if (ip != null) {
            int idx = ip.indexOf(',');
            if (idx > -1) {
                ip = ip.substring(0, idx);
            }
        }

        request.setAttribute("userip", ip);
        String xprof = aReq.get("xprof");
        String subpage = aReq.get("pg");
        String stsMsisdn = aReq.get("msisdn");
        HttpSession ses = request.getSession();
        String msisdn = "";
        if (!stsMsisdn.equals("")) {
            msisdn = stsMsisdn;
        }
        String enMsisdn = aReq.get("mid");
        String fntString = aReq.get("fnt");
        String isp = internetserviceprovider.findIsp(ip);
        networkid = getNetworkid(isp);
        /*isp="mtn";
         networkid=getNetworkid(isp);
         msisdn="27840643438";
         */

        ZaDoi networkdoi = new ZaDoi();
        long beforeAuth = System.currentTimeMillis();
        String token = null;
        try {
            token = networkdoi.authenticate();
        } catch (Exception e) {
            token = null;
        }

        if (token != null && !"".equalsIgnoreCase(token)) {
            this.getServletContext().setAttribute("zatoken", token);
        }
        if (token != null && "".equalsIgnoreCase(token)) {
            token = (String) this.getServletContext().getAttribute("zatoken");
        }

        System.out.println("ZAHEADER wapid = " + wapid);
        System.out.println("ZAHEADER CLUB REGION = " + club.getRegion());

//============    BroadCast/Encryption Decrypting msisdn process  ===============
        if (!enMsisdn.equals("") && fntString.equals("")) { // relying on no font colour being given. Prone to future failure
            String deMsisdn = MiscCr.decrypt(enMsisdn);
            //System.out.println("madandecryptingmsisdn: "+deMsisdn+" campaignID: "+campaignId+"  ENmsisdn: "+enMsisdn);
            if (!deMsisdn.equals("")) {
                if (deMsisdn.startsWith("0")) {
                    deMsisdn = "27" + deMsisdn.substring(1);
                }
                msisdn = deMsisdn;

                try {
                    System.out.println("zatoken requesting network " + token);
                    if (msisdn != null && msisdn.trim().length() > 0) {
                        networkid = networkdoi.request_MsisdnNetwork(token, msisdn);
                    }
                } catch (Exception e) {
                    System.out.println("ZAHEADER Exception: ZA NETWORK DOI ERROR: " + e);
                }

                if (networkid != null && networkid.trim().length() > 0) {
                    networkdoi = null;
                }

                if (landingPage == null) {
                    landingPage = "unknown";
                }
                campaigndao.log("zaecnrypted", landingPage, msisdn, msisdn, handset, domain, campaignId, club.getUnique(), "IDENTIFIED", 0, request, response, networkid.toLowerCase(),"","","",pubId);
            }
        }

        // Take MSISDN from session where exists
        if (null != ses.getAttribute("sdc_msisdn_param")) {
            msisdn = (String) ses.getAttribute("sdc_msisdn_param");
        }

        String sts_id = ""; //This variable is used to set ID for STS when performing Msisdn router process
        if (ses != null && !stsReturnId.equals("")) {
            sts_id = (String) ses.getAttribute("sts_id"); //Receiving StsID from session set from msisdn lookup.

                    //Comparing session sts ID and stsReturnID (stsReturnID is read above in line no. 215 
            //and if they are equal set msisdn in session. 
            if (sts_id != null && sts_id.equals(stsReturnId) && !stsMsisdn.equals("")) {
                ses.setAttribute("sdc_msisdn_param", stsMsisdn);
            }
        }

		// **************************************************************************************************
        // This section is responsible for processing the response from the MSISDN lookup router process
        // **************************************************************************************************
        //System.out.println("Router Response= "+ request.getParameter("routerresponse"));
        if ("1".equals(request.getParameter("routerresponse"))) {
            if ("" != request.getParameter("msisdn") && null != request.getParameter("msisdn")) {
                msisdn = request.getParameter("msisdn");
                if (msisdn.startsWith("0")) {
                    msisdn = "27" + msisdn.substring(1);
                }

                try {
                    System.out.println("zatoken requesting network " + token);
                    if (msisdn != null && msisdn.trim().length() > 0) {
                        networkid = networkdoi.request_MsisdnNetwork(token, msisdn);
                    }
                } catch (Exception e) {
                    System.out.println("ZAHEADER Exception: ZA NETWORK DOI ERROR: " + e);
                }

                if (networkid != null && networkid.trim().length() > 0) {
                    networkdoi = null;
                }

                if (landingPage == null) {
                    landingPage = "unknown";
                }
                if (!"".equals(campaignId.trim())) {
                    campaigndao.log("zaheader", landingPage, msisdn, msisdn, handset, domain, campaignId, club.getUnique(), "IDENTIFIED", 0, request, response, networkid.toLowerCase(),"","","",pubId);
                }

                if ("".equals(campaignId.trim())) {
                    campaigndao.log("zaheader", landingPage, msisdn, msisdn, handset, domain, "8931907441KDS", club.getUnique(), "IDENTIFIED", 0, request, response, networkid.toLowerCase(),"","","",pubId);
                }

            }

        }

//		System.out.println("User "+user);
        if (user == null && (msisdn != null || !msisdn.equalsIgnoreCase(""))) {

            user = umeuserdao.getUser(msisdn);
                    //System.out.println("user is null and the msisdn is "+msisdn);

        }
        if (user != null) {
            uid = user.getUnique();
            //System.out.println("UID: " + uid);
            msisdn = user.getParsedMobile();
            wapid = user.getWapId();
            sbf.append("USER AUTHENTICATED: " + uid + ": " + msisdn + crlf);
            //System.out.println("USER AUTHENTICATED: " + uid + ": " + msisdn + crlf);
        }

                //Get the Template Engine for the domain. 
        //which queries domainsTemplates table to get the template folder. 
        za_engine = templateengine.getTemplateEngine(domain);//
        request.setAttribute("za_engine", za_engine);

                //SEtting All Attributes in REquest
        request.setAttribute("aReq", aReq);
        request.setAttribute("user", user);
        request.setAttribute("dmn", dmn);
        request.setAttribute("service", service);
        request.setAttribute("handsetdao", handsetdao);
        request.setAttribute("anyxsdc", umesdc);
        request.setAttribute("langpropdao", langpropdao);
        request.setAttribute("mobileclubdao", mobileclubdao);
        request.setAttribute("videoclipdao", videoclipdao);
        request.setAttribute("misc", misc);
        request.setAttribute("umeuserdao", umeuserdao);
        request.setAttribute("umemobileclubuserdao", umemobileclubuserdao);
        request.setAttribute("campaigndao", campaigndao);
        request.setAttribute("videolist", videolist);
        request.setAttribute("landingpage", landingpage);
        request.setAttribute("landingPage", landingPage);
        request.setAttribute("club", club);
        request.setAttribute("cmpg", cmpg);
        request.setAttribute("campaignId", campaignId);
        request.setAttribute("dParamMap", dParamMap);
        request.setAttribute("subpage", subpage);
        request.setAttribute("msisdn", msisdn);
        //request.setAttribute("msisdn","msisdn);
        request.setAttribute("billingplandao", billingplandao);
        request.setAttribute("clubdetails", clubdetails);
        request.setAttribute("cpaloggerdao", cpaloggerdao);
        request.setAttribute("doiresult", doiresult);
        request.setAttribute("templateengine", templateengine);
        request.setAttribute("passivevisitordao", passivevisitordao);
        request.setAttribute("networkid", networkid);
        request.setAttribute("umesmsdaoextension", umesmsdaoextension);
        request.setAttribute("umesmsdao", umesmsdao);
        request.setAttribute("doiresponselogdao", doiresponselogdao);
        request.setAttribute("campaignhitcounterdao", campaignhitcounterdao);
        request.setAttribute("userauthentication", userauthentication);
        request.setAttribute("ispnetwork", isp);
        request.setAttribute("checkstop", checkstop);
        request.setAttribute("subscriptioncreation", subscriptioncreation);
        request.setAttribute("cpapubid",pubId);
        request.setAttribute("banneraddao",banneraddao);

        if (session.getAttribute("zaheadercalled") == null) {
            session.setAttribute("zaheadercalled", "true");
        }

        //========END Setting All Attributes to http request
        boolean needToConfirmThisUser =
                //(!subpage.equals("subscribe"))
                club != null // We have a reference to the club...
                && !mobileclubdao.isActive(user, club) // They are not already active in this club...
                && club.getOptIn() > 0 // There is some level of opt in on this club
                && onMainIndexPage;			     // We are on the index page - never redirect off the back of other pages

//                boolean needToConfirmThisUser =
//		       (!subpage.equals("subscribe"))                // They're on the main index page...
//		        && club!=null				     // We have a reference to the club...
//			&& !mobileclubdao.isActive(user, club)       // They are not already active in this club...
//		        && club.getOptIn()>0			     // There is some level of opt in on this club
//		        && onMainIndexPage;			     // We are on the index page - never redirect off the back of other pages
        System.out.println("ZAHEADER needToConfirmThisUser " + needToConfirmThisUser);

//		System.out.println("===========================================================");
        if ((msisdn != null) && (!msisdn.trim().equals("")) && (club.getRegion().equals("ZA"))
                && (msisdn.length() <= 8
                || (!msisdn.startsWith("07")
                && !msisdn.startsWith("08")
                && !msisdn.startsWith("0027")
                && !msisdn.startsWith("27")))) {
            needToConfirmThisUser = false;
            //doMsisdnTrace( request, msisdn, "ZA MSISDN is invalid, so we skip STS/MESH DOI" );
        }

        if (user != null && user.getAccountType() == 99) { // User account is blocked/barred so don't DOI him
            needToConfirmThisUser = false;
            //doMsisdnTrace( request, msisdn, "ZA MSISDN is blocked (99), so we skip STS/MESH DOI." );
        }

        if (user != null && !wapid.equals("") && mobileclubdao.isActive(user, club)) {
            needToConfirmThisUser = false;
        }
        
        if (needToConfirmThisUser) {
            System.out.println("ZAHEADER  NeedToconfirm this User afterwards " + needToConfirmThisUser);
            request.setAttribute("confirmUser", "true");
            session.setAttribute("confirmUser", "true");
                //This isp value is for testing purpose for identified usersasdf
            //isp="Research in Motion UK Limited";
            //isp="mtn";
            //msisdn="27840643438";
            //msisdn="27723937357"; //vodacom
            //msisdn="27833260830"; //MTN

            Map<String, Object> xhtmlImagesMap = UmeTempCmsCache.xhtmlImages.get("xhtml_" + domain);
            //System.out.println("xhtmlImage : "+xhtmlImagesMap.get("img_header1_4"));
            String headerImage = "";
            String logos = "";
//              String footerImage="";
            if (xhtmlImagesMap.get("img_header1_4") != null) {
                headerImage = (String) xhtmlImagesMap.get("img_header1_4");
            }

            String initial = request.getHeader("User-Agent");
            int end = 0;
            try {
                end = initial.indexOf(")");
            } catch (Exception e) {
                end = 0;
            }
            int start = 0;
            try {
                start = initial.indexOf("(") + 1;
            } catch (Exception e) {
                start = 0;
            }
            String[] aux = null;
            String uagent = "";

            try {
                aux = initial.substring(start, end).split(";");
                aux[1] = java.net.URLEncoder.encode(aux[1].trim(), "UTF-8");
                uagent = aux[1];
            } catch (Exception e) {
                uagent = handset.getUnique();
            }

            boolean noVodacomMsisdn = ip.startsWith("41") && (isp.trim().equalsIgnoreCase("vodacom") || isp.trim().toLowerCase().contains("vodacom")
                    || isp.trim().toLowerCase().contains("research in motion uk limited"));

                    //System.out.println("ZA FILTER  GLOBAL WAP HEADER IS CALLED UPON.... 41 "+needToConfirmThisUser);
            boolean isBlackBerryOrOpera = isp.trim().toLowerCase().indexOf("opera") > -1 || handset.getUnique().trim().toLowerCase().indexOf("blackberry") > -1
                    || uagent.trim().toLowerCase().indexOf("opera") > -1 || uagent.trim().toLowerCase().indexOf("blackberry") > -1;

            if (noVodacomMsisdn && user == null && (msisdn == null || msisdn.trim().equals("")) && !isBlackBerryOrOpera) {
                msisdn = "27820000000";
                campaigndao.log("zaheaderweb3", landingPage, msisdn, msisdn, handset, domain, campaignId, club.getUnique(), "IDENTIFIED", 0, request, response, "vodacom","","","",pubId);
            }
            request.setAttribute("msisdn", msisdn);

            //String returnURL = "http://" + dmn.getDefaultUrl() + "/confirm.jsp?optin=1&cid=" + campaignId;
            String requestuid = Misc.generateUniqueId();
            String returnURL = "http://" + dmn.getDefaultUrl() + "/zaconfirm.jsp?optin=1&cid=" + campaignId + "&myuid=" + requestuid + "&ip=" + ip + "&l=" + landingPage + "&uagent=" + uagent+"&pubid="+pubId;
            logger.info("======== GLOBALWAPHEADER RETURN URL IS ============ "+returnURL+ "===== CLUB INFO IS "+club.getRegion()+" == "+club.getUnique()+" === msisdn === "+ msisdn);
                     //System.out.println("ZA FILTER  GLOBAL WAP HEADER IS CALLED UPON.... 5"+returnURL);
            if (club.getRegion().equals("ZA") && msisdn != null && msisdn.trim().length() > 0) {
                if (msisdn.startsWith("0")) {
                    msisdn = "27" + msisdn.substring(1);
                }
                if (msisdn.contains("+")) {
                    msisdn = msisdn.replace("+", "").trim();
                }
                System.out.println("ZAHEADER inside club.getRegion(ZA) and msisdn!=null " + msisdn);

                token = (String) this.getServletContext().getAttribute("zatoken");

                ZaDoi zadoi = new ZaDoi();
                beforeAuth = System.currentTimeMillis();
                if (token == null || (token != null && "".equalsIgnoreCase(token))) {
                    token = zadoi.authenticate();
                }
                String clubname = club.getClubName();
                String clubunique = club.getUnique();
                int frequency = 3;
                String serviceid = club.getOtpServiceName();
                long before = System.currentTimeMillis();
                String networkCode = "";
                if (msisdn != null && msisdn.equals("27820000000")) {
                    networkCode = "vodacom";
                } else {
                    networkCode = zadoi.request_MsisdnNetwork(token, msisdn);
                }

                System.out.println("ZAHEADER  msisdn " + msisdn + " Network " + networkCode + " line no. " + 593);

                /**
                 * If headerImage is defined on sitebuilder by uploading logo
                 * then send DOI this logo else send x-rated (taken as default
                 * one but we should use umelogo).
                 */
                if (headerImage != null && headerImage.trim().length() > 0) {
                    logos = "http://" + dmn.getContentUrl() + "/images/wap/" + headerImage;
                } else {
                    logos = "http://" + dmn.getContentUrl() + "/static/ZA/x-rated/img/xratedsmall.png";//"http://"+dmn.getContentUrl()+"/static/ZA/x-rated/img/x-rated.jpg";
                }
                //System.out.println("zadoilogo "+logos);
                String teaser = "X-Rated Vids - 18+ ONLY"; 
                try{
                    teaser=clubdetails.getTeaser();
                }catch(Exception e){teaser = "X-Rated Vids - 18+ ONLY";}
                
                int freeDay = 1;
                try {
                    freeDay = Integer.parseInt(clubdetails.getFreeDay());

                } catch (Exception e) {
                    freeDay = 1;
                }
                try {
                    frequency = clubdetails.getFrequency();
                } catch (Exception e) {
                    frequency = 3;
                }
                if (teaser == null || teaser.length() <= 0) {
                    teaser = " X-Rated Vids - 18+ ONLY";
                }
                if (freeDay == -1) {
                    freeDay = 1;
                }

                String wappage = "";

                boolean vodacomNoMsisdn = (msisdn == null || msisdn.trim().equalsIgnoreCase("") || msisdn.trim().equalsIgnoreCase("27820000000"))
                        && (isp.trim().equalsIgnoreCase("vodacom") || isp.trim().toLowerCase().contains("vodacom")
                        || isp.trim().toLowerCase().contains("research in motion uk limited"));

                if (vodacomNoMsisdn && !isBlackBerryOrOpera) {

                    request.setAttribute("wapoptin", "true");
                    wappage = zadoi.wapOptIn(token, serviceid, clubname, frequency, msisdn, returnURL, logos, "", "", teaser, freeDay);

                    WapDoiRequest wapDoiRequest = new WapDoiRequest();
                    wapDoiRequest.setRequestUid(requestuid);
                    wapDoiRequest.setToken(token);
                    wapDoiRequest.setMsisdn(msisdn);
                    wapDoiRequest.setClubName(club.getName() + " " + ip);
                    wapDoiRequest.setTeaser(teaser);
                    wapDoiRequest.setFreeDay(freeDay);
                    wapDoiRequest.setReturnUrl(returnURL);
                    wapDoiRequest.setRequestedDate(new java.sql.Timestamp(System.currentTimeMillis()));

                    wapdoilogdao.saveWapRequest(wapDoiRequest);
                    if (wappage != null) {

                        request.setAttribute("wappageurl", wappage);
                        session.setAttribute("wappageurl", wappage);
                        WapDoiResponse wapDoiResponse = new WapDoiResponse();
                        wapDoiResponse.setRequestUid(requestuid);
                        wapDoiResponse.setWapPage(wappage);
                        wapDoiResponse.setRespondedTime(new java.sql.Timestamp(System.currentTimeMillis()));
                        wapdoilogdao.saveWapResponse(wapDoiResponse);

                        //doRedirect(response, wappage);
                        return;
                    } else {
                        String errorwappage = "/index_main.jsp?pg=subscribe&cid=" + campaignId + "&redirsrc=global_wap_header";
                        request.setAttribute("wappageurl", errorwappage);
                        WapDoiResponse wapDoiResponse = new WapDoiResponse();
                        wapDoiResponse.setRequestUid(requestuid);
                        wapDoiResponse.setWapPage(wappage);
                        wapDoiResponse.setRespondedTime(new java.sql.Timestamp(System.currentTimeMillis()));
                        wapdoilogdao.saveWapResponse(wapDoiResponse);
                        //doRedirect(response, "/index_main.jsp?pg=subscribe&cid=" + campaignId+"&redirsrc=global_wap_header" );
                        return;
                    }

                } //NoVodacom Msisdn
                else { //for Other networks
                    String operation = "";
                    if (isp == null || isp.trim().isEmpty()) {
                        operation = "smsoptin";
                    } else {
                        operation = identifyOptin(networkCode, isp, handset, uagent);
                    }

//                    System.out.println("========================================================");
//                    System.out.println("msisdn "+msisdn+" isp: "+isp+" network "+networkCode+" Operation now is "+operation);
//                    System.out.println("========================================================");
                    if (operation.toLowerCase().trim().equals("wapoptin")) {
                        //a
                        request.setAttribute("wapoptin", "true");
                        wappage = zadoi.wapOptIn(token, serviceid, clubname, frequency, msisdn, returnURL, logos, "", "", teaser, freeDay);
                        System.out.println("ZAHEADER wapdoimsgs " + request.getAttribute("pagename") + " club: " + clubname + " msisdn " + msisdn + " at " + new Date() + " line no. 681");
                        WapDoiRequest wapDoiRequest = new WapDoiRequest();
                        wapDoiRequest.setRequestUid(requestuid);
                        wapDoiRequest.setToken(token);
                        wapDoiRequest.setMsisdn(msisdn);
                        wapDoiRequest.setClubName(club.getName() + " " + ip);
                        wapDoiRequest.setTeaser(teaser);
                        wapDoiRequest.setFreeDay(freeDay);
                        wapDoiRequest.setReturnUrl(returnURL);
                        wapDoiRequest.setRequestedDate(new java.sql.Timestamp(System.currentTimeMillis()));

                        try {
                            wapdoilogdao.saveWapRequest(wapDoiRequest);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //System.out.println("wapzatest test: "+" globalwapheader.jsp WAP OPT IN RESPONSE MSISDN: "+msisdn+ " IS : "+wappage+" at "+new Date()+" clubunique: "+clubunique+" frequency: "+ frequency);

                        if (wappage != null) {
                            request.setAttribute("wappageurl", wappage);
                            session.setAttribute("wappageurl", wappage);

                            System.out.println("ZAHEADER WapUrl: " + wappage);

                            /*boolean exist=passivevisitordao.alreadyExist(msisdn, club.getUnique());
                             
                             //System.out.println("====PASSIVE USER CALLING ======= "+exist);
                             if(!exist)*/
                            if (!passivevisitordao.exists(msisdn, club.getUnique())) {
                                PassiveVisitor visitor = new PassiveVisitor();
                                visitor.setUnique(SdcMisc.generateUniqueId());
                                visitor.setClubUnique(club.getUnique());
                                visitor.setFollowUpFlag(0);
                                visitor.setParsedMobile(msisdn);
                                visitor.setStatus(0);
                                visitor.setCreated(new Date());
                                visitor.setCampaign(campaignId);
                                visitor.setLandignPage(landingPage);
                                visitor.setPubId(pubId);
                                passivevisitordao.insertPassiveVisitor(visitor);
                                //System.out.println("====PASSIVE USER CALLING END ======= ");

                            }
                            WapDoiResponse wapDoiResponse = new WapDoiResponse();
                            wapDoiResponse.setRequestUid(requestuid);
                            wapDoiResponse.setWapPage(wappage);
                            wapDoiResponse.setRespondedTime(new java.sql.Timestamp(System.currentTimeMillis()));
                            wapdoilogdao.saveWapResponse(wapDoiResponse);
                            //doRedirect(response, wappage);
                            return;
                        } else {
                            String errorwappage = "/error-message.jsp?message=Optin Page Request Failed";
                            request.setAttribute("wappageurl", errorwappage);
                            WapDoiResponse wapDoiResponse = new WapDoiResponse();
                            wapDoiResponse.setRequestUid(requestuid);
                            wapDoiResponse.setWapPage(wappage);
                            wapDoiResponse.setRespondedTime(new java.sql.Timestamp(System.currentTimeMillis()));
                            wapdoilogdao.saveWapResponse(wapDoiResponse);
                            //doRedirect(response, "/error-message.jsp?message=Optin Page Request Failed");
                            return;
                        }

                    }//END of WAP doi

                    if (operation.toLowerCase().trim().equals("smsoptin")) {

                        String requestUid = Misc.generateUniqueId();
                        request.setAttribute("wapoptin", "false");
                        String notificationurl = "http://" + dmn.getDefaultUrl() + "/smsresponse.jsp?phase=1&optin=1&cid=" + campaignId + "&myuid=" + requestUid + "&ip=" + ip + "&l=" + landingPage + "&uagent=" + uagent+"&pubid="+pubId;

                        boolean confirmed = false;
                        boolean hasNoRecentDOIRequests = check24hour.hasValidDOIRequests(msisdn, club);

                        String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");
                        if (userUnique != null && !userUnique.equals("")) {
                            user = umeuserdao.getUser(msisdn);
                        }
                        if (user != null) {
                            SdcMobileClubUser clubUser = user.getClubMap().get(club.getUnique());
                            if (clubUser == null) {
                                clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                            }
                            if (clubUser != null && clubUser.getActive() != 1) {
                                hasNoRecentDOIRequests = true;
                            }
                        }

                        String doirequest = "";

                        if (hasNoRecentDOIRequests) {
                            confirmed = zadoi.request_SMSOptIn(token, serviceid, clubname, frequency, msisdn, notificationurl, teaser, freeDay);
                            SmsDoiRequest smsDoiRequest = new SmsDoiRequest();
                            smsDoiRequest.setRequestUid(requestUid);
                            smsDoiRequest.setClubName(club.getName() + " " + ip + "-zaheader");
                            smsDoiRequest.setFrequency(frequency);
                            smsDoiRequest.setMsisdn(msisdn);
                            smsDoiRequest.setNotificationUrl(notificationurl);
                            smsDoiRequest.setToken(token);
                            smsDoiRequest.setRequestedTime(new java.sql.Timestamp(System.currentTimeMillis()));
                            smsdoilogdao.saveSmsDoiRequest(smsDoiRequest);
                            //System.out.println("ZA WAP DOI : "+"  SMS OPT IN RESPONSE MSISDN: "+msisdn+ " IS : "+confirmed+" at "+new Date()+" clubunique: "+clubunique+" frequency: "+ frequency);
                        }
                        if (confirmed) {
                            session.setAttribute("doismssent", "true");

                            if (cmpg != null && cmpg.getSrc().trim().endsWith("RS")) {

                                String parameter1 = (String) session.getAttribute("revparam1");
                                String parameter2 = (String) session.getAttribute("revparam2");
                                String parameter3 = (String) session.getAttribute("revparam3");

                                // 2016.01.13 - AS - Removed commented code, check repo history if needed
                                int updatedRows = cpaloggerdao.updateRevShareVisitorLogMsisdn(msisdn, cmpg.getUnique(), parameter1, parameter2, parameter3);

                            }

                            if (cmpg != null && cmpg.getSrc().trim().endsWith("CPA")) {

                                String cpaparameter1 = (String) session.getAttribute("cpaparam1");
                                String cpaparameter2 = (String) session.getAttribute("cpaparam2");
                                String cpaparameter3 = (String) session.getAttribute("cpaparam3");

                                // 2016.01.13 - AS - Removed commented code, check repo history if needed
                                int updatedRows = cpaloggerdao.updateCpaVisitLogMsisdn(msisdn, cmpg.getUnique(), cpaparameter1, cpaparameter2, cpaparameter3);

                            }

                            String confirmedUrl = "/" + dmn.getDefPublicDir() + "/smssuccess.jsp?msisdn=" + msisdn + "&cid=" + campaignId;
                            request.setAttribute("smsconfirmed", confirmedUrl);
                            /* boolean exist=passivevisitordao.alreadyExist(msisdn, club.getUnique());
                             if(!exist)*/
                            if (passivevisitordao.exists(msisdn, club.getUnique())) {
                                PassiveVisitor visitor = new PassiveVisitor();
                                visitor.setUnique(SdcMisc.generateUniqueId());
                                visitor.setClubUnique(club.getUnique());
                                visitor.setFollowUpFlag(0);
                                visitor.setParsedMobile(msisdn);
                                visitor.setStatus(0);
                                visitor.setCreated(new Date());
                                visitor.setCampaign(campaignId);
                                visitor.setLandignPage(landingPage);
                                visitor.setPubId(pubId);
                                 //savePassiveVisitor(PassiveVisitor passivevisitor, int stat)
                                //if stat==0 insert record and if stat=1 update record
                                passivevisitordao.insertPassiveVisitor(visitor);
                            }
                            SmsDoiResponse smsDoiResponse = new SmsDoiResponse();
                            smsDoiResponse.setRequestUid(requestUid);
                            smsDoiResponse.setClubName(club.getName());
                            smsDoiResponse.setConfirmed(confirmed);
                            smsDoiResponse.setMsisdn(msisdn);
                            smsDoiResponse.setToken(token);
                            smsDoiResponse.setRespondedTime(new java.sql.Timestamp(System.currentTimeMillis()));
                            smsdoilogdao.saveSmsDoiResponse(smsDoiResponse);

		                 //System.out.println("Sent msisdn to "+msisdn+" sms response is "+confirmed);
                            //response.sendRedirect("http://"+dmn.getDefaultUrl()+"/smssuccess.jsp?msisdn=" + msisdn+ "&cid=" + campaignId);
                            return;
                        } else if (confirmed == false) {
                            if (hasNoRecentDOIRequests == false) {
                                doirequest = "alreadysent";
                            }

                            String confirmedUrlfalse = "/" + dmn.getDefPublicDir() + "/smsfailure.jsp?msisdn=" + msisdn + "&doirequest=" + doirequest;
                            //System.out.println("Couldn't send msisdn to "+msisdn+" sms response is "+confirmed);
                            request.setAttribute("smsconfirmed", confirmedUrlfalse);
                            //response.sendRedirect("http://"+dmn.getDefaultUrl()+"/smsfailure.jsp?invalidno="+msisdn);
                            SmsDoiResponse smsDoiResponse = new SmsDoiResponse();
                            smsDoiResponse.setRequestUid(requestUid);
                            smsDoiResponse.setClubName(club.getName());
                            smsDoiResponse.setConfirmed(confirmed);
                            smsDoiResponse.setMsisdn(msisdn);
                            smsDoiResponse.setToken(token);
                            smsDoiResponse.setRespondedTime(new java.sql.Timestamp(System.currentTimeMillis()));
                            smsdoilogdao.saveSmsDoiResponse(smsDoiResponse);
                            return;

                        }
                        //end of smsdoi
                    }
                }
            }//End if (club.getRegion().equals("ZA") && msisdn!=null && msisdn.trim().length()>0) 
            else //If Club is not ZA or something went wrong while processing
            {
                System.out.println("ZAHEADER INSIDE ELSE CONDITION when club is not ZA OR something wrong "+club.getUnique()+" -- "+club.getRegion());
                session.setAttribute("confirmUser", null);
                request.setAttribute("confirmUser", null);
                request.setAttribute("subscriptionurl", "http://" + dmn.getDefaultUrl() + "/index.jsp?pg=subscribe&cid=" + campaignId + "&clubid="+club.getUnique()+"&redir=headerelse&msisdn=" + msisdn);
                return;
            }

        } //END IF NEED TO CONFIRM THIS USER
        else {
            System.out.println("ZAHEADER INSIDE IF confirm the user is false ");
        }
    }

    void doRedirect(HttpServletResponse response, String url) {
        String referer = "headers";
        //System.out.println("doRedirect: " + url);
        try {
            response.sendRedirect(url);
        } catch (Exception e) {
            System.out.println("doRedirect Exception: " + e);
        }
    }

    private String identifyOptin(String networkCode, String isp, Handset handset, String uagent) {
        String optin = "wapoptin";
        boolean isBlackBerryOrOpera = isp.trim().toLowerCase().indexOf("opera") > -1 || handset.getUnique().trim().toLowerCase().indexOf("blackberry") > -1
                || uagent.trim().toLowerCase().indexOf("opera") > -1 || uagent.trim().toLowerCase().indexOf("blackberry") > -1;

        if ((networkCode.trim().equalsIgnoreCase("vodacom") || networkCode.trim().equalsIgnoreCase("cellc")) && isBlackBerryOrOpera) {
            optin = "smsoptin";
        } //         else if(networkCode.trim().equalsIgnoreCase("cellc") || networkCode.trim().equalsIgnoreCase("cell-c")
        //                 || networkCode.trim().equalsIgnoreCase("mtn")
        //                 || (networkCode.trim().equalsIgnoreCase("vodacom") && !(isp.toLowerCase().contains("opera")))
        //
        //                )
        //         {
        //             optin="wapoptin";
        //         }
        else if (networkCode.equalsIgnoreCase("") || networkCode.equalsIgnoreCase("unknown")) {
            optin = "smsoptin";

        } else if (!(isp.trim().equalsIgnoreCase("") && isp.trim().length() <= 0) && !(isp.toLowerCase().trim().contains("cellc") || isp.toLowerCase().trim().contains("cell-c") || isp.toLowerCase().trim().contains("vodacom") || isp.toLowerCase().trim().contains("mtn")
                || (isp.toLowerCase().trim().contains("amazon")) || (isp.toLowerCase().trim().equalsIgnoreCase("Research In Motion UK Limited")))) {

            optin = "smsoptin";

        } else if (networkCode.equalsIgnoreCase("heita")) {
            optin = "smsoptin";

        }

         //Returning optin.
        System.out.println("zaoptin debug " + optin + " network: " + networkCode + " isp " + isp);
        return optin;
    }

    private String getNetworkid(String isp) {
        String networkid = "";

        if (isp != null && !(isp.trim().equalsIgnoreCase("") && isp.trim().length() <= 0)) {
            if (isp.toLowerCase().trim().contains("cellc") || isp.toLowerCase().trim().contains("cell-c")) {
                networkid = "cellc";
            } else if (isp.toLowerCase().trim().contains("mtn")) {
                networkid = "mtn";
            } else if ((isp.toLowerCase().trim().contains("vodacom") || isp.toLowerCase().trim().equalsIgnoreCase("Research In Motion UK Limited"))) {
                networkid = "vodacom";
            } else {
                networkid = "";
            }

        }
        return networkid;
    }

}
