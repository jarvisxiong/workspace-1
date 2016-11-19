package com.neppro.competition.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="user")
@NamedEntityGraphs({
	@NamedEntityGraph(name = "userPrivileges", 
			attributeNodes = @NamedAttributeNode("privileges")),
	@NamedEntityGraph(name = "userClubDetails", 
            attributeNodes = @NamedAttributeNode(value = "mobileClubSubscribers", subgraph = "userMobileClubs"), 
            subgraphs = @NamedSubgraph(name = "userMobileClubs", attributeNodes = @NamedAttributeNode("mobileClub")))
})
public class User implements Serializable {
		
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "user_name")
	private String username;
	
	@Column(name = "password")
	private String password;

	@Column(name="password_salt")
	private String passwordSalt;
	
	@Column(name="msisdn")
	private String msisdn;
	
	@JsonIgnore
	@ManyToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinTable(name = "user_privilege", 
				joinColumns = {@JoinColumn(name = "user_id", nullable = false, updatable = false)}, 
				inverseJoinColumns = { @JoinColumn(name = "privilege_id",nullable = false, updatable = false)})
	private Set<Privilege> privileges;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="user",fetch=FetchType.LAZY)
	private Set<MobileClubSubscriber> mobileClubSubscribers;
	
	public Set<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Set<Privilege> privileges) {
		this.privileges = privileges;
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

	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	public Set<MobileClubSubscriber> getMobileClubSubscribers() {
		return mobileClubSubscribers;
	}

	public void setMobileClubSubscribers(
			Set<MobileClubSubscriber> mobileClubSubscribers) {
		this.mobileClubSubscribers = mobileClubSubscribers;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	
}
