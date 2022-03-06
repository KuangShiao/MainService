package com.bdps.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.bdps.dao.UserDao;
import com.bdps.entity.TblAccountBasis;
import com.bdps.service.UserService;

public class UserServiceImpl implements UserService {
    
	@Autowired
	private UserDao userDao;
	
	@Override
	public TblAccountBasis verifyUser(String acc, String pwd) throws Exception {
		
		if (!userDao.verifyUser(acc, pwd)) {
			throw new Exception("請確認帳號與密碼是否正確");
		}
		
		return userDao.getAccBasis(acc);
	}
	
	@Override
	public TblAccountBasis getAccBasis(String acc) throws Exception {
		return userDao.getAccBasis(acc);
	}
	
	@Override
	public boolean register(String acc, String pwd, String name, int cash) throws Exception {
		return userDao.register(acc, pwd, name, cash);
	}

	@Override
	public boolean uploadUserPhoto(String acc, byte[] file) throws Exception {
		return userDao.updateAccBasisPersonalPhoto(acc, file);
	}
	
}
