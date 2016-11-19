/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.za;

//import com.zadoi.service.ZaDoi;
import com.zadoi.service.ZaDoi;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcSmsGateway;
import ume.pareva.pojo.UmeClubMessages;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.smsapi.ZaSmsSubmit;
import ume.pareva.smsservices.SmsService;

/**
 *
 * @author madan
 */

@Component("zastopservice")
public class ZAStop {
    
    @Autowired
    SmsService umesmsdaoextension;
    
    @Autowired
    UmeClubDetailsDao umeclubdetailsdao;
 
    public void StopZAUser(SdcMobileClubUser clubUser, MobileClub club){
        ZaDoi zadoi = new ZaDoi();
        String token = zadoi.authenticate();
        String serviceName="";
          serviceName=club.getOtpServiceName();
        boolean responses=zadoi.delete_DoubleOptIn_Record(token, serviceName,clubUser.getParsedMobile());
        
        if(responses) {        
            List<UmeClubMessages> stopMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Stop");
            if(stopMessages!=null && !stopMessages.isEmpty()){
                for(int i=0;i<stopMessages.size();i++){
                    if(club!=null && !stopMessages.get(i).getaMessage().isEmpty()){
                        String transactionid=Misc.generateUniqueId(5)+"-"+club.getUnique();
                        ZaSmsSubmit stopsms = new ZaSmsSubmit();
                        stopsms.setUnique(transactionid);
                        stopsms.setLogUnique(transactionid);
                        stopsms.setSmsAccount("sts");
                        stopsms.setToNumber(clubUser.getParsedMobile());
                        stopsms.setMsgBody(stopMessages.get(i).getaMessage());
                        stopsms.setNetworkCode(clubUser.getClubUnique());
                        stopsms.setClubUnique(clubUser.getClubUnique());
                        stopsms.setRefMessageUnique("STOP");
                        stopsms.setStatus("SENT");
                        try { 
                            stopsms.setCampaignId(1144);//(Integer.parseInt(club.getSmsExt()));                   
                            } catch (NumberFormatException e) {}
            
                        stopsms.setMsgCode1("");                
                        String smsresp = "";
                            try{
                                SdcSmsGateway gw=new SdcSmsGateway();
                                gw.setAccounts(stopsms.getSmsAccount());
                                gw.setMsisdnFormat(4);
                                smsresp=umesmsdaoextension.send(stopsms, gw);
                                System.out.println(stopsms.getToNumber()+" response is "+smsresp);
                                }
                                catch(Exception e){e.printStackTrace();}
                
                    } //IF club!=null && stopMessage is not EMPTY
                } // END FOR LOOP
            }//IF stopMessages!=null
        }
        
    }
    
}
