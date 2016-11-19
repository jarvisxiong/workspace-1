

<%@page import="ume.pareva.dao.SdcRequest"%>
<%@page import="ume.pareva.sdk.MiscDate"%>
<%@page import="ume.pareva.pojo.MobileClubBillingPlan"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Calendar"%>
<%@page import="org.apache.http.HttpResponse"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.ResultSet"%>

<%!

public static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


protected synchronized void forBI(MobileClubBillingPlan item){
	
	   Calendar dcal = Calendar.getInstance();
	   Date curdate = dcal.getTime();
	   String currentTime = sdf2.format(curdate.getTime());
				
}
				
				
	 

protected synchronized int updateBillingRenew(MobileClubBillingPlan item,Date BillingRenew,Date BillingEnd,Connection con) throws Exception {

		int stat = 0;
		System.out.println("Germany : " + " Update Billing Renew details "
				+ item.getParsedMobile() + " ClubUnique : "
				+ item.getClubUnique());
		String updateBillingRenew = "UPDATE mobileClubUsers SET aBillingRenew=?,aBillingEnd=? "	+ " WHERE aParsedMobile=? AND aClubUnique=?";
		PreparedStatement preBillingUserUpdateStatement = null;
		try {
			 
			preBillingUserUpdateStatement = con.prepareStatement(updateBillingRenew);
			preBillingUserUpdateStatement.setString(1,MiscDate.toSqlDate(getAddedDate(BillingRenew)));
			preBillingUserUpdateStatement.setString(2,MiscDate.toSqlDate(getAddedDate(BillingEnd)));
			preBillingUserUpdateStatement.setString(3, item.getParsedMobile());
			preBillingUserUpdateStatement.setString(4, item.getClubUnique());
			preBillingUserUpdateStatement.executeUpdate();

		} catch (SQLException eE) {

		} finally {
			if (preBillingUserUpdateStatement != null) {
				preBillingUserUpdateStatement.close();
			}
		}
		return stat;
	}

	public Date getAddedDate(Date date) {
		 
		Calendar c = Calendar.getInstance();
		c.setTime(date); // Now use today date.
		c.add(Calendar.DATE, 6); // Adding 5 days

		return c.getTime();
	} 
	
	%>




<%
	/**
	 notification for RESERVED:
	 <NotificationSenderModule Handler #53> [HttpRequest] Sending POST request to http://heimvideos.realgirls.mobi/notification.jsp&userid=394343&msisdn=00491711049392&command=deliverTransactionState&serviceCode=DE011027&sessionId=DE011027x1385377312033&trid=206386&statusNumber=1&statusText=Reserved


	 and commmit payment:
	 Received request: GET /?username=umenter&serviceCode=DE011027&command=commitPayment&trid=206386&password=f6pUbr5d HTTP/1.1
	 ...
	 [HTTPAdapter] Sending data: <res><resultCode>100</resultCode><resultText>OK</resultText><msisdn>00491711049392</msisdn></res>

	 notiifcation CHARGED:
	 <NotificationSenderModule Handler #54> [HttpRequest] Sending POST request to http://heimvideos.realgirls.mobi/notification.jsp?userid=394343&msisdn=00491711049392&command=deliverTransactionState&serviceCode=DE011027&sessionId=DE011027x1385377312033&trid=206386&statusNumber=2&statusText=Charged

	 **/

	SdcRequest aReq = new SdcRequest(request);

	//=======================================These parameter form notification 

	 
	 
	String command = aReq.get("command");//Update type identificator (1=Subscribe 2=Cancel 3=Renew)
	String statusNumber = aReq.get("statusNumber");
	String statusText = aReq.get("statusText");
	String sessionIdNth=aReq.get("subscriptionId");
        System.out.println("germany testing "+command+" sessionIdNth "+sessionIdNth);

	//=====================================================================

	if (statusNumber.trim().equals("2") && statusText.trim().toLowerCase().equals("charged")) {

//		Connection con = DBH.getConnection();
//
//		if (con == null || con.isClosed()) {
//	con = DBH.getConnection();
//		}
//		
//		try{
//	
//	if(sessionIdNth!=null && sessionIdNth.length()>0){
//	
//	String sql="select aUserUnique,aClubUnique,aParsedMobile,aCampaign,aNetworkCode,aBillingEnd,aBillingRenew from mobileClubUsers where aParam1='"+ sessionIdNth+"'";
//	System.out.println("germany testing "+sql);
//	Statement st=con.createStatement();
//	ResultSet rs=st.executeQuery(sql);
//	
//	con.setAutoCommit(false);
//	
//	if(rs.next()){
//		
//		Calendar c1 = new GregorianCalendar();
//
////		MobileClubBillingPlan bill = new MobileClubBillingPlan;
////		bill.setSubUnique(rs.getString("aUserUnique"));
////		bill.setClubUnique(rs.getString("aClubUnique"));
////		bill.setParsedMobile(rs.getString("aParsedMobile"));
////		bill.setNetworkCode(rs.getString("aNetworkCode"));
////		bill.setCreated(c1.getTime());
////		bill.setStatus(1);
////		bill.setTariffClass(0);
////		bill.setLastPush(MiscDate.parseSqlDate("2011-01-01"));
////		bill.setCampaign(rs.getString("aCampaign"));
////		MobileClubBillingDao.saveItem(bill);
//		
//		//updateBillingRenew(bill,rs.getDate("aBillingEnd"),rs.getDate("aBillingRenew"),con);
//	}
//	con.commit();
//	}
//		} catch (SQLException sE) {
//	sE.printStackTrace();
//
//		} finally {
//			
//			if(con!=null && !con.isClosed()){
//				
//				try{
//					con.close();
//					
//				}catch(Exception e){}
//			}
//		}
		 
	}else{   
		
		
	}
%>