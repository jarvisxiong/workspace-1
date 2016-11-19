package ume.pareva.restservices;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import ume.pareva.userservice.InternetServiceProvider;

/**
 *
 * @author madan
 */
@Provider
public class JaxRsFilterAuthentication implements ContainerRequestFilter {
    
     //private static final Logger LOG = LoggerFactory.getLogger(JaxRsFilterAuthentication.class);
    
    @Context
    private ResourceInfo resourceInfo;
    
    @Context
    private HttpServletRequest request;
    
    @Autowired
    AuthenticationService authenticationService;
    
    @Autowired
    InternetServiceProvider ipprovider;
     
    private static final String AUTHENTICATION_HEADER = "Authorization";        
    private static final String AUTHORIZATION_PROPERTY = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";
    private static final String ACCESS_DENIED ="You cannot access this resource";
    private static final String ACCESS_FORBIDDEN ="Access blocked for all users !!";

    
    
    
    
    
    
	@Override
	public void filter(ContainerRequestContext containerRequest) throws WebApplicationException {

            //log.info("REST-Request from '{}' for '{}'" "XXX", containerRequest.getUriInfo().getPath());
            System.out.println("restservicequery REST-Request from "+containerRequest.getUriInfo().getPath());
            Method method = resourceInfo.getResourceMethod();
		String authCredentials = containerRequest.getHeaderString(AUTHENTICATION_HEADER);
         
             Response jsonresponse=null;
            MessageList responselist=null;
                
            //========== IP RESTRICTIONS ======================= 
                String myip=request.getHeader("X-Forwarded-For");
                if(myip==null) myip=request.getRemoteAddr();
                if (myip != null) {           
                int idx = myip.indexOf(',');
                if (idx > -1) {
                myip = myip.substring(0, idx);
                    }
                }
               String ispname=ipprovider.findIsp(myip);
                System.out.println("restservicequery  request from IP "+myip+" ISP "+ispname);
                
                if(!authenticationService.ipwhitelist(myip)) {
                    responselist=new MessageList();
                    responselist.setResponseCode("FAILURE");
                    responselist.setMessage(ACCESS_FORBIDDEN);
                      containerRequest.abortWith(jsonresponse.status(Response.Status.FORBIDDEN).entity(responselist).build());
                        return;
                }
                
            //========== END IP RESTRICTIONS ===================
                
            
                
                //Access Allowed for All 
            if(!method.isAnnotationPresent(PermitAll.class))
                {
                        //Access denied for all
                        if(method.isAnnotationPresent(DenyAll.class))
                    {
                         responselist=new MessageList();
                    responselist.setResponseCode("FAILURE");
                    responselist.setMessage(ACCESS_FORBIDDEN);
                        containerRequest.abortWith(jsonresponse.status(Response.Status.FORBIDDEN).entity(responselist).build());
                        
                        return;
                    }
                        
                //Get request headers
                final MultivaluedMap<String, String> headers = containerRequest.getHeaders();
              
                //Fetch authorization header
                final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
              
                //If no authorization information present; block access
                if(authorization == null || authorization.isEmpty()) {
                     responselist=new MessageList();
                    responselist.setResponseCode("FAILURE");
                    responselist.setMessage(ACCESS_DENIED);
                    containerRequest.abortWith(jsonresponse.status(Response.Status.UNAUTHORIZED).entity(responselist).build());
                    return;
                    }
                
                
               

		
                
            //Verify user access
            if(method.isAnnotationPresent(RolesAllowed.class))
            {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
                
                // better injected
		//AuthenticationService authenticationService = new AuthenticationService();
		boolean authenticationStatus = authenticationService.authenticate(authCredentials,rolesSet);
                
		if (!authenticationStatus) {
                     responselist=new MessageList();
                    responselist.setResponseCode("FAILURE");
                    responselist.setMessage(ACCESS_DENIED);
                    
			containerRequest.abortWith(jsonresponse.status(Response.Status.UNAUTHORIZED).entity(responselist).build());
		}
                
            }
                
          } //END PERMIT ALL Condition 

	}
        
}
