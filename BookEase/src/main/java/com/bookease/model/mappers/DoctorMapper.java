package com.bookease.model.mappers;

import com.bookease.model.dto.request.DoctorRequestDto;
import com.bookease.model.dto.response.DoctorResponseDto;
import com.bookease.model.dto.response.SpecialityResponseDto;
import com.bookease.model.entity.Doctor;
import com.bookease.model.entity.Speciality;
import com.bookease.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DoctorMapper {

    public Doctor toEntity(DoctorRequestDto dto, User user, Set<Speciality> specialities) {
        return Doctor.builder()
                .user(user)
                .specialities(specialities)
                .crm(dto.getCrm())
                .active(true)
                .build();
    }

    public DoctorResponseDto toResponseDto(Doctor entity) {
        User user = entity.getUser();
        Set<SpecialityResponseDto> specialities = entity.getSpecialities().stream()
                .map(speciality -> SpecialityResponseDto.builder()
                        .id(speciality.getId())
                        .name(speciality.getName())
                        .build())
                .collect(Collectors.toSet());

        return DoctorResponseDto.builder()
                .id(entity.getId())
                .userId(user.getUserId())
                .username(user.getUsername())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .crm(entity.getCrm())
                .specialities(specialities)
                .active(entity.isActive())
                .build();
    }
}
