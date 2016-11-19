/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.uk;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscDate;
import ume.pareva.util.ZACPA;

/**
 *
 * @author madan
 */
public class CompetitionSuccess extends HttpServlet {
    
    @Autowired
    MobileClubDao mobileclubdao;
    
    @Autowired
    UmeUserDao umeuserdao;
    
     @Autowired
    MobileClubCampaignDao campaigndao;

    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;
    
    @Autowired
    Misc misc;

    @Autowired
    UmeClubDetailsDao clubdetailsdao;
    
    @Autowired
    ZACPA zacpa;

       private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
      public void init(ServletConfig config) throws ServletException
        {
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        /*Reading Headers for Success
        X-PFI-SessionToken
        X-PFI-Status
        X-PFI-RequestTime
        X-PFI-Hash
        X-PFI-Alias
        X-PFI-NetInfo
        X-PFI-ContentId
        X-PFI-Reference
        X-PFI-AnswerRef
        X-PFI-TransactionId
        X-PFI-CallerId
        X-PFI-OptInStatus
                
        */
         HttpSession session = request.getSession();
         UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
         SdcService service = (SdcService) request.getAttribute("umeservice");
        RequestDispatcher rd = null;
        MobileClubCampaign cmpg=null;
        boolean forwarddispatcher=false;
        
        System.out.println("pfi  DOMAIN CHANGE "+dmn.getUnique());
        
        String pfisessiontoken=request.getHeader("X-PFI-SessionToken");
        String pfistatus=request.getHeader("X-PFI-Status");
        String pfirequestTime=request.getHeader("X-PFI-RequestTime");
        String pfihash=request.getHeader("X-PFI-Hash");
        String msisdn=request.getHeader("X-PFI-Alias");
        String pfinetInfo=request.getHeader("X-PFI-NetInfo");
        String pficontentid=request.getHeader("X-PFI-ContentId");
        String pfireference=request.getHeader("X-PFI-Reference");
        String pfianswerref= request.getHeader("X-PFI-AnswerRef");
        String pfitransid=request.getHeader("X-PFI-TransactionId");
        String pficallerid=request.getHeader("X-PFI-CallerId");
        String pfioptinstatus=request.getHeader("X-PFI-OptInStatus");
        String landingpage="",campaignid="",networkid="";
        String serviceid="";
        String optintype="";
        String pagename="";
        
        try{
            serviceid=request.getParameter("serviceid");
        }catch(Exception e){serviceid="";}
        try{
            landingpage=request.getParameter("l");
           }
        catch(Exception e){landingpage="unknown";}
        
        try{
            campaignid=request.getParameter("cid");
        }catch(Exception e){campaignid="";}
        
        try{
            networkid=getOperatorName(pfinetInfo);
        }catch(Exception e){networkid="unknown";}
        
        try{
            optintype=request.getParameter("optintype");
        }catch(Exception e){networkid="unknown";}
        
        UmeUser user=null;
        SdcMobileClubUser clubUser=null;
        boolean sendMessage=false;
        MobileClub club=UmeTempCmsCache.mobileClubMap.get(serviceid);
        
        if(club==null) club=UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
        
        
        java.util.Enumeration names = request.getHeaderNames();
        while(names.hasMoreElements()){
            String name = (String) names.nextElement();
            System.out.println("pfiquizheader "+name + ":" + request.getHeader(name) + "");
        }
        
        if(campaignid!=null)
            cmpg = UmeTempCmsCache.campaignMap.get(campaignid);
        
        //If Status is not empty 
        if(pfistatus!=null && pfistatus.trim().isEmpty()){
            
            //NEW SUBSCRIPTION RECORD 
            if(pfistatus.equalsIgnoreCase("PfiPurchaseSuccess")) {
                
                 
		    Date bstart=new Date();             
                    Date bend=DateUtils.addDays(bstart,7);
                    
            String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn","");
            if (!userUnique.equals("")) user = umeuserdao.getUser(msisdn); 
            
            if (user!=null) {
                
                if (user.getAccountType()==99) {
			    // User account is blocked/barred
		            		           
		            return;
		        }
                        user.updateMap("active", "1");
                        umeuserdao.commitUpdateMap(user);
		        user.clearUpdateMap();

		        clubUser = user.getClubMap().get(club.getUnique());
                        
                        if(clubUser==null){
		        clubUser=umemobileclubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
                        }
                        
                    if(clubUser!=null && clubUser.getActive()==0){
                        sendMessage=true;
                        clubUser.setActive(1);
                        clubUser.setCredits(club.getCreditAmount());
		        clubUser.setAccountType(0);
		            //if (confMsg) clubUser.setAccountType(5);
		        clubUser.setBillingStart(bstart);
                        clubUser.setBillingEnd(bend);
                        clubUser.setNextPush(MiscDate.parseSqlDate("2011-01-01"));
                        clubUser.setSubscribed(bstart);
                        umemobileclubuserdao.saveItem(clubUser);
                        
                        
                    } //end clubuser!=null
                    
                     //ClubUser Record does not exist so create a new one. 
		        else {
                        clubUser = new SdcMobileClubUser();
		            clubUser.setUnique(SdcMisc.generateUniqueId("BUS"));
		            clubUser.setUserUnique(user.getUnique());
		            clubUser.setClubUnique(club.getUnique());
		            clubUser.setParsedMobile(user.getParsedMobile());
		             //System.out.println("THE VALUE OF RESULT IS : "+"SECOND "+aReq.get("result"));
		            clubUser.setActive(1);
		            clubUser.setCredits(club.getCreditAmount());
		            clubUser.setAccountType(0);
		            clubUser.setBillingStart(bstart);
		            clubUser.setBillingEnd(bend);
		            clubUser.setBillingRenew(bstart);
		            clubUser.setPushCount(0);
		            clubUser.setCreated(new Date());
		            clubUser.setCampaign(campaignid);
		            clubUser.setNetworkCode(networkid);
		            clubUser.setUnsubscribed(new Date(0));
		            clubUser.setNextPush(MiscDate.parseSqlDate("2011-01-01"));
                            clubUser.setaExternalId(" ");
                            clubUser.setSubscribed(bstart);
                            clubUser.setLandingpage(landingpage);
		            umemobileclubuserdao.saveItem(clubUser);
		            user.getClubMap().put(club.getUnique(), clubUser);
                             
                        
                    }
                
            
                
            } //user!=null END
             
             // Set up a completely new user
            else{
                sendMessage=true;
                user = new UmeUser(); // <- new user...
		user.setMobile(msisdn);
		user.setWapId(SdcMisc.generateLogin(10));
		user.setDomain("5510024809921CDS"); //keeping partner domain here or default Domain name
		user.setActive(1);
		user.setCredits(club.getCreditAmount());
		String stat = umeuserdao.addNewUser(user);
            if (stat.equals("")) {
                clubUser = new SdcMobileClubUser();
		clubUser.setUnique(SdcMisc.generateUniqueId("BUS"));
		clubUser.setUserUnique(user.getUnique());
		clubUser.setClubUnique(club.getUnique());
		clubUser.setParsedMobile(user.getParsedMobile());
		clubUser.setActive(1);
		clubUser.setCredits(club.getCreditAmount());
		clubUser.setAccountType(0);
		clubUser.setBillingStart(bstart);
		clubUser.setBillingEnd(bend);
		clubUser.setBillingRenew(bstart);
		clubUser.setPushCount(0);
		clubUser.setCreated(new Date());
		clubUser.setCampaign(campaignid);
		clubUser.setNetworkCode(networkid);
		clubUser.setUnsubscribed(new Date(0));
		clubUser.setNextPush(MiscDate.parseSqlDate("2011-01-01")); 
                clubUser.setSubscribed(bstart);
                clubUser.setLandingpage(landingpage);
                umemobileclubuserdao.saveItem(clubUser);
                user.getClubMap().put(club.getUnique(), clubUser);
                    
                    
            } // end if stat equals ""
            
            
            
            
            
            
            
            } // END  ELSE FOR NEW USER AND CLUB USER 
            
            if(sendMessage){
            campaigndao.log("confirm",landingpage, user.getUnique(), user.getParsedMobile(),null, dmn.getUnique(), clubUser.getCampaign(), club.getUnique(),"SUBSCRIBED", 0, request, response,networkid,optintype,"","");
                    
            if(cmpg!=null && cmpg.getSrc().toLowerCase().endsWith("cpa"))
            {            
             if(("subscription").equalsIgnoreCase(cmpg.getCpaType())){
            zacpa.insertCpaLogging(clubUser.getParsedMobile(),cmpg.getSrc(),cmpg.getUnique(),club.getUnique(),clubUser.getNetworkCode());
             }
            }
            }
            request.setAttribute("subscription","new"); 
            
                           
            } //NEW SUBSCRIPTION RECORD END
          
            if(pfistatus.equalsIgnoreCase("PfiAlreadySubscribed")) {
                request.setAttribute("subscription","old"); 
                
            }// END Already subscribed 
            
         
         
            
            
        } // STATUS NOT EMPTY END 
        
        else {
            request.setAttribute("subscription","nostatus");
        }
        
        if(club!=null && club.getClassification().equalsIgnoreCase(("safe"))) {
            pagename="/compsuccess.jsp";
            forwarddispatcher=true;
        }
        else pagename="/content.jsp";
         request.setAttribute("dmn",dmn);
         
         
        
         
         String merchanttoken=(String) request.getAttribute("merchanttoken");
            if(merchanttoken==null || merchanttoken.length()<=0){
            merchanttoken=(String) session.getAttribute("merchanttoken");
            }

            if(merchanttoken==null || merchanttoken.length()<=0){
                if(dmn.getUnique().equalsIgnoreCase("3824583922341llun")) 
                merchanttoken="59E2E445-E8E3-4696-8015-037E7963F716";
    
                else
                    merchanttoken="FBEEBAC4-9F42-4DC4-8E5D-B481A30986B0";
                }
         
         
          request.setAttribute("sessiontoken",pfisessiontoken);
          request.setAttribute("merchanttoken",merchanttoken);
          session.setAttribute("sessiontoken",pfisessiontoken);
          session.setAttribute("merchanttoken",merchanttoken);
         
        if(forwarddispatcher) {
         rd = this.getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + pagename);
            rd.include((ServletRequest)request, (ServletResponse)response);
        }
        else{
            response.sendRedirect("http://x-stream.celldirect.mobi/"+pagename+"?merchanttoken="+merchanttoken+"&sessiontoken="+pfisessiontoken);
            return;
        }

    }
    
     public String getOperatorName(String code) {

     if (code == null || code.trim().length() <= 0) {
	return " ";
	}

    Hashtable<String, String> opcode = new Hashtable<String, String>();
    opcode.put("1", "vodafone");
    opcode.put("2", "o2");
    opcode.put("3", "orange");
    opcode.put("4", "ee");
    opcode.put("6", "three");

    try {
	return opcode.get(code.trim());
	} catch (Exception e) {
            return " ";
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
