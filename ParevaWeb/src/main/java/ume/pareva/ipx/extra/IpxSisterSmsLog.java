/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ume.pareva.ipx.extra;


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author trung
 */
@Entity
@Table(name = "ipxSisterSmsLog")
public class IpxSisterSmsLog implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "aUnique")
    private String unique;

    @Column(name = "aParsedMobile")
    private String parsedMobile;
    
    @Column(name = "aNetworkCode")
    private String networkCode;
    
    @Column(name = "aClubUnique")
    private String clubUnique;
    
    @Column(name = "aStatus")
    private int status = 0;
        
    @Column(name = "aPushDate")
    private Date pushDate;

    @Column(name = "aSentDate")
    private Date sentDate;

    @Column(name = "aCreated")
    private final Date created = new Date();
    
    @Column(name = "aCampaign")
    private String campaign;
    
    @Column(name = "aMessageId")
    private String messageId;
    
    @Column(name = "aExternalId")
    private String externalId;
    
    @Column(name = "aSubscribed")
    private Date subscribed;
    
    @Column(name = "aUnsubscribed")
    private Date unsubscribed;

    public IpxSisterSmsLog() {
    }
    
    public IpxSisterSmsLog(String unique, String parsedMobile, String networkCode, String clubUnique, int status, Date pushDate, Date sentDate, String campaign, String messageId, String externalId, Date unsubscribed, Date subscribed) {
        this.unique = unique;
        this.parsedMobile = parsedMobile;
        this.networkCode = networkCode;
        this.clubUnique = clubUnique;
        this.status = status;
        this.pushDate = pushDate;
        this.sentDate = sentDate;
        this.campaign = campaign;
        this.messageId = messageId;
        this.externalId = externalId;
        this.unsubscribed = unsubscribed;
        this.subscribed = subscribed;
    }
    
    public IpxSisterSmsLog(Object[] rawData) {
        //Integer.parseInt(String.valueOf(row[9])
        this.unique = ((String) rawData[0]);
        this.parsedMobile = ((String) rawData[1]);
        this.networkCode = ((String) rawData[2]);
        this.clubUnique = ((String) rawData[3]);
        this.status = Integer.parseInt(String.valueOf(rawData[4]));
        this.pushDate = ((Date) rawData[5]);
        this.sentDate = ((Date) rawData[6]);
        this.campaign = ((String) rawData[8]);
        this.messageId = ((String) rawData[9]);
        this.externalId = ((String) rawData[10]);
        this.unsubscribed = ((Date) rawData[11]);
        this.subscribed = ((Date) rawData[12]);
    }
    

    @Override
    public String toString() {
        return "IpxSisterSmsLog{" + "unique=" + unique + ", parsedMobile=" + parsedMobile + ", networkCode=" + networkCode + ", clubUnique=" + clubUnique + ", status=" + status + ", pushDate=" + pushDate + ", sentDate=" + sentDate + ", created=" + created + ", campaign=" + campaign + ", messageId=" + messageId + ", externalId=" + externalId + ", subscribed=" + subscribed + ", unsubscribed=" + unsubscribed + '}';
    }

    
    
    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public String getParsedMobile() {
        return parsedMobile;
    }

    public void setParsedMobile(String parsedMobile) {
        this.parsedMobile = parsedMobile;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    public String getClubUnique() {
        return clubUnique;
    }

    public void setClubUnique(String clubUnique) {
        this.clubUnique = clubUnique;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getPushDate() {
        return pushDate;
    }

    public void setPushDate(Date pushDate) {
        this.pushDate = pushDate;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Date getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Date subscribed) {
        this.subscribed = subscribed;
    }

    public Date getUnsubscribed() {
        return unsubscribed;
    }

    public void setUnsubscribed(Date unsubscribed) {
        this.unsubscribed = unsubscribed;
    }
    
    public Date getCreated() {
        return created;
    }
    
}
