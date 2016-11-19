package ume.pareva.it.servlets;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import ume.pareva.dao.UmeLanguagePropertyDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.it.IpxBillingDirect;
import ume.pareva.pojo.SdcLanguageProperty;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;
import ume.pareva.userservice.VideoList;
import ume.pareva.util.ZACPA;

import com.mitchellbosecke.pebble.PebbleEngine;
import java.text.SimpleDateFormat;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import ume.pareva.cms.BannerAdDao;
import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.dao.SdcRequest;

import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.ipx.extra.IpxBroadcastSmsLogDao;
import ume.pareva.ipx.extra.IpxSisterSmsLogDao;

import ume.pareva.it.IpxTimerSendSms;
import ume.pareva.smsservices.SmsService;
import ume.pareva.snp.CacheManager;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.userservice.StopUser;
import ume.pareva.userservice.UserAuthentication;

/**
 * Servlet implementation class GlobalWapHeader
 */
//@WebServlet("/GlobalWapHeader")
public class GlobalWapHeaderIT extends HttpServlet {

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
    TemplateEngine templateEngine;//

    @Autowired
    InternetServiceProvider internetserviceprovider;

    @Autowired
    LandingPage itlandingpage;

    @Autowired
    Misc misc;

    @Autowired
    UmeClubDetailsDao clubdetailsdao;

    @Autowired
    MobileClubBillingPlanDao billingplandao;

    @Autowired
    ZACPA zacpa;

    @Autowired
    DoiResult doiresult;

    @Autowired
    IpxBillingDirect directBilling;

    @Autowired
    CacheManager cachemanager;
    
    @Autowired
    UserAuthentication userauthentication;
    
    @Autowired
    CampaignHitCounterDao campaignhitcounterdao;
    
    @Autowired
    IpxTimerSendSms timersms;
       
    @Autowired
    IpxSisterSmsLogDao ipxsistersmslogdao;
   
    @Autowired
    IpxBroadcastSmsLogDao ipxbroadcastsmslogdao;
    
    @Autowired
    CpaLoggerDao cpaloggerdao;
     
    @Autowired
    BannerAdDao banneraddao;
     
    @Autowired
    StopUser stopUserDao;
     
    @Autowired
    SmsService smsDao;

    
    private static final long serialVersionUID = 1L;

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

        //System.out.println("IT FILTER  GLOBAL WAP HEADER IS CALLED UPON.... 1 ");

        HttpSession session = request.getSession();
        RequestDispatcher rd = null;

        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        //System.out.println("IT FILTER  GLOBAL WAP HEADER IS CALLED UPON.... 2 ");

        String domain = dmn.getUnique();
        String ddir = dmn.getDefPublicDir();
        String lang = aReq.getLanguage().getLanguageCode();

        String durl = dmn.getDefaultUrl().toString().trim();
        String stylesheet = aReq.getStylesheet();
        String pageEnc = aReq.getEncoding();
        request.setAttribute("pageEnc", pageEnc);
        response.setContentType("text/html; charset=" + pageEnc);

        String stsReturnId = aReq.get("ID"); //This ID is received only after msisdn lookup process which must be equivalent to sts_id declared below. 

        String fileName = request.getServletPath();
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        request.setAttribute("fileName", fileName);

        request.setAttribute("domain", domain);

        Handset handset = (Handset) session.getAttribute("handset");
        PebbleEngine it_engine = null;
        UmeClubDetails clubdetails = null;//		 
        SdcLanguageProperty lp = langpropdao.get("general", service, aReq.getLanguage(), dmn);
        
        PebbleEngine za_engine=null;

        SdcRequest aReqSdc = new SdcRequest(request);

        String landingpage=(String) request.getAttribute("landingPage");
        if (landingpage==null || landingpage.isEmpty())
            landingpage=(String) session.getAttribute("landingPage");  
        
        
        //System.out.println("IT FILTER  GLOBAL WAP HEADER IS CALLED UPON.... 3 ");

        boolean onMainIndexPage = fileName.indexOf("index") > -1;

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
            //System.out.println("italylanding  GlobalWapHeader "+cmpg.toString()+"  campaignId "+campaignId);
        }

        String uid = "";
        String wapid = "";
		//READING IP of USER

        String xprof = aReq.get("xprof");
        String subpage = aReq.get("pg");
        String msisdn = aReq.get("msisdn");
        HttpSession ses = request.getSession();

        if (handset == null) {
            handset = handsetdao.getHandset(request);
        }
        session.setAttribute("handset", handset);

