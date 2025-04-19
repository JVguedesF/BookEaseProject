package com.bookease.service;

import com.bookease.exception.EntityNotFoundException;
import com.bookease.model.dto.request.WorkScheduleRequestDto;
import com.bookease.model.dto.response.WorkScheduleResponseDto;
import com.bookease.model.entity.DoctorClinic;
import com.bookease.model.entity.WorkSchedule;
import com.bookease.model.mappers.WorkScheduleMapper;
import com.bookease.repository.DoctorClinicRepository;
import com.bookease.repository.WorkScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WorkScheduleService {

    private static final String ENTITY_NAME = "WorkSchedule";

    private final WorkScheduleRepository workScheduleRepository;
    private final DoctorClinicRepository doctorClinicRepository;
    private final WorkScheduleMapper workScheduleMapper;

    public WorkScheduleService(WorkScheduleRepository workScheduleRepository,
                               DoctorClinicRepository doctorClinicRepository,
                               WorkScheduleMapper workScheduleMapper) {
        this.workScheduleRepository = workScheduleRepository;
        this.doctorClinicRepository = doctorClinicRepository;
        this.workScheduleMapper = workScheduleMapper;
    }

    @Transactional
    public WorkScheduleResponseDto createWorkSchedule(WorkScheduleRequestDto dto) {
        DoctorClinic doctorClinic = doctorClinicRepository.findById(dto.doctorClinicId())
                .orElseThrow(() -> new EntityNotFoundException("DoctorClinic", dto.doctorClinicId()));
        WorkSchedule workSchedule = workScheduleMapper.toEntity(dto, doctorClinic);
        workSchedule = workScheduleRepository.save(workSchedule);
        return workScheduleMapper.toResponse(workSchedule);
    }

    public WorkScheduleResponseDto getWorkSchedule(UUID id) {
        WorkSchedule workSchedule = workScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
        return workScheduleMapper.toResponse(workSchedule);
    }

    public List<WorkScheduleResponseDto> getAllWorkSchedules() {
        return workScheduleRepository.findAll().stream()
                .map(workScheduleMapper::toResponse)
                .toList();
    }

    @Transactional
    public WorkScheduleResponseDto updateWorkSchedule(UUID id, WorkScheduleRequestDto dto) {
        WorkSchedule workSchedule = workScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
        updateFields(workSchedule, dto, false);
        workSchedule = workScheduleRepository.save(workSchedule);
        return workScheduleMapper.toResponse(workSchedule);
    }

    @Transactional
    public WorkScheduleResponseDto patchWorkSchedule(UUID id, WorkScheduleRequestDto dto) {
        WorkSchedule workSchedule = workScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
        updateFields(workSchedule, dto, true);
        workSchedule = workScheduleRepository.save(workSchedule);
        return workScheduleMapper.toResponse(workSchedule);
    }

    @Transactional
    public void deleteWorkSchedule(UUID id) {
        WorkSchedule workSchedule = workScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
        workSchedule.setActive(false);
        workScheduleRepository.save(workSchedule);
    }

    private void updateFields(WorkSchedule workSchedule, WorkScheduleRequestDto dto, boolean isPatch) {
        if (dto.doctorClinicId() != null) {
            DoctorClinic doctorClinic = doctorClinicRepository.findById(dto.doctorClinicId())
                    .orElseThrow(() -> new EntityNotFoundException("DoctorClinic", dto.doctorClinicId()));
            workSchedule.setDoctorClinic(doctorClinic);
        } else if (!isPatch) {
            throw new IllegalArgumentException("doctorClinicId é obrigatório para update completo");
        }

        if (dto.dayOfWeek() != null || !isPatch) {
            workSchedule.setDayOfWeek(dto.dayOfWeek());
        }
        if (dto.startTime() != null || !isPatch) {
            workSchedule.setStartTime(dto.startTime());
        }
        if (dto.endTime() != null || !isPatch) {
            workSchedule.setEndTime(dto.endTime());
        }
        workSchedule.setActive(dto.active());
    }
}
