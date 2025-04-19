package com.bookease.controller;

import com.bookease.exception.EntityNotFoundException;
import com.bookease.model.dto.request.ProcedureOfferedRequestDto;
import com.bookease.model.dto.response.ProcedureOfferedResponseDto;
import com.bookease.model.enums.ProcedureEnum;
import com.bookease.model.entity.ProcedureOffered;
import com.bookease.model.mappers.ProcedureOfferedMapper;
import com.bookease.service.ProcedureOfferedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/procedures-offered")
public class ProcedureOfferedController {

    private final ProcedureOfferedService procedureOfferedService;
    private final ProcedureOfferedMapper procedureOfferedMapper;

    @Autowired
    public ProcedureOfferedController(ProcedureOfferedService procedureOfferedService,
                                      ProcedureOfferedMapper procedureOfferedMapper) {
        this.procedureOfferedService = procedureOfferedService;
        this.procedureOfferedMapper = procedureOfferedMapper;
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<ProcedureOfferedResponseDto> createProcedureOffered(
            @RequestBody ProcedureOfferedRequestDto procedureOfferedRequestDto) {
        try {
            ProcedureOffered procedureOffered = procedureOfferedService.createProcedureOffered(procedureOfferedRequestDto);
            ProcedureOfferedResponseDto responseDto = procedureOfferedMapper.toResponseDto(procedureOffered);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/exists/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<Boolean> getExistsByIdAndActiveTrue(@PathVariable UUID id) {
        boolean exists = procedureOfferedService.getExistsByIdAndActiveTrue(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/count/doctor-clinic/{doctorClinicId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<Long> getCountByDoctorClinicIdAndActiveTrue(@PathVariable UUID doctorClinicId) {
        long count = procedureOfferedService.getCountByDoctorClinicIdAndActiveTrue(doctorClinicId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/price-between")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<List<ProcedureOfferedResponseDto>> getByPriceBetween(
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        List<ProcedureOfferedResponseDto> procedures = procedureOfferedService.getByPriceBetween(minPrice, maxPrice);
        return ResponseEntity.ok(procedures);
    }

    @GetMapping("/doctor-clinic/{doctorClinicId}/price-order")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<List<ProcedureOfferedResponseDto>> getActiveByDoctorClinicIdOrderByPriceAsc(
            @PathVariable UUID doctorClinicId) {
        List<ProcedureOfferedResponseDto> procedures = procedureOfferedService.getActiveByDoctorClinicIdOrderByPriceAsc(doctorClinicId);
        return ResponseEntity.ok(procedures);
    }

    @GetMapping("/doctor-clinic/{doctorClinicId}/price-between")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<List<ProcedureOfferedResponseDto>> getActiveByDoctorClinicIdAndPriceBetween(
            @PathVariable UUID doctorClinicId,
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        List<ProcedureOfferedResponseDto> procedures = procedureOfferedService.getActiveByDoctorClinicIdAndPriceBetween(doctorClinicId, minPrice, maxPrice);
        return ResponseEntity.ok(procedures);
    }

    @GetMapping("/procedure-name/{procedureName}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<List<ProcedureOfferedResponseDto>> getByProcedureNameAndActiveTrue(
            @PathVariable String procedureName) {
        List<ProcedureOfferedResponseDto> procedures = procedureOfferedService.getByProcedureNameAndActiveTrue(procedureName);
        return ResponseEntity.ok(procedures);
    }

    @GetMapping("/doctor-clinic/{doctorClinicId}/duration-min")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<List<ProcedureOfferedResponseDto>> getByDoctorClinicIdAndDurationMinutesGreaterThanEqualAndActiveTrue(
            @PathVariable UUID doctorClinicId,
            @RequestParam int minDuration) {
        List<ProcedureOfferedResponseDto> procedures = procedureOfferedService.getByDoctorClinicIdAndDurationMinutesGreaterThanEqualAndActiveTrue(doctorClinicId, minDuration);
        return ResponseEntity.ok(procedures);
    }

    @GetMapping("/procedure-display-name/{displayName}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<List<ProcedureOfferedResponseDto>> getByProcedureDisplayNameAndActiveTrue(
            @PathVariable String displayName) {
        List<ProcedureOfferedResponseDto> procedures = procedureOfferedService.getByProcedureDisplayNameAndActiveTrue(displayName);
        return ResponseEntity.ok(procedures);
    }

    @GetMapping("/procedure-enum/{procedureEnum}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<List<ProcedureOfferedResponseDto>> getByProcedureEnumAndActiveTrueOrderByPriceAsc(
            @PathVariable ProcedureEnum procedureEnum) {
        List<ProcedureOfferedResponseDto> procedures = procedureOfferedService.getByProcedureEnumAndActiveTrueOrderByPriceAsc(procedureEnum);
        return ResponseEntity.ok(procedures);
    }

    @GetMapping("/doctor-clinic/{doctorClinicId}/search")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<List<ProcedureOfferedResponseDto>> getByDoctorClinicIdAndProcedureDisplayNameContainingAndActiveTrue(
            @PathVariable UUID doctorClinicId,
            @RequestParam String keyword) {
        List<ProcedureOfferedResponseDto> procedures = procedureOfferedService.getByDoctorClinicIdAndProcedureDisplayNameContainingAndActiveTrue(doctorClinicId, keyword);
        return ResponseEntity.ok(procedures);
    }

    @GetMapping("/duration-and-price")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<List<ProcedureOfferedResponseDto>> getByDurationMinutesAndPriceBetweenAndActiveTrue(
            @RequestParam int duration,
            @RequestParam double minPrice,
            @RequestParam double maxPrice) {
        List<ProcedureOfferedResponseDto> procedures = procedureOfferedService.getByDurationMinutesAndPriceBetweenAndActiveTrue(duration, minPrice, maxPrice);
        return ResponseEntity.ok(procedures);
    }

    @GetMapping("/doctor-clinic/{doctorClinicId}/duration-order")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<List<ProcedureOfferedResponseDto>> getByDoctorClinicIdAndActiveTrueOrderByDurationMinutesAsc(
            @PathVariable UUID doctorClinicId) {
        List<ProcedureOfferedResponseDto> procedures = procedureOfferedService.getByDoctorClinicIdAndActiveTrueOrderByDurationMinutesAsc(doctorClinicId);
        return ResponseEntity.ok(procedures);
    }

    @GetMapping("/count/doctor-clinic/{doctorClinicId}/procedure-enum/{procedureEnum}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'CLINIC', 'ADMIN')")
    public ResponseEntity<Long> getCountByDoctorClinicIdAndProcedureEnumAndActiveTrue(
            @PathVariable UUID doctorClinicId,
            @PathVariable ProcedureEnum procedureEnum) {
        long count = procedureOfferedService.getCountByDoctorClinicIdAndProcedureEnumAndActiveTrue(doctorClinicId, procedureEnum);
        return ResponseEntity.ok(count);
    }
}