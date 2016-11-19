package ume.pareva.userservice;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TimeZone;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeTempCache;
import ume.pareva.es.IpxEsSimpleFunction;
import ume.pareva.ipx.extra.IpxUserStop;
import ume.pareva.ipx.extra.IpxUserStopDao;
import ume.pareva.it.DigitAPI;
import ume.pareva.it.IpxItSimpleFunction;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.sdk.MiscCr;
import ume.pareva.sdk.MiscDate;
import ume.pareva.snp.CacheManager;
import ume.pareva.uk.CompetitionStop;
import ume.pareva.util.ZACPA;
import ume.pareva.za.ZAStop;

/**
 *
 * @author madan
 */
@Component("userstopservice")
public class UserStopService {

    private static final String STOPPED = "SUBSCRIPTION RECORD STOPPED SUCCESSFULLY";
    private static final String ALREADY_STOPPED = "RECORD WAS ALREADY STOPPED";
    private static final String SUBSCRIPTION_ERROR = "ERROR - CONTACT MADAN/ALEX - madan@umelimited.com/alex.sanchez@umelimited.com";

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

    public Map<String, String> stopSingleSubscription(String msisdn, String clubUnique) {
        return stopSingleSubscription(msisdn, clubUnique, null, null);
    }

    public Map<String, String> stopSingleSubscriptionNormal(String msisdn, String clubUnique) {
        return stopSingleSubscriptionNormal(msisdn, clubUnique, null, null);
    }
    
    public Map<String, String> stopSingleSubscriptionPabx(String msisdn, String clubUnique) {
        return stopSingleSubscriptionSpecial(msisdn, clubUnique, 1, 3);
    }

    public Map<String, String> stopSingleSubscription(String msisdn, String clubUnique, HttpServletRequest req, HttpServletResponse resp) {
        boolean stopped = false;
        Map<String, String> stopMsisdnReport = new HashMap<String, String>();

        MobileClub club = UmeTempCmsCache.mobileClubMap.get(clubUnique);

        boolean specialStop = (club != null && (club.getRegion().equalsIgnoreCase("IT") || club.getRegion().equals("ES")))
                || (msisdn != null && (msisdn.startsWith("39") || msisdn.startsWith("34")));

        if (specialStop) {
            stopMsisdnReport = stopSingleSubscriptionSpecial(msisdn, clubUnique, 0, 3);
        } else {
            stopMsisdnReport = stopSingleSubscriptionNormal(msisdn, clubUnique, req, resp);
        }

        return stopMsisdnReport;
    }

    public Map<String, String> stopSingleSubscriptionSpecial(String msisdn, String clubUnique, int from, int days) {
        boolean stopped = true;
        String response = "SUCCESSFULLY STOPPED";
        Map<String, String> stopMsisdnReport = new HashMap<String, String>();
        try {
            SdcMobileClubUser clubUser = clubuserdao.getClubUserByMsisdn(msisdn, clubUnique);
            if (clubUser == null) {
                response = "USER DOESN'T EXIST";
            }
            if (clubUser != null && clubUser.getActive() == 0) {
                response = "USER ALREADY STOPPED";
            }
           

                if (clubUser!=null && clubUser.getActive() == 1 && !clubUser.getUnsubscribed().after(new Date())) {

                    MobileClub club = clubdao.getMobileClub(clubUser.getClubUnique());
                    if (club != null) {

//                        String fromNumber = "Conferma";
//                        String msg = "La tua richiesta e stata approvata. Grazie";
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

                        if (doStopCounter(from)) {
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
            
        } catch (Exception e) {
            System.out.println("Exception stopSingleSubscriptionSpecial");
            e.printStackTrace();
        }
        stopMsisdnReport.put(msisdn, response);
        return stopMsisdnReport;
    }

    public Map<String, String> stopSingleSubscriptionNormal(String msisdn, String clubUnique, HttpServletRequest req, HttpServletResponse resp) {
        boolean stopped = true;
        String response = "SUCCESSFULLY STOPPED";
        Map<String, String> stopMsisdnReport = new HashMap<String, String>();
        try {
            SdcMobileClubUser clubUser = clubuserdao.getClubUserByMsisdn(msisdn, clubUnique);
            MobileClubBillingPlan billingplan = null;
            if (clubUser == null) {
                response = "USER DOESN'T EXIST";
            }
            if (clubUser != null && clubUser.getActive() == 0) {
                response = "USER ALREADY STOPPED";
            }
            if (clubUser != null && clubUser.getActive() == 1) {
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

                    switch (club.getRegion()) {
                        case "IT":
                            campaigndao.log("APIIPXReq", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
                                    club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
                            ipxitsimplefunction.callTerminateIT_DOI(clubUser, club, "");
                            break;
                        case "ES":
                            campaigndao.log("APIIPXReq", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
                                    club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), zastoplog, 0, req, resp, clubUser.getNetworkCode());
                            ipxessimpleFunction.callTerminateES_DOI(clubUser, club);
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
                    }
                }
//                stopMsisdnReport.put(msisdn, response);
            } else {
                stopped = false;
            }
        } catch (Exception e) {
            System.out.println("Exception StopSingleSubscription");
            e.printStackTrace();
        }
        stopMsisdnReport.put(msisdn, response);
        return stopMsisdnReport;
    }

    private boolean doStopCounter(int from) {
        boolean stopUser = false;
        int countingStop = 0;
        int numberOfSpecial = 3;
        if (from == 1) {
            numberOfSpecial = 2;
        }
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

        if (from == 0) {
            stopUser = false;
        }

        return stopUser;
    }

    public Map<String, String> stopAllSubscription(String msisdn, HttpServletRequest req, HttpServletResponse resp) {
        boolean stopped = false;
        List<SdcMobileClubUser> clubUsers = clubuserdao.getClubUsersByMsisdn(msisdn);
        Map<String, String> responses = new HashMap<>(0);
        for (SdcMobileClubUser clubUser : clubUsers) {
//                stopSingleSubscription(clubUser.getParsedMobile(), clubUser.getClubUnique(), req, resp);
            Map<String, String> stopMsisdnReport = stopSingleSubscriptionNormal(clubUser.getParsedMobile(), clubUser.getClubUnique(), req, resp);
            responses.put(clubUser.getParsedMobile() + "-" + clubUser.getClubUnique(), stopMsisdnReport.get(clubUser.getParsedMobile()));
        }

        return responses;
    }

    public Map<String, String> stopAllSubscription(String msisdn) {
        return stopAllSubscription(msisdn, null, null);
    }

    public Map<String, String> bulkStop(List<String> msisdns, HttpServletRequest req, HttpServletResponse resp) {
        Map<String, String> stopped = new HashMap<>(0);
        if (msisdns != null) {
            for (String msisdn : msisdns) {
                Map<String, String> stoppedmsisdn = stopAllSubscription(msisdn, req, resp);
                for (String key : stoppedmsisdn.keySet()) {
                    stopped.put(key, stoppedmsisdn.get(key));
                }
            }
        }
        return stopped;
    }

    public Map<String, String> bulkStop(List<String> msisdns) {
        return bulkStop(msisdns, null, null);
    }

}
