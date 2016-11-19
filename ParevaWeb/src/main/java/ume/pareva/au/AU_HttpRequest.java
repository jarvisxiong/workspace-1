/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.au;

import com.mitchellbosecke.pebble.PebbleEngine;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.dao.SdcRequest;
import ume.pareva.ire.IREConnConstants;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.util.httpconnection.HttpURLConnectionWrapper;

/**
 *
 * @author Madan
 */
@WebServlet(name = "AU_HttpRequest", urlPatterns = {"/AU_HttpRequest"})
public class AU_HttpRequest extends HttpServlet {
    
     @Autowired
    TemplateEngine templateEngine;//
    
    
    private final static String AU_HTTPURL="http://sms1.ireland.operatele.com:9081/subs.php";
    
     private final Logger logger = LogManager.getLogger(AU_HttpRequest.class.getName());
    
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
        PrintWriter writer=response.getWriter();
        Map<String,Object> context=new HashMap<String,Object>();
        
        HttpSession ses = request.getSession();
        logger.info("AUINDEX "+"session id is  "+ses.getId());
        ServletContext application = request.getServletContext(); //.setAttribute(null, request);
        logger.info("AUINDEX "+"servletContext is  "+application.getContextPath());
        SdcRequest aReq = new SdcRequest(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();
        String ddir = dmn.getDefPublicDir();
        String lang = aReq.getLanguage().getLanguageCode();
        
        String username="";
        String password="";
       /*
       parameter source
       web= Request originated from a webserver
       wap Request originated from a wap server
       ivr Request originated from an IVR server
       */
       String source= "web"; 
       
       /**
        * Parameter request
        * subscribe-  Subscribe a user to a service
          unsubscribe-  Unsubscribe a user from a service
          lookup-  Check whether a user is subscribed to a service
        */
       String requestfor=request.getParameter("requestfor");
       
       /**
        * Parameter listid
        * This is the Subscription ID of the list which is the subject of the request. It must be a
        valid and active listID on the Subscription platform at the time of the request, and must
        be owned by the username/password with which the request was authenticated.
        */
       
       String listid=request.getParameter("listid");
       String msisdn=request.getParameter("msisdn");
       
       HttpURLConnectionWrapper urlwrapper = urlwrapper = new HttpURLConnectionWrapper(AU_HTTPURL);
       Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("username",username);
        parameterMap.put("password",password);
        parameterMap.put("source",source);
        parameterMap.put("request",requestfor);
        parameterMap.put("listid",listid);
        parameterMap.put("msisdn",msisdn);
        
        urlwrapper.wrapGet(parameterMap);

        String responsecode = urlwrapper.getResponseCode();
        String responsedesc = urlwrapper.getResponseContent();
        boolean isSuccessful = urlwrapper.isSuccessful();
        
        
        
        context.put("status",responsecode+":"+responsedesc);
        context.put("msisdn",msisdn);
        //context.put("sendAction","processau.jsp");
        context.put("contenturl","http://"+dmn.getContentUrl());
 
        PebbleEngine au_engine=templateEngine.getTemplateEngine(dmn.getUnique());
        
        try{
        au_engine.getTemplate("status").evaluate(writer, context);
        }catch(Exception e){}
        


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
