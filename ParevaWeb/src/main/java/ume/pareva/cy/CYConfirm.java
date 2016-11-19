package ume.pareva.cy;

import ume.pareva.ire.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mitchellbosecke.pebble.PebbleEngine;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.MobileNetworksDao;
import ume.pareva.dao.PassiveVisitorDao;
import ume.pareva.dao.QueryHelper;
import ume.pareva.dao.QuizSmsDao;
import ume.pareva.dao.QuizValidationDao;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.PassiveVisitor;
import ume.pareva.pojo.QuizValidation;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeClubMessages;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.uk.CompetitionMessage;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.util.NetworkMapping;
import ume.pareva.util.ValidationUtil;

/**
 *
 * @author madan
 */
@WebServlet(name = "CYConfirm", urlPatterns = {"/CYConfirm"})
public class CYConfirm extends HttpServlet {
    
    @Autowired
    MobileNetworksDao mobilenetwork;
     
     @Autowired
    ValidationUtil validationutil;
     
     @Autowired
    MobileClubCampaignDao campaigndao;
     
    @Autowired
    HandsetDao handsetdao;
    
    @Autowired
    UmeUserDao umeuserdao;
    
    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;
    
    @Autowired
    TemplateEngine templateEngine;//
    
    @Autowired
    QuizValidationDao quizvalidationdao;
    
    @Autowired
    CompetitionMessage compmessage;
    
    @Autowired
    PassiveVisitorDao passivevisitordao;
    
    @Autowired
    QueryHelper queryhelper;
    
    @Autowired
    UmeSmsDao umesmsdao;
    
    @Autowired
    QuizSmsDao quizsmsdao;
    
    @Autowired
    UmeClubDetailsDao umeclubdetailsdao;
    
    @Autowired
    SubscriptionCreation subscriptioncreation;
    
    @Autowired
    MobileClubBillingPlanDao billingplandao;
    
    @Autowired
    NetworkMapping networkMapping;
    
      
        private final Logger logger = LogManager.getLogger(CYConfirm.class.getName());
    
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
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        ThreadContext.put("ROUTINGKEY", "CY");
        logger.info("cyconfirm "+"ProcessREQEST is called upon ");

        HttpSession session = request.getSession();
        logger.info("cyconfirm "+"session id is  "+session.getId());
        ServletContext application = request.getServletContext(); //.setAttribute(null, request);
        logger.info("cyconfirm "+"servletContext is  "+application.getContextPath());
        
        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();
        String ddir = dmn.getDefPublicDir();
        String lang = aReq.getLanguage().getLanguageCode();
        String defClubDomain ="5510024809921CDS"; // This is default domain in users table.
        boolean passiveupdate=true; //This will decide if we need to update passiveVisitor or not.
        boolean sendTeaser=false;
        String isIdentified=aReq.get("identified");
        boolean trickytime=true;
        
        PebbleEngine engine=(PebbleEngine)request.getAttribute("engine");
        if(engine==null) engine=(PebbleEngine) session.getAttribute("engine");
        if(engine==null) engine=templateEngine.getTemplateEngine(dmn.getUnique());
        
        PrintWriter writer = response.getWriter();
        Map<String, Object> context = new HashMap();
        context.put("contenturl","http://"+dmn.getContentUrl());
        
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
        UmeClubDetails userClubDetails=null;
        if(club!=null){
	userClubDetails=UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        }
        //String msg=userClubDetails.getTeaser();
        String shortCode=club.getSmsNumber();
        
        String msisdn=aReq.get("msisdn");
        String campaignId=aReq.get("cid");
        String confirmlanding=aReq.get("landingpage");
        String myisp=aReq.get("myisp");
        String pubId=(String) request.getAttribute("cpapubid");
        if(pubId==null) pubId=(String) session.getAttribute("cpapubid");
        if(pubId==null) pubId="";
        
