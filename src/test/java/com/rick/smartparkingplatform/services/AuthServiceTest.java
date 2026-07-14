package com.rick.smartparkingplatform.services;

import com.rick.smartparkingplatform.entity.User;
import com.rick.smartparkingplatform.repository.UserRepository;
import com.rick.smartparkingplatform.security.dto.LoginRequest;
import com.rick.smartparkingplatform.security.dto.LoginResponse;
import com.rick.smartparkingplatform.security.service.AuthService;
import com.rick.smartparkingplatform.security.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginShouldReturnToken() {

        LoginRequest request = new LoginRequest(
                "rick@email.com",
                "123456"
        );

        User user = new User();
        user.setEmail(request.email());

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(mock(Authentication.class));

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.message());

        verify(authenticationManager)
                .authenticate(any(Authentication.class));

        verify(userRepository)
                .findByEmail(request.email());

        verify(jwtService)
                .generateToken(user);
    }

    @Test
    void loginShouldThrowExceptionWhenUserNotFound() {

        LoginRequest request = new LoginRequest(
                "rick@email.com",
                "123456"
        );

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(mock(Authentication.class));

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> authService.login(request)
        );

        verify(authenticationManager)
                .authenticate(any(Authentication.class));

        verify(userRepository)
                .findByEmail(request.email());

        verify(jwtService, never())
                .generateToken(any(User.class));
    }

}