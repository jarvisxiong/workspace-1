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
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.pojo.CampaignHitCounter;
import ume.pareva.pojo.SdcMobileClubUser;
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
@WebServlet(name = "IMIIndex", urlPatterns = {"/IMIIndex"})
public class IMIIndex extends HttpServlet {
    
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
    CampaignHitCounterDao campaignhitcounterdao;
    
    @Autowired
    TemplateEngine templateEngine;
    
    @Autowired
    MobileClubDao mobileclubdao;
    
    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;

    
    private final Logger logger = LogManager.getLogger(IMIIndex.class.getName());
    
    
    
    
    
    
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
        logger.info("imiukindex "+"ProcessREQEST is called upon ");

        HttpSession session = request.getSession();
        logger.info("imiukindex "+"session id is  "+session.getId());
        ServletContext application = request.getServletContext(); //.setAttribute(null, request);
        logger.info("imiukindex "+"servletContext is  "+application.getContextPath());
        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();
        String ddir = dmn.getDefPublicDir();
        String lang = aReq.getLanguage().getLanguageCode();
        logger.info("imiukindex "+" after SDC request ");
        PrintWriter writer = response.getWriter();
        Map<String, Object> context = new HashMap();
        
        PebbleEngine engine=templateEngine.getTemplateEngine(dmn.getUnique());
        
        String fileName ="index.jsp";// request.getServletPath();
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        System.out.println("imiindex "+" FileName is  "+fileName);

        String cloudfronturl = dmn.getContentUrl();
        session.setAttribute("cloudfrontUrl", cloudfronturl);
        application.setAttribute("cloudfrontUrl", cloudfronturl);
        
        session.setAttribute("dmn",dmn);
        request.setAttribute("dmn",dmn);
        session.setAttribute("aReq",aReq);
        
        session.setAttribute("mobileclubdao", mobileclubdao);
        request.setAttribute("mobileclubdao", mobileclubdao);
        
        logger.info("imiindex "+"contentUrl is  "+cloudfronturl);
        String msisdn = aReq.getMsisdn();
        String enMsisdn = aReq.get("mid");
        String transaction_ref = "ukcpa";
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
            System.out.println("ukmid the mid parameter is "+enMsisdn);
            String deMsisdn = MiscCr.decrypt(enMsisdn);
            if (!deMsisdn.equals("")) {
                if (deMsisdn.startsWith("0")) {
                    deMsisdn = "44" + deMsisdn.substring(1);
                    System.out.println("ukmid the deMsisdn value of mid "+enMsisdn+"  is "+deMsisdn);
                }
                msisdn = deMsisdn;
                System.out.println("ukmid  finally the value of msisdn is "+msisdn+"  of mid "+enMsisdn);
                
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
            
                   try {
            String networkname = myisp.toLowerCase();
            networkname = mobilenetwork.getMobileNetwork("UK", networkname);
           
                System.out.println("UK INDEX JSP LOGGING TO CAMPAIGN LOG ");
                if (!aReq.get("cid").equalsIgnoreCase("") && session.getAttribute("bilogged")==null) {
                    campaigndao.log("imiukweb3", landingPage, msisdn, msisdn, handset, domain, campaignId, clubUnique, "INDEX", 0, request, response, networkname,"","","",pubId);
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
            System.out.println("IMIUK Index Error for campaignlog " + e);
            logger.error("IMIUK INDEX ERROR FOR CAMPAIGN LOG {}", e.getMessage());
            logger.error("IIMIUK INDEX ERROR FOR CAMPAIGN LOG ", e);
        }
        
        if(club!=null){
            context.put("contenturl","http://"+dmn.getContentUrl());
            context.put("endofmonth",getEndOfmonth());
            context.put("landingpage",landingPage);
            context.put("myisp",myisp);
            //System.out.println("ukmid msisdn value in context variable is "+msisdn);
            context.put("msisdn",msisdn);
            
            if(userclubdetails.getBillingType().equalsIgnoreCase("subscription")){
             
                if(userclubdetails.getServiceType().equalsIgnoreCase("content")) {
                     
                try {
                        application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp").forward(request, response);
                        return;
                    } catch (Exception e) {
                        logger.error("IMIUK EXCEPTION {}", e.getMessage());
                        logger.error("IMIUK EXCEPTION ", e);
                    } 
            
                 } //End if clubdetails is content
                
                 else if(userclubdetails.getServiceType().equalsIgnoreCase("competition")) {
                       if(user!=null){
                        String clubId=aReq.get("clublink");
                        SdcMobileClubUser clubUser=umemobileclubuserdao.getClubUserByMsisdn(user.getParsedMobile(),clubId);
                       try {
                           String voucherwindomain="9524711450641llun";
                        //application.getRequestDispatcher("/" + System.getProperty("dir_" + voucherwindomain + "_pub") + "/question.jsp?msisdn="+user.getParsedMobile()+"&clubid="+club.getUnique()).forward(request, response);
                        
                        if(clubUser!=null && clubUser.getActive()==1){
                        response.sendRedirect("http://www.voucherwin.co.uk/question.jsp?msisdn="+user.getParsedMobile()+"&clubid="+clubId);
                        return;
                        }
                        else{
                            engine.getTemplate(landingPage).evaluate(writer, context);
                        }
                    } catch (Exception e) {
                        logger.error("IMIUK EXCEPTION {}", e.getMessage());
                        logger.error("IMIUK EXCEPTION ", e);
                    }
                       
                     }
                       else{
                     try{
                     engine.getTemplate(landingPage).evaluate(writer, context);
                     }
                     catch(Exception e){}
                 } //END else 
                     
                 } //END Competition
               
            } //END BillingType is subscription
            
        } //END if club!=null 
            
            
        
   
    } //End ProcessRequest 
    
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
