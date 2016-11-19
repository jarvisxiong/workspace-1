package com.neppro.localbus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neppro.localbus.dao.VehicleDao;
import com.neppro.localbus.model.Vehicle;

@Service
public class VehicleServiceImpl implements VehicleService {

	@Autowired
	VehicleDao vehicleDao;
	
	@Transactional
	@Override
	public void saveVehicle(Vehicle vechicle) {
		vehicleDao.create(vechicle);
	}

	@Transactional(readOnly=true)
	@Override
	public List<Vehicle> getAllVehicles() {
		return vehicleDao.getAll();
	}

	@Transactional
	@Override
	public List<Vehicle> getAllVehiclesInCategory(String vehicleCategoryId) {
		return vehicleDao.getAllVehiclesInCategory(vehicleCategoryId);
	}

}
