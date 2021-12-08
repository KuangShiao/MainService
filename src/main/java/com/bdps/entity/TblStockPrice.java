package com.bdps.entity;

import java.sql.Timestamp;

public class TblStockPrice {

	private String stockNo;
	private Timestamp openDt;
	private double closePrice;
	private double highPrice;
	private double openPrice;
	private double lowPrice;
	private int volume;
	private double sma5;
	private double sma10;
	private double sma20;
	private double sma60;
	private double sma120;
	private double sma240;
	private Timestamp createDate;
	private Timestamp modifyDate;

	public String getStockNo() {
		return stockNo;
	}

	public void setStockNo(String stockNo) {
		this.stockNo = stockNo;
	}

	public Timestamp getOpenDt() {
		return openDt;
	}

	public void setOpenDt(Timestamp openDt) {
		this.openDt = openDt;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}

	public double getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
	}

	public double getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
	}

	public double getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(double lowPrice) {
		this.lowPrice = lowPrice;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public double getSma5() {
		return sma5;
	}

	public void setSma5(double sma5) {
		this.sma5 = sma5;
	}

	public double getSma10() {
		return sma10;
	}

	public void setSma10(double sma10) {
		this.sma10 = sma10;
	}

	public double getSma20() {
		return sma20;
	}

	public void setSma20(double sma20) {
		this.sma20 = sma20;
	}

	public double getSma60() {
		return sma60;
	}

	public void setSma60(double sma60) {
		this.sma60 = sma60;
	}

	public double getSma120() {
		return sma120;
	}

	public void setSma120(double sma120) {
		this.sma120 = sma120;
	}

	public double getSma240() {
		return sma240;
	}

	public void setSma240(double sma240) {
		this.sma240 = sma240;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public Timestamp getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Timestamp modifyDate) {
		this.modifyDate = modifyDate;
	}

}
