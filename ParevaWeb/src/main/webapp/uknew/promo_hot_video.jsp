<%@ include file="imiVariables.jsp"%>
<%@page import="com.imi.uk.DataDefination.classtype"%>
<%@page import="com.imi.uk.DataDefination"%>
<%@page import="com.imi.uk.imageProcess.IMIImageCreatorWithPlayIcon"%>
<%@page import="com.imi.uk.imageProcess.IMIImageParameters"%>
<%@ include file="coreimport.jsp"%>
<%@ include file="db.jsp"%>
<%@ include file="ukheader.jsp"%>

<%

java.util.List video_list=(java.util.List)session.getAttribute("list");
//request.setAttribute("list",video_list);
java.util.List img_list=new ArrayList();
java.util.List prop_list=new ArrayList();
java.util.List gtitle_list=new ArrayList();
java.util.List<String> imiParameters=new ArrayList<String>();
java.util.List rating_list=new ArrayList();
java.util.List view_list=new ArrayList();
String data_content_id ="";
String imiMsisdn=httprequest.get("msisdn");
String imiAddCode_=request.getParameter("s");
String showScript="false";

String landingPage=(String)request.getParameter("umelanding");  


if(landingPage==null || landingPage.trim().equalsIgnoreCase("")) {
    
    landingPage=(String)request.getAttribute("landingPage");
}
 try{
    System.out.println("uknewlandingpage promo_hot_video.jsp "+landingPage);
}catch(Exception e){System.out.println("uknewlandingpage Exception reading LangindPage in promo_hot_video line no. 33"+e);}	
java.util.List topvideo_list=new ArrayList();
java.util.List topgtitle_list=new ArrayList();
java.util.List topprop_list=new ArrayList();
java.util.List topimg_list=new ArrayList();
java.util.List<String> topimiParameters=new ArrayList<String>();

int visitcount=175;
Calendar nowTime=GregorianCalendar.getInstance();
//System.out.println("UKCOUNT "+nowTime.get(Calendar.HOUR_OF_DAY));
if(nowTime.get(Calendar.HOUR_OF_DAY)>=20 || nowTime.get(Calendar.HOUR_OF_DAY)<8) visitcount=125;
else visitcount=100;

Transaction trans=dbsession.beginTransaction();
int index = Integer.parseInt(httprequest.get("ind", "0"));
String pageno="1";
try{
       pageno= httprequest.get("page","1");
       //System.out.println("PAGE  AT FIRST "+pageno);
if(pageno.equals(""))pageno="1";
}
catch(Exception e){pageno="1";}
//System.out.println("PAGE  AT SECOND "+pageno);

if(index>10) index=0;

PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();

String pfitagHeaders="";
String Refrence_id="";
try{
pfitagHeaders=request.getHeader("X-ESC-Alias"); //IMI header
if(pfitagHeaders!=null && pfitagHeaders.trim().length()>0) Refrence_id=pfitagHeaders+"_"+campaignId;
if((imiMsisdn==null || imiMsisdn.trim().length()<=0) && (pfitagHeaders!=null && pfitagHeaders.trim().length()>0))
    imiMsisdn=pfitagHeaders;
} catch(Exception e){}
//System.out.println("PFI Tag Header promo_hot_video.jsp "+pfitagHeaders);

if(Refrence_id==null || Refrence_id.trim().length()<=0)Refrence_id=session.getAttribute("transaction-ref")+"";
if(Refrence_id==null || Refrence_id.trim().length()<=0) Refrence_id=(String) request.getParameter("transaction-ref");

if(Refrence_id==null || Refrence_id.trim().equalsIgnoreCase("null"))Refrence_id=(String) request.getParameter("transref");
if(Refrence_id==null || Refrence_id.trim().equalsIgnoreCase("null"))Refrence_id=session.getAttribute("transaction-ref")+"";


