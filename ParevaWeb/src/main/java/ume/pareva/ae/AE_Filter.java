package ume.pareva.ae;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.core.RegionFilter;
import ume.pareva.core.UmeFilter;
import ume.pareva.core.UmeFilter.ServiceType;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.SdcLanguage;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.snp.SnpUser;
import ume.pareva.snp.SnpUserDao;
import ume.pareva.userservice.UserAuthentication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import ume.pareva.cms.MobileClubCampaign;

/**
 *
 * @author madan
 */

@Component("AE_Filter")
public class AE_Filter implements RegionFilter{
    static final Logger logger = LogManager.getLogger(AE_Filter.class.getName());
    
    @Autowired
	UmeTempCache umesdc;
        
        @Autowired 
        SnpUserDao snpuserdao;
//        
        @Autowired 
        UmeUserDao umeuserdao;
        
        @Autowired
        UserAuthentication userauthentication;
    
    public AE_Filter(){}
    

    @Override
    public boolean doFilterForRegion(HttpServletRequest  req, HttpServletResponse res) throws IOException, ServletException {
        ThreadContext.put("ROUTINGKEY", "UAE");
        ThreadContext.put("EXTRA", "");
        
        //TODO ZA Filteration here. 
        //System.out.println("---ZA FILTER CALLED ---- ");
        UmeDomain domain = null;
        SdcService service = null;
        SnpUser user = null;
        SdcLanguage lang = null;
        String path = "";
        boolean defaultServlet = false;
        Cookie ck = null;
        //userauthentication=new UserAuthentication();
        UmeFilter.ServiceType serviceType = null;
         MobileClub club=null;
        
        evaluateIPAddress(req);
       
        try{
            domain = umesdc.getDomainsByHost().get(req.getServerName());
            //logger.info("---UAE FILTER CALLED ---- {}",domain.getDefaultUrl());
            club= UmeTempCmsCache.mobileClubMap.get(domain.getUnique());
            
            //logger.info("---UAE FILTER CALLED ---- {} Club {}",domain.getDefaultUrl(),club.getName());
            
            user = userauthentication.authenticateUser(domain, req, res);
            
            
            
             if ((SdcRequest.get("anyx_kplg", req).equals("true") || !SdcRequest.get("id", req).equals("")) && user!=null && !user.getActiveClubCode().equals("")) {
                logger.info("Setting ANYX_KPLG Cookie: " + user.getActiveClubCode());
                 ck = new Cookie("ANYX_KPLG", user.getActiveClubCode());
                 ck.setMaxAge(2592000);
                 ck.setPath("/");
                 //ck.setDomain(domain.getDefaultUrl());
                 res.addCookie(ck);
            }
          
            if (user!=null) lang = umesdc.getLanguageMap().get(user.getLanguage());
            if (lang==null) lang = umesdc.getLanguageMap().get(domain.getDefaultLang());
            if (lang==null) lang = umesdc.getLanguageMap().get("en");
            
            path = get("_sdcpath", req);
            //logger.info("UAE FILTER PATH RECEIVED IS "+path);
            
            if(path.contains("jpt.jsp")) path="index_main.jsp";
            //==Download Link 
               if (!get("axud", req).equals("") || !get("anyx_usedef", req).equals("")) 
               {
                defaultServlet = true;
                path = get("d", req);
                if (!path.endsWith("/")) path += "/";
                //logger.info("==SERVICE PATH::>> DEFAULTSERVLET : "+defaultServlet +" path: "+path);
            }
               
            else defaultServlet = false;
               
                if (path.startsWith("/")) { 
                path = path.substring(1); logger.info("PATH WITH / "+path); 
               
                }
                
                if (path.length()==0 || path.equals("/")) {
                serviceType = ServiceType.ROOT;
                if (user==null) service = umesdc.getServiceMap().get(domain.getUnique() + "_pub");
                else {
                    //logger.info("SERVICE in path "+path +" domain unique pri "+domain.getUnique()+"_pri"+" Services in "+umesdc.getServiceMap().get(domain.getUnique() + "_pri"));
                    service = umesdc.getServiceMap().get(domain.getUnique() + "_pri");
                }
                path = service.getDefaultPage();
                //logger.info("PATH DEFAULT PAGE "+path);
            }
                
               else {
                int i;
                if ((i = path.indexOf("/"))>-1) {
                    serviceType = ServiceType.STANDARD;
                    service = umesdc.getServicesByDir().get(path.substring(0, i));                    
                }
                else if (path.indexOf(".")==-1) {
                    serviceType = ServiceType.STANDARD;
                    service = umesdc.getServicesByDir().get(path);
                }
                else {
                    serviceType = ServiceType.ROOT;
                    if (user==null) {
                        service = umesdc.getServiceMap().get(domain.getUnique() + "_pub");
                    }
                    else{ 
                        //System.out.println("SERVICE IN UmeFilter:: "+domain.getUnique()+"_pri" +" services "+umesdc.getServiceMap().get(domain.getUnique() + "_pri"));
                        service = umesdc.getServiceMap().get(domain.getUnique() + "_pri");
                    }
                }

                //System.out.println("SERVICE: " + service);
                //System.out.println("Type: " + serviceType);
            }
                
                if(user==null){
                    String addparam="";
                    MobileClubCampaign cmpg=null;
                    String midparam=req.getParameter("mid");
                    String msisdnparam=req.getParameter("msisdn");
                    HttpSession session=req.getSession(false);
                    String campaignId=(String) req.getAttribute("cid");
                    if(campaignId==null) campaignId=(String) session.getAttribute("cid");
                    if(campaignId==null)campaignId=(String)req.getParameter("cid");
                    if (campaignId!=null && !campaignId.equals("")) {
                        cmpg=UmeTempCmsCache.campaignMap.get(campaignId);
                        addparam="&cid="+campaignId;
                        if(cmpg==null || (cmpg!=null && cmpg.getaDisableInitialRedirect()==0)){
                            
                        String bybRedirected=(String)session.getAttribute("redirected");
                        String bybParam=(String) req.getParameter("redirected");
                        if(bybRedirected==null && bybParam==null && domain.getaInitialRedirectUrl()!=null && !domain.getaInitialRedirectUrl().equalsIgnoreCase("")  && (path.equals("umequiz.jsp") || (path.equals("index.jsp")))){
                            session.setAttribute("redirected","1");                                  
                                  if(midparam!=null && !"".equalsIgnoreCase(midparam)) addparam+="&mid="+midparam;
                                  if(msisdnparam!=null && !"".equalsIgnoreCase(msisdnparam))addparam+="&msisdn="+msisdnparam;
                                  
                            res.sendRedirect(domain.getaInitialRedirectUrl()+"?redirected=1"+addparam); //
                            return true;
                           }
                        }
                    }
      
                         
                }// End if user==null 
             
            if (service==null) {
                serviceType = ServiceType.ROOT;
                service = umesdc.getServiceMap().get(domain.getUnique() + "_pub");
                path = "error/service_not_ok.jsp";
             
            }
  
            forwardRequest(serviceType, path, domain, service, user, lang, defaultServlet, req, res);
 
        }
        catch(Exception e){
            //e.printStackTrace();
            ThreadContext.put("EXTRA", "ERROR");
            logger.error("EXCEPTION ClubUnique {}  domain {}  {}",club.getUnique(),req.getServerName(),  e.getMessage());
            logger.error("EXCEPTION {}", e);
            
            ThreadContext.put("EXTRA", "");
        }
        return true;
        
        
        
    }
    
