package com.neppro.localbus.dao;

import com.neppro.localbus.model.Role;

public interface RoleDao extends GenericDao<Role, String> {
	
	public Role getUserRole(String username);

}
