package com.bdps.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.bdps.dao.UserDao;
import com.bdps.service.UserService;

public class UserServiceImpl implements UserService {
    
	@Autowired
	private UserDao userDao;
	
	@Override
	public boolean verifyUser(String acc, String pwd) throws Exception {
		return userDao.verifyUser(acc, pwd);
	}
	
	@Override
	public boolean register(String acc, String pwd, String name) throws Exception {
		return userDao.register(acc, pwd, name);
	}

}
