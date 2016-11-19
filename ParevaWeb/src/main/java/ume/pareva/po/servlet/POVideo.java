package ume.pareva.po.servlet;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.SdcRequest;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.userservice.VideoList;

import com.mitchellbosecke.pebble.PebbleEngine;

public class POVideo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(POVideo.class.getName());
       
	@Autowired
	VideoList videolist;
	
	@Autowired
	HandsetDao handsetdao;
	
	@Autowired
	TemplateEngine templateEngine;//

	public POVideo() {
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
    	List<Map<String,String>> videos=new ArrayList<Map<String,String>>();
    	List<String> videoUniqueList=new ArrayList<String>();
    	
    	SdcRequest sdcRequest = new SdcRequest(request);
    	UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
    	String domain=dmn.getUnique();
    	Handset handset = handsetdao.getHandset(request);
    	int index = Integer.parseInt(sdcRequest.get("ind", "0"));
    	String lang = sdcRequest.getLanguage().getLanguageCode();
    	String search = sdcRequest.get("search").trim().toLowerCase();
    	String videoType=sdcRequest.get("videoType");
    	List<String> videoCategorySrvcList=getServiceUniqueList(domain);
    	
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
    	renderTemplateWithPagination(index,videoParameter,videos,request,response);
    }

    public List<String> getServiceUniqueList(String domain){
    	
    	List<String[]> serviceList = (List<String[]>)UmeTempCmsCache.clientServices.get(domain);
    	List<String> videoCategorySrvcList=new ArrayList<String>();
    	
    	if(serviceList!=null && !serviceList.isEmpty() && serviceList.size()>0) {
    		for (int i = 0; i < serviceList.size(); ++i) {
    			String[] serviceParameter = (String[])serviceList.get(i);
    			String srvc = serviceParameter[1];
    			String fName = serviceParameter[3];            

    			if (fName.equals("promo_hot_video_category.jsp")) {
    				videoCategorySrvcList.add(srvc);
    			}
    		}
    	}
    	return videoCategorySrvcList;
    }
    
    public void renderTemplateWithPagination(int index,HashMap videoParameterMap,List<Map<String,String>> videos,
    		HttpServletRequest request, HttpServletResponse response){
    	
    	UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
    	int numberOfElements=Integer.parseInt(videoParameterMap.get("numberOfElements").toString());
    	int totalNumberOfVideos=Integer.parseInt(videoParameterMap.get("totalNumberOfVideos").toString());
    	List<Map<String,String>> categoryList=(List<Map<String,String>>)videoParameterMap.get("categoryList");
    	
    	
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
    		context.put("terms","terms.jsp");
    		context.put("account","account.jsp");
    		PebbleEngine frEngine=templateEngine.getTemplateEngine(dmn.getUnique());
    		frEngine.getTemplate("index").evaluate(writer, context);
    	}catch(Exception e){
    		logger.error("Exception Rendering Video Template");
    	}

    }
}
