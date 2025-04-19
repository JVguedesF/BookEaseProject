package com.bookease.model.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ClinicResponseDto(
        UUID id,
        UUID userId,
        String username,
        String name,
        String phone,
        String email,
        String cnpj,
        String description,
        String city,
        String address,
        boolean active
) {
}
