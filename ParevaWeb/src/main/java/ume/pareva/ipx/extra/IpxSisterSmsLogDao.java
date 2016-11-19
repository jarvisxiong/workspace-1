/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.ipx.extra;

/**
 *
 * @author trung
 */
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.dao.SdcMiscDate;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.sdk.Misc;
import ume.pareva.util.ZACPA;

/**
 *
 * @author trung
 */
@Component("ipxsistersmslogdao")

public class IpxSisterSmsLogDao {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    ZACPA zacpalog;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session openSession() {
        return sessionFactory.openSession();
    }

    public IpxSisterSmsLog getSisterSmsLogByUniqueId(String unique) {
        IpxSisterSmsLog item = null;
        String sqlstr = "SELECT * FROM ipxSisterSmsLog"
                + " WHERE aUnique='" + unique + "'";
        try {
            Session session = openSession();
            Transaction trans = session.beginTransaction();
            Query query = session.createSQLQuery(sqlstr);
            query.setMaxResults(1);
            item = new IpxSisterSmsLog((Object[]) query.uniqueResult());
            trans.commit();
            session.close();
        } catch (Exception e) {
            System.out.println("IPX getSisterSmsLogByUniqueId exception: " + e);
            e.printStackTrace();
        }
        return item;
    }

    public IpxSisterSmsLog getSisterSmsLogByMsisdnAndClubUnique(String msisdn, String clubUnique) {
        IpxSisterSmsLog item = null;
        String sqlstr = "SELECT * FROM ipxSisterSmsLog"
                + " WHERE aParsedMobile='" + msisdn + "'"
                + " AND aClubUnique='" + clubUnique + "'";
        try {
            Session session = openSession();
            Transaction trans = session.beginTransaction();
            Query query = session.createSQLQuery(sqlstr);
            query.setMaxResults(1);
            item = new IpxSisterSmsLog((Object[]) query.uniqueResult());
            trans.commit();
            session.close();
        } catch (Exception e) {
            System.out.println("IPX getSisterSmsLogByMsisdnAndClubUnique exception: " + e);
        }
        return item;
    }

    public List<IpxSisterSmsLog> getSisterSmsLogsByPushDate(Date pushDate) {
        List<IpxSisterSmsLog> list = new ArrayList<>();
        try {
            list = getSisterSmsLogsByPushDate(pushDate, 0);
        } catch (Exception e) {
            System.out.println("IPX getSisterSmsLogsByPushDate exception: " + e);
        }
        return list;
    }

    public List<IpxSisterSmsLog> getSisterSmsLogsByPushDate(Date pushDate, int status) {
        List<IpxSisterSmsLog> list = new ArrayList<>();
        IpxSisterSmsLog item;
        String sqlstr = "SELECT * FROM ipxSisterSmsLog"
                + " WHERE aPushDate<='" + SdcMiscDate.toSqlDate(pushDate) + "'"
                + " AND aStatus='" + status + "'";
        try {
            Session session = openSession();
            Transaction trans = session.beginTransaction();
            Query query = session.createSQLQuery(sqlstr);
            List result = query.list();
            for (Object o : result) {
                Object[] row = (Object[]) o;
                item = new IpxSisterSmsLog(row);
                list.add(item);
            }
            trans.commit();
            session.close();
        } catch (Exception e) {
            System.out.println("IPX getSisterSmsLogByMsisdnAndClubUnique exception: " + e);
        }
        return list;
    }

    public int updateSisterSmsLog(IpxSisterSmsLog item) {
        int stat = 0;
        try {
            String sqlstr = "UPDATE ipxSisterSmsLog SET "
                    + " aParsedMobile='" + item.getParsedMobile() + "' "
                    + ", aNetworkCode='" + item.getNetworkCode() + "' "
                    + ", aClubUnique='" + item.getClubUnique() + "' "
                    + ", aStatus='" + item.getStatus() + "' "
                    + ", aPushDate='" + SdcMiscDate.toSqlDate(item.getPushDate()) + "' "
                    + ", aSentDate='" + SdcMiscDate.toSqlDate(item.getSentDate()) + "' "
                    + ", aCreated='" + SdcMiscDate.toSqlDate(item.getCreated()) + "' "
                    + ", aCampaign='" + item.getCampaign() + "' "
                    + ", aMessageId='" + item.getMessageId() + "' "
                    + ", aExternalId='" + item.getExternalId() + "' "
                    + ", aUnsubscribed='" + SdcMiscDate.toSqlDate(item.getUnsubscribed()) + "' "
                    + ", aSubscribed='" + SdcMiscDate.toSqlDate(item.getSubscribed()) + "' "
                    + " WHERE aUnique='" + item.getUnique() + "'";
            stat = zacpalog.executeUpdateCPA(sqlstr);

        } catch (Exception e) {
            System.out.println("IPX updateSisterSmsLog exception: " + e);
            e.printStackTrace();
        }
        return stat;
    }

