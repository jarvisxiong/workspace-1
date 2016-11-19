<%-- 
<%@page import="com.mixmobile.anyx.cpa.cpaLog"%>
--%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="org.apache.commons.collections.bidimap.DualHashBidiMap"%>
<%@ include file="global-wap-header.jsp"%>


<%
	/**
	 Set of help queries for user insertion

	 select * from users where aMobile1='1111111111';
	
	 select * frommobileClubSubscribers where aparsedmobile='1111111111';
	
	 select aOtpServiceName from mobileclubs where aUnique='5702793075331KDS';

	 update  mobileclubs set aOtpServiceName='123' where aUnique='5702793075331KDS';

	 select * from domains where aDomainUnique='4182325962731CDS';

	 **/
%>

<%!

String findCampaignSrc(String campaignid,Session dbsession) {
	String campaignsrc = "";
	Transaction trans=dbsession.beginTransaction();
	String sqlstr = "Select aSrc from mobileClubCampaigns where aUnique='"
			+ campaignid + "'";
	
	Query query=null; 
	query=dbsession.createSQLQuery(sqlstr).addScalar("aSrc");

	//ResultSet rs = null;
	//Connection con = null;

	try {
		//con = DBH.getConnection();
		
		java.util.List result=query.list();
			if(!result.isEmpty()) {
            	
            for(Object o:result)
            {
            	
            	campaignsrc=o.toString();
            
			
            }

        }
		

		//rs = DBH.getRs(con, sqlstr);
		//if (rs.next()) {
		//	campaignsrc = rs.getString("aSrc");
		//}
	//rs.close();
	//con.close();
	} catch (Exception eE) {
		eE.printStackTrace();
	}
	trans.commit();
	return campaignsrc;
}

%>


