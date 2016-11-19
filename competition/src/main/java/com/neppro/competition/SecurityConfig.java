package com.neppro.competition;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.neppro.competition.security.SaltedCredentialsMatcher;
import com.neppro.competition.security.SpringHibernateJdbcRealm;
import com.neppro.competition.service.UserService;

@Configuration
public class SecurityConfig {

    @Bean
    public SpringHibernateJdbcRealm springHibernateJdbcRealm(UserService userService){
    	SpringHibernateJdbcRealm springHibernateJdbcRealm=new SpringHibernateJdbcRealm(userService);
    	springHibernateJdbcRealm.setCredentialsMatcher(saltedCredentialsMatcher());
    	return springHibernateJdbcRealm;
    }
    
    @Bean
    public SaltedCredentialsMatcher saltedCredentialsMatcher(){
    	return new SaltedCredentialsMatcher();
    }

    @Bean
    public WebSecurityManager securityManager(UserService userService){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(springHibernateJdbcRealm(userService));
        return securityManager;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn(value="lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        return new DefaultAdvisorAutoProxyCreator();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(UserService userService){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager(userService));
        return authorizationAttributeSourceAdvisor;
    }
}
