package ume.pareva.it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.pojo.UmeUser;
import ume.pareva.smsapi.IpxSmsSubmit;
import ume.pareva.smsservices.SmsService;
import ume.pareva.util.MessageReplacement;

@Component("ipxtimersmsit")
public class IpxTimerSendSms {

    @Autowired
    private ume.pareva.es.DigitAPI firstsms;

    @Autowired
    private SmsService smsDao;

    @Autowired
    UmeSmsDao umesmsdao;
    
    @Autowired
    MessageReplacement messageReplacement;

    public void requestSendSmsTimer(final MobileClub club,
            final String msisdn, final String operator, final String subscriptionId, final SdcRequest aReq, final UmeUser user, final int time, final boolean trickyTime) {

        if (!trickyTime) {
            requestSendSms(club,
                    msisdn, operator, subscriptionId, aReq, user);
        } else {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            requestSendSms(club,
                                    msisdn, operator, subscriptionId, aReq, user);
                        }
                    },
                    time
            );
        }
    }

    public void requestSendSmsMessage(final MobileClub club,
            final String msisdn, final String operator, final String subscriptionId, final SdcRequest aReq, final UmeUser user, String message) {
        IpxSmsSubmit sms;
        if (aReq == null) {
            sms = new IpxSmsSubmit();
        } else {
            sms = new IpxSmsSubmit(aReq);
        }

        String msg = "";

        String linkWithId = "/?id=" + user.getWapId();
        msg = message;
        msg = msg.replace("<id>", linkWithId);
        msg = messageReplacement.replaceMessagePlaceholders(msg, msisdn, club.getUnique());
        
        System.out.println("ipx after replacement: " + msg);
        sms.setFromNumber("3399942323");
        sms.setServiceCategory("INFORMATION");
        sms.setReferenceID(subscriptionId);
        sms.setReferenceID("");
        sms.setSmsAccount("ipx");
        sms.setUsername(club.getOtpSoneraId());
        sms.setPassword(club.getOtpTelefiId());
        sms.setToNumber(user.getParsedMobile());
        sms.setMsgBody(msg);
        sms.setCurrencyCode("EUR");

//	        UmeSmsDaoExtension smsDao = new UmeSmsDaoExtension();
        String resp = smsDao.send((SdcSmsSubmit) sms);

        System.out.println("IPX sms sending: " + resp);

        boolean isSuccessful = resp.contains("successful");

        //TODO send SMS premium IPX
        if (isSuccessful) {
            try {
                umesmsdao.log(sms);
            } catch (Exception e) {
                System.out.println("requestSendSmsMessage exception: " + e.getMessage());
            }
        }

    }

    public void requestSendSmsMessage(final MobileClub club,
            final String msisdn, final String operator, final String subscriptionId, final SdcRequest aReq, final UmeUser user, final int time, final boolean trickyTime, final String message) {

        System.out.println("requestSendSmsMessage: " + msisdn + "--" + message + "--" + trickyTime);
        
        if (!trickyTime) {
//            requestSendSmsMessage(club,
//                    msisdn, operator, subscriptionId, aReq, user);
            requestSendSmsMessage(club,
                                    msisdn, operator, subscriptionId, aReq, user, message);
        } else {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            requestSendSmsMessage(club,
                                    msisdn, operator, subscriptionId, aReq, user, message);
                        }
                    },
                    time
            );
        }
    }

    public void requestSendSms(final MobileClub club,
            final String msisdn, final String operator, final String subscriptionId, final SdcRequest aReq, final UmeUser user) {
        IpxSmsSubmit sms;
        if (aReq == null) {
            sms = new IpxSmsSubmit();
        } else {
            sms = new IpxSmsSubmit(aReq);
        }

        String msg = "";

        String linkWithId = "/?id=" + user.getWapId();
        msg = club.getWelcomeSms();
        msg = msg.replace("<id>", linkWithId);
        msg = messageReplacement.replaceMessagePlaceholders(msg, msisdn, club.getUnique());

        if (operator.equals("wind")) {
            sms.setFromNumber("3202071010");
            msg = msg.replace("<wind>", "/SMS STOP al 3202071010");
        } else { //operator is TIM
            sms.setFromNumber("3399942323");
            sms.setServiceCategory("INFORMATION");
            sms.setReferenceID("");
//            sms.setReferenceID(subscriptionId);
            msg = msg.replace("<wind>", "");
        }

        System.out.println("IPX welcome message: " + msg);
        System.out.println("IPX welcome message length: " + msg.length());

        sms.setSmsAccount("ipx");
        sms.setUsername(club.getOtpSoneraId());
        sms.setPassword(club.getOtpTelefiId());
        sms.setToNumber(user.getParsedMobile());
        sms.setMsgBody(msg);
        sms.setCurrencyCode("EUR");

//	        UmeSmsDaoExtension smsDao = new UmeSmsDaoExtension();
        String resp = smsDao.send((SdcSmsSubmit) sms);

        System.out.println("IPX sms sending: " + resp);
    }

    public void requestSendSmsSpainTimer(final MobileClub club,
            final String msisdn, final String fromNumber, final UmeUser user, final int time, final boolean trickyTime) {

        if (!trickyTime) {
            requestSendSmsSpainTimer(club,
                    msisdn, fromNumber, user);
        } else {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            requestSendSmsSpainTimer(club,
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

        try {
            firstsms.sendSMS();
        } catch (Exception e) {
            System.out.println("IPX Spain Send Sms Exception: " + e);
            e.printStackTrace();
        }
    }

    public void requestSendSmsMessageTimer(
            final String msisdn, final String fromNumber, final String msg, final int time, final MobileClub club) {

        if (time <= 0) {
            requestSendSmsMessageTimer(
                    msisdn, fromNumber, msg, club);
        } else {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            requestSendSmsMessageTimer(
                                    msisdn, fromNumber, msg, club);
                        }
                    },
                    time
            );
        }
    }

    public void requestSendSmsMessageTimer(
            final String msisdn, final String fromNumber, String msg, MobileClub club) {
        
        msg = messageReplacement.replaceMessagePlaceholders(msg, msisdn, club.getUnique());
        firstsms.setMsg(msg);
        firstsms.setMsisdn(msisdn);
        firstsms.setFrom(fromNumber); //11 alphanumeric characters OR upto 16 digit phone number
        firstsms.setReport("True");
        firstsms.setNetwork("UNKNOWN");
        System.out.println("IPX Italy Send SMS: " + msisdn + "--" + fromNumber + "--" + msg);

        try {
            firstsms.sendSMS();
        } catch (Exception e) {
            System.out.println("IPX Spain Send Sms Exception: " + e);
            e.printStackTrace();
        }
    }

    public void requestSendSmsTimerMessage(final String message,
            final String msisdn, final String fromNumber, final UmeUser user, final int time, final boolean trickyTime, final MobileClub club) {

        if (!trickyTime) {
            requestSendSmsTimerMessage(message,
                    msisdn, fromNumber, user, club);
        } else {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            requestSendSmsTimerMessage(message,
                                    msisdn, fromNumber, user, club);
                        }
                    },
                    time
            );
        }
    }

    public void requestSendSmsTimerMessage(final String message,
            final String msisdn, final String fromNumber, final UmeUser user, MobileClub club) {

        String msg = "";
        String linkWithId = "/?id=" + user.getWapId();
        msg = message.replace("<id>", linkWithId);
        
        msg = messageReplacement.replaceMessagePlaceholders(msg, msisdn, club.getUnique());

        firstsms.setMsg(message);
        //firstsms.setMsisdn("447427626522"); 
        firstsms.setMsisdn(msisdn);
        firstsms.setFrom(fromNumber); //11 alphanumeric characters OR upto 16 digit phone number
        firstsms.setReport("True");
        firstsms.setNetwork("UNKNOWN");

        System.out.println("IPX Spain Send SMS: " + msisdn + "--" + fromNumber + "--" + msg);

        try {
            firstsms.sendSMS();
        } catch (Exception e) {
            System.out.println("IPX Spain Send Sms Exception: " + e);
            e.printStackTrace();
        }
    }

}
