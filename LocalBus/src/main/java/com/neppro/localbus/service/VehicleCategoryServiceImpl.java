package com.neppro.localbus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neppro.localbus.dao.VehicleCategoryDao;
import com.neppro.localbus.model.VehicleCategory;

@Service
public class VehicleCategoryServiceImpl implements VehicleCategoryService {

	@Autowired
	VehicleCategoryDao vehicleCategoryDao;

	@Transactional
	public String saveVehicleCategory(VehicleCategory vehicleCategory) {
		return vehicleCategoryDao.create(vehicleCategory);
	}

	@Transactional
	public List<VehicleCategory> getAllVehicleCategories() {
		return vehicleCategoryDao.getAll();
	}

	@Transactional
	public boolean deleteVehicleCategory(String vehicleCategoryId) {
		boolean deleted=false;
		VehicleCategory vehicleCategory=vehicleCategoryDao.get(vehicleCategoryId);
		if(vehicleCategory!=null){
			vehicleCategoryDao.delete(vehicleCategory);
			deleted=true;
		}	
		return deleted;
	}

	@Transactional
	public VehicleCategory getVehicleCategory(String vehicleCategoryId) {
		return vehicleCategoryDao.get(vehicleCategoryId);
	}


	public boolean updateVehicleCategory(String vehicleCategoryId) {
		boolean updated=false;
		VehicleCategory vehicleCategory=vehicleCategoryDao.get(vehicleCategoryId);
		if(vehicleCategory!=null){
			vehicleCategoryDao.update(vehicleCategory);
			updated=true;
		}	
		return updated;
	}

	@Transactional
	public boolean updateVehicleCategory(String vehicleCategoryId,
			VehicleCategory vehicleCategory) {
		boolean updated=false;
		if(vehicleCategoryDao.get(vehicleCategoryId)!=null){
			vehicleCategory.setCategoryId(vehicleCategoryId);
			vehicleCategoryDao.update(vehicleCategory);
			updated=true;
		}
		return updated;
	}

}
