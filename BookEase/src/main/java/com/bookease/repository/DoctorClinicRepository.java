package com.bookease.repository;

import com.bookease.model.entity.Clinic;
import com.bookease.model.entity.Doctor;
import com.bookease.model.entity.DoctorClinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorClinicRepository extends JpaRepository<DoctorClinic, UUID> {
    @Override
    @Query("SELECT dc FROM DoctorClinic dc WHERE dc.id = :uuid AND dc.active = true")
    Optional<DoctorClinic> findById(UUID uuid);

    @Query("SELECT dc FROM DoctorClinic dc WHERE dc.doctor.id = :doctorId AND dc.active = true")
    List<DoctorClinic> findDoctorClinicsByDoctor(@Param("doctorId") UUID doctorId);

    @Query("SELECT dc FROM DoctorClinic dc WHERE dc.clinic.id = :clinicId AND dc.active = true")
    List<DoctorClinic> findDoctorClinicsByClinic(@Param("clinicId") UUID clinicId);

    @Query("SELECT dc.doctor FROM DoctorClinic dc WHERE dc.clinic.id = :clinicId AND dc.active = true")
    List<Doctor> findDoctorsByClinic(@Param("clinicId") UUID clinicId);

    @Query("SELECT dc.clinic FROM DoctorClinic dc WHERE dc.doctor.id = :doctorId AND dc.active = true")
    List<Clinic> findClinicsByDoctor(@Param("doctorId") UUID doctorId);
}
