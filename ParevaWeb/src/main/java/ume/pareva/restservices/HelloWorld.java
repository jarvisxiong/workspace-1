package ume.pareva.restservices;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import ume.pareva.dao.MobileClubBillingPlanDao;


@Path("/parevatest")
public class HelloWorld {
  

	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response sayXMLHello() {
            ParsedList list=new ParsedList();
      list.setId(1);
      list.setMsisdn("227712345678");
      System.out.println("restservicequery helloworld list value "+list.toString());
      return Response.status(200).entity(list).build(); //
	}

	

}