package com.neppro.localbus.service;

import java.util.List;

import com.neppro.localbus.model.VehicleCategory;

public interface VehicleCategoryService {
	public String saveVehicleCategory(VehicleCategory vehicleCategory);
	public List<VehicleCategory> getAllVehicleCategories();
	public boolean deleteVehicleCategory(String vehicleCategoryId);
	public VehicleCategory getVehicleCategory(String vehicleCategoryId);
	public boolean updateVehicleCategory(String vehicleCategoryId,VehicleCategory vehicleCategory);

}
