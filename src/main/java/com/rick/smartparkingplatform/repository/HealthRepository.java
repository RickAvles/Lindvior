package com.rick.smartparkingplatform.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class HealthRepository {

    private final JdbcTemplate jdbcTemplate;

    public HealthRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isDatabaseAvailable() {
        try {
            jdbcTemplate.queryForObject(
                    "SELECT 1",
                    Integer.class
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
