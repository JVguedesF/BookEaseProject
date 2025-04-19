package com.bookease.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
        @NotBlank(message = "Username é obrigatório")
        @Size(min = 4, max = 30, message = "Username deve ter entre 4 e 30 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9._-]*$", message = "Username deve começar com letra ou número e pode conter apenas letras, números, pontos, hífens ou underscores")
        private String username;

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, max = 64, message = "Senha deve ter entre 8 e 64 caracteres")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Senha deve conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial (@$!%*?&)")
        private String password;

        @NotBlank(message = "Nome completo é obrigatório")
        @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
        @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]+$", message = "Nome deve conter apenas letras e espaços")
        private String name;

        @Size(min = 10, max = 11, message = "Telefone deve ter entre 10 e 11 dígitos")
        @Pattern(regexp = "^\\d{10,11}$", message = "Telefone deve conter apenas números (ex.: 11987654321)")
        private String phone;

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
        private String email;
}