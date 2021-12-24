package com.quiteharmless.mbrweb.model.service;

import java.sql.Types;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.quiteharmless.mbrweb.bo.LogEntry;
import com.quiteharmless.mbrweb.model.IDatabaseLoggerModelService;
import com.quiteharmless.mbrweb.model.rowmapper.LogEntryRowMapper;

@Component("databaseLoggerModelService")
public class DatabaseLoggerModelService extends AbstractBaseModelService implements IDatabaseLoggerModelService {

	@Autowired
	private JdbcTemplate logJdbcTemplate;

	@Value("${logRetentionDays}")
	private long logRetentionDays;

	private PreparedStatementCreatorFactory purgeOldLogPscf = new PreparedStatementCreatorFactory(
			"delete from log where timestamp < ?"
			, Types.BIGINT
			);

	private PreparedStatementCreatorFactory getLogEntriesPscf = new PreparedStatementCreatorFactory(
			"select id, timestamp, level, logger, message, exception from log order by timestamp desc"
			);

	private static final Logger log = LoggerFactory.getLogger(DatabaseLoggerModelService.class);

	@Override
	@Scheduled(cron = "${logDBPurgeCronExp}")
	public void purge() {
		long cutoffTimestampMillis = System.currentTimeMillis() - (this.logRetentionDays * 24L * 60L * 60L * 1000L);

		log.debug("cutoffTimestampMillis = " + cutoffTimestampMillis);

		PreparedStatementCreator purgeOldLogPsc = purgeOldLogPscf.newPreparedStatementCreator(new Object[] {cutoffTimestampMillis});

		int rowNum = this.logJdbcTemplate.update(purgeOldLogPsc);

		log.info("Purged " + rowNum + " row(s) of log entries");
	}

	@Override
	public List<LogEntry> getLogEntries() {
		PreparedStatementCreator getLogEntriesPsc = getLogEntriesPscf.newPreparedStatementCreator((Object[]) null);
		List<LogEntry> logEntries = this.logJdbcTemplate.query(getLogEntriesPsc, new LogEntryRowMapper());

		return logEntries;
	}
}
