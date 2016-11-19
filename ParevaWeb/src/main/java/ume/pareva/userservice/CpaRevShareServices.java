package ume.pareva.userservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.dao.CpaAdvertiserDao;
import ume.pareva.dao.CpaVisitLogDao;
import ume.pareva.pojo.CpaAdvertiser;
import ume.pareva.pojo.CpaVisitLog;
import ume.pareva.dao.RevSharePartersDao;
import ume.pareva.dao.RevShareVisitorLogDao;
import ume.pareva.revshare.RevSharePartners;
import ume.pareva.revshare.RevShareVisitorLog;
import ume.pareva.servlet.ZAIndex;

/**
 *
 * @author madan
 */

@Component("cparevshare")
public class CpaRevShareServices {
    
    @Autowired
    RevShareVisitorLogDao revvisitorlogdao;
    
    @Autowired
    RevSharePartersDao revsharepartnersdao;
    
    @Autowired
    CpaAdvertiserDao advertiserdao ;
    
    @Autowired
    CpaVisitLogDao cpavisitlogdao;
    
     private final Logger logger = LogManager.getLogger(CpaRevShareServices.class.getName());
    private final SimpleDateFormat longDateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
     private String visitsubscribed = "1970-01-01 00:00:00";
        //String ipaddress=request.getRemoteAddr();
     private String isSubscribed = "0";
     
