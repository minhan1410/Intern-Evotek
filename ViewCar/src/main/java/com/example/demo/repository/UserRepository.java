package com.example.demo.repository;

import com.example.demo.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByUsername(String userName);
    Optional<UserEntity> findByVerificationCode(String code);
    Optional<UserEntity> findByEmail(String email);
    Boolean existsByUsername(String userName);
    Boolean existsByEmail(String email);
}
