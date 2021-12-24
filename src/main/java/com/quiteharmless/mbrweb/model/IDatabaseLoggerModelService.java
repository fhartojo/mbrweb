package com.quiteharmless.mbrweb.model;

import java.util.List;

import com.quiteharmless.mbrweb.bo.LogEntry;

public interface IDatabaseLoggerModelService extends IModelService {

	public void purge();

	public List<LogEntry> getLogEntries();
}
