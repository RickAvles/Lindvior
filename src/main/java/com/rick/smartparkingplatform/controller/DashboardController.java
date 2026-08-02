package com.rick.smartparkingplatform.controller;

import com.rick.smartparkingplatform.dto.dashboard.DashboardLayout;
import com.rick.smartparkingplatform.dto.response.DashboardResponse;
import com.rick.smartparkingplatform.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // Retorna o estado atual do dashboard.
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard() {

        return ResponseEntity.ok(
                dashboardService.getDashboard()
        );

    }

    @GetMapping("/layout")
    public ResponseEntity<DashboardLayout> getLayout() {
        return ResponseEntity.ok(dashboardService.getLayout());
    }

}