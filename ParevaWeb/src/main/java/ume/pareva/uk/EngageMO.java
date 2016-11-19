package ume.pareva.uk;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import static ume.pareva.core.UmeConfValue.umesdc;
import ume.pareva.dao.CpaVisitLogDao;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.MobileNetworksDao;
import ume.pareva.dao.PassiveVisitorDao;
import ume.pareva.dao.QueryHelper;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeQuizDao;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.dao.UmeSmsKeywordDao;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.engage.pojo.SMSParameter;
import ume.pareva.engageimpl.EngageInterface;
import ume.pareva.engageimpl.EngageInterfaceImpl;
import ume.pareva.pojo.EngageDRParameters;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.PassiveVisitor;
import ume.pareva.pojo.QuizUserAttempted;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.userservice.StopUser;
import ume.pareva.userservice.SubscriptionCreation;
import ume.pareva.util.NetworkMapping;
import ume.pareva.pojo.UmeClubMessages;


/**
 *
 * @author madan
 */

@Component("engagemo")
public class EngageMO {
    private static final Logger logger = LogManager.getLogger(EngageMO.class.getName());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    UmeQuizDao umequizdao;
    
    @Autowired
    NetworkMapping networkMapping;
    
    @Autowired
    MobileNetworksDao mobilenetwork;
    
    @Autowired
    UmeUserDao umeuserdao;
    
    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;
    
    @Autowired
    CpaVisitLogDao cpavisitlogdao;
    
    @Autowired
    PassiveVisitorDao passivevisitordao; 
    
    @Autowired
    MobileClubBillingPlanDao billingplandao;
    
    @Autowired
    MobileClubCampaignDao campaigndao;
    
    @Autowired
    QueryHelper queryhelper;
    
    @Autowired
    UmeSmsDao umesmsdao;
    
    @Autowired
    UmeSmsKeywordDao smskeywordao;
    
    @Autowired
    StopUser stopuser;
    
    @Autowired
    SubscriptionCreation subscriptioncreation;
    
    @Autowired
    UmeClubDetailsDao umeclubdetailsdao;
    
    
    
  
    