        context.put("campaignid",campaignId);
        context.put("landingpage",confirmlanding);
        context.put("myisp",myisp);
        context.put("cpapubid",pubId);
        context.put("msisdn",msisdn);
        
        
        String networkname=myisp.toLowerCase();
        System.out.println("cyconfirm subscription network original " + networkname.toLowerCase());
            try{
                networkname=mobilenetwork.getMobileNetwork("CY",networkname);
            }catch(Exception e){networkname="unknown";}
        System.out.println("cyconfirm subscription network after " + networkname.toLowerCase());
        if(msisdn.startsWith("0")){
           msisdn="357"+msisdn.substring(1);
        }
        
           //======== HANDSET Recognition ==============
        Handset handset = (Handset) session.getAttribute("handset");
        if(handset==null) handset=(Handset) request.getAttribute("handset");
        if(handset==null) handset=handsetdao.getHandset(request);
        if (handset != null) {
            session.setAttribute("handset", handset);
            request.setAttribute("handset", handset);            
        }
        //======== END HANDSET Recognition ==============
        msisdn=msisdn.replaceAll("\\s","");
        boolean validMsisdn=validationutil.isIREValidPhone(msisdn);
        if(validMsisdn){
         
         /**
          * This is added on 14th July 2016. This is to not log manual if msisdn passing for vodafone network identifies the msisdn. 
          * the session is set from msisdnrouter.jsp And if it is null only then log manual hits. 
          */
        if(session.getAttribute("vodafoneidentified")==null && session.getAttribute("msisdnidentified")==null){
         campaigndao.log("cyconfirmweb3", confirmlanding, msisdn,msisdn, handset,domain, campaignId, club.getUnique(), "MANUAL", 0, request,response,networkname.toLowerCase(),"","","",pubId);   
        }
        String[] toAddress={msisdn};
         context.put("mid",MiscCr.encrypt(msisdn));
         context.put("msisdn",msisdn);
         //======== FINDING IF USER EXISTS OR NOT ==========================
       String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", defClubDomain);
	if (!userUnique.equals("")) user = umeuserdao.getUser(msisdn);
        
        	if (user!=null) {
                    SdcMobileClubUser clubUser = user.getClubMap().get(club.getUnique());
                    if (clubUser == null) {
                        clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                    }
        if ((clubUser!=null && clubUser.getActive()!=1) ||(clubUser==null)) {
            
                context.put("subscribed","false");
                QuizValidation validation=new QuizValidation();
                validation.setaParsedMobile(msisdn);
                validation.setaCampaign(campaignId);
                validation.setaClubUnique(club.getUnique());
                int inserted=quizvalidationdao.insertValidationRecord(validation);
                sendTeaser=true;
               
            }
        else{ //clubuser is active then
            
            if(userClubDetails.getBillingType().equalsIgnoreCase(("adhoc"))){
                sendTeaser=true;
            }
            else{
                System.out.println("quiz2winlanding page "+clubUser.getParsedMobile()+"  "+clubUser.getActive()+" inside else condition ");
	    	context.put("subscribed","true");                
                passiveupdate=false;
            }
                
	    }
     
       } //End if user!=null
        
        //==========END IF USER EXISTS OR NOT ============================
           else { // if user is null or for new user
              context.put("subscribed","false"); 
              sendTeaser=true;
           
            
        }
                
              if(sendTeaser){
                  java.util.List<UmeClubMessages> teaserMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Teaser");
                  String msg=teaserMessages.get(0).getaMessage(); //
                  
                  
                  //=============== THIS IS FOR CREATION OF ADHOC USERS ===============================================================
                  if(userClubDetails.getBillingType().equalsIgnoreCase(("adhoc"))){
                      trickytime=false;
                      String subsresponse = subscriptioncreation.checkSubscription(msisdn, club, campaignId,club.getPeriod(), "", networkname.toLowerCase(), "",confirmlanding, pubId);
                      MobileClubBillingPlan billingplan = null;
                      SdcMobileClubUser clubUser = null;
                      boolean createbillPlan=false;
                      if (subsresponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY")
                        || subsresponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY")) {
                    userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");
                    context.put("subscribed","true");
                    if (userUnique != null && !userUnique.equals("")) {
                        user = umeuserdao.getUser(msisdn); //
                    }
                    clubUser = user.getClubMap().get(club.getUnique());
                    if (clubUser == null) {
                        clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                    }
                    if (user != null && clubUser != null) {
                        user.getClubMap().put(club.getUnique(), clubUser);
                        createbillPlan = true;
                    }

                    logger.info("cyconfirm subscription  " + clubUser.toString() + " Create Billing Plan is " + createbillPlan);
                }

                if (subsresponse.equals("SUBSCRIPTION RECORD ALREADY EXISTS")) {
                    createbillPlan = false;
                    userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");
                    if (userUnique != null && !userUnique.equals("")) {
                        user = umeuserdao.getUser(msisdn);
                    }
                    clubUser = user.getClubMap().get(club.getUnique());
                    if (clubUser == null) {
                        clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                    }

                    System.out.println("cyconfirm subscription  " + clubUser.toString() + " createbillPlan IS " + createbillPlan);
                }
                
                       //Get the Active Billing Plan of the user. 
                billingplan = billingplandao.getActiveBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());  
                int totalPlans=-1;
                if(clubUser!=null){
                    totalPlans=billingplandao.getTotalActivePlans(clubUser, 30);
                }
                if(userClubDetails.getServiceType().equalsIgnoreCase(("competition"))){
                    if(billingplan!=null && totalPlans>=3)
                    {
                        createbillPlan=false;
                        sendTeaser=false;
                    }
                    else {
                        createbillPlan=true;
                    }
                }
                
                System.out.println("cyconfirm subscription  " +clubUser.getParsedMobile()+" value of createbillPlan is "+createbillPlan);
                  if (billingplan == null || createbillPlan) {
                      System.out.println("cyconfirm subscription network adhoc " +clubUser.getParsedMobile()+" NETWORKNAME before if " + networkname.toLowerCase());
                      if(networkname.toLowerCase().contains("vodafone")){
                          networkname="VODAFONE9IE";
                          clubUser.setNetworkCode(networkname);
                      }
                      else if(networkname.toLowerCase().contains("o2")){
                          networkname="O29IE";
                          clubUser.setNetworkCode(networkname);
                      }
                      else if(networkname.toLowerCase().contains("tesco")){
                          networkname="TESCO9IE";
                      clubUser.setNetworkCode(networkname);
                      }
                      else if(networkname.toLowerCase().contains("meteor")){
                          networkname="METEOR9IE";
                          clubUser.setNetworkCode(networkname);
                      }
                      else if(networkname.toLowerCase().contains("three")){
                          networkname="THREE9IE";
                          clubUser.setNetworkCode(networkname);
                      }
                      
                      
                      if(clubUser.getNetworkCode().isEmpty() || clubUser.getNetworkCode().equalsIgnoreCase("unknown")){
                          System.out.println("cyconfirm subscription network adhoc " +clubUser.getParsedMobile()+" NETWORKNAME before looking up " + networkname);
                          String mncCode=getNetwork(clubUser.getParsedMobile());
                          
                          if(mncCode!=null && !mncCode.isEmpty()){
                          networkname=networkMapping.getIeNetworkMap().get(mncCode);
                          System.out.println("cyconfirm subscription network adhoc " +clubUser.getParsedMobile()+" NETWORKNAME after looking up " + (networkname==null?"null":networkname));
                          if(networkname==null || networkname.isEmpty()) networkname=networkMapping.getIeNetworkMap().get(mncCode.substring(1));
                          clubUser.setNetworkCode(networkname);
                          } else{
                              System.out.println("cyconfirm subscription network adhoc mncCode is blank for "+clubUser.getParsedMobile()+" at "+new Date());
                          }
                          
                      }                      
                      
                      System.out.println("cyconfirm subscription network adhoc " +clubUser.getParsedMobile()+" NETWORKNAME BEFORE SMSNUMBER LOOK UP  " + networkname);
                       // 6ie bit relates to 57977 and 9ie realates to 57976
                      if(club.getSmsNumber().equals("57977") && networkname.toUpperCase().endsWith("9IE")){
                          networkname = networkname.substring(0, networkname.length() - 3);
                          networkname = networkname + "6IE";
                          clubUser.setNetworkCode(networkname);
                      }
                      System.out.println("cyconfirm subscription network adhoc " +clubUser.getParsedMobile()+" NETWORKNAME DEFINITIVE " + networkname);
                      
                      
                    Calendar c1 = new GregorianCalendar();
                    Date bstart = c1.getTime();
                    c1.setTime(bstart);
                    c1.add(Calendar.DATE, 30);                    
                    Date bend = c1.getTime();
            
                    if(user!=null){
                    billingplan = new MobileClubBillingPlan();
                    billingplan.setTariffClass(club.getPrice());
                    billingplan.setActiveForAdvancement(1);
                        
                    System.out.println(user.getParsedMobile()+" the value of identified is "+isIdentified);
                    if(isIdentified.equalsIgnoreCase("true")){
                        billingplan.setActiveForBilling(1); 
                        sendTeaser=false;
                        context.put("subscribed","true");
                        //for the adhoc identified
                        campaigndao.log("ieadhoc-identified", clubUser.getLandingpage(), clubUser.getParsedMobile(),clubUser.getParsedMobile(), handset,domain, clubUser.getCampaign(),clubUser.getClubUnique(), "SUBSCRIBED", 0, request,response,clubUser.getNetworkCode().toLowerCase(),"","","",pubId);
                        
                    }
                    else { billingplan.setActiveForBilling(-1);}
                    
                    System.out.println(user.getParsedMobile()+" the value of identified is "+isIdentified+" active for billing is "+billingplan.getActiveForBilling());
                    billingplan.setAdhocsRemaining(0.0);
                    billingplan.setBillingEnd(bend);
                    billingplan.setClubUnique(club.getUnique());
                    billingplan.setContractType("");
                    billingplan.setLastPaid(clubUser.getSubscribed());
                    billingplan.setLastSuccess(new Date(0));
                    billingplan.setLastPush(clubUser.getSubscribed());
                    billingplan.setNetworkCode(networkname);
                    billingplan.setNextPush(DateUtils.addMinutes(new Date(), 2) );
                    billingplan.setParsedMobile(user.getParsedMobile());
                    billingplan.setPartialsPaid(0.0);
                    billingplan.setSubscribed(clubUser.getSubscribed());
                    billingplan.setPartialsRequired(Double.parseDouble(String.valueOf(userClubDetails.getFrequency() + "")));
                    billingplan.setPushCount(0.0);
                    billingplan.setServiceDate(bstart);
                    billingplan.setSubUnique(clubUser.getUserUnique());
                    billingplan.setExternalId("adhocpending"); //This is Ireland adhoc
                    billingplan.setServiceDateBillsRemaining(Double.parseDouble(String.valueOf(userClubDetails.getFrequency())));
                    billingplandao.insertBillingPlan(billingplan);
                    if(clubUser!=null) umemobileclubuserdao.saveItem(clubUser);
                    }
                    }
                  //==================================DUPLICATED HERE PASSIVE VISITOR CODE NEED TO ADDRESS AFTER HOLIDAY ====================================
                  
                   boolean exist=passivevisitordao.exists(msisdn, club.getUnique());
                    if(!exist){
                        PassiveVisitor visitor=new PassiveVisitor();
                        visitor.setUnique(SdcMisc.generateUniqueId());
                        visitor.setClubUnique(club.getUnique());
                        visitor.setFollowUpFlag(0);
                        visitor.setParsedMobile(msisdn);
                        visitor.setStatus(0);
                        visitor.setCreated(new Date());
                        visitor.setCampaign(campaignId);
                        visitor.setLandignPage(confirmlanding);
                        visitor.setPubId(pubId);
                        passivevisitordao.insertPassiveVisitor(visitor);
                }
                    else if(exist){
                if(passiveupdate){ // If user is null OR if clubUser is not active
                        PassiveVisitor visitor=passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
                        visitor.setCampaign(campaignId);
                        visitor.setFollowUpFlag(0);
                        visitor.setStatus(0);
                        visitor.setCreated(new Date());
                        visitor.setLandignPage(confirmlanding);
                        visitor.setPubId(pubId);
                        passivevisitordao.updatePassiveVisitor(visitor);
                }
                    }
                    
                               //======updating CPA params with msisdn 
        if(campaignId!=null && !campaignId.equalsIgnoreCase("")){
            MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
            
            if(cmpg!=null && cmpg.getSrc().endsWith("CPA")) {
            
            String cpaparameter1=(String) session.getAttribute("cpaparam1");
            String cpaparameter2=(String) session.getAttribute("cpaparam2");
            String cpaparameter3=(String) session.getAttribute("cpaparam3");
         String cpalogQuery="UPDATE cpavisitlog set aParsedMobile='"+msisdn+"',isSubscribed='1',aSubscribed='2011-01-01' WHERE aCampaignId='"+cmpg.getUnique()+"' "
                 + " AND (aHashcode='"+cpaparameter1+"' AND cpacampaignid='"+cpaparameter2+"'" + " AND clickid='"+cpaparameter3+"') ";   
            queryhelper.executeUpdateQuery(cpalogQuery,"IMIConfirm Servlet");
            
         }
         
            } //END campaignId!=null checks. 
                  
                  
                  //=====================================END DUPLICATED CODE =========================================================
        
        
                  if(userClubDetails.getServiceType().equalsIgnoreCase("content") && userClubDetails.getServedBy().equalsIgnoreCase("ume") && isIdentified.equals("true")){
                      String redirecturl="http://"+dmn.getDefaultUrl()+"/?id="+user.getWapId();
                      response.sendRedirect(redirecturl); return;
                      
                  }
                  
                    if(userClubDetails.getServedBy().equalsIgnoreCase("thirdparty") && isIdentified.equals("true")){
                      String redirecturl="http://"+dmn.getDefaultUrl()+"/?id="+user.getWapId();
                      response.sendRedirect(redirecturl); return;
                      
                  }
               
                  
                  } // ENDING clubDetails get adhoc condition
                  
                  //==================== END CREATION OF ADHOC USERS ====================================================================
                  
                  System.out.println("ie compmessage ie confirm "+toAddress[0]+" msg is "+msg+" shortCode is "+shortCode);
                  
                  if(sendTeaser)
                compmessage.requestIESendSmsTimer(toAddress,msg,shortCode,club,userClubDetails,6000*10,trickytime, campaignId);  //12000*10= 120000 - 2 mins. 
                  
               
                  
//                    if(sendSMS(toAddress,msg,shortCode,club,userClubDetails)){
//                    //TODO anything with true or false response
//                }
              }
        boolean exist=passivevisitordao.exists(msisdn, club.getUnique());
            if(!exist){
                        PassiveVisitor visitor=new PassiveVisitor();
                        visitor.setUnique(SdcMisc.generateUniqueId());
                        visitor.setClubUnique(club.getUnique());
                        visitor.setFollowUpFlag(0);
                        visitor.setParsedMobile(msisdn);
                        visitor.setStatus(0);
                        visitor.setCreated(new Date());
                        visitor.setCampaign(campaignId);
                        visitor.setLandignPage(confirmlanding);
                        visitor.setPubId(pubId);
                        passivevisitordao.insertPassiveVisitor(visitor);
                }
            else if(exist){
                if(passiveupdate){ // If user is null OR if clubUser is not active
                        PassiveVisitor visitor=passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
                        visitor.setCampaign(campaignId);
                        visitor.setFollowUpFlag(0);
                        visitor.setStatus(0);
                        visitor.setCreated(new Date());
                        visitor.setLandignPage(confirmlanding);
                        visitor.setPubId(pubId);
                        passivevisitordao.updatePassiveVisitor(visitor);
                }
                    }
            
