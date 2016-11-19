package ume.pareva.admin.wapsiteadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component("wapSiteAdminDao")
public class WapSiteAdminDao {

	@Autowired
	private SessionFactory sessionFactory;

	private final Logger logger = LogManager.getLogger(WapSiteAdminDao.class
			.getName());

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session openSession() {
		return sessionFactory.openSession();
	}

	public List<Map<String,String>> checkIfLandingPageUsedByCampaigns(String landingPage) {
		List<Map<String,String>> campaignList = new ArrayList<Map<String,String>>();
		Session session = null;
		SQLQuery query = null;
		try {
			session = openSession();
			session.beginTransaction();
			query = session.createSQLQuery("select * from mobileClubCampaignsLandingPages mcclp, mobileClubCampaigns mcc where mcc.aUnique=mcclp.aCampaignUnique and " 
					+"aLandingPageCode=:landingPage").addScalar("aUnique").addScalar("aCampaign");
			query.setParameter("landingPage", landingPage);
			List result = query.list();
			for (Object o : result) {
				Object[] row = (Object[]) o;
				Map<String,String> campaignMap=new HashMap<String,String>();
				campaignMap.put("aUnique",String.valueOf(row[0]));
				campaignMap.put("aCampaign",String.valueOf(row[1]));
				campaignList.add(campaignMap);
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			logger.error("EXCEPTION {}", e.getMessage());
			logger.error("SQL QUERY {}", query.toString());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return campaignList;

	}

	public int deleteLandingPage(String landingPage,String domain){
		int deletedRows=0;
		Session session = null;
		SQLQuery query = null;
		try {
			session = openSession();
			session.beginTransaction();
			query = session.createSQLQuery("delete from domainsLandingPages where aDomainUnique=:domain and aLandingPage=:landingPage");
			query.setParameter("landingPage", landingPage);
			query.setParameter("domain",domain);
			deletedRows=query.executeUpdate();
			/*if(usedInCampaign){
				query = session.createSQLQuery("delete from mobileClubCampaignsLandingPages where aLandingPageCode=:landingPage");
				query.setParameter("landingPage", landingPage);
				deletedRows=query.executeUpdate();
			}*/
			
			session.getTransaction().commit();
		} catch (Exception e) {
			logger.error("EXCEPTION {}", e.getMessage());
			logger.error("SQL QUERY {}", query.toString());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return deletedRows;
	}

}
