package com.bookease.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ClinicRequestDto (
        @NotBlank(message = "CNPJ é obrigatório")
        String cnpj,

        @NotBlank(message = "Descrição é obrigatória")
        String description,

        @NotBlank(message = "Cidade é obrigatória")
        String city,

        @NotBlank(message = "Endereço é obrigatório")
        String address,

        @NotNull(message = "Dados do usuário são obrigatórios")
        @Valid
        UserRequestDto userRequestDto
) {
}
