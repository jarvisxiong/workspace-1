<%@include file="coreimport.jsp" %>
<%@include file="db.jsp"%>
<%
try {
System.out.println("FromDEfaultServlet called upon ");
//***************************************************************************************************
SdcRequest aReq = new SdcRequest(request);
UmeUser user = aReq.getUser();
String uid = "";
if (user!=null) uid = user.getParsedMobile();


UmeDomain dmn = aReq.getDomain();
SdcService service = aReq.getService();
String msisdn = "";
String operator = "";
String domain = dmn.getUnique();
String lang = aReq.getLanguage().getLanguageCode();
String stylesheet = aReq.getStylesheet();
String pageEnc = aReq.getEncoding();

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

String iUnique = aReq.get("iunq").trim();
String iType = aReq.get("itype").trim().toLowerCase();
String cType = aReq.get("ctype");
String iLang = aReq.get("ilang", lang);
String iParam = aReq.get("iparam");

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
BgImageDao bgimagedao=null;
JavaGameDao javagamedao=null;
GifAnimDao gifanimdao=null;
RingtoneDao ringtonedao=null;
UmeMobileClubUserDao umemobileclubuserdao=null;
ContentProviderDao contentproviderdao=null; 
ZAItemTicket zaitemtickets=null;
 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      clientdao=(ClientDao) ac.getBean("clientdao");
	  videoclipdao=(VideoClipDao)ac.getBean("videoclipdao");
	  bgimagedao=(BgImageDao)ac.getBean("bgimagesdao");
	  javagamedao=(JavaGameDao)ac.getBean("javagamedao");
	  gifanimdao=(GifAnimDao)ac.getBean("gifanimdao");
	  ringtonedao=(RingtoneDao)ac.getBean("ringtonedao");
	  contentproviderdao=(ContentProviderDao)ac.getBean("contentproviderdao");
	  umemobileclubuserdao=(UmeMobileClubUserDao)ac.getBean("umemobileclubuserdao");
      zaitemtickets=(ZAItemTicket) ac.getBean("zaitemticket");
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
System.out.println("clientid: " + client);
ContentItem contentItem = null;
MastertoneDao mastertonedao=null;
try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      mastertonedao=(MastertoneDao) ac.getBean("mastertonedao");
     System.out.println("FromdefaultServlet Testing: MasterToneDao:> "+mastertonedao);
      }
      catch(Exception e){
          e.printStackTrace();
      }

if (iType.equals("master") || iType.equals("true") || iType.equals("fun") || iType.equals("real")) {
    contentItem = mastertonedao.getItem(iUnique);
    if (cType.equals("")) cType = "mp3";

    if (contentItem!=null) {
        if (contentItem.getOwner().equalsIgnoreCase("sony")) contentItem.setOwner("ADC1215681657877");
        else if (contentItem.getOwner().equalsIgnoreCase("emi")) contentItem.setOwner("ADC1215681657879");
        else if (contentItem.getOwner().equalsIgnoreCase("warner")) contentItem.setOwner("ADC1215681657878");
        else if (contentItem.getOwner().equalsIgnoreCase("edel")) contentItem.setOwner("4487087323421CDA");
        else if (contentItem.getOwner().equalsIgnoreCase("mixmobile")) contentItem.setOwner("ADC1211946194726");
        else if (contentItem.getOwner().equalsIgnoreCase("mixem")) contentItem.setOwner("ADC1211946194726");
        else if (contentItem.getOwner().equalsIgnoreCase("audicon")) contentItem.setOwner("3487087323421CDA");
    }
}
else if (iType.indexOf("video")>-1) {
    contentItem = videoclipdao.getItem(iUnique);
    if (cType.equals("")) cType = "3gp";
}
else if (iType.indexOf("java")>-1) {
    contentItem = javagamedao.getItem(iUnique);    
    cType = "jad";
}
else if (iType.indexOf("bg")>-1) {
    contentItem = bgimagedao.getItem(iUnique);    
    cType = "jpg";

    if (contentItem!=null) {
        if (contentItem.getOwner().equalsIgnoreCase("mixmobile")) contentItem.setOwner("ADC1211946194726");
        else if (contentItem.getOwner().equalsIgnoreCase("mixem")) contentItem.setOwner("ADC1211946194726");
        else if (contentItem.getOwner().equalsIgnoreCase("dreamsection")) contentItem.setOwner("4274936239121CDA");
        else if (contentItem.getOwner().equalsIgnoreCase("daydream")) contentItem.setOwner("2199438846221CDA");
        else if (contentItem.getOwner().equalsIgnoreCase("arphiola")) contentItem.setOwner("3199438846221CDA");
    }
}
else if (iType.indexOf("poly")>-1) {
    contentItem = ringtonedao.getItem(iUnique);
    cType = "mid";

    if (contentItem!=null) {
        if (contentItem.getOwner().equalsIgnoreCase("drystone")) contentItem.setOwner("7469087323421CDA");
        else if (contentItem.getOwner().equalsIgnoreCase("mixmobile")) contentItem.setOwner("ADC1211946194726");
        else if (contentItem.getOwner().equalsIgnoreCase("mixem")) contentItem.setOwner("ADC1211946194726");
    }
}
else if (iType.indexOf("anim")>-1) {
    contentItem = gifanimdao.getItem(iUnique);
    cType = "gif";
}

