/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.userservice;

import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author madan
 */
@Component("checkstop")
public class CheckStop {

    @Autowired
    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session openSession() {
        return sessionFactory.openSession();
    }

    public boolean checkStopCount(String region) {
        boolean shouldStop = true;
        String sqlstr = "";
        Query query = null;
        Session session = openSession();
        Transaction trans = session.beginTransaction();
        String parsedMobilelike = "";
        if (region.equalsIgnoreCase("ZA")) {
            parsedMobilelike = "27";
        }

        if (region.equalsIgnoreCase("IT")) {
            parsedMobilelike = "39";
        }
        sqlstr = "SELECT COUNT(*) as count FROM mobileClubSubscribers WHERE date(aUnsubscribed)=date(now()) AND aParsedMobile like '" + parsedMobilelike + "%' AND aActive='0'";
        System.out.println("Stopconditionquery: " + sqlstr);
        query = session.createSQLQuery(sqlstr);
        List countList = query.list();
        int count = 0;
        for (Object o : countList) {
            count = Integer.parseInt(o.toString());

        }
        if (count > 1500) {
            shouldStop = false;
        }
        
        if(region.equalsIgnoreCase("IT") && count>2000){
            shouldStop = false;
        }

        trans.commit();
        session.close();
        return shouldStop;
    }

}
