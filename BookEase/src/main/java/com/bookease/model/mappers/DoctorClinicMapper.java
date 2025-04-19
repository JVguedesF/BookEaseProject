package com.bookease.model.mappers;

import com.bookease.model.dto.request.DoctorClinicRequestDto;
import com.bookease.model.dto.response.DoctorClinicResponseDto;
import com.bookease.model.entity.Clinic;
import com.bookease.model.entity.Doctor;
import com.bookease.model.entity.DoctorClinic;
import org.springframework.stereotype.Component;

@Component
public class DoctorClinicMapper {

    public DoctorClinic toEntity(DoctorClinicRequestDto dto, Doctor doctor, Clinic clinic) {
        return DoctorClinic.builder()
                .id(dto.id())
                .doctor(doctor)
                .clinic(clinic)
                .active(true)
                .build();
    }

    public DoctorClinicResponseDto toResponseDto(DoctorClinic entity) {
        return DoctorClinicResponseDto.builder()
                .id(entity.getId())
                .doctorId(entity.getDoctor().getId())
                .clinicId(entity.getClinic().getId())
                .active(entity.isActive())
                .build();
    }
}
