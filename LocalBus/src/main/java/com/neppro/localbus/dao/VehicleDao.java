package com.neppro.localbus.dao;

import java.util.List;

import com.neppro.localbus.model.Vehicle;

public interface VehicleDao extends GenericDao<Vehicle, String>{
	public List<Vehicle> getAllVehiclesInCategory(String vehicleCategoryId);
}


