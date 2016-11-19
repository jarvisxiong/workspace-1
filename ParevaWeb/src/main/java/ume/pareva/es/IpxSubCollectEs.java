package ume.pareva.es;

/**
 *
 * @author trung
 */
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubCampaignDao;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.dao.UmeUserDao;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.MiscCr;
import ume.pareva.sdk.MiscDate;
import ume.pareva.util.ZACPA;

@Component("ipxsubcollectes")
public class IpxSubCollectEs {

    @Autowired
    UmeUserDao umeuserdao;

    @Autowired
    UmeMobileClubUserDao umemobileclubuserdao;

    @Autowired
    IpxTimerSendSms timersms;

    @Autowired
    IpxBillingDirectEs directBilling;

    @Autowired
    MobileClubCampaignDao campaigndao;

    @Autowired
    MobileClubDao mobileclubdao;

    @Autowired
    ZACPA zacpa;

    @Autowired
    CpaLoggerDao cpaloggerdao;

    public void requestSubscribeTimerPending(final MobileClub club,
            final String msisdn, final String transactionId,
            final String operator, final String campaignId,
            final UmeDomain dmn,
            final MobileClubCampaign cmpg,
            final String parameter1,
            final String parameter2,
            final String parameter3,
            final int time, final HttpServletRequest req, final HttpServletResponse resp
    ) {

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        String url = "http://" + dmn.getDefaultUrl() + "/subscribe_temp.jsp?";
                        url += "transactionid=" + transactionId;
                        url += "&msisdn=" + msisdn;
                        url += "&op=" + operator;
                        url += "&cid=" + campaignId;
                        url += "&clubUnique=" + club.getUnique();
                        url += "&userName=" + club.getOtpSoneraId();
                        url += "&password=" + club.getOtpTelefiId();
                        url += "&service=" + club.getOtpServiceName();
                        //, , club.getOtpServiceName()
//                		String url = "http://ume/servlet/subtemp?transactionid=23907556565&op=wind&msisdn=393205684194";
                        System.out.println("IPX url request: " + url);
                        SendGetandPost http = new SendGetandPost();
                        String response = "";

                        try {
                            response = http.sendGet(url);
                            System.out.println("IPX request response: " + response);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            System.out.println("IPX request exception: " + e);
                        }
                        if (response.contains("successful")) {
                            try {
                                String subscriptionId = response.split(",")[1];
                                System.out.println("IPX successful subscriptionId: " + subscriptionId);
                                if (subscriptionId != null) {
                                    handleCheckingSubscriptionIdPending(msisdn, operator, transactionId, club, cmpg, dmn, campaignId, subscriptionId, req, resp,
                                            parameter1, parameter2, parameter3);
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                System.out.println("IPX request handle subscriptionId exception: " + e);
                            }
                        }
                    }
                },
                time
        );
    }

    // TODO handling subscriptionId here
    public void handleCheckingSubscriptionIdPending(String msisdn, String operator, String transactionId, MobileClub club,
            MobileClubCampaign cmpg, UmeDomain dmn, String campaignId, String subscriptionId, HttpServletRequest request, HttpServletResponse response,
            String parameter1,
            String parameter2,
            String parameter3) {
        // Next period need to have clubId also, campaignUnique

        if (msisdn == null) {
            msisdn = "";
        }
        if (operator == null) {
            operator = "";
        }
        if (subscriptionId == null) {
            subscriptionId = "";
        }
        if (transactionId == null) {
            transactionId = "";
        }

        System.out.println("IPX ES subcollect handleCheckingSubscriptionId: " + subscriptionId);

        // TODO checking msisdn and subscription existing or not. if not create user  
        String defClubDomain = "5510024809921CDS";

        Calendar c1 = new GregorianCalendar();
        c1.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        Date bstart = c1.getTime();
        Date getNowTime = c1.getTime();
        c1.add(Calendar.DATE, club.getPeriod());
        c1.add(Calendar.MINUTE, -5);
        Date bend = c1.getTime();

        SdcMobileClubUser clubUser = null;

        String campaignUnique = "";
        String campaignsrc = "";
        if (cmpg != null) {
            campaignUnique = cmpg.getUnique();
            campaignsrc = cmpg.getSrc();
        }
        if (campaignUnique.trim().equalsIgnoreCase("")) {
            if (!campaignId.equals("")) {
                cmpg = UmeTempCmsCache.campaignMap.get(campaignId);
            }
            try {
                campaignUnique = cmpg.getUnique();
                campaignsrc = cmpg.getSrc();
            } catch (Exception e) {
                campaignUnique = "";
                campaignsrc = "";
            }
        }

        try {
            if (!msisdn.equals("") && !subscriptionId.equals("")
                    && !operator.equals("") && !transactionId.equals("")) {
                UmeUser user = null;
                if (user == null && !msisdn.equals("")) {
                    System.out
                            .println("*************** inside msisdn: " + msisdn);
                    user = umeuserdao.getUser(msisdn);
                }

                if (user == null) {
                    System.out
                            .println("*************** Checking User is Null");
                    user = new UmeUser();
                    user.setMobile(msisdn);
                    user.setWapId(SdcMisc.generateLogin(10));
                    user.setDomain(defClubDomain);
                    user.setActive(1);
                    user.setCredits(club.getCreditAmount());
                    user.setLanguage(dmn.getDefaultLang());
                    //TODO check with Madan why stat is emtpy string
                    String stat = umeuserdao.addNewUser(user);
                    user.setClubMap(umemobileclubuserdao.getClubs(user.getUnique()));
                }

                if (user != null) {
                    if (user.getAccountType() == 99) {
                        return;
                    }
                    user.updateMap("active", "1");
                    umeuserdao.commitUpdateMap(user);
                    user.clearUpdateMap();
                    clubUser = umemobileclubuserdao.getClubUserByMsisdn(user.getMobile(), club.getUnique());
                    if (clubUser != null) {
                        if (mobileclubdao.isActive(user, club)) {
                            return;
                            //return;
                        }

                        // Update existing clubuser
                        clubUser.setParsedMobile(user.getParsedMobile());
                        clubUser.setActive(1);
                        clubUser.setCredits(club.getCreditAmount());
                        clubUser.setAccountType(0);
                        clubUser.setBillingStart(bstart);
                        clubUser.setBillingEnd(bend);
                        clubUser.setBillingRenew(bstart);
                        clubUser.setCampaign(campaignUnique);
                        clubUser.setNetworkCode(operator);
                        clubUser.setSubscribed(new Date());
                        clubUser.setParam1(transactionId);
                        clubUser.setParam2(subscriptionId);
                        clubUser.setNextPush(MiscDate.parseSqlDate("2011-01-01"));
                        umemobileclubuserdao.saveItem(clubUser);
                    } else {
                        clubUser = new SdcMobileClubUser();
                        clubUser.setUnique(SdcMisc.generateUniqueId());
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
                        clubUser.setCampaign(campaignUnique);
                        clubUser.setNetworkCode(operator);
                        clubUser.setUnsubscribed(new Date(0));
                        clubUser.setSubscribed(new Date());
                        clubUser.setParam1(transactionId);
                        clubUser.setParam2(subscriptionId);
                        umemobileclubuserdao.saveItem(clubUser);
                        if (user.getClubMap() != null) {
                            user.getClubMap().put(club.getUnique(), clubUser);
                        }
                    }
                }

                if (clubUser == null) {
                    System.out.println("*************************** oh my god clubUser is null");
                }

                try {
                    String fromNumber = club.getClubName();
                    timersms.requestSendSmsSpainTimer(club, user.getParsedMobile(), fromNumber, user, 3 * 60 * 1000, false);
                } catch (Exception timerSenderSMSException) {
                    System.out.println("IPX timerSenderSMSException: " + timerSenderSMSException.toString());
                }

                MobileClubBillingPlan billingplan = null;
                billingplan = new MobileClubBillingPlan();
                billingplan.setTariffClass(club.getPrice());
                billingplan.setActiveForAdvancement(1);
                billingplan.setActiveForBilling(1);
                billingplan.setAdhocsRemaining(0.0);
                billingplan.setBillingEnd(bend);
                billingplan.setClubUnique(club.getUnique());
                billingplan.setLastPaid(clubUser.getSubscribed());
                billingplan.setLastSuccess(new Date(0));
                billingplan.setLastPush(new Date(0));
                billingplan.setNetworkCode(operator);
                billingplan.setNextPush(bend);
                billingplan.setParsedMobile(user.getParsedMobile());
                billingplan.setPartialsPaid(0.0);
                billingplan.setSubscribed(clubUser.getSubscribed());
                //billingplan.setPartialsRequired(Double.parseDouble(String.valueOf(userclubdetails.getFrequency() + "")));
                billingplan.setPartialsRequired(1.0);
                billingplan.setPushCount(0.0);
                billingplan.setServiceDate(getNowTime);
                billingplan.setSubUnique(clubUser.getUserUnique());
                billingplan.setServiceDateBillsRemaining(1.0);
                billingplan.setBillingEnd(bend);
                billingplan.setContractType("");
                billingplan.setExternalId(clubUser.getParam2());
                try {
                    directBilling.requestBillingPlanDirect(billingplan, clubUser, club.getOtpSoneraId(), club.getOtpTelefiId(), club.getOtpServiceName());
                } catch (Exception eee) {
                    System.out.println("***************requestBillingPlanDirect ES CONFIRM BABE*********************");
                    eee.printStackTrace();
                    System.out.println("***************requestBillingPlanDirect ES CONFIRM BABE*********************");
                }
                try {
                    campaigndao.log("confirm", "xhtml", user.getUnique(),
                            user.getParsedMobile(), null, dmn.getUnique(),
                            campaignUnique, club.getUnique(), "SUBSCRIBED",
                            1, request, response, operator);
                    if (cmpg != null) {
                        cmpg.setBillingCount(cmpg.getBillingCount() + 2);
                        campaigndao.saveItem(cmpg);
                    }

                    if (cmpg != null && cmpg.getSrc().trim().toLowerCase().endsWith("cpa")) {
                        // 2016.01.13 - AS - Removed commented code, check repo history if needed
                        //IF CPAType is subscription then we log inside cpaLogging for notification
                        if (cmpg != null && cmpg.getCpaType().equalsIgnoreCase("subscription")) {
                            // 2016.01.13 - AS - Removed commented code, check repo history if needed
                            int insertedRows = cpaloggerdao.insertIntoCpaLogging(msisdn, campaignUnique, club.getUnique(), 10, clubUser.getNetworkCode(), campaignsrc);

                            // 2016.01.13 - AS - Removed commented code, check repo history if needed
                            int updatecpavisit = cpaloggerdao.updateCpaVisitLogMsisdnSubscriptionDate(msisdn, clubUser.getSubscribed(), cmpg.getUnique(), parameter1, parameter2, parameter3);
                        }
                    }

                    if (cmpg != null && cmpg.getSrc().trim().endsWith("RS")) {
//                      // 2016.01.13 - AS - Removed commented code, check repo history if needed
                        String enMsisdn = MiscCr.encrypt(msisdn);
                        int insertedRows =  cpaloggerdao.insertIntoRevShareLogging(0,  cmpg.getPayoutCurrency(), msisdn, enMsisdn, campaignUnique, club.getUnique(), 0, clubUser.getNetworkCode(), campaignsrc, 0);

                        // 2016.01.13 - AS - Removed commented code, check repo history if needed
                        int updatedRows = cpaloggerdao.updateRevShareVisitLogMsisdnSubscriptionDate(msisdn, clubUser.getSubscribed(),  cmpg.getUnique(), parameter1, parameter2, parameter3);
                    }

                    System.out
                            .println("***************END OF IT CONFIRM BABE*********************");

                } catch (Exception campaignDaoException) {
                    System.out
                            .println("***************campaignDaoException IT CONFIRM BABE*********************");
                    campaignDaoException.printStackTrace();
                    System.out
                            .println("***************campaignDaoException IT CONFIRM BABE*********************");
                }

            }

        } catch (Exception e) {
            System.out.println("IPX Subscription Collection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
