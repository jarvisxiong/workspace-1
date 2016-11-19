package com.neppro.localbus.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.neppro.localbus.model.VehicleCategory;

@Repository
public class VehicleCategoryDaoImpl extends GenericDaoImpl<VehicleCategory, String> implements VehicleCategoryDao {
	
	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	public VehicleCategoryDaoImpl(@Qualifier("sessionFactory")SessionFactory sessionFactory) {
		super(sessionFactory, VehicleCategory.class);
		
	}

}
