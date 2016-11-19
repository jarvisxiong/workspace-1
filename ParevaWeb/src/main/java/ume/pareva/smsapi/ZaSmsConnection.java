package ume.pareva.smsapi;

import com.za.sms.SmsWSLocator;
import com.za.sms.SmsWSSoap;
import com.zabillingdoi.OnlineBillingLocator;
import com.zabillingdoi.OnlineBillingSoap;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.holders.BigDecimalHolder;
import javax.xml.rpc.holders.BooleanHolder;
import javax.xml.rpc.holders.StringHolder;
import ume.pareva.dao.FifoQueue;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.SdcSmsResp;
import ume.pareva.pojo.SdcSmsGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeSmsDao;

/**
 *
 * @author madan
 */


public class ZaSmsConnection implements Runnable, ISmsExtension  {
    
    @Autowired
    UmeSmsDao umesmsdao;
    
    SdcSmsGateway gw = null;
				
	private FifoQueue requestBox;
	private FifoQueue responseBox;
	private ZaSmsSubmit sms;
		
	private String tName = "";

        StringHolder authnRes;
        StringHolder token;
        OnlineBillingSoap bsoap;
        
          public ZaSmsConnection() { // for South Africa Daemons
            this.gw = new SdcSmsGateway();
            gw.setLogin("UME2");
            gw.setPassword("UM32P@ssword");
            gw.setMsisdnFormat(4);
            requestBox = new FifoQueue(1);
            responseBox = new FifoQueue(1);
            try {
                bsoap = (new OnlineBillingLocator()).getOnlineBillingSoap(); 
            
            
            } catch (ServiceException e) {e.printStackTrace();}
	}
       public ZaSmsConnection(SdcSmsGateway gw) {
            this.gw = gw;
            requestBox = new FifoQueue(1);
            responseBox = new FifoQueue(1);
            try { 
                bsoap = (new OnlineBillingLocator()).getOnlineBillingSoap(); 
                
            } catch (ServiceException e) {}
	}
       
       

