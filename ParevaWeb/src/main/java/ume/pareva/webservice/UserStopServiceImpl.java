package ume.pareva.webservice;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
//import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import ume.pareva.userservice.StopUser;

/**
 *
 * @author madan
 */

@WebService(endpointInterface="ume.pareva.webservice.UserStopService",
serviceName="UMEStopService")

public class UserStopServiceImpl implements UserStopService {
    
    //final static Logger logger = Logger.getLogger(UserStopServiceImpl.class);
    
    @Resource
    private WebServiceContext context; 
    

    public Object fetchBean(String beanId){
		Object bean=null;
		try{
			ServletContext servletContext = (ServletContext) context.getMessageContext().get("javax.xml.ws.servlet.context");
			WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
			bean = webApplicationContext.getAutowireCapableBeanFactory().getBean(beanId);
		}catch(Exception e){
			System.out.println("No Bean Defined By ID: "+beanId);
			e.printStackTrace();
		}
		return bean;
	}

    @Override
    public boolean StopSingleSubscription(String login, String pwd, String msisdn, String clubUnique) {
        
        System.out.println("Webservice Stop Single Subscription called upon "+ login+" "+pwd+" "+msisdn+" "+clubUnique);
        //logger.info("WebService StopSingle Subscription "+msisdn+" clubUnique "+clubUnique);
        boolean stopped=false;
        WebServiceClient webServiceClient=null;
        StopUser stopuser=null;
        webServiceClient=(WebServiceClientImpl)fetchBean("webserviceclientimpl");
        stopuser=(StopUser) fetchBean("stopuser");
         System.out.println("Webservice Stop Single Subscription called upon "+ login+" "+pwd+" "+msisdn+" "+clubUnique+"");               
        if(webServiceClient.authenticateClient(login, pwd)){
         if(msisdn!=null && !"".equalsIgnoreCase(msisdn)){
        stopped=stopuser.stopSingleSubscription(msisdn, clubUnique, null, null);
        System.out.println("Webservice Stop Single Subscription called upon "+ login+" "+pwd+" "+msisdn+" "+clubUnique);
        
         }
        }
        System.out.println("Webservice The value of stop returned is "+stopped);
        return stopped;
    }

    @Override
    public boolean StopAllSubscription(String login, String pwd, String msisdn) {
        boolean stopped=false;
        WebServiceClient webServiceClient=null;
        StopUser stopuser=null;
        webServiceClient=(WebServiceClientImpl)fetchBean("webserviceclientimpl");
        stopuser=(StopUser) fetchBean("stopuser");
           //logger.info("WebService StopAllSubscription "+msisdn);              
        System.out.println("WebService StopAllSubscription "+msisdn);
        if(webServiceClient.authenticateClient(login, pwd)){
         if(msisdn!=null && !"".equalsIgnoreCase(msisdn)){
        stopped=stopuser.stopAllSubscription(msisdn, null, null);
       
         }
        }
        return stopped;
    }

    @Override
    public boolean bulkStop(String login, String pwd, List<String> msisdn) {
        boolean stopped=false;
        WebServiceClient webServiceClient=null;
        StopUser stopuser=null;
        webServiceClient=(WebServiceClientImpl)fetchBean("webserviceclientimpl");
        stopuser=(StopUser) fetchBean("stopuser");
          //logger.info(new Date()+" BULK STOP called with "+msisdn.size()+"  msisdn "+msisdn.toString());
          System.out.println("Webservice BULK STOP called with "+msisdn.size()+"  msisdn "+msisdn.toString());
        if(webServiceClient.authenticateClient(login, pwd)){
        if(msisdn!=null && !msisdn.isEmpty()){
       stopped=stopuser.bulkStop(msisdn, null, null);
       
        }
        }
       return stopped;
    }
    
}
