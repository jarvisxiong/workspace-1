package ume.pareva.uk;

import com.mitchellbosecke.pebble.PebbleEngine;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.MobileNetworksDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.pojo.CampaignHitCounter;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.template.TemplateEngine;
import ume.pareva.userservice.CpaRevShareServices;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;

/**
 *
 * @author madan
 */
@WebServlet(name = "PFIIndex", urlPatterns = {"/PFIIndex"})
public class PFIIndex extends HttpServlet {
    private final Logger logger = LogManager.getLogger(PFIIndex.class.getName());
    
    @Autowired
    InternetServiceProvider ipprovider;
    
    @Autowired
    HandsetDao handsetdao;
    
    @Autowired
    LandingPage landingpage;
    
    @Autowired
    CpaRevShareServices cparevshare;
    
    @Autowired
    MobileNetworksDao mobilenetwork;
    
    @Autowired
    MobileClubCampaignDao campaigndao;
    
    @Autowired
    MobileClubDao mobileclubdao;
    
    @Autowired
    CampaignHitCounterDao campaignhitcounterdao;
    
    @Autowired
    TemplateEngine templateEngine;
    
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
        logger.info("pfiukindex "+"ProcessREQEST is called upon ");

        HttpSession session = request.getSession();
        logger.info("pfiukindex "+"session id is  "+session.getId());
        ServletContext application = request.getServletContext(); //.setAttribute(null, request);
        logger.info("pfiukindex "+"servletContext is  "+application.getContextPath());
        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();
        String ddir = dmn.getDefPublicDir();
        String lang = aReq.getLanguage().getLanguageCode();
        String originalmsisdn=aReq.get("pmob");
        String fromclubname=aReq.get("clubname");
        logger.info("pfiukindex "+" after SDC request domain "+domain);
        PrintWriter writer = response.getWriter();
        Map<String, Object> context = new HashMap();
        
        PebbleEngine engine=templateEngine.getTemplateEngine(dmn.getUnique());
        
         String fileName ="index.jsp";// request.getServletPath();
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        System.out.println("pfiukindex "+" FileName is  "+fileName);

        String cloudfronturl = dmn.getContentUrl();
        session.setAttribute("cloudfrontUrl", cloudfronturl);
        application.setAttribute("cloudfrontUrl", cloudfronturl);
        
        session.setAttribute("dmn",dmn);
        request.setAttribute("dmn",dmn);
        session.setAttribute("aReq",aReq);
        
        session.setAttribute("mobileclubdao", mobileclubdao);
        request.setAttribute("mobileclubdao", mobileclubdao);
        
        logger.info("pfiukindex "+"contentUrl is  "+cloudfronturl);
        String msisdn = aReq.getMsisdn();
        String enMsisdn = aReq.get("mid");
        String pubId = "";
        MobileClubCampaign cmpg = null;

        String simulate = aReq.get("simulate");
        String pageName = aReq.get("pageName");
        request.setAttribute("simulate", simulate);
        request.setAttribute("pageName", pageName);
        
          String myip = request.getHeader("X-Forwarded-For");
        if (myip != null) {
            int idx = myip.indexOf(',');
            if (idx > -1) {
                myip = myip.substring(0, idx);
            }
        }
        String myisp=ipprovider.findIsp(myip);;
        session.setAttribute("userip", myip);
        request.setAttribute("userip", myip);
        session.setAttribute("userisp",myisp);
        request.setAttribute("userisp",myisp);
        session.setAttribute("networkid",myisp);
        request.setAttribute("networkid",myisp);
        
                  //======== HANDSET Recognition ==============
        Handset handset = handsetdao.getHandset(request);
        if (handset != null) {
            session.setAttribute("handset", handset);
            request.setAttribute("handset", handset);            
        }
        //======== END HANDSET Recognition ==============
        
         //========= Reading Encrypted MSISDN ====================== 
        if (!enMsisdn.equals("")) {
            String deMsisdn = MiscCr.decrypt(enMsisdn);
            if (!deMsisdn.equals("")) {
                if (deMsisdn.startsWith("0")) {
                    deMsisdn = "44" + deMsisdn.substring(1);
                }
                msisdn = deMsisdn;
            }
        }
         //========= END  Reading Encrypted MSISDN ====================== 
        
