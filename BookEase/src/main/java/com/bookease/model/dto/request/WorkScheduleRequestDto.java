package com.bookease.model.dto.request;

import com.bookease.model.enums.DayOfWeekEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalTime;
import java.util.UUID;

@Builder
public record WorkScheduleRequestDto(
        @NotNull
        UUID doctorClinicId,

        @NotNull
        DayOfWeekEnum dayOfWeek,

        @NotNull
        LocalTime startTime,

        @NotNull
        LocalTime endTime,

        boolean active
) {}

