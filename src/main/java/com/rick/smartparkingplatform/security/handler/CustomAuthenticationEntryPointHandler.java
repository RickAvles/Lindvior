package com.rick.smartparkingplatform.security.handler;

import com.rick.smartparkingplatform.dto.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws IOException, ServletException {
        ErrorResponse errorResponse =
                new ErrorResponse(
                        401,
                        "UNAUTHORIZED",
                        "Authentication token is required",
                        LocalDateTime.now(),
                        List.of()
                );

        response.setStatus(401);
        response.setContentType("application/json");

        objectMapper.writeValue(
                response.getWriter(),
                errorResponse
        );
    }
}
