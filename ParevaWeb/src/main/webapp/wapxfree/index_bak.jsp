<%@include file="commonfunc.jsp"%>

<%

String uid = "";
String wapid = "";
String msisdn = aReq.getMsisdn();
String mobilenumber="", parsedmobile="";
String subpage="";
String enMsisdn = aReq.get("mid");
//====Required for CPA=====
String insertQuery="";
String campaignsrc="";
String visitsubscribed="1970-01-01 00:00:00";
//String ipaddress=request.getRemoteAddr();
String isSubscribed="0";
String transaction_ref="zacpa";
//====CPA Requirement end ===========

Handset handset = handsetdao.getHandset(request);
if(handset!=null) 
{
    session.setAttribute("handset",handset);
    request.setAttribute("handset",handset);
    
    //System.out.println("zahandsetinfo "+handset.getUnique());
}
if(!enMsisdn.equals("")){
    String deMsisdn=MiscCr.decrypt(enMsisdn);
    if(!deMsisdn.equals("")){
        if (deMsisdn.startsWith("0")) deMsisdn = "27" + deMsisdn.substring(1);        
        msisdn=deMsisdn;
    }
  
    
}

String campaignId = aReq.get("cid");
MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
String clubUnique = "";
if (club!=null) clubUnique = club.getUnique();

if(campaignId!=null && campaignId.trim().length()>0) {
    MobileClubCampaign cmpg = null;
   if (!campaignId.equals("")) cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
   if(cmpg!=null) campaignsrc = cmpg.getSrc();
    
    }

if(!subpage.equals("subscribe")){
if(campaignsrc.trim().toLowerCase().contains("cpa"))
    
{

%>
    //CPA Tracking Code comes here. 
	<%@include file="cpavisit.jsp"%>
	<%@include file="cpavisitlog.jsp"%>
<% }}
try{
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	StringBuilder sbf = new StringBuilder();
	String crlf = "\r\n";
	boolean onMainIndexPage = fileName.indexOf("index")>-1; 
	subpage = aReq.get("pg");
	HttpSession ses = request.getSession();
	String sts_id = "";
	if (user!=null) 
		{
		    uid = user.getUnique();
		    //System.out.println("UID: " + uid);
		    msisdn = user.getParsedMobile();
		    wapid = user.getWapId();
		    sbf.append("USER AUTHENTICATED: " + uid + ": " + msisdn + crlf);
		    //System.out.println("USER AUTHENTICATED: " + uid + ": " + msisdn + crlf);
		}

		else if (!aReq.get("routerresponse").equals("1") && club!=null && club.getRegion().equals("ZA") && msisdn.equals("") && onMainIndexPage
		       && aReq.get("rdsts").equals("") && aReq.get("optin").equals("") && subpage.equals("")&& ses.getAttribute("doneMsisdnParse") == null ) 
		{                        
		    ses.setAttribute("doneMsisdnParse",true);
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
			
			String _rdurl = "http://" + dmn.getDefaultUrl() + "?cid=" + campaignId + "&pg=" + subpage+"&routerresponse=1";
			String url = "http://www.smartcalltech.co.za/msisdnrouter/default.aspx?ID=" + sts_id + "&url=" + _rdurl;
		        
		        doRedirect(response, url );					
                       return;
					
		               }
		    
		    else
		    {
		       //If club is not ZA , we are not sending it for Msisdn Lookup Process
		        //doMsisdnTrace( request, msisdn,  club.getRegion() + " - NOT sending for MSISDN lookup -" );
		    }
		}
     
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
}
catch (Exception e) 
{
    String exception_=getStackTraceAsString(e);             
   System.out.println("wapxza "+" Exception "+e); System.out.println(exception_);
e.printStackTrace();
}

