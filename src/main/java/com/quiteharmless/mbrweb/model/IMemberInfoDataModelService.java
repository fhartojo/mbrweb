package com.quiteharmless.mbrweb.model;

import com.quiteharmless.mbrweb.bo.MemberInfoData;

public interface IMemberInfoDataModelService extends IModelService {

	public MemberInfoData getMemberInfoData(String lookupId, boolean isLookupId);
}
