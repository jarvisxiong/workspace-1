package ume.pareva.uk;

import com.mitchellbosecke.pebble.PebbleEngine;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.template.TemplateEngine;

/**
 *
 * @author madan
 */
@WebServlet(name = "WeeklyQuiz", urlPatterns = {"/WeeklyQuiz"})
public class WeeklyQuiz extends HttpServlet {
    
    
    @Autowired
    TemplateEngine templateEngine;
    
    @Autowired
    UmeUserDao umeuserdao;
    
    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;
    
     
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
    
    
    private final Logger logger = LogManager.getLogger(WeeklyQuiz.class.getName());
        /**
     * This method is needed to support Autowired Spring beans
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }
    

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        ThreadContext.put("ROUTINGKEY", "UK");
        logger.info("uknewlogic WeeklyQuiz "+"ProcessREQEST is called upon ");

        HttpSession session = request.getSession();
        logger.info("uknewlogic WeeklyQuiz "+"session id is  "+session.getId());
        System.out.println("uknewlogic WeeklyQuiz "+"session id is  "+session.getId());
        ServletContext application = request.getServletContext(); //.setAttribute(null, request);
        logger.info("uknewlogic WeeklyQuiz  "+"servletContext is  "+application.getContextPath());
        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();
        String ddir = dmn.getDefPublicDir();
        String lang = aReq.getLanguage().getLanguageCode();
        String defClubDomain ="5510024809921CDS"; // This is default domain in users table.
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
        UmeClubDetails userclubdetails=null;
        SdcMobileClubUser clubUser = null;
        PrintWriter writer = response.getWriter();
        Map<String, Object> context = new HashMap();
        
        PebbleEngine engine=templateEngine.getTemplateEngine(dmn.getUnique());
        
        String clubUnique=aReq.get("clubid");
        String usermsisdn=aReq.get("msisdn");
        
        System.out.println("uknewlogic WeeklyQuiz "+"clubid and msisdn  is  "+clubUnique+" usermsisdn "+usermsisdn+" --- "+dmn.getUnique());
        
        if(!"".equalsIgnoreCase(clubUnique)) {
            System.out.println("uknewlogic WeeklyQuiz ClubUnique "+clubUnique);
            MobileClub sourceClub=UmeTempCmsCache.mobileClubMap.get(clubUnique);
            System.out.println("uknewlogic WeeklyQuiz ~Source Club "+sourceClub.getUnique()+" -- aName: "+sourceClub.getName());
            
          if(!"".equalsIgnoreCase(usermsisdn)){
              
           String userUnique = umeuserdao.getUserUnique(usermsisdn, "msisdn", defClubDomain);
            if (!userUnique.equals("")) user = umeuserdao.getUser(usermsisdn);
            if (user!=null) {
                System.out.println("uknewlogic WeeklyQuiz User is not NULL "+ user.getParsedMobile()+" -- "+user.getWapId());
	       clubUser=umemobileclubuserdao.getClubUserByMsisdn(usermsisdn,clubUnique);
               System.out.println("uknewlogic WeeklyQuiz CLUBUSER is not NULL "+ clubUser.getParsedMobile()+" -- "+clubUser.getClubUnique()+"-- "+clubUser.getActive());
            }
            if(user==null)
                System.out.println("uknewlogic WeeklyQuiz User is NULL ");
              
          } //END not blank MSISDN 
        } //END BLANK ClubUnique 
        
        if(clubUser!=null && clubUser.getActive()==1 ){
            System.out.println("uknewlogic WeeklyQuiz "+"clubUser is  "+clubUser.getParsedMobile()+"  "+clubUser.getClubUnique());
            //TODO Get the QuizQuestion
            List<String> quizQuestion=getQuestion(clubUnique,"");
            String quizNo="";
            String weeklyQuestion="";
            String optionA="";
            String optionB="";
            
            if(quizQuestion!=null && !quizQuestion.isEmpty()) {
                quizNo=quizQuestion.get(0);
                weeklyQuestion=quizQuestion.get(1);
                optionA=quizQuestion.get(2);
                optionB=quizQuestion.get(3);
                
                System.out.println("uknewlogic Questions to display are "+weeklyQuestion+" "+optionA+"- "+optionB);
            }
            context.put("contenturl","http://"+dmn.getContentUrl());
            context.put("quizno",quizNo);
            context.put("question",weeklyQuestion);
            context.put("answer1",optionA);
            context.put("answer2",optionB);
            context.put("clubid",clubUnique);
            context.put("msisdn",usermsisdn);
            
        }
        try{
            engine.getTemplate("question").evaluate(writer, context);
        }catch(Exception e){System.out.println("uknewlogic Exception for getTemplate "+e);e.printStackTrace();}
    } //END PROCESS REQUEST 
    
    public List<String> getQuestion(String clubUnique,String filter){
        List<String> quizQuestion=new ArrayList<String>(); 
        String quizNo="";
        String sqlstr = "SELECT t3.`quizNo`, t3.`quizQuestion`, t3.`answerOption` ansA,t4.`answerOption` ansB " +
                "FROM " +
                "(SELECT t1.`quizNo`, t2.`quizQuestion`, `optionNo`,`answerOption` " +
                " FROM `quizQuestionOptions` t1,`quizQuestionsWithClub` t2" +
                " WHERE  t1.`quizNo`= t2.`quizNo` AND `optionNo`='A' AND " +
                " NOW() BETWEEN t2.`startDateTime` AND t2.`endDateTime` AND t2.`aClubUnique` LIKE '%"+clubUnique+"%') t3, " +
                " (SELECT t1.`quizNo`, t2.`quizQuestion`, `optionNo`,`answerOption` " +
                " FROM `quizQuestionOptions` t1,`quizQuestionsWithClub` t2 " +
                " WHERE  t1.`quizNo`= t2.`quizNo` AND `optionNo`='B' AND  " +
                " NOW() BETWEEN t2.`startDateTime` AND t2.`endDateTime`AND t2.`aClubUnique` LIKE '%"+clubUnique+"%') t4 " +
                " WHERE t3.quizNo=t4.quizNo";
                
                sqlstr+= " LIMIT 1";
                
                System.out.println("uknewlogic   "+sqlstr);
         Session session = null;
        try{
            session=openSession();
            Transaction trans = session.beginTransaction();
            Query query = session.createSQLQuery(sqlstr).addScalar("quizNo",StandardBasicTypes.STRING)
                    .addScalar("quizQuestion",StandardBasicTypes.STRING).addScalar("ansA",StandardBasicTypes.STRING)
                    .addScalar("ansB",StandardBasicTypes.STRING);
            
            List queryresult=query.list();
            int counter=0;
            if(queryresult.size()>0){
                for(Object o:queryresult){
                    Object[]row=(Object[]) o;
                quizQuestion.add(String.valueOf(row[0])); //QuizNo
                quizQuestion.add(String.valueOf(row[1])); //QuizQuestion
                quizQuestion.add(String.valueOf(row[2])); // ansA
                quizQuestion.add(String.valueOf(row[3])); //ansB
                
                System.out.println("uknewlogic  quiz question values : -"+quizQuestion.get(counter));
                counter++;
                }
            }
            trans.commit();
        }
        catch(Exception e){}
        finally{
            session.close();
        }
        return quizQuestion;
    }
    
    

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
