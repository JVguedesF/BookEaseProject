package com.bookease.model.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DoctorClinicResponseDto(
        UUID id,
        UUID doctorId,
        UUID clinicId,
        boolean active
) {
}
