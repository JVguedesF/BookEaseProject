package com.bookease.service;

import com.bookease.exception.EntityNotFoundException;
import com.bookease.exception.UniqueFieldException;
import com.bookease.model.dto.request.ClinicRequestDto;
import com.bookease.model.dto.response.ClinicResponseDto;
import com.bookease.model.entity.Clinic;
import com.bookease.model.entity.Role;
import com.bookease.model.entity.User;
import com.bookease.model.mappers.ClinicMapper;
import com.bookease.repository.ClinicRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class ClinicServiceTest {

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private ClinicMapper clinicMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ClinicService clinicService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createClinic_shouldReturnClinicResponseDto_whenClinicCreated() {
        ClinicRequestDto clinicRequestDto = ClinicRequestDto.builder()
                .cnpj("12345678901234")
                .userRequestDto(null)
                .build();
        User dummyUser = User.builder().userId(UUID.randomUUID()).build();
        Clinic dummyClinic = Clinic.builder()
                .id(UUID.randomUUID())
                .cnpj("12345678901234")
                .active(true)
                .build();
        ClinicResponseDto dummyResponse = ClinicResponseDto.builder()
                .id(dummyClinic.getId())
                .build();
        doNothing().when(userService).validateUniqueField(eq("12345678901234"), eq(clinicRepository), any(), eq("CNPJ"));
        when(userService.createUserWithRole(any(), eq(Role.Values.CLINIC))).thenReturn(dummyUser);
        when(clinicMapper.toEntity(eq(clinicRequestDto), eq(dummyUser))).thenReturn(dummyClinic);
        when(clinicRepository.save(dummyClinic)).thenReturn(dummyClinic);
        when(clinicMapper.toResponseDto(dummyClinic)).thenReturn(dummyResponse);
        ClinicResponseDto response = clinicService.createClinic(clinicRequestDto);
        assertNotNull(response);
        assertEquals(dummyResponse.id(), response.id());
        verify(userService).validateUniqueField(eq("12345678901234"), eq(clinicRepository), any(), eq("CNPJ"));
    }

    @Test
    void getClinicById_shouldReturnClinicResponseDto_whenClinicExists() {
        UUID clinicId = UUID.randomUUID();
        ClinicResponseDto dummyResponse = ClinicResponseDto.builder()
                .id(clinicId)
                .build();
        when(userService.findByIdOrThrow(eq(clinicId), eq(clinicRepository), any(), eq("Clinic")))
                .thenReturn(dummyResponse);
        ClinicResponseDto response = clinicService.getClinicById(clinicId);
        assertNotNull(response);
        assertEquals(clinicId, response.id());
    }

    @Test
    void getClinicById_shouldThrowException_whenClinicDoesNotExist() {
        UUID clinicId = UUID.randomUUID();
        when(userService.findByIdOrThrow(eq(clinicId), eq(clinicRepository), any(), eq("Clinic")))
                .thenThrow(new EntityNotFoundException("Clinic", clinicId));
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> clinicService.getClinicById(clinicId));
        assertEquals(String.format("Clinic with identifier %s not found", clinicId), exception.getMessage());
    }

    @Test
    void createClinic_shouldThrowUniqueFieldException_whenCnpjAlreadyExists() {
        ClinicRequestDto clinicRequestDto = ClinicRequestDto.builder()
                .cnpj("12345678901234")
                .userRequestDto(null)
                .build();
        doThrow(new UniqueFieldException("CNPJ já existe"))
                .when(userService).validateUniqueField(eq("12345678901234"), eq(clinicRepository), any(), eq("CNPJ"));
        assertThrows(UniqueFieldException.class, () -> clinicService.createClinic(clinicRequestDto));
    }

    @Test
    void getClinicByCnpj_shouldReturnClinicResponseDto_whenClinicExists() {
        String cnpj = "12345678901234";
        Clinic dummyClinic = Clinic.builder().cnpj(cnpj).active(true).build();
        ClinicResponseDto dummyResponse = ClinicResponseDto.builder().build();
        when(clinicRepository.findByCnpj(cnpj)).thenReturn(Optional.of(dummyClinic));
        when(clinicMapper.toResponseDto(dummyClinic)).thenReturn(dummyResponse);
        ClinicResponseDto response = clinicService.getClinicByCnpj(cnpj);
        assertNotNull(response);
    }

    @Test
    void getClinicByCnpj_shouldThrowException_whenClinicNotFound() {
        String cnpj = "12345678901234";
        when(clinicRepository.findByCnpj(cnpj)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> clinicService.getClinicByCnpj(cnpj));
        assertEquals(String.format("Clinic with identifier %s not found", cnpj), exception.getMessage());
    }

    @Test
    void getClinicByName_shouldReturnClinicResponseDto_whenClinicExists() {
        String name = "ClinicTest";
        Clinic dummyClinic = Clinic.builder().active(true).build();
        ClinicResponseDto dummyResponse = ClinicResponseDto.builder().build();

        when(clinicRepository.findByUserName(name)).thenReturn(List.of(dummyClinic));

        when(clinicMapper.toResponseDto(dummyClinic)).thenReturn(dummyResponse);

        ClinicResponseDto response = clinicService.getClinicByName(name);

        assertNotNull(response);
    }

    @Test
    void getClinicByName_shouldThrowException_whenClinicNotFound() {
        String name = "ClinicTest";

        when(clinicRepository.findByUserName(name)).thenReturn(List.of());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> clinicService.getClinicByName(name));

        assertEquals(String.format("Clinic with identifier %s not found", name), exception.getMessage());
    }


    @Test
    void updateClinic_shouldReturnUpdatedClinicResponseDto_whenClinicExists() {
        UUID clinicId = UUID.randomUUID();
        ClinicRequestDto clinicRequestDto = ClinicRequestDto.builder().build();
        Clinic dummyClinic = Clinic.builder().id(clinicId).active(true).build();
        User dummyUser = User.builder().build();
        Clinic updatedClinic = Clinic.builder().id(clinicId).active(true).build();
        ClinicResponseDto dummyResponse = ClinicResponseDto.builder().id(clinicId).build();
        when(clinicRepository.findById(clinicId)).thenReturn(Optional.of(dummyClinic));
        when(userService.updateUserEntity(eq(dummyClinic), eq(clinicRequestDto.userRequestDto()), any())).thenReturn(dummyUser);
        when(clinicRepository.save(any(Clinic.class))).thenReturn(updatedClinic);
        when(clinicMapper.toResponseDto(updatedClinic)).thenReturn(dummyResponse);
        ClinicResponseDto response = clinicService.updateClinic(clinicId, clinicRequestDto);
        assertNotNull(response);
        assertEquals(clinicId, response.id());
    }

    @Test
    void updateClinic_shouldThrowException_whenClinicNotFound() {
        UUID clinicId = UUID.randomUUID();
        ClinicRequestDto clinicRequestDto = ClinicRequestDto.builder().build();
        when(clinicRepository.findById(clinicId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> clinicService.updateClinic(clinicId, clinicRequestDto));
        assertEquals(String.format("Clinic with identifier %s not found", clinicId), exception.getMessage());
    }

    @Test
    void deactivateClinic_shouldSucceed_whenClinicExists() {
        UUID clinicId = UUID.randomUUID();
        Clinic dummyClinic = Clinic.builder().id(clinicId).active(true).build();
        when(clinicRepository.findById(clinicId)).thenReturn(Optional.of(dummyClinic));
        doNothing().when(userService).deactivateEntity(eq(clinicId), eq(clinicRepository), any(), eq(true), eq("Clinic"));
        clinicService.deactivateClinic(clinicId);
        verify(userService).deactivateEntity(eq(clinicId), eq(clinicRepository), any(), eq(true), eq("Clinic"));
    }

    @Test
    void deactivateClinic_shouldThrowException_whenClinicNotFound() {
        UUID clinicId = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Clinic", clinicId))
                .when(userService).deactivateEntity(eq(clinicId), eq(clinicRepository), any(), eq(true), eq("Clinic"));
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> clinicService.deactivateClinic(clinicId));
        assertEquals(String.format("Clinic with identifier %s not found", clinicId), exception.getMessage());
    }
}