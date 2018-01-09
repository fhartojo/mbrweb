package com.quiteharmless.mbrweb.model.service;

import java.sql.Types;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;

import com.quiteharmless.mbrweb.bo.MemberInfo;
import com.quiteharmless.mbrweb.bo.MemberInfoData;
import com.quiteharmless.mbrweb.bo.Visitor;
import com.quiteharmless.mbrweb.model.IMemberInfoDataModelService;
import com.quiteharmless.mbrweb.model.IVisitorModelService;
import com.quiteharmless.mbrweb.util.Constants;
import com.quiteharmless.mbrweb.util.MemberInfoStatus;
import com.quiteharmless.mbrweb.util.MemberVisitStatus;

public class VisitorModelService extends AbstractBaseModelService implements IVisitorModelService {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private IMemberInfoDataModelService memberInfoDataModelService;

	PreparedStatementCreatorFactory insertMemberVisitHistoryPscf = new PreparedStatementCreatorFactory(
			"insert into mbr_visit_hist(mbr_id, visit_st_cd, visit_ts) values(?, ?, datetime('now'))"
			, Types.BIGINT, Types.INTEGER
	);

	private static final Logger log = LogManager.getLogger(VisitorModelService.class);

	@Override
	public Visitor getVisitorInfo(String lookupId) {
		log.debug("lookupId:  " + lookupId);

		long now = System.currentTimeMillis();
		MemberInfo memberInfo;
		MemberInfoData memberInfoData = memberInfoDataModelService.getMemberInfoData(lookupId, true);
		MemberVisitStatus memberVisitStatus = MemberVisitStatus.REJECT;
		String memberVisitStatusMessage = "";
		Visitor visitor = new Visitor();

		visitor.setTimestamp(now);
		visitor.setLookupId(lookupId);

		if (memberInfoData.getStatus() == MemberInfoStatus.NOT_FOUND) {
			log.debug("Trying lookupId as a member ID");

			memberInfoData = memberInfoDataModelService.getMemberInfoData(lookupId, false);
		}

		memberInfo = memberInfoData.getData();

		if (memberInfo != null) {
			visitor.setMemberId(memberInfo.getMemberId());
			visitor.setOk(true);
			visitor.setFirstName(memberInfo.getFirstName());
			visitor.setLastName(memberInfo.getLastName());
		}

		switch (memberInfoData.getStatus()) {
		case INCOMPLETE:
			memberVisitStatus = MemberVisitStatus.INCOMPLETE;
			memberVisitStatusMessage = messageSource.getMessage("visit.conditional.incompleteData", null, Locale.getDefault());
			break;

		case FOUND:
			if (memberInfo.getEndDate().isAfter(memberInfo.getTodayDate())) {
				memberVisitStatus = MemberVisitStatus.ADMIT;
			} else {
				if (memberInfo.isActiveIndicator()) {
					memberVisitStatus = MemberVisitStatus.EXPIRED;
					memberVisitStatusMessage = messageSource.getMessage("visit.conditional.expired", null, Locale.getDefault());
				} else {
					memberVisitStatus = MemberVisitStatus.REJECT;
					memberVisitStatusMessage = messageSource.getMessage("visit.reject.expired", null, Locale.getDefault());

					visitor.setOk(false);
				}
			}
			break;

		case NOT_FOUND:
			memberVisitStatus = MemberVisitStatus.REJECT;
			memberVisitStatusMessage = messageSource.getMessage("visit.reject.unknown", null, Locale.getDefault());

			visitor.setMemberId(Constants.UNKNOWN_VISITOR_ID);
			visitor.setOk(false);
			visitor.setFirstName(Constants.UNKNOWN_VISITOR_NAME);
			visitor.setLastName(Constants.UNKNOWN_VISITOR_NAME);
			break;
		}

		log.debug("memberVisitStatusMessage:  " + memberVisitStatusMessage);

		visitor.setNotes(memberVisitStatusMessage);

		insertVisitorData(visitor.getMemberId(), memberVisitStatus);

		return visitor;
	}

	private void insertVisitorData(int id, MemberVisitStatus memberVisitStatus) {
		PreparedStatementCreator psc = insertMemberVisitHistoryPscf.newPreparedStatementCreator(new Object[] {id, memberVisitStatus.getValue()});

		int rowNum = this.getJdbcTemplate().update(psc);

		log.debug("rowNum = " + rowNum);
	}
}
