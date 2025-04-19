package com.bookease.model.mappers;

import com.bookease.model.dto.response.ProcedureOfferedResponseDto;
import com.bookease.model.entity.DoctorClinic;
import com.bookease.model.entity.Procedure;
import com.bookease.model.entity.ProcedureOffered;
import org.springframework.stereotype.Component;

@Component
public class ProcedureOfferedMapper {

    public ProcedureOffered toEntity(DoctorClinic doctorClinic, Procedure procedure, int durationMinutes, double price) {
        return ProcedureOffered.builder()
                .doctorClinic(doctorClinic)
                .procedure(procedure)
                .durationMinutes(durationMinutes)
                .price(price)
                .active(true)
                .build();
    }

    public ProcedureOfferedResponseDto toResponseDto(ProcedureOffered procedureOffered) {
        return ProcedureOfferedResponseDto.builder()
                .id(procedureOffered.getId())
                .doctorClinicId(procedureOffered.getDoctorClinic().getId())
                .procedureId(procedureOffered.getProcedure().getId())
                .procedureName(procedureOffered.getProcedure().getProcedureEnum().getDisplayName())
                .durationMinutes(procedureOffered.getDurationMinutes())
                .price(procedureOffered.getPrice())
                .active(procedureOffered.isActive())
                .build();
    }
}