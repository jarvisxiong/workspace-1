package ume.pareva.it.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.*;
import ume.pareva.pojo.CampaignHitCounter;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.userservice.CpaRevShareServices;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;

/**
 *
 * @author Alex
 */
public class ITIndex extends HttpServlet {
    
        @Autowired
        UmeLanguagePropertyDao langpropdao;
        @Autowired
        HandsetDao handsetdao;
        @Autowired
        MobileClubDao mobileclubdao;
        @Autowired
        UmeTempCache umesdc;
        /*@Autowired
        RedirectSettingDao redirectsettingdao;*/
        @Autowired
        MobileClubCampaignDao campaigndao;
        @Autowired
        InternetServiceProvider ipprovider;   
        /*@Autowired
        UserClicksDao userclicksdao ;*/
        @Autowired
        LandingPage landingpage;
        @Autowired
        CampaignHitCounterDao campaignhitcounterdao;
      
        @Autowired
        UmeUserDao umeuserdao;
        @Autowired
        UmeMobileClubUserDao umemobileclubuserdao;
        
        @Autowired
        MobileNetworksDao mobilenetwork;
        
        @Autowired
        CpaRevShareServices cparevshare;

    private final Logger logger = LogManager.getLogger(ITIndex.class.getName());
    private final SimpleDateFormat longDateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

        ThreadContext.put("ROUTINGKEY", "ZA");
        logger.info("ITIndex "+"ProcessREQEST is called upon ");

