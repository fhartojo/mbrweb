package com.quiteharmless.mbrweb.context;

import java.util.Properties;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.quiteharmless.mbrweb.model.service.DatabasePropertySource;

public class DatabasePropertySourceInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {

	@Override
	public void initialize(ConfigurableWebApplicationContext context) {
		DatabasePropertySource databasePropertySource = new DatabasePropertySource("appProperties", new Properties());

		context.getEnvironment().getPropertySources().addLast(databasePropertySource);
	}
}
