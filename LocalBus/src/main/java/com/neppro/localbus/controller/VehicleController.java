package com.neppro.localbus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.neppro.localbus.model.Vehicle;
import com.neppro.localbus.model.VehicleCategory;
import com.neppro.localbus.service.VehicleCategoryService;
import com.neppro.localbus.service.VehicleService;


@RestController
public class VehicleController {

	@Autowired
	VehicleService vehicleService;
	
	@Autowired
	VehicleCategoryService vehicleCategoryService;
	
	@RequestMapping(value = "/vehicle-categories/{vehicleCategoryId}/vehicles", method = RequestMethod.POST)
	public ResponseEntity<Void> saveVehicle(@PathVariable String vehicleCategoryId, @RequestBody Vehicle vehicle) {
		VehicleCategory vehicleCategory=vehicleCategoryService.getVehicleCategory(vehicleCategoryId);
		vehicle.setVehicleCategory(vehicleCategory);
		vehicleService.saveVehicle(vehicle);
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/vehicle-categories/{vehicleCategoryId}/vehicles", method = RequestMethod.GET)
	public ResponseEntity<List<Vehicle>> getAllVehiclesInCategory(@PathVariable String vehicleCategoryId) {
		List<Vehicle> vehiclesInCategory = vehicleService.getAllVehiclesInCategory(vehicleCategoryId);
		if(vehiclesInCategory.isEmpty()){
			return new ResponseEntity<List<Vehicle>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
		}
		return new ResponseEntity<List<Vehicle>>(vehiclesInCategory, HttpStatus.OK);

	}

}
