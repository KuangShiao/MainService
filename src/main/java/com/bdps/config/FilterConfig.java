package com.bdps.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bdps.filter.CORSFilter;

@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<CORSFilter> corsFilter() {
		
	    FilterRegistrationBean<CORSFilter> registrationBean = new FilterRegistrationBean<>();
	    registrationBean.setFilter(new CORSFilter());
	    registrationBean.addUrlPatterns("/*");
	        
	    return registrationBean;    
	}
}
