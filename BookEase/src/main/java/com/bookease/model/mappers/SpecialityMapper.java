package com.bookease.model.mappers;

import com.bookease.model.dto.request.SpecialityRequestDto;
import com.bookease.model.dto.response.SpecialityResponseDto;
import com.bookease.model.entity.Speciality;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpecialityMapper {

    public List<Speciality> toEntities(SpecialityRequestDto dto) {
        return dto.specialityNames().stream()
                .map(name -> Speciality.builder()
                        .name(name)
                        .active(true)
                        .build())
                .toList();
    }


    public SpecialityResponseDto toResponseDto(Speciality entity) {
        return SpecialityResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .active(entity.isActive())
                .build();
    }
}
