<%
    request.setAttribute("pagename","promo_hot_video_category.jsp");
String headerincluded=request.getParameter("include_header"); 

if(headerincluded!=null && headerincluded.equalsIgnoreCase("true")){
%>
<jsp:include page="/AUMerchant"/>

<%
}
UmeSessionParameters aReq = (UmeSessionParameters)request.getAttribute("aReq");	
%>
<%@include file="coreimport.jsp" %>

<%
	//UmeSessionParameters aReq = (UmeSessionParameters)request.getAttribute("aReq");	

	
	String msisdn=(String)request.getAttribute("msisdn");
	Handset handset = (Handset)session.getAttribute("handset");
	String lang =aReq.getLanguage().getLanguageCode();
	UmeDomain dmn = aReq.getDomain();
	UmeUser user = aReq.getUser();
	String domain = dmn.getUnique();
	VideoList videolist=(VideoList)request.getAttribute("videolist");
	MobileClubCampaignDao campaigndao=(MobileClubCampaignDao)request.getAttribute("campaigndao");
	Map<String,String>dParamMap=(Map<String,String>)request.getAttribute("dParamMap");
	PebbleEngine au_engine=(PebbleEngine)request.getAttribute("au_engine");
        BannerAdDao banneraddao=(BannerAdDao)request.getAttribute("banneraddao");
	String wapid=(String)request.getAttribute("wapid");
	java.util.List videoCategoryList=(java.util.List)session.getAttribute("promo_hot_video_category_list");
	//java.util.List videos=new ArrayList();
        java.util.List<Map<String,String>> videos=new ArrayList<Map<String,String>>();
	java.util.List categoryList=new ArrayList();
	java.util.List<String> uniqueBanners=new ArrayList<String>();
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
	int numberOfElements=10;
        int totalNumberOfVideos=0;
	System.out.println("AUdebug promo_hot_videocategory.js SRVC: "+videoCategoryList.get(0));
	int index = Integer.parseInt(aReq.get("ind", "0"));
	String search = aReq.get("search").trim().toLowerCase();
	if (!search.equals(""))
		context.put("search",search);
	
        System.out.println("AUdebug promo_hot_videocategory.js SRVC: "+videoCategoryList.get(0)+" ----domain- "+domain+" ---- handset-- "+handset);
	videoParameter=videolist.getVideoCategory(videoCategoryList.get(0).toString(),domain,handset);
	System.out.println("AUdebug getvideos  the size of videoParameter is "+videoParameter.size());
	categoryList=(java.util.List<Map<String,String>>)videoParameter.get("categoryList");
//	category=(String)videoParameter.get("category");
	numberOfElements=Integer.parseInt(videoParameter.get("numberOfElements").toString());
	totalNumberOfVideos=Integer.parseInt(videoParameter.get("totalNumberOfVideos").toString());
	//list=(java.util.List)videoParameter.get("list");
