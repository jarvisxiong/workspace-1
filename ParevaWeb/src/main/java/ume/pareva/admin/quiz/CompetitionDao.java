package ume.pareva.admin.quiz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ume.pareva.dao.SdcMiscDate;
import ume.pareva.pojo.QuestionPack;

@Component("competitionDao")
public class CompetitionDao {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private SessionFactory sessionFactory;

	private final Logger logger = LogManager.getLogger(CompetitionDao.class
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

	public List<Competition> getAllCompetitions() {
		List<Competition> competitionList = new ArrayList<Competition>();
		Session session = null;
		SQLQuery query = null;
		try{
			session = openSession();
			session.beginTransaction();
			query = session.createSQLQuery("SELECT * FROM competition order by aCreated desc");
			List result = query.list();
			for (Object o : result) {
				Object[] row = (Object[]) o;
				Competition competition= new Competition();
				getCompetition(competition, row);
				competitionList.add(competition);
			}
			session.getTransaction().commit();
		}catch (Exception e) {
			logger.error("EXCEPTION {}", e.getMessage());
			logger.error("SQL QUERY {}", query.toString());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return competitionList;

	}
	
	public Competition getCompetitionByUnique(String aUnique){
		Competition competition=null;
		Session session = null;
		SQLQuery query = null;
		try{
			session = openSession();
			session.beginTransaction();
			query = session.createSQLQuery("SELECT * FROM competition where aUnique = :aUnique order by aCreated desc");
			query.setParameter("aUnique",aUnique);
			List result = query.list();
			for (Object o : result) {
				Object[] row = (Object[]) o;
				competition = new Competition();
				getCompetition(competition, row);
			}
			session.getTransaction().commit();
		}catch (Exception e) {
			logger.error("EXCEPTION {}", e.getMessage());
			logger.error("SQL QUERY {}", query.toString());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return competition;
	}

	public void saveCompetition(Competition competition) {
		Session session = null;
		SQLQuery query = null;
		try {
			session = openSession();
			query = session
					.createSQLQuery("INSERT INTO competition (aUnique,aName,aMarket,aPeriod,aStartDate,aFrequency,aPrize,aClubUnique,aStatus,aCreated) values "
							+ "(:aUnique,:aName,:aMarket,:aPeriod,:aStartDate,:aFrequency,:aPrize,:aClubUnique,:aStatus,:aCreated)");
			session.beginTransaction();
				query.setParameter("aUnique", competition.getaUnique());
				query.setParameter("aName", competition.getaName());
				query.setParameter("aMarket", competition.getaMarket());
				query.setParameter("aPeriod", competition.getaPeriod());
				query.setParameter("aStartDate", SdcMiscDate.toSqlDate(competition.getaStartDate()));
				query.setParameter("aFrequency", competition.getaFrequency());
				query.setParameter("aPrize", competition.getaPrize());
				query.setParameter("aClubUnique", competition.getaClubUnique());
				query.setParameter("aStatus", competition.isaStatus()?1:0);
				query.setParameter("aCreated", SdcMiscDate.toSqlDate(competition.getaCreated()));
				
				query.executeUpdate();
			session.getTransaction().commit();
		} catch (Exception e) {
			logger.error("EXCEPTION {}", e.getMessage());
			logger.error("SQL QUERY {}", query.toString());
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void updateCompetition(Competition competition) {
		Session session = null;
		SQLQuery query = null;
		try {
			session = openSession();
			query = session.createSQLQuery("UPDATE competition set "
					+ "aName=:aName," 
					+ "aMarket=:aMarket," 
					+ "aPeriod=:aPeriod,"
					+ "aStartDate=:aStartDate," 
					+ "aFrequency=:aFrequency,"
					+ "aPrize=:aPrize,"
					+ "aClubUnique=:aClubUnique,"
					+ "aStatus=:aStatus"
					+ " where aUnique=:aUnique");
			session.beginTransaction();
			query.setParameter("aUnique", competition.getaUnique());
			query.setParameter("aName", competition.getaName());
			query.setParameter("aMarket", competition.getaMarket());
			query.setParameter("aPeriod", competition.getaPeriod());
			query.setParameter("aStartDate", SdcMiscDate.toSqlDate(competition.getaStartDate()));
			query.setParameter("aFrequency", competition.getaFrequency());
			query.setParameter("aPrize", competition.getaPrize());
			query.setParameter("aClubUnique", competition.getaClubUnique());
			query.setParameter("aStatus", competition.isaStatus()?1:0);
			query.executeUpdate();
			session.getTransaction().commit();
		} catch (Exception e) {
			logger.error("EXCEPTION {}", e.getMessage());
			logger.error("SQL QUERY {}", query.toString());
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void deleteCompetition(Competition competition) {
		Session session = null;
		SQLQuery query = null;
		try {
			session = openSession();
			query = session
					.createSQLQuery("DELETE from competition where aUnique=:aUnique");
			session.beginTransaction();
			query.setParameter("aUnique", competition.getaUnique());
			query.executeUpdate();
			session.getTransaction().commit();
		} catch (Exception e) {
			logger.error("EXCEPTION {}", e.getMessage());
			logger.error("SQL QUERY {}", query.toString());
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public int saveQuestionPacksInCompetition(String competitionUnique,List<String> questionPackList){
		int savedRows=0;
		Session session = null;
		SQLQuery query = null;
		try {
			session = openSession();
			query = session
					.createSQLQuery("INSERT INTO questionPackInCompetition(aCompetitionUnique,aQuestionPackUnique) VALUES (:aCompetitionUnique,:aQuestionPackUnique)");
			session.beginTransaction();
			for(String questionPackUnique:questionPackList){
				query.setParameter("aCompetitionUnique", competitionUnique);
				query.setParameter("aQuestionPackUnique", questionPackUnique);
				savedRows=savedRows+query.executeUpdate();
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			logger.error("EXCEPTION {}", e.getMessage());
			logger.error("SQL QUERY {}", query.toString());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return savedRows;

		
	}
	
	public List<String> getQuestionPacksInCompetition(String aUnique){
		List<String> questionPackList= new ArrayList<String>();
		Session session = null;
		SQLQuery query = null;
		try{
			session = openSession();
			session.beginTransaction();
			query = session.createSQLQuery("SELECT * FROM questionPackInCompetition where aCompetitionUnique=:aUnique");
			query.setParameter("aUnique",aUnique);
			List result = query.list();
			for (Object o : result) {
				Object[] row = (Object[]) o;
				questionPackList.add(String.valueOf(row[1]));
			}
			session.getTransaction().commit();
		}catch (Exception e) {
			logger.error("EXCEPTION {}", e.getMessage());
			logger.error("SQL QUERY {}", query.toString());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return questionPackList;
	}
	
	public void getCompetition(Competition competition, Object[] row) {
		try {
			competition.setaUnique(String.valueOf(row[0]));
			competition.setaName(String.valueOf(row[1]));
			competition.setaMarket(String.valueOf(row[2]));
			competition.setaPeriod(String.valueOf(row[3]));
			competition.setaStartDate(SdcMiscDate.parseSqlDate(String.valueOf(row[4])));
			competition.setaFrequency(Integer.parseInt(String.valueOf(row[5])));
			competition.setaPrize(String.valueOf(row[6]));
			competition.setaClubUnique(String.valueOf(row[7]));
			competition.setaStatus(Boolean.parseBoolean(String.valueOf(row[8]).equals("1")?"true":"false"));
			competition.setaCreated(SdcMiscDate.parseSqlDate(String.valueOf(row[9])));
		} catch (Exception e) {
			logger.error("Error Creating Competition Object");
			e.printStackTrace();
		}

	}

}