<%
	String status = "";

	String requestreference = aReq.get("requestreference");
	Calendar debug_time = new GregorianCalendar();
	Date time_stamp = debug_time.getTime();

	boolean processingResult = false;

	String campaignUnique = "";
	if (cmpg != null)
		campaignUnique = cmpg.getUnique();

	boolean isErrorOccur = false;

	if (aReq.get("result").length() > 1) { // if we got an answer from STS

		processingResult = true;

		if (aReq.get("result").equals("confirm") || aReq.get("result").equals("error")) {

			if (aReq.get("result").equals("confirm")) {

				// Log this event
			//	mobileclubcampaigndao.log("global_wap_header", "xhtml",
				//		uid, msisdn, null, domain, campaignUnique,
				//		club.getUnique(), "ML_CONFIRM", 0, request,
				//		response);

			} else {
				// Log this event

				isErrorOccur = true;

			//	mobileclubcampaigndao.log("global_wap_header", "xhtml",
					//	uid, msisdn, null, domain, campaignUnique,
					//	club.getUnique(), "ML_DECLINE", 0, request,
					//	response);

			}

		}
	}

	msisdn = SdcMisc.parseMobileNumber(msisdn);

	if (club == null) {
		status = "ERROR: subscription service not found";

	} else if (processingResult && isNullEmpty(msisdn)) {
		status = "Sorry, we need your cell number! "
				+ msisdn
				+ " Please go back and enter your cell number and we'll send you your FREE link instantly!";
	//	mobileclubcampaigndao.log("confirm", "xhtml", uid, msisdn,
		//		handset, domain, "", club.getUnique(), "NUMBER_INVLD",
		//		0, request, response);

	} else if (processingResult && isNotNullEmpty(msisdn)
			&& isErrorOccur) {
		status = "Sorry, that's not a valid cell number! "
				+ msisdn
				+ " Please go back and enter your cell number and we'll send you your FREE link instantly!";
	//	mobileclubcampaigndao.log("confirm", "xhtml", uid, msisdn,
		//		handset, domain, "", club.getUnique(), "NUMBER_INVLD",
			//	0, request, response);
	} else if (!processingResult) {

		status = "Sorry, that's not a valid cell number! "
				+ msisdn
				+ " Please go back and enter your cell number and we'll send you your FREE link instantly!";
	}

	else {

		// Log this event
		//mobileclubcampaigndao.log("confirm", "xhtml", uid, msisdn,
		//		handset, domain, campaignUnique, club.getUnique(),
		//		"MANUAL", 0, request, response);

		String defClubDomain = System
				.getProperty("CMS_defaultClubDomain");

		if (defClubDomain == null) {
			defClubDomain = "3651626793031CDS"; //got it from application.properties
		}

		System.out.println("defClubDomain: "
				+ System.getProperty("CMS_defaultClubDomain"));

		Calendar c1 = new GregorianCalendar();
		Date bstart = c1.getTime();
		c1.add(Calendar.DATE, club.getPeriod());
		Date bend = c1.getTime();

		SdcMobileClubUser clubUser = null;

		if (user == null && isNotNullEmpty(msisdn)) {
			String userUnique = umeuserdao.getUserUnique(msisdn,
					"msisdn", defClubDomain);
			if (!userUnique.equals(""))
				user = umeuserdao.getUser(userUnique);
		}

		if (user != null) {

			if (user.getAccountType() == 99) {
				// User account is blocked/barred
				System.out.println("Account Type = 99");
				doRedirect(response, "http://" + dmn.getDefaultUrl()
						+ "/blocked.jsp");
				return;
			}

			user.updateMap("active", "1");
			umeuserdao.commitUpdateMap(user);
			user.clearUpdateMap();

			clubUser = user.getClubMap().get(club.getUnique());
			if (clubUser != null) {
				clubUser.setParsedMobile(user.getParsedMobile());
				clubUser.setActive(1);
				clubUser.setCredits(club.getCreditAmount());
				clubUser.setAccountType(0);
				clubUser.setBillingStart(bstart);
				clubUser.setBillingEnd(bend);
				clubUser.setBillingRenew(bstart);
				if (!campaignUnique.equals(""))
					clubUser.setCampaign(campaignUnique);

				clubUser.setSubscribed(new Date());

				System.out.println("SAVING EXISTING USER: "
						+ clubUser.getAccountType() + ": "
						+ clubUser.getUserUnique() + ": " + clubUser);
				umemobileclubuserdao.saveItem(clubUser);

				//confMsg = false;
			} else {

				clubUser = new SdcMobileClubUser();
				clubUser.setUnique(SdcMisc.generateUniqueId());
				clubUser.setUserUnique(user.getUnique());
				clubUser.setClubUnique(club.getUnique());
				clubUser.setParsedMobile(user.getParsedMobile());
				clubUser.setActive(1);
				clubUser.setCredits(club.getCreditAmount());
				clubUser.setAccountType(0);
				clubUser.setBillingStart(bstart);
				clubUser.setBillingEnd(bend);
				clubUser.setBillingRenew(bstart);
				clubUser.setPushCount(0);
				clubUser.setCreated(new Date());
				clubUser.setCampaign(campaignUnique);
				clubUser.setUnsubscribed(new Date(0));
				clubUser.setSubscribed(new Date());
				clubUser.setParsedMobile(msisdn);

				umemobileclubuserdao.saveItem(clubUser); // Mobile clube user
				user.getClubMap().put(club.getUnique(), clubUser);
			}

		} else {

			// Set up a completely new user
			//if (user==null) {
			user = new UmeUser(); // <- new user...
			user.setMobile(msisdn);
			user.setWapId(SdcMisc.generateLogin(10));
		//	user.setDomain(defClubDomain);
		user.setDomain(domain);	
		user.setActive(1);
			user.setCredits(club.getCreditAmount());
			
			
			/*****************************************************************/
			UmeUserDetails sdcUserDetails=new UmeUserDetails();
			sdcUserDetails.setParsedMobile(msisdn);
			user.setUserDetails(sdcUserDetails);
			/*****************************************************************/
			
			
			
			String stat = umeuserdao.addNewUser(user);
			//}

			System.out.println("STAT: " + stat);
			if (stat.equals("")) {

				clubUser = new SdcMobileClubUser();
				clubUser.setUnique(SdcMisc.generateUniqueId());
				clubUser.setUserUnique(user.getUnique());
				clubUser.setClubUnique(club.getUnique());
				clubUser.setParsedMobile(user.getParsedMobile());
				clubUser.setActive(1);
				clubUser.setCredits(club.getCreditAmount());
				clubUser.setAccountType(0);
				clubUser.setBillingStart(bstart);
				clubUser.setBillingEnd(bend);
				clubUser.setBillingRenew(bstart);
				clubUser.setPushCount(0);
				clubUser.setCreated(new Date());
				clubUser.setCampaign(campaignUnique);
				clubUser.setUnsubscribed(new Date(0));
				clubUser.setSubscribed(new Date());
				clubUser.setParsedMobile(msisdn); //Important on Mexico else it wont work 

				umemobileclubuserdao.saveItem(clubUser);

				status = "OK: User added, unique: " + user.getUnique();
			} else
				status = SdcMisc.htmlEscape(stat);
		}
		
		
		      //===========   CPA Campaign Update Start  =============================
		    		  
		    SimpleDateFormat sdf22 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    		  
             String campaignsrc=findCampaignSrc(campaignId.trim(),dbsession);
             String updatequery="";
             SimpleDateFormat sdf233 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
             Calendar nowtimess=new GregorianCalendar();
             if (campaignsrc.equalsIgnoreCase("BitterCPA")){
                  String hashcode = (String) session.getAttribute("hashcode");
                  String bitteripAddr= (String) session.getAttribute("bitterip");
                 updatequery="UPDATE cpavisitlog SET isSubscribed='1', aSubscribed='"+sdf22.format(nowtimess.getTime())+"',"
                + " aParsedMobile='"+msisdn+"'  WHERE aHashcode='"+hashcode+"' AND isSubscribed='0'";
             }
             else if (campaignsrc.equalsIgnoreCase("BitterOldCPA")){
                  String hashcode = (String) session.getAttribute("bitterparam");
                  String bitteripAddr= (String) session.getAttribute("bitterip");
                 updatequery="UPDATE cpavisitlog SET isSubscribed='1', aSubscribed='"+sdf22.format(nowtimess.getTime())+"',"
                + " aParsedMobile='"+msisdn+"'  WHERE aHashcode='"+hashcode+"' AND isSubscribed='0'";
             }
              
             else if (campaignsrc.equalsIgnoreCase("MundoCPA")){
                String mundcampaign = (String) session.getAttribute("mundocampaign");
                String clickid = (String) session.getAttribute("mundoclickid");
                String ipAddr = (String) session.getAttribute("mundoip");
                
                updatequery="UPDATE cpavisitlog set isSubscribed='1', aSubscribed='"+sdf22.format(nowtimess.getTime())+"',"
                + "aParsedMobile='"+msisdn+"'  WHERE isSubscribed='0' " 
                + " AND cpacampaignid='"+mundcampaign+"'"+ " AND clickid='"+clickid+"'";
                  
              }
             if(updatequery.trim().length()>0)
             {
                 try{
 //   Connection updateconnection=DBHStatic.getConnection();
   //    if(updateconnection==null || updateconnection.isClosed()) updateconnection=DBHStatic.getConnection();
       Transaction trans=dbsession.beginTransaction();
       Query query=null; 
   		query=dbsession.createSQLQuery(updatequery);
   		int result=query.executeUpdate();   
   		trans.commit();
      // DBHStatic.execUpdate(updateconnection, updatequery);
	//DBHStatic.closeConnection(updateconnection); 
                 }catch(Exception ee){System.out.println("ZA cpa debug "+"confirm update query: Exception "+ ee);}
     
            try{     
                 if(campaignsrc.toLowerCase().contains("cpa"))
	pauseCPATrack(msisdn,campaignId,club.getUnique(),"telcel");
    }
    catch(Exception eE)
    {
        System.out.println("ZA cpa debug "+"confirm PauseCPA track "+ eE);
        
    }
             }
             
          //====================== CPA Campaign End ================================ 

		/*
		This doesn't need ticket because ticket will be created either by adhoc billing or billing deamon according to the information set be 
		notification url via notification.jsp page
		 */

		//This is for subscription so this must

//		mobileclubcampaigndao.updateStatus(1, user.getUnique(),
	//			campaignUnique);
//mobileclubcampaigndao.log("confirm", "xhtml", user.getUnique(), user.getParsedMobile(), handset, domain, campaignUnique, club.getUnique(),"SUBSCRIBED", 1, request, response);

		status = club.getWebConfirmation();

		if (status == null || status.equals(""))
			status = "Your account has been activated.<br>You should receive a Text shortly containing your personal link to the service.";
		ses.setAttribute("sdc_msisdn_param", msisdn);
           if(user!=null){
        	   
        	  System.out.println("redirecting from confirm.jsp");
   doRedirect(response, "http://" + dmn.getDefaultUrl() + "/?id="
     + user.getWapId());
   
   return;

  }
	}