    @Override
    public void run() {
              Thread thisThread = Thread.currentThread();
            tName = thisThread.getName();

            while (true) {
                try {
                    sms = (ZaSmsSubmit) requestBox.remove(40000);
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
    public String doRequest(ZaSmsSubmit sms) {
        String res = "";
        String logUnique = "";
        String sendingxml="";
            
        //System.out.println("Sending SMS in ZASms..");
        sms.setToNumber(SdcMisc.parseMobileNumber(sms.getToNumber(), gw.getMsisdnFormat()));
         try {

                if (sms.getBillingMsg()) {

                    if (token==null) {
                        //System.out.println("Authenticating STS billing");
                        authnRes = new StringHolder();
                        token = new StringHolder();
                        bsoap.authenticateUser(gw.getLogin(), gw.getPassword(), authnRes, token);
//                        System.out.println("AuthnRes: " + authnRes.value);
//                        System.out.println("Token: " + token.value);
                    }                       

                    if (authnRes.value.equalsIgnoreCase("success")) {

                        //System.out.println("Making STS billing request: " + sms.getToNumber() + ": " + sms.getMsgType());

                        Calendar subDate = null;
                        Calendar createdDate=null;
                        if (sms.getSubDate()!=null) {
                            subDate = new GregorianCalendar();
                            subDate.setTime(sms.getSubDate());
                        }
                          if (sms.getCreated()!=null) {
                            createdDate = new GregorianCalendar();
                            createdDate.setTime(sms.getCreated());
                        }
                        
                        StringHolder processSubscriptionTicket = new StringHolder();
                        StringHolder responseref = new StringHolder();
                        StringHolder responsedescription = new StringHolder();
                        StringHolder errorcode = new StringHolder();
                        StringHolder msisdnnetwork=new StringHolder();
                         StringHolder contracttype=new StringHolder();
                         
                        BigDecimal ticketAmount=null;
                        if(sms.getCost()!=0)
                        {
                             if(sms.getCost()==10)
                            {
                             ticketAmount=new BigDecimal("9.99");
                            }
                            else
                            ticketAmount=new BigDecimal(sms.getCost());                    
                           
                        }
                        int transId = (int) (System.currentTimeMillis() -  1301000000000L);

    bsoap.processSubscriptionTicket(token.value, sms.getServiceGuid(), sms.getMsgType(), createdDate, transId, 
                                 sms.getToNumber(), ticketAmount, sms.getSubType(), subDate, processSubscriptionTicket, responseref, responsedescription, errorcode, msisdnnetwork,contracttype);

//                        System.out.println("processSubscriptionTicket: " + processSubscriptionTicket.value);
//                        System.out.println("ResponseRef: " + responseref.value);
//                        System.out.println("ResponseDescription: " + responsedescription.value);
//                        System.out.println("ErrorCode: " + errorcode.value);
//                        System.out.println("Msisdnnetwork: " + msisdnnetwork.value);
//                        System.out.println("Contracttype: " + contracttype.value);

                        res = "<status>" + processSubscriptionTicket.value + "</status>\r\n"
                            + "<transaction-id>" + transId + "</transaction-id>\r\n"
                            + "<resp-ref>" + responseref.value + "</resp-ref>\r\n"
                            + "<resp-desc>" + responsedescription.value + "</resp-desc>\r\n"
                            + "<resp-code>" + errorcode.value + "</resp-code>\r\n"
                            + "<msisdnetwork>" + msisdnnetwork.value + "</msisdnetwork>\r\n"
                            + "<contracttype>" + contracttype.value + "</contracttype>\r\n";
                      
                    }
 
                }
                else {
                    
                   // System.out.println("SMSTEST"+" Trying to Send Welcome Sms now "+sms+" smsdao : "+umesmsdao);
                    try{
                    
                    logUnique = sms.getLogUnique();
                    }
                     catch(Exception e){logUnique=SdcMisc.generateUniqueId();}
                    //logUnique = new UmeSmsDao().log(sms);
                    //System.out.println("SMSTEST"+"Trying to Send Welcome Sms now "+" 2with logUnique: "+logUnique);
                    SmsWSSoap soap = (new SmsWSLocator()).getSmsWSSoap();
                    //System.out.println("SMSTEST"+" SOAP: " + soap);
                    BooleanHolder loginRes = new BooleanHolder();
                    StringHolder token = new StringHolder();
                    //System.out.println("SMSTEST"+" Doing login: " + gw.getLogin() + ": " + gw.getPassword());
                    soap.login(gw.getLogin(), gw.getPassword(), loginRes, token);

//                    System.out.println("SMSTEST"+" LoginRes: " + loginRes);
//                    System.out.println("SMSTEST"+" Token: " + token);
//                    System.out.println("SMSTEST"+" LoginRes: " + loginRes.value);
//                    System.out.println("SMSTEST"+" Token: " + token.value);
                    
                      sendingxml = "<token>" + token.value + "</token>\r\n"
                            + "<recipient>" + sms.getToNumber() + "</recipient>\r\n"
                            + "<message>" + sms.getMsgBody() + "</message>\r\n"
                            + "<reference>" + logUnique+ "</reference>\r\n"
                            + "<campaignid>" + sms.getCampaignId() + "</campaignid>\r\n";
                          
                      //System.out.println("SMSTEST"+" first sendingxml "+sendingxml);
                    if (loginRes.value) {
                        if (sms.getMsgType().equals("wap")) {
                            //System.out.println("Sending WAP URL: " + sms.getToNumber() + ": " + sms.getUrl() + ": " + sms.getMsgBody()+" sms campaignId "+sms.getCampaignId());
                            res = soap.sendWAPLink(token.value, sms.getToNumber(), sms.getUrl(), sms.getMsgBody(), logUnique, sms.getCampaignId());
                            
                       sendingxml = "<token>" + token.value + "</token>\r\n"
                            + "<recipient>" + sms.getToNumber() + "</recipient>\r\n"
                            + "<href>" + sms.getUrl() + "</href>\r\n"
                            + "<text>" + sms.getMsgBody() + "</text>\r\n"
                            + "<reference>" + logUnique+ "</reference>\r\n"
                            + "<campaignid>" + sms.getCampaignId() + "</campaignid>\r\n";       
                            
                     
                        }
                        else {
                            
                              sendingxml = "<token>" + token.value + "</token>\r\n"
                            + "<recipient>" + sms.getToNumber() + "</recipient>\r\n"
                            + "<message>" + sms.getMsgBody() + "</message>\r\n"
                            + "<reference>" + logUnique+ "</reference>\r\n"
                            + "<campaignid>" + sms.getCampaignId() + "</campaignid>\r\n";  
                            
                            //System.out.println("SMSTEST"+" NOT WAP sendingxml "+sendingxml);
                            //System.out.println("SMSTEST"+" Sending Message: " + sms.getToNumber() + ": " + sms.getMsgBody()+" sms campaignId "+sms.getCampaignId()+" token:"+token.value+" logUnique: "+logUnique);
                            res = soap.sendSMS(token.value, sms.getToNumber(), sms.getMsgBody(), logUnique, sms.getCampaignId());
                        }
                        
                    }
                    
//                    System.out.println("SMSTEST"+" Response "+res);
//                    System.out.println("SMSTEST"+" Sending message xml \n"+sendingxml);
                    
                    
                }
                
                

            }
            catch (Exception e) { System.out.println(tName + ": " + e); res = SdcSmsResp.get(32); e.printStackTrace(); }
            finally {
                
            }
        
        
        return res;
        
    }
    
    //===========   Retrieving Balance ========================
        public String doBalanceRequest(ZaSmsSubmit sms) {

            String res = "";
            String logUnique = "";
            
            //System.out.println("Sending SMS in StsSmsConnectionNew...");
            
            sms.setToNumber(SdcMisc.parseMobileNumber(sms.getToNumber(), gw.getMsisdnFormat()));

            try {

                if (sms.getBillingMsg()) {

                    if (token==null) {
                        //System.out.println("Authenticating STS billing");
                        authnRes = new StringHolder();
                        token = new StringHolder();
                        bsoap.authenticateUser(gw.getLogin(), gw.getPassword(), authnRes, token);
//                        System.out.println("AuthnRes: " + authnRes.value);
//                        System.out.println("Token: " + token.value);
                    }                       

                    if (authnRes.value.equalsIgnoreCase("success")) {

                        //System.out.println("Making STS billing Amount request: " + sms.getToNumber() + ": " + sms.getMsgType());

                                                
                        BigDecimalHolder retrieveBalance = new  BigDecimalHolder();
                       StringHolder errorcode = new StringHolder();
                     
                       bsoap.retrieveBalance(token.value, sms.getToNumber(), retrieveBalance, errorcode);

//                        System.out.println("retrieveBalance: " + retrieveBalance.value);
//                        System.out.println("Error: " + errorcode.value);
                      

                        res = "<status>" + retrieveBalance.value + "</status>\r\n"
                            + "<resp-code>" + errorcode.value + "</resp-code>\r\n";
                           
                      
                    }
 
                }
                
            }
            catch (Exception e) { 
                System.out.println(tName + ": " + e); res = SdcSmsResp.get(32); }
            finally {
                
            }

            return res;
                 
	}
        
        //========= End Retrieving Balance ========================

    @Override
    public synchronized void handOff(Object sms) throws InterruptedException {
        requestBox.add(sms);
    }

    @Override
    public synchronized String getResponse() throws InterruptedException {
        return (String) responseBox.remove();
    }
    
    public String doSingleRequest(SdcSmsSubmit sms, SdcSmsGateway gw) {
            ZaSmsConnection smscon = new ZaSmsConnection(gw);
            smscon.tName = "SingleStsConnection";
            //System.out.println("ZA Connection doSingleREquest is CALLED ON ");
            return smscon.doRequest((ZaSmsSubmit) sms);
	}
    public void invalidateBillingToken() {
            bsoap = null; authnRes = null; token = null;
        }
    
    
    
    
}
