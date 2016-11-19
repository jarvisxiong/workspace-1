package ume.pareva.smsapi;

import com.ipx.www.api.services.smsapi52.SmsApiPort;
import com.ipx.www.api.services.smsapi52.SmsApiServiceLocator;
import com.ipx.www.api.services.smsapi52.types.SendRequest;
import com.ipx.www.api.services.smsapi52.types.SendResponse;

import ume.pareva.dao.FifoQueue;
import ume.pareva.dao.SdcSmsResp;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.pojo.SdcSmsGateway;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.SdcMisc;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;

public class IpxSmsConnection implements Runnable, ISmsExtension  {
	
    @Autowired
    UmeSmsDao umesmsdao;
	
    public boolean useBackupCon = false;
	
    private SdcSmsGateway gw;

				
	private FifoQueue requestBox;
	private FifoQueue responseBox;
	private IpxSmsSubmit sms;
		
	private String tName = "";
	
        public IpxSmsConnection() { // for UME Daemons
            this.gw = new SdcSmsGateway();
            gw.setLogin("universalmob-it");
            gw.setPassword("987OLIpt5r");
            // gw.setMsisdnFormat(4);
            requestBox = new FifoQueue(1);
            responseBox = new FifoQueue(1);
	}
        
	public IpxSmsConnection(SdcSmsGateway gw) {
            //System.out.println("----------- 1 --------------");
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
            //System.out.println("----------- 4 --------------");
            IpxSmsConnection smscon = new IpxSmsConnection(gw);
            smscon.tName = "SingleIpxConnection";
            return smscon.doRequest((IpxSmsSubmit)sms);
	}
	
	public void run() {

            Thread thisThread = Thread.currentThread();
            tName = thisThread.getName();
            //System.out.println("----------- 5 --------------");

            while (true) {
                try {
                    sms = (IpxSmsSubmit) requestBox.remove(40000);
                    responseBox.add(doRequest(sms));
                }
                catch (InterruptedException e) {
                    sms = null;
                }
                catch (ClassCastException e) {
                    try { responseBox.add(SdcSmsResp.get(18)); } catch (InterruptedException ec) {}
                    sms = null;                    
                }
            }
	}
	
	public String doRequest(IpxSmsSubmit sms) {

            String res = "";
//            String logUnique = "";
            
            System.out.println("Sending SMS in IpxSmsConnection..: " + gw.getLogin() + ": " + gw.getPassword());
            
            sms.setToNumber(SdcMisc.parseMobileNumber(sms.getToNumber(), gw.getMsisdnFormat()));
            
            if (sms.getTariffClass()==0 && sms.getCurrencyCode().equals("")) sms.setCurrencyCode("EUR");

//            try{
//                String messagelogUnique = umesmsdao.log(sms);
//                System.out.println("Message IPXSmsConnection sent: " + messagelogUnique);
//            }catch(Exception e){
//                System.out.println("Message IPXSmsConnection send exception : " + e.getMessage());
//                e.printStackTrace();
//            }            
            try {
//                logUnique = SdcSmsDao.log((SdcSmsSubmit)sms);
//            	SdcSmsDao.log((SdcSmsSubmit)sms);
                //TODO ASK MR. MADAN
                //umesmsdao.log(sms);
                SmsApiPort soap = (new SmsApiServiceLocator()).getSmsApi52();
                SendRequest req = new SendRequest();
                if (SdcMisc.validateNumbers(sms.getFromNumber())) req.setOriginatorTON(0);
                else req.setOriginatorTON(1);
                
                req.setCorrelationId(sms.getLogUnique());
                req.setOriginatingAddress(sms.getFromNumber());                
                req.setDestinationAddress(sms.getToNumber());
                req.setTariffClass(sms.getCurrencyCode().toUpperCase() + (int)sms.getTariffClass());

                req.setUsername(sms.getUsername());
                req.setPassword(sms.getPassword());
                
                if(sms.getUsername().equals("") || sms.getPassword().equals("")){
                  req.setUsername(gw.getLogin());
                  req.setPassword(gw.getPassword());
                }
                	                                
                req.setUserData(sms.getMsgBody());
                req.setUserDataHeader(sms.getUserDataHeader());
                req.setDeliveryTime("");
                req.setAccountName("");
                req.setVAT(-1);
                req.setReferenceId("");
                req.setServiceName("");
                req.setServiceCategory("");
                req.setServiceMetaData("");
                req.setCampaignName("");
                
                if(!sms.getReferenceID().equals(""))
                    req.setReferenceId(sms.getReferenceID());
                if(!sms.getServiceCategory().equals(""))
                    req.setServiceCategory(sms.getServiceCategory());
                
                printReq(req);
                
                SendResponse resp = soap.send(req);    
                
                if(resp.getReasonCode()==0 && resp.getMessageId()!=null && !resp.getMessageId().equals("")){
                    res = "successful--" + resp.getMessageId();
                }else{
                    res = "failed--" + resp.getResponseMessage();
                }
                
                printResp(resp);
                return res;
            }
            catch (Exception e) { System.out.println(tName + ": " + e); res = "failed--" + e.getMessage(); }

            return res;
	}
        
