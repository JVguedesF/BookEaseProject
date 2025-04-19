package com.bookease.service;

import com.bookease.exception.EntityNotFoundException;
import com.bookease.exception.EntityOperationException;
import com.bookease.exception.UniqueFieldException;
import com.bookease.model.dto.request.UserRequestDto;
import com.bookease.model.entity.Role;
import com.bookease.model.entity.User;
import com.bookease.model.mappers.UserMapper;
import com.bookease.repository.RoleRepository;
import com.bookease.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthService authService;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       AuthService authService,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.authService = authService;
        this.roleRepository = roleRepository;
    }

    public void validateUserUniqueness(UserRequestDto userDto) {
        if (userDto.getUsername() != null && userRepository.existsByUsername(userDto.getUsername())) {
            throw new UniqueFieldException("Username já existe");
        }
        if (userDto.getEmail() != null && userRepository.existsByEmail(userDto.getEmail())) {
            throw new UniqueFieldException("Email já existe");
        }
        if (userDto.getPhone() != null && userRepository.existsByPhone(userDto.getPhone())) {
            throw new UniqueFieldException("Telefone já existe");
        }
    }

    @Transactional
    public <T> User updateUserEntity(T entity, UserRequestDto updateDto,
                                     Function<T, User> getUserFunction) {
        User user = getUserFunction.apply(entity);
        authService.verifyOwnership(user);
        userMapper.updateFromDto(user, updateDto);
        validateUserUniquenessForUpdate(updateDto, user.getUserId());
        return userRepository.save(user);
    }

    @Transactional
    public User createUserWithRole(UserRequestDto userDto, Role.Values roleName) {
        validateUserUniqueness(userDto);
        User user = userMapper.toEntity(userDto);
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role", roleName));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @SuppressWarnings({"squid:S125", "unused"})
    public void checkAndHandleInactiveUser(String email, String phone) {
        Optional<User> inactiveUser = userRepository.findInactiveUser(email, phone);

        if (inactiveUser.isPresent()) {
            //emailService.sendReactivationEmail(email);
            //throw new UserInactiveException("Usuário inativo detectado. Enviamos um e-mail para reativação.");
            throw new EntityOperationException("Usuário inativo detectado. Enviamos um e-mail para reativação.");
        }
    }



    public <T, R> R findByIdOrThrow(UUID id, JpaRepository<T, UUID> repository,
                                    Function<T, R> mapper, String entityName) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));
        return mapper.apply(entity);
    }

    @Transactional
    public <T> void deactivateEntity(UUID id, JpaRepository<T, UUID> repository,
                                     Function<T, User> getUserFunction, boolean verifyOwnership,
                                     String entityName) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));
        if (verifyOwnership) {
            authService.verifyOwnership(getUserFunction.apply(entity));
        }
        try {
            entity.getClass().getMethod("setActive", boolean.class).invoke(entity, false);
            User user = getUserFunction.apply(entity);
            user.setActive(false);
        } catch (Exception e) {
            throw new EntityOperationException(
                    String.format("Erro ao desativar entidade: %s", e.getMessage()), e);
        }
        repository.save(entity);
    }

    // Método genérico utilizando Predicate<T> para validação
    public <T> void validateUniqueField(String fieldValue, JpaRepository<T, ?> repository,
                                        Predicate<T> predicate, String fieldName) {
        if (fieldValue != null && repository.findAll().stream().anyMatch(predicate)) {
            throw new UniqueFieldException(String.format("%s já existe", fieldName));
        }
    }

    public void validateUserUniquenessForUpdate(UserRequestDto userDto, UUID currentUserId) {
        if (userDto.getUsername() != null) {
            userRepository.findByUsername(userDto.getUsername())
                    .filter(ignored -> !ignored.getUserId().equals(currentUserId))
                    .ifPresent(ignored -> {
                        throw new UniqueFieldException("Username já existe");
                    });
        }
        if (userDto.getEmail() != null) {
            userRepository.findByEmail(userDto.getEmail())
                    .filter(ignored -> !ignored.getUserId().equals(currentUserId))
                    .ifPresent(ignored -> {
                        throw new UniqueFieldException("Email já existe");
                    });
        }
        if (userDto.getPhone() != null) {
            userRepository.findByPhone(userDto.getPhone())
                    .filter(ignored -> !ignored.getUserId().equals(currentUserId))
                    .ifPresent(ignored -> {
                        throw new UniqueFieldException("Telefone já existe");
                    });
        }
    }
}
