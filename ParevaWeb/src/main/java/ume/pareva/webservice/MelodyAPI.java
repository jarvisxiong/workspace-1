package ume.pareva.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface MelodyAPI {

	@WebMethod
	public String isUserActive(String login, String pwd, String user, String serviceId);
	@WebMethod
	public String creditDetail(String login, String pwd, String user, String serviceId, int credits);        
        @WebMethod
        public boolean StopSingleSubscription(@WebParam(name = "login") String login,@WebParam(name = "pwd") String pwd,@WebParam(name = "msisdn") String msisdn,@WebParam(name = "clubunique") String clubUnique);
}