            //======updating CPA params with msisdn 
        if(campaignId!=null && !campaignId.equalsIgnoreCase("")){
            MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
            
            if(cmpg!=null && cmpg.getSrc().endsWith("CPA")) {
            
            String cpaparameter1=(String) session.getAttribute("cpaparam1");
            String cpaparameter2=(String) session.getAttribute("cpaparam2");
            String cpaparameter3=(String) session.getAttribute("cpaparam3");
         String cpalogQuery="UPDATE cpavisitlog set aParsedMobile='"+msisdn+"',isSubscribed='1',aSubscribed='2011-01-01' WHERE aCampaignId='"+cmpg.getUnique()+"' "
                 + " AND (aHashcode='"+cpaparameter1+"' AND cpacampaignid='"+cpaparameter2+"'" + " AND clickid='"+cpaparameter3+"') ";   
            queryhelper.executeUpdateQuery(cpalogQuery,"IMIConfirm Servlet");
            
         }
         
            } //END campaignId!=null checks. 
        
        //====== END updating CPA parameters with msisdn value ==============
       } //END Valid msisdn 
          else if(!validMsisdn){
           context.put("subscribed","error");
           context.put("statusMsg","The mobile number you entered was invalid");
           context.put("statusMsg2","Resend message");                  
           
       }
       else{ //FOR ALL OTHER ERROR 
           context.put("subscribed","error");
           context.put("statusMsg","Error occurred");
           context.put("statusMsg2","Resend message");
          
       }
        
        try{
	   engine.getTemplate("confirm2").evaluate(writer, context);
        }catch(Exception e){}
        
   
    } //End of ProcessList
    
  
   public String getNetwork(String msisdn){
       String networkname="";
       String httpsrequest="",message="";
        int responseCode=-1;
       //=========================================================================
        StringBuilder sb = new StringBuilder();
        StringBuilder jsonsb=new StringBuilder();

        httpsrequest="http://smsc.txtnation.com:5002/checkHLR?msisdn="+msisdn+"&username=moonlight001&password=7lD7sabU&output=json";
        System.out.println("irelookup request sent to  "+httpsrequest); 
  
             URL obj = null;
            HttpURLConnection con = null;
       
       //========================================================================
       
          try{
              obj = new URL(httpsrequest);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("Accept", "application/json");
            
            responseCode = con.getResponseCode();  
            message=con.getResponseMessage();
             //engine.getTemplate("msisdnparser").evaluate(writer, context); 
		System.out.println("irelookup HTTP URL  Connection Sending 'GET' request to URL : " + httpsrequest);
		System.out.println("irelookup HTTPURLConnection  Response Code : " + responseCode+" message- "+message);
                
            sb.append("responsecode: "+responseCode+"\n"+ "message : "+message+"\n");
              
           switch (responseCode) {
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                    jsonsb.append(line+"\n");
                }
                br.close();
                
        }
           String data=jsonsb.toString();
          
           JsonParser parser = new JsonParser();
	   JsonObject json = parser.parse(data).getAsJsonObject();
        
            JsonElement jelem =  json.get("mnc");
            if(jelem!=null && !jelem.isJsonNull()){
            System.out.print("irelookup  network parsed is "+jelem.getAsString());
            networkname= jelem.getAsString()+"";
        }       
           
        System.out.println("irelookup the networkcode returned for mnc is "+msisdn+" mnc "+networkname);

    } catch (Exception ex) {       
            sb.append("error "+ex);
    }finally {
       if (con != null) {
          try {
              con.disconnect();
          } catch (Exception ex) {
             
            sb.append("error "+ex);
          }
       }
    }
       
       return networkname;
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
