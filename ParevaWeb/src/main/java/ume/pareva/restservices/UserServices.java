package ume.pareva.restservices;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.sdk.MiscCr;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.userservice.UserStopService;

/**
 *
 * @author madan
 */

@Component
@Path("/UserServices")
public class UserServices {
    
    @Autowired
    SubscriptionCreation subscriptioncreation;
    
    @Autowired
    UserStopService userstopservice;
    
    @GET
    @Path("/userstatus")
    @RolesAllowed({"ADMIN","MANAGER"})
    @Produces({MediaType.APPLICATION_JSON})
    public Response isUserActive(@QueryParam("msisdn")String msisdn, @QueryParam("serviceid") String clubunique) {
        String userActive = "false";
        Response jsonresponse=null;
        
        Map<String,String> useractiveresponse=new HashMap<>(0);
        useractiveresponse=subscriptioncreation.checkUser(msisdn, clubunique);
        String activeresponse=useractiveresponse.get(msisdn);
        
        SubscriptionList list=new SubscriptionList();
        list.setMsisdn(msisdn);
        list.setClub(clubunique);
        list.setResponseCode("SUCCESS");
        list.setMessage(activeresponse);  
        
        return jsonresponse.status(200).entity(list).build();
        
    }
    
    @GET
    @Path("/usercredit")
    @RolesAllowed({"ADMIN","MANAGER"})
    @Produces({MediaType.APPLICATION_JSON})
    public Response creditDetail(@QueryParam("msisdn")String msisdn, @QueryParam("serviceid") String clubunique, @QueryParam("creditamount") int creditamount) {
        
        String userActive = "false";
        Response jsonresponse=null;
       
        Map<String,String> usercreditresponse=new HashMap<>(0);
        if(creditamount==-1 || creditamount==0) creditamount=1;
        usercreditresponse=subscriptioncreation.creditDetail(msisdn, clubunique,creditamount);
        String activeresponse=usercreditresponse.get(msisdn);
        
        SubscriptionList list=new SubscriptionList();
        list.setMsisdn(msisdn);
        list.setClub(clubunique);
        list.setResponseCode("SUCCESS");
        list.setMessage(activeresponse);  
        
        return jsonresponse.status(200).entity(list).build();
     
    }
    
    @GET
    @Path("/creditDetail")
    @RolesAllowed({"ADMIN","MANAGER"})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getcreditDetail(@QueryParam("msisdn")String msisdn, @QueryParam("serviceid") String clubunique, @QueryParam("creditamount") int creditamount) {
        
        String userActive = "false";
        Response jsonresponse=null;
       
        Map<String,String> usercreditresponse=new HashMap<>(0);
        if(creditamount==-1 || creditamount==0) creditamount=1;
        usercreditresponse=subscriptioncreation.creditDetail(msisdn, clubunique,creditamount);
        String activeresponse=usercreditresponse.get(msisdn);
        
        SubscriptionList list=new SubscriptionList();
        list.setMsisdn(msisdn);
        list.setClub(clubunique);
        list.setResponseCode("SUCCESS");
        list.setMessage(activeresponse);  
        
        return jsonresponse.status(200).entity(list).build();
     
    }
    
    
    @GET
    @Path("/stopsinglesubscription")
    @RolesAllowed({"ADMIN","MANAGER"})
    @Produces({MediaType.APPLICATION_JSON})
    public Response StopSingleSubscription(@QueryParam("msisdn")String msisdn, @QueryParam("serviceid") String clubunique) {
        String response="FAILURE";
        SubscriptionList list=null; 
        Response jsonresponse=null;
        MobileClub club=null;
        
        if(msisdn==null || msisdn.equals("")){
        response=" MSISDN IS MISSING. PLEASE CHECK YOUR PARAMETERS";
        list=new SubscriptionList();
        list.setMsisdn(msisdn);
        list.setClub(clubunique);
        list.setResponseCode("MSISDN_MISSING");
        list.setMessage(response);
                 
        }
        else{
             //TODO We need a condition here to check if encrypted msisdnaa
                String demsisdn = MiscCr.decrypt(msisdn); //
                
                System.out.println("DECRYPTED MSISDN IS "+demsisdn+" FOR ORIGINAL "+msisdn);
                
                if(clubunique!=null) club = UmeTempCmsCache.mobileClubMap.get(clubunique);
                Map<String,String> stopSingleservice=new HashMap<String,String>();
                stopSingleservice=userstopservice.stopSingleSubscription(demsisdn, clubunique);
                String responsecode="FAILURE";
            
                String stopresponse=stopSingleservice.get(demsisdn);
                if(stopresponse.toLowerCase().contains("successful")) responsecode="SUCCESS";
                list=new SubscriptionList();
                list.setMsisdn(msisdn);
                list.setClub(clubunique);
                list.setResponseCode(stopresponse);
                list.setMessage(stopresponse);      
            }
        
        
        return jsonresponse.status(200).entity(list).build();
    }
    
    
    
    
    
}
