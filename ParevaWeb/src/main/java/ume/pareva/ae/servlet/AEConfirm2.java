/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.ae.servlet;
/**
 *
 * @author trung
 */
import com.mitchellbosecke.pebble.PebbleEngine; //
import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.ae.util.AESendMessage;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
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
import ume.pareva.pojo.PassiveVisitor;
import ume.pareva.pojo.QuizValidation;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeClubMessages;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.MiscCr;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.uk.CompetitionMessage;
import ume.pareva.util.ValidationUtil;

/**
 *
 * @author madan
 */
@WebServlet(name = "AEConfirm2", urlPatterns = {"/AEConfirm2"})
public class AEConfirm2 extends HttpServlet {

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
    AESendMessage aesendmessage;

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
    AESendMessage aeSendMessage;

    private final Logger logger = LogManager.getLogger(AEConfirm2.class.getName());

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
        ThreadContext.put("ROUTINGKEY", "AE");
        logger.info("aeconfirm " + "ProcessREQEST is called upon ");

        HttpSession session = request.getSession();
        logger.info("aeconfirm " + "session id is  " + session.getId());
        ServletContext application = request.getServletContext(); //.setAttribute(null, request);
        logger.info("aeconfirm " + "servletContext is  " + application.getContextPath());

        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();
        String ddir = dmn.getDefPublicDir();
        String lang = aReq.getLanguage().getLanguageCode();
        String defClubDomain = "5510024809921CDS"; // This is default domain in users table.
        boolean passiveupdate = true; //This will decide if we need to update passiveVisitor or not.
        boolean sendTeaser = false;

        PebbleEngine engine = (PebbleEngine) request.getAttribute("engine");
        if (engine == null) {
            engine = (PebbleEngine) session.getAttribute("engine");
        }
        if (engine == null) {
            engine = templateEngine.getTemplateEngine(dmn.getUnique());
        }

        PrintWriter writer = response.getWriter();
        Map<String, Object> context = new HashMap();
        context.put("contenturl", "http://" + dmn.getContentUrl());

        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
        UmeClubDetails userClubDetails = null;
        if (club != null) {
            userClubDetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        }

        String msg = userClubDetails.getTeaser();
        
        String msisdn = aReq.get("msisdn");
        String campaignId = aReq.get("cid");
        String confirmlanding = aReq.get("landingpage");
        String myisp = aReq.get("myisp");
        String network = aReq.get("network").toLowerCase();
        String shortCode = this.getAEShortCodeFromNetwork(network);

        String pubId = (String) request.getAttribute("cpapubid");
        if (pubId == null) {
            pubId = (String) session.getAttribute("cpapubid");
        }
        if (pubId == null) {
            pubId = "";
        }

        context.put("campaignid", campaignId);
        context.put("landingpage", confirmlanding);
        context.put("myisp", myisp);
        context.put("cpapubid", pubId);
        context.put("msisdn", msisdn);

//        String networkname = myisp.toLowerCase();
//        try {
//            networkname = mobilenetwork.getMobileNetwork("AE", networkname);
//        } catch (Exception e) {
//            networkname = "unknown";
//        }

//        if (msisdn.startsWith("03")) {
//            msisdn = "39" + msisdn.substring(1);
//        }
//        if (msisdn.startsWith("0039")) {
//            msisdn = msisdn.substring(2);
//        }

