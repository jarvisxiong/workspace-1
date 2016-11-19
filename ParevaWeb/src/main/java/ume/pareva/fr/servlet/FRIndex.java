package ume.pareva.fr.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

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
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.fr.FRConstant;
import ume.pareva.fr.util.FRUtil;
import ume.pareva.pojo.CampaignHitCounter;
import ume.pareva.pojo.TemplatePojo;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.userservice.CpaRevShareServices;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;
import ume.pareva.util.NetworkMapping;

import com.mitchellbosecke.pebble.PebbleEngine;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.ThreadContext;

public class FRIndex extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Autowired
	FRUtil frUtil;
	
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
    UmeTempCache umesdc;
        
    @Autowired
    CpaRevShareServices cparevshare;
    
    @Autowired
    NetworkMapping networkMapping;
	
    @Autowired
    UmeRequest umeRequest;
    
	private static final Logger logger = LogManager.getLogger( FRIndex.class.getName());
    
    public FRIndex() {
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
            
            ThreadContext.put("ROUTINGKEY", "FR");
        ThreadContext.put("EXTRA","");
	
            HttpSession session = request.getSession();
		String network="unknown";
		String landingPageNetwork="all";
		UmeDomain dmn=umeRequest.getDomain();
		String campaignId=umeRequest.get("cid");
		Map<String,String> resutlMap=resolveNetwork(request);
		String networkType=resutlMap.get("network");
		if(!networkType.equals("unknown"))
			network=networkMapping.getFrNetworkMap().get(resutlMap.get("ope_id"));
		String errorCode=resutlMap.get("error_code");
                
                  // ====  Retrieving  LandingPage in this part =========================================
		String landingPage = (String) request.getAttribute("landingPage");
                 if(landingPage==null || landingPage.trim().isEmpty()) landingPage = (String) session.getAttribute("landingPage");

                 if(landingPage==null || landingPage.trim().isEmpty()) {
		 landingPage=evaluateLandingPage(dmn.getUnique(),campaignId,landingPageNetwork);
            }
                //====== ENDING LandingPage =================================================
                
                
		
		manageCampaignHit(dmn.getUnique(),campaignId,landingPage);
		if(errorCode.equals("0")){
			renderTemplate(landingPage,network,networkType,request,response);
		}else{
			logger.error("Error Resolving Network For IP Address: "+request.getAttribute("ipAddress").toString());
			logger.error(resutlMap.get("error_desc"));
			try {
				response.sendRedirect("http://"+dmn.getDefaultUrl());
				return;
			} catch (IOException e) {
				logger.error("Error Redirecting To Home Page");
				e.printStackTrace();
			}
		}
	}
	
	
	public Map<String,String> resolveNetwork(HttpServletRequest request){
		
		Map<String,String> parameterMap=new HashMap<String,String>();
		parameterMap.put("login",FRConstant.LOGIN);
		parameterMap.put("password",FRConstant.PASSWORD);
		parameterMap.put("ip_address",request.getAttribute("ipAddress").toString());
		Map<String,String> resutlMap=frUtil.makeRestCall(FRConstant.NETWORK_INFO_URL, parameterMap);
		return resutlMap;
		
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
	
	public void manageCampaignHit(String domain,String campaignId,String landingPage){
		Date today=new Date();
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
	
	public void biLog(String sessionId,String template,HttpServletRequest request,HttpServletResponse response,MobileClubCampaign cmpg){
            HttpSession ses = request.getSession();	
            UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
		String campaignId=umeRequest.get("cid");
		Handset handset=handsetdao.getHandset(request);
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		String clubUnique=club.getUnique();
		String myisp=internetserviceprovider.findIsp(request.getAttribute("ipAddress").toString());
                    if(request.getParameter("loghits")==null && ses.getAttribute("bilogged")==null) {
		mobileclubcampaigndao.log("index",template,sessionId,sessionId,handset,dmn.getUnique(),campaignId,clubUnique,"INDEX",0,request,response,myisp.toLowerCase());   
                    ses.setAttribute("bilogged", "true");
                    }

                
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////             
                         if(cmpg!=null&&!cmpg.getaInterstitialLandingPage().equals("")&& cmpg.getaInterstitialLandingPage()!=null && request.getParameter("loghits")==null){                           
                                              
                          String interstitialLandingPage=cmpg.getaInterstitialLandingPage();
                          String interstitialRedirected=request.getParameter("interstitialredirected"); //(String)session.getAttribute("interstitialRedirected"); //
                          String redirectBack="http://"+dmn.getDefaultUrl();
                          if(interstitialRedirected==null){
                           //session.setAttribute("interstitialRedirected",null);
                           String interstitialDomainUnique=dmn.getPartnerDomain();
                           UmeDomain interstitialDomain=umesdc.getDomainMap().get(interstitialDomainUnique);
                           //redirectBack="http://"+dmn.getDefaultUrl()+"/?cid="+cid+"&clubid="+clubId+"&interstitiallandingpage="+interstitialLandingPage+"&interstitialredirected=1&loghits=no";
                           System.out.println("interstitial FR redirectback url = "+redirectBack);
                           String interstitialRedirectUrl="http://"+interstitialDomain.getDefaultUrl();
                           
                                                  
                           
                           
                           String params="redirectback="+redirectBack+"&cid="+campaignId+"&clubid="+club.getUnique()+"&interstitiallandingpage="+interstitialLandingPage+"&interstitialredirected=1";
                           interstitialRedirectUrl+="/?C3RC183="+Misc.encrypt(params);
                           System.out.println("interstitialFR index params "+params);
                           
                        try {
                            response.sendRedirect(interstitialRedirectUrl);
                        } catch (IOException ex) {
                            java.util.logging.Logger.getLogger(FRIndex.class.getName()).log(Level.SEVERE, null, ex);
                        }
                           return;
                          }
                         }
                         
                         
                         
                         
                         
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 
	}
	
	public void renderTemplate(String template,String network,String networkType,HttpServletRequest request,HttpServletResponse response){
		
		UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
		TemplatePojo templatePojo=umesdc.getTemplateMap().get(dmn.getUnique());
		String campaignId=umeRequest.get("cid");
		String sessionId="";
		if(!campaignId.equals("")){
			MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
			if(cmpg!=null){ 
				cparevshare.cparevshareLog(request, cmpg,"","frcpa", request.getSession(),"entry");
			}
			biLog(sessionId,template,request,response,cmpg);
		}
		try{
			
			Map<String,Object> context=new HashMap<String,Object>();
			PebbleEngine frEngine=templateEngine.getTemplateEngine(dmn.getUnique());
			PrintWriter writer=response.getWriter();
			context.put("service_id", FRConstant.SERVICE_ID);
			context.put("contenturl","http://"+dmn.getContentUrl());
			context.put("cid",campaignId);
			context.put("landingPage",template);
			context.put("sessionId",sessionId);
			context.put("contact","contact.jsp");
    		context.put("terms","terms.jsp");
    		context.put("account","account.jsp");
    		context.put("network",networkType+"_"+network);
    		context.put("login","login.jsp");
    		if(networkType.equals("unknown")){
				context.put("networkType","unIdentified");
				context.put("msisdnexist","false");
			}else{
				context.put("networkType","identified");
				context.put("msisdnexist","true");
			}
    		System.out.println("Template Folder: "+templatePojo.getTemplateFolder());
			frEngine.getTemplate(template).evaluate(writer, context);
		
		}catch(Exception e){
			logger.error("Error Loading Landing Template");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}
	

}
