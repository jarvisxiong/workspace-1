<%@page import="ume.pareva.ire.IREConnConstants"%>
<%@page import="ume.pareva.util.httpconnection.HttpURLConnectionWrapper"%>
<%@page import="ume.pareva.util.httpconnection.HttpURLConnectionWrapper"%>
<%@ include file="coreimport.jsp"%>
<%@ include file="ukheader.jsp"%>

<%

MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
UmeClubDetails userClubDetails=null;
if(club!=null)
	userClubDetails=UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());

long currentTime=System.currentTimeMillis();
java.sql.Date today=new java.sql.Date(currentTime);
QuizQuestions quizQuestion=new QuizQuestions();
quizQuestion=umequizdao.getQuizQuestion(today);

String msg=userClubDetails.getTeaser();
//String msg="Quiz2win - U have joined Quiz2win.com's competition to win an iPad Air or 4x�50 runner up cash prizes. For more information visit quiz2win.com";
//String msg=quizQuestion.getQuizMsg();
String shortCode=club.getSmsNumber();

String msisdn=httprequest.get("msisdn");
String campaignId=httprequest.get("cid");
String confirmlanding=httprequest.get("landingpage");
String myisp=httprequest.get("myisp");

String networkname=myisp.toLowerCase();
try{
networkname=mobilenetwork.getMobileNetwork("IE",networkname);
}catch(Exception e){networkname="unknown";}

if(campaignId.equalsIgnoreCase("8372952441KDS"))
{
     campaigndao.log("quizdaemon", "", msisdn,msisdn, handset,domain, campaignId, club.getUnique(), "IDENTIFIED", 0, request,response,"");
}



if(msisdn.startsWith("07") || msisdn.startsWith("08")){
	msisdn="353"+msisdn.substring(1);
}
String transactionId=String.valueOf(Misc.generateUniqueIntegerId());
String freeCostId="0";
String serviceId="6119598063441KDS";
//String deliveryReceipt="13";
String deliveryReceipt="11";
String typeId="2";
//msisdn=request.getParameter("msisdn");
boolean validMsisdn=validationutil.isIREValidPhone(msisdn);
System.out.println("quizdebug msisdn "+msisdn);
System.out.println("quizdebug valid or not "+validMsisdn);

String defClubDomain ="5510024809921CDS";

        PrintWriter writer = response.getWriter();
        Map<String, Object> context = new HashMap();
        context.put("contenturl","http://"+dmn.getContentUrl());

