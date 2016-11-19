package ume.pareva.uk;

import com.mitchellbosecke.pebble.PebbleEngine;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
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
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.MobileNetworksDao;
import ume.pareva.dao.PassiveVisitorDao;
import ume.pareva.dao.QueryHelper;
import ume.pareva.dao.QuizValidationDao;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.PassiveVisitor;
import ume.pareva.pojo.QuizValidation;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.util.ValidationUtil;
import ume.pareva.pojo.UmeClubMessages;
import ume.pareva.sdk.MiscCr;

/**
 *
 * @author madan
 */
@WebServlet(name = "IMIConfirm", urlPatterns = {"/IMIConfirm"})
public class IMIConfirm extends HttpServlet {
    
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
    UmeClubDetailsDao umeclubdetailsdao;
    
    @Autowired
    UmeTempCache umesdc;
   
    
    private final Logger logger = LogManager.getLogger(IMIConfirm.class.getName());
    
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
        
        ThreadContext.put("ROUTINGKEY", "UK");
        ThreadContext.put("EXTRA","");
        logger.info("imiukconfirm "+"ProcessREQEST is called upon ");

        HttpSession session = request.getSession();
        logger.info("imiukconfirm "+"session id is  "+session.getId());
        ServletContext application = request.getServletContext(); //.setAttribute(null, request);
        logger.info("imiukconfirm "+"servletContext is  "+application.getContextPath());
        
        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();
        String ddir = dmn.getDefPublicDir();
        String lang = aReq.getLanguage().getLanguageCode();
        String defClubDomain ="5510024809921CDS"; // This is default domain in users table.
        
        String freeCostId="1";
        String serviceId="1";
        //String deliveryReceipt="13";
        String deliveryReceipt="11";
        String typeId="2";
        boolean passiveupdate=true; //This will decide if we need to update passiveVisitor or not. 
        
        PebbleEngine engine=(PebbleEngine)request.getAttribute("engine");
        if(engine==null) engine=(PebbleEngine) session.getAttribute("engine");
        if(engine==null) engine=templateEngine.getTemplateEngine(dmn.getUnique());
        
        PrintWriter writer = response.getWriter();
        Map<String, Object> context = new HashMap();
        context.put("contenturl","http://"+dmn.getContentUrl());
        
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
        MobileClub partnerClub=null;
        UmeClubDetails userClubDetails=null;
        if(club!=null) {
	userClubDetails=UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        partnerClub=UmeTempCmsCache.mobileClubMap.get(userClubDetails.getaPartnerClub());
        }
        
        List<UmeClubMessages> teaserMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Teaser");
        
        //String teasermsg=userClubDetails.getTeaser();
        String teasermsg=null;
        try{
         System.out.println("uknewlogic the size of teaserMessages is "+teaserMessages.size()+" for clubunique "+club.getUnique());
        teasermsg=teaserMessages.get(0).getaMessage();
        }catch(Exception e){}
        if(teasermsg!=null) {
            System.out.println("uknewlogic Teaser Message: "+teasermsg);
        }
        if(teasermsg==null || "".equals(teasermsg))
            teasermsg=userClubDetails.getTeaser();
        
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
            try{
                networkname=mobilenetwork.getMobileNetwork("UK",networkname);
            }catch(Exception e){networkname="unknown";}
      
       if(msisdn.startsWith("07")){
            msisdn="44"+msisdn.substring(1);
        }
       msisdn=msisdn.replaceAll("\\s","");
       boolean validMsisdn=validationutil.isValidPhone(msisdn);
       String transactionId=String.valueOf(Misc.generateUniqueIntegerId())+"-"+club.getUnique();
       
       
          //======== HANDSET Recognition ==============
        Handset handset = (Handset) session.getAttribute("handset");
        if(handset==null) handset=(Handset) request.getAttribute("handset");
        if(handset==null) handset=handsetdao.getHandset(request);
        if (handset != null) {
            session.setAttribute("handset", handset);
            request.setAttribute("handset", handset);            
        }
        //======== END HANDSET Recognition ==============
       
