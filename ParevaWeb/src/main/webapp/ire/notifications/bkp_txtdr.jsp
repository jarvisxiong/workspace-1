<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@page import="org.joda.time.Days"%>
<%@page import="org.joda.time.DateTime"%>
<%@page import="ume.pareva.ire.IREConnConstants"%>
<%@page import="ume.pareva.util.httpconnection.HttpURLConnectionWrapper"%>
<%@ include file="coreimport.jsp"%>
<%@ include file="ukheader.jsp"%>
<%
/**
 * Variable	Content
action	mp_report
id	the unique message id number
number	the originating telephone number
report	the delivery report (e.g. delivered/acked/failed)

 */
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
Calendar deliverytime = new GregorianCalendar();
 String serviceId="6119598063441KDS";
 String defClubDomain ="5510024809921CDS";
 String freeCostId="0";
 String deliveryReceipt="11";
 String transactionId=Misc.generateUniqueIntegerId()+"";
 String typeId="2";
 

MobileClub club = UmeTempCmsCache.mobileClubMap.get(serviceId);
UmeClubDetails clubdetail = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());

  Enumeration parameterList = request.getParameterNames();
  while( parameterList.hasMoreElements() )
  {
    String sName = parameterList.nextElement().toString();
   System.out.println("txtnation: callback DR "+ sName+":"+request.getParameter(sName));
      
    }
  
  
  String id=request.getParameter("id");
  String report=request.getParameter("report"); // This can be DELIVERED, ACKNOWLEDGED, FAILED
  String messageid=request.getParameter("message_id");
  String msisdn=request.getParameter("number");
  
  quizsmsdao.updateResponse(id,report,sdf.format(deliverytime.getTime()),report,msisdn);
  umesmsdao.updateResponse(id,report,sdf.format(deliverytime.getTime()),report,msisdn);
  
  
  if(report.equals("DELIVERED")){
      
      MobileClubBillingPlan mobileClubBillingPlan=null; 
      String successResponse="00";
      
      SdcSmsSubmit smsrecord=quizsmsdao.getSmsMsgLog(id);
      if(smsrecord!=null){
          System.out.println("txtnation DR.jsp  smsrecord is not null"+ smsrecord.getLogUnique()+" "+smsrecord.getMsgType()+" "+smsrecord.getMsgCode1());
      if(smsrecord.getLogUnique().equals(id) && smsrecord.getMsgType().equals("Premium") && smsrecord.getMsgCode1().equals("txtmo.jsp")
              && smsrecord.getReqType().equals("firstbillable")){
          
          System.out.println("txtnation DR.jsp getting userunique from msisdn "+smsrecord.getMsgCode1());
          UmeUser user=null;
          SdcMobileClubUser clubUser = null;
          String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", defClubDomain);
	        if (!userUnique.equals("")) user = umeuserdao.getUser(msisdn);
                System.out.println("txtnation DR.jsp getting userunique from msisdn "+user.toString());
                if(user!=null) {
                  clubUser = user.getClubMap().get(club.getUnique());
                    
                    if(clubUser==null){
	        clubUser=umemobileclubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
                mobileClubBillingPlan=billingplandao.getBillingPlanByMsisdnAndClubUnique(msisdn,club.getUnique());
                    }
                
                    System.out.println("txtnation DR.jsp getting ClubUser from msisdn "+clubUser.toString());
          
                    //BillingTry Logging for first success
                    if(DateUtils.isSameDay(new Date(), clubUser.getSubscribed()))
                       successResponse="003";
                    
                    MobileClubBillingTry mobileClubBillingTry=new MobileClubBillingTry();
				mobileClubBillingTry.setLogUnique(Misc.generateUniqueId());
				mobileClubBillingTry.setAggregator("TXT");
				mobileClubBillingTry.setClubUnique(club.getUnique());
				mobileClubBillingTry.setCreated(new Date());
				mobileClubBillingTry.setNetworkCode(clubUser.getNetworkCode());
				mobileClubBillingTry.setParsedMsisdn(msisdn);
				mobileClubBillingTry.setRegionCode("IE");
				mobileClubBillingTry.setResponseCode(successResponse);
				mobileClubBillingTry.setResponseDesc("successful");
				mobileClubBillingTry.setResponseRef(transactionId);
				mobileClubBillingTry.setStatus("success");
                                mobileClubBillingTry.setTariffClass(2.5);
				mobileClubBillingTry.setTransactionId(transactionId);
                                mobileClubBillingTry.setCampaign(clubUser.getCampaign());
				mobileclubbillingdao.addTryItem(mobileClubBillingTry);
                    
                    //End BillingTry Logging
                    
                if(mobileClubBillingPlan!=null){
		   Calendar c = Calendar.getInstance();
                    mobileClubBillingPlan.setLastSuccess(c.getTime());
                    mobileClubBillingPlan.setLastPaid(c.getTime());
                    mobileClubBillingPlan.setPushCount(mobileClubBillingPlan.getPushCount()+1);
                    
                    umemobileclubuserdao.updateBillingRenew(clubUser.getUserUnique(),club.getUnique());
                    billingplandao.updateRegionSuccessBillingPlan(mobileClubBillingPlan,"IE");
		    MobileClubBillingSuccesses mobileClubBillingSuccesses=new MobileClubBillingSuccesses(mobileClubBillingPlan,mobileClubBillingTry);
		    billingplandao.save(mobileClubBillingSuccesses);
                    
                                        
                }
                    
                    
          if(sendPremiumSMS(msisdn,freeCostId,clubUser.getNetworkCode(),serviceId,deliveryReceipt,club.getSmsConfirmation(),club.getSmsNumber(),transactionId,typeId,club,umesmsdao,quizsmsdao,umequizdao,"secondbillable")){
              
                         //campaigndao.log("irestopmo", "", msisdn,msisdn, null,null, clubUser.getCampaign(), clubUser.getClubUnique(), "STOP", 0, request,response,network);
            
                   
               }
                    
          
      }
          }
      
      //3RD Billable Messages
      
            if(smsrecord.getLogUnique().equals(id) && smsrecord.getMsgType().equals("Premium") && smsrecord.getMsgCode1().equals("txtdr.jsp")
              && smsrecord.getReqType().equals("secondbillable")){
          
          System.out.println("txtnation DR.jsp getting userunique from msisdn "+smsrecord.getMsgCode1());
          UmeUser user=null;
          SdcMobileClubUser clubUser = null;
          String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", defClubDomain);
	        if (!userUnique.equals("")) user = umeuserdao.getUser(msisdn);
                System.out.println("txtnation DR.jsp getting userunique from msisdn "+user.toString());
                if(user!=null) {
                  clubUser = user.getClubMap().get(club.getUnique());
                    
                    if(clubUser==null){
	        clubUser=umemobileclubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
                    }
                
                    System.out.println("txtnation DR.jsp getting ClubUser from msisdn "+clubUser.toString());
                    
                         //BillingTry Logging for first success
                    if(DateUtils.isSameDay(new Date(), clubUser.getSubscribed()))
                       successResponse="003";
                    
                    MobileClubBillingTry mobileClubBillingTry=new MobileClubBillingTry();
				mobileClubBillingTry.setLogUnique(Misc.generateUniqueId());
				mobileClubBillingTry.setAggregator("TXT");
				mobileClubBillingTry.setClubUnique(club.getUnique());
				mobileClubBillingTry.setCreated(new Date());
				mobileClubBillingTry.setNetworkCode(clubUser.getNetworkCode());
				mobileClubBillingTry.setParsedMsisdn(msisdn);
				mobileClubBillingTry.setRegionCode("IE");
				mobileClubBillingTry.setResponseCode(successResponse);
				mobileClubBillingTry.setResponseDesc("successful");
				mobileClubBillingTry.setResponseRef(transactionId);
				mobileClubBillingTry.setStatus("success");
                                mobileClubBillingTry.setTariffClass(2.5);
				mobileClubBillingTry.setTransactionId(transactionId);
                                mobileClubBillingTry.setCampaign(clubUser.getCampaign());
				mobileclubbillingdao.addTryItem(mobileClubBillingTry);
                    
                    //End BillingTry Logging
                    
                if(mobileClubBillingPlan!=null){
		   Calendar c = Calendar.getInstance();
                    mobileClubBillingPlan.setLastSuccess(c.getTime());
                    mobileClubBillingPlan.setLastPaid(c.getTime());
                    mobileClubBillingPlan.setPushCount(mobileClubBillingPlan.getPushCount()+1);
                    
                    umemobileclubuserdao.updateBillingRenew(clubUser.getUserUnique(),club.getUnique());
                    billingplandao.updateRegionSuccessBillingPlan(mobileClubBillingPlan,"IE");
		    MobileClubBillingSuccesses mobileClubBillingSuccesses=new MobileClubBillingSuccesses(mobileClubBillingPlan,mobileClubBillingTry);
		    billingplandao.save(mobileClubBillingSuccesses);
                    
                                        
                }
             
          if(clubdetail!=null && clubdetail.getBillingmessage3()!=null && !"".equals(clubdetail.getBillingmessage3())){
          if(sendPremiumSMS(msisdn,freeCostId,clubUser.getNetworkCode(),serviceId,deliveryReceipt,clubdetail.getBillingmessage3(),club.getSmsNumber(),transactionId,typeId,club,umesmsdao,quizsmsdao,umequizdao,"thirdbillable")){
        
                         //campaigndao.log("irestopmo", "", msisdn,msisdn, null,null, clubUser.getCampaign(), clubUser.getClubUnique(), "STOP", 0, request,response,network);
            
                   
               } 
          }
          
      }
          } //3RD Billable Message
            
        //If 3rd Billable Message is delivered then he has paid all 7.50 so his Entry Confirmation becomes true
            if(smsrecord.getLogUnique().equals(id) && smsrecord.getMsgType().equals("Premium") && smsrecord.getMsgCode1().equals("txtdr.jsp")
              && smsrecord.getReqType().equals("thirdbillable")){
                
                  System.out.println("txtnation DR.jsp getting userunique from msisdn "+smsrecord.getMsgCode1());
          UmeUser user=null;
          SdcMobileClubUser clubUser = null;
          String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", defClubDomain);
	        if (!userUnique.equals("")) user = umeuserdao.getUser(msisdn);
                System.out.println("txtnation DR.jsp getting userunique from msisdn "+user.toString());
                if(user!=null) {
                  clubUser = user.getClubMap().get(club.getUnique());
                    
                    if(clubUser==null){
	        clubUser=umemobileclubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
                    }
             
                    //BillingTry Logging for first success
                    if(DateUtils.isSameDay(new Date(), clubUser.getSubscribed()))
                       successResponse="003";
                    
                    MobileClubBillingTry mobileClubBillingTry=new MobileClubBillingTry();
				mobileClubBillingTry.setLogUnique(Misc.generateUniqueId());
				mobileClubBillingTry.setAggregator("TXT");
				mobileClubBillingTry.setClubUnique(club.getUnique());
				mobileClubBillingTry.setCreated(new Date());
				mobileClubBillingTry.setNetworkCode(clubUser.getNetworkCode());
				mobileClubBillingTry.setParsedMsisdn(msisdn);
				mobileClubBillingTry.setRegionCode("IE");
				mobileClubBillingTry.setResponseCode(successResponse);
				mobileClubBillingTry.setResponseDesc("successful");
				mobileClubBillingTry.setResponseRef(transactionId);
				mobileClubBillingTry.setStatus("success");
                                mobileClubBillingTry.setTariffClass(2.5);
				mobileClubBillingTry.setTransactionId(transactionId);
                                mobileClubBillingTry.setCampaign(clubUser.getCampaign());
				mobileclubbillingdao.addTryItem(mobileClubBillingTry);
                    
                    //End BillingTry Logging
                    
                if(mobileClubBillingPlan!=null){
		   Calendar c = Calendar.getInstance();
                    mobileClubBillingPlan.setLastSuccess(c.getTime());
                    mobileClubBillingPlan.setLastPaid(c.getTime());
                    mobileClubBillingPlan.setPushCount(mobileClubBillingPlan.getPushCount()+1);
                    
                    umemobileclubuserdao.updateBillingRenew(clubUser.getUserUnique(),club.getUnique());
                    billingplandao.updateRegionSuccessBillingPlan(mobileClubBillingPlan,"IE");
		    MobileClubBillingSuccesses mobileClubBillingSuccesses=new MobileClubBillingSuccesses(mobileClubBillingPlan,mobileClubBillingTry);
		    billingplandao.save(mobileClubBillingSuccesses);
                    
                                        
                }
                
                
                QuizUserAttempted quizUserAttempted=new QuizUserAttempted();
                    		quizUserAttempted.setaParsedMsisdn(msisdn);
                    		quizUserAttempted.setClubUnique(club.getUnique());
                    		quizUserAttempted.setType("Entry Confirmation");
                    		quizUserAttempted.setStatus("true");
                    		quizUserAttempted.setaUnique(String.valueOf(transactionId));
                    		quizUserAttempted.setaCreated(new java.sql.Timestamp(System.currentTimeMillis()));
                    		umequizdao.saveQuizUserAttempted(quizUserAttempted); 
            
            }
            
       // End 3rd Billable Message Delivered condition
            
      
      
  }
  }
  
  }
  
  
  response.setStatus(200);

