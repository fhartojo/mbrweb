package com.quiteharmless.mbrweb.model.service;

import org.springframework.jdbc.core.JdbcTemplate;

import com.quiteharmless.mbrweb.model.IModelService;

public abstract class AbstractBaseModelService implements IModelService {

	private JdbcTemplate jdbcTemplate;

	/**
	 * @return the jdbcTemplate
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	/**
	 * @param jdbcTemplate the jdbcTemplate to set
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
