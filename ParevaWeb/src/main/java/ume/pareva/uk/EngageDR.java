package ume.pareva.uk;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar; ///

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.imimobile.defaults.GetTransactionStatusRequest;
import net.imimobile.defaults.GetTransactionStatusResponse;
import net.imimobile.serviceimpl.IMIBillingDoi;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Weeks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ume.pareva.dao.QueryHelper;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubBillingTry;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.dao.MobileBillingDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaVisitLogDao;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.MobileNetworksDao;
import ume.pareva.dao.QuizSmsDao;
import ume.pareva.dao.SdcMiscDate;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeQuizDao;
import ume.pareva.dao.UmeSmsDao;
import ume.pareva.pojo.CpaVisitLog;
import ume.pareva.pojo.EngageDRParameters;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.MobileClubBillingSuccesses;
import ume.pareva.pojo.QuizUserAttempted;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.TransactionStatusRequest;
import ume.pareva.pojo.TransactionStatusResponse;
import ume.pareva.sdk.Misc;
import ume.pareva.userservice.SmsResponses;
import ume.pareva.util.NetworkMapping;

@Component("engageDR")
public class EngageDR {

    @Autowired
    private UmeQuizDao umequizdao;

    @Autowired
    private UmeSmsDao umesmsdao;

    @Autowired
    private NetworkMapping networkMapping;

    @Autowired
    private MobileClubBillingPlanDao billingplandao;

    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;
    
    @Autowired
    MobileBillingDao mobilebillingdao;

    @Autowired
    CpaVisitLogDao cpavisitlogdao;

    @Autowired
    QueryHelper queryhelper;

    @Autowired
    MobileNetworksDao mobilenetworks;

    @Autowired
    MobileClubCampaignDao campaigndao;
    
    @Autowired
    QuizSmsDao quizsmsdao;
    
    @Autowired
    SmsResponses smsresponse;

    private static final Logger logger = LogManager.getLogger(EngageDR.class.getName());
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void processRequest(EngageDRParameters engageDRParameters) {
        String tpXML = engageDRParameters.getTpXML();
        Calendar deliverytime = new GregorianCalendar();
        tpXML = tpXML.substring(tpXML.indexOf("<WIN_RECEIPTS>"));
        System.out.println("ukdrmo  ENGAGE DR tpxml is  : " + tpXML);
        
        
        Document doc = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(new InputSource(new StringReader(tpXML)));
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            System.out.println("ukdrmo Exception Error Parsing XML: processRequest method of EngageDR " + e);
            e.printStackTrace();
            logger.info("ukdrmo ERROR PARSING XML {}", e.getMessage());
            logger.info("ukdrmo ERROR PARSING XML ", e);
        }
        if (doc == null) {
            return;
        }

