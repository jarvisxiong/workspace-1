package ume.pareva.webservice;

import org.springframework.stereotype.Component;

@Component("webserviceclientimpl")
public class WebServiceClientImpl implements WebServiceClient {

	@Override
	public boolean authenticateClient(String login, String pwd) {
		// TODO Auto-generated method stub
		if((login.equals("melody")&& pwd.equals("Melody@pwd"))
                   || (login.equals("umeuser") && pwd.equals("ume@pwd123")) )
			return true;
		else
			return false;
	}

}
