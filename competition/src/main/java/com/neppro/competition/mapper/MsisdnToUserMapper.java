package com.neppro.competition.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.neppro.competition.dao.UserDao;
import com.neppro.competition.model.User;

@Component("msisdnToUserMapper")
public class MsisdnToUserMapper {
	
	private UserDao userDao;
	
	public UserDao getUserDao() {
		return userDao;
	}

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public User msisdnToUser(String msisdn){
		User user=null;
		user=userDao.getUserByMsisdn(msisdn);
		/*if(user==null){
			user=new User();
			user.setId(new Timestamp(new Date().getTime()).toString());
			user.setMsisdn(msisdn);
		}*/
		return user;	
	}

}
