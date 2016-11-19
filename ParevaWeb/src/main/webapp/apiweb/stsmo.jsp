<%@page import="ume.pareva.util.ZACPA"%>
<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@page import="ume.pareva.smsapi.ZaSmsSubmit"%>
<%@page import="org.jdom.Namespace"%>
<%@page import="org.jdom.Element"%>
<%@page import="org.jdom.Document"%>
<%@page import="org.jdom.input.SAXBuilder"%>
<%@page import="com.zadoi.service.ZaDoi"%>
<%@include file="coreimport.jsp" %>

<%
System.out.println("zastopquery ZASTOP Called Upon");
UmeSessionParameters httprequest= new UmeSessionParameters(request);
UmeUser user = httprequest.getUser();
String lang = httprequest.getLanguage().getLanguageCode();
UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
SdcService service = (SdcService) request.getAttribute("umeservice");

httprequest.setPageEnc("iso-8859-1");
String fileName = "stsmo";
String crlf = "\r\n";
String landingpage="unknown";
String ip = request.getRemoteAddr();
// Restrict IP
//System.out.println("ip: " + ip);
//
//System.out.println("ZASTOP --------------------- Starting " + fileName);
//System.out.println("ZASTOP cLength: " + request.getContentLength());

BufferedReader reader = request.getReader();
StringBuilder sb = new StringBuilder();

int c;
while ((c = reader.read()) != -1) {
    sb.append((char)c);
}
so.l("Read");

if (sb.length()<=0) {
    //System.out.println("ZASTOP"+" No XML. returning");
    return;
}
System.out.println("zastopquery ZASTOP: "+sb.toString());

String sessionId = "";
String msgCode1 = "";
String fromNumber = "";
String toNumber = "";
String networkCode = "";
String msgBody = "";
String msgCode2 = "";
String status="";
String reference="";
String deliveryDatetime="";
String tag = "";
String tag2 = "";

SAXBuilder builder = new SAXBuilder(false);
builder.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
Document doc = builder.build(new StringReader(sb.toString()));
Element elem = doc.getRootElement();
Namespace xmlns = elem.getNamespace();

java.util.List list  = elem.getChildren();
for (int i=0; i<list.size(); i++) {
    elem = (Element) list.get(i);
    tag = elem.getName().toLowerCase();
    System.out.println("ZASTOP "+" TAG: " + tag);
    
    if (tag.equals("moid")) sessionId = elem.getTextTrim();
    else if (tag.equals("usermessagereference")) msgCode1 = elem.getTextTrim();
    else if (tag.equals("msisdn")) fromNumber = elem.getTextTrim();
    else if (tag.equals("network_id")) networkCode = elem.getTextTrim();
    else if (tag.equals("destination")) toNumber = elem.getTextTrim();
    else if (tag.equals("message")) msgBody = elem.getTextTrim();
    else if (tag.equals("datetime")) msgCode2 = elem.getTextTrim();
    else if (tag.equals("reference")) reference = elem.getTextTrim();
    else if (tag.equals("status")) status = elem.getTextTrim();
    else if (tag.equals("deliverydatetime")) deliveryDatetime = elem.getTextTrim();

}






//System.out.println("ZASTOP now going for further checks ");

boolean responses=true;
String serviceName="";
String doiresp="";

SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

String msisdn = "";
String kw ="";
boolean stopmsg=false;

if(msgBody!=null && msgBody.trim().length()>0) kw=msgBody;

//System.out.println("ZASTOP "+" VALUE OF STOPMSG is FIRST "+stopmsg);
if(kw!=null && kw.trim().length()>0)
{
  stopmsg=kw.equalsIgnoreCase("stop") || kw.equalsIgnoreCase("end") || kw.equalsIgnoreCase("cancel") 
        || kw.equalsIgnoreCase("unsubscribe") || kw.equalsIgnoreCase("quit") || kw.trim().toLowerCase().contains("stop");
  
  if(stopmsg)
  {
      msisdn=fromNumber;
  }
  else 
   {
      msisdn=toNumber;
    }
}
else 
   {
      msisdn=toNumber;
    }
//System.out.println("ZASTOP "+" message receiving before "+ msisdn);
if (msisdn.equals("")) return;
System.out.println("zastopquery ZASTOP "+" message receiving"+ msisdn);

String resp = "";

String shortCode ="";// toNumber;
    
UmeSmsDao smsdao=null;
UmeMobileClubUserDao clubuserdao=null;
UmeSmsDaoExtension umesmsdaoextension=null;
MobileClubCampaignDao campaigndao=null;
MobileClubBillingPlanDao billingplandao=null;
ZACPA zacpa=null;
MobileClubCampaign cmpgn = null;
UserAuthentication userauthentication=null;
CheckStop checkstopcount=null;
MobileClubDao mobileclubdao=null;
StopUser stopuser=null;
try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      smsdao=(UmeSmsDao)ac.getBean("umesmsdao");
      clubuserdao=(UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
      umesmsdaoextension=(UmeSmsDaoExtension) ac.getBean("umesmsdaoextension");
      campaigndao=(MobileClubCampaignDao) ac.getBean("mobileclubcampaigndao");
      billingplandao=(MobileClubBillingPlanDao) ac.getBean("billingplandao");
      zacpa=(ZACPA) ac.getBean("zacpa");
      userauthentication=(UserAuthentication) ac.getBean("authorizeuser");
      checkstopcount=(CheckStop) ac.getBean("checkstop");
      mobileclubdao=(MobileClubDao) ac.getBean("mobileclubdao");
      stopuser=(StopUser) ac.getBean("stopuser");
    }
