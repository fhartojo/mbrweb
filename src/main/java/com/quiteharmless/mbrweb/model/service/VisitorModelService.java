package com.quiteharmless.mbrweb.model.service;

import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.stereotype.Component;

import com.quiteharmless.mbrweb.bo.MemberInfo;
import com.quiteharmless.mbrweb.bo.MemberInfoData;
import com.quiteharmless.mbrweb.bo.Visitor;
import com.quiteharmless.mbrweb.model.IMemberInfoDataModelService;
import com.quiteharmless.mbrweb.model.IMembershipTypeModelService;
import com.quiteharmless.mbrweb.model.IVisitorModelService;
import com.quiteharmless.mbrweb.util.Constants;
import com.quiteharmless.mbrweb.util.MemberInfoStatus;
import com.quiteharmless.mbrweb.util.MemberVisitStatus;

@Component("visitorModelService")
public class VisitorModelService extends AbstractBaseModelService implements IVisitorModelService {

	@Autowired
	private JdbcTemplate visitorJdbcTemplate;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private IMemberInfoDataModelService memberInfoDataModelService;

	@Autowired
	private IMembershipTypeModelService membershipTypeModelService;

	PreparedStatementCreatorFactory insertMemberVisitHistoryPscf = new PreparedStatementCreatorFactory(
			"insert into mbr_visit_hist(mbr_id, visit_admit, visit_st_cd, visit_ts, visit_note_txt) values(?, ?, ?, datetime(?, 'unixepoch'), ?)"
			, Types.BIGINT, Types.BOOLEAN, Types.INTEGER, Types.TIMESTAMP, Types.VARCHAR
	);

	private static final Logger log = LogManager.getLogger(VisitorModelService.class);

	@Override
	public Visitor getVisitorInfo(String lookupId) {
		log.debug("lookupId:  " + lookupId);

		long now = System.currentTimeMillis();
		ZonedDateTime todayZdt = Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault());
		LocalDate todayDate = todayZdt.toLocalDate();
		MemberInfo memberInfo;
		MemberInfoData memberInfoData = memberInfoDataModelService.getMemberInfoData(lookupId, true);
		MemberVisitStatus memberVisitStatus = MemberVisitStatus.REJECT;
		String memberVisitStatusMessage = "";
		String customMessage = "";
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

			if (StringUtils.isNotBlank(memberInfo.getNotes())) {
				customMessage = messageSource.getMessage("visit.customMessage", new Object[] {memberInfo.getNotes()}, Locale.getDefault());
			}
		}

		switch (memberInfoData.getStatus()) {
		case INCOMPLETE:
			memberVisitStatus = MemberVisitStatus.INCOMPLETE;
			memberVisitStatusMessage = messageSource.getMessage("visit.conditional.incompleteData", null, Locale.getDefault());
			break;

		case FOUND:
			if (memberInfo.getEndDate().isAfter(todayDate)) {
				memberVisitStatus = MemberVisitStatus.ADMIT;
			} else {
				if (membershipTypeModelService.isAutoRenew(memberInfo.getMembershipTypeId())) {
					if (memberInfo.getActiveIndicator()) {
						memberVisitStatus = MemberVisitStatus.EXPIRED;
						memberVisitStatusMessage = messageSource.getMessage("visit.conditional.expired", null, Locale.getDefault());
					} else {
						memberVisitStatus = MemberVisitStatus.REJECT;
						memberVisitStatusMessage = messageSource.getMessage("visit.reject.expired", null, Locale.getDefault());

						visitor.setOk(false);
					}
				} else {
					memberVisitStatus = MemberVisitStatus.REJECT;
					memberVisitStatusMessage = messageSource.getMessage("visit.reject.expired", null, Locale.getDefault());

					visitor.setOk(false);
				}
			}
			break;

		case NOT_FOUND:
			memberVisitStatus = MemberVisitStatus.UNKNOWN;
			memberVisitStatusMessage = messageSource.getMessage("visit.reject.unknown", null, Locale.getDefault());

			visitor.setMemberId(Constants.UNKNOWN_VISITOR_ID);
			visitor.setOk(false);
			visitor.setFirstName(Constants.UNKNOWN_VISITOR_NAME);
			visitor.setLastName(Constants.UNKNOWN_VISITOR_NAME);
			break;
		}

		log.debug("customMessage:  " + customMessage + "; memberVisitStatusMessage:  " + memberVisitStatusMessage);

		visitor.setNotes(StringUtils.isNotBlank(customMessage) ? customMessage : memberVisitStatusMessage);

		insertVisitorData(visitor, memberVisitStatus);

		return visitor;
	}

	private void insertVisitorData(Visitor visitor, MemberVisitStatus memberVisitStatus) {
		PreparedStatementCreator psc = insertMemberVisitHistoryPscf.newPreparedStatementCreator(new Object[] {visitor.getMemberId(), visitor.isOk(), memberVisitStatus.getValue(), visitor.getTimestamp() / 1000L, visitor.getNotes()});

		int rowNum = this.visitorJdbcTemplate.update(psc);

		log.debug("rowNum = " + rowNum);
	}
}
