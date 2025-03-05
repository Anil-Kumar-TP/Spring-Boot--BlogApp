package com.anil.blog.repositories;

import com.anil.blog.domain.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByToken(String token);
}