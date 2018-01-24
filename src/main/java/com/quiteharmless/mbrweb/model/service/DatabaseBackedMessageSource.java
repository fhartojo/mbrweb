package com.quiteharmless.mbrweb.model.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component("messageSource")
public class DatabaseBackedMessageSource extends AbstractMessageSource {

	@Autowired
	private JdbcTemplate appJdbcTemplate;

	private Messages messages;

	private PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory("select msg_lang, msg_country, msg_variant, msg_cd, msg_txt from messages");

	private PreparedStatementCreator psc = this.pscf.newPreparedStatementCreator((List<?>) null);

	private static final Logger log = LogManager.getLogger(DatabaseBackedMessageSource.class);

	@PostConstruct
	private void init() {
		this.messages = this.appJdbcTemplate.query(psc, new ResultSetExtractor<Messages>() {

			@Override
			public Messages extractData(ResultSet rs) throws SQLException, DataAccessException {
				Messages messages = new Messages();

				while (rs.next()) {
					String lang = rs.getString("msg_lang");
					String country = rs.getString("msg_country");
					String variant = rs.getString("msg_variant");
					Locale locale;

					if (variant == null) {
						if (country == null) {
							locale = new Locale(lang);
						} else {
							locale = new Locale(lang, country);
						}
					} else {
						locale = new Locale(lang, country, variant);
					}

					messages.addMessage(rs.getString("msg_cd"), locale, rs.getString("msg_txt"));
				}

				return messages;
			}
		});
	}

	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		String msg = this.messages.getMessage(code, locale);

		log.debug("code:  " + code + "; locale:  " + locale.toString() + "; msg:  " + msg);

		return createMessageFormat(msg, locale);
	}

	private static class Messages {
		private Map<String, Map<Locale, String>> messages;

		void addMessage(String code, Locale locale, String msg) {
			if (messages == null) {
				messages = new HashMap<String, Map<Locale, String>>();
			}

			Map<Locale, String> data = messages.get(code);
			if (data == null) {
				data = new HashMap<Locale, String>();
				messages.put(code, data);
			}

			data.put(locale, msg);
		}

		String getMessage(String code, Locale locale) {
			Map<Locale, String> data = messages.get(code);

			log.debug("data:  " + data);

			return data != null ? data.get(locale) : null;
		}
	}
}
