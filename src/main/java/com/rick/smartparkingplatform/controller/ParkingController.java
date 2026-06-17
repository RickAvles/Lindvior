package com.rick.smartparkingplatform.controller;

import com.rick.smartparkingplatform.dto.request.ParkingRequest;
import com.rick.smartparkingplatform.dto.response.ParkingResponse;
import com.rick.smartparkingplatform.service.ParkingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parkings")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping
    public ParkingResponse create(@Valid @RequestBody ParkingRequest request) {
        return parkingService.create(request);
    }

    @GetMapping
    public List<ParkingResponse> getAll() {
        return parkingService.findAll();
    }

    @GetMapping("/{id}")
    public ParkingResponse findById(@PathVariable UUID id) {
        return parkingService.findById(id);
    }

    @PutMapping("/{id}")
    public ParkingResponse update(@PathVariable UUID id, @Valid @RequestBody ParkingRequest request) {
        return parkingService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        parkingService.delete(id);
    }

}