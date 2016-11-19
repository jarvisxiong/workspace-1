package ume.pareva.ipx.extra;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author trung
 */
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "ipxBroadcastSmsLog")
public class IpxBroadcastSmsLog implements java.io.Serializable {
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

    @Column(name = "aCreated")
    private final Date created = new Date();
    
    @Column(name = "aCampaign")
    private String campaign;
    
    @Column(name = "aMessageId")
    private String messageId;
    
    @Column(name = "aExternalId")
    private String externalId;
    

    public IpxBroadcastSmsLog() {
    }
    
    public IpxBroadcastSmsLog(String unique, String parsedMobile, String networkCode, String clubUnique, int status, String campaign, String messageId, String externalId) {
        this.unique = unique;
        this.parsedMobile = parsedMobile;
        this.networkCode = networkCode;
        this.clubUnique = clubUnique;
        this.status = status;
        this.campaign = campaign;
        this.messageId = messageId;
        this.externalId = externalId;

    }
    
    public IpxBroadcastSmsLog(Object[] rawData) {
        //Integer.parseInt(String.valueOf(row[9])
        this.unique = ((String) rawData[0]);
        this.parsedMobile = ((String) rawData[1]);
        this.networkCode = ((String) rawData[2]);
        this.clubUnique = ((String) rawData[3]);
        this.status = Integer.parseInt(String.valueOf(rawData[4]));
        this.campaign = ((String) rawData[6]);
        this.messageId = ((String) rawData[7]);
        this.externalId = ((String) rawData[8]);
    }
    

//    @Override
//    public String toString() {
//        return "IpxSisterSmsLog{" + "unique=" + unique + ", parsedMobile=" + parsedMobile + ", networkCode=" + networkCode + ", clubUnique=" + clubUnique + ", status=" + status + ", pushDate=" + pushDate + ", sentDate=" + sentDate + ", created=" + created + ", campaign=" + campaign + ", messageId=" + messageId + ", externalId=" + externalId + ", subscribed=" + subscribed + ", unsubscribed=" + unsubscribed + '}';
//    }
    @Override
    public String toString() {
        return "IpxSisterSmsLog{" + "unique=" + unique + ", parsedMobile=" + parsedMobile + ", networkCode=" + networkCode + ", clubUnique=" + clubUnique + ", status=" + status  + ", messageId=" + messageId + ", externalId=" + externalId + '}';
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
    
    public Date getCreated() {
        return created;
    }
    
}
