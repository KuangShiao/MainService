package com.bdps.module;

import java.sql.Timestamp;

public class StockInfo {

	private double closePrice;      // 目前股價
	private String stockNo;         // 股票代號
	private String stockName;       // 股票名稱
	private Timestamp listingDate;  // 上市日期
	private String marketNo;        // 市場別代碼
	private String industryNo;      // 產業別代碼
	private String industryName;    // 產業別名稱
	
	public double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}

	public String getStockNo() {
		return stockNo;
	}

	public void setStockNo(String stockNo) {
		this.stockNo = stockNo;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public Timestamp getListingDate() {
		return listingDate;
	}

	public void setListingDate(Timestamp listingDate) {
		this.listingDate = listingDate;
	}

	public String getMarketNo() {
		return marketNo;
	}

	public void setMarketNo(String marketNo) {
		this.marketNo = marketNo;
	}

	public String getIndustryNo() {
		return industryNo;
	}

	public void setIndustryNo(String industryNo) {
		this.industryNo = industryNo;
	}

	public String getIndustryName() {
		return industryName;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}

}
