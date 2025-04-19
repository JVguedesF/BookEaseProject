package com.bookease.service;

import com.bookease.exception.EntityNotFoundException;
import com.bookease.exception.ResourceNotFoundException;
import com.bookease.model.dto.request.DoctorRequestDto;
import com.bookease.model.dto.request.UserRequestDto;
import com.bookease.model.dto.response.DoctorResponseDto;
import com.bookease.model.entity.*;
import com.bookease.model.mappers.DoctorMapper;
import com.bookease.repository.DoctorRepository;
import com.bookease.repository.SpecialityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private static final String ENTITY_NAME = "Doctor";

    private final DoctorRepository doctorRepository;
    private final SpecialityRepository specialityRepository;
    private final DoctorMapper doctorMapper;
    private final UserService userService;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         SpecialityRepository specialityRepository,
                         DoctorMapper doctorMapper,
                         UserService userService) {
        this.doctorRepository = doctorRepository;
        this.specialityRepository = specialityRepository;
        this.doctorMapper = doctorMapper;
        this.userService = userService;
    }

    @Transactional
    public DoctorResponseDto createDoctor(DoctorRequestDto doctorDto) {
        userService.validateUniqueField(doctorDto.getCrm(), doctorRepository,
                d -> d.getCrm().equals(doctorDto.getCrm()), "CRM");

        User user = userService.createUserWithRole(doctorDto.getUserRequestDto(), Role.Values.DOCTOR);
        Set<Speciality> specialities = fetchSpecialitiesByNames(doctorDto.getSpecialityNames());

        Doctor doctor = doctorMapper.toEntity(doctorDto, user, specialities);

        doctor = doctorRepository.save(doctor);

        return doctorMapper.toResponseDto(doctor);
    }

    public DoctorResponseDto getDoctorById(UUID doctorId) {
        return userService.findByIdOrThrow(doctorId, doctorRepository,
                doctorMapper::toResponseDto, ENTITY_NAME);
    }

    public DoctorResponseDto getDoctorByName(String name) {
        Doctor doctor = doctorRepository.findByUserNameWithDoctorRole(name)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, name));
        return doctorMapper.toResponseDto(doctor);
    }

    public List<DoctorResponseDto> getDoctorsBySpeciality(String specialityName) {
        List<Doctor> doctors = doctorRepository.findAllBySpeciality(specialityName);
        if (doctors.isEmpty()) {
            throw new EntityNotFoundException(ENTITY_NAME, specialityName);
        }
        return doctors.stream()
                .map(doctorMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public DoctorResponseDto updateDoctor(UUID doctorId, UserRequestDto userUpdateDto) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, doctorId));

        User updatedUser = userService.updateUserEntity(doctor, userUpdateDto, Doctor::getUser);
        doctor.setUser(updatedUser);
        doctorRepository.save(doctor);

        return doctorMapper.toResponseDto(doctor);
    }

    @Transactional
    public DoctorResponseDto addSpecialities(UUID doctorId, List<String> specialityNames) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado"));

        List<Speciality> specialities = specialityRepository.findAllByNameIn(specialityNames);
        if (specialities.isEmpty()) {
            throw new ResourceNotFoundException("Nenhuma especialidade encontrada para os nomes fornecidos");
        }

        for (Speciality speciality : specialities) {
            if (!doctor.getSpecialities().contains(speciality)) {
                doctor.getSpecialities().add(speciality);
            }
        }

        doctorRepository.save(doctor);
        return doctorMapper.toResponseDto(doctor);
    }



    public void deactivateDoctor(UUID doctorId) {
        userService.deactivateEntity(doctorId, doctorRepository, Doctor::getUser, true, ENTITY_NAME);
    }

    private Set<Speciality> fetchSpecialitiesByNames(Set<String> specialityNames) {
        if (specialityNames == null || specialityNames.isEmpty()) {
            return Collections.emptySet();
        }

        List<String> displayNames = specialityNames.stream()
                .map(String::toUpperCase)
                .toList();

        List<Speciality> foundSpecialities = specialityRepository.findByNameInIgnoreCase(displayNames);

        if (foundSpecialities.size() != specialityNames.size()) {
            Set<String> missingNames = new HashSet<>(specialityNames);
            missingNames.removeAll(foundSpecialities.stream()
                    .map(Speciality::getName)
                    .collect(Collectors.toSet()));
            throw new EntityNotFoundException("Speciality", missingNames);
        }
        return new HashSet<>(foundSpecialities);
    }
}