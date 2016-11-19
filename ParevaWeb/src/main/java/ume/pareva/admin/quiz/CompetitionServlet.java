package ume.pareva.admin.quiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.QuestionDao;
import ume.pareva.dao.QuestionPackDao;
import ume.pareva.dao.SdcMiscDate;
import ume.pareva.pojo.Question;
import ume.pareva.pojo.QuestionPack;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.util.DateUtil;

@WebServlet("/Competition")
public class CompetitionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UmeRequest umeRequest;
	private CompetitionDao competitionDao;
	private QuestionPackDao questionPackDao;
	private WinnerDao winnerDao;
	private MobileClubDao mobileClubDao;
	private QuestionDao questionDao;
	private List<Competition> competitionList=new ArrayList<Competition>();
	private List<String> questionPackList=new ArrayList<String>();  

	public UmeRequest getUmeRequest() {
		return umeRequest;
	}

	@Autowired
	public void setUmeRequest(UmeRequest umeRequest) {
		this.umeRequest = umeRequest;
	}

	public CompetitionDao getCompetitionDao() {
		return competitionDao;
	}

	@Autowired
	public void setCompetitionDao(CompetitionDao competitionDao) {
		this.competitionDao = competitionDao;
	}

	public QuestionPackDao getQuestionPackDao() {
		return questionPackDao;
	}

	@Autowired
	public void setQuestionPackDao(QuestionPackDao questionPackDao) {
		this.questionPackDao = questionPackDao;
	}

	public WinnerDao getWinnerDao() {
		return winnerDao;
	}

	@Autowired
	public void setWinnerDao(WinnerDao winnerDao) {
		this.winnerDao = winnerDao;
	}

	public MobileClubDao getMobileClubDao() {
		return mobileClubDao;
	}

	@Autowired
	public void setMobileClubDao(MobileClubDao mobileClubDao) {
		this.mobileClubDao = mobileClubDao;
	}
	
	public QuestionDao getQuestionDao() {
		return questionDao;
	}

	@Autowired
	public void setQuestionDao(QuestionDao questionDao) {
		this.questionDao = questionDao;
	}

	public CompetitionServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Map<String,Object>> competitions=new ArrayList<Map<String,Object>>();
		List<Winner> winnerList=new ArrayList<Winner>();
		String action=umeRequest.get("action");
		if(action.equals("Update")){
			String aUnique=umeRequest.get("unique");
			Competition competition=competitionDao.getCompetitionByUnique(aUnique);
			List<MobileClub> mobileClubs=mobileClubDao.getMobileClubByRegion(competition.getaMarket());
			request.setAttribute("action",action);
			request.setAttribute("competition",getCompetitionDetails(competition));
			request.setAttribute("mobileClubs",mobileClubs);

		}else{


			competitionList=competitionDao.getAllCompetitions();
			for(Competition competition:competitionList){
				competitions.add(getCompetitionDetails(competition));
				/*Map<String,Object> competitionMap=new HashMap<String,Object>();
				questionPackList=competitionDao.getQuestionPacksInCompetition(competition.getaUnique());
				int numberOfQuestions=0;
				for(String aUnique:questionPackList){
					numberOfQuestions=numberOfQuestions+questionPackDao.getNumberOfQuestionsInPack(aUnique);
				}
				winnerList=winnerDao.getWinnerByCompetitionUnique(competition.getaUnique());
				Date nextDrawDate=null;
				Date lastDrawDate=null;
				if(!winnerList.isEmpty()){
					Date drawDate=winnerList.get(0).getaDrawDate();
					nextDrawDate=getNextDrawDate(drawDate, competition.getaPeriod());
					lastDrawDate=drawDate;
				}else{
					nextDrawDate=getNextDrawDate(competition.getaStartDate(),competition.getaPeriod());
				}
				if(!questionPackList.isEmpty())
					competitionMap.put("competitionLanguage",questionPackDao.getQuestionPackByUnique(questionPackList.get(0)).getaLanguage());
				else
					competitionMap.put("competitionLanguage","");
				competitionMap.put("competition",competition);
				competitionMap.put("numberOfQuestions",numberOfQuestions);
				competitionMap.put("winnerList", winnerList);
				if(nextDrawDate!=null)
					competitionMap.put("nextDrawDate", nextDrawDate);
				else
					competitionMap.put("nextDrawDate", "");
				competitions.add(competitionMap);*/
				request.setAttribute("competitions",competitions);
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action=umeRequest.get("action");
		if(action.equals("Update")){
			String unique=umeRequest.get("unique");
			String name=umeRequest.get("name");
			String market=umeRequest.get("market");
			String period=umeRequest.get("period");
			String frequency=umeRequest.get("frequency");
			String prize=umeRequest.get("prize");
			String club=umeRequest.get("club");
			String startDate=umeRequest.get("start_date");
			String status=umeRequest.get("status");
			String[] questionPacks=request.getParameterValues("question_pack");
			List<String> questionPackList=Arrays.asList(questionPacks);
			System.out.println("Competition Status: "+status);
			Competition competition=new Competition();
			competition.setaUnique(unique);
			competition.setaName(name);
			competition.setaMarket(market);
			competition.setaPeriod(period);
			competition.setaFrequency((!frequency.equals(""))?Integer.parseInt(frequency):0);
			competition.setaPrize(prize);
			competition.setaClubUnique(club);
			competition.setaStartDate((!startDate.equals(""))?SdcMiscDate.parseSqlDate(startDate):SdcMiscDate.parseSqlDate("0000-00-00 00:00:00"));
			System.out.println("Start Date: "+competition.getaStartDate());
			competition.setaStatus((!status.equals(""))?true:false);
			competitionDao.updateCompetition(competition);
			competitionDao.saveQuestionPacksInCompetition(unique, questionPackList);
		}
	}

	public Date addDays(Date date,int daysToAdd){
		Calendar c = Calendar.getInstance();
		c.setTime(date); // Now use today date.
		c.add(Calendar.DATE, daysToAdd); // Adding 5 days
		return c.getTime();
	}

	public Date getNextDrawDate(Date date,String period){
		Date nextDrawDate=null;
		if(period.equals("Daily")){
			nextDrawDate=addDays(date,1);
		}else if(period.equals("Weekly")){
			nextDrawDate=addDays(date,7);
		}else if(period.equals("Monthly")){
			int month=DateUtil.getMonth(date);
			if(month==1||month==3||month==5||month==7||month==8||month==10||month==12)
				nextDrawDate=addDays(date,31);
			else if (month==4||month==6||month==9||month==11)
				nextDrawDate=addDays(date,30);
			else if (month==2){
				int year=DateUtil.getYear(date);
				if(year%4==0){
					nextDrawDate=addDays(date,29);
				}else{
					nextDrawDate=addDays(date,28);
				}
			}
		}else if(period.equals("Annual")){
			int year=DateUtil.getYear(date);
			if(year%4==0){
				nextDrawDate=addDays(date,366);
			}else{
				nextDrawDate=addDays(date,365);
			}
		}
		return nextDrawDate;
	}
	
	public Map<String,Object> getCompetitionDetails(Competition competition){
		Map<String,Object> competitionMap=new HashMap<String,Object>();
		//List<Question> questionList=new ArrayList<Question>();
		List<Map<String,Object>> questionList=new ArrayList<Map<String,Object>>();
		questionPackList=competitionDao.getQuestionPacksInCompetition(competition.getaUnique());
		int numberOfQuestions=0;
		for(String aUnique:questionPackList){
			Map<String,Object> tempMap=new HashMap<String,Object>();
			List<Question> tempQuestionList=new ArrayList<Question>();
			numberOfQuestions=numberOfQuestions+questionPackDao.getNumberOfQuestionsInPack(aUnique);
			QuestionPack questionPack=questionPackDao.getQuestionPackByUnique(aUnique);
			tempQuestionList=questionDao.getQuestionsByPack(aUnique);
			tempMap.put("questionPack",questionPack);
			tempMap.put("questions",tempQuestionList);
			questionList.add(tempMap);
		}
		
		List<Winner> winnerList=winnerDao.getWinnerByCompetitionUnique(competition.getaUnique());
		Date nextDrawDate=null;
		Date lastDrawDate=null;
		if(!winnerList.isEmpty()){
			Date drawDate=winnerList.get(0).getaDrawDate();
			nextDrawDate=getNextDrawDate(drawDate, competition.getaPeriod());
			lastDrawDate=drawDate;
		}else{
			nextDrawDate=getNextDrawDate(competition.getaStartDate(),competition.getaPeriod());
		}
		if(!questionPackList.isEmpty())
			competitionMap.put("competitionLanguage",questionPackDao.getQuestionPackByUnique(questionPackList.get(0)).getaLanguage());
		else
			competitionMap.put("competitionLanguage","");
		competitionMap.put("competition",competition);
		competitionMap.put("numberOfQuestions",numberOfQuestions);
		competitionMap.put("winnerList", winnerList);
		if(nextDrawDate!=null)
			competitionMap.put("nextDrawDate", nextDrawDate);
		else
			competitionMap.put("nextDrawDate", "");
		if(lastDrawDate!=null)
			competitionMap.put("lastDrawDate", lastDrawDate);
		else
			competitionMap.put("lastDrawDate", "");
		competitionMap.put("regions",mobileClubDao.getDistinctRegions());
		competitionMap.put("questionList", questionList);
		competitionMap.put("questionPackList", questionPackDao.getAllQuestionPack());
		return competitionMap;
	}

}
