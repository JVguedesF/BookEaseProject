package com.bookease.model.dto.response;

import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record DoctorResponseDto(
        UUID id,
        UUID userId,
        String username,
        String name,
        String phone,
        String email,
        String crm,
        Set<SpecialityResponseDto> specialities,
        boolean active
) {
}
