/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.it;

//import com.ipx.www.api.services.subscriptionapi31.SubscriptionApiBindingStub;
//import com.ipx.www.api.services.subscriptionapi31.SubscriptionApiPort;
//import com.ipx.www.api.services.subscriptionapi31.SubscriptionApiServiceLocator;
//import com.ipx.www.api.services.subscriptionapi31.types.TerminateSubscriptionRequest;
//import com.ipx.www.api.services.subscriptionapi31.types.TerminateSubscriptionResponse;

import com.google.common.base.Strings;
import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiBindingStub;
import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiPort;
import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiServiceLocator;
import com.ipx.www.api.services.subscriptionapi40.types.TerminateSubscriptionRequest;
import com.ipx.www.api.services.subscriptionapi40.types.TerminateSubscriptionResponse;

import java.net.URL;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import ume.pareva.cms.MobileClub;
import ume.pareva.dao.PassiveVisitorDao;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.pojo.PassiveVisitor;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeClubMessages;
import ume.pareva.smsapi.IpxSmsConnection;
import ume.pareva.smsapi.IpxSmsSubmit;

/**
 *
 * @author trung
 */

@Component("ipxitsimplefunction")
public class IpxItSimpleFunction {
    @Autowired
    private UmeSmsDao umesmsdao;
    @Autowired
    PassiveVisitorDao passivevisitordao;
//    public void callTerminateIT_DOI_OLD(SdcMobileClubUser clubUser, MobileClub club) {
//        try {
//            URL anIpxSubUrl = new URL("http://europe.ipx.com/api/services2/SubscriptionApi31?wsdl");
//            SubscriptionApiPort aPort = new SubscriptionApiServiceLocator().getSubscriptionApi31(anIpxSubUrl);
//            // Set read timeout to 10 minute            
//            ((SubscriptionApiBindingStub) aPort).setTimeout(1 * 60 * 1000);
//            TerminateSubscriptionRequest aTerminateRequest = new TerminateSubscriptionRequest();
//            aTerminateRequest.setCorrelationId("universalmob");
//            aTerminateRequest.setConsumerId(clubUser.getParsedMobile());
//            aTerminateRequest.setSubscriptionId(clubUser.getParam2());
//            aTerminateRequest.setUsername(club.getOtpSoneraId());
//            aTerminateRequest.setPassword(club.getOtpTelefiId());
//            TerminateSubscriptionResponse aTerminateResponse = aPort.terminateSubscription(aTerminateRequest);
//            System.out.println("IPX Terminate result: responseCode: " + aTerminateResponse.getResponseCode());
//            System.out.println("IPX Terminate result: responseMessage: " + aTerminateResponse.getResponseMessage());
//        } catch (Exception e) {
//            System.out.println("callTerminateIT_DOI exception: ");
//            e.printStackTrace();
//        }
//    }
    
    public void checkPassiveVisitor(String msisdn, MobileClub club, String campaignId, String landingPage, String pubId){
        boolean exist=passivevisitordao.exists(msisdn, club.getUnique());
            if(!exist){
                PassiveVisitor visitor=new PassiveVisitor();
                visitor.setUnique(SdcMisc.generateUniqueId());
                visitor.setClubUnique(club.getUnique());
                visitor.setFollowUpFlag(0);
                visitor.setParsedMobile(msisdn);
                visitor.setStatus(0);
                visitor.setCreated(new Date());
                visitor.setCampaign(campaignId);
                visitor.setLandignPage(landingPage);
                visitor.setPubId(pubId);
                passivevisitordao.insertPassiveVisitor(visitor);
        }
    }
    
    public void updatePassiveVisitor(String msisdn, MobileClub club){
            PassiveVisitor visitor=passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
            if(visitor!=null){
                visitor.setFollowUpFlag(1);
                visitor.setStatus(1);
                passivevisitordao.updatePassiveVisitor(visitor);
            }
    }
    
    public void callTerminateIT_DOI(SdcMobileClubUser clubUser, MobileClub club, String stopMessage) {
        try {
            URL anIpxSubUrl = new URL("http://europe.ipx.com/api/services2/SubscriptionApi40?wsdl");
            SubscriptionApiPort aPort = new SubscriptionApiServiceLocator().getSubscriptionApi40(anIpxSubUrl);
            // Set read timeout to 10 minute            
            ((SubscriptionApiBindingStub) aPort).setTimeout(1 * 60 * 1000);
            TerminateSubscriptionRequest aTerminateRequest = new TerminateSubscriptionRequest();
            aTerminateRequest.setCorrelationId("universalmob");
            aTerminateRequest.setConsumerId(clubUser.getParsedMobile());
            aTerminateRequest.setSubscriptionId(clubUser.getParam2());
            aTerminateRequest.setUsername(club.getOtpSoneraId());
            aTerminateRequest.setPassword(club.getOtpTelefiId());
            TerminateSubscriptionResponse aTerminateResponse = aPort.terminateSubscription(aTerminateRequest);
            System.out.println("IPX Terminate result: responseCode: " + aTerminateResponse.getResponseCode());
            System.out.println("IPX Terminate result: responseMessage: " + aTerminateResponse.getResponseMessage());
        } catch (Exception e) {
            System.out.println("callTerminateIT_DOI exception: ");
            e.printStackTrace();
        }
        
        try {
//            if (clubUser.getNetworkCode().equals("tim")) {
            requestSendTerminateSms(club, clubUser, stopMessage);
//            }
        } catch (Exception e) {
            System.out.println("callTerminateIT_DOI exception: " + e);
            e.printStackTrace();
        }
    }
    
    public void requestSendTerminateSms(MobileClub club, SdcMobileClubUser clubUser, String stopMessage) {
        IpxSmsSubmit sms = new IpxSmsSubmit();
        IpxSmsConnection smsConnection = new IpxSmsConnection();
        String msg = stopMessage;

        // This handle code fixed
        if(Strings.isNullOrEmpty(msg))
            msg = club.getStopSms();
        
//        sms.setReferenceID(clubUser.getParam2());
        sms.setSmsAccount("ipx");
        sms.setUsername(club.getOtpSoneraId());
        sms.setPassword(club.getOtpTelefiId());
        sms.setToNumber(clubUser.getParsedMobile());
        sms.setMsgBody(msg);
        sms.setCurrencyCode("EUR");
        if (clubUser.getNetworkCode().equals("tim")) {
            sms.setFromNumber("3399942323");
            sms.setServiceCategory("INFORMATION");
            String resp = smsConnection.doRequest(sms);
        }
        try{
            String messagelogUnique = umesmsdao.log(sms);
        }catch(Exception e){
        }

    }
}
