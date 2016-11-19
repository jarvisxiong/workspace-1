package ume.pareva.restservices;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement
public class ParsedList implements Serializable {

    private int id;
    
    private String msisdn;
    
    public ParsedList() {
    }

    
     public ParsedList(int id,String msisdn) {
       this.id=id;
       this.msisdn=msisdn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    @Override
    public String toString() {
        return "ParsedList{" + "id=" + id + ", msisdn=" + msisdn + '}';
    }
     
 
    
    
}