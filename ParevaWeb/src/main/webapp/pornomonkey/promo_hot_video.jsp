<%@ include file="imiVariables.jsp"%>
<%@page import="com.imi.uk.DataDefination.classtype"%>
<%@page import="com.imi.uk.DataDefination"%>
<%@page import="com.imi.uk.imageProcess.IMIImageCreatorWithPlayIcon"%>
<%@page import="com.imi.uk.imageProcess.IMIImageParameters"%>
<%@ include file="coreimport.jsp"%>
<%@ include file="db.jsp"%>
<%@ include file="ukheader.jsp"%>
<%@ include file="umereporo.jsp"%>

<%

java.util.List videos=new ArrayList();
java.util.List categoryList=new ArrayList();
java.util.List uniqueBanners=new ArrayList();
java.util.List pageList=new ArrayList();
String category="";
int number_of_elements=0;
java.util.List list = new ArrayList();
int index = Integer.parseInt(httprequest.get("ind", "0"));
String data_content_id ="";
String imiMsisdn=httprequest.get("msisdn");
String imiAddCode_=request.getParameter("s");

String pfitagHeaders="";
String Refrence_id="";
try{
pfitagHeaders=request.getHeader("X-ESC-Alias"); //IMI header
if(pfitagHeaders!=null && pfitagHeaders.trim().length()>0) Refrence_id=pfitagHeaders;
} catch(Exception e){}

Refrence_id=session.getAttribute("transaction-ref")+"";
if(Refrence_id==null || Refrence_id.trim().length()<=0)Refrence_id=session.getAttribute("transaction-ref")+"";
if(Refrence_id==null || Refrence_id.trim().length()<=0) Refrence_id=(String) request.getParameter("transaction-ref");

if(Refrence_id==null || Refrence_id.trim().equalsIgnoreCase("null"))Refrence_id=(String) request.getParameter("transref");

java.util.List video_category_list=(java.util.List)session.getAttribute("promo_hot_video_list");

HashMap videoParameter=new HashMap();


videoParameter=videolist.getVideoCategory(video_category_list,domain,handset);

categoryList=(java.util.List)videoParameter.get("categoryList");
category=(String)videoParameter.get("category");
number_of_elements=Integer.parseInt(videoParameter.get("number_of_elements").toString());
list=(java.util.List)videoParameter.get("list");

session.setAttribute("categoryList",categoryList);

int numberOfVideos=2;

videos=videolist.getVideos(index,list,domain,handset,numberOfVideos,lang);
//videos=videolist.getVideos(index,list,domain,handset,number_of_elements,lang,pfitagHeaders,Refrence_id,dmn,imiMsisdn,imiAddCode_,imi_price);

System.out.println("Video List Size: "+videos.size());




java.util.List<String> imiParameters=new ArrayList<String>();


String showScript="false";

String imagesurl="";


int visitcount=75;
Calendar nowTime=GregorianCalendar.getInstance();
System.out.println("UKCOUNT "+nowTime.get(Calendar.HOUR_OF_DAY));
if(nowTime.get(Calendar.HOUR_OF_DAY)>=20 || nowTime.get(Calendar.HOUR_OF_DAY)<8) visitcount=75;
else visitcount=150;

Transaction trans=dbsession.beginTransaction();

PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();


boolean validCampaign=!campaignId.equalsIgnoreCase("");


if(session.getAttribute("ukidentified")!=null)
{
    if(validCampaign)    
      {
    %>
    <%@include file="counter.jsp"%>
<%
    }
}


Integer ukidentifiedcounter =  (Integer)application.getAttribute("hitCounter");
if(validCampaign){

System.out.println("UKCOUNT "+visitcount);
if(ukidentifiedcounter!=null && ukidentifiedcounter>visitcount)
{
    boolean handsetdevice=!handset.isAndroid && !handset.isIphone && !handset.isSmartPhone && !handset.isWindowsPhone;
    String imimsisdn="";
    boolean billed=true;
    try{
    imimsisdn=(String) session.getAttribute("ukidentified");
    }catch(Exception e){}
    
    if(imimsisdn!=null) billed=true;//checkSuccess(dbsession, imimsisdn);
    
    System.out.println("==THE VALUE OF BOOLEANS ARE "+handsetdevice +" billed "+billed +" ======== "+handset.isTablet);
    if(!billed && !handsetdevice){
    
    showScript="true";
    application.setAttribute("hitCounter",0);
    }
    
    if(session.getAttribute("ukidentified")!=null){
         System.out.println("UKCOUNTER "+ukidentifiedcounter+" FOR campaign"+campaignId+ "msisdn:" +session.getAttribute("ukidentified")+" showscript "+showScript);
    }    
    else System.out.println("UKCOUNTER "+ukidentifiedcounter+" FOR campaign"+campaignId);
}
}

