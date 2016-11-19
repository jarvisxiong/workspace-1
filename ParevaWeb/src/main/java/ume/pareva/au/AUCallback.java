package ume.pareva.au;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubBillingTry;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.AusTrackingDao;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobileBillingDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.AusTracking;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.MobileClubBillingSuccesses;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.userservice.StopUser;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.util.ZACPA;

/**
 *
 * @author madan
 */
@WebServlet(name = "AUCallback", urlPatterns = {"/AUCallback"})
public class AUCallback extends HttpServlet {
    static final Logger logger = LogManager.getLogger(AUCallback.class.getName());
    
     @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;

    @Autowired
    MobileClubCampaignDao campaigndao;
    
    @Autowired
    CpaLoggerDao cpaloggerdao;
    
    @Autowired
    AusTrackingDao austrackingdao;
    
    @Autowired
    MobileBillingDao mobileclubbillingdao;
    
    @Autowired
    StopUser stopuser;
    
    @Autowired
    ZACPA zacpa;
    
    @Autowired
    SubscriptionCreation subscriptioncreation;
    
    @Autowired
    UmeUserDao umeuserdao;
    
    
          /**
     * This method is needed to support Autowired Spring beans
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods. Used for Au DLR Notification
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        ThreadContext.put("ROUTINGKEY", "AU");
        System.out.println("AUCallback "+"ProcessREQEST is called upon ");

        HttpSession session = request.getSession();
        System.out.println("AUCallback "+"session id is  "+session.getId());
        ServletContext application = request.getServletContext(); //.setAttribute(null, request);
        System.out.println("AUCallback  "+"servletContext is  "+application.getContextPath());
        UmeSessionParameters aReq = new UmeSessionParameters(request);
	UmeDomain dmn=aReq.getDomain();
        UmeUser user = aReq.getUser();
        /**
         * MSISDN- msisdn
         * TransID- 
         * ServiceID
         * Status
         * Reason
         * Subscriptionlistid
         */
            /**
         * Reading parameters from Australian Aggregator parametersMap variable
         * compares the parameters and BI logging is done 
         */
        Map<String, String> parametersMap = new HashMap<>();

        for (String key : request.getParameterMap().keySet()) {
            System.out.println("AUCallback audebug: " + key + " -- " + request.getParameter(key));
            parametersMap.put(key.toLowerCase().trim(), request.getParameter(key).trim());
        }
        
        String transId=aReq.get("transactionid");
        if("".equals(transId)) transId=aReq.get("transactionID");
        String msisdn=aReq.get("msisdn");
        String status=aReq.get("status");
        String reason=aReq.get("reason");
        String subscriptionlistid= aReq.get("subscriptionlistid");
        if("".equalsIgnoreCase(subscriptionlistid)) subscriptionlistid=aReq.get("listid");
        String serviceId=aReq.get("serviceid");
        if("".equalsIgnoreCase(serviceId)) serviceId=aReq.get("serviceID");
        String event=aReq.get("action");
         if("".equals(event)) event=aReq.get("event");
        String campaignId="",landingpage="";
        AusTracking austracking=null;
        
        
        if(!transId.isEmpty()){
              try{
                    austrackingdao.updateTransId(transId,msisdn, status,reason);
        
        }catch(Exception e){
        try{
            austrackingdao.updateTransId(transId, msisdn);
        }catch(Exception ex){}
        
        }
        }
        
         try{
                if(!transId.isEmpty())austracking=austrackingdao.getAusTracking(transId, msisdn);
                
                if(austracking==null) austracking=austrackingdao.getAusTracking(msisdn);
                landingpage=austracking.getLandingPage();
                campaignId=austracking.getCampaignid();
            }catch(Exception e){};
       
