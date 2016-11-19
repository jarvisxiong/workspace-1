package ume.pareva.pt.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.aggregator.go4mobility.Go4MobilityService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.PTConstants;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.template.TemplateEngine;
import ume.pareva.userservice.LandingPage;

import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.CancelSubscriptionRequest;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.UserIdToken;
import com.mitchellbosecke.pebble.PebbleEngine;


public class PTConfirmCancelSubscription extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( PTConfirmCancelSubscription.class.getName());
	private UserIdToken userIdToken;   
	
	@Autowired
	TemplateEngine templateEngine;
	
	@Autowired
	UmeRequest umeRequest;

	@Autowired 
	Go4MobilityService go4MobilityService;
	
	@Autowired
	PTConstants ptConstants;
	   
	@Autowired
	LandingPage landingpage;

    public PTConfirmCancelSubscription() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
 		super.init(config);
 		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
 				config.getServletContext());
 	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		renderTemplate("confirm_unsubscribe",request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);
		int cancelSubscriptionResult=999;
		String cancelSubscriptionStatus="";
		String subscriptionId=umeRequest.get("subscriptionId");
		if(!subscriptionId.equals("")){
			userIdToken=go4MobilityService.getUserIdToken(Go4MobilityService.login, Go4MobilityService.pw);
			CancelSubscriptionRequest cancelSubscriptionRequest=go4MobilityService.getCancelSubscriptionRequest(userIdToken, subscriptionId);
			cancelSubscriptionResult=go4MobilityService.cancelSubscription(cancelSubscriptionRequest);
			if(cancelSubscriptionResult==0){
				/*Cookie cookie=umeRequest.getCookie("subscriptionId");
				if(cookie!=null){
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
				*/redirectToHome(request,response);
				logger.info("Subscription Cancelled Successfully for SubscriptionId {}",subscriptionId);
				session.invalidate();
			}else{
				logger.error("Exception: Error Cancelling Subscription - "+ptConstants.getResultCodeMap().get(cancelSubscriptionStatus));
			}
		}
		
	}
	
	public void redirectToHome(HttpServletRequest request,HttpServletResponse response){
		UmeDomain dmn=umeRequest.getDomain();
		try {
			response.sendRedirect("http://"+dmn.getDefaultUrl()+"/?subscription_cancelled=true");
		} catch (IOException e) {
			logger.error("Error Redirecting to Home Page");
			e.printStackTrace();
		}

	}
	
	public Map<String,Object> renderTemplate(String template,HttpServletRequest request,HttpServletResponse response){
		HttpSession session=request.getSession();
		String cancelSubscriptionStatus="";
		UmeDomain dmn=umeRequest.getDomain();
		String subscriptionId="";
		if(session.getAttribute("subscriptionId")!=null)
			subscriptionId=session.getAttribute("subscriptionId").toString();
		else
			subscriptionId=umeRequest.readCookie("subscriptionId");
		Map<String,Object> context=new HashMap<String,Object>();
		PebbleEngine ptEngine=templateEngine.getTemplateEngine(dmn.getUnique());
		context.put("contenturl","http://"+dmn.getContentUrl());
		context.put("subscriptionId",subscriptionId);
		context.put("cancelSubscription","confirmCancelSubscription.jsp");
		try{
			PrintWriter writer=response.getWriter();
			ptEngine.getTemplate(template).evaluate(writer, context);
			
		}catch(Exception e){
			logger.error("Exception: Error Loading Ubsubscription Template in renderTemplate method of PTCancelSubscription Servlet");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return context;

	}
	
	

}
