package com.quiteharmless.mbrweb.model.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.quiteharmless.mbrweb.model.IDataLoaderModelService;
import com.quiteharmless.mbrweb.util.Constants;

@Component("dataLoaderModelService")
public class DataLoaderModelService extends AbstractBaseModelService implements IDataLoaderModelService {

	@Autowired
	private JdbcTemplate memberJdbcTemplate;

	@Value("${applicationName}")
	private String applicationName;

	@Value("${credentialFilePath}")
	private String credentialFilePath;

	@Value("${spreadsheetId}")
	private String spreadsheetId;

	@Value("${timezoneId}")
	private String timezoneId;

	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);

	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	private PreparedStatementCreatorFactory replaceMemberPscf = new PreparedStatementCreatorFactory(
			"insert or replace into mbr(mbr_id, first_nm, last_nm, load_id) values(?, ?, ?, ?)"
			, Types.BIGINT, Types.VARCHAR, Types.VARCHAR, Types.BIGINT
			);

	private PreparedStatementCreatorFactory replaceNotePscf = new PreparedStatementCreatorFactory(
			"insert or replace into mbr_note(mbr_id, mbr_note_txt, load_id) values(?, ?, ?)"
			, Types.BIGINT, Types.VARCHAR, Types.BIGINT
			);

	private PreparedStatementCreatorFactory replaceMembershipPscf = new PreparedStatementCreatorFactory(
			"insert or replace into mbr_mbrship(mbr_id, active_ind, mbrship_type_id, mbr_mbrship_start_dt, mbr_mbrship_end_dt, mbr_hof_id, load_id) values(?, ?, ?, date(?, 'unixepoch'), date(?, 'unixepoch'), ?, ?)"
			, Types.BIGINT, Types.INTEGER, Types.INTEGER, Types.BIGINT, Types.BIGINT, Types.BIGINT, Types.BIGINT
			);

	private PreparedStatementCreatorFactory replaceMembershipLookupPscf = new PreparedStatementCreatorFactory(
			"insert or replace into mbr_lu(mbr_id, mbr_lu_id, load_id) values(?, ?, ?)"
			, Types.BIGINT, Types.VARCHAR, Types.BIGINT
			);

	private PreparedStatementCreatorFactory invalidatePreviousLoadsPscf = new PreparedStatementCreatorFactory(
			"update loader set active_ind=0"
			);

	private PreparedStatementCreatorFactory recordLoadPscf = new PreparedStatementCreatorFactory(
			"insert into loader(load_id, active_ind, load_ts) values (?, 1, datetime('now'))"
			, Types.BIGINT
			);

	private PreparedStatementCreatorFactory purgeOldMemberPscf = new PreparedStatementCreatorFactory(
			"delete from mbr where load_id not in (select load_id from loader order by load_ts desc limit 2)"
			);

	private PreparedStatementCreatorFactory purgeOldMembershipPscf = new PreparedStatementCreatorFactory(
			"delete from mbr_mbrship where load_id not in (select load_id from loader order by load_ts desc limit 2)"
			);

	private PreparedStatementCreatorFactory purgeOldMembershipLookupPscf = new PreparedStatementCreatorFactory(
			"delete from mbr_lu where load_id not in (select load_id from loader order by load_ts desc limit 2)"
			);

	private PreparedStatementCreatorFactory purgeOldNotePscf = new PreparedStatementCreatorFactory(
			"delete from mbr_note where load_id not in (select load_id from loader order by load_ts desc limit 2)"
			);

	private PreparedStatementCreatorFactory purgeOldLoaderPscf = new PreparedStatementCreatorFactory(
			"delete from loader where load_id not in (select load_id from loader order by load_ts desc limit 2)"
			);

	private PreparedStatementCreator purgeOldMemberPsc = purgeOldMemberPscf.newPreparedStatementCreator((Object[]) null);
	private PreparedStatementCreator purgeOldMembershipPsc = purgeOldMembershipPscf.newPreparedStatementCreator((Object[]) null);
	private PreparedStatementCreator purgeOldMembershipLookupPsc = purgeOldMembershipLookupPscf.newPreparedStatementCreator((Object[]) null);
	private PreparedStatementCreator purgeOldNotePsc = purgeOldNotePscf.newPreparedStatementCreator((Object[]) null);
	private PreparedStatementCreator purgeOldLoaderPsc = purgeOldLoaderPscf.newPreparedStatementCreator((Object[]) null);

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");

	private LocalDate infiniteDate = LocalDate.of(9999, 12, 31);

	private static final Logger log = LoggerFactory.getLogger(DataLoaderModelService.class);

	@Override
	@Scheduled(cron = "${membershipCronExp}")
	public void loadMembershipData() throws Exception {
		log.debug("Starting loadMembershipData()...");

		long loadId = System.currentTimeMillis();

		try {
			final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
					.setApplicationName(this.applicationName)
					.build();

			List<List<Object>> values = service.spreadsheets().values().get(this.spreadsheetId, "!A2:N").execute().getValues();

			loadMembersData(values, loadId);
			recordLoad(loadId);
			purgeOldMembersData();

			log.info("Loaded {} rows in {}ms", values.size(), (System.currentTimeMillis() - loadId));
		} catch (Exception e) {
			log.error("Exception", e);

			throw e;
		}

		log.debug("End of loadMembershipData()");
	}

	@Transactional
	private void loadMembersData(List<List<Object>> membersData, long loadId) throws Exception {
		ZoneId zoneId = ZoneId.of(this.timezoneId);
		LocalDate today = LocalDate.now(zoneId);

		log.debug("membersData.size() = " + membersData.size());

		for (List<Object> row: membersData) {
			int colSize = row.size();
			String idString = StringUtils.trimToEmpty((String) row.get(0));
			Long id = Long.valueOf(idString);
			String startDateString = (String) row.get(1);
			String firstName = StringUtils.trimToEmpty((String) row.get(2));
			String lastName = StringUtils.trimToEmpty((String) row.get(3));
			String type = StringUtils.trimToEmpty(((String) row.get(4)).toUpperCase());
			String typeLength = StringUtils.trimToEmpty(((String) row.get(5)).toUpperCase());
			Integer isCurrent = Integer.valueOf(((String) row.get(6)).toUpperCase().equals("Y") ? 1 : 0);
			Long hofId = Long.valueOf(0);
			String endDateString = null;
			String note = null;
			String lookupIdString = null;
			boolean isFamily = false;

			if (8 < colSize) {
				try {
					hofId = Long.valueOf(StringUtils.trimToNull((String) row.get(8)) != null ? (String) row.get(8) : "0");
				} catch (NumberFormatException e) {
				}
			}

			if (11 < colSize) {
				endDateString = StringUtils.trimToNull((String) row.get(11));
			}

			if (12 < colSize) {
				note = StringUtils.trimToNull((String) row.get(12));
			}

			if (13 < colSize) {
				lookupIdString = StringUtils.trimToNull((String) row.get(13));
			}

			PreparedStatementCreator replaceMemberPsc = replaceMemberPscf.newPreparedStatementCreator(new Object[] {id, firstName, lastName, loadId});

			memberJdbcTemplate.update(replaceMemberPsc);

			if (StringUtils.isNotBlank(lookupIdString)) {
				PreparedStatementCreator replaceMembershipLookupPsc = replaceMembershipLookupPscf.newPreparedStatementCreator(new Object[] {id, lookupIdString, loadId});

				memberJdbcTemplate.update(replaceMembershipLookupPsc);
			}

			Integer membershipTypeId = 0;
			LocalDate startDate = LocalDate.parse(startDateString, formatter);
			LocalDate endDate = MonthDay.from(startDate).atYear(today.getYear());

			if (typeLength.equals("LIFE") || typeLength.equals("HON")) {
				membershipTypeId = 1;
				endDate = infiniteDate;
			} else if (type.equals("FAM")) {
				isFamily = true;

				if (typeLength.equals("ANN")) {
					membershipTypeId = 2;
					if (StringUtils.isNotBlank(endDateString)) {
						endDate = LocalDate.parse(endDateString, formatter);
					} else {
						if (isCurrent.intValue() == 1) {
							log.error("endDateString is blank for {}", idString);

							endDate = null;
						} else {
							endDate = startDate.plusYears(1L);
						}
					}
				} else if (typeLength.equals("MON")) {
					membershipTypeId = 4;
					if (StringUtils.isNotBlank(endDateString)) {
						endDate = LocalDate.parse(endDateString, formatter);
					} else {
						if (isCurrent.intValue() == 1) {
							endDate = infiniteDate;
						} else {
							endDate = startDate.plusMonths(1L);
						}
					}
				} else if (typeLength.equals("SHT")) {
					membershipTypeId = 6;
					if (StringUtils.isNotBlank(endDateString)) {
						endDate = LocalDate.parse(endDateString, formatter);
					} else {
						if (isCurrent.intValue() == 1) {
							log.error("endDateString is blank for {}", idString);

							endDate = null;
						} else {
							endDate = startDate.plusMonths(1L);
						}
					}
				} else {
					log.error("Unrecognised typeLength:  " + typeLength);
				}
			} else if (type.equals("IND")) {
				if (typeLength.equals("ANN")) {
					membershipTypeId = 3;
					if (StringUtils.isNotBlank(endDateString)) {
						endDate = LocalDate.parse(endDateString, formatter);
					} else {
						if (isCurrent.intValue() == 1) {
							log.error("endDateString is blank for {}", idString);

							endDate = null;
						} else {
							endDate = startDate.plusYears(1L);
						}
					}
				} else if (typeLength.equals("MON")) {
					membershipTypeId = 5;
					if (StringUtils.isNotBlank(endDateString)) {
						endDate = LocalDate.parse(endDateString, formatter);
					} else {
						if (isCurrent.intValue() == 1) {
							endDate = infiniteDate;
						} else {
							endDate = startDate.plusMonths(1L);
						}
					}
				} else if (typeLength.equals("SHT")) {
					membershipTypeId = 7;
					if (StringUtils.isNotBlank(endDateString)) {
						endDate = LocalDate.parse(endDateString, formatter);
					} else {
						if (isCurrent.intValue() == 1) {
							log.error("endDateString is blank for {}", idString);

							endDate = null;
						} else {
							endDate = startDate.plusMonths(1L);
						}
					}
				} else {
					log.error("Unrecognised typeLength:  {} for {}", typeLength, idString);
				}
			}

			log.debug(id + "," + firstName + "," + lastName + "," + type + "," + typeLength + "," + isCurrent + ","  + endDateString + "," + hofId + "," + isFamily + "," + (id.equals(hofId)));

			Object[] replaceMembershipParams = new Object[7];

			replaceMembershipParams[0] = id;
			replaceMembershipParams[2] = membershipTypeId;
			replaceMembershipParams[3] = startDate.atStartOfDay(zoneId).toEpochSecond();
			replaceMembershipParams[5] = hofId;
			replaceMembershipParams[6] = loadId;

			if (!isFamily || (isFamily && (id.equals(hofId)))) {
				replaceMembershipParams[1] = isCurrent;
				replaceMembershipParams[4] = endDate.atStartOfDay(zoneId).toEpochSecond();
			} else {
				replaceMembershipParams[1] = null;
				replaceMembershipParams[4] = null;
			}

			PreparedStatementCreator replaceMembershipPsc = replaceMembershipPscf.newPreparedStatementCreator(replaceMembershipParams);

			memberJdbcTemplate.update(replaceMembershipPsc);

			if (StringUtils.isNotBlank(note)) {
				PreparedStatementCreator replaceNotePsc = replaceNotePscf.newPreparedStatementCreator(new Object[] {id, note, loadId});

				memberJdbcTemplate.update(replaceNotePsc);
			}
		}

		PreparedStatementCreator replaceMemberPsc = replaceMemberPscf.newPreparedStatementCreator(new Object[] {Constants.VISITOR_ID, Constants.VISITOR_NAME, Constants.VISITOR_NAME, loadId});

		memberJdbcTemplate.update(replaceMemberPsc);

		Object[] replaceMembershipParams = new Object[7];

		replaceMembershipParams[0] = Constants.VISITOR_ID;
		replaceMembershipParams[1] = Integer.valueOf(1);
		replaceMembershipParams[2] = Integer.valueOf(1);
		replaceMembershipParams[3] = Constants.VISITOR_START_DATE.atStartOfDay(zoneId).toEpochSecond();
		replaceMembershipParams[4] = infiniteDate.atStartOfDay(zoneId).toEpochSecond();
		replaceMembershipParams[5] = Long.valueOf(0);
		replaceMembershipParams[6] = loadId;

		PreparedStatementCreator replaceMembershipPsc = replaceMembershipPscf.newPreparedStatementCreator(replaceMembershipParams);

		memberJdbcTemplate.update(replaceMembershipPsc);

		PreparedStatementCreator replaceMembershipLookupPsc = replaceMembershipLookupPscf.newPreparedStatementCreator(new Object[] {Constants.VISITOR_ID, Constants.VISITOR_LOOKUP_ID, loadId});

		memberJdbcTemplate.update(replaceMembershipLookupPsc);
	}

	@Transactional
	private void recordLoad(long loadId) throws Exception {
		PreparedStatementCreator invalidatePreviousLoadsPsc = invalidatePreviousLoadsPscf.newPreparedStatementCreator((Object[]) null);

		memberJdbcTemplate.update(invalidatePreviousLoadsPsc);

		PreparedStatementCreator recordLoadPsc = recordLoadPscf.newPreparedStatementCreator(new Object[] {loadId});

		memberJdbcTemplate.update(recordLoadPsc);
	}

	@Transactional
	private void purgeOldMembersData() throws Exception {
		memberJdbcTemplate.update(purgeOldMemberPsc);
		memberJdbcTemplate.update(purgeOldMembershipPsc);
		memberJdbcTemplate.update(purgeOldMembershipLookupPsc);
		memberJdbcTemplate.update(purgeOldNotePsc);
		memberJdbcTemplate.update(purgeOldLoaderPsc);
	}

	private Credential getCredentials() throws IOException {
		try (FileInputStream fin = new FileInputStream(new File(this.credentialFilePath))) {
			GoogleCredential credential = GoogleCredential.fromStream(fin).createScoped(SCOPES);

			return credential;
		} catch (Exception e) {
			log.error("Exception", e);

			throw e;
		}
	}
}
