package com.bookease.service;

import com.bookease.exception.EntityNotFoundException;
import com.bookease.exception.UniqueFieldException;
import com.bookease.model.dto.request.PatientRequestDto;
import com.bookease.model.dto.request.UserRequestDto;
import com.bookease.model.dto.response.PatientResponseDto;
import com.bookease.model.entity.Patient;
import com.bookease.model.entity.Role;
import com.bookease.model.entity.User;
import com.bookease.model.mappers.PatientMapper;
import com.bookease.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private PatientService patientService;

    private User user;
    private Patient patient;
    private UUID patientId;
    private UserRequestDto userDto;
    private PatientRequestDto patientDto;
    private PatientResponseDto responseDto;

    private static final String ENTITY_NAME = "Patient";

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        user = User.builder()
                .userId(UUID.randomUUID())
                .username("testuser")
                .password("hashedpassword")
                .name("Test User")
                .email("test@example.com")
                .phone("123456789")
                .active(true)
                .tokenRevoked(false)
                .build();
        patient = Patient.builder()
                .id(patientId)
                .cpf("12345678901")
                .active(true)
                .user(user)
                .build();

        userDto = new UserRequestDto("testuser", "password", "Test User", "test@example.com", "123456789");
        // Correção: Passando ambos os argumentos necessários para PatientRequestDto
        patientDto = new PatientRequestDto("12345678901", userDto);
        responseDto = new PatientResponseDto(patientId, user.getUserId(), "testuser", "Test User",
                "123456789", "test@example.com", "12345678901", patient.isActive(), user.isTokenRevoked());
    }

    @Test
    void createPatient_whenDataValid_returnsResponseDto() {
        PatientRequestDto patientDto = new PatientRequestDto("12345678901", userDto);
        doNothing().when(userService).validateUniqueField(eq("12345678901"), eq(patientRepository), any(), eq("CPF"));
        when(userService.createUserWithRole(userDto, Role.Values.PATIENT)).thenReturn(user);
        when(patientMapper.toEntity(patientDto, user)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponseDto(patient)).thenReturn(responseDto);

        PatientResponseDto result = patientService.createPatient(patientDto);

        assertEquals(responseDto, result);
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void createPatient_whenCpfExists_throwsException() {
        PatientRequestDto patientDto = new PatientRequestDto("12345678901", userDto);
        doThrow(new UniqueFieldException("CPF já existe"))
                .when(userService).validateUniqueField(eq("12345678901"), eq(patientRepository), any(), eq("CPF"));

        UniqueFieldException exception = assertThrows(UniqueFieldException.class,
                () -> patientService.createPatient(patientDto),
                "Erro: Deveria lançar exceção para CPF existente.");
        assertEquals("CPF já existe", exception.getMessage());
    }

    @Test
    void getPatientById_whenPatientExists_returnsResponseDto() {
        when(userService.findByIdOrThrow(eq(patientId), eq(patientRepository), any(), eq("Patient")))
                .thenReturn(responseDto);

        PatientResponseDto result = patientService.getPatientById(patientId);

        assertNotNull(result, "Resultado não deveria ser null");
        assertEquals(responseDto, result);
    }

    @Test
    void getPatientById_whenPatientNotFound_throwsException() {
        doThrow(new EntityNotFoundException("Patient", patientId))
                .when(userService)
                .findByIdOrThrow(eq(patientId), eq(patientRepository), any(), eq("Patient"));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> patientService.getPatientById(patientId),
                "Erro: Deveria lançar exceção para paciente não encontrado.");
        assertEquals("Patient with identifier " + patientId + " not found", exception.getMessage());
    }

    @Test
    void getPatientByCpf_whenPatientExists_returnsResponseDto() {
        when(patientRepository.findByCpf("12345678901")).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDto(patient)).thenReturn(responseDto);

        PatientResponseDto result = patientService.getPatientByCpf("12345678901");

        assertEquals(responseDto, result);
    }

    @Test
    void getPatientByCpf_whenPatientNotFound_throwsException() {
        when(patientRepository.findByCpf("12345678901")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> patientService.getPatientByCpf("12345678901"),
                "Erro: Deveria lançar exceção para paciente não encontrado.");
        assertEquals("Patient with identifier 12345678901 not found", exception.getMessage());
    }

    @Test
    void getPatientByName_whenPatientExists_returnsResponseDto() {
        when(patientRepository.findByUserNameWithPatientRole("Test User")).thenReturn(Optional.of(patient));
        when(patientMapper.toResponseDto(patient)).thenReturn(responseDto);

        PatientResponseDto result = patientService.getPatientByName("Test User");

        assertEquals(responseDto, result);
    }

    @Test
    void getPatientByName_whenPatientNotFound_throwsException() {
        when(patientRepository.findByUserNameWithPatientRole("Test User")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> patientService.getPatientByName("Test User"),
                "Erro: Deveria lançar exceção para paciente não encontrado.");
        assertEquals("Patient with identifier Test User not found", exception.getMessage());
    }

    @Test
    void updatePatient_whenDataValid_updatesPatient() {
        UserRequestDto updateDto = new UserRequestDto(
                "newuser", null, "New User", "new@example.com", "987654321");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(userService.updateUserEntity(eq(patient), eq(updateDto), any()))
                .thenReturn(user);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponseDto(patient)).thenReturn(responseDto);

        PatientResponseDto result = patientService.updatePatient(patientId, updateDto);

        assertEquals(responseDto, result);
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void updatePatient_whenPatientNotFound_throwsException() {
        UserRequestDto updateDto = new UserRequestDto(
                "newuser", null, "New User", "new@example.com", "987654321");

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> patientService.updatePatient(patientId, updateDto));
        assertEquals("Patient with identifier " + patientId + " not found", exception.getMessage());
    }

    @Test
    void deactivatePatient_whenPatientExists_deactivatesPatient() {
        doNothing().when(userService).deactivateEntity(eq(patientId), eq(patientRepository), any(), eq(true), eq(ENTITY_NAME));

        patientService.deactivatePatient(patientId);

        verify(userService, times(1)).deactivateEntity(eq(patientId), eq(patientRepository), any(), eq(true), eq(ENTITY_NAME));
    }

    @Test
    void deactivatePatient_whenPatientNotFound_throwsException() {
        doThrow(new EntityNotFoundException(ENTITY_NAME, patientId))
                .when(userService).deactivateEntity(eq(patientId), eq(patientRepository), any(), eq(true), eq(ENTITY_NAME));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> patientService.deactivatePatient(patientId));
        assertEquals(ENTITY_NAME + " with identifier " + patientId + " not found", exception.getMessage());
    }
}