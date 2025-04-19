package com.bookease.service;

import com.bookease.exception.EntityNotFoundException;
import com.bookease.model.dto.request.ClinicRequestDto;
import com.bookease.model.dto.response.ClinicResponseDto;
import com.bookease.model.entity.Clinic;
import com.bookease.model.entity.Role;
import com.bookease.model.entity.User;
import com.bookease.model.mappers.ClinicMapper;
import com.bookease.repository.ClinicRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClinicService {

    private static final String ENTITY_NAME = "Clinic";

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;
    private final UserService userService;

    @Autowired
    public ClinicService(ClinicRepository clinicRepository,
                         ClinicMapper clinicMapper,
                         UserService userService) {
        this.clinicRepository = clinicRepository;
        this.clinicMapper = clinicMapper;
        this.userService = userService;
    }

    @Transactional
    public ClinicResponseDto createClinic(ClinicRequestDto clinicDto) {
        userService.validateUniqueField(clinicDto.cnpj(), clinicRepository,
                c -> c.getCnpj().equals(clinicDto.cnpj()), "CNPJ");

        User user = userService.createUserWithRole(clinicDto.userRequestDto(), Role.Values.CLINIC);

        Clinic clinic = clinicMapper.toEntity(clinicDto, user);
        clinic = clinicRepository.save(clinic);

        return clinicMapper.toResponseDto(clinic);
    }

    public ClinicResponseDto getClinicById(UUID clinicId) {
        return userService.findByIdOrThrow(clinicId, clinicRepository,
                clinicMapper::toResponseDto, ENTITY_NAME);
    }

    public ClinicResponseDto getClinicByCnpj(String cnpj) {
        Clinic clinic = clinicRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, cnpj));
        return clinicMapper.toResponseDto(clinic);
    }

    public ClinicResponseDto getClinicByName(String name) {
        List<Clinic> clinics = clinicRepository.findByUserName(name);

        if (clinics.isEmpty()) {
            throw new EntityNotFoundException(ENTITY_NAME, name);
        } else {
            return clinicMapper.toResponseDto(clinics.getFirst());
        }
    }

    public ClinicResponseDto getClinicByCity(String city) {
        List<Clinic> clinics = clinicRepository.findAllByCity(city);

        if (clinics.isEmpty()) {
            throw new EntityNotFoundException(ENTITY_NAME, city);
        } else {
            return clinicMapper.toResponseDto(clinics.getFirst());
        }
    }

    public ClinicResponseDto updateClinic(UUID clinicId, ClinicRequestDto clinicDto) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, clinicId));

        User updatedUser = userService.updateUserEntity(clinic, clinicDto.userRequestDto(), Clinic::getUser);
        clinic.setUser(updatedUser);
        clinic = clinicRepository.save(clinic);

        return clinicMapper.toResponseDto(clinic);
    }

    public void deactivateClinic(UUID clinicId) {
       userService.deactivateEntity(clinicId, clinicRepository, Clinic::getUser, true, ENTITY_NAME);
    }

}
