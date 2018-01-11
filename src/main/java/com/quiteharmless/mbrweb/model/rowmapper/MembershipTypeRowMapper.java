package com.quiteharmless.mbrweb.model.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.quiteharmless.mbrweb.bo.MembershipType;

public class MembershipTypeRowMapper implements RowMapper<MembershipType> {

	@Override
	public MembershipType mapRow(ResultSet rs, int rowNum) throws SQLException {
		MembershipType membershipType = new MembershipType();

		membershipType.setId(rs.getLong("mbrship_type_id"));
		membershipType.setDescription(rs.getString("mbrship_type_nm"));
		membershipType.setLength(rs.getInt("mbrship_len"));
		membershipType.setLengthTypeCode(rs.getString("mbrship_len_type_cd"));
		membershipType.setAutoRenew(rs.getBoolean("mbrship_auto_renew"));
		membershipType.setMaxVisit(rs.getInt("mbrship_max_visit"));

		return membershipType;
	}

}
