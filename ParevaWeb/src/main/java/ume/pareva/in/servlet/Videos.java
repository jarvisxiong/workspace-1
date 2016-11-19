package ume.pareva.in.servlet;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.BannerAd;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.template.TemplateEngine;
import ume.pareva.userservice.VideoList;
import ume.pareva.util.httpconnection.HttpURLConnectionWrapper;

import com.mitchellbosecke.pebble.PebbleEngine;

/**
 * Servlet implementation class Videos
 */
//@WebServlet("/Videos")
public class Videos extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Autowired
	VideoList videolist;
	
	@Autowired
	HandsetDao handsetdao;
	
	@Autowired
	TemplateEngine templateengine;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Videos() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
    /**
     * This method is needed to support Autowired Spring beans
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
        config.getServletContext());
        }


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processVideos(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processVideos(request, response);
	}
	
    protected void processVideos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	UmeSessionParameters aReq = new UmeSessionParameters(request);
    	UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
    	String domain=dmn.getUnique();
    	Handset handset = handsetdao.getHandset(request);
    	int index = Integer.parseInt(aReq.get("ind", "0"));
    	String lang = aReq.getLanguage().getLanguageCode();

    	List<String[]> list = (List<String[]>)UmeTempCmsCache.clientServices.get(domain);
    	List<String> videoCategorySrvcList=new ArrayList<String>();
    	List<String> bannerSrvcList = new ArrayList<String>();
    	List<Map<String,String>> videos=new ArrayList<Map<String,String>>();
    	List<Map<String,String>> categoryList=new ArrayList<Map<String,String>>();
    	List<String> videoUniqueList=new ArrayList<String>();
    	List<BannerAd> uniqueBanners=new ArrayList<BannerAd>();
    	java.util.List pageList=new ArrayList();

    	String category="";
    	int numberOfElements=0;
    	if(list!=null && !list.isEmpty() && list.size()>0) {
    		for (int i = 0; i < list.size(); ++i) {
    			String[] servicesList = (String[])list.get(i);
    			String srvc = servicesList[1];
    			String fName = servicesList[3];            

    			if (fName.equals("promo_hot_video_category.jsp")) {
    				videoCategorySrvcList.add(srvc);
    				continue;
    			}
    			if (!fName.equals("promo_banner.jsp")) 
    				bannerSrvcList.add(srvc);
    			continue;
    		}
    	}
    	HashMap videoParameter=new HashMap();
    	videoParameter=videolist.getVideoCategory(videoCategorySrvcList.get(0),domain,handset);
    	categoryList=(List)videoParameter.get("categoryList");
    	numberOfElements=10;//Integer.parseInt(videoParameter.get("numberOfElements").toString());
    	videoUniqueList=(List)videoParameter.get("list");
    	videos=videolist.getVideos(index,videoCategorySrvcList.get(0),domain,handset,numberOfElements,lang);
    //	videos=videolist.getVideos(index,videoUniqueList,domain,handset,numberOfElements,lang);
    	int totalNumberOfVideos=Integer.parseInt(videoParameter.get("totalNumberOfVideos").toString());
    	
    	int maxCount = 0;
    	try{
    		maxCount=numberOfElements;
    		if(maxCount==0) maxCount=10;
    	}catch(Exception e){}

    	int pageCount = (totalNumberOfVideos-index)/maxCount;
    	if((totalNumberOfVideos-index)%maxCount!=0)
    		pageCount=pageCount+1;
    	int curPage = (index/maxCount)+1;

    	int totalPage=totalNumberOfVideos/maxCount;
    	if(totalNumberOfVideos%maxCount!=0)
    		totalPage=totalPage+1;

    	for(int j=0;j<totalPage;j++){
    		HashMap paginationMap=new HashMap<String,Object>();
    		paginationMap.put("index",j*maxCount);
    		paginationMap.put("pageNo",j+1);
    		pageList.add(paginationMap);

    	}

    	PrintWriter writer = response.getWriter();
    	Map<String, Object> context = new HashMap();
    	context.put("categories",categoryList);
    	context.put("videos",videos);
    	context.put("maxCount",maxCount);
    	context.put("curPage",curPage);
    	context.put("pageCount",pageCount);
    	context.put("pageList",pageList);
    	context.put("totalPage",totalPage);
    	context.put("domainUrl",dmn.getDefaultUrl());
    	context.put("contenturl","http://"+dmn.getContentUrl());
    	PebbleEngine in_engine=templateengine.getTemplateEngine(domain);

    	try{
    		in_engine.getTemplate("index").evaluate(writer, context);
    	}catch(Exception e){
    		System.out.println("Exception Rendering Template");
    	}
    }

}
