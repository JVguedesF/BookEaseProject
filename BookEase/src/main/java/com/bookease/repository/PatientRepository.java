package com.bookease.repository;

import com.bookease.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    @Override
    @Query("SELECT p FROM Patient p WHERE p.id = :id AND p.active = true")
    Optional<Patient> findById(@NonNull @Param("id") UUID id);

    @Query("SELECT p FROM Patient p WHERE p.cpf = :cpf AND p.active = true")
    Optional<Patient> findByCpf(@NonNull @Param("cpf") String cpf);

    @Query("SELECT COUNT(p) > 0 FROM Patient p WHERE p.cpf = :cpf AND p.active = true")
    boolean existsByCpf(@NonNull @Param("cpf") String cpf);

    @Query("SELECT p FROM Patient p JOIN p.user u JOIN u.roles r " +
            "WHERE u.name = :name AND r.name = 'PATIENT' AND p.active = true")
    Optional<Patient> findByUserNameWithPatientRole(@NonNull @Param("name") String name);
}