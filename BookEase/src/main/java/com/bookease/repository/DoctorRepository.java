package com.bookease.repository;

import com.bookease.model.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    @Override
    @Query("SELECT d FROM Doctor d WHERE d.id = :uuid AND d.active = true")
    Optional<Doctor> findById(@Param("uuid") UUID uuid);

    @Query("SELECT d FROM Doctor d WHERE d.crm = :crm AND d.active = true")
    Optional<Doctor> findByCrm(@Param("crm") String crm);

    @Query("SELECT d FROM Doctor d JOIN d.specialities s WHERE UPPER(s.name) = UPPER(:specialityName) AND d.active = true")
    List<Doctor> findAllBySpeciality(@Param("specialityName") String specialityName);

    @Query("SELECT d FROM Doctor d JOIN d.user u JOIN u.roles r " +
            "WHERE u.name = :name AND r.name = 'DOCTOR' AND d.active = true")
    Optional<Doctor> findByUserNameWithDoctorRole(@NonNull @Param("name") String name);
}