        HttpSession ses = request.getSession();
        logger.info("ITIndex "+"session id is  "+ses.getId());
        ServletContext application = request.getServletContext(); //.setAttribute(null, request);
        logger.info("ITIndex "+"servletContext is  "+application.getContextPath());
        SdcRequest aReq = new SdcRequest(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();
        String ddir = dmn.getDefPublicDir();
        String lang = aReq.getLanguage().getLanguageCode();
        logger.info("ITIndex "+" after SDC request ");
        
        ses.setAttribute("mobileclubdao", mobileclubdao);
        request.setAttribute("mobileclubdao", mobileclubdao);
       
        
        String fileName ="index.jsp";// request.getServletPath();
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        System.out.println("ITIndex "+" FileName is  "+fileName);

        String cloudfronturl = dmn.getContentUrl();
        ses.setAttribute("cloudfrontUrl", cloudfronturl);
        application.setAttribute("cloudfrontUrl", cloudfronturl);
        
        logger.info("ITIndex "+"contentUrl is  "+cloudfronturl);

        //==== Identifying Sister Club ====================
        String notSubscribedClubs = aReq.get("notSubscribedClubs");
        List<String> notSubscribedUrlList = new ArrayList<String>();
        if (!notSubscribedClubs.equals("")) {
            if (notSubscribedClubs.contains(",")) {
                notSubscribedUrlList = new ArrayList<String>(Arrays.asList(notSubscribedClubs.split(",")));
                request.setAttribute("popunderDomain", notSubscribedUrlList.get((int) java.lang.Math.floor(java.lang.Math.random() * notSubscribedUrlList.size())));
                request.setAttribute("notSubscribedUrlList", notSubscribedUrlList);
            } else {
                notSubscribedUrlList.add(notSubscribedClubs);
                request.setAttribute("popunderDomain", notSubscribedUrlList.get(0));
                request.setAttribute("notSubscribedUrlList", notSubscribedUrlList);
            }
        }
        logger.info("Not subscribed Clubs: " + notSubscribedClubs);
        
         //==== END  Identifying Sister Club ====================

        String uid = "",wapid = "";
        String msisdn = aReq.getMsisdn();
        String subpage = aReq.get("pg");
        String enMsisdn = aReq.get("mid"); //reading encrypted msisdn which is sent as mid param. (broadcast, followup)
        
        // ==== Required for CPA=====
        String pubId = "";
        String campaignsrc = "";        
        String transaction_ref = "zacpa";
        // ==== CPA Requirement end ===========
        
        String redirectCampaignId = "";
        String popunderCampaignId = "";
        MobileClubCampaign cmpg = null;

        String simulate = aReq.get("simulate");
        String pageName = aReq.get("pageName");
        request.setAttribute("simulate", simulate);
        request.setAttribute("pageName", pageName);

   //=====  GET the IP Address of Visitor ============================
        String myip = request.getHeader("X-Forwarded-For");
        if (myip != null) {
            int idx = myip.indexOf(',');
            if (idx > -1) {
                myip = myip.substring(0, idx);
            }
        }
        
        String vodacomHeader = request.getHeader("x-up-vfza-id");
        String myisp = "";
        if (vodacomHeader != null && vodacomHeader.equals("65501")) {
            myisp = "vodacom";
        } else {
            myisp = ipprovider.findIsp(myip);
        }

        myisp = myisp.toLowerCase();
        if (myisp.contains("mtn")) {
            myisp = "mtn";
        } else if (myisp.contains("vodacom") || myisp.contains("research in motion uk limited")) {
            myisp = "vodacom";
        } else if (myisp.contains("cellc") || myisp.contains("cell-c") || myisp.contains("cell c")) {
            myisp = "cellc";
        } else if (myisp.contains("heita") || myisp.contains("8ta")) {
            myisp = "heita";
        } else {
            myisp = "unknown";
        }

        ses.setAttribute("userip", myip);
        request.setAttribute("userip", myip);
        
         //=====  END GET the IP Address of Visitor ============================

        //======== HANDSET Recognition ==============
        Handset handset = handsetdao.getHandset(request);
        if (handset != null) {
            ses.setAttribute("handset", handset);
            request.setAttribute("handset", handset);            
        }
        //======== END HANDSET Recognition ==============
        
        //========= Reading Encrypted MSISDN ====================== 
        if (!enMsisdn.equals("")) {
            String deMsisdn = MiscCr.decrypt(enMsisdn);
            if (!deMsisdn.equals("")) {
                if (deMsisdn.startsWith("0")) {
                    deMsisdn = "27" + deMsisdn.substring(1);
                }
                msisdn = deMsisdn;
            }
        }
         //========= END  Reading Encrypted MSISDN ====================== 

        //========= Reading Campaign and Initializing Club and Campaign ======================
        String campaignId = aReq.get("cid");
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
        String clubUnique = "";
        if (club != null) {
            clubUnique = club.getUnique();
        }

        if (campaignId != null && campaignId.trim().length() > 0) {

            if (!campaignId.equals("")) {
                cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
            }
            if (cmpg != null) {
                campaignsrc = cmpg.getSrc();
            }

        }
        
        //========= Reading Campaign and Initializing Club and Campaign ======================

        // ====  Retrieving  LandingPage in this part =========================================
        

        String landingPage = "";
        
        /*
        When returned from STS msisdn router it must contain landingapge with parameter "l" 
        */
         if(ses.getAttribute("doneMsisdnParse")!=null){
             landingPage=(String) request.getParameter("l");
             try{
                 pubId=(String) request.getParameter("pubid");
             }catch(Exception e){pubId="";}
         }

         if(landingPage==null || landingPage.isEmpty()){
             
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
            }
                request.setAttribute("landingPage", landingPage);
                ses.setAttribute("landingPage",landingPage);
                request.setAttribute("cid",campaignId);
                ses.setAttribute("cid",campaignId); //
        // ====  End Landing Page Retrieval =============================================

        //=======  CPA and RS =================
        if (!subpage.equals("subscribe")) {
            if (cmpg!=null) {
                //cparevshare.cparevshareLog(request, cmpg,msisdn,transaction_ref, ses,"entry");
                try{
                pubId=ses.getAttribute("cpapubid")+"";
                        }
                catch(Exception e){pubId="";}
            } 
        }
                request.setAttribute("cpapubid",pubId);
                ses.setAttribute("cpapubid",pubId);
        
         //=======  END CPA and RS =================
        
        // ==== Logging to mobileClubCampaignLog Hits ===============================

        try {
            String networkname = myisp.toLowerCase();
            System.out.println("ITIndex getnetworkname in index.jsp " + networkname);
            networkname = mobilenetwork.getMobileNetwork("IT", networkname);
            
            

            System.out.println("ITIndex JSP LOGGING BEFORE logging " + subpage + " mroute " + aReq.get("mroute"));
            if (!subpage.equals("subscribe")) {
                System.out.println("ZA INDEX JSP LOGGING TO CAMPAIGN LOG ");
                if (!aReq.get("cid").equalsIgnoreCase("") && (ses.getAttribute("doneMsisdnParse")==null)) {
                    campaigndao.log("index", landingPage, msisdn, msisdn, handset, domain, campaignId, clubUnique, "INDEX", 0, request, response, networkname,"",networkname,"",pubId);
                }

            }

        } catch (Exception e) {
            System.out.println("ZA Index Error for campaignlog " + e);
            logger.error("ZA INDEX ERROR FOR CAMPAIGN LOG {}", e.getMessage());
            logger.error("ZA INDEX ERROR FOR CAMPAIGN LOG ", e);
        }

// ==== End Logigng to mobileClubCampaignLog hits ===========================
        
        
        

        try {
            boolean onMainIndexPage = fileName.indexOf("index") > -1;
            
            String sts_id = "";

            if (user == null && (msisdn != null || !msisdn.equalsIgnoreCase(""))) {
                user = umeuserdao.getUser(msisdn);
            }

            if (user != null) {
                uid = user.getUnique();
                //System.out.println("UID: " + uid);
                msisdn = user.getParsedMobile();
                wapid = user.getWapId();
                logger.info("USER AUTHENTICATED: {}:  {} {} ", uid, msisdn);
                //System.out.println("USER AUTHENTICATED: " + uid + ": " + msisdn + crlf);
            } else if (!aReq.get("routerresponse").equals("1") && club != null && club.getRegion().equals("ZA") && msisdn.equals("") && onMainIndexPage
                    && aReq.get("rdsts").equals("") && aReq.get("optin").equals("") && subpage.equals("") && ses.getAttribute("doneMsisdnParse") == null) {
                
                ses.setAttribute("doneMsisdnParse", true);
                if (club.getRegion().equals("ZA")) {
                    // We know little about this user who has just landed on the index page including the MSISDN
                    // Redirect the user off to the MSISDN router to try to look up an identifier
                    //doMsisdnTrace( request, msisdn, "ZA - Sending this session to the MISDN router" );
                    sts_id = Misc.generateLogin(30);
                    ses.setAttribute("sts_id", sts_id);

                    logger.info("STS Session before redirection: {} {}", ses);
                    logger.info("Session isNew: {} ", ses.isNew());
                    logger.info("STS_id attribute after setting: {} ", ses.getAttribute("sts_id"));

                    // Log this event
                    //String _rdurl = "http://" + dmn.getDefaultUrl() + "/index_main.jsp?cid=" + campaignId + "&pg=" + subpage + "&routerresponse=1&mroute=1";
                    String _rdurl = "http://" + dmn.getDefaultUrl() + "/index_main.jsp?cid=" + campaignId + "&pg=" + subpage + "&routerresponse=1&mroute=1&l=" + landingPage+"&pubid="+pubId;
                    String url = "http://www.smartcalltech.co.za/msisdnrouter/default.aspx?ID=" + sts_id + "&url=" +java.net.URLEncoder.encode(_rdurl,"UTF-8");

                    System.out.println("ZA debug BEFORE REDIRECTION ::> " + url);

                    response.sendRedirect(url);

                    // doRedirect(response, url);
                    return;

                } else {
                    //If club is not ZA , we are not sending it for Msisdn Lookup Process
                    //doMsisdnTrace( request, msisdn,  club.getRegion() + " - NOT sending for MSISDN lookup -" );
                }
            }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            logger.error("WAPXZA EXCEPTION {}", e.getMessage());
            logger.error("WAPXZA EXCEPTION ", e);
        }

        System.out.println("User: " + user);
        List<UmeDomain> notSubscribedClubDomains = new ArrayList<UmeDomain>();
        notSubscribedClubDomains = getNotSubscribedClubDomains(club, mobileclubdao, user, umesdc);
        System.out.println("notSubscribedClubDomains index.jsp: " + notSubscribedClubDomains.size());
        request.setAttribute("notSubscribedClubDomains", notSubscribedClubDomains);

        if (user != null && club != null && mobileclubdao.isActive(user, club) && null == request.getParameter("id") && !campaignId.equals("") /* && null != request.getParameter("incoming") */) {
            String userMobile = user.getMobile().trim();
            String enMsi = MiscCr.encrypt(userMobile);

            List<String> notSubscribedClubDomainsUrls = new ArrayList<String>();

            if (notSubscribedClubDomains.size() >= 2 /*&&  handset.isSmartPhone */) {

                UmeDomain sisterDomain1 = notSubscribedClubDomains.get(0);
                redirectCampaignId = campaigndao.getCampaignUnique(sisterDomain1.getUnique(), "Redirect");
                //	popunderCampaignId=campaigndao.getCampaignUnique(domain,"PopUnder");
                request.setAttribute("sisterDomain1", "http://" + sisterDomain1.getDefaultUrl() + "/?id=" + user.getWapId() + "&mid=" + enMsi + "&logtype=redirect&cid=" + redirectCampaignId);
                for (int i = 1; i < notSubscribedClubDomains.size(); i++) {
                    UmeDomain sisterDomain2 = notSubscribedClubDomains.get(i);
                    popunderCampaignId = campaigndao.getCampaignUnique(sisterDomain2.getUnique(), "PopUnder");
                    notSubscribedClubDomainsUrls.add("http://" + sisterDomain2.getDefaultUrl() + "/?id=" + user.getWapId() + "&mid=" + enMsi + "&logtype=redirect&cid=" + popunderCampaignId);

                }
                String notSubscribedClubDomainsUrlsJson = new Gson().toJson(notSubscribedClubDomainsUrls);
                System.out.println("JSON: " + notSubscribedClubDomainsUrlsJson);
                request.setAttribute("notSubscribedClubDomainsUrlsJson", notSubscribedClubDomainsUrlsJson);

                RequestDispatcher rd = request.getRequestDispatcher("SisterDomain.jsp");
                rd.forward(request, response);
                return;

            } else if (notSubscribedClubDomains.size() == 1) {
                UmeDomain sisterDomain1 = notSubscribedClubDomains.get(0);
                redirectCampaignId = campaigndao.getCampaignUnique(sisterDomain1.getUnique(), "Redirect");
                //doRedirect(response, "http://" + sisterDomain1.getDefaultUrl() + "/?id=" + user.getWapId() + "&mid=" + enMsi + "&logtype=redirect&cid=" + redirectCampaignId);
                response.sendRedirect("http://" + sisterDomain1.getDefaultUrl() + "/?id=" + user.getWapId() + "&mid=" + enMsi + "&logtype=redirect&cid=" + redirectCampaignId);
                return;
            }

        }

//System.out.println("LANDING PAGE IN INDEX.jsp "+landingPage+ " attribute "+request.getAttribute("landingPage"));
        if (!subpage.equals("subscribe")) {
            /* Commented on 2016-10-14  as this is not required
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
            */
        }

        try {

            application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp").forward(request, response);
            //application.getRequestDispatcher("/IndexMain").forward(request,response);

        } catch (Exception e) {
            logger.error("WAPXZA EXCEPTION {}", e.getMessage());
            logger.error("WAPXZA EXCEPTION ", e);
        }

    }

