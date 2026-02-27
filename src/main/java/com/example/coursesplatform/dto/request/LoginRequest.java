package com.example.coursesplatform.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email invalido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}
