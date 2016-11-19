package ume.pareva.uk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.engage.pojo.SMSParameter;
import ume.pareva.engageimpl.EngageInterface;
import ume.pareva.engageimpl.EngageInterfaceImpl;
import ume.pareva.ire.IREConnConstants;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeClubMessages;
import ume.pareva.sdk.Misc;
import ume.pareva.util.httpconnection.HttpURLConnectionWrapper;

/**
 *
 * @author madan
 */

@Component("competitionstopservice")
public class CompetitionStop {
    
    @Autowired
    UmeSmsDao umesmsdao;
    
    @Autowired
    UmeClubDetailsDao umeclubdetailsdao;
    
    
    public boolean StopCompetition(SdcMobileClubUser clubUser, MobileClub club){
        boolean stopcompetition=false;
        UmeClubDetails userclubdetails=null;
        userclubdetails=UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        
        if((clubUser!=null && clubUser.getParsedMobile().startsWith("44")) && 
                (club!=null && club.getRegion().equals("UK"))) {
            stopcompetition=UKStop(clubUser,club,userclubdetails);
        }
        
          if((clubUser!=null && clubUser.getParsedMobile().startsWith("353")) && 
                (club!=null && club.getRegion().equals("IE"))) {
            stopcompetition=IEStop(clubUser,club,userclubdetails);
        }
        
        
        return stopcompetition;
        
    }
    
    
       public boolean StopCompetition(SdcMobileClubUser clubUser, MobileClub club,String requestFrom){
        boolean stopcompetition=false;
        UmeClubDetails userclubdetails=null;
        userclubdetails=UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        
        if((clubUser!=null && clubUser.getParsedMobile().startsWith("44")) && 
                (club!=null && club.getRegion().equals("UK"))) {
            if(!requestFrom.equalsIgnoreCase("mo")) stopcompetition=UKStop(clubUser,club,userclubdetails);
        }
        
          if((clubUser!=null && clubUser.getParsedMobile().startsWith("353")) && 
                (club!=null && club.getRegion().equals("IE"))) {
            stopcompetition=IEStop(clubUser,club,userclubdetails);
        }
        
        
        return stopcompetition;
        
    }
    
    public boolean UKStop(SdcMobileClubUser clubUser, MobileClub club,UmeClubDetails userclubdetails){
        boolean stopped=false;
        String serviceId="1";
//String deliveryReceipt="13";
        String deliveryReceipt="11";
        String typeId="2";
        String freeCostId="1";
        String freeServiceId="1";
        String shortCode="Quiz2Win";
        
        try{
        shortCode=userclubdetails.getClubSpoof();
        }catch(Exception e){shortCode="Quiz2Win";}
        int transactionId=Misc.generateUniqueIntegerId();
        String[] toAddress={clubUser.getParsedMobile()};
        
        List<UmeClubMessages> stopMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Stop");
        
        if(stopMessages!=null && !stopMessages.isEmpty()){
        for(int i=0;i<stopMessages.size();i++){
        if(club!=null && !stopMessages.get(i).getaMessage().isEmpty()){
        SMSParameter stopSMSParameter=new SMSParameter();
        stopSMSParameter.setCostId(freeCostId);
        stopSMSParameter.setServiceId(serviceId);
        stopSMSParameter.setDeliveryReceipt(deliveryReceipt);
        stopSMSParameter.setMsgText(stopMessages.get(i).getaMessage());
        stopSMSParameter.setShortCode(shortCode);
        stopSMSParameter.setTransactionId(String.valueOf(transactionId));
        stopSMSParameter.setTypeId(typeId);
        stopSMSParameter.setToAddress(toAddress);
        stopSMSParameter.setWinTransactionId(String.valueOf(Misc.generateUniqueIntegerId()));
        
        EngageInterface stopengage=new EngageInterfaceImpl();
        int responseCode=stopengage.sendSMS(stopSMSParameter);
        if(responseCode==200){
            stopped=true;
        SdcSmsSubmit stopmsg=new SdcSmsSubmit();
                    		
        stopmsg.setUnique(String.valueOf(transactionId));
        stopmsg.setLogUnique(String.valueOf(transactionId));
        stopmsg.setFromNumber(shortCode);
        stopmsg.setToNumber(clubUser.getParsedMobile());
        stopmsg.setMsgType("FREE");
        stopmsg.setMsgBody(stopMessages.get(i).getaMessage());
        stopmsg.setCost(club.getPrice());
        stopmsg.setMsgCode1("competitionstop");
        stopmsg.setStatus("SENT");
        stopmsg.setRefMessageUnique("STOP");
        stopmsg.setClubUnique(clubUser.getClubUnique());
        stopmsg.setNetworkCode(clubUser.getNetworkCode().toLowerCase());
        umesmsdao.log(stopmsg);    
            } //END 200 Response
            } //END IF club!=null and stopMessage is not Empty 
        } //END FOR Loop 
        
        }//END clubMessage!=null
        
        return stopped;
    }
    
    
    public boolean IEStop(SdcMobileClubUser clubUser, MobileClub club,UmeClubDetails userclubdetails){
        boolean stopped=false;
        stopped=IEsendStopSMS(clubUser.getParsedMobile(),club,userclubdetails);
     return true;   
    }
    
    
    
