package ume.pareva.admin.quiz;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.dao.QuestionDao;
import ume.pareva.dao.QuestionInPackDao;
import ume.pareva.dao.QuestionPackDao;
import ume.pareva.pojo.Question;
import ume.pareva.pojo.QuestionInPack;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Misc;

@WebServlet("/SaveQuestion")
public class SaveQuestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private QuestionPackDao questionPackDao;
	private UmeRequest umeRequest;
	private QuestionDao questionDao;
	private QuestionInPackDao questionInPackDao;

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

	public SaveQuestion() {
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
		PrintWriter writer=response.getWriter();
		try{
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
			writer.print("saved");
		}catch(Exception e){
			writer.print("error");
		}
	}

}
