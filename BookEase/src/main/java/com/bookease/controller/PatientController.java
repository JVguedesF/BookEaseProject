package com.bookease.controller;

import com.bookease.model.dto.request.PatientRequestDto;
import com.bookease.model.dto.request.UserRequestDto;
import com.bookease.model.dto.response.PatientResponseDto;
import com.bookease.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/register")
    public ResponseEntity<PatientResponseDto> registerPatient(
            @Valid @RequestBody PatientRequestDto patientRequestDto) {
        PatientResponseDto response = patientService.createPatient(patientRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{patientId}")
    @PreAuthorize("hasAnyRole('CLINIC', 'DOCTOR', 'ADMIN', 'PATIENT')")
    public ResponseEntity<PatientResponseDto> getPatientById(
            @PathVariable UUID patientId) {
        PatientResponseDto response = patientService.getPatientById(patientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasAnyRole('CLINIC', 'DOCTOR', 'ADMIN', 'PATIENT')")
    public ResponseEntity<PatientResponseDto> getPatientByCpf(
            @PathVariable String cpf) {
        PatientResponseDto response = patientService.getPatientByCpf(cpf);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name")
    @PreAuthorize("hasAnyRole('CLINIC', 'DOCTOR', 'ADMIN', 'PATIENT')")
    public ResponseEntity<PatientResponseDto> getPatientByName(
            @RequestParam String name) {
        PatientResponseDto response = patientService.getPatientByName(name);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{patientId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @PathVariable UUID patientId,
            @Valid @RequestBody UserRequestDto userUpdateDto) {
        PatientResponseDto response = patientService.updatePatient(patientId, userUpdateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{patientId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> deactivatePatient(@PathVariable UUID patientId) {
        patientService.deactivatePatient(patientId);
        return ResponseEntity.noContent().build();
    }
}