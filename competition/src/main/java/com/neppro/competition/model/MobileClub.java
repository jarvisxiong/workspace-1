package com.neppro.competition.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="mobile_club")
@NamedEntityGraphs({
	@NamedEntityGraph(name = "mobileClubSubscribers", 
			attributeNodes = @NamedAttributeNode("mobileClubSubscribers")),
	@NamedEntityGraph(name = "mobileClubCompetition", 
			attributeNodes = @NamedAttributeNode("competition")),
	@NamedEntityGraph(name = "mobileClubDomain", 
			attributeNodes = @NamedAttributeNode("domain")),
	@NamedEntityGraph(name = "mobileClubMarket", 
			attributeNodes = @NamedAttributeNode("market")),
	@NamedEntityGraph(name = "mobileClubService", 
	attributeNodes = @NamedAttributeNode("service"))
})
public class MobileClub implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="id")
	private String id;
	
	@Column(name="name")
	private String name = "";
	
	@Column(name="from_shortcode")
	private String fromShortcode= "";
	
	@Column(name="to_shortcode")
	private String toShortcode= "";

	@Type(type="yes_no")
	@Column(name="active")
	private boolean active;
	
	@Column(name="created")
	private Timestamp created;
	
	@Column(name="currency")
	private String currency;
	
	@Column(name="web_confirmation")
	private String webConfirmation;
	
	@Column(name="welcome_sms")
	private String welcomeSms;
	
	@Column(name="reminder_sms")
	private String reminderSms = "";
	
	@Column(name="stop_sms")
	private String stopSms;
	
	@OneToOne
	@JoinColumn(name="domain_id")
	private Domain domain;
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="market_id")
    private Market market;

	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="service_id")
    private Service service;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="mobileClub")
	private Set<MobileClubSubscriber> mobileClubSubscribers;
	
	@OneToOne(mappedBy="mobileClub",fetch=FetchType.LAZY)
	private Competition competition;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFromShortcode() {
		return fromShortcode;
	}

	public void setFromShortcode(String fromShortcode) {
		this.fromShortcode = fromShortcode;
	}

	public String getToShortcode() {
		return toShortcode;
	}

	public void setToShortcode(String toShortcode) {
		this.toShortcode = toShortcode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getWebConfirmation() {
		return webConfirmation;
	}

	public void setWebConfirmation(String webConfirmation) {
		this.webConfirmation = webConfirmation;
	}

	public String getWelcomeSms() {
		return welcomeSms;
	}

	public void setWelcomeSms(String welcomeSms) {
		this.welcomeSms = welcomeSms;
	}

	public String getReminderSms() {
		return reminderSms;
	}

	public void setReminderSms(String reminderSms) {
		this.reminderSms = reminderSms;
	}

	public String getStopSms() {
		return stopSms;
	}

	public void setStopSms(String stopSms) {
		this.stopSms = stopSms;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public Market getMarket() {
		return market;
	}

	public void setMarket(Market market) {
		this.market = market;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public Set<MobileClubSubscriber> getMobileClubSubscribers() {
		return mobileClubSubscribers;
	}

	public void setMobileClubSubscribers(
			Set<MobileClubSubscriber> mobileClubSubscribers) {
		this.mobileClubSubscribers = mobileClubSubscribers;
	}

	public Competition getCompetition() {
		return competition;
	}

	public void setCompetition(Competition competition) {
		this.competition = competition;
	}
		
}
