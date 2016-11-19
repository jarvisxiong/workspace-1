/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.ipx.extra;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.dao.SdcMiscDate;
import ume.pareva.sdk.Misc;

/**
 *
 * @author trung
 */
@Component("ipxuserstopdao")

public class IpxUserStopDao {
    @Autowired
    private SessionFactory sessionFactory;
    
    public SessionFactory getSessionFactory() {
          return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
    }

    private Session openSession(){     
        return sessionFactory.openSession();
    }
    
    
    public IpxUserStop getUserStop(String msisdn, String clubUnique){
        IpxUserStop item = null;
        
        try{
        
        }catch(Exception e){
            
        }
        
        return item;
    }
    
    public List<IpxUserStop> getUserStopToday(){
        List<IpxUserStop> list = new ArrayList<IpxUserStop>();
        
        try{
        
        }catch(Exception e){
            
        }
        return list;
    }
    
    public int addNewUserStop(IpxUserStop item){
        int stat = 0;
        
        if(item.getUnique()==null)
            item.setUnique(Misc.generateUniqueId());
                //inserting into cpalog. 
        String sqlString = "INSERT INTO ipxUserStop "
                + "(aUnique, aParsedMobile, aClubUnique, aCreated, aUnsubscribed, aExternalId,"
                + "aNetworkCode, aFrom, aStatus) VALUES("
                + "'" + item.getUnique()  + "',"
                + "'" + item.getParsedMobile() + "',"
                + "'" + item.getClubUnique() + "',"
                + "'" + SdcMiscDate.toSqlDate(item.getCreated()) + "',"
                + "'" + SdcMiscDate.toSqlDate(item.getUnsubscribed()) + "',"
                + "'" + item.getExternalId() + "',"
                + "'" + item.getNetworkCode() + "',"
                + "'" + item.getFrom() + "',"
                + "'" + item.getStatus() + "')";
        
        try {
            Session session=openSession();
            Transaction trans=session.beginTransaction();
            Query query=session.createSQLQuery(sqlString);
            stat=query.executeUpdate();
            trans.commit();
            session.close();
           
        } catch (Exception e) { 
            System.out.println("IPX addNewUserStop exception: " + e);
            e.printStackTrace();
        }
        return stat;
    }
    
}
