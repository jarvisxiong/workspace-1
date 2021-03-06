<%-- <%@ include file="global-wap-header.jsp"%>--%>
<jsp:include page="/GlobalWapHeader"/>

<%
String subscriptionurl=(String) request.getParameter("subscriptionurl");
if(subscriptionurl==null) subscriptionurl=(String) request.getAttribute("subscriptionurl");
if(subscriptionurl!=null && !subscriptionurl.equalsIgnoreCase(""))
{
    response.sendRedirect(subscriptionurl);return;
}

String needToConfirm=(String) request.getParameter("confirmUser");
if(needToConfirm==null) needToConfirm=(String) request.getAttribute("confirmUser");

if(needToConfirm!=null && needToConfirm.equalsIgnoreCase("true"))
{
    String wapoptin=(String) request.getParameter("wapoptin");
    if(wapoptin==null) wapoptin=(String) request.getAttribute("wapoptin");
    
    if(wapoptin!=null && wapoptin.equalsIgnoreCase("true"))
    {
        String wapurl=(String) request.getParameter("wappageurl");
        if(wapurl==null) wapurl=(String) request.getAttribute("wappageurl");
        
        if(wapurl!=null) 
        {
            response.sendRedirect(wapurl);return;
        }
        
                
    }
    
    else if(wapoptin!=null && wapoptin.equalsIgnoreCase("false"))
    {
        String smsredirection=(String) request.getParameter("smsconfirmed");
        if(smsredirection==null ) smsredirection=(String) request.getAttribute("smsconfirmed");
        
        if(smsredirection!=null) 
        {
            response.sendRedirect(smsredirection);return;
        }
    }
    
    
    
    
    
    
}




