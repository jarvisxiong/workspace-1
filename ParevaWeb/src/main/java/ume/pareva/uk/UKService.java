/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.uk;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaAdvertiserDao;
import ume.pareva.dao.CpaVisitLogDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.pojo.CpaAdvertiser;
import ume.pareva.pojo.CpaVisitLog;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.dao.RevSharePartersDao;
import ume.pareva.dao.RevShareVisitorLogDao;
import ume.pareva.revshare.RevSharePartners;
import ume.pareva.revshare.RevShareVisitorLog;
import ume.pareva.sdk.Misc;
import ume.pareva.util.DateUtil;

/**
 *
 * @author madan
 * 
 * This class is created particularly for X-Stream UK Service where 
 * Users Cookies are maintained when they are redirected to Success URL. 
 * X-Stream UK Service is a semi-subscription module which implements IMI's iFrame and when user receives
 * authentic code and his bill is successful, he is redirected to success URL to download/view Contents. 
 * This class is necessary to create Cookies for him so that when he returns and is valid we show him the 
 * Content page directly. 
 */
public class UKService extends HttpServlet {
    
    
    @Autowired
    UKSuccessDao uksuccessdao;
    
    @Autowired
    CpaVisitLogDao cpavisitlogdao;
    
    @Autowired
    CpaAdvertiserDao advertiserdao;
    
    @Autowired
    RevShareVisitorLogDao revvisitorlogdao;
    
    @Autowired
    RevSharePartersDao revsharepartnersdao;
    
    @Autowired
    Misc misc;
    
  
    
    
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    
     public void init(ServletConfig config) throws ServletException
        {
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
        
        HttpSession session = request.getSession();
        String sessionId=session.getId();
        
        String param1="",param2="",cpaparam1="",cpaparam2="",cpaparam3="",param3="",cpaquery="",publisherid="";
        String userIp=getClientIpAddr(request);
        
        //System.out.println("xstreamtesting: Servlet callupon "+session.getId());
        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeDomain dmn = aReq.getDomain();
        
        String domain = dmn.getUnique();
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);   
        
        //Campaign identification
        String cid=aReq.get("cid");
        MobileClubCampaign cmpg = null;
        if (!cid.equals("")) cmpg = UmeTempCmsCache.campaignMap.get(cid);
        
        String msisdn=aReq.get("msisdn");
        
        
        boolean msisdnPresent=false;
        boolean servicePresent=false;
        CpaAdvertiser advertiser=null;
        RevSharePartners revadvertiser=null;
        
        Calendar currentTime=Calendar.getInstance();
        Calendar ukdatetime= Calendar.getInstance();
        String creationdate=sdf2.format(ukdatetime.getTime());
        
        ukdatetime.add(Calendar.HOUR,24);
        String expiry=sdf2.format(ukdatetime.getTime()); 
        
        String transaction_ref="x"+"-"+cid;
        //Keeping the value -1 as a default to check if user is valid within 24 hours of period. 
        int hourDifference=-1; 
         String expired="";
         Date expiryDate=null;
        
        String cookieValue="";
        boolean isLoadContent=false;
        String page="/landingpage.jsp";
        //Cookie cookie=null;
        Cookie[] cookies = null;
        //System.out.println("xstreamtesting: UKService before Cookie loop "+session.getId());
        cookies = request.getCookies();
         if( cookies != null ){
      
         for (Cookie cookie:cookies){
            //cookie = cookies[];
            //System.out.println("xstreamtesting: UKService: "+cookie.getName()+": "+cookie.getValue());
            msisdnPresent=cookie.getName().equals("msisdn") && (cookie.getValue()!=null && !cookie.getValue().isEmpty());
            servicePresent=cookie.getName().equals("umeservicename") && (cookie.getValue()!=null && !cookie.getValue().isEmpty());  
            
            if(cookie.getName().equals("msisdn") && (cookie.getValue()!=null && !cookie.getValue().isEmpty()))
            {
                if(msisdn==null || msisdn.trim().length()<=0 || msisdn.equalsIgnoreCase("0"))
                    msisdn=cookie.getValue();
            }
          
            if(msisdnPresent && servicePresent) {
                //TODO
                // This means User is Valid so we are going to redirect this user to main content page
                
                //System.out.println("xstreamtesting:Redirecting to content.jsp now from UKService ");
               
                if(cookie.getName().equals("expiryDate")) expired=cookie.getValue();
                if(expired!=null && !expired.isEmpty()){
                    
                    try{
                    expiryDate=sdf2.parse(expired);
                    }catch(Exception pE){System.out.println("xstreamtesting: ParseException "+pE);expiryDate=null;}
                
                
                hourDifference=DateUtil.hoursDiff(currentTime.getTime(),expiryDate);
                }
                if(hourDifference!=-1 && hourDifference<=168)
                isLoadContent=true;
                break;              
                
            }         
           
         }        
         
        }
         