     public void cparevshareLog(HttpServletRequest request,MobileClubCampaign cmpg , String msisdn, String transaction_ref, HttpSession session, String action){
         if(cmpg!=null){
         String campaignsrc=cmpg.getSrc().trim();
         String campaignId=cmpg.getUnique();
         
          if (campaignsrc.toLowerCase().endsWith("cpa")) {
             insertToCpaVisitLog(request,campaignsrc,msisdn,campaignId,transaction_ref,session);
          }
          
          else if (campaignsrc.trim().endsWith("RS")) {
              insertToRevShareVisitLog(request, campaignsrc ,msisdn, campaignId,session);
          }
         
         }
     }
   private void insertToCpaVisitLog(HttpServletRequest request,String campaignsrc, String msisdn, String campaignId, String transaction_ref, HttpSession session) {
         
       
        //Calendar nowtime = new GregorianCalendar();
        String ipbitterAddress = getClientIpAddr(request);

        String param1 = "", param2 = "", cpaparam1 = "", cpaparam2 = "", cpaparam3 = "", param3 = "",pubid="",cpapubid="";
        CpaAdvertiser advertiser = null;

       
        if (campaignsrc.toLowerCase().endsWith("cpa")) {
            advertiser = advertiserdao.getAdvertiser(campaignsrc);
            if (advertiser.getParameter1() != null && advertiser.getParameter1().trim().length() > 0) {
                param1 = "aHashcode";
                cpaparam1 = request.getParameter(advertiser.getParameter1());
            }
            if (advertiser.getParameter2() != null && advertiser.getParameter2().trim().length() > 0) {

                param2 = "cpacampaignid";
                cpaparam2 = request.getParameter(advertiser.getParameter2());
            }
            if (advertiser.getParameter3() != null && advertiser.getParameter3().trim().length() > 0) {
                param3 = "clickid";
                cpaparam3 = request.getParameter(advertiser.getParameter3());
            }
              if(advertiser.getPubId()!=null && advertiser.getPubId().trim().length()>0)
                {       
                pubid="pubid";
                    try{
                        cpapubid=request.getParameter(advertiser.getPubId());
                    }catch(Exception e){cpapubid="";}
                }

            if (cpaparam1 != null && cpaparam2 != null && cpaparam3 != null) {
                CpaVisitLog cpaVisitLog = new CpaVisitLog();
                cpaVisitLog.setaHashcode(cpaparam1);
                cpaVisitLog.setCpacampaignid(cpaparam2);
                cpaVisitLog.setClickid(cpaparam3);
                cpaVisitLog.setaParsedMobile(msisdn);
                cpaVisitLog.setaCampaignId(campaignId);
                cpaVisitLog.setaCreated(longDateSdf.format(new Date()));
                cpaVisitLog.setaSubscribed(visitsubscribed);
                cpaVisitLog.setIsSubscribed(isSubscribed);
                cpaVisitLog.setaSrc(campaignsrc);
                cpaVisitLog.setPublisherid(cpapubid);
                cpaVisitLog.setIp(ipbitterAddress);
                cpaVisitLog.setTransaction_id(transaction_ref);

                cpavisitlogdao.insert(cpaVisitLog);

                session.setAttribute("cpaparam1", cpaVisitLog.getaHashcode());
                session.setAttribute("cpaparam2", cpaVisitLog.getCpacampaignid());
                session.setAttribute("cpaparam3", cpaVisitLog.getClickid());
                session.setAttribute("cpapubid",cpaVisitLog.getPublisherid());

                //System.out.println("SESSIONINFORMATION "+ session.getAttribute("cpaparam1")+" "+session.getAttribute("cpaparam2")+" "+session.getAttribute("cpaparam3"));
            }

        }
    }
    
    
        private void insertToRevShareVisitLog(HttpServletRequest request, String campaignsrc ,String msisdn, String campaignId, HttpSession session) {

        //SimpleDateFormat revsharesdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Calendar nowtime = new GregorianCalendar();
        String ipbitterAddress = getClientIpAddr(request);

        String param1 = "", param2 = "", cpaparam1 = "", cpaparam2 = "", cpaparam3 = "", param3 = "", cpaquery = "";
        RevSharePartners advertiser = null;
        
        if (campaignsrc.trim().endsWith("RS")) {
            advertiser = revsharepartnersdao.getAdvertiser(campaignsrc);

            System.out.println(advertiser);
            if (advertiser.getParameter1() != null && advertiser.getParameter1().trim().length() > 0) {
                param1 = "tracker_id";
                cpaparam1 = request.getParameter(advertiser.getParameter1());
            }
            if (advertiser.getParameter2() != null && advertiser.getParameter2().trim().length() > 0) {

                param2 = "cpacampaignid";
                cpaparam2 = request.getParameter(advertiser.getParameter2());
            }
            if (advertiser.getParameter3() != null && advertiser.getParameter3().trim().length() > 0) {
                param3 = "clickid";
                cpaparam3 = request.getParameter(advertiser.getParameter3());
            }

            System.out.println("REVSHARE LOG JSP " + cpaparam1);
            if (cpaparam1 != null && cpaparam2 != null && cpaparam3 != null) {
                RevShareVisitorLog revShareVisitLog = new RevShareVisitorLog();
                revShareVisitLog.setParameter1(cpaparam1);
                revShareVisitLog.setParameter2(cpaparam2);
                revShareVisitLog.setParameter3(cpaparam3);
                revShareVisitLog.setaParsedMobile(msisdn);
                revShareVisitLog.setaCampaignId(campaignId);
                revShareVisitLog.setaCreated(longDateSdf.format(new Date()));
                revShareVisitLog.setaSubscribed(visitsubscribed);
                revShareVisitLog.setIsSubscribed(isSubscribed);
                revShareVisitLog.setaSrc(campaignsrc);
                revShareVisitLog.setIp(ipbitterAddress);
                revShareVisitLog.setTransaction_id("zarevshare");
                revShareVisitLog.setPublisherid("");
                revvisitorlogdao.insert(revShareVisitLog);
                session.setAttribute("revparam1", revShareVisitLog.getParameter1());
                session.setAttribute("revparam2", revShareVisitLog.getParameter2());
                session.setAttribute("revparam3", revShareVisitLog.getParameter3());
            }
        }

    }
        
        
        
    
        
       public String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null) {
            int idx = ip.indexOf(',');
            if (idx > -1) {
                ip = ip.substring(0, idx);
            }
        }

        return ip;
    }
    
}
