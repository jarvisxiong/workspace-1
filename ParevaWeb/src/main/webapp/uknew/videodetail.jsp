<%@include file="ukheader.jsp"%>
<%@include file="coreimport.jsp"%>
<%@include file="db.jsp" %>
<%@page import="org.hibernate.Hibernate"%>
<%@ include file="imiVariables.jsp"%>
<%@page import="com.imi.uk.DataDefination.classtype"%>
<%@page import="com.imi.uk.DataDefination"%>
<%@page import="com.imi.uk.imageProcess.IMIImageCreatorWithPlayIcon"%>
<%@page import="com.imi.uk.imageProcess.IMIImageParameters"%>

<%

String unique = httprequest.get("unq");
String ticket = Misc.hex8Decode(httprequest.get("tk"));

String cat = httprequest.get("cat");
String sort = httprequest.get("sort");
String fp = httprequest.get("fp");
String newsUnique = httprequest.get("nunq");
String index = httprequest.get("ind");
String max = httprequest.get("max");
String hexTitle = httprequest.get("ttl");
String search = httprequest.get("ss");
String cType = "";
String tempname = "clip";
String errMsg = "";
VideoClip item=null;
String sqlstr;
String srvc=httprequest.get("srvc");
String video_img="";
String data_content_id ="";
String videotitle="";
String gtitle="";
String contentIdUme="";
String landingPage="";

try{
    landingPage=request.getParameter("umelanding");
}catch(Exception e){System.out.println("uknew Exception video detail landingpage"+e);}//landingPage="landing_default";}


int pagination_index = Integer.parseInt(httprequest.get("ind", "0"));

try{
contentIdUme=request.getParameter("cntid");

}catch(Exception e){}
String defaultUrl = "http://" + dmn.getDefaultUrl();
String contentUrl="http://"+dmn.getContentUrl();

String imiMsisdn=httprequest.get("msisdn");
String imiAddCode_=request.getParameter("s");

java.util.List<String> imiParameters=new ArrayList<String>();
java.util.List rating_list=new ArrayList();
java.util.List view_list=new ArrayList();
java.util.List<HashMap> videoList=new ArrayList<HashMap>();

Transaction trans=dbsession.beginTransaction();

item = videoclipdao.getItem(unique, UmeTempCmsCache.clientDomains.get(domain));
ItemImage image = item.getItemImage("webthumb", 0, 0);
		if (image!=null) {
    
	video_img = image.getPath();
        videotitle=item.getTitle();
   // iw = image.getWidth();   
   // ih = image.getHeight();
	//img_list.add(img);
}
if (item==null) {
    try { application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/error.jsp").forward(request,response); }
    catch (Exception e) { System.out.println("VideoDetail Error "+e);e.printStackTrace();  
}
    return;    
}



Object[] status = mobileclubdao.authenticate(user, dmn, "video", item.getUnique());
int authnStatus = ((Integer) status[0]).intValue();

//System.out.println("videodetail.jsp authnSTatus value is "+authnStatus);

String price = "1";//(String) UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());

if (handset.get("playback_3gpp").equals("true") && item.getResourceMap().get("3gp")!=null) cType = "3gp";
else if (handset.get("playback_mp4").equals("true") && item.getResourceMap().get("mp4")!=null) cType = "mp4";
else if (handset.get("playback_mp4").equals("true") && item.getResourceMap().get("mpeg")!=null) cType = "mpeg";
else if (handset.get("playback_flv").equals("true") && item.getResourceMap().get("flv")!=null) cType = "flv";
else if (handset.get("playback_3g2").equals("true") && item.getResourceMap().get("3g2")!=null) cType = "3g2";
else if (handset.get("playback_mov").equals("true") && item.getResourceMap().get("mov")!=null) cType = "mov";
else if ((handset.get("playback_wmv").equals("7") || handset.get("playback_wmv").equals("8") || handset.get("playback_wmv").equals("9"))
        && item.getResourceMap().get("wmv")!=null) cType = "wmv";

String dllink = tempname + "." + cType + "?d=" + ddir + "&iunq=" + item.getUnique() + "&axud=1&itype=video&ctype=" + cType;

if (!ticket.equals("")) { dllink = ticket; price = ""; }
else if (authnStatus==0) {
    if (club.getType().equals("credit")) price = status[1] + " " + lp.get("credits2");
    
}
else errMsg = lp.get("authnerror_" + authnStatus);

if (!price.equals("")) price = "(" + price + ")";

String params = "sort=" + sort + "&cat=" + cat + "&ind=" + index + "&ttl=" + hexTitle + "&ss=" + search + "&amp;max=" + max;
String desc = item.getDescription("punch", lang);


if (desc==null) desc = "";
//System.out.println("DLLINK link : "+dllink);

//System.out.println("club option = "+club.getOptIn());
String finalurl=dllink;
//response.sendRedirect(finalurl);

%>


<%
String pfiHeaders="";
String Refrence_id="";
try{
pfiHeaders=request.getHeader("X-ESC-Alias"); //IMI header
if(pfiHeaders!=null && pfiHeaders.trim().length()>0) Refrence_id=pfiHeaders+"_"+campaignId;
if((imiMsisdn==null || imiMsisdn.trim().length()<=0) && (pfiHeaders!=null && pfiHeaders.trim().length()>0))
    imiMsisdn=pfiHeaders;
} catch(Exception e){}

//System.out.println("PFI Tag Header promo_hot_video.jsp "+pfiHeaders);

//Refrence_id=session.getAttribute("transaction-ref")+"";

     if(Refrence_id==null || Refrence_id.trim().length()<=0) Refrence_id=(String) request.getParameter("data-reference");
     if(Refrence_id==null || Refrence_id.trim().length()<=0) Refrence_id=(String) request.getParameter("transref");
     if(Refrence_id==null || Refrence_id.trim().length()<=0) Refrence_id=(String) session.getAttribute("transaction-ref")+"";

 //System.out.println("ReferenceID in Video detail page "+Refrence_id+"  data-reference Information "+request.getParameter("data-reference"));

  //java.util.List list = new VideoContent().getVideoList(dbsession,srvc, domain, UmeTempCmsCache.clientDomains.get(domain), unique);
  java.util.List list = new VideoContent().getRelatedVideoList(dbsession,srvc, domain, UmeTempCmsCache.clientDomains.get(domain));
String[] props = null;

		
		
		
VideoClipDao videoclipdao_=null;
 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      videoclipdao_=(VideoClipDao) ac.getBean("videoclipdao");
		 
      }
      catch(Exception e){
          e.printStackTrace();
      }	

