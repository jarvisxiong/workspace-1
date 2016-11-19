package ume.pareva.it;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang.time.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.MobileClub;

import ume.pareva.cms.MobileClubBilling;
import ume.pareva.cms.MobileClubBillingTry;
import ume.pareva.cms.MobileClubCampaign;
import ume.pareva.cms.MobileClubDao;

import ume.pareva.dao.CpaLoggerDao;
import ume.pareva.dao.MobileBillingDao;

import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.MobileClubBillingPlanDao;
import ume.pareva.pojo.MobileClubBillingPlan;
import ume.pareva.pojo.MobileClubBillingSuccesses;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.dao.RevShareLoggingDao;
import ume.pareva.sdk.FileUtil;
import ume.pareva.sdk.Misc;
import ume.pareva.sdk.MiscCr;
import ume.pareva.sdk.MiscDate;
import ume.pareva.smsapi.IpxBillingConnection;
import ume.pareva.smsapi.IpxBillingSubmit;
import ume.pareva.util.ZACPA;

@Component("ipxbillingdirect")
public class IpxBillingDirect {
    
    static final Date creation = new Date();
    @Autowired
    CpaLoggerDao cpaloggerdao;
    
    @Autowired
    private ZACPA zalog;
    
    @Autowired
    private MobileClubBillingPlanDao billingPlanDao;
    
    @Autowired
    RevShareLoggingDao revshareloggingdao;
    
    @Autowired
    MobileClubDao mobileclubdao;
    
    @Autowired
    MobileBillingDao mobileclubbillingsuccessdao;

