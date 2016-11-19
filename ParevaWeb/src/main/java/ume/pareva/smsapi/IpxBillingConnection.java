package ume.pareva.smsapi;

import com.ipx.www.api.services.chargingapi11.ChargingApiBindingStub;
import com.ipx.www.api.services.chargingapi11.ChargingApiPort;
import com.ipx.www.api.services.chargingapi11.ChargingApiServiceLocator;
import com.ipx.www.api.services.chargingapi11.types.AuthorizeRequest;
import com.ipx.www.api.services.chargingapi11.types.AuthorizeResponse;
import com.ipx.www.api.services.chargingapi11.types.CaptureRequest;
import com.ipx.www.api.services.chargingapi11.types.CaptureResponse;
import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiBindingStub;
import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiPort;
import com.ipx.www.api.services.subscriptionapi40.SubscriptionApiServiceLocator;
import com.ipx.www.api.services.subscriptionapi40.types.AuthorizePaymentRequest;
import com.ipx.www.api.services.subscriptionapi40.types.AuthorizePaymentResponse;
import com.ipx.www.api.services.subscriptionapi40.types.CapturePaymentRequest;
import com.ipx.www.api.services.subscriptionapi40.types.CapturePaymentResponse;

import ume.pareva.dao.FifoQueue;
import ume.pareva.dao.SdcSmsResp;
import ume.pareva.pojo.SdcSmsGateway;
import ume.pareva.dao.SdcSmsSubmit;

import java.net.URL;

public class IpxBillingConnection implements Runnable, ISmsExtension {

    public boolean useBackupCon = false;

    private SdcSmsGateway gw;

    private FifoQueue requestBox;
    private FifoQueue responseBox;
    private IpxBillingSubmit sms;

    private String tName = "";

    private URL anIpxUrl;
    private SubscriptionApiPort aSubscriptionPort;
    private ChargingApiPort aChargingPort;

    public IpxBillingConnection() { // for UME Daemons
        this.gw = new SdcSmsGateway();
        gw.setLogin("universalmob-it");
        gw.setPassword("987OLIpt5r");
        // gw.setMsisdnFormat(4);
        requestBox = new FifoQueue(1);
        responseBox = new FifoQueue(1);
    }

    public IpxBillingConnection(SdcSmsGateway gw) {
        this.gw = gw;
        requestBox = new FifoQueue(1);
        responseBox = new FifoQueue(1);
    }

    public synchronized void handOff(Object obj) throws InterruptedException {
        requestBox.add(obj);
    }

    public synchronized String getResponse() throws InterruptedException {
        return (String) responseBox.remove();
    }

    public static String doSingleRequest(SdcSmsSubmit sms, SdcSmsGateway gw) {
        IpxBillingConnection smscon = new IpxBillingConnection(gw);
        smscon.settName("SingleIpxBillingConnection");
        return smscon.doRequest((IpxBillingSubmit) sms);
    }

    public void run() {

        Thread thisThread = Thread.currentThread();
        settName(thisThread.getName());
        while (true) {
            try {
                sms = (IpxBillingSubmit) requestBox.remove(40000);
                responseBox.add(doRequest(sms));
            } catch (InterruptedException e) {
                sms = null;
            } catch (ClassCastException e) {
                try {
                    responseBox.add(SdcSmsResp.get(18));
                } catch (InterruptedException ec) {
                }
                sms = null;
            } // catch anIpxUrl and aPort exception
            catch (Exception e) {
                sms = null;
            }
        }
    }

    public String doRequest(IpxBillingSubmit sms) {
        String res = "";
//		System.out.println("Sending SMS in IpxBillingConnection..: "
//				+ gw.getLogin() + ": " + gw.getPassword());

        if (!sms.getOperator().equals("vodafone") && sms.getTariffClass() == 240) {
            res = onDemandCharging(sms);
        } else {
            res = capturePaymentSubscription(sms);
        }

        return res;
    }

