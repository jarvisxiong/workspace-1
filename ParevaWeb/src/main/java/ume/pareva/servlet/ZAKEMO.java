package ume.pareva.servlet;

import com.kenya.sms.service.KenyaSms;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.tempuri.APIRequestResponse;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.SdcMiscDate;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.dao.UmeSmsDaoExtension;
import ume.pareva.dao.UmeSmsKeywordDao;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeClubMessages;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.so;
import ume.pareva.userservice.CheckStop;
import ume.pareva.userservice.StopUser;
import ume.pareva.userservice.UserAuthentication;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.util.ZACPA;

/**
 *
 * @author madan
 */
@WebServlet(name = "ZAKEMO", urlPatterns = {"/ZAKEMO"})
public class ZAKEMO extends HttpServlet {
    
    @Autowired
    UmeSmsDao smsdao;
    
    @Autowired
    UmeMobileClubUserDao clubuserdao;
    
    
    @Autowired
    MobileClubCampaignDao campaigndao;
    
    @Autowired
    MobileClubBillingPlanDao billingplandao;
    
    @Autowired
    ZACPA zacpa;
    
    @Autowired
    UserAuthentication userauthentication;
    
    @Autowired
    CheckStop checkstopcount;
    
    @Autowired
    MobileClubDao mobileclubdao;
    
    @Autowired
    StopUser stopuser;
    
    @Autowired
    UmeSmsKeywordDao smskeywordao;
    
    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;
	
    @Autowired
    UmeUserDao umeuserdao;
    
    @Autowired
    SubscriptionCreation subscriptioncreation;
    
    @Autowired
    UmeTempCache umesdc;
    
    @Autowired
    UmeClubDetailsDao umeclubdetailsdao;
    
    private final Logger logger = LogManager.getLogger(ZAKEMO.class.getName());
    
    
    
    
    
    
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        response.setContentType("text/html;charset=UTF-8");
        UmeSessionParameters httprequest= new UmeSessionParameters(request);
        UmeUser user = httprequest.getUser();
        String lang = httprequest.getLanguage().getLanguageCode();
        UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
        SdcService service = (SdcService) request.getAttribute("umeservice");
        
        UmeSmsDaoExtension umesmsdaoextension=null;
        MobileClubCampaign cmpgn = null;
    
        String fileName = "stsmo";
        String crlf = "\r\n";
        String landingpage="unknown";
        String ip = request.getRemoteAddr();
        String shortCode="";
        
        
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();

        int c;
        while ((c = reader.read()) != -1) {
            sb.append((char)c);
        }
        so.l("Read");  //class of ume.pareva.sdk  so.l prints out the information. 

        if (sb.length()<=0) {
            //System.out.println("ZASTOP"+" No XML. returning");
            return;
        }
        
        String sessionId = "";
        String msgCode1 = "";
        String fromNumber = "";
        String toNumber = "";
        String networkCode = "";
        String msgBody = "";
        String msgCode2 = "";
        String status="";
        String reference="";
        String deliveryDatetime="";
        String tag = "";
        String tag2 = "";
        
        SAXBuilder builder = new SAXBuilder(false);
        builder.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        Document doc = null;
        try{
        doc=builder.build(new StringReader(sb.toString()));
        }catch(Exception e){doc=null; return;}
        
        Element elem = doc.getRootElement();
        Namespace xmlns = elem.getNamespace();
        
        //================ Reading the Element List ============================
        java.util.List list  = elem.getChildren();
        for (int i=0; i<list.size(); i++) {
            elem = (Element) list.get(i);
            tag = elem.getName().toLowerCase();
            System.out.println("ZASTOP "+" TAG: " + tag);
    
            if (tag.equals("moid")) sessionId = elem.getTextTrim();
            else if (tag.equals("usermessagereference")) msgCode1 = elem.getTextTrim();
            else if (tag.equals("msisdn")) fromNumber = elem.getTextTrim();
            else if (tag.equals("network_id")) networkCode = elem.getTextTrim();
            else if (tag.equals("destination")) toNumber = elem.getTextTrim();
            else if (tag.equals("message")) msgBody = elem.getTextTrim();
            else if (tag.equals("datetime")) msgCode2 = elem.getTextTrim();
            else if (tag.equals("reference")) reference = elem.getTextTrim();
            else if (tag.equals("status")) status = elem.getTextTrim();
            else if (tag.equals("deliverydatetime")) deliveryDatetime = elem.getTextTrim();
        }
        