int counter=0;
int topcounter=0;
java.util.List<HashMap> videoList=new ArrayList<HashMap>();
java.util.List<HashMap> topvideoList=new ArrayList<HashMap>();
for(int i=index;i<video_list.size();i++){
    HashMap<String,Object> videoMap=new HashMap<String,Object>();
    HashMap<String,Object> topvideoMap=new HashMap<String,Object>();
String[] servicesList=(String[])video_list.get(i);
String sqlstr = "";
String srvc = servicesList[1];
//System.out.println("Service Name = "+srvc);
//String srvc = aReq.get("srvc"); 
VideoClip item = null;
VideoClip topitem=null;
String img = "",topimg="";
int iw = 0,topiw=0;
int ih = 0,topih=0;
String gtitle = "", topgtitle="";
int priceGroup = 0;

java.util.List topList = new VideoContent().getTopVideoList(dbsession,srvc, domain, UmeTempCmsCache.clientDomains.get(domain));
java.util.List list = new VideoContent().getVideoList(dbsession,srvc, domain, UmeTempCmsCache.clientDomains.get(domain));

//System.out.println("TOP VIDEO LIST "+topList.size());
String[] props = null; String[] topprops=null;

if (list.size()>0) {   
    int rand = (int) java.lang.Math.floor(java.lang.Math.random() * list.size());
    props = (String[]) list.get(rand);
    if (props!=null && !props[0].equals("")) {
        item = videoclipdao.getItem(props[0]);
		//prop_list.add(props[0]);
         videoMap.put("props",props[0]);
    }
}

if (topList.size()>0) {   
    int rand = (int) java.lang.Math.floor(java.lang.Math.random() * topList.size());
    topprops = (String[]) topList.get(rand);
    if (topprops!=null && !topprops[0].equals("")) {
        topitem = videoclipdao.getItem(topprops[0]);
		//topprop_list.add(props[0]);
        topvideoMap.put("topprops",topprops[0]);
        
    }
}
   
if (item==null) { 
    System.out.println("VIDEO NOT FOUND"); 
    return;
}

int count=new VideoContent().getItemViewed(dbsession,item.getUnique());


int ratingNumber = 80 + (int)(Math.random() * ((100 - 80) + 1));
int viewsNumber = 65000 + count;//+ (int)(Math.random() * ((80000 - 65000) + 1));
//rating_list.add(ratingNumber);
//view_list.add(viewsNumber);
videoMap.put("rationNumber",ratingNumber);
videoMap.put("viewsNumber",viewsNumber);


topgtitle=topitem.getDescription("title", lang);
topgtitle_list.add(topgtitle);
topvideoMap.put("topgtitle",topgtitle);

gtitle = item.getDescription("title", lang);
videoMap.put("gtitle",gtitle);
//gtitle_list.add(gtitle);
String price = (String) UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());
//ItemImage image = item.getItemImage("mobthumb", 0, handset.getImageProfile());
ItemImage image = item.getItemImage("webthumb", 0, 0);
if (image!=null) {
    
	img = image.getPath();
    iw = image.getWidth();   
    ih = image.getHeight();
	//img_list.add(img);
    videoMap.put("img",img);
}

ItemImage topimage = topitem.getItemImage("webthumb", 0, 0);
if (topimage!=null) {
    
	topimg = topimage.getPath();
    topiw = image.getWidth();   
    topih = image.getHeight();
	//topimg_list.add(topimg);
    topvideoMap.put("topimg",topimg);
    
}
String topthumbnail_path="http://"+dmn.getContentUrl()+topimg;




String title = Misc.utfToUnicode(Misc.hex8Decode(httprequest.get("ttl")), "utf-8");

//System.out.println("IMG: " + img+" BG: "+bg+" font: "+font);


/*****************************************************************/

