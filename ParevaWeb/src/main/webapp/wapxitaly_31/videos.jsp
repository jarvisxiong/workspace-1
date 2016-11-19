<jsp:include page="/GlobalWapHeader"/>
<%
String routerresponse = (String)request.getParameter("routerresponse");
if(routerresponse==null) routerresponse=(String) request.getAttribute("routerresponse");
if(routerresponse!=null && routerresponse.equalsIgnoreCase("true"))
{
    String redirectUrl=(String) request.getParameter("routerurl");
    if(redirectUrl==null) redirectUrl=(String) request.getAttribute("routerurl");
    
    if(redirectUrl!=null)
    {
        response.sendRedirect(redirectUrl);return;
    }
    
}

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




if(routerresponse==null && needToConfirm==null)
{
%>
<%@include file="coreimport.jsp" %>
<%@include file="db.jsp" %>



<%
UmeSessionParameters aReq = (UmeSessionParameters)request.getAttribute("aReq");	
	
	Handset handset = (Handset)session.getAttribute("handset");
	String lang = aReq.getLanguage().getLanguageCode();
	UmeDomain dmn = aReq.getDomain();
	UmeUser user = aReq.getUser();
	String domain = dmn.getUnique();
      //  Map<String, String> dParamMap = UmeTempCmsCache.domainParameters.get(domain);
        VideoClipDao videoclipdao=(VideoClipDao) request.getAttribute("videoclipdao");
        PebbleEngine za_engine=(PebbleEngine)request.getServletContext().getAttribute("za_engine");
java.util.List categoryList=(java.util.List) session.getAttribute("categoryList");
System.out.println("CategoryList Size = "+categoryList.size());
String cat = aReq.get("cat");
String sort = aReq.get("sort");
int index = Integer.parseInt(aReq.get("ind", "0"));
int max = Integer.parseInt(aReq.get("max", "10"));
String hexTitle = aReq.get("ttl");
String title = Misc.utfToUnicode(Misc.hex8Decode(hexTitle), "utf-8");
String search = aReq.get("ss").trim().toLowerCase();
String bg = aReq.get("bg");
String font = aReq.get("fnt");
String style1 = "style=\"background-color:#" + bg +"; color:#" + font +";\"";
java.util.List pageList=new ArrayList();

ContentSet cs = new ContentSet();
cs.setSortString(sort);
cs.setDomain(domain);
//cs.setHandset(handset);
cs.setItemsPerPage(max);
cs.setPageIndex(index);
cs.setCategory(cat);
cs.setNumberOfTopPages(20);
cs.setSearchString(search);
cs.setTimeSpan(300);
cs.setClassification("topless");

// Don't remove this, this is for classification
//if (dParamMap.get("classification")!=null) cs.setClassification(dParamMap.get("classification"));


videoclipdao.populate(cs);
java.util.List list = cs.getList();
//System.out.println("Number of Videos in Category = "+ list.size());
//VideoClip item = null;
//ItemImage image = null;
//String img = "";
//int iw = 0;
//int ih = 0;

java.util.List<ItemCategory> catlist = UmeTempCmsCache.itemCategoryMap.get("video");


//int count = cs.getItemCount();
//int pageCount = cs.getPageCount();
//int maxCount = cs.getItemsPerPage();
//int curPage = (index/maxCount)+1;

String params2 = "sort=" + sort + "&amp;cat=" + cat + "&amp;ttl=" + hexTitle + "&amp;ss=" + java.net.URLEncoder.encode(search) + "&amp;max=" + max;
String params = params2 + "&amp;ind=" + index + "&amp;fp=videos";



%>

<% 
java.util.List<HashMap> videos=new ArrayList<HashMap>();

VideoClip item = null;
ItemImage image = null;

String img="";
int iw = 0;
int ih = 0;
String sqlstr = "";
//System.out.println("ITME LIST SIZE: " + list.size());
int counter =0;
   for (int i=index; i<list.size(); i++) {
		HashMap<String,Object> videoMap=new HashMap<String,Object>();
        item = (VideoClip) list.get(i);
        image = item.getItemImage("mobthumb", 0, handset.getImageProfile());
        img = "";

        if(image==null){
            image = item.getItemImage("webthumb", 0, handset.getImageProfile());
        }
        if (image!=null) {
            img = image.getPath();
            iw = image.getWidth();
            ih = image.getHeight();

			request.setAttribute("img",img);
			request.setAttribute("iw",iw);
			request.setAttribute("ih",ih);
			
		}
  
	
    int views = new VideoContent().getItemViewed(dbsession,item.getUnique());;
	
    int ratingNumber = 80 + (int)(Math.random() * ((100 - 80) + 1));
    int viewsNumber = 65000 + views;
	request.setAttribute("ratingNumber",ratingNumber);
	request.setAttribute("viewsNumber",viewsNumber);
	String description=item.getDescription("title",lang);
	videoMap.put("item",item);
	videoMap.put("image",image);
	videoMap.put("img",img);
	videoMap.put("iw",iw);
	videoMap.put("ih",ih);
	videoMap.put("ratingNumber",ratingNumber);
	videoMap.put("viewsNumber",viewsNumber);
	videoMap.put("description",description);
	videoMap.put("videoUnique",item.getUnique());
	videos.add(videoMap);
	counter=counter+1;
	if(counter==5)
		break;
}	
   
   

int maxCount = 5;
int pageCount = (list.size()-index)/maxCount;
if((list.size()-index)%maxCount!=0)
	pageCount=pageCount+1;
//int maxCount = cs.getItemsPerPage();
int curPage = (index/maxCount)+1;

//System.out.println("Current Page = "+curPage);
//System.out.println("Page Count = "+pageCount);
//System.out.println("Page Count - Current Page = "+(pageCount-curPage));


int totalPage=list.size()/maxCount;
if(list.size()%maxCount!=0)
	totalPage=totalPage+1;


for(int j=0;j<totalPage;j++){
	HashMap paginationMap=new HashMap<String,Object>();
	paginationMap.put("index",j*maxCount);
	paginationMap.put("pageNo",j+1);
	pageList.add(paginationMap);
	
}

//System.out.println("Total Page = "+totalPage);
	
PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();
  

//String headerlogo=(String)dImages.get("img_header1_" + handset.getImageProfile());
//String footerlogo=(String)dImages.get("img_footer1_" + handset.getImageProfile());

String include_header="true";
int number_of_category=0;

/*
for(int i=0;i<categoryList.size();i++){
	HashMap categoryMap=(HashMap)categoryList.get(i);
	if(cat.equals(categoryMap.get("unique"))){
		categoryList.remove(i);
	}
}
*/

context.put("include_header",include_header);
context.put("number_of_category",number_of_category);
context.put("videos",videos);
context.put("categories",categoryList);
context.put("cat",cat);


context.put("contenturl","http://"+ dmn.getContentUrl());
context.put("maxCount",maxCount);
context.put("curPage",curPage);
context.put("pageCount",pageCount);
context.put("pageList",pageList);
context.put("totalPage",totalPage);

//trans.commit();
//dbsession.close();
//PebbleEngine mexico_engine=(PebbleEngine)this.getServletContext().getAttribute("mexico_engine");
za_engine.getTemplate("videos").evaluate(writer, context);

}//end if header
	%>
	
