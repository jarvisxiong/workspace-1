package ume.pareva.de;

import com.mitchellbosecke.pebble.PebbleEngine;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.PassiveVisitorDao;
import ume.pareva.dao.QueryHelper;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.PassiveVisitor;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.userservice.CheckStop;
import ume.pareva.userservice.LandingPage;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.userservice.UserAuthentication;

/**
 *
 * @author Madan
 */
@WebServlet(name = "DEConfirm", urlPatterns = {"/DEConfirm"})
public class DEConfirm extends HttpServlet {

    @Autowired
    UmeUserDao umeuserdao;

    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;

    @Autowired
    MobileClubBillingPlanDao billingplandao;

    @Autowired
    MobileClubCampaignDao campaigndao;

    @Autowired
    PassiveVisitorDao passivevisitordao;

    @Autowired
    HandsetDao handsetdao;
    
     @Autowired
    QueryHelper queryhelper;

    @Autowired
    CpaLoggerDao cpaloggerdao;
    
    @Autowired
    UserAuthentication userauthentication;

    //CheckStop checkstop=(CheckStop) request.getAttribute("checkstop");
    @Autowired
    CheckStop checkstop;
    
    @Autowired
    UmeClubDetailsDao umeclubdetailsdao;
    
    @Autowired
    MobileClubDao mobileclubdao;
    
    @Autowired
    UmeTempCache umesdc;

    //SubscriptionCreation subscriptioncreation=(SubscriptionCreation) request.getAttribute("subscriptioncreation");
    @Autowired
    SubscriptionCreation subscriptioncreation;
    
