package ume.pareva.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.servlet.support.RequestContextUtils;

import ume.pareva.ire.IEutil;
import ume.pareva.ire.MsisdnPassingResponse;

/**
 * Servlet implementation class MsisdnCheck
 */
public class MsisdnCheck extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Autowired
	IEutil ieUtil;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MsisdnCheck() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("Inside MSISDN Check");
		
		String transactionId=request.getParameter("transactionId");
		
 /* 
 httpsrequest="https://msisdn.sla-alacrity.com/authenticate/msisdn?uri=partner:91909c32-e422-42e3-845a-d3cbf15af4fa&transaction_id="+request.getParameter("transactionid");
 System.out.println("manualvodafone step 2 calling up "+httpsrequest);
 
 String username="txtnationl_743_live"; // "txtnationl_731_live";
    String pass="2y5okuty";//"ndondawutu";

    
    String userpass=username+":"+pass;
    String encoding ="Basic "+ new sun.misc.BASE64Encoder().encode(userpass.getBytes());
            URL obj = new URL(httpsrequest);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestProperty("Authorization", encoding);
            con.setRequestProperty("Accept", "application/json");
            
            responseCode = con.getResponseCode();  
            message=con.getResponseMessage();
             //engine.getTemplate("msisdnparser").evaluate(writer, context); 
		System.out.println("manualvodafone Sending 'GET' request to URL : " + httpsrequest);
		System.out.println("manualvodafone Response Code : " + responseCode+" message- "+message);
       */         
				
				MsisdnPassingResponse msisdnPassingResponse=ieUtil.sendPersonalLink(transactionId);
       System.out.println("MsisdnPassingResponse: "+msisdnPassingResponse);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
