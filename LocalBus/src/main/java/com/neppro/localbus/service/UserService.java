package com.neppro.localbus.service;

import java.util.List;

import com.neppro.localbus.model.User;

public interface UserService {
	public User getUser(String username);
	public List<User> getAllUsers();
	public String saveUser(User user);

}
