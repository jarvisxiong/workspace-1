<%@ include file="imiVariables.jsp"%>
<%@page import="com.imi.uk.DataDefination.classtype"%>
<%@page import="com.imi.uk.DataDefination"%>
<%@page import="com.imi.uk.imageProcess.IMIImageCreatorWithPlayIcon"%>
<%@page import="com.imi.uk.imageProcess.IMIImageParameters"%>
<%@ include file="coreimport.jsp"%>
<%@ include file="db.jsp"%>
<%@ include file="ukheader.jsp"%>

<%

java.util.List video_list=(java.util.List)request.getAttribute("list");
java.util.List img_list=new ArrayList();
java.util.List prop_list=new ArrayList();
java.util.List gtitle_list=new ArrayList();
java.util.List<String> imiParameters=new ArrayList<String>();
java.util.List rating_list=new ArrayList();
java.util.List view_list=new ArrayList();
String data_content_id ="";
String imiMsisdn=httprequest.get("msisdn");
String imiAddCode_=httprequest.get("s");


 	//System.out.println("VIDEO LIST SIZE "+video_list.size());

Transaction trans=dbsession.beginTransaction();

int index = Integer.parseInt(httprequest.get("ind", "0"));
System.out.println("VIDEO LIST SIZE "+index);

PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();

String pfitagHeaders="";
String Refrence_id="";
try{
pfitagHeaders=request.getHeader("X-ESC-Alias"); //IMI header
if(pfitagHeaders!=null && pfitagHeaders.trim().length()>0) Refrence_id=pfitagHeaders;
} catch(Exception e){}
System.out.println("PFI Tag Header promo_hot_video.jsp "+pfitagHeaders);

if(Refrence_id==null || Refrence_id.trim().length()<=0)Refrence_id=session.getAttribute("transaction-ref")+"";
if(Refrence_id==null || Refrence_id.trim().length()<=0) Refrence_id=(String) request.getParameter("transaction-ref");

if(Refrence_id==null || Refrence_id.trim().equalsIgnoreCase("null"))Refrence_id=(String) request.getParameter("transref");

int counter=0;
java.util.List<HashMap> videoList=new ArrayList<HashMap>();

for(int i=index;i<10;i++){
HashMap<String,Object> videoMap=new HashMap<String,Object>();
//String[] servicesList=(String[])video_list.get(i);
String sqlstr = "";
//String srvc = servicesList[1];

String srvc = "2724972838141KDS"; 
System.out.println("Service Name in CLONE = "+srvc);
VideoClip item = null;
String img = "";
int iw = 0;
int ih = 0;
String gtitle = "";
int priceGroup = 0;

java.util.List list = new VideoContent().getVideoList(dbsession,srvc, domain, UmeTempCmsCache.clientDomains.get(domain));
String[] props = null;

if (list.size()>0) {   
    int rand = (int) java.lang.Math.floor(java.lang.Math.random() * list.size());
    props = (String[]) list.get(rand);
    if (props!=null && !props[0].equals("")) {
        item = videoclipdao.getItem(props[0]);
        videoMap.put("props",props[0]);
		//prop_list.add(props[0]);
    }
}
   
if (item==null) { 
    System.out.println("VIDEO NOT FOUND"); 
    return;
}

int count=new VideoContent().getItemViewed(dbsession,item.getUnique());


int ratingNumber = 80 + (int)(Math.random() * ((100 - 80) + 1));
int viewsNumber = 65000 + count;//+ (int)(Math.random() * ((80000 - 65000) + 1));
videoMap.put("rationNumber",ratingNumber);
videoMap.put("viewsNumber",viewsNumber);
//rating_list.add(ratingNumber);
//view_list.add(viewsNumber);


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
    videoMap.put("img",img);
	//img_list.add(img);
}





String title = Misc.utfToUnicode(Misc.hex8Decode(httprequest.get("ttl")), "utf-8");

//System.out.println("IMG: " + img+" BG: "+bg+" font: "+font);


/*****************************************************************/

int value = img.lastIndexOf('/');
    String imageName = img.substring(value + 1);
    imageName = imageName.toLowerCase();
    String finalImageName = imageName;

    //try {

     String pathx = imi_path + finalImageName;

     System.out.println("FINAL IMAGE PATH "+" filename "+finalImageName+ " pathx "+pathx+"  Image path: "+img);
