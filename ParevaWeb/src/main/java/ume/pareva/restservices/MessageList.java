package ume.pareva.restservices;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author madan
 */
@XmlRootElement
public class MessageList implements Serializable {
    
    private String responseCode;
    private String message;
    
    public MessageList(){
        
    }
    
    public MessageList(String responseCode,String message){
        this.responseCode=responseCode;
        this.message=message;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageList{" + "responseCode=" + responseCode + ", message=" + message + '}';
    }
    
    
    
    
}
