<%@page import="ume.pareva.smsapi.*"%>
<%@include file="coreimport.jsp"%>
<%@include file="db.jsp"%>
<%--<%@include file="globalfunction.jsp"%>--%>

<%!
void doRedirect(HttpServletResponse response, String url) {
        String referer = "headers";
        System.out.println("doRedirect: " + url);
        try {
              response.sendRedirect(url);
        }
        catch (Exception e) {
                System.out.println("doRedirect EXCEPTION: " + e);
        }
}
String findIsp(Session dbsession, String ipaddress)
{
    String ispnetwork=null;
    try{
        
        String findipquery="SELECT isp FROM ispLookup WHERE INET_ATON('"+ipaddress+"') BETWEEN ip_start AND ip_end LIMIT 1";
        
       Query query=dbsession.createSQLQuery(findipquery).addScalar("isp");
       java.util.List isplist=query.list();
       query.setCacheable(true);
        if(isplist.size()>0)
        {
            for(Object o:isplist){
                String row=o.toString();
                ispnetwork=row;
            }
           
        }
    }
    catch(Exception e){e.printStackTrace();}

System.out.println("wapzatest test: "+" globalwapfunctions.jsp :> isp : "+ispnetwork);
    
    return ispnetwork;
}

%>

<%
	//*************************************************************************************
UmeSessionParameters aReq = new UmeSessionParameters(request);
UmeUser user = aReq.getUser();
UmeDomain dmn = aReq.getDomain();
SdcService service = aReq.getService();
Transaction dbtransaction=dbsession.beginTransaction();




String domain = dmn.getUnique();
String ddir = dmn.getDefPublicDir();
String lang = aReq.getLanguage().getLanguageCode();

String durl=dmn.getDefaultUrl().toString().trim();
String stylesheet = aReq.getStylesheet();
String pageEnc = aReq.getEncoding();
response.setContentType("text/html; charset=" + pageEnc);

String stsReturnId = aReq.get("ID"); //This ID is received only after msisdn lookup process which must be equivalent to sts_id declared below. 

String fileName = request.getServletPath();
fileName = fileName.substring(fileName.lastIndexOf("/")+1);
fileName = fileName.substring(0,fileName.lastIndexOf("."));
request.setAttribute("fileName",fileName);

HandsetDao handsetdao=null;
UmeTempCache anyxsdc=null;
UmeLanguagePropertyDao langpropdao=null;
Handset handset = (Handset)session.getAttribute("handset");
MobileClubDao mobileclubdao=null;
VideoClipDao videoclipdao=null;
PebbleEngine za_engine=null;
Misc misc=null;
UmeClubDetails clubdetails=null;
MobileClubCampaignDao campaigndao=null;
UmeUserDao umeuserdao=null;
UmeMobileClubUserDao umemobileclubuserdao=null;
UmeSmsDao_bak umesmsdao=null;
 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      handsetdao=(HandsetDao) ac.getBean("handsetdao");
      anyxsdc=(UmeTempCache) ac.getBean("umesdc");
      langpropdao=(UmeLanguagePropertyDao) ac.getBean("umelanguagepropertydao");
      mobileclubdao=(MobileClubDao) ac.getBean("mobileclubdao");
      videoclipdao=(VideoClipDao) ac.getBean("videoclipdao");
      misc=(Misc) ac.getBean("misc");
      umeuserdao=(UmeUserDao) ac.getBean("umeuserdao");
      umemobileclubuserdao=(UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
      campaigndao=(MobileClubCampaignDao) ac.getBean("mobileclubcampaigndao");
      umesmsdao=(UmeSmsDao_bak) ac.getBean("umesmsdao");
      //za_engine=(PebbleEngine) ac.getBean("pebbleEngine");
      }
      catch(Exception e){
          e.printStackTrace();
      }
 
SdcLanguageProperty lp = langpropdao.get("general", service, aReq.getLanguage(), dmn);
//***************************************************************************************************
boolean onMainIndexPage = fileName.indexOf("index")>-1; 
        
String crlf = "\r\n";
StringBuilder sbf = new StringBuilder();
sbf.append(fileName.toUpperCase() + ".JSP: " + new Date() + crlf + crlf);
Misc.addHttpHeaders(sbf, request);
Misc.addHttpParameters(sbf, request);
Map<String, String> dParamMap = UmeTempCmsCache.domainParameters.get(domain);
if (dParamMap==null) dParamMap = new HashMap<String,String>();

MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
if(club!=null)
clubdetails=UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());

