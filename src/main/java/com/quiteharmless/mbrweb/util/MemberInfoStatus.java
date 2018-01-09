package com.quiteharmless.mbrweb.util;

public enum MemberInfoStatus {

	FOUND(1)
	, INCOMPLETE(2)
	, NOT_FOUND(3);

	private int value;

	private MemberInfoStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}