package com.bookease.model.dto.response;

import com.bookease.model.enums.AppointmentEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponseDto(
        UUID id,
        LocalDateTime dateTime,
        String description,
        AppointmentEnum status,
        UUID procedureOfferedId,
        UUID doctorClinicId,
        UUID patientId,
        UUID workScheduleId,
        boolean active
) {
}