
package ume.pareva.restservices;

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
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.userservice.SubscriptionCreation;

/**
 *
 * @author madan
 */
@Component
@Path("/SubscriptionService")
public class SubscriptionService {
    
    @Autowired
    SubscriptionCreation subscriptioncreation;
    
    @Autowired
    MobileClubDao mobileclubdao;
    
    private static final String SUCCESS_RESULT="<result>success</result>";
    private static final String FAILURE_RESULT="<result>failure</result>";
    
    @GET
    @Path("/createsubscription")
    @RolesAllowed({"ADMIN","MANAGER"})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createSubscription(@QueryParam("msisdn")String msisdn,
            @QueryParam("serviceid") String clubunique,@QueryParam("campaignid") String campaignid,@QueryParam("duration") int duration, 
            @QueryParam("susbcriptiondate") String subscription, @QueryParam("network") String networkid, @QueryParam("externalid") String externalId,
            @QueryParam("landingpage") String landingpage){
        
      System.out.println("restservicequery createSubscriptionRecord "+msisdn+" "+clubunique+" "+campaignid+" "+duration+" "+networkid+" "+landingpage);    
        
        SubscriptionList list=null; 
        Response jsonresponse=null;
        String response=FAILURE_RESULT;
        MobileClub club=null;
              if(msisdn==null || clubunique==null || msisdn.equals("") || clubunique.equals("")){
           
        response=FAILURE_RESULT+" MSISDN OR CLUB IS MISSING. PLEASE CHECK YOUR PARAMETERS";
        list=new SubscriptionList();
        list.setMsisdn(msisdn);
        list.setClub(clubunique);
        list.setResponseCode("MSISDN_CLUB_MISSING");
        list.setMessage(response);
         
       }
        
       else{
           if(duration==-1 || duration==0) duration=1;
           if(subscription==null) subscription="";
           if(networkid==null) networkid="";
           if(externalId==null) externalId="";
           if(landingpage==null) landingpage="";
        club = UmeTempCmsCache.mobileClubMap.get(clubunique);
        if(club==null) club=mobileclubdao.getMobileClub(clubunique);
        System.out.println("restservicequery "+" clubobject is "+club.toString());
        /**
         * Assuming in REST API SERVICE PUBID is never received..  we are sending RESTAPI as a pubId.
         */
       msisdn=validateMsisdn(msisdn, club);
       
        
        response=subscriptioncreation.checkSubscription(msisdn, club, campaignid, duration, subscription, networkid, externalId, landingpage,"RESTAPI");
        list=new SubscriptionList();
            list.setMsisdn(msisdn);
            list.setClub(club.getName());
            list.setMessage(response);
            list.setResponseCode("SUCCESS");
            if(response.trim().contains("SUBSCRIPTION RECORD CREATED SUCCESSFULLY"))
                list.setResponseCode("SUCCESS");
            if(response.trim().contains("SUBSCRIPTION RECORD ALREADY EXIST!"))
                list.setResponseCode("SUB_ALREADY_EXIST");
            if(response.trim().contains("USER IS BLOCKED SO NOT FORWARDING FOR SUBSCRIPTION"))
                list.setResponseCode("USER_BLOCKED");
            if(response.trim().contains("ERROR"))
                list.setResponseCode("ERROR");
            
            if(response.trim().equalsIgnoreCase("SUBSCRIPTION REQUEST RECEIVED SUCCESSFULLY"))
                list.setResponseCode("SUCCESS");
            
        
       }
        
        return jsonresponse.status(200).entity(list).build();
    }
    
    
    
    @GET
    @Path("/createsubscriptionRecord")
    @RolesAllowed({"ADMIN","MANAGER"})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createSubscriptionRecord(@QueryParam("msisdn")String msisdn,
            @QueryParam("serviceid") String clubunique,@QueryParam("campaignid") String campaignid,@QueryParam("duration") int duration, 
            @QueryParam("network") String networkid,@QueryParam("landingpage") String landingpage){
        
    System.out.println("restservicequery createSubscriptionRecord "+msisdn+" "+clubunique+" "+campaignid+" "+duration+" "+networkid+" "+landingpage);    
        
        SubscriptionList list=null; 
        String response=FAILURE_RESULT;
        MobileClub club=null;
        Response jsonresponse=null;
       
        if(msisdn==null || clubunique==null || msisdn.equals("") || clubunique.equals("")){
           
        response=FAILURE_RESULT+" MSISDN OR CLUB IS MISSING. PLEASE CHECK YOUR PARAMETERS";
        list=new SubscriptionList();
        list.setMsisdn(msisdn);
        list.setClub(clubunique);
        list.setResponseCode("MSISDN_CLUB_MISSING");
        list.setMessage(response);
         
       }
       else{
           
            club = UmeTempCmsCache.mobileClubMap.get(clubunique);
            System.out.println("restservicequery "+" clubobject is "+club.toString());
            response=subscriptioncreation.checkSubscription(msisdn, club, campaignid, duration, "", networkid, "", landingpage,"RESTAPI");
        
            list=new SubscriptionList();
            list.setMsisdn(msisdn);
            list.setClub(club.getName());
            list.setMessage(response);
            list.setResponseCode("SUCCESS");
            if(response.trim().contains("SUBSCRIPTION RECORD CREATED SUCCESSFULLY"))
                list.setResponseCode("SUCCESS");
            if(response.trim().contains("SUBSCRIPTION RECORD ALREADY EXIST!"))
                list.setResponseCode("SUB_ALREADY_EXIST");
            if(response.trim().contains("USER IS BLOCKED SO NOT FORWARDING FOR SUBSCRIPTION"))
                list.setResponseCode("USER_BLOCKED");
            if(response.trim().contains("ERROR"))
                list.setResponseCode("ERROR");
            
            
                   
                    
       }
        return jsonresponse.status(200).entity(list).build();
    }
      
   private String validateMsisdn(String msisdn,MobileClub club){
       
       if(club.getRegion().equalsIgnoreCase(("ZA"))){
           if(msisdn.contains("+")) msisdn=msisdn.replace("+","").trim();
           if(msisdn.startsWith("0")) msisdn="27" + msisdn.substring(1);
       }
       else if(club.getRegion().equalsIgnoreCase("KE")){
           if(msisdn.contains("+")) msisdn=msisdn.replace("+","").trim();
           if(msisdn.startsWith("0")) msisdn="254" + msisdn.substring(1);
       }
       else if(club.getRegion().equalsIgnoreCase("UK")){
           if(msisdn.contains("+")) msisdn=msisdn.replace("+","").trim();
           if(msisdn.startsWith("0")) msisdn="44" + msisdn.substring(1);
       }
       else if(club.getRegion().equalsIgnoreCase(("IE"))){
           if(msisdn.contains("+")) msisdn=msisdn.replace("+","").trim();
           if(msisdn.startsWith("0")) msisdn="353" + msisdn.substring(1);
       }
        else if(club.getRegion().equalsIgnoreCase(("IT"))){
           if(msisdn.contains("+")) msisdn=msisdn.replace("+","").trim();
           if(msisdn.startsWith("0")) msisdn="39" + msisdn.substring(1);
       }
       
       
       return msisdn;
   }
    
   
    
}
