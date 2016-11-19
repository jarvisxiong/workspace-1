<%@include file="coreimport.jsp"%>
<%@ page import="java.util.*,java.text.*"%>

<%!void doRedirect(HttpServletResponse response, String url) {
		try {
			response.sendRedirect(url);
		} catch (Exception e) {
			System.out.println("doRedirect EXCEPTION: " + e);
		}
	}

	String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}%>
<%
	//***************************************************************************************************
System.out.println("germany visited");
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
        InternetServiceProvider ipprovider=null;
        MobileClubBillingPlanDao billingplandao=null;
        LandingPage landingpage=null;
        CampaignHitCounterDao campaignhitcounterdao=null;
        
        MobileClubCampaign cmpg = null;

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
      ipprovider=(InternetServiceProvider) ac.getBean("internetserviceprovider");
      umemobileclubuserdao=(UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
      landingpage=(LandingPage) ac.getBean("landingpage");
      campaigndao=(MobileClubCampaignDao) ac.getBean("mobileclubcampaigndao");
      umesmsdao=(UmeSmsDao) ac.getBean("umesmsdao");
      billingplandao=(MobileClubBillingPlanDao) ac.getBean("billingplandao");
      campaignhitcounterdao=(CampaignHitCounterDao) ac.getBean("campaignhitcounterdao");
      //za_engine=(PebbleEngine) ac.getBean("pebbleEngine");
      }
      catch(Exception e){
          e.printStackTrace();
      }
        
      
	SdcLanguageProperty lp = langpropdao.get("general",service, aReq.getLanguage(), dmn);
	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Calendar nowtime = new GregorianCalendar();
	Map<String, String> dParamMap = UmeTempCmsCache.domainParameters.get(domain);
	if (dParamMap == null) dParamMap = new HashMap<String, String>();

	//***************************************************************************************************

	String uid = "";
	String wapid = aReq.get("id");
        
	String msisdn = aReq.getMsisdn();
	handset = handsetdao.getHandset(request);
	String campaignId = aReq.get("cid");
	MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
        
         String myip = request.getHeader("X-Forwarded-For");
            
               if (myip != null) {
            int idx = myip.indexOf(',');
            if (idx > -1) {
                myip = myip.substring(0, idx);
            }
        } else {
            myip = request.getRemoteAddr();
        }

        session.setAttribute("userip", myip);
        request.setAttribute("userip", myip);

        String myisp = ipprovider.findIsp(myip);
        
        session.setAttribute("myisp", myisp);
        request.setAttribute("myisp", myisp);
	String userIP = myip;

	String clubUnique = "";

	if (club != null) clubUnique = club.getUnique();
        
           String previewLandingPage = aReq.get("previewLandingPage");
                if ("" != previewLandingPage) {
                    previewLandingPage = previewLandingPage.substring(0, previewLandingPage.indexOf("."));
                    System.out.println("previewLandingPage: " + previewLandingPage);
                }
        
        if (!previewLandingPage.equals("")) {
        String msisdnexist = aReq.get("msisdnexist");
        String previewNetwork = aReq.get("previewNetworkInput");
        System.out.println("previewNetwork " + previewNetwork);
        PrintWriter writer = response.getWriter();
        Map<String, Object> context = new HashMap();
        TemplateEngine templateengine = null;

        if ("".equals(msisdnexist)) {
            msisdnexist = "false";
        }
        context.put("msisdnexist", msisdnexist);

        context.put("operator", previewNetwork);
        context.put("contenturl", "http://" + dmn.getContentUrl());

        try {
            ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
            ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request, servletContext);
            templateengine = (TemplateEngine) ac.getBean("templateengine");

        } catch (Exception e) {
            e.printStackTrace();
        }
        engine = templateengine.getTemplateEngine(dmn.getUnique());
        engine.getTemplate(previewLandingPage).evaluate(writer, context);

    }
        else{
            String landingPage = "";
           
        
            if (!campaignId.equals("")) {
            landingPage = landingpage.initializeLandingPage(domain, campaignId, "all");
            request.setAttribute("landingPage", landingPage);
            session.setAttribute("landingPage", landingPage);//

        } else {
            landingPage = landingpage.initializeLandingPage(domain);
            request.setAttribute("landingPage", landingPage);
            session.setAttribute("landingPage", landingPage);
        }

	// **************************************************************************************************

	//MobileClubCampaignDao.log("index", "xhtml", uid, msisdn, handset,domain, campaignId, clubUnique, "INDEX", 0, request,response);
            
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        CampaignHitCounter campaignHitCounter = campaignhitcounterdao.HitRecordExistsOrNot(today, domain, campaignId, landingPage);
        if (campaignHitCounter == null) {
            campaignHitCounter = new CampaignHitCounter();
            campaignHitCounter.setaUnique(Misc.generateUniqueId());
            campaignHitCounter.setaDomainUnique(domain);
            campaignHitCounter.setCampaignId(campaignId);
            campaignHitCounter.setLandingPage(landingPage);
            campaignHitCounter.setDate(today);
            campaignHitCounter.setHitCounter(1);
            campaignHitCounter.setSubscribeCounter(0);
            campaignhitcounterdao.saveCampaignHitCounter(campaignHitCounter);

        } else {
            //campaignHitCounter.setDate(today);
            campaignhitcounterdao.updateCampaignHitCounter(campaignHitCounter);
        }
 

	Calendar cal = GregorianCalendar.getInstance();

	String domainName = System.getProperty(domain + "_url");

	String subPage = aReq.get("pg");
	String referer = request.getHeader("referer");

	String adultcontent = dParamMap.get("adultcontent");
	if (adultcontent == null)
		adultcontent = "0";
	if (adultcontent.equals("1") && !mobileclubdao.isActive(user, club) && subPage.equals("")) {

		 referer = request.getHeader("referer");
		if (referer == null)
			referer = "";
		if (referer.indexOf(domainName) == -1 || referer.indexOf("simulator.jsp") > -1) {

			String disclaimerUrl = "http://" + domainName+ "?pg=3404014118631KDS";
			if (!campaignId.equals(""))
				disclaimerUrl += "&cid=" + campaignId;
			response.sendRedirect(disclaimerUrl);
			return;

		}
	}

	// redirect urser to identification api
	// **************************************************************************************************    
	String ua = request.getHeader("User-Agent").toLowerCase();
	if (ua.indexOf("nagios-plugins") != -1) {
		return;
	}

	//commented by manoj manandhar down line
	//if(club!=null && club.getRegion().equals("IT") && user==null && subPage.equals(""))

	if (club != null && user == null && subPage.equals("")) {
            if (campaignId != null && campaignId.trim().length() > 0) {
                cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
                
                String pubId = (String) session.getAttribute("cpapubid");
                campaigndao.log("index", landingPage, msisdn, msisdn, handset, domain, campaignId, clubUnique, "INDEX", 0, request, response, myisp, "", "", "", pubId); 
            }
            
		
            
            /*
            String identifyUrl = "/index_main.jsp";

		if (!campaignId.equals("")) {
			identifyUrl += "?cid=" + campaignId;

			if (userIP != null && (userIP.trim().length() > 0)) {
				identifyUrl += "&userIP=" + userIP;
			}
		} else {
			if (userIP != null && (userIP.trim().length() > 0)) {
				identifyUrl += "?userIP=" + userIP;
			}
		}

		String url = "/" + System.getProperty("dir_" + domain + "_pub")+ identifyUrl;
		 
		doRedirect(response, identifyUrl);
		return;
                    */
	}
	try {

		String url = "/" + System.getProperty("dir_" + domain + "_pub")	+ "/index_main.jsp";

//		if (userIP != null && (userIP.trim().length() > 0)) {
//			url += "?userIP=" + userIP;
//		}
                

		application.getRequestDispatcher(url).forward(request, response);

	} catch (Exception e) {
		System.out.println("Germany test " + e);
	}
        
}
%>