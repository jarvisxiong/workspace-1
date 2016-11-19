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

int pagination_index = Integer.parseInt(httprequest.get("ind", "0"));

try{
String contentIdUme=request.getParameter("cntid");


 if(contentIdUme!=null && contentIdUme.trim().length()>0){
 //System.out.println("Video contentid "+contentIdUme);
 Cookie added  = new Cookie(contentIdUme,contentIdUme);
 added.setPath("/");
     response.addCookie(added);
   }
}catch(Exception e){}
String defaultUrl = "http://" + dmn.getDefaultUrl();
//String contentUrl="http://"+dmn.getDefaultUrl();
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
String activationCode = "409d-2c20-ded1-856e";
String interstitial="22592"; 
HashMap<String,String> zones = new HashMap<String,String>();
zones.put("ad_interstitial", interstitial);
ReporoAds reporoService = ReporoAds.getNewInstance();
reporoService.setActivationCode(activationCode); 
reporoService.setBlockBanner(true); 
reporoService.setBlockCampaign(false);

HashMap<String,ReporoAdBean> ads = reporoService.getAds(zones, request.getHeader("X-Forwarded-For"), request.getHeader("User-Agent"));

int mcount = 6;

int currentpage = (pagination_index/mcount)+1;

String interstitialad=ads.get("ad_interstitial").getMarkup();
interstitialad=interstitialad.replace("'", "\"");
PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();
context.put("finalUrl",dllink);
context.put("interstitial",interstitialad);
context.put("contenturl",contentUrl);
context.put("curPage",currentpage);
engine.getTemplate("advertisement").evaluate(writer, context);

//String finalurl=dllink;
//response.sendRedirect(finalurl);

%>
