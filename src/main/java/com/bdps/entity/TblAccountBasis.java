package com.bdps.entity;

import java.sql.Timestamp;

public class TblAccountBasis {

	private String account;
	private String pwd;
	private String name;
	private Timestamp createDate;
	private int cash;
	private byte[] personalPhoto;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public int getCash() {
		return cash;
	}

	public void setCash(int cash) {
		this.cash = cash;
	}

	public byte[] getPersonalPhoto() {
		return personalPhoto;
	}

	public void setPersonalPhoto(byte[] personalPhoto) {
		this.personalPhoto = personalPhoto;
	}

}
