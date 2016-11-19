package com.neppro.localbus.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.neppro.localbus.model.User;

@Repository
public class UserDaoImpl extends GenericDaoImpl<User,String> implements UserDao {

	@Autowired
	SessionFactory sessionFactory;
	
	@Autowired
	public UserDaoImpl(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		super(sessionFactory,User.class);
	}

}
