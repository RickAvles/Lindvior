package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.request.UserRequest;
import com.rick.smartparkingplatform.dto.response.UserResponse;
import com.rick.smartparkingplatform.entity.User;
import com.rick.smartparkingplatform.enums.Role;
import com.rick.smartparkingplatform.exception.EmailAlreadyExistsException;
import com.rick.smartparkingplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User requestToEntity(UserRequest request) {
        User user = new User();

        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.OPERATOR);
        user.setActive(true);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return user;
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt());
    }

    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
    }

    public UserResponse create(UserRequest request) {

        validateEmail(request.email());

        //validação request.password

        User user = requestToEntity(request);

        user = userRepository.save(user);

        return toResponse(user);
    }
}
