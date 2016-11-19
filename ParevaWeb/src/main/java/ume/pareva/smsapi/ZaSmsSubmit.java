package ume.pareva.smsapi;

import java.util.Date;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.dao.UmeSessionParameters;

/**
 *
 * @author madan
 */
public class ZaSmsSubmit extends SdcSmsSubmit {
    private String title = "";
    private String url = "";
    boolean billingMsg = false;
    private String subType = "";
    private Date subDate = null;
    private int campaignId = 0;
    private String serviceGuid = "";
    
    public ZaSmsSubmit(){}
    
    public ZaSmsSubmit(SdcRequest sReq) { super(sReq); }
    public ZaSmsSubmit(UmeSessionParameters sReq) { super(sReq); }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public boolean getBillingMsg() { return billingMsg; }
    public void setBillingMsg(boolean billingMsg) { this.billingMsg = billingMsg; }

    public String getSubType() { return subType; }
    public void setSubType(String subType) { this.subType = subType; }

    public Date getSubDate() { return subDate; }
    public void setSubDate(Date subDate) { this.subDate = subDate; }

    public int getCampaignId() { return campaignId; }
    public void setCampaignId(int campaignId) { this.campaignId = campaignId; }
    
    public String getServiceGuid() { return serviceGuid; }
    public void setServiceGuid(String serviceGuid) { this.serviceGuid = serviceGuid; }

    @Override
    public String toString() {
        return "ZaSmsSubmit{" + "title=" + title + ", url=" + url + ", billingMsg=" + billingMsg + ", subType=" + subType + ", subDate=" + subDate + ", campaignId=" + campaignId + ", serviceGuid=" + serviceGuid + '}';
    }
    
    
}
