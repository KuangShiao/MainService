package com.bdps.dao.impl;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bdps.dao.UserDao;

public class UserDaoImpl implements UserDao {
	
	private static Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
	@Override
	public boolean verifyUser(String acc, String pwd) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select count(*)                 ").append(System.lineSeparator())
		   .append("  from tblAccountBasis          ").append(System.lineSeparator())
		   .append(" where account = :account       ").append(System.lineSeparator())
		   .append("   and pwd = :pwd               ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("account", acc);
		paramMap.put("pwd", Base64.getEncoder().encodeToString(pwd.getBytes("UTF-8")));
		
		return this.namedParameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class) > 0;
	}
	
	public boolean register(String acc, String pwd, String name) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("insert into tblAccountBasis (account, pwd, name, createDate)    ").append(System.lineSeparator())
		   .append("                    values (:account, :pwd, :name, sysDate)     ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("account", acc);
		paramMap.put("pwd", Base64.getEncoder().encodeToString(pwd.getBytes("UTF-8")));
		paramMap.put("name", name);
		
		logger.info("register sql: {}, paramMap: {}", sql, paramMap);
		int status = this.namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
		
		return status > 0;
	}

}
