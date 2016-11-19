package ume.pareva.ke.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.dao.SdcRequest;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.template.TemplateEngine;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;

public class KESmsSuccess extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( KESmsSuccess.class.getName());
	
	@Autowired
	TemplateEngine templateEngine;
       
    public KESmsSuccess() {
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
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SdcRequest sdcRequest=new SdcRequest(request);
		UmeDomain dmn=sdcRequest.getDomain();
		String status = "Thanks! You Should Receive SMS Shortly. Reply <b>YES</b> to Confirm You are 18+ and Start Your <b>FREE</b> Trial.";
		renderTemplate(dmn,status,response);
		
	}
	
	public void renderTemplate(UmeDomain dmn,String status,HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		Map<String, Object> context = new HashMap<String,Object>();
		context.put("smssuccess","true"); 
		context.put("message",status);
		context.put("contenturl","http://"+ dmn.getContentUrl());
		PebbleEngine keEngine=templateEngine.getTemplateEngine(dmn.getUnique());
		try {
			keEngine.getTemplate("status").evaluate(writer, context);
		} catch (PebbleException e) {
			logger.error("Error Rendering SMS Success Template");
			e.printStackTrace();
		}

	}


}
