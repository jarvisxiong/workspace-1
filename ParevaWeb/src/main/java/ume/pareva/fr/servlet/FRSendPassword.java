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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.fr.util.FRUtil;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.template.TemplateEngine;

import com.mitchellbosecke.pebble.PebbleEngine;

@WebServlet("/FRSendPassword")
public class FRSendPassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( FRSendPassword.class.getName());
    
	@Autowired
	TemplateEngine templateEngine;
	
	@Autowired
	UmeMobileClubUserDao umemobileclubuserdao;
	
	@Autowired
	UmeRequest umeRequest;
	
	@Autowired
	FRUtil frUtil;
	
    public FRSendPassword() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		renderSendPasswordTemplate(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response){
		UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
		String username=umeRequest.get("username");
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		SdcMobileClubUser clubUser=	umemobileclubuserdao.getClubUserByParam1(username, club.getUnique());
		if(clubUser!=null){
			boolean sent=frUtil.sendPassword(clubUser, dmn);
			if(sent){
				//Thank you - you should receive an email shortly
				passwordSentSuccessfully(request,response,"Merci - vous devriez recevoir rapidement un message",sent);
			}else{
				//Due to Technical Problem, we could not send password. Please try again later
				errorSendingPassword(request,response,"Mot de passe envoi a échoué. Veuillez réessayer",sent);
			}
		}else{
			//Sorry we have no account active with that username. Please contact customer care.
			errorSendingPassword(request,response,"Désolé, nous avons aucun compte actif avec ce nom d'utilisateur . S'il vous plaît contacter le service à la clientèle",false);
		}
	}
	
	public void renderSendPasswordTemplate(HttpServletRequest request, HttpServletResponse response,String message,boolean mailSent){
		SdcRequest sdcRequest=new SdcRequest(request);
		UmeDomain dmn=sdcRequest.getDomain();
		try{
			Map<String,Object> context=new HashMap<String,Object>();
			PebbleEngine frEngine=templateEngine.getTemplateEngine(dmn.getUnique());
			PrintWriter writer=response.getWriter();
			context.put("message",message);
			if(mailSent)
				context.put("mailSent","true");
			else
				context.put("mailSent","false");
			frEngine.getTemplate("send-password").evaluate(writer,context);
		} catch (Exception e) {
			logger.error("Error Loading SendPassword Template");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void errorSendingPassword(HttpServletRequest request, HttpServletResponse response,String message,boolean mailSent){
		renderSendPasswordTemplate(request,response,message,mailSent);
	}
	
	public void renderSendPasswordTemplate(HttpServletRequest request, HttpServletResponse response){
		renderSendPasswordTemplate(request,response,"",false);
	}
	
	public void passwordSentSuccessfully(HttpServletRequest request, HttpServletResponse response,String message,boolean mailSent){
		renderSendPasswordTemplate(request,response,message,mailSent);
	}
	

}
