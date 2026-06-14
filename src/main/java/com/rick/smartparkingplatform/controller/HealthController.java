package com.rick.smartparkingplatform.controller;

import com.rick.smartparkingplatform.dto.HealthResponse;
import com.rick.smartparkingplatform.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/api/v1/health")
    public HealthResponse health() {
        return healthService.checkHealth();
    }

}