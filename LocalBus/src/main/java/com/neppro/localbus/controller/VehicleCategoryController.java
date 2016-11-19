package com.neppro.localbus.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.neppro.localbus.dto.Fault;
import com.neppro.localbus.dto.VehicleCategoryList;
import com.neppro.localbus.exception.NotFoundException;
import com.neppro.localbus.model.VehicleCategory;
import com.neppro.localbus.service.VehicleCategoryService;

@RestController
public class VehicleCategoryController {

	@Autowired
	VehicleCategoryService vehicleCategoryService;
	
	@RequestMapping(value = "/vehicle-categories", method = RequestMethod.GET)
	public ResponseEntity<VehicleCategoryList> getAllVehicleCategories() {
		VehicleCategoryList vehicleCategoryList=new VehicleCategoryList();
		List<VehicleCategory> vehicleCategories = vehicleCategoryService.getAllVehicleCategories();
		if(vehicleCategories.isEmpty()){
			return new ResponseEntity<VehicleCategoryList>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
		}
		
		vehicleCategoryList.setFault(new Fault());
		vehicleCategoryList.setVehicleCategories(vehicleCategories);
		return new ResponseEntity<VehicleCategoryList>(vehicleCategoryList, HttpStatus.OK);
	}
	
	@RequiresPermissions("vehicle-categories:create")
	@RequestMapping(value = "/vehicle-categories", method = RequestMethod.POST)
	public ResponseEntity<Void> saveVehicleCategory(@Valid @RequestBody VehicleCategory vehicleCategory) {
		vehicleCategoryService.saveVehicleCategory(vehicleCategory);
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/vehicle-categories/{vehicleCategoryId}", method = RequestMethod.DELETE)
	public ResponseEntity<Fault> deleteVehicleCategory(@PathVariable String vehicleCategoryId) throws NotFoundException{
		boolean deleted=vehicleCategoryService.deleteVehicleCategory(vehicleCategoryId);
		if(deleted){
			Fault fault=new Fault();
			return new ResponseEntity<Fault>(fault, HttpStatus.OK);
		}else{
			System.out.println("Throwing Exception");
			throw new NotFoundException("Vehicle Category with ID: "+vehicleCategoryId+" Not Found");
		}
	}

	@RequestMapping(value = "/vehicle-categories/{vehicleCategoryId}", method = RequestMethod.PUT)
	public ResponseEntity<Fault> updateVehicleCategory(@PathVariable String vehicleCategoryId,@RequestBody VehicleCategory vehicleCategory) throws NotFoundException{
		boolean updated=vehicleCategoryService.updateVehicleCategory(vehicleCategoryId,vehicleCategory);
		System.out.println("Vehicle Category Updated: "+updated);
		if(updated){
			Fault fault=new Fault();
			return new ResponseEntity<Fault>(fault, HttpStatus.OK);
		}else{
			System.out.println("Throwing Exception");
			throw new NotFoundException("Vehicle Category with ID: "+vehicleCategoryId+" Not Found");
		}
	}


}
