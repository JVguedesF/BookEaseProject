package com.bookease.model.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProcedureOfferedResponseDto(
        UUID id,
        UUID doctorClinicId,
        UUID procedureId,
        String procedureName,
        int durationMinutes,
        double price,
        boolean active
) {
}
