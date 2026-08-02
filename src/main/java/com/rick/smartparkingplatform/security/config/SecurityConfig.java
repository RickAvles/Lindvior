package com.rick.smartparkingplatform.security.config;

import com.rick.smartparkingplatform.security.filter.JwtAuthenticationFilter;
import com.rick.smartparkingplatform.security.handler.CustomAccessDeniedHandler;
import com.rick.smartparkingplatform.security.handler.CustomAuthenticationEntryPointHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPointHandler authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDenied;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                authenticationEntryPoint
                        )
                        .accessDeniedHandler(accessDenied)
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/health",
                                "/api/v1/info"
                        )
                        .permitAll()

                        //testes
                        .requestMatchers(
                                "/ws",
                                "/dashboard/**"
                        ).permitAll()

                        //Dashboard

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/dashboard"
                        )
                        .hasAnyRole("ADMIN", "USER")

                        //User

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/users"
                        )
                        .hasRole("ADMIN")

                        //Parking

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/parking"
                        )
                        .hasAnyRole("ADMIN", "USER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/parking/**"
                        )
                        .hasRole("ADMIN")

                        //ParkingSpots

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/parking-spots"
                        )
                        .hasAnyRole("ADMIN", "USER")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/parking-spots"
                        )
                        .hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/parking-spots/**"
                        )
                        .hasRole("ADMIN")

                        //ParkingSession

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/parking-sessions/**"
                        )
                        .hasAnyRole("ADMIN", "USER")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/parking-sessions"
                        )
                        .hasAnyRole("ADMIN", "USER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/parking-sessions/*/close"
                        )
                        .hasAnyRole("ADMIN", "USER")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

}
