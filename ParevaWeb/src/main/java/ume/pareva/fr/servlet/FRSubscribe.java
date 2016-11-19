package ume.pareva.fr.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UserSessionDao;
import ume.pareva.fr.FRConstant;
import ume.pareva.fr.util.FRUtil;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UserSession;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;

public class FRSubscribe extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
	@Autowired
	FRUtil frUtil;
	
	@Autowired
	MobileClubCampaignDao mobileclubcampaigndao;
	
	@Autowired
	HandsetDao handsetdao;
	
	@Autowired
	UserSessionDao userSessionDao;
	
	@Autowired
	UmeRequest umeRequest;
	
	@Autowired
	UmeMobileClubUserDao umemobileclubuserdao;
	
	private static final Logger logger = LogManager.getLogger( FRSubscribe.class.getName());
    
    public FRSubscribe() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
		MobileClub club=UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		String subscriptionId=frUtil.readCookie(request,"subscriptionId");
		if(!subscriptionId.equals("")){
			SdcMobileClubUser clubUser=umemobileclubuserdao.getClubUserByMsisdn(subscriptionId, club.getUnique());
			System.out.println("param1: "+clubUser.getParam1());
			if(clubUser.getParam1().equals("null")||clubUser.getParam1().equals("0"))
				request.getRequestDispatcher("/FRCallback?error_code=37&subscription_id="+subscriptionId+"&welcome=true").forward(request, response);
			else
				request.getRequestDispatcher("/FRCallback?error_code=37&subscription_id="+subscriptionId).forward(request, response);;
			return;
		}
		int insertedUserSessionRows=0;
		String networkType="unknown";
		String campaignId=umeRequest.get("cid");
		String landingPage=umeRequest.get("landingPage");
		String sessionId=Misc.generateAnyxSessionId();
		String network=umeRequest.get("network");
		String operator="unknown";
		if(!network.equals("")&&network.contains("_")){
			networkType=network.split("_")[0];
			operator=network.split("_")[1];
		}
		
		MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
		System.out.println("campaign: "+cmpg);
		UserSession userSession=null;
		if(cmpg!=null){
			if (!"".equalsIgnoreCase(cmpg.getSrc()) && cmpg.getSrc().endsWith("RS")){
				Map<String,String> revshareParamMap=frUtil.readRevshareParamFromCookie(campaignId, request);
				userSession=new UserSession.UserSessionBuilder(sessionId, club.getUnique(), campaignId, landingPage, operator, networkType,new Date())
				.revshareParam(revshareParamMap.get("revParam1"),revshareParamMap.get("revParam2"),revshareParamMap.get("revParam3")).build();
				insertedUserSessionRows=userSessionDao.saveUserSession(userSession);
			}
			else if (!"".equalsIgnoreCase(cmpg.getSrc()) && cmpg.getSrc().endsWith("CPA")) {
				Map<String,String> cpaParamMap=frUtil.readCpaParamFromCookie(campaignId, request);		
				userSession=new UserSession.UserSessionBuilder(sessionId, club.getUnique(), campaignId, landingPage, operator, networkType,new Date())
				.cpaParam(cpaParamMap.get("cpaParam1"),cpaParamMap.get("cpaParam2"),cpaParamMap.get("cpaParam3"),cpaParamMap.get("cpaPubId")).build();
				insertedUserSessionRows=userSessionDao.saveUserSession(userSession);
			}else{
				userSession=new UserSession.UserSessionBuilder(sessionId, club.getUnique(), campaignId, landingPage, operator, networkType,new Date()).build();
				insertedUserSessionRows=userSessionDao.saveUserSession(userSession);
			}
		}else{
			userSession=new UserSession.UserSessionBuilder(sessionId, club.getUnique(), campaignId, landingPage, operator, networkType,new Date()).build();
			insertedUserSessionRows=userSessionDao.saveUserSession(userSession);
		}
		
		if(insertedUserSessionRows>0){
		Map<String,String> resutlMap=getToken(sessionId);
		String errorCode=resutlMap.get("error_code");
		if(errorCode.equals("0")){
			subscribeUser(resutlMap.get("token"),request,response);
		}else{
			logger.error("Error Getting Token, redirecting to Home Page");
			response.sendRedirect("http://"+dmn.getDefaultUrl());
            return;
		}
		}else{
			logger.error("Unable to Save UserSession in DB, redirecting to Home Page");
			response.sendRedirect("http://"+dmn.getDefaultUrl());
            return;
		}
	}
	
	public Map<String,String> getToken(String sessionId){
		
		Map<String,String> tokenParameterMap=new HashMap<String,String>();
		tokenParameterMap.put("login",FRConstant.LOGIN);
		tokenParameterMap.put("password", FRConstant.PASSWORD);
		tokenParameterMap.put("service_id", FRConstant.SERVICE_ID);
		tokenParameterMap.put("session_id", sessionId );
		Map<String,String> resutlMap=frUtil.makeRestCall(FRConstant.TOKEN_URL, tokenParameterMap);
		return resutlMap;
		
	}
	
	public void subscribeUser(String token,HttpServletRequest request,HttpServletResponse response){
		SdcRequest sdcRequest=new SdcRequest(request);
		String templateType=sdcRequest.get("templateType");
		String cid=sdcRequest.get("cid");
		String landingPage=sdcRequest.get("landingPage");
		String network=sdcRequest.get("network");
		System.out.println("Template Type: "+templateType);
		System.out.println("landingPage: "+landingPage);
		System.out.println("Network: "+network);
		if(!cid.equals("")){
			biLog(request,response);
		}
		
		try{
			if(templateType.equals("unIdentified")){
				String msisdn=sdcRequest.get("msisdn");
				if(msisdn.startsWith("0")){
					msisdn="+33"+msisdn.substring(1);
					msisdn=URLEncoder.encode(msisdn,"UTF-8");
				}
				logger.info("Subscription URL: {}&token={}&msisdn={}",FRConstant.SUBSCRIPTION_URL,token,msisdn);
				response.sendRedirect(FRConstant.SUBSCRIPTION_URL+"&token="+token+"&msisdn="+msisdn);
				return;
			}else{
				logger.info("Subscription URL: {}&token={}",FRConstant.SUBSCRIPTION_URL,token);
				response.sendRedirect(FRConstant.SUBSCRIPTION_URL+"&token="+token);
                return;
			}
		}catch(Exception e){
			logger.error("Error Subscribing User");
			e.printStackTrace();
		}

	}
	
	public void biLog(HttpServletRequest request,HttpServletResponse response){
		String operator="unknown";
		String networkType="unknown";
		UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
		String templateType=umeRequest.get("templateType").toUpperCase();
		String campaignId=umeRequest.get("cid");
		String network=umeRequest.get("network");
		String sessionId=umeRequest.get("sessionId");
		String landingPage=umeRequest.get("landingPage");
		Handset handset=handsetdao.getHandset(request);
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		String clubUnique=club.getUnique();
		if(templateType.equalsIgnoreCase("unIdentified")) templateType="MANUAL";
		if(!network.equals("")&&network.contains("_")){
			networkType=network.split("_")[0];
			operator=network.split("_")[1];
		}
		mobileclubcampaigndao.log("subscribe",landingPage,sessionId,sessionId,handset,dmn.getUnique(),campaignId,clubUnique,templateType,0,request,response,operator,networkType,"","");    	
	}

	
}
