package com.neppro.localbus.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.neppro.localbus.model.Vehicle;

@Repository
public class VehicleDaoImpl extends GenericDaoImpl<Vehicle, String> implements
		VehicleDao {
	
	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	public VehicleDaoImpl(
			@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory, Vehicle.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Vehicle> getAllVehiclesInCategory(String vehicleCategoryId) {
		Query query=sessionFactory.getCurrentSession().createQuery("from vehicle where vehicleCategory.categoryId = :vehicleCategoryId ");
		query.setParameter("vehicleCategoryId", vehicleCategoryId);
		List<Vehicle> vehiclesInCategory=query.list();
		return vehiclesInCategory;
	}

}