if( user != null && club != null && mobileclubdao.isActive(user, club) && null == request.getParameter("id") && null != request.getParameter("incoming"))
{
    String userMobile=user.getMobile().toString().trim();
    String enMsi=MiscCr.encrypt(userMobile);
    boolean activeuser=mobileclubdao.isActive(user, club);
    String orgigettingUrl="";
    int counter=0;
//    while(activeuser && counter<20)
//    {        
//            RedirectSetting setting=umesdc.getRedirectByUnique().get(domain);
//            orgigettingUrl=setting.getRedirectUrl();
//            String gettingUrl=orgigettingUrl;
//            
//         if(gettingUrl.contains(".mobi"))
//        {
//         gettingUrl=gettingUrl.substring(0,gettingUrl.indexOf(".mobi"));
//         gettingUrl=gettingUrl+".mobi";
//        }
//         
//        if(gettingUrl.contains(".co.uk"))
//        {
//             gettingUrl=gettingUrl.substring(0,gettingUrl.indexOf(".co.uk"));
//         gettingUrl=gettingUrl+".co.uk";
//        }
//           if(gettingUrl.contains(".com"))
//        {
//         gettingUrl=gettingUrl.substring(0,gettingUrl.indexOf(".com"));
//         gettingUrl=gettingUrl+".com";
//        }
//           if(gettingUrl.contains(".tv"))
//        {
//             gettingUrl=gettingUrl.substring(0,gettingUrl.indexOf("tv"));
//         gettingUrl=gettingUrl+".tv";
//        }
//           
//        RedirectSetting settings=redirectsettingdao.getDomainDetails(gettingUrl);
//        domain=settings.getDomainUnique();
//        club = UmeTempCmsCache.mobileClubMap.get(domain);
//        activeuser=mobileclubdao.isActive(user,club);
//        counter++;
//        
//    }
    
    doRedirect(response,"http://" + orgigettingUrl +"&id=" + user.getWapId()+"&mid="+enMsi+"&logtype=redirect");
    return;
}

String myip=request.getHeader("X-Forwarded-For");


if (myip != null) {           
           int idx = myip.indexOf(',');
           if (idx > -1) {
           myip = myip.substring(0, idx);
           }
    }


String myisp=ipprovider.findIsp(myip);
if(myisp.toLowerCase().contains("mtn")) myisp="mtn";
else if(myisp.toLowerCase().contains("vodacom") || myisp.toLowerCase().contains("research in motion uk limited")) myisp="vodacom";
else if(myisp.toLowerCase().contains("cellc") || myisp.toLowerCase().contains("cell-c") || myisp.toLowerCase().contains("cell c")) 
myisp="cellc";
else if(myisp.toLowerCase().contains("heita") || myisp.toLowerCase().contains("8ta"))
myisp="heita";
else
myisp="unknown";

/****************************************************************************************************************************************/


String landingPage="";


if(myisp!=null && !myisp.equals("unknown")){	

if(!campaignId.equals("")){
		landingPage=landingpage.initializeLandingPage(domain,campaignId,myisp);
   request.setAttribute("landingPage",landingPage);
	}else {
		landingPage=landingpage.initializeLandingPage(domain,"",myisp);
   request.setAttribute("landingPage",landingPage);
	}
 
}else
{
  landingPage=landingpage.initializeLandingPage(domain,campaignId,"all");
  	request.setAttribute("landingPage",landingPage);
}
System.out.println("Intialized Landing Page: "+landingPage);

if(!subpage.equals("subscribe")){
java.sql.Date today=new java.sql.Date(System.currentTimeMillis());
CampaignHitCounter campaignHitCounter=campaignhitcounterdao.HitRecordExistsOrNot(today,domain,campaignId,landingPage);
if(campaignHitCounter==null){
	campaignHitCounter=new CampaignHitCounter();
	campaignHitCounter.setaUnique(Misc.generateUniqueId());
	campaignHitCounter.setaDomainUnique(domain);
	campaignHitCounter.setCampaignId(campaignId);
	campaignHitCounter.setLandingPage(landingPage);
	campaignHitCounter.setDate(today);
	campaignHitCounter.setHitCounter(1);
	campaignHitCounter.setSubscribeCounter(0);
	campaignhitcounterdao.saveCampaignHitCounter(campaignHitCounter);
	
}else{
	//campaignHitCounter.setDate(today);
	campaignhitcounterdao.updateCampaignHitCounter(campaignHitCounter);
}
	
}

try 
{ 
    
    if(!aReq.get("cid").equalsIgnoreCase("")){
         
       /****************************************************************************************************************************************/
       
       //System.out.println("=========== MYISP IS ================ "+myisp);
     //campaigndao.log("index", "xhtml", "", "", handset,domain, campaignId, clubUnique, "INDEX", 0, request,response,myisp);
  
       campaigndao.log("index", landingPage, "", "", handset,domain, campaignId, clubUnique, "INDEX", 0, request,response,myisp);
    
    }
}
    catch(Exception e){System.out.println("ZA Index Error for campaignlog "+e); e.printStackTrace();}

try{
       
	application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp").forward(request,response); 
	//application.getRequestDispatcher("/IndexMain").forward(request,response); 
	
}
catch (Exception e) 
{
    String exception_=getStackTraceAsString(e);             
   System.out.println("wapxza "+" Exception "+e); System.out.println(exception_);
e.printStackTrace();
}


%>


