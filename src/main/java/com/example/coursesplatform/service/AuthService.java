package com.example.coursesplatform.service;

import com.example.coursesplatform.dto.request.LoginRequest;
import com.example.coursesplatform.dto.request.RegisterRequest;
import com.example.coursesplatform.dto.response.AuthResponse;
import com.example.coursesplatform.entity.EmailVerificationToken;
import com.example.coursesplatform.entity.RefreshToken;
import com.example.coursesplatform.entity.User;
import com.example.coursesplatform.repository.EmailVerificationTokenRepository;
import com.example.coursesplatform.repository.RefreshTokenRepository;
import com.example.coursesplatform.repository.UserRepository;
import com.example.coursesplatform.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Tentando registrar usuário: {}", request.getLogin());

        if(userRepository.existsByLogin(request.getLogin())) {
            throw new EmailAlreadyExistsException("Email já cadastrado"); // Create Exception
        }

        User user = User.builder()
                .login(request.getLogin())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole())
                .emailVerified(false)
                .build();

        user = userRepository.save(user);
        log.info("Usuário criado com sucesso: {}", user.getId());

        String verificationToken = UUID.randomUUID().toString();
        EmailVerificationToken loginToken = EmailVerificationToken.builder()
                .user(user)
                .token(verificationToken)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();

        emailService.sendVerificationLogin(user.getLogin(), user.getFullName(), verificationToken); // Create method

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Tentando fazer login: {}", request.getLogin());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByLogin(request.getLogin())
                    .orElseThrow(() -> new InvalidCredentialsException("Credenciais inválidas")); // create exception

            log.info("Login bem-sucedido: {}", user.getId());


            String accessToken = jwtTokenProvider.generateAccessToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);


            refreshTokenRepository.deletedByUser(user);
            saveRefreshToken(user, refreshToken);

            return buildAuthResponse(user, accessToken, refreshToken);

        } catch (AuthenticationException e) {
            log.error("Falha na autenticação para: {}", request.getLogin());
            throw new InvalidCredentialsException("Credenciais inválidas"); // create exception
        }
    }

    @Transactional
    public void logout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        refreshTokenRepository.deletedByUser(user);
        log.info("Logout realizado: {}", userId);
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .userId(user.getId())
                .login(user.getLogin())
                .fullName(user.getFullName())
                .role(user.getRole())
                .emailVerified(user.getEmailVerified())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(jwtTokenProvider.getAccessTokenExpirationMs())
                .build();
    }
}
