package ume.pareva.userservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.SdcPackage;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.pojo.UmeUserGroup;
import ume.pareva.snp.SnpUser;
import ume.pareva.snp.SnpUserDao;

/**
 *
 * @author madan
 */

@Service("authorizeuser")
public class UserAuthentication {
    
        @Autowired
        UmeTempCache umesdc;
        
        @Autowired 
        SnpUserDao snpuserdao;
        
        @Autowired 
        UmeUserDao umeuserdao;
        
      public static int sessionTimeout = 86400; // 24 hour
    
    public SnpUser authenticateUser(UmeDomain domain, HttpServletRequest req, HttpServletResponse res) {

        
        String login = get("anyx_login", req);
        String pw = get("anyx_pw", req);
        //System.out.println("UME FILTER LOGIN DETAILS "+login+" pwd: "+pw);
        String wapid = "";
        String msisdn = "";
        String fbAccessToken = "";
        HttpSession session = null;
        SnpUser user = null;
        String userUnique = "";
        boolean checkKeepLogged = true;

        try {
            if (domain.getWapIps().equals("1")) {
                wapid = get("id", get("anyxid", "", req), req);
                //System.out.println("==UMEFILTER WAPID RECEIVED IS "+wapid);
                msisdn = getMsisdn(domain, req, req.getSession(false));
                //System.out.println("&&&&&&&&&& UmeFilter MSISDN: " + msisdn);
                req.setAttribute("sdc_msisdn", msisdn);
                req.setAttribute("umemsisdn", msisdn);
            }

            if (!login.equals("") && !pw.equals("")) {
                //System.out.println("BEFORE CALLING SNPUSERDAO::>> Login "+login+" password: "+pw+" SNPUSERDAO "+snpuserdao);
                user = snpuserdao.authenticateUser(login, pw, 1, domain, req);
                
                if (user!=null) {
                    session = req.getSession();
                    //System.out.println("User authenticated. Adding attribute to session: " + session.getId() + ": attr: " + user.getUnique());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    session.setAttribute("sdc_user_unique", user.getParsedMobile());
                    session.setAttribute("user_login_time", sdf.format(new Date()));
                    
                    session.setAttribute("sdc_user_unique", user.getParsedMobile());
                    //System.out.println("USER UNIQUE in SESSION "+user.getParsedMobile());
                    //session.setAttribute("sdc_user", user);
                    session.setMaxInactiveInterval(sessionTimeout);
                    //userUnique = (String) session.getAttribute("sdc_user_unique");
                    //System.out.println("Checking attribute USER_UNIQUE: " + userUnique);
                    user.setHttpSession(session.getId());
                    
                    //MembaseSnp.setUser(user);
                    umesdc.getActiveUsers().put(user.getParsedMobile(), user);
                    umesdc.getUserCache().put(user.getParsedMobile(), user);
                }
                else invalidateUser(req);
            }
            else if (!wapid.equals("")) {
                System.out.println("UserAuthentication  inside wapid "+wapid+" "+domain.getDefaultUrl());
                user = snpuserdao.authenticateUser(wapid, "", 2, domain, req);
                System.out.println("UserAuthentication inside wapid USER ="+user.getParsedMobile());
                if (user!=null) {
                    session = req.getSession();
                    session.setAttribute("sdc_user_unique", user.getParsedMobile());
                    //session.setAttribute("sdc_user", user);
                    session.setMaxInactiveInterval(sessionTimeout);
                    user.setHttpSession(session.getId());
                    
                    System.out.println("UserAuthentication inside wapid session timeout Options "+session.getMaxInactiveInterval());
                    //MembaseSnp.setUser(user);
                    umesdc.getActiveUsers().put(user.getParsedMobile(), user);
                    umesdc.getUserCache().put(user.getParsedMobile(), user);
                     req.setAttribute("sdc_msisdn", user.getParsedMobile());
                req.setAttribute("umemsisdn", user.getParsedMobile());
                }
                else invalidateUser(req);
            }
            else {

                session = req.getSession(false);
                if (session!=null) {
                    //System.out.println("Authenticating and getting user from session: " + session.getId());
                    //user = (SnpUser) session.getAttribute("sdc_user");
                    userUnique = (String) session.getAttribute("sdc_user_unique");

                    if (userUnique!=null && !userUnique.equals("")) {
                        //System.out.println("GOT USER UNIQUE FROM SESSION: " + userUnique);
                        user = snpuserdao.getSnpUser(userUnique); 
                        //user = MembaseSnp.getUser(userUnique);
                        //user = (SnpUser) UmeTempCache.activeUsers.get(userUnique);
                        if (user==null) session.removeAttribute("sdc_user_unique");
                        else umesdc.getActiveUsers().put(user.getParsedMobile(), user);
                        //user = SnpUserDao.getSnpUser(userUnique);                        
                        //if (user==null) 
                        //else MembaseSnp.setUser(user);; //Anyxsdc.activeUsers.put(user.getUnique(), user);
                        
                        checkKeepLogged = false;
                        //System.out.println("SNPUSER FROM SESSION: " + user);
                    }
                }


                if (checkKeepLogged) {

                    if (!msisdn.equals("")) {
                        user = snpuserdao.authenticateUser(msisdn, "", 3, domain, req);
                        if (user!=null) {
                            session = req.getSession();
                            session.setAttribute("sdc_user_unique", user.getParsedMobile());
                            //session.setAttribute("sdc_user", user);
                            session.setMaxInactiveInterval(sessionTimeout);
                            user.setHttpSession(session.getId());
                            //MembaseSnp.setUser(user);
                            umesdc.getActiveUsers().put(user.getParsedMobile(), user);
                            umesdc.getUserCache().put(user.getParsedMobile(), user);
                        }
                        else invalidateUser(req);
                    }
                    else if (session.getAttribute("sdc_access_token")!=null && !session.getAttribute("sdc_access_token").equals("")) {
                        //System.out.println("UmeFilter_SDC_ACCESS_TOKEN: " + session.getAttribute("sdc_access_token"));
                        user = snpuserdao.authenticateUser((String)session.getAttribute("sdc_access_token"), "", 5, domain, req);
                        if (user!=null) {                            
                            session.setAttribute("sdc_user_unique", user.getParsedMobile());
                            //session.setAttribute("sdc_user", user);
                            session.setMaxInactiveInterval(sessionTimeout);
                            user.setHttpSession(session.getId());
                            //MembaseSnp.setUser(user);
                            umesdc.getActiveUsers().put(user.getParsedMobile(), user);
                            umesdc.getUserCache().put(user.getParsedMobile(), user);
                        }
                        else invalidateUser(req);
                    }
                    else {

                        Cookie ck = null;
                        Cookie[] cks = req.getCookies();
                        checkKeepLogged = false;

                        if (cks!=null) {
                            for (int i=0; i<cks.length; i++) {
                                ck = (Cookie) cks[i];
                                //System.out.println("COOOOOKIES: " + ck.getName());
                                if (ck!=null && ck.getName().equals("ANYX_KPLG")) {
                                    login = ck.getValue();
                                    if (login!=null && !login.equals("")) checkKeepLogged = true;
                                    break;
                                }
                            }
                        }

                        //System.out.println("checkKeepLogged: " + checkKeepLogged);
                        //System.out.println("Login: " + login);

                        if (checkKeepLogged) {
                            user = snpuserdao.authenticateUser(login, "", 4, domain, req);
                            if (user!=null) {
                                session = req.getSession();
                                //System.out.println("ANYX_KPLG: User authenticated. Adding attribute to session: " + session.getId() + ": attr: " + user.getUnique());
                                session.setAttribute("sdc_user_unique", user.getParsedMobile());
                                //session.setAttribute("sdc_user", user);
                                session.setMaxInactiveInterval(sessionTimeout);
                                //userUnique = (String) session.getAttribute("sdc_user_unique");
                                //System.out.println("Checking attribute USER_UNIQUE: " + userUnique);
                                user.setHttpSession(session.getId());
                                //MembaseSnp.setUser(user);
                                umesdc.getActiveUsers().put(user.getParsedMobile(), user);
                                umesdc.getUserCache().put(user.getParsedMobile(), user);
                            }
                            else {
                                invalidateUser(req);
                                //System.out.println("Invalidating ANYX_KPLG cookie");
                                ck = new Cookie("ANYX_KPLG", "");
                                ck.setMaxAge(0);
                                ck.setPath("/");
                                res.addCookie(ck);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) { System.out.println("UmeFilter.authenticateUser: " + e);e.printStackTrace(); }

        return user;
        
    }
    
        private String getMsisdn(UmeDomain domain, HttpServletRequest req, HttpSession ses) {

        if (domain.getMsisdnHeaders()!=null) {
            String[] hdrs = domain.getMsisdnHeaders().split("\r\n");
            for (int i=0; i<hdrs.length; i++) {
                //System.out.println(hdrs[i] + ": " + req.getHeader(hdrs[i]));
                if (!hdrs[i].equals("") && req.getHeader(hdrs[i])!=null && !req.getHeader(hdrs[i]).equals("")) 
                    return SdcMisc.parseMsisdn(req.getHeader(hdrs[i]));
            }
        }
        if (ses!=null && ses.getAttribute("sdc_msisdn_param")!=null) 
            return SdcMisc.parseMsisdn((String)ses.getAttribute("sdc_msisdn_param"));
        
        return "";
    }
        
        
        
        
    public boolean authenticateService(SdcService service, UmeUser user, UmeDomain domain) {
        
        try {
            //System.out.println("SERVICE: " + service + ": " + service.getServiceType() + ": " + (service.getServiceType()==SdcService.ServiceType.DOMAIN));
            //System.out.println("USER: " + user);
            if (service==null) return false;
            if (service.getSecLevel()==0) return true;
            if (user==null) return false;

            if (service.getServiceType()==SdcService.ServiceType.DOMAIN) {
                //System.out.println("SERVICE DIR: " + service.getDirectory() + ": domain.privateDir: " + domain.getDefPrivateDir());
                if (service.getDirectory().equals(domain.getDefPrivateDir())) return true;
                return false;
            }

            if (service.getServiceType()!=SdcService.ServiceType.ANYXADMIN) {
                //System.out.println("Service Sec: " + service.getSecLevel() + ": " + user.getSecLevel());
                if (service.getSecLevel()>user.getSecLevel()) return false;

                UmeUserGroup group = umesdc.getUserGroupMap().get(user.getUserGroup());
                //System.out.println("group: " + group);
                Iterator<SdcPackage> it = service.getPackageList().iterator();
                while (it.hasNext()) {
                    //String unique = it.next().getUnique();
                    //System.out.println("pack: " + unique);
                    //System.out.println("packMap: " + group.getPackageMap());
                    if (group.getPackageMap().get(it.next().getUnique())!=null) return true;
                }
                return false;
            }

            // Handle ADMIN service
            if (user.getAdminGroup()>=service.getSecLevel()) return true;

        }
        catch (NullPointerException e) { System.out.println("UmeCore.authneticateService: " + e); }
        return false;
    }
        
       public void invalidateUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        //System.out.println("invalidating session: " + session);
        if (session!=null) {
            //System.out.println("invalidating session: " + session.getId());
            //session.removeAttribute("sdc_user");
            if (session.getAttribute("sdc_user_unique")!=null && !session.getAttribute("sdc_user_unique").equals("")) {
                umesdc.getActiveUsers().remove((String) session.getAttribute("sdc_user_unique"));
            }
            session.removeAttribute("sdc_user_unique");
            session.removeAttribute("sdc_access_token");
            //session.invalidate();
            
            Cookie[] cookies = null;
             cookies = req.getCookies();
         if( cookies != null ){
             for(Cookie cookie:cookies)
             {
                 cookie.setValue("");
                 cookie.setMaxAge(0);
             }
             
         }
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
