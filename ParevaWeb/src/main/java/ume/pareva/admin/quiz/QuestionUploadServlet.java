package ume.pareva.admin.quiz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.dao.QuestionPackDao;
import ume.pareva.pojo.QuestionPack;

@WebServlet("/QuestionUpload")
public class QuestionUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private QuestionPackDao questionPackDao;
	private List<QuestionPack> questionPackList=new ArrayList<QuestionPack>();  
		 
       
    public QuestionUploadServlet() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }
    
    public QuestionPackDao getQuestionPackDao() {
		return questionPackDao;
	}

    @Autowired
	public void setQuestionPackDao(QuestionPackDao questionPackDao) {
		this.questionPackDao = questionPackDao;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getQuestionPacks(request);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	}
	
	public void getQuestionPacks(HttpServletRequest request){
		questionPackList=questionPackDao.getAllQuestionPack();
		request.setAttribute("questionPackList", questionPackList);
	}

}
