package com.rick.smartparkingplatform.security.service;

import com.rick.smartparkingplatform.security.dto.LoginRequest;
import com.rick.smartparkingplatform.security.dto.LoginResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        return new LoginResponse("Login sucessful");
    }

}
