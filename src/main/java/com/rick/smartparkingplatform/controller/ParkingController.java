package com.rick.smartparkingplatform.controller;

import com.rick.smartparkingplatform.dto.request.ParkingRequest;
import com.rick.smartparkingplatform.dto.response.ParkingResponse;
import com.rick.smartparkingplatform.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parkings")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping()
    public ParkingResponse getParking() {
        return parkingService.getParking();
    }


    @PutMapping("/{id}")
    public ParkingResponse update(@PathVariable UUID id, @Valid @RequestBody ParkingRequest request) {
        return parkingService.update(id, request);
    }
}