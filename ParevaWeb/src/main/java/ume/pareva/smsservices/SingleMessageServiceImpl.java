package ume.pareva.smsservices;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeUser;

/**
 *
 * @author madan
 */

@Component("messagingservice")
public class SingleMessageServiceImpl implements ISingleMessageSender {
    
    @Autowired
    ZASmsService zasmsservice;

    @Override
    public boolean sendMessage(String toMsisdn, String fromMsisdn, String msgText, MobileClub club, UmeClubDetails clubDetails, Map<String, String> extraParameters) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean sendMessage(String toMsisdn, String fromMsisdn, String msgText, MobileClub club, UmeClubDetails clubDetails, String msgType, boolean billable) {
        
        boolean sent=true;
        
        switch (club.getRegion()) {
            case "ZA": break;
            case "UK": break;
            case "IE": break;
            case "IT": break;
            case "KE": break;
                
                
            
        }
        
       return true; 
    }

    @Override
    public boolean sendMessage(UmeUser user, SdcMobileClubUser clubuser, String msgText, MobileClub club, UmeClubDetails clubDetails, String msgType, boolean billable) {
         boolean sent=true;
        
        switch (club.getRegion()) {
            case "ZA": break;
            case "UK": break;
            case "IE": break;
            case "IT": break;
            case "KE": break;
                
                
            
        }
        
       return true; 
    }

    @Override
    public boolean sendMessage(UmeSessionParameters aReq, UmeUser user, SdcMobileClubUser clubuser, String msgText, MobileClub club, UmeClubDetails clubDetails, String msgType, boolean billable) {
       boolean sent=true;
        
        switch (club.getRegion()) {
            case "ZA": zasmsservice.sendMessage(aReq, user, clubuser, msgText, club, clubDetails, msgType, billable);
                break;
            case "UK": break;
            case "IE": break;
            case "IT": break;
            case "KE": break;
                
                
            
        }
        
       return true; 
    }
    
    
}