        //========= Reading Campaign and Initializing Club and Campaign ======================
        String campaignId = aReq.get("cid");
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
        UmeClubDetails userclubdetails=null;
        String clubUnique = "";
        if (club != null) {
            clubUnique = club.getUnique();
            userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
            request.setAttribute("club",club);
            session.setAttribute("club",club);
            
            logger.info("pfiukindex  The club information is "+club.getUnique()+" merchantid is "+club.getSmsExt());
        }

        if (campaignId != null && campaignId.trim().length() > 0) {

            if (!campaignId.equals("")) {
                cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
                 context.put("campaignid",campaignId); //cid
                 request.setAttribute("cmpg",cmpg);
                 session.setAttribute("cmpg",cmpg);
                 request.setAttribute("campaignId",campaignId);
                 session.setAttribute("campaignId",campaignId);
            }
           
        }
        
        //========= Reading Campaign and Initializing Club and Campaign ======================
         // ====  Retrieving  LandingPage in this part =========================================
        String landingPage = "";
                     
            if (cmpg != null && cmpg.getSrc().trim().equalsIgnoreCase("test")) {
                landingPage = landingpage.initializeLandingPage(domain, campaignId, "test");           
            }
            else {
                if (!campaignId.equals("")) {
                    landingPage = landingpage.initializeLandingPage(domain, campaignId, "all");                    
                } 
                else {
                    landingPage = landingpage.initializeLandingPage(domain);
                    }
                
                }
            
                request.setAttribute("landingPage", landingPage);
                session.setAttribute("landingPage",landingPage);
                request.setAttribute("cid",campaignId);
                session.setAttribute("cid",campaignId);
                request.setAttribute("engine",engine);
                session.setAttribute("engine",engine);
                request.setAttribute("landingpageengine",landingpage);
                session.setAttribute("landingpageengine",landingpage);
                
                request.setAttribute("domain",domain);
                session.setAttribute("domain",domain);
        // ====  End Landing Page Retrieval =============================================
                
        //=======  CPA and RS =================
            if(cmpg!=null && session.getAttribute("bilogged")==null){ // session getATtribute bilogged is for avoiding duplicate logging.
                //cparevshare.cparevshareLog(request, cmpg,msisdn,transaction_ref, session,"entry");
                try{
                pubId=session.getAttribute("cpapubid")+"";
                }
                catch(Exception e){pubId="";}
            
                request.setAttribute("cpapubid",pubId);
                session.setAttribute("cpapubid",pubId);  
                
    }
         //=======  END CPA and RS =================
        
            try{
                Thread.sleep(500);
                System.out.println("msisdnheader "+request.getHeader("X-PFI-Alias"));
                if(msisdn==null || "".equalsIgnoreCase(msisdn)){
                msisdn=request.getHeader("X-PFI-Alias");
                System.out.println("pfiukindex value of msisdn "+msisdn);
                }
            }catch(Exception e){msisdn="";}
        
        String transaction_ref="";
        String contentid=Misc.generateLogin(5)+"_"+campaignId;
        transaction_ref=request.getHeader("X-PFI-SessionToken");
        if(transaction_ref==null || transaction_ref.trim().length()<=0)
        transaction_ref=(String) session.getAttribute("sessiontoken");
        
            if(transaction_ref==null || transaction_ref.trim().length()<=0){
                UUID submission_id = UUID.randomUUID();
                transaction_ref=submission_id+""; 
                session.setAttribute("sessiontoken",transaction_ref);
                request.setAttribute("sessiontoken",transaction_ref);
                System.out.println("pfiukindex pfitoken "+transaction_ref);  
            }
            
