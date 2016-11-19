package ume.pareva.gr;

import ume.pareva.ire.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.core.RegionFilter;
import ume.pareva.core.UmeFilter;
import ume.pareva.dao.QueryHelper;
import ume.pareva.dao.SdcMiscDate;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.pojo.SdcLanguage;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Misc;
import ume.pareva.snp.SnpUser;
import ume.pareva.userservice.UserAuthentication;

/**
 *
 * @author madan
 */

@Component("GR"+UmeFilter.SUFFIX)
public class GRFilter implements RegionFilter {
    static final Logger logger = LogManager.getLogger(GRFilter.class.getName());
    
    @Autowired
    UmeTempCache umesdc;
    
    @Autowired
    UserAuthentication userauthentication;
    
    @Autowired
    QueryHelper queryhelper;
    

    @Override
    public boolean doFilterForRegion(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        ThreadContext.put("ROUTINGKEY", "GR");
        //System.out.println("---UK FILTER CALLED ---- ");
        UmeDomain domain = null;
        SdcService service = null;
        SdcLanguage lang = null;
        String path = "";
        SnpUser user = null;
        boolean defaultServlet = false;
        Cookie ck = null;        
        UmeFilter.ServiceType serviceType = null;
        
        try{
             domain = umesdc.getDomainsByHost().get(req.getServerName());
            //System.out.println("---UK FILTER CALLED ---- "+domain.getDefaultUrl());
            MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain.getUnique());
            //System.out.println("---UK FILTER CALLED ---- "+domain.getDefaultUrl()+" Club "+club.getName());
             user = userauthentication.authenticateUser(domain, req, res);
             
            
                   if ((SdcRequest.get("anyx_kplg", req).equals("true") || !SdcRequest.get("id", req).equals("")) && user!=null && !user.getActiveClubCode().equals("")) {
                logger.info("Setting ANYX_KPLG Cookie: " + user.getActiveClubCode());
                 ck = new Cookie("ANYX_KPLG", user.getActiveClubCode());
                 ck.setMaxAge(2592000);
                 ck.setPath("/");
                 //ck.setDomain(domain.getDefaultUrl());
                 res.addCookie(ck);
            }
             
             
         if (lang==null) lang = umesdc.getLanguageMap().get(domain.getDefaultLang());
         if (lang==null) lang = umesdc.getLanguageMap().get("en");
         
            path = get("_sdcpath", req);
            
            //==Download Link 
               if (!get("axud", req).equals("") || !get("anyx_usedef", req).equals("")) 
               {
                defaultServlet = true;
                path = get("d", req);
                if (!path.endsWith("/")) path += "/";
                //System.out.println("==SERVICE PATH::>> DEFAULTSERVLET : "+defaultServlet +" path: "+path);
            }
               else defaultServlet = false;
             
            
                     if (path.startsWith("/")) { 
                path = path.substring(1); //System.out.println("PATH WITH / "+path); 
               
                }
                
                if (path.length()==0 || path.equals("/")) {
                serviceType = UmeFilter.ServiceType.ROOT;
                service = umesdc.getServiceMap().get(domain.getUnique() + "_pub");
               
                path = service.getDefaultPage();
                //System.out.println("PATH DEFAULT PAGE "+path);
            }
                
        else {
                int i;
                if ((i = path.indexOf("/"))>-1) {
                    serviceType = UmeFilter.ServiceType.STANDARD;
                    service = umesdc.getServicesByDir().get(path.substring(0, i));                    
                }
                else if (path.indexOf(".")==-1) {
                    serviceType = UmeFilter.ServiceType.STANDARD;
                    service = umesdc.getServicesByDir().get(path);
                }
                else {
                    serviceType = UmeFilter.ServiceType.ROOT;                    
                        service = umesdc.getServiceMap().get(domain.getUnique() + "_pub");
                    
                }

                //System.out.println("SERVICE: " + service);
                //System.out.println("Type: " + serviceType);
            }
                if(user==null){
                    String addparam="",ip="",referer="",requestUrl="";
                    MobileClubCampaign cmpg=null;
                    String midparam=req.getParameter("mid");
                    String msisdnparam=req.getParameter("msisdn");
                    HttpSession session=req.getSession(false);
                    String campaignId=(String) req.getAttribute("cid");
                    String sessionStr = "";
                     Cookie[] cki = null;
                    
                    if(campaignId==null) campaignId=(String) session.getAttribute("cid");
                    if(campaignId==null)campaignId=(String)req.getParameter("cid");
                    if (campaignId!=null && !campaignId.equals("")) {
                        
                        
                        if (req != null) {
                                cki = req.getCookies();
                                ip = req.getHeader("X-Forwarded-For");
                          
                            try{
                                referer = req.getHeader("Referer");
                            }catch(Exception e){referer="";}
            
                            try{
                                requestUrl = req.getRequestURL() + "?" + Misc.getQueryString(req);
                            }catch(Exception e){requestUrl="";}
                        }
                        
                            if (ck != null) {
                                for (int i = 0; i < cki.length; i++) {
                                    if (cki[i] != null && cki[i].getName().equals("_ANYXWAPSESSION")) {
                                        sessionStr = cki[i].getValue();
                                        break;
                                    }
                                }
                            }
                        
                        
                        
                        
                        cmpg=UmeTempCmsCache.campaignMap.get(campaignId);
                        addparam="&cid="+campaignId;
                        if(cmpg==null || (cmpg!=null && cmpg.getaDisableInitialRedirect()==0)){
                            
                        String bybRedirected=(String)session.getAttribute("redirected");
                        String bybParam=(String) req.getParameter("redirected");
                        if(bybRedirected==null && bybParam==null && domain.getaInitialRedirectUrl()!=null && !domain.getaInitialRedirectUrl().equalsIgnoreCase("")  && (path.equals("umequiz.jsp") || (path.equals("index.jsp")))){
                            session.setAttribute("redirected","1");                                  
                                  if(midparam!=null && !"".equalsIgnoreCase(midparam)) addparam+="&mid="+midparam;
                                  if(msisdnparam!=null && !"".equalsIgnoreCase(msisdnparam))addparam+="&msisdn="+msisdnparam;
                                  
                            
                            String sqlquery=" INSERT INTO bybtrack(aUnique,sessionid,aCreated,aParsedMobile,aCampaignId,byburl,referrer,requesturl,aSessionStr,ipaddress) " +
                                    " VALUES('"+Misc.generateAnyxSessionId()+"','"+session.getId()+"','"+SdcMiscDate.toSqlDate(new Date())+"','"+Misc.encrypt(msisdnparam)+"','"+campaignId+"','"+domain.getaInitialRedirectUrl()+"','"+referer+"','"+requestUrl+"','"+sessionStr+"','"+ip+"')";
                            System.out.println("bybtrackquery "+sqlquery);
                            
                            queryhelper.executeUpdateQuery(sqlquery, "IEFilterBYB");
                                  
                            res.sendRedirect(domain.getaInitialRedirectUrl()+"?redirected=1"+addparam);
                            System.out.println("bybtrackip "+ip+" referer "+referer);
                            return true;
                           }
                        }
                    }
                         
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////             
                         if(cmpg!=null&&!cmpg.getaInterstitialLandingPage().equals("")&&cmpg.getaInterstitialLandingPage()!=null){  
                          String redirectBack=URLEncoder.encode("http://"+domain.getDefaultUrl(),"UTF-8");
                          String cid=URLEncoder.encode((campaignId!=null)?campaignId:"","UTF-8");
                          String clubId=URLEncoder.encode(club.getUnique(),"UTF-8");
                          String interstitialLandingPage=cmpg.getaInterstitialLandingPage();
                          String interstitialRedirected=(String)session.getAttribute("interstitialRedirected");
                          if(interstitialRedirected==null){
                           session.setAttribute("interstitialRedirected","1");
                           String interstitialDomainUnique=domain.getPartnerDomain();
                           UmeDomain interstitialDomain=umesdc.getDomainMap().get(interstitialDomainUnique);
                           String interstitialRedirectUrl="http://"+interstitialDomain.getDefaultUrl()+"/?redirectback="+redirectBack+"&cid="+cid+"&clubid="+clubId+"&interstitiallandingpage="+interstitialLandingPage;
                           res.sendRedirect(interstitialRedirectUrl);
                           return true;
                          }
                         }
                         
                         
                         
                         
                         
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////             
                         
                }// End if user==null 
                
            if (service==null) {
                serviceType = UmeFilter.ServiceType.ROOT;
                service = umesdc.getServiceMap().get(domain.getUnique() + "_pub");
                path = "error/service_not_ok.jsp";
                
            }
            
//            System.out.println("SERVICE: " + service);
//            System.out.println("UK_FILTERSERVICETYPE: " + service.getServiceType());
//            System.out.println("UK_Filter "+service.getDirectory() + ": " + service.getUnique());
//            System.out.println("UK_Filter Type: " + serviceType);
//            System.out.println("UK_Filter PATH: " + path);
            
            forwardRequest(serviceType, path, domain, service,user, lang, defaultServlet, req, res);
            
        }
        catch(Exception e){System.out.println("IRE Filter Exception "+e);e.printStackTrace();}
        
        
        return true;
    }
    
     private void forwardRequest(UmeFilter.ServiceType serviceType, String path, UmeDomain domain, SdcService service,UmeUser user, SdcLanguage lang,
                                        boolean defaultServlet, HttpServletRequest req, HttpServletResponse res) {

        try {
//System.out.println("INSIDE forwardRequest : value of defaultServlet "+defaultServlet +" path: "+path+" service type: "+service.getServiceType());
            if (serviceType==UmeFilter.ServiceType.ROOT) {
                path = service.getDirectory() + "/" + path + "?anyx_srvc=" + service.getUnique();
            }
            else if (serviceType==UmeFilter.ServiceType.STANDARD) {
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
            
            //System.out.println("UK_Filter Forwarding to /"+path);
            
            umesdc.getCxt().getRequestDispatcher("/" + path).forward(req,res);
        }
        catch (Exception e) { 
        	if(req!=null){
        		System.out.println("GR_Filter.forwardRequest exception req.getServerName(): " + req.getServerName());
        		System.out.println("GR_Filter.forwardRequest exception domain: " + domain);
        		System.out.println("GR_Filter.forwardRequest exception service: " + service);
        		System.out.println("GR_Filter.forwardRequest exception lang: " + lang);        		
        		System.out.println("GR_Filter.forwardRequest exception serviceType: " + serviceType); 
        		System.out.println("GR_Filter.forwardRequest exception path: " + path); 
        	}
        	
        	System.out.println("GR_Filter UmeCore.forwardRequest exception : " + e);
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
