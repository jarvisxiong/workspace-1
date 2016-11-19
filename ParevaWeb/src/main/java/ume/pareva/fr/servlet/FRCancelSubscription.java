package ume.pareva.fr.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
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

import ume.pareva.fr.FRConstant;
import ume.pareva.fr.util.FRUtil;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Misc;

@WebServlet("/FRCancelSubscription")
public class FRCancelSubscription extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger( FRCancelSubscription.class.getName());
	
    @Autowired
	FRUtil frUtil;
    
    @Autowired
    UmeRequest umeRequest;
    
    public FRCancelSubscription() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}
	
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		logger.info("Cancelling Subscription");
		HttpSession session=request.getSession();
		String sessionId=Misc.generateAnyxSessionId();
		UmeDomain dmn=umeRequest.getDomain();
		String subscriptionId="";
		if(session.getAttribute("subscriptionId")!=null)
			subscriptionId=session.getAttribute("subscriptionId").toString();
		if(!subscriptionId.equals("")){
			logger.info("Subscription ID: "+subscriptionId);
			Map<String,String> resutlMap=getToken(sessionId,subscriptionId);
			String errorCode=resutlMap.get("error_code");
			if(errorCode.equals("0")){
				unSubscribeUser(resutlMap,request,response);
			}else{
				logger.error("Error Getting Token, Redirecting to Home Page");
				response.sendRedirect("http://"+dmn.getDefaultUrl());
				return;
			}
		}/*else{
			Map<String,String> resutlMap=getToken(sessionId,subscriptionId);
			String errorCode=resutlMap.get("error_code");
			if(errorCode.equals("0")){
				String token=resutlMap.get("token");
				response.sendRedirect(FRConstant.SUBSCRIPTION_URL+"&token="+token);
				return;
			}else{
				logger.error("Error Getting Token, Redirecting to Home Page");
				response.sendRedirect("http://"+dmn.getDefaultUrl());
				return;
			}

		}*/

	}
	
	public void unSubscribeUser(Map<String,String> resutlMap,HttpServletRequest request,HttpServletResponse response){
		UmeDomain dmn=umeRequest.getDomain();
		String token=resutlMap.get("token");
		try {
			logger.info("Redirecting to Subscription Cancellation Link");
			String callback=URLEncoder.encode("http://"+dmn.getDefaultUrl()+"/callback_ok.jsp?event=UNSUBSCRIBE","UTF-8");
			response.sendRedirect(FRConstant.SUBSCRIPTION_CANCEL_URL+"&token="+token+"&callback_ok="+callback+"&callback_ko="+callback+"&callback_cancel="+callback);
		} catch (IOException e) {
			logger.error("Error Unsubscribing User");
			e.printStackTrace();
		}
	}
	
	public Map<String,String> getToken(String sessionId,String subscriptionId){
		Map<String,String> tokenParameterMap=new HashMap<String,String>();
		tokenParameterMap.put("login",FRConstant.LOGIN);
		tokenParameterMap.put("password", FRConstant.PASSWORD);
		tokenParameterMap.put("service_id", FRConstant.SERVICE_ID);
		tokenParameterMap.put("session_id", sessionId );
		tokenParameterMap.put("subscription_id", subscriptionId );
		Map<String,String> resutlMap=frUtil.makeRestCall(FRConstant.TOKEN_URL, tokenParameterMap);
		return resutlMap;
	}
	
	
}
