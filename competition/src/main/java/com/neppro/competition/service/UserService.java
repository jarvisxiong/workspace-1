package com.neppro.competition.service;

import java.util.List;

import com.neppro.competition.model.User;

public interface UserService {
	public User getUser(String username);
	public List<User> getAllUsers();
	public User saveUser(User user);
	public User saveUser(String msisdn);
	public User getUserByMsisdn(String msisdn);

}
