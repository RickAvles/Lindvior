package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.request.UserRequest;
import com.rick.smartparkingplatform.dto.response.UserResponse;
import com.rick.smartparkingplatform.entity.User;
import com.rick.smartparkingplatform.enums.Role;
import com.rick.smartparkingplatform.exception.EmailAlreadyExistsException;
import com.rick.smartparkingplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createShouldThrowEmailAlreadyExistsException() {

        UserRequest request = new UserRequest(
                "rick@email.com",
                "123456"
        );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.create(request)
        );

        verify(userRepository)
                .existsByEmail(request.email());

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void createShouldReturnUser() {

        UserRequest request = new UserRequest(
                "rick@email.com",
                "123456"
        );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.email());
        user.setPassword("encoded-password");
        user.setRole(Role.USER);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponse response = userService.create(request);

        assertNotNull(response);
        assertEquals(request.email(), response.email());
        assertEquals(Role.USER, response.role());
        assertTrue(response.active());

        verify(userRepository)
                .existsByEmail(request.email());

        verify(passwordEncoder)
                .encode(request.password());

        verify(userRepository)
                .save(any(User.class));
    }
}