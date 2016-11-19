package com.neppro.localbus.service;

import java.util.List;

import com.neppro.localbus.model.Vehicle;

public interface VehicleService {
	public void saveVehicle(Vehicle vechicle);
	public List<Vehicle> getAllVehicles();
	public List<Vehicle> getAllVehiclesInCategory(String vehicleCategoryId);
	

}
