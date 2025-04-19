package com.bookease.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;
import java.util.UUID;


@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRequestDto {
        private UUID id;
        @NotBlank(message = "O documento é obrigatório")
        private String crm;
        @JsonProperty("specialityNames")
        private Set<String> specialityNames;
        private UserRequestDto userRequestDto;
}