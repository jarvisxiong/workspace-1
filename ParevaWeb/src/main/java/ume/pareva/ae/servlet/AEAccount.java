package ume.pareva.ae.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.mitchellbosecke.pebble.PebbleEngine;//
import javax.servlet.annotation.WebServlet;

import ume.pareva.pojo.UmeDomain;
import ume.pareva.template.TemplateEngine;//

@WebServlet("/AEAccount")
public class AEAccount extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(AEAccount.class.getName());

    @Autowired
    TemplateEngine templateEngine;//

    public AEAccount() {
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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
        String subscriptionId = readCookie(request, "subscriptionId");
        try {
            PrintWriter writer = response.getWriter();
            Map<String, Object> context = new HashMap<String, Object>();
            if (!subscriptionId.equals("")) {
                context.put("resiliationLink", "cancelSubscription.jsp");
            } else {
                context.put("resiliationLink", "http://billing.virgopass.com/fr_unsubscription.php");
            }
            context.put("contenturl", "http://" + dmn.getContentUrl());
            context.put("contact", "contact.jsp");
            context.put("terms", "terms.jsp");
            context.put("unsubscribe", "unsubscribe.jsp");
            PebbleEngine frEngine = templateEngine.getTemplateEngine(dmn.getUnique());
            frEngine.getTemplate("account").evaluate(writer, context);
        } catch (Exception e) {
            logger.error("Error Rendering Account Template");
            e.printStackTrace();
        }

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
