package com.quiteharmless.mbrweb.model.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.quiteharmless.mbrweb.bo.LogEntry;

public class LogEntryRowMapper implements RowMapper<LogEntry> {

	@Override
	public LogEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
		LogEntry logEntry = new LogEntry();

		logEntry.setId(rs.getString("id"));
		logEntry.setTimestamp(rs.getLong("timestamp"));
		logEntry.setLevel(rs.getString("level"));
		logEntry.setLogger(rs.getString("logger"));
		logEntry.setMessage(rs.getString("message"));
		logEntry.setException(rs.getString("exception"));

		return logEntry;
	}

}
