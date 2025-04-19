package com.bookease.model.mappers;


import com.bookease.model.dto.request.PatientRequestDto;
import com.bookease.model.dto.response.PatientResponseDto;
import com.bookease.model.entity.Patient;
import com.bookease.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequestDto dto, User user) {
        return Patient.builder()
                .user(user)
                .cpf(dto.cpf())
                .active(true)
                .build();
    }

    public PatientResponseDto toResponseDto(Patient entity) {
        User user = entity.getUser();
        return PatientResponseDto.builder()
                .id(entity.getId())
                .userId(user.getUserId())
                .username(user.getUsername())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .cpf(entity.getCpf())
                .active(entity.isActive())
                .tokenRevoked(user.isTokenRevoked())
                .build();
    }
}