%>

<%!
  public void pauseCPATrack(String msisdn,String campaign,String clubunique, String networkcode)
     
{
    SimpleDateFormat dates = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String nextpush="";
    //cpaLog cpapause=new cpaLog();
    Calendar currentTime=Calendar.getInstance();
    currentTime.add(Calendar.MINUTE, 10);
    nextpush=dates.format(currentTime.getTime());
   // cpapause.pauseLogFor(msisdn,campaign,clubunique,nextpush,0,networkcode);
    
}


%>





<%@ include file="xhtmlhead.jsp"%>
<body>
	<div id="pgframe" align="center">
		<div class="container">
			<div class="header" align="left">
				<img src="/images/wap/<%=dImages.get("img_header2_" + handset.getImageProfile())%>" alt="" width="<%=handset.getFullWidth()%>" 	height="<%=handset.getImageHeight("header2", dImages)%>" />
			</div>

			<div class="item" align="left">
				<br />
				<%=status%>

				<img src="http://rad.reporo.net/conversion" />

				<%
					if (status.startsWith("You have chosen not to confirm")) {
				%>
				<br /> <br />
				<div style="background-color: #FFFFFF; color: #3333FF;" align="left">

					<a
						href="/wapx/act_bannerlog.jsp?bunq=2457564907031KDS&id=nKtRMw6vG5"
						target="_blank"> <img src="/images/ads/7457564907031KDS.gif"
						border="0">
					</a>
				</div>
				<%
					}
				%>

				<br /> <br />
			</div>


			<div class="footer" align="left">
				<img src="/images/wap/<%=dImages.get("img_footer2_" + handset.getImageProfile())%>" alt="" width="<%=handset.getFullWidth()%>" height="<%=handset.getImageHeight("footer2", dImages)%>" />
			</div>
		</div>
	</div>
</body>
</html>
