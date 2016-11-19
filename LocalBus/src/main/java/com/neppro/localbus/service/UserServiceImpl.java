package com.neppro.localbus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neppro.localbus.dao.UserDao;
import com.neppro.localbus.model.User;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;
	
	@Transactional
	public User getUser(String username) {
		return userDao.get(username);
	}

	@Transactional
	public List<User> getAllUsers() {
		return userDao.getAll();
	}

	@Transactional
	public String saveUser(User user) {
		return userDao.create(user);
	}

}