    /*
     * Function capture payment charging per download for TIM and WIND
     */
    public String onDemandCharging(IpxBillingSubmit sms) {
        String res = "";
        try {

            // ** initialize aPort
            anIpxUrl = new URL("http://europe.ipx.com/api/services2/ChargingApi11?wsdl");
            aChargingPort = new ChargingApiServiceLocator().getChargingApi11(anIpxUrl);
            // Set timeout to 1 minute
            ((ChargingApiBindingStub) aChargingPort).setTimeout(1 * 60 * 1000);

            AuthorizeRequest aAuthRequest = new AuthorizeRequest();
            aAuthRequest.setCorrelationId(sms.getTransactionId());
            aAuthRequest.setConsumerId(sms.getConsumerId());
            aAuthRequest.setReferenceId(sms.getTransactionId());
            aAuthRequest.setCampaignName(sms.getCampaignName());
            aAuthRequest.setServiceCategory("#NULL#");

            if (sms.getNetworkCode().toLowerCase().equals("tim")) {
                aAuthRequest.setServiceCategory("VIDEO");
            }

            aAuthRequest.setServiceMetaData(sms.getServiceMetaData());
            aAuthRequest.setServiceName(sms.getServiceName());
            aAuthRequest.setServiceId(sms.getServiceId());
            aAuthRequest.setTariffClass("EUR240");
            aAuthRequest.setVAT(-1);

            aAuthRequest.setUsername(sms.getUsername());
            aAuthRequest.setPassword(sms.getPassword());

            if (sms.getUsername().equals("") || sms.getPassword().equals("")) {
                aAuthRequest.setUsername(gw.getLogin());
                aAuthRequest.setPassword(gw.getPassword());
            }

            System.out.println("Ipx Charging onDemand CorrelationId: " + aAuthRequest.getCorrelationId());
            System.out.println("Ipx Charging onDemand ConsumerId: " + aAuthRequest.getConsumerId());
            System.out.println("Ipx Charging onDemand ReferenceId: " + aAuthRequest.getReferenceId());
            System.out.println("Ipx Charging onDemand ServiceCategory: " + aAuthRequest.getServiceCategory());
            System.out.println("Ipx Charging onDemand CampaignName: " + aAuthRequest.getCampaignName());
            System.out.println("Ipx Charging onDemand ServiceMetaData: " + aAuthRequest.getServiceMetaData());
            System.out.println("Ipx Charging onDemand ServiceName: " + aAuthRequest.getServiceName());
            System.out.println("Ipx Charging onDemand ServiceId: " + aAuthRequest.getServiceId());
            System.out.println("Ipx Charging onDemand TariffClass: " + aAuthRequest.getTariffClass());
            System.out.println("Ipx Charging onDemand Username: " + aAuthRequest.getUsername());
            System.out.println("Ipx Charging onDemand Password: " + aAuthRequest.getPassword());
            System.out.println("Ipx Charging onDemand VAT: " + aAuthRequest.getVAT());

            // Invoke web service
            AuthorizeResponse aAuthResponse = aChargingPort.authorize(aAuthRequest);

            // If the API invokation failed, we have no valid session to capture
            if (aAuthResponse.getResponseCode() != 0) {
                System.out.println(" IPX Charging onDemand Authorization failed: "
                        + aAuthResponse.getResponseMessage());
                res = "Ipx Charging onDemand Authorized failed--"
                        + aAuthResponse.getResponseCode() + "--"
                        + sms.getConsumerId() + "--"
                        + aAuthResponse.getSessionId() + "--"
                        + aAuthResponse.getResponseMessage();
                return res;
            }

            CaptureRequest aCapRequest = new CaptureRequest();
            aCapRequest.setCorrelationId(aAuthResponse.getCorrelationId());
            aCapRequest.setSessionId(aAuthResponse.getSessionId());

            aCapRequest.setUsername(sms.getUsername());
            aCapRequest.setPassword(sms.getPassword());

            if (sms.getUsername().equals("") || sms.getPassword().equals("")) {
                aCapRequest.setUsername(gw.getLogin());
                aCapRequest.setPassword(gw.getPassword());
            }

            // Invoke web service
            CaptureResponse aCapResponse = aChargingPort.capture(aCapRequest);

            res = "Failed--" + aCapResponse.getResponseCode() + "--"
                    + sms.getConsumerId() + "--"
                    + aCapResponse.getTransactionId() + "--"
                    + aCapResponse.getResponseMessage();
            // If the API invokation failed, capture of the payment failed
            if (aCapResponse.getResponseCode() == 0 && aCapResponse.getBillingStatus() == 2) {
                System.out.println("Ipx Charging onDemand Capture Payment successful");
                res = "Successful--"
                        + aCapResponse.getResponseCode() + "--"
                        + sms.getConsumerId() + "--"
                        + aCapResponse.getTransactionId() + "--"
                        + aCapResponse.getResponseMessage();
            } else {
                System.out.println("Ipx Charging onDemand Capture Payment failed");
            }

        } catch (Exception e) {
            res = "Ipx Charging onDemand Exception: " + e.getMessage() + "--999--"
                    + sms.getConsumerId() + "--" + sms.getTransactionId()
                    + "--" + "IPX Charging onDemand exception error";
        }
        return res;
    }

