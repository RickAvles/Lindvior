package com.rick.smartparkingplatform.security.service;

import com.rick.smartparkingplatform.entity.User;
import com.rick.smartparkingplatform.repository.UserRepository;
import com.rick.smartparkingplatform.security.dto.LoginRequest;
import com.rick.smartparkingplatform.security.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email()).orElseThrow();

        String token = jwtService.generateToken(user);

        return new LoginResponse(token);
    }

}