      //  MobileClub club=UmeTempCmsCache.mobileClubMap.get(serviceId);
        MobileClub club=UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
        UmeClubDetails userclubdetails = null;//
        
        
         MobileClubCampaign cmpg = null;
        if (campaignId!=null && !campaignId.equals("")) {
            cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
        }
        
        
          if (club != null) {
            userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        }
        System.out.println("AUCallback AUClub: "+club.getUnique());
        SdcMobileClubUser clubUser = null;
         if(event.equalsIgnoreCase("subscribe")){
             String subsresponse = subscriptioncreation.checkSubscription(msisdn, club, campaignId, 1, "", "", transId, landingpage,"");
             
              if (subsresponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY")
                    || subsresponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY")) {
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
                 campaigndao.log("auconfirmweb3", landingpage, user.getUnique(), user.getParsedMobile(), null, dmn.getUnique(),campaignId, club.getUnique(), "SUBSCRIBED", 0, request, response, "", "", "", "","");         
                //CPA and RS Logging after club User Creation is done on AUMerchant where Cookies are read for CPA Parameters
                       
            }
              
              if (subsresponse.equals("SUBSCRIPTION RECORD ALREADY EXISTS")) {
                
                System.out.println("AUCallback MSISDN = " + (msisdn==null?"null msisdn":msisdn));
                String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");
                if (userUnique != null && !userUnique.equals("")) {
                    System.out.println("AUCallback  USERUNIQUE = " + userUnique);
                    user = umeuserdao.getUser(msisdn);
                }
                clubUser = user.getClubMap().get(club.getUnique());
                if (clubUser == null) {
                    System.out.println("AUCallback  LOOKING FOR SUBSCRIBER " + msisdn + " - " + club.getUnique());
                    clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                }
                if (user != null && clubUser != null) {
                    user.getClubMap().put(club.getUnique(), clubUser);
                   
                }
                System.out.println("AUCallback CLUBUSER  " + clubUser.toString() );
            }  
              
              
             
         }  //=========== END SUBSCRIPTION ================  
         else if(event.equalsIgnoreCase("unsubscribe")){
            stopuser.stopSingleSubscriptionNormal(msisdn, club.getUnique(), request, response);
	}
      
        try {
            clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
        }catch(Exception e){clubUser=null;}
       
        if(clubUser!=null) System.out.println("AUCallback AUClubUser: "+clubUser.getUnique());        
             
