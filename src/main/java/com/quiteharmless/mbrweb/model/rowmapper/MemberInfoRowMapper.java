package com.quiteharmless.mbrweb.model.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.quiteharmless.mbrweb.bo.MemberInfo;

public class MemberInfoRowMapper implements RowMapper<MemberInfo> {

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public MemberInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
		MemberInfo memberInfo = new MemberInfo();
		String dateString;

		memberInfo.setMemberId(rs.getInt("mbr_id"));
		memberInfo.setFirstName(rs.getString("first_nm"));
		memberInfo.setLastName(rs.getString("last_nm"));
		memberInfo.setActiveIndicator(rs.getBoolean("active_ind"));
		dateString = rs.getString("mbr_mbrship_end_dt");
		if (StringUtils.isNotBlank(dateString)) {
			memberInfo.setEndDate(LocalDate.parse(dateString, formatter));
		}
		memberInfo.setHeadOfFamilyId(rs.getInt("mbr_hof_id"));
		memberInfo.setTodayDate(LocalDate.parse(rs.getString("today_dt"), formatter));

		return memberInfo;
	}
}
