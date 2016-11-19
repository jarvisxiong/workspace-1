package ume.pareva.pt.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.aggregator.go4mobility.Go4MobilityService;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubBillingTry;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobileBillingDao;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.ActiveSession;
import ume.pareva.pt.util.ActiveUser;
import ume.pareva.pt.util.PTConstants;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.template.TemplateEngine;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.util.NetworkMapping;

import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.GetSubscriptionPortfolioRequest;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.GetSubscriptionPortfolioResult;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.SubscriptionDetail;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.UserIdToken;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.WapBillingRequest;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.WapBillingResult;
import com.mitchellbosecke.pebble.PebbleEngine;


public class PTSubscribe extends HttpServlet {

	private static final Logger logger = LogManager.getLogger( PTSubscribe.class.getName());

	private static final long serialVersionUID = 1L;
	private UserIdToken userIdToken;

	@Autowired 
	Go4MobilityService go4MobilityService;

	@Autowired 
	UmeRequest umeRequest;

	@Autowired
	PTConstants ptConstants;
	
	@Autowired
	MobileClubCampaignDao mobileclubcampaigndao;
	
	@Autowired
	HandsetDao handsetdao;
	
	@Autowired
	SubscriptionCreation subscriptioncreation;
	
	@Autowired
	UmeMobileClubUserDao umemobileclubuserdao;
	
	@Autowired
	UmeUserDao umeuserdao;
	
	@Autowired
	InternetServiceProvider internetserviceprovider;
	
	@Autowired
    NetworkMapping networkMapping;
	
	@Autowired
	CpaLoggerDao cpaloggerdao;
	
	@Autowired
	MobileBillingDao mobilebillingdao;
	
	@Autowired
	TemplateEngine templateEngine;
	
