package com.bookease.controller;

import com.bookease.exception.UserInactiveException;
import com.bookease.model.dto.request.DoctorRequestDto;
import com.bookease.model.dto.request.SpecialityRequestDto;
import com.bookease.model.dto.request.UserRequestDto;
import com.bookease.model.dto.response.DoctorResponseDto;
import com.bookease.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> registerDoctor(
            @Valid @RequestBody DoctorRequestDto requestDto) {
        System.out.println("Requisição recebida no controller: " + requestDto);
        try {
            DoctorResponseDto response = doctorService.createDoctor(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserInactiveException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }


    @GetMapping("/{doctorId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<DoctorResponseDto> getDoctorById(
            @PathVariable UUID doctorId) {
        DoctorResponseDto response = doctorService.getDoctorById(doctorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<DoctorResponseDto> getDoctorByName(
            @RequestParam String name) {
        DoctorResponseDto response = doctorService.getDoctorByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/speciality")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<List<DoctorResponseDto>> getDoctorsBySpeciality(
            @RequestParam String specialityName) {
        List<DoctorResponseDto> response = doctorService.getDoctorsBySpeciality(specialityName);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<DoctorResponseDto> updateDoctor(
            @PathVariable UUID doctorId,
            @Valid @RequestBody UserRequestDto userUpdateDto) {
        DoctorResponseDto response = doctorService.updateDoctor(doctorId, userUpdateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<DoctorResponseDto> deactivateDoctor(
            @PathVariable UUID doctorId) {
        doctorService.deactivateDoctor(doctorId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{doctorId}/specialities")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<DoctorResponseDto> addSpecialities(
            @PathVariable UUID doctorId,
            @Valid @RequestBody SpecialityRequestDto request) {

        List<String> specialityNames = request.specialityNames();

        DoctorResponseDto response = doctorService.addSpecialities(doctorId, specialityNames);

        return ResponseEntity.ok(response);
    }


}
