<%@include file="coreimport.jsp" %>
<%@include file="db.jsp"%>
<%@include file="readparam.jsp"%>
<%
try {
String uid = "";
if (user!=null) uid = user.getParsedMobile();
String msisdn = "";
String operator = "";
String pageEnc = "utf-8";

//***************************************************************************************************

Map<String, String> dParamMap = UmeTempCmsCache.domainParameters.get(domain);
if (dParamMap==null) dParamMap = new HashMap<String,String>();


MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
SdcMobileClubUser clubUser = null;




Handset handset=(Handset)session.getAttribute("handset");
String ug = request.getHeader("user-agent");


String defCP = "ADC1211946194726";


String sqlstr = "";

String clientId = (String) UmeTempCmsCache.clientDomains.get(domain);

String iUnique = httprequest.get("iunq").trim();
String iType = httprequest.get("itype").trim().toLowerCase();
String cType = httprequest.get("ctype");
String iLang = httprequest.get("ilang", lang);
String iParam = httprequest.get("iparam");

String itemTicket = "";
System.out.println("ITYPE = "+iType);



int authnStatus=0;
Object[] status = null;

if(club.getOptIn() == 0){
    authnStatus = 0;
}
MobileClubDao mobileclubdao=null;
VideoClipDao videoclipdao=null;
ClientDao clientdao=null;
UmeMobileClubUserDao umemobileclubuserdao=null;
ContentProviderDao contentproviderdao=null; 
ItemTicket itemtickets=null;
 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      clientdao=(ClientDao) ac.getBean("clientdao");
	  videoclipdao=(VideoClipDao)ac.getBean("videoclipdao");
	  contentproviderdao=(ContentProviderDao)ac.getBean("contentproviderdao");
	  umemobileclubuserdao=(UmeMobileClubUserDao)ac.getBean("umemobileclubuserdao");
      itemtickets=(ItemTicket) ac.getBean("itemticket");
	  mobileclubdao=(MobileClubDao)ac.getBean("mobileclubdao");
 
      }
      catch(Exception e){
          e.printStackTrace();
      }



status = mobileclubdao.authenticate(user, dmn, iType, iUnique);
authnStatus = ((Integer) status[0]).intValue();




boolean itemFound = false;
String logUnique = Misc.generateUniqueId() + Misc.generatePw(4); 





Client client = clientdao.getClient(clientId);
if (client==null) 
client = clientdao.getClient(System.getProperty("CMS_defaultClient"));
ContentItem contentItem = null;

if (iType.indexOf("video")>-1) {
    contentItem = videoclipdao.getItem(iUnique);
    if (cType.equals("")) cType = "mp4";
}

if (contentItem!=null) {
    
    ContentProvider cp = contentproviderdao.getContentProvider(contentItem.getOwner());
    if (cp==null) cp = contentproviderdao.getContentProvider(defCP);

    itemTicket = itemtickets.get(contentItem, client.getUnique(), msisdn, logUnique, cType, iType, iLang, iParam, dmn.getDefaultUrl());
	System.out.println("ITEM TICKET = "+itemTicket);
    if (itemTicket.startsWith("http://")) {

        itemFound = true;
            //logger.log();
    }

}

if (itemFound) {
   
    response.sendRedirect(itemTicket);
    
} else {
   response.setContentType(handset.getContentType(pageEnc));
}

return;
} catch (Exception e) { System.out.println("fromDefaultServlet: " + e); e.printStackTrace();}
%>