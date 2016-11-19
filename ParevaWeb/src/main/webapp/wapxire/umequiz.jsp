<%@page import="com.fasterxml.jackson.databind.ObjectMapper"%>
<%@page import="javax.xml.bind.DatatypeConverter"%>
<%@page import="ume.pareva.util.httpconnection.HttpURLConnectionWrapper"%>
<%@page import="ume.pareva.util.httpconnection.HttpURLConnectionWrapper"%>
<%@page import="org.joda.time.format.DateTimeFormatter"%>
<%@page import="org.joda.time.format.DateTimeFormat"%>
<%@page import="org.joda.time.DateTime"%>
<%@ include file="coreimport.jsp"%>
<%@ include file="ukheader.jsp"%>
<%@include file="cpavisit.jsp"%>


<%
String quizNo = httprequest.get("quizNo");
String optionAnswer=httprequest.get("selectedOption");
String campaignId=httprequest.get("cid");
String invalidno=httprequest.get("invalidno");
String step2request=httprequest.get("step2request");

String networktest=httprequest.get("test");

System.out.println("quiztestinvalid "+invalidno);
String campaignsrc="";
String landingPage="landing-page-c-ire";
String msisdn="";
String visitsubscribed="1970-01-01 00:00:00";
//String ipaddress=request.getRemoteAddr();
String isSubscribed="0";
String transaction_ref="compcpa";

String myip=getClientIpAddr(request);
session.setAttribute("userip",myip);
request.setAttribute("userip",myip);
String myisp=ipprovider.findIsp(myip);
 MobileClubCampaign cmpg = null;
 
String networkname=myisp.toLowerCase();
try{
networkname=mobilenetwork.getMobileNetwork("IE",networkname);
}catch(Exception e){networkname="unknown";}


MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
String clubUnique = "";
if (club!=null) clubUnique = club.getUnique();

//This is for Test 
if(campaignId!=null && campaignId.trim().length()>0) {
   
   if (!campaignId.equals("")) cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
   if(cmpg!=null) { campaignsrc = cmpg.getSrc();  }
   
}





if(cmpg!=null && cmpg.getSrc().equalsIgnoreCase("test"))
  {
    landingPage=landingpage.initializeLandingPage(domain,campaignId,"test");
                request.setAttribute("landingPage",landingPage);
}
else{
 if(!campaignId.equals("")){
		landingPage=landingpage.initializeLandingPage(domain,campaignId,"all");
                request.setAttribute("landingPage",landingPage);
	}else {
		landingPage=landingpage.initializeLandingPage(domain);
                request.setAttribute("landingPage",landingPage);
	}
 
 //System.out.println("Exception landing page name is "+landingPage);
}

if("".equalsIgnoreCase(quizNo) && "".equalsIgnoreCase(optionAnswer)) {
    
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
}


String previousQuiz="";
String currentQuiz="";
boolean correct=false;
long currentTime=System.currentTimeMillis();
java.sql.Date today=new java.sql.Date(currentTime);
QuizQuestions quizQuestion=new QuizQuestions();
QuizQuestionOptions quizQuestionOption=new QuizQuestionOptions();
java.util.List options=new ArrayList();
try{
	
	//quizQuestion=umequizdao.getQuizQuestion(today);
          quizQuestion=umequizdao.getTeaserQuestion();
	options=umequizdao.getQuizQuestionOptions(quizQuestion);
	
	quizQuestionOption.setOptionNo(optionAnswer);
	correct=umequizdao.checkIfCorrect(quizQuestion, quizQuestionOption);
	System.out.println("Correct or Not: "+correct);
	
}catch(Exception e){
	System.out.println("Exception Occured Fetching Quiz For This Week");
	e.printStackTrace();
}

PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();


if(myisp!=null && myisp.trim().length()>0 && myisp.toLowerCase().trim().contains("vodafone")) {
    context.put("showmsisdnrouter","true");
    
    System.out.println("txtnation showmsisdnrouter is true ");
}



context.put("quizQuestion",quizQuestion);

context.put("quizQuestionOption",quizQuestionOption);
context.put("currentQuiz",currentQuiz);
context.put("options",options);
context.put("contenturl","http://"+dmn.getContentUrl());
context.put("endofmonth",getEndOfmonth());
context.put("campaignid",campaignId); //cid
if(cmpg!=null) context.put("campaignsrc",cmpg.getSrc().trim());
context.put("landingpage",landingPage);
context.put("myisp",myisp);
/* context.put("length_4","02:12");
String header_logo="584959013814121.jpg"; 
context.put("header_logo",header_logo);
context.put("defaulturl","http://"+dmn.getDefaultUrl());
context.put("pricing","<div id=\""+data_content_id+"\"><div class=\"priceInfo\"><font size=\"2\" color=\"white\">Click to buy and <b>&nbsp;<span class=\"content-price\">&pound;6</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>");
context.put("websitetitle","UME-UK");
//context.put("contenturl","http://"+dmn.getDefaultUrl());
context.put("contenturl","http://"+dmn.getContentUrl());
context.put("transref",Refrence_id);
context.put("defaulturl","http://"+dmn.getDefaultUrl()); */

