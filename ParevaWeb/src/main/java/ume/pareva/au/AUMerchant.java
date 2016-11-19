package ume.pareva.au;

import com.mitchellbosecke.pebble.PebbleEngine;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.cms.VideoClipDao;
import ume.pareva.dao.CpaAdvertiserDao;
import ume.pareva.dao.CpaVisitLogDao;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.PassiveVisitorDao;
import ume.pareva.dao.UmeLanguagePropertyDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.SdcLanguageProperty;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.dao.RevSharePartersDao;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;
import ume.pareva.userservice.UserAuthentication;
import ume.pareva.userservice.VideoList;
import ume.pareva.util.ZACPA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import ume.pareva.cms.BannerAdDao;
import ume.pareva.dao.AusTrackingDao;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.pojo.AusTracking;
import ume.pareva.userservice.SubscriptionCreation;

/**
 *
 * @author madan
 */
public class AUMerchant extends HttpServlet {

    @Autowired
    CpaVisitLogDao cpavisitlogdao;

    @Autowired
    CpaAdvertiserDao advertiserdao;

    @Autowired
    RevSharePartersDao revsharepartnersdao;

    @Autowired
    Misc misc;

    @Autowired
    MobileClubDao mobileclubdao;

    @Autowired
    UmeUserDao umeuserdao;

    @Autowired
    MobileClubCampaignDao campaigndao;

    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;

    @Autowired
    TemplateEngine templateEngine;//

    @Autowired
    InternetServiceProvider internetserviceprovider;

    @Autowired
    LandingPage landingpage;

    @Autowired
    UmeClubDetailsDao clubdetailsdao;

    @Autowired
    MobileClubBillingPlanDao billingplandao;

    @Autowired
    ZACPA zacpalog;

    @Autowired
    CampaignHitCounterDao campaignhitcounterdao;

    @Autowired
    UserAuthentication userauthentication;

    @Autowired
    UmeLanguagePropertyDao langpropdao;

    @Autowired
    VideoClipDao videoclipdao;

    @Autowired
    VideoList videolist;

    @Autowired
    PassiveVisitorDao passivevisitordao;
    
    @Autowired
    CpaLoggerDao cpaloggerdao;
    
    @Autowired
    SubscriptionCreation subscriptioncreation;
    
    @Autowired
    AusTrackingDao austrackingdao;
    
    @Autowired
    BannerAdDao banneraddao;

    static final Logger logger = LogManager.getLogger(AUMerchant.class.getName());

    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ThreadContext.put("ROUTINGKEY", "AU");
        logger.info(" ===== AUMerchant SERVLET INIT ===== ");
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ThreadContext.put("ROUTINGKEY", "AU");
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        RequestDispatcher rd = null;

        String cpaparam1 = "", cpaparam2 = "", cpaparam3 = "", revparam1 = "", revparam2 = "", revparam3 = "",cpapubid="";/*,cpaquery="",publisherid=""*/;
        String transID = ""; // TRANSACTION
        String serviceID = "";
        String msisdn = "";
        String status = "";
        String landingPage="";
        String userIp = getClientIpAddr(request);
        String campaignId = "";
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf2.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg"));
        String networkid = ""; //This is not yet populated

        //System.out.println("xstreamtesting: Servlet callupon "+session.getId());
        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();

        String domain = dmn.getUnique();

        Map<String, String> dParamMap = UmeTempCmsCache.domainParameters.get(domain);
        if (dParamMap == null) {
            dParamMap = new HashMap<String, String>();
        }
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);

        //Campaign identification
        String cid = aReq.get("cid");
        String subpage = aReq.get("pg");
        MobileClubCampaign cmpg = null;
        if (!cid.equals("")) {
            cmpg = UmeTempCmsCache.campaignMap.get(cid);
        }