        //=============== END Reading the Element List =========================
        
        String msisdn = "";
        String kw ="";
        boolean stopmsg=false;
        boolean shouldStop=false;//checkstopcount.checkStopCount("ZA");
        if(msgBody!=null && msgBody.trim().length()>0) kw=msgBody;
        

        
        
    
        if(kw!=null && kw.trim().length()>0) {
            
            //====Validating if the message received is stopMsg or not. 
            stopmsg=kw.equalsIgnoreCase("stop") || kw.equalsIgnoreCase("end") || kw.equalsIgnoreCase("cancel") 
            || kw.equalsIgnoreCase("unsubscribe") || kw.equalsIgnoreCase("quit") || kw.trim().toLowerCase().contains("stop");
            
               msisdn=fromNumber;   
        } //END kw is not null or not empty 
       
        if (msisdn.equals("")) return;
        
        
            if(stopmsg) {
        //========== Insertion of MOs in smsMsgLog ===========================
               
            SdcSmsSubmit sdcSmsSubmit = new SdcSmsSubmit();
            sdcSmsSubmit.setUnique(Misc.generateUniqueId());
            sdcSmsSubmit.setLogUnique(reference);
            sdcSmsSubmit.setFromNumber(fromNumber);
            sdcSmsSubmit.setToNumber(toNumber);
            sdcSmsSubmit.setMsgBody(msgBody);
            sdcSmsSubmit.setNetworkCode(networkCode);
            sdcSmsSubmit.setStatus("RECEIVED");
            sdcSmsSubmit.setMsgCode1(msgCode1);
            sdcSmsSubmit.setMsgCode2(msgCode2);
            sdcSmsSubmit.setRefMessageUnique(msgCode1);
            sdcSmsSubmit.setMsgUdh(status); //JUST for Test, will change in near future. 
            smsdao.log(sdcSmsSubmit);
        //========== END Insertion of MOs in smsMsgLog =======================
          
                shortCode=toNumber;
                shouldStop=true; //checkstopcount.checkStopCount("ZA");
                
                if(shortCode.length()>11) shortCode = "ext" + shortCode.substring(11);
                if (shortCode.trim().equalsIgnoreCase("DOI")) shortCode = "43201";
                
                MobileClub club = null;
                java.util.List<MobileClub> clubs = UmeTempCmsCache.mobileClubsByNumber.get(shortCode);
            
                if(clubs!=null) {
                              
                //===43201 Checking and Stopping the latest Subscriptions ===========
                if(toNumber.equalsIgnoreCase("43201")) {
                System.out.println("ZASTOP  tonumber is 43201");
                SdcMobileClubUser clubUser=clubuserdao.getLatestSubscribedClub(msisdn);
                if(clubUser!=null && clubUser.getActive()==1) {
                club=mobileclubdao.getMobileClubMap().get(clubUser.getClubUnique());
                
                //This will handle all the ZADOI STOP methods. 
                stopuser.stopSingleSubscriptionNormal(msisdn,club.getUnique(), request, response);        
                    }            
                } // END if toNumber is 43201 
                 //===43201 Checking and Stopping the latest Subscriptions ===========
                
                
                // ================ Normal STops ========================
                    else {
    
                        for (int i=0; i<clubs.size(); i++) {
                        club = clubs.get(i);                               
                        SdcMobileClubUser clubUser=clubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
                        if(clubUser!=null && clubUser.getActive()==1) {
                        stopuser.stopSingleSubscriptionNormal(msisdn,club.getUnique(), request, response);           
                            }
                        } // End FOR Loop  
                    
                    } //END Else
                
                //================== END Normal Stops ==========================
                
                
                
                
                
                
            } //END If Clubs!=null
            
            } // End STOP Message
         
