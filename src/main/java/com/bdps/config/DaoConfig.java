package com.bdps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bdps.dao.StockDao;
import com.bdps.dao.UserDao;
import com.bdps.dao.impl.StockDaoImpl;
import com.bdps.dao.impl.UserDaoImpl;

@Configuration
public class DaoConfig {
	
	@Bean
	public UserDao userDao() {
		return new UserDaoImpl();
	}
	
	@Bean
	public StockDao stockDao() {
		return new StockDaoImpl();
	}

}
