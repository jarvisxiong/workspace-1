package ume.pareva.smsapi;


import java.util.ArrayList;
import java.util.List;

import ume.pareva.dao.SdcMisc;
import ume.pareva.dao.SdcRequest;
import ume.pareva.dao.SdcSmsMessage;
import ume.pareva.dao.SdcSmsSubmit;
import ume.pareva.pojo.SdcService;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;



public class IpxBillingEsSubmit extends SdcSmsSubmit {

    protected SdcSmsMessage sdcsms = null;
    protected UmeUser sdcuser = null;
    protected UmeDomain sdcdomain = null;
    protected SdcService sdcservice = null;

    List<String> msgList = new ArrayList<String>();
    List<String> toList = new ArrayList<String>();

    String userUnique = "";
    String reqType = "submit";
    String refMessageUnique = "";
    String tariffClassIPX = "";
    int serviceDesc = 1;
    String appId = "";
    double cost = 0;
    boolean isTotalCost = false;
    boolean useDefaultAccount = false;
    boolean useDefaultAccount2 = false;
    
    String schedule = "now";
    String currencyCode = "";
    
    String correlationId = "";  
    String clientIPAddress ="";

    String returnURL = "";
    String serviceName = "";
    String serviceId = "";

	String serviceCategory = "";
    String serviceMetaData = "";
    String language = "";
    String campaignName = "";
    String username = "";
    String password = "";
    String transactionId = "";
    String consumerId = "";
    String operatorCode = "";
    String operator = "";
    String subscriptionId = "";
    
        
    public IpxBillingEsSubmit() {}

    public IpxBillingEsSubmit(SdcRequest sReq) { initRequest(sReq); }
    