        if(status!=null && !"".equals(status)) {
            
        if(status.equalsIgnoreCase("successful")){
            String successResponse="00";
                if(clubUser!=null){
                   if(DateUtils.isSameDay(new Date(), clubUser.getSubscribed())) {
                       successResponse="003";
                    }
                    if(cmpg!=null && cmpg.getSrc().toLowerCase().endsWith("cpa")) {     
                        System.out.println("AUCallback iminotificationcall  cmpg of CPA is  "+cmpg.getSrc()+"  type is "+cmpg.getCpaType());
                        if(("billing").equalsIgnoreCase(cmpg.getCpaType())){
                        zacpa.insertCpaLogging(msisdn,cmpg.getSrc(),cmpg.getUnique(),club.getUnique(),clubUser.getNetworkCode());
                        }
                    }
                }
                else if(clubUser==null){
                    if(austracking!=null){ //If BillingNotification is received before event=subscribe notification, TransactionId created date is used for New Bills
                        if(DateUtils.isSameDay(new Date(), austracking.getaCreated()))
                            successResponse="003";
                    }
                }
                         
                    MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                        mobileClubBillingTry.setUnique(Misc.generateUniqueId(10));
                        mobileClubBillingTry.setLogUnique(Misc.generateUniqueId());
                        mobileClubBillingTry.setAggregator("Oxy8");
                        mobileClubBillingTry.setClubUnique(club.getUnique());
                        mobileClubBillingTry.setCreated(new Date());
                        mobileClubBillingTry.setNetworkCode("");
                        mobileClubBillingTry.setParsedMsisdn(msisdn);
                        mobileClubBillingTry.setRegionCode("AU");
                        mobileClubBillingTry.setResponseCode(successResponse);
                        mobileClubBillingTry.setResponseDesc("successful");
                        mobileClubBillingTry.setResponseRef(transId);
                        mobileClubBillingTry.setStatus("success");
                        mobileClubBillingTry.setTransactionId(transId);
                        mobileClubBillingTry.setCampaign(campaignId);
                        mobileClubBillingTry.setTariffClass(club.getPrice());
                        mobileclubbillingdao.insertBillingTry(mobileClubBillingTry);
                        
                        MobileClubBillingPlan mobileClubBillingPlan= new MobileClubBillingPlan();
                        mobileClubBillingPlan.setParsedMobile(msisdn);
                        mobileClubBillingPlan.setClubUnique(club.getUnique());
                        mobileClubBillingPlan.setTariffClass(club.getPrice());
                        MobileClubBillingSuccesses mobileClubBillingSuccesses = new MobileClubBillingSuccesses(mobileClubBillingPlan, mobileClubBillingTry);
                        mobileclubbillingdao.insertBillingSuccess(mobileClubBillingSuccesses);
                        
                             
        }
        
        else if(status.equalsIgnoreCase("unsuccessful")){
            String responseCode="99";
            String responseDesc="Other Error";
            String billingstatus="failure";
            if(reason!=null && !"".equals(reason)){
                if(reason.equalsIgnoreCase("av_failed")){
                    billingstatus="adultverfication";
                }
                else if(reason.equalsIgnoreCase("account_barred")){
                    billingstatus="account suspended";
                    boolean stopped=stopuser.stopSingleSubscription(msisdn,club.getUnique(),null,null);
                    
                }
                else if(reason.equalsIgnoreCase("failed_credit")){
                    responseCode="51";
                    responseDesc="Insufficient Fund";
                    billingstatus="prepaid";
                }
                
                    else if(reason.equalsIgnoreCase("INSUFFICIENT_FUNDS")){
                    responseCode="51";
                    responseDesc="Insufficient Fund";
                    billingstatus="prepaid";
                }
                   else if(reason.equalsIgnoreCase("failed_credit_limit")){
                    responseCode="51";
                    responseDesc="Insufficient Fund";
                    billingstatus="postpaid";
                }
                   else if(reason.equalsIgnoreCase("failed_spend_limit")){
                    responseCode="51";
                    responseDesc="Insufficient Fund";
                    billingstatus="failed spend limit";
                }
                   else if(reason.equalsIgnoreCase("invalid")){
                    responseCode="99";
                    responseDesc="Other Error";
                    billingstatus="invalid network";
                }
                   else if(reason.equalsIgnoreCase("invalid_charge_amount")){
                    responseCode="99";
                    responseDesc="Other Error";
                    billingstatus="Invalid Price";
                }
                   else if(reason.equalsIgnoreCase("system_error")){
                    responseCode="99";
                    responseDesc="Other Error";
                    billingstatus="system error";
                }
                   else if(reason.equalsIgnoreCase("unknown_error")){
                    responseCode="99";
                    responseDesc="Other Error";
                    billingstatus="unknown error";
                }
                   else if(reason.equalsIgnoreCase("unsupported")){
                    responseCode="99";
                    responseDesc="Other Error";
                    billingstatus="unsupported";
                }
                
            } //END if reason is not blank 
            MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                        mobileClubBillingTry.setUnique(Misc.generateUniqueId(10));
                        mobileClubBillingTry.setLogUnique(Misc.generateUniqueId());
                        mobileClubBillingTry.setAggregator("Oxy8");
                        mobileClubBillingTry.setClubUnique(club.getUnique());
                        mobileClubBillingTry.setCreated(new Date());
                        mobileClubBillingTry.setNetworkCode("");
                        mobileClubBillingTry.setParsedMsisdn(msisdn);
                        mobileClubBillingTry.setRegionCode("AU");
                        mobileClubBillingTry.setResponseCode(responseCode);
                        mobileClubBillingTry.setResponseDesc(responseDesc);
                        mobileClubBillingTry.setResponseRef(transId);
                        mobileClubBillingTry.setStatus(reason);
                        mobileClubBillingTry.setTransactionId(transId);
                        mobileClubBillingTry.setCampaign(campaignId);
                        mobileClubBillingTry.setTariffClass(club.getPrice());
                        mobileclubbillingdao.insertBillingTry(mobileClubBillingTry);
        } //end status is unsuccessful
        
        
   
        
        
        } //END if Status is not null or blank 
        
        
       
		

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
