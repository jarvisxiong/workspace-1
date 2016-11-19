package ume.pareva.ire;

import com.mitchellbosecke.pebble.PebbleEngine;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.MobileNetworksDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.pojo.CampaignHitCounter;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.TemplatePojo;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.servlet.ZAIndex;
import ume.pareva.template.TemplateEngine;//
import ume.pareva.userservice.CpaRevShareServices;
import ume.pareva.userservice.InternetServiceProvider;
import ume.pareva.userservice.LandingPage;

/**
 *
 * @author madan
 */
@WebServlet(name = "IEIndex", urlPatterns = {"/IEIndex"})
public class IEIndex extends HttpServlet {
	private final Logger logger = LogManager.getLogger(IEIndex.class.getName());

	@Autowired
	InternetServiceProvider ipprovider;

	@Autowired
	HandsetDao handsetdao;

	@Autowired
	LandingPage landingpage;

	@Autowired
	CpaRevShareServices cparevshare;

	@Autowired
	MobileNetworksDao mobilenetwork;

	@Autowired
	MobileClubCampaignDao campaigndao;

	@Autowired
	CampaignHitCounterDao campaignhitcounterdao;

	@Autowired
	TemplateEngine templateEngine;//

	@Autowired
	MobileClubDao mobileclubdao;

	@Autowired
	UmeMobileClubUserDao umemobileclubuserdao;

	@Autowired
	MobileClubBillingPlanDao billingplandao;
        
        @Autowired
        UmeTempCache umesdc;


