package ume.pareva.es;

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
import ume.pareva.cms.MobileClubCampaign;
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

/**
 * 
 * @author madan
 */

@Component("ES_Filter")
public class ES_Filter implements RegionFilter {


	@Autowired
	UmeTempCache umesdc;

	@Autowired
	SnpUserDao snpuserdao;
	//
	@Autowired
	UmeUserDao umeuserdao;

	@Autowired
	UserAuthentication userauthentication;

	public ES_Filter() {
	}

	@Override
	public boolean doFilterForRegion(HttpServletRequest req,
			HttpServletResponse res) throws IOException, ServletException {

		// TODO ES Filteration here.
		// System.out.println("---ES FILTER CALLED ---- ");
		UmeDomain domain = null;
		SdcService service = null;
		SnpUser user = null;
		SdcLanguage lang = null;
		String path = "";
		boolean defaultServlet = false;
		Cookie ck = null;
		// userauthentication=new UserAuthentication();
		UmeFilter.ServiceType serviceType = null;

		try {
			domain = umesdc.getDomainsByHost().get(req.getServerName());
			// System.out.println("---ES FILTER CALLED ---- "+domain.getDefaultUrl());
			MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain.getUnique());
			// System.out.println("---ES FILTER CALLED ---- "+domain.getDefaultUrl()+" Club "+club.getName());

			user = userauthentication.authenticateUser(domain, req, res);

			if ((SdcRequest.get("anyx_kplg", req).equals("true") || !SdcRequest
					.get("id", req).equals(""))
					&& user != null
					&& !user.getActiveClubCode().equals("")) {
				// System.out.println("Setting ANYX_KPLG Cookie: " +
				// user.getActiveClubCode());
				ck = new Cookie("ANYX_KPLG", user.getActiveClubCode());
				ck.setMaxAge(2592000);
				ck.setPath("/");
				// ck.setDomain(domain.getDefaultUrl());
				res.addCookie(ck);
			}

			if (user != null)
				lang = umesdc.getLanguageMap().get(user.getLanguage());
			if (lang == null)
				lang = umesdc.getLanguageMap().get(domain.getDefaultLang());
			if (lang == null)
				lang = umesdc.getLanguageMap().get("en");

			path = get("_sdcpath", req);
			//System.out.println("ES FILTER PATH RECEIVED IS " + path);
			
			// ==Download Link
			if (!get("axud", req).equals("") || !get("anyx_usedef", req).equals("")) {
				defaultServlet = true;
				path = get("d", req);
				if (!path.endsWith("/"))
					path += "/";
				// System.out.println("==SERVICE PATH::>> DEFAULTSERVLET : "+defaultServlet
				// +" path: "+path);
			}

			else
				defaultServlet = false;

			if (path.startsWith("/")) {
				path = path.substring(1); // System.out.println("PATH WITH / "+path);

			}

			if (path.length() == 0 || path.equals("/")) {
				serviceType = ServiceType.ROOT;
				if (user == null)
					service = umesdc.getServiceMap().get(domain.getUnique() + "_pub");
				else {
					// System.out.println("SERVICE in path "+path
					// +" domain unique pri "+domain.getUnique()+"_pri"+" Services in "+umesdc.getServiceMap().get(domain.getUnique()
					// + "_pri"));
					service = umesdc.getServiceMap().get(domain.getUnique() + "_pri");
				}
				path = service.getDefaultPage();
				// System.out.println("PATH DEFAULT PAGE "+path);
			}

			else {
				int i;
				if ((i = path.indexOf("/")) > -1) {
					serviceType = ServiceType.STANDARD;
					service = umesdc.getServicesByDir().get(
							path.substring(0, i));
				} else if (path.indexOf(".") == -1) {
					serviceType = ServiceType.STANDARD;
					service = umesdc.getServicesByDir().get(path);
				} else {
					serviceType = ServiceType.ROOT;
					if (user == null) {
						service = umesdc.getServiceMap().get(
								domain.getUnique() + "_pub");
					} else {
						// System.out.println("SERVICE IN UmeFilter:: "+domain.getUnique()+"_pri"
						// +" services "+umesdc.getServiceMap().get(domain.getUnique()
						// + "_pri"));
						service = umesdc.getServiceMap().get(
								domain.getUnique() + "_pri");
					}
				}

				// System.out.println("SERVICE: " + service);
				// System.out.println("Type: " + serviceType);
			}
                            
                        
                        
                        if(user==null){
                    String addparam="";
                    MobileClubCampaign cmpg=null;
                    String midparam=req.getParameter("mid");
                    HttpSession session=req.getSession(false);
                    String campaignId=(String) req.getAttribute("cid");
                    if(campaignId==null) campaignId=(String) session.getAttribute("cid");
                    if(campaignId==null)campaignId=(String)req.getParameter("cid");
                    if (campaignId!=null && !campaignId.equals("")) {
                        cmpg=UmeTempCmsCache.campaignMap.get(campaignId);
                        addparam="&cid="+campaignId;
                        if(cmpg==null || (cmpg!=null && cmpg.getaDisableInitialRedirect()==0)){
                            
                        String bybRedirected=(String)session.getAttribute("redirected");
                        if(bybRedirected==null && domain.getaInitialRedirectUrl()!=null && !domain.getaInitialRedirectUrl().equalsIgnoreCase("")  && (path.equals("umequiz.jsp") || (path.equals("index.jsp")))){
                            session.setAttribute("redirected","1");                                  
                                  if(midparam!=null && !"".equalsIgnoreCase(midparam))
                                      addparam+="&mid="+midparam;
                                  
                            res.sendRedirect(domain.getaInitialRedirectUrl()+"?redirected=1"+addparam);
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
                          String interstitialRedirected=req.getParameter("interstitialredirected"); //(String)session.getAttribute("interstitialRedirected");
                          
                          if(interstitialRedirected==null){
                           //session.setAttribute("interstitialRedirected","1");
                           String interstitialDomainUnique=domain.getPartnerDomain();
                           UmeDomain interstitialDomain=umesdc.getDomainMap().get(interstitialDomainUnique);
                           String interstitialRedirectUrl="http://"+interstitialDomain.getDefaultUrl()+"/?redirectback="+redirectBack+"&cid="+cid+"&clubid="+clubId+"&interstitiallandingpage="+interstitialLandingPage+"&interstitialredirected=1";
                           res.sendRedirect(interstitialRedirectUrl);
                           return true;
                          }
                         }
                         
                         
                         
                         
                         
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////             
                         
                }// End if user==null 

			if (service == null) {
				serviceType = ServiceType.ROOT;
				service = umesdc.getServiceMap().get(domain.getUnique() + "_pub");
				path = "error/service_not_ok.jsp";
				// if (user==null) service =
				// UmeTempCache.serviceMap.get(domain.getUnique() + "_pub");
				// else service = UmeTempCache.serviceMap.get(domain.getUnique()
				// + "_pri");
				// path = service.getDefaultPage();
			}
                        /*
			System.out.println("SERVICE: " + service);
			System.out.println("IT_FILTERSERVICETYPE: "
					+ service.getServiceType());
			System.out.println("IT_Filter " + service.getDirectory() + ": "
					+ service.getUnique());
			System.out.println("IT_Filter Type: " + serviceType);
			System.out.println("IT_Filter PATH: " + path);
*/
			if (!userauthentication.authenticateService(service, user, domain)) {
				// System.out.println("Could NOT authneticate SMS service.");
				service = umesdc.getServiceMap().get(domain.getSmsErrorSrvc());
			}

//			System.out.println("IT FILTER REDIRECTION CALLED UPON :"
//					+ serviceType + " path: " + path);
//
//			System.out.println("*******************************************");
//			System.out.println("IT_FILTER line 185 SERVICE: " + service);
//			System.out.println("IT_FILTER line 185 path: " + path);
//			System.out.println("IT_FILTER line 185 domain: " + domain);
//			System.out.println("IT_FILTER line 185 serviceType: " + serviceType);
//			System.out.println("IT_FILTER line 185 user: " + user);
//			System.out.println("IT_FILTER line 185 lang: " + lang);
//			System.out.println("IT_FILTER line 185 defaultServlet: " + defaultServlet);
//			System.out.println("IT_FILTER line 185 req: " + req);
//			System.out.println("IT_FILTER line 185 res: " + res);
//			System.out.println("*******************************************");

			forwardRequest(serviceType, path, domain, service, user, lang,
					defaultServlet, req, res);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;

	}

	private void forwardRequest(ServiceType serviceType, String path,
			UmeDomain domain, SdcService service, UmeUser user,
			SdcLanguage lang, boolean defaultServlet, HttpServletRequest req,
			HttpServletResponse res) {

		try {
//			 System.out.println("INSIDE forwardRequest : value of defaultServlet "+defaultServlet
//					 +" path: "+path+" service type: "+service.getServiceType());
			if (serviceType == ServiceType.ROOT) {
				path = service.getDirectory() + "/" + path + "?anyx_srvc="
						+ service.getUnique();
			} else if (serviceType == ServiceType.STANDARD) {
				if (defaultServlet) {
					if (!path.endsWith("/"))
						path += "/";
					path += "fromDefaultServlet.jsp";
					// System.out.println("Inside Forward REquest defaultServlet true condition path= "+path);
				} else if (path.endsWith("/"))
					path += service.getDefaultPage();
				else if (path.indexOf(".") == -1)
					path += "/" + service.getDefaultPage();
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

//			System.out.println("*******************************************");
//			System.out.println("forwardRequest Forwarding: " + path +"  ====   "+umesdc.getCxt().getContextPath());
//			System.out.println("forwardRequest Forwarding to /"+path);
//			System.out.println("forwardRequest line 185 req: " + req);
//			System.out.println("forwardRequest line 185 res: " + res);
//			System.out.println("*******************************************");

			umesdc.getCxt().getRequestDispatcher("/" + path).forward(req, res);
			return;
		} catch (Exception e) {
			if (req != null) {
				System.out.println("ES_FILTER.forwardRequest exception req.getServerName(): "+ req.getServerName());
				System.out.println("ES_FILTER.forwardRequest exception domain: "+ domain);
				System.out.println("ES_FILTER.forwardRequest exception service: "+ service);
				System.out.println("ES_FILTER.forwardRequest exception user: "+ user);
				System.out.println("ES_FILTER.forwardRequest exception lang: "+ lang);
				System.out.println("ES_FILTER.forwardRequest exception serviceType: "+ serviceType);
				System.out.println("ES_FILTER.forwardRequest exception path: "+ path);
			}

			System.out.println("ES_FILTER UmeCore.forwardRequest exception : " + e);
			e.printStackTrace();

		}
	}

	private String get(String name, HttpServletRequest req) {
		return get(name, "", true, req);
	}

	private String get(String name, boolean trim, HttpServletRequest req) {
		return get(name, "", trim, req);
	}

	private String get(String name, String defaultValue, HttpServletRequest req) {
		return get(name, defaultValue, true, req);
	}

	private String get(String name, String defaultValue, boolean trim,
			HttpServletRequest req) {
		if (req.getParameter(name) != null
				&& !req.getParameter(name).equals("")) {
			if (trim)
				return req.getParameter(name).trim();
			else
				return req.getParameter(name);
		}
		return defaultValue;
	}

}
