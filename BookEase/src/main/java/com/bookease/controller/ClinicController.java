package com.bookease.controller;

import com.bookease.model.dto.request.ClinicRequestDto;
import com.bookease.model.dto.response.ClinicResponseDto;
import com.bookease.service.ClinicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/clinic")
public class ClinicController {

    private final ClinicService clinicService;

    @Autowired
    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClinicResponseDto> registerClinic(
            @Valid @RequestBody ClinicRequestDto clinicRequestDto) {
        ClinicResponseDto response = clinicService.createClinic(clinicRequestDto);
        URI location = URI.create("/clinic/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{clinicId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<ClinicResponseDto> getClinicById(
            @PathVariable UUID clinicId) {
        ClinicResponseDto response = clinicService.getClinicById(clinicId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cnpj/{cnpj}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<ClinicResponseDto> getClinicByCnpj(
            @PathVariable String cnpj) {
        ClinicResponseDto response = clinicService.getClinicByCnpj(cnpj);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<ClinicResponseDto> getClinicByName(
            @RequestParam String name) {
        ClinicResponseDto response = clinicService.getClinicByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/city")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<ClinicResponseDto> getClinicByCity(
            @RequestParam String city) {
        ClinicResponseDto response = clinicService.getClinicByCity(city);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{clinicId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'CLINIC')")
    public ResponseEntity<ClinicResponseDto> updateClinic(
            @PathVariable UUID clinicId,
            @Valid @RequestBody ClinicRequestDto clinicUpdateDto) {
        ClinicResponseDto response = clinicService.updateClinic(clinicId, clinicUpdateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{clinicId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'CLINIC')")
    public ResponseEntity<Void> deactivateClinic(@PathVariable UUID clinicId) {
        clinicService.deactivateClinic(clinicId);
        return ResponseEntity.noContent().build();
    }
}