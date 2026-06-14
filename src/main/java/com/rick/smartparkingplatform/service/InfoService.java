package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.InfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InfoService {

    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${application.version}")
    private String version;
    @Value("${application.environment}")
    private String environment;

    public InfoResponse checkInfo() {
        return new InfoResponse(
                applicationName,
                version,
                environment,
                LocalDateTime.now()
        );
    }
}
