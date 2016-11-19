package ume.pareva.smsservices;

import java.util.Arrays;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.dao.QuizSmsDao;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeQuizDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.engage.pojo.SMSParameter;
import ume.pareva.engageimpl.EngageInterfaceImpl;
import ume.pareva.pojo.QuizUserAttempted;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;

/**
 *
 * @author madan
 */

@Component("UK_SMS")
public class UKSmsService {
    
    private static final Logger logger = LogManager.getLogger(UKSmsService.class.getName());
    
    @Autowired
    UmeSmsDao umesmsdao;
    
    @Autowired
    UmeQuizDao umequizdao;
    
    @Autowired
    QuizSmsDao quizsmsdao;
    
    
    public boolean sendMessage(UmeSessionParameters aReq,UmeUser user, SdcMobileClubUser clubuser, String msgText, MobileClub club, UmeClubDetails clubDetails, String msgType, boolean billable,String serviceId,String deliveryReceipt,String senderReference){
        boolean sent=false;
        String costId;
        String shortcode;//=clubDetails.getClubSpoof();
        String transactionId=Misc.generateUniqueIntegerId()+"-"+club.getUnique();
        String freeorpremium;//="Free";
        String status;
        String typeId="2";
          String[] toAddress={clubuser.getParsedMobile()};
          
        if(billable){ 
            costId="2";
            shortcode=club.getSmsNumber();
            freeorpremium="Premium";status="SENT";
        }
        else{
            status="DELIVERED";
            if(clubDetails.getClubSpoof()==null||clubDetails.getClubSpoof().trim().isEmpty()){
                shortcode="Quiz2Win";}
            else{
            shortcode=clubDetails.getClubSpoof();
            }
            
            freeorpremium="Free";costId="1";}
        if(msgType.equalsIgnoreCase("teaser")){ shortcode=club.getSmsNumber(); transactionId="UKW"+transactionId;}
        
        if (msgText.contains("####")) {
            String encrypt = "Q2W" + MiscCr.encrypt(clubuser.getParsedMobile()).substring(0, 5);            
            msgText = msgText.replace("####", encrypt); //SdcMisc.generateLogin(5));
            
        }
        String texttosend = "<![CDATA[" + msgText + "]]>";
        
        SMSParameter smsparameter=new SMSParameter();
        smsparameter.setCostId(costId);
        smsparameter.setServiceId(serviceId);
        smsparameter.setDeliveryReceipt(deliveryReceipt);
        smsparameter.setMsgText(texttosend);
        smsparameter.setShortCode(shortcode);
        smsparameter.setTransactionId(transactionId);
        smsparameter.setTypeId(typeId);
        smsparameter.setToAddress(toAddress);
        smsparameter.setWinTransactionId(String.valueOf(Misc.generateUniqueIntegerId()));
        
        System.out.println("UKSMSSENDING COSTID=" + smsparameter.getCostId() + " SERVICEID=" + smsparameter.getServiceId() + " DELIVERYRECEIPT=" + smsparameter.getDeliveryReceipt()
                + " SHORTCODE=" + smsparameter.getShortCode() + " TRANSACTIONID=" + smsparameter.getTransactionId() + " TYPEID=" + smsparameter.getTypeId()
                + " TOADDRESS=" + Arrays.toString(smsparameter.getToAddress()) + " MSGTEXT=" + smsparameter.getMsgText());
        
        logger.info("UKSMSSENDING COSTID=" + smsparameter.getCostId() + " SERVICEID=" + smsparameter.getServiceId() + " DELIVERYRECEIPT=" + smsparameter.getDeliveryReceipt()
                + " SHORTCODE=" + smsparameter.getShortCode() + " TRANSACTIONID=" + smsparameter.getTransactionId() + " TYPEID=" + smsparameter.getTypeId()
                + " TOADDRESS=" + Arrays.toString(smsparameter.getToAddress()) + " MSGTEXT=" + smsparameter.getMsgText());
        double before = System.currentTimeMillis();
        EngageInterfaceImpl eii = new EngageInterfaceImpl();
        int resp = eii.sendSMS(smsparameter);
        double totalResponseTime = (double) ((System.currentTimeMillis() - before) / 1000);
        
        if(resp==200)sent=true;
        //===================       Logging to smsMsgLog =======================================
        SdcSmsSubmit sdcSmsSubmit=new SdcSmsSubmit();                    		
        sdcSmsSubmit.setUnique(smsparameter.getTransactionId());
        sdcSmsSubmit.setLogUnique(smsparameter.getTransactionId());
        sdcSmsSubmit.setFromNumber(shortcode);
        sdcSmsSubmit.setToNumber(clubuser.getParsedMobile());
        sdcSmsSubmit.setMsgType(freeorpremium);
        sdcSmsSubmit.setMsgBody(msgText);
        sdcSmsSubmit.setCost(club.getPrice());
        sdcSmsSubmit.setMsgCode1(senderReference);
        sdcSmsSubmit.setNetworkCode(clubuser.getNetworkCode());
        sdcSmsSubmit.setClubUnique(clubuser.getClubUnique());
        sdcSmsSubmit.setStatus(status);
        sdcSmsSubmit.setSmsAccount("ukimi");
        umesmsdao.log(sdcSmsSubmit);    
       //================ Loggint to smsMsgLog END ================= 
        
        if(billable) {
          quizsmsdao.log(sdcSmsSubmit);
          if(clubDetails.getServiceType().equalsIgnoreCase("competition")){
                    		QuizUserAttempted quizUserAttempted=new QuizUserAttempted();
                    		quizUserAttempted.setaParsedMsisdn(clubuser.getParsedMobile());
                    		quizUserAttempted.setClubUnique(club.getUnique());
                    		quizUserAttempted.setType("Entry Confirmation");
                    		quizUserAttempted.setStatus("false");
                    		quizUserAttempted.setaUnique(smsparameter.getTransactionId());
                    		quizUserAttempted.setaCreated(new Date());
                    		umequizdao.saveQuizUserAttempted(quizUserAttempted);
        }
                                
        }
        return sent;
    }
    
    
}
