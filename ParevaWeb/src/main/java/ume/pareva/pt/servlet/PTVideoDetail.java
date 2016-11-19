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
import ume.pareva.cms.ItemImage;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.cms.VideoClip;
import ume.pareva.cms.VideoClipDao;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.pt.util.PTUtility;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.template.TemplateEngine;
import ume.pareva.userservice.VideoList;

import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.GetSubscriptionPortfolioRequest;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.GetSubscriptionPortfolioResult;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.SubscriptionDetail;
import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.UserIdToken;
import com.mitchellbosecke.pebble.PebbleEngine;

/**
 * Servlet implementation class PTVideoDetail
 */
public class PTVideoDetail extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( PTVideoDetail.class.getName());

	@Autowired
	VideoClipDao videoclipdao;

	@Autowired
	MobileClubDao mobileclubdao;

	@Autowired
	HandsetDao handsetdao;

	@Autowired
	UmeRequest umeRequest;

	@Autowired
	VideoList videolist;

	@Autowired 
	Go4MobilityService go4MobilityService;

	@Autowired
	TemplateEngine templateengine;

	@Autowired
	PTUtility ptUtility;

	public PTVideoDetail() {
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
		UmeDomain dmn=umeRequest.getDomain();
		String domain=dmn.getUnique();
		String unique = umeRequest.get("unq");
		//String cat = umeRequest.get("cat");
		
    	
		String ticket = Misc.hex8Decode(umeRequest.get("tk"));
		String cType = "";
		String tempname = "clip";
		String videoDetailImage="";
		String videoDetailTitle="";
		int index = Integer.parseInt(umeRequest.get("ind", "0"));
		List<Map<String,String>> videos=new ArrayList<Map<String,String>>();

		//	boolean status=userStatus.checkIfUserActiveAndNotSuspended(user, club);
		// 	if(status){
		Handset handset=handsetdao.getHandset(request);
		String ddir = dmn.getDefPublicDir();
		VideoClip item = videoclipdao.getItem(unique, UmeTempCmsCache.clientDomains.get(dmn.getUnique()));
		if (item==null) {
			try { 
				request.getServletContext().getRequestDispatcher("/" + System.getProperty("dir_" + dmn.getUnique() + "_pub") + "/error.jsp").forward(request,response); 
			}
			catch (Exception e) { 
				System.out.println("videodetail Exception "+e); 
			}
			return;    
		}

		ItemImage image = item.getItemImage("webthumb", 0, 0);

		if(image==null)
		{
			image = item.getItemImage("mobthumb", 0, 0);
		}
		if (image!=null) {
			videoDetailImage = image.getPath();
			videoDetailTitle=item.getTitle();
		}

		if (handset.get("playback_3gpp").equals("true") && item.getResourceMap().get("3gp")!=null) cType = "3gp";
		else if (handset.get("playback_mp4").equals("true") && item.getResourceMap().get("mp4")!=null) cType = "mp4";
		else if (handset.get("playback_mp4").equals("true") && item.getResourceMap().get("mpeg")!=null) cType = "mpeg";
		else if (handset.get("playback_flv").equals("true") && item.getResourceMap().get("flv")!=null) cType = "flv";
		else if (handset.get("playback_3g2").equals("true") && item.getResourceMap().get("3g2")!=null) cType = "3g2";
		else if (handset.get("playback_mov").equals("true") && item.getResourceMap().get("mov")!=null) cType = "mov";
		else if ((handset.get("playback_wmv").equals("7") || handset.get("playback_wmv").equals("8") || handset.get("playback_wmv").equals("9"))
				&& item.getResourceMap().get("wmv")!=null) cType = "wmv";


		String dllink = tempname + "." + cType + "?d=" + ddir + "&iunq=" + item.getUnique() + "&axud=1&itype=video&ctype=" + cType;



		if (!ticket.equals("")){ 
			dllink = ticket; 
		}

		String networkCode=umeRequest.getNetworkCode();
		logger.info("Netowrk Code: "+ networkCode);
		//String lang = umeRequest.getLanguage().getLanguageCode();
		String lang="en";
		List<String> videoCategorySrvcList=ptUtility.getServiceUniqueList(dmn.getUnique(),networkCode);
		int numberOfElements=0;
		HashMap videoParameter=new HashMap();

		videoParameter=videolist.getVideoCategory(videoCategorySrvcList.get(0),dmn.getUnique(),handset);
		numberOfElements=Integer.parseInt(videoParameter.get("numberOfElements").toString());

		videos=videolist.getVideos(index,videoCategorySrvcList.get(0),unique,domain,handset,numberOfElements,lang);
		videoParameter.put("dllink",dllink);
		videoParameter.put("videoDetailImage",videoDetailImage);
		videoParameter.put("videoDetailTitle",videoDetailTitle);
		videoParameter.put("networkCode",networkCode);
		renderTemplateWithPagination(index,videoParameter,videos,request,response);

	}

	public void renderTemplateWithPagination(int index,HashMap videoParameterMap,List<Map<String,String>> videos,
			HttpServletRequest request, HttpServletResponse response){
		HttpSession session=request.getSession();
		UmeDomain dmn=umeRequest.getDomain();
		String campaignId="";
		String landingPage="";
		if(session.getAttribute("cid")!=null)
			campaignId=session.getAttribute("cid").toString();
		if(session.getAttribute("landingPage")!=null)
			landingPage=session.getAttribute("landingPage").toString();	
		int numberOfElements=Integer.parseInt(videoParameterMap.get("numberOfElements").toString());
		int totalNumberOfVideos=Integer.parseInt(videoParameterMap.get("totalNumberOfVideos").toString());
		String dllink=videoParameterMap.get("dllink").toString();
		String videoDetailImage=videoParameterMap.get("videoDetailImage").toString();
		String videoDetailTitle=videoParameterMap.get("videoDetailTitle").toString();
		List<Map<String,String>> categoryList=(List<Map<String,String>>)videoParameterMap.get("categoryList");
		String unique = umeRequest.get("unq");
		String networkCode=(String)videoParameterMap.get("networkCode");

		List<HashMap<String,Object>> pageList=new ArrayList<HashMap<String,Object>>();


		int maxCount = 0;
		try{
			maxCount=numberOfElements;
			if(maxCount==0) maxCount=10;
		}catch(Exception e){}

		int pageCount = (totalNumberOfVideos-index)/maxCount;
		if((totalNumberOfVideos-index)%maxCount!=0){
			pageCount=pageCount+1;
		}
		int curPage = (index/maxCount)+1;
		int totalPage=totalNumberOfVideos/maxCount;
		if(totalNumberOfVideos%maxCount!=0){
			totalPage=totalPage+1;
		}
		for(int j=0;j<totalPage;j++){
			HashMap<String,Object> paginationMap=new HashMap<String,Object>();
			paginationMap.put("index",j*maxCount);
			paginationMap.put("pageNo",j+1);
			pageList.add(paginationMap);
		}
		try{
			PrintWriter writer = response.getWriter();
			Map<String,Object> context = new HashMap<String,Object>();
			/*	if(videoParameterMap.get("search")!=null){
	        		context.put("search",videoParameterMap.get("search").toString());
	        	}
	        	if(videoParameterMap.get("searchResult")!=null){
	        		context.put("searchResult",videoParameterMap.get("searchResult").toString());
	        	}
	        	if(videoParameterMap.get("videoType")!=null){
	        		context.put("videoType",videoParameterMap.get("videoType").toString());
	        	}

			 */	
			context.put("unique",unique);
			context.put("dllink", dllink);
			context.put("videoDetailImage", videoDetailImage);
			context.put("videoDetailTitle", videoDetailTitle);
			context.put("videos",videos);
			context.put("categories",categoryList);
			context.put("maxCount",maxCount);
			context.put("curPage",curPage);
			context.put("pageCount",pageCount);
			context.put("pageList",pageList);
			context.put("totalPage",totalPage);
			context.put("domainUrl",dmn.getDefaultUrl());
			context.put("contenturl","http://"+dmn.getContentUrl());
			context.put("contact","contact.jsp");
			//	context.put("landingPage",landingPage);
			//	context.put("cid",campaignId);
			context.put("categoryLink", "videoByCategory.jsp?cid="+campaignId+"&landingPage="+landingPage+"&cat=");
			//	context.put("action","subscribe.jsp?cid="+campaignId+"&landingPage="+landingPage+"&unq=");
			context.put("footerSubscribeLink","subscribe.jsp?cid="+campaignId+"&landingPage="+landingPage);
			context.put("footerCancelLink","#");
			context.put("terms","terms.jsp");
			if(networkCode.equals("01"))
    			context.put("vodaUser","true");
			if(session.getAttribute("subscriptionStatus")!=null){
				if(session.getAttribute("subscriptionStatus").toString().equals("INITIATED")){
					context.put("action","subscribe.jsp?cid="+campaignId+"&landingPage="+landingPage+"&unq=");
					context.put("categoryLink", "videoByCategory.jsp?cid="+campaignId+"&landingPage="+landingPage+"&cat=");
					context.put("footerSubscribeLink","subscribe.jsp?cid="+campaignId+"&landingPage="+landingPage);
					context.put("footerCancelLink","#");
					session.invalidate();
				}else if (session.getAttribute("subscriptionStatus").toString().equals("ALLOW_ACCESS")){
					context.put("categoryLink", "videoByCategory.jsp?cat=");
					context.put("action","videodetail.jsp?unq=");
					context.put("footerSubscribeLink","#");
					context.put("footerCancelLink","cancelSubscription.jsp");

				}
			}
			context.put("moreVideoAction","videodetail.jsp");

			PebbleEngine ptEngine=templateengine.getTemplateEngine(dmn.getUnique());
			ptEngine.getTemplate("video_detail").evaluate(writer, context);
		}catch(Exception e){
			logger.error("Exception Rendering Video Template");
		}

	}
}
