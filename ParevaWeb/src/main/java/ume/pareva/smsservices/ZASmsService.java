/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.smsservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcSmsGateway;
import ume.pareva.pojo.UmeUser;
import ume.pareva.smsapi.ZaSmsSubmit;

/**
 *
 * @author madan
 */

@Component("ZA_SMS")
public class ZASmsService {
    
    @Autowired
    SmsService smsservice;
    
    public boolean sendMessage(UmeSessionParameters aReq,UmeUser user, SdcMobileClubUser clubuser, String msgText, MobileClub club, UmeClubDetails clubDetails, String msgType, boolean billable){
        boolean sent=true;
        int shortcode=1144;
        String resp = "";
        SdcSmsGateway gw = null;
        if(billable) shortcode=Integer.parseInt(club.getSmsExt());
        
        ZaSmsSubmit zasms = new ZaSmsSubmit(aReq);
        zasms.setSmsAccount("sts");
        zasms.setUmeUser(user);
        zasms.setToNumber(clubuser.getParsedMobile());
        zasms.setFromNumber(shortcode+"");
        zasms.setNetworkCode(clubuser.getNetworkCode());
        zasms.setClubUnique(clubuser.getClubUnique());
        zasms.setMsgBody(msgText);
            try {
                    gw = new SdcSmsGateway();
                    zasms.setCampaignId(shortcode);//(Integer.parseInt(club.getSmsExt())); 
                    gw.setAccounts(zasms.getSmsAccount());
                    gw.setMsisdnFormat(4);
                    resp = smsservice.send(zasms, gw);

                    System.out.println("ZA MESSAGE SENT " + zasms.getMsgBody() + "  " + "Response " + resp + " msisdn: " + zasms.getToNumber());

                } catch (NumberFormatException e) {
                }

        
        return sent;
    }
    
}
