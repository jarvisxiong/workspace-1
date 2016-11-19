/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.it.servlets;

import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiBindingStub;
import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiPort;
import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiServiceLocator;
import com.ipx.www.api.services.subscriptionapi40.types.TerminateSubscriptionRequest;
import com.ipx.www.api.services.subscriptionapi40.types.TerminateSubscriptionResponse;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.cms.VideoClipDao;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.DoiResult;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.UmeLanguagePropertyDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.it.DigitAPI;
import ume.pareva.it.IpxBillingDirect;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.snp.CacheManager;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;
import ume.pareva.userservice.StopUser;
import ume.pareva.userservice.UserAuthentication;
import ume.pareva.userservice.VideoList;
import ume.pareva.util.ZACPA;

/**
 * Servlet implementation class GlobalWapHeader
 */
//@WebServlet("/GlobalWapHeader")
public class TerminateBulkIT extends HttpServlet {

    @Autowired
    UmeTempCache umesdc;

    @Autowired
    HandsetDao handsetdao;

    @Autowired
    UmeLanguagePropertyDao langpropdao;

    @Autowired
    MobileClubDao mobileclubdao;

    @Autowired
    VideoClipDao videoclipdao;

    @Autowired
    UmeUserDao umeuserdao;

    @Autowired
    CpaLoggerDao cpaloggerdao;
    
    @Autowired
    MobileClubCampaignDao campaigndao;

    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;

    @Autowired
    VideoList videolist;

    @Autowired
    TemplateEngine templateEngine;//

    @Autowired
    InternetServiceProvider internetserviceprovider;

    @Autowired
    LandingPage landingpage;

    @Autowired
    Misc misc;

    @Autowired
    UmeClubDetailsDao clubdetailsdao;

    @Autowired
    MobileClubBillingPlanDao billingplandao;

    @Autowired
    ZACPA zacpa;

    @Autowired
    DoiResult doiresult;

    @Autowired
    IpxBillingDirect directBilling;

    @Autowired
    CacheManager cachemanager;

    @Autowired
    UserAuthentication userauthentication;

    @Autowired
    CampaignHitCounterDao campaignhitcounterdao;

    @Autowired
    UmeSmsDao umesmsdao;

    @Autowired
    DigitAPI firstsms;
    
    @Autowired
    StopUser stopUserDao;
    
    private static final long serialVersionUID = 1L;

