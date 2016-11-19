<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@page import="org.joda.time.Days"%>
<%@page import="org.joda.time.DateTime"%>
<%@page import="ume.pareva.ire.IREConnConstants"%>
<%@page import="ume.pareva.util.httpconnection.HttpURLConnectionWrapper"%>
<%@ include file="coreimport.jsp"%>
<%@ include file="ukheader.jsp"%>
<%
/**
 * 
 * 
Variable	Content
id	the unique message id number
number	the originating telephone number
network	the originating network
message	the contents of the message
shortcode	the orginating shortcode
billing	The network billing type where available
country	The 2 digit ISO country code (e.g. UK, US, ES).
 */

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
 Enumeration parameterList = request.getParameterNames();
  while( parameterList.hasMoreElements() )
  {
    String sName = parameterList.nextElement().toString();
   System.out.println("txtnation: callback MO "+ sName+":"+request.getParameter(sName));
      
    }
  String id=request.getParameter("id");
  String msisdn=request.getParameter("number");
  String network=request.getParameter("network");
  String message=request.getParameter("message");
  String shortcode=request.getParameter("shortcode");
  String billing=request.getParameter("billing");
  String country=request.getParameter("country");
  String transactionId=Misc.generateUniqueIntegerId()+"";
  
    boolean sendConfirmation=true;
    boolean createbillPlan=true;
    MobileClubCampaign cmpg = null;

String billednetwork=network;
   
network=mobilenetwork.getMobileNetwork("IE",network.toLowerCase());
UmeUser user=null;
  
  boolean exist=umequizdao.quizReplyExistOrNot(id);
  System.out.println("txtnation exist value "+exist);
  if(!exist){ 
      String serviceId="6119598063441KDS";
      String freeCostId="0";
      umequizdao.saveQuizReply(id,msisdn, message,shortcode,serviceId,network);
  

//String deliveryReceipt="13";
String deliveryReceipt="11";
String typeId="2";

      
      MobileClub club = null;
java.util.List<MobileClub> clubs = UmeTempCmsCache.mobileClubsByNumber.get(shortcode);

if(clubs!=null)
{
    club = clubs.get(0);
}

String msg=club.getWebConfirmation();



Calendar manualSubscribedDate = new GregorianCalendar();
String ume_manualSubscribed=sdf.format(manualSubscribedDate.getTime());
UmeClubDetails userclubdetails=null;
if(club!=null) userclubdetails=UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());   

String campaignUnique = "";
boolean exists=false;
boolean entryConfirmation=false;
String status = "";
String subscdate="";
String hash = "";

String defClubDomain ="5510024809921CDS";

Calendar c1 = new GregorianCalendar();
Date bstart = c1.getTime();
c1.setTime(bstart);
c1.add(Calendar.DATE, 7);
Date bend = c1.getTime();
      
SdcMobileClubUser clubUser = null;
MobileClubBillingPlan billingplan=null;

       if (!msisdn.equals("")) {
	        String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", defClubDomain);
	        if (!userUnique.equals("")) user = umeuserdao.getUser(msisdn);
                
                if(user!=null) clubUser=umemobileclubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
                     //Subscription CPA 
                        CpaVisitLog visitorLog=cpavisitlogdao.getDetails(msisdn);
                        if(visitorLog!=null)
                        {
                            String campaignId=visitorLog.getaCampaignId();
                            if(null!=campaignId && !"".equals(campaignId))
                            {
                                campaignUnique=campaignId;
                            }
                            
                        }
	    }
       
       