	/**
	 * This method is needed to support Autowired Spring beans
	 */
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
	 protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			 throws ServletException, IOException {
		 response.setContentType("text/html;charset=UTF-8");

		 ThreadContext.put("ROUTINGKEY", "IE");
                 ThreadContext.put("EXTRA","");
		 logger.info("ieindex "+"ProcessREQEST is called upon ");

		 HttpSession session = request.getSession();
		 logger.info("ieindex "+"session id is  "+session.getId());
		 ServletContext application = request.getServletContext(); //.setAttribute(null, request);
		 logger.info("ieindex "+"servletContext is  "+application.getContextPath());
		 UmeSessionParameters aReq = new UmeSessionParameters(request);
		 UmeUser user = aReq.getUser();
		 UmeDomain dmn = aReq.getDomain();
		 SdcService service = aReq.getService();
		 String domain = dmn.getUnique();
		 String ddir = dmn.getDefPublicDir();
		 String lang = aReq.getLanguage().getLanguageCode();
		 logger.info("ieindex "+" after SDC request ");
		 PrintWriter writer = response.getWriter();
		 Map<String, Object> context = new HashMap();
                 String templatefolder="";
                 
            

		 PebbleEngine engine=templateEngine.getTemplateEngine(dmn.getUnique());
                 
                Map<String,TemplatePojo> templates=new HashMap<String,TemplatePojo>();    
                    templates=umesdc.getTemplateMap();
                    if(templates.get(domain)!=null){
                    TemplatePojo template=templates.get(domain);
                    templatefolder=template.getTemplateName();
                    request.setAttribute("templatefolder", templatefolder);
                    session.setAttribute("templatefolder", templatefolder);
                    }

		 String fileName ="index.jsp";// request.getServletPath();
		 fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		 fileName = fileName.substring(0, fileName.lastIndexOf("."));
		 System.out.println("zaindex "+" FileName is  "+fileName);

		 String cloudfronturl = dmn.getContentUrl();
		 session.setAttribute("cloudfrontUrl", cloudfronturl);
		 application.setAttribute("cloudfrontUrl", cloudfronturl);

		 session.setAttribute("dmn",dmn);
		 request.setAttribute("dmn",dmn);
		 session.setAttribute("aReq",aReq);

		 session.setAttribute("mobileclubdao", mobileclubdao);
		 request.setAttribute("mobileclubdao", mobileclubdao);

		 logger.info("ieindex "+"contentUrl is  "+cloudfronturl);
		 String msisdn = aReq.getMsisdn();
		 if("".equalsIgnoreCase(msisdn)){
                     msisdn=aReq.get("msisdn");
                 }

		 String enMsisdn = aReq.get("mid");
		 String transaction_ref = "iecpa";
		 String pubId = "";
		 // ==== CPA Requirement end ===========
		 MobileClubCampaign cmpg = null;

		 String simulate = aReq.get("simulate");
		 String pageName = aReq.get("pageName");
		 request.setAttribute("simulate", simulate);
		 request.setAttribute("pageName", pageName);

		 //=====  GET the IP Address of Visitor ============================
		 String myip = request.getHeader("X-Forwarded-For");
		 if (myip != null) {
			 int idx = myip.indexOf(',');
			 if (idx > -1) {
				 myip = myip.substring(0, idx);
			 }
		 }
		 String myisp=ipprovider.findIsp(myip);
                 String originalisp=myisp.toLowerCase();
                   if(originalisp.trim().contains(("'"))){
            originalisp=originalisp.replace("'","");
            if(originalisp!=null && !"".equals(originalisp.trim()) && originalisp.length()>29){
               originalisp=originalisp.substring(0,20);
           }
        }
		 session.setAttribute("userip", myip);
		 request.setAttribute("userip", myip);
		 session.setAttribute("userisp",myisp);
		 request.setAttribute("userisp",myisp);
		 session.setAttribute("networkid",myisp);
		 request.setAttribute("networkid",myisp);

		 //======== HANDSET Recognition ==============
		 Handset handset = handsetdao.getHandset(request);
		 if (handset != null) {
			 session.setAttribute("handset", handset);
			 request.setAttribute("handset", handset);            
		 }
		 //======== END HANDSET Recognition ==============

		 //========= Reading Encrypted MSISDN ====================== 
		 if (!enMsisdn.equals("")) {
			 System.out.println("iemsisdn the mid parameter is "+enMsisdn);
			 context.put("mid", enMsisdn);
			 String deMsisdn = MiscCr.decrypt(enMsisdn);
			 if (!deMsisdn.equals("")) {
				 if (deMsisdn.startsWith("0")) {
					 deMsisdn = "353" + deMsisdn.substring(1);
					 System.out.println("iemsisdn the deMsisdn value of mid "+enMsisdn+"  is "+deMsisdn);
				 }
				 msisdn = deMsisdn;
				 System.out.println("iemsisdn  finally the value of msisdn is "+msisdn+"  of mid "+enMsisdn);

			 }
		 }
		 //========= END  Reading Encrypted MSISDN ====================== 

		 //========= Reading Campaign and Initializing Club and Campaign ======================
		 String campaignId = aReq.get("cid");
		 MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
		 UmeClubDetails userclubdetails=null;
		 String clubUnique = "";
		 if (club != null) {
			 clubUnique = club.getUnique();
			 userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
			 request.setAttribute("club",club);
			 session.setAttribute("club",club);

		 }

		 if (campaignId != null && campaignId.trim().length() > 0) {

			 if (!campaignId.equals("")) {
				 cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
				 context.put("campaignid",campaignId); //cid
				 request.setAttribute("cmpg",cmpg);
				 session.setAttribute("cmpg",cmpg);
				 request.setAttribute("campaignId",campaignId);
				 session.setAttribute("campaignId",campaignId);
			 }

		 }
                 
                      // Redirecting tsc.winalert.eu to  Quiz2win.mobi
//                 if(dmn.getUnique().equalsIgnoreCase("4392826144541llun")) {
//                     response.sendRedirect("http://irl.quiz2win.mobi/?cid="+campaignId+"&mid="+enMsisdn+"&msisdn="+msisdn); return;
//                 }

		 //========= Reading Campaign and Initializing Club and Campaign ======================
		 // ====  Retrieving  LandingPage in this part =========================================
		 String landingPage = "";

		 if (cmpg != null && cmpg.getSrc().trim().equalsIgnoreCase("test")) {
			 landingPage = landingpage.initializeLandingPage(domain, campaignId, "test");           
		 }
		 else {
			 if (!campaignId.equals("")) {
				 landingPage = landingpage.initializeLandingPage(domain, campaignId, "all");                    
			 } 
			 else {
				 landingPage = landingpage.initializeLandingPage(domain);
			 }

		 }

		 request.setAttribute("landingPage", landingPage);
		 session.setAttribute("landingPage",landingPage);
		 request.setAttribute("cid",campaignId);
		 session.setAttribute("cid",campaignId);
		 request.setAttribute("engine",engine);
		 session.setAttribute("engine",engine);
		 request.setAttribute("landingpageengine",landingpage);
		 session.setAttribute("landingpageengine",landingpage);

		 request.setAttribute("domain",domain);
		 session.setAttribute("domain",domain);
		 // ====  End Landing Page Retrieval =============================================

		 //=======  CPA and RS =================
		 if(cmpg!=null && session.getAttribute("bilogged")==null){ // session getATtribute bilogged is for avoiding duplicate logging.
			 //cparevshare.cparevshareLog(request, cmpg,msisdn,transaction_ref, session,"entry");
			 try{
				 pubId=session.getAttribute("cpapubid")+"";
			 }
			 catch(Exception e){pubId="";}

			 request.setAttribute("cpapubid",pubId);
			 session.setAttribute("cpapubid",pubId);  

		 }
		 //=======  END CPA and RS =================

		 // ==== Logging to mobileClubCampaignLog Hits ===============================

		 try {
			 String networkname = myisp.toLowerCase();
			 networkname = mobilenetwork.getMobileNetwork("IE", networkname);

			 System.out.println("IE INDEX JSP LOGGING TO CAMPAIGN LOG ");
			 if (!aReq.get("cid").equalsIgnoreCase("") && session.getAttribute("bilogged")==null) {
				 campaigndao.log("ieindex", landingPage, msisdn, msisdn, handset, domain, campaignId, clubUnique, "INDEX", 0, request, response, networkname,"",originalisp,"",pubId);

				 if(msisdn!=null && !msisdn.equalsIgnoreCase(""))
					 campaigndao.log("ieindex", landingPage, msisdn, msisdn, handset, domain, campaignId, clubUnique, "IDENTIFIED", 0, request, response, networkname,"",originalisp,"",pubId);


				 session.setAttribute("bilogged","true"); //This session is set to avoid duplicate BI logging.                     

				 //=== To update campaignHit counter after logging for LandingPage Logic Rotation=====
				 /* Commented on 2016-10-14
                                 Date today = new Date();
				 CampaignHitCounter campaignHitCounter = campaignhitcounterdao.HitRecordExistsOrNot( today, domain, campaignId, landingPage);
				 if (campaignHitCounter == null) {
					 campaignHitCounter = new CampaignHitCounter();
					 campaignHitCounter.setaUnique(Misc.generateUniqueId());
					 campaignHitCounter.setaDomainUnique(domain);
					 campaignHitCounter.setCampaignId(campaignId);
					 campaignHitCounter.setLandingPage(landingPage);
					 campaignHitCounter.setDate(today);
					 campaignHitCounter.setHitCounter(1);
					 campaignHitCounter.setSubscribeCounter(0);
					 campaignhitcounterdao.saveCampaignHitCounter(campaignHitCounter);

				 } else {
					 //campaignHitCounter.setDate(today);
					 campaignhitcounterdao.updateCampaignHitCounter(campaignHitCounter);
				 }
				 //=== END To update campaignHit counter after logging for LandingPage Logic Rotation=======
                                 */
			 } //END Logging to INDEX HITS

                            

		 } catch (Exception e) {
			 System.out.println("IE Index Error for campaignlog " + e);
			 logger.error("IE INDEX ERROR FOR CAMPAIGN LOG {}", e.getMessage());
			 logger.error("IE INDEX ERROR FOR CAMPAIGN LOG ", e);
		 }

		 if(club!=null){
			 String transactionid=MiscCr.MD5("moonlight"+"||"+Misc.generateUniqueId());
			 String txturi=java.net.URLEncoder.encode("partner:91909c32-e422-42e3-845a-d3cbf15af4fa","UTF-8");

			 String msisdnrouter="<img src=\"http://msisdn.sla-alacrity.com/authenticate/image.gif?transaction_id="+transactionid+"&uri="+txturi+"\" alt=\"\">";
			 System.out.println("irevodafone step1 "+transactionid+" msisdnrouter: "+msisdnrouter);

			 if(campaignId!=null && campaignId.equalsIgnoreCase("3356177641KDS")){
				 System.out.println(campaignId+" msisdnrouter is "+msisdnrouter);
			 }
			 if(myisp!=null && myisp.trim().length()>0 && myisp.toLowerCase().trim().contains("vodafone")) {

				 session.setAttribute("showmsisdnrouter","true");
				 request.setAttribute("showmsisdnrouter","true");

			 }
			 session.setAttribute("showmsisdnrouter","true");
			 request.setAttribute("showmsisdnrouter","true");
			 context.put("contenturl","http://"+dmn.getContentUrl());
			 context.put("endofmonth",getEndOfmonth());
			 context.put("landingpage",landingPage);
			 context.put("myisp",myisp);
			 context.put("msisdn",msisdn);
                         
                        if(!"".equalsIgnoreCase(msisdn)) {
                            context.put("msisdnexist","true");
                            context.put("mid", Misc.encrypt(msisdn));
                            context.put("showmsisdnrouter","false");
                            session.setAttribute("msisdnidentified", true);
                        }            
                        else{
                            context.put("msisdnexist","false");
                            context.put("mid","");
                            context.put("showmsisdnrouter","true");
                        }
                         
                         context.put("transactionid",transactionid);
			 context.put("msisdnrouter",msisdnrouter.trim());
			 //System.out.println("irevodafone step1 URL"+msisdnrouter);

			 session.setAttribute("transactionid",transactionid);
			 session.setAttribute("msisdnrouter",msisdnrouter);
			 request.setAttribute("transactionid",transactionid);
			 request.setAttribute("msisdnrouter",msisdnrouter);
			 if(userclubdetails.getBillingType().equalsIgnoreCase("subscription")){

				 if(userclubdetails.getServiceType().equalsIgnoreCase("content")) {

					 try {
						 application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp").forward(request, response);
						 return;
					 } catch (Exception e) {
						 logger.error("WAPXIRE EXCEPTION {}", e.getMessage());
						 logger.error("WAPXIRE EXCEPTION ", e);
					 } 

				 } //End if clubdetails is content





				 else if(userclubdetails.getServiceType().equalsIgnoreCase("competition")) {

					 if(user!=null){
						 String clubId=aReq.get("clublink");
						 request.setAttribute("user", user);
						 SdcMobileClubUser clubUser=umemobileclubuserdao.getClubUserByMsisdn(user.getParsedMobile(),clubId);
						 try {
							 String voucherwindomain="9524711450641llun";
							 //application.getRequestDispatcher("/" + System.getProperty("dir_" + voucherwindomain + "_pub") + "/question.jsp?msisdn="+user.getParsedMobile()+"&clubid="+club.getUnique()).forward(request, response);

							 if(clubUser!=null && clubUser.getActive()==1){
								 response.sendRedirect("http://www.voucherwin.co.uk/question.jsp?msisdn="+user.getParsedMobile()+"&clubid="+clubId);
								 return;
							 }
							 //                        else{
							 //                            engine.getTemplate(landingPage).evaluate(writer, context);
							 //                        }
						 } catch (Exception e) {
							 logger.error("IMIUK EXCEPTION {}", e.getMessage());
							 logger.error("IMIUK EXCEPTION ", e);
						 }

					 }

					 //                     else{
					 //                     try{
					 //                     engine.getTemplate(landingPage).evaluate(writer, context);
					 //                     }
					 //                     catch(Exception e){}
					 //                 }

				 }

			 }

			 else if(userclubdetails.getBillingType().equalsIgnoreCase("adhoc")){
				 System.out.println("ierealgirls  adhoc service condition ");
				 if(user!=null){
					 request.setAttribute("user", user);
					 SdcMobileClubUser clubUser=umemobileclubuserdao.getClubUserByMsisdn(user.getParsedMobile(),club.getUnique());
					 System.out.println("ierealgirls user is not null ");
					 if(clubUser!=null && clubUser.getActive()==1){
						 System.out.println("ierealgirls clubUser is active "+clubUser.getParsedMobile());
						 MobileClubBillingPlan billingplan = billingplandao.getAnyStatusBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique(),-1);  
						 if(billingplan!=null && billingplan.getActiveForBilling()<0 && billingplan.getExternalId().equalsIgnoreCase("adhocpending")){
							 Calendar c1 = new GregorianCalendar();
							 Date bstart = c1.getTime();
							 c1.setTime(bstart);

							 c1.add(Calendar.MINUTE, 5);
							 Date bend = c1.getTime();
							 billingplan.setNextPush(new Date());

							 c1.add(Calendar.DATE, club.getPeriod());
							 bend = c1.getTime();
							 billingplan.setActiveForBilling(1);

							 //billingplan.setBillingEnd(bend);

							 billingplandao.update(billingplan);
							 campaigndao.log("ieadhoc", clubUser.getLandingpage(), clubUser.getParsedMobile(),clubUser.getParsedMobile(), handset,domain, clubUser.getCampaign(),clubUser.getClubUnique(), "SUBSCRIBED", 0, request,response,clubUser.getNetworkCode().toLowerCase(),"","","",pubId);
							 try {
                                                             if(userclubdetails.getServiceType().equalsIgnoreCase("competition")) {
								 engine.getTemplate("thank").evaluate(writer, context);
								 return;
                                                             }
                                                             else {
                                                                  application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp").forward(request, response);
								 return;
                                                             }
							 } catch (Exception e) {
								 e.printStackTrace();
							 }
						 }else{

							 try {
                                                             if(user.getParsedMobile().equalsIgnoreCase("353867804346")){
                                                                 System.out.println("quickwindebug "+user.getParsedMobile()+" forwarding to index main ");
                                                             }
								 application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/index_main.jsp").forward(request, response);
								 return;
							 } catch (Exception e) {
								 logger.error("WAPXIRE EXCEPTION {}", e.getMessage());
								 logger.error("WAPXIRE EXCEPTION ", e);
							 } 
						 } 
					 } //clubuser!=null
				 }  //user!=null 
			 } //END adhoc 
			 try{
				 engine.getTemplate(landingPage).evaluate(writer, context);
			 }
			 catch(Exception e){}

		 }


