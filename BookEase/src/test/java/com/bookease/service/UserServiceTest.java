package com.bookease.service;

import com.bookease.exception.EntityNotFoundException;
import com.bookease.exception.EntityOperationException;
import com.bookease.exception.UniqueFieldException;
import com.bookease.model.dto.request.UserRequestDto;
import com.bookease.model.dto.response.UserResponseDto;
import com.bookease.model.entity.Clinic;
import com.bookease.model.entity.Role;
import com.bookease.model.entity.User;
import com.bookease.model.mappers.UserMapper;
import com.bookease.repository.ClinicRepository;
import com.bookease.repository.RoleRepository;
import com.bookease.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthService authService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void validateUserUniqueness_shouldThrowException_whenUsernameExists() {
        UserRequestDto userDto = UserRequestDto.builder().username("testuser").build();
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        UniqueFieldException exception = assertThrows(UniqueFieldException.class,
                () -> userService.validateUserUniqueness(userDto));
        assertEquals("Username já existe", exception.getMessage());
    }

    @Test
    void validateUserUniqueness_shouldNotThrowException_whenFieldsAreUnique() {
        UserRequestDto userDto = UserRequestDto.builder()
                .username("testuser")
                .email("test@example.com")
                .phone("123456789")
                .build();
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.existsByPhone("123456789")).thenReturn(false);
        assertDoesNotThrow(() -> userService.validateUserUniqueness(userDto));
    }

    @Test
    void createUserWithRole_shouldReturnUser_whenSuccessful() {
        UserRequestDto userDto = UserRequestDto.builder().username("testuser").build();
        User user = User.builder().userId(UUID.randomUUID()).username("testuser").build();
        Role role = Role.builder().name(Role.Values.CLINIC).build(); // Usando CLINIC como exemplo
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(roleRepository.findByName(Role.Values.CLINIC)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);
        User result = userService.createUserWithRole(userDto, Role.Values.CLINIC);
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(1, result.getRoles().size());
    }

    @Test
    void createUserWithRole_shouldThrowException_whenRoleNotFound() {
        UserRequestDto userDto = UserRequestDto.builder().username("testuser").build();
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(roleRepository.findByName(Role.Values.CLINIC)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.createUserWithRole(userDto, Role.Values.CLINIC));
        assertEquals("Role with identifier CLINIC not found", exception.getMessage());
    }

    @Test
    void updateUserEntity_shouldUpdateUser_whenSuccessful() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().userId(userId).username("olduser").build();
        UserRequestDto updateDto = UserRequestDto.builder().username("newuser").build();
        User updatedUser = User.builder().userId(userId).username("newuser").build();
        doNothing().when(authService).verifyOwnership(user);
        when(userRepository.save(user)).thenReturn(updatedUser);
        User result = userService.updateUserEntity(user, updateDto, u -> u);
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        verify(userMapper).updateFromDto(user, updateDto);
    }

    @Test
    void findByIdOrThrow_shouldReturnMappedResult_whenEntityExists() {
        UUID id = UUID.randomUUID();
        User user = User.builder().userId(id).build();
        UserResponseDto responseDto = UserResponseDto.builder().userId(id).build();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(responseDto);
        UserResponseDto result = userService.findByIdOrThrow(id, userRepository, userMapper::toResponseDto, "User");
        assertNotNull(result);
        assertEquals(id, result.userId());
    }

    @Test
    void findByIdOrThrow_shouldThrowException_whenEntityNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.findByIdOrThrow(id, userRepository, userMapper::toResponseDto, "User"));
        assertEquals(String.format("User with identifier %s not found", id), exception.getMessage());
    }

    @Test
    void deactivateEntity_shouldDeactivate_whenEntityExists() {
        UUID id = UUID.randomUUID();
        User user = User.builder().userId(id).active(true).build();
        Clinic clinic = Clinic.builder().id(id).user(user).active(true).build();
        when(clinicRepository.findById(id)).thenReturn(Optional.of(clinic));
        doNothing().when(authService).verifyOwnership(user);
        when(clinicRepository.save(clinic)).thenReturn(clinic);
        userService.deactivateEntity(id, clinicRepository, Clinic::getUser, true, "Clinic");
        verify(clinicRepository).save(clinic);
        assertFalse(user.isActive());
    }

    @Test
    void validateUniqueField_shouldThrowException_whenFieldExists() {
        String fieldValue = "123456789";
        Predicate<Clinic> predicate = c -> c.getCnpj().equals(fieldValue);
        when(clinicRepository.findAll()).thenReturn(List.of(Clinic.builder().cnpj(fieldValue).build()));
        UniqueFieldException exception = assertThrows(UniqueFieldException.class,
                () -> userService.validateUniqueField(fieldValue, clinicRepository, predicate, "CNPJ"));
        assertEquals("CNPJ já existe", exception.getMessage());
    }

    @Test
    void checkAndHandleInactiveUser_shouldThrowException_whenInactiveUserExists() {
        String email = "test@example.com";
        String phone = "123456789";
        User inactiveUser = User.builder().email(email).phone(phone).active(false).build();
        when(userRepository.findInactiveUser(email, phone)).thenReturn(Optional.of(inactiveUser));
        EntityOperationException exception = assertThrows(EntityOperationException.class,
                () -> userService.checkAndHandleInactiveUser(email, phone));
        assertEquals("Usuário inativo detectado. Enviamos um e-mail para reativação.", exception.getMessage());
    }

    @Test
    void validateUserUniqueness_shouldThrowException_whenEmailExists() {
        UserRequestDto userDto = UserRequestDto.builder().email("test@example.com").build();
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        UniqueFieldException exception = assertThrows(UniqueFieldException.class,
                () -> userService.validateUserUniqueness(userDto));
        assertEquals("Email já existe", exception.getMessage());
    }

    @Test
    void validateUserUniqueness_shouldThrowException_whenPhoneExists() {
        UserRequestDto userDto = UserRequestDto.builder().phone("123456789").build();
        when(userRepository.existsByPhone("123456789")).thenReturn(true);
        UniqueFieldException exception = assertThrows(UniqueFieldException.class,
                () -> userService.validateUserUniqueness(userDto));
        assertEquals("Telefone já existe", exception.getMessage());
    }

    @Test
    void updateUserEntity_shouldThrowUniqueFieldException_whenNewUsernameExists() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().userId(userId).username("olduser").build();
        UserRequestDto updateDto = UserRequestDto.builder().username("existinguser").build();
        User existingUser = User.builder().userId(UUID.randomUUID()).username("existinguser").build();
        doNothing().when(authService).verifyOwnership(user);
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));
        UniqueFieldException exception = assertThrows(UniqueFieldException.class,
                () -> userService.updateUserEntity(user, updateDto, u -> u));
        assertEquals("Username já existe", exception.getMessage());
    }

    @Test
    void deactivateEntity_shouldThrowException_whenEntityNotFound() {
        UUID id = UUID.randomUUID();
        when(clinicRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.deactivateEntity(id, clinicRepository, Clinic::getUser, true, "Clinic"));
        assertEquals(String.format("Clinic with identifier %s not found", id), exception.getMessage());
    }

    @Test
    void validateUniqueField_shouldNotThrowException_whenFieldIsUnique() {
        String fieldValue = "123456789";
        Predicate<Clinic> predicate = c -> c.getCnpj().equals(fieldValue);
        when(clinicRepository.findAll()).thenReturn(List.of());
        assertDoesNotThrow(() -> userService.validateUniqueField(fieldValue, clinicRepository, predicate, "CNPJ"));
    }

    @Test
    void validateUserUniquenessForUpdate_shouldThrowException_whenNewEmailExists() {
        UUID userId = UUID.randomUUID();
        UserRequestDto userDto = UserRequestDto.builder().email("existing@example.com").build();
        User existingUser = User.builder().userId(UUID.randomUUID()).email("existing@example.com").build();
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));
        UniqueFieldException exception = assertThrows(UniqueFieldException.class,
                () -> userService.validateUserUniquenessForUpdate(userDto, userId));
        assertEquals("Email já existe", exception.getMessage());
    }

    @Test
    void checkAndHandleInactiveUser_shouldNotThrowException_whenNoInactiveUser() {
        String email = "test@example.com";
        String phone = "123456789";
        when(userRepository.findInactiveUser(email, phone)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> userService.checkAndHandleInactiveUser(email, phone));
    }
}