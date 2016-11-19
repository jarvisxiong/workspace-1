
package ume.pareva.au;

import com.mitchellbosecke.pebble.PebbleEngine;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
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
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.UmeLanguagePropertyDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.CampaignHitCounter;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.userservice.CpaRevShareServices;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;

/**
 *
 * @author Madan
 */
@WebServlet(name = "AUIndex", urlPatterns = {"/AUIndex"})
public class AUIndex extends HttpServlet {
    
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
        
        @Autowired
	TemplateEngine templateEngine;//

    private final Logger logger = LogManager.getLogger(AUIndex.class.getName());
    private final SimpleDateFormat longDateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    
    
    
    
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
        PrintWriter writer=response.getWriter();
        Map<String,Object> context=new HashMap<String,Object>();
        
        ThreadContext.put("ROUTINGKEY", "AU");
        logger.info("AUINDEX "+"ProcessREQEST is called upon ");

        HttpSession ses = request.getSession();
        logger.info("AUINDEX "+"session id is  "+ses.getId());
        ServletContext application = request.getServletContext(); //.setAttribute(null, request);
        logger.info("AUINDEX "+"servletContext is  "+application.getContextPath());
        SdcRequest aReq = new SdcRequest(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();
        String ddir = dmn.getDefPublicDir();
        String lang = aReq.getLanguage().getLanguageCode();
        logger.info("AUindex "+" after SDC request ");
        
        PebbleEngine engine=templateEngine.getTemplateEngine(dmn.getUnique());  
        
        ses.setAttribute("mobileclubdao", mobileclubdao);
        request.setAttribute("mobileclubdao", mobileclubdao);
        
        request.setAttribute("engine",engine);
        ses.setAttribute("engine",engine);
        
        
        String fileName ="index.jsp";// request.getServletPath();
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        System.out.println("AUindex "+" FileName is  "+fileName);

        String cloudfronturl = dmn.getContentUrl();
        ses.setAttribute("cloudfrontUrl", cloudfronturl);
        application.setAttribute("cloudfrontUrl", cloudfronturl);
        
        logger.info("AUindex "+"contentUrl is  "+cloudfronturl);

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
        
        String uid = "",wapid = "";
        String msisdn = aReq.getMsisdn();
        if("".equalsIgnoreCase(msisdn)){            
            msisdn=aReq.get("msisdn");
            if(!"".equalsIgnoreCase(msisdn)){            
                if(msisdn.startsWith("0")){
                    msisdn="61"+msisdn.substring(1);
                }
                if(msisdn.startsWith("00")){
                    msisdn=msisdn.substring(2);
                }        
                if(!msisdn.startsWith("0") && !msisdn.startsWith("61"))msisdn="61"+msisdn;
        }
    }
        String enMsisdn = aReq.get("mid"); //reading encrypted msisdn which is sent as mid param. (broadcast, followup)
        String redirectCampaignId = "",campaignsrc="";
        String popunderCampaignId = "",landingPage="",pubId="";
        MobileClubCampaign cmpg = null;
        
         String myip = request.getHeader("X-Forwarded-For");
        if (myip != null) {
            int idx = myip.indexOf(',');
            if (idx > -1) {
                myip = myip.substring(0, idx);
            }
        }
        
        String myisp = ipprovider.findIsp(myip);
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
            System.out.println("aumid the mid parameter is "+enMsisdn);
            context.put("mid", enMsisdn);
            String deMsisdn = MiscCr.decrypt(enMsisdn);
            if (!deMsisdn.equals("")) {
                if (deMsisdn.startsWith("0")) {
                    deMsisdn = "61" + deMsisdn.substring(1);
                    System.out.println("aumid the deMsisdn value of mid "+enMsisdn+"  is "+deMsisdn);
                }
                msisdn = deMsisdn;
                System.out.println("aumid  finally the value of msisdn is "+msisdn+"  of mid "+enMsisdn);
                
            }
        }
         //========= END  Reading Encrypted MSISDN ====================== 
        String campaignId = aReq.get("cid");
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
        UmeClubDetails userclubdetails=null;
        String clubUnique = "";
        if (club != null) {
            clubUnique = club.getUnique();
            userclubdetails=UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());            
            
        }

        if (campaignId != null && campaignId.trim().length() > 0) {

            if (!campaignId.equals("")) {
                cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
            }
            if (cmpg != null) {
                campaignsrc = cmpg.getSrc();
                pubId=ses.getAttribute("cpapubid")+"";
            }

        }
        
        //========= Reading Campaign and Initializing Club and Campaign ======================
        
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
                ses.setAttribute("cid",campaignId);
                request.setAttribute("cpapubid",pubId);
                ses.setAttribute("cpapubid",pubId);
        
        
        try{
             String networkname = myisp.toLowerCase();
            System.out.println("zaindex getnetworkname in index.jsp " + networkname);
            networkname = mobilenetwork.getMobileNetwork("AU", networkname);
            
            if (!aReq.get("cid").equalsIgnoreCase("") && ses.getAttribute("bilogged")==null) {
                campaigndao.log("auindex", landingPage, msisdn, msisdn, handset, domain, campaignId, clubUnique, "INDEX", 0, request, response, networkname,"","","",pubId);
                
                 if(msisdn!=null && !msisdn.equalsIgnoreCase("")) {
                   campaigndao.log("auindex", landingPage, msisdn, msisdn, handset, domain, campaignId, clubUnique, "IDENTIFIED", 0, request, response, networkname,"","","",pubId);
                 }
                    
                 ses.setAttribute("bilogged","true");
                
                
                
                
                 Cookie cookie = new Cookie("cid",campaignId);
                cookie.setMaxAge(168*60*60); 
                response.addCookie(cookie);
        
                Cookie cookie1=new Cookie("landingpage",landingPage);
                cookie1.setMaxAge(168*60*60);
                response.addCookie(cookie1);
            }
            
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////             
                         if(cmpg!=null&&!cmpg.getaInterstitialLandingPage().equals("")&& cmpg.getaInterstitialLandingPage()!=null && request.getParameter("loghits")==null){  
                          
                          String cid=URLEncoder.encode((campaignId!=null)?campaignId:"","UTF-8");
                          String clubId=URLEncoder.encode(club.getUnique(),"UTF-8");
                          String testlanding=request.getParameter("interlanding");
                           String interstitialLandingPage=testlanding;
                          
                          if(interstitialLandingPage==null || "".equals(interstitialLandingPage)) interstitialLandingPage=cmpg.getaInterstitialLandingPage();
                          
                          String interstitialRedirected=request.getParameter("interstitialredirected"); //(String)session.getAttribute("interstitialRedirected"); //
                          String redirectBack="http://"+dmn.getDefaultUrl();
                          if(interstitialRedirected==null){
                           //session.setAttribute("interstitialRedirected",null);
                           String interstitialDomainUnique=dmn.getPartnerDomain();
                           UmeDomain interstitialDomain=umesdc.getDomainMap().get(interstitialDomainUnique);
                           //redirectBack="http://"+dmn.getDefaultUrl()+"/?cid="+cid+"&clubid="+clubId+"&interstitiallandingpage="+interstitialLandingPage+"&interstitialredirected=1&loghits=no";
                           System.out.println("interstitial ZA redirectback url = "+redirectBack);
                           String interstitialRedirectUrl="http://"+interstitialDomain.getDefaultUrl();
                           
                           
                           
                          
                           String params="redirectback="+redirectBack+"&cid="+campaignId+"&clubid="+club.getUnique()+"&interstitiallandingpage="+interstitialLandingPage+"&interstitialredirected=1";
                           interstitialRedirectUrl+="/?C3RC183="+Misc.encrypt(params);
                           System.out.println("interstitialAU index params "+params);
                           //System.out.println("interstitial ZAIndex  params "+params);
                           ses.setAttribute("interparam",params);
                           request.setAttribute("interparam",params);
                           response.sendRedirect(interstitialRedirectUrl);
                           return;
                          }
                         }
                         
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////         
                         
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
                         
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
            // parameter cc is used to redirect user to oxygen8 directly if coming from interstitial page. 
            if(request.getParameter("cc")!=null && request.getParameter("cc").equals("yes"))
            {
                 request.getServletContext().getRequestDispatcher("/"+dmn.getDefPublicDir()+"/processau.jsp?cid="+campaignId+"&landingpage="+landingPage).forward(request, response);
                 return;
            }
            
            if(club!=null){
                if(userclubdetails.getServiceType().equalsIgnoreCase("competition")) {
                       if(user!=null){
                        String clubId=aReq.get("clublink");
                        if(clubId.equalsIgnoreCase("")) clubId=club.getUnique();
                        SdcMobileClubUser clubUser=umemobileclubuserdao.getClubUserByMsisdn(user.getParsedMobile(),clubId);
                      
                       try {
                           String voucherwindomain="9524711450641llun";
                        //application.getRequestDispatcher("/" + System.getProperty("dir_" + voucherwindomain + "_pub") + "/question.jsp?msisdn="+user.getParsedMobile()+"&clubid="+club.getUnique()).forward(request, response);
                        
                        if(clubUser!=null && clubUser.getActive()==1){
                        //response.sendRedirect("http://www.voucherwin.co.uk/question.jsp?msisdn="+user.getParsedMobile()+"&clubid="+clubId);
                        application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/weeklyquiz.jsp?clubid="+clubId+"&msisdn="+user.getParsedMobile()+"&mid="+Misc.encrypt(user.getParsedMobile())).forward(request, response);
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
                } // END competition clause
                if(userclubdetails.getServedBy().equalsIgnoreCase("thirdparty")) {
                    if(user!=null){
                        application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/thirdparty.jsp").forward(request, response);
                        
                    }
                    
                }
                
                
            } // END if club!=null 
            
            
            if(!"".equalsIgnoreCase(msisdn)) {
                context.put("msisdnexist","true");
                context.put("mid", Misc.encrypt(msisdn));
                ses.setAttribute("msisdnidentified",true);
            }            
            else{
                context.put("msisdnexist","false");
                context.put("mid","");
            }
            
            context.put("landingpage",landingPage);
            context.put("campaignid",campaignId);
            context.put("sendAction","processau.jsp");
            context.put("msisdn",msisdn);
            context.put("endofmonth",getEndOfmonth());
            context.put("contenturl","http://"+dmn.getContentUrl());
 
            
            engine.getTemplate(landingPage).evaluate(writer, context);
            
        }
        catch(Exception e){}
        
  
    }
    
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
