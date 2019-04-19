package com.quiteharmless.mbrweb.model.service;

import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.quiteharmless.mbrweb.model.rowmapper.VisitorRowMapper;
import com.quiteharmless.mbrweb.util.Constants;
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

	@Value("${managementContact}")
	private String managementContact;

	@Value("${expirationDaysWarn}")
	private long expirationDaysWarn;

	@Value("${timezoneId}")
	private String timezoneId;

	PreparedStatementCreatorFactory insertMemberVisitHistoryPscf = new PreparedStatementCreatorFactory(
			"insert into mbr_visit_hist(mbr_id, visit_admit, visit_st_cd, visit_ts, visit_note_txt) values(?, ?, ?, datetime(?, 'unixepoch'), ?)"
			, Types.BIGINT, Types.BOOLEAN, Types.INTEGER, Types.TIMESTAMP, Types.VARCHAR
	);

	PreparedStatementCreatorFactory getVisitorsPscf = new PreparedStatementCreatorFactory(
			"select "
			+	"mbr_id"
			+	", visit_admit"
			+	", visit_st_cd"
			+	", strftime('%s', visit_ts) as visit_ts"
			+	", visit_note_txt"
			+ " from "
			+	"mbr_visit_hist"
			+ " order by "
			+	"visit_ts desc limit 20"
	);

	private static final Logger log = LoggerFactory.getLogger(VisitorModelService.class);

	@Override
	public Visitor getVisitor(String id) {
		log.debug("id:  " + id);

		long now = System.currentTimeMillis();
		ZoneId zoneId = ZoneId.of(this.timezoneId);
		ZonedDateTime todayZdt = Instant.ofEpochMilli(now).atZone(zoneId);
		LocalDate todayDate = todayZdt.toLocalDate();
		MemberInfo memberInfo;
		MemberInfoData memberInfoData = memberInfoDataModelService.getMemberInfoData(id);
		MemberVisitStatus memberVisitStatus = MemberVisitStatus.REJECT;
		String memberVisitStatusMessage = "";
		String customMessage = "";
		Visitor visitor = new Visitor();

		visitor.setTimestamp(now);
		visitor.setLookupId(id);

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
			memberVisitStatusMessage = messageSource.getMessage("visit.reject.incompleteData", new Object[] {this.managementContact}, Locale.getDefault());

			visitor.setOk(false);
			break;

		case FOUND:
			long daysRemaining = todayDate.until(memberInfo.getEndDate(), ChronoUnit.DAYS);

			if (daysRemaining >= 0L) {
				memberVisitStatus = MemberVisitStatus.ADMIT;

				if (daysRemaining <= expirationDaysWarn) {
					if (daysRemaining > 1L) {
						memberVisitStatusMessage = messageSource.getMessage("visit.admit.expirationDaysWarn", new Object[] {daysRemaining}, Locale.getDefault());
					} else if (daysRemaining > 0L) {
						memberVisitStatusMessage = messageSource.getMessage("visit.admit.expirationTomorrowWarn", null, Locale.getDefault());
					} else {
						memberVisitStatusMessage = messageSource.getMessage("visit.admit.expirationTodayWarn", null, Locale.getDefault());
					}
				}
			} else {
				if (membershipTypeModelService.isAutoRenew(memberInfo.getMembershipTypeId())) {
					if (memberInfo.getActiveIndicator()) {
						memberVisitStatus = MemberVisitStatus.EXPIRED;
						memberVisitStatusMessage = messageSource.getMessage("visit.conditional.expired", new Object[] {this.managementContact}, Locale.getDefault());
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

	@Override
	public List<Visitor> getVisitors() {
		PreparedStatementCreator psc = getVisitorsPscf.newPreparedStatementCreator((List<?>) null);

		List<Visitor> visitors = this.visitorJdbcTemplate.query(psc, new VisitorRowMapper());

		for (Visitor visitor:  visitors) {
			MemberInfoData memberInfoData = memberInfoDataModelService.getMemberInfoData(visitor.getMemberId().toString());
			MemberInfo memberInfo = memberInfoData.getData();

			visitor.setFirstName(Constants.UNKNOWN_VISITOR_NAME);
			visitor.setLastName(Constants.UNKNOWN_VISITOR_NAME);

			if (memberInfo != null) {
				visitor.setFirstName(memberInfo.getFirstName());
				visitor.setLastName(memberInfo.getLastName());
			}
		}

		return visitors;
	}

	private void insertVisitorData(Visitor visitor, MemberVisitStatus memberVisitStatus) {
		PreparedStatementCreator psc = insertMemberVisitHistoryPscf.newPreparedStatementCreator(new Object[] {visitor.getMemberId(), visitor.isOk(), memberVisitStatus.getValue(), visitor.getTimestamp() / 1000L, visitor.getNotes()});

		int rowNum = this.visitorJdbcTemplate.update(psc);

		log.debug("rowNum = " + rowNum);
	}
}
