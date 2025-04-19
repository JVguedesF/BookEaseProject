package com.bookease.service;

import com.bookease.exception.EntityNotFoundException;
import com.bookease.model.dto.request.DoctorRequestDto;
import com.bookease.model.dto.request.UserRequestDto;
import com.bookease.model.dto.response.DoctorResponseDto;
import com.bookease.model.entity.*;
import com.bookease.model.enums.SpecialityEnum;
import com.bookease.model.mappers.DoctorMapper;
import com.bookease.repository.DoctorRepository;
import com.bookease.repository.SpecialityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecialityRepository specialityRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;
    private DoctorRequestDto doctorRequestDto;
    private DoctorResponseDto doctorResponseDto;
    private UserRequestDto userDto;
    private User user;
    private Speciality speciality;
    private UUID doctorId;

    private static final String ENTITY_NAME = "Doctor";

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        user = User.builder()
                .userId(UUID.randomUUID())
                .username("doctor1")
                .password("hashedpass")
                .name("Dr. John Doe")
                .email("doctor@example.com")
                .phone("11999998888")
                .active(true)
                .tokenRevoked(false)
                .build();

        speciality = Speciality.builder()
                .id(UUID.randomUUID())
                .name("Cardiology")
                .build();

        doctor = Doctor.builder()
                .id(doctorId)
                .user(user)
                .crm("CRM12345")
                .specialities(new HashSet<>())
                .active(true)
                .build();

        userDto = UserRequestDto.builder()
                .username("doctor1")
                .password("password")
                .name("Dr. John Doe")
                .email("doctor@example.com")
                .phone("11999998888")
                .build();

        doctorRequestDto = DoctorRequestDto.builder()
                .userRequestDto(userDto)
                .crm("CRM12345")
                .specialityNames(Set.of(speciality.getName()))
                .build();

        doctorResponseDto = DoctorResponseDto.builder()
                .id(doctorId)
                .userId(user.getUserId())
                .username(user.getUsername())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .crm(doctor.getCrm())
                .specialities(new HashSet<>())
                .active(true)
                .build();
    }

    @Test
    void createDoctor_ShouldCreateNewDoctor_WhenDataValid() {
        doNothing().when(userService).validateUniqueField(
                eq(doctorRequestDto.getCrm()),
                eq(doctorRepository),
                any(),
                eq("CRM")
        );


        List<String> specialityNames = doctorRequestDto.getSpecialityNames().stream()
                .map(name -> SpecialityEnum.valueOf(name).getDisplayName())
                .collect(Collectors.toList());
        when(specialityRepository.findByNameIn(eq(specialityNames)))
                .thenReturn(Collections.singletonList(speciality));

        when(userService.createUserWithRole(userDto, Role.Values.DOCTOR))
                .thenReturn(user);

        when(doctorMapper.toEntity(eq(doctorRequestDto), eq(user), any()))
                .thenReturn(doctor);

        when(doctorRepository.save(doctor)).thenReturn(doctor);
        when(doctorMapper.toResponseDto(doctor)).thenReturn(doctorResponseDto);

        DoctorResponseDto result = doctorService.createDoctor(doctorRequestDto);

        assertThat(result).isEqualTo(doctorResponseDto);

        verify(doctorRepository).save(doctor);
        verify(specialityRepository).findByNameIn(eq(specialityNames));
    }

    @Test
    void getDoctorById_ShouldReturnDoctorResponseDto_WhenDoctorExists() {
        when(userService.findByIdOrThrow(eq(doctorId), eq(doctorRepository), any(), eq(ENTITY_NAME)))
                .thenReturn(doctorResponseDto);

        DoctorResponseDto result = doctorService.getDoctorById(doctorId);

        assertThat(result).isEqualTo(doctorResponseDto);
        verify(userService).findByIdOrThrow(eq(doctorId), eq(doctorRepository), any(), eq(ENTITY_NAME));
    }

    @Test
    void getDoctorByName_ShouldReturnDoctorResponseDto_WhenDoctorExists() {
        when(doctorRepository.findByUserNameWithDoctorRole("Dr. John Doe")).thenReturn(Optional.of(doctor));
        when(doctorMapper.toResponseDto(doctor)).thenReturn(doctorResponseDto);

        DoctorResponseDto result = doctorService.getDoctorByName("Dr. John Doe");

        assertThat(result).isEqualTo(doctorResponseDto);
    }

    @Test
    void getDoctorsBySpeciality_ShouldReturnDoctorResponseDtoList_WhenSpecialityExists() {
        when(doctorRepository.findAllBySpeciality("Cardiology")).thenReturn(Collections.singletonList(doctor));
        when(doctorMapper.toResponseDto(doctor)).thenReturn(doctorResponseDto);

        List<DoctorResponseDto> result = doctorService.getDoctorsBySpeciality("Cardiology");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(doctorResponseDto);
    }

    @Test
    void updateDoctor_ShouldUpdateDoctor_WhenDataValid() {
        UserRequestDto updateDto = UserRequestDto.builder()
                .username("updatedDoctor")
                .name("Dr. Updated")
                .email("updated@example.com")
                .phone("1188887777")
                .build();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(userService.updateUserEntity(eq(doctor), eq(updateDto), any())).thenReturn(user);
        when(doctorRepository.save(doctor)).thenReturn(doctor);
        when(doctorMapper.toResponseDto(doctor)).thenReturn(doctorResponseDto);

        DoctorResponseDto result = doctorService.updateDoctor(doctorId, updateDto);

        assertThat(result).isEqualTo(doctorResponseDto);
        verify(doctorRepository).save(doctor);
    }

    @Test
    void updateDoctor_ShouldThrowException_WhenDoctorNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        UserRequestDto updateDto = UserRequestDto.builder()
                .username("updatedDoctor")
                .name("Dr. Updated")
                .email("updated@example.com")
                .phone("1188887777")
                .build();

        when(doctorRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> doctorService.updateDoctor(nonExistentId, updateDto));
        assertThat(exception.getMessage()).contains(nonExistentId.toString());
    }

    @Test
    void deactivateDoctor_ShouldCallUserServiceDeactivateEntity() {
        doNothing().when(userService)
                .deactivateEntity(eq(doctorId), eq(doctorRepository), any(), eq(true), eq(ENTITY_NAME));

        doctorService.deactivateDoctor(doctorId);

        verify(userService, times(1))
                .deactivateEntity(eq(doctorId), eq(doctorRepository), any(), eq(true), eq(ENTITY_NAME));
    }
}
