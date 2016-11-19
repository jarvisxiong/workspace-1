package com.neppro.localbus.dao;

import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.neppro.localbus.model.Role;
import com.neppro.localbus.model.User;

@Repository
public class RoleDaoImpl extends GenericDaoImpl<Role,String> implements RoleDao {

	@Autowired
	SessionFactory sessionFactory;
	
	@Autowired
	public RoleDaoImpl(@Qualifier("sessionFactory")SessionFactory sessionFactory) {
		super(sessionFactory, Role.class);
	}

	@Override
	public Role getUserRole(String username) {
		Role userRole=null;
		List<Role> roles=getAll();
		for(Role role:roles){
			Set<User> users=role.getUsers();
			for(User user:users){
				if(user.getUsername().equals(username)){
					return role;
				}
			}
		}
		return userRole;
	}

}
