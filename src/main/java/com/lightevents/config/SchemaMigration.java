package com.lightevents.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SchemaMigration {
    @Bean
    CommandLineRunner migrateEventCategoriesColumn(JdbcTemplate jdbc) {
        return args -> {
            jdbc.execute("ALTER TABLE events ADD COLUMN IF NOT EXISTS categories VARCHAR(1000)");
            jdbc.execute("ALTER TABLE events ADD COLUMN IF NOT EXISTS payout_method VARCHAR(255)");
            jdbc.execute("ALTER TABLE events ADD COLUMN IF NOT EXISTS payout_account_ref VARCHAR(1200)");
            jdbc.execute("ALTER TABLE events ADD COLUMN IF NOT EXISTS payout_schedule VARCHAR(255)");
            jdbc.execute("ALTER TABLE transactions ADD COLUMN IF NOT EXISTS organizer_payout_method VARCHAR(255)");
            jdbc.execute("ALTER TABLE transactions ADD COLUMN IF NOT EXISTS organizer_payout_account_ref VARCHAR(1200)");
            jdbc.execute("ALTER TABLE transactions ADD COLUMN IF NOT EXISTS payout_status VARCHAR(255)");
        };
    }
}
