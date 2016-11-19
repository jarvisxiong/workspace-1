package ume.pareva.userservice;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

public class InternetServiceProvider {
	
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

	
	public String findIsp(String ipaddress){
		
		String ispnetwork = "";
		Session session = null; 
		try {
                    session=openSession();
                    Transaction trans = session.beginTransaction();

			String findipquery = "SELECT isp FROM ispLookup WHERE INET_ATON('"+ ipaddress + "') BETWEEN ip_start AND ip_end LIMIT 1";
                        //System.out.println("====INTERNET SERVICE PROVIDER "+findipquery);
			Query query = session.createSQLQuery(findipquery)
					.addScalar("isp");
			java.util.List isplist = query.list();
			query.setCacheable(true);
			if (isplist.size() > 0) {
				for (Object o : isplist) {
					String row = o.toString();
					ispnetwork = row;
				}

			}
                        trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//System.out.println("wapzatest test: " + " globalwapfunctions.jsp :> isp : " + ispnetwork);

		finally{
		session.close();
                }
		return ispnetwork;
	}
		
	
}
