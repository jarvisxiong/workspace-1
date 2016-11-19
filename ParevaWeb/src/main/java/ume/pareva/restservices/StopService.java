package ume.pareva.restservices;

import java.util.HashMap;
import java.util.List;
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
import ume.pareva.userservice.UserStopService;

/**
 *
 * @author madan
 */

@Component
@Path("/StopService")
public class StopService {
    
    private static final String STOPSERVICE ="SUBSCRIPTION RECORD STOPPED CREATED SUCCESSFULLY";
    private static final String ALREADY_STOPPED ="RECORD WAS ALREADY STOPPED";
    private static final String SUBSCRIPTION_ERROR ="ERROR - CONTACT MADAN/ALEX - madan@umelimited.com/alex.sanchez@umelimited.com";
    private static final String SUCCESS_RESULT="<result>success</result>";
    private static final String FAILURE_RESULT="<result>failure</result>";
    
    @Autowired
    UserStopService userstopservice;
    
    
    @GET
    @Path("/stopsinglesubscription")
    @RolesAllowed("ADMIN")
    @Produces({MediaType.APPLICATION_JSON})
    public Response StopSingleSubscription(@QueryParam("msisdn")String msisdn, @QueryParam("clubunique") String clubunique) {
        String response=FAILURE_RESULT;
        SubscriptionList list=null; 
        
        MobileClub club=null;
        
        if(msisdn==null || msisdn.equals("")){
        response=FAILURE_RESULT+" MSISDN IS MISSING. PLEASE CHECK YOUR PARAMETERS";
        list=new SubscriptionList();
        list.setMsisdn(msisdn);
        list.setClub(clubunique);
        list.setResponseCode("MSISDN_MISSING");
        list.setMessage(response);
                 
        }
        else{
                if(clubunique!=null) club = UmeTempCmsCache.mobileClubMap.get(clubunique);
                Map<String,String> stopSingleservice=new HashMap<String,String>();
                stopSingleservice=userstopservice.stopSingleSubscription(msisdn, clubunique);
                String responsecode="FAILURE";
            
                String stopresponse=stopSingleservice.get(msisdn);
                if(stopresponse.toLowerCase().contains("successful")) responsecode="SUCCESS";
                list=new SubscriptionList();
                list.setMsisdn(msisdn);
                list.setClub(clubunique);
                list.setResponseCode(stopresponse);
                list.setMessage(stopresponse);      
            }
        
        
        return Response.status(200).entity(list).build();
    }
    
    @GET
    @Path("/stopallsubscription")
    @RolesAllowed("ADMIN")
    @Produces({MediaType.APPLICATION_JSON})
    public Response StopAllSubscription(@QueryParam("msisdn")String msisdn) {
        String response=FAILURE_RESULT;
        SubscriptionList list=null; 
        
        MobileClub club=null;
        
        if(msisdn==null || msisdn.equals("")){
        response=FAILURE_RESULT+" MSISDN IS MISSING. PLEASE CHECK YOUR PARAMETERS";
        list=new SubscriptionList();
        list.setMsisdn(msisdn);
        list.setResponseCode("MSISDN_MISSING");
        list.setMessage(response);
                 
        }
        else{
            Map<String,String> stopAllService=new HashMap<>(0);
            stopAllService=userstopservice.stopAllSubscription(msisdn);
            String responsecode="FAILURE";
            for ( String key : stopAllService.keySet() ) {
                String stopresponse=stopAllService.get(key);
                 if(stopresponse.toLowerCase().contains("successful")) responsecode="SUCCESS";
                list=new SubscriptionList();
                list.setMsisdn(msisdn);
                list.setResponseCode(stopresponse);
                list.setMessage(stopresponse);     
                }
            }
        
        
        return Response.status(200).entity(list).build();
        
    }
    
    @GET
    @Path("/bulkstop")
    @RolesAllowed("ADMIN")
    @Produces({MediaType.APPLICATION_JSON})
    public Response bulkStop(@QueryParam("msisdns")List<String> msisdns) {
        
        String response=FAILURE_RESULT;
        SubscriptionList list=null; 
        
        MobileClub club=null;
        
        if(msisdns==null || msisdns.isEmpty()){
        response=FAILURE_RESULT+" MSISDN IS MISSING. PLEASE CHECK YOUR PARAMETERS";
        list=new SubscriptionList();
        list.setMsisdn("");
        list.setResponseCode("MSISDN_MISSING");
        list.setMessage(response);
                 
        }
        else{
            Map<String,String> stopBulkService=new HashMap<>(0);
            stopBulkService=userstopservice.bulkStop(msisdns);
            String responsecode="FAILURE";
            int counter=0;
            for ( String key : stopBulkService.keySet() ) {
                String stopresponse=stopBulkService.get(key);
                 if(stopresponse.toLowerCase().contains("successful")) responsecode="SUCCESS";
                list=new SubscriptionList();
                list.setMsisdn(msisdns.get(counter));
                list.setResponseCode(stopresponse);
                list.setMessage(stopresponse); 
                counter++;
                }
            }
        
        
        return Response.status(200).entity(list).build();
        
        
    }
    
    
}
