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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.template.TemplateEngine;

import com.mitchellbosecke.pebble.PebbleEngine;

public class FRContact extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( FRContact.class.getName());
    
       
	@Autowired
	TemplateEngine templateengine;
	
	@Autowired
	UmeRequest umeRequest;
	
    public FRContact() {
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
		try{
			PrintWriter writer = response.getWriter();
			Map<String,Object> context = new HashMap<String,Object>();
			context.put("contenturl","http://"+dmn.getContentUrl());
			context.put("account","account.jsp");
			context.put("terms","terms.jsp");
			PebbleEngine frEngine=templateengine.getTemplateEngine(dmn.getUnique());
			frEngine.getTemplate("contact").evaluate(writer, context);
		}catch(Exception e){
    		logger.error("Error Rendering Contact Template");
    		e.printStackTrace();
    	}

	}

}
