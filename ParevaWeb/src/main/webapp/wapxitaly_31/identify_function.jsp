<%
	String DELIVER_TARGET = "http://" + dmn.getName() + "/identify_delivery.jsp";
	//System.out.println("Inside Identify IPX");

	if(!campaignId.equals("")){
	  	DELIVER_TARGET += "&cid=" + campaignId;  
	}
//	try{
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH");
//		Date today = new Date();
//		String debugFilePath = "/var/log/pareva/IT/ipxlog/identify/" + "IPX_identify_identification_log_" + sdf.format(today.getTime()) + ".txt";
//		String text = "IPX identification" + today + "\r\n";
//		FileUtil.writeRawToFile(debugFilePath, text, true);	
//	}catch(Exception ee){
//		System.out.println("IPX identify log exception: " + ee);
//	}
        
	String aDeliveryURL = DELIVER_TARGET;

	IdentificationApiPort myApi = (new IdentificationApiServiceLocator()).getIdentificationApi31(new URL(IDENTIFY_REQUEST_URL));
	((IdentificationApiBindingStub)myApi).setTimeout(50000);

	CreateSessionRequest aCreateSessionRequest = new CreateSessionRequest();
	aCreateSessionRequest.setCorrelationId("universalmob");
	aCreateSessionRequest.setClientIPAddress("#NULL#");
	aCreateSessionRequest.setServiceName("Ume Ipx Billing");
	aCreateSessionRequest.setReturnURL(aDeliveryURL);
	aCreateSessionRequest.setUsername(clubIPXUserName);
	aCreateSessionRequest.setPassword(clubIPXPassword);
	aCreateSessionRequest.setServiceCategory("#NULL#");
	aCreateSessionRequest.setServiceMetaData("#NULL#");
	aCreateSessionRequest.setLanguage("#NULL#");
	aCreateSessionRequest.setCampaignName("#NULL#");
	CreateSessionResponse aCreateSessionResponse = myApi.createSession(aCreateSessionRequest);


	if(aCreateSessionResponse.getResponseCode() != 0)
	{

		DELIVER_TARGET = "/index_main.jsp?pg=confirm";
		//System.out.println("IPX Identify failed: " + aCreateSessionResponse.getResponseCode() + "  " + aCreateSessionResponse.getResponseMessage());

		StringBuilder dd;
		String tab;
		String crlf;
		String debugFile;
		String debugPath;

		dd = new StringBuilder();
		tab = (new Character('\t')).toString();
		crlf = "\r\n";
		debugFile = (new StringBuilder("IPX_identify_failed_debug_")).append(MiscDate.sqlDate.format(new Date())).append("_").append(club.getUnique()).append(".txt").toString();
		debugPath = "/var/log/pareva/IT/ipxlog/identify/";		
		dd.append((new StringBuilder(String.valueOf(crlf))).append("IPX Processing HTTP Request").append("\t").append(new Date()).append("\t").toString());
		dd.append("IPX Identify failed: " + aCreateSessionResponse.getResponseCode() + "  " + aCreateSessionResponse.getResponseMessage() + crlf);
		//System.out.println("IPX Identify failed: " + aCreateSessionResponse.getResponseCode() + "  " + aCreateSessionResponse.getResponseMessage() + crlf);
		
		//FileUtil.writeRawToFile((new StringBuilder(String.valueOf(debugPath))).append(debugFile).toString(), (new StringBuilder(String.valueOf(dd.toString()))).append(crlf).toString(), true);
		dd.setLength(0);
				
		doRedirect(response, DELIVER_TARGET);	
		//return;	
		//SHOW MSISDN

	}
	else{
		session.setAttribute("ipx_idensessionid", aCreateSessionResponse.getSessionId());
		
		//System.out.println("IPX Identification Session Ending API remoteAdd: " + request.getRemoteAddr());
		//System.out.println("IPX Identification Session Ending API campaignId: " + campaignId);
		//System.out.println("IPX Identification Session Ending API idensessionid: " + aCreateSessionResponse.getSessionId());
		//System.out.println("IPX Identification response redirect: " + aCreateSessionResponse.getRedirectURL());		
		doRedirect(response, aCreateSessionResponse.getRedirectURL());	
		return;	
	}
%>