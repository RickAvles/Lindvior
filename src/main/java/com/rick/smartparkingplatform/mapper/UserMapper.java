package com.rick.smartparkingplatform.mapper;

import com.rick.smartparkingplatform.dto.request.UserRequest;
import com.rick.smartparkingplatform.dto.response.UserResponse;
import com.rick.smartparkingplatform.entity.User;
import com.rick.smartparkingplatform.enums.Role;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    // Cria um usuário.
    public User toEntity(UserRequest request) {

        User user = new User();

        user.setEmail(request.email());
        user.setRole(Role.OPERATOR);
        user.setActive(true);

        LocalDateTime now = LocalDateTime.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        return user;

    }

    // Converte uma entidade para o DTO de resposta.
    public UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );

    }

}