package com.bookease.repository;

import com.bookease.model.entity.Appointment;
import com.bookease.model.entity.DoctorClinic;
import com.bookease.model.entity.Patient;
import com.bookease.model.enums.AppointmentEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByDoctorClinic(DoctorClinic doctorClinic);

    List<Appointment> findByStatus(AppointmentEnum status);

    List<Appointment> findByDateTime(LocalDateTime dateTime);

    List<Appointment> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Appointment> findByPatientAndActiveTrue(Patient patient);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctorClinic = :doctorClinic AND a.dateTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorClinicAndDateRange(@Param("doctorClinic") DoctorClinic doctorClinic,
                                                     @Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);
}