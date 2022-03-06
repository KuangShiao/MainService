package com.bdps.dao.impl;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bdps.dao.UserDao;
import com.bdps.entity.TblAccountBasis;

public class UserDaoImpl implements UserDao {
	
	private static Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
	@Override
	public boolean verifyUser(String acc, String pwd) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("select count(*)                 ").append(System.lineSeparator())
		   .append("  from tblAccountBasis          ").append(System.lineSeparator())
		   .append(" where account = :account       ").append(System.lineSeparator())
		   .append("   and pwd = :pwd               ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("account", acc);
		paramMap.put("pwd", Base64.getEncoder().encodeToString(pwd.getBytes("UTF-8")));
		
		return this.namedParameterJdbcTemplate.queryForObject(sql.toString(), paramMap, Integer.class) > 0;
	}
	
	@Override
	public boolean register(String acc, String pwd, String name, int cash) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("insert into tblAccountBasis (account, pwd, name, cash, createDate)     ").append(System.lineSeparator())
		   .append("                    values (:account, :pwd, :name, :cash, sysDate)     ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("account", acc);
		paramMap.put("pwd", Base64.getEncoder().encodeToString(pwd.getBytes("UTF-8")));
		paramMap.put("name", name);
		paramMap.put("cash", cash);
		
		logger.info("register sql: {}, paramMap: {}", sql, paramMap);
		int status = this.namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
		
		return status > 0;
	}
	
	@Override
	public TblAccountBasis getAccBasis(String acc) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("select *                    ").append(System.lineSeparator())
		   .append("  from tblAccountBasis      ").append(System.lineSeparator())
		   .append(" where account = :account   ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("account", acc);
		
		logger.info("getAccBasis sql: {}, account: {}", sql, paramMap);
		List<TblAccountBasis> list = this.namedParameterJdbcTemplate.query(sql.toString(), paramMap, new BeanPropertyRowMapper<>(TblAccountBasis.class));
		logger.debug("Result size: {}", list.size());
		
		if (!list.isEmpty()) {
			return list.get(0);
		}
		
		return null;
	}
	
	@Override
	public boolean updateAccBasisCash(String acc, int cash) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("update tblAccountBasis    ").append(System.lineSeparator())
		   .append("   set cash = :cash       ").append(System.lineSeparator())
		   .append(" where account = :account ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("account", acc);
		paramMap.put("cash", cash);
		
		logger.info("updateAccBasisCash sql: {}, paramMap: {}", sql, paramMap);
		int status = this.namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
		
		return status > 0;
	}
	
	public boolean insertCashHistory(String acc, String tradeType, int transactionCash, int remainingCash) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("insert into tblCashHistory (account, tradeDt, tradeType, transactionCash, remainingCash)      ").append(System.lineSeparator())
		   .append("                   values (:account, sysDate, :tradeType, :transactionCash, :remainingCash)   ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("account", acc);
		paramMap.put("tradeType", tradeType);
		paramMap.put("transactionCash", transactionCash);
		paramMap.put("remainingCash", remainingCash);
		
		logger.info("insertCashHistory sql: {}, paramMap: {}", sql, paramMap);
		int status = this.namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
		
		return status > 0;		
	}
	
	public boolean updateAccBasisPersonalPhoto(String acc, byte[] file) throws Exception {
		
		StringBuilder sql = new StringBuilder(System.lineSeparator());
		sql.append("update tblAccountBasis                  ").append(System.lineSeparator())
		   .append("   set personalPhoto = :personalPhoto   ").append(System.lineSeparator())
		   .append(" where account = :account               ").append(System.lineSeparator());
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("account", acc);
		paramMap.put("personalPhoto", file);
		
		logger.info("updateAccBasisPersonalPhoto sql: {}, account: {}", sql, acc);
		int status = this.namedParameterJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug("status: {}", status);
		
		return status > 0;		
	}

}
