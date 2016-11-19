/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.servlet;

import java.util.Date;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ume.pareva.cms.MobileClub;

/**
 *
 * @author madan
 */

@Component("za24hour")
public class Check24Hour {
    
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
        
    public boolean hasValidDOIRequests(String msisdn, MobileClub club) {
        
/*        String sqlSmsDoiQuery = "SELECT requestedTime FROM smsDoiRequest WHERE msisdn='" + msisdn + "' "
                + " AND clubName LIKE '" + club.getName() + "%' "
                + " AND requestedTime > DATE_SUB(NOW(), INTERVAL 1 DAY) "
                + " ORDER BY requestedTime DESC limit 1";

        String sqlWapDoiQuery = "SELECT requestedTime FROM wapDoiRequest WHERE  msisdn='" + msisdn + "' "
                + " AND clubName LIKE '" + club.getName() + "%' "
                + " AND requestedTime > DATE_SUB(NOW(), INTERVAL 1 DAY) "
                + " ORDER BY requestedTime DESC LIMIT 1";
*/        
        
        String sqlSmsDoiQuery = "SELECT count(*) as count FROM smsDoiRequest WHERE msisdn='" + msisdn + "' "
                + " AND clubName LIKE '" + club.getName() + "%' "
                + " AND requestedTime > DATE_SUB(NOW(), INTERVAL 1 DAY)";

        String sqlWapDoiQuery = "SELECT count(*) as count FROM wapDoiRequest WHERE  msisdn='" + msisdn + "' "
                + " AND clubName LIKE '" + club.getName() + "%' "
                + " AND requestedTime > DATE_SUB(NOW(), INTERVAL 1 DAY)";


        Session session = null;
        //Date smsDoiDate, wapDoiDate;
        int smsDoiCount, wapDoiCount;
        try {
            session=openSession();
            Transaction txSMS=session.beginTransaction();
            
            Query querySms = session.createSQLQuery(sqlSmsDoiQuery).addScalar("count",StandardBasicTypes.INTEGER);
            System.out.println("SMSDOI QUERY: " + sqlSmsDoiQuery);
            smsDoiCount = (Integer)querySms.uniqueResult();
            txSMS.commit();

            Transaction txWAP = session.beginTransaction();
            Query queryWap = session.createSQLQuery(sqlWapDoiQuery).addScalar("count",StandardBasicTypes.INTEGER);
            System.out.println("WAPDOI QUERY: " + sqlWapDoiQuery);
            wapDoiCount = (Integer) queryWap.uniqueResult();
            txWAP.commit();
        } catch (Exception ex) {
            System.out.println("ERROR WHILE VERIFYING THE DOI REQUESTS: " + ex.getMessage()+" Query is "+sqlSmsDoiQuery+" ---- "+sqlWapDoiQuery);
            System.out.println("SMSDOI QUERY: " + sqlSmsDoiQuery);
            System.out.println("WAPDOI QUERY: " + sqlWapDoiQuery);
            System.out.println("ERROR WHILE VERIFYING THE DOI REQUESTS "+ex);
            smsDoiCount = 0;
            wapDoiCount = 0;
        } finally {
            session.close();
        }

        return (smsDoiCount > 3 || wapDoiCount > 3);
    }
    
    
}
