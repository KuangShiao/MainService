package com.bdps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bdps.service.HttpService;
import com.bdps.service.StockService;
import com.bdps.service.UserService;
import com.bdps.service.impl.HttpServiceImpl;
import com.bdps.service.impl.StockServiceImpl;
import com.bdps.service.impl.UserServiceImpl;

@Configuration
public class ServiceConfig {

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }
    
    @Bean
    public StockService stockService() {
    	return new StockServiceImpl();
    }
    
    @Bean
    public HttpService httpService() {
    	return new HttpServiceImpl();
    }

}
