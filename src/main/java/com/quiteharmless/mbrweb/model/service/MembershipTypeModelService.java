package com.quiteharmless.mbrweb.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.stereotype.Component;

import com.quiteharmless.mbrweb.bo.MembershipType;
import com.quiteharmless.mbrweb.model.IMembershipTypeModelService;
import com.quiteharmless.mbrweb.model.rowmapper.MembershipTypeRowMapper;

@Component("membershipTypeModelService")
public class MembershipTypeModelService extends AbstractBaseModelService implements IMembershipTypeModelService {

	@Autowired
	private JdbcTemplate memberJdbcTemplate;

	PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(
			"select "
			+ "mbrship_type_id "
			+ ", mbrship_type_nm "
			+ ", mbrship_len "
			+ ", mbrship_len_type_cd "
			+ ", mbrship_auto_renew "
			+ ", mbrship_max_visit "
			+ "from "
			+ "mbrship_type"
	);

	Map<Long, MembershipType> membershipTypeMap = new HashMap<Long, MembershipType>();

	@PostConstruct
	private void init() {
		PreparedStatementCreator psc = pscf.newPreparedStatementCreator(new Object[] {});
		List<MembershipType> membershipTypeList = this.memberJdbcTemplate.query(psc, new MembershipTypeRowMapper());

		for (MembershipType membershipType:  membershipTypeList) {
			membershipTypeMap.put(membershipType.getId(), membershipType);
		}
	}

	@Override
	public boolean isAutoRenew(Long membershipTypeId) {
		MembershipType membershipType = membershipTypeMap.get(membershipTypeId);

		if (membershipType != null) {
			return membershipType.getAutoRenew();
		} else {
			return false;
		}
	}

	@Override
	public MembershipType getMembershipType(Long membershipTypeId) {
		return membershipTypeMap.get(membershipTypeId);
	}

}
