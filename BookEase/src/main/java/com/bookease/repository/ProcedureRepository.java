package com.bookease.repository;

import com.bookease.model.entity.Procedure;
import com.bookease.model.enums.ProcedureEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProcedureRepository extends JpaRepository<Procedure, UUID> {
    @Query("SELECT p FROM Procedure p WHERE p.id = :id AND p.active = true")
    Optional<Procedure> findActiveById(@Param("id") UUID id);

    @Query("SELECT p FROM Procedure p WHERE p.procedureEnum = :procedureEnum AND p.active = true")
    Optional<Procedure> findActiveByProcedureEnum(@Param("procedureEnum") ProcedureEnum procedureEnum);
}