if (list.size()>0) { 
	 int counter=0;
for(int i=pagination_index;i<list.size();i++){
	HashMap<String,Object> videoMap=new HashMap<String,Object>();

String img="";  
     
    props = (String[]) list.get(i);
    if (props!=null && !props[0].equals("")) {
        item = videoclipdao.getItem(props[0]);
		image = item.getItemImage("webthumb", 0, 0);
		if (image!=null) {    
	img = image.getPath();
}
	
		gtitle = item.getDescription("title", lang);
		videoMap.put("gtitle",gtitle);	

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
     //ee.printStackTrace();
    }

    String data_serviceid ="196"; // item.getUnique();
    data_content_id = item.getUnique();
    String data_reference = Refrence_id;
    String data_content_location = "http://"+dmn.getDefaultUrl()
      + "/videodetail.jsp?unq=" + props[0]+"&srvc=" + srvc+"&data-reference="+data_reference+"&cntid=" + data_content_id+"&msisdn=" + imiMsisdn+"&s=" + imiAddCode_+"&cid="+campaignId+"&umelanding="+landingPage;
    //String thumbnail_path = "http://" + dmn.getContentUrl()+ "/images/imiuk/" + finalImageName;
    // String thumbnail_path="http://"+dmn.getContentUrl()+img;
    String thumbnail_path = "http://" + dmn.getContentUrl() + "/images/18streamuk425/" + finalImageName;
    String content_price="<thumbclickable><a class =\"billable block video-link\" href=\"javascript:void(0)\" data-clickable=\"true\"></a> </thumbclickable><div id=\""+data_content_id+"\"> <div class=\"priceInfo\" style=\"display: none;\"><font size=\"2\" color=\"white\">Click to buy and <b>&nbsp;<span class=\"content-price\">"+imi_price+"</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>";
   
    StringBuffer values=new StringBuffer();


String space=" ", type="thumbnail";

    values.append("<img src=\""+thumbnail_path+"\"  alt=\""+"stream18 videos"+"\" />");
    values.append("<div"+space);  
    values.append("data-serviceid=\""+data_serviceid+"\""+space );
    values.append("data-content-location=\""+data_content_location+"\""+space );
    values.append("data-reference=\""+data_reference+"\""+space );
    values.append("data-content-id=\""+data_content_id+"\">"+space );       
    //values.append("</div>");
    imiParameters.add(values.toString());
    videoMap.put("imi",values.toString());
    
    int ratingNumber = 80 + (int)(Math.random() * ((100 - 80) + 1));
    int viewsNumber =  (int)(Math.random() * ((80000 - 65000) + 1));
    videoMap.put("ratingNumber",ratingNumber);
    videoMap.put("viewsNumber",viewsNumber);
    rating_list.add(ratingNumber);
    view_list.add(viewsNumber);
   		}
    counter=counter+1;
	//System.out.println("COUNTER = "+counter);
	
    videoList.add(videoMap);
    if(counter==4)
		break;
    }

}

