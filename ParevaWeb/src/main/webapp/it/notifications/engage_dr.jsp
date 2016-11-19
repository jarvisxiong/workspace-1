<%@page import="ume.pareva.pojo.EngageDRParameters,ume.pareva.uk.EngageDR"%>
<%@page import="org.springframework.web.servlet.support.RequestContextUtils"%>
<%@page import="org.springframework.context.ApplicationContext"%>


<%
System.out.println("User: "+request.getParameter("USER"));
System.out.println("Password: "+request.getParameter("Password"));
System.out.println("RequestIDser: "+request.getParameter("RequestID"));
System.out.println("XML: "+request.getParameter("TP_XML"));

EngageDRParameters engageDRParameters=new EngageDRParameters();
engageDRParameters.setUser(request.getParameter("USER"));
engageDRParameters.setPassword(request.getParameter("Password"));
engageDRParameters.setRequestId(request.getParameter("RequestID"));
engageDRParameters.setTpXML(request.getParameter("TP_XML"));

EngageDR engageDR=null;

try{
     ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
     ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
     engageDR=(EngageDR) ac.getBean("engageDR");
     engageDR.processRequest(engageDRParameters);
     }
     catch(Exception e){
         e.printStackTrace();
     }


//String tpXML=" <?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><!DOCTYPE WIN_RECEIPTS SYSTEM \"tpbound_receipts_v3p1.dtd\"><WIN_RECEIPTS><SMSRECEIPT><SERVICEID>2</SERVICEID><SOURCE_ADDR>+447611111111</SOURCE_ADDR><TRANSACTIONID>514138176</TRANSACTIONID><NETWORKID>2</NETWORKID><STATUSID>5</STATUSID><STATUSDATETIME><DD>26</DD><MMM>AUG</MMM><YYYY>2015</YYYY><HH>06</HH><MM>17</MM><SS>59</SS></STATUSDATETIME><TOTALFRAGMENTNO>0</TOTALFRAGMENTNO><FRAGMENTID>0</FRAGMENTID></SMSRECEIPT></WIN_RECEIPTS>";
/* String tpXML=request.getParameter("TP_XML");
tpXML=tpXML.substring(tpXML.indexOf("<WIN_RECEIPTS>"));


DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
Document doc = dBuilder.parse(new InputSource(new StringReader(tpXML)));
doc.getDocumentElement().normalize();
String transactionId=doc.getElementsByTagName("TRANSACTIONID").item(0).getTextContent();
String msisdn=doc.getElementsByTagName("SOURCE_ADDR").item(0).getTextContent().substring(1);
boolean exist=umequizdao.quizReceiptExistOrNot(Integer.parseInt(transactionId));
if(!exist){
	umequizdao.saveReceipt(tpXML);
	SdcSmsSubmit sdcSmsSubmit=umesmsdao.getSmsMsgLog(transactionId);
String shortCode=sdcSmsSubmit.getFromNumber();

MobileClub club = null;
java.util.List<MobileClub> clubs = UmeTempCmsCache.mobileClubsByNumber.get(shortCode);

if(clubs!=null)
{
    club = clubs.get(0);
}
    
if(sdcSmsSubmit!=null && sdcSmsSubmit.getMsgType().equals("Premium")){
	System.out.println("Checking Transaction Status");
	GetTransactionStatusRequest getTransactionStatusRequest=new GetTransactionStatusRequest(new Integer(transactionId),transactionId+"_"+msisdn);
	IMIBillingDoi imiBillingDoi=new IMIBillingDoi();
	TransactionStatusRequest transactionStatusRequest=new TransactionStatusRequest(Integer.parseInt(transactionId),transactionId+"_"+msisdn,msisdn,new java.sql.Timestamp(System.currentTimeMillis()));
	umequizdao.saveTransactionStatusRequest(transactionStatusRequest);
	GetTransactionStatusResponse getTransactionStatusResponse=imiBillingDoi.getTransactionStatus(getTransactionStatusRequest);
	System.out.println("TransactionID: "+getTransactionStatusResponse.getTransactionId());
	System.out.println("Transaction Reference: "+getTransactionStatusResponse.getTransactionReference());
	System.out.println("Transaction Status: "+getTransactionStatusResponse.getStatus().getValue());
	System.out.println("Transaction MSISDN: "+getTransactionStatusResponse.getMSISDN());
	TransactionStatusResponse transactionStatusResponse=new TransactionStatusResponse(getTransactionStatusResponse.getTransactionId(),
														getTransactionStatusResponse.getTransactionReference(),getTransactionStatusResponse.getStatus().getValue(),
														getTransactionStatusResponse.getMSISDN(),
														networkMapping.getUkNetworkMap().get(getTransactionStatusResponse.getNetworkId()),
														getTransactionStatusResponse.getSignature(),getTransactionStatusResponse.getFault().getMessage(),
														new java.sql.Timestamp(System.currentTimeMillis()));
	umequizdao.saveTransactionStatusResponse(transactionStatusResponse);
	if(getTransactionStatusResponse.getStatus().getValue().equals("1")){
			MobileClubBillingPlan mobileClubBillingPlan=billingplandao.getBillingPlanByMsisdnAndClubUnique(msisdn,club.getUnique());
		if(mobileClubBillingPlan!=null){
			Calendar c = Calendar.getInstance();
			// Set the calendar to monday of the current week
	    	c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	    	c.setTime(c.getTime());
	    	c.add(Calendar.DATE,7);
	    	mobileClubBillingPlan.setNextPush(c.getTime());
	    	SdcMobileClubUser clubUser=umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
	    	umemobileclubuserdao.updateBillingRenew(clubUser.getUserUnique(),club.getUnique());
			billingplandao.updateSuccessBillingPlan(mobileClubBillingPlan,"UK");
		}
	}else{
		MobileClubBillingPlan mobileClubBillingPlan=billingplandao.getBillingPlanByMsisdnAndClubUnique(msisdn,club.getUnique());
		System.out.println("MobileClubBIllingPlan :"+mobileClubBillingPlan);
		if(mobileClubBillingPlan!=null){
			Calendar cal = Calendar.getInstance(); 
    		cal.setTime(new Date()); 
    		cal.add(Calendar.HOUR_OF_DAY, 12); 
    		billingplandao.updateFailedBillingPlan(mobileClubBillingPlan, cal.getTime());
		}
	}
}
} */
%>
