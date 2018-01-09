package com.quiteharmless.mbrweb.util;

public enum MemberVisitStatus {

	ADMIT(1)
	, REJECT(2)
	, UNKNOWN(3)
	, INCOMPLETE(4)
	, EXPIRED(5)
	, OTHER(6)
	;

	private int value;

	private MemberVisitStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}