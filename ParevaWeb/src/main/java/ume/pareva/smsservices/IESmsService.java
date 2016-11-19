package ume.pareva.smsservices;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.dao.QuizSmsDao;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeQuizDao;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.ire.IREConnConstants;
import ume.pareva.pojo.QuizUserAttempted;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.util.httpconnection.HttpURLConnectionWrapper;

/**
 *
 * @author madan
 */

@Component("IE_SMS")
public class IESmsService {
     private static final Logger logger = LogManager.getLogger(IESmsService.class.getName());
    
    @Autowired
    UmeSmsDao umesmsdao;
    
    @Autowired
    UmeQuizDao umequizdao;
    
    @Autowired
    QuizSmsDao quizsmsdao;
    
    
    
    public boolean sendMessage(String shortCode,UmeUser user, SdcMobileClubUser clubuser, String msgText, MobileClub club, UmeClubDetails userclubdetails, String msgType, boolean billable,String network,String reqType,String dateofweek,String senderReference,boolean wappush){
        boolean sent=false;
        /**
         * $req = 'reply=0'; $req .= '&id='.uniqid(); $req .=
         * '&number='.$number; $req .= '&network=INTERNATIONAL'; $req .=
         * '&message='.$msg; $req .= '&value=0'; $req .= '&currency=GBP'; $req
         * .= '&cc='.$company; $req .= '&title='.$title; $req .= '&ekey='.$ekey;
         * http://client.txtnation.com/gateway.php?reply=0&id=123456&number=353851485271
         * &network=international&value=0&currency=EUR&cc=moonlight&ekey=a6815e707c675f7a3f307656d462bca6&message=Your+message
         */
        String irehttp = "http://client.txtnation.com/gateway.php";
        String id=Misc.generateUniqueId()+"-"+club.getUnique();
        
        String costvalue;
        
        String freeorpremium;//="Free";
        String status="SENT";
        
         if(billable){ 
            freeorpremium="Premium";
            costvalue="2.50";
        }
         else{
             freeorpremium="Free";
             costvalue="0";
         }
        
        String ekey=club.getOtpServiceName();//"a6815e707c675f7a3f307656d462bca6";
    String msg=msgText;
    System.out.println("iemomessage  BEFORE changing "+msg);
     if (msgText.contains("####")) {
            String encrypt = "Q2W" + MiscCr.encrypt(clubuser.getParsedMobile()).substring(0, 5);            
            msgText = msgText.replace("####", encrypt); //SdcMisc.generateLogin(5));
            
        }
        try {

            msg = java.net.URLEncoder.encode(msgText, "utf-8");
        } catch (Exception e) {
        }
        String cc="ume";
        try{
        cc=userclubdetails.getCompanyCode();
        }catch(Exception e) {cc="ume";}
        
        HttpURLConnectionWrapper urlwrapper = urlwrapper = new HttpURLConnectionWrapper(IREConnConstants.getDomainHttp());
        Map<String, String> ireMap = new HashMap<String, String>();
        
        ireMap.put("reply", "0");
        ireMap.put("id", id);
        ireMap.put("number", clubuser.getParsedMobile());
        ireMap.put("network", network);
        //ireMap.put("value","0");
        ireMap.put("value", costvalue);
        ireMap.put("currency", "EUR");
        ireMap.put("cc",cc);
        ireMap.put("ekey", ekey);
        System.out.println("iemomessage SENDING  " + msg);
        ireMap.put("message", msg);
        ireMap.put("title", shortCode);

        if (wappush) {
            ireMap.put("wappush", "1");
        }
        urlwrapper.wrapGet(ireMap);

        String responsecode = urlwrapper.getResponseCode();
        String responsedesc = urlwrapper.getResponseContent();
        boolean isSuccessful = urlwrapper.isSuccessful();
        
        if(isSuccessful){
            SdcSmsSubmit sdcSmsSubmit = new SdcSmsSubmit();

            sdcSmsSubmit.setUnique(id);
            sdcSmsSubmit.setFromNumber(shortCode);
            sdcSmsSubmit.setToNumber(clubuser.getParsedMobile());
            sdcSmsSubmit.setMsgType(freeorpremium);
            sdcSmsSubmit.setMsgBody(msgText);
            sdcSmsSubmit.setCost(club.getPrice());
            sdcSmsSubmit.setMsgCode1(senderReference);
            sdcSmsSubmit.setReqType(reqType);
            sdcSmsSubmit.setLogUnique(id);
            sdcSmsSubmit.setNetworkCode(network);
            sdcSmsSubmit.setMsgUdh(dateofweek);
            sdcSmsSubmit.setClubUnique(club.getUnique());
            sdcSmsSubmit.setSmsAccount(cc);
            umesmsdao.log(sdcSmsSubmit);
            
            if(billable){
            quizsmsdao.log(sdcSmsSubmit);
            if(userclubdetails.getServiceType().equalsIgnoreCase("competition")){
            QuizUserAttempted quizUserAttempted = new QuizUserAttempted();
            quizUserAttempted.setaParsedMsisdn(clubuser.getParsedMobile());
            quizUserAttempted.setClubUnique(club.getUnique());
            quizUserAttempted.setType("Entry Confirmation");
            quizUserAttempted.setStatus("false");
            quizUserAttempted.setaUnique(id);
            quizUserAttempted.setaCreated(new Date());
            umequizdao.saveQuizUserAttempted(quizUserAttempted);
            }
            }
            
        }
        
        
        
        
        return sent;
    }
    
}
