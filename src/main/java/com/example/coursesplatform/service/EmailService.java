package com.example.coursesplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    public void sendVerificationEmail(String email, String fullName, String token) {
        log.info("Email de verificação enviado para: {}", email);
        log.info("Token de verificação: {}", token);
        log.info("Link: http://localhost:8080/api/auth/verify-email?token={}", token);
    }
}
