package com.bookease.service;

import com.bookease.model.dto.request.SpecialityRequestDto;
import com.bookease.model.dto.response.SpecialityResponseDto;
import com.bookease.model.entity.Doctor;
import com.bookease.model.entity.Speciality;
import com.bookease.model.mappers.SpecialityMapper;
import com.bookease.repository.DoctorRepository;
import com.bookease.repository.SpecialityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialityServiceTest {

    @Mock
    private SpecialityRepository specialityRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecialityMapper specialityMapper;

    @InjectMocks
    private SpecialityService specialityService;

    private Speciality speciality;
    private SpecialityRequestDto requestDto;
    private SpecialityResponseDto responseDto;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        UUID specialityId = UUID.randomUUID();
        speciality = Speciality.builder()
                .id(specialityId)
                .name("Ortodontia")
                .build();

        responseDto = SpecialityResponseDto.builder()
                .id(specialityId)
                .name("Ortodontia")
                .build();

        doctor = Doctor.builder()
                .id(UUID.randomUUID())
                .crm("12345")
                .specialities(new HashSet<>())
                .active(true)
                .build();
    }

    @Test
    void createSpeciality_ShouldCreateNewSpeciality_WhenNameDoesNotExist() {
        SpecialityRequestDto requestDto = new SpecialityRequestDto(List.of("Ortodontia"));
        Speciality speciality = Speciality.builder()
                .name("ortodontia")
                .active(true)
                .build();
        SpecialityResponseDto responseDto = SpecialityResponseDto.builder()
                .name("ortodontia")
                .active(true)
                .build();

        when(specialityRepository.existsByNameIgnoreCase("ortodontia")).thenReturn(false);
        when(specialityMapper.toEntities(requestDto)).thenReturn(List.of(speciality));
        when(specialityRepository.saveAll(List.of(speciality))).thenReturn(List.of(speciality));
        when(specialityMapper.toResponseDto(speciality)).thenReturn(responseDto);

        // Act
        List<SpecialityResponseDto> result = specialityService.createSpeciality(requestDto);

        // Assert
        assertThat(result).containsExactly(responseDto);
        verify(specialityRepository).saveAll(List.of(speciality));
    }

    @Test
    void createSpeciality_ShouldThrowException_WhenNameExists() {
        when(specialityRepository.findByName("Ortodontia")).thenReturn(Optional.of(speciality));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> specialityService.createSpeciality(requestDto));
        assertThat(exception.getMessage()).isEqualTo("Especialidade já existe");
        verify(specialityRepository, never()).save(any());
    }

    @Test
    void findById_ShouldReturnSpeciality_WhenIdExists() {
        when(specialityRepository.findById(speciality.getId())).thenReturn(Optional.of(speciality));
        when(specialityMapper.toResponseDto(speciality)).thenReturn(responseDto);

        Optional<SpecialityResponseDto> result = specialityService.findById(speciality.getId());

        assertThat(result).contains(responseDto);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenIdDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        when(specialityRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<SpecialityResponseDto> result = specialityService.findById(nonExistentId);

        assertThat(result).isNotPresent();
    }

    @Test
    void findByName_ShouldReturnSpeciality_WhenNameExists() {
        when(specialityRepository.findByName("Ortodontia")).thenReturn(Optional.of(speciality));
        when(specialityMapper.toResponseDto(speciality)).thenReturn(responseDto);

        Optional<SpecialityResponseDto> result = specialityService.findByName("Ortodontia");

        assertThat(result).contains(responseDto);
    }

    @Test
    void findByName_ShouldReturnEmpty_WhenNameDoesNotExist() {
        when(specialityRepository.findByName("Endodontia")).thenReturn(Optional.empty());

        Optional<SpecialityResponseDto> result = specialityService.findByName("Endodontia");

        assertThat(result).isNotPresent();
    }

    @Test
    void findAll_ShouldReturnListOfSpecialities() {
        List<Speciality> specialities = List.of(speciality);
        List<SpecialityResponseDto> responseDtos = List.of(responseDto);
        when(specialityRepository.findAll()).thenReturn(specialities);
        when(specialityMapper.toResponseDto(speciality)).thenReturn(responseDto);

        List<SpecialityResponseDto> result = specialityService.findAll();

        assertThat(result)
                .hasSize(1)
                .containsExactlyElementsOf(responseDtos);
    }

    @Test
    void deleteById_ShouldDeleteSpeciality_WhenIdExists() {
        when(specialityRepository.findById(speciality.getId())).thenReturn(Optional.of(speciality));
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));

        specialityService.deleteById(speciality.getId());

        verify(doctorRepository).findAll();
        verify(specialityRepository).delete(speciality);
        assertThat(doctor.getSpecialities()).doesNotContain(speciality);
    }

    @Test
    void deleteById_ShouldThrowException_WhenIdDoesNotExist() {
        UUID nonExistentId = UUID.randomUUID();
        when(specialityRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> specialityService.deleteById(nonExistentId));
        assertThat(exception.getMessage()).isEqualTo("Speciality with identifier " + nonExistentId + " not found");
        verify(specialityRepository, never()).delete(any());
    }

    @Test
    void deleteByName_ShouldDeleteSpeciality_WhenNameExists() {
        when(specialityRepository.findByName("Ortodontia")).thenReturn(Optional.of(speciality));
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));

        specialityService.deleteByName("Ortodontia");

        verify(doctorRepository).findAll();
        verify(specialityRepository).delete(speciality);
        assertThat(doctor.getSpecialities()).doesNotContain(speciality);
    }

    @Test
    void deleteByName_ShouldThrowException_WhenNameDoesNotExist() {
        when(specialityRepository.findByName("Endodontia")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> specialityService.deleteByName("Endodontia"));
        assertThat(exception.getMessage()).isEqualTo("Speciality with identifier Endodontia not found");
        verify(specialityRepository, never()).delete(any());
    }

    @Test
    void updateSpeciality_ShouldUpdateDoctorSpecialities_WhenDoctorAndSpecialitiesExist() {
        UUID doctorId = doctor.getId();
        Set<UUID> specialityIds = Set.of(speciality.getId());
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(specialityRepository.findById(speciality.getId())).thenReturn(Optional.of(speciality));
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        specialityService.updateSpeciality(doctorId, specialityIds);

        verify(doctorRepository).save(doctor);
        assertThat(doctor.getSpecialities()).containsExactly(speciality);
    }

    @Test
    void updateSpeciality_ShouldThrowException_WhenDoctorDoesNotExist() {
        UUID nonExistentDoctorId = UUID.randomUUID();
        Set<UUID> specialityIds = Set.of(speciality.getId());
        when(doctorRepository.findById(nonExistentDoctorId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> specialityService.updateSpeciality(nonExistentDoctorId, specialityIds));
        assertThat(exception.getMessage()).isEqualTo("Speciality with identifier " + nonExistentDoctorId + " not found");
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void updateSpeciality_ShouldThrowException_WhenSpecialityDoesNotExist() {
        UUID doctorId = doctor.getId();
        UUID nonExistentSpecialityId = UUID.randomUUID();
        Set<UUID> specialityIds = Set.of(nonExistentSpecialityId);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(specialityRepository.findById(nonExistentSpecialityId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> specialityService.updateSpeciality(doctorId, specialityIds));
        assertThat(exception.getMessage()).isEqualTo("Speciality with identifier " + nonExistentSpecialityId + " not found");
        verify(doctorRepository, never()).save(any());
    }
}