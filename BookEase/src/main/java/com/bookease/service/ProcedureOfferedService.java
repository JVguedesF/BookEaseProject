package com.bookease.service;

import com.bookease.exception.EntityNotFoundException;
import com.bookease.model.dto.request.ProcedureOfferedRequestDto;
import com.bookease.model.dto.response.ProcedureOfferedResponseDto;
import com.bookease.model.entity.DoctorClinic;
import com.bookease.model.entity.Procedure;
import com.bookease.model.enums.ProcedureEnum;
import com.bookease.model.entity.ProcedureOffered;
import com.bookease.model.mappers.ProcedureOfferedMapper;
import com.bookease.repository.DoctorClinicRepository;
import com.bookease.repository.ProcedureOfferedRepository;
import com.bookease.repository.ProcedureRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProcedureOfferedService {

    private final ProcedureOfferedRepository procedureOfferedRepository;
    private final DoctorClinicRepository doctorClinicRepository;
    private final ProcedureRepository procedureRepository;
    private final ProcedureOfferedMapper procedureOfferedMapper;

    @Autowired
    public ProcedureOfferedService(ProcedureOfferedRepository procedureOfferedRepository,
                                   ProcedureRepository procedureRepository,
                                   DoctorClinicRepository doctorClinicRepository,
                                   ProcedureOfferedMapper procedureOfferedMapper) {
        this.procedureOfferedRepository = procedureOfferedRepository;
        this.doctorClinicRepository = doctorClinicRepository;
        this.procedureRepository = procedureRepository;
        this.procedureOfferedMapper = procedureOfferedMapper;
    }

    @Transactional
    public ProcedureOffered createProcedureOffered(ProcedureOfferedRequestDto requestDto) {

        DoctorClinic doctorClinic = doctorClinicRepository.findById(requestDto.doctorClinicId())
                .orElseThrow(() -> new EntityNotFoundException("DoctorClinic", requestDto.doctorClinicId()));

        Procedure procedure = procedureRepository.findActiveByProcedureEnum(requestDto.procedureEnum())
                .orElseThrow(() -> new EntityNotFoundException("Procedure", requestDto.procedureEnum()));

        ProcedureOffered procedureOffered = procedureOfferedMapper.toEntity(
                doctorClinic,
                procedure,
                requestDto.durationMinutes(),
                requestDto.price()
        );
        return procedureOfferedRepository.save(procedureOffered);
    }

    public boolean getExistsByIdAndActiveTrue(UUID id) {
        return procedureOfferedRepository.existsByIdAndActiveTrue(id);
    }

    public long getCountByDoctorClinicIdAndActiveTrue(UUID doctorClinicId) {
        return procedureOfferedRepository.countByDoctorClinicIdAndActiveTrue(doctorClinicId);
    }

    public List<ProcedureOfferedResponseDto> getByPriceBetween(double minPrice, double maxPrice) {
        List<ProcedureOffered> procedures = procedureOfferedRepository.findByPriceBetween(minPrice, maxPrice);
        return procedures.stream()
                .map(procedureOfferedMapper::toResponseDto)
                .toList();
    }

    public List<ProcedureOfferedResponseDto> getActiveByDoctorClinicIdOrderByPriceAsc(UUID doctorClinicId) {
        List<ProcedureOffered> procedures = procedureOfferedRepository.findActiveByDoctorClinicIdOrderByPriceAsc(doctorClinicId);
        return procedures.stream()
                .map(procedureOfferedMapper::toResponseDto)
                .toList();
    }

    public List<ProcedureOfferedResponseDto> getActiveByDoctorClinicIdAndPriceBetween(UUID doctorClinicId, double minPrice, double maxPrice) {
        List<ProcedureOffered> procedures = procedureOfferedRepository.findActiveByDoctorClinicIdAndPriceBetween(doctorClinicId, minPrice, maxPrice);
        return procedures.stream()
                .map(procedureOfferedMapper::toResponseDto)
                .toList();
    }

    public List<ProcedureOfferedResponseDto> getByProcedureNameAndActiveTrue(String procedureName) {
        List<ProcedureOffered> procedures = procedureOfferedRepository.findByProcedureNameAndActiveTrue(procedureName);
        return procedures.stream()
                .map(procedureOfferedMapper::toResponseDto)
                .toList();
    }

    public List<ProcedureOfferedResponseDto> getByDoctorClinicIdAndDurationMinutesGreaterThanEqualAndActiveTrue(UUID doctorClinicId, int minDuration) {
        List<ProcedureOffered> procedures = procedureOfferedRepository.findByDoctorClinicIdAndDurationMinutesGreaterThanEqualAndActiveTrue(doctorClinicId, minDuration);
        return procedures.stream()
                .map(procedureOfferedMapper::toResponseDto)
                .toList();
    }

    public List<ProcedureOfferedResponseDto> getByProcedureDisplayNameAndActiveTrue(String displayName) {
        List<ProcedureOffered> procedures = procedureOfferedRepository.findByProcedureDisplayNameAndActiveTrue(displayName);
        return procedures.stream()
                .map(procedureOfferedMapper::toResponseDto)
                .toList();
    }

    public List<ProcedureOfferedResponseDto> getByProcedureEnumAndActiveTrueOrderByPriceAsc(ProcedureEnum procedureEnum) {
        List<ProcedureOffered> procedures = procedureOfferedRepository.findByProcedureEnumAndActiveTrueOrderByPriceAsc(procedureEnum);
        return procedures.stream()
                .map(procedureOfferedMapper::toResponseDto)
                .toList();
    }

    public List<ProcedureOfferedResponseDto> getByDoctorClinicIdAndProcedureDisplayNameContainingAndActiveTrue(UUID doctorClinicId, String keyword) {
        List<ProcedureOffered> procedures = procedureOfferedRepository.findByDoctorClinicIdAndProcedureDisplayNameContainingAndActiveTrue(doctorClinicId, keyword);
        return procedures.stream()
                .map(procedureOfferedMapper::toResponseDto)
                .toList();
    }

    public List<ProcedureOfferedResponseDto> getByDurationMinutesAndPriceBetweenAndActiveTrue(int duration, double minPrice, double maxPrice) {
        List<ProcedureOffered> procedures = procedureOfferedRepository.findByDurationMinutesAndPriceBetweenAndActiveTrue(duration, minPrice, maxPrice);
        return procedures.stream()
                .map(procedureOfferedMapper::toResponseDto)
                .toList();
    }

    public List<ProcedureOfferedResponseDto> getByDoctorClinicIdAndActiveTrueOrderByDurationMinutesAsc(UUID doctorClinicId) {
        List<ProcedureOffered> procedures = procedureOfferedRepository.findByDoctorClinicIdAndActiveTrueOrderByDurationMinutesAsc(doctorClinicId);
        return procedures.stream()
                .map(procedureOfferedMapper::toResponseDto)
                .toList();
    }

    public long getCountByDoctorClinicIdAndProcedureEnumAndActiveTrue(UUID doctorClinicId, ProcedureEnum procedureEnum) {
        return procedureOfferedRepository.countByDoctorClinicIdAndProcedureEnumAndActiveTrue(doctorClinicId, procedureEnum);
    }


}

