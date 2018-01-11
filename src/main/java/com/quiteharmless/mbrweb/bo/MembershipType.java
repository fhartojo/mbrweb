package com.quiteharmless.mbrweb.bo;

public class MembershipType {

	private Long id;

	private String description;

	private Integer length;

	private String lengthTypeCode;

	private Boolean autoRenew;

	private Integer maxVisit;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public String getLengthTypeCode() {
		return lengthTypeCode;
	}

	public void setLengthTypeCode(String lengthTypeCode) {
		this.lengthTypeCode = lengthTypeCode;
	}

	public Boolean getAutoRenew() {
		return autoRenew;
	}

	public void setAutoRenew(Boolean autoRenew) {
		this.autoRenew = autoRenew;
	}

	public Integer getMaxVisit() {
		return maxVisit;
	}

	public void setMaxVisit(Integer maxVisit) {
		this.maxVisit = maxVisit;
	}
}
