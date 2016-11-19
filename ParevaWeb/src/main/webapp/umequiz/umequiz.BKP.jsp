f<%@page import="org.joda.time.format.DateTimeFormatter"%>
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


String encryptmsisdn=httprequest.get("mid");


System.out.println("quiztestinvalid "+invalidno);
String campaignsrc="";
String landingPage="landing_default";
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
 
 
 if(!"".equalsIgnoreCase(encryptmsisdn)) {
     msisdn=MiscCr.decrypt(encryptmsisdn);
    
}
 
 
 
 
 
 
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
      System.out.println("quiz2winlanding page "+cmpg.getSrc());
    landingPage=landingpage.initializeLandingPage(domain,campaignId,"test");
                request.setAttribute("landingPage",landingPage);
      System.out.println("quiz2winlanding page "+cmpg.getSrc() +" landing page "+landingPage);       
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
context.put("quizQuestion",quizQuestion);

context.put("quizQuestionOption",quizQuestionOption);
context.put("currentQuiz",currentQuiz);
context.put("options",options);
context.put("contenturl","http://"+dmn.getContentUrl());
context.put("campaignid",campaignId); //cid
context.put("endofmonth",getEndOfmonth());
if(cmpg!=null) context.put("campaignsrc",cmpg.getSrc().trim());
context.put("landingpage",landingPage);
context.put("myisp",myisp);
context.put("msisdn",msisdn);
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

  if((!quizNo.equals("") && !optionAnswer.equals("") && correct) || invalidno.equalsIgnoreCase("true")) {
      
      String popup="popup";
      if(campaignId!=null && !"".equalsIgnoreCase(campaignId)) popup="cidtpop-up";
          
          
	engine.getTemplate(popup).evaluate(writer, context);
        
        
  }
  else if(!quizNo.equals("") && !optionAnswer.equals("") &&!correct)
	  engine.getTemplate("wrongAnswer").evaluate(writer, context);  
  else
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
