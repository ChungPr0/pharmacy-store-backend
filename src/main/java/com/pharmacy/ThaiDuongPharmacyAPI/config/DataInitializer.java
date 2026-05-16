package com.pharmacy.ThaiDuongPharmacyAPI.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        try {
            jdbcTemplate.execute("UPDATE orders SET created_at = NOW() WHERE created_at IS NULL");
            jdbcTemplate.execute("UPDATE accounts SET created_at = NOW() WHERE created_at IS NULL");
            jdbcTemplate.execute("UPDATE products SET created_at = NOW() WHERE created_at IS NULL");
        } catch (Exception e) {
        }
    }
}