        //=========== Logging to BI ===========================================
                          try {
            String networkname = myisp.toLowerCase();
            networkname = mobilenetwork.getMobileNetwork("UK", networkname);
           
                System.out.println("PFI INDEX JSP LOGGING TO CAMPAIGN LOG ");
                if (!aReq.get("cid").equalsIgnoreCase("") && session.getAttribute("bilogged")==null && request.getParameter("loghits")==null) {
                    campaigndao.log("pfiukweb3", landingPage, msisdn, msisdn, handset, domain, campaignId, clubUnique, "INDEX", 0, request, response, networkname,"","","",pubId);
                    
                    if(msisdn!=null && !msisdn.trim().isEmpty() && msisdn.trim().length()>0)
                        campaigndao.log("pfiukweb3", landingPage, msisdn, msisdn, handset, domain, campaignId, clubUnique, "IDENTIFIED", 0, request, response, networkname,"","","",pubId);
                    session.setAttribute("bilogged","true"); //This session is set to avoid duplicate BI logging.                     
                    
                    //=== To update campaignHit counter after logging for LandingPage Logic Rotation=====
                    Date today = new Date();
                    CampaignHitCounter campaignHitCounter = campaignhitcounterdao.HitRecordExistsOrNot( today, domain, campaignId, landingPage);
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
                        //campaignHitCounter.setDate(today);
                        campaignhitcounterdao.updateCampaignHitCounter(campaignHitCounter);
                    }
                    //=== END To update campaignHit counter after logging for LandingPage Logic Rotation=======
            } //END Logging to INDEX HITS

            

        } catch (Exception e) {
            System.out.println("PFIUK Index Error for campaignlog " + e);
            logger.error("PFIUK INDEX ERROR FOR CAMPAIGN LOG {}", e.getMessage());
            logger.error("PFIIUK INDEX ERROR FOR CAMPAIGN LOG ", e);
        }
        //=========== END Loggint to BI =====================================
            if(club!=null){
            context.put("contenturl","http://"+dmn.getContentUrl());
            context.put("endofmonth",getEndOfmonth());
            context.put("landingpage",landingPage);
            context.put("myisp",myisp);
            context.put("clubname",fromclubname);
            
            if(!originalmsisdn.equalsIgnoreCase("")) context.put("msisdn",originalmsisdn);
            else context.put("msisdn",msisdn);
            context.put("transactionid",transaction_ref);
            context.put("referenceid",contentid);
            //=====TODO WITH CLUB SETTINGS !! =================
            String merchanttoken=club.getSmsExt();
                    
            if(merchanttoken==null || "".equalsIgnoreCase(merchanttoken)){
                merchanttoken=merchanttoken="FBEEBAC4-9F42-4DC4-8E5D-B481A30986B0";            

            if(dmn.getUnique().equalsIgnoreCase("3824583922341llun")) 
            merchanttoken="59E2E445-E8E3-4696-8015-037E7963F716";
            
            }
            
            System.out.println("pfiukindex merchanttoken "+dmn.getUnique()+" --- "+merchanttoken);
            request.setAttribute("merchanttoken",merchanttoken);
            session.setAttribute("merchanttoken",merchanttoken);
            response.addHeader("X-PFI-MerchantToken",merchanttoken);
            response.addHeader("X-PFI-SessionToken", transaction_ref);
             //===== END TODO WITH CLUB SETTINGS !! ===================
            
            try{
             engine.getTemplate(landingPage).evaluate(writer, context);
            }catch(Exception e){}
            
//            if(userclubdetails.getBillingType().equalsIgnoreCase("subscription")){                
//                if(userclubdetails.getServiceType().equalsIgnoreCase("content")) {
//                     
//                try {
//                        application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp").forward(request, response);
//                        return;
//                    } catch (Exception e) {
//                        logger.error("PFIUK EXCEPTION {}", e.getMessage());
//                        logger.error("PFIUK EXCEPTION ", e);
//                    } 
//            
//                 } //End if clubdetails is content
//                
//                 else if(userclubdetails.getServiceType().equalsIgnoreCase("competition")) {
//                     try{
//                     engine.getTemplate(landingPage).evaluate(writer, context);
//                     }
//                     catch(Exception e){}
//                     
//                 }
//               
//            } //END if clubDetails is subscription
            
        } //END if club!=null
        
        
    } //END of Processlist
    
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
        System.out.println(endofmonth);
        return endofmonth;
    
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
