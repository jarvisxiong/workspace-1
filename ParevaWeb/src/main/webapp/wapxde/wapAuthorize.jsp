<%@page import="com.germany.utils.ResponseTags"%>
<%@page import="com.germany.xml.Response"%>
<%@page import="com.germany.utils.QueryStringCreatorGI"%>
<%@ include file="global-wap-header.jsp"%>
<%@ include file="parameters.jsp"%>


<%
	String CONFIRM_TARGET = "/confirm.jsp";
	String ERROR_TARGET = "/error.jsp";

	campaignId = aReq.get("cid");

	String callbackurl = aReq.get("callbackurl");

	if (msisdn == null
			|| (msisdn != null && msisdn.trim().length() <= 0)) {
		msisdn = aReq.get("msisdn");
		request.getSession().setAttribute("msisdn", msisdn);
	}

	if (msisdn != null && dmn != null) {
		user = umeuserdao.authenticateUser(msisdn, "", 3, dmn);
		if (user != null && mobileclubdao.isActive(user, club)) {
			response.sendRedirect("http://" + dmn.getDefaultUrl()+ "/?id=" + user.getWapId());
			return;
		}
	}

	if (campaignId != null && campaignId.trim().length()>0) {
		CONFIRM_TARGET = CONFIRM_TARGET + "?cid=" + campaignId;
	}

	try {

		String resultCode = "";
		String sessionId = "";
		String resultText = "";
		String userid = "";
		String operatorCode = "";
		String trid = "";
		String url = "";

		userid = aReq.get("userid");
		sessionId = aReq.get("sessionId");
		
	
		Response gresponse = null;

		if ((callbackurl != null && callbackurl.trim().toLowerCase()
				.equals("yes"))) {

			if (userid != null && sessionId != null && msisdn!=null) {
				
				System.out.print("germany\t"+userid+"\t"+sessionId+"\t"+msisdn+"\t wapAuthorize.jsp line no 60");

				url = checkStatus(sessionId);
				gresponse = getResponseFromNTH(url);

				if (gresponse != null && gresponse.getResultCode().trim().equals("100")
						&& gresponse.getStatusNumber() != null
						&& gresponse.getStatusNumber().trim()
								.toLowerCase().equals("2")) {

					url = preparePayment(sessionId,userid,msisdn);
					gresponse = getResponseFromNTH(url);
					if (gresponse != null) {
						resultText = gresponse.getResultText();
						trid = gresponse.getTrid();

						url = commitPayment(trid);
						gresponse = getResponseFromNTH(url);
						if (gresponse != null) {
							resultText = gresponse.getResultText();
							if (gresponse.getResultCode().trim()
									.toLowerCase().equals("100")) {

								request.getSession().setAttribute("sessionId",
										sessionId);
								
								request.getSession().setAttribute("result",
										"confirm");
								response.sendRedirect(CONFIRM_TARGET);
								return;

							}

						}
					}

				}

			}

		} else {
			resultText = "Cannot process further Error occur";
		}

		msisdn = "";
		request.getSession().setAttribute("msisdn", "");

		request.getSession().setAttribute("error_message", resultText);

		response.sendRedirect(ERROR_TARGET);

		return;
	} catch (Exception e) {

		String resultText = getStackTraceAsString(e);
		request.getSession().setAttribute("error_message", resultText);

		response.sendRedirect(ERROR_TARGET);
		 
	}
 %>

<%!

private String getStackTraceAsString(Exception e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);

		StringBuffer error = stringWriter.getBuffer();

		String allError = error.toString();

		char[] chars = allError.toCharArray();

		StringBuffer sb = new StringBuffer();
		sb.append("Debugging Germany  ");

		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '\n') {

				sb.append("\n Debuffing Germany : ");
			} else {
				sb.append(chars[i]);
			}
		}

		return sb.toString();
	}%>

<%!public String getUser(String uid) {

		QueryStringCreatorGI query = new QueryStringCreatorGI(conn_path);
		query.addParameter(ResponseTags.command, "getUser");
		query.addParameter(ResponseTags.username, username);
		query.addParameter(ResponseTags.password, password);
		query.addParameter(ResponseTags.serviceCode, serviceCode);
		query.addParameter(ResponseTags.uid, uid);

		return query.getQuery();

	}

	public String checkStatus(String sessionId) {
		QueryStringCreatorGI query = new QueryStringCreatorGI(conn_path);
		query.addParameter(ResponseTags.command, "checkStatus");
		query.addParameter(ResponseTags.username, username);
		query.addParameter(ResponseTags.password, password);
		query.addParameter(ResponseTags.serviceCode, serviceCode);
		query.addParameter(ResponseTags.sessionId, sessionId);

		return query.getQuery();

	}
	
	public String preparePayment(String sessionId,String uid,String msisdn) {
		QueryStringCreatorGI query = new QueryStringCreatorGI(conn_path);
		query.addParameter(ResponseTags.command, "preparePayment");
		query.addParameter(ResponseTags.username, username);
		query.addParameter(ResponseTags.password, password);
		query.addParameter(ResponseTags.serviceCode, serviceCode);
		query.addParameter(ResponseTags.sessionId, sessionId);
		query.addParameter(ResponseTags.price, price);
		query.addParameter(ResponseTags.notificationUrl, notificationURL+ "&userid=" + uid + "&msisdn=" + msisdn);
		return query.getQuery();

	}

 

	public String commitPayment(String trid) {
		QueryStringCreatorGI query = new QueryStringCreatorGI(conn_path);
		query.addParameter(ResponseTags.command, "commitPayment");
		query.addParameter(ResponseTags.username, username);
		query.addParameter(ResponseTags.password, password);
		query.addParameter(ResponseTags.serviceCode, serviceCode);
		query.addParameter(ResponseTags.trid, trid);
		return query.getQuery();

	}

	public Response getResponseFromNTH(String url) {

		Response gresponse = null;

		List<String> result = connection.connect(url);

		try {
			gresponse = reader.convertStringToResponseObject(result.get(0));
		} catch (Exception e) {
			gresponse = null;
		}
		return gresponse;

	}%>