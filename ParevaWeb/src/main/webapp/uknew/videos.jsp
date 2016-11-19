<%@ include file="imiVariables.jsp"%>

<%@page import="com.imi.uk.DataDefination.classtype"%>
<%@page import="com.imi.uk.DataDefination"%>
<%@page import="com.imi.uk.imageProcess.IMIImageCreatorWithPlayIcon"%>
<%@page import="com.imi.uk.imageProcess.IMIImageParameters"%>
<%@ include file="coreimport.jsp"%>
<%@ include file="db.jsp"%>
<%@ include file="ukheader.jsp"%>

<%
java.util.List<String> imiParameters=new ArrayList<String>();
String imiMsisdn=httprequest.get("msisdn");
String imiAddCode_=request.getParameter("s");

Transaction trans=dbsession.beginTransaction();

String srvc="";
String cat = httprequest.get("cat");
String sort = httprequest.get("sort");
int index = Integer.parseInt(httprequest.get("ind", "0"));
int max = Integer.parseInt(httprequest.get("max", "10"));
String hexTitle = httprequest.get("ttl");
String title = Misc.utfToUnicode(Misc.hex8Decode(hexTitle), "utf-8");
String search = httprequest.get("ss").trim().toLowerCase();
String data_content_id="";
String landingPage="";
try{
request.getParameter("umelanding");
}catch(Exception e){System.out.println("uknew Exception videos.jsp parameter reading  landingPage "+e);}

if(landingPage==null) {
    try{
    
    landingPage=(String) request.getAttribute("landingPage");
    }
    catch(Exception e){System.out.println("uknew Exception videos.jsp attribute reading landingPage"+e);}
    
}

request.setAttribute("title",title);
request.setAttribute("search",search);

String pfiHeaders="";
String Refrence_id="";
try{
pfiHeaders=request.getHeader("X-ESC-Alias"); //IMI Header
if(pfiHeaders!=null && pfiHeaders.trim().length()>0)Refrence_id=pfiHeaders+"_"+campaignId;
if((imiMsisdn==null || imiMsisdn.trim().length()<=0) && (pfiHeaders!=null && pfiHeaders.trim().length()>0))
    imiMsisdn=pfiHeaders;
} catch(Exception e){}
//System.out.println("PFI Tag Header videos.jsp "+pfiHeaders);



if(Refrence_id==null || Refrence_id.trim().length()<=0) Refrence_id=(String) request.getParameter("transref");//session.getAttribute("transaction-ref")+"";
if(Refrence_id==null || Refrence_id.trim().length()<=0) Refrence_id=(String) request.getParameter("transaction-ref");
if(Refrence_id==null || Refrence_id.trim().length()<=0) Refrence_id=session.getAttribute("transaction-ref")+"";

ContentSet cs = new ContentSet();
cs.setSortString(sort);
cs.setDomain(domain);
cs.setHandset(handset);
cs.setItemsPerPage(max);
cs.setPageIndex(index);
cs.setCategory(cat);
cs.setNumberOfTopPages(20);
cs.setSearchString(search);
cs.setTimeSpan(300);
cs.setClassification("hardcore");

if (dParamMap.get("classification")!=null) cs.setClassification(dParamMap.get("classification"));

cs.setClassification("hardcore");
videoclipdao.populate(cs);
java.util.List list = cs.getList();



java.util.List<ItemCategory> catlist = UmeTempCmsCache.itemCategoryMap.get("video");


int count = cs.getItemCount();
int pageCount = cs.getPageCount();
int maxCount = cs.getItemsPerPage();
int curPage = (index/maxCount)+1;

String params2 = "sort=" + sort + "&amp;cat=" + cat + "&amp;ttl=" + hexTitle + "&amp;ss=" + java.net.URLEncoder.encode(search) + "&amp;max=" + max;
String params = params2 + "&amp;ind=" + index + "&amp;fp=videos";

request.setAttribute("list",list);

System.out.println("Parameter2 ="+params2);
System.out.println("Category ="+cat);
request.setAttribute("pageCount",pageCount);
request.setAttribute("maxCount",maxCount);
request.setAttribute("curPage",curPage);
request.setAttribute("params2",params2);
request.setAttribute("params",params);



%>

<% 
java.util.List<HashMap> videoList=new ArrayList<HashMap>();

VideoClip item = null;
ItemImage image = null;

