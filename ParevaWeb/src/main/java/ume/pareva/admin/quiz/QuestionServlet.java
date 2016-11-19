package ume.pareva.admin.quiz;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import ume.pareva.dao.QuestionInPackDao;
import ume.pareva.dao.QuestionPackDao;
import ume.pareva.pojo.Question;
import ume.pareva.pojo.QuestionInPack;
import ume.pareva.pojo.QuestionPack;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Misc;


@WebServlet("/Question")
public class QuestionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( QuestionServlet.class.getName());
	private QuestionPackDao questionPackDao;
	private UmeRequest umeRequest;
	private QuestionDao questionDao;
	private QuestionInPackDao questionInPackDao;
	private List<Question> questionList=new ArrayList<Question>();
	private List<QuestionPack> questionPackList=new ArrayList<QuestionPack>();  
	private List<String> languageList=new ArrayList<String>();
	

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
	
	public QuestionInPackDao getQuestionInPackDao() {
		return questionInPackDao;
	}

	@Autowired
	public void setQuestionInPackDao(QuestionInPackDao questionInPackDao) {
		this.questionInPackDao = questionInPackDao;
	}

	public QuestionServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action=umeRequest.get("action");
		if(action.equals("")){
		String questionPackId=umeRequest.get("question_pack_id");
		questionList=questionDao.getAllQuestions();
		if(questionPackId.equals(""))
			getQuestionPacks(request);
		languageList=questionPackDao.getDistinctLanguages();
		request.setAttribute("languageList", languageList);		
		request.setAttribute("questionList",questionList);
		request.setAttribute("questionPackId", questionPackId);
		}else if(action.equals("Update")){
			List<QuestionPack> questionPackList=new ArrayList<QuestionPack>();
			List<QuestionPack> questionInPacks=new ArrayList<QuestionPack>();
			//List<String> languages=questionPackDao.getDistinctLanguages();
			String questionId=umeRequest.get("questionId");
			Question question=questionDao.getQuestionById(questionId);
			questionInPacks=questionPackDao.getQuestionPack(questionId);
			questionPackList=questionPackDao.getQuestionPacks("",questionInPacks.get(0).getaLanguage());
			request.setAttribute("question",question);
			request.setAttribute("questionPackList",questionPackList);
			request.setAttribute("language",questionInPacks.get(0).getaLanguage());
			request.setAttribute("questionInPacks",questionInPacks);
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String questionPackId=umeRequest.get("question_pack_id");
		String questionId=Misc.generateUniqueId();
		Question question=new Question();
		question.setQuestion(umeRequest.get("question"));
		question.setOptionA(umeRequest.get("option_a"));
		question.setOptionB(umeRequest.get("option_b"));
		question.setCorrectOption(umeRequest.get("correct_option"));
		question.setQuestionId(questionId);
		question.setaCreated(new Date());
		
		QuestionInPack questionInPack=new QuestionInPack();
		questionInPack.setQuestionId(questionId);
		questionInPack.setQuestionPackId(questionPackId);
		
		questionDao.saveQuestion(question);
		questionInPackDao.saveQuestionInPack(questionInPack);
		PrintWriter writer=response.getWriter();
		writer.print("saved");
		
	}
	
	public void getQuestionPacks(HttpServletRequest request){
		questionPackList=questionPackDao.getAllQuestionPack();
		request.setAttribute("questionPackList", questionPackList);
	}

}
