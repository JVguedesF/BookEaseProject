package com.bookease.model.mappers;

import com.bookease.model.dto.request.ClinicRequestDto;
import com.bookease.model.dto.response.ClinicResponseDto;
import com.bookease.model.entity.Clinic;
import com.bookease.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ClinicMapper {

    public Clinic toEntity(ClinicRequestDto dto, User user) {
        return Clinic.builder()
                .cnpj(dto.cnpj())
                .user(user)
                .description(dto.description())
                .city(dto.city())
                .address(dto.address())
                .active(true)
                .build();
    }

    public ClinicResponseDto toResponseDto(Clinic entity) {
        User user = entity.getUser();
        return ClinicResponseDto.builder()
                .id(entity.getId())
                .userId(user.getUserId())
                .username(user.getUsername())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .cnpj(entity.getCnpj())
                .description(entity.getDescription())
                .city(entity.getCity())
                .address(entity.getAddress())
                .active(entity.isActive())
                .build();

    }

}
