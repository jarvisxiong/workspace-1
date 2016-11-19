package com.neppro.localbus.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name="vehicle")
public class Vehicle implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "registration_no")
	private String registrationNo;
	
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="category_id")
    private VehicleCategory vehicleCategory;
	
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="commitee_id")
    private Committee committee;
	
	public Vehicle(){}
	
	public Vehicle(String registrationNo){
		this.registrationNo=registrationNo;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public VehicleCategory getVehicleCategory() {
		return vehicleCategory;
	}


	public void setVehicleCategory(VehicleCategory vehicleCategory) {
		this.vehicleCategory = vehicleCategory;
	}

	public Committee getCommittee() {
		return committee;
	}

	public void setCommittee(Committee committee) {
		this.committee = committee;
	}	
	
	
}
