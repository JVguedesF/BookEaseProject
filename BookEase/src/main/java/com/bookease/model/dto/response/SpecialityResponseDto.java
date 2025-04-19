package com.bookease.model.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record SpecialityResponseDto(
        UUID id,
        String name,
        boolean active
) {
}