<%@ page import="com.mixem.sdc.*, com.mixmobile.anyx.snp.*, com.mixmobile.anyx.sdk.*, java.util.*, java.text.*, java.io.*, java.sql.*" %><%

//***************************************************************************************************
SdcRequest aReq = new SdcRequest(request);
UmeUser user = aReq.getUser();
UmeDomain domain = aReq.getDomain();
SdcService service = aReq.getService();

String stylesheet = aReq.getStylesheet();
String pageEnc = aReq.getEncoding();
response.setContentType("text/html; charset=" + pageEnc);

String fileName = "indexsms";
//LangProps lp = LangProps.getFromContext(service.getUnique(), fileName, aReq.getLangCode(), domain.getUnique(), application, true);
//***************************************************************************************************

SdcSmsMessage msg = aReq.getSmsMessage();
System.out.println("smsMsg: " + msg);
System.out.println("User: " + user);
if (msg==null) return;

String msisdn = Misc.parseMobileNumber(msg.getFromNumber());
if (msisdn.equals("")) return;

String uid = "";
if (user!=null) uid = user.getUnique();

System.out.println("User Unique: " + uid);
System.out.println("Incoming SMS:");
msg.print();

String resp = "New SMS is activated. Msg: " + msg.getKeyword() + " " + msg.getMsgBody();
System.out.println("RESP: " + resp);


if (!resp.equals("")) {

    OutgoingSmsMsg oSms = new OutgoingSmsMsg(aReq, application);
    oSms.setTariffClass(0);
    oSms.setServiceDesc(0);
    oSms.setUserMobile(msisdn);
    oSms.setMsgUnique("res_" + msg.getUnique());
    oSms.setMaxMsgs(1);
    oSms.setCostPerMsg(0);
    oSms.setToNumber(msisdn);
    oSms.setMsgBody(resp);
    oSms.send();

}

%>