        if(!stopmsg) {
            
            String keyClubUnique="";
            MobileClub club=null;
            UmeClubDetails userclubdetails = null;
            SdcMobileClubUser clubUser=null;
            boolean sendWelcome=false;
            Map<String,Object>subscriptionMap=new HashMap<String,Object>();
            
            //====== START Kenya Subscription ========================
            if(msisdn.startsWith("254")){
                keyClubUnique = smskeywordao.getClubUnique(msgBody.toLowerCase(), "ke");
                
                if (keyClubUnique != null && !keyClubUnique.equals("")) {
                club = UmeTempCmsCache.mobileClubMap.get(keyClubUnique);
                }
               
               if(club==null) return;
               if(club!=null){
                   userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
               }
               
               //====== Subscription Initialisation =========================
               	if(userclubdetails.getBillingType().equalsIgnoreCase("subscription")){
                    subscriptionMap=createOrUpdateSubscription(msisdn,club,"campaignId","networkId","landingPage");	
                    user=(UmeUser)subscriptionMap.get("user");
                    clubUser=(SdcMobileClubUser)subscriptionMap.get("clubUser");
                    sendWelcome=(boolean)subscriptionMap.get("sendWelcome");
                    addBillingPlan(user,clubUser,userclubdetails,club,"networkId");
                }
                biLog(user,"networkId",request,response);
                
                //====== END Subscription Initialisation ============= 
                //======= Action According to Club Details ===================
                
                if(userclubdetails.getServiceType().equalsIgnoreCase("content") && user!=null && clubUser!=null){
					
                    if(sendWelcome){
			sendWelcomeSms(user,clubUser,club);
			sendPersonalLink(user,clubUser,club);	                
                    }
                }
		//=========== If it is Provided by UME

                
                
                //======= End Action According to Club Details ===================
                
                
            } //END msisdn starts from 254 for Kenya Subscription 
         
            else{           
                String resp="OK";
                if(status.equalsIgnoreCase("DELIVRD")) {
                resp="OK";      
                status="delivered";
            }
            else  resp="ERROR";
            smsdao.updateResponse(reference,status,SdcMiscDate.toSqlDate(new Date()),resp,"");
            }
        
        } //END IF not STOP Message
            
    
    
    
    
    
    } //END ProcessRequest
    
    
    	public Map<String,Object> createOrUpdateSubscription(String msisdn,MobileClub club,String campaignId,String networkId,String landingPage){
		boolean sendWelcome=false;
		SdcMobileClubUser clubUser = null;
		UmeUser user=null;
		String subsresponse = subscriptioncreation.checkSubscription(msisdn, club, campaignId, 1, "", networkId, "", landingPage,"");
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

		if (subsresponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY")
				|| subsresponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY")) {
			sendWelcome = true;
		}

		Map<String,Object> subscriptionMap=new HashMap<String,Object>();
		subscriptionMap.put("user",user);
		subscriptionMap.put("clubUser", clubUser);
		subscriptionMap.put("sendWelcome",sendWelcome);
		return subscriptionMap;
		
	}
        
        public void addBillingPlan(UmeUser user,SdcMobileClubUser clubUser,UmeClubDetails userclubdetails,MobileClub club,String networkId){
	MobileClubBillingPlan billingplan = new MobileClubBillingPlan();
        billingplan.setTariffClass(club.getPrice());
        billingplan.setActiveForAdvancement(1);
        billingplan.setActiveForBilling(1);
        billingplan.setAdhocsRemaining(0.0);
        billingplan.setBillingEnd(clubUser.getBillingEnd());
        billingplan.setClubUnique(club.getUnique());
        billingplan.setContractType("");
        billingplan.setLastPaid(new Date());
        billingplan.setLastSuccess(new Date(0));
        billingplan.setLastPush(new Date(0));
        billingplan.setNetworkCode(networkId);
        billingplan.setNextPush(clubUser.getBillingEnd());
        billingplan.setParsedMobile(user.getParsedMobile());
        billingplan.setPartialsPaid(0.0);
        billingplan.setSubscribed(clubUser.getSubscribed());
        billingplan.setPartialsRequired(Double.parseDouble(String.valueOf(userclubdetails.getFrequency() + "")));
        billingplan.setPushCount(0.0);
        billingplan.setServiceDate(new Date());
        billingplan.setSubUnique(clubUser.getUserUnique());
        billingplan.setExternalId(""); //This is for Italy SubscriptionId so just setting the values. 
        //billingplan.setServiceDateBillsRemaining(Double.parseDouble(String.valueOf(userclubdetails.getFrequency())));
        billingplan.setServiceDateBillsRemaining(0.0);
        billingplandao.insertBillingPlan(billingplan);
		
	}
        
       public void biLog(UmeUser user,String networkId,HttpServletRequest request,HttpServletResponse response){
		SdcRequest sdcRequest=new SdcRequest(request);
		UmeDomain dmn=sdcRequest.getDomain();
		String campaignId=sdcRequest.get("cid");
		//Handset handset=handsetdao.getHandset(request);
		String landingPage=sdcRequest.get("landingPage");
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		campaigndao.log("KEMO", landingPage, user.getUnique(), user.getParsedMobile(),null, dmn.getUnique(), campaignId, club.getUnique(), "SUBSCRIBED", 0, request, response, networkId, "smsoptin", "", "");
       }
       
       
       	public void sendWelcomeSms(UmeUser user,SdcMobileClubUser clubUser,MobileClub club){
		
		List<UmeClubMessages> welcomeMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Welcome");
                //UmeDomain dmn=sdcRequest.getDomain();
                UmeDomain dmn=umesdc.getDomainMap().get(club.getWapDomain());
        //for(int i=0;i<welcomeMessages.size();i++){
            
        	KenyaSms freeKenyaSms=new KenyaSms();
        	//APIRequestResponse apiRequestResponse=freeKenyaSms.processRequest("Sending Welcome SMS", welcomeMessages.get(i).getaMessage(), club.getOtpServiceName(), clubUser.getParsedMobile(),"http://"+dmn.getDefaultUrl()+"/status.jsp");
                APIRequestResponse apiRequestResponse=freeKenyaSms.processRequest("Sending Welcome SMS", club.getWelcomeSms(), club.getOtpServiceName(), clubUser.getParsedMobile(),"http://"+dmn.getDefaultUrl()+"/status.jsp");
        	System.out.println("Welcome Message Response Success or Not: "+apiRequestResponse.isRequestSuccess());
        	System.out.println("Welcome Message Response Error Code : "+apiRequestResponse.getErrorCode());
        	System.out.println("Welcome Message Response Error Description: "+apiRequestResponse.getErrorText());
        //}
		
	}
	
	public void sendPersonalLink(UmeUser user,SdcMobileClubUser clubUser,MobileClub club){
		try {
			//UmeDomain dmn=sdcRequest.getDomain();
                        UmeDomain dmn=umesdc.getDomainMap().get(club.getWapDomain());
			String personalLink="Your Personal Link to the service is " + "http://" + dmn.getDefaultUrl() + "/?id=" + user.getWapId();
			KenyaSms freeKenyaSms=new KenyaSms();
			APIRequestResponse apiRequestResponse=freeKenyaSms.processRequest("Sending Personal Link", personalLink, club.getOtpServiceName(), clubUser.getParsedMobile(),"http://"+dmn.getDefaultUrl()+"/status.jsp");
			System.out.println("Personal Link Response Success or Not: "+apiRequestResponse.isRequestSuccess());
        	System.out.println("Personal Link Response Error Code : "+apiRequestResponse.getErrorCode());
        	System.out.println("Personal Link Response Error Description: "+apiRequestResponse.getErrorText());
        } catch (Exception e) {
            e.printStackTrace();
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
