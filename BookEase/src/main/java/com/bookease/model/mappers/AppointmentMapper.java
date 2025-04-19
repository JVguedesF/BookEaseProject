package com.bookease.model.mappers;

import com.bookease.model.dto.request.AppointmentRequestDto;
import com.bookease.model.dto.response.AppointmentResponseDto;
import com.bookease.model.entity.Appointment;
import com.bookease.model.entity.DoctorClinic;
import com.bookease.model.entity.Patient;
import com.bookease.model.entity.ProcedureOffered;
import com.bookease.model.entity.WorkSchedule;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequestDto dto, ProcedureOffered procedureOffered,
                                DoctorClinic doctorClinic, Patient patient, WorkSchedule workSchedule) {
        return Appointment.builder()
                .dateTime(dto.dateTime())
                .description(dto.description())
                .status(dto.status())
                .procedureOffered(procedureOffered)
                .doctorClinic(doctorClinic)
                .patient(patient)
                .workSchedule(workSchedule)
                .active(true)
                .build();
    }

    public AppointmentResponseDto toResponseDto(Appointment entity) {
        return new AppointmentResponseDto(
                entity.getId(),
                entity.getDateTime(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getProcedureOffered().getId(),
                entity.getDoctorClinic().getId(),
                entity.getPatient().getId(),
                entity.getWorkSchedule().getId(),
                entity.isActive()
        );
    }
}