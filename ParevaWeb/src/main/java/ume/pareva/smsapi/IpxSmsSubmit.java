package ume.pareva.smsapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.SdcSmsSubmit;

import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.SdcSmsMessage;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;


public class IpxSmsSubmit extends SdcSmsSubmit {

    String title = "";
    String url = "";
    boolean billingMsg = false;
    String subType = "";
    Date subDate = null;

    int campaignId = 0;
    String serviceGuid = "";
    String userDataHeader = "";
    String serviceCategory = "";
    String referenceID = "";
    
    String serviceMetaData = "";

    String username = "";
	String password = "";
    
    protected SdcSmsMessage sdcsms = null;
    protected UmeUser sdcuser = null;
    protected UmeDomain sdcdomain = null;
    protected SdcService sdcservice = null;

    List<String> msgList = new ArrayList<String>();
    List<String> toList = new ArrayList<String>();
    
    String userUnique = "";
    
    public IpxSmsSubmit(){}
    
    public IpxSmsSubmit(SdcRequest sReq) { initRequest(sReq); }
    
    public void initRequest(SdcRequest sReq) {
        
        setSmsMessage(sReq.getSmsMessage());
        setUmeUser(sReq.getUser());
        setSdcDomain(sReq.getDomain());
        setSdcService(sReq.getService());

        if (getSmsMessage()!=null) {
            setRefMessageUnique(sdcsms.getUnique());
            setFromNumber(sdcsms.getToNumber());
            setSmsAccount(sdcsms.getSmsAccount());
        }

        setUnique(SdcMisc.generateUniqueId());
        setMsgType("text");;
    }
    
    public UmeDomain getSdcDomain() { return sdcdomain; }
    public void setSdcDomain(UmeDomain sdcdomain) { this.sdcdomain = sdcdomain; }

    public SdcService getSdcService() { return sdcservice; }
    public void setSdcService(SdcService sdcservice) { this.sdcservice = sdcservice; }

    public List<String> getMsgList() { return msgList; }
    public void setMsgList(List<String> msgList) { this.msgList = msgList; }

    public List<String> getToList() { return toList; }
    public void setToList(List<String> toList) { this.toList = toList; }

    public String getUserUnique() { return userUnique; }
    public void setUserUnique(String userUnique) { this.userUnique = userUnique; }  
    
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

    public String getUserDataHeader() { return userDataHeader; }
    public void setUserDataHeader(String userDataHeader) { this.userDataHeader = userDataHeader; }

    public String getServiceCategory() { return this.serviceCategory; }
    public void setServiceCategory(String serviceCategory) { this.serviceCategory = serviceCategory; }
    
    public String getReferenceID() { return this.referenceID; }
    public void setReferenceID(String referenceID) { this.referenceID = referenceID; }
    
    public String getServiceMetaData() {
        return serviceMetaData;
    }

    public void setServiceMetaData(String serviceMetaData) {
        this.serviceMetaData = serviceMetaData;
    }
    
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
