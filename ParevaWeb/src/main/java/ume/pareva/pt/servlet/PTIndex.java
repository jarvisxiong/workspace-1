package ume.pareva.pt.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.aggregator.go4mobility.Go4MobilityService;
import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.pojo.CampaignHitCounter;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;

import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.GetSubscriptionPortfolioRequest;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.GetSubscriptionPortfolioResult;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.SubscriptionDetail;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.UserIdToken;
import com.mitchellbosecke.pebble.PebbleEngine;

public class PTIndex extends HttpServlet {
	private static final Logger logger = LogManager.getLogger( PTIndex.class.getName());
	private static final long serialVersionUID = 1L;
	private UserIdToken userIdToken;


	@Autowired
	TemplateEngine templateEngine;//

	@Autowired
	MobileClubCampaignDao mobileclubcampaigndao;

	@Autowired
	HandsetDao handsetdao;

	@Autowired
	InternetServiceProvider internetserviceprovider;

	@Autowired
	LandingPage landingpage;

	@Autowired
	CampaignHitCounterDao campaignhitcounterdao;

	@Autowired
	UmeRequest umeRequest;

	@Autowired 
	Go4MobilityService go4MobilityService;

	public PTIndex() {
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

	public void processRequest(HttpServletRequest request, HttpServletResponse response){
		HttpSession session=request.getSession();
		UmeDomain dmn=umeRequest.getDomain();
		String campaignId=umeRequest.get("cid");
		String subscriptionCancelled=umeRequest.get("subscription_cancelled");
		if(subscriptionCancelled.equals("")){
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
									session.setAttribute("subscriptionId",subscriptionDetail.getSubscriptionId());
									session.setAttribute("networkCode",subscriptionDetail.getMnc());
									redirectUserAndSaveSession(subscriptionStatus,request,response);
								}else{
									session.setAttribute("subscriptionStatus",subscriptionDetail.getStatus());
								}
							}
						}
					}
				}

			}
		}


		String landingPageNetwork="all";
                
                  // ====  Retrieving  LandingPage in this part =========================================
		String landingPage = (String) request.getAttribute("landingPage");
                 if(landingPage==null || landingPage.trim().isEmpty()) landingPage = (String) session.getAttribute("landingPage");

                 if(landingPage==null || landingPage.trim().isEmpty()) {
		 landingPage=evaluateLandingPage(dmn.getUnique(),campaignId,landingPageNetwork);
            }
                //====== ENDING LandingPage =================================================
                
                
                
                
		

		Date today=new Date();

		manageCampaignHit(today,dmn.getUnique(),campaignId,landingPage);
		renderTemplate(landingPage,request,response);

	}

	public String evaluateLandingPage(String domain,String campaignId,String network){
		String landingPage="";
		if(!campaignId.equals("")){
			landingPage=landingpage.initializeLandingPage(domain,campaignId,network);
		}else {
			landingPage=landingpage.initializeLandingPage(domain);
		}
		return landingPage;
	}

	public void manageCampaignHit(Date today,String domain,String campaignId,String landingPage){

		CampaignHitCounter campaignHitCounter=campaignhitcounterdao.HitRecordExistsOrNot(today,domain,campaignId,landingPage);
		if(campaignHitCounter==null){
			campaignHitCounter=new CampaignHitCounter();
			campaignHitCounter.setaUnique(Misc.generateUniqueId());
			campaignHitCounter.setaDomainUnique(domain);
			campaignHitCounter.setCampaignId(campaignId);
			campaignHitCounter.setLandingPage(landingPage);
			campaignHitCounter.setDate(today);
			campaignHitCounter.setHitCounter(1);
			campaignHitCounter.setSubscribeCounter(0);
			campaignhitcounterdao.saveCampaignHitCounter(campaignHitCounter);

		}else{
			campaignhitcounterdao.updateCampaignHitCounter(campaignHitCounter);
		}
	}

	public void biLog(String sessionId,String template,HttpServletRequest request,HttpServletResponse response){
            HttpSession ses = request.getSession();
		UmeDomain dmn=umeRequest.getDomain();
		String campaignId=umeRequest.get("cid");
		Handset handset=handsetdao.getHandset(request);
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		String clubUnique=club.getUnique();
		String myisp=internetserviceprovider.findIsp(request.getAttribute("ipAddress").toString());
                
                if(ses.getAttribute("bilogged")==null) {
		mobileclubcampaigndao.log("index",template,"","",handset,dmn.getUnique(),campaignId,clubUnique,"INDEX",0,request,response,myisp.toLowerCase());    	
                ses.setAttribute("bilogged", "true");
                }
	}



	public void renderTemplate(String template,HttpServletRequest request,HttpServletResponse response){
		UmeDomain dmn=umeRequest.getDomain();
		String campaignId=umeRequest.get("cid");
		String sessionId=Misc.generateAnyxSessionId();
		String isp=internetserviceprovider.findIsp(request.getAttribute("ipAddress").toString());
		if(!campaignId.equals("")){
			biLog(sessionId,template,request,response);
		}
		try{

			Map<String,Object> context=new HashMap<String,Object>();
			PebbleEngine ptEngine=templateEngine.getTemplateEngine(dmn.getUnique());
			PrintWriter writer=response.getWriter();
			if(isp.toLowerCase().contains("meo"))
				context.put("operator","meo");
			else if(isp.toLowerCase().contains("vodafone"))
				context.put("operator","vodafone");
			else if(isp.toLowerCase().contains("nos")||isp.toLowerCase().contains("tvcabo"))
				context.put("operator","nos");
			else
				context.put("operator","unknown");
			context.put("contenturl","http://"+dmn.getContentUrl());
			context.put("cid",campaignId);
			context.put("landingPage",template);
			context.put("sessionId",sessionId);
			logger.info("Template Name: "+template);
			ptEngine.getTemplate(template).evaluate(writer, context);

		}catch(Exception e){
			logger.error("Exception: Error Loading Landing Template");
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public void forwardUser(HttpServletRequest request,HttpServletResponse response){
		try {
			request.getServletContext().getRequestDispatcher("/wapxpt/videos.jsp").forward(request,response);
		} catch (ServletException | IOException e) {
			logger.error("Exception: Error Forwarding User To /wapxpt/videos.jsp");
			e.printStackTrace();
		}
	}

	public void redirectUserAndSaveSession(String subscriptionStatus,HttpServletRequest request,HttpServletResponse response){

		UmeDomain dmn=umeRequest.getDomain();
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		UmeClubDetails userClubDetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
		HttpSession session=request.getSession();
		session.setAttribute("subscriptionStatus","ALLOW_ACCESS");
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
			}else {
				//redirection code for competition service
			}
		}catch(Exception e){
			logger.error("Exception: Error Redirecting To Service Page {}",serviceUrl);
			e.printStackTrace();
		}

	}


}
