package com.bookease.service;

import com.bookease.exception.EntityNotFoundException;
import com.bookease.model.dto.request.DoctorClinicRequestDto;
import com.bookease.model.dto.response.ClinicResponseDto;
import com.bookease.model.dto.response.DoctorClinicResponseDto;
import com.bookease.model.dto.response.DoctorResponseDto;
import com.bookease.model.entity.Clinic;
import com.bookease.model.entity.Doctor;
import com.bookease.model.entity.DoctorClinic;
import com.bookease.model.mappers.ClinicMapper;
import com.bookease.model.mappers.DoctorClinicMapper;
import com.bookease.model.mappers.DoctorMapper;
import com.bookease.repository.ClinicRepository;
import com.bookease.repository.DoctorClinicRepository;
import com.bookease.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DoctorClinicService {

    private static final String ENTITY_NAME = "DoctorClinic";

    private final DoctorClinicRepository doctorClinicRepository;

    private final ClinicRepository clinicRepository;

    private final DoctorRepository doctorRepository;

    private final DoctorClinicMapper doctorClinicMapper;

    private final DoctorMapper doctorMapper;

    private final ClinicMapper clinicMapper;


    @Autowired
    public DoctorClinicService(DoctorClinicRepository doctorClinicRepository,
                               ClinicRepository clinicRepository,
                               DoctorRepository doctorRepository,
                               DoctorClinicMapper doctorClinicMapper,
                               DoctorMapper doctorMapper,
                               ClinicMapper clinicMapper) {
        this.doctorClinicRepository = doctorClinicRepository;
        this.clinicRepository = clinicRepository;
        this.doctorRepository = doctorRepository;
        this.doctorClinicMapper = doctorClinicMapper;
        this.doctorMapper = doctorMapper;
        this.clinicMapper = clinicMapper;
    }

    public DoctorClinicResponseDto createDoctorClinic(DoctorClinicRequestDto doctorClinicDto) {
        Doctor doctor = doctorRepository.findById(doctorClinicDto.doctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor", doctorClinicDto.doctorId().toString()));

        Clinic clinic = clinicRepository.findById(doctorClinicDto.clinicId())
                .orElseThrow(() -> new EntityNotFoundException("Clinic", doctorClinicDto.clinicId().toString()));

        DoctorClinic doctorClinic = doctorClinicMapper.toEntity(doctorClinicDto, doctor, clinic);
        doctorClinic = doctorClinicRepository.save(doctorClinic);
        return doctorClinicMapper.toResponseDto(doctorClinic);
    }

    public DoctorClinicResponseDto getDoctorClinicById(UUID doctorClinicId) {
        DoctorClinic doctorClinic = doctorClinicRepository.findById(doctorClinicId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, doctorClinicId.toString()));
        return doctorClinicMapper.toResponseDto(doctorClinic);
    }

    public List<DoctorClinicResponseDto> getDoctorClinicByDoctorId(UUID doctorId) {
        List<DoctorClinic> doctorClinics = doctorClinicRepository.findDoctorClinicsByDoctor(doctorId);
        if (doctorClinics.isEmpty()) {
            throw new EntityNotFoundException(ENTITY_NAME, doctorId.toString());
        }
        return doctorClinics.stream()
                .map(doctorClinicMapper::toResponseDto)
                .toList();
    }

    public List<DoctorClinicResponseDto> getDoctorClinicByClinicId(UUID clinicId) {
        List<DoctorClinic> doctorClinics = doctorClinicRepository.findDoctorClinicsByClinic(clinicId);
        if (doctorClinics.isEmpty()) {
            throw new EntityNotFoundException(ENTITY_NAME, clinicId.toString());
        }
        return doctorClinics.stream()
                .map(doctorClinicMapper::toResponseDto)
                .toList();
    }

    public List<DoctorResponseDto> getDoctorsByClinicId(UUID clinicId) {
        List<Doctor> doctors = doctorClinicRepository.findDoctorsByClinic(clinicId);
        if (doctors.isEmpty()) {
            throw new EntityNotFoundException("Doctor", clinicId.toString());
        }
        return doctors.stream()
                .map(doctorMapper::toResponseDto)
                .toList();
    }

    public List<ClinicResponseDto> getClinicsByDoctorId(UUID doctorId) {
        List<Clinic> clinics = doctorClinicRepository.findClinicsByDoctor(doctorId);
        if (clinics.isEmpty()) {
            throw new EntityNotFoundException("Clinic", doctorId.toString());
        }
        return clinics.stream()
                .map(clinicMapper::toResponseDto)
                .toList();
    }
}
