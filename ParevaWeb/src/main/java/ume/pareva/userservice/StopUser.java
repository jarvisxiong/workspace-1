package ume.pareva.userservice;

import static com.germany.utils.ParametersEnums.password;
import com.germany.utils.QueryBuilder;
import com.germany.xml.Response;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.ae.util.AESendMessage;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetailsDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobBillNotificationDao;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.es.IpxEsSimpleFunction;
import ume.pareva.es.IpxTimerSendSms;

import ume.pareva.ipx.extra.IpxUserStop;
import ume.pareva.ipx.extra.IpxUserStopDao;
import ume.pareva.it.DigitAPI;
import ume.pareva.it.IpxItSimpleFunction;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeClubMessages;
import ume.pareva.sdk.MiscCr;
import ume.pareva.sdk.MiscDate;
import ume.pareva.snp.CacheManager;
import ume.pareva.uk.CompetitionStop;
import ume.pareva.util.ZACPA;
import ume.pareva.za.ZAStop;

/**
 *
 * @author trung
 */
@Component("stopuser")
public class StopUser {

	@Autowired
	private UmeMobileClubUserDao clubuserdao;
	@Autowired
	private MobileClubCampaignDao campaigndao;
	@Autowired
	private MobileClubDao clubdao;
	@Autowired
	private CacheManager cacheManager;
	@Autowired
	private ZACPA zacpa;
	@Autowired
	private UserAuthentication userauthentication;
	@Autowired
	private IpxUserStopDao ipxuserstopdao;
	@Autowired
	IpxItSimpleFunction ipxitsimplefunction;

	@Autowired
	IpxEsSimpleFunction ipxessimpleFunction;

	@Autowired
	ZAStop zastop;

	@Autowired
	DigitAPI firstsms;

	@Autowired
	CompetitionStop competitionstop;

	@Autowired
	MobileClubBillingPlanDao billingplandao;

	@Autowired
	UmeTempCache umesdc;

	@Autowired
	CpaLoggerDao cpaloggerdao;

	@Autowired
	MobBillNotificationDao mobBillNotificationDao;

	@Autowired
	UmeClubDetailsDao umeclubdetailsdao;

	@Autowired
	IpxTimerSendSms timerSms;
        
        @Autowired
        RegionalDate regionaldate;

        @Autowired
        AESendMessage aeSendMessage;

	static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public boolean stopAllSubscription(String msisdn, HttpServletRequest req, HttpServletResponse resp) {
		boolean stopped = false;
		List<SdcMobileClubUser> clubUsers = clubuserdao.getClubUsersByMsisdn(msisdn);
		if (clubUsers != null && clubUsers.size() > 0) {
			for (SdcMobileClubUser clubUser : clubUsers) {
				//                stopSingleSubscription(clubUser.getParsedMobile(), clubUser.getClubUnique(), req, resp);
				stopped = stopSingleSubscriptionNormal(clubUser.getParsedMobile(), clubUser.getClubUnique(), req, resp);
			}
		}
		return stopped;
	}

	public boolean stopAllSubscription(String msisdn, HttpServletRequest req, HttpServletResponse resp,String requestfrom) {
		boolean stopped = false;
		List<SdcMobileClubUser> clubUsers = clubuserdao.getClubUsersByMsisdn(msisdn);
		if (clubUsers != null && clubUsers.size() > 0) {
			for (SdcMobileClubUser clubUser : clubUsers) {
				//                stopSingleSubscription(clubUser.getParsedMobile(), clubUser.getClubUnique(), req, resp);
				stopped = stopSingleSubscriptionNormal(clubUser.getParsedMobile(), clubUser.getClubUnique(), req, resp,requestfrom);
			}
		}
		return stopped;
	}