//		System.out.println("User "+user);
        if (user != null) {
            uid = user.getUnique();
            //System.out.println("UID: " + uid);
            msisdn = user.getParsedMobile();
            wapid = user.getWapId();
            sbf.append("USER AUTHENTICATED: " + uid + ": " + msisdn + crlf);
           // System.out.println("USER AUTHENTICATED: " + uid + ": " + msisdn + crlf);
        }

        //******* IT GLOBAL HEADER FROM HERE
        String clubIPXUserName = "";
        String clubIPXPassword = "";

        //String clubName = "";
        if (club != null) {
            clubIPXUserName = club.getOtpSoneraId();
            clubIPXPassword = club.getOtpTelefiId();

            if (!clubIPXUserName.equals("") && !clubIPXPassword.equals("")) {
                session.setAttribute("clubIPXUserName", clubIPXUserName);
                session.setAttribute("clubIPXPassword", clubIPXPassword);
                session.setAttribute("ipx_clubUnique", club.getUnique());
                session.setAttribute("clubServiceMetadata", club.getOtpServiceName());
            }
        }

        if (user != null) {
            uid = user.getUnique();
            //System.out.println("UID: " + uid);
            msisdn = user.getParsedMobile();
            wapid = user.getWapId();
           // System.out.println("USER AUTHENTICATED: " + uid + ": " + msisdn + crlf);
        }
        

        String adultcontent = dParamMap.get("adultcontent");
        if (adultcontent == null) {
            adultcontent = "0";
        }

        boolean trickyTime = false;
        Calendar cal = GregorianCalendar.getInstance();
        int HOUR_OF_DAY = cal.get(Calendar.HOUR_OF_DAY);
        boolean nightTime = false;
        if (HOUR_OF_DAY >= 17 || HOUR_OF_DAY < 5) {
            nightTime = true;
            if (!campaignId.equals("")) {
                trickyTime = true;
            }
        }
        
        za_engine=templateEngine.getTemplateEngine(domain);
                request.setAttribute("engine",za_engine);

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
        request.setAttribute("itlandingpage", itlandingpage);
        request.setAttribute("landingpage", landingpage);
        request.setAttribute("club", club);
        request.setAttribute("cmpg", cmpg);
        request.setAttribute("campaignId", campaignId);
        request.setAttribute("dParamMap", dParamMap);
        request.setAttribute("subpage", subpage);
        request.setAttribute("msisdn", msisdn);
        request.setAttribute("billingplandao", billingplandao);
        request.setAttribute("clubdetails", clubdetails);
        request.setAttribute("templateengine", templateEngine); //
        request.setAttribute("trickyTime", trickyTime);
        request.setAttribute("nightTime", nightTime);
        request.setAttribute("ipxdirectbilling", directBilling);
        request.setAttribute("cachemanager", cachemanager);
        request.setAttribute("cpaloggerdao", cpaloggerdao);
        // request.setAttribute("zacpa",zacpa);
        // request.setAttribute("userauthentication",userauthentication);
        // request.setAttribute("campaignhitcounterdao",campaignhitcounterdao);
        
        // request.setAttribute("aReqSdc", aReqSdc);
        // request.setAttribute("timersms",timersms);
        // request.setAttribute("ipxsistersmslogdao", ipxsistersmslogdao);
        // request.setAttribute("ipxbroadcastsmslogdao", ipxbroadcastsmslogdao);        
        // request.setAttribute("internetserviceprovider", internetserviceprovider);

        request.setAttribute("ipxsistersmslogdao", ipxsistersmslogdao);
        request.setAttribute("ipxbroadcastsmslogdao", ipxbroadcastsmslogdao);        
        request.setAttribute("zacpa",zacpa);
        request.setAttribute("userauthentication",userauthentication);
        request.setAttribute("campaignhitcounterdao",campaignhitcounterdao);
        
        request.setAttribute("aReqSdc", aReqSdc);
        request.setAttribute("timersms",timersms);
        request.setAttribute("ipxsistersmslogdao", ipxsistersmslogdao);
        request.setAttribute("ipxbroadcastsmslogdao", ipxbroadcastsmslogdao);        
        request.setAttribute("internetserviceprovider", internetserviceprovider);
        request.setAttribute("banneraddao",banneraddao);
        request.setAttribute("stopUserDao",stopUserDao);
        request.setAttribute("smsDao",smsDao);
        request.setAttribute("endofmonth",getEndOfmonth());
        request.setAttribute("endofyear",getEndOfYear());

        
    }

    
    String getEndOfmonth(){
        Calendar nowTime=GregorianCalendar.getInstance();
        SimpleDateFormat formattr=new SimpleDateFormat("yyyy-MM-dd");
        String currentDate=formattr.format(nowTime.getTime()).toString();
        System.out.println("currentDate "+currentDate);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dateTime = dtf.parseDateTime(currentDate);
        DateTime lastDate = dateTime.dayOfMonth().withMaximumValue();
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yy");
        String endofmonth=dtfOut.print(lastDate);
        return endofmonth;
    }
    String getEndOfYear(){
        Calendar nowTime=GregorianCalendar.getInstance();
        SimpleDateFormat formattr=new SimpleDateFormat("yyyy-MM-dd");
        String currentDate=formattr.format(nowTime.getTime()).toString();
        System.out.println("currentDate "+currentDate);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dateTime = dtf.parseDateTime(currentDate);
        DateTime lastDate = dateTime.dayOfYear().withMaximumValue();
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yy");
        String endofyear=dtfOut.print(lastDate);
        return endofyear;
    }
    void doRedirect(HttpServletResponse response, String url) {
        String referer = "headers";
        //System.out.println("doRedirect: " + url);
        try {
            response.sendRedirect(url);
        } catch (Exception e) {
            System.out.println("GlobalwapHeader IT : " + e);
        }
    }
}