    public void processRequest(EngageDRParameters engageDRParameters) {
        
        String tpXML = engageDRParameters.getTpXML();
        tpXML = tpXML.substring(tpXML.indexOf("<WIN_TPBOUND_MESSAGES>"));
        Document doc = null;
        boolean sendConfirmation=true;
        boolean createbillPlan=true;
        MobileClubCampaign cmpg = null;
        UmeUser user=null;
        String defClubDomain ="5510024809921CDS";
        
        String billableCostId="2";
        //String serviceId="1";
        //String deliveryReceipt="13";
        String deliveryReceipt="11";
        String typeId="2";
        String freeCostId="1";
        String freeServiceId="1";
        
        String campaignUnique = "";
        boolean exists=false;
        boolean entryConfirmation=false;
        String status = "";
        String subscdate="";
        String hash = "";
        String binetwork="unknown";
        String landingpage="",pubId="";
        System.out.println("ukdrmo ENGAGEMO  tpxml is "+tpXML);
        
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(new InputSource(new StringReader(tpXML)));
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            System.out.println("ukdrmo ENGAGEMO Exception Error Parsing XML: processRequest method of EngageMO "+e);
            e.printStackTrace();
            logger.info("ERROR PARSING XML {}", e.getMessage());
            logger.info("ERROR PARSING XML ", e);
        }
        if(doc==null)return;
         NodeList nodes = doc.getElementsByTagName("SMSTOTP");
         System.out.println("ukdrmo EngageMO  Nodes size is  "+nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {            
            Node node = nodes.item(i);
            System.out.println("ukdrmo  ENGAGEMO Current NODE inside node loop : " + node.getNodeName()+" ITEM is "+i+" NODE getNodetype "+node.getNodeType()+" NODE ELEMENT "+Node.ELEMENT_NODE);
            sendConfirmation=true;
            createbillPlan=true;
            cmpg = null;
            user=null;
            
        
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                
                System.out.println("ukdrmo  ENGAGEMO Current ELEMENT NAME=" + element.getTextContent()+ " VALUE=" + element.getTextContent());
                
                String winTransactionId=doc.getElementsByTagName("WINTRANSACTIONID").item(0).getTextContent();
                boolean exist=umequizdao.quizReplyExistOrNot(winTransactionId);
                
                System.out.println("ukdrmo ENGAGEMO  winTransactionId is  "+winTransactionId+" and value of exist is "+exist+" size is "+i);
                
        if(!exist){ 
            //umequizdao.saveReply(tpXML);
            
            String msisdn=doc.getElementsByTagName("SOURCE_ADDR").item(0).getTextContent().substring(1); // substring(1) for ignoring +. 
            String toNumber=doc.getElementsByTagName("DESTINATION_ADDR").item(0).getTextContent();
            String motext=doc.getElementsByTagName("TEXT").item(0).getTextContent();
            String serviceId=doc.getElementsByTagName("SERVICEID").item(0).getTextContent();
            String networkid=networkMapping.getUkNetworkMap().get(Integer.parseInt(doc.getElementsByTagName("NETWORKID").item(0).getTextContent()));
            String keyClubUnique=serviceId;
            try{
            umequizdao.saveQuizReply(winTransactionId,msisdn,motext,toNumber,serviceId,networkid);
            }catch(Exception e){}
           System.out.println("ukdrmo ENGAGEMO msisdn "+msisdn+" toNumber "+toNumber+" text is "+motext+" networkid "+networkid);
            MobileClub club = null;
            UmeClubDetails userclubdetails=null; 
            
                 if(motext!=null && motext.toLowerCase().contains("stop")){
                     stopuser.stopAllSubscription(msisdn, null, null,"mo"); //Stop All Subscription
                     return;
                     
                 }
            
             if(!motext.toLowerCase().contains("stop")) {
                 if(motext.contains(".")) motext=motext.replace(".","");   
                 try{
                System.out.println("ukdrmo ENGAGEMO keyworddao UK "+motext+" clubunique is "+smskeywordao.getClubUnique(motext.toLowerCase().trim(),"uk"));
                 }catch(Exception e){}
                //keyClubUnique=smskeywordao.getClubUnique(motext.toLowerCase().trim(),"uk");
                keyClubUnique=smskeywordao.getClubUnique(motext.toLowerCase().trim(),"uk", toNumber);
            }
             
            
            
            //java.util.List<MobileClub> clubs = UmeTempCmsCache.mobileClubsByNumber.get(toNumber);
            //java.util.List<MobileClub> clubs = UmeTempCmsCache.mobileClubsByNumber.get(toNumber);
            club=UmeTempCmsCache.mobileClubMap.get(keyClubUnique);
            if(club!=null) userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
            //System.out.println("ukdrmo EngageMO "+club.getUnique()+"  "+club.getName());
            if(club==null) return;
                
            String shortCode=club.getSmsNumber();
            String msg=club.getWebConfirmation();
            int transactionId=Misc.generateUniqueIntegerId();
            
            /**
             * costid=2  is used for 87066
             * costid=3  is used for 80005
             */
            billableCostId=club.getOtpSoneraId(); // This field is used as Billable cost id
            if(billableCostId==null || billableCostId.trim().isEmpty()) billableCostId="2";
                           
            if(networkid==null) networkid="unknown";
                try{
                binetwork=mobilenetwork.getMobileNetwork("UK",networkid.toLowerCase());
                }catch(Exception e){binetwork="unknown";}
            
      
      
            SdcMobileClubUser clubUser = null;
            MobileClubBillingPlan billingplan=null;
            Calendar c1 = new GregorianCalendar();
            Date bstart = c1.getTime();
            c1.setTime(bstart);
            c1.add(Calendar.DATE, 7);
            Date bend = c1.getTime();
            
                       
                   boolean passiveexist=false;
              
                    PassiveVisitor visitor=passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
                    //passiveexist=passivevisitordao.exists(msisdn, club.getUnique());
                  
                        if(visitor!=null) {                                                       
                            landingpage=visitor.getLandignPage();
                            System.out.println("ukdrmo ENGAGEMO ukpassivedebug  LANDING PAGE  "+msisdn+"  club "+club.getUnique()+" msisdn : "+msisdn+" landingpage "+landingpage);
                            campaignUnique=visitor.getCampaign();
                            System.out.println("ukdrmo ENGAGEMO ukpassivedebug  Calling CampaignUnique "+msisdn+"  club "+club.getUnique()+" msisdn : "+msisdn+" campaign "+campaignUnique);
                            pubId=visitor.getPubId();
                        }
                   if(motext!=null && motext.toLowerCase().contains("stop")){
                    //handled already up. it is kept blank to maintain else condition below
                     
                     
                 }  //END HANDLING IF motext stop  STOP HANDLING     
                        
                else {  // IF MOTEXT is not STOP  start subscription
                       boolean logtocampaign=true;
               visitor = passivevisitordao.getPassiveVisitor(msisdn, club.getUnique());
                 if (visitor != null && visitor.getStatus() == 0) {
                 passivevisitordao.updatePassiveVisitorStatus(visitor, 1);
                }
                 //=======  if User !=null create Subscription ==================
                 String subscriptionparam="";
                 if(pubId.equalsIgnoreCase("restapi")) subscriptionparam="subscribe";
            String subsresponse = subscriptioncreation.checkSubscription(msisdn, club, campaignUnique, 7, subscriptionparam, networkid, "", landingpage,pubId);
            
            
            if (subsresponse.equals("OLD SUBSCRIPTION RECORD UPDATED SUCCESSFULLY")
                    || subsresponse.equals("SUBSCRIPTION RECORD CREATED SUCCESSFULLY")) {
                String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");
                if (userUnique != null && !userUnique.equals("")) {
                    user = umeuserdao.getUser(msisdn);
                }
                clubUser = user.getClubMap().get(club.getUnique());
                if (clubUser == null) {
                    clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                }
                if (user != null && clubUser != null) {
                    user.getClubMap().put(club.getUnique(), clubUser);
                    createbillPlan = true;
                    logtocampaign=true;
                    sendConfirmation=true;
                }
               
                logger.info("UKMO subscription  " + clubUser.toString() + " Create Billing Plan is " + createbillPlan);
            }
            
            if (subsresponse.equals("SUBSCRIPTION RECORD ALREADY EXISTS")) {
                createbillPlan = false;
                logtocampaign=false;
                sendConfirmation=false;
                String userUnique = umeuserdao.getUserUnique(msisdn, "msisdn", "");
                if (userUnique != null && !userUnique.equals("")) {
                    user = umeuserdao.getUser(msisdn);
                }
                clubUser = user.getClubMap().get(club.getUnique());
                if (clubUser == null) {
                    clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                }
             
                System.out.println("IEMO subscription  " + clubUser.toString() + " createbillPlan IS " + createbillPlan);
            }
              //Get the Active Billing Plan of the user.
            try{
             billingplan=billingplandao.getActiveBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());
            }catch(Exception e){billingplan=null;}
            
            
            //============ END NEW USER REGISTRATION =================
               if(billingplan==null || createbillPlan){
                        billingplan=new MobileClubBillingPlan();
                        billingplan.setTariffClass(club.getPrice());
                        billingplan.setActiveForAdvancement(1);
                        billingplan.setActiveForBilling(1);
                        billingplan.setAdhocsRemaining(0.0);
                        billingplan.setBillingEnd(bend);
                        billingplan.setClubUnique(club.getUnique());
                        billingplan.setContractType("");
                        billingplan.setLastPaid(clubUser.getSubscribed());
                        billingplan.setLastSuccess(new Date(0));
                        billingplan.setLastPush(clubUser.getSubscribed());
                        billingplan.setNetworkCode(networkid.toLowerCase());
                        billingplan.setNextPush(bend);
                        billingplan.setParsedMobile(user.getParsedMobile());
                        billingplan.setPartialsPaid(0.0);
                        billingplan.setSubscribed(clubUser.getSubscribed());
                        billingplan.setPartialsRequired(Double.parseDouble(String.valueOf(userclubdetails.getFrequency()+"")));
                        billingplan.setPushCount(0.0);
                        billingplan.setServiceDate(bstart);
                        billingplan.setServiceDateBillsRemaining(Double.parseDouble(String.valueOf(userclubdetails.getFrequency())));
                        billingplan.setSubUnique(clubUser.getUserUnique());
                       	billingplan.setExternalId(""); //This is for Italy SubscriptionId so just setting the values. 
                        //billingplan.setServiceDateBillsRemaining(Double.parseDouble(String.valueOf(userclubdetails.getFrequency())));
                        billingplan.setServiceDateBillsRemaining(1.0); // We need to use Frequency here. 
                        billingplandao.insertBillingPlan(billingplan);                       
                        if(logtocampaign)
                    campaigndao.log("ukmo", landingpage, msisdn,msisdn, null,null, clubUser.getCampaign(), clubUser.getClubUnique(), "SUBSCRIBED", 0, null,null,binetwork.toLowerCase(),"","","",pubId);
                                   
                        //Subscription CPA
                        if(null!=campaignUnique && !"".equalsIgnoreCase(campaignUnique)){
                            cmpg = UmeTempCmsCache.campaignMap.get(campaignUnique);                            
                            if(cmpg!=null){
                            if(cmpg.getSrc().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("subscription")){
                                    String cpaLogstatus="0";
                                    SimpleDateFormat currentsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Calendar nowtimess=new GregorianCalendar();
                            Calendar currentTime=Calendar.getInstance();
	            	    currentTime.add(Calendar.MINUTE, 10);
	            	    String nextpush=currentsdf.format(currentTime.getTime());
                            String  cpaloggingquery="insert into cpaLogging (aParsedMobile,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc) values"
	            			 			+"('"+user.getParsedMobile()+"','"+campaignUnique+"','"+club.getUnique()+"','"+currentsdf.format(nowtimess.getTime())+"','"+nextpush+"','"+cpaLogstatus+"','"+networkid+"','"+cmpg.getSrc()+"')";
                            int insertedRows=queryhelper.executeUpdateQuery(cpaloggingquery," EngageMO cpalogging query = "+cpaloggingquery);
                                } // end if cmpg.getSrc                                    
                            } // end if cmpg!=null                            
                        } //END  null!=campaignUnique
                      } //END billingplan==null 
               
               //=========== SENDING Entry Confirmation MESSAGE ========================
                    String[] toAddress={msisdn};
                        if(sendConfirmation){
                          
                         
                         if(userclubdetails.getBillingType().equalsIgnoreCase("subscription") ){
                             sendWelcomeMessages(club, clubUser,userclubdetails);
                          if(userclubdetails.getServiceType().equalsIgnoreCase("competition")){
                             java.util.List<UmeClubMessages> billableMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Billable"); 
                              
                             // msg=club.getWebConfirmation();
                             msg=billableMessages.get(0).getaMessage();
                             if(msg!=null) {
                                System.out.println("uknewlogic  Billable message: "+msg); 
                             }
                             if(msg==null || "".contains(msg)) msg=club.getWebConfirmation();
                          }
                          
                          else if (userclubdetails.getServiceType().equalsIgnoreCase("content")){
                              msg="Your Personal link http://"+club.getWapDomain()+"/?id="+user.getWapId();
                          }
                         }
                         
                             if(msg.contains("thisisthencryptedversionofmsisdn")){
                                msg=msg.replace("thisisthencryptedversionofmsisdn",MiscCr.encrypt(msisdn));
                                System.out.println("ukbilablemessage msisdn "+msisdn+" msg is "+msg);
                            }
                            
                             
                        String texttosend = "<![CDATA[" + msg + "]]>";
                        SMSParameter billableSMSParameter=new SMSParameter();
                        billableSMSParameter.setCostId(billableCostId);
                        billableSMSParameter.setServiceId(serviceId);
                        billableSMSParameter.setDeliveryReceipt(deliveryReceipt);
                        billableSMSParameter.setMsgText(texttosend);
                        billableSMSParameter.setShortCode(shortCode);
                        billableSMSParameter.setTransactionId(String.valueOf(transactionId)+"-"+club.getUnique());
                        billableSMSParameter.setTypeId(typeId);
                        billableSMSParameter.setToAddress(toAddress);
                    	billableSMSParameter.setWinTransactionId(String.valueOf(Misc.generateUniqueIntegerId()));
                    	
                    	EngageInterface engageInterface=new EngageInterfaceImpl();
                    	int responseCode=engageInterface.sendSMS(billableSMSParameter);
                    	System.out.println("ukdrmo EngageMO billablesmsparameter transid "+billableSMSParameter.getTransactionId()+" msgText "+billableSMSParameter.getMsgText()+" and Response is "+responseCode);
                    	if(responseCode==200){
                    		
                                if(userclubdetails.getServiceType().equalsIgnoreCase("competition")){
                    		QuizUserAttempted quizUserAttempted=new QuizUserAttempted();
                    		quizUserAttempted.setaParsedMsisdn(msisdn);
                    		quizUserAttempted.setClubUnique(club.getUnique());
                    		quizUserAttempted.setType("Entry Confirmation");
                    		quizUserAttempted.setStatus("false");
                    		quizUserAttempted.setaUnique(billableSMSParameter.getTransactionId());
                    		quizUserAttempted.setaCreated(new java.sql.Timestamp(System.currentTimeMillis()));
                    		umequizdao.saveQuizUserAttempted(quizUserAttempted);
                                }
                    		
                    		SdcSmsSubmit sdcSmsSubmit=new SdcSmsSubmit();
                    		
                    		sdcSmsSubmit.setUnique(billableSMSParameter.getTransactionId());
                    		sdcSmsSubmit.setLogUnique(billableSMSParameter.getTransactionId());
                    		sdcSmsSubmit.setFromNumber(shortCode);
                    		sdcSmsSubmit.setToNumber(msisdn);
                    		sdcSmsSubmit.setMsgType("Premium");
                    		sdcSmsSubmit.setMsgBody(msg);
                                sdcSmsSubmit.setCost(club.getPrice());
                                sdcSmsSubmit.setStatus("SENT");
                                sdcSmsSubmit.setRefMessageUnique("BILLING");
                                sdcSmsSubmit.setMsgCode1("engage_mo.jsp");
                                sdcSmsSubmit.setNetworkCode(networkid.toLowerCase());
                                sdcSmsSubmit.setClubUnique(club.getUnique());
                    		umesmsdao.log(sdcSmsSubmit);    
                         }	
                    		
                        }
               //============== END SENDING Entry Confirmation MESSAGE =================
              
            }  //END ELSE MOTEXT not STOP  STATEMENT 
            } // END if NOT EXIST 
            } //END IF NODE 
            System.out.println("ukdrmo  ENGAGEMO  END of FOR LOOP FOR  size "+i+" NODE getNodetype "+node.getNodeType()+" NODE ELEMENT "+Node.ELEMENT_NODE);
            } //END for loop 
        } //END PRocessREquest
    
     private void sendWelcomeMessages(MobileClub club, SdcMobileClubUser clubUser,UmeClubDetails details) {

        if (club==null && (club!=null && !club.getRegion().equalsIgnoreCase("UK"))) {
            logger.warn("ONLY SENDING TO QUIZ2WIN AND VOUCHER");
            System.out.println("WELCOMEUK - ONLY SENDING TO QUIZ2WIN AND VOUCHER");
            return;
        }
        
        java.util.List<UmeClubMessages> welcomeMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Welcome");
        int counter=1;
        for(int i=0;i<welcomeMessages.size();i++){
            sendWelcomeMessage(clubUser.getParsedMobile(), club, details,counter+"",welcomeMessages.get(i).getaMessage());
            if(counter==1) clubUser.setParam1("1");
            if(counter==2)clubUser.setParam2("1");
            counter++;
            
        }
        /*
        //====================== WElcome Message Sending ============================================== 
        if (sendMessage(clubUser.getParsedMobile(), club, details, "1")) {
            logger.info("FIRST TEXT SUCCESSFULLY SENT TO " + clubUser.getParsedMobile());
            System.out.println("WELCOMEUK - FIRST TEXT SUCCESSFULLY SENT TO " + clubUser.getParsedMobile());
            clubUser.setParam1("1");

            try {
                Thread.sleep(50);
            } catch (Exception e) {
                // SLEEP TO ADD 
            }

            if (sendMessage(clubUser.getParsedMobile(), club, details, "2")) {
                logger.info("SECOND TEXT SUCCESSFULLY SENT TO " + clubUser.getParsedMobile());
                System.out.println("WELCOMEUK - 2ND TEXT SUCCESSFULLY SENT TO " + clubUser.getParsedMobile());
                clubUser.setParam2("1");
            } else {
                logger.info("2ND WELCOME MESSAGE FAILED FOR {}, SET STATUS TO 1,5", clubUser.getParsedMobile());
                System.out.println("WELCOMEUK - 2ND WELCOME MESSAGE FAILED FOR "+clubUser.getParsedMobile()+", SET STATUS TO 1,5");
                clubUser.setParam2("5");
            }
        } else {
            logger.info("1ST WELCOME MESSAGE FAILED FOR {}, SET STATUS TO 5,0", clubUser.getParsedMobile());
            System.out.println("WELCOMEUK - 1ST WELCOME MESSAGE FAILED FOR "+clubUser.getParsedMobile()+", SET STATUS TO 5,0");
            clubUser.setParam1("5");
        }
        
        //========================== END Send Welcome Message ==============================================
        */
        
    }

    
    
    
    
    private boolean sendMessage(String subscriber, MobileClub club, UmeClubDetails details, String addendum) {

        String msgBody;
        if (addendum.equals("1")) {
            msgBody = club.getWelcomeSms();
        } else if (addendum.equals("2")) {
            msgBody = details.getClubWelcomeSms2();
        } else {
            msgBody = "";
        }
        if (msgBody.trim().isEmpty()) {
            logger.warn(subscriber + " MESSAGE IS EMPTY!");
            return false;
        }

        String[] toAddress = {subscriber};
        SMSParameter smsParameter = new SMSParameter();
        smsParameter.setCostId("1");
        smsParameter.setServiceId("2");
        smsParameter.setDeliveryReceipt("13");
        //smsParameter.setMsgText(msgBody);

        if (msgBody.contains("####")) {
            msgBody = msgBody.replace("####", "Q2W" + SdcMisc.generateLogin(5));
            logger.info("REPLACING #### " + msgBody);
        }  else if (msgBody.contains("****")) {
            //http:// defaultdomain/?id=user.getWapId();
            String domainUnique = club.getWapDomain();
            UmeDomain domain = umesdc.getDomainMap().get(domainUnique);
            UmeUser user = umeuserdao.getUser(subscriber);
            String personalLink = "http://" + domain.getDefaultUrl();
            if (user != null) {
                personalLink = personalLink + "/?id=" + user.getWapId();
            } else {
                logger.warn("NO USER FOUND FOR {}", subscriber);
            }
            msgBody = msgBody.replace("*****", personalLink);
        }
        
        
        

        String texttosend = "<![CDATA[" + msgBody + "]]>";
        logger.info("SENDING MESSAGE {}", texttosend);
        smsParameter.setMsgText(texttosend);

        String shortCode="Quiz2Win";

        //SPOOFING
      try{
        shortCode=club.getSmsExt();
        }catch(Exception e){shortCode="Quiz2Win";}
        smsParameter.setShortCode(shortCode);
        String transactionID = "UKW" + MiscCr.generateUniqueId()+"-"+club.getUnique();
        smsParameter.setTransactionId(transactionID);
        smsParameter.setTypeId("2");
        smsParameter.setToAddress(toAddress);

        logger.info("SENDING COSTID=" + smsParameter.getCostId() + " SERVICEID=" + smsParameter.getServiceId() + " DELIVERYRECEIPT=" + smsParameter.getDeliveryReceipt()
                + " SHORTCODE=" + smsParameter.getShortCode() + " TRANSACTIONID=" + smsParameter.getTransactionId() + " TYPEID=" + smsParameter.getTypeId()
                + " TOADDRESS=" + Arrays.toString(smsParameter.getToAddress()) + " MSGTEXT=" + smsParameter.getMsgText());

        double before = System.currentTimeMillis();
        EngageInterfaceImpl eii = new EngageInterfaceImpl();
        int resp = eii.sendSMS(smsParameter);
        double totalResponseTime = (double) ((System.currentTimeMillis() - before) / 1000);
        logger.info("TOTAL STS RESPONSE TIME FOR " + subscriber + ": " + totalResponseTime + " RESPONSE = " + resp);

        logger.info(addendum + " Welcome message: <?xml version=1.0 standalone=no?><!DOCTYPE WIN_DELIVERY_2_SMS "
                + "SYSTEM winbound_messages_v1.dtd><WIN_DELIVERY_2_SMS><SMSMESSAGE>" + subscriber
                + "<TEXT>" + texttosend
                + "</TEXT><TRANSACTIONID>" + transactionID + "</TRANSACTIONID><TYPEID>2</TYPEID><SERVICEID>2</SERVICEID>"
                + "<COSTID>1</COSTID><SOURCE_ADDR>" + shortCode + "</SOURCE_ADDR>"
                + "<DELIVERYRECEIPT>13</DELIVERYRECEIPT>"
                + "</SMSMESSAGE></WIN_DELIVERY_2_SMS>");
        
        System.out.println(addendum + " Welcome message: <?xml version=1.0 standalone=no?><!DOCTYPE WIN_DELIVERY_2_SMS "
                + "SYSTEM winbound_messages_v1.dtd><WIN_DELIVERY_2_SMS><SMSMESSAGE>" + subscriber
                + "<TEXT>" + texttosend
                + "</TEXT><TRANSACTIONID>" + transactionID + "</TRANSACTIONID><TYPEID>2</TYPEID><SERVICEID>2</SERVICEID>"
                + "<COSTID>1</COSTID><SOURCE_ADDR>" + shortCode + "</SOURCE_ADDR>"
                + "<DELIVERYRECEIPT>13</DELIVERYRECEIPT>"
                + "</SMSMESSAGE></WIN_DELIVERY_2_SMS>");
        SdcSmsSubmit sdcSmsSubmit = new SdcSmsSubmit();

        sdcSmsSubmit.setUnique(transactionID);
        sdcSmsSubmit.setLogUnique(transactionID);//
        sdcSmsSubmit.setFromNumber(shortCode);
        sdcSmsSubmit.setToNumber(subscriber);
        sdcSmsSubmit.setMsgType("FREE");
        sdcSmsSubmit.setMsgBody(msgBody);
        sdcSmsSubmit.setCost(club.getPrice());
        sdcSmsSubmit.setClubUnique(club.getUnique());
        sdcSmsSubmit.setStatus("SENT");
        sdcSmsSubmit.setRefMessageUnique("WELCOME");
        sdcSmsSubmit.setMsgCode1("welcomeUK" + addendum.trim());
        String response;
        if (200 == resp) {
            response = "SUCCESS";
        } else {
            response = "FAILURE";
        }
        sdcSmsSubmit.setResponse(response);
        umesmsdao.log(sdcSmsSubmit);
        return 200 == resp;
    }
    
       
      private boolean sendWelcomeMessage(String subscriber, MobileClub club, UmeClubDetails details, String addendum,String message) {

        String msgBody;
        
            msgBody = message;
      
        if (msgBody.trim().isEmpty()) {
            logger.warn(subscriber + " MESSAGE IS EMPTY!");
            return false;
        }
        
        

        String[] toAddress = {subscriber};
        SMSParameter smsParameter = new SMSParameter();
        smsParameter.setCostId("1");
        smsParameter.setServiceId("2");
        smsParameter.setDeliveryReceipt("13");
        //smsParameter.setMsgText(msgBody);

        if (msgBody.contains("####")) {
            msgBody = msgBody.replace("####", "Q2W" + SdcMisc.generateLogin(5));
            logger.info("REPLACING #### " + msgBody);
        }  else if (msgBody.contains("****")) {
            //http:// defaultdomain/?id=user.getWapId();
            String domainUnique = club.getWapDomain();
            UmeDomain domain = umesdc.getDomainMap().get(domainUnique);
            UmeUser user = umeuserdao.getUser(subscriber);
            String personalLink = "http://" + domain.getDefaultUrl();
            if (user != null) {
                personalLink = personalLink + "/?id=" + user.getWapId();
            } else {
                logger.warn("NO USER FOUND FOR {}", subscriber);
            }
            msgBody = msgBody.replace("*****", personalLink);
        }
       
        System.out.println("uknewlogic Welcome Message "+msgBody);

        String texttosend = "<![CDATA[" + msgBody + "]]>";
        logger.info("SENDING MESSAGE {}", texttosend);
        smsParameter.setMsgText(texttosend);

        String shortCode="Quiz2Win";

        //SPOOFING
      try{
        shortCode=club.getSmsExt();
        }catch(Exception e){shortCode="Quiz2Win";}
        smsParameter.setShortCode(shortCode);
        String transactionID = "UKW" + MiscCr.generateUniqueId()+"-"+club.getUnique();
        smsParameter.setTransactionId(transactionID);
        smsParameter.setTypeId("2");
        smsParameter.setToAddress(toAddress);

        logger.info("SENDING COSTID=" + smsParameter.getCostId() + " SERVICEID=" + smsParameter.getServiceId() + " DELIVERYRECEIPT=" + smsParameter.getDeliveryReceipt()
                + " SHORTCODE=" + smsParameter.getShortCode() + " TRANSACTIONID=" + smsParameter.getTransactionId() + " TYPEID=" + smsParameter.getTypeId()
                + " TOADDRESS=" + Arrays.toString(smsParameter.getToAddress()) + " MSGTEXT=" + smsParameter.getMsgText());

        double before = System.currentTimeMillis();
        EngageInterfaceImpl eii = new EngageInterfaceImpl();
        int resp = eii.sendSMS(smsParameter);
        double totalResponseTime = (double) ((System.currentTimeMillis() - before) / 1000);
        logger.info("TOTAL STS RESPONSE TIME FOR " + subscriber + ": " + totalResponseTime + " RESPONSE = " + resp);

        logger.info(addendum + " Welcome message: <?xml version=1.0 standalone=no?><!DOCTYPE WIN_DELIVERY_2_SMS "
                + "SYSTEM winbound_messages_v1.dtd><WIN_DELIVERY_2_SMS><SMSMESSAGE>" + subscriber
                + "<TEXT>" + texttosend
                + "</TEXT><TRANSACTIONID>" + transactionID + "</TRANSACTIONID><TYPEID>2</TYPEID><SERVICEID>2</SERVICEID>"
                + "<COSTID>1</COSTID><SOURCE_ADDR>" + shortCode + "</SOURCE_ADDR>"
                + "<DELIVERYRECEIPT>13</DELIVERYRECEIPT>"
                + "</SMSMESSAGE></WIN_DELIVERY_2_SMS>");
        
        System.out.println(addendum + " Welcome message: <?xml version=1.0 standalone=no?><!DOCTYPE WIN_DELIVERY_2_SMS "
                + "SYSTEM winbound_messages_v1.dtd><WIN_DELIVERY_2_SMS><SMSMESSAGE>" + subscriber
                + "<TEXT>" + texttosend
                + "</TEXT><TRANSACTIONID>" + transactionID + "</TRANSACTIONID><TYPEID>2</TYPEID><SERVICEID>2</SERVICEID>"
                + "<COSTID>1</COSTID><SOURCE_ADDR>" + shortCode + "</SOURCE_ADDR>"
                + "<DELIVERYRECEIPT>13</DELIVERYRECEIPT>"
                + "</SMSMESSAGE></WIN_DELIVERY_2_SMS>");
        SdcSmsSubmit sdcSmsSubmit = new SdcSmsSubmit();

        sdcSmsSubmit.setUnique(transactionID);
        sdcSmsSubmit.setLogUnique(transactionID);//
        sdcSmsSubmit.setFromNumber(shortCode);
        sdcSmsSubmit.setToNumber(subscriber);
        sdcSmsSubmit.setMsgType("FREE");
        sdcSmsSubmit.setMsgBody(msgBody);
        sdcSmsSubmit.setCost(club.getPrice());
        sdcSmsSubmit.setClubUnique(club.getUnique());
        sdcSmsSubmit.setStatus("SENT");
        sdcSmsSubmit.setRefMessageUnique("WELCOME");
        sdcSmsSubmit.setMsgCode1("welcomeUK" + addendum.trim());
        String response;
        if (200 == resp) {
            response = "SUCCESS";
        } else {
            response = "FAILURE";
        }
        sdcSmsSubmit.setResponse(response);
        umesmsdao.log(sdcSmsSubmit);
        return 200 == resp;
    }
    
    
    
    
    
    
} //END class 