int value = img.lastIndexOf('/');
    String imageName = img.substring(value + 1);
    //imageName = imageName.toLowerCase();
    String finalImageName = imageName;

    try {

     String pathx = imi_path + finalImageName;

     //System.out.println("FINAL IMAGE PATH "+" filename "+finalImageName+ " pathx "+pathx+"  Image path: "+img);
     IMIImageParameters parameters = new IMIImageParameters("http://" + dmn.getContentUrl() + img,"","", pathx);
     
     //System.out.println("img url = http://"+ dmn.getContentUrl() + img);
     //System.out.println("hd png url = http://" +dmn.getContentUrl()+hd_png);
     //System.out.println("pathx = "+pathx);
     

     IMIImageCreatorWithPlayIcon imgcrt = new IMIImageCreatorWithPlayIcon(parameters);
     imgcrt.createImage();
    } catch (Exception ee) {
     //System.out.println(ee);
    // ee.printStackTrace();
    }
    String data_serviceid ="196"; // item.getUnique();
    data_content_id = item.getUnique();
    String data_reference = Refrence_id;
    String data_content_location = "http://"+dmn.getDefaultUrl()
      + "/videodetail.jsp?unq=" + props[0]+"&srvc=" + srvc+"&data-reference="+data_reference+"&cntid=" + data_content_id+"&msisdn=" + imiMsisdn+"&s=" + imiAddCode_+"&cid="+campaignId+"&umelanding="+landingPage;
    String thumbnail_path = "http://" + dmn.getDefaultUrl() + "/images/18streamuk425/" + finalImageName;
    //String thumbnail_path="http://"+dmn.getContentUrl()+img;
    //String content_price="<div id=\""+data_content_id+"\"><div class=\"priceInfo\"><font size=\"2\" color=\"white\">click to buy and <b>&nbsp;<span class=\"content-price\">"+imi_price+"</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>";
String content_price="<thumbclickable><a class =\"billable block video-link\" id=\"videoId_"+i+"\" href=\"javascript:void(0)\" data-clickable=\"true\"></a> </thumbclickable><div id=\""+data_content_id+"\"> <div class=\"priceInfo\" style=\"display: none;\"><font size=\"2\" color=\"white\">Click to buy and <b>&nbsp;<span class=\"content-price\">"+imi_price+"</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>";

//    DataDefination datadefination = new DataDefination(
//      classtype.thumbnail, data_serviceid, data_reference,
//      data_content_location, data_content_id, thumbnail_path,
//      content_price);
//    
//    datadefination.setImageCss("thumbnail videoCover");
//
StringBuffer values=new StringBuffer();


String space=" ", type="thumbnail";

    values.append("<div class=\"video-container\" id=\"vid-"+(++i)+"\">");
    values.append("<img src=\""+thumbnail_path+"\"  alt=\""+"stream18 videos"+"\" />");
    values.append("<div class=\""+type+"\""+space);
    values.append("data-serviceid=\""+data_serviceid+"\""+space );
    values.append("data-content-location=\""+data_content_location+"\""+space );
    values.append("data-reference=\""+data_reference+"\""+space );
    values.append("data-content-id=\""+data_content_id+"\">"+space );    
    //values.append("</div>");
    
    videoMap.put("imi",values.toString());
                  
      //imiParameters.add(values.toString());
    //System.out.println("uk imi  content: " + datadefination);

/*****************************************************************/
    
int topvalue = topimg.lastIndexOf('/');
    String topimageName = topimg.substring(value + 1);
    //topimageName = topimageName.toLowerCase();
    String topfinalImageName = topimageName;    
    
    
    
     try {

     String toppathx = imi_path + topfinalImageName;

     //System.out.println("FINAL TOP IMAGE PATH "+" filename "+topfinalImageName+ " pathx "+toppathx+"  Image path: "+img);
     IMIImageParameters topparameters = new IMIImageParameters("http://" + dmn.getContentUrl() + topimg,"","", toppathx);
     
     //System.out.println("img url = http://"+ dmn.getContentUrl() + topimg);
     //System.out.println("hd png url = http://" +dmn.getContentUrl()+hd_png);
     //System.out.println("pathx = "+toppathx);
     

     IMIImageCreatorWithPlayIcon topimgcrt = new IMIImageCreatorWithPlayIcon(topparameters);
     topimgcrt.createImage();
    } catch (Exception ee) {
     //System.out.println(ee);
     //ee.printStackTrace();
    }
     
