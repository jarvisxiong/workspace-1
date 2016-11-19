<%@include file="coreimport.jsp"%>
<%@ page import="java.util.*, java.lang.Math, java.sql.Connection, java.sql.ResultSet, java.text.*, java.net.URL"%>
<%!// Option to redirect via a debug page...
	void doRedirect(HttpServletResponse response, String url) {
		String referer = "headers";
		System.out.println("doRedirect: " + url);
		try {
			response.sendRedirect(url);
		} catch (Exception e) {
			System.out.println("doRedirect EXCEPTION: " + e.getMessage());
		}
	}%>
<%
	//***************************************************************************************************
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
        
        
        
        
        
	handset = handsetdao.getHandset(request);
	response.setContentType(handset.getContentType(pageEnc));

	MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);

	SdcLanguageProperty lp = langpropdao.get("general",service, aReq.getLanguage(), dmn);

	String title = Misc.utfToUnicode(Misc.hex8Decode(aReq.get("ttl")),pageEnc);
	String bg = aReq.get("bg");
	String font = aReq.get("fnt");
	String style1 = "style=\"background-color:" + bg + "; color:"+ font + ";\"";
	String style2 = "style=\"color:" + font + ";\"";

	String message = aReq.get("message");
%>
<div <%=style1%> align="center">
	<%
		if (message.equals("")) {
	%>
	<b><%=lp.get("msg_subscription_terminated")%></b>
	<%
		try {
				session.invalidate();
			} catch (Exception e) {
			}
	%>
	<%
		} else {
	%>
	<b><%=lp.get("msg_subscription_terminated_failed")%></b>
	<%
		}
	%>
</div>