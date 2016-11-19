package ume.pareva.ae.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.dao.SdcRequest;
import ume.pareva.ae.util.RestUtil;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.sdk.Misc;

@WebServlet("/AECancelSubscription")
public class AECancelSubscription extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String login = "universalmobile";
    private static final String password = "56gnP15A";
    private static final String tokenUrl = "http://billing.virgopass.com/api_v1.5.php?getToken";
    private static final String subscriptionCancelUrl = "http://billing.virgopass.com/api_v1.5.php?resiliation";
    private static final String subscriptionUrl = "http://billing.virgopass.com/api_v1.5.php?subscription";
    private static final String serviceId = "23461";
    private static final Logger logger = LogManager.getLogger(AECancelSubscription.class.getName());

    @Autowired
    RestUtil restUtil;

    public AECancelSubscription() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SdcRequest sdcRequest = new SdcRequest(request);
        String sessionId = Misc.generateAnyxSessionId();
        UmeDomain dmn = sdcRequest.getDomain();
        String subscriptionId = readCookie(request, "subscriptionId");
        if (!subscriptionId.equals("")) {
            Map<String, String> resutlMap = getToken(sessionId, subscriptionId);
            String errorCode = resutlMap.get("error_code");
            if (errorCode.equals("0")) {
                unSubscribeUser(resutlMap, request, response);
            } else {
                logger.error("Error Getting Token");
                logger.error("Redirecting To Home Page");
                response.sendRedirect("http://" + dmn.getDefaultUrl());
                return;
            }
        } else {
            Map<String, String> resutlMap = getToken(sessionId, subscriptionId);
            String errorCode = resutlMap.get("error_code");
            if (errorCode.equals("0")) {
                String token = resutlMap.get("token");
                response.sendRedirect(subscriptionUrl + "&token=" + token);
                return;
            } else {
                logger.error("Error Getting Token");
                logger.error("Redirecting To Home Page");
                response.sendRedirect("http://" + dmn.getDefaultUrl());
                return;
            }

        }

    }

    public void unSubscribeUser(Map<String, String> resutlMap, HttpServletRequest request, HttpServletResponse response) {
        SdcRequest sdcRequest = new SdcRequest(request);
        UmeDomain dmn = sdcRequest.getDomain();

        String token = resutlMap.get("token");
        try {
            String callback = URLEncoder.encode("http://" + dmn.getDefaultUrl() + "/callback_ok.jsp/?event=UNSUBSCRIBE", "UTF-8");
            response.sendRedirect(subscriptionCancelUrl + "&token=" + token + "&callback_ok=" + callback + "&callback_ko=" + callback + "&callback_cancel=" + callback);
        } catch (IOException e) {
            logger.error("Error Subscribing User");
            e.printStackTrace();
        }
    }

    public Map<String, String> getToken(String sessionId, String subscriptionId) {

        Map<String, String> tokenParameterMap = new HashMap<String, String>();
        tokenParameterMap.put("login", login);
        tokenParameterMap.put("password", password);
        tokenParameterMap.put("service_id", serviceId);
        tokenParameterMap.put("session_id", sessionId);
        if (!subscriptionId.equals("")) {
            tokenParameterMap.put("subscription_id", subscriptionId);
        }

        Map<String, String> resutlMap = restUtil.makeRestCall(tokenUrl, tokenParameterMap);
        return resutlMap;

    }

    public String readCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        String cookieValue = "";
        if (cookies != null) {
            for (int loopIndex = 0; loopIndex < cookies.length; loopIndex++) {
                Cookie cookie1 = cookies[loopIndex];
                if (cookie1.getName().equals(cookieName)) {
                    cookieValue = cookie1.getValue();
                    break;
                }
            }
        }

        return cookieValue;

    }

}
