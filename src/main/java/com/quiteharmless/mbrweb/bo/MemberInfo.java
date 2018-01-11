package com.quiteharmless.mbrweb.bo;

import java.time.LocalDate;

public class MemberInfo {

	private Long memberId;

	private String firstName;

	private String lastName;

	private Long membershipTypeId;

	private Boolean activeIndicator;

	private LocalDate endDate;

	private Long headOfFamilyId;

	private LocalDate todayDate;

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

	public Long getMembershipTypeId() {
		return membershipTypeId;
	}

	public void setMembershipTypeId(Long membershipTypeId) {
		this.membershipTypeId = membershipTypeId;
	}

	public Boolean getActiveIndicator() {
		return activeIndicator;
	}

	public void setActiveIndicator(Boolean activeIndicator) {
		this.activeIndicator = activeIndicator;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Long getHeadOfFamilyId() {
		return headOfFamilyId;
	}

	public void setHeadOfFamilyId(Long headOfFamilyId) {
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
				+ ";membershipTypeId:" + membershipTypeId
				+ ";activeIndicator:" + activeIndicator
				+ ";endDate:" + endDate
				+ ";headOfFamilyId:" + headOfFamilyId
				;
	}
}
