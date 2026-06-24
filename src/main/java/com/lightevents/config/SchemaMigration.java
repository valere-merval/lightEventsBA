package com.lightevents.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SchemaMigration {
    @Bean
    CommandLineRunner migrateEventCategoriesColumn(JdbcTemplate jdbc) {
        return args -> jdbc.execute("ALTER TABLE events ADD COLUMN IF NOT EXISTS categories VARCHAR(1000)");
    }
}
