package com.quiteharmless.mbrweb.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.quiteharmless.mbrweb.bo.LogEntry;
import com.quiteharmless.mbrweb.model.IDatabaseLoggerModelService;

@Controller
public class DatabaseLoggerController {

	@Autowired
	private IDatabaseLoggerModelService databaseLoggerModelService;

	private static final Logger log = LoggerFactory.getLogger(DatabaseLoggerController.class);

	@RequestMapping(value="/api/log", method=RequestMethod.GET)
	public @ResponseBody List<LogEntry> getLogEntries() {
		List<LogEntry> logEntries = this.databaseLoggerModelService.getLogEntries();

		return logEntries;
	}
}
