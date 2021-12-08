package com.bdps.entity;

import java.sql.Timestamp;

public class TblStockBasis {

	private String stockNo;         // 股票代號
	private String stockName;       // 股票名稱
	private Timestamp listingDate;  // 上市日期
	private String marketNo;        // 市場別代碼
	private String industryNo;      // 產業別代碼

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

}
