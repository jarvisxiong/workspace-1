<%@include file="coreimport.jsp"%>
<%
System.out.println("******************************VIDEODETAIL.JSP**********************************");

UmeSessionParameters aReq = new UmeSessionParameters(request);
UmeDomain dmn = aReq.getDomain();
SdcService service = aReq.getService();
String unique = aReq.get("unq");
String cType = "";
String tempname = "clip";
String errMsg = "";

String domain = dmn.getUnique();
String ddir = dmn.getDefPublicDir();
VideoClipDao videoclipdao=null;
try{
     ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      videoclipdao=(VideoClipDao) ac.getBean("videoclipdao");
     
}catch(Exception e){e.printStackTrace();}



VideoClip item = videoclipdao.getItem(unique, UmeTempCmsCache.clientDomains.get(domain));

if (item==null) {
    try { application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/error.jsp").forward(request,response); }
    catch (Exception e) { System.out.println("videodetail Exception "+e); }
    return;    
}


	if(item.getResourceMap().get("mp4")!=null) 
		cType = "mp4";
	else if(item.getResourceMap().get("3gp")!=null) 
		cType = "3gp";
	
String dllink = tempname + "." + cType + "?d=" + ddir + "&iunq=" + item.getUnique() + "&axud=1&itype=video&ctype=" + cType;

response.sendRedirect(dllink);
%>
