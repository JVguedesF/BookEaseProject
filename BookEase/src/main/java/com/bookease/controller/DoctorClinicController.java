package com.bookease.controller;

import com.bookease.model.dto.request.DoctorClinicRequestDto;
import com.bookease.model.dto.response.ClinicResponseDto;
import com.bookease.model.dto.response.DoctorClinicResponseDto;
import com.bookease.model.dto.response.DoctorResponseDto;
import com.bookease.service.DoctorClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/doctor-clinic")
public class DoctorClinicController {

    private final DoctorClinicService doctorClinicService;

    @Autowired
    public DoctorClinicController(DoctorClinicService doctorClinicService) {
        this.doctorClinicService = doctorClinicService;
    }

    @PostMapping("/associate")
    @PreAuthorize("hasAnyRole('CLINIC', 'ADMIN')")
    public ResponseEntity<DoctorClinicResponseDto> associateDoctorToClinic(
            @RequestBody DoctorClinicRequestDto doctorClinicDto) {
        DoctorClinicResponseDto responseDto = doctorClinicService.createDoctorClinic(doctorClinicDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }


    @GetMapping("/{doctorClinicId}")
    @PreAuthorize("hasAnyRole('CLINIC', 'ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<DoctorClinicResponseDto> getDoctorClinicById(
            @PathVariable UUID doctorClinicId) {
        DoctorClinicResponseDto response = doctorClinicService.getDoctorClinicById(doctorClinicId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('CLINIC', 'ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<DoctorClinicResponseDto>> getDoctorClinicByDoctorId(
            @PathVariable UUID doctorId) {
        List<DoctorClinicResponseDto> response = doctorClinicService.getDoctorClinicByDoctorId(doctorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/clinic/{clinicId}")
    @PreAuthorize("hasAnyRole('CLINIC', 'ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<DoctorClinicResponseDto>> getDoctorClinicByClinicId(
            @PathVariable UUID clinicId) {
        List<DoctorClinicResponseDto> response = doctorClinicService.getDoctorClinicByClinicId(clinicId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctors/{clinicId}")
    @PreAuthorize("hasAnyRole('CLINIC', 'ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<DoctorResponseDto>> getDoctorsByClinicId(
            @PathVariable UUID clinicId) {
        List<DoctorResponseDto> doctors = doctorClinicService.getDoctorsByClinicId(clinicId);
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/clinics/{doctorId}")
    @PreAuthorize("hasAnyRole('CLINIC', 'ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<List<ClinicResponseDto>> getClinicsByDoctorId(
            @PathVariable UUID doctorId) {
        List<ClinicResponseDto> clinics = doctorClinicService.getClinicsByDoctorId(doctorId);
        return ResponseEntity.ok(clinics);
    }
}
