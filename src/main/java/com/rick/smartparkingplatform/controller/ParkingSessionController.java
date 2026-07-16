package com.rick.smartparkingplatform.controller;

import com.rick.smartparkingplatform.dto.filter.ParkingSessionFilter;
import com.rick.smartparkingplatform.dto.request.ParkingSessionRequest;
import com.rick.smartparkingplatform.dto.response.ParkingSessionResponse;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/parking-sessions")
public class ParkingSessionController {

    private final ParkingSessionService parkingSessionService;
    
    @GetMapping
    public Page<ParkingSessionResponse> get(@PageableDefault(page = 0, size = 20) Pageable pageable,
                                            @RequestParam(required = false) String licensePlate,
                                            @RequestParam(required = false) StatusParkingSession status,
                                            @RequestParam(required = false) String parkingSpotCode,
                                            @RequestParam(required = false) LocalDateTime startDate,
                                            @RequestParam(required = false) LocalDateTime endDate
    ) {
        ParkingSessionFilter filter = new ParkingSessionFilter(licensePlate, status, parkingSpotCode, startDate, endDate);

        return parkingSessionService.findAll(pageable, filter);
    }

    @GetMapping("/{id}")
    public ParkingSessionResponse getById(@PathVariable UUID id) {
        return parkingSessionService.getById(id);
    }
}
