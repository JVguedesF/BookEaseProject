package com.bookease.model.mappers;

import com.bookease.model.dto.request.RoleRequestDto;
import com.bookease.model.dto.response.RoleResponseDto;
import com.bookease.model.entity.Role;
import org.springframework.stereotype.Component;
@Component
public class RoleMapper {

    public Role toEntity(RoleRequestDto dto) {
        return Role.builder()
                .name(dto.name())
                .build();
    }

    public RoleResponseDto toResponseDto(Role entity) {
        return RoleResponseDto.builder()
                .id(entity.getRoleId())
                .name(entity.getName())
                .build();
    }
}