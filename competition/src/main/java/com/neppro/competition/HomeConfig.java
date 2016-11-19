package com.neppro.competition;

import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@ComponentScan(basePackages={"com.neppro.competition.controller"})
public class HomeConfig {

	@Bean
	public ServletRegistrationBean homeDispatcher() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(HomeConfig.class);
		dispatcherServlet.setApplicationContext(applicationContext);
		String[] urlMappings={"/"};
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet,urlMappings);
		servletRegistrationBean.setName(DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);
		return servletRegistrationBean;
	}
}
