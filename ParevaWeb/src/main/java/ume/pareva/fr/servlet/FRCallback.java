package ume.pareva.fr.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.fr.util.FRUtil;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;


public class FRCallback extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( FRCallback.class.getName());

	@Autowired
	FRUtil frUtil;
	
	@Autowired
	UmeRequest umeRequest;

	public FRCallback() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		Map<String,String> callbackMap=frUtil.mapRequestToCallback(request);
		System.out.println("*****************************************Callback Parameter Start***********************************************");

		System.out.println("Error Code: "+callbackMap.get("error_code"));
		System.out.println("Error Desc: "+callbackMap.get("error_desc"));
		System.out.println("Subscription ID: "+callbackMap.get("subscription_id"));
		System.out.println("Session ID: "+callbackMap.get("session_id"));
		System.out.println("Operator ID: "+callbackMap.get("ope_id"));
		System.out.println("Opt1: "+callbackMap.get("opt1"));
		System.out.println("Opt2: "+callbackMap.get("opt2"));
		System.out.println("Event: "+callbackMap.get("event"));
		System.out.println("Welcome: "+callbackMap.get("welcome"));

		StringBuilder responsebuilder=new StringBuilder();
		responsebuilder.append("Error Code: "+callbackMap.get("error_code"));
		responsebuilder.append("Error Desc: "+callbackMap.get("error_desc"));
		responsebuilder.append("Subscription ID: "+callbackMap.get("subscription_id"));
		responsebuilder.append("Session ID: "+callbackMap.get("session_id"));
		responsebuilder.append("Operator ID: "+callbackMap.get("ope_id"));
		responsebuilder.append("Opt1: "+callbackMap.get("opt1"));
		responsebuilder.append("Opt2: "+callbackMap.get("opt2"));
		responsebuilder.append("Event: "+callbackMap.get("event"));
		responsebuilder.append("Welcome: "+callbackMap.get("welcome"));
		System.out.println("francecallback URL "+responsebuilder.toString());

		System.out.println("*****************************************Callback Parameter End*************************************************");

		String errorCode=callbackMap.get("error_code");
		UmeDomain dmn=umeRequest.getDomain();
		if(errorCode.equals("0")){
			if(callbackMap.get("event").equalsIgnoreCase("UNSUBSCRIBE")){
				response.sendRedirect("http://"+dmn.getDefaultUrl());
			}else{
				saveCookie(callbackMap,response);
				redirectUserAndSaveSession(callbackMap,request,response);
			}
		}else if(errorCode.equals("88")){
			redirectUserAndSaveSession(callbackMap,request,response);

		}else if(errorCode.equals("37")){
			redirectUserAndSaveSession(callbackMap,request,response);

		}else if (errorCode.equals("33")){
			logger.info("User Cancelled Payment");
			response.sendRedirect("http://"+dmn.getDefaultUrl());
		}else{
			//Unable to Unsubscribe User so taking back to content page 
			if(callbackMap.get("event").equalsIgnoreCase("UNSUBSCRIBE")){
				redirectUserAndSaveSession(callbackMap,request,response);
			}

		}			
	}

	public void redirectUserAndSaveSession(Map<String,String> callbackMap,HttpServletRequest request,HttpServletResponse response){
		UmeDomain dmn=umeRequest.getDomain();
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		UmeClubDetails userClubDetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
		try{
			if(userClubDetails.getServiceType().equals("Content")){
				if(userClubDetails.getServedBy().equals("UME")){
					frUtil.setSessionAttribute(request,"subscriptionId",callbackMap.get("subscription_id"));
					String errorCode=callbackMap.get("error_code");
					if(errorCode.equals("0")){
						response.sendRedirect("http://"+dmn.getDefaultUrl()+"/welcome.jsp?subscription_id="+callbackMap.get("subscription_id"));
					}else if (errorCode.equals("37")){
						if(callbackMap.get("welcome").equals("true"))
							response.sendRedirect("http://"+dmn.getDefaultUrl()+"/welcome.jsp?subscription_id="+callbackMap.get("subscription_id"));		
						else if(callbackMap.get("welcome").equals(""))
							response.sendRedirect("http://"+dmn.getDefaultUrl()+"/videos.jsp");
					}else{
						response.sendRedirect("http://"+dmn.getDefaultUrl()+"/videos.jsp");
					}
				}else{
					response.sendRedirect("http://"+dmn.getRedirectUrl());
				}

			}else {
				//redirection code for competition service
			}
		}catch(Exception e){
			logger.error("Error Redirecting To Video Page");
			e.printStackTrace();
		}

	}
	
	public void saveCookie(Map<String,String> callbackMap,HttpServletResponse response){
		String subscriptionId=callbackMap.get("subscription_id");
		Cookie cookie = new Cookie("subscriptionId",subscriptionId);
		cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
		response.addCookie(cookie);
	}

}
