package com.bookease.model.dto.request;

import com.bookease.model.entity.Role;
import lombok.Builder;

@Builder
public record RoleRequestDto(
        Role.Values name
) {}