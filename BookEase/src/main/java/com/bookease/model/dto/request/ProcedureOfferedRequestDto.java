package com.bookease.model.dto.request;

import com.bookease.model.enums.ProcedureEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ProcedureOfferedRequestDto(
        @NotNull(message = "Doctor Clinic Id cannot be null")
        UUID doctorClinicId,

        @NotNull(message = "Procedure cannot be null")
        ProcedureEnum procedureEnum,

        @Min(value = 1, message = "Duration must be at least 1 minute")
        int durationMinutes,

        @Positive(message = "Price must be positive")
        double price,

        boolean active
) {
}
