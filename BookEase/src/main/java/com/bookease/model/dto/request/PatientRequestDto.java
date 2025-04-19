package com.bookease.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.br.CPF;

@Builder
public record PatientRequestDto(
        @NotBlank(message = "CPF é obrigatório")
        @CPF(message = "CPF inválido")
        String cpf,

        UserRequestDto userRequestDto) {
}