package ume.pareva.restservices;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.dao.MobileClubBillingPlanDao;

/**
 *
 * @author madan
 */
@Component
@Path("/QueryService")
public class QueryServices {
    
  
    @Autowired
    MobileClubBillingPlanDao queryservicedao;
    
   private static final String SUCCESS_RESULT="<result>success</result>";
   private static final String FAILURE_RESULT="<result>failure</result>";
   
  

    

   @GET
   @Path("/msisdn")
   @RolesAllowed("ADMIN")
   @Produces({MediaType.APPLICATION_JSON})
   public Response getBillableMsisdn(@QueryParam("region") String Region, @QueryParam("sqlquery") String sqlstr){
       System.out.println("restservicequery "+sqlstr);
       
      List<String> msisdnlist=queryservicedao.getMsisdn(Region, sqlstr);
      ParsedList list=new ParsedList();
      list.setId(1);
      list.setMsisdn(msisdnlist.get(0));
      System.out.println("restservicequery list value "+list.toString());
      return Response.status(200).entity(list).build();
      //return list;
    
      
      //return Response.ok(listofmsisdn).build();
   }
}