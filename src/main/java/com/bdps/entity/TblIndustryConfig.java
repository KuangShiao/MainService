package com.bdps.entity;

import java.sql.Timestamp;

public class TblIndustryConfig {

	private String industryNo;
	private String industryName;
	private Timestamp createDate;
	private Timestamp modifyDate;

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