    /**
     * This method is needed to support Autowired Spring beans
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
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

    private List<UmeDomain> getNotSubscribedClubDomains(MobileClub club, MobileClubDao mobileclubdao, UmeUser user, UmeTempCache umesdc) {
        List<String> sisterClubList = new ArrayList<String>();
        // String[] sisterClubArray=new String;
        String sisterClubs = club.getSisterClubs();
        System.out.println("Sister Clubs: " + sisterClubs);
        if (sisterClubs.contains("?")) {
            sisterClubList = new ArrayList<String>(Arrays.asList(sisterClubs.split("\\?")));
        } else {
            if (sisterClubs != null && !sisterClubs.equals("")) {
                sisterClubList.add(sisterClubs);
            }

        }

        boolean activeInSisterClub = false;
        List<UmeDomain> notSubscribedClubDomains = new ArrayList<UmeDomain>();
        //List<String> notSubscribedClubDomainsUrls = new ArrayList<String>();

        if (sisterClubList.size() > 0 && sisterClubList != null) {
            for (String sisterClubUnique : sisterClubList) {
                MobileClub sisterClub =UmeTempCmsCache.mobileClubMap.get(sisterClubUnique);// mobileclubdao.getMobileClubMap().get(sisterClubUnique);
                if (user != null) {
                    activeInSisterClub = mobileclubdao.isActive(user, sisterClub);
                    if (!activeInSisterClub) {
                        String clubDomainUnique = sisterClub.getWapDomain();
                        //UmeDomain sisterDomain=umesdc.getDomainMap().get(clubDomainUnique);
                        notSubscribedClubDomains.add(umesdc.getDomainMap().get(clubDomainUnique));
                        //notSubscribedClubDomainsUrls.add(umesdc.getDomainMap().get(clubDomainUnique).getDefaultUrl());
	    		/* doRedirect(response,"http://" + sisterDomain.getDefaultUrl() +"/?id=" + user.getWapId()+"&mid="+enMsi+"&logtype=redirect");
                         return; */
                    }
                } else {
                    String clubDomainUnique = sisterClub.getWapDomain();
                    notSubscribedClubDomains.add(umesdc.getDomainMap().get(clubDomainUnique));
                }

            }
        }
        return notSubscribedClubDomains;
    }

    Map<String, String> getCampaignFromReferer(String refererUrl) {
        String delimiter = "?";
        String delimeter2 = "&";
        refererUrl = refererUrl.replace(delimiter, delimeter2);

        String[] params = refererUrl.split("&");

        Map<String, String> map = new HashMap<String, String>();
        String value = "";
        for (String param : params) {
            if (param.contains("=")) {
                String name = param.split("=")[0];
                if (param.length() > 1) {
                    value = param.split("=")[1];
                }

                map.put(name, value);
            }
        }
        return map;
    }
}
