<%
// Subpage
String __subpage = Misc.getCookie("_MXMSUBPAGE", request);

// Handset must be defined and initialized
Map dImages = (Map) ume.pareva.cms.UmeTempCmsCache.xhtmlImages.get("xhtml_" + domain);
if (dImages==null) dImages = new Hashtable();

String __domainName = System.getProperty(domain + "_name").toLowerCase();
String __host = request.getHeader("host");

System.out.println("HOST: " + __host);
System.out.println("domain: " + __domainName + ": " + domain);
System.out.println("IP_ADDR: " + request.getRemoteAddr());

String __font1 = "medium";
String __font2 = "large";
String __font3 = "x-small";
String thumbnailTd = "";
if(handset.getXhtmlProfile()==1){
	thumbnailTd = "#thumbnailTd{ width:34px;}";
	__font1 = "x-small"; __font2 = "x-small";
	__font3 = "7px";
}else if(handset.getXhtmlProfile()==2){
	thumbnailTd = "#thumbnailTd{ width:64px;}";
	__font1 = "small"; __font2 = "small";
}else if(handset.getXhtmlProfile()==3){
	thumbnailTd = "#thumbnailTd{ width:96px;}";
	__font3 = "x-small";	
}else{
	thumbnailTd = "#thumbnailTd{ width:132px;}";
	__font3 = "small";
}

String snpUrl = "";
%>

<%@ include file="xhtmlhead_ume.jsp" %>