//int maxCount = 2;
//int pageCount = (4-index)/maxCount;
//if((4-index)%maxCount!=0)
//	pageCount=pageCount+1;
//int maxCount = cs.getItemsPerPage();
//int curPage = (index/maxCount)+1;

int maxCount = 2;
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
		if(j==4)
			break;
		
	}
	
	//int counter=10-index;
	
	

	  if(curPage==1)
	  {
	     context.put("counter1st","10");
	     context.put("counter2nd","09");
	     context.put("adlink","http://m.rk.com/?layout=tour-5&ad=3209:1%3A2&ad2=3210:2%3A5&ad3=3211:3%3A6&ad4&id=fill&cmp=fill");
	     context.put("adimage","linkcode1.gif");
	  }
	  else  if(curPage==2)
	  {
	     context.put("counter1st","08");
	     context.put("counter2nd","07");
	     context.put("adlink","http://mobile.teenslovehugecocks.com/main.htm?id=fill&cmp=fill");
	     context.put("adimage","linkcode2.gif");
	  }
	   else  if(curPage==3)
	  {
	     context.put("counter1st","06");
	     context.put("counter2nd","05");
	     context.put("adlink","http://mobile.teenslovehugecocks.com/main.htm?ad=641%3A2&ad2=642%3A5&ad3=643%3A6&ad4=644%3A&id=fill&cmp=fill");
	     context.put("adimage","linkcode3.gif");
	  }
	   else  if(curPage==4)
	  {
	     context.put("counter1st","04");
	     context.put("counter2nd","03");
	     context.put("adlink","http://enter.brazzersmobile.com/track/NjA3MTY5OjQ0OToxMTI/landing/zzk2/?ad1=id,404665;xt,gif;po,1&ad2=7379;po,2&ad3=85011;po,3&ad4=7919;po,4&ad5=83552;po,5&ad6=7682;po,6&ad7=1234;po,7&ad8=7890;po,8");
	     context.put("adimage","linkcodebrazzers1.gif");
	  }
	  
	   else  if(curPage==5)
	  {
	     context.put("counter1st","02");
	     context.put("counter2nd","01");
	     context.put("adlink","http://enter.brazzersmobile.com/track/NjA3MTcwOjQ0OToxMTI/landing/zzk2?ad1=id,404674;xt,gif;po,1&ad2=7379;po,2&ad3=85011;po,3&ad4=7919;po,4&ad5=85012;po,5&ad6=7682;po,6&ad7=1234;po,7&ad8=7890;po,8");
	     context.put("adimage","linkcodebrazzers2.gif");
	  }
	



showScript="true";
//curPage=Integer.parseInt(pageno);
context.put("counterno",ukidentifiedcounter);
context.put("videos",videos);
context.put("categoryList",categoryList);
context.put("maxCount",maxCount);
context.put("curPage",curPage);
context.put("pageCount",pageCount);
context.put("totalPage",totalPage);
context.put("pageList",pageList);

