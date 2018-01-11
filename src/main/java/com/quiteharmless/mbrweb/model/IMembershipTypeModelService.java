package com.quiteharmless.mbrweb.model;

import com.quiteharmless.mbrweb.bo.MembershipType;

public interface IMembershipTypeModelService extends IModelService {

	public boolean isAutoRenew(Long membershipTypeId);

	public MembershipType getMembershipType(Long membershipTypeId);
}