        //======== HANDSET Recognition ==============
        Handset handset = (Handset) session.getAttribute("handset");
        if (handset == null) {
            handset = (Handset) request.getAttribute("handset");
        }
        if (handset == null) {
            handset = handsetdao.getHandset(request);
        }
        if (handset != null) {
            session.setAttribute("handset", handset);
            request.setAttribute("handset", handset);
        }
        //======== END HANDSET Recognition ==============
        boolean validMsisdn = true; //validationutil.isNORValidPhone(msisdn);
        System.out.println("AErconfirm VALIDATION of msisdn " + msisdn + " is " + validMsisdn);
        if (validMsisdn) {
            campaigndao.log("aerconfirmweb", confirmlanding, msisdn, msisdn, handset, domain, campaignId, club.getUnique(), "MANUAL", 0, request, response, network.toLowerCase(), "", "", "", pubId);
            System.out.println("aerconfirm VALIDATION of msisdn INSIDE CONDITION " + msisdn + " is " + validMsisdn);
            String[] toAddress = {msisdn};
            context.put("mid", MiscCr.encrypt(msisdn));
            context.put("msisdn", msisdn);
            //======== FINDING IF USER EXISTS OR NOT ==========================
            String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", defClubDomain);
            if (!userUnique.equals("")) {
                user = umeuserdao.getUser(msisdn);
            }

            if (user != null) {
                SdcMobileClubUser clubUser = null;
                clubUser = user.getClubMap().get(club.getUnique());
                if (clubUser == null) {
                    clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                }
                if ((clubUser != null && clubUser.getActive() != 1) || (clubUser == null)) {
                    context.put("subscribed", "false");
//                    QuizValidation validation = new QuizValidation();
//                    validation.setaParsedMobile(msisdn);
//                    validation.setaCampaign(campaignId);
//                    validation.setaClubUnique(club.getUnique());
//                    int inserted = quizvalidationdao.insertValidationRecord(validation);
                    sendTeaser = true;

                } else { //clubuser is active then
                    System.out.println("ae page " + clubUser.getParsedMobile() + "  " + clubUser.getActive() + " inside else condition ");
                    context.put("subscribed", "true");
                    passiveupdate = false;
                }
            } //End if user!=null
            //==========END IF USER EXISTS OR NOT ============================
            else { // if user is null or for new user
                context.put("subscribed", "false");
                sendTeaser = true;

            }
            System.out.println("aerconfirm the value of sendTeaser is " + sendTeaser + "For msisdn " + msisdn);
            if (sendTeaser) {
                java.util.List<UmeClubMessages> teaserMessages = umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Teaser");
                msg = teaserMessages.get(0).getaMessage();
                System.out.println("aedr compmessage ie confirm " + toAddress[0] + " msg is " + msg + " shortCode is " + shortCode);
                
                //TODO send TEASER message
//                aesendmessage.requestAESendSmsTimer(toAddress, msg, shortCode, club, userClubDetails, 20 * 10, false);
//                aesendmessage.requestAESendSmsTimer(toAddress, msg, "1154", club, userClubDetails, 20 * 10, false);
                
                aeSendMessage.requestAESendSmsTimer(msisdn, network, msg, shortCode, "", "Teaser", club, userClubDetails, 1000, false);
            }
            boolean exist = passivevisitordao.exists(msisdn, club.getUnique());
            if (!exist) {
                PassiveVisitor visitor = new PassiveVisitor();
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
            } else if (exist) {
                if (passiveupdate) { // If user is null OR if clubUser is not active
                    PassiveVisitor visitor = passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
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
            if (campaignId != null && !campaignId.equalsIgnoreCase("")) {
                MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);

                if (cmpg != null && cmpg.getSrc().endsWith("CPA")) {

                    String cpaparameter1 = (String) session.getAttribute("cpaparam1");
                    String cpaparameter2 = (String) session.getAttribute("cpaparam2");
                    String cpaparameter3 = (String) session.getAttribute("cpaparam3");
                    String cpalogQuery = "UPDATE cpavisitlog set aParsedMobile='" + msisdn + "',isSubscribed='1',aSubscribed='2011-01-01' WHERE aCampaignId='" + cmpg.getUnique() + "' "
                            + " AND (aHashcode='" + cpaparameter1 + "' AND cpacampaignid='" + cpaparameter2 + "'" + " AND clickid='" + cpaparameter3 + "') ";
                    queryhelper.executeUpdateQuery(cpalogQuery, "IMIConfirm Servlet");

                }

            } //END campaignId!=null checks. 

            //====== END updating CPA parameters with msisdn value ==============
        } //END Valid msisdn 
        else if (!validMsisdn) {
            context.put("subscribed", "error");
            context.put("statusMsg", "The mobile number you entered was invalid");
            context.put("statusMsg2", "Resend message");

        } else { //FOR ALL OTHER ERROR 
            context.put("subscribed", "error");
            context.put("statusMsg", "Error occurred");
            context.put("statusMsg2", "Resend message");

        }

        try {
            engine.getTemplate("confirm2").evaluate(writer, context);
        } catch (Exception e) {
        }
    }
    
    private String getAEShortCodeFromNetwork(String network){
        String shortCode = "1154";
        if(network.equalsIgnoreCase("Etisalat"))
            shortCode = "MobiPlanet";
        return shortCode;
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