		 // ==== End Logigng to mobileClubCampaignLog hits ===========================

	 } //End of ProcessRequest

	 String getEndOfmonth(){

		 Calendar nowTime=GregorianCalendar.getInstance();
		 SimpleDateFormat formattr=new SimpleDateFormat("yyyy-MM-dd");
		 String currentDate=formattr.format(nowTime.getTime()).toString();

		 System.out.println("currentDate "+currentDate);
		 DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
		 DateTime dateTime = dtf.parseDateTime(currentDate);
		 DateTime lastDate = dateTime.dayOfMonth().withMaximumValue();

		 DateTimeFormatter dtfOut = DateTimeFormat.forPattern("dd/MM/yy");
		 String endofmonth=dtfOut.print(lastDate);
		 System.out.println(endofmonth);
		 return endofmonth;

	 }

	 private List<UmeDomain> getNotSubscribedClubDomains(MobileClub club, MobileClubDao mobileclubdao, UmeUser user, UmeTempCache umesdc) {
		 List<String> sisterClubList = new ArrayList<String>();
		 // String[] sisterClubArray=new String;
		 String sisterClubs = club.getSisterClubs();
		 System.out.println("Sister Clubs: " + sisterClubs);
		 if (sisterClubs.contains("?")) {
			 sisterClubList = new ArrayList<String>(Arrays.asList(sisterClubs.split("\\?")));
		 } else {
			 if (sisterClubs != null && !sisterClubs.equals("")) {
				 sisterClubList.add(sisterClubs);
			 }

		 }

		 boolean activeInSisterClub = false;
		 List<UmeDomain> notSubscribedClubDomains = new ArrayList<UmeDomain>();
		 //List<String> notSubscribedClubDomainsUrls = new ArrayList<String>();

		 if (sisterClubList.size() > 0 && sisterClubList != null) {
			 for (String sisterClubUnique : sisterClubList) {
				 MobileClub sisterClub =UmeTempCmsCache.mobileClubMap.get(sisterClubUnique);// mobileclubdao.getMobileClubMap().get(sisterClubUnique);
				 if (user != null) {
					 activeInSisterClub = mobileclubdao.isActive(user, sisterClub);
					 if (!activeInSisterClub) {
						 String clubDomainUnique = sisterClub.getWapDomain();
						 //UmeDomain sisterDomain=umesdc.getDomainMap().get(clubDomainUnique);
						 notSubscribedClubDomains.add(umesdc.getDomainMap().get(clubDomainUnique));
						 //notSubscribedClubDomainsUrls.add(umesdc.getDomainMap().get(clubDomainUnique).getDefaultUrl());
						 /* doRedirect(response,"http://" + sisterDomain.getDefaultUrl() +"/?id=" + user.getWapId()+"&mid="+enMsi+"&logtype=redirect");
                         return; */
					 }
				 } else {
					 String clubDomainUnique = sisterClub.getWapDomain();
					 notSubscribedClubDomains.add(umesdc.getDomainMap().get(clubDomainUnique));
				 }

			 }
		 }
		 return notSubscribedClubDomains;
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






} //End of class
