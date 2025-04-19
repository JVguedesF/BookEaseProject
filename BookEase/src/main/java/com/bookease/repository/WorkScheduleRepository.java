package com.bookease.repository;

import com.bookease.model.entity.WorkSchedule;
import com.bookease.model.enums.DayOfWeekEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, UUID> {
    List<WorkSchedule> findByDoctorClinicId(UUID doctorClinicId);

    List<WorkSchedule> findByDayOfWeek(DayOfWeekEnum dayOfWeek);

    List<WorkSchedule> findByActiveTrue();

    List<WorkSchedule> findByDoctorClinicIdAndDayOfWeek(UUID doctorClinicId, DayOfWeekEnum dayOfWeek);
}
