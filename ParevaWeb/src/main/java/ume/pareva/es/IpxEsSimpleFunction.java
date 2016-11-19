/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.es;

import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiBindingStub;
import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiPort;
import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiServiceLocator;
import com.ipx.www.api.services.subscriptionapi40.types.TerminateSubscriptionRequest;
import com.ipx.www.api.services.subscriptionapi40.types.TerminateSubscriptionResponse;
import java.net.URL;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.pojo.SdcMobileClubUser;

/**
 *
 * @author trung
 */
@Component("ipxessimplefunction")
public class IpxEsSimpleFunction {
    public void callTerminateES_DOI(SdcMobileClubUser clubUser, MobileClub club) {
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
    }
}
