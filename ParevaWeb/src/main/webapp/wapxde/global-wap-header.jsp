<%@include file="coreimport.jsp"%>
<%@page import="com.germany.xml.Response"%>
<%@page import="com.germany.utils.QueryBuilder"%>
<%@ page import="java.util.*, java.lang.Math, java.sql.Connection, java.sql.ResultSet, java.text.*, java.io.*"%>




<%!// Option to redirect via a debug page...
	void doRedirect(HttpServletResponse response, String url_) {
		String referer = "headers";
		try {
			response.sendRedirect(url_);
		} catch (Exception e) {
			System.out.println("doRedirect EXCEPTION: " + e);
		}
	}

	void addAttributeInSession(HttpSession ses, String key, String value) {

		if (isNotNullEmpty(value)) {
			ses.setAttribute(key, value);
                }
	}

	boolean isNullEmpty(String value) {

		return (value == null || (value != null && value.trim().length() <= 0) || (value != null && value
				.trim().toLowerCase().equals("null")));

	}

	boolean isNotNullEmpty(String value) {
		return (value != null && value.trim().length() > 0 && !(value.trim()
				.toLowerCase().equals("null")));
	}

	// Used for logging specially formatted messages%>





<%
	//***************************************************************************************************
//System.out.println("Germany test "+ " Global wap header called ");
	UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
	SdcService service = aReq.getService();
	String domain = dmn.getUnique();

	String ddir = dmn.getDefPublicDir();
	String lang = aReq.getLanguage().getLanguageCode();
	String stylesheet = aReq.getStylesheet();
	String pageEnc = aReq.getEncoding();

	response.setContentType("text/html; charset=" + pageEnc);
	String fileName = request.getServletPath();
	fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
	fileName = fileName.substring(0, fileName.lastIndexOf("."));
        
        HandsetDao handsetdao=null;
        UmeTempCache anyxsdc=null;
        UmeLanguagePropertyDao langpropdao=null;
        Handset handset = (Handset)session.getAttribute("handset");
        MobileClubDao mobileclubdao=null;
        VideoClipDao videoclipdao=null;
        PebbleEngine engine=null;
        Misc misc=null;
        UmeClubDetails clubdetails=null;
        MobileClubCampaignDao campaigndao=null;
        UmeUserDao umeuserdao=null;
        UmeMobileClubUserDao umemobileclubuserdao=null;
        UmeSmsDao umesmsdao=null;
        MobileClubBillingPlanDao billingplandao=null;

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
      umesmsdao=(UmeSmsDao) ac.getBean("umesmsdao");
      billingplandao=(MobileClubBillingPlanDao) ac.getBean("billingplandao");
      //za_engine=(PebbleEngine) ac.getBean("pebbleEngine");
      }
      catch(Exception e){
          e.printStackTrace();
      }

	SdcLanguageProperty lp = langpropdao.get("general", service, aReq.getLanguage(), dmn);

	boolean onMainIndexPage = fileName.indexOf("index") > -1;

	String crlf = "\r\n";
	StringBuilder sbf = new StringBuilder();
	sbf.append(fileName.toUpperCase() + ".JSP: " + new Date() + crlf+ crlf);
	Misc.addHttpHeaders(sbf, request);
	Misc.addHttpParameters(sbf, request);

	Map<String, String> dParamMap = UmeTempCmsCache.domainParameters.get(domain);
	if (dParamMap == null)
		dParamMap = new HashMap<String, String>();

	String campaignId = aReq.get("cid");

