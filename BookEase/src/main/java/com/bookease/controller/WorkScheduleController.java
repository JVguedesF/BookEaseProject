package com.bookease.controller;

import com.bookease.model.dto.request.WorkScheduleRequestDto;
import com.bookease.model.dto.response.WorkScheduleResponseDto;
import com.bookease.service.WorkScheduleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/work-schedules")
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    public WorkScheduleController(WorkScheduleService workScheduleService) {
        this.workScheduleService = workScheduleService;
    }

    @PostMapping
    public ResponseEntity<WorkScheduleResponseDto> create(@RequestBody @Valid WorkScheduleRequestDto dto) {
        WorkScheduleResponseDto response = workScheduleService.createWorkSchedule(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkScheduleResponseDto> getById(@PathVariable UUID id) {
        WorkScheduleResponseDto response = workScheduleService.getWorkSchedule(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkScheduleResponseDto>> getAll() {
        List<WorkScheduleResponseDto> response = workScheduleService.getAllWorkSchedules();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkScheduleResponseDto> update(@PathVariable UUID id,
                                                          @RequestBody @Valid WorkScheduleRequestDto dto) {
        WorkScheduleResponseDto response = workScheduleService.updateWorkSchedule(id, dto);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WorkScheduleResponseDto> patch(@PathVariable UUID id,
                                                         @RequestBody WorkScheduleRequestDto dto) {
        WorkScheduleResponseDto response = workScheduleService.patchWorkSchedule(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        workScheduleService.deleteWorkSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
