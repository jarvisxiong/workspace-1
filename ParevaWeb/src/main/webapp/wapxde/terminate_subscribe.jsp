<%@page import="org.apache.jasper.tagplugins.jstl.core.Catch"%>
<%@ include file="global-wap-header.jsp"%>
<%@ include file="de_parameters.jsp"%>

<%
	String title = Misc.utfToUnicode(Misc.hex8Decode(aReq.get("ttl")),
			pageEnc);
	String bg = aReq.get("bg");
	String font = aReq.get("fnt");
	String style1 = "style=\"background-color:" + bg + "; color:"
			+ font + ";\"";
	String style2 = "style=\"color:" + font + ";\"";

	String confirm = aReq.get("confirm");
	
	System.out.println("germany user : " + user);
	System.out.println("germany club : " + club);

	if (user != null && club != null && isUserActive) {

		if (confirm.equals("yes")) {			

			uid = user.getUnique();
			msisdn = user.getParsedMobile();

			try {

				SdcMobileClubUser clubUser = SdcMobileClubUserDao
						.getClubUser(uid, club.getUnique());
				String subscriptionId = clubUser.getParam1();

				QueryBuilder germanyQueryBuilder = new QueryBuilder(
						username, password, serviceCodeWAP,
						serviceCodeWIFI, conn_path, price);

				String responsecode = "";

				String url = germanyQueryBuilder.closeSubscription(
						subscriptionId, false);
				Response gresponse = germanyQueryBuilder
						.getResponseFromNTH(url);

				if (gresponse != null) {
					responsecode = gresponse.getResultCode();
					if (responsecode == null) {
						responsecode = "";
					}
				}

				if (!responsecode.equals("100")) {
					url = germanyQueryBuilder.closeSubscription(subscriptionId, true);
					gresponse = germanyQueryBuilder
							.getResponseFromNTH(url);
					responsecode = gresponse.getResultCode();
					if (responsecode == null) {
						responsecode = "";
					}
				}

				if (responsecode.equals("100")) {
					SdcMobileClubUserDao.disable(clubUser.getUnique());
					  CacheManagerSdc.delete(uid);
					doRedirect(response,"/terminate_message.jsp");
					return;
				} else {
					doRedirect(response,"/terminate_message.jsp?message=2");
					return;
				}

			} catch (Exception e) {
				doRedirect(response,"/terminate_message.jsp?message=2");
				return;
			}
		} else if (confirm.equals("no")) {

			doRedirect(response, "/index_main.jsp?cid=" + campaignId+ "&pg=" + subpage);
			return;
		}

	} else {

		doRedirect(response, "/index_main.jsp?cid=" + campaignId
				+ "&pg=" + subpage);
		return;
	}
%>
<%
	String[] props = null;
	java.util.List list = UmeTempCmsCache.clientServices.get(domain);
	//hard code logo

	if (list != null && list.size() > 1) {
		props = (String[]) list.get(1);
		String link = props[2] + "?lang=" + lang + "&amp;ttl="	+ Misc.hex8Code(props[0]);
	}
%>

<body>
	<div id="pgframe" align="center">
		<div class="container">

			<%
				if (props != null && props.length > 4) {
			%>

			<jsp:include page="<%=props[3]%>">
				<jsp:param name="srvc" value="<%=props[1]%>" />
				<jsp:param name="ttl" value="<%=Misc.hex8Code(props[0])%>" />
				<jsp:param name="fnt" value="<%=props[5]%>" />
			</jsp:include>
			<%
				}
			%>
			<br> <br>

			<%
				String terminateURL_yes = "/terminate_subscribe.jsp?confirm=yes&cid="+ campaignId + "&pg=" + subpage;

				String terminateURL_No = "/terminate_subscribe.jsp?confirm=no&cid="+ campaignId + "&pg=" + subpage;
			%>


			<div <%=style1%> align="center">
				<b><%=lp.get("would_like_terminate_subscription")%></b><br> <br>
				<!--<b><%=lp.get("would_like_terminate_subscription")%></b><br>-->
				<a href="<%=terminateURL_yes%>"><%=lp.get("confirm")%></a> <span
					style="margin-left: 10px;"><a href="<%=terminateURL_No%>"><%=lp.get("cancel")%></a></span>
			</div>
			<br>
			<div class="footer" align="left">
				
			</div>
		</div>
	</div>
</body>
</html>
