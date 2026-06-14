package com.rick.smartparkingplatform.controller;

import com.rick.smartparkingplatform.dto.InfoResponse;
import com.rick.smartparkingplatform.service.InfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {
    private final InfoService infoService;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/api/v1/info")
    public InfoResponse info() {
        return infoService.checkInfo();
    }

}