context.put("length_0","02:34");
context.put("length_1","03:34");
context.put("length_2","03:03");
context.put("length_3","02:59");
context.put("length_4","02:12");
context.put("userip",request.getHeader("X-Forwarded-For"));
String header_logo="584959013814121.jpg"; 
context.put("header_logo",header_logo);
context.put("defaulturl","http://"+dmn.getDefaultUrl());
context.put("pricing","<div id=\""+data_content_id+"\"><div class=\"priceInfo\"><font size=\"2\" color=\"white\">Click to buy and <b>&nbsp;<span class=\"content-price\">&pound;6</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>");
context.put("websitetitle","UME-UK");
//context.put("contenturl","http://"+dmn.getDefaultUrl());
context.put("contenturl","http://"+dmn.getContentUrl());
context.put("transref",Refrence_id);
context.put("defaulturl","http://"+dmn.getDefaultUrl());
context.put("contentprice",contentprice);
context.put("showscript",showScript);
String topbanner=ads.get("img_top").getMarkup();
System.out.println("IMAGE TOP "+topbanner);
//topbanner=topbanner.substring(0,topbanner.indexOf("href"))+"target=\"_blank\" "+topbanner.substring(topbanner.indexOf("href"),topbanner.lastIndexOf("href"))+"target=\"_blank\" "+topbanner.substring(topbanner.lastIndexOf("href"));
String bottombanner=ads.get("img_bottom").getMarkup();
//System.out.println("BOTTOM BANNER VALUES "+bottombanner);
//System.out.println("BOTTOM BANNER: "+bottombanner.substring(0,bottombanner.indexOf("href"))+"target=\"_blank\" "+bottombanner.substring(bottombanner.indexOf("href"),bottombanner.lastIndexOf("href"))+"target=\"_blank\" "+bottombanner.substring(bottombanner.lastIndexOf("href")));
int startindex=0;
int addby=0;
String close="";
if(topbanner.toLowerCase().contains("a href=")){
startindex=topbanner.indexOf("a href=");
addby=8;
close=">";
}
else if (topbanner.toLowerCase().contains("iframe src=")){
 startindex=topbanner.indexOf("iframe src=");
addby=12;   
close=" id";
    
}
    

        int indexclose=topbanner.indexOf(close,startindex);
        String topmore_banner=topbanner.substring(startindex+addby,indexclose-1);
        
        
System.out.println("BOTTOM BANNER "+bottombanner);
int startindex_bottom=0;
int bottomaddby=0;
String closeIndex="";
if(bottombanner.toLowerCase().contains("a href=")){
    startindex_bottom=bottombanner.indexOf("a href=");
    bottomaddby=8;
    closeIndex=">";
}
else if (bottombanner.toLowerCase().contains("iframe src=")){
 startindex_bottom=bottombanner.indexOf("iframe src=");
bottomaddby=12; 
closeIndex=" id";
    
}


        int indexclose_bottom=bottombanner.indexOf(closeIndex,startindex_bottom);
        String bottommore_banner=bottombanner.substring(startindex_bottom+bottomaddby,indexclose_bottom-1);


 System.out.println("BOTTOM BANNER URL "+bottommore_banner);

//bottombanner=bottombanner.substring(0,bottombanner.indexOf("href"))+"target=\"_blank\" "+bottombanner.substring(bottombanner.indexOf("href"),bottombanner.lastIndexOf("href"))+"target=\"_blank\" "+bottombanner.substring(bottombanner.lastIndexOf("href"));
context.put("topbanner",topbanner.substring(0,topbanner.indexOf("href"))+"target=\"_blank\" "+topbanner.substring(topbanner.indexOf("href"),topbanner.lastIndexOf("href"))+"target=\"_blank\" "+topbanner.substring(topbanner.lastIndexOf("href")));
//context.put("bottombanner",context.put("bottombanner",bottombanner.substring(0,bottombanner.indexOf("href"))+"target=\"_blank\" "+bottombanner.substring(bottombanner.indexOf("href"),bottombanner.lastIndexOf("href"))+"target=\"_blank\" "+bottombanner.substring(bottombanner.lastIndexOf("href"))));
context.put("bottombanner",bottombanner);
//String topmore_banner=topbanner.substring(topbanner.indexOf("<a")+9,topbanner.indexOf("'><img"))+" target=\"_blank\"";
//System.out.println("TOPMORE "+topmore_banner);
context.put("topmore",topmore_banner);
context.put("bottommore",bottommore_banner);
  trans.commit();
  dbsession.close();
  
engine.getTemplate("index").evaluate(writer, context);
String output = writer.toString();


%>
<%!
public boolean checkSuccess(Session dbsession, String msisdn)
{
    boolean billSuccess=false;
    String sqlstr = "Select * from mobileclubbillingtries where aParsedMsisdn='"+ msisdn+"' AND (aResponseCode='00' OR aResponseCode='001') LIMIT 1";
    Query query=null;
    try{
        query=dbsession.createSQLQuery(sqlstr);
        java.util.List result=query.list();
        if(result.size()>0) billSuccess=true;
    }
    catch(Exception e){}
    return billSuccess;
}




%>