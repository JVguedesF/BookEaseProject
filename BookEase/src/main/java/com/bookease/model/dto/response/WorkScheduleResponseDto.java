package com.bookease.model.dto.response;

import com.bookease.model.enums.DayOfWeekEnum;
import java.time.LocalTime;
import java.util.UUID;

public record WorkScheduleResponseDto(
        UUID id,
        UUID doctorClinicId,
        DayOfWeekEnum dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        boolean active
) {
}