//        System.out.println("AUMERCHANT cid=" + cid + " CAMPAIGN = " + cmpg.getUnique() + " " + cmpg.getCampaign());

        PebbleEngine au_engine = null;
        UmeClubDetails userclubdetails = null;//
        SdcLanguageProperty lp = langpropdao.get("general", service, aReq.getLanguage(), dmn);

        if (club != null) {
            userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        }
        System.out.println("AUMERCHANT CLUB = " + club.getUnique());

        au_engine = templateEngine.getTemplateEngine(domain);

        request.setAttribute("au_engine", au_engine);
        request.setAttribute("aReq", aReq);
        request.setAttribute("dmn", dmn);
        request.setAttribute("service", service);
        request.setAttribute("mobileclubdao", mobileclubdao);
        request.setAttribute("videoclipdao", videoclipdao);
        request.setAttribute("misc", misc);
        request.setAttribute("umeuserdao", umeuserdao);
        request.setAttribute("umemobileclubuserdao", umemobileclubuserdao);
        request.setAttribute("campaigndao", campaigndao);
        request.setAttribute("videolist", videolist);
        request.setAttribute("landingpage", landingpage);
        request.setAttribute("club", club);
        request.setAttribute("dParamMap", dParamMap);
 //       request.setAttribute("cmpg", cmpg);
        request.setAttribute("campaignId", campaignId);
        request.setAttribute("subpage", subpage);
        request.setAttribute("clubdetails", userclubdetails);
        request.setAttribute("templateengine", templateEngine);
        request.setAttribute("passivevisitordao", passivevisitordao);
        request.setAttribute("campaignhitcounterdao", campaignhitcounterdao);
        request.setAttribute("userauthentication", userauthentication);
        request.setAttribute("banneraddao",banneraddao);

        /**
         * Reading parameters from Australian Aggregator parametersMap variable
         * compares the parameters and redirects to appropriate page based on
         * Status
         */
        Map<String, String> parametersMap = new HashMap<>();

        for (String key : request.getParameterMap().keySet()) {
            System.out.println("AUMERCHANT " + key + " -- " + request.getParameter(key));
            parametersMap.put(key.toLowerCase().trim(), request.getParameter(key).trim());
        }
        System.out.println("AUMERCHANT parametersMap " + parametersMap.get("status") + "  " + parametersMap.get("msisdn") + "   " + parametersMap.get("reason"));
        msisdn = parametersMap.get("msisdn");
        request.setAttribute("msisdn", msisdn);
        
        try{
        austrackingdao.updateTransId(parametersMap.get("transactionid"), msisdn,parametersMap.get("status"),parametersMap.get("reason"));
        }catch(Exception e){
            try{
        austrackingdao.updateTransId(parametersMap.get("transactionid"),msisdn);
            }catch(Exception ex){}
        }
        
        if (parametersMap.get("status") == null || msisdn == null || msisdn.isEmpty()) {
            // Australia didn't send Status or MSISDN parameter
            // Redirection to aufilure page for not receiving any parameters
            System.out.println("AUMERCHANT STATUS OR MSISDN WERE NOT SENT BACK, REDIRECTING TO AUFAILURE");
            logger.info("AUMERCHANT STATUS OR MSISDN WERE NOT SENT BACK, REDIRECTING TO AUFAILURE");
            response.sendRedirect("/aufailure.jsp?param=null");
        } else if (parametersMap.get("status") != null && parametersMap.get("msisdn") != null && parametersMap.get("reason") != null
                && parametersMap.get("reason").equalsIgnoreCase("REPEAT_SUBSCRIPTION_NOT_PERMITTED")) {
            // This means User is actively subscribed to the service
            System.out.println("AUMERCHANT REPEAT SUBSCRIPTION CONDITION TRUE ");
            logger.info("AUMERCHANT REPEAT SUBSCRIPTION CONDITION TRUE "+msisdn+" status is "+parametersMap.get("status")+" reason is "+parametersMap.get("reason"));
         

            String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");
            System.out.println("AUMERCHANT MSISDN NOT NULL so userUnique " + userUnique);
            logger.info("AUMERCHANT MSISDN NOT NULL so userUnique " + userUnique);
            if (!userUnique.equals("")) {
                user = umeuserdao.getUser(msisdn);
            }

            System.out.println("AUMERCHANT USER OBJECT " + user.getParsedMobile() + " " + user.getUnique());
            logger.info("AUMERCHANT USER OBJECT " + user.getParsedMobile() + " " + user.getUnique());
            if (user != null) {
                SdcMobileClubUser clubUser = user.getClubMap().get(club.getUnique());
                request.setAttribute("user", user);
                if (clubUser == null) {
                    clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                }
                System.out.println("AUMERCHANT CLUBUSER RECORD " + clubUser.toString());
                if (clubUser != null && clubUser.getActive() == 1) {

                    System.out.println("AUMERCHANT " + " clubUser " + clubUser.toString());
                    System.out.println("AUMERCHANT Now Redirecting to  INDEX MAIN .jsp FOR resubscription  ");
                    rd = this.getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/index_main.jsp");
                    rd.include((ServletRequest) request, (ServletResponse) response);
                }
            }
        } else {
            transID = parametersMap.get("transactionid"); // TRANSACTIONID requested while subscribing users
            serviceID = parametersMap.get("serviceid"); // Australian Service id .. most probably club otpservicename
            msisdn = parametersMap.get("msisdn");
            status = parametersMap.get("status");
            String reason = parametersMap.get("reason");
            
               try{
                AusTracking austracking=austrackingdao.getAusTracking(transID, msisdn);
                landingPage=austracking.getLandingPage();
                campaignId=austracking.getCampaignid();
            }catch(Exception e){};

            //Successful Subscription and Billing
            if (null != status && !"".equals(status) && "Successful".equalsIgnoreCase(status)) {
                //Subscription Starts Here now !! 
                // Success
                // SUBSCRIPTION CODE GOES HERE

                System.out.println("AUMERCHANT " + transID + " serviceId " + serviceID + " msisdn " + msisdn + " " + status + " " + reason);
                Cookie[] cookies = null;
                cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("cid") && (cookie.getValue() != null && !cookie.getValue().isEmpty())) {
                            campaignId = cookie.getValue();

                            request.setAttribute("campaignId", campaignId);

                        }

                        if (null != campaignId && !"".equalsIgnoreCase(campaignId)) {
                            cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
                            if (null != cmpg) {
                                request.setAttribute("cmpg", cmpg);
                                if (!"".equalsIgnoreCase(cmpg.getSrc()) && cmpg.getSrc().endsWith("CPA")) {
                                    if (cookie.getName().equals("cpaparam1") && (cookie.getValue() != null && !cookie.getValue().isEmpty())) {
                                        cpaparam1 = cookie.getValue();
                                    }
                                    if (cookie.getName().equals("cpaparam2") && (cookie.getValue() != null && !cookie.getValue().isEmpty())) {
                                        cpaparam2 = cookie.getValue();
                                    }
                                    if (cookie.getName().equals("cpaparam3") && (cookie.getValue() != null && !cookie.getValue().isEmpty())) {
                                        cpaparam3 = cookie.getValue();
                                    }
                                    if(cookie.getName().equals("cpapubid") && (cookie.getValue()!=null && !cookie.getValue().isEmpty())){
                                        cpapubid=cookie.getValue();
                                    }
                                } // end CPA cookies condition

                                if (!"".equalsIgnoreCase(cmpg.getSrc()) && cmpg.getSrc().endsWith("RS")) {
                                    if (cookie.getName().equals("revparam1") && (cookie.getValue() != null && !cookie.getValue().isEmpty())) {
                                        revparam1 = cookie.getValue();
                                    }
                                    if (cookie.getName().equals("revparam2") && (cookie.getValue() != null && !cookie.getValue().isEmpty())) {
                                        revparam2 = cookie.getValue();
                                    }
                                    if (cookie.getName().equals("revparam3") && (cookie.getValue() != null && !cookie.getValue().isEmpty())) {
                                        revparam3 = cookie.getValue();
                                    }
                                    
                                } //End Revshare cookies condition

                            } // end if cmpg is not null condition

                        } //end if campaignid is not null
                    } //end loop Cookies:cookie
                } //end if cookies!=null

                // Create a User and Subscription 
                Calendar c1 = new GregorianCalendar();
                Calendar dateC = new GregorianCalendar();
                Date bstart = c1.getTime();
                String ume_manualSubscribed = sdf2.format(bstart);
                Date getNowTime = dateC.getTime();
                dateC.add(Calendar.DATE, 7);
                Date bend = dateC.getTime();
                SdcMobileClubUser clubUser = null;
                MobileClubBillingPlan billingplan = null;
                
                
                //======================= USER SUBSCRIPTION STARTS ===========================================
                if(userclubdetails!=null && userclubdetails.getBillingType().equalsIgnoreCase("subscription")){               
           
                String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");
                if (userUnique != null && !userUnique.equals("")) {
                    user = umeuserdao.getUser(msisdn);
                }
                clubUser = user.getClubMap().get(club.getUnique());
                if (clubUser == null) {
                    clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                }
              
                //CPA and RS Logging after club User Creation 
                        if (cmpg != null && cmpg.getSrc().trim().toLowerCase().endsWith("cpa")
                                && cmpg.getCpaType().equalsIgnoreCase("subscription")) {
                            
                            // 2016.01.13 - AS - Removed commented code, check repo history if needed
                            int insertedRows = cpaloggerdao.insertIntoCpaLogging(msisdn, campaignId, club.getUnique(), 10, networkid, cmpg.getSrc());

                            // 2016.01.13 - AS - Removed commented code, check repo history if needed
                            int updatecpavisit = cpaloggerdao.updateCpaVisitLogMsisdnSubscriptionDate(msisdn, clubUser.getSubscribed(), cmpg.getUnique(), cpaparam1, cpaparam2, cpaparam3);

                        } //End CPA subscription

                        if (cmpg != null && cmpg.getSrc().trim().endsWith("RS")) {
                            // 2016.01.13 - AS - Removed commented code, check repo history if needed
                            String enMsisdn = MiscCr.encrypt(msisdn);
                            // 2016.01.13 - AS - Removed commented code, check repo history if needed
                            int insertedRows =  cpaloggerdao.insertIntoRevShareLogging(0,  cmpg.getPayoutCurrency(), msisdn, enMsisdn, campaignId, club.getUnique(), 0, networkid, cmpg.getSrc(), 0);
                            // 2016.01.13 - AS - Removed commented code, check repo history if needed
                            int updatedRows = cpaloggerdao.updateRevShareVisitLogMsisdnSubscriptionDate(msisdn, clubUser.getSubscribed(), cmpg.getUnique(), revparam1, revparam2, revparam3);

                        } //end RevShare Logging
            }
         //end if clubDetails is subscription type
                 //End user creation and subscription
                System.out.println("AUMERCHANT  REDIRECTING TO INDEX MAIN .jsp ");
                //doRedirect(response,"/index_main.jsp"); 
                rd = this.getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/index_main.jsp");
                rd.include((ServletRequest) request, (ServletResponse) response);
                } // end Successful Notification 

               
             //end if Successful Transaction STatus
            else {
                //Transaction Failure Redirection goes here 
                /**
                 * transID = parametersMap.get("TransID"); // TRANSACTIONID
                 * requested while subscribing users serviceID =
                 * parametersMap.get("ServiceID"); // Australian Service id ..
                 * most probably club otpservicename
                 * msisdn=parametersMap.get("MSISDN");
                 * status=parametersMap.get("Status"); String
                 * reason=parametersMap.get("Reason");
                 */
                doRedirect(response, "/aufailure.jsp?msisdn=" + msisdn + "&transid=" + transID + "&status=" + status + "&reason=" + reason);
            }

        }
    }

    private String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
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

        if (ip != null) {
            int idx = ip.indexOf(',');
            if (idx > -1) {
                ip = ip.substring(0, idx);
            }
        }

        return ip;
    }

    private void doRedirect(HttpServletResponse response, String page) {
        try {
            response.sendRedirect(page);
        } catch (Exception e) {
            System.out.println("australia testing java doRedirect EXCEPTION: " + e);
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