	public PTSubscribe() {
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

	protected void processRequest(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		HttpSession session=request.getSession();
		WapBillingResult wapBillingResult=null;
		UmeDomain dmn=umeRequest.getDomain();
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		String campaignId=umeRequest.get("cid");
		if(campaignId.equals("")){
			if(session.getAttribute("cid")!=null)
				campaignId=session.getAttribute("cid").toString();
		}
		String landingPage=umeRequest.get("landingPage");
		session.setAttribute("cid",campaignId);
		session.setAttribute("landingPage",landingPage);
		String sessionId=umeRequest.get("sessionId");
		String videoUnique=umeRequest.get("unq");
		String queryString="";
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		String subscriptionId=umeRequest.readCookie("subscriptionId");
		if(!subscriptionId.equals("")){
			logger.info("Analyzing Subscription Cookie");
			userIdToken=go4MobilityService.getUserIdToken(Go4MobilityService.login, Go4MobilityService.pw);
			GetSubscriptionPortfolioRequest getSubscriptionPortfolioRequest=go4MobilityService.getSubscriptionPortfolioRequest(userIdToken, Go4MobilityService.serviceName);
			GetSubscriptionPortfolioResult subscriptionPortfolioResult=go4MobilityService.getSubscriptionPortfolio(getSubscriptionPortfolioRequest);
			if(subscriptionPortfolioResult!=null){
				if(subscriptionPortfolioResult.getResultCode()==0){
					List<SubscriptionDetail> subscriptionDetailList=new ArrayList<SubscriptionDetail>();
					if(subscriptionPortfolioResult.getSubscriptionDetail().length>0){
						try{
							subscriptionDetailList=(Arrays.asList(subscriptionPortfolioResult.getSubscriptionDetail()));
						}catch(Exception e){
							logger.error("Exception: Error Converting Subscripton Detail Array to ArrayList");	
						}
					}
					for(SubscriptionDetail subscriptionDetail: subscriptionDetailList){
						if(subscriptionDetail.getSubscriptionId().equals(subscriptionId)){
							String subscriptionStatus=subscriptionDetail.getStatus();
							Calendar deactivationDate=subscriptionDetail.getDeactivationDate();
							logger.info("Subscription Status: {} and Deactivation Date: {} for Subscription Id: {}",subscriptionStatus,deactivationDate.getTime(),subscriptionId);
							if(subscriptionStatus.equals("ACTIVE")
									||deactivationDate.getTime().after(new Date())
									||deactivationDate.getTime().equals(new Date())){
								session.setAttribute("subscriptionStatus","ALLOW_ACCESS");
								session.setAttribute("subscriptionId",subscriptionDetail.getSubscriptionId());
								session.setAttribute("networkCode",subscriptionDetail.getMnc());
								UmeClubDetails userClubDetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
								String serviceUrl="";
								try{
									if(userClubDetails.getServiceType().equals("Content")){
										if(userClubDetails.getServedBy().equals("UME")){
											serviceUrl="http://"+dmn.getDefaultUrl()+"/videos.jsp";
										}else{
											serviceUrl="http://"+dmn.getRedirectUrl();
										}
										logger.info("Active Subscription Found, Redirecting User to Service Page {}",serviceUrl);
										response.sendRedirect(serviceUrl);
										return;
									}else {
										//redirection code for competition service
									}
								}catch(Exception e){
									logger.error("Exception: Error Redirecting To Service Page {}",serviceUrl);
									e.printStackTrace();
								}
							}else{
								session.setAttribute("subscriptionStatus",subscriptionDetail.getStatus());
							}
						}
					}
				}
			}

		}
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		
		if(session.getAttribute("subscriptionStatus")==null || session.getAttribute("subscriptionStatus").toString().equals("DEACTIVE")
				|| session.getAttribute("subscriptionStatus").toString().equals("SUSPENSE")){
			session.setAttribute("subscriptionStatus","INITIATED");
			queryString="cid="+campaignId+"&landingPage="+landingPage+"&sessionId="+sessionId;
			if(!videoUnique.equals(""))
				queryString=queryString+"&unq="+videoUnique;
		}else if (session.getAttribute("subscriptionStatus").toString().equals("INITIATED")){
			queryString=retrieveReqeustParameters(request);		
		}
		String clientIpAddress=request.getAttribute("ipAddress").toString();
		//String clientIpAddress="87.103.120.242";
		String returnUrl="http://"+dmn.getDefaultUrl()+"/subscribe.jsp";
		userIdToken=go4MobilityService.getUserIdToken(Go4MobilityService.login, Go4MobilityService.pw);
		WapBillingRequest wapBillingRequest=go4MobilityService.getWapBillingRequest(userIdToken,Go4MobilityService.serviceName,returnUrl,queryString,clientIpAddress);
		wapBillingResult=go4MobilityService.wapBilling(wapBillingRequest);
		if(wapBillingResult!=null){
			if(wapBillingResult.getResultCode()==0){
				if(wapBillingResult.getAction()==0){
					saveCookie(wapBillingResult,response);
					String subscriptonResponse=createUser(wapBillingResult,request);
					logger.info("Forwarding User to Home Page");
					System.out.println("Network Code: "+wapBillingResult.getMnc());
					if(!campaignId.equals("") && subscriptonResponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY")){
						logger.info("PTINFO: Subscription Record Created");
						MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
						if (cmpg != null && cmpg.getSrc().trim().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("subscription")) {
							logger.info("PTINFO: Cpa log created");
							int insertedRows = cpaloggerdao.insertIntoCpaLogging(wapBillingResult.getSubscriptionId(), campaignId, club.getUnique(), 10, networkMapping.getPtNetworkMap().get(wapBillingResult.getMnc()), cmpg.getSrc());
						}

						if (cmpg != null && cmpg.getSrc().trim().endsWith("RS")) {
							//String enMsisdn = MiscCr.encrypt(msisdn);
							int insertedRows =  cpaloggerdao.insertIntoRevShareLogging(0,cmpg.getPayoutCurrency(), wapBillingResult.getSubscriptionId(), wapBillingResult.getMsisdn(), campaignId, club.getUnique(), 0, networkMapping.getPtNetworkMap().get(wapBillingResult.getMnc()), cmpg.getSrc(), 0);
						}
						
						
					
					}
					if(!campaignId.equals("")&&(subscriptonResponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY") || subscriptonResponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY"))){
						biLog(wapBillingResult,request,response);
					}
					
					//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					logger.info("Referral: "+request.getHeader("referer"));
				    logger.info("Session ID: "+session.getId());
				    int sessionCounter=0;
			        ActiveSession activeSession=ActiveSession.getActiveSession();
			        if(activeSession.getActiveSessionPerUser().get(wapBillingResult.getSubscriptionId())!=null)
			        	sessionCounter=activeSession.getActiveSessionPerUser().get(wapBillingResult.getSubscriptionId());
			        logger.info("Number of Active Session: "+sessionCounter);
			        boolean oldSession=false;
			        for(Map.Entry<String, ActiveUser> activeUserSession : activeSession.getActiveUserSession().entrySet()){
			        	if(activeUserSession.getKey().equals(session.getId())){
			        		oldSession=true;
			        	}
			        }
			        
			        if(oldSession){
			        	logger.info("Old Session");
			        }else{
			        	if(sessionCounter==5){
			        		logger.info("no more sesssions allowed");
			        		return;
			        	}else{
			        		if(session.getAttribute("activeSession")==null){
			        			session.setAttribute("activeSession",new ActiveUser(wapBillingResult.getSubscriptionId(),session));
			        		}
			        	}
			        }
			        logger.info("Session Counter: "+activeSession.getActiveSessionPerUser().get(wapBillingResult.getSubscriptionId()));
					/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					String subscriptionStatus="ALLOW_ACCESS";
					session.setAttribute("subscriptionId", wapBillingResult.getSubscriptionId());
					session.setAttribute("networkCode",wapBillingResult.getMnc());
					logger.info("User Subscribed Successfully, Result Code: {} Action Code: {} SubscriptionId: {}",wapBillingResult.getResultCode(),wapBillingResult.getAction(),wapBillingResult.getSubscriptionId());
					redirectUserAndSaveSession(subscriptionStatus,subscriptonResponse,request,response);
					
				}else if(wapBillingResult.getAction()==1){
					logger.info("Redirecting User to {} to Collect More Informaton",wapBillingResult.getRedirectUrl());
					logger.info("CampaignID = {}",campaignId);
				//	if(!campaignId.equals("")){
						if(session.getAttribute("redirectionCounter")!=null){
							int redirectionCounter=Integer.parseInt(session.getAttribute("redirectionCounter").toString());
							redirectionCounter=redirectionCounter+1;
							session.setAttribute("redirectionCounter",redirectionCounter);
						}else{
							session.setAttribute("redirectionCounter",1);
						}
						biLog(wapBillingResult,request,response);
				//	}
					response.sendRedirect(wapBillingResult.getRedirectUrl());
				}else{
					logger.error("Exception: Error Subscribing User with Action Code {} ",ptConstants.getActionCodeMap().get(wapBillingResult.getAction()));
					redirectToHome(request,response);
				}
			}else if(wapBillingResult.getResultCode()==120||wapBillingResult.getResultCode()==123||wapBillingResult.getResultCode()==124) {
				logger.error("Exception: Error Subscribing User with Error Code {}",ptConstants.getResultCodeMap().get(wapBillingResult.getResultCode()));
				renderTemplate("billing_general_error",request,response);
			}else if(wapBillingResult.getResultCode()==150){
				logger.error("Exception: Error Subscribing User with Error Code {}",ptConstants.getResultCodeMap().get(wapBillingResult.getResultCode()));
				renderTemplate("subscription_suspended_error",request,response);
			}
		}else{
			redirectToHome(request,response);
		}

	}