catch(Exception e){e.printStackTrace();}
//smsdao.logDelivery(msg, uid, dmn.getUnique(), service.getUnique());



System.out.println("ZASTOP "+" value of stopmsg is "+ stopmsg);
if(stopmsg) shortCode=toNumber;
boolean shouldStop=true;//checkstopcount.checkStopCount("ZA");

if(msisdn.equals("27833260830")) {
    System.out.println("ZASTOP SessionId (moid): " + sessionId);
System.out.println("ZASTOP User message ref: " + msgCode1);
System.out.println("ZASTOP msisdn: " + fromNumber);
System.out.println("ZASTOP network id: " + networkCode);
System.out.println("ZASTOP destination: " + toNumber);
System.out.println("ZASTOP message " + msgBody);
System.out.println("ZASTOP datetime: " + msgCode2);
System.out.println("ZASTOP status: " + status);
System.out.println("ZASTOP reference: " + reference);
System.out.println("ZASTOP shouldStop: " + shouldStop);
}
if(shouldStop){
if (stopmsg && shortCode!=null && shortCode.trim().length()>0)
{
    if(shortCode.length()>11) shortCode = "ext" + shortCode.substring(11);
    if (shortCode.trim().equalsIgnoreCase("DOI")) shortCode = "43201";
    
MobileClub club = null;
java.util.List<MobileClub> clubs = UmeTempCmsCache.mobileClubsByNumber.get(shortCode);
System.out.println("ZASTOP "+" keyword: "+kw+" shortcode "+shortCode+" msisdn "+msisdn+" clubs "+clubs.size());

String stoprecordMOQuery="insert into smsMsgLog(aUnique,aMsgUnique,aFromNumber,aToNumber,aCreated,aMsgBody,aClubUnique,aMsgType,aMsgCode1,aStatus)"
                     + " VALUES('"+Misc.generateUniqueId()+"','"+reference+"','"+fromNumber+"','"+toNumber+"','"+sdf2.format(new Date())+"',:msgbody,'"+clubs.get(0).getUnique()+"','"+"STOP"+"','"+msgCode1+"','RECEIVED')";
System.out.println("ZASTOP QUERY "+stoprecordMOQuery);
  int inserted=zacpa.executeZAStop(stoprecordMOQuery,msgBody);
if(clubs!=null) {
    ZaDoi zadoi=new ZaDoi();
    long beforeAuth = System.currentTimeMillis();
    String token = zadoi.authenticate();
    boolean terminated=false;
    String unSubscribed=sdf2.format(new Date());
    
    if(toNumber.equalsIgnoreCase("43201")) {
        System.out.println("ZASTOP  tonumber is 43201");
        SdcMobileClubUser clubUser=clubuserdao.getLatestSubscribedClub(msisdn);
        if(clubUser!=null && clubUser.getActive()==1) {
        club=mobileclubdao.getMobileClubMap().get(clubUser.getClubUnique());
        stopuser.stopSingleSubscriptionNormal(msisdn,club.getUnique(), request, response);
        
        }
    }
    else {
    
       for (int i=0; i<clubs.size(); i++) {
        club = clubs.get(i);
        serviceName=club.getOtpServiceName();		        
        terminated=zadoi.delete_DoubleOptIn_Record(token,serviceName,msisdn);
        System.out.println("ZASTOP "+"==USER TERMINATED ==== "+terminated+" mobile "+msisdn+" club: "+club.getUnique());
        
        if(terminated){
       
        SdcMobileClubUser clubUser=clubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
        if(clubUser!=null && clubUser.getActive()==1) {
        stopuser.stopSingleSubscriptionNormal(msisdn,club.getUnique(), request, response);
           
        }
        }
         
     } // End FOR Loop  
}
}

    
}
}
//System.out.println("ZASTOP "+" stopmsg before entering into condition "+ msisdn+" stopmsg "+stopmsg+ " status "+status);
if(!stopmsg)
{
    String smsresp="";
    String networkid="";
        if(networkCode!=null && networkCode.trim().length()>0) 
		               {
		        if(networkCode.trim().equals("1"))networkid="vodacom";
		        if(networkCode.trim().equals("2"))networkid="mtn";
		        if(networkCode.trim().equals("3"))networkid="cellc";
		        if(networkCode.trim().equals("5"))networkid="heita";
		         
		    }
    if(status.equalsIgnoreCase("DELIVRD"))
    {
        resp="OK";      
        status="delivered";
    }
    else  resp="ERROR";
    
    
        String deliverydate=sdf2.format(new Date());
        //System.out.println("ZASTOP "+" updating the status as "+status+" resp "+resp); //+" deliverydate: "+deliverydate+ " smsdao "+smsdao
        smsdao.updateResponse(reference,status,deliverydate,resp,"");
}
%>