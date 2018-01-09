package com.quiteharmless.mbrweb.model.service;

import java.sql.Types;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

import com.quiteharmless.mbrweb.bo.MemberInfo;
import com.quiteharmless.mbrweb.bo.MemberInfoData;
import com.quiteharmless.mbrweb.model.IMemberInfoDataModelService;
import com.quiteharmless.mbrweb.model.rowmapper.MemberInfoRowMapper;
import com.quiteharmless.mbrweb.util.Constants;
import com.quiteharmless.mbrweb.util.MemberInfoStatus;

public class MemberInfoDataModelService extends AbstractBaseModelService implements IMemberInfoDataModelService {

	PreparedStatementCreatorFactory getMemberInfoByLookupIdPscf = new PreparedStatementCreatorFactory(
			"select "
			+	"member.mbr_id "
			+	", member.first_nm "
			+ 	",member.last_nm "
			+ 	",mbr_mbrship.active_ind "
			+ 	",mbr_mbrship.mbr_mbrship_end_dt "
			+ 	",mbrship_fmly.mbr_hof_id "
			+ 	",date('now','localtime') as today_dt "
			+ "from "
			+ 	"mbr_lu "
			+ 	",member "
			+ "left join "
			+ 	"mbrship_fmly "
			+ "on "
			+ 	"mbrship_fmly.mbr_id=mbr_lu.mbr_id "
			+ "left join "
			+ 	"mbr_mbrship "
			+ "on "
			+ 	"mbr_mbrship.mbr_id=mbr_lu.mbr_id "
			+ "where "
			+ 	"mbr_lu.mbr_lu_id=? "
			+ 	"and member.mbr_id=mbr_lu.mbr_id"
			, Types.VARCHAR
	);

	PreparedStatementCreatorFactory getMemberInfoByIdPscf = new PreparedStatementCreatorFactory(
			"select "
			+ 	"member.mbr_id "
			+ 	",member.first_nm "
			+ 	",member.last_nm "
			+ 	",mbr_mbrship.active_ind "
			+ 	",mbr_mbrship.mbr_mbrship_end_dt "
			+ 	",mbrship_fmly.mbr_hof_id "
			+ 	",date('now','localtime') as today_dt "
			+ "from "
			+ 	"member "
			+ "left join "
			+ 	"mbrship_fmly "
			+ "on "
			+ 	"mbrship_fmly.mbr_id=member.mbr_id "
			+ "left join "
			+ 	"mbr_mbrship "
			+ "on "
			+ 	"mbr_mbrship.mbr_id=member.mbr_id "
			+ "where "
			+ 	"member.mbr_id=?"
			, Types.BIGINT
	);

	private static final Logger log = LogManager.getLogger(MemberInfoDataModelService.class);

	@Override
	public MemberInfoData getMemberInfoData(String id, boolean isLookupId) {
		PreparedStatementCreatorFactory pscf = (isLookupId ? getMemberInfoByLookupIdPscf : getMemberInfoByIdPscf);
		PreparedStatementCreator psc = null;
		MemberInfo memberInfo = null;
		MemberInfo hofMemberInfo;
		MemberInfoData memberInfoData = new MemberInfoData();
		MemberInfoStatus memberInfoStatus = MemberInfoStatus.NOT_FOUND;

		if (!isLookupId) {
			int memberId = Constants.UNKNOWN_VISITOR_ID;

			try {
				memberId = Integer.parseInt(id);
			} catch (NumberFormatException e) {
			}

			if (memberId == Constants.UNKNOWN_VISITOR_ID) {
				log.error(id + " is neither a valid lookup ID nor a member ID");
			} else {
				psc = pscf.newPreparedStatementCreator(new Object[] {memberId});
			}
		} else {
			psc = pscf.newPreparedStatementCreator(new Object[] {id});
		}

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
						psc = getMemberInfoByIdPscf.newPreparedStatementCreator(new Object[] {memberInfo.getHeadOfFamilyId()});
						hofMemberInfo = getMemberInfo(psc);

						if (hofMemberInfo != null) {
							if (isMemberInfoDataSufficient(hofMemberInfo)) {
								memberInfo.setActiveIndicator(hofMemberInfo.isActiveIndicator());
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
			List<MemberInfo> memberInfoList = this.getJdbcTemplate().query(psc, new MemberInfoRowMapper());

			log.debug("memberInfoList.size() = " + memberInfoList.size());

			if (memberInfoList.size() >= 1) {
				if (memberInfoList.size() > 1) {
					log.warn("memberInfoList.size() = " + memberInfoList.size());
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
				return true;
			}
		} else {
			return false;
		}
	}
}
