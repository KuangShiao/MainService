package com.bdps.dao;

import java.sql.Timestamp;
import java.util.List;

import com.bdps.entity.TblIndustryConfig;
import com.bdps.entity.TblStockBasis;
import com.bdps.module.StockInfo;

public interface StockDao {

	public void saveStockBasis(String stockNo, String stockName, String listingDate, String marketNo, String industryNo) throws Exception;

	public List<TblStockBasis> findStockBasisByMarketNo(String marketNo) throws Exception;
	
	public void saveStockPrice(String stockNo, Timestamp openDt, double closePrice, double highPrice, double openPrice, double lowPrice, int volume) throws Exception;

	public List<TblIndustryConfig> findAllIndustryConfig() throws Exception;

	public List<StockInfo> findStockInfo(String stockNoName, String industryNo) throws Exception;
	
	public double findSma(String stockNo, Timestamp openDt, int sma) throws Exception;
	
	public void updateSma(String stockNo, Timestamp openDt, double sma5, double sma10, double sma20, double sma60, double sma120, double sma240) throws Exception;

}
