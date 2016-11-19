package ume.pareva.es;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.pojo.UmeUser;
import ume.pareva.smsapi.IpxSmsSubmit;
import ume.pareva.smsservices.SmsService;

@Component("ipxtimersmses")
public class IpxTimerSendSms {
//        @Autowired
//        private UmeSmsDao umesmsdao;
        @Autowired
        private DigitAPI firstsms;
        
        @Autowired
        SmsService smsDao;
        
	public void requestSendSmsTimer(final MobileClub club,
			final String msisdn, final String operator, final String subscriptionId , final String campaignId,final SdcRequest aReq, final UmeUser user, final int time, final boolean trickyTime) {

		if(!trickyTime){
        	requestSendSms( club,
        			msisdn, operator, subscriptionId, campaignId , aReq, user);
		}else{
	        new java.util.Timer().schedule( 
	                new java.util.TimerTask() {
	                    @Override
	                    public void run() {
	                    	requestSendSms( club,
	                    			msisdn, operator, subscriptionId , campaignId, aReq, user);
	                	}
	                }, 
	                time 
	        );
		}
	}
	
	public void requestSendSms(final MobileClub club,
			final String msisdn, final String operator, final String subscriptionId , String campaignId,final SdcRequest aReq, final UmeUser user) {
	        IpxSmsSubmit  sms = new IpxSmsSubmit(aReq);
	        String msg = "";
	
	        String linkWithId = "/?id=" + user.getWapId();
	        msg = club.getWelcomeSms();
	        msg = msg.replace("<id>", linkWithId);
	
	        if(operator.equals("wind")){
	            sms.setFromNumber("3202071010");
	            msg = msg.replace("<wind>", "/SMS STOP al 3202071010");
	        }
	        else{ //operator is TIM
	            sms.setFromNumber("3399942323");
	            sms.setServiceCategory("INFORMATION");
	            sms.setReferenceID(subscriptionId);
	            msg = msg.replace("<wind>", "");
	        }   
	
	        System.out.println("IPX welcome message: " + msg);
	        System.out.println("IPX welcome message length: " + msg.length());
	
	        sms.setSmsAccount("ipx");
	        sms.setUsername(club.getOtpSoneraId());
	        sms.setPassword(club.getOtpTelefiId());
	        sms.setToNumber(user.getParsedMobile());
                sms.setClubUnique(club.getUnique());
//                sms.setCampaignId(campaignId);
	        sms.setMsgBody(msg);
	        sms.setCurrencyCode("EUR");
	        
	        String resp = smsDao.send((SdcSmsSubmit)sms);
	        
	        System.out.println("IPX sms sending: " + resp);
	}
	
	public void requestSendSmsSpainTimer(final MobileClub club,
			final String msisdn, final String fromNumber, final UmeUser user, final int time, final boolean trickyTime) {

		if(!trickyTime){
			requestSendSmsSpainTimer( club,
        			msisdn, fromNumber, user);
		}else{
	        new java.util.Timer().schedule( 
	                new java.util.TimerTask() {
	                    @Override
	                    public void run() {
	                    	requestSendSmsSpainTimer( club,
	                    			msisdn, fromNumber, user);
	                	}
	                }, 
	                time 
	        );
		}
	}
	
	public void requestSendSmsSpainTimer(final MobileClub club,
			final String msisdn, final String fromNumber, final UmeUser user) {
		
			
	        String msg = "";	
	        String linkWithId = "/?id=" + user.getWapId();
	        msg = club.getWelcomeSms();
	        msg = msg.replace("<id>", linkWithId);
		        
	        firstsms.setMsg(msg);
	        //firstsms.setMsisdn("447427626522"); 
	        firstsms.setMsisdn(msisdn);        
	        firstsms.setFrom(fromNumber); //11 alphanumeric characters OR upto 16 digit phone number
	        firstsms.setReport("True");      
	        firstsms.setNetwork("UNKNOWN");

	        System.out.println("IPX Spain Send SMS: " + msisdn + "--" + fromNumber + "--" + msg);
	        
	        try{
	        	firstsms.sendSMS();  
	        }
	        catch(Exception e){
	        	System.out.println("IPX Spain Send Sms Exception: " + e);
	        	e.printStackTrace();}  	
	}
	
        
        public void requestSendSmsSpainTimerMessage(final String message,
			final String msisdn, final String fromNumber, final UmeUser user, final int time, final boolean trickyTime) {

		if(!trickyTime){
			requestSendSmsSpainTimerMessage( message,
        			msisdn, fromNumber, user);
		}else{
	        new java.util.Timer().schedule( 
	                new java.util.TimerTask() {
	                    @Override
	                    public void run() {
	                    	requestSendSmsSpainTimerMessage( message,
	                    			msisdn, fromNumber, user);
	                	}
	                }, 
	                time 
	        );
		}
	}
	
	public void requestSendSmsSpainTimerMessage(final String message,
			final String msisdn, final String fromNumber, final UmeUser user) {
		
			
                String msg = "";
	        String linkWithId = "/?id=" + user.getWapId();
	        msg = message.replace("<id>", linkWithId);
		        
	        firstsms.setMsg(msg);
	        //firstsms.setMsisdn("447427626522"); 
	        firstsms.setMsisdn(msisdn);        
	        firstsms.setFrom(fromNumber); //11 alphanumeric characters OR upto 16 digit phone number
	        firstsms.setReport("True");      
	        firstsms.setNetwork("UNKNOWN");

	        System.out.println("IPX Spain Send SMS: " + msisdn + "--" + fromNumber + "--" + msg);
	        
	        try{
	        	firstsms.sendSMS();  
	        }
	        catch(Exception e){
	        	System.out.println("IPX Spain Send Sms Exception: " + e);
	        	e.printStackTrace();}  	
	}
        
        public void requestSendSmsSpainTimerMessage(final String message,
			final String msisdn, final String fromNumber, final int time, final boolean trickyTime) {

		if(!trickyTime){
			requestSendSmsSpainTimerMessage( message,
        			msisdn, fromNumber);
		}else{
	        new java.util.Timer().schedule( 
	                new java.util.TimerTask() {
	                    @Override
	                    public void run() {
	                    	requestSendSmsSpainTimerMessage( message,
	                    			msisdn, fromNumber);
	                	}
	                }, 
	                time 
	        );
		}
	}
	
	public void requestSendSmsSpainTimerMessage(final String message,
			final String msisdn, final String fromNumber) {
				        
	        firstsms.setMsg(message);
	        //firstsms.setMsisdn("447427626522"); 
	        firstsms.setMsisdn(msisdn);        
	        firstsms.setFrom(fromNumber); //11 alphanumeric characters OR upto 16 digit phone number
	        firstsms.setReport("True");      
	        firstsms.setNetwork("UNKNOWN");

	        System.out.println("IPX Spain Send SMS: " + msisdn + "--" + fromNumber + "--" + message);
	        
	        try{
	        	firstsms.sendSMS();  
	        }
	        catch(Exception e){
	        	System.out.println("IPX Spain Send Sms Exception: " + e);
	        	e.printStackTrace();}  	
	}

}

