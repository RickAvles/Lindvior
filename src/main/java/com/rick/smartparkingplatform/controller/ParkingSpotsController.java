package com.rick.smartparkingplatform.controller;

import com.rick.smartparkingplatform.dto.filter.ParkingSpotFilter;
import com.rick.smartparkingplatform.dto.request.ParkingSpotRequest;
import com.rick.smartparkingplatform.dto.response.OccupancyResponse;
import com.rick.smartparkingplatform.dto.response.ParkingSpotResponse;
import com.rick.smartparkingplatform.enums.ParkingSectorType;
import com.rick.smartparkingplatform.enums.ParkingSpotType;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/parking-spots")
public class ParkingSpotsController {

    private final ParkingSpotService parkingSpotService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingSpotResponse create(@Valid @RequestBody ParkingSpotRequest request) {
        return parkingSpotService.create(request);
    }

    @GetMapping
    public Page<ParkingSpotResponse> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String sector,
            @RequestParam(required = false) ParkingSpotType parkingSpotType,
            @RequestParam(required = false) ParkingSectorType parkingSectorType,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) StatusParkingSpot status,
            @RequestParam(required = false) Boolean active) {

        Pageable pageable = PageRequest.of(page, size);

        ParkingSpotFilter filter = new ParkingSpotFilter(
                sector,
                parkingSpotType,
                parkingSectorType,
                floor,
                status,
                active
        );

        return parkingSpotService.findAll(pageable, filter);
    }

    @GetMapping("/occupancy")
    public OccupancyResponse getOccupancy() {
        return parkingSpotService.getOccupancy();
    }
}