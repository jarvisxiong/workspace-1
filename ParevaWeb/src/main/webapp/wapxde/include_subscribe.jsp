<%@ include file="global-wap-header.jsp"%>
<%
	
	if (club != null) {

		
		if ((String) request.getSession().getAttribute("ipx_msisdn") != null) {
			msisdn = (String) request.getSession().getAttribute("msisdn");
			request.getSession().setAttribute("msisdn", msisdn);
			
			String userUnique = umeuserdao.getUserUnique(msisdn,"msisdn", dmn.getUnique());
			if (!userUnique.equals(""))
				user = umeuserdao.getUser(userUnique);
		}
	}

	if (cmpg != null) {
		cmpg.setHitCount(cmpg.getHitCount() + 1);
		cmpg.setLastHit(new Date());
		//MobileClubCampaignDao.saveItem(cmpg);
	}

	//String clubUnique = "";
	if (club != null)
		clubUnique = club.getUnique();

	String campaignUnique = "";
	if (cmpg != null)
		campaignUnique = cmpg.getUnique();

	if (!msisdn.equals("")) {
		//MobileClubCampaignDao.log("promo_subscribe", "xhtml", uid,msisdn, handset, domain, campaignUnique, clubUnique,"IDENTIFIED", 0, request, response);
	}
%>