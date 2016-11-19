package com.neppro.localbus.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity(name="vehicle_category")
public class VehicleCategory implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "category_id")
	private String categoryId;
	
	@Column(name = "name")
	@Size(min=2,max=10)
	private String name;
	
	@XmlTransient
	@JsonIgnore
	@OneToMany(mappedBy="vehicleCategory",cascade=CascadeType.ALL)
    private Set<Vehicle> vehicles;
	
	public VehicleCategory(){}
	
	public VehicleCategory(String categoryId){
		this.categoryId=categoryId;
	}
	
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Vehicle> getVehicles() {
		return vehicles;
	}

	public void setVehicles(Set<Vehicle> vehicles) {
		this.vehicles = vehicles;
	}
	
}