    public void initRequest(SdcRequest sReq) {
        
        setSmsMessage(sReq.getSmsMessage());
        setSdcUser(sReq.getUser());
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

    public SdcSmsMessage getSmsMessage() { return sdcsms; }
    public void setSmsMessage(SdcSmsMessage sdcsms) { this.sdcsms = sdcsms; }

    public UmeUser getSdcUser() { return sdcuser; }
    public void setSdcUser(UmeUser sdcuser) {
        this.sdcuser = sdcuser;
        if (sdcuser!=null) userUnique = sdcuser.getUnique();
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

    public String getReqType() { return reqType; }
    public void setReqType(String reqType) { this.reqType = reqType; }
    
    public String getRefMessageUnique() { return refMessageUnique; }
    public void setRefMessageUnique(String refMessageUnique) { this.refMessageUnique = refMessageUnique; }

    //public String getTariffClass() { return tariffClass; }
    public String getTariffClassIPX() { return this.tariffClassIPX; }

    public void setTariffClassIPX(String tariffClass) { this.tariffClassIPX = tariffClass; }

    public int getServiceDesc() { return serviceDesc; }
    public void setServiceDesc(int serviceDesc) { this.serviceDesc = serviceDesc; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public boolean getIsTotalCost() { return isTotalCost; }
    public void setIsTotalCost(boolean isTotalCost) { this.isTotalCost = isTotalCost; }

    public boolean getUseDefaultAccount() { return useDefaultAccount; }
    public void setUseDefaultAccount(boolean useDefaultAccount) { this.useDefaultAccount = useDefaultAccount; }

    public boolean getUseDefaultAccount2() { return useDefaultAccount2; }
    public void setUseDefaultAccount2(boolean useDefaultAccount2) { this.useDefaultAccount2 = useDefaultAccount2; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    
    /**
     * Gets the correlationId value for this IpxBillingSubmit.
     * 
     * @return correlationId
     */
    public java.lang.String getCorrelationId() {
        return correlationId;
    }


    /**
     * Sets the correlationId value for this IpxBillingSubmit.
     * 
     * @param correlationId
     */
    public void setCorrelationId(java.lang.String correlationId) {
        this.correlationId = correlationId;
    }
    /**
     * Gets the subscriptionId value for this IpxBillingSubmit.
     * 
     * @return subscriptionId
     */
    public java.lang.String getSubscriptionId() {
        return subscriptionId;
    }


    /**
     * Sets the subscriptionId value for this IpxBillingSubmit.
     * 
     * @param subscriptionId
     */
    public void setSubscriptionId(java.lang.String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }


    /**
     * Gets the clientIPAddress value for this IpxBillingSubmit.
     * 
     * @return clientIPAddress
     */
    public java.lang.String getClientIPAddress() {
        return clientIPAddress;
    }


    /**
     * Sets the clientIPAddress value for this IpxBillingSubmit.
     * 
     * @param clientIPAddress
     */
    public void setClientIPAddress(java.lang.String clientIPAddress) {
        this.clientIPAddress = clientIPAddress;
    }


    /**
     * Gets the returnURL value for this IpxBillingSubmit.
     * 
     * @return returnURL
     */
    public java.lang.String getReturnURL() {
        return returnURL;
    }


    /**
     * Sets the returnURL value for this IpxBillingSubmit.
     * 
     * @param returnURL
     */
    public void setReturnURL(java.lang.String returnURL) {
        this.returnURL = returnURL;
    }

    public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
    
    /**
     * Gets the serviceName value for this IpxBillingSubmit.
     * 
     * @return serviceName
     */
    public java.lang.String getServiceName() {
        return serviceName;
    }


    /**
     * Sets the serviceName value for this IpxBillingSubmit.
     * 
     * @param serviceName
     */
    public void setServiceName(java.lang.String serviceName) {
        this.serviceName = serviceName;
    }


    /**
     * Gets the serviceCategory value for this IpxBillingSubmit.
     * 
     * @return serviceCategory
     */
    public java.lang.String getServiceCategory() {
        return serviceCategory;
    }


    /**
     * Sets the serviceCategory value for this IpxBillingSubmit.
     * 
     * @param serviceCategory
     */
    public void setServiceCategory(java.lang.String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }


    /**
     * Gets the serviceMetaData value for this IpxBillingSubmit.
     * 
     * @return serviceMetaData
     */
    public java.lang.String getServiceMetaData() {
        return serviceMetaData;
    }


    /**
     * Sets the serviceMetaData value for this IpxBillingSubmit.
     * 
     * @param serviceMetaData
     */
    public void setServiceMetaData(java.lang.String serviceMetaData) {
        this.serviceMetaData = serviceMetaData;
    }


    /**
     * Gets the language value for this IpxBillingSubmit.
     * 
     * @return language
     */
    public java.lang.String getLanguage() {
        return language;
    }


    /**
     * Sets the language value for this IpxBillingSubmit.
     * 
     * @param language
     */
    public void setLanguage(java.lang.String language) {
        this.language = language;
    }


    /**
     * Gets the campaignName value for this IpxBillingSubmit.
     * 
     * @return campaignName
     */
    public java.lang.String getCampaignName() {
        return campaignName;
    }


    /**
     * Sets the campaignName value for this IpxBillingSubmit.
     * 
     * @param campaignName
     */
    public void setCampaignName(java.lang.String campaignName) {
        this.campaignName = campaignName;
    }


    /**
     * Gets the username value for this IpxBillingSubmit.
     * 
     * @return username
     */
    public java.lang.String getUsername() {
        return username;
    }


    /**
     * Sets the username value for this IpxBillingSubmit.
     * 
     * @param username
     */
    public void setUsername(java.lang.String username) {
        this.username = username;
    }


    /**
     * Gets the password value for this IpxBillingSubmit.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this IpxBillingSubmit.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }
     
    /**
     * Gets the transactionId value for this IpxBillingSubmit.
     * 
     * @return transactionId
     */
    public java.lang.String getTransactionId() {
        return transactionId;
    }


    /**
     * Sets the transactionId value for this IpxBillingSubmit.
     * 
     * @param transactionId
     */
    public void setTransactionId(java.lang.String transactionId) {
        this.transactionId = transactionId;
    }
    
    /**
     * Gets the consumerId value for this IpxBillingSubmit.
     * 
     * @return consumerId
     */
    public String getConsumerId() {
        return consumerId;
    }
    /**
     * Sets the consumerId value for this IpxBillingSubmit.
     * 
     * @param consumerId
     */
    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }
    /**
     * Gets the operatorCode value for this IpxBillingSubmit.
     * 
     * @return operatorCode
     */
    public String getOperatorCode() {
        return operatorCode;
    }
    /**
     * Sets the operatorCode value for this IpxBillingSubmit.
     * 
     * @param operatorCode
     */
    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }
}
