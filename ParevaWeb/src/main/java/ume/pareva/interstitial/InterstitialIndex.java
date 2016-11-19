package ume.pareva.interstitial;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

import ume.pareva.cms.BannerAd;
import ume.pareva.cms.BannerAdDao;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.template.TemplateEngine;
import ume.pareva.userservice.LandingPage;
import ume.pareva.userservice.VideoList;

import com.google.common.base.Splitter;
import com.mitchellbosecke.pebble.PebbleEngine;


public class InterstitialIndex extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( InterstitialIndex.class.getName());

	@Autowired
	TemplateEngine templateEngine;

	@Autowired
	LandingPage landingpage;

	@Autowired
	UmeRequest umeRequest;

	@Autowired
	VideoList videolist;

	@Autowired
	HandsetDao handsetdao;

	@Autowired
	BannerAdDao banneraddao;


	public InterstitialIndex() {
		super();

	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String decryptedParameterString=Misc.decrypt(umeRequest.get("C3RC183"));
		Map<String,String> parameterMap=Splitter.on( "&" ).withKeyValueSeparator( '=' ).split( decryptedParameterString );
		String landingPage="";
		String region="";
		String clubName="";
		String classification="";
		List<Map<String,String>> videos=new ArrayList<Map<String,String>>();
		List<String> uniqueBanners=new ArrayList<String>();
		List<Map<String,String>> banners=new ArrayList<Map<String,String>>();
		UmeDomain dmn=umeRequest.getDomain();
		HttpSession session=request.getSession(false);
		session.setAttribute("doneMsisdnParse", null);
		String clubId=parameterMap.get("clubid");
		String campaignId=parameterMap.get("cid");
		String redirectBack=parameterMap.get("redirectback");
		Handset handset = handsetdao.getHandset(request);

		session.invalidate();
		int index = Integer.parseInt(umeRequest.get("ind", "0"));
		String lang = "en";
		if(!clubId.equals("")){
			MobileClub club=UmeTempCmsCache.mobileClubMap.get(clubId);
			String domain=club.getWapDomain();
			region=club.getRegion();
			clubName=club.getName();
			classification=club.getClassification();
			List<String> videoCategorySrvcList=getServiceUniqueList(club.getWapDomain(),"video");
			int numberOfElements=0;
			HashMap videoParameter=new HashMap();

			videoParameter=videolist.getVideoCategory(videoCategorySrvcList.get(0),club.getWapDomain(),handset);
			numberOfElements=Integer.parseInt(videoParameter.get("numberOfElements").toString());
			videos=videolist.getVideos(index,videoCategorySrvcList.get(0),club.getWapDomain(),handset,numberOfElements,lang);

			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			List<String> bannerSrvc=getServiceUniqueList(club.getWapDomain(),"banner");
			System.out.println("interstitial the size of the bannerSrvc is "+bannerSrvc.size()+"--- domain "+domain+" bannersrvc "+bannerSrvc.get(0).toString());

			if(!bannerSrvc.isEmpty()) {
				uniqueBanners=videolist.getBanner(bannerSrvc.get(0).toString(),domain,"topless","ZA");
				System.out.println("interstitial  BannerService Unique Banners "+uniqueBanners.size()+" -- domain "+domain+" --  bannersrvc "+bannerSrvc.get(0).toString());
			}


			if (uniqueBanners.size() > 0) {
				BannerAd bannerItem = null;
				String bannerType = "mobimage";
				int counter=0;
				for (int i = 0; i < uniqueBanners.size(); i++) {
					HashMap bannerMap = new HashMap();
					String bannerUnique = uniqueBanners.get(i);
					bannerItem = banneraddao.getBanner(bannerUnique);
					String bannerPath = "";
					String bannerLink = "";

					bannerPath = bannerItem.getImagePath(bannerType, 0, handset.getXhtmlProfile());
					bannerLink = bannerItem.getMobileLink();
					System.out.println("interstitial Banner Path: " + bannerPath+" ---  "+bannerItem.getUnique());
					System.out.println("interstitial Banner Link: " + bannerLink);


					if(bannerLink.contains(dmn.getDefaultUrl())) continue;

					System.out.println("interstitial AFTER Banner Path: " + bannerPath+" ---  "+bannerItem.getUnique());
					System.out.println("interstitial AFTER Banner Link: " + bannerLink);
					if (bannerLink.startsWith("http://")) {
						bannerLink = dmn.getDefPublicDir() + "/act_bannerlog.jsp?bunq=" + bannerItem.getUnique();
						bannerMap.put("bannerImage", bannerPath);
						bannerMap.put("bannerLink", bannerLink);


					}
					banners.add(bannerMap);
					System.out.println("interstitial Banner Number " + (i + 1));
					//					context.put("banner" + (counter + 1), bannerMap);
					counter++;
				}

			}
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		}

		if(landingPage==null || landingPage.equals("")){
			String landingPageNetwork="all";
			landingPage=evaluateLandingPage(dmn.getUnique(),campaignId,landingPageNetwork);
		}



		try{

			Map<String,Object> context=new HashMap<String,Object>();
			PebbleEngine interstitialEngine=templateEngine.getTemplateEngine(dmn.getUnique());
			PrintWriter writer=response.getWriter();
			context.put("videos",videos);
			context.put("banners",banners);
			context.put("contenturl","http://"+dmn.getContentUrl());
			context.put("cid",campaignId);
			System.out.println("interstitial redirect back url = "+redirectBack);
			context.put("redirectBack",redirectBack);
			context.put("clubId",clubId);
			context.put("region",region);
			context.put("clubName",clubName);
			context.put("classification",classification);
			//               context.put("interstitialredirected",interstitialredirected);
			logger.info("Template Name: "+landingPage);
			interstitialEngine.getTemplate(landingPage).evaluate(writer, context);

		}catch(Exception e){
			logger.error("interstitial Exception: Error Loading Landing Template");
			logger.error(e.getMessage());
			e.printStackTrace();
		}

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

	public List<String> getServiceUniqueList(String domain,String type){

		List<String[]> serviceList = (List<String[]>)UmeTempCmsCache.clientServices.get(domain);
		List<String> srvcList=new ArrayList<String>();
		if(serviceList!=null && !serviceList.isEmpty() && serviceList.size()>0) {
			for (int i = 0; i < serviceList.size(); ++i) {
				String[] serviceParameter = (String[])serviceList.get(i);
				String srvc = serviceParameter[1];
				String fName = serviceParameter[3];            

				if (fName.equals("promo_hot_video_category.jsp")&& type.equals("video")) {
					srvcList.add(srvc);
				}
				if (fName.equals("promo_banner.jsp")&& type.equals("banner")) {
					srvcList.add(srvc);
				}
			}
		}
		return srvcList;
	}
}