	public void redirectToHome(HttpServletRequest request,HttpServletResponse response){
		UmeDomain dmn=umeRequest.getDomain();
		try {
			response.sendRedirect("http://"+dmn.getDefaultUrl());
		} catch (IOException e) {
			logger.error("Error Redirecting to Home Page");
			e.printStackTrace();
		}

	}
	
	public void biLog(WapBillingResult wapBillingResult,HttpServletRequest request,HttpServletResponse response){
		HttpSession session=request.getSession();
		UmeDomain dmn=umeRequest.getDomain();
		String campaignId=umeRequest.get("cid");
		String landingPage=umeRequest.get("landingPage");
	//	String sessionId=umeRequest.get("sessionId");
		Handset handset=handsetdao.getHandset(request);
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		String clubUnique=club.getUnique();
		if(wapBillingResult.getAction()==0){
			if(session.getAttribute("redirectionCounter")==null){
				mobileclubcampaigndao.log("subscribe",landingPage,wapBillingResult.getMsisdn(),wapBillingResult.getSubscriptionId(),handset,dmn.getUnique(),campaignId,clubUnique,
					"IDENTIFIED",0,request,response,networkMapping.getPtNetworkMap().get(wapBillingResult.getMnc()),"","","");    
			}	
			mobileclubcampaigndao.log("subscribe",landingPage,wapBillingResult.getMsisdn(),wapBillingResult.getSubscriptionId(),handset,dmn.getUnique(),campaignId,clubUnique,
					"SUBSCRIBED",0,request,response,networkMapping.getPtNetworkMap().get(wapBillingResult.getMnc()),"","","");    
		}else if(wapBillingResult.getAction()==1){
			logger.debug("Inside BiLog");
			if(session.getAttribute("redirectionCounter")!=null){
				int redirectionCounter=Integer.parseInt(session.getAttribute("redirectionCounter").toString());
				logger.debug("Redirection Counter Value: "+redirectionCounter);
				if(redirectionCounter==1){
					String myisp=internetserviceprovider.findIsp(request.getAttribute("ipAddress").toString());
					mobileclubcampaigndao.log("subscribe",landingPage,"","",handset,dmn.getUnique(),campaignId,clubUnique,"MANUAL",0,request,response,myisp.toLowerCase());    	
				}
			}
		}
	}
	
