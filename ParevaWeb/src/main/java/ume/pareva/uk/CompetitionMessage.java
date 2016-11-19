package ume.pareva.uk;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.dao.QuizSmsDao;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.engage.pojo.SMSParameter;
import ume.pareva.engageimpl.EngageInterface;
import ume.pareva.engageimpl.EngageInterfaceImpl;
import ume.pareva.ire.IREConnConstants;
import ume.pareva.sdk.Misc;
import ume.pareva.smsapi.IpxSmsConnection;
import ume.pareva.smsapi.IpxSmsSubmit;
import ume.pareva.util.MessageReplacement;
import ume.pareva.util.httpconnection.HttpURLConnectionWrapper;

/**
 *
 * @author madan
 */
@Component("competitionmessage")
public class CompetitionMessage {

    @Autowired
    QuizSmsDao quizsmsdao;

    @Autowired
    UmeSmsDao umesmsdao;
    
    @Autowired
    MessageReplacement messagereplace;

    private final Logger logger = LogManager.getLogger(CompetitionMessage.class.getName());

    public boolean requestSendSmsTimer(final String[] msisdn, final String costId, final String serviceId, final String deliveryReceipt, final String msgText, final String shortCode, final String transactionId, final String typeId, final MobileClub club, final int time, final boolean trickyTime, final String campaignId) {
        boolean sent = true;
        if (!trickyTime) {
            sent = sendSMS(msisdn, costId, serviceId, deliveryReceipt, msgText, shortCode, transactionId, typeId, club, campaignId);
        } else {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            sendSMS(msisdn, costId, serviceId, deliveryReceipt, msgText, shortCode, transactionId, typeId, club, campaignId);
                        }
                    },
                    time
            );
        }

        return sent;
    }

    public boolean requestIESendSmsTimer(final String[] msisdn, final String msgText, final String shortCode, final MobileClub club, final UmeClubDetails userClubDetails, final int time, final boolean trickyTime, final String campaignId) {
        boolean sent = true;

        System.out.println("ie compmessage " + msisdn[0] + "  tricky time is " + trickyTime + " Time is " + time + " at " + new Date());
        if (!trickyTime) {
            sent = sendIESMS(msisdn, msgText, shortCode, club, userClubDetails, campaignId);
        } else {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("ie compmessage sending now " + msisdn[0] + "  tricky time is " + trickyTime + " Time is " + time + " at " + new Date());

                            sendIESMS(msisdn, msgText, shortCode, club, userClubDetails, campaignId);
                        }
                    }, time);
        }

        return sent;
    }

    public boolean requestITSendSmsTimer(final String[] msisdn, final String msgText, final String shortCode, final MobileClub club, final UmeClubDetails userClubDetails, final int time, final boolean trickyTime) {
        boolean sent = true;

        System.out.println("ie compmessage " + msisdn[0] + "  tricky time is " + trickyTime + " Time is " + time + " at " + new Date());
        if (!trickyTime) {
            sent = sendITSMS(msisdn, msgText, shortCode, club, userClubDetails);
        } else {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("ie compmessage sending now " + msisdn[0] + "  tricky time is " + trickyTime + " Time is " + time + " at " + new Date());

                            sendITSMS(msisdn, msgText, shortCode, club, userClubDetails);
                        }
                    }, time);
        }

        return sent;
    }
    
    public boolean requestAESendSmsTimer(final String[] msisdn, final String msgText, final String shortCode, final MobileClub club, final UmeClubDetails userClubDetails, final int time, final boolean trickyTime) {
        boolean sent = true;

        System.out.println("ie compmessage " + msisdn[0] + "  tricky time is " + trickyTime + " Time is " + time + " at " + new Date());
        if (!trickyTime) {
            sent = sendAESMS(msisdn, msgText, shortCode, club, userClubDetails);
        } else {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("ie compmessage sending now " + msisdn[0] + "  tricky time is " + trickyTime + " Time is " + time + " at " + new Date());

                            sendAESMS(msisdn, msgText, shortCode, club, userClubDetails);
                        }
                    }, time);
        }

        return sent;
    }
    
    public boolean sendSMS(String[] msisdn, String costId, String serviceId, String deliveryReceipt, String msgText, String shortCode, String transactionId, String typeId, MobileClub club, String campaignId) {
        boolean success = false;
        String clubunique = "";
        if (club != null) {
            clubunique = club.getUnique();
        }
        
        

        if (msgText.equals("")) {
            return success;
        } else {
            
            msgText=messagereplace.replaceMessagePlaceholders(msgText, msisdn[0], club.getUnique());
            
            
            if (!quizsmsdao.sent3Times(msisdn[0], clubunique)) {
                String encodedMessage = "<![CDATA[" + msgText + "]]>";
                SMSParameter smsParameter = new SMSParameter();
                smsParameter.setCostId(costId);
                smsParameter.setServiceId(serviceId);
                smsParameter.setDeliveryReceipt(deliveryReceipt);
                smsParameter.setMsgText(encodedMessage);
                smsParameter.setShortCode(shortCode);
                smsParameter.setTransactionId(String.valueOf(transactionId));
                smsParameter.setTypeId(typeId);
                smsParameter.setToAddress(msisdn);
                smsParameter.setWinTransactionId(String.valueOf(Misc.generateUniqueIntegerId()));

                EngageInterface engageInterface = new EngageInterfaceImpl();
                int responseCode = 0;
                try {
                    responseCode = engageInterface.sendSMS(smsParameter);
                } catch (Exception e) {
                    responseCode = 0;
                }

                if (responseCode == 200) {
                    for (int i = 0; i < msisdn.length; i++) {

//                	QuizUserAttempted quizUserAttempted=new QuizUserAttempted();
//                    		quizUserAttempted.setaParsedMsisdn(msisdn[i]);
//                    		quizUserAttempted.setClubUnique(club.getUnique());
//                    		quizUserAttempted.setType("Teaser");
//                    		quizUserAttempted.setStatus("false");
//                    		quizUserAttempted.setaUnique(String.valueOf(transactionId));
//                    		quizUserAttempted.setaCreated(new java.sql.Timestamp(System.currentTimeMillis()));
//                    		umequizdao.saveQuizUserAttempted(quizUserAttempted);    
                        SdcSmsSubmit sdcSmsSubmit = new SdcSmsSubmit();

                        sdcSmsSubmit.setUnique(String.valueOf(transactionId));
                        sdcSmsSubmit.setLogUnique(String.valueOf(transactionId));
                        sdcSmsSubmit.setFromNumber(shortCode);
                        sdcSmsSubmit.setToNumber(msisdn[i]);
                        sdcSmsSubmit.setMsgType("Free");
                        sdcSmsSubmit.setMsgBody(msgText);
                        sdcSmsSubmit.setCost(club.getPrice());
                        sdcSmsSubmit.setMsgCode1("confirm.jsp");
                        sdcSmsSubmit.setReqType("teaser");
                        sdcSmsSubmit.setStatus("SENT");
                        sdcSmsSubmit.setRefMessageUnique("TEASER");
                        sdcSmsSubmit.setClubUnique(clubunique);
                        sdcSmsSubmit.setCampaignUnique(campaignId);
                        //sdcSmsSubmit.setAppId("web3");
                        umesmsdao.log(sdcSmsSubmit);
                        quizsmsdao.log(sdcSmsSubmit);

                    }
                    success = true;
                } else {
                    System.out.println("quiz2winerrorcode " + responseCode);
                }
            }
        }
        return success;

    }

    //======== SENDING IE MESSAGE =================
    public boolean sendIESMS(String[] msisdn, String msgText, String shortCode, MobileClub club, UmeClubDetails userClubDetails, String campaignid) {

        /**
         * $req = 'reply=0'; $req .= '&id='.uniqid(); $req .=
         * '&number='.$number; $req .= '&network=INTERNATIONAL'; $req .=
         * '&message='.$msg; $req .= '&value=0'; $req .= '&currency=GBP'; $req
         * .= '&cc='.$company; $req .= '&title='.$title; $req .= '&ekey='.$ekey;
         * http://client.txtnation.com/gateway.php?reply=0&id=123456&number=353851485271
         * &network=international&value=0&currency=EUR&cc=moonlight&ekey=a6815e707c675f7a3f307656d462bca6&message=Your+message
         */
        boolean success = false;
        if (msgText.equalsIgnoreCase("")) {
            return success;
        } else {
            if (!quizsmsdao.sent3Times(msisdn[0], club.getUnique())) {
                String irehttp = "http://client.txtnation.com/gateway.php";
                String id = Misc.generateUniqueId() + "-" + club.getUnique();
                String network = "international";
                String ekey = club.getOtpServiceName();
                String cc = userClubDetails.getCompanyCode();
                String msgBkp = msgText;
        
                msgText=messagereplace.replaceMessagePlaceholders(msgText, msisdn[0], club.getUnique());
        
        
        if(msgText.isEmpty()){msgText=msgBkp;}
        String msg = msgText;
                try {
                    msg = java.net.URLEncoder.encode(msgText, "utf-8");
                } catch (Exception e) {
                }

                HttpURLConnectionWrapper urlwrapper = urlwrapper = new HttpURLConnectionWrapper(IREConnConstants.getDomainHttp());
                Map<String, String> ireMap = new HashMap<String, String>();

                ireMap.put("reply", "0");
                ireMap.put("id", id);
                ireMap.put("number", msisdn[0]);
                ireMap.put("network", network);
                ireMap.put("value", "0");
                ireMap.put("currency", club.getCurrency());
                ireMap.put("cc", cc);
                ireMap.put("ekey", ekey);
                ireMap.put("message", msg);
                ireMap.put("title", shortCode);
                ireMap.put("smscat", "991");

                urlwrapper.wrapGet(ireMap);

                String responsecode = urlwrapper.getResponseCode();
                String responsedesc = urlwrapper.getResponseContent();
                boolean isSuccessful = urlwrapper.isSuccessful();
                irehttp += "?reply=0&id=" + id + "&number=" + msisdn[0] + "&network=" + network + "&value=0&currency=" + club.getCurrency()
                        + "&cc=" + cc + "&ekey=" + ekey + "&message=" + msg + "&title="+shortCode+"&smscat=991";
                System.out.println("txtnation: http request sent " + irehttp);

                logger.info("txtnation : http request sent to " + msisdn[0] + "  request: " + irehttp);

                System.out.println("txtnation:  confirm.jsp response " + responsecode + "  desc " + responsedesc + " successful: " + isSuccessful);

                if (isSuccessful) {
                    for (int i = 0; i < msisdn.length; i++) {

                        SdcSmsSubmit sdcSmsSubmit = new SdcSmsSubmit();

                        sdcSmsSubmit.setUnique(id);
                        sdcSmsSubmit.setLogUnique(id);
                        sdcSmsSubmit.setFromNumber(shortCode);
                        sdcSmsSubmit.setToNumber(msisdn[i]);
                        sdcSmsSubmit.setMsgType("Free");
                        sdcSmsSubmit.setMsgBody(msgText);
                        sdcSmsSubmit.setReqType("teaser");
                        sdcSmsSubmit.setStatus("SENT");
                        sdcSmsSubmit.setClubUnique(club.getUnique());
                        sdcSmsSubmit.setCost(0);
                        sdcSmsSubmit.setSmsAccount(cc);
                        sdcSmsSubmit.setRefMessageUnique("TEASER");
                        sdcSmsSubmit.setMsgCode1("confirm.jsp");
                        sdcSmsSubmit.setCampaignUnique(campaignid);
                        umesmsdao.log(sdcSmsSubmit);
                        quizsmsdao.log(sdcSmsSubmit);

                    }
                    success = true;
                } else {
                    System.out.println("irelandtesting  " + responsecode + ":" + responsedesc);
                }
            }
        }
        return success;
    }

    //==== END SENDING IE MESSAGE =================

    
    //======== SENDING IT MESSAGE =================
    public boolean sendITSMS(String[] msisdn, String msgText, String shortCode, MobileClub club, UmeClubDetails userClubDetails) {

        boolean success = false;
        if (msgText.equalsIgnoreCase("")) {
            return success;
        } else {
            if (!quizsmsdao.sent3Times(msisdn[0], club.getUnique())) {
                
                double tariffClass = 0;
                
                IpxSmsSubmit sms = new IpxSmsSubmit();
                IpxSmsConnection smsConnection = new IpxSmsConnection();
                sms.setFromNumber("4882000");
                sms.setToNumber(msisdn[0]);

                sms.setReferenceID("");
                sms.setSmsAccount("ipx");
                sms.setUsername(club.getOtpSoneraId());
                sms.setPassword(club.getOtpTelefiId());
                sms.setMsgBody(msgText);
                sms.setCurrencyCode("EUR");
                sms.setTariffClass(tariffClass);
//                if (clubUser.getNetworkCode().equals("tim")) {
//                    sms.setServiceCategory("INFORMATION");
//                    String resp = smsConnection.doRequest(sms);
//                }smd
                
                sms.setServiceCategory("INFORMATION");

                String resp = smsConnection.doRequest(sms);
                boolean isSuccessful = resp.contains("successful");
                
                String id = Misc.generateUniqueId() + "-" + club.getUnique();
                String cc = userClubDetails.getCompanyCode();

                if (isSuccessful) {
                    for (int i = 0; i < msisdn.length; i++) {
                        SdcSmsSubmit sdcSmsSubmit = new SdcSmsSubmit();
                        sdcSmsSubmit.setUnique(id);
                        sdcSmsSubmit.setLogUnique(id);
                        sdcSmsSubmit.setFromNumber(shortCode);
                        sdcSmsSubmit.setToNumber(msisdn[i]);
                        sdcSmsSubmit.setMsgType("Free");
                        sdcSmsSubmit.setMsgBody(msgText);
                        sdcSmsSubmit.setReqType("teaser");
                        sdcSmsSubmit.setStatus("SENT");
                        sdcSmsSubmit.setClubUnique(club.getUnique());
                        sdcSmsSubmit.setCost(0);
                        sdcSmsSubmit.setSmsAccount(cc);
                        sdcSmsSubmit.setRefMessageUnique("TEASER");
                        sdcSmsSubmit.setMsgCode1("confirm.jsp");
                        umesmsdao.log(sdcSmsSubmit);
                        quizsmsdao.log(sdcSmsSubmit);

                    }
                    success = true;
                } else {
                    System.out.println("itlandtesting  " + resp);
                }
            }
        }
        return success;
    }
    //==== END SENDING IT MESSAGE =================

    
    //======== SENDING AE MESSAGE =================
    public boolean sendAESMS(String[] msisdn, String msgText, String shortCode, MobileClub club, UmeClubDetails userClubDetails) {

        boolean success = false;
        if (msgText.equalsIgnoreCase("")) {
            return success;
        } else {
            if (!quizsmsdao.sent3Times(msisdn[0], club.getUnique())) {
                
                double tariffClass = 0;
                
                IpxSmsSubmit sms = new IpxSmsSubmit();
                IpxSmsConnection smsConnection = new IpxSmsConnection();
                sms.setFromNumber("4882000");
                sms.setToNumber(msisdn[0]);

                sms.setReferenceID("");
                sms.setSmsAccount("ipx");
                sms.setUsername(club.getOtpSoneraId());
                sms.setPassword(club.getOtpTelefiId());
                sms.setMsgBody(msgText);
                sms.setCurrencyCode("EUR");
                sms.setTariffClass(tariffClass);
//                if (clubUser.getNetworkCode().equals("tim")) {
//                    sms.setServiceCategory("INFORMATION");
//                    String resp = smsConnection.doRequest(sms);
//                }smd
                
                sms.setServiceCategory("INFORMATION");

                String resp = smsConnection.doRequest(sms);
                boolean isSuccessful = resp.contains("successful");
                
                String id = Misc.generateUniqueId() + "-" + club.getUnique();
                String cc = userClubDetails.getCompanyCode();

                if (isSuccessful) {
                    for (int i = 0; i < msisdn.length; i++) {
                        SdcSmsSubmit sdcSmsSubmit = new SdcSmsSubmit();
                        sdcSmsSubmit.setUnique(id);
                        sdcSmsSubmit.setLogUnique(id);
                        sdcSmsSubmit.setFromNumber(shortCode);
                        sdcSmsSubmit.setToNumber(msisdn[i]);
                        sdcSmsSubmit.setMsgType("Free");
                        sdcSmsSubmit.setMsgBody(msgText);
                        sdcSmsSubmit.setReqType("teaser");
                        sdcSmsSubmit.setStatus("SENT");
                        sdcSmsSubmit.setClubUnique(club.getUnique());
                        sdcSmsSubmit.setCost(0);
                        sdcSmsSubmit.setSmsAccount(cc);
                        sdcSmsSubmit.setRefMessageUnique("TEASER");
                        sdcSmsSubmit.setMsgCode1("confirm.jsp");
                        umesmsdao.log(sdcSmsSubmit);
                        quizsmsdao.log(sdcSmsSubmit);

                    }
                    success = true;
                } else {
                    System.out.println("itlandtesting  " + resp);
                }
            }
        }
        return success;
    }
    //==== END SENDING AESMS MESSAGE =================
}
