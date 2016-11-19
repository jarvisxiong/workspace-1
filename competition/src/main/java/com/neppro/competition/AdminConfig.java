package com.neppro.competition;

import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@ComponentScan(basePackages={"com.neppro.competition.admin"})
public class AdminConfig{

	
	@Bean
	public ServletRegistrationBean adminDispatcher() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		System.out.println("Application Context in AdminConfig"+applicationContext);
		applicationContext.register(AdminConfig.class);
		dispatcherServlet.setApplicationContext(applicationContext);
		String[] urlMappings={"/admin/*","/admin","/admin/"};
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet,urlMappings);
		servletRegistrationBean.setName("admin");
		
		return servletRegistrationBean;
	}
}
