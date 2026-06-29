package com.lightevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@SpringBootApplication
@EnableScheduling
public class LightEventsBackendApplication {

	public static void main(String[] args) {
		configureRenderPostgresUrl();
		SpringApplication.run(LightEventsBackendApplication.class, args);
	}

	private static void configureRenderPostgresUrl() {
		String databaseUrl = firstPresent("SPRING_DATASOURCE_URL", "JDBC_DATABASE_URL", "DATABASE_URL", "RENDER_DATABASE_URL");
		String databaseUsername = firstPresent("SPRING_DATASOURCE_USERNAME", "JDBC_DATABASE_USERNAME", "DATABASE_USERNAME", "POSTGRES_USER");
		String databasePassword = firstPresent("SPRING_DATASOURCE_PASSWORD", "JDBC_DATABASE_PASSWORD", "DATABASE_PASSWORD", "POSTGRES_PASSWORD");

		if (databaseUrl == null || databaseUrl.isBlank()) return;
		try {
			if (databaseUrl.startsWith("jdbc:postgresql:")) {
				configurePostgres(databaseUrl, databaseUsername, databasePassword);
				return;
			}
			if (!databaseUrl.startsWith("postgres://") && !databaseUrl.startsWith("postgresql://")) return;

			URI uri = URI.create(databaseUrl);
			String query = uri.getQuery();
			String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + (uri.getPort() > 0 ? ":" + uri.getPort() : "") + uri.getPath() + (query == null || query.isBlank() ? "?sslmode=require" : "?" + query);
			String userInfo = uri.getUserInfo();
			if (userInfo != null && !userInfo.isBlank()) {
				String[] parts = userInfo.split(":", 2);
				// Render internal database URLs already include the correct DB user/password.
				// Prefer those credentials over manually-entered env vars so a mistaken
				// DATABASE_USERNAME value cannot break startup.
				if (parts.length > 0) databaseUsername = decode(parts[0]);
				if (parts.length > 1) databasePassword = decode(parts[1]);
			}
			configurePostgres(jdbcUrl, databaseUsername, databasePassword);
		} catch (Exception ignored) {
			// Keep application.properties fallback so local/dev environments continue to boot.
		}
	}

	private static void configurePostgres(String jdbcUrl, String username, String password) {
		System.setProperty("spring.datasource.url", jdbcUrl);
		System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
		System.setProperty("spring.jpa.database-platform", "org.hibernate.dialect.PostgreSQLDialect");
		System.setProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		if (username != null && !username.isBlank()) System.setProperty("spring.datasource.username", username);
		if (password != null && !password.isBlank()) System.setProperty("spring.datasource.password", password);
	}

	private static String firstPresent(String... names) {
		for (String name : names) {
			String value = System.getenv(name);
			if (value != null && !value.isBlank()) return value;
		}
		return null;
	}

	private static String decode(String value) {
		return URLDecoder.decode(value, StandardCharsets.UTF_8);
	}
}