    public boolean isExist(SdcMobileClubUser item) {
        boolean isExist = false;
        String sqlstr = "";
        Query query = null;
        Session session = openSession();
        Transaction trans = session.beginTransaction();

        sqlstr = "SELECT COUNT(*) as count FROM ipxSisterSmsLog WHERE aParsedMobile = '" + item.getParsedMobile() + "'";
//        System.out.println("isExist: " + sqlstr);
        query = session.createSQLQuery(sqlstr);
        List countList = query.list();

        int count = 0;
        for (Object o : countList) {
            count = Integer.parseInt(o.toString());
        }
        if (count > 0) {
            isExist = true;
        }

        trans.commit();
        session.close();
        return isExist;
    }

    public int addSisterSmsLog(IpxSisterSmsLog item) {
        int stat = 0;

        if (item.getUnique() == null) {
            item.setUnique(Misc.generateUniqueId());
        }
        //inserting into cpalog. 
        String sqlString = "INSERT INTO ipxSisterSmsLog "
                + "(aUnique, aParsedMobile, aNetworkCode, aClubUnique, "
                + " aStatus, aPushDate, aSentDate, aCreated, aCampaign, aMessageId, "
                + " aExternalId, aUnsubscribed, aSubscribed)"
                + " VALUES("
                + "'" + item.getUnique() + "',"
                + "'" + item.getParsedMobile() + "',"
                + "'" + item.getNetworkCode() + "',"
                + "'" + item.getClubUnique() + "',"
                + "'" + item.getStatus() + "',"
                + "'" + SdcMiscDate.toSqlDate(item.getPushDate()) + "',"
                + "'" + SdcMiscDate.toSqlDate(item.getSentDate()) + "',"
                + "'" + SdcMiscDate.toSqlDate(item.getCreated()) + "',"
                + "'" + item.getCampaign() + "',"
                + "'" + item.getMessageId() + "',"
                + "'" + item.getExternalId() + "',"
                + "'" + SdcMiscDate.toSqlDate(item.getUnsubscribed()) + "',"
                + "'" + SdcMiscDate.toSqlDate(item.getSubscribed()) + "')";

//        System.out.println("sqlString: " + sqlString);
        try {
            Session session = openSession();
            Transaction trans = session.beginTransaction();
            Query query = session.createSQLQuery(sqlString);
            stat = query.executeUpdate();
            trans.commit();
            session.close();

        } catch (Exception e) {
            System.out.println("IPX updateSisterSmsLog exception: " + e);
            e.printStackTrace();
        }
        return stat;
    }

    public void addSisterSmsLog(SdcMobileClubUser clubUser) {
        IpxSisterSmsLog item = new IpxSisterSmsLog();
        Calendar nowTime = Calendar.getInstance();
        if (clubUser.getParsedMobile().startsWith("39")) {
            nowTime.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        } else if (clubUser.getParsedMobile().startsWith("34")) {
            nowTime.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
        }
        nowTime.add(java.util.Calendar.DATE, 7);
        nowTime.set(java.util.Calendar.HOUR, 19);
        nowTime.set(java.util.Calendar.MINUTE, 30);
        nowTime.set(java.util.Calendar.SECOND, 00);

        item.setClubUnique(clubUser.getClubUnique());
        item.setParsedMobile(clubUser.getParsedMobile());
        item.setNetworkCode(clubUser.getNetworkCode());
        item.setStatus(0);
        item.setPushDate(nowTime.getTime());
        item.setSentDate(new Date(0));
        item.setCampaign(clubUser.getCampaign());
        item.setMessageId("");
        item.setMessageId("");
        item.setExternalId("");
        item.setUnsubscribed(clubUser.getUnsubscribed());
        item.setSubscribed(clubUser.getSubscribed());
        addSisterSmsLog(item);
    }
}
