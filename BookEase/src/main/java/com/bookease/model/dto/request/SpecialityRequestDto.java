package com.bookease.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record SpecialityRequestDto(
        @NotEmpty(message = "A lista de especialidades não pode ser vazia")
        List<String> specialityNames
) {}
