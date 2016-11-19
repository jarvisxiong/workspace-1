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
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.fr.util.FRUtil;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Smtp;
import ume.pareva.template.TemplateEngine;

import com.mitchellbosecke.pebble.PebbleEngine;

@WebServlet("/FRWelcome")
public class FRWelcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( FRWelcome.class.getName());

	@Autowired
	TemplateEngine templateEngine;

	@Autowired
	UmeUserDao umeuserdao;

	@Autowired
	UmeMobileClubUserDao umemobileclubuserdao;

	@Autowired
	Smtp smtp;

	@Autowired
	FRUtil frUtil;
	
	@Autowired
	UmeRequest umeRequest;

	public FRWelcome() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		renderWelcomeTemplate(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message="";
		SdcRequest sdcRequest=new SdcRequest(request);
		UmeDomain dmn=sdcRequest.getDomain();
		String msisdn=sdcRequest.get("msisdn");
		String username=sdcRequest.get("email");
		System.out.println("username: "+username);
		String password=SdcMisc.generatePw(8);
		String subscriptionId=sdcRequest.get("subscription_id");
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		SdcMobileClubUser clubUser=umemobileclubuserdao.getClubUserByMsisdn(subscriptionId, club.getUnique());
		System.out.println("Club User: "+clubUser.getParsedMobile());
		clubUser.setParam1(username);
		clubUser.setParam2(SdcMisc.encrypt(password));
		umemobileclubuserdao.saveItem(clubUser);
		UmeUser user=umeuserdao.getUser(subscriptionId);
		user.setPhoneUnique(msisdn);
		umeuserdao.saveUser(user);
		frUtil.sendPassword(clubUser, dmn);
		message="Thank you - we have sent you your password";
		passwordSendSuccessfully(request,response,message);

	}

	public void renderWelcomeTemplate(HttpServletRequest request, HttpServletResponse response){
		SdcRequest sdcRequest=new SdcRequest(request);
		UmeDomain dmn=sdcRequest.getDomain();
		String subscriptionId=sdcRequest.get("subscription_id");
		String msisdn="";
		try{
			Map<String,String> userInfoMap=frUtil.getUserInfo(subscriptionId);
			msisdn=userInfoMap.get("msisdn");
		}catch(Exception e){
			e.printStackTrace();
		}
		Map<String,Object> context=new HashMap<String,Object>();
		PebbleEngine frEngine=templateEngine.getTemplateEngine(dmn.getUnique());
		try {
			PrintWriter writer=response.getWriter();
			context.put("msisdn",msisdn);
			context.put("subscriptionId",subscriptionId);
			frEngine.getTemplate("welcome").evaluate(writer,context);
		} catch (Exception e) {
			logger.error("Error Loading Welcome Template");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void passwordSendSuccessfully(HttpServletRequest request, HttpServletResponse response,String message){
		String serviceUrl=frUtil.getServiceUrl();
		UmeDomain dmn=umeRequest.getDomain();
		Map<String,Object> context=new HashMap<String,Object>();
		PebbleEngine frEngine=templateEngine.getTemplateEngine(dmn.getUnique());
		try {
			PrintWriter writer=response.getWriter();
			context.put("serviceUrl",serviceUrl);
			context.put("message",message);
			context.put("mailSent","true");
			frEngine.getTemplate("welcome").evaluate(writer,context);
		} catch (Exception e) {
			logger.error("Error Loading Welcome Template");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}



}
