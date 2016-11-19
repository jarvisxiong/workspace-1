package ume.pareva.uk;

import java.util.Date;

/**
 *
 * @author madan
 */
public class UKSuccess {
    
    private String aUnique;
    private String tid;
    private String clubUnique;
    private String campaignId;
    private String status;
    private String type;
    private String sid;
    private String aParsedMobile;
    private String aNetworkId;
    private String aHash;
    private Date created;
    private String expiry;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getClubUnique() {
        return clubUnique;
    }

    public void setClubUnique(String clubUnique) {
        this.clubUnique = clubUnique;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getaParsedMobile() {
        return aParsedMobile;
    }

    public void setaParsedMobile(String aParsedMobile) {
        this.aParsedMobile = aParsedMobile;
    }

    public String getaNetworkId() {
        return aNetworkId;
    }

    public void setaNetworkId(String aNetworkId) {
        this.aNetworkId = aNetworkId;
    }

    public String getaHash() {
        return aHash;
    }

    public void setaHash(String aHash) {
        this.aHash = aHash;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getaUnique() {
        return aUnique;
    }

    public void setaUnique(String aUnique) {
        this.aUnique = aUnique;
    }
    
    

    @Override
    public String toString() {
        return "UKSuccess{" + "tid=" + tid + ", clubUnique=" + clubUnique + ", campaignId=" + campaignId + ", status=" + status + ", type=" + type + ", sid=" + sid + ", aParsedMobile=" + aParsedMobile + ", aNetworkId=" + aNetworkId + ", aHash=" + aHash + ", created=" + created + ", expiry=" + expiry + '}';
    }
    
    
    

    
    
    
    
}