	public String createUser(WapBillingResult wapBillingResult, HttpServletRequest request){
		String network="";
		if(wapBillingResult.getMnc()!=null){
			network=networkMapping.getPtNetworkMap().get(wapBillingResult.getMnc());
			if(network==null){
				network="unknown";
			}
		}
		UmeDomain dmn=umeRequest.getDomain();
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		String campaignId=umeRequest.get("cid");
		String landingPage=umeRequest.get("landingPage");
		String subscriptionId=wapBillingResult.getSubscriptionId();
		return subscriptioncreation.checkSubscription(subscriptionId,club,campaignId,7,network,landingPage);
		
		
	}
	
	public void saveCookie(WapBillingResult wapBillingResult,HttpServletResponse response){
		String subscriptionId=wapBillingResult.getSubscriptionId();
		Cookie cookie = new Cookie("subscriptionId",subscriptionId);
		cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
		response.addCookie(cookie);
	}
	
	public void insertBillingTryForUser(SdcMobileClubUser clubUser,MobileClub club){
		MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
		mobileClubBillingTry.setTransactionId("");
		mobileClubBillingTry.setResponseCode("003");
		mobileClubBillingTry.setCreated(new Date());
		mobileClubBillingTry.setRegionCode(club.getRegion());
		mobileClubBillingTry.setClubUnique(club.getUnique());
		mobileClubBillingTry.setCampaign(clubUser.getCampaign());
		mobilebillingdao.insertBillingTry(mobileClubBillingTry);
	}
	
	public void redirectUserAndSaveSession(String subscriptionStatus,String subscriptionResponse,HttpServletRequest request,HttpServletResponse response){
		UmeDomain dmn=umeRequest.getDomain();
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		UmeClubDetails userClubDetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
		String videoUnique=umeRequest.get("unq");
		HttpSession session=request.getSession();
		String serviceUrl="";
		try{
			if(userClubDetails.getServiceType().equals("Content")){
				
				if(userClubDetails.getServedBy().equals("UME")){
					session.setAttribute("subscriptionStatus",subscriptionStatus);
					if(subscriptionResponse.equals("SUBSCRIPTION RECORD ALREADY EXISTS")){
						if(!videoUnique.equals(""))
							serviceUrl="http://"+dmn.getDefaultUrl()+"/videodetail.jsp?unq="+videoUnique;
						else{
							serviceUrl="http://"+dmn.getDefaultUrl()+"/videos.jsp";
						}
					}else{
						if(!videoUnique.equals(""))
							serviceUrl="http://"+dmn.getDefaultUrl()+"/videodetail.jsp?unq="+videoUnique;
						else{
							serviceUrl="http://"+dmn.getDefaultUrl()+"/welcome.jsp";
						}
					}
				}else{
					serviceUrl="http://"+dmn.getRedirectUrl();
				}
				response.sendRedirect(serviceUrl);
			}else {
				//redirection code for competition service
			}
		}catch(Exception e){
			logger.error("Exception: Error Redirecting To Service Page {}",serviceUrl);
			e.printStackTrace();
		}

	}
	
	public String retrieveReqeustParameters(HttpServletRequest request){
		Enumeration<String> parameterNames = request.getParameterNames();
		String queryString="";
		while (parameterNames.hasMoreElements()) {
			String paramName = parameterNames.nextElement();
			String paramValue=umeRequest.get(paramName);
		
			try {
				queryString=queryString+paramName+"="+URLEncoder.encode(paramValue,"UTF-8")+"&";
			} catch (UnsupportedEncodingException e) {
				logger.error("Exception: Error Encoding Parameter {}",paramValue);
				e.printStackTrace();
			}
		}
		return queryString.substring(0,queryString.length()-1);
	}

	public void renderTemplate(String template,HttpServletRequest request, HttpServletResponse response){
		SdcRequest sdcRequest=new SdcRequest(request);
		UmeDomain dmn=sdcRequest.getDomain();
		try{
			Map<String,Object> context=new HashMap<String,Object>();
			PebbleEngine frEngine=templateEngine.getTemplateEngine(dmn.getUnique());
			PrintWriter writer=response.getWriter();
			context.put("landingPage","http://"+dmn.getDefaultUrl());
			if(template.equals("subscription_suspended_error")){
				context.put("cancelSubscription","cancelSubscription.jsp");
			}
			frEngine.getTemplate(template).evaluate(writer,context);
		} catch (Exception e) {
			logger.error("Error Loading "+template+" Template");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
