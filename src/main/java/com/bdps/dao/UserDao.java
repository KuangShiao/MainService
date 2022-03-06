package com.bdps.dao;

import com.bdps.entity.TblAccountBasis;

public interface UserDao {

	public boolean verifyUser(String acc, String pwd) throws Exception;

	public boolean register(String acc, String pwd, String name, int cash) throws Exception;
	
	public TblAccountBasis getAccBasis(String acc) throws Exception;
	
	public boolean updateAccBasisCash(String acc, int cash) throws Exception;
	
	public boolean insertCashHistory(String acc, String tradeType, int transactionCash, int remainingCash) throws Exception;

	public boolean updateAccBasisPersonalPhoto(String acc, byte[] file) throws Exception;

}
