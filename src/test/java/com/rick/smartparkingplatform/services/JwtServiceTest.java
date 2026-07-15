package com.rick.smartparkingplatform.services;

import com.rick.smartparkingplatform.entity.User;
import com.rick.smartparkingplatform.security.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                "1234567890123456789012345678901234567890123456789012345678901234"
        );

        ReflectionTestUtils.setField(
                jwtService,
                "expiration",
                3600000L
        );
    }

    @Test
    void generateTokenShouldReturnToken() {

        User user = new User();
        user.setEmail("rick@email.com");

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsernameShouldReturnEmail() {

        User user = new User();
        user.setEmail("rick@email.com");

        String token = jwtService.generateToken(user);

        String username = jwtService.extractUsername(token);

        assertEquals("rick@email.com", username);
    }

    @Test
    void isTokenValidShouldReturnTrue() {

        User user = new User();
        user.setEmail("rick@email.com");

        String token = jwtService.generateToken(user);

        boolean valid = jwtService.isTokenValid(token, user);

        assertTrue(valid);
    }

    @Test
    void isTokenValidShouldReturnFalseWhenUsernameIsDifferent() {

        User tokenUser = new User();
        tokenUser.setEmail("rick@email.com");

        User anotherUser = new User();
        anotherUser.setEmail("outro@email.com");

        String token = jwtService.generateToken(tokenUser);

        boolean valid = jwtService.isTokenValid(token, anotherUser);

        assertFalse(valid);
    }

}