package ume.pareva.ke.servlet;

import java.io.IOException;
import java.io.PrintWriter;
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

import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.SdcRequest;
import ume.pareva.ke.UserStatus;
import ume.pareva.pojo.CampaignHitCounter;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.template.TemplateEngine;
import ume.pareva.userservice.CpaRevShareServices;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;

import com.mitchellbosecke.pebble.PebbleEngine;

public class KEIndex extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( KEIndex.class.getName());
	
	
	@Autowired
	TemplateEngine templateEngine;
	
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
    CpaRevShareServices cparevshare;
	
    @Autowired
	UserStatus userStatus;
	
    public KEIndex() {
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
	
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		SdcRequest sdcRequest=new SdcRequest(request);
		UmeDomain dmn=sdcRequest.getDomain();
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
    	UmeUser user=sdcRequest.getUser();
    	boolean status=userStatus.checkIfUserActiveAndNotSuspended(user,club);
    	if(status){
    		request.getServletContext().getRequestDispatcher("/KEVideo").forward(request,response);
    		return;
    	}
    	String campaignId=sdcRequest.get("cid");
		String landingPageNetwork="all";
		String landingPage=evaluateLandingPage(dmn.getUnique(),campaignId,landingPageNetwork);
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
	
	public void biLog(String template,HttpServletRequest request,HttpServletResponse response){
		UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
		SdcRequest sdcRequest=new SdcRequest(request);
		String campaignId=sdcRequest.get("cid");
		Handset handset=handsetdao.getHandset(request);
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		String clubUnique=club.getUnique();
		String myisp=internetserviceprovider.findIsp(request.getAttribute("ipAddress").toString());
		mobileclubcampaigndao.log("index",template,"","",handset,dmn.getUnique(),campaignId,clubUnique,"INDEX",0,request,response,myisp.toLowerCase());    	
	}
	
	public void renderTemplate(String template,HttpServletRequest request,HttpServletResponse response){
		
		UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
		SdcRequest sdcRequest=new SdcRequest(request);
		String campaignId=sdcRequest.get("cid");
		String wrongMsisdn=sdcRequest.get("wrongMsisdn");
                MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		if(!campaignId.equals("")){
			MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
			if(cmpg!=null){ 
				cparevshare.cparevshareLog(request, cmpg,"","kecpa", request.getSession(),"entry");
			}
			biLog(template,request,response);
		}
		try{
			
			Map<String,Object> context=new HashMap<String,Object>();
			PebbleEngine keEngine=templateEngine.getTemplateEngine(dmn.getUnique());
			PrintWriter writer=response.getWriter();
			if(wrongMsisdn.equals("true")){
				context.put("invalidnumber","Please Enter Valid Number Starting From 07");
			}
			context.put("contenturl","http://"+dmn.getContentUrl());
			context.put("campaignId",campaignId);
			context.put("landingPage",template);
			context.put("sendAction","subscribe.jsp");
                        context.put("clubprice",club.getPrice());
			
    		keEngine.getTemplate(template).evaluate(writer, context);
		
		}catch(Exception e){
			logger.error("Error Loading Landing Template");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}
}
