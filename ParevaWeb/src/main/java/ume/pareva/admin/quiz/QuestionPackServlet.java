package ume.pareva.admin.quiz;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.dao.QuestionDao;
import ume.pareva.dao.QuestionPackDao;
import ume.pareva.dao.SdcMiscDate;
import ume.pareva.pojo.Question;
import ume.pareva.pojo.QuestionPack;
import ume.pareva.pt.util.UmeRequest;


@WebServlet("/QuestionPack")
public class QuestionPackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( QuestionPackServlet.class.getName());
	private List<QuestionPack> questionPackList=new ArrayList<QuestionPack>();  
	private List<String> languageList=new ArrayList<String>();
	private List<Question> questionList=new ArrayList<Question>();
	private QuestionPackDao questionPackDao;
	private UmeRequest umeRequest;
	private QuestionDao questionDao;

	public UmeRequest getUmeRequest() {
		return umeRequest;
	}

	@Autowired
	public void setUmeRequest(UmeRequest umeRequest) {
		this.umeRequest = umeRequest;
	}

	public QuestionPackDao getQuestionPackDao() {
		return questionPackDao;
	}

	@Autowired
	public void setQuestionPackDao(QuestionPackDao questionPackDao) {
		this.questionPackDao = questionPackDao;
	}
	
	public QuestionDao getQuestionDao() {
		return questionDao;
	}

	@Autowired
	public void setQuestionDao(QuestionDao questionDao) {
		this.questionDao = questionDao;
	}

	public QuestionPackServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String unique=umeRequest.get("unique");
		String action=umeRequest.get("action");
		if(action.equals("")){
			getQuestionPacks(request,"","");
			getDistinctRegions(request);
		}else if(action.equals("Update")){
			QuestionPack questionPack=questionPackDao.getQuestionPackByUnique(unique);
			int numberOfQuestions=questionPackDao.getNumberOfQuestionsInPack(unique);
			questionList=questionDao.getQuestionsByPack(unique);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			String created=sdf.format(questionPack.getaCreated());
			request.setAttribute("created",created);
			request.setAttribute("action",action);
			request.setAttribute("questionPack",questionPack);
			request.setAttribute("numberOfQuestions",numberOfQuestions);
			request.setAttribute("questionList",questionList);
		}else if(action.equals("Create")){
			request.setAttribute("action",action);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action=umeRequest.get("action");
		String unique=umeRequest.get("unique");
		String name=umeRequest.get("name");
		String language=umeRequest.get("language");
		String created=umeRequest.get("created");
		if(action.equals("Update")){
			QuestionPack questionPack=saveOrUpdateOrDelete(unique,name,language,created,action);
			request.setAttribute("questionPack",questionPack);
			request.setAttribute("action",action);
			request.setAttribute("created",created);
			request.setAttribute("msg","QuestionPack Updated Sucessfully");
		}else if(action.equals("Create")){
			saveOrUpdateOrDelete(unique,name,language,created,action);
			request.setAttribute("msg","QuestionPack Created Sucessfully");
			doGet(request,response);
		}else if(action.equals("Delete")){
			saveOrUpdateOrDelete(unique,name,language,created,action);
			request.setAttribute("msg","QuestionPack Deleted Sucessfully");
			doGet(request,response);
		}
	}

	public void getQuestionPacks(HttpServletRequest request,String searchByQuestionPackName,String filterByQuestionPackLanguage){
		questionPackList=questionPackDao.getQuestionPacks(searchByQuestionPackName,filterByQuestionPackLanguage);
		request.setAttribute("questionPackList", questionPackList);
	}

	public void getDistinctRegions(HttpServletRequest request){
		languageList=questionPackDao.getDistinctLanguages();
		request.setAttribute("languageList", languageList);		
	}

	public QuestionPack saveOrUpdateOrDelete(String unique,String name,String language,String created,String action){
		int numberOfRows=0;
		QuestionPack questionPack=new QuestionPack();
		questionPack.setaUnique(unique);
		questionPack.setaName(name);
		questionPack.setaLanguage(language);
		if(!created.equals(""))
			questionPack.setaCreated(SdcMiscDate.parseSqlDate(created));
		if(action.equals("Update")){
			numberOfRows=questionPackDao.updateQuestionPack(questionPack);
		}else if(action.equals("Create")){
			numberOfRows=questionPackDao.saveQuestionPack(questionPack);
		}else if(action.equals("Delete")){
			numberOfRows=questionPackDao.deleteQuestionPack(questionPack);
		}
		if(numberOfRows>0)
			return questionPack;
		else
			return null;
	}

}
