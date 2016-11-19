package ume.pareva.fr.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

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

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.SdcMiscCr;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.fr.util.FRUtil;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.template.TemplateEngine;

import com.mitchellbosecke.pebble.PebbleEngine;

@WebServlet("/FRLogin")
public class FRLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Autowired
	UmeUserDao umeuserdao;

	@Autowired
	TemplateEngine templateEngine;

	@Autowired
	UmeMobileClubUserDao umemobileclubuserdao;

	@Autowired
	FRUtil frUtil;
	
	@Autowired
	UmeRequest umeRequest;

	private static final Logger logger = LogManager.getLogger( FRLogin.class.getName());

	public FRLogin() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username=umeRequest.get("username");
		String password=umeRequest.get("password");
		if(!username.equals("")&&!password.equals(""))
			doPost(request,response);
		else
			renderLoginTemplate(request,response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession();
		int retry=0;
		if(session.getAttribute("retry")!=null)
			retry=(Integer)session.getAttribute("retry");
		UmeDomain dmn=umeRequest.getDomain();
		String username=umeRequest.get("username");
		String password=umeRequest.get("password");
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		SdcMobileClubUser clubUser=	umemobileclubuserdao.getClubUserByParam1(username, club.getUnique());
		if(clubUser!=null){
			String userPassword=SdcMiscCr.decrypt(clubUser.getParam2());
			if(password.equals(userPassword)){
				Map<String, String> userInfoMap = frUtil.getUserInfo(clubUser.getParsedMobile());
				String userStatus = userInfoMap.get("status");
				if(userStatus!=null&&userStatus.equals("subscribed")){
					redirectUserAndSaveSession(clubUser.getParsedMobile(),request,response);
				}
				else{
					//Username and password do not match
					errorLogin(request,response,"Your Subscription is in "+userStatus+" state");
				}
			}else{
				
				session.setAttribute("retry",++retry);
				System.out.println("number of Retry: "+retry);
				//Username and password do not match
				errorLogin(request,response,"Nom d'utilisateur et mot de passe ne correspondent pas");
			}
		}else{
			errorLogin(request,response,"Nom d'utilisateur et mot de passe ne correspondent pas");
		}
	}

	public void errorLogin(HttpServletRequest request, HttpServletResponse response,String errorMessage){
		renderLoginTemplate(request,response,errorMessage);
	}

	public void renderLoginTemplate(HttpServletRequest request, HttpServletResponse response){
		renderLoginTemplate(request,response,"");
	}

	public void renderLoginTemplate(HttpServletRequest request, HttpServletResponse response,String errorMessage){
		SdcRequest sdcRequest=new SdcRequest(request);
		UmeDomain dmn=sdcRequest.getDomain();
		HttpSession session=request.getSession();
		int retry=0;
		if(session.getAttribute("retry")!=null)
			retry=(Integer)session.getAttribute("retry");
		try{
			Map<String,Object> context=new HashMap<String,Object>();
			PebbleEngine frEngine=templateEngine.getTemplateEngine(dmn.getUnique());
			PrintWriter writer=response.getWriter();
			context.put("retry",retry);
			context.put("errorMessage",errorMessage);
			frEngine.getTemplate("login").evaluate(writer,context);
		} catch (Exception e) {
			logger.error("Error Loading Login Template");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}


	public void redirectUserAndSaveSession(String subscriptionId,HttpServletRequest request,HttpServletResponse response){
		frUtil.setSessionAttribute(request,"subscriptionId",subscriptionId);
		String serviceUrl=frUtil.getServiceUrl();
		try {
			response.sendRedirect(serviceUrl);
		} catch (IOException e) {
			logger.error("Error Redirecting to Video Page"+e);
		}
		}

}