System.out.println("Germany test "+" global wap header user "+ user);

	//=====================================================================================
	HttpSession ses = request.getSession();

	if ((isNullEmpty(campaignId))) {
		campaignId = (String) request.getParameter("cid");
		if (isNullEmpty(campaignId)) {
			campaignId = "";
		}
	}

	MobileClubCampaign cmpg = null;
	if (!campaignId.equals("")) {
		cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
	}

	String uid = "";
	String wapid ="";// request.getParameter("id");

	String ip = request.getRemoteAddr();
	String xprof = aReq.get("xprof");

	//needToConfirmThisUser==true----then-----/index_main.jsp?pg=subscribe&cid=&redirsrc=global_wap_header

	String subpage = aReq.get("pg");
	String msisdn = "";
	String process = request.getParameter("process");
 

	if (isNotNullEmpty(aReq.get("msisdn"))) {
		msisdn = aReq.get("msisdn");
		
		if(!msisdn.startsWith("00")){
			msisdn="00"+msisdn.trim();
		}
		
		ses.setAttribute("msisdn", msisdn);
		ses.setAttribute("sdc_msisdn_param", msisdn);
	}



	if (isNullEmpty(subpage)) {
		subpage = request.getParameter("pg");
		if (isNullEmpty(subpage)) {
			subpage = "";
		}
	}

	//***********************************************************
	// Take MSISDN from session where exists
	//***********************************************************

	 
	if ((isNullEmpty(msisdn)) && null != ses.getAttribute("msisdn")) {
		msisdn = (String) ses.getAttribute("msisdn");
	}
	if ((isNotNullEmpty(msisdn)) && null == ses.getAttribute("msisdn")) {
		ses.setAttribute("msisdn", msisdn);
	}

	//The lower portion is improtant because other file find user through this param sdc_msisdn_param
	if (isNotNullEmpty(msisdn) && null == ses.getAttribute("sdc_msisdn_param")) {
		ses.setAttribute("sdc_msisdn_param", msisdn);
	}

	//***********************************************************
	// Initilize User
	//***********************************************************

	boolean isUserActive = false;

	MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
	SdcMobileClubUser clubuser = null;

	String clubUnique = "";

	if (club != null) {
		clubUnique = club.getUnique();
	}
        System.out.println("Germany test "+clubUnique);
	if (user == null) {
		if (clubUnique != null && msisdn != null && msisdn.length()>0){
                    System.out.println("Germany test global wap header"+ " clubunique "+clubUnique+" msisdn "+msisdn);
	            clubuser = umemobileclubuserdao.getClubUserByMsisdn(msisdn,clubUnique);
		   

                }
		 
                else if(clubuser!=null) 
                    user = umeuserdao.getUser(clubuser.getParsedMobile());
                
                //System.out.println("Germany test "+" Global wap header: "+ " user is null "+ user);

	}
	
	

	if (user != null) {
		isUserActive = mobileclubdao.isActive(user, club);
                clubuser = umemobileclubuserdao.getClubUserByMsisdn(msisdn,clubUnique);
                
	}
	
	if(user!=null){
                        System.out.println("Germany test : user "+user+" "+ user.getUnique()+" clubuser: "+clubuser.getUserUnique()+" "+clubuser.getClubUnique()+" active status: "+clubuser.getActive());
                        System.out.println("Germany test : isUserActive "+isUserActive );
            }
	

	if (isUserActive) {
		uid = user.getUnique();
		msisdn = user.getParsedMobile();
		wapid = user.getWapId();

	}
	
	if( !(fileName.indexOf("video") > -1 && isUserActive)){
		 

	if (isNotNullEmpty(process)
			&& (process.trim().equals("msisdnentryform")|| process.trim().toLowerCase().equals("start"))
			&& isNotNullEmpty(msisdn) && user != null && isUserActive
			&& isNotNullEmpty(uid) && isNotNullEmpty(wapid)) {

		//This is because if user is wifi and he put the msisdn no which is already a subscriber member then dont go for pin .. forward it to his wap id
		process = null;

		doRedirect(response, "http://" + dmn.getDefaultUrl() + "/?id="+ wapid);

		//doRedirect(response, "/index_main.jsp?cid=" + campaignId+ "&pg=" + subpage);
		return;

	}

	// **************************************************************************************************
	// This section is responsible for Redirection to index_main 
	// **************************************************************************************************

	boolean redirOk = true;

	if (!subpage.equals(""))
		redirOk = false;

	else if (fileName.indexOf("promo_") > -1)
		redirOk = false;

	else if (fileName.indexOf("de_") > -1)
		redirOk = false;

	else if (fileName.equals("error"))
		redirOk = false;

	else if (fileName.equals("notification"))
		redirOk = false;

	else if (fileName.indexOf("confirm") > -1)
		redirOk = false;

	else if (fileName.indexOf("index") > -1)
		redirOk = false;

	else if (!aReq.get("redir").equals(""))
		redirOk = false;
	
	

	if (redirOk && (user == null || !isUserActive) && club != null
			&& club.getOptIn() > 0) {

            //System.out.println(" Germany ::>> "+ " Global wap header Redirection "+ "/index_main.jsp?cid=" + campaignId+ "&pg=" + subpage );
		doRedirect(response, "/index_main.jsp?cid=" + campaignId+ "&pg=" + subpage);
		return;

	}

	// **************************************************************************************************
	// This section is responsible for processing the response from the MSISDN lookup in NTH System
	// **************************************************************************************************

	if (isNullEmpty(process)
			&& isNotNullEmpty((String) ses.getAttribute("process"))) {
		process = (String) ses.getAttribute("process");
	} else if (isNotNullEmpty(process)) {
		ses.setAttribute("process", process);
	}

	if (onMainIndexPage) {

		if (user == null || !mobileclubdao.isActive(user, club)) {
			if (isNullEmpty(msisdn) && isNullEmpty(process)) {
				doRedirect(response, "/de_msisdnlookup.jsp?cid="+ campaignId + "&pg=" + subpage);
				return;

			}
		}

	}
	} ///////////////////ending

	//***********************************************************************************************

	if(handset==null) handset=handsetdao.getHandset(request);
	response.setContentType(handset.getContentType(pageEnc));

	String backlink = aReq.get("back");
	if (backlink.startsWith("-hex-"))
		backlink = Misc.hex8Decode(backlink.substring(5));

	String statusmsg = java.net.URLDecoder.decode(
			aReq.get("statusmsg"), pageEnc);

	System.out.println(statusmsg);
	String errormsg = java.net.URLDecoder.decode(aReq.get("errormsg"),pageEnc);
	//System.out.println("Germany  Error message Global wap header ::>> "+errormsg);
Map<String,TemplatePojo> templates=new HashMap<String,TemplatePojo>(); 
templates=anyxsdc.getTemplateMap();
TemplatePojo template=templates.get(dmn.getUnique());
String templatefolder=template.getTemplateFolder();
FileLoader template_loader = new FileLoader();
//za_template_loader.setPrefix("/opt/pareva/templates/parsed/ZA/x-rated");
template_loader.setPrefix(templatefolder.trim());
template_loader.setSuffix(template.getSuffix().trim());
engine = new PebbleEngine(template_loader);
EscaperExtension escaper = engine.getExtension(EscaperExtension.class);
escaper.setAutoEscaping(false);
%>
