package ume.pareva.admin.quiz;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.dao.MobileClubBillingSuccessesDAO;
import ume.pareva.pojo.MobileClubBillingSuccesses;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Misc;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/DrawWinner")
public class DrawWinner extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private MobileClubBillingSuccessesDAO mobileClubBillingSuccessesDAO;
    private CompetitionDao competitionDao;
    private WinnerDao winnerDao;
    private UmeRequest umeRequest;
    private Random randomGenerator;
	
	public MobileClubBillingSuccessesDAO getMobileClubBillingSuccessesDAO() {
		return mobileClubBillingSuccessesDAO;
	}

	@Autowired
	public void setMobileClubBillingSuccessesDAO(
			MobileClubBillingSuccessesDAO mobileClubBillingSuccessesDAO) {
		this.mobileClubBillingSuccessesDAO = mobileClubBillingSuccessesDAO;
	}
	
	public CompetitionDao getCompetitionDao() {
		return competitionDao;
	}

	@Autowired
	public void setCompetitionDao(CompetitionDao competitionDao) {
		this.competitionDao = competitionDao;
	}

	public UmeRequest getUmeRequest() {
		return umeRequest;
	}
	
	@Autowired
	public void setUmeRequest(UmeRequest umeRequest) {
		this.umeRequest = umeRequest;
	}

	public WinnerDao getWinnerDao() {
		return winnerDao;
	}

	@Autowired
	public void setWinnerDao(WinnerDao winnerDao) {
		this.winnerDao = winnerDao;
	}

	public DrawWinner() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<MobileClubBillingSuccesses> mobileClubBillingSuccessesList=new ArrayList<MobileClubBillingSuccesses>();
		String startDate=umeRequest.get("start_date");
		String endDate=umeRequest.get("end_date");
		String competitionUnique=umeRequest.get("unique");
		Competition competition=competitionDao.getCompetitionByUnique(competitionUnique);
		String clubUnique=competition.getaClubUnique();
		mobileClubBillingSuccessesList=mobileClubBillingSuccessesDAO.getBillingSuccesses(startDate, endDate, clubUnique);
		randomGenerator=new Random();
		int index = randomGenerator.nextInt(mobileClubBillingSuccessesList.size());
		MobileClubBillingSuccesses mobileClubBillingSuccesses=mobileClubBillingSuccessesList.get(index);
		Winner winner=new Winner();
		winner.setaClubUnique(mobileClubBillingSuccesses.getClubUnique());
		winner.setaCompetitionUnique(competitionUnique);
		winner.setaDrawDate(new Date());
		winner.setaName("Mr. Winner");
		winner.setaParsedMobile(mobileClubBillingSuccesses.getParsedMobile());
		winner.setaPrize(competition.getaPrize());
		winner.setaUnique(Misc.generateUniqueId());
		winnerDao.saveWinner(winner);
		PrintWriter writer=response.getWriter();
		ObjectMapper mapper = new ObjectMapper();
		try
	      {
	         mapper.writeValue(writer, mobileClubBillingSuccesses);
	      } catch (JsonGenerationException e)
	      {
	         e.printStackTrace();
	      } catch (JsonMappingException e)
	      {
	         e.printStackTrace();
	      } catch (IOException e)
	      {
	         e.printStackTrace();
	      }
		}

}
