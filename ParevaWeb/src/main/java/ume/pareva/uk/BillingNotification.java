package ume.pareva.uk;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubBillingTry;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobileBillingDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.sdk.MiscDate;
import ume.pareva.userservice.StopUser;
import ume.pareva.util.ZACPA;

/**
 *
 * @author madan
 */
public class BillingNotification extends HttpServlet {

    @Autowired
    ZACPA zacpa;

    @Autowired
    UKSuccessDao uksuccessdao;

    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;

    @Autowired
    MobileClubCampaignDao campaigndao;

    @Autowired
    CpaLoggerDao cpaloggerdao;
    
    @Autowired
    StopUser stopuser;
    
    @Autowired
    MobileBillingDao mobileclubbillingdao;

    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void init(ServletConfig config) throws ServletException {
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

              System.out.println("iminotificationcall  BillingNotification called upon ");
        UmeSessionParameters httprequest = new UmeSessionParameters(request);
        UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
        String domain = dmn.getUnique(); 
        
        System.out.println("iminotificationcall  domain is "+domain);
        String tid="";
        String checktidQuery="";
        String type="";
        String sid="";
        String clubid ="";
        String aHash="";
        String landingPage="";
        String splitby="-";        
        UKSuccess ukSuccess=null;
        String reference_id ="";
        String MSISDN ="";
        String transactionRef_CampaignId="";
        String campaignid="",uknotificationquery="";
        String status_imi = request.getParameter("status");
        MobileClub club=null;
        MobileClubCampaign cmpg=null;
        
        
       Map<String, String[]> parameters = request.getParameterMap();
	java.util.List<String[]> valuesList = new ArrayList<String[]>();

	for (String parameter : parameters.keySet()) {
		String values[] = new String[2];
		values[0] = parameter;
		if (parameter.trim().equalsIgnoreCase("anyx_srvc") || parameter.trim().equalsIgnoreCase("_sdcpath")) {
			continue;
		}

		String[] value = parameters.get(parameter);
		if (value != null && value.length > 0) {
			values[1] = value[0];
		} else {
                    values[1] = "";
		}
		valuesList.add(values);
                
                 System.out.println("iminotificationcall  parameter is "+parameter);
	}
        
         try{
            aHash=request.getParameter("hash");
          }catch(Exception e){aHash="";}
          
          try{          
            sid=request.getParameter("sid");
          }catch(Exception e){sid="";}
          
          try{
          club= UmeTempCmsCache.mobileClubMap.get(sid);
          }catch(Exception e){}
          
          if(sid.equalsIgnoreCase("363")) clubid="7576652769441KDS";
          if(sid.equalsIgnoreCase("358")) clubid="8517362569441KDS";
          
        
          //if(club!=null) clubid=club.getUnique();
          if(club==null) club=UmeTempCmsCache.mobileClubMap.get(clubid);
          
        if (status_imi != null && status_imi.trim().length() > 0) {
		status_imi = status_imi.trim();
	} else {
		status_imi = "-1";
	}
        
        reference_id = request.getParameter("ref");	
        try{
        MSISDN=request.getParameter("msisdn");
        }catch(Exception e){MSISDN="";}
        
        
        System.out.println("iminotificationtrack hash:"+aHash+" sid: "+sid+" msisdn: "+MSISDN+" status: "+status_imi+" ref: "+reference_id+" type: "+request.getParameter("type")+" tid:"+request.getParameter("tid"));
        
        transactionRef_CampaignId=afterUnderScore(reference_id);
          
        //This if for fake Msisdn send from BI (should remove this if done from Alex S cpa daemon. 
         if(MSISDN!=null && MSISDN.equalsIgnoreCase("Hae123Fake")){
            campaignid=request.getParameter("cid");
            
        }
         
        String imiNetworkid = getOperatorName(request.getParameter("networkid"));
              
        if("196".equalsIgnoreCase(sid)){
             transactionRef_CampaignId=afterUnderScore(reference_id);
         }
        
        else if("287".equalsIgnoreCase(sid)){
            //if(reference_id.contains("_")) splitby="_";
            //if(reference_id.contains("-")) splitby="-";
            System.out.println("iminotificationcall of 287 "+reference_id);
            String[] references=afterUnderScores(reference_id,"-");
            
            //System.out.println("287debug "+reference_id);
            transactionRef_CampaignId=references[1];
            try{
            landingPage=references[2];
            }
            
            catch(Exception e){landingPage="";}
            
        }
        else{
            
        }
        
        System.out.println("iminotificationcall  campaignid is "+transactionRef_CampaignId);
           if(transactionRef_CampaignId!=null && MSISDN!=null && !MSISDN.equalsIgnoreCase("Hae123Fake")) {
            campaignid=transactionRef_CampaignId;            
            cmpg = UmeTempCmsCache.campaignMap.get(transactionRef_CampaignId);          
            
            try{
            System.out.println("iminotificationcall  cmpg is  "+cmpg.toString());
            }catch(Exception e){}
        }
           
        try{
            tid=request.getParameter("tid");
            type=request.getParameter("type");
            boolean tidAlreadyExist=false;
            
            if(type!=null && !type.toLowerCase().contains("stop"))
            tidAlreadyExist=uksuccessdao.tidExist(tid);
            
             System.out.println("iminotificationcall  tid  is "+tid +" AND  tid Already Exist "+tidAlreadyExist);
            
            if(!tidAlreadyExist) {
            String unique = Misc.generateUniqueId();
            Date curDate = new Date(System.currentTimeMillis());
           for (String[] value : valuesList) {
                uknotificationquery= " INSERT INTO uk_notification_info"
			+ " (aUnique, paramName, paramValue, entrytime)"
			+ " VALUES('"+unique+"','"+value[0]+"','"+value[1]+"','"+MiscDate.toSqlDate(curDate)+"')";
                
                try{
                     System.out.println("iminotificationcall  query for uknotificationinfo is "+uknotificationquery);
                    zacpa.executeUpdateCPA(uknotificationquery);
                    
                }catch(Exception e){e.printStackTrace();}
           }
            
            
            
            
            System.out.println("iminotificationcall type  is "+type);
            //STOP Handling 
            if(type!=null && type.equalsIgnoreCase("stop")){
                System.out.println("iminotification_debug "+"type "+type);
                System.out.println("xstreamtesting STOP Notification from MSISDN "+MSISDN+" type "+type);
                
              //  ================================================================================
                   ukSuccess=new UKSuccess();
                ukSuccess.setTid(tid);
                ukSuccess.setaParsedMobile(MSISDN);
                ukSuccess.setType("stop");
                ukSuccess.setExpiry(sdf2.format(new Date()));
                System.out.println("iminotificationcall STOP Notification from MSISDN "+MSISDN+" type "+type+" stopping user");
        	uksuccessdao.saveSuccessfulUser(ukSuccess);
                
                boolean stopped=stopuser.stopSingleSubscription(MSISDN,clubid,null,null);
                if(cmpg!=null && cmpg.getSrc().endsWith("RS")){                 
                String revloggingquery="insert into revShareLogging (aUnique,aAmount,aCurrencyCode,aParsedMobile,aEncryptedMSISDN,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc,aEvent) values"
	            	+"('"+Misc.generateUniqueId()+"','0','"+cmpg.getPayoutCurrency()+"','"+MSISDN+"','"+MiscCr.encrypt(MSISDN)+"','"+cmpg.getUnique()+"','"+club.getUnique()+"','"+sdf2.format(new Date())+"','"+sdf2.format(new Date())+"','0','"+imiNetworkid+"','"+cmpg.getSrc() +"','2')";
                
                zacpa.executeUpdateCPA(revloggingquery);         
                }
                
                    
                     
            } //END STOP Handling 
            
              //This is to check for UKSuccess for Subscription Flow
            ukSuccess=uksuccessdao.checkTid(tid);
            
           System.out.println("iminotificationcall  checking uksuccess now ");
            
            if(ukSuccess!=null && ukSuccess.getStatus().equals("1") && !type.equalsIgnoreCase("stop") ){        		
                Calendar c = Calendar.getInstance();
                c.setTime(sdf2.parse(ukSuccess.getExpiry()));
                c.add(Calendar.DATE, 7);  // number of days to add
        	ukSuccess.setExpiry(sdf2.format(c.getTime()));
        	uksuccessdao.updateExpiryDate(ukSuccess);
                 
            } //END UKSUCCESS NOT STOP NOTIFICATION
            
                  
         //========  START HANDLING SUBSCRIPTION SERVICE ========================  
        if(type.equalsIgnoreCase("sub")) {
       if(status_imi.equals("1")){ 
            Calendar currentTime=Calendar.getInstance();
            String creationdate=sdf2.format(currentTime.getTime());
            currentTime.add(Calendar.HOUR,168);
            String expiry=sdf2.format(currentTime.getTime());
            UKSuccess successfuluser=new UKSuccess();
            successfuluser.setTid(tid);
            successfuluser.setClubUnique(clubid);
            if(cmpg!=null) successfuluser.setCampaignId(cmpg.getUnique());
            else successfuluser.setCampaignId("");
            
            successfuluser.setStatus(status_imi);
            successfuluser.setType(type);
            successfuluser.setSid(sid);
            successfuluser.setaParsedMobile(MSISDN);
            successfuluser.setaNetworkId(imiNetworkid);
            successfuluser.setaHash(aHash);
            successfuluser.setCreated(sdf2.parse(creationdate));        
            successfuluser.setExpiry(expiry);
            uksuccessdao.saveSuccessfulUser(successfuluser);
            
            
            
        if(cmpg!=null && cmpg.getSrc().toLowerCase().endsWith("cpa"))
            {     
                System.out.println("iminotificationcall  cmpg of CPA is  "+cmpg.getSrc()+"  type is "+cmpg.getCpaType());
             if(("billing").equalsIgnoreCase(cmpg.getCpaType())){
            zacpa.insertCpaLogging(MSISDN,cmpg.getSrc(),cmpg.getUnique(),clubid, imiNetworkid);
             }
        }
        
        SdcMobileClubUser clubUser = null;
         
             try{
                 String successResponse="00";
             clubUser=umemobileclubuserdao.getClubUserByMsisdn(MSISDN,clubid);
             if(clubUser!=null){
                   if(DateUtils.isSameDay(new Date(), clubUser.getSubscribed())) {
                       successResponse="003";
                    }
                    MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                        mobileClubBillingTry.setUnique(reference_id);
                        mobileClubBillingTry.setLogUnique(Misc.generateUniqueId());
                        mobileClubBillingTry.setAggregator("PFI");
                        mobileClubBillingTry.setClubUnique(clubid);
                        mobileClubBillingTry.setCreated(new Date());
                        mobileClubBillingTry.setNetworkCode(clubUser.getNetworkCode().toLowerCase());
                        mobileClubBillingTry.setParsedMsisdn(MSISDN);
                        mobileClubBillingTry.setRegionCode("UK");
                        mobileClubBillingTry.setResponseCode(successResponse);
                        mobileClubBillingTry.setResponseDesc("successful");
                        mobileClubBillingTry.setResponseRef(reference_id);
                        mobileClubBillingTry.setStatus("success");
                        mobileClubBillingTry.setTransactionId(reference_id);
                        mobileClubBillingTry.setCampaign(clubUser.getCampaign());
                        mobileClubBillingTry.setTariffClass(club.getPrice());
                        mobileclubbillingdao.insertBillingTry(mobileClubBillingTry);
                        
               
             }
             }
             catch(Exception e){}
     
       } //================ END HANDLING SUBSCRIPTION =======================
       
       else if(status_imi.equals("3")){ 
            String responsecode = "51";
            String responsedesc = "Insufficient Fund";
            String responseref = "failed";
            
            MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                        mobileClubBillingTry.setUnique(reference_id);
                        mobileClubBillingTry.setLogUnique(Misc.generateUniqueId());
                        mobileClubBillingTry.setAggregator("PFI");
                        mobileClubBillingTry.setClubUnique(clubid);
                        mobileClubBillingTry.setCreated(new Date());
                        mobileClubBillingTry.setNetworkCode("");
                        mobileClubBillingTry.setParsedMsisdn(MSISDN);
                        mobileClubBillingTry.setRegionCode("UK");
                        mobileClubBillingTry.setResponseCode(responsecode);
                        mobileClubBillingTry.setResponseDesc(responsedesc);
                        mobileClubBillingTry.setResponseRef(reference_id);
                        mobileClubBillingTry.setStatus(responseref);
                        mobileClubBillingTry.setTransactionId(reference_id);
                        mobileClubBillingTry.setCampaign(cmpg.getUnique());
                        mobileClubBillingTry.setTariffClass(club.getPrice());
                        mobileclubbillingdao.insertBillingTry(mobileClubBillingTry);
           
       }
             else { 
            String responsecode = "99";
            String responsedesc = "Other Error";
            String responseref = "failed";
            
            MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                        mobileClubBillingTry.setUnique(reference_id);
                        mobileClubBillingTry.setLogUnique(Misc.generateUniqueId());
                        mobileClubBillingTry.setAggregator("PFI");
                        mobileClubBillingTry.setClubUnique(clubid);
                        mobileClubBillingTry.setCreated(new Date());
                        mobileClubBillingTry.setNetworkCode("");
                        mobileClubBillingTry.setParsedMsisdn(MSISDN);
                        mobileClubBillingTry.setRegionCode("UK");
                        mobileClubBillingTry.setResponseCode(responsecode);
                        mobileClubBillingTry.setResponseDesc(responsedesc);
                        mobileClubBillingTry.setResponseRef(reference_id);
                        mobileClubBillingTry.setStatus(responseref);
                        mobileClubBillingTry.setTransactionId(reference_id);
                        mobileClubBillingTry.setCampaign(cmpg.getUnique());
                        mobileClubBillingTry.setTariffClass(club.getPrice());
                        mobileclubbillingdao.insertBillingTry(mobileClubBillingTry);
           
       }
            }
       
            
            
        } //END TID NOT EXIST     
        }catch(Exception e){System.out.println("Exception BillingNotification line no.325");}
           
       
       request.setAttribute("ukbillednotify","true");
      RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/pfibilling.jsp");
            rd.include((ServletRequest)request, (ServletResponse)response);

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

    public String afterUnderScore(String longString) {
        if (longString == null) {
            return "";
        }

        int index = longString.indexOf("_");
        if (index == -1) {
            return longString;
        }

        String aux = longString.substring(index + 1);
        if (aux.equalsIgnoreCase("null")) {
            return "";
        }

        return aux;
    }

    public String[] afterUnderScores(String longString, String separateby) {
        System.out.println("287debug  inside underscore: " + longString);
        String[] array = longString.split(separateby);
        System.out.println("287debug 0: " + array[0] + " 1: " + array[1] + " 2: " + array[2]);
        return array;
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