if (contentItem!=null) {
    
    ContentProvider cp = contentproviderdao.getContentProvider(contentItem.getOwner());
    if (cp==null) cp = contentproviderdao.getContentProvider(defCP);

    itemTicket = zaitemtickets.get(contentItem, client.getUnique(), msisdn, logUnique, cType, iType, iLang, iParam, dmn.getDefaultUrl());
	System.out.println("ITEM TICKET = "+itemTicket);
    if (itemTicket.startsWith("http://")) {

        itemFound = true;
        ItemLogger logger = new ItemLogger(logUnique);
        logger.itemTicket = itemTicket;
        logger.itemUnique = iUnique;
        logger.itemType = iType;
        logger.billingType = "wap";
        logger.status = "";
        logger.statusDesc = "";
        logger.retrieveCount = 0;
        logger.orderCommand = "";
        logger.file = "";
        logger.fileType = "";
        logger.uid = "";
        logger.parsedMobile = msisdn;
        //logger.phoneUnique = handset.getUnique();
        //logger.drmSupport = "forwardLock: " + handset.supportsForwardLock();
        logger.userAgent = ug; 
        logger.operator = "";
        logger.priceGroup = contentItem.getPriceGroup();
        if (logger.priceGroup==0) logger.priceGroup = 1;
        try { logger.currencyCode = (String) UmeTempCmsCache.domainPriceGroups.get(domain + "_" + logger.priceGroup + "_code");
            } catch (NullPointerException ex) { System.out.println("currencyCode " + ex); }
        try { logger.endUserPrice = ((Double) UmeTempCmsCache.domainPriceGroups.get(domain + "_" + logger.priceGroup + "_price")).doubleValue();
            } catch (NullPointerException ex) { System.out.println("endUserPrice " + ex); }
        logger.premiumIncome = logger.endUserPrice;
        logger.client = client.getUnique();
        logger.clientShare = 0;
        logger.domain = domain;
        logger.owner = cp.getUnique();
        logger.ownerShare = cp.getRevShare(iType, logger.premiumIncome);
        if (club!=null) logger.clubUnique = club.getUnique();
        if (clubUser!=null) logger.reseller = clubUser.getCampaign();
       

	   
        if (authnStatus==0) {
            logger.billingType = club.getType();    
            
            if(club.getOptIn() == 0){
                logger.endUserPrice = 0;
            }else{
                logger.endUserPrice = ((Integer) status[1]).intValue();
                if (club.getType().equals("credit")) umemobileclubuserdao.subtract((SdcMobileClubUser) status[2], ((Integer) status[1]).intValue());
            
			}
        }
        else if (club!=null) {

	    if (!(club.getRegion().equals("KE"))) {

	    

            } else { 

            
	    }
        }
        logger.log();
    }

}

if (itemFound) {
//System.out.println("Exception ticketfound : "+ itemTicket);
    
    response.sendRedirect(itemTicket);
    return;
} else {
   response.setContentType("utf-8");
%>

    <%@ include file="xhtmlhead.jsp" %>
	<body>
        <div class="title" align="center"><strong>Error</strong></div>
        <div align="center">Item was not found<br/><br/></div>
        <div class="back" align="left"><a href="/<%=dmn.getDefPublicDir()%>/index.jsp">&lt;&lt;Main Page</a></div>
        <br/>
        <br/>
    </body>
    </html>

<%
}

return;
} catch (Exception e) { System.out.println("fromDefaultServlet: " + e); e.printStackTrace();}
%>