	public boolean stopAllSubscriptionByShortCode(String msisdn, String shortCode, HttpServletRequest req, HttpServletResponse resp,String requestfrom) {
		boolean stopped = false;
		List<SdcMobileClubUser> clubUsers = clubuserdao.getClubUsersByMsisdn(msisdn);

		if (clubUsers != null && clubUsers.size() > 0) {
			for (SdcMobileClubUser clubUser : clubUsers) {
				//                stopSingleSubscription(clubUser.getParsedMobile(), clubUser.getClubUnique(), req, resp);
				MobileClub club=UmeTempCmsCache.mobileClubMap.get(clubUser.getClubUnique());
				if(club!=null && club.getSmsNumber().equalsIgnoreCase(shortCode)) {
                                    if (requestfrom.equalsIgnoreCase("iemo")) {
                                        System.out.println("IEMO STOP " + "inside stopuser " + club.getUnique() + " - " + club.getName() + " - " + club.getSmsNumber());
                                    }
					stopped = stopSingleSubscriptionNormal(clubUser.getParsedMobile(), clubUser.getClubUnique(), req, resp,requestfrom);
				} // end if club!=null and club smsNumber is equal to shortCode
			}
		}
		return stopped;
	}



	public boolean bulkStop(List<String> msisdns, HttpServletRequest req, HttpServletResponse resp) {
		boolean stopped = false;
		if (msisdns != null) {
			for (String msisdn : msisdns) {
				stopped = stopAllSubscription(msisdn, req, resp);
			}
		}
		return stopped;
	}

	public boolean stopSingleSubscription(String msisdn, String clubUnique, HttpServletRequest req, HttpServletResponse resp) {
		boolean stopped = false;
		System.out.println("Webservice stopsingle subscription inside StopUser is called " + msisdn + " " + clubUnique);
		MobileClub club = UmeTempCmsCache.mobileClubMap.get(clubUnique);
		if (club != null) {
			if (club.getRegion().equalsIgnoreCase("IT")) {
				stopped = stopSingleSubscriptionSpecial(msisdn, clubUnique, 0, 3);
			} 
                        
                        else if(club.getRegion().equalsIgnoreCase("DE")){
                            sendStopDESubscription(msisdn,clubUnique);
                            stopped = stopSingleSubscriptionNormal(msisdn, clubUnique, req, resp);
			
                            
                        }
                        else {
				stopped = stopSingleSubscriptionNormal(msisdn, clubUnique, req, resp);
			}

		} else {
			if (msisdn.startsWith("39")) {
				stopped = stopSingleSubscriptionSpecial(msisdn, clubUnique, 0, 3);
			} else {
				stopped = stopSingleSubscriptionNormal(msisdn, clubUnique, req, resp);
			}

		}

		return stopped;
	}