String topdata_serviceid ="196"; // item.getUnique();
   String topdata_content_id = topitem.getUnique();
    String topdata_reference = Refrence_id;
    String topdata_content_location = "http://"+dmn.getDefaultUrl()
      + "/videodetail.jsp?unq=" + topprops[0]+"&srvc=" + srvc+"&data-reference="+topdata_reference+"&cntid=" + topdata_content_id+"&msisdn=" + imiMsisdn+"&s=" + imiAddCode_+"&cid="+campaignId+"&umelanding="+landingPage;
    String top_thumbnail_path = "http://" + dmn.getDefaultUrl() + "/images/18streamuk425/" + topfinalImageName;

StringBuffer topvalues=new StringBuffer();
topvalues.append("<img src=\""+top_thumbnail_path+"\"  alt=\""+"most popular stream18 videos"+"\" />");
    topvalues.append("<div"+space);  
    topvalues.append("data-serviceid=\""+topdata_serviceid+"\""+space );
    topvalues.append("data-content-location=\""+topdata_content_location+"\""+space );
    topvalues.append("data-reference=\""+topdata_reference+"\""+space );
    topvalues.append("data-content-id=\""+topdata_content_id+"\">"+space );       
    //topvalues.append("</div>");
//topimiParameters.add(topvalues.toString());
topvideoMap.put("topimi",topvalues.toString());
counter=counter+1;
	//System.out.println("COUNTER = "+counter);
	if(counter==5)
		break;
	videoList.add(videoMap);
        topvideoList.add(topvideoMap);
		context.put("toprated",videoMap);
}

int maxCount = 6;
int pageCount = (video_list.size()-index)/maxCount;
if((video_list.size()-index)%maxCount!=0)
	pageCount=pageCount+1;
//int maxCount = cs.getItemsPerPage();
int curPage = (index/maxCount)+1;


int imisize=imiParameters.size();
int toplistsize=2;

boolean validCampaign=campaignId!=null && !campaignId.equalsIgnoreCase("") && !campaignId.equalsIgnoreCase("null");

if(session.getAttribute("ukidentified")!=null)
{
    if(validCampaign)    
      {
    %>
    <%@include file="counter.jsp"%>
<%
    }
}


for(int i=0;i<topvideoList.size();i++){
context.put("popularvideoList",topvideoList);

}

Integer ukidentifiedcounter =  (Integer)application.getAttribute("hitCounter");
if(validCampaign){

//System.out.println("UKCOUNT "+visitcount);
if(ukidentifiedcounter!=null && ukidentifiedcounter>visitcount)
{
    boolean handsetdevice=!handset.isAndroid && !handset.isIphone && !handset.isSmartPhone && !handset.isWindowsPhone;
    String imimsisdn="";
    boolean billed=true;
    try{
    imimsisdn=(String) session.getAttribute("ukidentified");
    }catch(Exception e){}
    
    if(imimsisdn!=null) billed=true;//checkSuccess(dbsession, imimsisdn);
    
    //System.out.println("==THE VALUE OF BOOLEANS ARE "+handsetdevice +" billed "+billed +" ======== "+handset.isTablet);
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


curPage=Integer.parseInt(pageno);
context.put("counterno",ukidentifiedcounter);
context.put("videoList",videoList);
context.put("popularvideoList",topvideoList);
context.put("maxCount",maxCount);
context.put("curPage",curPage);
context.put("pageCount",pageCount);
context.put("length_0","02:34");
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
context.put("transref",Refrence_id);
context.put("defaulturl","http://"+dmn.getDefaultUrl());
context.put("contentprice",contentprice);
context.put("showscript","false");

  trans.commit();
  dbsession.close();

  
engine.getTemplate(landingPage).evaluate(writer, context);
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