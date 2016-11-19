package ume.pareva.ke;

import java.io.IOException;
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

@Component("KE_Filter")
public class KE_Filter implements RegionFilter{
    static final Logger logger = LogManager.getLogger( KE_Filter.class.getName());
    
    @Autowired
	UmeTempCache umesdc;
        
        @Autowired 
        SnpUserDao snpuserdao;
//        
        @Autowired 
        UmeUserDao umeuserdao;
        
        @Autowired
        UserAuthentication userauthentication;
            
    public KE_Filter(){}
    

    @Override
    public boolean doFilterForRegion(HttpServletRequest  req, HttpServletResponse res) throws IOException, ServletException {
        ThreadContext.put("ROUTINGKEY", "KE");
        
        //System.out.println("---KE FILTER CALLED ---- ");
        UmeDomain domain = null;
        SdcService service = null;
        SnpUser user = null;
        SdcLanguage lang = null;
        String path = "";
        boolean defaultServlet = false;
        Cookie ck = null;
        //userauthentication=new UserAuthentication();
        UmeFilter.ServiceType serviceType = null;
        
        String ipAddress = req.getHeader("X-FORWARDED-FOR");  
        if (ipAddress == null) {  
     	   ipAddress = req.getRemoteAddr();  
        }
        req.setAttribute("ipAddress", ipAddress);
        
        try{
            domain = umesdc.getDomainsByHost().get(req.getServerName());
            //logger.info("---KE FILTER CALLED ---- {}",domain.getDefaultUrl());
            MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain.getUnique());
            //logger.info("---KE FILTER CALLED ---- {} Club {}",domain.getDefaultUrl(),club.getName());
            
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
            logger.info("FR FILTER PATH RECEIVED IS "+path);
            if(path.contains("jpt.jsp")) path="index_main.jsp";
            //==Download Link 
               if (!get("axud", req).equals("") || !get("anyx_usedef", req).equals("")) 
               {
                defaultServlet = true;
                path = get("d", req);
                if (!path.endsWith("/")) path += "/";
                logger.info("==SERVICE PATH::>> DEFAULTSERVLET : "+defaultServlet +" path: "+path);
            }
               
            else defaultServlet = false;
               
                if (path.startsWith("/")) { 
                path = path.substring(1); logger.info("PATH WITH / "+path); 
               
                }
                
                if (path.length()==0 || path.equals("/")) {
                serviceType = ServiceType.ROOT;
                if (user==null) service = umesdc.getServiceMap().get(domain.getUnique() + "_pub");
                else {
                    logger.info("SERVICE in path "+path +" domain unique pri "+domain.getUnique()+"_pri"+" Services in "+umesdc.getServiceMap().get(domain.getUnique() + "_pri"));
                    service = umesdc.getServiceMap().get(domain.getUnique() + "_pri");
                }
                path = service.getDefaultPage();
                logger.info("PATH DEFAULT PAGE "+path);
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
                      String midparam=req.getParameter("mid");
                     
                	HttpSession session=req.getSession(false);
                         String campaignId=(String) req.getAttribute("cid");
                         if(campaignId==null) campaignId=(String) session.getAttribute("cid");
                         if(campaignId==null)campaignId=(String)req.getParameter("cid");
                         if (campaignId!=null && !campaignId.equals("")) {
                             MobileClubCampaign cmpg=UmeTempCmsCache.campaignMap.get(campaignId);
                            addparam="&cid="+campaignId;
                         if(cmpg==null || (cmpg!=null && cmpg.getaDisableInitialRedirect()==0)){
                        String bybRedirected=(String)session.getAttribute("redirected");
                	   if(bybRedirected==null && domain.getaInitialRedirectUrl()!=null && !domain.getaInitialRedirectUrl().equalsIgnoreCase("")
                			   && (path.equals("umequiz.jsp") || (path.equals("index.jsp")))){
                		  session.setAttribute("redirected","1");
                                  if(midparam!=null && !"".equalsIgnoreCase(midparam))
                                      addparam+="&mid="+midparam;
                		  res.sendRedirect(domain.getaInitialRedirectUrl()+"?redirected=1"+addparam);
                          return true;
                           }
                         }
                    }
                }
             
            if (service==null) {
                serviceType = ServiceType.ROOT;
                service = umesdc.getServiceMap().get(domain.getUnique() + "_pub");
                path = "error/service_not_ok.jsp";
                //if (user==null) service = UmeTempCache.serviceMap.get(domain.getUnique() + "_pub");
                //else service = UmeTempCache.serviceMap.get(domain.getUnique() + "_pri");
                //path = service.getDefaultPage();
            }
            
//                System.out.println("SERVICE: " + service);
//            System.out.println("KE_FilterSERVICETYPE: " + service.getServiceType());
//            System.out.println("KE_Filter "+service.getDirectory() + ": " + service.getUnique());
//            System.out.println("KE_Filter Type: " + serviceType);
//            System.out.println("KE_Filter PATH: " + path);
                
            
            if (!userauthentication.authenticateService(service, user, domain)) {
                //System.out.println("Could NOT authneticate SMS service.");
               service = umesdc.getServiceMap().get(domain.getSmsErrorSrvc());
           }
            
            //System.out.println("FR FILTER REDIRECTION CALLED UPON :"+serviceType+ " path: "+path);
            forwardRequest(serviceType, path, domain, service, user, lang, defaultServlet, req, res);
            
            
            
            
            
        }
        catch(Exception e){e.printStackTrace();}
        
        
        
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
            
            //System.out.println("KE_Filter Forwarding to /"+path);
            
            umesdc.getCxt().getRequestDispatcher("/" + path).forward(req,res);
            return;
        }
        catch (Exception e) { 
        	if(req!=null){
        		System.out.println("KE_Filter.forwardRequest exception req.getServerName(): " + req.getServerName());
        		System.out.println("KE_Filter.forwardRequest exception domain: " + domain);
        		System.out.println("KE_Filter.forwardRequest exception service: " + service);        		
        		System.out.println("KE_Filter.forwardRequest exception user: " + user);        		
        		System.out.println("KE_Filter.forwardRequest exception lang: " + lang);        		
        		System.out.println("KE_Filter.forwardRequest exception serviceType: " + serviceType); 
        		System.out.println("KE_Filter.forwardRequest exception path: " + path); 
        	}
        	
        	System.out.println("KE_Filter UmeCore.forwardRequest exception : " + e);
                e.printStackTrace();
        
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
    
}
