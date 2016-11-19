package ume.pareva.webservice;

import java.text.SimpleDateFormat;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.MiscCr;
import ume.pareva.userservice.StopUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

@WebService(endpointInterface = "ume.pareva.webservice.MelodyAPI",
        serviceName = "UMEWebService")

public class MelodyAPIImpl implements MelodyAPI {

    final Logger logger = LogManager.getLogger(MelodyAPIImpl.class.getName());

    @Resource
    private WebServiceContext context;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Object fetchBean(String beanId) {
        Object bean = null;
        try {
            ServletContext servletContext = (ServletContext) context.getMessageContext().get("javax.xml.ws.servlet.context");
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            bean = webApplicationContext.getAutowireCapableBeanFactory().getBean(beanId);
        } catch (Exception e) {
            logger.fatal("No Bean Defined By ID {} {}",beanId,e.getMessage());
            logger.fatal("No Bean Defined By ID {}",beanId,e);
        }
        return bean;
    }

    @Override
    public String isUserActive(String login, String pwd, String user, String serviceId) {
        String userActive = "false";
        String deMsisdn ="";
        try {
            MobileClubDao mobileclubdao = null;
            UmeUserDao umeuserdao = null;
            WebServiceClient webServiceClient = null;
            webServiceClient = (WebServiceClientImpl) fetchBean("webserviceclientimpl");
            if (webServiceClient.authenticateClient(login, pwd)) {
                String msisdn = MiscCr.encrypt(user);
                deMsisdn = MiscCr.decrypt(user); //previously it was msisdn
                mobileclubdao = (MobileClubDao) fetchBean("mobileclubdao");
                umeuserdao = (UmeUserDao) fetchBean("umeuserdao");
                UmeUser umeUser = umeuserdao.getUser(deMsisdn);
                MobileClub club = mobileclubdao.getMobileClub(serviceId);

                SdcMobileClubUser clubUser = umeUser.getClubMap().get(club.getUnique()); //

                DateTime nowTime = new DateTime();
                DateTime billingRenew = new DateTime(clubUser.getBillingRenew().getTime());
                int days = Days.daysBetween(billingRenew, nowTime).getDays();
                /*System.out.println("Now Time: "+nowTime);
                 System.out.println("Billing Renew: "+clubUser.getBillingRenew());
                 System.out.println("NowTime - BillingRenwe: "+days);*/

                //if(MobileClubDao.isActive(umeUser,club)&& clubUser.getCredits()>0 &&days<=7)
                if (mobileclubdao.isActive(umeUser, club)) {
                    userActive = "true";
                }
            } else {
                userActive = "Authentication Error";
            }
        } catch (Exception e) {
            logger.fatal("Exception in isUserActive Method of Class MelodyAPIImpl {}---{}---{}",user,deMsisdn, e.getMessage());
            logger.fatal("Exception in isUserActive Method of Class MelodyAPIImpl ", e);
        }
        return userActive;
    }

    @Override
    public String creditDetail(String login, String pwd, String user, String serviceId, int credits) {
        String availableCredit = "0";
        try {
            UmeUserDao umeuserdao = null;
            UmeMobileClubUserDao umemobileclubuserdao = null;
            WebServiceClient webServiceClient = null;
            webServiceClient = (WebServiceClientImpl) fetchBean("webserviceclientimpl");
            if (webServiceClient.authenticateClient(login, pwd)) {
                String msisdn = MiscCr.encrypt(user);
                String deMsisdn = MiscCr.decrypt(user); //previously it was msisdn
                umeuserdao = (UmeUserDao) fetchBean("umeuserdao");
                umemobileclubuserdao = (UmeMobileClubUserDao) fetchBean("umemobileclubuserdao");
                UmeUser umeUser = umeuserdao.getUser(deMsisdn);
                SdcMobileClubUser clubUser = umeUser.getClubMap().get(serviceId);
                availableCredit = Integer.toString(clubUser.getCredits());
                umemobileclubuserdao.subtract(clubUser, credits);
            } else {
                availableCredit = "Authentication Error";
            }

        } catch (Exception e) {
            System.out.println("Exception in isUserActive Method of Class MelodyAPIImpl");
            e.printStackTrace();
        }
        return availableCredit;
    }

    @Override
    public boolean StopSingleSubscription(String login, String pwd, String msisdn, String clubUnique) {
        boolean stopped = false;
        WebServiceClient webServiceClient = null;
        StopUser stopuser = null;
        webServiceClient = (WebServiceClientImpl) fetchBean("webserviceclientimpl");
        stopuser = (StopUser) fetchBean("stopuser");
        System.out.println("Webservice Stop Single Subscription called upon " + login + " " + pwd + " " + msisdn + " " + clubUnique + "");
        if (webServiceClient.authenticateClient(login, pwd)) {
            String deMsisdn = MiscCr.decrypt(msisdn); //previously it was msisdn
            stopuser.stopSingleSubscription(deMsisdn, clubUnique, null, null);
            System.out.println("Webservice Stop Single Subscription called upon " + login + " " + pwd + " " + msisdn + " " + deMsisdn + " " + clubUnique);
            stopped = true;
        }
        System.out.println("Webservice The value of stop returned is " + stopped);
        return stopped;
    }

}
