/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.uk;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.VideoClipDao;
import ume.pareva.dao.UmeLanguagePropertyDao;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.template.TemplateEngine;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;
import ume.pareva.userservice.VideoList;

/**
 *
 * @author madan
 */
public class UKHeader extends HttpServlet {

    @Autowired
    UmeTempCache umesdc;
    
    @Autowired
    HandsetDao handsetdao;
    
    @Autowired
    UmeLanguagePropertyDao langpropdao;
    
    @Autowired
    MobileClubDao mobileclubdao;
    
    @Autowired
    VideoClipDao videoclipdao;
    
    @Autowired
    MobileClubCampaignDao campaigndao;
    
    @Autowired
    VideoList videolist;
    
    @Autowired
    TemplateEngine templateengine;
    
    @Autowired
    InternetServiceProvider internetserviceprovider;
    
    @Autowired
    LandingPage landingpage;
    
    @Autowired
    Misc misc;
    
     /**
         * This method is needed to support Autowired Spring beans
         */
        public void init(ServletConfig config) throws ServletException 
        {
            super.init(config);
            SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
            config.getServletContext());
        }
        
        
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        System.out.println("ZA FILTER  GLOBAL WAP HEADER IS CALLED UPON.... 1 ");
            
		HttpSession session = request.getSession();
		RequestDispatcher rd=null;
 
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
