package com.quiteharmless.mbrweb.model.service;

import java.sql.Types;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.stereotype.Component;

import com.quiteharmless.mbrweb.bo.MemberInfo;
import com.quiteharmless.mbrweb.bo.MemberInfoData;
import com.quiteharmless.mbrweb.model.IMemberInfoDataModelService;
import com.quiteharmless.mbrweb.model.rowmapper.MemberInfoRowMapper;
import com.quiteharmless.mbrweb.util.Constants;
import com.quiteharmless.mbrweb.util.MemberInfoStatus;

@Component("memberInfoDataModelService")
public class MemberInfoDataModelService extends AbstractBaseModelService implements IMemberInfoDataModelService {

	@Autowired
	private JdbcTemplate memberJdbcTemplate;

	PreparedStatementCreatorFactory getMemberInfoPscf = new PreparedStatementCreatorFactory(
			"select "
			+	"mbr.mbr_id "
			+	",mbr.first_nm "
			+ 	",mbr.last_nm "
			+	",mbr_mbrship.mbrship_type_id "
			+ 	",mbr_mbrship.active_ind "
			+ 	",mbr_mbrship.mbr_mbrship_end_dt "
			+ 	",mbr_mbrship.mbr_hof_id "
			+	",mbr_note.mbr_note_txt "
			+ "from "
			+ 	"mbr_lu "
			+ 	",mbr "
			+ 	",loader "
			+ "left join "
			+ 	"mbr_mbrship "
			+ "on "
			+ 	"mbr_mbrship.mbr_id=mbr_lu.mbr_id "
			+ 	"and mbr_mbrship.load_id=loader.load_id "
			+ "left join "
			+	"mbr_note "
			+ "on "
			+ 	"mbr_note.mbr_id=mbr_lu.mbr_id "
			+ 	"and mbr_note.load_id=loader.load_id "
			+ "where "
			+ 	"(mbr_lu.mbr_lu_id=? "
			+	"or mbr.mbr_id=?) "
			+ 	"and mbr.mbr_id=mbr_lu.mbr_id "
			+ 	"and mbr.load_id=loader.load_id "
			+ 	"and mbr_lu.load_id=loader.load_id "
			+ 	"and loader.active_ind=1"
			, Types.VARCHAR
			, Types.BIGINT
	);

	private static final Logger log = LoggerFactory.getLogger(MemberInfoDataModelService.class);

	@Override
	public MemberInfoData getMemberInfoData(String id) {
		Long memberId = Constants.UNKNOWN_VISITOR_ID;
		PreparedStatementCreator psc = null;
		MemberInfo memberInfo = null;
		MemberInfo hofMemberInfo;
		MemberInfoData memberInfoData = new MemberInfoData();
		MemberInfoStatus memberInfoStatus = MemberInfoStatus.NOT_FOUND;

		log.debug("id:  " + id);

		try {
			memberId = Long.parseLong(id);
		} catch (NumberFormatException e) {
		}

		psc = getMemberInfoPscf.newPreparedStatementCreator(new Object[] {id, memberId});

		if (psc != null) {
			memberInfo = getMemberInfo(psc);

			if (memberInfo != null) {
				if (isMemberInfoDataSufficient(memberInfo)) {
					memberInfoStatus = MemberInfoStatus.FOUND;
				} else {
					if (memberInfo.getHeadOfFamilyId() == 0) {
						log.error("Unable to determine status of member:  " + memberInfo.toString());

						memberInfoStatus = MemberInfoStatus.INCOMPLETE;
					} else {
						psc = getMemberInfoPscf.newPreparedStatementCreator(new Object[] {"", memberInfo.getHeadOfFamilyId()});
						hofMemberInfo = getMemberInfo(psc);

						if (hofMemberInfo != null) {
							if (isMemberInfoDataSufficient(hofMemberInfo)) {
								memberInfo.setActiveIndicator(hofMemberInfo.getActiveIndicator());
								memberInfo.setEndDate(hofMemberInfo.getEndDate());

								memberInfoStatus = MemberInfoStatus.FOUND;
							} else {
								log.error("Unable to determine status of head of family member:  " + hofMemberInfo.toString());

								memberInfoStatus = MemberInfoStatus.INCOMPLETE;
							}
						} else {
							log.error("Unable to retrieve head of family member info for member:  " + memberInfo.toString());

							memberInfoStatus = MemberInfoStatus.INCOMPLETE;
						}
					}
				}
			}
		}

		memberInfoData.setData(memberInfo);
		memberInfoData.setStatus(memberInfoStatus);

		return memberInfoData;
	}

	private MemberInfo getMemberInfo(PreparedStatementCreator psc) {
		if (psc != null) {
			List<MemberInfo> memberInfoList = this.memberJdbcTemplate.query(psc, new MemberInfoRowMapper());

			if (memberInfoList.size() >= 1) {
				if (memberInfoList.size() > 1) {
					log.warn("ID:  {}; memberInfoList.size() = {}", memberInfoList.get(0).getMemberId(), memberInfoList.size());
				}

				return memberInfoList.get(0);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private boolean isMemberInfoDataSufficient(MemberInfo memberInfo) {
		if (memberInfo != null) {
			if (memberInfo.getEndDate() == null) {
				return false;
			} else {
				log.debug("memberInfo.endDate:  " + memberInfo.getEndDate());

				return true;
			}
		} else {
			return false;
		}
	}
}