if(needToConfirm==null)
{
%>
<%@include file="coreimport.jsp" %>

<%
	UmeSessionParameters aReq = (UmeSessionParameters)request.getAttribute("aReq");	

	
	String msisdn=(String)request.getAttribute("msisdn");
	Handset handset = (Handset)session.getAttribute("handset");
	String lang = aReq.getLanguage().getLanguageCode();
	UmeDomain dmn = aReq.getDomain();
	UmeUser user = aReq.getUser();
	String domain = dmn.getUnique();
	VideoList videolist=(VideoList)request.getAttribute("videolist");
	Map<String,String>dParamMap=(Map<String,String>)request.getAttribute("dParamMap");
	PebbleEngine za_engine=(PebbleEngine)request.getAttribute("za_engine");
	String wapid=(String)request.getAttribute("wapid");
	java.util.List video_category_list=(java.util.List)session.getAttribute("promo_hot_video_list");
	java.util.List videos=new ArrayList();
	java.util.List categoryList=new ArrayList();
	java.util.List uniqueBanners=new ArrayList();
	java.util.List pageList=new ArrayList();
	java.util.List list = new ArrayList();
	HashMap searchMap=new HashMap();
	HashMap newVideoMap=new HashMap();
	HashMap popularVideoMap=new HashMap();
	HashMap topVideoMap=new HashMap();
	PrintWriter writer = response.getWriter();
	Map<String, Object> context = new HashMap();
	
	HashMap videoParameter=new HashMap();
	String videoType=aReq.get("videoType");
	String category="";
	int number_of_elements=0;
	
	int index = Integer.parseInt(aReq.get("ind", "0"));
	String search = aReq.get("search").trim().toLowerCase();
	if (!search.equals(""))
		context.put("search",search);
	
        
	videoParameter=videolist.getVideoCategory(video_category_list,domain,handset);
	
	categoryList=(java.util.List)videoParameter.get("categoryList");
	category=(String)videoParameter.get("category");
	number_of_elements=Integer.parseInt(videoParameter.get("number_of_elements").toString());
	list=(java.util.List)videoParameter.get("list");
	
	session.setAttribute("categoryList",categoryList);
	
	
	videos=videolist.getVideos(index,list,domain,handset,number_of_elements,lang);
	
	if (!search.equals("")){
		searchMap=videolist.searchVideos(search,domain,handset,dParamMap,lang,number_of_elements,index);
		videos=(java.util.List)searchMap.get("videos");
		
		list=(java.util.List)searchMap.get("searchList");
		System.out.println("list size = "+list.size());
		if(videos.size()<=0){
			context.put("searchResult","blank");
		}
	}
	
	
	System.out.println("Video Type = "+videoType);
	
	if(videoType.equals("recent")){
		videos=null;
		context.put("videoType",videoType);
		newVideoMap=videolist.getRecentVideos(handset,domain,index,lang,number_of_elements);
		//videos=(java.util.List)newVideoMap.get("videos");
		videos=videolist.getVideos(index,list,domain,handset,number_of_elements,lang);
		
		list=(java.util.List)newVideoMap.get("newVideoList");
		System.out.println("RECENT VIDEO SIZE LIST  = "+list.size());
               
	}
	
	if(videoType.equals("toprated")){
		context.put("videoType",videoType);
		topVideoMap=videolist.getTopVideos(handset,domain,index,lang,number_of_elements);
		//videos=(java.util.List)topVideoMap.get("videos");
                videos=videolist.getVideos(index,list,domain,handset,number_of_elements,lang);
		
                
                
		list=(java.util.List)topVideoMap.get("topVideoList");
		System.out.println("list size = "+list.size());
	}
	
	if(videoType.equals("popular")){
		context.put("videoType",videoType);
		popularVideoMap=videolist.getPopularVideos(handset,domain,index,lang,number_of_elements);
		//videos=(java.util.List)popularVideoMap.get("videos");
                videos=videolist.getVideos(index,list,domain,handset,number_of_elements,lang);
		
		list=(java.util.List)popularVideoMap.get("popularVideoList");
		System.out.println("list size = "+list.size());
	}
		
	int maxCount = number_of_elements;
	int pageCount = (list.size()-index)/maxCount;
	if((list.size()-index)%maxCount!=0)
		pageCount=pageCount+1;
	int curPage = (index/maxCount)+1;
	
	int totalPage=list.size()/maxCount;
	if(list.size()%maxCount!=0)
		totalPage=totalPage+1;
	
	for(int j=0;j<totalPage;j++){
		HashMap paginationMap=new HashMap<String,Object>();
		paginationMap.put("index",j*maxCount);
		paginationMap.put("pageNo",j+1);
		pageList.add(paginationMap);
		
	}
	
	try{
	java.util.List bannerSrvc=(java.util.List)session.getAttribute("promo_banner_list");
	uniqueBanners=videolist.getBanner(bannerSrvc,domain,handset,"topless","ZA");
	
	if(uniqueBanners.size()>0){	
		BannerAd bannerItem=null;
		String bannerType = "mobimage";
		for(int i=0;i<uniqueBanners.size();i++){
			HashMap bannerMap=new HashMap();
			bannerItem=(BannerAd)uniqueBanners.get(i);
			String bannerPath = "";
			String bannerLink = "";
			bannerPath = bannerItem.getImagePath(bannerType,0,handset.getXhtmlProfile());
			bannerLink = bannerItem.getMobileLink();
			System.out.println("Banner Path: "+bannerPath);
			System.out.println("Banner Link: "+bannerLink);
			if (bannerLink.startsWith("http://")){
				bannerLink=dmn.getDefPublicDir()+"/act_bannerlog.jsp?bunq="+bannerItem.getUnique()+"&msisdn="+msisdn+"&routerresponse=1";
				bannerMap.put("bannerImage",bannerPath);
				bannerMap.put("bannerLink",bannerLink);
			}
                        System.out.println("Banner Number "+(i+1));
			context.put("banner"+(i+1),bannerMap);
		}	
		
	}
	}catch(Exception e){}
	
	String wapId = "";
	if (user!=null){
		wapId = "&id=" + user.getWapId();
	}
	
//	Map<String, Object> xhtmlImagesMap = UmeTempCmsCache.xhtmlImages.get("xhtml_"+domain);
//	System.out.println("xhtmlImage : "+xhtmlImagesMap.get("img_header1_4"));
//	String headerImage="";
//	String footerImage="";
//	if(xhtmlImagesMap.get("img_header1_4")!=null)
//		headerImage=(String)xhtmlImagesMap.get("img_header1_4");
//	if(xhtmlImagesMap.get("img_footer1_4")!=null)
//		footerImage=(String)xhtmlImagesMap.get("img_footer1_4");
//	
//	
//	context.put("headerImage",headerImage);
//	context.put("footerImage",footerImage);
	
	String include_header="";
	System.out.println("Categories = "+session.getAttribute("number_of_category"));
	
	include_header=aReq.get("include_header");
	String number_of_category=aReq.get("number_of_category");
	
	System.out.println("include header from request = "+include_header);
	System.out.println("number_of_category from request = "+number_of_category);
	context.put("contenturl","http://"+dmn.getContentUrl());
	
	if(number_of_category.equals(""))
		context.put("number_of_category",session.getAttribute("number_of_category"));
	else
		context.put("number_of_category",Integer.parseInt(number_of_category));
	
	if(include_header.equals("")){
		context.put("include_header",(String)session.getAttribute("include_header"));
		include_header=(String)session.getAttribute("include_header");
	}
	else{
		context.put("include_header",include_header);
	}	
	if(include_header.equals("true")){
		context.put("categories",categoryList);
		context.put("videos",videos);
		context.put("maxCount",maxCount);
		context.put("curPage",curPage);
		context.put("pageCount",pageCount);
		context.put("pageList",pageList);
		context.put("totalPage",totalPage);
		//context.put("search",search);
		za_engine.getTemplate("index").evaluate(writer, context);
		include_header="false";
		session.setAttribute("include_header",include_header);
	}else {
		context.put("categories",categoryList);
		context.put("videos",videos);
		context.put("maxCount",maxCount);
		context.put("curPage",curPage);
		context.put("pageCount",pageCount);
		context.put("pageList",pageList);
		context.put("totalPage",totalPage);
		//context.put("search",search);
		za_engine.getTemplate("index").evaluate(writer, context);
	}
        
}//end if of header.
%>

