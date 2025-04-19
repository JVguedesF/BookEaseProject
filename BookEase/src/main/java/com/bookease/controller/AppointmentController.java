package com.bookease.controller;

import com.bookease.model.dto.request.AppointmentRequestDto;
import com.bookease.model.dto.response.AppointmentResponseDto;
import com.bookease.model.enums.AppointmentEnum;
import com.bookease.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDto> create(@RequestBody @Valid AppointmentRequestDto dto) {
        AppointmentResponseDto response = appointmentService.createAppointment(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> getById(@PathVariable UUID id) {
        AppointmentResponseDto response = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDto>> getByPatient(@PathVariable UUID patientId) {
        List<AppointmentResponseDto> responses = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/doctorClinic/{doctorClinicId}")
    public ResponseEntity<List<AppointmentResponseDto>> getByDoctorClinic(@PathVariable UUID doctorClinicId) {
        List<AppointmentResponseDto> responses = appointmentService.getAppointmentsByDoctorClinic(doctorClinicId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentResponseDto>> getByStatus(@PathVariable AppointmentEnum status) {
        List<AppointmentResponseDto> responses = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/daterange")
    public ResponseEntity<List<AppointmentResponseDto>> getByDateRange(@RequestParam("start") LocalDateTime start,
                                                                       @RequestParam("end") LocalDateTime end) {
        List<AppointmentResponseDto> responses = appointmentService.getAppointmentsByDateRange(start, end);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> update(@PathVariable UUID id, @RequestBody @Valid AppointmentRequestDto dto) {
        AppointmentResponseDto response = appointmentService.updateAppointment(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<AppointmentResponseDto> deactivate(@PathVariable UUID id) {
        AppointmentResponseDto response = appointmentService.deactivateAppointment(id);
        return ResponseEntity.ok(response);
    }
}