if(validMsisdn){
	campaigndao.log("ireconfirm2", confirmlanding, msisdn,msisdn, handset,domain, campaignId, club.getUnique(), "MANUAL", 0, request,response,networkname.toLowerCase());
	String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", defClubDomain);
	if (!userUnique.equals("")) user = umeuserdao.getUser(msisdn);
	if (user!=null) {
		SdcMobileClubUser clubUser = null;
        clubUser = user.getClubMap().get(club.getUnique());
        if(clubUser==null){
	        clubUser=umemobileclubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
        }
	    if ((clubUser!=null && clubUser.getActive()!=1) ||(clubUser==null)) {
	    	String[] toAddress={msisdn};
                context.put("subscribed","false");
                QuizValidation validation=new QuizValidation();
                validation.setaParsedMobile(msisdn);
                validation.setaCampaign(campaignId);
                validation.setaClubUnique(club.getUnique());
                int inserted=quizvalidationdao.insertValidationRecord(validation);
	    	if(sendSMS(toAddress,freeCostId,serviceId,deliveryReceipt,msg,shortCode,transactionId,typeId,club,umesmsdao,quizsmsdao)){
                    System.out.println("quizdebug sent quiz to user "+toAddress+" CLUBUSER is null or clubUser is not active");
				context.put("statusMsg","Thank You. You should recieve your message shortly!</br>Reply to the message for your chance to WIN");
                                context.put("showquestion","false");
				engine.getTemplate("confirm2").evaluate(writer, context);  //a
	    	}    	
	 
	    }else{
	    	context.put("statusMsg","Sorry, you have already Entered this Competition!");
                context.put("showquestion","false");
                context.put("subscribed","true");
	    	engine.getTemplate("confirm2").evaluate(writer, context);  
	    }
	}else{ 
            //campaigndao.log("ukconfirm", confirmlanding, msisdn,msisdn, handset,domain, campaignId, club.getUnique(), "MANUAL", 0, request,response,myisp);
		String[] toAddress={msisdn};
                context.put("subscribed","false");
		if(sendSMS(toAddress,freeCostId,serviceId,deliveryReceipt,msg,shortCode,transactionId,typeId,club,umesmsdao,quizsmsdao)){
                    System.out.println("quizdebug sent quiz to user "+toAddress+" USER is null");
			context.put("statusMsg","Thank You. You should receive your message shortly!</br>Reply to the message for your chance to WIN");
                        context.put("statusMsg1","It may take a few minutes for you to receive your message. If you have not received your message please click below to resend.");
			context.put("showMsg2","true");
                        context.put("statusMsg2","Resend message");
                        context.put("showquestion","false");
                        context.put("resendlink","/");
                        engine.getTemplate("confirm2").evaluate(writer, context);  
                           boolean exist=passivevisitordao.exists(msisdn, club.getUnique());
                        System.out.println("umepassive debug "+exist);
                           if(!exist){
                            PassiveVisitor visitor=new PassiveVisitor();
                                 visitor.setUnique(SdcMisc.generateUniqueId());
                                 visitor.setClubUnique(club.getUnique());
                                 visitor.setFollowUpFlag(0);
                                 visitor.setParsedMobile(msisdn);
                                 visitor.setStatus(0);
                                 visitor.setCreated(new Date());
                                 visitor.setCampaign(campaignId);
                                 visitor.setLandignPage(confirmlanding);
                                 System.out.println("txtnation debug "+visitor.toString());
                                 passivevisitordao.insertPassiveVisitor(visitor);
                            }
		}
                
                else{
                    context.put("statusMsg","An Error Occurred!!");
                    context.put("statusMsg2","Resend message");
                    context.put("showquestion","true");
                    context.put("subscribed","error");
                    context.put("resendlink","/");
                    engine.getTemplate("confirm2").evaluate(writer, context);  
                }
		
		
	}
        
        
     if(campaignId!=null && !campaignId.equalsIgnoreCase("")){
          MobileClubCampaign cmpg = null;
          String campaignsrc="";
          cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
         if(cmpg!=null) campaignsrc = cmpg.getSrc();
         if(cmpg!=null && cmpg.getSrc().endsWith("CPA")) {
          String cpaparameter1=(String) session.getAttribute("cpaparam1");
          String cpaparameter2=(String) session.getAttribute("cpaparam2");
          String cpaparameter3=(String) session.getAttribute("cpaparam3"); 
          
            String cpalogQuery="UPDATE cpavisitlog set aParsedMobile='"+msisdn+"',isSubscribed='1',aSubscribed='2011-01-01' WHERE aCampaignId='"+cmpg.getUnique()+"' AND (aHashcode='"+cpaparameter1+"' AND cpacampaignid='"+cpaparameter2+"'" + " AND clickid='"+cpaparameter3+"') ";
//                      if(msisdn!=null && msisdn.trim().length()>0)
//                          cpaloggingquery+=" AND aParsedMobile='"+msisdn+"'"; 
                         
                         int updatecpavisit=zacpalog.executeUpdateCPA(cpalogQuery);
          
         }
     }
}else{
    String quizNo = httprequest.get("quizNo");
String optionAnswer=httprequest.get("selectedOption");
String campaignid=httprequest.get("cid");
String currentQuiz="";
boolean correct=false;

quizQuestion=new QuizQuestions();
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
	System.out.println("quizdebug Exception Occured Fetching Quiz For This Week");
	e.printStackTrace();
        
}
context.put("quizQuestion",quizQuestion);

context.put("quizQuestionOption",quizQuestionOption);
context.put("currentQuiz",currentQuiz);
context.put("options",options);
context.put("contenturl","http://"+dmn.getContentUrl());
context.put("campaignid",campaignid); //cid
context.put("showquestion","true");
                        context.put("statusMsg","The mobile number you entered was invalid.");
                        context.put("tryagain","true");
                        context.put("statusMsg2","Click below to try again.");
                    
                        engine.getTemplate("confirm2").evaluate(writer, context);
	
}

%>

