package com.lightevents.config;
import org.springframework.boot.CommandLineRunner; import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration;
@Configuration
public class DataSeeder {
 @Bean CommandLineRunner seed(){ return args -> { /* Production flow starts empty: no default organizer and no default event. */ }; }
}