        public String doRequest(IpxSmsSubmit sms, int reportFlag) {

            String res = "";
//            String logUnique = "";
            
            System.out.println("Sending SMS in IpxSmsConnection..: " + gw.getLogin() + ": " + gw.getPassword());
            
            sms.setToNumber(SdcMisc.parseMobileNumber(sms.getToNumber(), gw.getMsisdnFormat()));
            
            if (sms.getTariffClass()==0 && sms.getCurrencyCode().equals("")) sms.setCurrencyCode("EUR");

//            try{
//                String messagelogUnique = umesmsdao.log(sms);
//                System.out.println("Message IPXSmsConnection sent: " + messagelogUnique);
//            }catch(Exception e){
//                System.out.println("Message IPXSmsConnection send exception : " + e.getMessage());
//                e.printStackTrace();
//            }            
            try {
//                logUnique = SdcSmsDao.log((SdcSmsSubmit)sms);
//            	SdcSmsDao.log((SdcSmsSubmit)sms);
                //TODO ASK MR. MADAN
                //umesmsdao.log(sms);
                SmsApiPort soap = (new SmsApiServiceLocator()).getSmsApi52();
                SendRequest req = new SendRequest();
                if (SdcMisc.validateNumbers(sms.getFromNumber())) req.setOriginatorTON(0);
                else req.setOriginatorTON(1);
                
                req.setCorrelationId(sms.getLogUnique());
                req.setOriginatingAddress(sms.getFromNumber());                
                req.setDestinationAddress(sms.getToNumber());
                req.setTariffClass(sms.getCurrencyCode().toUpperCase() + (int)sms.getTariffClass());

                req.setUsername(sms.getUsername());
                req.setPassword(sms.getPassword());
                
                if(sms.getUsername().equals("") || sms.getPassword().equals("")){
                  req.setUsername(gw.getLogin());
                  req.setPassword(gw.getPassword());
                }
                	                                
                req.setUserData(sms.getMsgBody());
                req.setUserDataHeader(sms.getUserDataHeader());
                req.setDeliveryTime("");
                req.setAccountName("");
                req.setVAT(-1);
                req.setReferenceId("");
                req.setServiceName("");
                req.setServiceCategory("");
                req.setServiceMetaData(sms.getServiceMetaData());
                req.setCampaignName("");
                req.setStatusReportFlags(reportFlag);
                
                if(!sms.getReferenceID().equals(""))
                    req.setReferenceId(sms.getReferenceID());
                if(!sms.getServiceCategory().equals(""))
                    req.setServiceCategory(sms.getServiceCategory());
                
                printReq(req);
                
                SendResponse resp = soap.send(req);    
                
                if(resp.getReasonCode()==0 && resp.getMessageId()!=null && !resp.getMessageId().equals("")){
                    res = "successful--" + resp.getMessageId();
                }else{
                    res = "failed--" + resp.getResponseMessage();
                }
                
                printResp(resp);
                return res;
            }
            catch (Exception e) { System.out.println(tName + ": " + e); res = "failed--" + e.getMessage(); }

            return res;
	}
        
        private static void printReq(SendRequest req) {
            
            System.out.println("---------------------------- IpxSmsConnection REQ ----------------------------");
            System.out.println("CorrelationId: " + req.getCorrelationId());
            System.out.println("OriginatingAddress: " + req.getOriginatingAddress());
            System.out.println("OriginatorTON: " + req.getOriginatorTON());
            System.out.println("DestinationAddress: " + req.getDestinationAddress());
            System.out.println("UserData: " + req.getUserData());
            System.out.println("UserDataHeader: " + req.getUserDataHeader());
            System.out.println("DCS: " + req.getDCS());
            System.out.println("PID: " + req.getPID());
            System.out.println("RelativeValidityTime: " + req.getRelativeValidityTime());
            System.out.println("DeliveryTime: " + req.getDeliveryTime());
            System.out.println("StatusReportFlags: " + req.getStatusReportFlags());
            System.out.println("AccountName: " + req.getAccountName());            
            System.out.println("TariffClas: " + req.getTariffClass());
            System.out.println("VAT: " + req.getVAT());
            System.out.println("ReferenceId: " + req.getReferenceId());
            System.out.println("ServiceName: " + req.getServiceName());
            System.out.println("ServiceCategory: " + req.getServiceCategory());
            System.out.println("ServiceMetaData: " + req.getServiceMetaData());
            System.out.println("CampaignName: " + req.getCampaignName());            
            System.out.println("Username: " + req.getUsername());
            System.out.println("Password: " + req.getPassword());
            System.out.println("---------------------------- IpxSmsConnection REQ ----------------------------");
        }
        
        private static void printResp(SendResponse resp) {
            
            System.out.println("---------------------------- IpxSmsConnection RESP ----------------------------");
            System.out.println("CorrelationId: " + resp.getCorrelationId());
            System.out.println("MessageId: " + resp.getMessageId());
            System.out.println("ResponseCode: " + resp.getResponseCode());
            System.out.println("ReasonCode: " + resp.getReasonCode());
            System.out.println("ResponseMessage: " + resp.getResponseMessage());
            System.out.println("TemporaryError: " + resp.isTemporaryError());
            System.out.println("BillingStatus: " + resp.getBillingStatus());
            System.out.println("VAT: " + resp.getVAT());
            System.out.println("---------------------------- IpxSmsConnection RESP ----------------------------");
        }
}
