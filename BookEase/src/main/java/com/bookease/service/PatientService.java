package com.bookease.service;

import com.bookease.exception.EntityNotFoundException;
import com.bookease.model.dto.request.PatientRequestDto;
import com.bookease.model.dto.request.UserRequestDto;
import com.bookease.model.dto.response.PatientResponseDto;
import com.bookease.model.entity.Patient;
import com.bookease.model.entity.Role;
import com.bookease.model.entity.User;
import com.bookease.model.mappers.PatientMapper;
import com.bookease.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PatientService {

    private static final String ENTITY_NAME = "Patient";

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final UserService userService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                          PatientMapper patientMapper,
                          UserService userService) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.userService = userService;
    }

    @Transactional
    public PatientResponseDto createPatient(PatientRequestDto patientDto) {
        userService.validateUniqueField(patientDto.cpf(), patientRepository,
                p -> p.getCpf().equals(patientDto.cpf()), "CPF");

        User user = userService.createUserWithRole(patientDto.userRequestDto(), Role.Values.PATIENT);

        Patient patient = patientMapper.toEntity(patientDto, user);
        patient = patientRepository.save(patient);

        return patientMapper.toResponseDto(patient);
    }

    public PatientResponseDto getPatientById(UUID patientId) {
        return userService.findByIdOrThrow(patientId, patientRepository,
                patientMapper::toResponseDto, ENTITY_NAME);
    }

    public PatientResponseDto getPatientByCpf(String cpf) {
        Patient patient = patientRepository.findByCpf(cpf)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, cpf));
        return patientMapper.toResponseDto(patient);
    }

    public PatientResponseDto getPatientByName(String name) {
        Patient patient = patientRepository.findByUserNameWithPatientRole(name)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, name));
        return patientMapper.toResponseDto(patient);
    }

    @Transactional
    public PatientResponseDto updatePatient(UUID patientId, UserRequestDto userUpdateDto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, patientId));

        User updatedUser = userService.updateUserEntity(patient, userUpdateDto, Patient::getUser);
        patient.setUser(updatedUser);
        patient = patientRepository.save(patient);

        return patientMapper.toResponseDto(patient);
    }

    @Transactional
    public void deactivatePatient(UUID patientId) {
        userService.deactivateEntity(patientId, patientRepository, Patient::getUser, true, ENTITY_NAME);
    }
}
