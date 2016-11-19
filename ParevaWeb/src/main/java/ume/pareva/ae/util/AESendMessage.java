/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.ae.util;

import java.io.UnsupportedEncodingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.dao.QuizSmsDao;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.sdk.Misc;
import ume.pareva.util.MessageReplacement;

/**
 *
 * @author madan
 */
@Component("aesendmessage")
public class AESendMessage {

    @Autowired
    QuizSmsDao quizsmsdao;

    @Autowired
    UmeSmsDao umesmsdao;

    @Autowired
    MessageReplacement messagereplace;

    private final Logger logger = LogManager.getLogger(AESendMessage.class.getName());

    public boolean requestAESendSmsTimer(final String msisdn, final String network, final String msgText,
            final String shortCode, final String smsid, final String smsType, final MobileClub club, final UmeClubDetails userClubDetails, final int time, final boolean trickyTime) {
        boolean sent = true;

//        System.out.println("ae compmessage " + msisdn[0] + "  tricky time is " + trickyTime + " Time is " + time + " at " + new Date());
        if (!trickyTime) {
            sent = sendAESMS(msisdn, network, msgText, shortCode, smsid, smsType, club, userClubDetails);
        } else {
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
//                            System.out.println("ie compmessage sending now " + msisdn[0] + "  tricky time is " + trickyTime + " Time is " + time + " at " + new Date());
                            sendAESMS(msisdn, network, msgText, shortCode, smsid, smsType, club, userClubDetails);
                        }
                    }, time);
        }

        return sent;
    }

    //======== SENDING AE MESSAGE =================
    public boolean sendAESMS(String msisdn, String network, String msgText, String shortCode, String smsid, String smsType,
            MobileClub club, UmeClubDetails userClubDetails) {

        boolean success = false;
        if (msgText.equalsIgnoreCase("")) {
            return success;
        } else {
            try {
//            sendSmsFunction
                msgText = messagereplace.replaceMessagePlaceholders(msgText, msisdn, club.getUnique());
                
                String id = Misc.generateUniqueId() + "-" + club.getUnique();
                String response = sendSmsFunction(msisdn, network, msgText, shortCode, id);
                if (!response.isEmpty() && response.equalsIgnoreCase("ok")) {
                    success = true;
                }

                if (success) {
                    String cc = userClubDetails.getCompanyCode();
                    SdcSmsSubmit sdcSmsSubmit = new SdcSmsSubmit();
                    sdcSmsSubmit.setUnique(id);
                    sdcSmsSubmit.setLogUnique(id);
                    sdcSmsSubmit.setFromNumber(shortCode);
                    sdcSmsSubmit.setToNumber(msisdn);
                    sdcSmsSubmit.setMsgType("Free");
                    sdcSmsSubmit.setMsgBody(msgText);
                    sdcSmsSubmit.setReqType(smsType);
                    sdcSmsSubmit.setStatus("SENT");
                    sdcSmsSubmit.setClubUnique(club.getUnique());
                    sdcSmsSubmit.setCost(0);
                    sdcSmsSubmit.setSmsAccount(cc);
                    sdcSmsSubmit.setRefMessageUnique(smsType);
                    sdcSmsSubmit.setMsgCode1("confirm.jsp");
                    umesmsdao.log(sdcSmsSubmit);
                }

            } catch (Exception ex) {
                logger.error(ex);
            }
        }
        return success;
    }

    private String sendSmsFunction(String msisdn, String network, String contentMessage, String shortCode, String logUnique) {
        
        String returnCode = "failed";
        try {
            String url = AppConstants.BASE_URL
                    + "?username=" + AppConstants.USERNAME
                    + "&password=" + AppConstants.PASSWORD
                    + "&receiver=" + msisdn
                    + "&ContentBody=" + java.net.URLEncoder.encode(contentMessage, "UTF-8")
                    + "&originator=" + shortCode
                    + "&countryname=uae"
                    + "&operatorname=" + network
                    + "&contentid=" + logUnique
                    + "&ContentTypeDetails=text"
                    + "&ContentSubType=smsreply&id_application=4193";
            returnCode = ServerConnectController
                    .getUrlPostResponse(url);

            System.out.println("restUrlRequest: " + url);
            System.out.println("The response is\n" + returnCode);
            System.out.println("Description is : "
                    + ServerConnectController.getDescription(returnCode));
        } catch (UnsupportedEncodingException ex) {
            System.out.println("AE sendSmsFunction Excpetion e" + ex.toString());
        }

        return returnCode;
    }
    //==== END SENDING AESMS MESSAGE =================
}
