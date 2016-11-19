/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.it.servlets;

import com.mitchellbosecke.pebble.PebbleEngine;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.template.TemplateEngine;//

/**
 *
 * @author trung
 */
@WebServlet(name = "Terms", urlPatterns = {"/terms"})
public class Terms extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(Terms.class.getName());

    @Autowired
    TemplateEngine templateEngine;//

    public Terms() {
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
            
            PebbleEngine frEngine = templateEngine.getTemplateEngine(dmn.getUnique());
            frEngine.getTemplate("terms").evaluate(writer, context);
        
        } catch (Exception e) {
            logger.error("Error Rendering Terms Template");
            e.printStackTrace();
        }
    }
}
