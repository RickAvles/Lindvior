package com.rick.smartparkingplatform.repository;

import com.rick.smartparkingplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);
    
    Optional<User> findByEmail(String email);

}