<%!
public boolean sendSMS(String[] msisdn, String costId,String serviceId, String deliveryReceipt, String msgText, String shortCode,String transactionId, String typeId,MobileClub club,UmeSmsDao umesmsdao,QuizSmsDao quizsmsdao){
	
    /**
     * 	$req = 'reply=0';
	$req .= '&id='.uniqid();
	$req .= '&number='.$number;
	$req .= '&network=INTERNATIONAL';
	$req .= '&message='.$msg;
	$req .= '&value=0';
	$req .= '&currency=GBP';
	$req .= '&cc='.$company;
	$req .= '&title='.$title;
	$req .= '&ekey='.$ekey;
http://client.txtnation.com/gateway.php?reply=0&id=123456&number=353851485271
* &network=international&value=0&currency=EUR&cc=moonlight&ekey=a6815e707c675f7a3f307656d462bca6&message=Your+message
     */
     boolean success=false;
   
    if(!quizsmsdao.sent3Times(msisdn[0])){
    String irehttp="http://client.txtnation.com/gateway.php";
    String id=Misc.generateUniqueId();
    String network="international";
    String ekey="a6815e707c675f7a3f307656d462bca6";
    String msg=msgText;
    try{
    msg=java.net.URLEncoder.encode(msgText,"utf-8");
    }catch(Exception e){}
    
    irehttp+="?reply=0&id="+id+"&number="+msisdn+"&network="+network+"&value=0&currency=EUR&cc=moonlight&ekey="+ekey+"&message="+msg;
    
    HttpURLConnectionWrapper urlwrapper=urlwrapper=new HttpURLConnectionWrapper(IREConnConstants.getDomainHttp());
     Map<String, String> ireMap=new HashMap<String,String>();
        
        ireMap.put("reply", "0");
        ireMap.put("id",id);
        ireMap.put("number",msisdn[0]);
        ireMap.put("network",network);
        ireMap.put("value","0");
        ireMap.put("currency","EUR");
        ireMap.put("cc","moonlight");
        ireMap.put("ekey",IREConnConstants.getEKey());
        ireMap.put("message",msg);
        ireMap.put("title",shortCode);
        ireMap.put("smscat","991");
        
        urlwrapper.wrapGet(ireMap);
    
        String responsecode=urlwrapper.getResponseCode();
        String responsedesc=urlwrapper.getResponseContent();
        boolean isSuccessful=urlwrapper.isSuccessful();
        
      System.out.println("txtnation: http request sent "+irehttp);
    
    System.out.println("txtnation:  confirm.jsp response "+responsecode+"  desc "+responsedesc+" successful: "+isSuccessful);
//    
   
//	SMSParameter smsParameter=new SMSParameter();
//	smsParameter.setCostId(costId);
//	smsParameter.setServiceId(serviceId);
//	smsParameter.setDeliveryReceipt(deliveryReceipt);
//	smsParameter.setMsgText(msgText);
//	smsParameter.setShortCode(shortCode);
//	smsParameter.setTransactionId(String.valueOf(transactionId));
//	smsParameter.setTypeId(typeId);
//	smsParameter.setToAddress(msisdn);
//	smsParameter.setWinTransactionId(String.valueOf(Misc.generateUniqueIntegerId()));
//	
//	EngageInterface engageInterface=new EngageInterfaceImpl();
//        int responseCode=0;
//        try{
//	responseCode=engageInterface.sendSMS(smsParameter);
//        }catch(Exception e){responseCode=0;}
	
	if(isSuccessful){
		for(int i=0;i<msisdn.length;i++){
                    
                    
//                	QuizUserAttempted quizUserAttempted=new QuizUserAttempted();
//                    		quizUserAttempted.setaParsedMsisdn(msisdn[i]);
//                    		quizUserAttempted.setClubUnique(club.getUnique());
//                    		quizUserAttempted.setType("Teaser");
//                    		quizUserAttempted.setStatus("false");
//                    		quizUserAttempted.setaUnique(String.valueOf(transactionId));
//                    		quizUserAttempted.setaCreated(new java.sql.Timestamp(System.currentTimeMillis()));
//                    		umequizdao.saveQuizUserAttempted(quizUserAttempted);    
                
		SdcSmsSubmit sdcSmsSubmit=new SdcSmsSubmit();
		
		sdcSmsSubmit.setUnique(String.valueOf(transactionId));
		sdcSmsSubmit.setFromNumber(shortCode);
		sdcSmsSubmit.setToNumber(msisdn[i]);
		sdcSmsSubmit.setMsgType("Free");
		sdcSmsSubmit.setMsgBody(msgText);
                sdcSmsSubmit.setLogUnique(id);
                sdcSmsSubmit.setReqType("teaser");
        sdcSmsSubmit.setCost(club.getPrice());
        sdcSmsSubmit.setMsgCode1("confirm.jsp");
		umesmsdao.log(sdcSmsSubmit);    
                quizsmsdao.log(sdcSmsSubmit);
		
		}
		success=true;
	}
        else {
            System.out.println("irelandtesting  "+responsecode+":"+responsedesc);
        }
}
		return success;
}

%>



