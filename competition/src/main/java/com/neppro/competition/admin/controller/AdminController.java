package com.neppro.competition.admin.controller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.neppro.competition.model.Domain;

//@RestController
@Controller
@SessionAttributes("sessionDomain")
public class AdminController {
	
	private static final Logger logger = LogManager.getLogger(AdminController.class.getName());
	
	
	
	private ServletContext servletContext;
	
	@Autowired
	private HttpServletRequest request;
	
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Autowired
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}



	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String sayHellFromAdmin(@RequestAttribute("domain") Domain domain,Model model){
		
		/*WebApplicationContext wac=WebApplicationContextUtils.getWebApplicationContext(servletContext);
		
		System.out.println("Application Name: "+wac.getApplicationName());
		String[] beans=wac.getBeanDefinitionNames();
		for(String bean: beans){
			System.out.println("bean: "+bean);
		}
		
		System.out.println("WAC Attribute: "+request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE));
		AnnotationConfigWebApplicationContext wac1=(AnnotationConfigWebApplicationContext) request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		System.out.println("WAC_ID: "+wac1.getId());*/
		model.addAttribute("sessionDomain",domain.getName());
		System.out.println("Domain: "+domain.getName());
		
		return "admin/index";
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public String sayTest(){
		/*WebApplicationContext wac=WebApplicationContextUtils.getWebApplicationContext(servletContext);
		
		System.out.println("Application Name: "+wac.getApplicationName());
		String[] beans=wac.getBeanDefinitionNames();
		for(String bean: beans){
			System.out.println("bean: "+bean);
		}
		
		System.out.println("WAC Attribute: "+request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE));
		AnnotationConfigWebApplicationContext wac1=(AnnotationConfigWebApplicationContext) request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		System.out.println("WAC_ID: "+wac1.getId());*/
		return "test";
	}
}
