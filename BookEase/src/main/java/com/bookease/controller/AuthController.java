package com.bookease.controller;

import com.bookease.model.dto.request.LoginRequestDto;
import com.bookease.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDto loginDto) {
        Map<String, String> tokens = authService.authenticate(loginDto);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/revoke")
    public ResponseEntity<Void> revokeTokens() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID userId = authService.getUserIdByUsername(username);
        authService.revokeTokens(userId);
        return ResponseEntity.noContent().build();
    }
}