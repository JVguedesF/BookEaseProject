package com.bookease.model.mappers;

import com.bookease.model.dto.request.WorkScheduleRequestDto;
import com.bookease.model.dto.response.WorkScheduleResponseDto;
import com.bookease.model.entity.DoctorClinic;
import com.bookease.model.entity.WorkSchedule;
import org.springframework.stereotype.Component;

@Component
public class WorkScheduleMapper {

    public WorkSchedule toEntity(WorkScheduleRequestDto dto, DoctorClinic doctorClinic) {
        return WorkSchedule.builder()
                .doctorClinic(doctorClinic)
                .dayOfWeek(dto.dayOfWeek())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .active(dto.active())
                .build();
    }

    public WorkScheduleResponseDto toResponse(WorkSchedule entity) {
        return new WorkScheduleResponseDto(
                entity.getId(),
                entity.getDoctorClinic().getId(),
                entity.getDayOfWeek(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.isActive()
        );
    }
}