    /*
     * Function capture payment subscription
     */
    public String capturePaymentSubscription(IpxBillingSubmit sms) {
        String res = "";
        try {
            System.out.println(" IPX capturePaymentSubscription italy");

            // ** initialize aPort
//            anIpxUrl = new URL(
//                    "http://europe.ipx.com/api/services2/SubscriptionApi31?wsdl");
//            aSubscriptionPort = new SubscriptionApiServiceLocator()
//                    .getSubscriptionApi31(anIpxUrl);
//            ((SubscriptionApiBindingStub) aSubscriptionPort).setTimeout(10 * 60 * 1000);
//            // ** Authorize a payment for the subscription
//            AuthorizePaymentRequest anAuthorizeRequest = new AuthorizePaymentRequest();

                        // ** initialize aPort
            anIpxUrl = new URL(
                    "http://europe.ipx.com/api/services2/SubscriptionApi40?wsdl");
            aSubscriptionPort = new SubscriptionApiServiceLocator()
                    .getSubscriptionApi40(anIpxUrl);
            ((SubscriptionApiBindingStub) aSubscriptionPort).setTimeout(10 * 60 * 1000);
            // ** Authorize a payment for the subscription
            AuthorizePaymentRequest anAuthorizeRequest = new AuthorizePaymentRequest();
            
            anAuthorizeRequest.setCorrelationId(sms.getTransactionId());
            anAuthorizeRequest.setSubscriptionId(sms.getSubscriptionId());
            anAuthorizeRequest.setConsumerId(sms.getConsumerId());
            anAuthorizeRequest.setServiceMetaData("#NULL#");

            //if (sms.getOperator().equals("wind") || sms.getOperator().equals("tim") || sms.getOperator().equals("three"))
            if (!sms.getOperator().equals("vodafone")) {
                anAuthorizeRequest.setServiceMetaData(sms.getServiceMetaData());
            }
            System.out.println("IPX billingConnection serviceMetaData: " + anAuthorizeRequest.getServiceMetaData());
//			anAuthorizeRequest.setServiceMetaData("service=em");
//			anAuthorizeRequest.setUsername(gw.getLogin());
//			anAuthorizeRequest.setPassword(gw.getPassword());
            anAuthorizeRequest.setUsername(sms.getUsername());
            anAuthorizeRequest.setPassword(sms.getPassword());

            if (sms.getUsername().equals("") || sms.getPassword().equals("")) {
                anAuthorizeRequest.setUsername(gw.getLogin());
                anAuthorizeRequest.setPassword(gw.getPassword());
            }

            System.out.println(" IPX Authorization of payment failed: " + sms.getServiceMetaData());

            AuthorizePaymentResponse anAuthorizeResponse = this.aSubscriptionPort
                    .authorizePayment(anAuthorizeRequest);

            // If the API invokation failed, we have no valid session to capture
            if (anAuthorizeResponse.getResponseCode() != 0) {
                System.out.println(" IPX Authorization of payment failed: "
                        + anAuthorizeResponse.getResponseMessage());
                res = "Ipx Billing Authorized failed--"
                        + anAuthorizeResponse.getResponseCode() + "--"
                        + sms.getSubscriptionId() + "--"
                        + sms.getTransactionId() + "--"
                        + anAuthorizeResponse.getResponseMessage();
                return res;
            }

            // ** Capture the payment for the subscription
            CapturePaymentRequest aCaptureRequest = new CapturePaymentRequest();
            aCaptureRequest.setCorrelationId(sms.getTransactionId());
            aCaptureRequest.setSessionId(anAuthorizeResponse.getSessionId());

//			aCaptureRequest.setUsername(gw.getLogin());
//			aCaptureRequest.setPassword(gw.getPassword());
            aCaptureRequest.setUsername(sms.getUsername());
            aCaptureRequest.setPassword(sms.getPassword());

            if (sms.getUsername().equals("") || sms.getPassword().equals("")) {
                aCaptureRequest.setUsername(gw.getLogin());
                aCaptureRequest.setPassword(gw.getPassword());
            }

            CapturePaymentResponse aCaptureResponse = this.aSubscriptionPort
                    .capturePayment(aCaptureRequest);

            String SuccessfulStatus = "";

            if (sms.getOperator().equals("vodafone") || sms.getOperator().equals("wind")) {
                SuccessfulStatus = "Successful sent billing attempt IPX--";
            } else {
                SuccessfulStatus = "Charged Successfully--";
            }

//			SuccessfulStatus = "Charged Successfully--";
            res = SuccessfulStatus + aCaptureResponse.getResponseCode() + "--"
                    + sms.getSubscriptionId() + "--"
                    + aCaptureResponse.getTransactionId() + "--"
                    + aCaptureResponse.getResponseMessage();

            // If the API invokation failed, capture of the payment failed
            if (aCaptureResponse.getResponseCode() != 0) {
                System.out.println(" IPX Capture of payment failed");
                res = "Ipx Billing Capture failed--"
                        + aCaptureResponse.getResponseCode() + "--"
                        + sms.getSubscriptionId() + "--"
                        + sms.getTransactionId() + "--"
                        + aCaptureResponse.getResponseMessage();
            }

        } catch (Exception e) {
            res = "Ipx Billing Exception: " + e.getMessage() + "--999--"
                    + sms.getSubscriptionId() + "--" + sms.getTransactionId()
                    + "--" + "IPX billing exception error";
        }
        return res;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }
}
