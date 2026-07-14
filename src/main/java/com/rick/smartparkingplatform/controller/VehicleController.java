package com.rick.smartparkingplatform.controller;

import com.rick.smartparkingplatform.dto.request.VehicleRequest;
import com.rick.smartparkingplatform.dto.response.VehicleResponse;
import com.rick.smartparkingplatform.service.VehicleService;
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
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse create(
            @Valid @RequestBody VehicleRequest request) {

        return vehicleService.create(request);
    }

    @GetMapping
    public Page<VehicleResponse> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        Pageable pageable = PageRequest.of(page, size);

        return vehicleService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public VehicleResponse getById(@PathVariable UUID id) {

        return vehicleService.getById(id);
    }

    @PutMapping("/{id}")
    public VehicleResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleRequest request) {

        return vehicleService.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public VehicleResponse deactivate(@PathVariable UUID id) {

        return vehicleService.deactivate(id);
    }

}