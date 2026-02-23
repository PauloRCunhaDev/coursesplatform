package com.example.coursesplatform.dto.request;

import com.example.coursesplatform.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String login;

    @NotBlank(message = "Senha é obrigatória")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d).{8,}$",
            message = "Senha deve ter no mínimo 8 caracteres, uma maiúscula, uma, minúscula e um número"
    )
    private String password;

    @NotBlank(message = "Nome completo é obrigatório")
    private String fullName;

    @NotNull(message = "Role é obrigatória")
    private UserRole role;
}
