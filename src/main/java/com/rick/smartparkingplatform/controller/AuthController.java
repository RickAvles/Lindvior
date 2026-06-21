package com.rick.smartparkingplatform.controller;

import com.rick.smartparkingplatform.security.dto.LoginRequest;
import com.rick.smartparkingplatform.security.dto.LoginResponse;
import com.rick.smartparkingplatform.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth/login")
public class AuthController {
    private final AuthService authService;

    @PostMapping
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
