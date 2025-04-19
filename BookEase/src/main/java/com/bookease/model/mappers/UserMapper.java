package com.bookease.model.mappers;

import com.bookease.model.dto.request.UserRequestDto;
import com.bookease.model.dto.response.UserResponseDto;
import com.bookease.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toEntity(UserRequestDto dto) {
        return User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .active(true)
                .tokenRevoked(false)
                .build();
    }

    public UserResponseDto toResponseDto(User entity) {
        return UserResponseDto.builder()
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .name(entity.getName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .active(entity.isActive())
                .tokenRevoked(entity.isTokenRevoked())
                .build();
    }

    public void updateFromDto(User user, UserRequestDto dto) {
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
    }
}
