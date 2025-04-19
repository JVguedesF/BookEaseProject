package com.bookease.model.dto.response;

import com.bookease.model.entity.Role;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RoleResponseDto(
        UUID id,
        Role.Values name
) {}