//Map dImages = (Map) ume.pareva.cms.UmeTempCmsCache.xhtmlImages.get("xhtml_" + domain);
//Getting Campaign of visited domain
String campaignId = aReq.get("cid");
MobileClubCampaign cmpg = null;
if (!campaignId.equals("")) cmpg = UmeTempCmsCache.campaignMap.get(campaignId);

String uid = "";
String wapid = "";

String ip = request.getRemoteAddr();
String xprof = aReq.get("xprof");
String subpage = aReq.get("pg");
String stsMsisdn = aReq.get("msisdn");
HttpSession ses = request.getSession();
String msisdn = "";
if(!stsMsisdn.equals("")) msisdn=stsMsisdn;
String enMsisdn = aReq.get("mid");
String fntString = aReq.get("fnt");
String isp=findIsp(dbsession,ip);

if(handset==null) handset=handsetdao.getHandset(request);
%>

<%
//============    BroadCast Decrypting msisdn process  ===============
if(!enMsisdn.equals("") && fntString.equals("")){ // relying on no font colour being given. Prone to future failure
    String deMsisdn = MiscCr.decrypt(enMsisdn);
//System.out.println("madan decrypting msisdn: "+deMsisdn+" campaignID: "+campaignId+"  ENmsisdn: "+enMsisdn);
    if(!deMsisdn.equals("")){
        
    	if (deMsisdn.startsWith("0")) deMsisdn = "27" + deMsisdn.substring(1);        
        msisdn=deMsisdn;
      
        
      %>
        <%@ include file="identifieduser.jsp" %>
      <%   
      }
}


//===== END Broadcast Decryption of Msisdn 

%>

<%
// Take MSISDN from session where exists
if( null != ses.getAttribute("sdc_msisdn_param") )
    msisdn = (String) ses.getAttribute("sdc_msisdn_param");

String sts_id = ""; //This variable is used to set ID for STS when performing Msisdn router process
if (ses!=null && !stsReturnId.equals("")) 
{
    sts_id = (String) ses.getAttribute("sts_id"); //Receiving StsID from session set from msisdn lookup. 
    if (sts_id!=null && sts_id.equals(stsReturnId) && !stsMsisdn.equals("")) {
        ses.setAttribute("sdc_msisdn_param", stsMsisdn);
    }
}

// **************************************************************************************************
// This section is responsible for processing the response from the MSISDN lookup router process
// **************************************************************************************************
//System.out.println("Router Response= "+ request.getParameter("routerresponse"));
if( "1".equals(request.getParameter("routerresponse"))) 
{
   
    if( ""!=request.getParameter("msisdn") && null!=request.getParameter("msisdn")) 
    {     
        msisdn = request.getParameter("msisdn");
        if (msisdn.startsWith("0")) msisdn = "27" + msisdn.substring(1);
        
        System.out.println("AFter Visiting and router response is 1 the value of msisdn is "+msisdn);
           
    %>
    <%@ include file="identifieduser.jsp" %>    
    <%
                
    }
    else if(""!=request.getParameter("UserNumber") && null!=request.getParameter("UserNumber"))
    {
        msisdn = request.getParameter("UserNumber");         
        if (msisdn.startsWith("0")) msisdn = "27" + msisdn.substring(1);
       
%>
        <%@ include file="identifieduser.jsp" %>
<%
    }
    else    
    {
        //This Section is for Msisdn Failed. 
	//campaigndao.log("global_wap_header", "xhtml", uid, msisdn, null, domain, campaignId, club.getUnique(),"MSISDNP_FAIL", 0, request, response);
    }
}
%>

<%

System.out.println("User "+user);
if (user!=null) 
{
    uid = user.getUnique();
    //System.out.println("UID: " + uid);
    msisdn = user.getParsedMobile();
    wapid = user.getWapId();
    sbf.append("USER AUTHENTICATED: " + uid + ": " + msisdn + crlf);
    System.out.println("USER AUTHENTICATED: " + uid + ": " + msisdn + crlf);
}

