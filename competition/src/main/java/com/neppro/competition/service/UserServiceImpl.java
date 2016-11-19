package com.neppro.competition.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.neppro.competition.dao.UserDao;
import com.neppro.competition.model.User;

@Service
public class UserServiceImpl implements UserService {

	private UserDao userDao;
	
	@Autowired
	public UserServiceImpl(UserDao userDao){
		this.userDao=userDao;
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
	public User getUser(String username) {
		System.out.println("Getting User");
		User user=userDao.findOne(username);
		return user;
	}

	@Transactional
	public List<User> getAllUsers() {
		return (List<User>)userDao.findAll();
	}

	@Transactional
	public User saveUser(User user) {
		return userDao.save(user);
	}

	@Override
	public User getUserByMsisdn(String msisdn) {
		return userDao.getUserByMsisdn(msisdn);
	}
	
	@Transactional
	public User saveUser(String msisdn){
		User user=new User();
		user.setId(String.valueOf(new Date().getTime()));
		user.setMsisdn(msisdn);
		return userDao.save(user);
	}

}
