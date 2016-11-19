package ume.pareva.pt.servlet;

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

import ume.pareva.aggregator.go4mobility.Go4MobilityService;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.PTUtility;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.template.TemplateEngine;
import ume.pareva.userservice.VideoList;

import com.go4mobility.www.ws.skysms.wapBilling.v1_0.service.UserIdToken;
import com.mitchellbosecke.pebble.PebbleEngine;

public class PTVideo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( PTVideo.class.getName());
	   
	@Autowired
	VideoList videolist;
	
	@Autowired
	HandsetDao handsetdao;
	
	@Autowired
	TemplateEngine templateengine;
	
	@Autowired
	UmeRequest umeRequest;
	
	@Autowired 
	Go4MobilityService go4MobilityService;
	
	@Autowired
	PTUtility ptUtility;

	public PTVideo() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
        config.getServletContext());
        }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processVideos(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processVideos(request, response);
	}
	
    protected void processVideos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//HttpSession session=request.getSession();
    	List<Map<String,String>> videos=new ArrayList<Map<String,String>>();
    	UmeDomain dmn = umeRequest.getDomain();
    	String domain=dmn.getUnique();
    	Handset handset = handsetdao.getHandset(request);
    	int index = Integer.parseInt(umeRequest.get("ind", "0"));
    	String networkCode=umeRequest.getNetworkCode();
    	
    	logger.info("Netowrk Code: "+ networkCode);
    	
    	//String lang = umeRequest.getLanguage().getLanguageCode();
    	String lang="en";
    	String search = umeRequest.get("search").trim().toLowerCase();
    	String videoType=umeRequest.get("videoType");
    	List<String> videoCategorySrvcList=ptUtility.getServiceUniqueList(domain,networkCode);
    	int numberOfElements=0;
    	HashMap videoParameter=new HashMap();
    	
    	videoParameter=videolist.getVideoCategory(videoCategorySrvcList.get(0),domain,handset);
    	numberOfElements=Integer.parseInt(videoParameter.get("numberOfElements").toString());
    	
    	if (!search.equals("")){
    		videoParameter.put("search",search);
    		videos=videolist.searchVideos(videoCategorySrvcList.get(0),search,index,domain,numberOfElements,lang);
    		if(videos.isEmpty()){
    			videoParameter.put("searchResult","blank");
    			
    		}else{
    			Map<String,String> searchedVideo=videos.get(0);
    			int totalNumberOfVideos=Integer.parseInt(searchedVideo.get("searchListSize").toString());
    			videoParameter.put("totalNumberOfVideos",totalNumberOfVideos);
    		}
    	}
    	if(videoType.equals("recent")){
    		videoParameter.put("videoType",videoType);
    		//context.put("videoType",videoType);
    		videos=videolist.getRecentVideos(videoCategorySrvcList.get(0),domain,index,lang,numberOfElements);
    	}else if(videoType.equals("toprated")){
    		videoParameter.put("videoType",videoType);
    		//context.put("videoType",videoType);
    		videos=videolist.getTopVideos(videoCategorySrvcList.get(0),domain,index,lang,numberOfElements);
    	}else if(videoType.equals("popular")){
    		videoParameter.put("videoType",videoType);
    		//context.put("videoType",videoType);
    		videos=videolist.getPopularVideos(videoCategorySrvcList.get(0),domain,index,lang,numberOfElements);
    	}else{
    		videos=videolist.getVideos(index,videoCategorySrvcList.get(0),domain,handset,numberOfElements,lang);
    	}
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
    	List<Map<String,String>> categoryList=(List<Map<String,String>>)videoParameterMap.get("categoryList");
    	
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
    		if(videoParameterMap.get("search")!=null){
        		context.put("search",videoParameterMap.get("search").toString());
        	}
        	if(videoParameterMap.get("searchResult")!=null){
        		context.put("searchResult",videoParameterMap.get("searchResult").toString());
        	}
        	if(videoParameterMap.get("videoType")!=null){
        		context.put("videoType",videoParameterMap.get("videoType").toString());
        	}
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
    		context.put("landingPage",landingPage);
    		context.put("cid",campaignId);
    		context.put("categoryLink", "videoByCategory.jsp?cid="+campaignId+"&landingPage="+landingPage+"&cat=");
    		context.put("action","subscribe.jsp?cid="+campaignId+"&landingPage="+landingPage+"&unq=");
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
    		context.put("moreVideoAction","videos.jsp");
    		
    		PebbleEngine ptEngine=templateengine.getTemplateEngine(dmn.getUnique());
    		ptEngine.getTemplate("index").evaluate(writer, context);
    	}catch(Exception e){
    		logger.error("Exception Rendering Video Template");
    	}

    }    
}

