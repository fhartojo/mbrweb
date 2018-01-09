package com.quiteharmless.mbrweb.bo;

import com.quiteharmless.mbrweb.util.MemberInfoStatus;

public class MemberInfoData {

	private MemberInfo data;

	private MemberInfoStatus status;

	public MemberInfo getData() {
		return data;
	}

	public void setData(MemberInfo data) {
		this.data = data;
	}

	public MemberInfoStatus getStatus() {
		return status;
	}

	public void setStatus(MemberInfoStatus status) {
		this.status = status;
	}
}
