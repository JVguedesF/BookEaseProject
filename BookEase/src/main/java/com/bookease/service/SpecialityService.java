package com.bookease.service;

import com.bookease.exception.EntityAlreadyExistsException;
import com.bookease.exception.EntityNotFoundException;
import com.bookease.model.dto.request.SpecialityRequestDto;
import com.bookease.model.dto.response.SpecialityResponseDto;
import com.bookease.model.entity.Doctor;
import com.bookease.model.entity.Speciality;
import com.bookease.model.mappers.SpecialityMapper;
import com.bookease.repository.DoctorRepository;
import com.bookease.repository.SpecialityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SpecialityService {

    private static final String ENTITY_NAME = "Speciality";

    private final SpecialityRepository specialityRepository;
    private final DoctorRepository doctorRepository;
    private final SpecialityMapper specialityMapper;

    @Autowired
    public SpecialityService(SpecialityRepository specialityRepository,
                             DoctorRepository doctorRepository,
                             SpecialityMapper specialityMapper) {
        this.specialityRepository = specialityRepository;
        this.doctorRepository = doctorRepository;
        this.specialityMapper = specialityMapper;
    }

    @Transactional
    public List<SpecialityResponseDto> createSpeciality(SpecialityRequestDto dto) {
        dto.specialityNames().forEach(name -> {
            String normalizedName = name.trim().toLowerCase();
            if (specialityRepository.existsByNameIgnoreCase(normalizedName)) {
                throw new EntityAlreadyExistsException("Especialidade já existe: " + normalizedName);
            }
        });

        List<Speciality> specialities = specialityMapper.toEntities(dto);

        List<Speciality> savedSpecialities = specialityRepository.saveAll(specialities);

        return savedSpecialities.stream()
                .map(specialityMapper::toResponseDto)
                .toList();
    }


    public Optional<SpecialityResponseDto> findById(UUID id) {
        return specialityRepository.findById(id)
                .map(specialityMapper::toResponseDto);
    }

    public Optional<SpecialityResponseDto> findByName(String name) {
        return specialityRepository.findByName(name)
                .map(specialityMapper::toResponseDto);
    }

    public List<SpecialityResponseDto> findAll() {
        return specialityRepository.findAll()
                .stream()
                .map(specialityMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public void deleteById(UUID id) {
        Speciality speciality = specialityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id));
        doctorRepository.findAll().forEach(doctor -> doctor.getSpecialities().remove(speciality));
        specialityRepository.delete(speciality);
    }

    @Transactional
    public void deleteByName(String name) {
        Speciality speciality = specialityRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, name));
        doctorRepository.findAll().forEach(doctor -> doctor.getSpecialities().remove(speciality));
        specialityRepository.delete(speciality);
    }

    @Transactional
    public void updateSpeciality(UUID doctorId, Set<UUID> specialityIds) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, doctorId));

        Set<Speciality> specialities = specialityIds.stream()
                .map(id -> specialityRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id)))
                .collect(Collectors.toSet());

        doctor.setSpecialities(specialities);
        doctorRepository.save(doctor);
    }
}