package com.neppro.competition.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="domain")
@NamedEntityGraphs({
	@NamedEntityGraph(name = "domainMobileClub", 
			attributeNodes = @NamedAttributeNode("mobileClub"))
})
public class Domain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="id")
	private String id;
    
	@Column(name="name")
	private String name;
    
	@Column(name="defaultUrl")
	private String defaultUrl;
	
	@Column(name="defaultLang")
    private String defaultLang;
	
	@Type(type="yes_no")
	@Column(name="active")
    private boolean active;
	
	@Column(name="created")
    private Timestamp created;

	@Column(name="modified")
	private Timestamp modified;
	
	@Column(name="description")
    private String description;
	
	@Column(name="dispatcherServlet")
    private String dispatcherServlet;
	
	@OneToOne(mappedBy="domain")
	private MobileClub mobileClub;

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

	public String getDefaultUrl() {
		return defaultUrl;
	}

	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}

	public String getDefaultLang() {
		return defaultLang;
	}

	public void setDefaultLang(String defaultLang) {
		this.defaultLang = defaultLang;
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

	public Timestamp getModified() {
		return modified;
	}

	public void setModified(Timestamp modified) {
		this.modified = modified;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDispatcherServlet() {
		return dispatcherServlet;
	}

	public void setDispatcherServlet(String dispatcherServlet) {
		this.dispatcherServlet = dispatcherServlet;
	}

	public MobileClub getMobileClub() {
		return mobileClub;
	}

	public void setMobileClub(MobileClub mobileClub) {
		this.mobileClub = mobileClub;
	}
	
    
}
