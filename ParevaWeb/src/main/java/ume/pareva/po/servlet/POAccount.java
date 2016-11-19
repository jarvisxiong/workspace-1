package ume.pareva.po.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.mitchellbosecke.pebble.PebbleEngine;

import ume.pareva.pojo.UmeDomain;
import ume.pareva.template.TemplateEngine;//

public class POAccount extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(POAccount.class.getName());

    @Autowired
    TemplateEngine templateEngine;//

    public POAccount() {
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
        try {
            PrintWriter writer = response.getWriter();
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("contenturl", "http://" + dmn.getContentUrl());
            context.put("contact", "contact.jsp");
            context.put("terms", "terms.jsp");
            PebbleEngine frEngine = templateEngine.getTemplateEngine(dmn.getUnique());
            frEngine.getTemplate("account").evaluate(writer, context);
        } catch (Exception e) {
            logger.error("Error Rendering Account Template");
            e.printStackTrace();
        }
    }
}
