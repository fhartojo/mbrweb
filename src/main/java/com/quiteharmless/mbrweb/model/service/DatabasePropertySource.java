package com.quiteharmless.mbrweb.model.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.core.env.PropertiesPropertySource;

public class DatabasePropertySource extends PropertiesPropertySource {

	private static final Logger log = LoggerFactory.getLogger(DatabasePropertySource.class);

	public DatabasePropertySource(String name, Properties source) {
		super(name, source);

		init();
	}

	private void init() {
		Connection appDbConnection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			log.debug("init()");

			Context ctx = new InitialContext();
			DataSource appDbDataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/appDB");

			appDbConnection = appDbDataSource.getConnection();
			ps = appDbConnection.prepareStatement("select prop_key, prop_value from properties");
			rs = ps.executeQuery();

			while (rs.next()) {
				this.getSource().put(rs.getString("prop_key"), rs.getString("prop_value"));
			}

			log.debug("this.getSource().size() = " + this.getSource().size());
		} catch (NamingException e) {
			log.error("NamingException", e);
		} catch (SQLException e) {
			log.error("SQLException", e);
		} catch (Exception e) {
			log.error("Exception", e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (ps != null) {
					ps.close();
				}

				if (appDbConnection != null) {
					appDbConnection.close();
				}
			} catch (SQLException e) {
			}
		}
	}
}