    //final MobileClubBillingPlanDao billingPlanDao, final MobileClubBillingDao billingClubDao
    //TODO NEED to UPGRADE
    public void requestBillingPlanDirect(final MobileClubBillingPlan bill,
            final SdcMobileClubUser clubUser, final String clubIPXUserName,
            final String clubIPXPassword, final String serviceMetaData) {
        
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String debugPath = "/var/log/pareva/IT/ipxlog/billing_direct";
                String debugFile = "IPX_direct_charged_debug_"
                        + MiscDate.sqlDate.format(new Date()) + ".txt";
                StringBuilder dd = new StringBuilder();
                String crlf = "\r\n";
                
                System.out.println("IPX requestBillingDirect runnable "
                        + new Date());
                dd.append(crlf + "IPX requestBillingDirect runnable "
                        + new Date());
//                HALF_DAY_TIME = DateUtils.truncate(DateUtils.addHours(new Date(),12), Calendar.HOUR);				
                Date HALF_DAY_TIME, NEXT_DATE_ZERO;
                {
                    // DATE SETTINGS
                    HALF_DAY_TIME = getHalfDayTime();
                    NEXT_DATE_ZERO = getNextDateZero_CHANGED();
                }
                
                boolean successfulCharged = false;
                boolean addTryAsync = false;
                try {
                    IpxBillingSubmit sms = null;
                    sms = new IpxBillingSubmit();
                    sms.setSmsAccount("ipx_billing");
                    sms.setTransactionId(clubUser.getParam1());
                    sms.setSubscriptionId(clubUser.getParam2());
                    sms.setConsumerId(bill.getParsedMobile());
                    sms.setOperator(clubUser.getNetworkCode());
                    sms.setTariffClass(bill.getTariffClass());
                    sms.setUsername(clubIPXUserName);
                    sms.setPassword(clubIPXPassword);
                    sms.setServiceMetaData(serviceMetaData);
                    IpxBillingConnection billingConnection = new IpxBillingConnection();
                    String resp = billingConnection.doRequest(sms);
                    
                    String[] props;
                    props = resp.split("--");
                    
                    bill.setPushCount(bill.getPushCount() + 1);
                    
                    System.out.println("IPX billing response: " + resp + "--"
                            + bill.getParsedMobile() + "--"
                            + clubUser.getNetworkCode() + "--"
                            + serviceMetaData + "--" + new Date());
                    dd.append(crlf + "IPX billing response: " + resp + "--"
                            + bill.getParsedMobile() + "--"
                            + clubUser.getNetworkCode() + "--" + new Date());
                    bill.setLastPush(getNowTimeIT());
                    if (props[1].equals("0")) {
                        if (clubUser.getNetworkCode().equals("vodafone")
                                || clubUser.getNetworkCode().equals("wind")) {
                            addTryAsync = true;
//                            bill.setNextPush(getHalfDayTime());
                            bill.setNextPush(HALF_DAY_TIME);
//                            if(clubUser.getNetworkCode().equals("vodafone"))
//                                bill.setNextPush(NEXT_DATE_ZERO);    

                        } else {
                            //TODO CPA FOR ALEX
                            if (clubUser.getCampaign() != null) {
                                MobileClubCampaign cmpg = UmeTempCmsCache.campaignMap.get(clubUser.getCampaign());
                                
                                if (cmpg != null && cmpg.getSrc().endsWith("RS")) {
                                    MobileClub club = mobileclubdao.getMobileClub(clubUser.getClubUnique());
                                    revshareloggingdao.addRevShareLogging(cmpg, "", clubUser.getParsedMobile(), MiscCr.encrypt(clubUser.getParsedMobile()), club, clubUser.getNetworkCode(), "IT", "1");
                                }
                                
                                if (cmpg != null && cmpg.getSrc().toLowerCase().endsWith("cpa") && cmpg.getCpaType().equalsIgnoreCase(("billing"))) {
                                    // 2016.01.13 - AS - Removed commented code, check repo history if needed
                                    int insertedRows = cpaloggerdao.insertIntoCpaLogging(clubUser.getParsedMobile(), clubUser.getCampaign(), clubUser.getClubUnique(), 10, clubUser.getNetworkCode(), cmpg.getSrc());
                                }
                            }
                        }
                        //========= Finish Adding RevShare Coding 
                        bill.setServiceDateBillsRemaining(bill.getServiceDateBillsRemaining() - 1);
                        bill.setPartialsPaid(bill.getPartialsPaid() + 1);
//                            bill.setNextPush(getNextHourZero());
                        bill.setNextPush(new Date());
                        successfulCharged = true;
                    } else {
//                        bill.setNextPush(getNextFourHour());                        
                        if (bill.getNetworkCode().equals("vodafone") || bill.getNetworkCode().equals("wind")) {
//                            bill.setNextPush(HALF_DAY_TIME);  
                            bill.setNextPush(HALF_DAY_TIME);
//                            if(clubUser.getNetworkCode().equals("vodafone"))
//                                bill.setNextPush(NEXT_DATE_ZERO);  
                        } else if (bill.getNetworkCode().equals("three")) {
                            bill.setNextPush(getQuarterDayTimeThree());
                        } else {
                            bill.setNextPush(getQuarterDayTime());
                        }
                    }
                    
                    MobileClubBillingTry btry = parseIpxResponse(resp, bill, "IT", clubUser.getCampaign());
                    
                    if (successfulCharged) {
                        try {
                            MobileClubBillingSuccesses success = new MobileClubBillingSuccesses(bill, btry, sms.getSmsAccount(), sms.getServiceDesc(), sms.getResponse(), "S");
                            
                            mobileclubbillingsuccessdao.insertBillingSuccess(success);
                        } catch (Exception e) {
                            System.out.println("IPX requestBillingDirect billingsuccessdao exception: "
                                    + e + "--" + new Date());
                            e.printStackTrace();
                            dd.append(crlf + "IPX requestBillingDirect billingsuccessdao exception: " + e
                                    + "--" + new Date());
                        }
                    }
//                    if (!billingPlanDao.addBillingPlan(bill)) {
//                        billingPlanDao.addBillingPlan(bill);
//                    }
                    if (billingPlanDao.insertBillingPlan(bill) <= 0) {
                        billingPlanDao.insertBillingPlan(bill);
                    }
                    
                    if (addTryAsync) {
                        addTryItemAsync(btry);
                    } else {
                        addTryItem(btry);
                    }
                    
                } catch (Exception e) {
                    System.out.println("IPX requestBillingDirect exception: "
                            + e + "--" + new Date());
                    e.printStackTrace();
                    
                    dd.append(crlf + "IPX requestBillingDirect exception: " + e
                            + "--" + new Date());
                    //billingDao.saveItem(bill);
//                    if (!billingPlanDao.addBillingPlan(bill)) {
//                        billingPlanDao.addBillingPlan(bill);
//                    }
                    if (billingPlanDao.insertBillingPlan(bill) <= 0) {
                        billingPlanDao.insertBillingPlan(bill);
                    }
                }
                //log.info();
                FileUtil.writeRawToFile(debugPath + debugFile, dd.toString()
                        + crlf, true);
                dd.setLength(0);
            }
        });
        executorService.shutdown();
    }
    
    public void requestBillingPlanTrial(final MobileClubBillingPlan bill,
            final SdcMobileClubUser clubUser, final String clubIPXUserName,
            final String clubIPXPassword, final String serviceMetaData) {
        
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String debugPath = "/var/log/pareva/IT/ipxlog/billing_direct";
                String debugFile = "IPX_direct_charged_debug_"
                        + MiscDate.sqlDate.format(new Date()) + ".txt";
                StringBuilder dd = new StringBuilder();
                String crlf = "\r\n";
                
                System.out.println("IPX requestBillingDirect trial runnable "
                        + new Date());
                dd.append(crlf + "IPX requestBillingDirect trial runnable "
                        + new Date());
                
                Date NEXT_DAY_TIME = DateUtils.truncate(DateUtils.addHours(new Date(), 24), Calendar.HOUR);

//                Date NEXT_DATE_ZERO;
//                {
//                    NEXT_DATE_ZERO = getNextDateZero_CHANGED();
//                }                
                try {
                    // bill.setNextPush(NEXT_DAY_TIME);
                    // if (!billingPlanDao.addBillingPlan(bill)) {
                    //     billingPlanDao.addBillingPlan(bill);
                    // }
                    bill.setNextPush(NEXT_DAY_TIME);
                    
//                    if (bill.getNetworkCode().equals("wind")) {
//                        bill.setNextPush(getNextDateZero());
//                    }
                    // 2016.01.13 - AS - WTF?
                    if (billingPlanDao.insertBillingPlan(bill) <= 0) {
                        billingPlanDao.insertBillingPlan(bill);
                    }
                } catch (Exception e) {
                    System.out.println("IPX requestBillingDirect trial exception: "
                            + e + "--" + new Date());
                    e.printStackTrace();
                    dd.append(crlf + "IPX requestBillingDirect trial exception: " + e
                            + "--" + new Date());
                    // if (!billingPlanDao.addBillingPlan(bill)) {
                    //     billingPlanDao.addBillingPlan(bill);
                    // }
                    if (billingPlanDao.insertBillingPlan(bill) <= 0) {
                        billingPlanDao.insertBillingPlan(bill);
                    }
                }
                //log.info();
                FileUtil.writeRawToFile(debugPath + debugFile, dd.toString()
                        + crlf, true);
                dd.setLength(0);
            }
        });
        executorService.shutdown();
    }
    
    public int addTryItem(MobileClubBillingTry btryItem) {
        int stat = 0;
        Date serviceDate = new Date();
        
        if (btryItem.getResponseCode().equals("1001")) {
            btryItem.setResponseCode("200");
        }
        
        String addMobileTriesQuery = "INSERT INTO mobileclubbillingtries"
                + " (aUnique, aLogUnique, aAggregator,"
                + " aStatus, aTransactionId, aResponseRef,"
                + " aResponseCode, aResponseDesc, aCreated,"
                + " aRegionCode, aNetworkCode, aParsedMsisdn,"
                + " aTariffClass, aBillingType, aClubUnique,"
                + " aCampaign, aTicketCreated)" + " VALUES("
                + "'" + btryItem.getUnique() + "', '" + btryItem.getLogUnique() + "', '"
                + btryItem.getAggregator() + "',"
                + "'" + btryItem.getStatus() + "', '" + btryItem.getTransactionId() + "', '"
                + btryItem.getResponseRef() + "',"
                + "'" + btryItem.getResponseCode() + "', '" + btryItem.getResponseDesc()
                + "', '" + MiscDate.toSqlDate(new Date()) + "',"
                + "'" + btryItem.getRegionCode() + "', '" + btryItem.getNetworkCode()
                + "', '" + btryItem.getParsedMsisdn() + "',"
                + "" + btryItem.getTariffClass() + ", '" + btryItem.getBillingType()
                + "', '" + btryItem.getClubUnique() + "','"
                + btryItem.getCampaign() + "', '" + MiscDate.toSqlDate(serviceDate) + "')";
        
        stat = zalog.executeUpdateCPA(addMobileTriesQuery);
        
        return stat;
    }
    
    public int addTryItemAsync(MobileClubBillingTry btryItem) {
        int stat = 0;
        Date serviceDate = new Date();
        if (btryItem.getResponseCode().equals("1001")) {
            btryItem.setResponseCode("200");
        }
        String addMobileTriesQuery = "INSERT INTO mobileClubBillingTriesAsync"
                + " (aUnique, aLogUnique, aAggregator,"
                + " aStatus, aTransactionId, aResponseRef,"
                + " aResponseCode, aResponseDesc, aCreated,"
                + " aRegionCode, aNetworkCode, aParsedMsisdn,"
                + " aTariffClass, aBillingType, aClubUnique,"
                + " aCampaign, aTicketCreated)" + " VALUES("
                + "'" + btryItem.getUnique() + "', '" + btryItem.getLogUnique() + "', '"
                + btryItem.getAggregator() + "',"
                + "'" + btryItem.getStatus() + "', '" + btryItem.getTransactionId() + "', '"
                + btryItem.getResponseRef() + "',"
                + "'" + btryItem.getResponseCode() + "', '" + btryItem.getResponseDesc()
                + "', '" + MiscDate.toSqlDate(new Date()) + "',"
                + "'" + btryItem.getRegionCode() + "', '" + btryItem.getNetworkCode()
                + "', '" + btryItem.getParsedMsisdn() + "',"
                + "" + btryItem.getTariffClass() + ", '" + btryItem.getBillingType()
                + "', '" + btryItem.getClubUnique() + "','"
                + btryItem.getCampaign() + "', '" + MiscDate.toSqlDate(serviceDate) + "')";
        
        stat = zalog.executeUpdateCPA(addMobileTriesQuery);
        
        return stat;
    }
    
    public Calendar getNowTimeITCalendar() {
        Calendar nowTime = GregorianCalendar.getInstance();
        nowTime.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        return nowTime;
    }
    
    public Date getNowTimeIT() {
        Calendar nowTime = getNowTimeITCalendar();
        return nowTime.getTime();
    }
    
    public Date getNextHourZero() {
        Calendar nowTime = getNowTimeITCalendar();
        nowTime.add(Calendar.HOUR, 1);
        nowTime.set(Calendar.MINUTE, 0);
        nowTime.set(Calendar.SECOND, 0);
        return nowTime.getTime();
    }
    
    public Date getNowHour() {
        Calendar nowTime = getNowTimeITCalendar();
        return nowTime.getTime();
    }
    
    public Date getNextFourHour() {
        Calendar nowTime = getNowTimeITCalendar();
        nowTime.add(Calendar.HOUR, 4);
        return nowTime.getTime();
    }
    
    public Date getNextTwelveHour() {
        Calendar nowTime = getNowTimeITCalendar();
        nowTime.add(Calendar.HOUR, 12);
        return nowTime.getTime();
    }
    
    private Date getNextDateZero_CHANGED() {
        Calendar date = new GregorianCalendar();
        date.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        date.add(Calendar.DAY_OF_MONTH, 1);
        return date.getTime();
    }
    
    public Date getNextDateZero() {
        // Calendar date = new GregorianCalendar();
        // date.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        // date.set(Calendar.HOUR_OF_DAY, 0);
        // date.set(Calendar.MINUTE, 0);
        // date.set(Calendar.SECOND, 0);
        // date.set(Calendar.MILLISECOND, 0);
        // date.add(Calendar.DAY_OF_MONTH, 1);
        // return date.getTime();
        Calendar nowTime = getNowTimeITCalendar();
        nowTime.add(Calendar.DATE, 1);
        nowTime.set(Calendar.HOUR, 0);
        nowTime.set(Calendar.MINUTE, 0);
        nowTime.set(Calendar.SECOND, 0);
        return nowTime.getTime();
    }
    
    public Date getQuarterDayTime() {
        
        Calendar nowTime = getNowTimeITCalendar();
        
        if (nowTime.get(Calendar.HOUR_OF_DAY) >= 0
                && nowTime.get(Calendar.HOUR_OF_DAY) < 6) {
            nowTime.set(Calendar.HOUR_OF_DAY, 9);
            nowTime.set(Calendar.MINUTE, 00);
            nowTime.set(Calendar.SECOND, 00);
        } else if (nowTime.get(Calendar.HOUR_OF_DAY) >= 6
                && nowTime.get(Calendar.HOUR_OF_DAY) < 11) {
            nowTime.set(Calendar.HOUR_OF_DAY, 13);
            nowTime.set(Calendar.MINUTE, 00);
            nowTime.set(Calendar.SECOND, 00);
        } else if (nowTime.get(Calendar.HOUR_OF_DAY) >= 11
                && nowTime.get(Calendar.HOUR_OF_DAY) < 16) {
            nowTime.set(Calendar.HOUR_OF_DAY, 17);
            nowTime.set(Calendar.MINUTE, 00);
            nowTime.set(Calendar.SECOND, 00);
        } else {
            nowTime.add(Calendar.DATE, 1);
            nowTime.set(Calendar.HOUR_OF_DAY, 00);
            nowTime.set(Calendar.MINUTE, 00);
            nowTime.set(Calendar.SECOND, 00);
        }
        return nowTime.getTime();
    }
    
    public Date getQuarterDayTimeThree() {
        
        Calendar nowTime = getNowTimeITCalendar();
        
        if (nowTime.get(Calendar.HOUR_OF_DAY) >= 0
                && nowTime.get(Calendar.HOUR_OF_DAY) < 8) {
            nowTime.set(Calendar.HOUR_OF_DAY, 8);
            nowTime.set(Calendar.MINUTE, 00);
            nowTime.set(Calendar.SECOND, 00);
        } else if (nowTime.get(Calendar.HOUR_OF_DAY) >= 8
                && nowTime.get(Calendar.HOUR_OF_DAY) < 11) {
            nowTime.set(Calendar.HOUR_OF_DAY, 11);
            nowTime.set(Calendar.MINUTE, 00);
            nowTime.set(Calendar.SECOND, 00);
        } else if (nowTime.get(Calendar.HOUR_OF_DAY) >= 11
                && nowTime.get(Calendar.HOUR_OF_DAY) < 15) {
            nowTime.set(Calendar.HOUR_OF_DAY, 15);
            nowTime.set(Calendar.MINUTE, 00);
            nowTime.set(Calendar.SECOND, 00);
        } else if (nowTime.get(Calendar.HOUR_OF_DAY) >= 15
                && nowTime.get(Calendar.HOUR_OF_DAY) < 20) {
            nowTime.set(Calendar.HOUR_OF_DAY, 19);
            nowTime.set(Calendar.MINUTE, 00);
            nowTime.set(Calendar.SECOND, 00);
        } else {
            nowTime.add(Calendar.DATE, 1);
            nowTime.set(Calendar.HOUR_OF_DAY, 8);
            nowTime.set(Calendar.MINUTE, 00);
            nowTime.set(Calendar.SECOND, 00);
        }
        return nowTime.getTime();
    }

    // public  Date getHalfDayTime() {
    //     Calendar nowTime = getNowTimeITCalendar();
    //     if (nowTime.get(Calendar.HOUR_OF_DAY) >= 12     && nowTime.get(Calendar.HOUR_OF_DAY) < 23) {
    //         nowTime.add(Calendar.DATE, 1);
    //         nowTime.set(Calendar.HOUR_OF_DAY, 00);
    //         nowTime.set(Calendar.MINUTE, 00);
    //         nowTime.set(Calendar.SECOND, 00);
    //     }else{
    //         nowTime.set(Calendar.HOUR_OF_DAY, 12);
    //         nowTime.set(Calendar.MINUTE, 00);
    //         nowTime.set(Calendar.SECOND, 00);
    //     }
    //     return nowTime.getTime();
    // }
    public Date getHalfDayTime() {
        
        Calendar nowTime = getNowTimeITCalendar();
        
        if (nowTime.get(Calendar.HOUR_OF_DAY) >= 0 && nowTime.get(Calendar.HOUR_OF_DAY) < 12) {
            nowTime.set(Calendar.HOUR_OF_DAY, 12);
            nowTime.set(Calendar.MINUTE, 00);
            nowTime.set(Calendar.SECOND, 00);
        } //else if (duration <= 0 && nowTime.get(Calendar.HOUR_OF_DAY) >= 12     && nowTime.get(Calendar.HOUR_OF_DAY) < 23) {
        else {
            nowTime.add(Calendar.DATE, 1);
            nowTime.set(Calendar.HOUR_OF_DAY, 00);
            nowTime.set(Calendar.MINUTE, 00);
            nowTime.set(Calendar.SECOND, 00);
        }
        return nowTime.getTime();
    }
    // Added 2014-01-06

    public MobileClubBillingTry parseIpxResponse(String resp,
            MobileClubBilling item, String region) {
        
        MobileClubBillingTry btry = new MobileClubBillingTry();

        //For not creating unique key when two threads call this. 
        synchronized (creation) {
            btry.setUnique(Misc.generateUniqueId());
        }
        
        btry.setLogUnique(item.getUnique());
        btry.setAggregator("ipx");
        
        String[] props;
        props = resp.split("--");
        System.out.println("IPX billing response: " + resp);
        
        btry.setStatus(props[0]);
        
        if (props[1].equals("0")) {
            if (item.getNetworkCode().equals("vodafone")
                    || item.getNetworkCode().equals("wind")) {
                btry.setResponseCode("0000");
            } else {
                btry.setResponseCode("0");
            }
        } else {
            btry.setResponseCode(props[1]);
        }
        btry.setTransactionId(props[2]);
        btry.setResponseRef(props[3]);
        btry.setResponseDesc(props[4]);
        btry.setCreated(new Date());

        // Added 2014-01-06 ///////
        btry.setRegionCode(region);
        btry.setNetworkCode(item.getNetworkCode());
        btry.setParsedMsisdn(item.getParsedMobile());
		// /////////////////////////

        // Added 2014-01-08 //
        btry.setTariffClass(item.getTariffClass());
        btry.setBillingType(item.getBillingType());
        btry.setClubUnique(item.getClubUnique());
        btry.setCampaign(item.getCampaign());
        btry.setTicketCreated(item.getCreated());
        // /////////////////////////

        return btry;
    }
    
    public MobileClubBillingTry parseIpxResponse(String resp,
            MobileClubBillingPlan item, String region, String campaign) {
        
        MobileClubBillingTry btry = new MobileClubBillingTry();
        
        synchronized (creation) {
            btry.setUnique(Misc.generateUniqueId());
        }
        btry.setLogUnique(item.getUnique());
        btry.setAggregator("ipx");
        
        String[] props;
        props = resp.split("--");
        System.out.println("IPX billing response: " + resp);
        
        btry.setStatus(props[0]);
        
        if (props[1].equals("0")) {
            if (item.getNetworkCode().equals("vodafone")
                    || item.getNetworkCode().equals("wind")) {
                btry.setResponseCode("000");
            } else {
                btry.setResponseCode("003");
            }
        } else {
            btry.setResponseCode(props[1]);
        }
        btry.setTransactionId(props[2]);
        btry.setResponseRef(props[3]);
        btry.setResponseDesc(props[4]);
        btry.setCreated(new Date());

        // Added 2014-01-06 ///////
        btry.setRegionCode(region);
        btry.setNetworkCode(item.getNetworkCode());
        btry.setParsedMsisdn(item.getParsedMobile());
		// /////////////////////////

        // Added 2014-01-08 //
        btry.setTariffClass(item.getTariffClass());
        btry.setBillingType("club");
        btry.setClubUnique(item.getClubUnique());
        btry.setCampaign(campaign);
        btry.setTicketCreated(new Date());
        // /////////////////////////
        return btry;
    }
}
