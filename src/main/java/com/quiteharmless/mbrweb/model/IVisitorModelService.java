package com.quiteharmless.mbrweb.model;

import com.quiteharmless.mbrweb.bo.Visitor;

public interface IVisitorModelService extends IModelService {

	public Visitor getVisitorInfo(String lookupId);
}