    private final Logger logger = LogManager.getLogger(DEConfirm.class.getName());
    
    
      /**
     * This method is needed to support Autowired Spring beans
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }
    
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        ThreadContext.put("ROUTINGKEY", "DE");
        ThreadContext.put("EXTRA", "");
        
        HttpSession session = request.getSession();
      

        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();

        String msisdn=(String)request.getAttribute("msisdn");
        MobileClub club=(MobileClub)request.getAttribute("club");
        MobileClubCampaign cmpg=(MobileClubCampaign)request.getAttribute("cmpg");
        PebbleEngine engine=(PebbleEngine)request.getAttribute("engine");
        //String domain = (String)request.getAttribute("domain");
        LandingPage landingPage=(LandingPage)request.getAttribute("landingpage");
        //LandingPage_campaign landingpage=(LandingPage_campaign)request.getAttribute("landingpage");
        String wapid = (String)request.getAttribute("wapid");
        String networkid=(String)request.getAttribute("networkid");
        String subpage=(String) request.getAttribute("subpage");
        //String networkid="";
         Handset userhandset = (Handset) session.getAttribute("handset");
        if (userhandset == null) {
            userhandset = handsetdao.getHandset(request);
        }
        String optintype="smsoptin";

        String cid=(String)request.getAttribute("campaignId");
        String process=(String) request.getAttribute("deprocess");
        if(process==null) process=(String) session.getAttribute("deprocess");
        
        
        boolean NTHDeclined = false;
	String errorCode = aReq.get("errorCode");

	String requestreference = aReq.get("requestreference");
	System.out.println("Germany Testing DEConfirm requestreference\t\t" + requestreference);
        
        String country = aReq.get("country");
	String wapuser = aReq.get("wapuser");
        String sessionId = aReq.get("sessionId");
	String operatorCod = aReq.get("operatorCode");
        String landing_page=aReq.get("landingpage");
         String pubId=aReq.get("pubid");
        boolean processingResult = false;
        String status = "";
        UmeClubDetails userclubdetails = null;
        
         if (club != null) {
            userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        }
        
        if (isNotNullEmpty(wapuser) && wapuser.trim().toLowerCase().equals("yes")) {
		wapuser = "wap";
                optintype="wapoptin";
	} 
        else {
		wapuser = "wifi";
                optintype="smsoptin";
	}
        
        if (cmpg != null) cid = cmpg.getUnique();
        
        if (aReq.get("result").length() > 1) { // if we got an answer from NTH
            processingResult = true;
            
            	if (aReq.get("result").equalsIgnoreCase("confirm") || aReq.get("result").equalsIgnoreCase("error")) {
                    
                    System.out.println(" Germany "+" Result "+ aReq.get("result")+" at "+ new Date());

			if (aReq.get("result").equalsIgnoreCase("confirm") || aReq.get("result").equalsIgnoreCase("error")) {

				// Log this event
				//campaigndao.log("global_wap_header", "xhtml",uid, msisdn, null, domain, campaignUnique,club.getUnique(), "NTH_CONFIRM", 0, request,response);
                                //campaigndao.log("index", landingPage, msisdn, msisdn, handset, domain, campaignId, clubUnique, "INDEX", 0, request, response, myisp, "", "", "", pubId);
				NTHDeclined = false;
			} else {
				// Log this event
				//campaigndao.log("global_wap_header", "xhtml",uid, msisdn, null, domain, campaignUnique,	club.getUnique(), "NTH_DECLINE", 0, request,response);
				NTHDeclined = true;
			}

		}
            
            
            
        } //END if (aReq.get("result").length() > 1)
        
        if (club == null) {
		status = "ERROR: subscription service not found";

	} else if (processingResult && isNullEmpty(msisdn)) {
		status = "Sorry, we need your cell number! "+ msisdn+ " Please go back and enter your cell number and we'll send you your FREE link instantly!";
		//campaigndao.log("confirm", "xhtml", uid, msisdn,handset, domain, "", club.getUnique(), "NUMBER_INVLD",0, request, response);

	} else if (processingResult && isNotNullEmpty(msisdn)
			&& NTHDeclined && isNullEmpty(errorCode)) { // this is to get around a KE problem
		status = "Sorry, that's not a valid cell number! "+ msisdn+ " Please go back and enter your cell number and we'll send you your FREE link instantly!";
		//campaigndao.log("confirm", "xhtml", uid, msisdn,handset, domain, "", club.getUnique(), "NUMBER_INVLD",0, request, response);
	} else if (processingResult && isNotNullEmpty(msisdn)
			&& NTHDeclined && isNotNullEmpty(errorCode)) { // this is to get around a KE problem
		status = "You have insert  "+ errorCode	+ ". Please go back and enter your cell number and we'll send you your FREE link instantly!";
		//campaigndao.log("confirm", "xhtml", uid, msisdn,handset, domain, "", club.getUnique(), "NUMBER_INVLD",	0, request, response);
	} else if (!processingResult) {
		status = "Sorry, that's not a valid cell number! "+ msisdn+ " Please go back and enter your cell number and we'll send you your FREE link instantly!";
	}
        
        else{
            String defClubDomain = System.getProperty("CMS_defaultClubDomain");

		if (defClubDomain == null || defClubDomain.trim().isEmpty()) {
			defClubDomain = "5510024809921CDS"; //got it from application.properties
		}
                
                if(!msisdn.startsWith("00")){
			msisdn="00"+msisdn.trim();
		}
		
		session.setAttribute("msisdn", msisdn);
		session.setAttribute("sdc_msisdn_param", msisdn);
                
                SdcMobileClubUser clubUser = null;
                MobileClubBillingPlan billingplan = null;
                
                //=============== Subscription Start ==============================================
                
                if(userclubdetails.getBillingType().equalsIgnoreCase("subscription")){
            //User Subscription Starts Here ! 
                
            String subsresponse = subscriptioncreation.checkSubscription(msisdn, club, cid, club.getPeriod(), subpage, networkid, sessionId, landing_page, pubId, sessionId, wapuser);
            if (subsresponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY") || subsresponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY")) {
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
                    
                }
                
                campaigndao.log("deconfirm", landing_page, user.getUnique(), user.getParsedMobile(), userhandset, domain, cid, club.getUnique(), "SUBSCRIBED", 0, request, response, networkid, optintype, sessionId, "",pubId);
               
                //========================= HANDLING GOOGLE AD tracks ==================================
                if(userclubdetails.getServedBy().equalsIgnoreCase("thirdparty")){
                    try{
                    String trackurl="http://www.googleadservices.com/pagead/conversion/882134728/?label=4HQjCOaS3mYQyJ3RpAM&guid=ON&script=0";                     
                    //URL obj = new URL(trackurl);
                    //HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                    request.setAttribute("googletrack","true");
                    session.setAttribute("googletrack","true");
                    
                    //System.out.println("googleadservice "+trackurl+"  ResponseCode "+connection.getResponseCode()+"  ResponseMessage "+connection.getResponseMessage());
                }
                catch(Exception e){
                    System.out.println("googleadservice Exception "+e);e.printStackTrace();}
                    
                } //END Melody Tracking
                //==============================END handling google ad tracks =========================== 
                
                 if (cmpg != null && cmpg.getSrc().trim().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("subscription")) {
                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                    String cpaparameter1 = (String) session.getAttribute("cpaparam1");
                    String cpaparameter2 = (String) session.getAttribute("cpaparam2");
                    String cpaparameter3 = (String) session.getAttribute("cpaparam3");
                    logger.info("cpaupdate for msisdn {} campaign {} {} club {} parameters [{}|{}|{}]", msisdn, cid, cmpg.getSrc(), club.getUnique(), cpaparameter1, cpaparameter2, cpaparameter3);
                    ThreadContext.put("EXTRA", "CPA");
                    logger.info("cpaupdate for msisdn {} campaign {} {} club {} parameters [{}|{}|{}]", msisdn, cid, cmpg.getSrc(), club.getUnique(), cpaparameter1, cpaparameter2, cpaparameter3);
                    ThreadContext.put("EXTRA", "");
                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                    int insertedRows = cpaloggerdao.insertIntoCpaLogging(msisdn, cid, club.getUnique(), 10, networkid, cmpg.getSrc());
                    
                     boolean updaterec=(cpaparameter1!=null && !"".equalsIgnoreCase(cpaparameter1.trim())) || (cpaparameter2!=null && !"".equalsIgnoreCase(cpaparameter2.trim()))
                                || (cpaparameter3!=null && !"".equalsIgnoreCase(cpaparameter3.trim()));
                     
                     if(updaterec){
                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                         if(clubUser!=null){
                    int updatecpavisit = cpaloggerdao.updateCpaVisitLogMsisdnSubscriptionDate(msisdn, clubUser.getSubscribed(), cmpg.getUnique(), cpaparameter1, cpaparameter2, cpaparameter3);
                         }
                        }
                     }
                 
                  if (cmpg != null && cmpg.getSrc().trim().endsWith("RS")) {

                    String parameter1 = (String) session.getAttribute("revparam1");
                    String parameter2 = (String) session.getAttribute("revparam2");
                    String parameter3 = (String) session.getAttribute("revparam3");

                    //currentTime.add(Calendar.MINUTE, 10);
                    //String nextpush = SdcMiscDate.toSqlDate(new Date());

                    String enMsisdn = MiscCr.encrypt(msisdn);
                    logger.info("Revshare for msisdn {} campaign {} {} club {} parameters [{}|{}|{}]", msisdn, cid, cmpg.getSrc(), club.getUnique(), parameter1, parameter2, parameter3);
                    ThreadContext.put("EXTRA", "Revshare");
                    logger.info("Revshare for msisdn {} campaign {} {} club {} parameters [{}|{}|{}]", msisdn, cid, cmpg.getSrc(), club.getUnique(), parameter1, parameter2, parameter3);
                    ThreadContext.put("EXTRA", "");
                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                    int insertedRows =  cpaloggerdao.insertIntoRevShareLogging(0,  cmpg.getPayoutCurrency(), msisdn, enMsisdn, cid, club.getUnique(), 0, networkid, cmpg.getSrc(), 0);

                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                    if(clubUser!=null){
                    int updatedRows = cpaloggerdao.updateRevShareVisitLogMsisdnSubscriptionDate(msisdn, clubUser.getSubscribed(),  cmpg.getUnique(), parameter1, parameter2, parameter3);
                    }
                }

                PassiveVisitor visitor = passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
                if (visitor != null && visitor.getStatus() == 0) {
                    passivevisitordao.updatePassiveVisitorStatus(visitor, 1);
                    logger.info("PassiveVisitor details {} ",visitor);
                }
                
                
                
                
                
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
                    request.setAttribute("personallink", redirecturl);
                    return;
                }
               
            }

                     //User Subscription Creation Ends here
            
            //System.out.println("=======================ADDING BILLING PLAN RECORD NOW ================== ");
            //====== Add a BillingPlan Record of this user =======
            //System.out.println("THE VALUE OF RESULT IS : "+"Before Plan "+clubUser.getParsedMobile()+" "+aReq.get("result")+" "+ clubUser.getActive()+" welcomesms "+sendWelcome);
             //Get the Active Billing Plan of the user. 
                
            } //END subscription creation
                
                
                
                
                //=============== END Subscription Start ==========================================
             
                if(clubUser!=null) session.setAttribute("sdc_msisdn_param", clubUser.getParsedMobile());
                
                  String redirecturl = "http://" + dmn.getDefaultUrl();
            
            if(aReq.get("result").equalsIgnoreCase("CONFIRM")){
                
            //========= If Club Service is Content
                if(userclubdetails.getServiceType().equalsIgnoreCase("content") && user!=null && clubUser!=null){
                    
                    //=========== If it is Provided by UME
                    if(userclubdetails.getServedBy().equalsIgnoreCase("ume")){                
                    redirecturl="http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId();
                    }
                    
                    //========  If it is provided by Third Party like MobiPlanet
                    else if(userclubdetails.getServedBy().equalsIgnoreCase("thirdparty")){
                       redirecturl="http://" + dmn.getRedirectUrl() + "/?m="+Misc.encrypt(clubUser.getParsedMobile())+"&sub=success&credit="+clubUser.getCredits();
                    }
                    //====== If no setting is present=============
                    else redirecturl="http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId();
                }
                
                //===== TODO if this service is Competition =================
            if(userclubdetails.getServiceType().equalsIgnoreCase("competition") && user!=null && clubUser!=null){
                redirecturl="http://"+dmn.getDefaultUrl()+"/weeklyquiz.jsp?clubid="+club.getUnique()+"&msisdn="+user.getParsedMobile()+"&mid="+Misc.encrypt(user.getParsedMobile());
            }
            
         }
        }
        
      
    }
    
    void doRedirect(HttpServletResponse response, String url_) {
		String referer = "headers";
		try {
			response.sendRedirect(url_);
		} catch (Exception e) {
			System.out.println("doRedirect EXCEPTION: " + e);
		}
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
