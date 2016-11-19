package com.neppro.localbus.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;


@Entity(name="committee")
public class Committee implements Serializable  {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "commitee_id")
	private String committeeId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "address")
	private String address;
	
	@Column(name = "contact_no")
	private String contactNo;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="committee")
	private Set<Vehicle> vehicles=new HashSet<Vehicle>();
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="committee")
	private Set<Route> routes=new HashSet<Route>();
	
	
	public String getCommitteeId() {
		return committeeId;
	}

	public void setCommitteeId(String committeeId) {
		this.committeeId = committeeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public Set<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(Set<Route> routes) {
		this.routes = routes;
	}
	
	
		
}
