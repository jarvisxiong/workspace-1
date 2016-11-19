package ume.pareva.uk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.dao.SdcMiscDate;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;

/**
 *
 * @author madan
 */

@Component("uksuccessdao")
public class UKSuccessDao {
    	
    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    UmeTempCache umesdc;
    
        
    public SessionFactory getSessionFactory() {
          return sessionFactory;
    }


    public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
    }


    private Session openSession(){
         
        return sessionFactory.openSession();
    }
    
    	public void saveSuccessfulUser(UKSuccess uksuccess){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
//                   
        	Session session=openSession();			
			Transaction trans=session.beginTransaction();
			
            String sqlstr = "INSERT INTO ukSuccess(aUnique,tid,aClubUnique,aCampaignId,aStatus,aType,aSid,aParsedMobile,aNetworkId,aHash,aCreated,aExpiry)"
                    + " VALUES ("
                    + "'" + Misc.generateUniqueId() + "'"
                    + ",'" + uksuccess.getTid() + "'"
                    + ",'" + uksuccess.getClubUnique() + "'"
                    + ",'" + uksuccess.getCampaignId() + "'"
                    + ",'" + uksuccess.getStatus() + "'"
                    + ",'" + uksuccess.getType() + "'"
                    + ",'" + uksuccess.getSid() + "'"
                    + ",'" + uksuccess.getaParsedMobile() + "'"
                    + ",'" + uksuccess.getaNetworkId() + "'"
                    + ",'" + uksuccess.getaHash() + "'"
                    + ",'" + SdcMiscDate.toSqlDate(new Date()) + "'"
                     + ",'" + uksuccess.getExpiry() + "'"
                    + ")";
            
            System.out.println("xstreamtesting: "+sqlstr);
          Query query=session.createSQLQuery(sqlstr);
          int row=query.executeUpdate();
           trans.commit();
           session.close();
        }
        catch (Exception e) { System.out.println("xstreamtesting: Error Saving UK Success " + e); 
        e.printStackTrace();
        }
	}
        
        public void updateExpiryDate(UKSuccess uksuccess){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
//                   
        	Session session=openSession();			
			Transaction trans=session.beginTransaction();
			
            String sqlstr = "UPDATE ukSuccess SET aExpiry='"+formatter.format(uksuccess.getExpiry())+"' WHERE aParsedMobile='"+uksuccess.getaParsedMobile()+"'"
                    + " AND aClubUnique='"+uksuccess.getClubUnique()+"'";
                               
            System.out.println("xstreamtesting: "+sqlstr);
          Query query=session.createSQLQuery(sqlstr);
          int row=query.executeUpdate();
           trans.commit();
           session.close();
        }
        catch (Exception e) { System.out.println("xstreamtesting: Error Saving UK Success " + e); 
        e.printStackTrace();
        }
	}
        
        
  public UKSuccess getDetails(String msisdn){
      UKSuccess item=null;
      Query query=null;
        String sqlstr;
                                
        try {        
        	Session session=openSession();
        	Transaction trans=session.beginTransaction();
            sqlstr = "SELECT * FROM ukSuccess WHERE aParsedMobile='" + msisdn + "' ORDER by aCreated desc LIMIT 1";                        
            //System.out.println(sqlstr);
            query=session.createSQLQuery(sqlstr);
            List result=query.list();          
            for(Object o:result) {
                Object[] row=(Object[]) o;
                item = new UKSuccess();
                getUKDetails(item, row);
            }
                     
            
        }
        catch (Exception e) { System.out.println(e); e.printStackTrace(); }
                
        return item;
    
  }
  
    public UKSuccess getSubscriptionDetails(String msisdn){
      UKSuccess item=null;
      Query query=null;
        String sqlstr;
                                
        try {        
        	Session session=openSession();
        	Transaction trans=session.beginTransaction();
            sqlstr = "SELECT * FROM ukSuccess WHERE aParsedMobile='" + msisdn + "' AND aStatus='1' AND aType='sub' ORDER by aCreated desc LIMIT 1";                        
            //System.out.println(sqlstr);
            query=session.createSQLQuery(sqlstr);
            List result=query.list();          
            for(Object o:result) {
                Object[] row=(Object[]) o;
                item = new UKSuccess();
                getUKDetails(item, row);
            }
                     
            
        }
        catch (Exception e) { System.out.println(e); e.printStackTrace(); }
                
        return item;
    
  }
  
    public UKSuccess checkTid(String tid){
      UKSuccess item=null;
      Query query=null;
        String sqlstr;
                                
        try {        
        	Session session=openSession();
        	Transaction trans=session.beginTransaction();
            sqlstr = "SELECT * FROM ukSuccess WHERE tid='" + tid + "' ORDER by aCreated desc LIMIT 1";                        
            //System.out.println(sqlstr);
            query=session.createSQLQuery(sqlstr);
            List result=query.list();          
            for(Object o:result) {
                Object[] row=(Object[]) o;
                item = new UKSuccess();
                getUKDetails(item, row);
            }
                     
            
        }
        catch (Exception e) { System.out.println(e); e.printStackTrace(); }
                
        return item;
    
  }
  
  
  public void getUKDetails(UKSuccess item, Object[]row)
  {
        item.setaUnique((String) row[0]);
        item.setTid((String)row[1]);
        item.setClubUnique((String)row[2]);
        item.setCampaignId((String)row[3]);
        item.setStatus(String.valueOf(row[4]));
        item.setType(String.valueOf(row[5]));
        item.setSid(String.valueOf(row[6]));
        item.setaParsedMobile((String) row[7]);
        item.setaNetworkId((String) row[8]);
        item.setaHash((String) row[9]);
        item.setCreated((Date) row[10]);
        item.setExpiry((String) row[11]);
  }
    
  
  public boolean tidExist(String tid){
    boolean exist=false;
    String sqlstr="select * from uk_notification_info where paramName='tid' and paramValue='"+tid+"'";
    Query query=null;
    Session session=openSession();
    
    try{
        Transaction trans=session.beginTransaction();
        query=session.createSQLQuery(sqlstr);
        List tidresult=query.list();   
        if(tidresult==null || (tidresult!=null && tidresult.size()<=0))  exist=false;
        else exist=true;
    }
    catch(Exception e){exist=false; e.printStackTrace();}
    finally{
        session.close();
    }
    return exist;
    }
}
