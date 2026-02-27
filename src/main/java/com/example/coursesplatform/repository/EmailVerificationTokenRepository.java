package com.example.coursesplatform.repository;

import com.example.coursesplatform.entity.EmailVerificationToken;
import com.example.coursesplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID>{
    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findByUser(User user);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken evt WHERE evt.user = :user")
    void deleteByUser(User user);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken evt WHERE evt.expiresAt = :now")
    void deleteExpiredTokens(LocalDateTime now);

    boolean existsByUser(User user);
}
