package ume.pareva.pt.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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


public class PTVideoByCategory extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( PTVideoByCategory.class.getName());
	
	@Autowired
	UmeRequest umeRequest;

	@Autowired
	VideoList videolist;

	@Autowired
	HandsetDao handsetdao;

	@Autowired
	TemplateEngine templateengine;

	@Autowired 
	Go4MobilityService go4MobilityService;
	
	@Autowired
	PTUtility ptUtility;
	
	public PTVideoByCategory() {
		super();
	}

	public void init(ServletConfig config) throws ServletException{
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
		List<Map<String,String>> videos=new ArrayList<Map<String,String>>();
		UmeDomain dmn = umeRequest.getDomain();
		String domain=dmn.getUnique();
		Handset handset = handsetdao.getHandset(request);
		String networkCode=umeRequest.getNetworkCode();
		

    	logger.info("Netowrk Code: "+ networkCode);
    			
		List<String> videoCategorySrvcList=ptUtility.getServiceUniqueList(domain,networkCode);
		String lang="en";
		String cat = umeRequest.get("cat");
		int index = Integer.parseInt(umeRequest.get("ind", "0"));
		HashMap videoParameter=new HashMap();
		videoParameter=videolist.getVideoCategory(videoCategorySrvcList.get(0),domain,handset);
		videoParameter.put("networkCode",networkCode);
		int numberOfElements=Integer.parseInt(videoParameter.get("numberOfElements").toString());
		videos=videolist.getVideosByCategory(cat,index, domain,numberOfElements, lang);
		renderTemplateWithPagination(index,videoParameter,videos,request,response);
	}

	public void renderTemplateWithPagination(int index,HashMap videoParameterMap,List<Map<String,String>> videos,
			HttpServletRequest request, HttpServletResponse response){
		HttpSession session=request.getSession();
		UmeDomain dmn=umeRequest.getDomain();
		String cat = umeRequest.get("cat");			
		String campaignId=umeRequest.get("cid");
    	String landingPage=umeRequest.get("landingPage");	
    	String networkCode=(String)videoParameterMap.get("networkCode");
		List<HashMap<String,Object>> pageList=new ArrayList<HashMap<String,Object>>();
		int numberOfElements=Integer.parseInt(videoParameterMap.get("numberOfElements").toString());
		Map<String,String> videoMap=videos.get(0);
		int totalNumberOfVideosInCategory=Integer.parseInt(videoMap.get("searchListSize"));
		List<Map<String,String>> categoryList=(List<Map<String,String>>)videoParameterMap.get("categoryList");
		int maxCount = numberOfElements;
		int pageCount = totalNumberOfVideosInCategory/maxCount;
		if((totalNumberOfVideosInCategory-index)%maxCount!=0)	
			pageCount=pageCount+1;
		int curPage = (index/maxCount)+1;

		int totalPage=totalNumberOfVideosInCategory/maxCount;
		if(totalNumberOfVideosInCategory%maxCount!=0)
			totalPage=totalPage+1;

		for(int j=0;j<totalPage;j++){
			HashMap paginationMap=new HashMap<String,Object>();
			paginationMap.put("index",j*maxCount);
			paginationMap.put("pageNo",j+1);
			pageList.add(paginationMap);

		}


		try{
			PrintWriter writer = response.getWriter();
			Map<String, Object> context = new HashMap();

			context.put("videos",videos);
			context.put("categories",categoryList);
			context.put("maxCount",maxCount);
			context.put("curPage",curPage);
			context.put("pageCount",pageCount);
			context.put("pageList",pageList);
			context.put("totalPage",totalPage);
			context.put("defaulturl","http://"+dmn.getDefaultUrl());
			context.put("contenturl","http://"+dmn.getContentUrl());
			context.put("cat",cat);
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
    		context.put("moreVideoAction","videoByCategory.jsp");

			PebbleEngine ptEngine=templateengine.getTemplateEngine(dmn.getUnique());
			ptEngine.getTemplate("video_by_category").evaluate(writer, context);		
		}catch(Exception e){
			logger.error("Exception Rendering Video By Category Template");
		}
	}

	
	 public String readCookie(HttpServletRequest request,String cookieName){
			Cookie[] cookies = request.getCookies();
			String cookieValue = "";
			if(cookies!=null){
				for(int loopIndex = 0; loopIndex < cookies.length; loopIndex++) { 
					Cookie cookie1 = cookies[loopIndex];
					if (cookie1.getName().equals(cookieName)) {
						cookieValue=cookie1.getValue();
						break;
					}
				}  
			}

			return cookieValue;

		}
}
