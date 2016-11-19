package com.neppro.competition.security;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class ExtendedBasicHttpAuthenticationFilter extends BasicHttpAuthenticationFilter{
	
	private static final Logger log = LoggerFactory.getLogger(ExtendedBasicHttpAuthenticationFilter.class);
	
	@Override
	 protected boolean sendChallenge(ServletRequest request, ServletResponse response) {
       if (log.isDebugEnabled()) {
            log.debug("Authentication required: sending 401 Authentication challenge response.");
        }
        System.out.println("sending challenge");
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String authcHeader = getAuthcScheme() + " realm=\"" + getApplicationName() + "\"";
        httpResponse.setHeader(AUTHENTICATE_HEADER, authcHeader);
        httpResponse.setContentType("application/json");
        try {
			httpResponse.getWriter().write("{"
					+ "\"errorCode\":\"401\","
					+ "\"errorDescription\":\"UNAUTHORIZE\""
					+ "}");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;
    }

}
