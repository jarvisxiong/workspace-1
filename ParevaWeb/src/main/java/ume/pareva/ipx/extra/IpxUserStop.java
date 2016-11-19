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
@Table(name = "ipxUserStop")
public class IpxUserStop implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "aUnique")
    private String unique;

    @Column(name = "aParsedMobile")
    private String parsedMobile;

    @Column(name = "aClubUnique")
    private String clubUnique;

    @Column(name = "aCreated")
    private final Date created = new Date();

    @Column(name = "aUnsubscribed")
    private Date unsubscribed;

    @Column(name = "aExternalId")
    private String externalId;

    @Column(name = "aNetworkCode")
    private String networkCode;

    @Column(name = "aFrom")
    private int from = 0;

    @Column(name = "aStatus")
    private int status = 0;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
    public IpxUserStop() {
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

    public String getClubUnique() {
        return clubUnique;
    }

    public void setClubUnique(String clubUnique) {
        this.clubUnique = clubUnique;
    }

    public Date getUnsubscribed() {
        return unsubscribed;
    }

    public void setUnsubscribed(Date unsubscribed) {
        this.unsubscribed = unsubscribed;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getNetworkCode() {
        return networkCode;
    }

    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }
    
    public Date getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "[unique=<" + unique + ", " + parsedMobile + ","
                + unsubscribed.toString() + "-" + networkCode + ">]";
    }
}
