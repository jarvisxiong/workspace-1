/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.smsservices;

import org.springframework.stereotype.Component;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.SdcSmsResp;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeSmsDaoExtension;
import ume.pareva.pojo.SdcSmsGateway;
import ume.pareva.smsapi.ISmsExtension;
import ume.pareva.smsapi.IpxSmsConnection;
import ume.pareva.smsapi.ZaSmsConnection;

/**
 *
 * @author madan
 */

@Component("smsservice")
public class SmsService extends UmeSmsDaoExtension {
    
      public String send(SdcSmsSubmit sms) {
        
          //System.out.println("SMSDAOEXTENSIONDAO  umesmsdao value is "+umesmsdao);
        String error = umesmsdao.initMessage(sms);
        if (error.equals("")) return doRequest(sms);
        else return error;
    }
    
     public String send(SdcSmsSubmit sms,SdcSmsGateway gateway) {
        
        String error = umesmsdao.initMessage(sms);
        if (error.equals("")) return this.doRequest(sms,gateway);
        else return error;
    }

    /**
     *
     * @param sms
     * @return
     */
    
      public String doRequest(SdcSmsSubmit sms, SdcSmsGateway gateway) {
        
        SdcSmsGateway gw = gateway;
        boolean doSingleRequest = false;
        String logUnique = "";
        
        //System.out.println("Getting MY gateway for the account: " + sms.getSmsAccount()+ "umesdc "+umesdc);
        try{
            
//            gw = umesdc.getSmsGatewayMap().get(sms.getSmsAccount());
//         System.out.println("SMSDAO EXTENSION GATEWAY INFO :"+umesdc.getSmsGatewayMap().get(sms.getSmsAccount()));
            gw.setAccounts(sms.getSmsAccount());
            gw.setMsisdnFormat(4);
            gw.setLogin("UME2");
            gw.setPassword("UM32P@ssword");
            gw.setType("sts_za");
        //gw=smsgatewaydao.getGateway(sms.getSmsAccount());
        
        }
        catch(Exception e){System.out.println("umesdc value "+umesdc.toString());e.printStackTrace();}
        
        if (gw==null){ System.out.println("GW IS NULL=========");return SdcSmsResp.get(17);}
        
        //System.out.println("Gateway : "+gw.getAccounts()+" GATEWAY INFORMATION :> "+gw.getUnique()+" idlequeue: "+gw.idleQueue);

        if (gw.idleQueue!=null) {
            
            if (gw.getType().equals("mxmgw")) return doRequest(sms);
            
            //System.out.println("Using Gateway: " + gw.getName() + ": " + gw.getIp());

            try {
                ISmsExtension smsCon = (ISmsExtension) gw.idleQueue.remove(10000);
                if (smsCon!=null) {
                    //System.out.println("Got SmsExtCon instance. handing off SMS");
                    smsCon.handOff(sms);
                    //System.out.println("Handed off to SmsExtCon. Waiting for resp.");
                    String response = smsCon.getResponse();
                    try { gw.idleQueue.add(smsCon); } catch (InterruptedException e) {}
                    
                    return response;
                    
                } else { doSingleRequest = true; }
            } catch (InterruptedException e) { doSingleRequest = true; e.printStackTrace(); }
        } else { doSingleRequest = true; }
        
        if (doSingleRequest) {
            //System.out.println("GW TYPE "+gw.getType()+" now calling ZA SMS Connection ");
            if (gw.getType().equals("sts_za"))
            { 
                       try{
                    
                    logUnique = umesmsdao.log(sms);
                    sms.setLogUnique(logUnique);
                    }
                     catch(Exception e){logUnique=SdcMisc.generateUniqueId();sms.setLogUnique(logUnique);}
                
                 String response=new ZaSmsConnection().doSingleRequest(sms, gw);
               //System.out.println("ZASMS CONNECTION RESPONSE IS "+response);
               //umesmsdao.log(sms);
               return response;
            
            
            }
            
            
//            if (gw.getType().equals("sts_ke")) return StsSmsKeConnection.doSingleRequest(sms, gw);
//            else if (gw.getType().startsWith("yo")) return YoConnection.doSingleRequest(sms, gw);
            else if (gw.getType().equals("ipx")) return IpxSmsConnection.doSingleRequest(sms, gw);
//            //add to gateway
//            else if (gw.getType().equals("ipx_billing")) return IpxBillingConnection.doSingleRequest(sms, gw);
//            else if (gw.getType().startsWith("utl")) return UtlBillingConnection.doSingleRequest(sms, gw);
//            else if (gw.getType().startsWith("tanla")) return TanlaSmsConnection.doSingleRequest(sms, gw);
        }
        
        return umesmsdao.doRequest(sms);
      }
      
          public String doRequest(SdcSmsSubmit sms) {
        
        SdcSmsGateway gw = null;
        boolean doSingleRequest = false;
        
        
        //System.out.println("Getting gateway for the account: " + sms.getSmsAccount());
        gw = umesdc.getSmsGatewayMap().get(sms.getSmsAccount());
        if (gw==null) return SdcSmsResp.get(17);

        if (gw.idleQueue!=null) {
            
            if (gw.getType().equals("mxmgw")) return umesmsdao.doRequest(sms);
            
            //System.out.println("Using Gateway: " + gw.getName() + ": " + gw.getIp());

            try {
                ISmsExtension smsCon = (ISmsExtension) gw.idleQueue.remove(10000);
                if (smsCon!=null) {
                    //System.out.println("Got SmsExtCon instance. handing off SMS");
                    smsCon.handOff(sms);
                    //System.out.println("Handed off to SmsExtCon. Waiting for resp.");
                    String response = smsCon.getResponse();
                    try { gw.idleQueue.add(smsCon); } catch (InterruptedException e) {}
                    
                    return response;
                    
                } else { doSingleRequest = true; }
            } catch (InterruptedException e) { doSingleRequest = true; }
        } else { doSingleRequest = true; }
        
        if (doSingleRequest) {
            //System.out.println("INside doSingle Request the value of umesmsdao is "+umesmsdao);
            if (gw.getType().equals("sts_za")) 
            {
                String response=new ZaSmsConnection().doSingleRequest(sms,gw);
               //System.out.println("ZASMS CONNECTION RESPONSE IS "+response);
               umesmsdao.log(sms);
               return response;
            }
//            if (gw.getType().equals("sts_ke")) return StsSmsKeConnection.doSingleRequest(sms, gw);
//            else if (gw.getType().startsWith("yo")) return YoConnection.doSingleRequest(sms, gw);
            else if (gw.getType().equals("ipx")) return IpxSmsConnection.doSingleRequest(sms, gw);
//            //add to gateway
//            else if (gw.getType().equals("ipx_billing")) return IpxBillingConnection.doSingleRequest(sms, gw);
//            else if (gw.getType().startsWith("utl")) return UtlBillingConnection.doSingleRequest(sms, gw);
//            else if (gw.getType().startsWith("tanla")) return TanlaSmsConnection.doSingleRequest(sms, gw);
        }
        
        return umesmsdao.doRequest(sms);
      }
    
    
}
