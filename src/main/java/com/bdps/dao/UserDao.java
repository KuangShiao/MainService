package com.bdps.dao;

public interface UserDao {

	public boolean verifyUser(String acc, String pwd) throws Exception;

}
