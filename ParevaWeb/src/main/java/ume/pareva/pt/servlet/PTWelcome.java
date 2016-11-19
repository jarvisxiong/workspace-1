package ume.pareva.pt.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.template.TemplateEngine;

import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.GetSubscriptionPortfolioRequest;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.GetSubscriptionPortfolioResult;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.SubscriptionDetail;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.UserIdToken;
import com.mitchellbosecke.pebble.PebbleEngine;


public class PTWelcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( PTWelcome.class.getName());
	private UserIdToken userIdToken;


	@Autowired
	TemplateEngine templateEngine;

	@Autowired
	UmeRequest umeRequest;

	@Autowired 
	Go4MobilityService go4MobilityService;

	public PTWelcome() {
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

	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		HttpSession session=request.getSession();
		String networkCode="";
		if(session.getAttribute("networkCode")!=null){
			networkCode=session.getAttribute("networkCode").toString();
			if(networkCode.equals("01")){
				request.setAttribute("price", "3,99");
			}else if(networkCode.equals("03")){
				request.setAttribute("price", "3,99");
			}else if(networkCode.equals("02")||networkCode.equals("06")||networkCode.equals("80")){
				request.setAttribute("price", "3,49");
			}
		}else{
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
								logger.info("Subscription Status: {} for Subscription Id: {}",subscriptionStatus,subscriptionId);
								if(subscriptionStatus.equals("ACTIVE")){
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
		renderTemplate("welcome",request,response);

	}

	public void renderTemplate(String template,HttpServletRequest request,HttpServletResponse response){
		UmeDomain dmn=umeRequest.getDomain();
		String price="";
		if(request.getAttribute("price")!=null){
			price=request.getAttribute("price").toString();
		}
		try{

			Map<String,Object> context=new HashMap<String,Object>();
			PebbleEngine ptEngine=templateEngine.getTemplateEngine(dmn.getUnique());
			PrintWriter writer=response.getWriter();
			context.put("contenturl","http://"+dmn.getContentUrl());
			context.put("cancelSubscription","http://"+dmn.getDefaultUrl()+"/cancelSubscription.jsp");
			context.put("contentPortal","videos.jsp" );
			context.put("terms","terms.jsp");
			context.put("price",price);
			ptEngine.getTemplate(template).evaluate(writer, context);

		}catch(Exception e){
			logger.error("Exception: Error Loading Landing Template in renderTemplate method of PTIndex Servlet");
			logger.error(e.getMessage());
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