if(message!=null && (message.toLowerCase().contains("stop") || message.toLowerCase().contains("quit"))) {
    
         if(user!=null) {
                  clubUser = user.getClubMap().get(club.getUnique());
                    
                    if(clubUser==null){
	        clubUser=umemobileclubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
                    }
               if(clubUser!=null){
                   clubUser.setActive(0);
                   clubUser.setUnsubscribed(SdcMiscDate.parseSqlDateTime(ume_manualSubscribed));
                   clubUser.setParam1("0");
                   clubUser.setParam2("0");
                   clubUser.setLandingpage("");
                   umemobileclubuserdao.saveItem(clubUser);
                   
                   billingplan=billingplandao.getBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());
                         if(billingplan!=null && billingplan.getActiveForBilling()==1){
                             createbillPlan=false;
                             billingplandao.disableBillingPlan(msisdn,club.getUnique());
                             billingplan=null;
                         }
                         
                    //STOP Message Sending
                     String[] toAddress={msisdn};
                        if(sendConfirmation){
                            String[] toAddresse={msisdn};
                            
                            msg=club.getStopSms();
                        
		if(sendStopSMS(toAddresse,freeCostId,network,serviceId,deliveryReceipt,msg,shortcode,transactionId,typeId,club,umesmsdao,quizsmsdao)){
                    String stop="STOP";
                    if(DateUtils.isSameDay(clubUser.getSubscribed(), clubUser.getUnsubscribed())) stop="STOPFD";
                         campaigndao.log("irestopmo", "", msisdn,msisdn, null,null, clubUser.getCampaign(), clubUser.getClubUnique(), stop, 0, request,response,network);
            
                   
               }
           
                   
            } //End sendConfirmation clause
               }// End If clubUser is not null 
            } //If user is not null while sending stop message
    
}//End stop message

