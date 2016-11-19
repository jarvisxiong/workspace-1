package ume.pareva.fr.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.template.TemplateEngine;

import com.mitchellbosecke.pebble.PebbleEngine;


public class FRAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( FRAccount.class.getName());
    
	@Autowired
	TemplateEngine templateEngine;
	   
	@Autowired
	UmeRequest umeRequest;
	
    public FRAccount() {
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
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response){
		UmeDomain dmn = umeRequest.getDomain();
		HttpSession session=request.getSession();
		String subscriptionId="";
		if(session.getAttribute("subscriptionId")!=null)
			subscriptionId=session.getAttribute("subscriptionId").toString();
		try{
			PrintWriter writer = response.getWriter();
			Map<String,Object> context = new HashMap<String,Object>();
			if(!subscriptionId.equals("")){
				context.put("resiliationLink","cancelSubscription.jsp");
				context.put("showLogout","true");
				context.put("logout","logout.jsp");
			}else{
				context.put("showLogout","false");
				context.put("resiliationLink","http://billing.virgopass.com/fr_unsubscription.php");
			}
			context.put("contenturl","http://"+dmn.getContentUrl());
			context.put("contact","contact.jsp");
			context.put("terms","terms.jsp");
			context.put("unsubscribe","unsubscribe.jsp");
			PebbleEngine frEngine=templateEngine.getTemplateEngine(dmn.getUnique());
			frEngine.getTemplate("account").evaluate(writer, context);
		}catch(Exception e){
    		logger.error("Error Rendering Account Template");
    		e.printStackTrace();
    	}

	}

}
