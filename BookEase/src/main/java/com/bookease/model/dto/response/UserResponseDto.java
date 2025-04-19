package com.bookease.model.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponseDto(
        UUID userId,
        String username,
        String name,
        String phone,
        String email,
        boolean active,
        boolean tokenRevoked
) {}