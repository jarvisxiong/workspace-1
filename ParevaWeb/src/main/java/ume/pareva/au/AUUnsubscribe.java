
package ume.pareva.au;

import com.mitchellbosecke.pebble.PebbleEngine;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ume.pareva.dao.SdcRequest;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.userservice.UserStopService;

/**
 *
 * @author Madan
 */
@WebServlet(name = "AUUnsubscribe", urlPatterns = {"/AUUnsubscribe"})
public class AUUnsubscribe extends HttpServlet {
    
    @Autowired
    TemplateEngine templateEngine;//
    
     @Autowired
    UserStopService userstopservice;

    
    
    private final Logger logger = LogManager.getLogger(AUUnsubscribe.class.getName());
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
         SdcRequest aReq = new SdcRequest(request);
        UmeUser user = aReq.getUser();
        UmeDomain dmn = aReq.getDomain();
        SdcService service = aReq.getService();
        String domain = dmn.getUnique();
        String ddir = dmn.getDefPublicDir();
        String lang = aReq.getLanguage().getLanguageCode();
        String message="",status="";
        int responseCode=-1;
        PrintWriter writer = response.getWriter();
        Map<String,Object> context = new HashMap<String,Object>();
        
        String httpsrequest="http://sms1.ireland.operatele.com:9081/subs.php";
        String msisdn=aReq.get("msisdn");
        String clubunique=aReq.get("clubid");
        if("".equalsIgnoreCase(msisdn)) msisdn=user.getParsedMobile();
        if(!msisdn.equalsIgnoreCase("") && msisdn.startsWith("61")){
            
            
         httpsrequest+="?source=web&request=unsubscribe&listid=9410&notify=yes&msisdn="+msisdn;   
         System.out.println("audebug url about to be requested is "+httpsrequest);
         
        String username="umeremote";
        String password="4MVjq9MexG9nMkMz";
        String listid="9410";
        
         String userpass=username+":"+password;
        String encoding ="Basic "+ new sun.misc.BASE64Encoder().encode(userpass.getBytes());
            URL obj = new URL(httpsrequest);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("Authorization", encoding);
            con.setRequestProperty("Accept", "application/json");
            
            responseCode = con.getResponseCode();  
            message=con.getResponseMessage();
             //engine.getTemplate("msisdnparser").evaluate(writer, context); 
		System.out.println("audebug HTTP URL Connection Sending 'GET' request to URL : " + httpsrequest);
		System.out.println("audebug HTTPURLConnection  Response Code : " + responseCode+" message- "+message);
                
                if(responseCode==200 || responseCode==201){
                    status="success";
                    Map<String,String> stopSingleservice=new HashMap<String,String>();
                    if(!"".equalsIgnoreCase(clubunique))
                stopSingleservice=userstopservice.stopSingleSubscription(msisdn, clubunique);
                    else
                stopSingleservice=userstopservice.stopAllSubscription(msisdn);
                }
                else{
                    status="failure";
                }
          
            
        }
              try{
			
			context.put("contenturl","http://"+dmn.getContentUrl());
			context.put("msisdn",msisdn);
			
			PebbleEngine auEngine=templateEngine.getTemplateEngine(dmn.getUnique());
			auEngine.getTemplate("unsubscribe").evaluate(writer, context);
		}catch(Exception e){
    		logger.error("audebug Unsubscription Error Rendering Unsubscribe Template");
    		e.printStackTrace();
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