    /**
     * This method is needed to support Autowired Spring beans
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        processTerminateBulk(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        processTerminateBulk(request, response);
    }

    protected void processTerminateBulk(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("IT processTerminateBulk IS CALLED UPON.... 1 ");

        HttpSession session = request.getSession();
        RequestDispatcher rd = null;

        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        System.out.println("IPX IT FILTER  GLOBAL WAP HEADER IS CALLED UPON.... 2 ");

        String domain = dmn.getUnique();

        String pageEnc = aReq.getEncoding();
        request.setAttribute("pageEnc", pageEnc);
        response.setContentType("text/html; charset=" + pageEnc);

        String nbrs = aReq.get("msisdn");
        String[] list = Misc.stringSplit(nbrs, "\r\n");
        String submit = aReq.get("submit");

        //System.out.println("nbrs: " + nbrs);
        //System.out.println("list " + list.toString());
        int itCount = 0;
        int bCount = 0;
        if (!submit.equals("")) {
            for (int i = 0; i < list.length; i++) {
                String msisdn = list[i];
                if (msisdn.startsWith("39") || msisdn.startsWith("34")) {
                    List<SdcMobileClubUser> mobileClubUserList = umemobileclubuserdao.getClubUsersByMsisdn(msisdn);
                    if (mobileClubUserList != null) {
                        System.out.println(msisdn + " -- IPX mobileClubUserList.size(): " + mobileClubUserList.size());
                    } else {
                        System.out.println(msisdn + " -- IPX mobileClubUserList null ");
                    }

                    if (mobileClubUserList != null && mobileClubUserList.size() > 0) {
                        System.out.println(msisdn + " -- IPX inside if ");
                        for (int j = 0; j < mobileClubUserList.size(); j++) {
                            System.out.println(msisdn + " -- IPX inside for ");
                            SdcMobileClubUser clubUser = mobileClubUserList.get(j);
                            MobileClub club = UmeTempCmsCache.mobileClubMap.get(clubUser.getClubUnique());
                            if (club == null) {
                                System.out.println("IPX club is null for this msisdn: " + clubUser.getParsedMobile());
                            }

                            if (club != null && clubUser != null && clubUser.getActive() == 1) {
                                System.out.println("IPX club: " + club.getName() + "--" + club.toString());                                
                                stopUserDao.stopSingleSubscriptionNormal(clubUser.getParsedMobile(), clubUser.getClubUnique(), request, response);
                                itCount++;
                                bCount++;
                                
//                                if (clubUser != null) {
//                                    System.out.println("IPX clubUser: " + clubUser.getParsedMobile() + "--" + club.toString());
//                                }
////                                boolean isStopSpecial = isStopSpecial();                               
////                                String msg = "";    
////                                String fromNumber = club.getClubName();
//
//                                try {
//                                    URL anIpxSubUrl = new URL("http://europe.ipx.com/api/services2/SubscriptionApi40?wsdl");
//                                    SubscriptionApiPort aPort = new SubscriptionApiServiceLocator().getSubscriptionApi40(anIpxSubUrl);
//                                    // Set read timeout to 10 minute            
//                                    ((SubscriptionApiBindingStub) aPort).setTimeout(10 * 60 * 1000);
//                                    TerminateSubscriptionRequest aTerminateRequest = new TerminateSubscriptionRequest();
//                                    aTerminateRequest.setCorrelationId("universalmob");
//                                    aTerminateRequest.setConsumerId(msisdn);
//                                    aTerminateRequest.setSubscriptionId(clubUser.getParam2());
//                                    aTerminateRequest.setUsername(club.getOtpSoneraId());
//                                    aTerminateRequest.setPassword(club.getOtpTelefiId());
//
//                                    TerminateSubscriptionResponse aTerminateResponse = aPort.terminateSubscription(aTerminateRequest);
//                                    // Debug
//                                    System.out.println("IPX Terminate result: responseCode: " + aTerminateResponse.getResponseCode());
//                                    System.out.println("IPX Terminate result: responseMessage: " + aTerminateResponse.getResponseMessage());
//
//                                    mobileclubdao.unsubscribe(club, null, msisdn);
//                                    cachemanager.delete(clubUser.getUserUnique());
//                                    userauthentication.invalidateUser(request);
//                                    
//                                    clubUser.setUnsubscribed(new Date());
//                                    String zastoplog="STOP";
//                                    if(DateUtils.isSameDay(clubUser.getSubscribed(),clubUser.getUnsubscribed())) zastoplog="STOPFD";
//
//                                    campaigndao.log("IPXReq", "", clubUser.getUserUnique(), clubUser.getParsedMobile(), null, domain, clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, request, response, clubUser.getNetworkCode());
//                                    itCount++;
//                                    bCount++;
//                                    if (clubUser.getCampaign() != null && clubUser.getCampaign().trim().length() > 0) {
//                                        MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(clubUser.getCampaign());
//                                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                        if (cmpg != null && cmpg.getSrc().endsWith("RS")) {
//                                            //String query = "insert into rev--ShareLogging "
//                                            //        + "(aUnique,aAmount,aCurrencyCode,aParsedMobile,aEncryptedMSISDN,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc,aEvent) values" 
//                                            // + "('" + Misc.generateUniqueId() + "','0','" + cmpg.getPayoutCurrency() + "','" + clubUser.getParsedMobile() + "','" + MiscCr.encrypt(clubUser.getParsedMobile()) + "','" + clubUser.getCampaign() + "','" + club.getUnique() + "','" + sdf2.format(new Date()) + "','" + sdf2.format(new Date()) + "','0','" + clubUser.getNetworkCode() + "','" + cmpg.getSrc() + "','2')";
//                                            //int updateRow = zacpa.executeUpdateCPA(query);
//                                            String enMsisdn =MiscCr.encrypt(clubUser.getParsedMobile());
//                                            int insertedRows =  cpaloggerdao.insertIntoRevShareLogging(0,  cmpg.getPayoutCurrency(), clubUser.getParsedMobile(), enMsisdn, clubUser.getCampaign(), club.getUnique(),0, clubUser.getNetworkCode(), cmpg.getSrc(), 2);
//                                        }
//                                    }
//                                } catch (Exception e) {
//                                    System.out.println("IPX bulk stop exception: " + e);
//                                    e.printStackTrace();
//                                    System.out.println("IPX bulk stop exception: " + e);
//                                }

//                                if(isStopSpecial){     
//                                    fromNumber = "Conferma";
//                                    msg = "La tua richiesta e' stata approvata. Grazie";        
////                                    DigitAPI firstsms=new DigitAPI();
//                                    firstsms.setMsg(msg);
//                                    firstsms.setMsisdn(msisdn);        
//                                    firstsms.setFrom(fromNumber); //11 alphanumeric characters OR upto 16 digit phone number
//                                    firstsms.setReport("True");      
//                                    firstsms.setNetwork("UNKNOWN");
////                                    firstsms.setUmesmsdao(umesmsdao);
//                                    try{
//                                        firstsms.sendSMS();  
//                                    }
//                                    catch(Exception e){e.printStackTrace();} 
//                                }else{
//                                    //TODO DO TERMINATION; BILLINGPLAN TERMINATE; USER TERMINATE; CACHE TERMINATE
//                                    try {
//                                        URL anIpxSubUrl = new URL("http://europe.ipx.com/api/services2/SubscriptionApi31?wsdl");
//                                        SubscriptionApiPort aPort = new SubscriptionApiServiceLocator().getSubscriptionApi31(anIpxSubUrl);
//                                        // Set read timeout to 10 minute            
//                                        ((SubscriptionApiBindingStub) aPort).setTimeout(10 * 60 * 1000);
//                                        TerminateSubscriptionRequest aTerminateRequest = new TerminateSubscriptionRequest();
//                                        aTerminateRequest.setCorrelationId("universalmob");
//                                        aTerminateRequest.setConsumerId(msisdn);
//                                        aTerminateRequest.setSubscriptionId(clubUser.getParam2());
//                                        aTerminateRequest.setUsername(club.getOtpSoneraId());
//                                        aTerminateRequest.setPassword(club.getOtpTelefiId());
//
//                                        TerminateSubscriptionResponse aTerminateResponse = aPort.terminateSubscription(aTerminateRequest);
//                                        // Debug
//                                        System.out.println("IPX Terminate result: responseCode: " + aTerminateResponse.getResponseCode());
//                                        System.out.println("IPX Terminate result: responseMessage: " + aTerminateResponse.getResponseMessage());
//
//                                        mobileclubdao.unsubscribe(club, null, msisdn);
//                                        cachemanager.delete(clubUser.getUserUnique());
//                                        userauthentication.invalidateUser(request);
//                                        campaigndao.log("IPXReq", "", clubUser.getUserUnique(), clubUser.getParsedMobile(), null, domain, clubUser.getCampaign(), clubUser.getClubUnique(), "STOP", 0, request, response, clubUser.getNetworkCode());
//                                        itCount++;
//                                        bCount++;
//                                        if (clubUser.getCampaign() != null && clubUser.getCampaign().trim().length() > 0) {
//                                            MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(clubUser.getCampaign());
//                                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                            if (cmpg != null && cmpg.getSrc().endsWith("RS")) {
//                                                String query = "insert into rev--ShareLogging "
//                                                        + "(aUnique,aAmount,aCurrencyCode,aParsedMobile,aEncryptedMSISDN,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc,aEvent) values" + "('" + Misc.generateUniqueId() + "','0','" + cmpg.getPayoutCurrency() + "','" + clubUser.getParsedMobile() + "','" + MiscCr.encrypt(clubUser.getParsedMobile()) + "','" + clubUser.getCampaign() + "','" + club.getUnique() + "','" + sdf2.format(new Date()) + "','" + sdf2.format(new Date()) + "','0','" + clubUser.getNetworkCode() + "','" + cmpg.getSrc() + "','2')";
//                                                int updateRow = zacpa.executeUpdateCPA(query);
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                        System.out.println("IPX bulk stop exception: " + e);
//                                        e.printStackTrace();
//                                        System.out.println("IPX bulk stop exception: " + e);
//                                    }
//                                }
                            }
                        }
                    }
                }
            }
            System.out.println("IPX IT FILTER  GLOBAL WAP HEADER IS CALLED UPON.... 3: " + itCount + "--" + bCount);
            request.setAttribute("itCount", itCount);
            request.setAttribute("bCount", bCount);
        }
    }

    private boolean isStopSpecial() {
        ServletContext application = getServletConfig().getServletContext();
        int countingStop = 0;
        int numberOfSpecial = 3;
        String ipxCountingStopName = "ipxCountingStopBulk";
        Integer countingStopped = (Integer) application.getAttribute(ipxCountingStopName);

        if (countingStopped == null) {
            countingStopped = 0;
        } else {
            countingStopped++;
        }
        application.setAttribute(ipxCountingStopName, countingStopped);
        countingStop = countingStopped;
        if (countingStop >= numberOfSpecial && (countingStop % numberOfSpecial) == 0) {
            return true;
        } else {
            return false;
        }
    }
}
