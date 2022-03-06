package com.bdps.dao;

import java.sql.Timestamp;
import java.util.List;

import com.bdps.entity.TblIndustryConfig;
import com.bdps.entity.TblStockBasis;
import com.bdps.entity.TblStockPrice;
import com.bdps.module.StockInfo;
import com.bdps.module.StockInventory;

public interface StockDao {

	public void saveStockBasis(String stockNo, String stockName, String listingDate, String marketNo, String industryNo) throws Exception;

	public List<TblStockBasis> findStockBasisByMarketNo(String marketNo) throws Exception;
	
	public void saveStockPrice(String stockNo, Timestamp openDt, double closePrice, double highPrice, double openPrice, double lowPrice, int volume) throws Exception;

	public List<TblIndustryConfig> findAllIndustryConfig() throws Exception;

	public List<StockInfo> findStockInfo(String stockNoName, String industryNo) throws Exception;
	
	public double findSma(String stockNo, Timestamp openDt, int sma) throws Exception;
	
	public void updateSma(String stockNo, Timestamp openDt, double sma5, double sma10, double sma20, double sma60, double sma120, double sma240) throws Exception;
	
	public void updateForeignInvestors(List<TblStockPrice> list) throws Exception;
	
	public void updateInvestmentTrust(List<TblStockPrice> list) throws Exception;
	
	public void updateDealer(List<TblStockPrice> list) throws Exception;
	
	public List<StockInfo> findStackInfoByStockNo(List<String> stockNo) throws Exception;
	public List<String> findStockNoByType01() throws Exception;
	public List<String> findStockNoByType02() throws Exception;
	public List<String> findStockNoByType03() throws Exception;

	public List<TblStockPrice> findStockPriceByStockNo(String stockNo) throws Exception;

	public void updateBuyAndSellByTxt(String stockNo, Timestamp openDt, String fv, String iv, String dv) throws Exception;

	public List<StockInventory> stockInventoryQuery(String account) throws Exception;
	
	public boolean insertStockInventory(String account, String stockNo, int num, double stockPrice, int costPrice) throws Exception;
	
	public boolean insertStockTradeHistory(String account, String stockNo, String tradeType, int num, double stockPrice, int costPrice, int income) throws Exception;

}