    public boolean IEsendStopSMS(String msisdn, MobileClub club,UmeClubDetails userclubdetails){
	
    /**
     * 	$req = 'reply=0';
	$req .= '&id='.uniqid();
	$req .= '&number='.$number;
	$req .= '&network=INTERNATIONAL';
	$req .= '&message='.$msg;
	$req .= '&value=0';
	$req .= '&currency=GBP';
	$req .= '&cc='.$company;
	$req .= '&title='.$title;
	$req .= '&ekey='.$ekey;
http://client.txtnation.com/gateway.php?reply=0&id=123456&number=353851485271
* &network=international&value=0&currency=EUR&cc=moonlight&ekey=a6815e707c675f7a3f307656d462bca6&message=Your+message
     */
     boolean success=false;
    String irehttp="http://client.txtnation.com/gateway.php";    
    String network="international";
    String ekey=club.getOtpServiceName();//"a6815e707c675f7a3f307656d462bca6";     
    String cc=userclubdetails.getCompanyCode();
    String encodedmsg;
   
    String shortCode="Quiz2Win";
    try{
        shortCode=userclubdetails.getClubSpoof();
    }catch(Exception e){shortCode="Quiz2Win";}
   
    List<UmeClubMessages> stopMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Stop");
        
        if(stopMessages!=null && !stopMessages.isEmpty()){
            
            for(int i=0;i<stopMessages.size();i++){
                String id=Misc.generateUniqueId()+"-"+club.getUnique();
                String msg=stopMessages.get(i).getaMessage();
                try{
                    encodedmsg=java.net.URLEncoder.encode(msg,"utf-8");
                }catch(Exception e){encodedmsg=msg;}
        
            irehttp+="?reply=0&id="+id+"&number="+msisdn+"&network="+network+"&value=0&currency=EUR&cc="+cc+"&ekey="+ekey+"&message="+msg;
    
                HttpURLConnectionWrapper urlwrapper=urlwrapper=new HttpURLConnectionWrapper(IREConnConstants.getDomainHttp());
                Map<String, String> ireMap=new HashMap<String,String>();
        
                ireMap.put("reply", "0");
                ireMap.put("id",id);
                ireMap.put("number",msisdn);
                ireMap.put("network",network);
                ireMap.put("value","0");
                ireMap.put("currency","EUR");
                ireMap.put("cc",cc);
                ireMap.put("ekey",ekey);
                ireMap.put("message",encodedmsg);
                ireMap.put("title",shortCode);
                ireMap.put("smscat","991");
        
                if(club!=null && !stopMessages.get(i).getaMessage().isEmpty()){
        
                    urlwrapper.wrapGet(ireMap);    
                    String responsecode=urlwrapper.getResponseCode();
                    String responsedesc=urlwrapper.getResponseContent();
                    boolean isSuccessful=urlwrapper.isSuccessful();
        
                    System.out.println("txtnation: http request sent "+irehttp);
    
                    System.out.println("txtnation:  confirm.jsp response "+responsecode+"  desc "+responsedesc+" successful: "+isSuccessful);
    
   

                if(isSuccessful){
                    SdcSmsSubmit sdcSmsSubmit=new SdcSmsSubmit();		
                    sdcSmsSubmit.setUnique(id);
                    sdcSmsSubmit.setFromNumber(shortCode);
                    sdcSmsSubmit.setToNumber(msisdn);
                    sdcSmsSubmit.setMsgType("Free");
                    sdcSmsSubmit.setMsgBody(msg);
                    sdcSmsSubmit.setLogUnique(id);
                    sdcSmsSubmit.setCost(club.getPrice());
                    sdcSmsSubmit.setMsgCode1("competitionstop");
                    sdcSmsSubmit.setClubUnique(club.getUnique());
                    sdcSmsSubmit.setStatus("SENT");
                    sdcSmsSubmit.setRefMessageUnique("STOP");
                    umesmsdao.log(sdcSmsSubmit);              
                    success=true;
                }
                else {
                    System.out.println("irelandtesting  "+responsecode+":"+responsedesc);
                } //END ELSE 
            } //END if club!=null 
        }// END FOR LOOP
    } // END if STOPMEssage is not null and Empty 
		return success;
}
    
}
