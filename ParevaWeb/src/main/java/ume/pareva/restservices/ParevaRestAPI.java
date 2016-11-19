package ume.pareva.restservices;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;


public class ParevaRestAPI extends ResourceConfig { 

	public ParevaRestAPI(){
		register(RequestContextFilter.class);
		register(HelloWorld.class);
		register(QueryServices.class);
		register(JacksonFeature.class);
                register(SubscriptionService.class);
                register(StopService.class);
                register(UserServices.class);
                 //Registering Auth Filter here
                register(JaxRsFilterAuthentication.class);
	}
}

