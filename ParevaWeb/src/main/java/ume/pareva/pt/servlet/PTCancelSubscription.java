package ume.pareva.pt.servlet;

import java.io.IOException;
import java.io.PrintWriter;
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

import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.CancelSubscriptionRequest;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.UserIdToken;
import com.mitchellbosecke.pebble.PebbleEngine;


public class PTCancelSubscription extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( PTCancelSubscription.class.getName());
	
	@Autowired
	TemplateEngine templateEngine;
	
	@Autowired
	UmeRequest umeRequest;

	public PTCancelSubscription() {
        super();
     }
     
     public void init(ServletConfig config) throws ServletException {
 		super.init(config);
 		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
 				config.getServletContext());
 	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		renderTemplate("unsubscribe",request,response);
	}

	public Map<String,Object> renderTemplate(String template,HttpServletRequest request,HttpServletResponse response){
		UmeDomain dmn=umeRequest.getDomain();
		Map<String,Object> context=new HashMap<String,Object>();
		PebbleEngine ptEngine=templateEngine.getTemplateEngine(dmn.getUnique());
		context.put("contenturl","http://"+dmn.getContentUrl());
		context.put("continueSubscription","videos.jsp");
		context.put("cancelSubscription","confirmCancelSubscription.jsp");
		
		try{
			PrintWriter writer=response.getWriter();
			ptEngine.getTemplate(template).evaluate(writer, context);
			
		}catch(Exception e){
			logger.error("Exception: Error Loading Ubsubscription Template");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return context;

	}
}
