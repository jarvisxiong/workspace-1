<%@ include file="coreimport.jsp" %>
<%
//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String uid = aReq.getUserId();
String ugid = aReq.getUserGroup();
String anyxSrvc = aReq.getAnyxService();
String domain = aReq.getDomain();
String enmsisdn=aReq.get("mid");
String msisdn=aReq.get("msisdn");
System.out.print("domainrr"+domain);
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "iso-8859-1";
response.setContentType("text/html; charset=" + pageEnc);


BannerAdDao banneraddao=null;
BannerLogDao bannerlogdao=null;

 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      banneraddao=(BannerAdDao) ac.getBean("banneraddao");
      bannerlogdao=(BannerLogDao) ac.getBean("bannerlogdao");
     
      }
      catch(Exception e){
          e.printStackTrace();
      }

 
//***************************************************************************************************

String unique = aReq.get("bunq");
if (unique.equals("")) return;

BannerAd item = banneraddao.getBanner(unique);
if (item==null) return;

bannerlogdao.logClick(item.getUnique(), item.getOwner(), item.getWebLink(), aReq.get("ip_addr"), item.getTitle(), request.getHeader("user-agent"));

String wapId = "";
String id = aReq.get("id");
if (id!=null) wapId = "&id=" + id;

System.out.println("&&&&&&&&& BANNER REDIRECT: " + item.getMobileLink());
//response.sendRedirect(item.getMobileLink()+wapId);
String enMsisdn=MiscCr.encrypt(msisdn);
response.sendRedirect(item.getMobileLink()+"&mid="+enMsisdn);
// return;
%>
