package com.neppro.localbus.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.neppro.localbus.model.VehicleCategory;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VehicleCategoryList {
	
	private Fault fault;
	private List<VehicleCategory> vehicleCategories;

	public List<VehicleCategory> getVehicleCategories() {
		return vehicleCategories;
	}

	public void setVehicleCategories(List<VehicleCategory> vehicleCategories) {
		this.vehicleCategories = vehicleCategories;
	}

	public Fault getFault() {
		return fault;
	}

	public void setFault(Fault fault) {
		this.fault = fault;
	}
}
