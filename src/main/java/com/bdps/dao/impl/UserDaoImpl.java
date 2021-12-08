package com.bdps.dao.impl;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bdps.dao.UserDao;

public class UserDaoImpl implements UserDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
	@Override
	public boolean verifyUser(String acc, String pwd) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*)                 ").append("\r\n")
		   .append("  from tblAccountBasis          ").append("\r\n")
		   .append(" where account = :account       ").append("\r\n")
		   .append("   and pwd = :pwd               ").append("\r\n");
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("account", acc);
		paramMap.put("pwd", Base64.getEncoder().encodeToString(pwd.getBytes("UTF-8")));
		
		return this.namedParameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class) > 0;
	}

}