         else if (cookies==null){          
             
             
         }
         
         
         /*
         Since cookies do not contain these we are goint to store
         CPA and RevShare stuffs here
         */
         if(!msisdnPresent || !servicePresent){
             
             if(cmpg!=null && cmpg.getSrc().toLowerCase().endsWith("cpa")) {
                 
                 //System.out.println("xstreamtesting : UKSERVICE  CPA visit log "+cmpg.getSrc());
                 advertiser=advertiserdao.getAdvertiser(cmpg.getSrc().trim());
                 if(advertiser.getParameter1()!=null && advertiser.getParameter1().trim().length()>0)
                    {        
                    param1="aHashcode";
                    cpaparam1=request.getParameter(advertiser.getParameter1());
                    }
                if(advertiser.getParameter2()!=null && advertiser.getParameter2().trim().length()>0)
                    {
        
                    param2="cpacampaignid";
                    cpaparam2=request.getParameter(advertiser.getParameter2());
                    }
                if(advertiser.getParameter3()!=null && advertiser.getParameter3().trim().length()>0)
                    {       
                    param3="clickid";
                    cpaparam3=request.getParameter(advertiser.getParameter3());
                    }
                
                 if(cpaparam1!=null && cpaparam2!=null && cpaparam3!=null) {
                CpaVisitLog cpaVisitLog=new CpaVisitLog();
                cpaVisitLog.setaHashcode(cpaparam1);
                cpaVisitLog.setCpacampaignid(cpaparam2);
                cpaVisitLog.setClickid(cpaparam3);
                cpaVisitLog.setaParsedMobile(msisdn);
                cpaVisitLog.setaCampaignId(cid);
                cpaVisitLog.setaCreated(sdf2.format(currentTime.getTime()));
                cpaVisitLog.setaSubscribed("1970-01-01");
                cpaVisitLog.setIsSubscribed("0");
                cpaVisitLog.setaSrc(cmpg.getSrc());
                cpaVisitLog.setIp(userIp);
                cpaVisitLog.setTransaction_id(transaction_ref);
    
                    if(request.getParameter("affiliate_id")!=null)
                    cpaVisitLog.setPublisherid(request.getParameter("affiliate_id")+"");
                    else
                    cpaVisitLog.setPublisherid("");
                    //System.out.println("xstreamtesting: CPA visit log "+cpaVisitLog.toString());
                    cpavisitlogdao.insert(cpaVisitLog);   
    
                    session.setAttribute("cpaparam1",cpaVisitLog.getaHashcode());
                    session.setAttribute("cpaparam2",cpaVisitLog.getCpacampaignid());
                    session.setAttribute("cpaparam3",cpaVisitLog.getClickid());
                    
                    request.setAttribute("cpaparam1",cpaVisitLog.getaHashcode());
                    request.setAttribute("cpaparam2",cpaVisitLog.getCpacampaignid());
                    request.setAttribute("cpaparam3",cpaVisitLog.getClickid());
                    
                     Cookie cookie1 = new Cookie("cpaparam1",cpaVisitLog.getaHashcode());
                     cookie1.setMaxAge(24*60*60); //24 hours
                     response.addCookie(cookie1); 
                     
                     Cookie cookie2 = new Cookie("cpaparam2",cpaVisitLog.getCpacampaignid());
                     cookie2.setMaxAge(24*60*60); //24 hours
                     response.addCookie(cookie2);
                     
                     Cookie cookie3 = new Cookie("cpaparam3",cpaVisitLog.getClickid());
                     cookie3.setMaxAge(24*60*60); //24 hours
                     response.addCookie(cookie3);
                    
                  
    
             }
             
             
             
             }
             if(cmpg!=null && cmpg.getSrc().endsWith("RS")) {
                 //System.out.println("xstreamtesting: REV Share Logging "+cmpg.getSrc());
                 revadvertiser=revsharepartnersdao.getAdvertiser(cmpg.getSrc());
                 //System.out.println("xstreamtesting: UKService revShare details "+revadvertiser.toString());
                 if(revadvertiser.getParameter1()!=null && revadvertiser.getParameter1().trim().length()>0)
                {        
                param1="tracker_id";
                cpaparam1=request.getParameter(revadvertiser.getParameter1());
                }
            if(revadvertiser.getParameter2()!=null && revadvertiser.getParameter2().trim().length()>0)
                {        
                param2="cpacampaignid";
                cpaparam2=request.getParameter(revadvertiser.getParameter2());
                }
            if(revadvertiser.getParameter3()!=null && revadvertiser.getParameter3().trim().length()>0)
                {       
                param3="clickid";
                cpaparam3=request.getParameter(revadvertiser.getParameter3());
                }
            
            //System.out.println("xstreamtesting: UK SErviceREVSHARE LOG JSP "+cpaparam1+ cpaparam2+ cpaparam3);
            if(cpaparam1!=null && cpaparam2!=null && cpaparam3!=null) {
            RevShareVisitorLog revVisitLog=new RevShareVisitorLog();
            revVisitLog.setParameter1(cpaparam1);
            revVisitLog.setParameter2(cpaparam2);
            revVisitLog.setParameter3(cpaparam3);
            revVisitLog.setaParsedMobile(msisdn);
            revVisitLog.setaCampaignId(cid);
            revVisitLog.setaCreated(sdf2.format(currentTime.getTime()));
            revVisitLog.setaSubscribed("1970-01-01");
            revVisitLog.setIsSubscribed("0");
            revVisitLog.setaSrc(cmpg.getSrc());
            revVisitLog.setIp(userIp);
            revVisitLog.setTransaction_id("ukrevshare");
            revVisitLog.setPublisherid("");
            revvisitorlogdao.insert(revVisitLog);
            
            session.setAttribute("revparam1",revVisitLog.getParameter1());
            session.setAttribute("revparam2",revVisitLog.getParameter2());
            session.setAttribute("revparam3",revVisitLog.getParameter3());
            
            request.setAttribute("revparam1",revVisitLog.getParameter1());
            request.setAttribute("revparam2",revVisitLog.getParameter2());
            request.setAttribute("revparam3",revVisitLog.getParameter3());
            
            Cookie cookie1 = new Cookie("revparam1",revVisitLog.getParameter1());
            cookie1.setMaxAge(168*60*60); //24 hours
            response.addCookie(cookie1); 
                     
            Cookie cookie2 = new Cookie("revparam2",revVisitLog.getParameter2());
            cookie2.setMaxAge(168*60*60); //24 hours
            response.addCookie(cookie2);
                     
            Cookie cookie3 = new Cookie("revparam3",revVisitLog.getParameter3());
            cookie3.setMaxAge(168*60*60); //24 hours
            response.addCookie(cookie3);
            
            
                 
             }
            }
         }
         
           session.setAttribute("transref",transaction_ref);
           request.setAttribute("transref",transaction_ref);
           
         if(isLoadContent) page="/content.jsp?cid="+cid+"&transref="+transaction_ref;
         else page="/landingpage.jsp?cid="+cid+"&transref="+transaction_ref;
            
         doRedirect(response,page);
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


private void doRedirect(HttpServletResponse response,String page)
{
    try {
              response.sendRedirect(page);
        }
        catch (Exception e) {
                System.out.println("xstreamtesting: UKService.java doRedirect EXCEPTION: " + e);
        }
}

private String getClientIpAddr(HttpServletRequest request) {
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
