package com.anil.blog.repositories;

import com.anil.blog.domain.entities.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, UUID> {
    Optional<RevokedToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM RevokedToken rt WHERE rt.expiryDate < :date")
    int deleteByExpiryDateBefore(LocalDateTime date);
}