    private void forwardRequest(ServiceType serviceType, String path, UmeDomain domain, SdcService service, UmeUser user, SdcLanguage lang,
                                        boolean defaultServlet, HttpServletRequest req, HttpServletResponse res) {

        try {
//System.out.println("INSIDE forwardRequest : value of defaultServlet "+defaultServlet +" path: "+path+" service type: "+service.getServiceType());
            if (serviceType==ServiceType.ROOT) {
                path = service.getDirectory() + "/" + path + "?anyx_srvc=" + service.getUnique();
            }
            else if (serviceType==ServiceType.STANDARD) {
                if (defaultServlet) {
                    if (!path.endsWith("/")) path += "/";
                    path += "fromDefaultServlet.jsp";
                    //System.out.println("Inside Forward REquest defaultServlet true condition path= "+path); 
                }
                else if (path.endsWith("/")) path += service.getDefaultPage();
                else if (path.indexOf(".")==-1) path += "/" + service.getDefaultPage();
                path += "?anyx_srvc=" + service.getUnique();
            }
           
            else {
                path = service.getDirectory() + "/" + path;
            }
           
            req.setAttribute("sdc_domain", domain);
            req.setAttribute("sdc_service", service);
            req.setAttribute("sdc_user", user);
            req.setAttribute("sdc_lang", lang);
            
            req.setAttribute("umedomain", domain);
            req.setAttribute("umeservice", service);
            req.setAttribute("umeuser", user);
            req.setAttribute("umelang", lang);
        
        
            //System.out.println("Forwarding: " + path+"  ====   "+umesdc.getCxt().getContextPath());
            
            //System.out.println("AE_Filter Forwarding to /"+path);
            
            umesdc.getCxt().getRequestDispatcher("/" + path).forward(req,res);
            return;
        }
        catch (Exception e) { 
        	if(req!=null){
        		System.out.println("UAE_FILTER.forwardRequest exception req.getServerName(): " + req.getServerName());
        		System.out.println("UAE_FILTER.forwardRequest exception domain: " + domain);
        		System.out.println("UAE_FILTER.forwardRequest exception service: " + service);        		
        		System.out.println("UAE_FILTER.forwardRequest exception user: " + user);        		
        		System.out.println("UAE_FILTER.forwardRequest exception lang: " + lang);        		
        		System.out.println("UAE_FILTER.forwardRequest exception serviceType: " + serviceType); 
        		System.out.println("UAE_FILTER.forwardRequest exception path: " + path); 
        	}
        	
        	System.out.println("UAE_FILTER UmeCore.forwardRequest exception : " + e.getMessage());
                //e.printStackTrace();
        
        }
    }
    
    private String get(String name, HttpServletRequest req) { return get(name, "", true, req); }
    private String get(String name, boolean trim, HttpServletRequest req) { return get(name, "", trim, req); }
    private String get(String name, String defaultValue, HttpServletRequest req) { return get(name, defaultValue, true, req); }
    private String get(String name, String defaultValue, boolean trim, HttpServletRequest req) {
        if (req.getParameter(name)!=null && !req.getParameter(name).equals("")) {
            if (trim) return req.getParameter(name).trim();
            else return req.getParameter(name);
        }
        return defaultValue;
    }
    
    public void evaluateIPAddress(HttpServletRequest req){
    	String ipAddress = req.getHeader("X-FORWARDED-FOR");
        if(ipAddress!=null){
        	int idx = ipAddress.indexOf(',');
        		if (idx > -1) {
        			ipAddress = ipAddress.substring(0, idx);
        		}
        }else{  
     	   ipAddress = req.getRemoteAddr();  
        }
        logger.info("---IP ADDRESS IN UAE FILTER  ---- {}",ipAddress);
        
      //  req.setAttribute("ipAddress", "80.215.131.176");
        req.setAttribute("ipAddress", ipAddress);
    }
    
}
