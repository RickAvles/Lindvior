package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.constant.ApplicationConstants;
import com.rick.smartparkingplatform.dto.response.HealthResponse;
import com.rick.smartparkingplatform.repository.HealthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HealthService {

    private final HealthRepository healthRepository;

    public HealthService(HealthRepository healthRepository) {
        this.healthRepository = healthRepository;
    }

    @Value("${spring.application.name}")
    private String applicationName;

    public HealthResponse checkHealth() {

        String status = healthRepository.isDatabaseAvailable() ? ApplicationConstants.STATUS_UP : ApplicationConstants.STATUS_DOWN;

        return new HealthResponse(
                status,
                applicationName,
                LocalDateTime.now()
        );
    }
}
