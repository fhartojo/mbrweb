package com.quiteharmless.mbrweb.bo;

import java.time.LocalDate;

public class MemberInfo {

	private int memberId;

	private String firstName;

	private String lastName;

	private boolean activeIndicator;

	private LocalDate endDate;

	private int headOfFamilyId;

	private LocalDate todayDate;

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
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

	public boolean isActiveIndicator() {
		return activeIndicator;
	}

	public void setActiveIndicator(boolean activeIndicator) {
		this.activeIndicator = activeIndicator;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public int getHeadOfFamilyId() {
		return headOfFamilyId;
	}

	public void setHeadOfFamilyId(int headOfFamilyId) {
		this.headOfFamilyId = headOfFamilyId;
	}

	public LocalDate getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(LocalDate todayDate) {
		this.todayDate = todayDate;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return 
				"memberId:" + memberId
				+ ";firstName:" + firstName
				+ ";lastName:" + lastName
				+ ";activeIndicator:" + activeIndicator
				+ ";endDate:" + endDate
				+ ";headOfFamilyId:" + headOfFamilyId
				;
	}
}
