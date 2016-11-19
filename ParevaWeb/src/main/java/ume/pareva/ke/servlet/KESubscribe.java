package ume.pareva.ke.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.QueryHelper;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.SmsDoiLogDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.ke.UserStatus;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SmsDoiRequest;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.servlet.Check24Hour;
import ume.pareva.snp.SnpUserDao;

import com.zadoi.service.ZaDoi;

public class KESubscribe extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( KESubscribe.class.getName());
	
	@Autowired
	UmeMobileClubUserDao umemobileclubuserdao;
	
	@Autowired
	UmeUserDao umeuserdao;
	
	@Autowired
	MobileClubDao mobileclubdao;
    
	@Autowired
	MobileClubCampaignDao mobileclubcampaigndao;
	
	@Autowired
	HandsetDao handsetdao;
	
	@Autowired
    SmsDoiLogDao smsdoilogdao;
	
	@Autowired
    Check24Hour check24hour;
	
	@Autowired 
	SnpUserDao snpuserdao;
	
	@Autowired 
	UserStatus userStatus;
	
	@Autowired
	QueryHelper queryhelper;
	 
    public KESubscribe() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
                config.getServletContext());
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processsRequest(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processsRequest(request,response);
	}
	
	protected void processsRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UmeUser user=null;
		SdcRequest sdcRequest=new SdcRequest(request);
        UmeDomain dmn=sdcRequest.getDomain();
        String msisdn=sdcRequest.get("msisdn");
        String campaignId=sdcRequest.get("cid");
        MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
    	boolean validMsisdn=checkMsisdn(msisdn);
        if(validMsisdn){
        	if(msisdn.startsWith("0"))
        		msisdn="254"+msisdn.substring(1);
        	SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn,club.getUnique());
        	if(clubUser!=null){
        		user = umeuserdao.getUser(clubUser.getParsedMobile());
        	}
        	boolean status=userStatus.checkIfUserActiveAndNotSuspended(user, club);
        	if(status){
        		response.sendRedirect("http://" + dmn.getDefaultUrl() + "/?id="	+ user.getWapId());
        	}else{
        		ZaDoi zadoi = new ZaDoi();
        		String token = zadoi.authenticate();
        		String networkCode=zadoi.request_MsisdnNetwork(token, msisdn);
        		if(networkCode.trim().equalsIgnoreCase("unknown")){
        			String doiRequstStatus="DOI Request Not Sent";
        			forwardToSmsFailure(dmn,msisdn,doiRequstStatus,request,response);
        			return;
        		}else{
        			if(check24hour.hasValidDOIRequests(msisdn, club)){
        				sendTeaser(request,response,networkCode,msisdn);
        				return;
        			}else{
        				String doiRequestStatus="DOI Request Already Sent";
        				forwardToSmsFailure(dmn,msisdn,doiRequestStatus,request,response);
        				return;
        			}
        		}
        	}
        }else{
        	response.sendRedirect("http://"+dmn.getDefaultUrl()+"?cid="+campaignId+"&wrongMsisdn=true");
        }
	}
		
	public void sendTeaser(HttpServletRequest request,HttpServletResponse response,String networkCode,String msisdn){
		SdcRequest sdcRequest=new SdcRequest(request);
		UmeDomain dmn=sdcRequest.getDomain();
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		UmeClubDetails clubdetails=UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
		String campaignId=sdcRequest.get("cid");
		String landingPage=sdcRequest.get("landingPage");
		String requestId=Misc.generateUniqueId();
		String teaser=clubdetails.getTeaser();
		if(teaser==null || teaser.length()<=0) 
			teaser=" X-Rated Vids - 18+ ONLY";
		
		int freeDay=1;
		try{
			freeDay=Integer.parseInt(String.valueOf(clubdetails.getFreeDay()));
		}catch(Exception e){
			logger.info("Free Day Not Set for Club: "+ club.getName());
			freeDay=1;
		}
		
		int frequency=3;
		try{
			frequency=clubdetails.getFrequency();
		}catch(Exception e){
			logger.info("Frequency Not Set for Club: "+club.getName());
			frequency=3;
		}
		String userAgent=getUserAgent(request);
		String ipAddress=request.getAttribute("ipAddress").toString();
		String notificationUrl="http://" + dmn.getDefaultUrl()+ "/keconfirm.jsp?phase=1&optin=1&cid="+campaignId+"&requestId="+requestId+"&ipAddresss="+ipAddress+
								"&landingPage="+landingPage+"&userAgent="+userAgent;
	        
		ZaDoi zadoi=new ZaDoi();
		String token=zadoi.authenticate();
		boolean confirmed =zadoi.request_SMSOptIn(token,club.getOtpSoneraId(),club.getName(),frequency,msisdn,notificationUrl,teaser,freeDay);
		if(confirmed){
			MobileClubCampaign cmpgn = null;
			HttpSession session=request.getSession(false);
			biLog(msisdn,landingPage,networkCode,request,response);
			saveSmsDoiRequest(requestId,club.getName(),frequency,msisdn,notificationUrl,token);
			if(campaignId!=null && campaignId.trim().length()>0) {
                cmpgn = UmeTempCmsCache.campaignMap.get(campaignId);
            }
			if(cmpgn!=null && cmpgn.getSrc().trim().endsWith("CPA")){
				updateCpaVisitLog(msisdn,campaignId,session);
			}
			 if(cmpgn!=null && cmpgn.getSrc().trim().endsWith("RS")){
				 updateRevShareVisitLog(msisdn,campaignId,session);
			 }
			forwardToSmsSuccess(dmn,msisdn,request,response);
        }else{
        	String doiRequstStatus="DOI Request Not Sent";
        	forwardToSmsFailure(dmn,msisdn,doiRequstStatus,request,response);
        }


	}
	
	public String getUserAgent(HttpServletRequest request){
		String initial=request.getHeader("User-Agent");
		int end = initial.indexOf(")");
		int start = initial.indexOf("(") + 1;
		String[] aux =null; 
		String uagent="";

		try{
			aux =initial.substring(start, end).split(";");
			aux[1] =java.net.URLEncoder.encode(aux[1].trim(), "UTF-8");
			uagent=aux[1];
		}catch(Exception e){
			logger.error("Error Getting User Agent");
			uagent="";
		}
		return uagent;
	}
	
	public void forwardToSmsFailure(UmeDomain dmn,String msisdn,String doiRequestStatus,HttpServletRequest request,HttpServletResponse response){
		try{
			request.getServletContext().getRequestDispatcher("/KESmsFailure?msisdn="+msisdn+"&doiRequestStatus="+doiRequestStatus).forward(request, response);
		}catch(Exception e){
			logger.error("Error Forwarding To smsfailure.jsp");
			e.printStackTrace();
		}
	}
	
	public void forwardToSmsSuccess(UmeDomain dmn,String msisdn,HttpServletRequest request,HttpServletResponse response){
		try{
			request.getServletContext().getRequestDispatcher("/KESmsSuccess?msisdn="+msisdn).forward(request, response);
		}catch(Exception e){
			logger.error("Error Forwarding To smssuccess.jsp");
			e.printStackTrace();
		}
	}
	
	public void biLog(String msisdn,String template,String networkCode,HttpServletRequest request,HttpServletResponse response){
		UmeDomain dmn=(UmeDomain)request.getAttribute("umedomain");
		SdcRequest sdcRequest=new SdcRequest(request);
		String campaignId=sdcRequest.get("cid");
		Handset handset=handsetdao.getHandset(request);
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
		String clubUnique=club.getUnique();
		mobileclubcampaigndao.log("subscribe",template,msisdn,msisdn,handset,dmn.getUnique(),campaignId,clubUnique,"MANUAL",0,request,response,networkCode.toLowerCase().trim());    	
	}
	
	 public boolean checkMsisdn(String msisdn){
		 boolean valid=false;
		 if(msisdn != null && !msisdn.trim().equals("")){
			 if(msisdn.length()>9){
				 if (msisdn.startsWith("07")|| msisdn.startsWith("254")){
					 valid=true;
				 }
			 }else if (msisdn.length()==9){
				 if(msisdn.startsWith("7")){
					 valid=true;
				 }
			 }
				 
		 }
		return valid;	 	        
	 }	
	 
	 public void saveSmsDoiRequest(String requestId,String clubName,int frequency,String msisdn,String notificationUrl,String token){
		 SmsDoiRequest smsDoiRequest = new SmsDoiRequest();
         smsDoiRequest.setRequestUid(requestId);
         smsDoiRequest.setClubName(clubName);
         smsDoiRequest.setFrequency(frequency);
         smsDoiRequest.setMsisdn(msisdn);
         smsDoiRequest.setNotificationUrl(notificationUrl);
         smsDoiRequest.setToken(token);
         smsDoiRequest.setRequestedTime(new java.sql.Timestamp(System.currentTimeMillis()));
         smsdoilogdao.saveSmsDoiRequest(smsDoiRequest);
         
	 }
	 
	 public void updateRevShareVisitLog(String msisdn,String campaignId,HttpSession session){
		 String parameter1=(String) session.getAttribute("revparam1");
		 String parameter2=(String) session.getAttribute("revparam2");
		 String parameter3=(String) session.getAttribute("revparam3");  

		 String cpaloggingquery="UPDATE revShareVisitLog SET aParsedMobile='"+msisdn+"' WHERE aCampaignId='"+campaignId+"' AND  (parameter1='"+parameter1+"' AND parameter2='"+parameter2+"'"
				 + " AND parameter3='"+parameter3+"')";

		 int updateRow=queryhelper.executeUpdateQuery(cpaloggingquery,"smsoptin.jsp query ="+cpaloggingquery);
	 }
	 
	 public void updateCpaVisitLog(String msisdn,String campaignId,HttpSession session){
		 String cpaparameter1=(String) session.getAttribute("cpaparam1");
		 String cpaparameter2=(String) session.getAttribute("cpaparam2");
		 String cpaparameter3=(String) session.getAttribute("cpaparam3");  

		 String cpavisitUpdateQuery="UPDATE cpavisitlog SET aParsedMobile='"+msisdn+"' WHERE aCampaignId='"+campaignId+"' AND  (aHashcode='"+cpaparameter1+"' AND cpacampaignid='"+cpaparameter2+"'"
				 + " AND clickid='"+cpaparameter3+"')";

		 int updateRow=queryhelper.executeUpdateQuery(cpavisitUpdateQuery,"smsoptin.jsp query="+cpavisitUpdateQuery);

	 }
	 
}