        NodeList nodes = doc.getElementsByTagName("SMSRECEIPT");
        System.out.println("ukdrmo  ENGAGE DR Nodes size " + nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            logger.info("ukdrmo  ENGAGE DR Current NODE inside node loop : " + node.getNodeName() + " ITEM=" + i + "/" +nodes.getLength() +  " NODE getNodetype " + node.getNodeType() + " NODE ELEMENT " + Node.ELEMENT_NODE);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                logger.info("ukdrmo  ENGAGE DR Current ELEMENT NAME=" + element.getTextContent()+ " VALUE=" + element.getTextContent());

                String transactionId = element.getElementsByTagName("TRANSACTIONID").item(0).getTextContent().trim();
                String msisdn = element.getElementsByTagName("SOURCE_ADDR").item(0).getTextContent().substring(1).trim(); // substring(1) for ignoring +. 
                String statusId = element.getElementsByTagName("STATUSID").item(0).getTextContent().trim();
                String networkId = element.getElementsByTagName("NETWORKID").item(0).getTextContent().trim();
                String serviceId=doc.getElementsByTagName("SERVICEID").item(0).getTextContent();
                //String shortCode=doc.getElementsByTagName("DESTINATION_ADDR").item(0).getTextContent();
                String keyclubunique="";
                
                String smsdeliveryresponse=smsresponse.getSmsStatus("UK",statusId);

                System.out.println("ukdrmo  ENGAGE DR DETAILS transactionid : " + transactionId + " msisdn " + msisdn + " statusid " + statusId + " network " + networkId + " size is " + i);
                    MobileClub club = null;
                    
                          if(transactionId.contains("-")) {
                        keyclubunique=getClubId(transactionId,"-").trim();
                        System.out.println("ukdrmo ENGAGEMO INSIDE - condition clubUnique IS "+keyclubunique);
                        club=UmeTempCmsCache.mobileClubMap.get(keyclubunique);
                    }
                      if(club==null) club=UmeTempCmsCache.mobileClubMap.get(serviceId);
                      
                    //System.out.println("EngageDR clubdetails "+club.getUnique()+"  "+club.getName());
            
                boolean exist = umequizdao.quizReceiptExistOrNot(transactionId);
                System.out.println("QuizReceiptExistOrNot query EnageDR  the value of exist is "+exist+" for transactionId "+transactionId);
                //if (transactionId != null && !transactionId.isEmpty() && !exist) {
                if (exist==false) {
                   System.out.println("EngageDR quizdaocheck  INSIDE EXIST CONDITION the value of exist is "+exist+" for transactionId "+transactionId+ " "+new Date());
                    
                    umequizdao.saveReceipt(tpXML, exist);//saveReceipt(tpXML,transactionId);
                    
                    quizsmsdao.updateResponse(transactionId,smsdeliveryresponse.toUpperCase(), sdf.format(deliverytime.getTime()),statusId, networkId);
                    umesmsdao.updateResponse(transactionId, smsdeliveryresponse.toUpperCase(), sdf.format(deliverytime.getTime()),statusId, networkId);
                    
                    SdcSmsSubmit sdcSmsSubmit = umesmsdao.getSmsMsgLog(transactionId);
                    
                    if (sdcSmsSubmit != null && sdcSmsSubmit.getMsgType().equalsIgnoreCase("Premium")) {
                        logger.info("SUBMIT SUCCESSFUL");
                        MobileClubBillingPlan mobileClubBillingPlan =null;
                        SdcMobileClubUser clubUser =null;
                        mobileClubBillingPlan = billingplandao.getAnyStatusBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());
                        clubUser = umemobileclubuserdao.getClubUserByMsisdn(msisdn, club.getUnique());
                        
                        if(clubUser==null || mobileClubBillingPlan==null){
                            logger.error("MSISDN {} {} DOESN't HAVE EITHER SUBSCRIPTION OR PLANS... INVESTIGATE",msisdn,club.getUnique());
                            return;
                        }
                            
                        TransactionStatusRequest transactionStatusRequest = new TransactionStatusRequest(transactionId, transactionId + "_" + msisdn, msisdn, new java.sql.Timestamp(System.currentTimeMillis()));
                        String binetwork = "unknown"; //mobilenetworks.
                        if (statusId.equals("5")) { //successful billing response

                            String successResponse = "00";
                            boolean cpaLog = false;
                            QuizUserAttempted quizAttempted = new QuizUserAttempted(transactionId);
                            quizAttempted.setStatus("true");
                            umequizdao.updateQuizUserAttempted(quizAttempted);

                            System.out.println("ukdrmo  ENGAGE DR statusID=5 condition : " + node.getNodeName() + " transactionId " + transactionId);
                            
                            if (mobileClubBillingPlan != null) {
                                Calendar c = Calendar.getInstance();
                                

                                mobileClubBillingPlan.setLastSuccess(c.getTime());                                
                                //mobileClubBillingPlan.setPushCount(mobileClubBillingPlan.getPushCount() + 1);
                                
                                //mobileClubBillingPlan.setLastPaid(c.getTime());
                                Date nextPush = DateUtils.addDays(mobileClubBillingPlan.getLastPush(), 7);
                                nextPush = DateUtils.setHours(nextPush, 9);
                                nextPush = DateUtils.truncate(nextPush, Calendar.HOUR_OF_DAY);
                                mobileClubBillingPlan.setNextPush(nextPush);
                                
                                  if (mobileClubBillingPlan.getServiceDateBillsRemaining() > 0) {
                                mobileClubBillingPlan.setServiceDateBillsRemaining(mobileClubBillingPlan.getServiceDateBillsRemaining() - 1);
                            }
                                
                                /**
                                 * We get subscription date from billingplans. 
                                 */
                                
                                /*
                                Trying weeks of subscription and their success
                                */
                                try{
                                Date subscribedDate=clubUser.getSubscribed();
                                Date today=new Date();
                                DateTime dateTime1 = new DateTime(subscribedDate);
                                DateTime dateTime2 = new DateTime(today);
                                int weeks = Weeks.weeksBetween(dateTime1, dateTime2).getWeeks();
                                
                                int totalSuccess=billingplandao.getTotalSuccessAfterEachSubscription(clubUser.getParsedMobile(), clubUser.getClubUnique(),clubUser.getSubscribed());
                                int pendingTickets=0;
                                int successfrequency=weeks*1; //We need to use Frequency here... 
                                if(totalSuccess==successfrequency) // User is upto-date 
                                {                                    
                                    mobileClubBillingPlan.setServiceDateBillsRemaining(0.0);
                                    mobileClubBillingPlan.setLastPaid(new Date());
                                }
                                else if (totalSuccess<successfrequency) { //UnderBill Users
                                    
                                    pendingTickets=successfrequency-totalSuccess;
                                    if(pendingTickets>=2){
                                        mobileClubBillingPlan.setServiceDateBillsRemaining(1.0);
                                    }
                                                                       
                                    nextPush = DateUtils.addDays(mobileClubBillingPlan.getLastPush(), 1);
                                    nextPush = DateUtils.setHours(nextPush, 9);
                                    nextPush = DateUtils.truncate(nextPush, Calendar.HOUR_OF_DAY);
                                    mobileClubBillingPlan.setNextPush(nextPush);
                                        
                                }
                                
                                   else if(totalSuccess>successfrequency) { //overbill users Or can be ok due to week calculation. 
                                    mobileClubBillingPlan.setLastPaid(new Date());
                                    int weekdifference=totalSuccess-successfrequency;
                                    nextPush=DateUtils.addWeeks(mobileClubBillingPlan.getLastPush(),weekdifference); //Adding not to overbill continuously
                                    mobileClubBillingPlan.setNextPush(nextPush);
                                }
                                
                                
                                
                                
                                System.out.println("weeks EngageDR-- "+clubUser.getParsedMobile()+"-- "+clubUser.getClubUnique()+"-- "+clubUser.getSubscribed()+"-- no. of weeks "+weeks+" noofsuccess "+totalSuccess);
                                
                                }catch(Exception e){}
                                
                                //==== END calculating the Subscription and Success ====== 
                         

                                umemobileclubuserdao.updateBillingRenew(clubUser.getUserUnique(), club.getUnique());

                                if (DateUtils.isSameDay(new Date(), clubUser.getSubscribed())) {
                                    successResponse = "003";
                                    cpaLog = true;
                                }

                                try {
                                    binetwork = mobilenetworks.getMobileNetwork("UK", clubUser.getNetworkCode());
                                } catch (Exception e) {
                                    binetwork = "unknown"; //
                                }

                                MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                                mobileClubBillingTry.setUnique(transactionId);
                                mobileClubBillingTry.setLogUnique(transactionId);
                                mobileClubBillingTry.setAggregator("IMI");
                                mobileClubBillingTry.setClubUnique(club.getUnique());
                                mobileClubBillingTry.setCreated(new Date());
                                mobileClubBillingTry.setNetworkCode(binetwork.toLowerCase());
                                mobileClubBillingTry.setParsedMsisdn(msisdn);
                                mobileClubBillingTry.setRegionCode("UK");
                                mobileClubBillingTry.setResponseCode(successResponse);
                                mobileClubBillingTry.setResponseDesc("successful");
                                mobileClubBillingTry.setResponseRef(transactionId);
                                mobileClubBillingTry.setStatus("success");
                                mobileClubBillingTry.setTariffClass(4.5);
                                mobileClubBillingTry.setTransactionId(transactionId);
                                mobileClubBillingTry.setCampaign(clubUser.getCampaign());

                                billingplandao.update(mobileClubBillingPlan);
                                MobileClubBillingSuccesses mobileClubBillingSuccesses = new MobileClubBillingSuccesses(mobileClubBillingPlan, mobileClubBillingTry);

                                try {
                                   // billingplandao.save(mobileClubBillingSuccesses);
                                    mobilebillingdao.insertBillingSuccess(mobileClubBillingSuccesses);
                                    mobilebillingdao.insertBillingTry(mobileClubBillingTry);
                                } catch (Exception e) {
                                }
                            }

                            String campaignId=clubUser.getCampaign();
                            if (null != campaignId && !"".equals(campaignId)) {
                                    MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
                                    if (cmpg != null) {
                                        if (cmpg.getSrc().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase("billing")) {
                                            String networkid = networkMapping.getUkNetworkMap().get(networkId);
                                            String cpaLogstatus = "0";
                                            SimpleDateFormat currentsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            Calendar nowtimess = new GregorianCalendar();
                                            Calendar currentTime = Calendar.getInstance();
                                            currentTime.add(Calendar.MINUTE, 10);
                                            String nextpush = currentsdf.format(currentTime.getTime());
                                            String cpaloggingquery = "insert into cpaLogging (aParsedMobile,aCampaign,aClubUnique,aCreated,aNextPush,status,aNetworkCode,aSrc) values"
                                                    + "('" + msisdn + "','" + campaignId + "','" + club.getUnique() + "','" + currentsdf.format(nowtimess.getTime()) + "','" + nextpush + "','" + cpaLogstatus + "','" + binetwork + "','" + cmpg.getSrc() + "')";

                                            System.out.println("ukdrmo  ENGAGE DR CPA Logging " + cpaloggingquery);

                                            int insertedRows = -1;
                                            if (cpaLog) {
                                                insertedRows = queryhelper.executeUpdateQuery(cpaloggingquery, "EngageDR cpalogging query = "+cpaloggingquery);
                                            }

                                        }
                                    }
                                }

                            

                        } else if (statusId.equals("11") || statusId.equals("13")) {
                            
                            try {
                                binetwork = mobilenetworks.getMobileNetwork("UK", clubUser.getNetworkCode());
                            } catch (Exception e) {
                                binetwork = "unknown";
                            }

                            if (clubUser != null && clubUser.getActive() == 1) {

                                MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                                mobileClubBillingTry.setUnique(transactionId);
                                mobileClubBillingTry.setLogUnique(transactionId);
                                mobileClubBillingTry.setAggregator("IMI");
                                mobileClubBillingTry.setClubUnique(club.getUnique());
                                mobileClubBillingTry.setCreated(new Date());
                                mobileClubBillingTry.setNetworkCode(binetwork.toLowerCase());
                                mobileClubBillingTry.setParsedMsisdn(msisdn);
                                mobileClubBillingTry.setRegionCode("UK");
                                mobileClubBillingTry.setResponseCode(statusId);
                                mobileClubBillingTry.setResponseDesc("Failure");
                                mobileClubBillingTry.setResponseRef(transactionId);
                                mobileClubBillingTry.setStatus("fail");
                                mobileClubBillingTry.setTransactionId(transactionId);
                                mobileClubBillingTry.setCampaign(clubUser.getCampaign());
                                mobilebillingdao.insertBillingTry(mobileClubBillingTry);

                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String unSubscribed = sdf2.format(new Date());
                                clubUser.setActive(0);
                                clubUser.setUnsubscribed(SdcMiscDate.parseSqlDateString(unSubscribed));
                                umemobileclubuserdao.saveItem(clubUser);

                                String stop = "STOP";
                                try {
                                    if (DateUtils.isSameDay(clubUser.getSubscribed(), clubUser.getUnsubscribed())) {
                                        stop = "STOPFD";
                                    }

                                    campaigndao.log("ukbilling", clubUser.getLandingpage(), clubUser.getParsedMobile(), clubUser.getParsedMobile(), null, null, clubUser.getCampaign(), clubUser.getClubUnique(), stop, 0, null, null, binetwork.toLowerCase());
                                } catch (Exception e) {
                                    System.out.println("Exception EngageDr while campaignlog for stops " + e);
                                    e.printStackTrace();
                                }
                                //umemobileclubuserdao.disable(clubUser.getUnique());

                            }
                            if (mobileClubBillingPlan != null) {
                                /*Calendar cal = Calendar.getInstance(); 
                                 cal.setTime(new Date()); 
                                 cal.add(Calendar.HOUR_OF_DAY, 12); */
                                billingplandao.disableBillingPlan(msisdn, club.getUnique());
                                //billingplandao.updateFailedBillingPlan(mobileClubBillingPlan, cal.getTime());
                            }
                        } else {

                            // FAILURE
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(new Date());
                            cal.add(Calendar.HOUR, 3);

                        if (cal.get(Calendar.HOUR_OF_DAY) > 18) {
                            cal.add(Calendar.DATE, 1);
                            cal.set(Calendar.HOUR_OF_DAY, 9);
                        } else if (cal.get(Calendar.HOUR_OF_DAY) > 12) {
                            cal.set(Calendar.HOUR_OF_DAY, 18);
                        } else if (cal.get(Calendar.HOUR_OF_DAY) > 9) {
                            cal.set(Calendar.HOUR_OF_DAY, 12);
                        } else {
                            cal.set(Calendar.HOUR_OF_DAY, 9);
                        }
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);

                            if (statusId.equals("12") || statusId.equals("19")) {
                                
                                System.out.println("MobileClubBIllingPlan :" + mobileClubBillingPlan);

                                try {
                                    binetwork = mobilenetworks.getMobileNetwork("UK", clubUser.getNetworkCode());
                                } catch (Exception e) {
                                    binetwork = "unknown";
                                }
                                MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                                mobileClubBillingTry.setUnique(transactionId);
                                mobileClubBillingTry.setLogUnique(transactionId);
                                mobileClubBillingTry.setAggregator("IMI");
                                mobileClubBillingTry.setClubUnique(club.getUnique());
                                mobileClubBillingTry.setCreated(new Date());
                                mobileClubBillingTry.setNetworkCode(binetwork.toLowerCase());
                                mobileClubBillingTry.setParsedMsisdn(msisdn);
                                mobileClubBillingTry.setRegionCode("UK");
                                mobileClubBillingTry.setResponseCode("51");
                                mobileClubBillingTry.setResponseDesc("Insufficient Funds");
                                mobileClubBillingTry.setResponseRef(transactionId);
                                mobileClubBillingTry.setStatus("fail");
                                mobileClubBillingTry.setTransactionId(transactionId);
                                mobileClubBillingTry.setCampaign(clubUser.getCampaign());
                                mobilebillingdao.insertBillingTry(mobileClubBillingTry);
                                if (mobileClubBillingPlan != null) {

                                    //Calendar cal = Calendar.getInstance();
                                    //cal.setTime(new Date());
                                    //mobileClubBillingPlan.setLastPush(cal.getTime());
                                    mobileClubBillingPlan.setPushCount(mobileClubBillingPlan.getPushCount() + 1);
                                    mobileClubBillingPlan.setNextPush(cal.getTime());
                                    //cal.add(Calendar.HOUR_OF_DAY, 12);
                                    billingplandao.update(mobileClubBillingPlan);
                                }
                            } else {
                                
                                System.out.println("MobileClubBIllingPlan :" + mobileClubBillingPlan);

                                try {
                                    binetwork = mobilenetworks.getMobileNetwork("UK", clubUser.getNetworkCode());
                                } catch (Exception e) {
                                    binetwork = "unknown";
                                }

                                MobileClubBillingTry mobileClubBillingTry = new MobileClubBillingTry();
                                mobileClubBillingTry.setUnique(transactionId);
                                mobileClubBillingTry.setLogUnique(transactionId);
                                mobileClubBillingTry.setAggregator("IMI");
                                mobileClubBillingTry.setClubUnique(club.getUnique());
                                mobileClubBillingTry.setCreated(new Date());
                                mobileClubBillingTry.setNetworkCode(binetwork.toLowerCase());
                                mobileClubBillingTry.setParsedMsisdn(msisdn);
                                mobileClubBillingTry.setRegionCode("UK");
                                mobileClubBillingTry.setResponseCode(statusId);
                                mobileClubBillingTry.setResponseDesc("Other Error");
                                mobileClubBillingTry.setResponseRef(transactionId);
                                mobileClubBillingTry.setStatus("fail");
                                mobileClubBillingTry.setTransactionId(transactionId);
                                mobileClubBillingTry.setCampaign(clubUser.getCampaign());
                               mobilebillingdao.insertBillingTry(mobileClubBillingTry);
                                if (mobileClubBillingPlan != null) {

                                    //Calendar cal = Calendar.getInstance();
                                    //cal.setTime(new Date());
                                    //mobileClubBillingPlan.setLastPush(cal.getTime());
                                    mobileClubBillingPlan.setPushCount(mobileClubBillingPlan.getPushCount() + 1);
                                    //cal.add(Calendar.HOUR_OF_DAY, 12);
                                    billingplandao.update(mobileClubBillingPlan);
                                }
                            }
                        }
                    }
                } //END transactionId !=null 
            } //END NODE 

            System.out.println("ukdrmo  ENGAGE DR  END of FOR LOOP FOR  size " + i + " NODE getNodetype " + node.getNodeType() + " NODE ELEMENT " + Node.ELEMENT_NODE);
        } // END FOR LOOP 
    }
    
       private String getClubId(String longString, String separateby) {
        int index = longString.indexOf(separateby);
        if (index > 0) {
            return longString.substring(index + separateby.length());
        } else {
            return "5015622786341KDS"; //Default Quiz2Win club if not found anya
        }
    }
       
       
}
