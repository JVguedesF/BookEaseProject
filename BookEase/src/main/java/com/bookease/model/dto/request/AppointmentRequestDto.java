package com.bookease.model.dto.request;

import com.bookease.model.enums.AppointmentEnum;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequestDto(
        @NotNull(message = "A data e hora são obrigatórias")
        @Future(message = "A data deve ser no futuro")
        LocalDateTime dateTime,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String description,

        @NotNull(message = "O status é obrigatório")
        AppointmentEnum status,

        @NotNull(message = "O ID do procedimento oferecido é obrigatório")
        String procedureOfferedId,

        @NotNull(message = "O ID da associação dentista-clínica é obrigatório")
        String doctorClinicId,

        @NotNull(message = "O ID do paciente é obrigatório")
        String patientId,

        @NotNull(message = "O ID do horário de trabalho é obrigatório")
        String workScheduleId
) {
}