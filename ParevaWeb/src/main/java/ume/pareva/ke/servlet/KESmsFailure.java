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

public class KESmsFailure extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( KESmsFailure.class.getName());
	
	@Autowired
	TemplateEngine templateEngine;
       
    public KESmsFailure() {
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
		String doiRequestStatus=sdcRequest.get("doiRequestStatus");
		String msisdn=sdcRequest.get("msisdn");
		String status="";
		boolean goToMain=false;
		if(doiRequestStatus.equals("DOI Request Already Sent")){
			status="You Already Made a Subscription Request for This Service in Last 24 Hours! Please Try Again Later !";
			goToMain=false;
		}else{
			status="Please Check Your Mobile Number And Try Again!!";
			goToMain=true;
		}
		renderTemplate(dmn,status,goToMain,response);
		
		
	}
	
	public void renderTemplate(UmeDomain dmn,String status,boolean goToMain,HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		Map<String, Object> context = new HashMap<String,Object>();
		if(goToMain){
			context.put("gotomainlink","http://"+ dmn.getDefaultUrl());
			context.put("gotomain","true");
		}else{
			context.put("gotomain","false");
		}
		
		context.put("smssuccess","false"); 
		context.put("message",status);
		context.put("contenturl","http://"+ dmn.getContentUrl());
		PebbleEngine keEngine=templateEngine.getTemplateEngine(dmn.getUnique());
		try {
			keEngine.getTemplate("status").evaluate(writer, context);
		} catch (PebbleException e) {
			logger.error("Error Rendering SMS Failure Template");
			e.printStackTrace();
		}

	}
}
