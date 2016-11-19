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

@Component("winnerDao")
public class WinnerDao {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private SessionFactory sessionFactory;

	private final Logger logger = LogManager.getLogger(WinnerDao.class
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

	public List<Winner> getAllWinners() {
		List<Winner> winnerList = new ArrayList<Winner>();
		Session session = null;
		SQLQuery query = null;
		try{
			session = openSession();
			session.beginTransaction();
			query = session.createSQLQuery("SELECT * FROM winner order by aDrawDate desc");
			List result = query.list();
			for (Object o : result) {
				Object[] row = (Object[]) o;
				Winner winner= new Winner();
				getWinner(winner, row);
				winnerList.add(winner);
			}
			session.getTransaction().commit();
		}catch (Exception e) {
			logger.error("EXCEPTION {}", e.getMessage());
			logger.error("SQL QUERY {}", query.toString());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return winnerList;

	}
	
	public List<Winner> getWinnerByCompetitionUnique(String aUnique){
		List<Winner> winnerList=new ArrayList<Winner>();
		Winner winner=null;
		Session session = null;
		SQLQuery query = null;
		try{
			session = openSession();
			session.beginTransaction();
			query = session.createSQLQuery("SELECT * FROM winner where aCompetitionUnique = :aUnique order by aDrawDate desc");
			query.setParameter("aUnique",aUnique);
			List result = query.list();
			for (Object o : result) {
				Object[] row = (Object[]) o;
				winner = new Winner();
				getWinner(winner, row);
				winnerList.add(winner);
			}
			session.getTransaction().commit();
		}catch (Exception e) {
			logger.error("EXCEPTION {}", e.getMessage());
			logger.error("SQL QUERY {}", query.toString());
			e.printStackTrace();
		} finally {
			session.close();
		}
		return winnerList;
	}

	public void saveWinner(Winner winner) {
		Session session = null;
		SQLQuery query = null;
		try {
			session = openSession();
			query = session
					.createSQLQuery("INSERT INTO winner (aUnique,aName,aParsedMobile,aCompetitionUnique,aClubUnique,aDrawDate,aPrize) values "
							+ "(:aUnique,:aName,:aParsedMobile,:aCompetitionUnique,:aClubUnique,:aDrawDate,:aPrize)");
			session.beginTransaction();
				query.setParameter("aUnique", winner.getaUnique());
				query.setParameter("aName", winner.getaName());
				query.setParameter("aParsedMobile", winner.getaParsedMobile());
				query.setParameter("aCompetitionUnique", winner.getaCompetitionUnique());
				query.setParameter("aClubUnique", winner.getaClubUnique());
				query.setParameter("aDrawDate", SdcMiscDate.toSqlDate(winner.getaDrawDate()));
				query.setParameter("aPrize", winner.getaPrize());
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
	
	public void getWinner(Winner winner, Object[] row) {
		try {
			winner.setaUnique(String.valueOf(row[0]));
			winner.setaName(String.valueOf(row[1]));
			winner.setaParsedMobile(String.valueOf(row[2]));
			winner.setaCompetitionUnique(String.valueOf(row[3]));
			winner.setaClubUnique(String.valueOf(row[4]));
			winner.setaDrawDate(SdcMiscDate.parseSqlDate(String.valueOf(row[5])));
			winner.setaPrize(String.valueOf(row[6]));
		} catch (Exception e) {
			logger.error("Error Creating Competition Object");
			e.printStackTrace();
		}

	}

}
