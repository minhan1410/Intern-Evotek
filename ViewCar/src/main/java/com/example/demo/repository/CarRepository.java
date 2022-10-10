package com.example.demo.repository;

import com.example.demo.constants.StatusEnum;
import com.example.demo.model.entity.CarEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<CarEntity, Long> {
    Page<CarEntity> findAllByStatus(StatusEnum statusEnum, Pageable pageable);

    Page<CarEntity> findByNameContainingAndStatus(String name, StatusEnum statusEnum, Pageable pageable);

    Page<CarEntity> findByEngineAndStatus(String engine, StatusEnum statusEnum, Pageable pageable);

    Optional<CarEntity> findByName(String name);

    Optional<CarEntity> findByImg(String img);

}
