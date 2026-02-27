package com.example.coursesplatform.dto.response;

import com.example.coursesplatform.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private UUID userId;
    private String email;
    private String fullName;
    private UserRole role;
    private Boolean emailVerified;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
}
