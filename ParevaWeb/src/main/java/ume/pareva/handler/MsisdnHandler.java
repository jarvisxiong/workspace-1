/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.handler;

import com.zadoi.service.ZaDoi;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.pojo.UmeUser;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.dao.UmeSessionParameters;

/**
 *
 * @author madan
 */
public class MsisdnHandler extends HttpServlet implements javax.servlet.Servlet {
    
    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;
    
    @Autowired
    UmeUserDao umeuserdao;
    
    @Autowired
    MobileClubDao mobileclubdao;
    
    @Autowired
    UmeClubDetailsDao umeclubdetailsdao;

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
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            String mobileno="";
            String campaignid="";
            String clubUnique = "";
            UmeDomain dmn=null;
            String domain="";
            MobileClub club=null;
            UmeClubDetails clubdetails=null;
            UmeSessionParameters aReq = new UmeSessionParameters(request);
            System.out.println("===Msisdn Handler ZA MSISDN CHECKING STARTING===== ");
             String phase ="";//request.getParameter("phase");
            
            try{
           System.out.println("===ZA-MSISDN-Handler CHECKING STARTING===== INSIDE TRY METHOD ");
           phase = aReq.get("phase");
           dmn = (UmeDomain) request.getAttribute("umedomain");
           domain=dmn.getUnique();
           mobileno=aReq.get("submsisdn");
           club = UmeTempCmsCache.mobileClubMap.get(domain);
           
           if(mobileno!=null && mobileno.trim().length()>0){
                if(mobileno.contains("+")) mobileno=mobileno.replace("+","").trim();
           }
           campaignid=request.getParameter("cid");
           
           System.out.println("ZA-MSISDN-Handler  "+mobileno+" domain:"+domain+" club: "+club.getUnique());
            }
            catch(Exception e){System.out.println("MsisdnHandler error while reading mobileno parameter "+e);e.printStackTrace();}
           boolean falseMobileNo=checkMsisdn(mobileno);
            System.out.println("ZA-MSISDN-Handler  "+mobileno+"THE value of validMobileno is "+!falseMobileNo);
            
