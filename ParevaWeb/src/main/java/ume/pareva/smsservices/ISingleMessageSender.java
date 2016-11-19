package ume.pareva.smsservices;

import java.util.Map;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeUser;


/**
 *
 * @author Alex
 */
public interface ISingleMessageSender {

    /**
     * Send <code>msgText</code> to <code>toMsisdn</code> from <code>fromMsisdn</code>  with the parameters <code>club</code>,<code>clubDetails</code> and <code>extraParameters</code> and logs to smsMsgLog
     * @param toMsisdn msisdn which will be sent the message
     * @param fromMsisdn sender
     * @param msgText message to be sent
     * @param club club used
     * @param clubDetails details of the club used
     * @param extraParameters parameters that might be needed
     * @return 
     */
    public boolean sendMessage(String toMsisdn, String fromMsisdn,String msgText, MobileClub club, UmeClubDetails clubDetails, Map<String,String> extraParameters);
    public boolean sendMessage(String toMsisdn, String fromMsisdn,String msgText, MobileClub club, UmeClubDetails clubDetails, String msgType, boolean billable);
    public boolean sendMessage(UmeUser user,SdcMobileClubUser clubuser,String msgText, MobileClub club, UmeClubDetails clubDetails, String msgType, boolean billable);
    public boolean sendMessage(UmeSessionParameters aReq,UmeUser user, SdcMobileClubUser clubuser, String msgText, MobileClub club, UmeClubDetails clubDetails, String msgType, boolean billable);
}
