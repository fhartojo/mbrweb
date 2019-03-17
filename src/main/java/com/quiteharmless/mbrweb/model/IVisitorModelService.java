package com.quiteharmless.mbrweb.model;

import java.util.List;

import com.quiteharmless.mbrweb.bo.Visitor;

public interface IVisitorModelService extends IModelService {

	public Visitor getVisitor(String id);

	public List<Visitor> getVisitors();
}
