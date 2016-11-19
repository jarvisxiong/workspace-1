f<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="org.joda.time.DateTime"%>
<%@ include file="coreimport.jsp"%>
<%@ include file="ukheader.jsp"%>
<%@include file="cpavisit.jsp"%>


<%
String campaignId=httprequest.get("cid");
String landingPage="unknown";
String campaignsrc="";
String visitsubscribed="1970-01-01 00:00:00";
String isSubscribed="0";
String msisdn="";
try{
    Thread.sleep(500);
System.out.println("msisdnheader "+request.getHeader("X-PFI-Alias"));
msisdn=request.getHeader("X-PFI-Alias");
System.out.println("msisdnheader value of msisdn "+msisdn);
}catch(Exception e){msisdn="";}


String transaction_ref="";
if(campaignId==null || campaignId.trim().isEmpty()) campaignId="1144";
String contentid=Misc.generateLogin(5)+"_"+campaignId;

try{
transaction_ref=request.getHeader("X-PFI-SessionToken");
if(transaction_ref==null || transaction_ref.trim().length()<=0)
    transaction_ref=(String) session.getAttribute("sessiontoken");

System.out.println("pfitoken  from header : "+request.getHeader("X-PFI-SessionToken"));

if(transaction_ref==null || transaction_ref.trim().length()<=0)
{
    UUID submission_id = UUID.randomUUID();
    transaction_ref=submission_id+""; 
    session.setAttribute("sessiontoken",transaction_ref);
    request.setAttribute("sessiontoken",transaction_ref);
    
    
    System.out.println("pfitoken "+transaction_ref);
    
    
}
} catch(Exception e){}

String myip=getClientIpAddr(request);
session.setAttribute("userip",myip);
request.setAttribute("userip",myip);
String myisp=ipprovider.findIsp(myip);
 MobileClubCampaign cmpg = null;
 
String networkname=myisp.toLowerCase();
try{
networkname=mobilenetwork.getMobileNetwork("UK",networkname);
}catch(Exception e){networkname="unknown";}


MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
String clubUnique = "";
if (club!=null) clubUnique = club.getUnique();

if (!campaignId.equals("")) cmpg = UmeTempCmsCache.campaignMap.get(campaignId);

if(cmpg!=null && cmpg.getSrc().equalsIgnoreCase("test"))
  {
     
    landingPage=landingpage.initializeLandingPage(domain,campaignId,"test");
                request.setAttribute("landingPage",landingPage);
         
}
else {
 if(!campaignId.equals("")){
		landingPage=landingpage.initializeLandingPage(domain,campaignId,"all");
                request.setAttribute("landingPage",landingPage);
	}else {
		landingPage=landingpage.initializeLandingPage(domain);
                request.setAttribute("landingPage",landingPage);
	}
}
    
if(campaignId!=null && campaignId.trim().length()>0) {
   
   if (!campaignId.equals("")) cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
   if(cmpg!=null) { campaignsrc = cmpg.getSrc();  }
   
   if(cmpg!=null && cmpg.getSrc().endsWith("CPA"))
   %>
    <%@include file="cpavisitlog.jsp"%>
   
   <%
   if(cmpg!=null && cmpg.getSrc().endsWith("RS"))
    %>
     <%@include file="revsharevisitlog.jsp"%>
    <%
}    
   
     campaigndao.log("index", landingPage, msisdn,msisdn, handset,domain, campaignId, club.getUnique(), "INDEX", 0, request,response,networkname.toLowerCase());
     
     //This is for IDENTIFIED HITS
     if(msisdn!=null && !msisdn.trim().isEmpty() && msisdn.trim().length()>0)
         campaigndao.log("index", landingPage, msisdn,msisdn, handset,domain, campaignId, club.getUnique(), "IDENTIFIED", 0, request,response,networkname.toLowerCase());
     

     
     //This is for CampaignHit Counter
     java.sql.Date today=new java.sql.Date(System.currentTimeMillis());
CampaignHitCounter campaignHitCounter=campaignhitcounterdao.HitRecordExistsOrNot(today,domain,campaignId,landingPage);
if(campaignHitCounter==null){
	campaignHitCounter=new CampaignHitCounter();
	campaignHitCounter.setaUnique(Misc.generateUniqueId());
	campaignHitCounter.setaDomainUnique(domain);
	campaignHitCounter.setCampaignId(campaignId);
	campaignHitCounter.setLandingPage(landingPage);
	campaignHitCounter.setDate(today);
	campaignHitCounter.setHitCounter(1);
	campaignHitCounter.setSubscribeCounter(0);
	campaignhitcounterdao.saveCampaignHitCounter(campaignHitCounter);
	
}else{
	//campaignHitCounter.setDate(today);
	campaignhitcounterdao.updateCampaignHitCounter(campaignHitCounter);
}

PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();

context.put("contenturl","http://"+dmn.getContentUrl());
context.put("campaignid",campaignId); //cid
context.put("cid",campaignId); //cid
context.put("endofmonth",getEndOfmonth());
if(cmpg!=null) context.put("campaignsrc",cmpg.getSrc().trim());
context.put("landingpage",landingPage);
context.put("myisp",myisp);
context.put("transactionid",transaction_ref);
context.put("referenceid",contentid);
context.put("msisdn",msisdn);
context.put("cpaparam1",cpaparam1);
context.put("cpaparam2",cpaparam2);
context.put("cpaparam3",cpaparam3);

System.out.println("sessiontoken "+transaction_ref);


String merchanttoken="FBEEBAC4-9F42-4DC4-8E5D-B481A30986B0";
System.out.println("merchanttoken "+dmn.getUnique());

if(dmn.getUnique().equalsIgnoreCase("3824583922341llun")) 
    merchanttoken="59E2E445-E8E3-4696-8015-037E7963F716";


request.setAttribute("merchanttoken",merchanttoken);
session.setAttribute("merchanttoken",merchanttoken);
response.addHeader("X-PFI-MerchantToken",merchanttoken);
response.addHeader("X-PFI-SessionToken", transaction_ref);

System.out.println("merchanttoken "+merchanttoken+ "session token "+transaction_ref);

engine.getTemplate(landingPage).evaluate(writer, context);      
String output = writer.toString();


%>

<%!

String getEndOfmonth(){
    
    Calendar nowTime=GregorianCalendar.getInstance();
        SimpleDateFormat formattr=new SimpleDateFormat("yyyy-MM-dd");
        String currentDate=formattr.format(nowTime.getTime()).toString();
        
        System.out.println("currentDate "+currentDate);
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime dateTime = dtf.parseDateTime(currentDate);
        DateTime lastDate = dateTime.dayOfMonth().withMaximumValue();
        
        DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yy");
        String endofmonth=dtfOut.print(lastDate);
        System.out.println(endofmonth);
        return endofmonth;
    
}


%>