	public boolean stopSingleSubscriptionSpecial(String msisdn, String clubUnique, int from, int days) {
		boolean stopped = true;
		try {
			SdcMobileClubUser clubUser = clubuserdao.getClubUserByMsisdn(msisdn, clubUnique);
			if (clubUser != null) {

				if (clubUser.getActive() == 1 && !clubUser.getUnsubscribed().after(new Date())) {

					MobileClub club = clubdao.getMobileClub(clubUser.getClubUnique());
					if (club != null) {

						String fromNumber = "Conferma";
						String msg = "Grazie per la tua richiesta. L.iscrizione a " + club.getClubName() + " e stata cancellata.";

						boolean isSendSMS = false;
						if (msisdn.startsWith("39") || msisdn.startsWith("34")) {
							isSendSMS = true;
							if(msisdn.startsWith("34")){
								fromNumber = "Confirma";
								msg= "Gracias por su solicitud. Su suscripción a " + club.getClubName() + " ha sido cancelada";

							}                            
						}

						if (doStopCounter()) {
							msg = "La tua richiesta e stata approvata. Grazie";
							if(msisdn.startsWith("34")){
								msg = "Su solicitud ha sido aprobada. Gracias";
							}    
						} else {
							Calendar nowTime = Calendar.getInstance();
							switch (club.getRegion()) {
							case "IT":
								nowTime.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
								break;
							case "ES":
								nowTime.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
								break;
							case "ZA":
								nowTime.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg"));
								break;
							}

							if (clubUser.getBillingEnd().after(nowTime.getTime())) {
								nowTime.setTime(clubUser.getBillingEnd());
							}
							nowTime.add(java.util.Calendar.DATE, days);

							clubUser.setUnsubscribed(nowTime.getTime());
							clubuserdao.saveItem(clubUser);

							IpxUserStop stopUser = new IpxUserStop();
							stopUser.setClubUnique(clubUnique);
							stopUser.setParsedMobile(msisdn);
							stopUser.setUnsubscribed(nowTime.getTime());
							stopUser.setExternalId(clubUser.getParam2());
							stopUser.setNetworkCode(clubUser.getNetworkCode());
							stopUser.setStatus(0);
							stopUser.setFrom(from);
							ipxuserstopdao.addNewUserStop(stopUser);
						}

						firstsms.setMsg(msg);
						firstsms.setMsisdn(msisdn);
						firstsms.setFrom(fromNumber); //11 alphanumeric characters OR upto 16 digit phone number
						firstsms.setReport("True");
						firstsms.setNetwork("UNKNOWN");
						try {
							if (isSendSMS) {
								firstsms.sendSMS();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					stopped = false;
				}
			}
		} catch (Exception e) {
			System.out.println("Exception stopSingleSubscriptionSpecial");
			e.printStackTrace();
		}
		return stopped;
	}

	public boolean stopSingleSubscriptionNormal(String msisdn, String clubUnique, HttpServletRequest req, HttpServletResponse resp) {
		boolean stopped = true;
		try {
			SdcMobileClubUser clubUser = clubuserdao.getClubUserByMsisdn(msisdn, clubUnique);
			MobileClubBillingPlan billingplan = null;
			if (clubUser != null && clubUser.getActive() == 1) {
				//               if(clubUser.getActive()==1 && !clubUser.getUnsubscribed().after(new Date())){
				MobileClub club = clubdao.getMobileClub(clubUser.getClubUnique());
				if (club != null) {

					clubdao.unsubscribe(club, null, clubUser.getParsedMobile());
					cacheManager.delete(clubUser.getUserUnique());

					billingplan = billingplandao.getActiveBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());
					if (billingplan != null && billingplan.getActiveForBilling() == 1) {
						billingplandao.disableBillingPlan(msisdn, club.getUnique());

					}
					//TOD confirm with Madan               
					if (req != null) {
						userauthentication.invalidateUser(req);
					}
					//
					MobileClubCampaign cmpgn = null;
					if (clubUser.getCampaign() != null && clubUser.getCampaign().trim().length() > 0) {
						cmpgn = UmeTempCmsCache.campaignMap.get(clubUser.getCampaign());
					}

					if (cmpgn != null && cmpgn.getSrc().endsWith("RS")) {
						// 2016.01.13 - AS - Removed commented code, check repo history if needed
						String enMsisdn = MiscCr.encrypt(clubUser.getParsedMobile());
						int insertedRows = cpaloggerdao.insertIntoRevShareLogging(0, cmpgn.getPayoutCurrency(), clubUser.getParsedMobile(), enMsisdn, clubUser.getCampaign(), club.getUnique(), 0, clubUser.getNetworkCode(), cmpgn.getSrc(), 2);
					}

					clubUser.setUnsubscribed(new Date());
					String zastoplog = "STOP";
					if (DateUtils.isSameDay(clubUser.getSubscribed(), clubUser.getUnsubscribed())) {
						zastoplog = "STOPFD";
					}
					String StopMessage = "";
					List<UmeClubMessages> stopMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Stop");
					if(stopMessages!=null && !stopMessages.isEmpty()){
						StopMessage = stopMessages.get(0).getaMessage();
						//                        System.out.println("**Stop message: " + StopMessage);
					}
					switch (club.getRegion()) {
					case "IT":
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						ipxitsimplefunction.callTerminateIT_DOI(clubUser, club, StopMessage);
						break;
					case "ES":
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						ipxessimpleFunction.callTerminateES_DOI(clubUser, club);
						timerSms.requestSendSmsSpainTimerMessage(StopMessage, msisdn, club.getClubName(), 3 * 60 * 1000, false);
						break;
					case "ZA":
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						zastop.StopZAUser(clubUser, club);
						break;

					case "UK":
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						competitionstop.StopCompetition(clubUser, club);
						break;

					case "IE":
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						competitionstop.StopCompetition(clubUser, club);
						break;

					case "IN":                        	 
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						break;

					case "PT":                        	 
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						break;
					case "AU":                        	 
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						break;
                                        case "AE":                        	 
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
                                                break;
                                            
                                        case "DE":                        	 
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						break;

					}
				}
				//            }
			} else {
				stopped = false;
			}
		} catch (Exception e) {
			System.out.println("Exception StopSingleSubscription");
			e.printStackTrace();
		}
		return stopped;
	}




	public boolean stopSingleSubscriptionNormal(String msisdn, String clubUnique, HttpServletRequest req, HttpServletResponse resp, String stopFrom) {
		boolean stopped = true;
		try {
			SdcMobileClubUser clubUser = clubuserdao.getClubUserByMsisdn(msisdn, clubUnique);
			MobileClubBillingPlan billingplan = null;
			if (clubUser != null) {
				//               if(clubUser.getActive()==1 && !clubUser.getUnsubscribed().after(new Date())){
				MobileClub club = clubdao.getMobileClub(clubUser.getClubUnique());
				if (club != null) {				

					billingplan = billingplandao.getActiveBillingPlanByMsisdnAndClubUnique(msisdn, club.getUnique());
					if (billingplan != null && billingplan.getActiveForBilling() == 1) {
						billingplandao.disableBillingPlan(msisdn, club.getUnique());

					}
					//TOD confirm with Madan               
					if (req != null) {
						userauthentication.invalidateUser(req);
					}
					//
					MobileClubCampaign cmpgn = null;
					if (clubUser.getCampaign() != null && clubUser.getCampaign().trim().length() > 0) {
						cmpgn = UmeTempCmsCache.campaignMap.get(clubUser.getCampaign());
					}

					if (cmpgn != null && cmpgn.getSrc().endsWith("RS")) {
						// 2016.01.13 - AS - Removed commented code, check repo history if needed
						String enMsisdn = MiscCr.encrypt(clubUser.getParsedMobile());
						int insertedRows = cpaloggerdao.insertIntoRevShareLogging(0, cmpgn.getPayoutCurrency(), clubUser.getParsedMobile(), enMsisdn, clubUser.getCampaign(), club.getUnique(), 0, clubUser.getNetworkCode(), cmpgn.getSrc(), 2);
					}

					clubUser.setUnsubscribed(new Date());
                                         Date stopdate = regionaldate.getRegionalDate(club.getRegion(), new Date());
					String zastoplog = "STOP";
					if (DateUtils.isSameDay(clubUser.getSubscribed(),stopdate)) {
						zastoplog = "STOPFD";
					}

					String StopMessage = "";
					List<UmeClubMessages> stopMessages=umeclubdetailsdao.getUmeClubMessages(club.getUnique(), "Stop");
					if(stopMessages!=null && !stopMessages.isEmpty()){
						StopMessage = stopMessages.get(0).getaMessage();
					}
                                        if(clubUser.getActive() == 1){
                                        clubdao.unsubscribe(club, null, clubUser.getParsedMobile());
					cacheManager.delete(clubUser.getUserUnique());
					switch (club.getRegion()) {
					case "IT":
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						ipxitsimplefunction.callTerminateIT_DOI(clubUser, club, StopMessage);
						break;
					case "ES":
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						ipxessimpleFunction.callTerminateES_DOI(clubUser, club);
						timerSms.requestSendSmsSpainTimerMessage(StopMessage, msisdn, club.getClubName(), 3 * 60 * 1000, false);
						break;
					case "ZA":
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						zastop.StopZAUser(clubUser, club);
						break;

					case "UK":
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						competitionstop.StopCompetition(clubUser, club, stopFrom);
						break;

					case "IE":
					case "NO":
					case "SE":
					case "AU":
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						competitionstop.StopCompetition(clubUser, club,stopFrom);
						break;

					case "IN":                        	 
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						break;
                                            
                                        case "DE":                        	 
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						break;

					default:
						campaigndao.log("API", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
								club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
						break;


					}
                                }
				}
				//            }
			} else {
				stopped = false;
			}
		} catch (Exception e) {
			System.out.println("Exception StopSingleSubscription");
			e.printStackTrace();
		}
		return stopped;
	}

	private boolean doStopCounter() {
		boolean stopUser = false;
		int countingStop = 0;
		int numberOfSpecial = 3;
		int maxNumber = 500;
		String ipxCountingStopName = "ipxCountingStopPABX_" + MiscDate.sqlDate.format(new Date());
		ServletContext cxt = umesdc.getCxt();
		Integer countingStopped = (Integer) cxt.getAttribute(ipxCountingStopName);
		if (countingStopped == null) {
			countingStopped = 0;
		} else {
			countingStopped++;
		}
		cxt.setAttribute(ipxCountingStopName, countingStopped);
		countingStop = countingStopped;

		if (countingStop >= maxNumber) {
			stopUser = true;
		} else if (countingStop >= numberOfSpecial && (countingStop % numberOfSpecial) == 0) {
			stopUser = true;
		} else {
			stopUser = false;
		}

		return stopUser;
	}
        
        private void sendStopDESubscription(String msisdn,String clubUnique){
            String username = "umenter";
            String password = "f6pUbr5d";
            String serviceCodeWAP = "DE010056";
            String serviceCodeWIFI = "DE010057";
            String price = "499";
            String conn_path="http://premium.mobile-gw.com:9000/"; 
            
            SdcMobileClubUser clubuser = clubuserdao.getClubUserByMsisdn(msisdn, clubUnique);
            if(clubuser!=null){
                boolean wifiuser=clubuser.getParam2().equalsIgnoreCase("wifi");
                QueryBuilder germanyQueryBuilder = new QueryBuilder(username, password, serviceCodeWAP,serviceCodeWIFI, conn_path, price);
                
                String responsecode = "";
                String subscriptionId=clubuser.getParam1();
                                
                                //Get Subscription record from NTH first 
                String urlsub=germanyQueryBuilder.getSubscriptionByMsisdn(msisdn,wifiuser);
                Response subresponse=germanyQueryBuilder.getResponseFromNTH(urlsub);
                if(subresponse!=null){
                        responsecode=subresponse.getResultCode();
                        subscriptionId=subresponse.getSubscription().getSubscriptionId();
                        String networkoperator=subresponse.getOperatorCode();
                        System.out.println("Germany Testing "+responsecode+" sessionId "+subscriptionId+" network "+networkoperator+" serviceCode "+serviceCodeWAP+" wifiuser = "+wifiuser);
                }
                
                String url = germanyQueryBuilder.closeSubscription(subscriptionId, wifiuser);
		Response gresponse = germanyQueryBuilder.getResponseFromNTH(url);
                
                    if (gresponse != null) {
			responsecode = gresponse.getResultCode();
			if (responsecode == null) {
				responsecode = "";
					}
				}

                    // If Responsecode is not 100 Try again to unsubscribe from Aggregator!! 
				if (!responsecode.equals("100")) {
					url = germanyQueryBuilder.closeSubscription(subscriptionId, true);
					gresponse = germanyQueryBuilder.getResponseFromNTH(url);
					responsecode = gresponse.getResultCode();
					if (responsecode == null) {
						responsecode = "";
					}
				}
                
            }
            
        }

	//    private boolean doStopCounter() {
	//        boolean stopUser = false;
	////        int countingStop = 0;
	////        int numberOfSpecial = 3;
	////        int maxNumber = 500;
	////        String ipxCountingStopName = "ipxCountingStopPABX_" + MiscDate.sqlDate.format(new Date());
	////        ServletContext cxt = umesdc.getCxt();
	////        Integer countingStopped = (Integer) cxt.getAttribute(ipxCountingStopName);
	////        if (countingStopped == null) {
	////            countingStopped = 0;
	////        } else {
	////            countingStopped++;
	////        }
	////        cxt.setAttribute(ipxCountingStopName, countingStopped);
	////        countingStop = countingStopped;
	////
	////        if (countingStop >= maxNumber) {
	////            stopUser = true;
	////        } else if (countingStop >= numberOfSpecial && (countingStop % numberOfSpecial) == 0) {
	////            stopUser = true;
	////        } else {
	////            stopUser = false;
	////        }
	//        return stopUser;
	//    }
}
