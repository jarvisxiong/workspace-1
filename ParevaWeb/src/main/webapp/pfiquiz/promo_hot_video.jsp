<%@ include file="imiVariables.jsp"%>
<%@page import="com.imi.uk.DataDefination.classtype"%>
<%@page import="com.imi.uk.DataDefination"%>
<%@page import="com.imi.uk.imageProcess.IMIImageCreatorWithPlayIcon"%>
<%@page import="com.imi.uk.imageProcess.IMIImageParameters"%>
<%@ include file="coreimport.jsp"%>
<%@ include file="ukheader.jsp"%>

<%

java.util.List videos=new ArrayList();
java.util.List categoryList=new ArrayList();
java.util.List pageList=new ArrayList();
String category="";
int number_of_elements=0;
java.util.List list = new ArrayList();
int index = Integer.parseInt(httprequest.get("ind", "0"));
String data_content_id ="";
String imiMsisdn=httprequest.get("msisdn");
String imiAddCode_=request.getParameter("s");
String visiteddomain=durl;//"i.x-stream1.co.uk";//httprequest.get("dom");
System.out.println("xstreamservername "+visiteddomain);
String pfitagHeaders="";
String Refrence_id="";
try{
pfitagHeaders=request.getHeader("X-ESC-Alias"); //IMI header
if(pfitagHeaders!=null && pfitagHeaders.trim().length()>0) Refrence_id=pfitagHeaders;
} catch(Exception e){}

Refrence_id=session.getAttribute("sessiontoken")+"";
if(Refrence_id==null || Refrence_id.trim().length()<=0)Refrence_id=session.getAttribute("sessiontoken")+"";
if(Refrence_id==null || Refrence_id.trim().length()<=0) Refrence_id=(String) request.getParameter("sessiontoken");

if(Refrence_id==null || Refrence_id.trim().equalsIgnoreCase("null"))Refrence_id=(String) request.getParameter("sessiontoken");

java.util.List video_category_list=(java.util.List)session.getAttribute("promo_hot_video_list");

HashMap videoParameter=new HashMap();


videoParameter=videolistuk.getVideoCategory(video_category_list,domain,handset);

categoryList=(java.util.List)videoParameter.get("categoryList");
category=(String)videoParameter.get("category");
number_of_elements=Integer.parseInt(videoParameter.get("number_of_elements").toString());
list=(java.util.List)videoParameter.get("list");

session.setAttribute("visiteddomain",visiteddomain);
session.setAttribute("categoryList",categoryList);


videos=videolistuk.getVideos(index,list,domain,handset,number_of_elements,lang,pfitagHeaders,Refrence_id,dmn,imiMsisdn,imiAddCode_,imi_price);

System.out.println("Video List Size: "+videos.size());


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

java.util.List<String> imiParameters=new ArrayList<String>();


String showScript="false";

String imagesurl="";

PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();

context.put("xstreamStatic","http://"+dmn.getContentUrl());

context.put("videos",videos);
context.put("categoryList",categoryList);
context.put("maxCount",maxCount);
context.put("curPage",curPage);
context.put("pageCount",pageCount);
context.put("pageList",pageList);
context.put("totalPage",totalPage);
context.put("index",index);
context.put("length_1","03:34");
context.put("length_2","03:03");
context.put("length_3","02:59");
context.put("length_4","02:12");
String header_logo="584959013814121.jpg"; 
context.put("header_logo",header_logo);
context.put("defaulturl","http://"+dmn.getDefaultUrl());
context.put("pricing","<div id=\""+data_content_id+"\"><div class=\"priceInfo\"><font size=\"2\" color=\"white\">Click to buy and <b>&nbsp;<span class=\"content-price\">&pound;6</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>");
context.put("websitetitle","UME-UK");
context.put("contenturl","http://"+dmn.getContentUrl());
System.out.println("Design Templates content URL: "+dmn.getContentUrl());
context.put("transref",Refrence_id);
context.put("contentprice",contentprice);
context.put("showscript",showScript);
context.put("action","promo_hot_video.jsp");
  
engine.getTemplate("video").evaluate(writer, context);
String output = writer.toString();


%>