else {
    
    //============== Passive Visitor =======================
    
    boolean passiveexist=false;
             try{
             passiveexist=passivevisitordao.alreadyExist(clubUser.getParsedMobile(), club.getUnique());
             }catch(Exception e){passiveexist=false;}
             
                 if(passiveexist)
                          {
                              //System.out.println("==ConfirmZA VISITOR EXIST "+exist);
                              boolean status0=false;
                              try{
                              status0=passivevisitordao.alreadyExistWithStatus0(clubUser.getParsedMobile(),club.getUnique());
                              }catch(Exception e){status0=false;}
                               //System.out.println("==ConfirmZA VISITOR EXIST with status 0 "+status0);
                              if(status0)
                              {
                                  PassiveVisitor visitor=new PassiveVisitor();
                                  visitor.setParsedMobile(clubUser.getParsedMobile());
                                  visitor.setClubUnique(club.getUnique());
                                  //System.out.println("==ConfirmZA VISITOR Update with status 1 ");
                                  passivevisitordao.savePassiveVisitor(visitor, 1);
                              }
                          }
    
    
    //============== Passive Visitor End ===================
    
    
    
    
    
    
    
    
    
    
    
      if (user!=null) {

	        user.updateMap("active", "1");
	        umeuserdao.commitUpdateMap(user);
	        user.clearUpdateMap();

	        clubUser = user.getClubMap().get(club.getUnique());
                    
                    if(clubUser==null){
	        clubUser=umemobileclubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
                    }
	        
	        if (clubUser!=null) {
	             clubUser.setParsedMobile(user.getParsedMobile());
	        
	        /*****************************************************************************************************************************/   
	             
	        	DateTime nowTime=new DateTime();
				 DateTime billingEnd=new DateTime(clubUser.getBillingEnd().getTime());
				 int days = Days.daysBetween(billingEnd,nowTime).getDays();
				 if(days>7){
				 
				 }
				 
			 /*****************************************************************************************************************************/        
	                 if(clubUser.getActive()==1)
                     {
                         entryConfirmation=false;
                         sendConfirmation=false;
                         billingplan=billingplandao.getBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());
                         if(billingplan!=null && billingplan.getActiveForBilling()==1) createbillPlan=false;
                     }  
                                 
                                 
                    if(clubUser.getActive()!=1)
                       {
                    clubUser.setActive(1);  
	            clubUser.setCredits(club.getCreditAmount());
                    clubUser.setParam1("0");
                    clubUser.setParam2("0");
	            clubUser.setAccountType(0);
	            //if (confMsg) clubUser.setAccountType(5);
	            clubUser.setBillingStart(bstart);
	            clubUser.setBillingEnd(bend);
	            if (!campaignUnique.equals("")) clubUser.setCampaign(campaignUnique);
	            clubUser.setNetworkCode(billednetwork);
	          
	       
	          if(club.getRegion().equals("IE")){
		    clubUser.setSubscribed(SdcMiscDate.parseSqlDateTime(ume_manualSubscribed));
	            }
	          //If Etienne's parameter is not received
	              else if(club.getRegion().equals("IE"))
	             {
	                clubUser.setSubscribed(SdcMiscDate.parseSqlDateTime(ume_manualSubscribed));
	             }
	            else{
	              //If user doesn't confirm the service then No need to do anything as this is Resubscription process for existing user
	              //We will wait for this user to confirm the service to update his Subscription Date
	                //clubUser.setSubscribed(new Date());
	                }
                    billingplan=billingplandao.getBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());
                  if(billingplan!=null && billingplan.getActiveForBilling()==1){
                      createbillPlan=false;
                     }

	            clubUser.setNextPush(MiscDate.parseSqlDate("2011-01-01"));
                    clubUser.setLandingpage("");
	            umemobileclubuserdao.saveItem(clubUser);
                     }
                       //confMsg = false;
	        }
                    //ClubUser Record does not exist so create a new one. 
	        else {
                    clubUser = new SdcMobileClubUser();
	            clubUser.setUnique(SdcMisc.generateUniqueId("BUS"));
	            clubUser.setUserUnique(user.getUnique());
	            clubUser.setClubUnique(club.getUnique());
	            clubUser.setParsedMobile(user.getParsedMobile());
	             //System.out.println("THE VALUE OF RESULT IS : "+"SECOND "+aReq.get("result"));
	            clubUser.setActive(1);
	            clubUser.setCredits(club.getCreditAmount());
	            clubUser.setAccountType(0);
	            clubUser.setBillingStart(bstart);
	            clubUser.setBillingEnd(bend);
	            clubUser.setBillingRenew(bstart);
	            clubUser.setPushCount(0);
	            clubUser.setCreated(new Date());
	            clubUser.setCampaign(campaignUnique);
	            clubUser.setNetworkCode(billednetwork);
	            clubUser.setUnsubscribed(new Date(0));
	            clubUser.setNextPush(MiscDate.parseSqlDate("2011-01-01"));
                       // clubUser.setaExternalId(" ");
	            
	            if(club.getRegion().equals("IE")){
		    clubUser.setSubscribed(SdcMiscDate.parseSqlDateTime(ume_manualSubscribed));
	            //System.out.println("updated Etienne's subscribed date with  "+ subscdate+ "for clubUser "+ clubUser.getParsedMobile());
	              }
	          //If Etienne's parameter is not received
	              else if(club.getRegion().equals("IE"))
	             {
	                clubUser.setSubscribed(SdcMiscDate.parseSqlDateTime(ume_manualSubscribed));
	            }
	            else{
	              // New ClubUser, So we will update his subscription date as 1970-01-01 until his Confirmation of Service
	                clubUser.setSubscribed(SdcMiscDate.parseSqlDateTime(ume_manualSubscribed));
	                }
	            clubUser.setLandingpage("");
	            umemobileclubuserdao.saveItem(clubUser);
	            user.getClubMap().put(club.getUnique(), clubUser);
	        }

	           
	        
	    } //end user!=null
            else {
                 

	        // Set up a completely new user
	        //if (user==null) {
	            user = new UmeUser(); // <- new user...
	            user.setMobile(msisdn);
	            user.setWapId(SdcMisc.generateLogin(10));
	            user.setDomain(defClubDomain);
	            user.setActive(1);
	            user.setCredits(club.getCreditAmount());
	            String stat = umeuserdao.addNewUser(user);
	        //}

	        //System.out.println("STAT: " + stat);
	        if (stat.equals("")) {
                   //System.out.println("THE VALUE OF RESULT IS : "+"THIRD "+aReq.get("result"));
	            clubUser = new SdcMobileClubUser();
	            clubUser.setUnique(SdcMisc.generateUniqueId("BUS"));
	            clubUser.setUserUnique(user.getUnique());
	            clubUser.setClubUnique(club.getUnique());
	            clubUser.setParsedMobile(user.getParsedMobile());
	            clubUser.setActive(1);
	            clubUser.setParam1("0");
	       		 clubUser.setCredits(club.getCreditAmount());
	            
	            clubUser.setAccountType(0);
	            //if (confMsg) clubUser.setAccountType(5);
	            clubUser.setBillingStart(bstart);
	            clubUser.setBillingEnd(bend);
	            clubUser.setBillingRenew(bstart);
	            clubUser.setPushCount(0);
	            clubUser.setCreated(new Date());
	            clubUser.setCampaign(campaignUnique);
	            clubUser.setNetworkCode(billednetwork);
	            clubUser.setUnsubscribed(new Date(0));
	            clubUser.setNextPush(MiscDate.parseSqlDate("2011-01-01"));
	            
	         if(club.getRegion().equals("IE")){
		    clubUser.setSubscribed(SdcMiscDate.parseSqlDateTime(ume_manualSubscribed));
	            //System.out.println("updated Etienne's subscribed date with  "+ subscdate+ "for clubUser "+ clubUser.getParsedMobile());
	              }
	          //If Etienne's parameter is not received
	              else if(club.getRegion().equals("IE"))
	             {
	                clubUser.setSubscribed(SdcMiscDate.parseSqlDateTime(ume_manualSubscribed));
	            }
	            else{
	              //New ClubUser So we will update his subscription date to 1970-01-01 until his Confirmation of Service
	                clubUser.setSubscribed(new Date(0));
	                }
	            //System.out.println("Added Subscription for user "+ clubUser.getParsedMobile()+ " for club: "+ clubUser.getClubUnique()+" DateTested "+ clubUser.getSubscribed()+" etiennedate: "+subscdate);
	            umemobileclubuserdao.saveItem(clubUser);
                    status = "OK: User added, unique: " + user.getUnique();
                }
                else status = SdcMisc.htmlEscape(stat);
	        }
      
           if(billingplan==null || createbillPlan ){
                        billingplan=new MobileClubBillingPlan();
                        billingplan.setTariffClass(club.getPrice());
                        billingplan.setActiveForAdvancement(1);
                        billingplan.setActiveForBilling(1);
                        billingplan.setAdhocsRemaining(0.0);
                        billingplan.setBillingEnd(bend);
                        billingplan.setClubUnique(club.getUnique());
                        billingplan.setContractType("");
                        billingplan.setLastPaid(clubUser.getSubscribed());
                        billingplan.setLastSuccess(new Date(0));
                        billingplan.setLastPush(new Date(0));
                        billingplan.setNetworkCode(billednetwork);
                        billingplan.setNextPush(bend);
                        billingplan.setParsedMobile(user.getParsedMobile());
                        billingplan.setPartialsPaid(0.0);
                        billingplan.setSubscribed(clubUser.getSubscribed());
                        billingplan.setPartialsRequired(Double.parseDouble(String.valueOf(userclubdetails.getFrequency()+"")));
                        billingplan.setPushCount(0.0);
                        billingplan.setServiceDate(bstart);
                        billingplan.setSubUnique(clubUser.getUserUnique());
                       	billingplan.setExternalId(""); //This is for Italy SubscriptionId so just setting the values. 
                        billingplan.setServiceDateBillsRemaining(Double.parseDouble(String.valueOf(userclubdetails.getFrequency())));
                        billingplan.setServiceDateBillsRemaining(0.0);
                        billingplandao.addBillingPlan(billingplan);
                        
                    campaigndao.log("iremo", "", msisdn,msisdn, null,null, clubUser.getCampaign(), clubUser.getClubUnique(), "SUBSCRIBED", 0, request,response,network.toLowerCase());
                        
                   
                        //Subscription CPA
                        if(null!=campaignUnique && !"".equalsIgnoreCase(campaignUnique)){
                            cmpg = UmeTempCmsCache.campaignMap.get(campaignUnique);
                            
                            if(cmpg!=null){
                                if(cmpg.getSrc().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("subscription")){
                                    String cpaLogstatus="0";
                                    SimpleDateFormat currentsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Calendar nowtimess=new GregorianCalendar();
                            Calendar currentTime=Calendar.getInstance();
	            	    currentTime.add(Calendar.MINUTE, 10);
	            	    String nextpush=currentsdf.format(currentTime.getTime());
                                  String  cpaloggingquery="insert into cpaLogging (aParsedMobile,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc) values"
	            			 			+"('"+user.getParsedMobile()+"','"+campaignUnique+"','"+club.getUnique()+"','"+currentsdf.format(nowtimess.getTime())+"','"+nextpush+"','"+cpaLogstatus+"','"+network+"','"+cmpg.getSrc()+"')";
	            	 int insertedRows=zacpalog.executeUpdateCPA(cpaloggingquery);
                                    
                                    
                                }
                                    
                            }
                            
                        }
                        
                        String toAddresse=user.getParsedMobile();
                        msg=club.getWebConfirmation();
                    if(sendPremiumSMS(toAddresse,freeCostId,billednetwork,serviceId,deliveryReceipt,msg,shortcode,transactionId,typeId,club,umesmsdao,quizsmsdao,umequizdao)){
        
                         //campaigndao.log("irestopmo", "", msisdn,msisdn, null,null, clubUser.getCampaign(), clubUser.getClubUnique(), "STOP", 0, request,response,network);
            
                   
               }    
                        
                        } //billingPlan ==null
           
           
} //End else for users replying message

  }//end if not exist
  
  
  
  response.setStatus(200);
  




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
    boolean success=false;
	
	if(isSuccessful){
		for(int i=0;i<msisdn.length;i++){
                
		SdcSmsSubmit sdcSmsSubmit=new SdcSmsSubmit();
		
		sdcSmsSubmit.setUnique(String.valueOf(transactionId));
		sdcSmsSubmit.setFromNumber(shortCode);
		sdcSmsSubmit.setToNumber(msisdn[i]);
		sdcSmsSubmit.setMsgType("Free");
		sdcSmsSubmit.setMsgBody(msgText);
                sdcSmsSubmit.setLogUnique(id);
        sdcSmsSubmit.setCost(club.getPrice());
        sdcSmsSubmit.setMsgCode1("txtmo.jsp");
		umesmsdao.log(sdcSmsSubmit);    
                quizsmsdao.log(sdcSmsSubmit);
		
		}
		success=true;
	}
        else {
            System.out.println("irelandtesting  "+responsecode+":"+responsedesc);
        }
		return success;
}






