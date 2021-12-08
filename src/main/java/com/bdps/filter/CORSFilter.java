package com.bdps.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(1)
public class CORSFilter implements Filter {

	private static Logger logger = LoggerFactory.getLogger(CORSFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse rsp, FilterChain chain)
			throws IOException, ServletException {

		/*
		Access to XMLHttpRequest at 'http://192.168.1.101:8080/verifyUser' from origin 'http://localhost:4200' has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
		POST http://192.168.1.101:8080/verifyUser net::ERR_FAILED 200
		ERROR HttpErrorResponse {headers: HttpHeaders, status: 0, statusText: 'Unknown Error', url: 'http://192.168.1.101:8080/verifyUser', ok: false, …}
		*/
		
		// 解決以上錯誤，跨來源資料共用(CORS)錯誤，允許針對不同來源請求
		
		logger.info(" ========== CORSFilter doFilter ========== ");
		
		HttpServletResponse response = (HttpServletResponse) rsp;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "X-requested-with, Content-Type, Cache-Control, Pragma, expires");
		
		chain.doFilter(req, rsp);
	}

}
