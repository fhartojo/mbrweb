package com.quiteharmless.mbrweb.bo;

public class Visitor {

	private String lookupId;

	private Long memberId;

	private String firstName;

	private String lastName;

	private long timestamp;

	private boolean ok;

	private String notes;

	public String getLookupId() {
		return lookupId;
	}

	public void setLookupId(String lookupId) {
		this.lookupId = lookupId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