int maxCount = 6;
int pageCount = (list.size()-pagination_index)/maxCount;
if((list.size()-pagination_index)%maxCount!=0)
	pageCount=pageCount+1;
//int maxCount = cs.getItemsPerPage();
int curPage = (pagination_index/maxCount)+1;

////////////////////////////////////////////////////////////////////////////////////////////////////




java.util.List relatedList=new ArrayList();
java.util.List ymal=new VideoContent().getTopVideoList(dbsession,srvc, domain, UmeTempCmsCache.clientDomains.get(domain));


for(int i=0;i<ymal.size();i++){
	HashMap<String,Object> videoMap=new HashMap<String,Object>();
 
        if(i==4) break;
	String img="";  
	     
	    props = (String[]) ymal.get(i);
	    if (props!=null && !props[0].equals("")) {
	        item = videoclipdao.getItem(props[0]);
			image = item.getItemImage("webthumb", 0, 0);
			if (image!=null) {    
		img = image.getPath();
	}
		
			gtitle = item.getDescription("title", lang);
			videoMap.put("gtitle",gtitle);	

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
     //ee.printStackTrace();
    }

	    String data_serviceid ="196"; // item.getUnique();
	    data_content_id = item.getUnique();
	    String data_reference = Refrence_id;
	    String data_content_location = "http://"+dmn.getDefaultUrl()
	      + "/videodetail.jsp?unq=" + props[0]+"&srvc=" + srvc+"&data-reference="+data_reference+"&cntid=" + data_content_id+"&msisdn=" + imiMsisdn+"&s=" + imiAddCode_+"&cid="+campaignId+"&umelanding="+landingPage;
	    //String thumbnail_path = "http://" + dmn.getContentUrl()+ "/images/imiuk/" + finalImageName;
	    // String thumbnail_path="http://"+dmn.getContentUrl()+img;
            String thumbnail_path = "http://" + dmn.getContentUrl() + "/images/18streamuk425/" + finalImageName;
	    String content_price="<thumbclickable><a class =\"billable block video-link\" href=\"javascript:void(0)\" data-clickable=\"true\"></a> </thumbclickable><div id=\""+data_content_id+"\"> <div class=\"priceInfo\" style=\"display: none;\"><font size=\"2\" color=\"white\">Click to buy and <b>&nbsp;<span class=\"content-price\">"+imi_price+"</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>";
	   
	    StringBuffer values=new StringBuffer();


	String space=" ", type="thumbnail";

	    values.append("<img src=\""+thumbnail_path+"\"  alt=\""+"stream18 videos"+"\" />");
	    values.append("<div"+space);  
	    values.append("data-serviceid=\""+data_serviceid+"\""+space );
	    values.append("data-content-location=\""+data_content_location+"\""+space );
	    values.append("data-reference=\""+data_reference+"\""+space );
	    values.append("data-content-id=\""+data_content_id+"\">"+space );       
	    //values.append("</div>");
	    imiParameters.add(values.toString());
	    videoMap.put("imi",values.toString());
	    
	    int ratingNumber = 80 + (int)(Math.random() * ((100 - 80) + 1));
	    int viewsNumber =  (int)(Math.random() * ((80000 - 65000) + 1));
	    videoMap.put("ratingNumber",ratingNumber);
	    videoMap.put("viewsNumber",viewsNumber);
	    rating_list.add(ratingNumber);
	    view_list.add(viewsNumber);
	   		}
	    
		relatedList.add(videoMap);
                
	    }





%>

<%
PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();
  
