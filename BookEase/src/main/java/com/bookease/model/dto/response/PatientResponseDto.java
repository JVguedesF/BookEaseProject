package com.bookease.model.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PatientResponseDto(
        UUID id,
        UUID userId,
        String username,
        String name,
        String phone,
        String email,
        String cpf,
        boolean active,
        boolean tokenRevoked
) {}