package com.rick.smartparkingplatform.controller;

import com.rick.smartparkingplatform.dto.filter.ParkingSessionFilter;
import com.rick.smartparkingplatform.dto.request.ParkingSessionRequest;
import com.rick.smartparkingplatform.dto.response.ParkingSessionResponse;
import com.rick.smartparkingplatform.enums.StatusParkingSession;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/parking-sessions")
public class ParkingSessionController {

    private final ParkingSessionService parkingSessionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingSessionResponse create(@RequestBody @Valid ParkingSessionRequest request) {
        return parkingSessionService.create(request);
    }

    @PutMapping("/{id}/close")
    public ParkingSessionResponse close(@PathVariable UUID id) {
        return parkingSessionService.close(id);
    }

    @GetMapping
    public Page<ParkingSessionResponse> get(@RequestParam(defaultValue = "0") Integer page,
                                            @RequestParam(defaultValue = "20") Integer size,
                                            @RequestParam(required = false) String licensePlate,
                                            @RequestParam(required = false) StatusParkingSession status) {

        Pageable pageable = PageRequest.of(page, size);
        ParkingSessionFilter filter = new ParkingSessionFilter(licensePlate, status);

        return parkingSessionService.findAll(pageable, filter);
    }

}