if(step2request!=null && !step2request.isEmpty() && step2request.equalsIgnoreCase("true")) {
    
       System.out.println("txtnatio quiz no and option Answer "+quizNo+" optionanswer "+optionAnswer);
 String httpsrequest="https://msisdn.sla-alacrity.com/authenticate/msisdn";
 System.out.println("txtnation step 2 calling up "+httpsrequest);
 
 String username= "txtnationl_731_live";
 String pass="ndondawutu";

 

 HttpURLConnectionWrapper urlwrapper=new HttpURLConnectionWrapper(httpsrequest);
     Map<String, String> ireMap=new HashMap<String,String>();
     
        
        ireMap.put("uri", "partner:91909c32-e422-42e3-845a-d3cbf15af4fa");
        ireMap.put("transaction_id",request.getParameter("transactionid"));
        
        System.out.println("txtnation transctionid "+request.getParameter("transactionid"));
        
        Map<String,String> headers = new HashMap<String,String>();
        String basic_auth=DatatypeConverter.printBase64Binary((username+":"+pass).getBytes("UTF-8"));
        headers.put("Authorization", "Basic " + basic_auth);
        headers.put("Accept","application/json");
        
        urlwrapper.wrapGet(ireMap,headers);
    
        String responsecode=urlwrapper.getResponseCode();
System.out.println("txtnation:  response from msisdn router  "+responsecode);
        
        
        if(urlwrapper.isSuccessful()){
            String responsedesc=urlwrapper.getResponseContent();
           msisdn= parseForMsisdn(responsedesc);  
           if(null!=msisdn && !"".equals(msisdn)) {
               context.put("msisdnexist","true");
                System.out.println("txtnation msisdn exist is true "+msisdn);
               
           }
           
        }
    
 
    
    
    context.put("msisdn",msisdn);
     engine.getTemplate("msisdnparser").evaluate(writer, context); 
    
}




if(!quizNo.equals("") && !optionAnswer.equals("")){
    
    
    System.out.println("txtnatio quiz no and option Answer "+quizNo+" optionanswer "+optionAnswer);
 String httpsrequest="https://msisdn.sla-alacrity.com/authenticate/msisdn";
 System.out.println("txtnation step 2 calling up "+httpsrequest);
 
 String username= "txtnationl_731_live";
 String pass="ndondawutu";

 

 HttpURLConnectionWrapper urlwrapper=new HttpURLConnectionWrapper(httpsrequest);
     Map<String, String> ireMap=new HashMap<String,String>();
     
        
        ireMap.put("uri", "partner:91909c32-e422-42e3-845a-d3cbf15af4fa");
        ireMap.put("transaction_id",request.getParameter("transactionid"));
        
        System.out.println("txtnation transctionid "+request.getParameter("transactionid"));
        
        Map<String,String> headers = new HashMap<String,String>();
        String basic_auth=DatatypeConverter.printBase64Binary((username+":"+pass).getBytes("UTF-8"));
        headers.put("Authorization", "Basic " + basic_auth);
        headers.put("Accept","application/json");
        
        urlwrapper.wrapGet(ireMap,headers);
    
        String responsecode=urlwrapper.getResponseCode();
System.out.println("txtnation:  response from msisdn router  "+responsecode);
        
        
        if(urlwrapper.isSuccessful()){
            String responsedesc=urlwrapper.getResponseContent();
           msisdn= parseForMsisdn(responsedesc);  
           if(null!=msisdn && !"".equals(msisdn)) {
               context.put("msisdnexist","true");
                System.out.println("txtnation msisdn exist is true "+msisdn);
               
           }
           
        }
    
 
    
    
    context.put("msisdn",msisdn);
    
  if(correct || invalidno.equalsIgnoreCase("true")) {
      
	String popup="popup";
      if(campaignId!=null && !"".equalsIgnoreCase(campaignId)) popup="cidtpop-up";
          engine.getTemplate(popup).evaluate(writer, context);
        
  }
  else if(!quizNo.equals("") && !optionAnswer.equals("") &&!correct)
	  engine.getTemplate("wrongAnswer").evaluate(writer, context);  
  
    }
  else {
  
    
      //Required for msisdn pass-through
      String transactionid=MiscCr.MD5("moonlight"+"||"+Misc.generateUniqueId());
      System.out.println("txtnation  landingtransactionid "+transactionid);
      String msisdnrouter="<img src=\" http://msisdn.sla-alacrity.com/authenticate/image.gif?transaction_id="+transactionid+"&uri=partner:91909c32-e422-42e3-845a-d3cbf15af4fa\" alt=\"\">";
      System.out.println("txtnation step1 "+msisdnrouter+"   IP: "+myip+"  NETWORK: "+myisp);
      
      if(myisp.toLowerCase().contains("vodafone"))
          System.out.println("irevodafone step1 "+msisdnrouter+"   IP: "+myip+"  NETWORK: "+myisp); 
      
      context.put("transactionid",transactionid);
      context.put("msisdnrouter",msisdnrouter);
      engine.getTemplate(landingPage).evaluate(writer, context);  
          
          
  }
    
String output = writer.toString();


%>
<%!

private static String parseForMsisdn(String data){

		Map<String, Map<String,String>> mapData = new HashMap<String, Map<String,String>>();
		String msisdn = null;
                System.out.println("txtnation data " +data);
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			mapData = objectMapper.readValue(data, Map.class);
			
			Map<String,String> innerMap = (mapData.get("msisdn_protection"));
			if(innerMap!=null){
				String value = innerMap.get("msisdn");
                                System.out.println("txtnation value is "+value);
				if(value!=null){
					String[] tels = value.split(":");
					msisdn = tels[1];
                                        
                                        System.out.println("txtnation msisdn is "+msisdn);
                                        
				}
			}else{
				System.out.println("txtnation msisdn_protection is null!");	
			}
		} catch (Exception ex) {
			System.out.println("txtnation Couldn't parse!");
		} finally {
			// to avoid null pointer exceptions
			if(msisdn==null)msisdn="";
		}
                System.out.println("txtnation returned msisdn is "+msisdn);
		return msisdn;
	}
	



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
