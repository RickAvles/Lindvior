package com.rick.smartparkingplatform.controller;


import com.rick.smartparkingplatform.dto.request.ParkingSectorRequest;
import com.rick.smartparkingplatform.dto.response.ParkingSectorResponse;
import com.rick.smartparkingplatform.service.ParkingSectorService;
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
@RequestMapping("/api/v1/parking-sectors")
public class ParkingSectorController {

    private final ParkingSectorService parkingSectorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParkingSectorResponse create(@Valid @RequestBody ParkingSectorRequest request) {
        return parkingSectorService.create(request);
    }

    @GetMapping
    public Page<ParkingSectorResponse> findAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        Pageable pageable = PageRequest.of(page, size);

        return parkingSectorService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ParkingSectorResponse getById(@PathVariable UUID id) {

        return parkingSectorService.getById(id);
    }

    @PutMapping("/{id}")
    public ParkingSectorResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody ParkingSectorRequest request) {

        return parkingSectorService.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public ParkingSectorResponse deactivate(@PathVariable UUID id) {

        return parkingSectorService.deactivate(id);
    }

}