package com.neppro.localbus.service;

import java.util.List;

import com.neppro.localbus.model.Role;

public interface RoleService {
	public Role getRole(String roleName);
	public List<Role> getAllRoles();
	public String saveRole(Role role);
	public Role getUserRole(String username);
	
}
