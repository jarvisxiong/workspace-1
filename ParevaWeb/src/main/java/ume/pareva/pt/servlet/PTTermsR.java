package ume.pareva.pt.servlet;

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
import ume.pareva.template.TemplateEngine;

import com.mitchellbosecke.pebble.PebbleEngine;

public class PTTermsR extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( PTTermsR.class.getName());
       
	@Autowired
	TemplateEngine templateengine;
	
    public PTTermsR() {
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
		UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
		try{
			PrintWriter writer = response.getWriter();
			Map<String,Object> context = new HashMap<String,Object>();
			context.put("contenturl","http://"+dmn.getContentUrl());
			PebbleEngine ptEngine=templateengine.getTemplateEngine(dmn.getUnique());
			ptEngine.getTemplate("terms_r").evaluate(writer, context);
		}catch(Exception e){
    		logger.error("Error Rendering TermsR Template");
    		e.printStackTrace();
    	}

	}


}
