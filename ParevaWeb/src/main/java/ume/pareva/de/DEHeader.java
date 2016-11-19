package ume.pareva.de;

import com.mitchellbosecke.pebble.PebbleEngine;
import java.io.IOException;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.BannerAdDao;
import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.cms.VideoClipDao;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.DoiResponseLogDao;
import ume.pareva.dao.DoiResult;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.PassiveVisitorDao;
import ume.pareva.dao.QueryHelper;
import ume.pareva.dao.SmsDoiLogDao;
import ume.pareva.dao.UmeLanguagePropertyDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.dao.UmeSmsDaoExtension;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.dao.WAPDoiLogDao;
import ume.pareva.pojo.SdcLanguageProperty;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.servlet.Check24Hour;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.userservice.CheckStop;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.userservice.UserAuthentication;
import ume.pareva.userservice.VideoList;

/**
 *
 * @author Madan
 */
@WebServlet(name = "DEHeader", urlPatterns = {"/DEHeader"})
public class DEHeader extends HttpServlet {
    
    
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
    private final Logger logger = LogManager.getLogger(DEHeader.class.getName());

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
        public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,config.getServletContext());
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession();
        RequestDispatcher rd = null;

        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        
        String ddir = dmn.getDefPublicDir();
	String lang = aReq.getLanguage().getLanguageCode();
	String stylesheet = aReq.getStylesheet();
	String pageEnc = aReq.getEncoding();

	String domain = dmn.getUnique();
	String fileName = request.getServletPath();
	fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
	fileName = fileName.substring(0, fileName.lastIndexOf("."));
        String campaignId = aReq.get("cid");
        String enMsisdn = aReq.get("mid");
        String redirecturl="";
        
        Map<String, String> dParamMap = UmeTempCmsCache.domainParameters.get(domain);
        MobileClubCampaign cmpg = null;
        
        boolean onMainIndexPage = fileName.indexOf("index") > -1;
        
      if (!campaignId.equals("")) {
		cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
	}
      
        String msisdn = "";
        String process = request.getParameter("process");
        
        String landingPage = (String) request.getAttribute("landingPage");
        if(landingPage==null) landingPage=(String) session.getAttribute("landingPage");
        String pubId= pubId=(String) request.getAttribute("cpapubid");
        if(pubId==null) pubId=(String) session.getAttribute("cpapubid");
        if(pubId==null) pubId="";
        
        String uid = "";
	String wapid ="";// request.getParameter("id");        
        String subpage = aReq.get("pg");
        
        boolean isUserActive = false;
        
         Handset handset = (Handset) session.getAttribute("handset");
        if (handset == null) {
            handset = (Handset) request.getAttribute("handset");
        }
        if (handset == null) {
            handset = handsetdao.getHandset(request);
        }
        session.setAttribute("handset", handset);
        request.setAttribute("handset", handset);
        
        PebbleEngine engine = null;
        UmeClubDetails clubdetails = null;//
        SdcLanguageProperty lp = langpropdao.get("general", service, aReq.getLanguage(), dmn);

	MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
        
         if (club != null) {
            clubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        }
         
         
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
        session.setAttribute("userip", ip);
         
        String isp = internetserviceprovider.findIsp(ip);
        
        SdcMobileClubUser clubuser = null;
        

	String clubUnique = "";

	if (club != null) {
		clubUnique = club.getUnique();
	}
        
        
           if (!enMsisdn.equals("")) { // relying on no font colour being given. Prone to future failure
            String deMsisdn = MiscCr.decrypt(enMsisdn);
            //System.out.println("madandecryptingmsisdn: "+deMsisdn+" campaignID: "+campaignId+"  ENmsisdn: "+enMsisdn);
            msisdn = deMsisdn;
            campaigndao.log("zaecnrypted", landingPage, msisdn, msisdn, handset, domain, campaignId, club.getUnique(), "IDENTIFIED", 0, request, response,isp.toLowerCase(),"","","",pubId);
            }
        
        
        
        
        
        if (isNotNullEmpty(aReq.get("msisdn"))) {
		msisdn = aReq.get("msisdn");
		
		if(!msisdn.startsWith("00")){
			msisdn="00"+msisdn.trim();
		}
		
		session.setAttribute("msisdn", msisdn);
		session.setAttribute("sdc_msisdn_param", msisdn);
	}



	if (isNullEmpty(subpage)) {
		subpage = request.getParameter("pg");
		if (isNullEmpty(subpage)) {
			subpage = "";
		}
	}
        
        if ((isNullEmpty(msisdn)) && null != session.getAttribute("msisdn")) {
		msisdn = (String) session.getAttribute("msisdn");
	}
	if ((isNotNullEmpty(msisdn)) && null == session.getAttribute("msisdn")) {
		session.setAttribute("msisdn", msisdn);
	}

	//The lower portion is improtant because other file find user through this param sdc_msisdn_param
	if (isNotNullEmpty(msisdn) && null == session.getAttribute("sdc_msisdn_param")) {
		session.setAttribute("sdc_msisdn_param", msisdn);
	}

	//***********************************************************
	// Initilize User
	//***********************************************************

	
           System.out.println("Germany test clubunique is "+clubUnique);
	if (user == null) {
		if (clubUnique != null && msisdn != null && msisdn.length()>0){
                    System.out.println("Germany test "+ " clubunique "+clubUnique+" msisdn "+msisdn);
	            clubuser = umemobileclubuserdao.getClubUserByMsisdn(msisdn,clubUnique);
		   

                }
		 
                else if(clubuser!=null) 
                    user = umeuserdao.getUser(clubuser.getParsedMobile());
                
                //System.out.println("Germany test "+" Global wap header: "+ " user is null "+ user);

	}
        
        	if (user != null) {
                    clubuser = umemobileclubuserdao.getClubUserByMsisdn(msisdn,clubUnique);
		isUserActive = mobileclubdao.isActive(user, club);
                
	}
	
	if(user!=null && clubuser!=null){
                        System.out.println("Germany test : user "+user+" "+ user.getUnique()+" clubuser: "+clubuser.getUserUnique()+" "+clubuser.getClubUnique()+" active status: "+clubuser.getActive());
                        System.out.println("Germany test : isUserActive "+isUserActive );
            }
	

	if (isUserActive) {
		uid = user.getUnique();
		msisdn = user.getParsedMobile();
		wapid = user.getWapId();

	}
        
        if( !(fileName.indexOf("video") > -1 && isUserActive)){
		 

	if (isNotNullEmpty(process)
			&& (process.trim().equals("msisdnentryform")|| process.trim().toLowerCase().equals("start"))
			&& isNotNullEmpty(msisdn) && user != null && isUserActive
			&& isNotNullEmpty(uid) && isNotNullEmpty(wapid)) {

		//This is because if user is wifi and he put the msisdn no which is already a subscriber member then dont go for pin .. forward it to his wap id
		process = null;

		doRedirect(response, "http://" + dmn.getDefaultUrl() + "/?id="+ wapid,session);

		//doRedirect(response, "/index_main.jsp?cid=" + campaignId+ "&pg=" + subpage);
		return;

	}

	// **************************************************************************************************
	// This section is responsible for Redirection to index_main 
	// **************************************************************************************************

	boolean redirOk = true;

	if (!subpage.equals(""))
		redirOk = false;

	else if (fileName.indexOf("promo_") > -1)
		redirOk = false;

	else if (fileName.indexOf("de_") > -1)
		redirOk = false;

	else if (fileName.equals("error"))
		redirOk = false;

	else if (fileName.equals("notification"))
		redirOk = false;

	else if (fileName.indexOf("confirm") > -1)
		redirOk = false;

	else if (fileName.indexOf("index") > -1)
		redirOk = false;

	else if (!aReq.get("redir").equals(""))
		redirOk = false;
	
	

	if (redirOk && (user == null || !isUserActive) && club != null
			&& club.getOptIn() > 0) {

            //System.out.println(" Germany ::>> "+ " Global wap header Redirection "+ "/index_main.jsp?cid=" + campaignId+ "&pg=" + subpage );
		doRedirect(response, "/index_main.jsp?cid=" + campaignId+ "&pg=" + subpage,session);
		return;

	}

	// **************************************************************************************************
	// This section is responsible for processing the response from the MSISDN lookup in NTH System
	// **************************************************************************************************

	if (isNullEmpty(process)
			&& isNotNullEmpty((String) session.getAttribute("process"))) {
		process = (String) session.getAttribute("process");
	} else if (isNotNullEmpty(process)) {
		session.setAttribute("process", process);
	}

	if (onMainIndexPage) {

		if (user == null || !mobileclubdao.isActive(user, club)) {
			if (isNullEmpty(msisdn) && isNullEmpty(process)) {
				doRedirect(response, "/de_msisdnlookup.jsp?cid="+ campaignId + "&pg=" + subpage,session);
				return;

			}
		}

	}
	}
        
        engine = templateEngine.getTemplateEngine(domain);//
        request.setAttribute("engine", engine);
        session.setAttribute("engine",engine);
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
        session.setAttribute("landingpage",landingpage); // LandingPage object
        request.setAttribute("landingPage", landingPage); //Landing page name
        session.setAttribute("landingPage",landingPage);
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
        request.setAttribute("templateengine", templateEngine);
        request.setAttribute("passivevisitordao", passivevisitordao);
        request.setAttribute("networkid", isp.toLowerCase());
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
        request.setAttribute("deprocess",process);
        session.setAttribute("deprocess",process);
        
        
        

    }
    
    void addAttributeInSession(HttpSession ses, String key, String value) {

		if (isNotNullEmpty(value)) {
			ses.setAttribute(key, value);
                }
	}

	boolean isNullEmpty(String value) {

		return (value == null || (value != null && value.trim().length() <= 0) || (value != null && value.trim().toLowerCase().equals("null")));

	}

	boolean isNotNullEmpty(String value) {
		return (value != null && value.trim().length() > 0 && !(value.trim().toLowerCase().equals("null")));
	}
        
        void doRedirect(HttpServletResponse response, String url_,HttpSession session) {
		String referer = "headers";
		try {
			//response.sendRedirect(url_);
                    session.setAttribute("redirecturl", url_);
                    
		} catch (Exception e) {
			System.out.println("doRedirect EXCEPTION: " + e);
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
