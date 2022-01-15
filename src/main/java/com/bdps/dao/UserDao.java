package com.bdps.dao;

public interface UserDao {

	public boolean verifyUser(String acc, String pwd) throws Exception;

	public boolean register(String acc, String pwd, String name) throws Exception;

}