       if(validMsisdn) {
        msisdn=msisdn.replaceAll("\\s","");
       campaigndao.log("imiconfirmweb3", confirmlanding, msisdn,msisdn, handset,domain, campaignId, club.getUnique(), "MANUAL", 0, request,response,networkname.toLowerCase(),"","","",pubId);
       context.put("mid",MiscCr.encrypt(msisdn));
       context.put("msisdn",msisdn);
       MobileClubCampaign cmpg =null;
       try{
                  //======updating CPA params with msisdn 
        if(campaignId!=null && !campaignId.equalsIgnoreCase("")){
            cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
            
            if(cmpg!=null && cmpg.getSrc().endsWith("CPA")) {
            
            String cpaparameter1=(String) session.getAttribute("cpaparam1");
            String cpaparameter2=(String) session.getAttribute("cpaparam2");
            String cpaparameter3=(String) session.getAttribute("cpaparam3");
         String cpalogQuery="UPDATE cpavisitlog set aParsedMobile='"+msisdn+"',isSubscribed='1',aSubscribed='2011-01-01' WHERE aCampaignId='"+cmpg.getUnique()+"' "
                 + " AND (aHashcode='"+cpaparameter1+"' AND cpacampaignid='"+cpaparameter2+"'" + " AND clickid='"+cpaparameter3+"') ";   
            queryhelper.executeUpdateQuery(cpalogQuery,"IMIConfirm Servlet");
            
         }
         
            }
        
        //====== END updating CPA parameters with msisdn value ==============
       }
       catch(Exception e){}
       
       
        
       //==========================STARTING NEW UK LOGIC ==========================================//
         PFIUtil pfiUtil=new PFIUtil();
         
         //HLR LOOKUP 
         //doHLRLookup(String msisdn,String sessionToken,MobileClub partnerclub) 
         String sessionToken=UUID.randomUUID().toString();
         HLRLookupResponse hlrLookupResponse=pfiUtil.doHLRLookup(msisdn,sessionToken,partnerClub);
		System.out.println("uknewlogic Network- "+hlrLookupResponse.getNetwork());
		System.out.println("uknewlogic HlrLookUpType- "+hlrLookupResponse.getHlrLookupType());
		System.out.println("uknewlogic MSISDN- "+hlrLookupResponse.getMsisdn());
		System.out.println("uknewlogic ERROR Text -"+hlrLookupResponse.getErrorText());
       
        String pfinetwork=hlrLookupResponse.getNetwork();
        if(pfinetwork!=null && (pfinetwork.equalsIgnoreCase("VODUK")) || pfinetwork.equalsIgnoreCase("TMOUK"))
            //|| pfinetwork.equalsIgnoreCase("3MOUK")))  //3MOUK is just for test. 
                {
            boolean sendPersonal=true;
            if(cmpg==null) sendPersonal=true;
           if(cmpg!=null ){
               if(cmpg.getaType().equalsIgnoreCase("email") || cmpg.getaType().equalsIgnoreCase("coreg") || cmpg.getaType().equalsIgnoreCase("sms")) sendPersonal=false; 
               else sendPersonal=true;
           }
           
                if(sendPersonal){
                    //Send Personal link and return
                //sendPersonalLink(String msisdn, MobileClub partnerClub, String message, String destinationurl)
                String pfimessage="Free Msg! Please click to confirm your request: {url}";
                String partnerdomainunique=partnerClub.getWapDomain();
                UmeDomain partnerdomain=umesdc.getDomainMap().get(partnerdomainunique);
                String domainurl=partnerdomain.getDefaultUrl();
                //String domainurl=partnerdomain.getAliasUrl1();
                if(domainurl==null || "".equalsIgnoreCase(domainurl))
                    domainurl="uk.clubvoucher.co.uk";
                
                String parameters="cid="+campaignId
                        +"&pmob="+msisdn
                        +"&clubname="+java.net.URLEncoder.encode(club.getClubName(),"UTF-8")
                        +"&loghits=no";
                
                String destinationurl="http://"+domainurl+"/?"+parameters;
                //String destinationurl="http://uk.quiz2win.mobi/?cid="+campaignId+"&pmob="+msisdn+"&clubname="+club.getClubName();
                System.out.println("uknewlogic Network- destination URL is "+destinationurl);
                SendPersonalLinkResponse sendPersonalLinkResponse=pfiUtil.sendPersonalLink(msisdn,partnerClub,pfimessage,destinationurl);
		System.out.println("uknewlogic messageSent "+sendPersonalLinkResponse.getMessageSent());
		System.out.println("uknewlogic getFault "+sendPersonalLinkResponse.getFault());
                
                campaigndao.log("psms2pfi", confirmlanding, msisdn,msisdn, handset,domain, campaignId, club.getUnique(), "REDIRECTED", 0, request,response,networkname.toLowerCase(),"","",partnerClub.getUnique(),pubId);       
                String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", defClubDomain);
                if (!userUnique.equals("")) user = umeuserdao.getUser(msisdn);
                if(user!=null){
                    SdcMobileClubUser clubUser = null;
                    clubUser = user.getClubMap().get(club.getUnique());
                    if(clubUser==null){
                    clubUser=umemobileclubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
                    }
                    if ((clubUser!=null && clubUser.getActive()!=1) ||(clubUser==null)) {
                        context.put("subscribed","false");
                    }
                    else context.put("subscribed","true");
                } //END if user!=null
                else context.put("subscribed","false");
                
                   try{
                       System.out.println("uknewlogic loading confirm2 now ");
                       System.out.println("uknewlogic getting context variable "+context.get("subscribed")+" -- "+context.get("mid") +" - "+context.get("msisdn"));
                        engine.getTemplate("confirm2").evaluate(writer, context);
                    }catch(Exception e){}
                    return;
                }
       }
       //===========================END UK NEW LOGIC =========================================//
       
       
       
       
             
       //======== FINDING IF USER EXISTS OR NOT ==========================
       String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", defClubDomain);
	if (!userUnique.equals("")) user = umeuserdao.getUser(msisdn);
	if (user!=null) {
		SdcMobileClubUser clubUser = null;
        clubUser = user.getClubMap().get(club.getUnique());
        if(clubUser==null){
	        clubUser=umemobileclubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
        }
        if ((clubUser!=null && clubUser.getActive()!=1) ||(clubUser==null)) {
            String[] toAddress={msisdn};
                context.put("subscribed","false");
                QuizValidation validation=new QuizValidation();
                validation.setaParsedMobile(msisdn);
                validation.setaCampaign(campaignId);
                validation.setaClubUnique(club.getUnique());
                int inserted=quizvalidationdao.insertValidationRecord(validation);
                compmessage.requestSendSmsTimer(toAddress,freeCostId,serviceId,deliveryReceipt,teasermsg,shortCode,transactionId,typeId,club,10*10,true, campaignId);
                System.out.println("uknewlogic teaser messages sent to "+toAddress[0]+ " message : "+teasermsg);
            }
        else{
                System.out.println("quiz2winlanding page "+clubUser.getParsedMobile()+"  "+clubUser.getActive()+" inside else condition ");
	    	context.put("subscribed","true");
                //context.put("mid",MiscCr.encrypt(clubUser.getParsedMobile()));
                passiveupdate=false;
                
	    }
     
       } //End if usre!=null
        
        //==========END IF USER EXISTS OR NOT ============================
    
        else { // if user is null or for new user
            String[] toAddress={msisdn};
            context.put("subscribed","false"); 
            compmessage.requestSendSmsTimer(toAddress,freeCostId,serviceId,deliveryReceipt,teasermsg,shortCode,transactionId,typeId,club,10*10,true,campaignId);
            
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
            
     
     
       } //End of validMsisdn
       
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
       
       
       
    } //End of processrequest

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
