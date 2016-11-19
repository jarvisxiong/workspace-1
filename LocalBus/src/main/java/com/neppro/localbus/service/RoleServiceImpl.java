package com.neppro.localbus.service;

import java.util.List;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neppro.localbus.dao.RoleDao;
import com.neppro.localbus.model.Role;
import com.neppro.localbus.model.Vehicle;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	RoleDao roleDao;
	
	@Transactional
	public Role getRole(String roleName) {
		return roleDao.get(roleName);
	}

	@Transactional
	public List<Role> getAllRoles() {
		return roleDao.getAll();
	}

	@Transactional
	public String saveRole(Role role) {
		return roleDao.create(role);
	}

	@Transactional
	public Role getUserRole(String username) {
		return roleDao.getUserRole(username);
	}

}
