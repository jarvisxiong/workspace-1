<%@ page import="com.mixmobile.anyx.sdk.*, java.util.Properties" %>

<%
/*
File Name: index_5.jsp 
Parent Project(s): 
Purpose: 
Current version of file: 
Derived from:
Modifiers of this file:
History: 
	16/05/2008 10:15:23 - Created By Anyx
JDK Version: jdk1.3 
Non-standard dependencies:
Comments/Caveats:
*/
//***************************************************************************************************
AnyxRequest aReq = new AnyxRequest(request);
String uid = aReq.getUserId();
String ugid = aReq.getUserGroup();
String anyxSrvc = aReq.getAnyxService();
String domain = aReq.getDomain();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "ISO-8859-1";
response.setContentType("text/html; charset=" + pageEnc);

String fileName = "index_5";
Properties p = ServiceProperties.getFromContext(anyxSrvc, "service", application);
//LangProps lp = LangProps.getFromContext(anyxSrvc, fileName, lang, domain, application);
//String picLang = lp.getLang();
//***************************************************************************************************

String statusMsg = "";

// Get Database Handler from server context
DBHandler dbH = (DBHandler) application.getAttribute("dbHandler");
String anyxDb = System.getProperty("dbName");

// Parses incoming message parameters and logs this message into the table 'msgInLog'
IncomingSmsMsg inMsg = new IncomingSmsMsg(aReq, dbH, anyxDb);
String fromNumber = inMsg.getFromNumber();
String command = inMsg.getCommand();
String bodyText = inMsg.getBodyText();

//***************************************************************************************************

String msg = "New Service: Coupon Management\nCreated: 16/05/2008 10:15:23\n You sent: " + command;

if (!fromNumber.equals("")) {
	if (!msg.equals("")) {
		
		OutgoingSmsMsg oSms = new OutgoingSmsMsg(aReq, application, dbH, anyxDb);
		oSms.setToNumber(fromNumber);
		oSms.setMsgBody(msg);		
		
		//Uncomment this if you want this message to be free for your users (no credits will subtracted)
		//oSms.setCostPerMsg(0);
		
		//Uncomment this if you DO NOT want this outgoing message to be logged (table: 'msgOutLog')
		//oSms.setLogThis(false);
		
		statusMsg = oSms.send();
	}
	else { statusMsg = Mxmp.get(56); }
}
else { statusMsg = Mxmp.get(58); }

// Prints the status message to ServletEngine's console window
System.out.println(statusMsg);

//***************************************************************************************************

%>



