<%@include file="coreimport.jsp" %>
<jsp:include page="/IT/GlobalWapHeaderIT"/>

<%
	UmeSessionParameters aReq = (UmeSessionParameters)request.getAttribute("aReq");		
	String msisdn=(String)request.getAttribute("msisdn");
	Handset handset = (Handset)session.getAttribute("handset");
	String lang = aReq.getLanguage().getLanguageCode();
	UmeDomain dmn = aReq.getDomain();
	UmeUser user = aReq.getUser();
	//String domain = dmn.getUnique();
	VideoList videolist=(VideoList)request.getAttribute("videolist");
	Map<String,String>dParamMap=(Map<String,String>)request.getAttribute("dParamMap");
	String domain=(String)request.getAttribute("domain");

    //UmeDomain dmn =(UmeDomain) request.getAttribute("dmn");

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
	

	System.out.println("ItalyVIDEO video_category_list: " + video_category_list);
	System.out.println("ItalyVIDEO domain: " + domain);
	System.out.println("ItalyVIDEO handset: " + handset);
	System.out.println("ItalyVIDEO videolist: " + videolist);

	//video_category_list: null
	//domain: 7971616237231CDS
	
	try{
		//TODO video_category_list always null
            System.out.println("ItalyVIDEO  videocategory list: "+video_category_list.size());
		videoParameter=videolist.getVideoCategory(video_category_list,domain,handset);
		categoryList=(java.util.List)videoParameter.get("categoryList");
		category=(String)videoParameter.get("category");
		number_of_elements=Integer.parseInt(videoParameter.get("number_of_elements").toString());
		list=(java.util.List)videoParameter.get("list");
		session.setAttribute("categoryList",categoryList);
                
                System.out.println("ItalyVIDEOS "+index+" "+list.size()+" "+number_of_elements);
		videos=videolist.getVideos(index,list,domain,handset,number_of_elements,lang);
	}catch(Exception ee){
		ee.printStackTrace();
	}

	if(videos!=null){
		System.out.println("ItalyVIDEOS SIZE IS "+videos.size());
	 	for(int i=0;i<videos.size();i++){
	            System.out.println(i+" "+videos.get(i).toString());
	    }
	}
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
		//System.out.println("RECENT VIDEO SIZE LIST  = "+list.size());
               
	}
	
	if(videoType.equals("toprated")){
		context.put("videoType",videoType);
		topVideoMap=videolist.getTopVideos(handset,domain,index,lang,number_of_elements);
		//videos=(java.util.List)topVideoMap.get("videos");
        videos=videolist.getVideos(index,list,domain,handset,number_of_elements,lang);                
		list=(java.util.List)topVideoMap.get("topVideoList");
		//System.out.println("list size = "+list.size());
	}
	
	if(videoType.equals("popular")){
		context.put("videoType",videoType);
		popularVideoMap=videolist.getPopularVideos(handset,domain,index,lang,number_of_elements);
		//videos=(java.util.List)popularVideoMap.get("videos");
        videos=videolist.getVideos(index,list,domain,handset,number_of_elements,lang);
		list=(java.util.List)popularVideoMap.get("popularVideoList");
		//System.out.println("list size = "+list.size());
	}

	int totalPage = 0;
	int maxCount = 1;
	int curPage = 0;
	int pageCount = 0;
	if(list!=null){	
		maxCount = number_of_elements;
		//pageCount = (list.size()-index)/maxCount;
		/*
		if((list.size()-index)%maxCount!=0)
			pageCount=pageCount+1;
		curPage = (index/maxCount)+1;
		
		totalPage=list.size()/maxCount;
		if(list.size()%maxCount!=0)
			totalPage=totalPage+1;
		
		for(int j=0;j<totalPage;j++){
			HashMap paginationMap=new HashMap<String,Object>();
			paginationMap.put("index",j*maxCount);
			paginationMap.put("pageNo",j+1);
			pageList.add(paginationMap);	
		}
		*/
	}
	
	String wapId = "";
	if (user!=null){
		wapId = "&id=" + user.getWapId();
	}

	String include_header="";
	
	include_header=aReq.get("include_header");
	String number_of_category=aReq.get("number_of_category");

	TemplateEngine templateengine=(TemplateEngine)request.getAttribute("templateengine");
	PebbleEngine za_engine=templateengine.getTemplateEngine(domain);	
	context.put("contenturl","http://"+dmn.getContentUrl());
	
	if(number_of_category.equals(""))
		context.put("number_of_category",session.getAttribute("number_of_category"));
	else
		context.put("number_of_category",Integer.parseInt(number_of_category));
	
	context.put("categories",categoryList);
	context.put("videos",videos);
	context.put("maxCount",maxCount);
	context.put("curPage",curPage);
	context.put("pageCount",pageCount);
	context.put("pageList",pageList);
	context.put("totalPage",totalPage);
	//context.put("search",search);
	za_engine.getTemplate("index").evaluate(writer, context);

	/*
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
	*/     
%>