//     IMIImageParameters parameters = new IMIImageParameters("http://" + dmn.getContentUrl() + img,"http://"+dmn.getContentUrl()+imi_png,"http://"+dmn.getContentUrl()+hd_png, pathx);
//     
////     System.out.println("img url = http://"+ dmn.getContentUrl() + img);
////     System.out.println("hd png url = http://" +dmn.getContentUrl()+hd_png);
////     System.out.println("pathx = "+pathx);
//     
//
//     IMIImageCreatorWithPlayIcon imgcrt = new IMIImageCreatorWithPlayIcon(
//       parameters);
//     imgcrt.createImage();
//    } catch (Exception ee) {
//     System.out.println(ee);
//    }
    String data_serviceid ="196"; // item.getUnique();
    data_content_id = item.getUnique();
    String data_reference = Refrence_id;
    String data_content_location = "http://"+dmn.getDefaultUrl()
      + "/videodetail.jsp?unq=" + props[0]+"&srvc=" + srvc+"&data-reference="+data_reference+"&cntid=" + data_content_id+"&msisdn=" + imiMsisdn+"&s=" + imiAddCode_;
    //String thumbnail_path = "http://" + dmn.getContentUrl() + "/images/imiuk/" + finalImageName;
    String thumbnail_path="http://"+dmn.getContentUrl()+img;
    //String content_price="<div id=\""+data_content_id+"\"><div class=\"priceInfo\"><font size=\"2\" color=\"white\">click to buy and <b>&nbsp;<span class=\"content-price\">"+imi_price+"</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>";
String content_price="<thumbclickable><a class =\"billable block video-link\" id=\"videoId_"+i+"\" href=\"javascript:void(0)\" data-clickable=\"true\"></a> </thumbclickable><div id=\""+data_content_id+"\"> <div class=\"priceInfo\" style=\"display: none;\"><font size=\"2\" color=\"white\">Click to buy and <b>&nbsp;<span class=\"content-price\">"+imi_price+"</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>";

//    DataDefination datadefination = new DataDefination(
//      classtype.thumbnail, data_serviceid, data_reference,
//      data_content_location, data_content_id, thumbnail_path,
//      content_price);
//    
//    datadefination.setImageCss("thumbnail videoCover");
//    
    //imiParameters.add("<img src=\""+thumbnail_path+"\"  alt=\""+"stream18 videos"+"\" />");
	videoMap.put("imi","<img src=\""+thumbnail_path+"\"  alt=\""+"stream18 videos"+"\" />");
    //System.out.println("uk imi  content: " + datadefination);

/*****************************************************************/

	counter=counter+1;
	System.out.println("COUNTER = "+counter);
	if(counter==3)
		break;
	videoList.add(videoMap);

 
}


int maxCount = 2;
int pageCount = (4-index)/maxCount;
if((4-index)%maxCount!=0)
	pageCount=pageCount+1;
//int maxCount = cs.getItemsPerPage();
int curPage = (index/maxCount)+1;

//context.put("img_"+0,imiParameters.get(0));
//context.put("img_"+1,imiParameters.get(1));
//int imisize=imiParameters.size();
//int toplistsize=2;



//    for(int i=0;i<toplistsize;i++)
//    {
//        context.put("imgtop_"+i,imiParameters.get(i));
//        //System.out.println("== IMIPARAM== "+imiParameters.get(i));
//	 //imiParameters.remove(i);
//    }
    
//    for(int i=0;i<toplistsize;i++)
//    {
//    context.put("proptop_"+i,prop_list.get(i));
//    //prop_list.remove(i);
//    }

//    for(int i=0;i<toplistsize;i++)
//    {
//    context.put("gtitletop_"+i,gtitle_list.get(i));
//    //gtitle_list.remove(i);
//    }
	
//	for(int i=0;i<toplistsize;i++)
//	{
//	context.put("ratingtop_"+i,rating_list.get(i));
//	//rating_list.remove(i);
//	}
	
//	for(int i=0;i<toplistsize;i++)
//	{
//	context.put("viewtop_"+i,view_list.get(i));
//	//view_list.remove(i);
//	}

//	for(int i=0;i<imiParameters.size();i++)
//	{
//	HashMap<String,Object> topMap=new HashMap<String,Object>();
//	topMap.put("imi",imiParameters.get(i));
//	topMap.put("prop",prop_list.get(i));
//	topMap.put("gtitle",gtitle_list.get(i));
//	topMap.put("view",view_list.get(i));
//	topMap.put("rating",rating_list.get(i));
//	videoList.add(topMap);
	
//	}
	
//for(int i=0;i<videoList.size();i++)
context.put("videoList",videoList);
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
  trans.commit();
  dbsession.close();
  
engine.getTemplate("index_clone").evaluate(writer, context);
String output = writer.toString();


%>