%>
<%!
public boolean sendPremiumSMS(String msisdn, String costId,String network,String serviceId, String deliveryReceipt, String msgText, String shortCode,String transactionId, String typeId,MobileClub club,UmeSmsDao umesmsdao,QuizSmsDao quizsmsdao,UmeQuizDao umequizdao,String whichbillable){
	
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
    
    String irehttp="http://client.txtnation.com/gateway.php";
    String id=Misc.generateUniqueId();
    //network="international";
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
        ireMap.put("number",msisdn);
        ireMap.put("network",network);
        //ireMap.put("value","0");
        ireMap.put("value","2.50");
        ireMap.put("currency","EUR");
        ireMap.put("cc","moonlight");
        ireMap.put("ekey",IREConnConstants.getEKey());
        ireMap.put("message",msg);
        ireMap.put("title",shortCode);
        
        urlwrapper.wrapGet(ireMap);
    
        String responsecode=urlwrapper.getResponseCode();
        String responsedesc=urlwrapper.getResponseContent();
        boolean isSuccessful=urlwrapper.isSuccessful();
        
      System.out.println("txtnation: txtdr http request sent "+irehttp);
    
    System.out.println("txtnation:  txtdr.jsp response "+responsecode+"  desc "+responsedesc+" successful: "+isSuccessful);
//    
    boolean success=false;
    String userattemptstatus="false";
	if(isSuccessful){
		
                    if(whichbillable.equals("thirdbillable")) userattemptstatus="false";
                    
                                QuizUserAttempted quizUserAttempted=new QuizUserAttempted();
                    		quizUserAttempted.setaParsedMsisdn(msisdn);
                    		quizUserAttempted.setClubUnique(club.getUnique());
                    		quizUserAttempted.setType("Entry Confirmation");
                    		quizUserAttempted.setStatus(userattemptstatus);
                    		quizUserAttempted.setaUnique(String.valueOf(transactionId));
                    		quizUserAttempted.setaCreated(new java.sql.Timestamp(System.currentTimeMillis()));
                    		umequizdao.saveQuizUserAttempted(quizUserAttempted);    
                
		SdcSmsSubmit sdcSmsSubmit=new SdcSmsSubmit();
		
		sdcSmsSubmit.setUnique(String.valueOf(transactionId));
		sdcSmsSubmit.setFromNumber(shortCode);
		sdcSmsSubmit.setToNumber(msisdn);
		sdcSmsSubmit.setMsgType("Premium");
		sdcSmsSubmit.setMsgBody(msgText);
                sdcSmsSubmit.setCost(club.getPrice());
                sdcSmsSubmit.setMsgCode1("txtdr.jsp");
                sdcSmsSubmit.setReqType(whichbillable);
                sdcSmsSubmit.setLogUnique(id);
		umesmsdao.log(sdcSmsSubmit);    
                quizsmsdao.log(sdcSmsSubmit);
		
		
		success=true;
	}
        else {
            System.out.println("irelandtesting  "+responsecode+":"+responsedesc);
        }
		return success;
}

%>




