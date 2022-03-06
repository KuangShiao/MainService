package com.bdps.service;

import java.util.List;

import org.joda.time.DateTime;

import com.bdps.entity.TblIndustryConfig;
import com.bdps.entity.TblStockPrice;
import com.bdps.module.StockInfo;
import com.bdps.module.StockInventory;

public interface StockService {

	public void printStockPrice() throws Exception;
	
	public void saveStockBasis() throws Exception;
	
	public void saveStockPrice(DateTime openStartDt, DateTime openEndDt) throws Exception;

	public void updateSma(DateTime openDt) throws Exception;
	
	public void updateBuyAndSell(DateTime openDt) throws Exception;
	
	public List<TblIndustryConfig> findAllIndustryConfig() throws Exception;

	public List<StockInfo> findStockInfo(String stockNoName, String industryNo) throws Exception;

	public List<StockInfo> findStockByTypeNo(String typeNo) throws Exception;

	public List<TblStockPrice> findStockPriceByStockNo(String stockNo) throws Exception;

	public void updateBuyAndSellByTxt(DateTime openDt) throws Exception;

	public List<StockInventory> stockInventoryQuery(String account) throws Exception;

	public void orderStock(String account, double closePrice, String stockNo, int stockNum) throws Exception;
	
	public void spendCash(String account, String tradeType, int cost) throws Exception;

}
