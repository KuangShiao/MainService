package com.bdps.service;

import com.bdps.entity.TblAccountBasis;

public interface UserService {

	public TblAccountBasis verifyUser(String acc, String pwd) throws Exception;
	
	public TblAccountBasis getAccBasis(String acc) throws Exception;

	public boolean register(String acc, String pwd, String name, int cash) throws Exception;

	public boolean uploadUserPhoto(String acc, byte[] file) throws Exception;

}
