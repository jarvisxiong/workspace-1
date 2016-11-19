package ume.pareva.restservices;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement
public class SubscriptionList implements Serializable {

    private String msisdn;
    private String club;
    private String responseCode;
    private String message;
    
    public SubscriptionList() {
    }

    
     public SubscriptionList(String msisdn,String club, String message,String responseCode) {
       this.msisdn=msisdn;
       this.club=club;
       this.message=message;
       this.responseCode=responseCode;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public String toString() {
        return "SubscriptionList{" + "msisdn=" + msisdn + ", club=" + club + ", responseCode=" + responseCode + ", message=" + message + '}';
    }
    
    

    

    
    
}