try{
   String clubUnique = "";
    try{        
if (club!=null) clubUnique = club.getUnique();
   }catch(Exception e){clubUnique="";}
  
    
Cookie[] cookies = request.getCookies();
 Cookie cookie = null;
 boolean saleLog=true;
 
 
 if(session.getAttribute(imiMsisdn)!=null && session.getAttribute(imiMsisdn).equals(contentIdUme))
 {
     System.out.println("SALELOGVALUE ::>> "+saleLog+" FOR MSISDN "+imiMsisdn+" SESSION ITEM "+session.getAttribute(imiMsisdn));
     saleLog=false;
     
 }
 
 
//    if( cookies != null ){
//      //System.out.println("Found Cookies Name and Value with cookies length "+cookies.length);
//      for (int i = 0; i < cookies.length; i++)
//      {
//         cookie = cookies[i];
//         //System.out.print("Name : " + cookie.getName() + ":");
//         //System.out.println("Value: " + cookie.getValue()+" ");
//         if(cookie.getName().equals(contentIdUme) && cookie.getValue().equals(contentIdUme)){
//             saleLog=false;
//             break;
//         }
//      }
//    }
    
    if(saleLog){
       
    campaigndao.log("index",landingPage, Refrence_id, imiMsisdn, handset,domain, campaignId, clubUnique, "SALE", 0, request,response,"imi");
    //System.out.println("CONTENTID VALUE :"+contentIdUme);
     if(contentIdUme!=null && contentIdUme.trim().length()>0){
 //System.out.println("Video contentid "+contentIdUme);
        Cookie added  = new Cookie(contentIdUme,contentIdUme);
        added.setPath("/");
        added.setMaxAge(600*600*24); 
        //System.out.println("SALELOGVALUE "+"ADDING COOKIE NOW "+added.getName()+" VALUE :"+added.getValue()+" FOR MSISDN "+imiMsisdn);
        response.addCookie(added);
        System.out.println("SALELOGVALUE "+" Setting contentid for msisdn "+imiMsisdn+" to "+contentIdUme+ "at "+new Date());
        session.setAttribute(imiMsisdn,contentIdUme);
      }
}
}
catch(Exception e){}

String headerlogo="5849590138141KDS.jpg";
String footerlogo="5849590138141KDS.jpg";//(String)dImages.get("img_footer1_" + handset.getImageProfile());

//System.out.println("==== PATH TO VIDEO LINK===== "+defaultUrl+"/"+dllink);
//System.out.println("==== IMAGE of VIDEODETAIL ===== "+contentUrl+video_img);
context.put("maxCount",maxCount);
context.put("pageCount",pageCount);
context.put("curPage",curPage);
context.put("selectedtitle",videotitle);
context.put("video_img","http://"+dmn.getContentUrl()+video_img);
context.put("unique",unique);
context.put("item",item);
context.put("headerlogo",headerlogo);
context.put("footerlogo",footerlogo);
context.put("desc",desc);
context.put("price",price);
context.put("fp",fp);
context.put("params",params);
context.put("dllink",defaultUrl+"/"+dllink);
context.put("path",item.getImagePath("webthumb", 0, 0));
context.put("back",lp.get("back"));
context.put("order",lp.get("order"));
context.put("up",lp.get("up"));
context.put("length_3","02:59");
context.put("defaulturl","http://"+dmn.getDefaultUrl());
context.put("pricing","<div id=\""+data_content_id+"\"><div class=\"priceInfo\"><font size=\"2\" color=\"white\">Click to buy and <b>&nbsp;<span class=\"content-price\">&pound;6</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>");
context.put("websitetitle","UME-Video Download");
context.put("contenturl","http://"+dmn.getContentUrl());
context.put("transref",Refrence_id);
context.put("videoList",videoList);
context.put("unique",unique);
context.put("relatedList",relatedList);
context.put("contentprice",contentprice);

//for(int i=0;i<imiParameters.size();i++){
 //   HashMap<String,Object> topMap=new HashMap<String,Object>();
 //   context.put("img_"+i,imiParameters.get(i));
  //  topMap.put("imi",imiParameters.get(i));
   // topMap.put("view",view_list.get(i));
  //  topMap.put("rating",rating_list.get(i));
  //  videoLists.add(topMap);
    

//}

//for(int i=0;i<videoLists.size();i++)
//context.put("videoList",videoLists);
//System.out.println("VIDEOLIST SIZE IN VIDEO DOWNLOAD PAGE IS "+videoLists.size());

trans.commit();
dbsession.close();

//engine=(PebbleEngine)this.getServletContext().getAttribute("uk_engine");
engine.getTemplate("videodownload").evaluate(writer, context);

%>