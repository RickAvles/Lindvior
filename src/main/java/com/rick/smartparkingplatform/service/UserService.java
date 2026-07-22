package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.request.UserRequest;
import com.rick.smartparkingplatform.dto.response.UserResponse;
import com.rick.smartparkingplatform.entity.User;
import com.rick.smartparkingplatform.exception.EmailAlreadyExistsException;
import com.rick.smartparkingplatform.mapper.UserMapper;
import com.rick.smartparkingplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;


    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
    }

    public UserResponse create(UserRequest request) {

        validateEmail(request.email());

        //validação request.password

        User user = mapper.toEntity(request);

        user = userRepository.save(user);

        return mapper.toResponse(user);
    }
}