//	System.out.println("Videolist size: "+list.size());
	
	session.setAttribute("categoryList",categoryList);
	
	
        System.out.println("AUdebug promo_hot_videocategory.jsp VideoCategory Videolist size "+list.size());
	videos=videolist.getVideos(index,videoCategoryList.get(0).toString(),domain,handset,numberOfElements,lang);
        
        for(int i=0;i<videos.size();i++){
            System.out.println("AUdebug Videos details "+videos.get(i).toString());
        }
	
	if (!search.equals("")){
		//searchMap=videolist.searchVideos(search,domain,handset,dParamMap,lang,numberOfElements,index);
		videos=videolist.searchVideos(videoCategoryList.get(0).toString(),search,index,domain,numberOfElements,lang);
		if(videos.isEmpty()){
			context.put("searchResult","blank");
		}else{
			Map<String,String> searchedVideo=videos.get(0);
			totalNumberOfVideos=Integer.parseInt(searchedVideo.get("searchListSize").toString());
			
		}
		//videos=(java.util.List)searchMap.get("videos");
		
		//list=(java.util.List)searchMap.get("searchList");
		//System.out.println("list size = "+list.size());
		
	}
	
	
	System.out.println("Video Type = "+videoType);
	
	if(videoType.equals("recent")){
		videos=null;
		context.put("videoType",videoType);
		newVideoMap=videolist.getRecentVideos(handset,domain,index,lang,numberOfElements);
		videos=videolist.getVideos(index,videoCategoryList.get(0).toString(),domain,handset,numberOfElements,lang);//(java.util.List)newVideoMap.get("videos");
		//videos=videolist.getVideos(index,list,domain,handset,number_of_elements,lang);
		
		list=(java.util.List)newVideoMap.get("newVideoList");
		System.out.println("RECENT VIDEO SIZE LIST  = "+list.size());
               
	}
	
	if(videoType.equals("toprated")){
		context.put("videoType",videoType);
		topVideoMap=videolist.getTopVideos(handset,domain,index,lang,numberOfElements);
		videos=videolist.getVideos(index,videoCategoryList.get(0).toString(),domain,handset,numberOfElements,lang);//(java.util.List)topVideoMap.get("videos");
                //videos=videolist.getVideos(index,list,domain,handset,number_of_elements,lang);
		
                
                
		list=(java.util.List)topVideoMap.get("topVideoList");
		System.out.println("list size = "+list.size());
	}
	
	if(videoType.equals("popular")){
		context.put("videoType",videoType);
		popularVideoMap=videolist.getPopularVideos(handset,domain,index,lang,numberOfElements);
		videos=videolist.getVideos(index,videoCategoryList.get(0).toString(),domain,handset,numberOfElements,lang);//(java.util.List)popularVideoMap.get("videos");
                //videos=videolist.getVideos(index,list,domain,handset,number_of_elements,lang);
		
		list=(java.util.List)popularVideoMap.get("popularVideoList");
		System.out.println("list size = "+list.size());
	}
		
      if(list==null){list=new ArrayList();list.add(0);list.add("1");list.add("2");list.add("3");list.add("4");list.add("5");}
	int maxCount = 0;
        try{
        maxCount=numberOfElements;
        if(maxCount==0) maxCount=10;
        }catch(Exception e){}
        
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
	uniqueBanners=videolist.getBanner(bannerSrvc,dmn,handset,"topless","AU");
	
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
                    System.out.println("AUdebugIT Banner Path: " + bannerPath+" ---  "+bannerItem.getUnique());
                    System.out.println("AUdebugIT Banner Link: " + bannerLink);
                    
                    
                    if(bannerLink.contains(dmn.getDefaultUrl())) continue;
                    
                    System.out.println("AUdebugIT AFTER Banner Path: " + bannerPath+" ---  "+bannerItem.getUnique());
                    System.out.println("AUdebugIT AFTER Banner Link: " + bannerLink);
                    if (bannerLink.startsWith("http://")) {
                        bannerLink = dmn.getDefPublicDir() + "/act_bannerlog.jsp?bunq=" + bannerItem.getUnique() + "&msisdn=" + msisdn + "&routerresponse=1";
                        bannerMap.put("bannerImage", bannerPath);
                        bannerMap.put("bannerLink", bannerLink);
                        
                        
                    }
                    
                    System.out.println("AUdebugIT Banner Number " + (i + 1));
                    context.put("banner" + (counter + 1), bannerMap);
                    counter++;
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
	
/* java.util.List notSubscribedClubDomains=(java.util.List)request.getAttribute("notSubscribedClubDomains");
System.out.println("notSubscribedClubDomains promo.jsp: "+notSubscribedClubDomains.size());
if(notSubscribedClubDomains!=null && !notSubscribedClubDomains.isEmpty()){
	UmeDomain popunderDomain=(UmeDomain)notSubscribedClubDomains.get((int) java.lang.Math.floor(java.lang.Math.random()* notSubscribedClubDomains.size()));
	String userMobile="",enMsi="",popunder="";
        
        if(user!=null)
        userMobile=user.getMobile().toString().trim();
        if(userMobile!=null && !userMobile.trim().equals(""))
	enMsi=MiscCr.encrypt(userMobile);
	String popunderCampaignId=campaigndao.getCampaignUnique(domain,"popunder");
        if(popunderDomain!=null && user!=null) popunder="http://"+popunderDomain.getDefaultUrl()+"/?id=" + user.getWapId()+"&mid="+enMsi+"&logtype=redirect&cid="+popunderCampaignId;
	context.put("popunderDomain",popunder);
}
String videoDownloaded=(String)request.getSession().getAttribute("videoDownloaded");
System.out.println("videoDownloaded: "+videoDownloaded);
if(videoDownloaded!=null){
	context.put("videoDownloaded",videoDownloaded);
}
context.put("videoUrl","http://"+dmn.getDefaultUrl()+"/?id="+user.getWapId());
 */

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
		au_engine.getTemplate("index").evaluate(writer, context);
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
		au_engine.getTemplate("index").evaluate(writer, context);
	}
        


%>