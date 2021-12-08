package com.bdps.service;

import java.util.List;

import org.joda.time.DateTime;

import com.bdps.entity.TblIndustryConfig;
import com.bdps.module.StockInfo;

public interface StockService {

	public void printStockPrice() throws Exception;
	
	public void saveStockBasis() throws Exception;
	
	public void saveStockPrice(DateTime openStartDt, DateTime openEndDt) throws Exception;

	public List<TblIndustryConfig> findAllIndustryConfig() throws Exception;

	public List<StockInfo> findStockInfo(String stockNoName, String industryNo) throws Exception;

	public void updateSma(DateTime openDt) throws Exception;

}