           if(!falseMobileNo && (phase==null || (phase!=null && !phase.equals("1"))))
           {
             if (mobileno.startsWith("0")) mobileno = "27" + mobileno.substring(1);  
            
             System.out.println("ZA-MSISDN-Handler  testing domain "+domain+" defaulturl "+dmn.getDefaultUrl());
             UmeUser user = null;
             SdcMobileClubUser clubuser = null;
             
             if (club != null) 
                 clubUnique = club.getUnique();
             
             if (clubUnique != null){
                 try{
		clubuser = umemobileclubuserdao.getClubUserByMsisdn(mobileno,clubUnique);
                 }catch(Exception e){}
                clubdetails=UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
                System.out.println("ZA-MSISDN-Handler  "+" msisdn Handler  msisdn debugging "+mobileno+" club "+clubUnique);
                }
             if (clubuser != null) user = umeuserdao.getUser(clubuser.getUserUnique());
             
             boolean isActive = false;
             String wapid = "";
	     String notificationurl = "http://" + dmn.getDefaultUrl()+ "/smsoptin.jsp?phase=1&optin=1&cid=" + campaignid;
             
             if (user != null) {
		isActive = mobileclubdao.isActive(user, club);
		wapid = user.getWapId();               
             
             if (isActive) {
		response.sendRedirect("http://" + dmn.getDefaultUrl() + "/?id="	+ wapid);
		return;		
                }
             }
             
             if(!isActive)
             {
                 String teaser=clubdetails.getTeaser();
                 int frequency=clubdetails.getFrequency();
                 
                int freeDay=Integer.parseInt(clubdetails.getFreeDay());
                System.out.println("ZA-MSISDN-Handler  "+" mobileno "+mobileno+" club "+clubUnique+" freeday "+freeDay);
                 if(teaser==null || teaser.length()<=0) teaser=club.getClubName()+" - 18+ ONLY";
                 if(freeDay<0) freeDay=1;
                
                 ZaDoi zadoi = new ZaDoi();
                 long beforeAuth = System.currentTimeMillis();
	         String token = zadoi.authenticate();
                 
                 String clubname = club.getClubName();
                 String serviceid = club.getOtpServiceName();
                 String networkCode=zadoi.request_MsisdnNetwork(token, mobileno);
                 
                 System.out.println("ZA-MSISDN-Handler  network name "+networkCode);
                 
                 if(networkCode.trim().equalsIgnoreCase("unknown")){
                  response.sendRedirect("http://" + dmn.getDefaultUrl()+"/zaconfirm.jsp?msisdn=" + mobileno.trim()+ "&cid=" + campaignid+"&submsisdn="+mobileno.trim());
                  return;
                  }
                 
                 else{
                
                        boolean confirmed =zadoi.request_SMSOptIn(token, serviceid, clubname, frequency,mobileno, notificationurl, teaser, freeDay);
                        
                        if (confirmed) {
                           response.sendRedirect("http://"+dmn.getDefaultUrl()+"/smssuccess.jsp?msisdn=" + mobileno+ "&cid=" + campaignid);
                           return;
                        }
                         else if (confirmed == false) {
                            response.sendRedirect("http://"+dmn.getDefaultUrl()+"/smsfailure.jsp?invalidno="+mobileno);
                            return;
                                
			}
                        
                 }
                 
                 
             }
             
             
             
           }
          
           
           if (phase != null && phase.trim().length() > 0 && phase.trim().equals("1")) {
               
               String optin = aReq.get("optin");
	       String requestreference = aReq.get("requestreference");
               String serviceidentifier = aReq.get("serviceidentifier");
               String result = aReq.get("result");//if result is confirm then go to confirm page same as wap
               mobileno = aReq.get("msisdn").trim();
               String errordescription = aReq.get("errordescription");
               String operatorid = aReq.get("operatorid");
               String timestamp = aReq.get("timestamp");
               String stsClubName=aReq.get("clubname");
               boolean falsemsisdn=checkMsisdn(mobileno);
               
                if(mobileno!=null && mobileno.trim().length()>0){
                if(mobileno.contains("+")) mobileno=mobileno.replace("+","").trim();
                }
              
             if(!falsemsisdn){
                 if (result != null
				&& (result.trim().toUpperCase().equals("CONFIRM")                                
                                || result.trim().toUpperCase().equals("ERROR")
                                || result.trim().toUpperCase().equals("TERMINATE")
                                || result.trim().toUpperCase().equals("DECLINE")
                                || result.trim().toUpperCase().equals("DECLINED"))) {
                    
		String redirecturl = "http://" + dmn.getDefaultUrl()
					+ "/zaconfirm.jsp?optin=1&cid=" + campaignid
					+ "&requestreference=" + requestreference
					+ "&serviceidentifier=" + serviceidentifier
                                        + "&result="+result
					+ "&submsisdn=" + mobileno.trim() + "&errordescription="
					+ errordescription + "&operatorid=" + operatorid
					+ "&timestamp=" + timestamp;
                response.sendRedirect(redirecturl);
             }// end result 
                 else{
                     response.sendRedirect(dmn.getDefaultUrl());
                 }
               
           }// end valid msisdn
                   
        } //End phase param
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

    
    
    private boolean checkMsisdn(String mobileno)
    {
       
        boolean notValidmsisdn=(mobileno != null) && (!mobileno.trim().equals(""))
			&& (mobileno.length() < 8 || (!mobileno.trim().startsWith("07")
					&& !mobileno.trim().startsWith("08")
                                        && !mobileno.trim().startsWith("06")
					&& !mobileno.trim().startsWith("0027") && !mobileno.trim().startsWith("27")));
        
        
        return notValidmsisdn;
        
    }
}
