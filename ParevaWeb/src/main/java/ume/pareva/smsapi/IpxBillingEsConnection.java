package ume.pareva.smsapi;

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

public class IpxBillingEsConnection implements Runnable, ISmsExtension {

    public boolean useBackupCon = false;

    private SdcSmsGateway gw;

    private FifoQueue requestBox;
    private FifoQueue responseBox;
    private IpxBillingEsSubmit sms;

    private String tName = "";

    private URL anIpxUrl;
    private SubscriptionApiPort aSubscriptionPort;
//    private ChargingApiPort aChargingPort;

    public IpxBillingEsConnection() { // for UME Daemons
        this.gw = new SdcSmsGateway();
        gw.setLogin("umelimDC-es");
        gw.setPassword("FBYuhj21");
        requestBox = new FifoQueue(1);
        responseBox = new FifoQueue(1);
    }

    public IpxBillingEsConnection(SdcSmsGateway gw) {
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
        IpxBillingEsConnection smscon = new IpxBillingEsConnection(gw);
        smscon.settName("SingleIpxBillingEsConnection");
        return smscon.doRequest((IpxBillingEsSubmit) sms);
    }

    public void run() {

        Thread thisThread = Thread.currentThread();
        settName(thisThread.getName());
        while (true) {
            try {
                sms = (IpxBillingEsSubmit) requestBox.remove(40000);
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

    public String doRequest(IpxBillingEsSubmit sms) {
        String res = "";
        res = capturePaymentSubscription(sms);
        return res;
    }

    /*
     * Function capture payment charging per download for TIM and WIND
     */
    /*
     * Function capture payment subscription
     */
    public String capturePaymentSubscription(IpxBillingEsSubmit sms) {
        String res = "";
        try {

            System.out.println(" IPX capturePaymentSubscription spain");

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
            if (!sms.getOperator().equals("airtel")) {
                anAuthorizeRequest.setServiceMetaData(sms.getServiceMetaData());
            }
            System.out.println("IPX billingConnection serviceMetaData: " + anAuthorizeRequest.getServiceMetaData());

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
            aCaptureRequest.setUsername(sms.getUsername());
            aCaptureRequest.setPassword(sms.getPassword());

            if (sms.getUsername().equals("") || sms.getPassword().equals("")) {
                aCaptureRequest.setUsername(gw.getLogin());
                aCaptureRequest.setPassword(gw.getPassword());
            }

            CapturePaymentResponse aCaptureResponse = this.aSubscriptionPort
                    .capturePayment(aCaptureRequest);

            String SuccessfulStatus = "";

            SuccessfulStatus = "Charged Successfully--";

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
