package ume.pareva.au;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.dao.AusTrackingDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.util.httpconnection.HttpURLConnectionWrapper;

/**
 *
 * @author madan
 */
public class ProcessAU extends HttpServlet {
    
    static final Logger logger = LogManager.getLogger(ProcessAU.class.getName());
    HttpURLConnectionWrapper urlwrapper = null;

    @Autowired
    AusTrackingDao austrackingdao;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ThreadContext.put("ROUTINGKEY", "AUSTRALIA");
        System.out.println(" ===== PROCESSAU SERVLET INIT ===== ");
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ThreadContext.put("ROUTINGKEY", "AU");
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        System.out.println("PROCESSAU session id is  "+session.getId());
        ServletContext application = request.getServletContext(); //.setAttribute(null, request);
        System.out.println("PROCESSAU servletContext is  "+application.getContextPath());
        UmeSessionParameters aReq = new UmeSessionParameters(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();
        String ddir = dmn.getDefPublicDir();
        String lang = aReq.getLanguage().getLanguageCode();

        urlwrapper = new HttpURLConnectionWrapper(AustraliaConnConstants.getDomainHttps());
        Map<String, String> ausMap = new HashMap<String, String>();
         String landingpage =aReq.get("landingpage"); 
        String campaignid = aReq.get("cid"); 
        String callbackurl="http://"+dmn.getDefaultUrl()+"/callback.jsp?cid="+campaignid+"&landingpage="+landingpage;
        String merchanturl="http://"+dmn.getDefaultUrl()+"/auconfirm.jsp";

        ausMap.put("serviceID", "187");
        ausMap.put("price", "8.99");
        ausMap.put("callbackurl", java.net.URLEncoder.encode("http://realchix.realgirls.mobi/callback.jsp?cid="+campaignid+"&landingpage="+landingpage,"UTF-8" ));
        ausMap.put("productname", "RealChix AUS");
        ausMap.put("merchanturl", java.net.URLEncoder.encode("http://realchix.realgirls.mobi/auconfirm.jsp","UTF-8"));

       
        //ausMap.put("","");
        AustraliaBaseConnection abc = new AustraliaBaseConnection();
        String transactionId = abc.sendPreparatoryRequest(urlwrapper, ausMap);
        System.out.println("PROCESSAU transactionid is " + transactionId);
        if (!urlwrapper.isSuccessful()) {
		   // / What to do here? --
            // urlwapper.getResponseContent() will get you the error message, maybe display to back to the user
        } else {
            String aurequestUrl=AustraliaConnConstants.getDomainHttps();
            //request.setAttribute("auredirecturl","http://http.au.oxygen8.com/dob/?serviceID=187&TransID="+transactionId);
            System.out.println("PROCESSAU redirecting to " +AustraliaConnConstants.getDomainHttps()+ "?serviceID=187&TransID=" + transactionId);
            austrackingdao.addTransId(transactionId, landingpage, campaignid);
            doRedirect(response, AustraliaConnConstants.getDomainHttps()+"?serviceID=187&TransID=" + transactionId);
        }

    }

    private void doRedirect(HttpServletResponse response, String page) {
        try {
            response.sendRedirect(page);
        } catch (Exception e) {
            System.out.println("PROCESSAU ProcessAU.java doRedirect EXCEPTION: " + e);
            logger.fatal("PROCESS REQUEST - ProcessAU.java doRedirect EXCEPTION: {}", e.getMessage());
            logger.fatal("PROCESS REQUEST - ProcessAU.java doRedirect EXCEPTION ", e);
        }
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
