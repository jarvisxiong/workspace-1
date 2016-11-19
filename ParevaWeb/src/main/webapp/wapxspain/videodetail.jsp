<%@ include file="global-wap-header.jsp" %>
<%@ include file="xhtmlhead.jsp" %>

<%

 
String unique = aReq.get("unq");
String ticket = Misc.hex8Decode(aReq.get("tk"));

String cat = aReq.get("cat");
String sort = aReq.get("sort");
String fp = aReq.get("fp");
String newsUnique = aReq.get("nunq");
String index = aReq.get("ind");
String max = aReq.get("max");
String hexTitle = aReq.get("ttl");
String search = aReq.get("ss");
String cType = "";
String tempname = "clip";
String errMsg = "";

VideoClip item = videoclipdao.getItem(unique, UmeTempCmsCache.clientDomains.get(domain));

if (item==null) {
    try { application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/error.jsp").forward(request,response); }
    catch (Exception e) { System.out.println("videodetail Exception "+e); }
    return;    
}

// CLUB AUTHENTICATION START
Object[] status = mobileclubdao.authenticate(user, dmn, "video", item.getUnique());
int authnStatus = ((Integer) status[0]).intValue();
//System.out.println("authnStatus: " + authnStatus + ", club: " + club);
// CLUB AUTHENTICATION END

//System.out.println("PRICE: "  + item.getPriceGroup());

String price = (String) UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());
//System.out.println("PRICE: "  + price);

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
    //else price = "";
}
else errMsg = lp.get("authnerror_" + authnStatus);

//if (!price.equals("")) price = "(" + price + ")";



String params = "sort=" + sort + "&cat=" + cat + "&ind=" + index + "&ttl=" + hexTitle + "&ss=" + search + "&amp;max=" + max;

String desc = item.getDescription("punch", "es");
if (desc==null) desc = "";
//System.out.println("mexico testing "+ " video link  dllink: "+dllink);

/**********************************************************************************************************/

PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();

String headerlogo=(String)dImages.get("img_header1_" + handset.getImageProfile());
String footerlogo=(String)dImages.get("img_footer1_" + handset.getImageProfile());

//context.put("video_img",video_img);
context.put("item",item);
context.put("headerlogo",headerlogo);
context.put("footerlogo",footerlogo);
context.put("contenturl","http://"+ dmn.getContentUrl());
//context.put("desc",desc);
//context.put("price",price);
//context.put("fp",fp);
//context.put("params",params);
context.put("dllink",dllink);
context.put("path",item.getImagePath("webthumb", 0, 0));
//context.put("back",lp.get("back"));
//context.put("order",lp.get("order"));
//context.put("up",lp.get("up"));
//context.put("header_bg",header_bg);
//context.put("footer_bg",footer_bg);


//za_engine.getTemplate("videodetail").evaluate(writer, context);

/**********************************************************************************************************/
response.sendRedirect(dllink); return;
%>
