package com.quiteharmless.mbrweb.model.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.quiteharmless.mbrweb.bo.Visitor;

public class VisitorRowMapper implements RowMapper<Visitor> {

	@Override
	public Visitor mapRow(ResultSet rs, int rowNum) throws SQLException {
		Visitor visitor = new Visitor();

		visitor.setMemberId(rs.getLong("mbr_id"));
		visitor.setTimestamp(rs.getLong("visit_ts") * 1000L);
		visitor.setOk(rs.getBoolean("visit_admit"));
		visitor.setNotes(rs.getString("visit_note_txt"));

		return visitor;
	}
}