String img="";
int iw = 0;
int ih = 0;
String sqlstr = "";
//System.out.println("ITME LIST SIZE: " + list.size());
   for (int i=0; i<list.size(); i++) {
		HashMap<String,Object> videos=new HashMap<String,Object>();
        item = (VideoClip) list.get(i);
        image = item.getItemImage("webthumb", 0, 0);
		if(image==null){
		image=  item.getItemImage("mobthumb", 0, 0);
		}
        img = "";
	
        if (image!=null) {
            img = image.getPath();
			//System.out.println("Image Path = "+img);
            iw = image.getWidth();
            ih = image.getHeight();

			request.setAttribute("img",img);
			request.setAttribute("iw",iw);
			request.setAttribute("ih",ih);
			
		}
		
		/*****************************************************************/

int value = img.lastIndexOf('/');
    String imageName = img.substring(value + 1);
    //imageName = imageName.toLowerCase();
    String finalImageName = imageName;
	//System.out.println("finalImage = "+finalImageName);
    try {

     String pathx = imi_path + finalImageName;

     IMIImageParameters parameters = new IMIImageParameters("http://" + dmn.getContentUrl() + img,"","", pathx);
     
     //System.out.println("img url = http://"+ dmn.getDefaultUrl() + img);
     //System.out.println("hd png url = http://" +dmn.getDefaultUrl()+hd_png);
     //System.out.println("pathx = "+pathx);
     

     IMIImageCreatorWithPlayIcon imgcrt = new IMIImageCreatorWithPlayIcon(
       parameters);
     imgcrt.createImage();
    } catch (Exception ee) {
     System.out.println("Exception in uknew videos.jsp line no 146 "+ee);
    }

    String data_serviceid ="196"; // item.getUnique();
    data_content_id = item.getUnique();
    String data_reference = Refrence_id;
     String data_content_location = "http://" + dmn.getDefaultUrl()
      + "/videodetail.jsp?unq=" + item.getUnique()+"&srvc=" + srvc+"&data-reference="+data_reference+"&cntid=" + data_content_id+"&msisdn=" + imiMsisdn+"&s=" + imiAddCode_+"&cid="+campaignId+"&umelanding="+landingPage;
    //String thumbnail_path = "http://" + dmn.getContentUrl()+ "/images/imiuk/" + finalImageName;
     String thumbnail_path = "http://" + dmn.getDefaultUrl() + "/images/18streamuk425/" + finalImageName;
    //String content_price="<div id=\""+data_content_id+"\"><div class=\"priceInfo\"><font size=\"2\" color=\"white\">click to buy and <b>&nbsp;<span class=\"content-price\">"+imi_price+"</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>";
String content_price="<thumbclickable><a class =\"billable block video-link\" href=\"javascript:void(0)\" data-clickable=\"true\"></a></thumbclickable><div id=\""+data_content_id+"\"> <div class=\"priceInfo\" style=\"display: none;\"><font size=\"2\" color=\"white\">Click to buy and <b>&nbsp;<span class=\"content-price\">"+imi_price+"</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>";

    /*DataDefination datadefination = new DataDefination(
      classtype.thumbnail, data_serviceid, data_reference,
      data_content_location, data_content_id, thumbnail_path,
      content_price);
    
    datadefination.setImageCss("thumbnail videoCover");
    
    //imiParameters.add(datadefination.toString());
    //System.out.println("uk imi  content: " + datadefination);*/

StringBuffer values=new StringBuffer();


String space=" ", type="thumbnail";

    values.append("<img src=\""+thumbnail_path+"\"  alt=\""+"stream18 videos"+"\" />");
    values.append("<div"+space);  
    values.append("data-serviceid=\""+data_serviceid+"\""+space );
    values.append("data-content-location=\""+data_content_location+"\""+space );
    values.append("data-reference=\""+data_reference+"\""+space );
    values.append("data-content-id=\""+data_content_id+"\">"+space );       
    //values.append("</div>");
    
    //videoMap.put("imi",values.toString());

/*****************************************************************/

		
		
		
  
	
    int views = new VideoContent().getItemViewed(dbsession,item.getUnique());
	
        int ratingNumber = 80 + (int)(Math.random() * ((100 - 80) + 1));
        int viewsNumber = 65000 + views;
	request.setAttribute("ratingNumber",ratingNumber);
	request.setAttribute("viewsNumber",viewsNumber);
	String description=item.getDescription("title",lang);
	videos.put("item",item);
	videos.put("image",image);
	//videos.put("img",img);
	videos.put("imiParameters",values.toString());
	
	videos.put("iw",iw);
	videos.put("ih",ih);
	videos.put("ratingNumber",ratingNumber);
	videos.put("viewsNumber",viewsNumber);
	videos.put("description",description);
	videoList.add(videos);
}	
	
PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();
  
trans.commit();
dbsession.close();
String header_logo="";//(String)dImages.get("img_header1_" + handset.getImageProfile());
String footerlogo="";//(String)dImages.get("img_footer1_" + handset.getImageProfile());

context.put("videoList",videoList);
context.put("headerlogo",header_logo);
context.put("footerlogo",footerlogo);
context.put("search",search);
context.put("title",title);
context.put("handset",handset);
context.put("pageCount",pageCount);
context.put("curPage",curPage);
context.put("params2",params2);
context.put("maxCount",maxCount);
context.put("max",max);
context.put("params",params);
context.put("previous",lp.get("previous"));
context.put("next",lp.get("next"));
context.put("up",lp.get("up"));
context.put("defaulturl","http://"+dmn.getDefaultUrl());
context.put("pricing","<div id=\""+data_content_id+"\"><div class=\"priceInfo\"><font size=\"2\" color=\"white\">Click to buy and <b>&nbsp;<span class=\"content-price\">&pound;6</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>");
context.put("websitetitle","UME-UK Categories");
context.put("contenturl","http://"+dmn.getContentUrl());
context.put("transref",Refrence_id);
context.put("contentprice",contentprice);

//engine=(PebbleEngine)this.getServletContext().getAttribute("uk_engine");
engine.getTemplate("videos").evaluate(writer, context);

	%>
	