public boolean sendStopSMS(String[] msisdn, String costId,String network,String serviceId, String deliveryReceipt, String msgText, String shortCode,String transactionId, String typeId,MobileClub club,UmeSmsDao umesmsdao,QuizSmsDao quizsmsdao){
	
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
    network="international";
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
        ireMap.put("title","57997");
        ireMap.put("smscat","991");
        
        urlwrapper.wrapGet(ireMap);
    
        String responsecode=urlwrapper.getResponseCode();
        String responsedesc=urlwrapper.getResponseContent();
        boolean isSuccessful=urlwrapper.isSuccessful();
        
      System.out.println("txtnation: http request sent "+irehttp);
    
    System.out.println("txtnation:  confirm.jsp response "+responsecode+"  desc "+responsedesc+" successful: "+isSuccessful);
//    
    boolean success=false;

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
        sdcSmsSubmit.setCost(club.getPrice());
        sdcSmsSubmit.setMsgCode1("txtmo.jsp");
		umesmsdao.log(sdcSmsSubmit);    
                quizsmsdao.log(sdcSmsSubmit);
		
		}
		success=true;
	}
        else {
            System.out.println("irelandtesting  "+responsecode+":"+responsedesc);
        }
		return success;
}

public boolean sendPremiumSMS(String msisdn, String costId,String network,String serviceId, String deliveryReceipt, String msgText, String shortCode,String transactionId, String typeId,MobileClub club,UmeSmsDao umesmsdao,QuizSmsDao quizsmsdao,UmeQuizDao umequizdao){
	
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
    
    irehttp+="?reply=0&id="+id+"&number="+msisdn+"&network="+network+"&value=2.50&currency=EUR&cc=moonlight&ekey="+ekey+"&message="+msg;
    
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
        
      System.out.println("txtnation: http premium request sent "+irehttp);
    
    System.out.println("txtnation:  txtmo.jsp response "+responsecode+"  desc "+responsedesc+" successful: "+isSuccessful);
//    
    boolean success=false;

	if(isSuccessful){
	
                    
                                QuizUserAttempted quizUserAttempted=new QuizUserAttempted();
                    		quizUserAttempted.setaParsedMsisdn(msisdn);
                    		quizUserAttempted.setClubUnique(club.getUnique());
                    		quizUserAttempted.setType("Entry Confirmation");
                    		quizUserAttempted.setStatus("false");
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
                sdcSmsSubmit.setMsgCode1("txtmo.jsp");
                sdcSmsSubmit.setReqType("firstbillable");
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
