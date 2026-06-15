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
		String databaseUrl = System.getenv("DATABASE_URL");
		if (databaseUrl == null || databaseUrl.isBlank() || databaseUrl.startsWith("jdbc:")) return;
		if (!databaseUrl.startsWith("postgres://") && !databaseUrl.startsWith("postgresql://")) return;
		try {
			URI uri = URI.create(databaseUrl);
			String query = uri.getQuery();
			String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + (uri.getPort() > 0 ? ":" + uri.getPort() : "") + uri.getPath() + (query == null || query.isBlank() ? "?sslmode=require" : "?" + query);
			System.setProperty("spring.datasource.url", jdbcUrl);
			System.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
			String userInfo = uri.getUserInfo();
			if (userInfo != null && !userInfo.isBlank()) {
				String[] parts = userInfo.split(":", 2);
				if (System.getenv("DATABASE_USERNAME") == null && parts.length > 0) System.setProperty("spring.datasource.username", decode(parts[0]));
				if (System.getenv("DATABASE_PASSWORD") == null && parts.length > 1) System.setProperty("spring.datasource.password", decode(parts[1]));
			}
		} catch (Exception ignored) {
			// Keep application.properties fallback so local/dev environments continue to boot.
		}
	}

	private static String decode(String value) {
		return URLDecoder.decode(value, StandardCharsets.UTF_8);
	}
}
