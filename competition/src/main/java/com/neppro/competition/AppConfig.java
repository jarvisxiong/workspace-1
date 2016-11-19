package com.neppro.competition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.connector.Connector;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.neppro.competition.interceptor.RequestInterceptor;
import com.neppro.competition.security.ExtendedBasicHttpAuthenticationFilter;

@Configuration
@ComponentScan(basePackages={"com.neppro.competition"},
				excludeFilters=@ComponentScan.Filter(type = FilterType.ANNOTATION,value=Controller.class)
				)
@EnableAutoConfiguration
@EnableTransactionManagement
@Import({SecurityConfig.class,DAOConfig.class})
public class AppConfig extends WebMvcConfigurerAdapter implements WebApplicationInitializer
{
	public static void main( String[] args )
	{
		ApplicationContext ctx = SpringApplication.run(AppConfig.class, args);
		
		System.out.println("Let's inspect the beans provided by Spring Boot:");
		String[] beanNames = ctx.getBeanDefinitionNames();
	    Arrays.sort(beanNames);
	    for (String beanName : beanNames) {
	        System.out.println(beanName);
	    }
	}

	@Bean
	public ShiroFilterFactoryBean shiroFilter(WebSecurityManager securityManager){
		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
		Map<String, String> definitionsMap = new HashMap<String, String>();
		shiroFilter.setSecurityManager(securityManager);
		shiroFilter.setLoginUrl("/login.jsp");
		definitionsMap.put("/login.jsp", "authc");
	//	definitionsMap.put("/admin", "authc");
		Map<String, Filter> filters = new HashMap<String, Filter>();
		filters.put("authcBasic", new ExtendedBasicHttpAuthenticationFilter());
		shiroFilter.setFilters(filters);
		shiroFilter.setFilterChainDefinitionMap(definitionsMap);
		return shiroFilter;
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		DelegatingFilterProxy shiroFilter=new DelegatingFilterProxy("shiroFilter");
		shiroFilter.setTargetFilterLifecycle(true);
		servletContext.addFilter("shiroFilter", shiroFilter)
		.addMappingForUrlPatterns(null, false, "/*");
	}

	@Bean(name="messageSource")
	public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource(){
		ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource=new ReloadableResourceBundleMessageSource();
		reloadableResourceBundleMessageSource.setBasename("classpath:validation_error_messages");
		reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
		return reloadableResourceBundleMessageSource;

	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor(){
		LocaleChangeInterceptor localeChangeInterceptor=new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;

	}

	@Bean
	public RequestInterceptor requestInterceptor(){
		RequestInterceptor requestInterceptor=new RequestInterceptor();
		return requestInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(requestInterceptor()).addPathPatterns("/");
	}

	@Bean(name="localeResolver")
	public SessionLocaleResolver sessionLocaleResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("en", "US"));
		return localeResolver;
	}

	/*@Bean(name="templateResolver")
	public TemplateResolver SpringResourceTemplateResolver() {
		SpringResourceTemplateResolver springResourceTemplateResolver=new SpringResourceTemplateResolver();
		springResourceTemplateResolver.setPrefix("classpath:/templates/");
		springResourceTemplateResolver.setSuffix(".html");
		springResourceTemplateResolver.setTemplateMode("HTML5");
		return springResourceTemplateResolver;
	}*/
	
	@Bean(name="templateResolver")
	public TemplateResolver fileTemplateResolver() {
		FileTemplateResolver fileTemplateResolver=new FileTemplateResolver();
		fileTemplateResolver.setPrefix("D:\\templates\\");//or fileTemplateResolver.setPrefix("D:/templates/"); 
		fileTemplateResolver.setSuffix(".html");
		fileTemplateResolver.setTemplateMode("HTML5");
		return fileTemplateResolver;
	}

	@Bean(name="templateEngine")
	public SpringTemplateEngine springTempalteEngine(){
		SpringTemplateEngine springTemplateEngine=new SpringTemplateEngine();
		//springTemplateEngine.setTemplateResolver(SpringResourceTemplateResolver());
		springTemplateEngine.setTemplateResolver(fileTemplateResolver());
		springTemplateEngine.setTemplateEngineMessageSource(reloadableResourceBundleMessageSource());
		return springTemplateEngine;
	}

	@Bean(name="thymeleafViewResolver")
	public ThymeleafViewResolver thymeleafViewResolver(){
		ThymeleafViewResolver thymeleafViewResolver=new ThymeleafViewResolver();
		thymeleafViewResolver.setTemplateEngine(springTempalteEngine());
		thymeleafViewResolver.setCharacterEncoding("UTF-8");
		return thymeleafViewResolver;
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
		Connector ajpConnector = new Connector("AJP/1.3");
		ajpConnector.setProtocol("AJP/1.3");
		ajpConnector.setPort(8009);
		ajpConnector.setSecure(false);
		ajpConnector.setAllowTrace(false);
		ajpConnector.setScheme("http");
		tomcat.addAdditionalTomcatConnectors(ajpConnector);
		return tomcat;
	}
	
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/META-INF/resources/webjars/").setCachePeriod(31556926);
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/").setCachePeriod(31556926);
        //registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCachePeriod(31556926);
        //registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(31556926);
    }
	
	/*@Bean
	public ServletRegistrationBean adminDispatcher() {
		DispatcherServlet dispatcherServlet = new DispatcherServlet();
		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(AdminConfig.class);
		dispatcherServlet.setApplicationContext(applicationContext);
		String[] urlMappings={"/admin/*","/admin","/admin/"};
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet,urlMappings);
		servletRegistrationBean.setName("admin");
		return servletRegistrationBean;
	}*/


}
