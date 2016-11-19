package ume.pareva.fr.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;

@WebServlet("/FRLogout")
public class FRLogout extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Autowired
	UmeRequest umeRequest;

	private static final Logger logger = LogManager.getLogger( FRLogout.class.getName());

	public FRLogout() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UmeDomain dmn=umeRequest.getDomain();
		HttpSession session=request.getSession();
		session.invalidate();
		response.sendRedirect("http://"+dmn.getDefaultUrl());
	}
}
