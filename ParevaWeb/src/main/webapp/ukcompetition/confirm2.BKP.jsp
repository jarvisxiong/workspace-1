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
//String msg="Quiz2win - U have joined Quiz2win.com's competition to win an iPad Air or 4x£50 runner up cash prizes. For more information visit quiz2win.com";
//String msg=quizQuestion.getQuizMsg();
String shortCode=club.getSmsNumber();

String msisdn=httprequest.get("msisdn");
String campaignId=httprequest.get("cid");
String confirmlanding=httprequest.get("landingpage");
String myisp=httprequest.get("myisp");

String networkname=myisp.toLowerCase();
try{
networkname=mobilenetwork.getMobileNetwork("UK",networkname);
}catch(Exception e){networkname="unknown";}

if(campaignId.equalsIgnoreCase("8372952441KDS"))
{
     campaigndao.log("quizdaemon", "", msisdn,msisdn, handset,domain, campaignId, club.getUnique(), "IDENTIFIED", 0, request,response,"");
}



if(msisdn.startsWith("07")){
	msisdn="44"+msisdn.substring(1);
}
String transactionId=String.valueOf(Misc.generateUniqueIntegerId());
String freeCostId="1";
String serviceId="1";
//String deliveryReceipt="13";
String deliveryReceipt="11";
String typeId="2";
//msisdn=request.getParameter("msisdn");
boolean validMsisdn=validationutil.isValidPhone(msisdn);
System.out.println("quizdebug msisdn "+msisdn);
System.out.println("quizdebug valid or not "+validMsisdn);

String defClubDomain ="5510024809921CDS";

        PrintWriter writer = response.getWriter();
        Map<String, Object> context = new HashMap();
        context.put("contenturl","http://"+dmn.getContentUrl());

if(validMsisdn){
    System.out.println("quiz2winlanding confirm-page 2 "+validMsisdn);
	campaigndao.log("ukconfirm", confirmlanding, msisdn,msisdn, handset,domain, campaignId, club.getUnique(), "MANUAL", 0, request,response,networkname.toLowerCase());
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
	    	compmessage.requestSendSmsTimer(toAddress,freeCostId,serviceId,deliveryReceipt,msg,shortCode,transactionId,typeId,club,10*10,true);
                    System.out.println("quizdebug sent quiz to user "+toAddress+" CLUBUSER is null");
		    context.put("statusMsg","Thanks! You should receive a text shortly! Simply reply MINE to confirm you are 16+ and complete!");
                    context.put("showquestion","false");                                
		    engine.getTemplate("confirm2").evaluate(writer, context);  //a
	    	    	
	 
	    }else{
                System.out.println("quiz2winlanding page "+clubUser.getParsedMobile()+"  "+clubUser.getActive()+" inside else condition ");
	    	context.put("statusMsg","Sorry, you have already Entered this Competition!");
                context.put("showquestion","true");
                context.put("subscribed","true");
	    	engine.getTemplate("confirm2").evaluate(writer, context);  
	    }
	}else{  // New User 
            //campaigndao.log("ukconfirm", confirmlanding, msisdn,msisdn, handset,domain, campaignId, club.getUnique(), "MANUAL", 0, request,response,myisp);
		String[] toAddress={msisdn};
                context.put("subscribed","false");
                System.out.println("quiz2winlanding confirm-page 2 "+validMsisdn+"  user is null and new "+msisdn);
                if(msisdn.equalsIgnoreCase("447799700438"))
                {
                    
                    context.put("statusMsg","An Error Occurred!!");
                    context.put("statusMsg2","Resend message");
                    context.put("showquestion","true");
                    context.put("subscribed","error");
                    context.put("resendlink","/");
                    engine.getTemplate("confirm2").evaluate(writer, context); 
                }
                
                else if(!msisdn.equalsIgnoreCase("447799700438")){
                    System.out.println("quiz2winlanding confirm-page 2 "+validMsisdn+"  user is null and new else if "+msisdn);    
                     compmessage.requestSendSmsTimer(toAddress,freeCostId,serviceId,deliveryReceipt,msg,shortCode,transactionId,typeId,club,10*10,true);
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
                                 System.out.println("umepassive debug "+visitor.toString());
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

System.out.println("umequizdebug  context variable subscribed "+context.get("subscribed"));
                        context.put("statusMsg","The mobile number you entered was invalid.");
                        context.put("tryagain","true");
                        context.put("statusMsg2","Click below to try again.");
                    
                        engine.getTemplate("confirm2").evaluate(writer, context);
	
}

%>

<%!
public boolean sendSMS(String[] msisdn, String costId,String serviceId, String deliveryReceipt, String msgText, String shortCode,String transactionId, String typeId,MobileClub club,UmeSmsDao umesmsdao,QuizSmsDao quizsmsdao){
	boolean success=false;
        
    if(!quizsmsdao.sent3Times(msisdn[0])){
	SMSParameter smsParameter=new SMSParameter();
	smsParameter.setCostId(costId);
	smsParameter.setServiceId(serviceId);
	smsParameter.setDeliveryReceipt(deliveryReceipt);
	smsParameter.setMsgText(msgText);
	smsParameter.setShortCode(shortCode);
	smsParameter.setTransactionId(String.valueOf(transactionId));
	smsParameter.setTypeId(typeId);
	smsParameter.setToAddress(msisdn);
	smsParameter.setWinTransactionId(String.valueOf(Misc.generateUniqueIntegerId()));
	
	EngageInterface engageInterface=new EngageInterfaceImpl();
        int responseCode=0;
        try{
	responseCode=engageInterface.sendSMS(smsParameter);
        }catch(Exception e){responseCode=0;}
	
	if(responseCode==200){
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
                sdcSmsSubmit.setReqType("teaser");
        sdcSmsSubmit.setCost(club.getPrice());
        sdcSmsSubmit.setMsgCode1("confirm.jsp");
		umesmsdao.log(sdcSmsSubmit);
                 quizsmsdao.log(sdcSmsSubmit);
		
		}
		success=true;
	}
        else {
            System.out.println("quiz2winerrorcode "+responseCode);
        }
        
}
		return success;
}

%>



