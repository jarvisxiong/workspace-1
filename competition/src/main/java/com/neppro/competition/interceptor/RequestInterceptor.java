package com.neppro.competition.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.neppro.competition.dao.DomainDao;
import com.neppro.competition.model.Domain;

public class RequestInterceptor extends HandlerInterceptorAdapter{

	private DomainDao domainDao;
	
	public DomainDao getDomainDao() {
		return domainDao;
	}

	@Autowired
	public void setDomainDao(DomainDao domainDao) {
		this.domainDao = domainDao;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
		throws Exception {
		System.out.println("Requested Intercepted: "+request.getServletPath());
		System.out.println("Requested Intercepted: "+request.getServerName()+":"+request.getServerPort());
		Domain domain=domainDao.getDomainByDefaultUrl(request.getServerName());
		if(domain!=null){
			String dispatcherServlet=domain.getDispatcherServlet();
			request.setAttribute("domain",domain);
			request.getSession().setAttribute("sessionDomain",domain);
			request.getServletContext().getRequestDispatcher(dispatcherServlet).forward(request, response);
			
		}
		return false;
	}
}
