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
@Component("ipxbroadcastsmslogdao")

public class IpxBroadcastSmsLogDao {

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

    public IpxBroadcastSmsLog getBroadcastSmsLogByUniqueId(String unique) {
        IpxBroadcastSmsLog item = null;
        String sqlstr = "SELECT * FROM ipxBroadcastSmsLog"
                + " WHERE aUnique='" + unique + "'";
        try {
            Session session = openSession();
            Transaction trans = session.beginTransaction();
            Query query = session.createSQLQuery(sqlstr);
            query.setMaxResults(1);
            item = new IpxBroadcastSmsLog((Object[]) query.uniqueResult());
            trans.commit();
            session.close();
        } catch (Exception e) {
            System.out.println("IPX getBroadcastSmsLogByUniqueId exception: " + e);
            e.printStackTrace();
        }
        return item;
    }

    public IpxBroadcastSmsLog getBroadcastSmsLogByMsisdnAndClubUnique(String msisdn, String clubUnique) {
        IpxBroadcastSmsLog item = null;
        String sqlstr = "SELECT * FROM ipxBroadcastSmsLog"
                + " WHERE aParsedMobile='" + msisdn + "'"
                + " AND aClubUnique='" + clubUnique + "'";
        try {
            Session session = openSession();
            Transaction trans = session.beginTransaction();
            Query query = session.createSQLQuery(sqlstr);
            query.setMaxResults(1);
            item = new IpxBroadcastSmsLog((Object[]) query.uniqueResult());
            trans.commit();
            session.close();
        } catch (Exception e) {
            System.out.println("IPX getBroadcastSmsLogByMsisdnAndClubUnique exception: " + e);
        }
        return item;
    }

    public List<IpxBroadcastSmsLog> getBroadcastSmsLogsByCreatedDate(Date createdDate) {
        List<IpxBroadcastSmsLog> list = new ArrayList<>();
        try {
            list = getBroadcastSmsLogsByCreatedDate(createdDate, 0);
        } catch (Exception e) {
            System.out.println("IPX getBroadcastSmsLogsByPushDate exception: " + e);
        }
        return list;
    }

    public List<IpxBroadcastSmsLog> getBroadcastSmsLogsByCreatedDate(Date createdDate, int status) {
        List<IpxBroadcastSmsLog> list = new ArrayList<>();
        IpxBroadcastSmsLog item;
        String sqlstr = "SELECT * FROM ipxBroadcastSmsLog"
                + " WHERE aCreated<='" + SdcMiscDate.toSqlDate(createdDate) + "'"
                + " AND aStatus='" + status + "'";
        try {
            Session session = openSession();
            Transaction trans = session.beginTransaction();
            Query query = session.createSQLQuery(sqlstr);
            List result = query.list();
            for (Object o : result) {
                Object[] row = (Object[]) o;
                item = new IpxBroadcastSmsLog(row);
                list.add(item);
            }
            trans.commit();
            session.close();
        } catch (Exception e) {
            System.out.println("IPX getBroadcastSmsLogsByCreatedDate exception: " + e);
        }
        return list;
    }

    public int updateBroadcastSmsLog(IpxBroadcastSmsLog item) {
        int stat = 0;
        try {
            String sqlstr = "UPDATE ipxBroadcastSmsLog SET "
                    + " aParsedMobile='" + item.getParsedMobile() + "' "
                    + ", aNetworkCode='" + item.getNetworkCode() + "' "
                    + ", aClubUnique='" + item.getClubUnique() + "' "
                    + ", aStatus='" + item.getStatus() + "' "
                    + ", aCreated='" + SdcMiscDate.toSqlDate(item.getCreated()) + "' "
                    + ", aCampaign='" + item.getCampaign() + "' "
                    + ", aMessageId='" + item.getMessageId() + "' "
                    + ", aExternalId='" + item.getExternalId() + "' "
                    + " WHERE aUnique='" + item.getUnique() + "'";
            stat = zacpalog.executeUpdateCPA(sqlstr);

        } catch (Exception e) {
            System.out.println("IPX updateBroadcastSmsLog exception: " + e);
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

        sqlstr = "SELECT COUNT(*) as count FROM ipxBroadcastSmsLog WHERE aParsedMobile = '" + item.getParsedMobile() + "'";
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

    public int addBroadcastSmsLog(IpxBroadcastSmsLog item) {
        int stat = 0;

        if (item.getUnique() == null) {
            item.setUnique(Misc.generateUniqueId());
        }
        //inserting into cpalog. 
        String sqlString = "INSERT INTO ipxBroadcastSmsLog "
                + "(aUnique, aParsedMobile, aNetworkCode, aClubUnique, "
                + " aStatus, aPushDate, aSentDate, aCreated, aCampaign, aMessageId, "
                + " aExternalId, aUnsubscribed, aSubscribed)"
                + " VALUES("
                + "'" + item.getUnique() + "',"
                + "'" + item.getParsedMobile() + "',"
                + "'" + item.getNetworkCode() + "',"
                + "'" + item.getClubUnique() + "',"
                + "'" + item.getStatus() + "',"
                + "'" + SdcMiscDate.toSqlDate(item.getCreated()) + "',"
                + "'" + item.getCampaign() + "',"
                + "'" + item.getMessageId() + "',"
                + "'" + item.getExternalId() + "')";

//        System.out.println("sqlString: " + sqlString);
        try {
            Session session = openSession();
            Transaction trans = session.beginTransaction();
            Query query = session.createSQLQuery(sqlString);
            stat = query.executeUpdate();
            trans.commit();
            session.close();

        } catch (Exception e) {
            System.out.println("IPX updateBroadcastSmsLog exception: " + e);
            e.printStackTrace();
        }
        return stat;
    }

    public void addBroadcastSmsLog(SdcMobileClubUser clubUser) {
        IpxBroadcastSmsLog item = new IpxBroadcastSmsLog();
        item.setClubUnique(clubUser.getClubUnique());
        item.setParsedMobile(clubUser.getParsedMobile());
        item.setNetworkCode(clubUser.getNetworkCode());
        item.setStatus(0);
        item.setCampaign(clubUser.getCampaign());
        item.setMessageId("");
        item.setMessageId("");
        item.setExternalId("");
        addBroadcastSmsLog(item);
    }
}