else if (!aReq.get("routerresponse").equals("1") && club!=null && club.getRegion().equals("ZA") && msisdn.equals("") && onMainIndexPage
       && aReq.get("rdsts").equals("") && aReq.get("optin").equals("") && subpage.equals("")&& ses.getAttribute("doneMsisdnParse") == null ) 
{
    
    ses.setAttribute("doneMsisdnParse",true);
    //doMsisdnTrace( request, msisdn, "********************************** NEW USER GOING FOR MSISDN PARSE PROCESS ***********************************" );
    //campaigndao.log("promo_subscribe", "xhtml", uid, msisdn, null, domain, campaignId, club.getUnique(), "ALL", 0, request, response);
    
    if( club.getRegion().equals("ZA"))
    {       
    	// We know little about this user who has just landed on the index page including the MSISDN
	// Redirect the user off to the MSISDN router to try to look up an identifier
	//doMsisdnTrace( request, msisdn, "ZA - Sending this session to the MISDN router" );
	sts_id = Misc.generateLogin(30);
	HttpSession sts_ses = request.getSession();
	sts_ses.setAttribute("sts_id", sts_id);
	
	sbf.append("STS Session before redirection: " + sts_ses + crlf);
	sbf.append("Session isNew: " + sts_ses.isNew() + crlf);
	sbf.append("STS_id attribute after setting: " + sts_ses.getAttribute("sts_id") + crlf);
	
	// Log this event
	//MobileClubCampaignDao.log("global_wap_header", "xhtml", uid, msisdn, null, domain, campaignId, club.getUnique(),
	      // "INITIAL_VISIT", 0, request, response);
	
	String _rdurl = "http://" + dmn.getDefaultUrl() + "/index_main.jsp?cid=" + campaignId + "&pg=" + subpage+"&routerresponse=1";
	String url = "http://www.smartcalltech.co.za/msisdnrouter/default.aspx?ID=" + sts_id + "&url=" + _rdurl;
        //String url="http://www.smartcalltech.co.za/msisdnrouter/default.aspx?ID=" + sts_id ;
   
        doRedirect(response, url );
	return;
               }
    
    else
    {
       //If club is not ZA , we are not sending it for Msisdn Lookup Process
        //doMsisdnTrace( request, msisdn,  club.getRegion() + " - NOT sending for MSISDN lookup -" );
    }
}

%>

<%
//boolean needToConfirmThisUser = 
 //      subpage.equals("")                           // They're on the main index page...
  //      && club!=null				     // We have a reference to the club...
//	&& !mobileclubdao.isActive(user, club)       // They are not already active in this club...
 //       && club.getOptIn()>0			     // There is some level of opt in on this club
  //      && onMainIndexPage;			     // We are on the index page - never redirect off the back of other pages
		
       boolean needToConfirmThisUser = 
       !subpage.equals("")                           // They're on the main index page...
        && club!=null				     // We have a reference to the club...
	&& !mobileclubdao.isActive(user, club)       // They are not already active in this club...
        && club.getOptIn()>0			     // There is some level of opt in on this club
        && onMainIndexPage;			     // We are on the index page - never redirect off the back of other pages
		
     boolean proceed=true;

if( (msisdn != null) && (!msisdn.trim().equals("")) && (club.getRegion().equals("ZA")) && 
	(
          msisdn.length() <= 8 || 
         (!msisdn.startsWith("07") && 
          !msisdn.startsWith("08") &&
          !msisdn.startsWith("0027") &&
          !msisdn.startsWith("27")
          )))
{
	needToConfirmThisUser = false;
        proceed=false;
	//doMsisdnTrace( request, msisdn, "ZA MSISDN is invalid, so we skip STS/MESH DOI" );
}

if (user!=null && user.getAccountType()==99) { // User account is blocked/barred so don't DOI him
	needToConfirmThisUser = false;
        proceed=false;
	//doMsisdnTrace( request, msisdn, "ZA MSISDN is blocked (99), so we skip STS/MESH DOI." );
}

System.out.println("==NEED to confirm this USER === "+needToConfirmThisUser);
%>



<%
//response.setContentType(handset.getContentType(pageEnc));
String backlink = aReq.get("back");
if (backlink.startsWith("-hex-")) backlink = Misc.hex8Decode(backlink.substring(5));

dbtransaction.commit();

Map<String,TemplatePojo> templates=new HashMap<String,TemplatePojo>();
if(getServletContext().getAttribute("za_engine")==null){
    
    templates=anyxsdc.getTemplateMap();
 
TemplatePojo template=templates.get(dmn.getUnique());
String templatefolder=template.getTemplateFolder();
FileLoader za_template_loader = new FileLoader();
//za_template_loader.setPrefix("/opt/pareva/templates/parsed/ZA/x-rated");
za_template_loader.setPrefix(templatefolder.trim());
za_template_loader.setSuffix(template.getSuffix().trim());
za_engine = new PebbleEngine(za_template_loader);
EscaperExtension za_escaper = za_engine.getExtension(EscaperExtension.class);
za_escaper.setAutoEscaping(false);
this.getServletContext().setAttribute("za_engine",za_engine);
}else{
	za_engine=(PebbleEngine)getServletContext().getAttribute("za_engine");
}

%>






