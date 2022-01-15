package com.bdps.service;

public interface UserService {

	public boolean verifyUser(String acc, String pwd) throws Exception;

	public boolean register(String acc, String pwd, String name) throws Exception;

}
