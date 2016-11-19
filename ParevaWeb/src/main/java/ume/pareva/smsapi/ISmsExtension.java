package ume.pareva.smsapi;

/**
 *
 * @author madan
 */
public interface ISmsExtension {
     public void handOff(Object sms) throws InterruptedException;
    public String getResponse() throws InterruptedException;  
    
}
