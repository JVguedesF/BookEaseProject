package com.bookease.model.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DoctorClinicRequestDto(
        UUID id,
        UUID doctorId,
        UUID clinicId
        ) {}
